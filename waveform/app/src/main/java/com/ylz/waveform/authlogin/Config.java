package com.ylz.waveform.authlogin;

public class Config {
//    public final static String IP_ADR = "http://19v216n428.imwork.net:51852/login";
    public final static String IP_ADR = "http://58.87.124.229:8080/login";
    public final static String AUTH_LOGIN_URL = IP_ADR+"/auth_login";
    public final static String GENERATE_VERIFY_CODE_URL = IP_ADR+"/generate_verify_code";
    public final static String BIND_AND_LOGIN_URL = IP_ADR + "/bindAndLogin";
    public final static String GET_USER_URL = IP_ADR + "/getUser";

    public final static String VERIFY_CODE_LOGIN_URL = IP_ADR + "/verifyLogin";

    public final static String  PASS_WORD_LOGIN_URL = IP_ADR + "/passwordLogin";

    public final static String MODIFY_PASSWORD_URL = IP_ADR + "/modifyPassword";

    public final static String SET_PASSWORD_URL = IP_ADR + "/setPassword";
}
