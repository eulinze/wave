package com.ylz.waveform.authlogin;

import com.google.gson.annotations.SerializedName;

public class User {



    public String userName;

    public String phoneNumber;

    @SerializedName("passWord")
    public String password;

    public String oldPassword;

    public String newPassword;
}
