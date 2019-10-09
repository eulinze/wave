package com.ylz.waveform.presswavecore.dialog;

import android.app.AlertDialog;
import android.content.Context;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import com.ylz.waveform.R;
import com.ylz.waveform.presswavecore.utils.ToastUtils;

public class SavePointDialog {

    private AlertDialog alertDialog;

    private Context context;

    public SavePointDialog(final Context context) {
        this.context = context;
        alertDialog = new AlertDialog.Builder(context).create();
        View view = View.inflate(context, R.layout.dialog_save_point,null);
        alertDialog.setView(view);
        alertDialog.setCanceledOnTouchOutside(false);
        final EditText etName = view.findViewById(R.id.et_name);
        final EditText etRemark = view.findViewById(R.id.et_remark);
        Button btnSave = view.findViewById(R.id.btn_save);
        Button btnCancel = view.findViewById(R.id.btn_cancel);
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dissmiss();
            }
        });
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = etName.getText().toString();
                String remark = etRemark.getText().toString();
                if(!"".equals(name)){
                    if(null != saveBtnClickListener){
                        saveBtnClickListener.onSaveBtnClick(name,remark);
                    }
                }else{
                    ToastUtils.toast(context,"名称不能为空");
                }
            }
        });
    }

    public void show(){
        alertDialog.show();
    }
    public void dissmiss(){
        alertDialog.dismiss();
    }

    public interface SaveBtnClickListener{
        void onSaveBtnClick(String name, String remark);
    }
    private SaveBtnClickListener saveBtnClickListener;

    public void setSaveBtnClickListener(SaveBtnClickListener saveBtnClickListener) {
        this.saveBtnClickListener = saveBtnClickListener;
    }
}
