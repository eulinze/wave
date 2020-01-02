package com.ylz.waveform.activity.bluetooth103;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import com.ylz.waveform.R;
import com.ylz.waveform.tools.StringUtil;
import java.util.Timer;
import java.util.TimerTask;
import static com.ylz.waveform.application.App.EXTRAS_DEVICE_ADDRESS;
import static com.ylz.waveform.application.App.EXTRAS_DEVICE_NAME;

public class ShowReceiveActivity extends Activity implements View.OnClickListener {
    private final static String TAG = ShowReceiveActivity.class.getSimpleName();

    static long recv_cnt = 0;

    private String mDeviceName;
    private String mDeviceAddress;
    private BluetoothLeService mBluetoothLeService;
    private boolean mConnected = false;

    private TextView mDataRecvText;
    private TextView mRecvBytes;
    private TextView mDataRecvFormat;
    private EditText mEditBox;
    private TextView mSendBytes;
    private TextView mDataSendFormat;
    private TextView mNotify_speed_text;

    private long recvBytes=0;
    private long lastSecondBytes=0;
    private long sendBytes;
    private StringBuilder mData;

    int sendIndex = 0;
    int sendDataLen=0;
    byte[] sendBuf;

    //测速
    private Timer timer;
    private TimerTask task;

    // Code to manage Service lifecycle.
    private final ServiceConnection mServiceConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName componentName, IBinder service) {
            mBluetoothLeService = ((BluetoothLeService.LocalBinder) service).getService();
            if (!mBluetoothLeService.initialize()) {
                Log.e(TAG, "Unable to initialize Bluetooth");
                finish();
            }
            mBluetoothLeService.connect(mDeviceAddress);
        }
        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            mBluetoothLeService = null;
        }
    };

    private final BroadcastReceiver mGattUpdateReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();
            if (BluetoothLeService.ACTION_GATT_CONNECTED.equals(action)) {

            } else if (BluetoothLeService.ACTION_GATT_DISCONNECTED.equals(action)) {
                mConnected = false;
                updateConnectionState(R.string.disconnected);
                invalidateOptionsMenu();
                mBluetoothLeService.connect(mDeviceAddress);
            } else if (BluetoothLeService.ACTION_GATT_SERVICES_DISCOVERED.equals(action)) {
                //特征值找到才代表连接成功
                mConnected = true;
                invalidateOptionsMenu();
                updateConnectionState(R.string.connected);
            }else if (BluetoothLeService.ACTION_GATT_SERVICES_NO_DISCOVERED.equals(action)){
                mBluetoothLeService.connect(mDeviceAddress);
            }

            else if (BluetoothLeService.ACTION_DATA_AVAILABLE.equals(action)) {
                String obj = intent.getExtras().getString(BluetoothLeService.EXTRA_DATA);
                byte[] array1 = obj.getBytes();
                displayData(array1);
            }

            else if (BluetoothLeService.ACTION_WRITE_SUCCESSFUL.equals(action)) {
                mSendBytes.setText(sendBytes + " ");
                if (sendDataLen>0)
                {
                    Log.v("log","Write OK,Send again");
                }
                else {
                    Log.v("log","Write Finish");
                }
            }
        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // setContentView(R.layout.gatt_services_characteristics);
        setContentView(R.layout.ble_spp_test);
        //获取蓝牙的名字和地址
        final Intent intent = getIntent();
        mDeviceName = intent.getStringExtra(EXTRAS_DEVICE_NAME);
        mDeviceAddress = intent.getStringExtra(EXTRAS_DEVICE_ADDRESS);

        mDataRecvText = (TextView) findViewById(R.id.data_read_text);
        mRecvBytes = (TextView) findViewById(R.id.byte_received_text);
        mDataRecvFormat = (TextView) findViewById(R.id.data_received_format);
        mEditBox = (EditText) findViewById(R.id.data_edit_box);
        mSendBytes = (TextView) findViewById(R.id.byte_send_text);
        mDataSendFormat = (TextView) findViewById(R.id.data_sended_format);
        mNotify_speed_text = (TextView) findViewById(R.id.notify_speed_text);

        Button mSendBtn = (Button) findViewById(R.id.send_data_btn);
        Button mCleanBtn = (Button) findViewById(R.id.clean_data_btn);
        Button mCleanTextBtn = (Button) findViewById(R.id.clean_text_btn);

        mDataRecvFormat.setOnClickListener(this);
        mDataSendFormat.setOnClickListener(this);
        mRecvBytes.setOnClickListener(this);
        mSendBytes.setOnClickListener(this);

        mCleanBtn.setOnClickListener(this);
        mSendBtn.setOnClickListener(this);
        mCleanTextBtn.setOnClickListener(this);
        mDataRecvText.setMovementMethod(ScrollingMovementMethod.getInstance());
        mData = new StringBuilder();

        final int SPEED = 1;
        final Handler handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                switch (msg.what) {
                    case SPEED:
                        lastSecondBytes = recvBytes - lastSecondBytes;
                        mNotify_speed_text.setText(String.valueOf(lastSecondBytes)+ " B/s");
                        lastSecondBytes = recvBytes;
                        break;
                }
            }
        };

        task = new TimerTask() {
            @Override
            public void run() {
                Message message = new Message();
                message.what = SPEED;
                message.obj = System.currentTimeMillis();
                handler.sendMessage(message);
            }
        };
        timer = new Timer();
        // 参数：
        // 1000，延时1秒后执行。
        // 1000，每隔2秒执行1次task。
        timer.schedule(task, 1000, 1000);

        Intent gattServiceIntent = new Intent(this, BluetoothLeService.class);
        bindService(gattServiceIntent, mServiceConnection, BIND_AUTO_CREATE);

        registerReceiver(mGattUpdateReceiver, makeGattUpdateIntentFilter());
        if (mBluetoothLeService != null) {
            final boolean result = mBluetoothLeService.connect(mDeviceAddress);
            Log.e(TAG, "Connect request result=" + result);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(mGattUpdateReceiver);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbindService(mServiceConnection);
        mBluetoothLeService = null;
    }

    private void updateConnectionState(final int resourceId) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(ShowReceiveActivity.this,"链接成功",Toast.LENGTH_LONG).show();
            }
        });
    }

    private static IntentFilter makeGattUpdateIntentFilter() {
        final IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(BluetoothLeService.ACTION_GATT_CONNECTED);
        intentFilter.addAction(BluetoothLeService.ACTION_GATT_DISCONNECTED);
        intentFilter.addAction(BluetoothLeService.ACTION_GATT_SERVICES_DISCOVERED);
        intentFilter.addAction(BluetoothLeService.ACTION_DATA_AVAILABLE);
        intentFilter.addAction(BluetoothLeService.ACTION_WRITE_SUCCESSFUL);
        intentFilter.addAction(BluetoothLeService.ACTION_GATT_SERVICES_NO_DISCOVERED);
        return intentFilter;
    }

    //动态效果
    public void convertText(final TextView textView, final int convertTextId) {
        final Animation scaleIn = AnimationUtils.loadAnimation(this,
                R.anim.text_scale_in);
        Animation scaleOut = AnimationUtils.loadAnimation(this,
                R.anim.text_scale_out);
        scaleOut.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                textView.setText(convertTextId);
                textView.startAnimation(scaleIn);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }
        });
        textView.startAnimation(scaleOut);
    }

    private void getSendBuf(){
        sendIndex = 0;
        if (mDataSendFormat.getText().equals(getResources().getString(R.string.data_format_default))) {
            sendBuf = mEditBox.getText().toString().trim().getBytes();
        } else {
            sendBuf = StringUtil.stringToBytes(StringUtil.getHexString(mEditBox.getText().toString()));
        }
        sendDataLen = sendBuf.length;

        onSendBtnClicked();
    }
    private void onSendBtnClicked() {
        if (sendDataLen>20) {
            sendBytes += 20;
            final byte[] buf = new byte[20];
            // System.arraycopy(buffer, 0, tmpBuf, 0, writeLength);
            for (int i=0;i<20;i++)
            {
                buf[i] = sendBuf[sendIndex+i];
            }
            sendIndex+=20;
            mBluetoothLeService.writeCharacteristicGattDb(buf);
            sendDataLen -= 20;
        }
        else {
            sendBytes += sendDataLen;
            final byte[] buf = new byte[sendDataLen];
            for (int i=0;i<sendDataLen;i++)
            {
                buf[i] = sendBuf[sendIndex+i];
            }
            mBluetoothLeService.writeCharacteristicGattDb(buf);
            sendDataLen = 0;
            sendIndex = 0;
        }
    }

//    private void displayData(byte[] buf) {
//        Log.e("buf",buf.length+"");
//        recvBytes += buf.length;
//        recv_cnt += buf.length;
//        if (recv_cnt>=1024)
//        {
//            recv_cnt = 0;
//            mData.delete(0,mData.length()/2); //UI界面只保留512个字节，免得APP卡顿
//        }
//        if (mDataRecvFormat.getText().equals("Ascii")) {
//            String s =StringUtil.asciiToString(buf)+" "+buf.length+"\n";
//            mData.append(s);
//        } else {
//            String s = StringUtil.bytesToString(buf)+" "+buf.length+"\n";
//            mData.append(s);
//        }
//        mDataRecvText.setText(mData.toString());
//        mRecvBytes.setText(recvBytes + " ");
//    }

    private void displayData(byte[] buf) {
        recvBytes += buf.length;
        recv_cnt += buf.length;
        if (recv_cnt>=1024)
        {
            recv_cnt = 0;
        }
        if (buf.length==16){
            byte[] messageTypeByte = new byte[1];
            messageTypeByte[0] = buf[1];

            mData.append(StringUtil.bytesToString(buf)+"\n");

            if ((StringUtil.bytesToString(messageTypeByte).replace(" ", "").equals("00") )) {
                mData.append("链接成功");
            }

            if ((StringUtil.bytesToString(messageTypeByte).replace(" ", "").equals("02") )) {
                mData.append("链接断开");
            }
            if ((StringUtil.bytesToString(messageTypeByte).replace(" ", "").equals("01") )) {

                byte[] indexBuf = new byte[2];
                indexBuf[0]=buf[2];
                indexBuf[1]=buf[3];
                String indexString16 = StringUtil.bytesToString(indexBuf).replace(" ","");
                String indexString = Integer.parseInt(indexString16,16)+"";

                byte[] yIntegerBuf = new byte[2];
                yIntegerBuf[0]=buf[4];
                yIntegerBuf[1]=buf[5];
                String yIntegerString16 = StringUtil.bytesToString(yIntegerBuf).replace(" ","");
                String yIntegerString= Integer.parseInt(yIntegerString16,16)+"";

                byte[] yDecimalBuf = new byte[2];
                yDecimalBuf[0]=buf[6];
                yDecimalBuf[1]=buf[7];
                String yDecimalString16 = StringUtil.bytesToString(yDecimalBuf).replace(" ","");
                String yDecimalString= Integer.parseInt(yDecimalString16,16)+"";

                String ySymbolString = "";
                byte[] ySymbolByte = new byte[1];
                ySymbolByte[0] = buf[8];
                if (StringUtil.eq("00",StringUtil.bytesToString(ySymbolByte).replace(" ",""))){
                    ySymbolString = "";
                }else{
                    ySymbolString = "-";
                }

                byte[] xCoordinateBuf = new byte[2];
                xCoordinateBuf[0]=buf[9];
                xCoordinateBuf[1]=buf[10];
                String xCoordinateString16 = StringUtil.bytesToString(xCoordinateBuf).replace(" ","");
                String xCoordinateString= Integer.parseInt(xCoordinateString16,16)+"";

                String xSymbolString = "";
                byte[] xSymbolByte = new byte[1];
                xSymbolByte[0] = buf[11];
                if (StringUtil.eq("00",StringUtil.bytesToString(xSymbolByte).replace(" ",""))){
                    xSymbolString = "";
                }else{
                    xSymbolString = "-";
                }

                byte[] isEndByte = new byte[1];
                isEndByte[0] = buf[14];
                if (StringUtil.eq("01",StringUtil.bytesToString(isEndByte).replace(" ",""))){
                    Toast.makeText(ShowReceiveActivity.this,"数据传输完毕",Toast.LENGTH_LONG).show();
                }

                mData.append("第"+indexString+"位采集点，X轴坐标 ："+xSymbolString+xCoordinateString+
                        ",Y轴坐标 ："+ySymbolString+yDecimalString+"\n\n");
            }
        }else{
            return;
//            Toast.makeText(ShowReceiveActivity.this,"数据异常",Toast.LENGTH_SHORT).show();
        }
        mDataRecvText.setText(mData.toString());
        mRecvBytes.setText(recvBytes + " ");
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.data_received_format:
                if (mDataRecvFormat.getText().equals(getResources().getString(R.string.data_format_default))) {
                    convertText(mDataRecvFormat, R.string.data_format_hex);
                } else {
                    convertText(mDataRecvFormat,R.string.data_format_default);
                }
                break;

            case R.id.data_sended_format:
                if (mDataSendFormat.getText().equals(getResources().getString(R.string.data_format_default)))  {
                    convertText(mDataSendFormat, R.string.data_format_hex);
                } else {
                    convertText(mDataSendFormat, R.string.data_format_default);
                }
                break;
            case R.id.byte_received_text:
                recvBytes = 0;
                lastSecondBytes=0;
                convertText(mRecvBytes, R.string.zero);
                break;

            case R.id.byte_send_text:
                sendBytes = 0;
                convertText(mSendBytes, R.string.zero);
                break;

            case R.id.send_data_btn:
                getSendBuf();
                break;
            case R.id.clean_data_btn:
                mData.delete(0, mData.length());
                mDataRecvText.setText(mData.toString());
                break;
            case R.id.clean_text_btn:
                mEditBox.setText("");
                break;
            default:
                break;
        }
    }
}
