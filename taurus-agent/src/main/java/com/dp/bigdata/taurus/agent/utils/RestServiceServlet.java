package com.dp.bigdata.taurus.agent.utils;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.*;
import java.io.IOException;
import java.io.OutputStream;

/**
 * Created by kirinli on 14-8-19.
 */
public class RestServiceServlet extends HttpServlet {
    private static final String GETLOG = "getlog";
    private static final String ISEND = "isend";
    private static final String ISNEW = "isnew";

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        doPost(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String action = request.getParameter("action") == null ? "" : request.getParameter("action").toLowerCase();
        if (action.equals(GETLOG)) {
            String date = (String) request.getParameter("date");               //日志日期
            String attemptID = (String) request.getParameter("attemptId");
            String fileOffset; //文件偏移量
            String flag = (String) request.getParameter("flag");               //这个主要是标志当web刷新时，任务还在执行，但是等请求的时候，任务已经结束的时间点
            String queryType = (String) request.getParameter("query_type");    //区分是log，还是error log
            String offSetType;
            ServletContext application=this.getServletContext();
            if (queryType.equals("log")) {
                offSetType = "logOffSet";
            } else {
                offSetType = "errorOffSet";
            }

           fileOffset = application.getAttribute(offSetType).toString();

            if (fileOffset == null) {
                fileOffset = "0";
            }

            System.out.println("query:" + queryType + "fileoffset:" + fileOffset);

            GetLogs getlogs = new GetLogs();
            String respStr = getlogs.getLogs(date, attemptID, fileOffset, flag, queryType);
            LogFileEnd logFileEnd = new LogFileEnd();
            String isEnd = logFileEnd.isEnd(attemptID);

            if (isEnd.equals("true")) {

             application.setAttribute(offSetType, "0");
            }else {
                long offsetSum = Long.parseLong(fileOffset) + respStr.length();
                application.setAttribute(offSetType, offsetSum);
            }

            System.out.println("end query:" + queryType + "fileoffset:" + fileOffset);

            OutputStream output = response.getOutputStream();
            output.write(respStr.getBytes());
            output.close();
        } else if (action.equals(ISEND)) {
            String attemptID = (String) request.getParameter("attemptId");
            LogFileEnd logFileEnd = new LogFileEnd();
            String respStr = logFileEnd.isEnd(attemptID);
            OutputStream output = response.getOutputStream();
            output.write(respStr.getBytes());
            output.close();
        } else if (action.equals(ISNEW)) {
            String respStr = "true";
            OutputStream output = response.getOutputStream();
            output.write(respStr.getBytes());
            output.close();
        }

    }

}
