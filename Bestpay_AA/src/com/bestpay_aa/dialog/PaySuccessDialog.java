package com.bestpay_aa.dialog;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Handler;
import android.view.GestureDetector;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;

import com.bestpay_aa.activity.R;
/**
 * 付款成功
 * @author zhouchaoxin
 *
 */
public class PaySuccessDialog extends Dialog {

	 private boolean t_cancel;
	 
	 
	public PaySuccessDialog(Context context, int theme,boolean b) {		
        super(context, theme);
        t_cancel=b;
    }
    public PaySuccessDialog(Context context, int theme) {
        super(context, theme);
    }

    public PaySuccessDialog(Context context) {
        super(context);
       
    }

    /**
     * Helper class for creating a custom dialog
     */
    public static class Builder {

        private Context context;
        private String title;
        private String message;
        private String positiveButtonText;
        private String negativeButtonText;
        private View contentView;
        private boolean cancel;


        private DialogInterface.OnClickListener 
                        positiveButtonClickListener,
                        negativeButtonClickListener;

        public Builder(Context context) {
            this.context = context;
        }
        public Builder(Context context,int theme) {
            this.context = context;
        }
        public Builder(Context context,boolean cancel) {
            this.context = context;
            this.cancel=cancel;
        }

        /**
         * Set the Dialog message from String
         * @param title
         * @return
         */
        public Builder setMessage(String message) {
            this.message = message;
            return this;
        }

        
        /**
         * Set the Dialog message from resource
         * @param title
         * @return
         */
        public Builder setMessage(int message) {
            this.message = (String) context.getText(message);
            return this;
        }

        /**
         * Set the Dialog title from resource
         * @param title
         * @return
         */
        public Builder setTitle(int title) {
            this.title = (String) context.getText(title);
            return this;
        }

        /**
         * Set the Dialog title from String
         * @param title
         * @return
         */
        public Builder setTitle(String title) {
            this.title = title;
            return this;
        }
        
        
        /**
         * Set the Dialog title from String
         * @param title
         * @return
         */
        public Builder setCancel(Boolean b) {
            this.cancel = b;
            return this;
        }
        /**
         * Set a custom content view for the Dialog.
         * If a message is set, the contentView is not
         * added to the Dialog...
         * @param v
         * @return
         */
        public Builder setContentView(View v) {
            this.contentView = v;
            return this;
        }

        /**
         * Set the positive button resource and it's listener
         * @param positiveButtonText
         * @param listener
         * @return
         */
        public Builder setPositiveButton(int positiveButtonText,
                DialogInterface.OnClickListener listener) {
            this.positiveButtonText = (String) context
                    .getText(positiveButtonText);
            this.positiveButtonClickListener = listener;
            return this;
        }

        /**
         * Set the positive button text and it's listener
         * @param positiveButtonText
         * @param listener
         * @return
         */
        public Builder setPositiveButton(String positiveButtonText,
                DialogInterface.OnClickListener listener) {
            this.positiveButtonText = positiveButtonText;
            this.positiveButtonClickListener = listener;
            return this;
        }

        /**
         * Set the negative button resource and it's listener
         * @param negativeButtonText
         * @param listener
         * @return
         */
        public Builder setNegativeButton(int negativeButtonText,
                DialogInterface.OnClickListener listener) {
            this.negativeButtonText = (String) context
                    .getText(negativeButtonText);
            this.negativeButtonClickListener = listener;
            return this;
        }

        /**
         * Set the negative button text and it's listener
         * @param negativeButtonText
         * @param listener
         * @return
         */
        public Builder setNegativeButton(String negativeButtonText,
                DialogInterface.OnClickListener listener) {
            this.negativeButtonText = negativeButtonText;
            this.negativeButtonClickListener = listener;
            return this;
        }

        /**
         * Create the custom dialog
         */
        public PaySuccessDialog create() {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View layout = inflater.inflate(R.layout.paysucdialog, null);
            final PaySuccessDialog dialog = new PaySuccessDialog(context,R.style.Dialog);
            ImageView img_success = (ImageView)layout.findViewById(R.id.img_success);
            img_success.setOnClickListener(new View.OnClickListener() {
				
				@Override
				public void onClick(View v) {
				
					dialog.dismiss();
				}
			});
            
            dialog.setContentView(layout);
            return dialog;
        }

    }

    @Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		
		 if (keyCode == KeyEvent.KEYCODE_BACK) { 
			 if(!t_cancel)
				 return true;
		 }
		// TODO Auto-generated method stub
		return super.onKeyDown(keyCode, event);
	}
}