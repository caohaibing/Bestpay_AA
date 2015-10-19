/**
 * 
 */
package com.bestpay_aa.view;

import com.bestpay_aa.activity.R;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Build;
import android.text.Spanned;
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
 * @EditDate    : 2013-4-28 下午4:02:12 [created]
 */
public class CustomDialog2  extends Dialog {


	public CustomDialog2(Context context, int theme) {
		super(context, theme);
	}

	public CustomDialog2(Context context) {
		super(context);
	}

	/**
	 * Helper class for creating a custom dialog
	 */
	public static class Builder {

		private Context context;
		private View dialogBodyView;
		private String title;
		private String message;
		private Spanned message1;
		private String positiveButtonText;
		private String negativeButtonText;
		private View contentView;
		private Boolean cancelFlag;
		private DialogInterface.OnCancelListener cancelListener;

		private DialogInterface.OnClickListener positiveButtonClickListener,
				negativeButtonClickListener;
		private DialogInterface.OnKeyListener onKeyListener;

		public Builder(Context context) {
			this.context = context;
			this.cancelFlag = false;
		}

		public Builder(Context context, boolean cancel) {
			this.context = context;
			this.cancelFlag = cancel;
		}

		/**
		 * Set the Dialog message from String
		 * 
		 * @param title
		 * @return
		 */
		public Builder setMessage(String message) {
			this.message = message;
			return this;
		}

		public Builder setMessage(Spanned message) {
			this.message1 = message;
			return this;
		}

		/**
		 * Set the Dialog message from resource
		 * 
		 * @param title
		 * @return
		 */
		public Builder setMessage(int message) {
			this.message = (String) context.getText(message);
			return this;
		}

		/**
		 * Set the Dialog title from resource
		 * 
		 * @param title
		 * @return
		 */
		public Builder setTitle(int title) {
			this.title = (String) context.getText(title);
			return this;
		}

		/**
		 * Set the Dialog title from String
		 * 
		 * @param title
		 * @return
		 */
		public Builder setTitle(String title) {
			this.title = title;
			return this;
		}

		/**
		 * Set the cancel
		 * 
		 * @param title
		 * @return
		 */
		public Builder setCancel(Boolean b) {
			this.cancelFlag = b;
			return this;
		}

		/**
		 * 
		 * @param listener
		 * @return
		 */
		public Builder setCancelListener(
				DialogInterface.OnCancelListener listener) {
			this.cancelListener = listener;
			return this;
		}

		public Builder setOnKeyListener(DialogInterface.OnKeyListener listener) {
			this.onKeyListener = listener;
			return this;
		}

		/**
		 * Set a custom content view for the Dialog. If a message is set, the
		 * contentView is not added to the Dialog...
		 * 
		 * @param v
		 * @return
		 */
		public Builder setContentView(View v) {
			this.contentView = v;
			return this;
		}

		/**
		 * Set a custom content view for the Dialog. If a message is set, the
		 * contentView is not added to the Dialog...
		 * 
		 * @param v
		 * @return
		 */
		public Builder setDialogView(View v) {
			this.dialogBodyView = v;
			return this;
		}

		/**
		 * Set the positive button resource and it's listener
		 * 
		 * @param positiveButtonText
		 * @param listener
		 * @return
		 */
		public Builder setPositiveButton(int positiveButtonText,
				DialogInterface.OnClickListener listener) {
			if (Build.VERSION.SDK_INT < 14) {
				this.positiveButtonText = (String) context
						.getText(positiveButtonText);
				this.positiveButtonClickListener = listener;
			} else {
				this.negativeButtonText = (String) context
						.getText(positiveButtonText);
				this.negativeButtonClickListener = listener;
			}

			return this;
		}

		/**
		 * Set the positive button text and it's listener
		 * 
		 * @param positiveButtonText
		 * @param listener
		 * @return
		 */
		public Builder setPositiveButton(String positiveButtonText,
				DialogInterface.OnClickListener listener) {
			if (Build.VERSION.SDK_INT < 14) {
				this.positiveButtonText = positiveButtonText;
				this.positiveButtonClickListener = listener;
			} else {
				this.negativeButtonText = positiveButtonText;
				this.negativeButtonClickListener = listener;
			}

			return this;
		}

		/**
		 * Set the negative button resource and it's listener
		 * 
		 * @param negativeButtonText
		 * @param listener
		 * @return
		 */
		public Builder setNegativeButton(int negativeButtonText,
				DialogInterface.OnClickListener listener) {
			if (Build.VERSION.SDK_INT < 14) {
				this.negativeButtonText = (String) context
						.getText(negativeButtonText);
				this.negativeButtonClickListener = listener;
			} else {

				this.positiveButtonText = (String) context
						.getText(negativeButtonText);
				this.positiveButtonClickListener = listener;
			}
			return this;
		}

		/**
		 * Set the negative button text and it's listener
		 * 
		 * @param negativeButtonText
		 * @param listener
		 * @return
		 */
		public Builder setNegativeButton(String negativeButtonText,
				DialogInterface.OnClickListener listener) {
			if (Build.VERSION.SDK_INT < 14) {
				this.negativeButtonText = negativeButtonText;
				this.negativeButtonClickListener = listener;
			} else {
				this.positiveButtonText = negativeButtonText;
				this.positiveButtonClickListener = listener;
			}

			return this;
		}

		/**
		 * Create the custom dialog
		 */
		public CustomDialog2 create() {
			LayoutInflater inflater = (LayoutInflater) context
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			// instantiate the dialog with the custom Theme
			final CustomDialog2 dialog = new CustomDialog2(context,
					R.style.Dialog);
			View layout = inflater.inflate(R.layout.custemdialog2, null);
			dialog.addContentView(layout, new LayoutParams(
					LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT));
			// set the dialog title
			((TextView) layout.findViewById(R.id.title)).setText(title);
			// set the confirm button
			dialog.setCancelable(cancelFlag);
			dialog.setOnCancelListener(cancelListener);
			dialog.setCanceledOnTouchOutside(false);	
			dialog.setOnKeyListener(onKeyListener);
			// set the content message

			if (positiveButtonText != null) {
				((Button) layout.findViewById(R.id.positiveButton))
						.setText(positiveButtonText);
				if (positiveButtonClickListener != null) {
					((Button) layout.findViewById(R.id.positiveButton))
							.setOnClickListener(new View.OnClickListener() {
								public void onClick(View v) {
									positiveButtonClickListener.onClick(dialog,
											DialogInterface.BUTTON_POSITIVE);
								}
							});
				}
			} else {
				// if no confirm button just set the visibility to GONE
				layout.findViewById(R.id.positiveButton).setVisibility(
						View.GONE);
			}
			// set the cancel button
			if (negativeButtonText != null) {
				((Button) layout.findViewById(R.id.negativeButton))
						.setText(negativeButtonText);
				if (negativeButtonClickListener != null) {
					((Button) layout.findViewById(R.id.negativeButton))
							.setOnClickListener(new View.OnClickListener() {
								public void onClick(View v) {
									negativeButtonClickListener.onClick(dialog,
											DialogInterface.BUTTON_NEGATIVE);
								}
							});
				}
			} else {
				// if no confirm button just set the visibility to GONE
				layout.findViewById(R.id.negativeButton).setVisibility(
						View.GONE);
			}

			if (message != null || message1 != null) {
				if (message != null) {
					((TextView) layout.findViewById(R.id.message))
							.setText(message);
				} else if (message1 != null) {
					((TextView) layout.findViewById(R.id.message))
							.setText(message1);
				}

			} else if (contentView != null) {
				// if no message set
				// add the contentView to the dialog body
				((LinearLayout) layout.findViewById(R.id.content))
						.removeAllViews();
				((LinearLayout) layout.findViewById(R.id.dialog_title))
					.removeAllViews();
				((LinearLayout) layout.findViewById(R.id.content)).addView(
						contentView, new LayoutParams(
								LayoutParams.FILL_PARENT,
								LayoutParams.WRAP_CONTENT));
			}
			if (dialogBodyView != null) {
				// if no message set
				// add the contentView to the dialog body
				((LinearLayout) layout.findViewById(R.id.dialog_body))
						.removeAllViews();
				((LinearLayout) layout.findViewById(R.id.dialog_body)).addView(
						dialogBodyView, new LayoutParams(
								LayoutParams.WRAP_CONTENT,
								LayoutParams.WRAP_CONTENT));
			}
			dialog.setContentView(layout);
			return dialog;
		}
	}


}
