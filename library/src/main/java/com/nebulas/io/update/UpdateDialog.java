package com.nebulas.io.update;

import android.app.Dialog;
import android.content.Context;
import android.support.annotation.NonNull;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import com.nebulas.io.R;
import com.nebulas.io.util.Utils;


public class UpdateDialog extends Dialog implements View.OnClickListener {

    private Button update;
    private OnButtonClickListener buttonClickListener;
    private UpdateInfo updateInfo;

    public UpdateDialog(@NonNull Context context, OnButtonClickListener onButtonClickListener,UpdateInfo updateInfo) {
        super(context, R.style.UpdateDialog);
        this.buttonClickListener = onButtonClickListener;
        this.updateInfo = updateInfo;
        init(context);
    }

    /**
     * 初始化布局
     */
    private void init(Context context) {
        View view = LayoutInflater.from(context).inflate(R.layout.dialog_update, null);
        setContentView(view);
        setWindowSize(context);
        initView(view);
    }

    private void initView(View view) {
        View ibClose = view.findViewById(R.id.ib_close);
        TextView description = view.findViewById(R.id.tv_description);
        update = view.findViewById(R.id.btn_update);
        update.setOnClickListener(this);
        ibClose.setOnClickListener(this);
        //设置界面数据
        description.setText(updateInfo.getUpdateText());
    }

    private void setWindowSize(Context context) {
        Window dialogWindow = this.getWindow();
        WindowManager.LayoutParams lp = dialogWindow.getAttributes();
        lp.width = (int) (Utils.getScreenWith(context) * 0.7f);
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        lp.gravity = Gravity.CENTER;
        dialogWindow.setAttributes(lp);
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.ib_close) {
            if (buttonClickListener != null) {
                buttonClickListener.onButtonClick(OnButtonClickListener.CANCEL);
            }
        } else if (id == R.id.btn_update) {
            //回调点击事件
            if (buttonClickListener != null) {
                buttonClickListener.onButtonClick(OnButtonClickListener.UPDATE);
            }
        }
    }
}
