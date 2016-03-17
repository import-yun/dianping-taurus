package com.dp.bigdata.taurus.alert.healthcheck;

import com.dianping.lion.EnvZooKeeperConfig;
import com.dianping.lion.client.ConfigCache;
import com.dp.bigdata.taurus.zookeeper.common.utils.ClassLoaderUtils;
import org.I0Itec.zkclient.ZkClient;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.annotation.PostConstruct;
import java.io.InputStream;
import java.util.Properties;

/**
 * Author   mingdongli
 * 16/3/16  下午2:43.
 */
public abstract class AbstractHealthChecker implements HealthChecker {

    protected final Log logger = LogFactory.getLog(getClass());

    private static final String ZK_CONF = "zooKeeper.properties";

    private static final String KEY_SESSION_TIMEOUT = "sessionTimeout";

    private static final int CONNECTION_TIMEOUT = 30 * 1000;

    private static final int RETRY_TIME = 4;

    private ZkClient zk;

    @PostConstruct
    public void initZkClient() {
        Properties props = new Properties();
        try {
            InputStream in = ClassLoaderUtils.getDefaultClassLoader().getResourceAsStream(ZK_CONF);
            props.load(in);
            in.close();
            String connectString = ConfigCache.getInstance(EnvZooKeeperConfig.getZKAddress()).getProperty("taurus.zookeeper.connectstring");
            int sessionTimeout = Integer.parseInt(props.getProperty(KEY_SESSION_TIMEOUT));
            zk = new ZkClient(connectString, sessionTimeout, CONNECTION_TIMEOUT);
        } catch (Exception e) {
            logger.info("init zkclient error", e);
        }
    }

    @Override
    public boolean isHealthy() {
        String checkPath = getCheckPath();
        int retry = 0;
        while (retry < RETRY_TIME) {
            retry++;

            if (!existPath(checkPath)) {
                continue;
            } else {
                String leaderIp = getData(checkPath);
                if (StringUtils.isBlank(leaderIp)) {
                    continue;
                }
            }

            if (retry >= RETRY_TIME) {
                return false;
            }
            return true;
        }

        return false;
    }

    protected String getData(String path) {

        try {
            return zk.readData(path, true).toString();
        }catch (Exception e){
            logger.error(String.format("read data form path %s error", path));
            return StringUtils.EMPTY;
        }

    }

    protected boolean existPath(String path) {
        return zk.exists(path);
    }

    protected abstract String getCheckPath();
}
