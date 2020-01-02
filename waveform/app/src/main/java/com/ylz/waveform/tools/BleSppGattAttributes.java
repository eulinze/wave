/*
 * Copyright (C) 2013 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.ylz.waveform.tools;

/**
 * This class includes a small subset of standard GATT attributes for demonstration purposes.
 */
public class BleSppGattAttributes {

//    public static String CLIENT_CHARACTERISTIC_CONFIG = "00002902-0000-1000-8000-00805f9b34fb";
//
//    public static String BLE_SPP_Service = "0000fff0-0000-1000-8000-00805f9b34fb";
//    public static String BLE_SPP_Notify_Characteristic = "0000fff4-0000-1000-8000-00805f9b34fb";
//    public static String  BLE_SPP_Read_Characteristic = "0000fff3-0000-1000-8000-00805f9b34fb";
//    public static String  BLE_SPP_Write_Characteristic = "0000fff3-0000-1000-8000-00805f9b34fb";
//    public static String  BLE_SPP_AT_Characteristic = "00002A37-0000-1000-8000-00805f9b34fb";


    public static String CLIENT_CHARACTERISTIC_CONFIG = "0003cdd1-0000-1000-8000-00805f9b0131";

    public static String BLE_SPP_Service = "0003cdd0-0000-1000-8000-00805f9b0131";
    public static String BLE_SPP_Notify_Characteristic = "0003cdd1-0000-1000-8000-00805f9b0131";
    public static String  BLE_SPP_Read_Characteristic = "0003cdd2-0000-1000-8000-00805f9b0131";
    public static String  BLE_SPP_Write_Characteristic = "0003cdd2-0000-1000-8000-00805f9b0131";
    public static String  BLE_SPP_AT_Characteristic = "0003cdd2-0000-1000-8000-00805f9b0131";

}
