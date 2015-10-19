/**
 * 
 */
package com.bestpay_aa.view;

import com.bestpay_aa.activity.R;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.text.method.ScrollingMovementMethod;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * @ClassName    : 
 * @Description  : <类描述>
 * @Developer    : <zcx> 
 * @Modifier     : <修改者姓名>
 * @EditContent  : <修改内容>
 * @EditDate    : 2013-4-28 下午2:47:52 [created]
 */
public class CustomProgressBarDialog_1  extends Dialog{



	private  static TextView tittle_text;
	private static TextView message_text;
	private static Button btn_loading_back;
	
	private int two=0;
	 private boolean t_cancel=false;
		public CustomProgressBarDialog_1(Context context, int theme,boolean b) {		
	        super(context, theme);
	        t_cancel=b;
	    }
    public CustomProgressBarDialog_1(Context context, int theme) {
        super(context, theme);
    }

    public CustomProgressBarDialog_1(Context context) {
        super(context);
    }
    
    public CustomProgressBarDialog_1(Context context,boolean b) {
        super(context);
        t_cancel=b;
    }

	public void setMessage(int id) {
		// TODO Auto-generated method stub
		message_text.setText(id);
		
	}
	public void setMessage(String str) {
		// TODO Auto-generated method stub
		message_text.setText(str);
		
	}
    /**
     * Helper class for creating a custom dialog
     */
    public static class Builder {

        private Context context;
        private String title;
        private String message;
      
        private View contentView;

        private boolean cancel;
        private DialogInterface.OnCancelListener cancelListener;
        private DialogInterface.OnClickListener positiveButtonClickListener;

        public Builder(Context context) {
            this.context = context;
        }


        public Builder(Context context,boolean b) {
            this.context = context;
            cancel=b;
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

        public Builder setCancel(Boolean b){
        	this.cancel=b;
        	return this;
        }
        
        /**
         * 
         * @param listener
         * @return
         */
        public Builder setCancelListener(DialogInterface.OnCancelListener listener) {
            this.cancelListener = listener;
            return this;
        }
        public Builder setPositiveButton(
                DialogInterface.OnClickListener listener) {
            this.positiveButtonClickListener = listener;
            return this;
        }
        /**
         * Create the custom dialog
         */
        public CustomProgressBarDialog_1 create() {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            // instantiate the dialog with the custom Theme
            final CustomProgressBarDialog_1 dialog = new CustomProgressBarDialog_1(context,R.style.Dialog,cancel);
            View layout = inflater.inflate(R.layout.progresslayout, null);
            dialog.addContentView(layout, new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT));
            // set the dialog title
            tittle_text=((TextView) layout.findViewById(R.id.progress_title));
            tittle_text.setText(title);
            message_text=((TextView) layout.findViewById(R.id.progress_message));
            message_text.setText(message);
//            btn_loading_back = ((Button) layout.findViewById(R.id.btn_loading_back));
//            btn_loading_back.setOnClickListener(new View.OnClickListener() {
//                public void onClick(View v) {
//                    positiveButtonClickListener.onClick(
//                    		dialog, 
//                            DialogInterface.BUTTON_POSITIVE);
//                }
//            });
            ((TextView) layout.findViewById(R.id.progress_message)).setMovementMethod(ScrollingMovementMethod.getInstance());
            dialog.setCancelable(cancel);
            dialog.setOnCancelListener(cancelListener);
            dialog.setCanceledOnTouchOutside(false);
           
            // set the content message
            if (message != null) {
                ((TextView) layout.findViewById(
                		R.id.progress_message)).setText(message);
            } else if (contentView != null) {
                // if no message set
                // add the contentView to the dialog body
                ((LinearLayout) layout.findViewById(R.id.content))
                        .removeAllViews();
                ((LinearLayout) layout.findViewById(R.id.content))
                        .addView(contentView, 
                                new LayoutParams(
                                        LayoutParams.WRAP_CONTENT, 
                                        LayoutParams.WRAP_CONTENT));
            }
            dialog.setContentView(layout);
            return dialog;
        }

    }
    
    @Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		
		 if (keyCode == KeyEvent.KEYCODE_BACK) { 
			 if(!t_cancel){
				 two++;
				 if(two>10)
					 t_cancel=true;
				 return true; 
			 }
				
		 }
		// TODO Auto-generated method stub
		return super.onKeyDown(keyCode, event);
	}

}
