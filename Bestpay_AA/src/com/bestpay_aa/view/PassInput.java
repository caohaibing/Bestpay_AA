package com.bestpay_aa.view;

import java.util.Timer;
import java.util.TimerTask;

import com.bestpay_aa.activity.R;
import com.bestpay_aa.util.Util;

import android.app.Activity;
import android.content.Context;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.InputType;
import android.text.Selection;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.ScrollView;


public class PassInput extends LinearLayout implements OnClickListener,
		OnFocusChangeListener {
	private int layout_id;
	private LinearLayout layout_password;
	private LinearLayout layout_popubt;
	private PopupWindow window;
	private EditText editPassword;
	private Context context;

	public PassInput(Context context,int id) {
		super(context);
		this.context = context;
		setLayoutId(R.id.password_xls);  //payment_password_edit,R.id.password_xls
		initEdit();
		initWindow();
	}

	public PassInput(Context context, AttributeSet attrs) {
		super(context, attrs);
		this.context = context;
		setLayoutId(R.id.password_xls); //R.id.password_xls
		initEdit();
		initWindow();
	}
	
	//���ò����ļ�
	public void setLayoutId(int id){
		this.layout_id = id;
	}
	public void setInputType(int type) {

		editPassword.setInputType(type);

	}

	private void initEdit() {
		layout_password = (LinearLayout) ((LayoutInflater) this.getContext()
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(
				R.layout.password_input, this);
		editPassword = (EditText) layout_password
				.findViewById(R.id.payment_password_edit);
		
		editPassword.setOnClickListener(this);
		editPassword.setOnFocusChangeListener(this);
//		 closeSystemKeyBoard();
        editPassword.setInputType(InputType.TYPE_NULL);// �������뷨
//		 editPassword.(false);
		editPassword.setTextSize(15);
		editPassword.setTransformationMethod(PasswordTransformationMethod
				.getInstance()); // ���Ǻ�
		editPassword.setOnLongClickListener(new OnLongClickListener() {
			
			@Override
			public boolean onLongClick(View v) {
				// TODO Auto-generated method stub
				return true;
			}
		});
	}
	public void  setOnFocusListener(final ScrollView sv,final Context ctx){
		editPassword.setOnFocusChangeListener(new OnFocusChangeListener() {
			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				if(hasFocus){
					if(Build.VERSION.SDK_INT > 7)
					SetSmoothScrollTo(sv);
					((InputMethodManager) ctx.getSystemService(ctx.INPUT_METHOD_SERVICE))
					.hideSoftInputFromWindow(v.getWindowToken(),
							InputMethodManager.HIDE_NOT_ALWAYS);
				}
					
			}
		});
	}
	public void SetSmoothScrollTo(final ScrollView sv){
		sv.post(new Runnable() {
			@Override
			public void run() {
				showWindow(layout_id);
				
				if(Build.MODEL.equalsIgnoreCase("S8600")) {
					sv.smoothScrollTo(0, 1500);
				} else {
					sv.smoothScrollTo(0, 500);
				}
			}
		});	
	}

	public Boolean checkPasslength(int length) {
		if (this.getPassword().equals("") || this.getPassword() == null) {

			return false;
		} else if (this.getPassword().length() < length) {

			return false;

		} else {
			if (Util.isNumeric(this.getPassword())) {

				return true;
			} else {

				return false;

			}
		}

	}

	// �������벻�ɼ�ɼ�
	public void setViewPassword(Boolean flag) {
		if (flag) {
			editPassword.setTransformationMethod(null); // �ɼ�
		} else {
			editPassword.setTransformationMethod(PasswordTransformationMethod
					.getInstance()); // ���Ǻ�
		}

	}
	// �������I939D���ֵĵ��������ɼ�ʱ���������Ĺ��ǰ�Ƶ�����
		// �������벻�ɼ�ɼ�
	public void setViewPassword1(Boolean flag) {
		if (flag) {
			// ��ʾΪ��ͨ�ı�
			// editPassword.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
			editPassword
					.setTransformationMethod(HideReturnsTransformationMethod
							.getInstance()); // �ɼ�
		} else {
			// ��ʾΪ����
			// editPassword.setInputType(InputType.TYPE_CLASS_TEXT |
			// InputType.TYPE_TEXT_VARIATION_PASSWORD);
			editPassword.setTransformationMethod(PasswordTransformationMethod
					.getInstance()); // ���Ǻ�
		}
		// ʹ���ʼ�������λ��
		Editable etable = editPassword.getText();
		Selection.setSelection(etable, etable.length());
	}

	public void setPassWordHit(int id) {
		editPassword.setHint(id);
	}
	
	public void setPassWordHit(String str) {
		editPassword.setHint(str);
	}
	
	public void closeSystemKeyBoard() {
		InputMethodManager imm = (InputMethodManager) getContext()
				.getSystemService(Context.INPUT_METHOD_SERVICE);
		imm.hideSoftInputFromWindow(editPassword.getWindowToken(), 0);
	}

	public void showWindow(int id) {
		window.showAtLocation(((Activity) context)
				.findViewById(id), Gravity.BOTTOM
				| Gravity.LEFT, Gravity.LEFT, 0);
	}

	private void initWindow() {
		int draw[] = new int[10];
		for (int i = 0; i < 10; i++)
			draw[i] = i;

		java.util.Random ran = new java.util.Random();
		java.util.List<Integer> list = new java.util.ArrayList<Integer>();
		while (list.size() < 10) {
			int n = ran.nextInt(10);
			if (!list.contains(n))
				list.add(n);// ���n������list�У������
		}

		layout_popubt = (LinearLayout) ((LayoutInflater) this.getContext()
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(
				R.layout.safe_keyboard, null);

		window = new PopupWindow(layout_popubt,
				ViewGroup.LayoutParams.FILL_PARENT,
				ViewGroup.LayoutParams.WRAP_CONTENT);
		window.setOutsideTouchable(true);

		final int key0 = list.get(0);
		Button btn0 = ((Button) layout_popubt
				.findViewById(R.id.payment_dial_num0_btn));
		// btn0.setBackgroundResource(draw[key0]);
		btn0.setText(draw[key0] + "");
		btn0.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
//				editPassword.setText(editPassword.getText()
//						+ String.valueOf(key0));
//				editPassword.setSelection(editPassword.getText().toString()
//						.length());
				setPassText(key0);
			}
		});

		final int key1 = list.get(1);
		Button btn1 = ((Button) layout_popubt
				.findViewById(R.id.payment_dial_num1_btn));
		btn1.setText(draw[key1] + "");
		btn1.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
//				editPassword.setText(editPassword.getText()
//						+ String.valueOf(key1));
//				editPassword.setSelection(editPassword.getText().toString()
//						.length());
				setPassText(key1);
			}
		});

		final int key2 = list.get(2);
		Button btn2 = ((Button) layout_popubt
				.findViewById(R.id.payment_dial_num2_btn));
		btn2.setText(draw[key2] + "");
		btn2.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
//				editPassword.setText(editPassword.getText()
//						+ String.valueOf(key2));
//				editPassword.setSelection(editPassword.getText().toString()
//						.length());
				setPassText(key2);
			}
		});

		final int key3 = list.get(3);
		Button btn3 = ((Button) layout_popubt
				.findViewById(R.id.payment_dial_num3_btn));
		btn3.setText(draw[key3] + "");
		btn3.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
//				editPassword.setText(editPassword.getText()
//						+ String.valueOf(key3));
//				editPassword.setSelection(editPassword.getText().toString()
//						.length());
				setPassText(key3);
			}
		});

		final int key4 = list.get(4);
		Button btn4 = ((Button) layout_popubt
				.findViewById(R.id.payment_dial_num4_btn));
		btn4.setText(draw[key4] + "");
		btn4.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
//				editPassword.setText(editPassword.getText()
//						+ String.valueOf(key4));
//				editPassword.setSelection(editPassword.getText().toString()
//						.length());
				setPassText(key4);
			}
		});

		final int key5 = list.get(5);
		Button btn5 = ((Button) layout_popubt
				.findViewById(R.id.payment_dial_num5_btn));
		btn5.setText(draw[key5] + "");
		btn5.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
//				editPassword.setText(editPassword.getText()
//						+ String.valueOf(key5));
//				editPassword.setSelection(editPassword.getText().toString()
//						.length());
				setPassText(key5);
			}
		});

		final int key6 = list.get(6);
		Button btn6 = ((Button) layout_popubt
				.findViewById(R.id.payment_dial_num6_btn));
		btn6.setText(draw[key6] + "");
		btn6.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
//				editPassword.setText(editPassword.getText()
//						+ String.valueOf(key6));
//				editPassword.setSelection(editPassword.getText().toString()
//						.length());
				setPassText(key6);
			}
		});

		final int key7 = list.get(7);
		Button btn7 = ((Button) layout_popubt
				.findViewById(R.id.payment_dial_num7_btn));
		btn7.setText(draw[key7] + "");
		btn7.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
//				editPassword.setText(editPassword.getText()
//						+ String.valueOf(key7));
//				editPassword.setSelection(editPassword.getText().toString()
//						.length());
				setPassText(key7);
			}
		});

		final int key8 = list.get(8);
		Button btn8 = ((Button) layout_popubt
				.findViewById(R.id.payment_dial_num8_btn));
		btn8.setText(draw[key8] + "");
		btn8.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
//				editPassword.setText(editPassword.getText()
//						+ String.valueOf(key8));
//				editPassword.setSelection(editPassword.getText().toString()
//						.length());
				setPassText(key8);
			}
		});

		final int key9 = list.get(9);
		Button btn9 = ((Button) layout_popubt
				.findViewById(R.id.payment_dial_num9_btn));
		btn9.setText(draw[key9] + "");
		btn9.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
//				editPassword.setText(editPassword.getText()
//						+ String.valueOf(key9));
//				editPassword.setSelection(editPassword.getText().toString()
//						.length());
				setPassText(key9);
			}
		});

		((Button) layout_popubt.findViewById(R.id.payment_dial_num_finish_btn))
				.setOnClickListener(this);
		// ==========================================
		// �������Ҫ�󣬶�ɾ��ť��ӳ����¼� add by caijh 20121024
		btn_delete = ((Button) layout_popubt.findViewById(R.id.payment_dial_num_delete_btn));
		btn_delete.setOnClickListener(this);
		
		btn_delete.setLongClickable(true);// ���ð�ť֧�ֳ���
		btn_delete.setOnLongClickListener(new OnLongClickListener() {

			@Override
			public boolean onLongClick(View v) {
				// TODO Auto-generated method stub
				if (v == btn_delete) {
					deletePassword();
				}
				return true;
			}
		});

	}

	Button btn_delete;// ��ȫ�����ϵ�ɾ��ť����Ϊ��Ҫ������ӳ����¼�

	// ����ʱ��ִ��һ��ɾ�����
	public void deletePassword() {
		final Timer t = new Timer(true);
		TimerTask tt = new TimerTask() {
			@Override
			public void run() {
				if (btn_delete.isPressed()) {
					Message msg = new Message();
					msg.what = 0;
					handle.sendMessage(msg);
				} else {
					t.cancel();
				}

			}
		};
		t.schedule(tt, 100, 100);
	}

	Handler handle = new Handler() {
		public void handleMessage(Message msg) {
			if (msg.what == 0) {
				if (editPassword.getText().length() > 0) {
					int index = editPassword.getSelectionStart();
					if (index > 0)
						editPassword.getText().delete(index - 1, index);
				}

			}
		};
	};

	// ==========================================

	
	void setPassText(int key){
		int index=editPassword.getSelectionStart();
		editPassword.getText().insert(index,  String.valueOf(key));
//		editPassword.setText(editPassword.getText()
//				+ String.valueOf(key));
//		editPassword.setSelection(editPassword.getText().toString()
//				.length());
	}
	
	
	public void onClick(View v) {
		switch (v.getId()) {

		case R.id.payment_password_edit:
			showWindow(layout_id);
			break;
		case R.id.payment_dial_num_finish_btn:
			window.dismiss();
			break;
		case R.id.payment_dial_num_delete_btn:
			if (editPassword.getText().length() > 0) {
//				editPassword.setText(editPassword.getText().subSequence(0,
//						editPassword.getText().length() - 1));
//				editPassword.setSelection(editPassword.getText().toString()
//						.length());
				int index=editPassword.getSelectionStart();
				if(index>0)
					editPassword.getText().delete(index-1, index);
			}
			break;
		default:
			break;
		}

	}

	
	public Boolean isWindowShowing() {
		if (window.isShowing()) {
			return true;
		}else
			return false;
	}
	
	public void closeWindow() {
		if (window.isShowing()) {
			window.dismiss();
		}
	}

	public void setPasswordText(String string) {
		editPassword.setText(string);
	}

	public String getPassword() {
		return editPassword.getText().toString();
	}

	@Override
	public void onFocusChange(View v, boolean hasFocus) {
		if (hasFocus) {

			showWindow(layout_id);
			closeSystemKeyBoard();
		} else {
			closeWindow();

		}
	}

}
