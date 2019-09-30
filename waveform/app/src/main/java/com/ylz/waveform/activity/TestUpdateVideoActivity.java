//package com.ylz.waveform.activity;
//
//import android.app.Activity;
//import android.content.Intent;
//import android.net.Uri;
//import android.os.Bundle;
//import android.support.annotation.Nullable;
//import android.support.v7.app.AppCompatActivity;
//import android.view.View;
//import android.widget.Button;
//
//import com.ylz.waveform.R;
//import com.ylz.waveform.tools.ToastUtils;
//
//import java.io.File;
//
//
//public class TestUpdateVideoActivity extends AppCompatActivity {
//
//
//    private Button chooseVideoButton;
//    @Override
//    protected void onCreate(@Nullable Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.test_update_video_layout);
//        chooseVideoButton = (Button)findViewById(R.id.choose_video_button);
//
//        chooseVideoButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent intent = new Intent();
//                intent.setType("video/*");
//                intent.setAction(Intent.ACTION_GET_CONTENT);
//                intent.addCategory(Intent.CATEGORY_OPENABLE);
//                startActivityForResult(intent,900);
//            }
//        });
//    }
//    /**
//     * 视频回调
//     */
//    @Override
//    public void onActivityResult(int requestCode, int resultCode, Intent data) {
//        switch (requestCode) {
//            case 900:
//                if (resultCode == Activity.RESULT_OK) {
//                    try {
//                        Uri uri = data.getData();
//                        uri = BitmapCache.geturi(this, data);
//                        path = getPath(uri);
//                        File file = new File(path);
//                        if (!file.exists()) {
//                            break;
//                        }
//                        if (file.length() > 100 * 1024 * 1024) {
//                            ToastUtils.getLongToastByString(TestUpdateVideoActivity.this,"文件大于100m");
//                            break;
//                        }
//                        //传换文件流，上传
//                        submitVedio();
//                    } catch (Exception e) {
//                    } catch (OutOfMemoryError e) {
//                    }
//                }
//                break;
//
//        }
//
//    }
//
//
//}
//
//
//
