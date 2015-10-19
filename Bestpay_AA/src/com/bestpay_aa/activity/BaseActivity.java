package com.bestpay_aa.activity;

import java.util.HashMap;
import java.util.Iterator;

import com.bestpay_aa.view.CustomToast;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class BaseActivity extends Activity {

	public static HashMap<String, Activity> uiMap = new HashMap<String, Activity>();
	
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		this.requestWindowFeature(Window.FEATURE_NO_TITLE);

		String className = this.getClass().getName();
		if (uiMap.get(className) != null) {
			uiMap.remove(className).finish();
		}
		uiMap.put(className, this);

	}

	public static void exitWithoutDialog() {
		Iterator<Activity> it = uiMap.values().iterator();
		// System.out.println(uiMap.size());
		// �˳�ʱfinish����activity
		while (it.hasNext()) {
			it.next().finish();
		}

		uiMap.clear();
		System.gc();
		System.exit(0);

	}

	/**
	 * zhangbiao ��ϵ�ͷ�
	 * 
	 * @param layoutId
	 * @param id
	 */
	protected void linkCustomer(int id) {
		TextView text = (TextView)findViewById(id);
		text.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent callIntent = new Intent(Intent.ACTION_VIEW, Uri
						.parse("tel:4008011888"));
				callIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				startActivity(callIntent);
			}
		});
	}
	
	/**
	 * ��˾��ʾ   zhangbiao
	 * @param context
	 * @param p
	 */
	protected void prompt(Context context,String p) {
		CustomToast toast = new CustomToast(context,p);
		toast.show();
	}
	
	protected String formatTelNum(String num) {
		return num.replaceAll("-", "");
	}
	
	
	/**
	 * �ж��ֻ�ǰ3λ
	 * 
	 * @param str
	 *            �ֻ�ŵ�ǰ3λ
	 * @return
	 */
	protected boolean judgeTMobile(String str) {
		boolean legal = false; // �Ϸ���

		if (str.length() >= 3) {
			if ("133".equals(str.substring(0, 3))
					|| "153".equals(str.substring(0, 3))
					|| "180".equals(str.substring(0, 3))
					|| "189".equals(str.substring(0, 3))
					|| "181".equals(str.substring(0, 3))) { // ���Ŷ�
				legal = true;
			} else if ("134".equals(str.substring(0, 3))
					|| "135".equals(str.substring(0, 3))
					|| "136".equals(str.substring(0, 3))
					|| "137".equals(str.substring(0, 3))
					|| "138".equals(str.substring(0, 3))
					|| "139".equals(str.substring(0, 3))
					|| "150".equals(str.substring(0, 3))
					|| "151".equals(str.substring(0, 3))
					|| "152".equals(str.substring(0, 3))
					|| "157".equals(str.substring(0, 3))
					|| "158".equals(str.substring(0, 3))
					|| "159".equals(str.substring(0, 3))
					|| "182".equals(str.substring(0, 3))
					|| "183".equals(str.substring(0, 3))
					|| "187".equals(str.substring(0, 3))
					|| "188".equals(str.substring(0, 3))
					|| "147".equals(str.substring(0, 3))) { // �ƶ���
				legal = true;
			} else if ("130".equals(str.substring(0, 3))
					|| "131".equals(str.substring(0, 3))
					|| "132".equals(str.substring(0, 3))
					|| "145".equals(str.substring(0, 3))
					|| "155".equals(str.substring(0, 3))
					|| "156".equals(str.substring(0, 3))
					|| "185".equals(str.substring(0, 3))
					|| "186".equals(str.substring(0, 3))) { // ��ͨ��
				legal = true;
			} else {
				legal = false;
			}
		}
		return legal;
	}
}
