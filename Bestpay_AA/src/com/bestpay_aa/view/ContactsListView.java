package com.bestpay_aa.view;


import com.bestpay_aa.activity.R;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.ViewTreeObserver;
import android.widget.LinearLayout;
import android.widget.ListView;
/**
 * 取得联系人自定义VIEW
 * @author zhouchaoxin
 *
 */
public class ContactsListView extends ListView {
	
	
	private LayoutInflater inflater;
	private LinearLayout itemView;
	public ContactsListView(Context context) {
		
		super(context, null);
		init(context);
	}

	public ContactsListView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context);
	}

	/**
	 * 初始化布局
	 * @param context
	 */
	private void init(Context context){
		setCacheColorHint(context.getResources().getColor(R.color.transparent));
		inflater = LayoutInflater.from(context);
		itemView = (LinearLayout) inflater.inflate(R.layout.contactslistitem, null);
	}
	
	
}
