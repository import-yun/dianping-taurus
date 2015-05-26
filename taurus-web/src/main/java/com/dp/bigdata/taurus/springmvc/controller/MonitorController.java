package com.dp.bigdata.taurus.springmvc.controller;

import java.io.IOException;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import javax.annotation.PostConstruct;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.restlet.data.MediaType;
import org.restlet.resource.ClientResource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.context.ServletContextAware;

import com.dianping.cat.Cat;
import com.dianping.lion.EnvZooKeeperConfig;
import com.dianping.lion.client.ConfigCache;
import com.dianping.lion.client.LionException;
import com.dp.bigdata.taurus.core.AttemptStatus;
import com.dp.bigdata.taurus.generated.module.Task;
import com.dp.bigdata.taurus.restlet.resource.IGetAttemptsByStatus;
import com.dp.bigdata.taurus.restlet.resource.IGetTaskLastStatus;
import com.dp.bigdata.taurus.restlet.resource.IGetTasks;
import com.dp.bigdata.taurus.restlet.resource.IHostsResource;
import com.dp.bigdata.taurus.restlet.resource.IUserTasks;
import com.dp.bigdata.taurus.restlet.shared.AttemptDTO;
import com.dp.bigdata.taurus.restlet.shared.HostDTO;
import com.dp.bigdata.taurus.springmvc.bean.WebResult;
import com.dp.bigdata.taurus.web.servlet.AttemptProxyServlet;
import com.dp.bigdata.taurus.web.utils.ReFlashHostLoadTask;
import com.dp.bigdata.taurus.web.utils.ReFlashHostLoadTaskTimer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

@Controller
@RequestMapping("/monitor")
public class MonitorController implements ServletContextAware {

	private Logger log = LoggerFactory.getLogger(this.getClass());
	
	private String RESTLET_URL_BASE;

    private static ArrayList<AttemptDTO> attempts;
    private static boolean is_flash = false;
	
    private ServletContext servletContext;
    
    @Override
	public void setServletContext(ServletContext sc) {
		this.servletContext=sc;  
	}
    
    @PostConstruct
	public void init() throws Exception{
		log.info("----------- into MonitorController init ------------");
		
		ReFlashHostLoadTaskTimer.getReFlashHostLoadManager().start();
		
		try {
            RESTLET_URL_BASE = ConfigCache.getInstance(EnvZooKeeperConfig.getZKAddress()).getProperty("taurus.web.restlet.url");
        } catch (LionException e) {
            RESTLET_URL_BASE = servletContext.getInitParameter("RESTLET_SERVER");
            Cat.logError("LionException",e);
        } catch (Exception e) {
            Cat.logError("LionException", e);
        }
	}

    @RequestMapping(value = "/jobdetail", method = RequestMethod.POST)
	public void jobdetail(ModelMap modelMap, HttpServletRequest request,
			HttpServletResponse response) throws IOException {
		log.info("--------------init the jobdetail------------");
    	
    	ClientResource cr = new ClientResource(RESTLET_URL_BASE + "host");
        IHostsResource hostsResource = cr.wrap(IHostsResource.class);
        cr.accept(MediaType.APPLICATION_XML);
        ArrayList<HostDTO> hosts = hostsResource.retrieve();
        
        OutputStream output = response.getOutputStream();
        String start = request.getParameter("start");
        String end = request.getParameter("end");

        cr = new ClientResource(RESTLET_URL_BASE + "jobdetail/" + "/" + start + "/" + end);
        IUserTasks userTasks = cr.wrap(IUserTasks.class);
        cr.accept(MediaType.APPLICATION_XML);
        String jsonString = userTasks.retrieve();
        output.write(jsonString.getBytes());
        output.close();
	}
    
    /**
     * 每次加载任务监控monitor.ftl都刷新作业们的运行历史
     * @param modelMap
     * @param request
     * @param response
     * @throws IOException
     */
    @RequestMapping(value = "/reflash_attempts", method = RequestMethod.POST)
	public void monitor(ModelMap modelMap, HttpServletRequest request,
			HttpServletResponse response) throws IOException {
		log.info("--------------init the reflash_attempts------------");
    	
    	ClientResource cr = new ClientResource(RESTLET_URL_BASE + "host");
        IHostsResource hostsResource = cr.wrap(IHostsResource.class);
        cr.accept(MediaType.APPLICATION_XML);
        ArrayList<HostDTO> hosts = hostsResource.retrieve();
        
        OutputStream output = response.getOutputStream();
        String start = request.getParameter("start");
        String taskTime = start;

        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        String url = RESTLET_URL_BASE + "getattemptsbystatus/";


        cr = new ClientResource(url + taskTime);
        IGetAttemptsByStatus resource = cr.wrap(IGetAttemptsByStatus.class);
        attempts = resource.retrieve();

        output.write("success".getBytes());
        output.close();
	}
	
    @RequestMapping(value = "/runningtasks", method = RequestMethod.GET)
	public String runningtasks(ModelMap modelMap, HttpServletRequest request,
			HttpServletResponse response) {
		log.info("--------------init the runningtasks------------");
    	
    	ClientResource cr = new ClientResource(RESTLET_URL_BASE + "host");
        IHostsResource hostsResource = cr.wrap(IHostsResource.class);
        cr.accept(MediaType.APPLICATION_XML);
        ArrayList<HostDTO> hosts = hostsResource.retrieve();
        
        String hourTimeStr = request.getParameter("hourTime");
        long hourTime = 60 * 60 * 1000;
        if (hourTimeStr != null && hourTimeStr.isEmpty()) {
            hourTime = Long.parseLong(hourTimeStr);

        }

        ClientResource crTask = new ClientResource(RESTLET_URL_BASE + "gettasks");
        IGetTasks taskResource = crTask.wrap(IGetTasks.class);
        ArrayList<Task> tasks = taskResource.retrieve();

        
        
        modelMap.addAttribute("attempts", attempts);
        modelMap.addAttribute("tasks", tasks);
        modelMap.addAttribute("mHelper", new MonitorHelper());
        return "/monitor/runningtasks.ftl";
	}
    
    
    @RequestMapping(value = "/submitfail", method = RequestMethod.POST)
	public String submitfail(ModelMap modelMap, HttpServletRequest request,
			HttpServletResponse response) throws IOException {
		log.info("--------------init the submitfail------------");
    	
    	ClientResource cr = new ClientResource(RESTLET_URL_BASE + "host");
        IHostsResource hostsResource = cr.wrap(IHostsResource.class);
        cr.accept(MediaType.APPLICATION_XML);
        ArrayList<HostDTO> hosts = hostsResource.retrieve();
        
        OutputStream output = response.getOutputStream();

        ArrayList<Task> tasks = ReFlashHostLoadTask.getTasks();
        if (tasks == null) {
            ClientResource crTask = new ClientResource(RESTLET_URL_BASE + "gettasks");
            IGetTasks taskResource = crTask.wrap(IGetTasks.class);
            tasks = taskResource.retrieve();
            ReFlashHostLoadTask.allTasks = tasks;
            ReFlashHostLoadTask.lastReadDataTime = new Date().getTime();
        }


        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm");

        String result = "";
        if (attempts != null)
            for (AttemptDTO dto : attempts) {
                String state = dto.getStatus();


                if (state.equals("SUBMIT_FAIL")) {
                    String status_api = RESTLET_URL_BASE + "getlaststatus";
                    String status;
                    try {
                        cr = new ClientResource(status_api + "/" + dto.getTaskID());
                        IGetTaskLastStatus statusResource = cr.wrap(IGetTaskLastStatus.class);
                        cr.accept(MediaType.APPLICATION_XML);
                        status = statusResource.retrieve();
                    } catch (Exception e) {
                        status = null;
                    }

                    String lastTaskStatus ="";
                    int taskState = -1;
                    if (status != null) {
                        try {
                            JsonParser parser = new JsonParser();
                            JsonElement statusElement = parser.parse(status);
                            JsonObject statusObject = statusElement.getAsJsonObject();
                            JsonElement statusValue = statusObject.get("status");

                            taskState = statusValue.getAsInt();

                            lastTaskStatus = AttemptStatus.getInstanceRunState(taskState);
                        } catch (Exception e) {
                            lastTaskStatus = "NULL";
                        }
                    }
                    if(lastTaskStatus.equals("SUCCEEDED") || lastTaskStatus.equals("RUNNING") ){
                        lastTaskStatus = "<span class='label label-info'>"
                                + lastTaskStatus
                                + "</span>";
                    }else{
                        lastTaskStatus = "<span class='label label-important'>"
                                + lastTaskStatus
                                + "</span>";
                    }

                    String taskName = "";
                    for (Task task : tasks) {
                        if (task.getTaskid().equals(dto.getTaskID())) {
                            taskName = task.getName();
                            break;
                        }
                    }
                    result += " <tr id = " + dto.getAttemptID() + " >"
                            + "<td >"
                            + dto.getTaskID()
                            + "</td >"
                            + "<td >"
                            + taskName
                            + "</td >";


                    if (dto.getStartTime() != null) {
                        result += "<td >"
                                + formatter.format(dto.getStartTime())
                                + "</td >";

                    } else {
                        result += "<td >"
                                + "NULL"
                                + "</td >";
                    }

                    if (dto.getEndTime() != null) {
                        result += "<td >"
                                + formatter.format(dto.getEndTime())
                                + "</td >";
                    } else {
                        result += "<td >"
                                + "NULL"
                                + "</td >";
                    }
                    if (dto.getScheduleTime() != null) {
                        result += "<td >"
                                + formatter.format(dto.getScheduleTime())
                                + "</td >";
                    } else {
                        result += "<td >"
                                + "NULL"
                                + "</td >";
                    }
                    if (dto.getExecHost() != null) {
                        result += "<td >"
                                + dto.getExecHost()
                                + "</td >";
                    } else {
                        result += "<td >"
                                + "NULL"
                                + "</td >";
                    }
                    result += "<td >"
                            + lastTaskStatus
                            + "</td >";
                    result += "<td> <a id ='submitFeedBtn' class='feedBtn'  href='feederror.jsp?id="
                            + dto.getAttemptID()
                            + "&status="
                            + dto.getStatus()
                            + "&taskName="
                            + taskName
                            + "&ip="
                            + dto.getExecHost()
                            + "&taskId="
                            + dto.getTaskID()
                            + "&feedtype=wechat"
                            + "&from=monitor"
                            + "'><img border='0' src='img/wechat.png'  width='20' height='20' color='blue' alt='点我报错' title='点我报错'/></a> |"
                            + "<a id ='submitFeedQQBtn' class='feedBtn'  href='feederror.jsp?id="
                            + dto.getAttemptID()
                            + "&status="
                            + dto.getStatus()
                            + "&taskName="
                            + taskName
                            + "&ip="
                            + dto.getExecHost()
                            + "&taskId="
                            + dto.getTaskID()
                            + "&feedtype=qq"
                            + "&from=monitor"
                            + "'><img border='0' src='img/qq.png'  width='20' height='20' color='blue' alt='点我报错' title='点我报错'/></a></td>";
                    result += "</tr>";
                }
            }
        output.write(result.getBytes());
        output.close();
        
        
        modelMap.addAttribute("attempts", attempts);
        modelMap.addAttribute("tasks", tasks);
        modelMap.addAttribute("mHelper", new MonitorHelper());
        return "/monitor/submitfail.ftl";
	}
    
    @RequestMapping(value = "/dependencypass", method = RequestMethod.POST)
	public String dependencypass(ModelMap modelMap, HttpServletRequest request,
			HttpServletResponse response) throws IOException {
		log.info("--------------init the dependencypass------------");
    	
    	ClientResource cr = new ClientResource(RESTLET_URL_BASE + "host");
        IHostsResource hostsResource = cr.wrap(IHostsResource.class);
        cr.accept(MediaType.APPLICATION_XML);
        ArrayList<HostDTO> hosts = hostsResource.retrieve();
        
        OutputStream output = response.getOutputStream();

        ArrayList<Task> tasks = ReFlashHostLoadTask.getTasks();
        if (tasks == null) {
            ClientResource crTask = new ClientResource(RESTLET_URL_BASE + "gettasks");
            IGetTasks taskResource = crTask.wrap(IGetTasks.class);
            tasks = taskResource.retrieve();
            ReFlashHostLoadTask.allTasks = tasks;
            ReFlashHostLoadTask.lastReadDataTime = new Date().getTime();
        }


        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm");

        String result = "";
        if (attempts != null)
            for (AttemptDTO dto : attempts) {
                String state = dto.getStatus();


                if (state.equals("DEPENDENCY_PASS")) {
                    String taskName = "";
                    for (Task task : tasks) {
                        if (task.getTaskid().equals(dto.getTaskID())) {
                            taskName = task.getName();
                            break;
                        }
                    }
                    result += " <tr id = " + dto.getAttemptID() + " >"
                            + "<td >"
                            + dto.getTaskID()
                            + "</td >"
                            + "<td >"
                            + taskName
                            + "</td >";


                    if (dto.getStartTime() != null) {
                        result += "<td >"
                                + formatter.format(dto.getStartTime())
                                + "</td >";

                    } else {
                        result += "<td >"
                                + "NULL"
                                + "</td >";
                    }

                    if (dto.getEndTime() != null) {
                        result += "<td >"
                                + formatter.format(dto.getEndTime())
                                + "</td >";
                    } else {
                        result += "<td >"
                                + "NULL"
                                + "</td >";
                    }
                    if (dto.getScheduleTime() != null) {
                        result += "<td >"
                                + formatter.format(dto.getScheduleTime())
                                + "</td >";
                    } else {
                        result += "<td >"
                                + "NULL"
                                + "</td >";
                    }
                    if (dto.getExecHost() != null) {
                        result += "<td >"
                                + dto.getExecHost()
                                + "</td >";
                    } else {
                        result += "<td >"
                                + "NULL"
                                + "</td >";
                    }

                    result += "<td> <a id ='denpencyFeedBtn' class='feedBtn'  href='feederror.jsp?id="
                            + dto.getAttemptID()
                            + "&status="
                            + dto.getStatus()
                            + "&taskName="
                            + taskName
                            + "&ip="
                            + dto.getExecHost()
                            + "&taskId="
                            + dto.getTaskID()
                            + "&feedtype=wechat"
                            + "&from=monitor"
                            + "'><img border='0' src='img/wechat.png'  width='20' height='20' color='blue' alt='点我报错' title='点我报错'/></a> |"
                            + "<a id ='denpencyFeedQQBtn' class='feedBtn'  href='feederror.jsp?id="
                            + dto.getAttemptID()
                            + "&status="
                            + dto.getStatus()
                            + "&taskName="
                            + taskName
                            + "&ip="
                            + dto.getExecHost()
                            + "&taskId="
                            + dto.getTaskID()
                            + "&feedtype=qq"
                            + "&from=monitor"
                            + "'><img border='0' src='img/qq.png'  width='20' height='20' color='blue' alt='点我报错' title='点我报错'/></a></td>";

                    result += "</tr>";
                }
            }
        output.write(result.getBytes());
        output.close();
        
        
        return "/monitor/dependencypass.ftl";
	}
    
    @RequestMapping(value = "/failedtasks", method = RequestMethod.POST)
	public String failedtasks(ModelMap modelMap, HttpServletRequest request,
			HttpServletResponse response) throws IOException {
		log.info("--------------init the failedtasks------------");
    	
    	ClientResource cr = new ClientResource(RESTLET_URL_BASE + "host");
        IHostsResource hostsResource = cr.wrap(IHostsResource.class);
        cr.accept(MediaType.APPLICATION_XML);
        ArrayList<HostDTO> hosts = hostsResource.retrieve();
        
        OutputStream output = response.getOutputStream();

        ArrayList<Task> tasks = ReFlashHostLoadTask.getTasks();
        if (tasks == null) {
            ClientResource crTask = new ClientResource(RESTLET_URL_BASE + "gettasks");
            IGetTasks taskResource = crTask.wrap(IGetTasks.class);
            tasks = taskResource.retrieve();
            ReFlashHostLoadTask.allTasks = tasks;
            ReFlashHostLoadTask.lastReadDataTime = new Date().getTime();
        }


        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm");

        String result = "";
        if (attempts != null)
            for (AttemptDTO dto : attempts) {
                String state = dto.getStatus();


                if (state.equals("FAILED")) {
                    String status_api = RESTLET_URL_BASE + "getlaststatus";
                    String status;
                    try {
                        cr = new ClientResource(status_api + "/" + dto.getTaskID());
                        IGetTaskLastStatus statusResource = cr.wrap(IGetTaskLastStatus.class);
                        cr.accept(MediaType.APPLICATION_XML);
                        status = statusResource.retrieve();
                    } catch (Exception e) {
                        status = null;
                    }

                    String lastTaskStatus ="";
                    int taskState = -1;
                    if (status != null) {
                        try {
                            JsonParser parser = new JsonParser();
                            JsonElement statusElement = parser.parse(status);
                            JsonObject statusObject = statusElement.getAsJsonObject();
                            JsonElement statusValue = statusObject.get("status");

                            taskState = statusValue.getAsInt();

                            lastTaskStatus = AttemptStatus.getInstanceRunState(taskState);
                        } catch (Exception e) {
                            lastTaskStatus = "NULL";
                        }
                    }
                    if(state == "SUCCEEDED"){
                        lastTaskStatus = "<span class='label label-info'>"
                                + lastTaskStatus
                                + "</span>";
                    }else{
                        lastTaskStatus = "<span class='label label-important'>"
                                + lastTaskStatus
                                + "</span>";
                    }

                    String taskName = "";
                    for (Task task : tasks) {
                        if (task.getTaskid().equals(dto.getTaskID())) {
                            taskName = task.getName();
                            break;
                        }
                    }

                    result += " <tr id = " + dto.getAttemptID() + " >"
                            + "<td >"
                            + dto.getTaskID()
                            + "</td >"
                            + "<td >"
                            + taskName
                            + "</td >";


                    if (dto.getStartTime() != null) {
                        result += "<td >"
                                + formatter.format(dto.getStartTime())
                                + "</td >";

                    } else {
                        result += "<td >"
                                + "NULL"
                                + "</td >";
                    }

                    if (dto.getEndTime() != null) {
                        result += "<td >"
                                + formatter.format(dto.getEndTime())
                                + "</td >";
                    } else {
                        result += "<td >"
                                + "NULL"
                                + "</td >";
                    }
                    if (dto.getScheduleTime() != null) {
                        result += "<td >"
                                + formatter.format(dto.getScheduleTime())
                                + "</td >";
                    } else {
                        result += "<td >"
                                + "NULL"
                                + "</td >";
                    }
                    if (dto.getExecHost() != null) {
                        result += "<td >"
                                + dto.getExecHost()
                                + "</td >";
                    } else {
                        result += "<td >"
                                + "NULL"
                                + "</td >";
                    }
                    result += "<td >"
                            + lastTaskStatus
                            + "</td >";
                    result += "<td >"
                            + "  <a target=\"_blank\" href=\"viewlog.jsp?id="
                            + dto.getAttemptID() + "&status=" + dto.getStatus()
                            + "\">日志</a>"
                            + "</td >";

                    result += "<td> <a id ='failedFeedBtn' class='feedBtn'  href='feederror.jsp?id="
                            + dto.getAttemptID()
                            + "&status="
                            + dto.getStatus()
                            + "&taskName="
                            + taskName
                            + "&ip="
                            + dto.getExecHost()
                            + "&taskId="
                            + dto.getTaskID()
                            + "&feedtype=wechat"
                            + "&from=monitor"
                            + "'><img border='0' src='img/wechat.png'  width='20' height='20' color='blue' alt='点我报错' title='点我报错'/></a> |"
                            + "<a id ='failedFeedQQBtn' class='feedBtn'  href='feederror.jsp?id="
                            + dto.getAttemptID()
                            + "&status="
                            + dto.getStatus()
                            + "&taskName="
                            + taskName
                            + "&ip="
                            + dto.getExecHost()
                            + "&taskId="
                            + dto.getTaskID()
                            + "&feedtype=qq"
                            + "&from=monitor"
                            + "'><img border='0' src='img/qq.png'  width='20' height='20' color='blue' alt='点我报错' title='点我报错'/></a></td>";


                    result += "</tr>";
                }
            }
        output.write(result.getBytes());
        output.close();
        
        
        return "/monitor/failedtasks.ftl";
	}
    
    @RequestMapping(value = "/dependencytimeout", method = RequestMethod.POST)
	public String dependencytimeout(ModelMap modelMap, HttpServletRequest request,
			HttpServletResponse response) throws IOException {
		log.info("--------------init the dependencytimeout------------");
    	
    	ClientResource cr = new ClientResource(RESTLET_URL_BASE + "host");
        IHostsResource hostsResource = cr.wrap(IHostsResource.class);
        cr.accept(MediaType.APPLICATION_XML);
        ArrayList<HostDTO> hosts = hostsResource.retrieve();
        
        OutputStream output = response.getOutputStream();

        ArrayList<Task> tasks = ReFlashHostLoadTask.getTasks();
        if (tasks == null) {
            ClientResource crTask = new ClientResource(RESTLET_URL_BASE + "gettasks");
            IGetTasks taskResource = crTask.wrap(IGetTasks.class);
            tasks = taskResource.retrieve();
            ReFlashHostLoadTask.allTasks = tasks;
            ReFlashHostLoadTask.lastReadDataTime = new Date().getTime();
        }


        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm");

        String result = "";
        if (attempts != null)
            for (AttemptDTO dto : attempts) {
                String state = dto.getStatus();


                if (state.equals("DEPENDENCY_TIMEOUT")) {
                    String taskName = "";
                    for (Task task : tasks) {
                        if (task.getTaskid().equals(dto.getTaskID())) {
                            taskName = task.getName();
                            break;
                        }
                    }
                    result += " <tr id = " + dto.getAttemptID() + " >"
                            + "<td >"
                            + dto.getTaskID()
                            + "</td >"
                            + "<td >"
                            + taskName
                            + "</td >";


                    if (dto.getStartTime() != null) {
                        result += "<td >"
                                + formatter.format(dto.getStartTime())
                                + "</td >";

                    } else {
                        result += "<td >"
                                + "NULL"
                                + "</td >";
                    }

                    if (dto.getEndTime() != null) {
                        result += "<td >"
                                + formatter.format(dto.getEndTime())
                                + "</td >";
                    } else {
                        result += "<td >"
                                + "NULL"
                                + "</td >";
                    }
                    if (dto.getScheduleTime() != null) {
                        result += "<td >"
                                + formatter.format(dto.getScheduleTime())
                                + "</td >";
                    } else {
                        result += "<td >"
                                + "NULL"
                                + "</td >";
                    }
                    if (dto.getExecHost() != null) {
                        result += "<td >"
                                + dto.getExecHost()
                                + "</td >";
                    } else {
                        result += "<td >"
                                + "NULL"
                                + "</td >";
                    }
                    result += "<td >"
                            + "  <a target=\"_blank\" href=\"viewlog.jsp?id="
                            + dto.getAttemptID() + "&status=" + dto.getStatus()
                            + "\">日志</a>"
                            + "</td >";

                    result += "<td> <a id ='denpencyTimeoutFeedBtn' class='feedBtn'  href='feederror.jsp?id="
                            + dto.getAttemptID()
                            + "&status="
                            + dto.getStatus()
                            + "&taskName="
                            + taskName
                            + "&ip="
                            + dto.getExecHost()
                            + "&taskId="
                            + dto.getTaskID()
                            + "&feedtype=wechat"
                            + "&from=monitor"
                            + "'><img border='0' src='img/wechat.png'  width='20' height='20' color='blue' alt='点我报错' title='点我报错'/></a> |"
                            + "<a id ='denpencyTimeoutFeedQQBtn' class='feedBtn'  href='feederror.jsp?id="
                            + dto.getAttemptID()
                            + "&status="
                            + dto.getStatus()
                            + "&taskName="
                            + taskName
                            + "&ip="
                            + dto.getExecHost()
                            + "&taskId="
                            + dto.getTaskID()
                            + "&feedtype=qq"
                            + "&from=monitor"
                            + "'><img border='0' src='img/qq.png'  width='20' height='20' color='blue' alt='点我报错' title='点我报错'/></a></td>";

                    result += "</tr>";
                }
            }
        output.write(result.getBytes());
        output.close();
        return "/monitor/dependencytimeout.ftl";
	}
    
    @RequestMapping(value = "/timeout", method = RequestMethod.POST)
	public String timeout(ModelMap modelMap, HttpServletRequest request,
			HttpServletResponse response) throws IOException {
		log.info("--------------init the timeout------------");
    	
    	ClientResource cr = new ClientResource(RESTLET_URL_BASE + "host");
        IHostsResource hostsResource = cr.wrap(IHostsResource.class);
        cr.accept(MediaType.APPLICATION_XML);
        ArrayList<HostDTO> hosts = hostsResource.retrieve();
        
        OutputStream output = response.getOutputStream();
        ArrayList<Task> tasks = ReFlashHostLoadTask.getTasks();
        if (tasks == null) {
            ClientResource crTask = new ClientResource(RESTLET_URL_BASE + "gettasks");
            IGetTasks taskResource = crTask.wrap(IGetTasks.class);
            tasks = taskResource.retrieve();
            ReFlashHostLoadTask.allTasks = tasks;
            ReFlashHostLoadTask.lastReadDataTime = new Date().getTime();
        }


        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm");

        String result = "";
        if (attempts != null)
            for (AttemptDTO dto : attempts) {
                String state = dto.getStatus();


                if (state.equals("TIMEOUT")) {
                    String status_api = RESTLET_URL_BASE + "getlaststatus";
                    String status;
                    try {
                        cr = new ClientResource(status_api + "/" + dto.getTaskID());
                        IGetTaskLastStatus statusResource = cr.wrap(IGetTaskLastStatus.class);
                        cr.accept(MediaType.APPLICATION_XML);
                        status = statusResource.retrieve();
                    } catch (Exception e) {
                        status = null;
                    }

                    String lastTaskStatus ="";
                    int taskState = -1;
                    if (status != null) {
                        try {
                            JsonParser parser = new JsonParser();
                            JsonElement statusElement = parser.parse(status);
                            JsonObject statusObject = statusElement.getAsJsonObject();
                            JsonElement statusValue = statusObject.get("status");

                            taskState = statusValue.getAsInt();

                            lastTaskStatus = AttemptStatus.getInstanceRunState(taskState);
                        } catch (Exception e) {
                            lastTaskStatus = "NULL";
                        }
                    }
                    if(state == "SUCCEEDED"){
                        lastTaskStatus = "<span class='label label-info'>"
                                + lastTaskStatus
                                + "</span>";
                    }else{
                        lastTaskStatus = "<span class='label label-important'>"
                                + lastTaskStatus
                                + "</span>";
                    }

                    String taskName = "";
                    for (Task task : tasks) {
                        if (task.getTaskid().equals(dto.getTaskID())) {
                            taskName = task.getName();
                            break;
                        }
                    }
                    result += " <tr id = " + dto.getAttemptID() + " >"
                            + "<td >"
                            + dto.getTaskID()
                            + "</td >"
                            + "<td >"
                            + taskName
                            + "</td >";


                    if (dto.getStartTime() != null) {
                        result += "<td >"
                                + formatter.format(dto.getStartTime())
                                + "</td >";

                    } else {
                        result += "<td >"
                                + "NULL"
                                + "</td >";
                    }

                    if (dto.getEndTime() != null) {
                        result += "<td >"
                                + formatter.format(dto.getEndTime())
                                + "</td >";
                    } else {
                        result += "<td >"
                                + "NULL"
                                + "</td >";
                    }
                    if (dto.getScheduleTime() != null) {
                        result += "<td >"
                                + formatter.format(dto.getScheduleTime())
                                + "</td >";
                    } else {
                        result += "<td >"
                                + "NULL"
                                + "</td >";
                    }
                    if (dto.getExecHost() != null) {
                        result += "<td >"
                                + dto.getExecHost()
                                + "</td >";
                    } else {
                        result += "<td >"
                                + "NULL"
                                + "</td >";
                    }
                    result += "<td >"
                            + lastTaskStatus
                            + "</td >";
                    result += "<td >"
                            + "  <a target=\"_blank\" href=\"viewlog.jsp?id="
                            + dto.getAttemptID() + "&status=" + dto.getStatus()
                            + "\">日志</a>"
                            + "</td >";

                    result += "<td> "
                            + "<a id ='timeOutFeedBtn' class='feedBtn'  href='feederror.jsp?id="
                            + dto.getAttemptID()
                            + "&status="
                            + dto.getStatus()
                            + "&taskName="
                            + taskName
                            + "&ip="
                            + dto.getExecHost()
                            + "&taskId="
                            + dto.getTaskID()
                            + "&feedtype=wechat"
                            + "&from=monitor"
                            + "'><img border='0' src='img/wechat.png'  width='20' height='20' color='blue' alt='点我报错' title='点我报错'/></a> |"
                            + "<a id ='timeOutFeedQQBtn' class='feedBtn'  href='feederror.jsp?id="
                            + dto.getAttemptID()
                            + "&status="
                            + dto.getStatus()
                            + "&taskName="
                            + taskName
                            + "&ip="
                            + dto.getExecHost()
                            + "&taskId="
                            + dto.getTaskID()
                            + "&feedtype=qq"
                            + "&from=monitor"
                            + "'><img border='0' src='img/qq.png'  width='20' height='20' color='blue' alt='点我报错' title='点我报错'/></a></td>";

                    result += "</tr>";
                }
            }
        output.write(result.getBytes());
        output.close();
		
        
        return "/monitor/timeout.ftl";
	}
    
    public class MonitorHelper{
    	/**
    	 * runningtasks.ftl辅助方法
    	 * @param ip
    	 * @return
    	 */
    	public boolean isViewLog(String ip){
    		boolean result = AttemptProxyServlet.isHostOverLoad(ip);
    		String zabbixSwitch = "";
            try {
                zabbixSwitch = ConfigCache.getInstance(EnvZooKeeperConfig.getZKAddress()).getProperty("taurus.zabbix.switch");
            }catch (LionException e){
                zabbixSwitch = "true";
            }

            if (zabbixSwitch.equals("false")){
            	result = false;
            }
            return result;
    	}
    	/**
    	 * submitfail.ftl辅助方法
    	 * @param taskID
    	 * @return
    	 */
    	public String getLastTaskStatus(String taskID){
    		String status_api = RESTLET_URL_BASE + "getlaststatus";
            String status;
            try {
            	ClientResource cr = new ClientResource(status_api + "/" + taskID);
                IGetTaskLastStatus statusResource = cr.wrap(IGetTaskLastStatus.class);
                cr.accept(MediaType.APPLICATION_XML);
                status = statusResource.retrieve();
            } catch (Exception e) {
                status = null;
            }

            String lastTaskStatus ="";
            int taskState = -1;
            if (status != null) {
                try {
                    JsonParser parser = new JsonParser();
                    JsonElement statusElement = parser.parse(status);
                    JsonObject statusObject = statusElement.getAsJsonObject();
                    JsonElement statusValue = statusObject.get("status");

                    taskState = statusValue.getAsInt();

                    lastTaskStatus = AttemptStatus.getInstanceRunState(taskState);
                } catch (Exception e) {
                    lastTaskStatus = "NULL";
                }
            }
            if(lastTaskStatus.equals("SUCCEEDED") || lastTaskStatus.equals("RUNNING") ){
                lastTaskStatus = "<span class='label label-info'>"
                        + lastTaskStatus
                        + "</span>";
            }else{
                lastTaskStatus = "<span class='label label-important'>"
                        + lastTaskStatus
                        + "</span>";
            }
            return lastTaskStatus;
    	}
    }
}
