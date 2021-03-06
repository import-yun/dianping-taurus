package com.cip.crane.restlet;

import com.cip.crane.common.lion.ConfigHolder;
import com.cip.crane.common.lion.LionKeys;
import com.cip.crane.restlet.utils.LionConfigUtil;
import com.cip.crane.restlet.utils.ReFlashHostLoadTaskTimer;
import com.cip.crane.zookeeper.common.elect.LeaderElector;
import com.cip.crane.zookeeper.common.event.LeaderChangedListener;
import com.cip.crane.zookeeper.common.visit.IpInfoLeaderElectorVisitor;
import com.dianping.cat.Cat;
import com.cip.crane.common.alert.TaurusAlert;
import com.cip.crane.common.alert.WeChatHelper;
import com.cip.crane.common.utils.SleepUtils;
import com.cip.crane.common.AttemptStatusMonitor;
import com.cip.crane.common.Engine;
import com.cip.crane.restlet.utils.ClearLogsTimerManager;
import com.cip.crane.restlet.utils.MonitorAgentOffLineTaskTimer;
import com.cip.crane.common.utils.EnvUtils;
import com.cip.crane.common.utils.RestCallUtils;
import com.cip.crane.zookeeper.common.elect.TaurusZKLeaderElector;
import com.cip.crane.common.lock.LockAction;
import com.cip.crane.zookeeper.common.event.LeaderChangeEvent;
import com.cip.crane.zookeeper.common.infochannel.guice.LeaderElectorChanelModule;
import com.cip.crane.common.utils.IPUtils;
import com.google.inject.Guice;
import com.google.inject.Injector;
import org.I0Itec.zkclient.IZkStateListener;
import org.apache.zookeeper.Watcher;
import org.restlet.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.PostConstruct;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * TaurusRestletServer mode: standalone | all
 *
 * @author damon.zhu
 */
public class TaurusServer implements LeaderChangedListener {

    private Logger log = LoggerFactory.getLogger(this.getClass());

    private static final String PING = LionConfigUtil.RESTLET_API_BASE + "healthCheck"; //只是健康测试,返回false

    @Autowired
    public Engine engine;

    @Autowired
    public TaurusAlert alert;

    @Autowired
    public Component restlet;

    @Autowired
    public AttemptStatusMonitor statusMonitor;

    private LeaderElector leaderElector;

    private final Object eventLock = new Object();

    private AtomicBoolean started = new AtomicBoolean(false);

    @PostConstruct
    public void initLeaderElector() {

        Injector injector = Guice.createInjector(new LeaderElectorChanelModule());
        leaderElector = injector.getInstance(LeaderElector.class);
        log.info("LeaderElector is initialized");

        leaderElector.addLeaderChangeListener(this);
        log.info("add listener TaurusServer to LeaderElector");

        leaderElector.addStateListener(new SessionExpirationListener());
        leaderElector.startup();
        log.info("LeaderElector startup");
    }

    public void start() {

        System.setProperty("org.restlet.engine.loggerFacadeClass", "org.restlet.ext.slf4j.Slf4jLoggerFacade");

        try {

            if (!LionConfigUtil.loadServerConf(leaderElector.getCurrentLeaderIp())) {
                alert.isInterrupt(true);
                engine.isInterrupt(true);
                log.info("lion config error....");
            }

            restlet.start();
            alert.start(-1);
            engine.start();

            log.info("taurus start....");

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void stop() {
        System.setProperty("org.restlet.engine.loggerFacadeClass", "org.restlet.ext.slf4j.Slf4jLoggerFacade");
        engine.stop();

        try {
            restlet.stop();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onBecomingLeader(LeaderChangeEvent leaderChangeEvent) {

        synchronized (eventLock) {

            //等待master调度完成，如果master宕机，SCHEDULE_SCHEDULING不会被删除，需要检测是否活着
            while (leaderElector.exists(LeaderElector.SCHEDULE_SCHEDULING) && isMasterLive()) {
                SleepUtils.sleepHalfSecond();
            }
            leaderElector.createPersistent(LeaderElector.SCHEDULE_SCHEDULING);

            LionConfigUtil.loadServerConf(leaderElector.getCurrentLeaderIp());
            engine.load();
            alert.load();
            alert.isInterrupt(false);
            engine.clearCache();
            engine.initCache();
            engine.isInterrupt(false);

            if (ClearLogsTimerManager.getClearLogsTimerManager().getTimer() == null) {
                ClearLogsTimerManager.getClearLogsTimerManager().start();
            }

            if (MonitorAgentOffLineTaskTimer.getMonitorAgentOffLineTimeManager().getTimer() == null) {
                MonitorAgentOffLineTaskTimer.getMonitorAgentOffLineTimeManager().start();
            }

            if (ReFlashHostLoadTaskTimer.getReFlashHostLoadManager().getTimer() == null) {
                ReFlashHostLoadTaskTimer.getReFlashHostLoadManager().start();
            }

            if (!started.get()) {
                starting();
            }

            log.info("start as master server....");
            Cat.logEvent("Taurus.Master", IPUtils.getFirstNoLoopbackIP4Address());
            WeChatHelper.sendWeChat(ConfigHolder.get(LionKeys.ADMIN_USER), EnvUtils.getEnv() + " taurus master start: " + IPUtils.getFirstNoLoopbackIP4Address(), ConfigHolder.get(LionKeys.ADMIN_WECHAT_AGENTID));
        }
    }

    @Override
    public void onResigningAsLeader(LeaderChangeEvent leaderChangeEvent) {

        synchronized (eventLock) {

            alert.isInterrupt(true);
            engine.isInterrupt(true);
            engine.clearCache();

            if (ClearLogsTimerManager.getClearLogsTimerManager().getTimer() != null) {
                ClearLogsTimerManager.getClearLogsTimerManager().stop();
            }

            if (MonitorAgentOffLineTaskTimer.getMonitorAgentOffLineTimeManager().getTimer() != null) {
                MonitorAgentOffLineTaskTimer.getMonitorAgentOffLineTimeManager().stop();
            }

            if (ReFlashHostLoadTaskTimer.getReFlashHostLoadManager().getTimer() != null) {
                ReFlashHostLoadTaskTimer.getReFlashHostLoadManager().stop();
            }

            if (started.get()) {
                while (!(engine.isTriggleThreadRestFlag()
                        && statusMonitor.isAttemptStatusMonitorRestFlag())) {
                    //wait the engine to finish last schedule
                }
                leaderElector.delete(LeaderElector.SCHEDULE_SCHEDULING);
            } else {
                starting();
            }

            log.info("start as slave server....");
            Cat.logEvent("Taurus.Slave", IPUtils.getFirstNoLoopbackIP4Address());
            WeChatHelper.sendWeChat(ConfigHolder.get(LionKeys.ADMIN_USER), EnvUtils.getEnv() + " taurus slave start: " + IPUtils.getFirstNoLoopbackIP4Address(), ConfigHolder.get(LionKeys.ADMIN_WECHAT_AGENTID));

            Object source = leaderChangeEvent.getSource();
            IpInfoLeaderElectorVisitor visitor = new IpInfoLeaderElectorVisitor();
            if (source instanceof TaurusZKLeaderElector) {
                TaurusZKLeaderElector taurusZKLeaderElector = (TaurusZKLeaderElector) source;
                if (taurusZKLeaderElector.needAlarm()) {
                    visitor.visitLeaderElector(taurusZKLeaderElector);
                    WeChatHelper.sendWeChat(ConfigHolder.get(LionKeys.ADMIN_USER), visitor.getContent(), ConfigHolder.get(LionKeys.ADMIN_WECHAT_AGENTID));
                }
            }
        }
    }

    private void starting() {
        started.set(true);
        start();
    }

    private boolean isMasterLive() {

        try {
            RestCallUtils.getRestCall(PING, String.class, 3000, 1000);
            return true;
        } catch (Exception e){
            return false;
        }
    }

    class SessionExpirationListener implements IZkStateListener {

        @Override
        public void handleStateChanged(Watcher.Event.KeeperState state) throws Exception {
            // do nothing, since zkclient will do reconnect for us.
            log.info("session expired and state is %d", state.getIntValue());
        }

        @Override
        public void handleNewSession() throws Exception {

            log.info("ZK expired; shut down schedule server and try to re-elect");
            leaderElector.getLock().doAction(new LockAction() {
                @Override
                public void doAction() {
                    onResigningAsLeader(new LeaderChangeEvent(this));
                    leaderElector.elect();
                }
            });
        }

    }
}
