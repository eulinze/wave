package com.ylz.waveform.authlogin;

import com.google.gson.annotations.SerializedName;

public class UserGson {
    @SerializedName("id")
    String userId;
    @SerializedName("screen_name")
    String userName;

    String gender;

}
