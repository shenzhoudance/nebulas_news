package com.nebulas.io.ui;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.nebulas.io.R;
import com.nebulas.io.util.Utils;


public class CustomDialog extends Dialog {

    public CustomDialog(Context context) {
        super(context);
    }

    public CustomDialog(Context context, int theme) {
        super(context, theme);
    }

    public static class Builder {
        private Context context;
        private LayoutInflater inflater;
        private String title;
        private CharSequence message;
        private String positiveButtonText;
        private String negativeButtonText;
        private String neutralButtonText;
        private View contentView;
        private OnClickListener positiveButtonClickListener;
        private OnClickListener negativeButtonClickListener;
        private OnClickListener neutralButtonClickListener;

        private DialogInterface.OnKeyListener keylistener;
        private boolean cancleable = true;
        private View btnDivider;
        private View btnDivider1;
        private View btnDivider2;
        private Button positiveBtn;
        private Button negativeBtn;
        private Button neurtalBtn;
        private LinearLayout.LayoutParams layoutParams;
        private boolean autoDismiss = true;

        public Builder setAutoDismiss(boolean autoDismiss) {
            this.autoDismiss = autoDismiss;
            return this;
        }

        public void setNegativeBtnEnable(boolean enable) {
            if (negativeBtn != null) {
                negativeBtn.setEnabled(enable);
            }
        }

        public Builder setLayoutParams(LinearLayout.LayoutParams layoutParams) {
            if (layoutParams != null) {
                this.layoutParams = layoutParams;
            }
            return this;
        }

        public Builder(Context context) {
            this.context = context;
            inflater = (LayoutInflater) context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        public Builder setMessage(CharSequence message) {
            this.message = message;
            return this;
        }

        public Builder setTitle(String title) {
            this.title = title;
            return this;
        }

        public Builder setContentView(View v) {
            this.contentView = v;
            return this;
        }

        public Builder setContentView(int id) {
            this.contentView = inflater.inflate(id, null);
            return this;
        }

        public Builder setPositiveButton(String positiveButtonText,
                                         OnClickListener listener) {
            this.positiveButtonText = positiveButtonText;
            this.positiveButtonClickListener = listener;
            return this;
        }

        public Builder setNegativeButton(String negativeButtonText,
                                         OnClickListener listener) {
            this.negativeButtonText = negativeButtonText;
            this.negativeButtonClickListener = listener;
            return this;
        }

        public Builder setOnKeyListener(DialogInterface.OnKeyListener listener) {
            this.keylistener = listener;
            return this;
        }

        public Builder setNetureButton(String neutralButtonText,
                                       OnClickListener listener) {
            this.neutralButtonText = neutralButtonText;
            this.neutralButtonClickListener = listener;
            return this;
        }

        public Builder setCancelable(boolean cancleable) {
            this.cancleable = cancleable;
            return this;
        }

        public CustomDialog create() {
            final CustomDialog dialog = new CustomDialog(context, R.style.alert_dialog);
            View layout = inflater.inflate(R.layout.common_dialog_layout, null);
            btnDivider = layout.findViewById(R.id.common_dialog_divider);
            btnDivider1 = layout.findViewById(R.id.common_dialog_divider1);
            btnDivider2 = layout.findViewById(R.id.common_dialog_divider2);
            positiveBtn = (Button) layout.findViewById(R.id.common_dialog_positive);
            negativeBtn = (Button) layout.findViewById(R.id.common_dialog_negitive);
            neurtalBtn = (Button) layout.findViewById(R.id.common_dialog_neture);
            dialog.addContentView(layout, new LayoutParams(
                    LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
            if (!TextUtils.isEmpty(title)) {
                ((TextView) layout.findViewById(R.id.common_dialog_title)).setText(title);
            } else {
                layout.findViewById(R.id.common_dialog_title).setVisibility(View.GONE);
                layout.findViewById(R.id.common_title_divider).setVisibility(View.GONE);
            }
            if (keylistener != null) {
                dialog.setOnKeyListener(keylistener);
            }
            dialog.setCancelable(cancleable);
            if (positiveButtonText != null) {
                positiveBtn.setText(positiveButtonText);

                positiveBtn.setOnClickListener(new View.OnClickListener() {
                    public void onClick(View v) {
                        if (positiveButtonClickListener != null) {
                            positiveButtonClickListener.onClick(dialog,
                                    DialogInterface.BUTTON_POSITIVE);
                        }
                        if (autoDismiss)
                            dialog.dismiss();
                    }
                });
            } else {
                btnDivider.setVisibility(View.GONE);
                positiveBtn.setVisibility(View.GONE);
            }
            if (negativeButtonText != null) {
                negativeBtn.setText(negativeButtonText);
                negativeBtn.setOnClickListener(new View.OnClickListener() {
                    public void onClick(View v) {
                        if (negativeButtonClickListener != null) {
                            negativeButtonClickListener.onClick(dialog,
                                    DialogInterface.BUTTON_NEGATIVE);
                        }
                        if (autoDismiss)
                            dialog.dismiss();
                    }
                });
            } else {
                btnDivider.setVisibility(View.GONE);
                negativeBtn.setVisibility(View.GONE);
            }

            if (neutralButtonText != null) {
                neurtalBtn.setText(neutralButtonText);
                neurtalBtn.setOnClickListener(new View.OnClickListener() {
                    public void onClick(View v) {
                        if (neutralButtonClickListener != null) {
                            neutralButtonClickListener.onClick(dialog,
                                    DialogInterface.BUTTON_NEUTRAL);
                        }
                        if (autoDismiss)
                            dialog.dismiss();
                    }
                });
            } else {
                btnDivider1.setVisibility(View.GONE);
                neurtalBtn.setVisibility(View.GONE);
            }

            if (positiveButtonText == null && negativeButtonText == null && neutralButtonText == null) {
                btnDivider2.setVisibility(View.GONE);
            }

            if (message != null) {
                ((TextView) layout.findViewById(R.id.common_dialog_content)).setText(message);
            } else if (contentView != null) {
                ((LinearLayout) layout.findViewById(R.id.common_dialog_con))
                        .removeAllViews();
                if (layoutParams == null) {
                    LinearLayout.LayoutParams addLayoutParams = new LinearLayout.LayoutParams(
                            LayoutParams.MATCH_PARENT,
                            LayoutParams.MATCH_PARENT);
                    addLayoutParams.setMargins(Utils.dip2px(context, 20), 0, Utils.dip2px(context, 20), 0);
                    ((LinearLayout) layout.findViewById(R.id.common_dialog_con)).addView(
                            contentView, addLayoutParams);
                } else {
                    ((LinearLayout) layout.findViewById(R.id.common_dialog_con)).addView(
                            contentView, layoutParams);
                }
            } else {
                layout.findViewById(R.id.common_dialog_con).setVisibility(View.GONE);
            }
            dialog.setContentView(layout);
            return dialog;
        }

    }


}
