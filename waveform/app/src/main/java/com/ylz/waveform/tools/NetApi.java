package com.ylz.waveform.tools;

/**
 * Created by Administrator on 2017/8/14.
 */

public class NetApi {




    //开发Ftp服务地址
    public static final String COMMON_MAIN = "http://192.168.82.8:8080/jiqingyuan/";
    public static final String FTP_URL = "172.16.30.203";
    public static final int FTP_PORT = 21;
    public static final String FTP_USER = "admin";//adminstrator
    public static final String FTP_PASSWORD = "admin";//jjyl0001

    public static final String SECURITY_IMAGE_FILE_DIRECTORY = "admin";//jjyl0001


    //获取登录信息
    public static final String LOGIN_URL = COMMON_MAIN + "personal/getPersonalFromPhone.action";

    //修改密码
    public static final String REGIST_URL = COMMON_MAIN + "personal/addPersonalFromPhone.action";

    //修改密码
    public static final String PWD_URL = COMMON_MAIN + "user/update/passwd.action";

    //添加任务信息
    public static final String ADD_TASK_URL = COMMON_MAIN + "task/addTaskBean";

    //获取今日任务
    public static final String GET_TODAY_TASK_URL = COMMON_MAIN + "task/getTodayTask";

    //获取本月任务
    public static final String GET_THIS_MONTH_TASKS_URL = COMMON_MAIN + "task/getThisMonthTasks";
}
