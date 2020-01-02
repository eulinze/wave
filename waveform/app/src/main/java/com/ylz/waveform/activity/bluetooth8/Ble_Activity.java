package com.ylz.waveform.activity.bluetooth8;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import com.ylz.waveform.R;
import com.ylz.waveform.tools.StringUtil;
import com.ylz.waveform.tools.ToastUtils;

import android.app.Activity;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattService;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.media.Image;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;

/**
 * 特别说明：HC_BLE助手是广州汇承信息科技有限公司独自研发的手机APP，方便用户调试08蓝牙模块。
 * 本软件只能支持安卓版本4.3并且有蓝牙4.0的手机使用。
 * 另外对于自家的05、06模块，要使用另外一套蓝牙2.0的手机APP，用户可以在汇承官方网的下载中心自行下载。
 * 本软件提供代码和注释，免费给购买汇承08模块的用户学习和研究，但不能用于商业开发，最终解析权在广州汇承信息科技有限公司。
 * **/

/**
 * @Description:  TODO<Ble_Activity实现连接BLE,发送和接受BLE的数据>
 * @author  广州汇承信息科技有限公司
 * @data:  2014-10-20 下午12:12:04
 * @version:  V1.0
 */
public class Ble_Activity extends Activity implements OnClickListener {

	private final static String TAG = Ble_Activity.class.getSimpleName();
	//蓝牙4.0的UUID,其中0000ffe1-0000-1000-8000-00805f9b34fb是广州汇承信息科技有限公司08蓝牙模块的UUID
	public static String HEART_RATE_MEASUREMENT = "0000ffe1-0000-1000-8000-00805f9b34fb";
	public static String EXTRAS_DEVICE_NAME = "DEVICE_NAME";;
	public static String EXTRAS_DEVICE_ADDRESS = "DEVICE_ADDRESS";
	public static String EXTRAS_DEVICE_RSSI = "RSSI";
	//蓝牙连接状态
	private boolean mConnected = false;
	private String status = "disconnected";
	//蓝牙名字
	private String mDeviceName;
	//蓝牙地址
	private String mDeviceAddress;
	//蓝牙信号值
	private String mRssi;
	private Bundle b;
	private String rev_str = "";
	//蓝牙service,负责后台的蓝牙服务
	private static BluetoothLeService mBluetoothLeService;
	//文本框，显示接受的内容
	private TextView rev_tv, connect_state;
	private TextView youmenNumberTextView;//youmen_number
	private TextView zhuansuNumberTextView;//zhuansu_number
	private TextView dangweiNumberTextView;//dangwei_number

	private ImageView youmen_jianImageView;//youmen_jian
	private ImageView youmen_jiaImageView;//youmen_jia
	private ImageView dangwei_jianImageView;//dangwei_jian
	private ImageView dangwei_jiaImageView;//dangwei_jia


	//发送按钮
	private Button send_btn;
	//文本编辑框
	private EditText send_et;
	private ScrollView rev_sv;
	private ArrayList<ArrayList<BluetoothGattCharacteristic>> mGattCharacteristics = new ArrayList<ArrayList<BluetoothGattCharacteristic>>();
	//蓝牙特征值
	private static BluetoothGattCharacteristic target_chara = null;
	private Handler mhandler = new Handler();
	private Handler myHandler = new Handler()
	{
		// 2.重写消息处理函数
		public void handleMessage(Message msg)
		{
			switch (msg.what)
			{
				// 判断发送的消息
				case 1:
				{
					// 更新View
					String state = msg.getData().getString("connect_state");
					connect_state.setText(state);

					break;
				}

			}
			super.handleMessage(msg);
		}

	};

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.ble_activity_8);
		b = getIntent().getExtras();
		//从意图获取显示的蓝牙信息
		mDeviceName = b.getString(EXTRAS_DEVICE_NAME);
		mDeviceAddress = b.getString(EXTRAS_DEVICE_ADDRESS);
		mRssi = b.getString(EXTRAS_DEVICE_RSSI);

		/* 启动蓝牙service */
		Intent gattServiceIntent = new Intent(this, BluetoothLeService.class);
		bindService(gattServiceIntent, mServiceConnection, BIND_AUTO_CREATE);
		init();

	}

	@Override
	protected void onDestroy()
	{
		super.onDestroy();
		//解除广播接收器
		unregisterReceiver(mGattUpdateReceiver);
		mBluetoothLeService = null;
	}

	// Activity出来时候，绑定广播接收器，监听蓝牙连接服务传过来的事件
	@Override
	protected void onResume()
	{
		super.onResume();
		//绑定广播接收器
		registerReceiver(mGattUpdateReceiver, makeGattUpdateIntentFilter());
		if (mBluetoothLeService != null)
		{
			//根据蓝牙地址，建立连接
			final boolean result = mBluetoothLeService.connect(mDeviceAddress);
			Log.d(TAG, "Connect request result=" + result);
		}
	}

	/**
	 * @Title: init
	 * @Description: TODO(初始化UI控件)
	 * @return void
	 * @throws
	 */
	private void init()
	{
		rev_sv = (ScrollView) this.findViewById(R.id.rev_sv);
		rev_tv = (TextView) this.findViewById(R.id.rev_tv);
		connect_state = (TextView) this.findViewById(R.id.connect_state);
		send_btn = (Button) this.findViewById(R.id.send_btn);
		send_et = (EditText) this.findViewById(R.id.send_et);
		connect_state.setText(status);
		send_btn.setOnClickListener(this);

		youmenNumberTextView= (TextView) this.findViewById(R.id.youmen_number);
		zhuansuNumberTextView= (TextView) this.findViewById(R.id.zhuansu_number);
		dangweiNumberTextView= (TextView) this.findViewById(R.id.dangwei_number);


		youmen_jianImageView= (ImageView) this.findViewById(R.id.youmen_jian);
		youmen_jiaImageView= (ImageView) this.findViewById(R.id.youmen_jia);
		dangwei_jianImageView= (ImageView) this.findViewById(R.id.dangwei_jian);
		dangwei_jiaImageView= (ImageView) this.findViewById(R.id.dangwei_jia);
		youmen_jianImageView.setOnClickListener(this);
		youmen_jiaImageView.setOnClickListener(this);
		dangwei_jianImageView.setOnClickListener(this);
		dangwei_jiaImageView.setOnClickListener(this);

	}

	/* BluetoothLeService绑定的回调函数 */
	private final ServiceConnection mServiceConnection = new ServiceConnection()
	{

		@Override
		public void onServiceConnected(ComponentName componentName,
									   IBinder service)
		{
			mBluetoothLeService = ((BluetoothLeService.LocalBinder) service)
					.getService();
			if (!mBluetoothLeService.initialize())
			{
				Log.e(TAG, "Unable to initialize Bluetooth");
				finish();
			}
			// Automatically connects to the device upon successful start-up
			// initialization.
			// 根据蓝牙地址，连接设备
			mBluetoothLeService.connect(mDeviceAddress);

		}

		@Override
		public void onServiceDisconnected(ComponentName componentName)
		{
			mBluetoothLeService = null;
		}

	};

	/**
	 * 广播接收器，负责接收BluetoothLeService类发送的数据
	 */
	private final BroadcastReceiver mGattUpdateReceiver = new BroadcastReceiver()
	{
		@Override
		public void onReceive(Context context, Intent intent)
		{
			final String action = intent.getAction();
			if (BluetoothLeService.ACTION_GATT_CONNECTED.equals(action))//Gatt连接成功
			{
				mConnected = true;
				status = "connected";
				//更新连接状态
				updateConnectionState(status);
				System.out.println("BroadcastReceiver :" + "device connected");

			} else if (BluetoothLeService.ACTION_GATT_DISCONNECTED//Gatt连接失败
					.equals(action))
			{
				mConnected = false;
				status = "disconnected";
				//更新连接状态
				updateConnectionState(status);
				System.out.println("BroadcastReceiver :"
						+ "device disconnected");

			} else if (BluetoothLeService.ACTION_GATT_SERVICES_DISCOVERED//发现GATT服务器
					.equals(action))
			{
				// Show all the supported services and characteristics on the
				// user interface.
				//获取设备的所有蓝牙服务
				displayGattServices(mBluetoothLeService
						.getSupportedGattServices());
				System.out.println("BroadcastReceiver :"
						+ "device SERVICES_DISCOVERED");
			} else if (BluetoothLeService.ACTION_DATA_AVAILABLE.equals(action))//有效数据
			{
				//处理发送过来的数据
				displayData(intent.getExtras().getString(
						BluetoothLeService.EXTRA_DATA));
//				displayData("1231");
				System.out.println("BroadcastReceiver onData:"
						+ intent.getStringExtra(BluetoothLeService.EXTRA_DATA));
			}
		}
	};

	/* 更新连接状态 */
	private void updateConnectionState(String status)
	{
		Message msg = new Message();
		msg.what = 1;
		Bundle b = new Bundle();
		b.putString("connect_state", status);
		msg.setData(b);
		//将连接状态更新的UI的textview上
		myHandler.sendMessage(msg);
		System.out.println("connect_state:" + status);

	}

	/* 意图过滤器 */
	private static IntentFilter makeGattUpdateIntentFilter()
	{
		final IntentFilter intentFilter = new IntentFilter();
		intentFilter.addAction(BluetoothLeService.ACTION_GATT_CONNECTED);
		intentFilter.addAction(BluetoothLeService.ACTION_GATT_DISCONNECTED);
		intentFilter
				.addAction(BluetoothLeService.ACTION_GATT_SERVICES_DISCOVERED);
		intentFilter.addAction(BluetoothLeService.ACTION_DATA_AVAILABLE);
		return intentFilter;
	}

	/**
	 * @Title: displayData
	 * @Description: TODO(接收到的数据在scrollview上显示)
	 * @param @param rev_string(接受的数据)
	 * @return void
	 * @throws
	 */
	private void displayData(final String rev_string)
	{

		if (rev_string.length() !=4) {
			runOnUiThread(new Runnable() {
				@Override
				public void run() {
					ToastUtils.getShortToastByString(Ble_Activity.this, "数据异常");
				}
			});
		}else {
			runOnUiThread(new Runnable() {
				@Override
				public void run() {
					if (StringUtil.eq("1", rev_string.substring(3))) {
						youmenNumberTextView.setText(rev_string.substring(1, 3));
					} else if (StringUtil.eq("2", rev_string.substring(3))) {
						dangweiNumberTextView.setText(rev_string.substring(1, 3));
					} else {
						zhuansuNumberTextView.setText(rev_string);
					}
				}
			});
		}

		runOnUiThread(new Runnable() {
			@Override
			public void run() {
				rev_str = rev_str+"\n"+rev_string;
				rev_tv.setText(rev_str);
				rev_sv.scrollTo(0, rev_tv.getMeasuredHeight());
				System.out.println("rev:" + rev_str);
			}
		});


	}

	/**
	 * @Title: displayGattServices
	 * @Description: TODO(处理蓝牙服务)
	 * @return void
	 * @throws
	 */
	private void displayGattServices(List<BluetoothGattService> gattServices)
	{

		if (gattServices == null)
			return;
		String uuid = null;
		String unknownServiceString = "unknown_service";
		String unknownCharaString = "unknown_characteristic";

		// 服务数据,可扩展下拉列表的第一级数据
		ArrayList<HashMap<String, String>> gattServiceData = new ArrayList<HashMap<String, String>>();

		// 特征数据（隶属于某一级服务下面的特征值集合）
		ArrayList<ArrayList<HashMap<String, String>>> gattCharacteristicData = new ArrayList<ArrayList<HashMap<String, String>>>();

		// 部分层次，所有特征值集合
		mGattCharacteristics = new ArrayList<ArrayList<BluetoothGattCharacteristic>>();

		// Loops through available GATT Services.
		for (BluetoothGattService gattService : gattServices)
		{

			// 获取服务列表
			HashMap<String, String> currentServiceData = new HashMap<String, String>();
			uuid = gattService.getUuid().toString();

			// 查表，根据该uuid获取对应的服务名称。SampleGattAttributes这个表需要自定义。

			gattServiceData.add(currentServiceData);

			System.out.println("Service uuid:" + uuid);

			ArrayList<HashMap<String, String>> gattCharacteristicGroupData = new ArrayList<HashMap<String, String>>();

			// 从当前循环所指向的服务中读取特征值列表
			List<BluetoothGattCharacteristic> gattCharacteristics = gattService
					.getCharacteristics();

			ArrayList<BluetoothGattCharacteristic> charas = new ArrayList<BluetoothGattCharacteristic>();

			// Loops through available Characteristics.
			// 对于当前循环所指向的服务中的每一个特征值
			for (final BluetoothGattCharacteristic gattCharacteristic : gattCharacteristics)
			{
				charas.add(gattCharacteristic);
				HashMap<String, String> currentCharaData = new HashMap<String, String>();
				uuid = gattCharacteristic.getUuid().toString();

				if (gattCharacteristic.getUuid().toString()
						.equals(HEART_RATE_MEASUREMENT))
				{
					// 测试读取当前Characteristic数据，会触发mOnDataAvailable.onCharacteristicRead()
					mhandler.postDelayed(new Runnable()
					{

						@Override
						public void run()
						{
							// TODO Auto-generated method stub
							mBluetoothLeService
									.readCharacteristic(gattCharacteristic);
						}
					}, 200);

					// 接受Characteristic被写的通知,收到蓝牙模块的数据后会触发mOnDataAvailable.onCharacteristicWrite()
					mBluetoothLeService.setCharacteristicNotification(
							gattCharacteristic, true);
					target_chara = gattCharacteristic;
					// 设置数据内容
					// 往蓝牙模块写入数据
					// mBluetoothLeService.writeCharacteristic(gattCharacteristic);
				}
				List<BluetoothGattDescriptor> descriptors = gattCharacteristic
						.getDescriptors();
				for (BluetoothGattDescriptor descriptor : descriptors)
				{
					System.out.println("---descriptor UUID:"
							+ descriptor.getUuid());
					// 获取特征值的描述
					mBluetoothLeService.getCharacteristicDescriptor(descriptor);
					// mBluetoothLeService.setCharacteristicNotification(gattCharacteristic,
					// true);
				}

				gattCharacteristicGroupData.add(currentCharaData);
			}
			// 按先后顺序，分层次放入特征值集合中，只有特征值
			mGattCharacteristics.add(charas);
			// 构件第二级扩展列表（服务下面的特征值）
			gattCharacteristicData.add(gattCharacteristicGroupData);

		}

	}
	/**
	 * 将数据分包
	 *
	 * **/
	public int[] dataSeparate(int len)
	{
		int[] lens = new int[2];
		lens[0]=len/20;
		lens[1]=len-20*lens[0];
		return lens;
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
			case R.id.send_btn:
				sendMessage(send_et.getText().toString());
				break;
			case R.id.youmen_jian:
				sendMessage("2");
				break;
			case R.id.youmen_jia:
				sendMessage("3");
				break;
			case R.id.dangwei_jian:
				sendMessage("4");
				break;
			case R.id.dangwei_jia:
				sendMessage("5");
				break;
			default:
				break;
		}
	}


	public void sendMessage(String sendString){
		// TODO Auto-generated method stub
		byte[] buff = sendString.getBytes();
		int len = buff.length;
		int[] lens = dataSeparate(len);
		for(int i =0;i<lens[0];i++)
		{
			String str = new String(buff, 20*i, 20);
			target_chara.setValue(str);//只能一次发送20字节，所以这里要分包发送
			//调用蓝牙服务的写特征值方法实现发送数据
			mBluetoothLeService.writeCharacteristic(target_chara);
		}
		if(lens[1]!=0)
		{
			String str = new String(buff, 20*lens[0], lens[1]);
			target_chara.setValue(str);
			//调用蓝牙服务的写特征值方法实现发送数据
			mBluetoothLeService.writeCharacteristic(target_chara);
		}
	}
}
