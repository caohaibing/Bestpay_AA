package com.bestpay_aa.activity;


import android.app.ActivityGroup;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * 账单详情与以参与人员
 * @author zhouchaoxin
 *
 */
public class AADetailActivityGroup extends ActivityGroup implements OnClickListener{
	
	/**
	 * 动态显示界面
	 */
	private LinearLayout container;
	/**
	 * 账单明细
	 */
	private LinearLayout linear_aa_gather;
	/**
	 * 参与人员
	 */
	private LinearLayout linear_aa_payment;
	
	private Bundle bundle;
	
	private ImageView btn_aa_payment;
	private ImageView btn_aa_gather;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		setContentView(R.layout.aa_detail_group);
		super.onCreate(savedInstanceState);
		container = (LinearLayout) findViewById(R.id.container);
		linear_aa_gather = (LinearLayout) findViewById(R.id.linear_aa_gather);
		linear_aa_payment = (LinearLayout) findViewById(R.id.linear_aa_payment);
		btn_aa_payment =(ImageView)findViewById(R.id.btn_aa_payment);
		btn_aa_gather =(ImageView)findViewById(R.id.btn_aa_gather);
		linear_aa_gather.setOnClickListener(this);
		linear_aa_payment.setOnClickListener(this);
		
		bundle = getIntent().getExtras();
		if (bundle != null) {
			if (bundle.getString("TRANSSTAT").equals("交易完成") || bundle.getString("TRANSSTAT").equals("交易关闭")) {
				showView(0,bundle);
			} else {
				showView(1,bundle);
			}
		}
		
	}

	@Override
	public void onClick(View v) {

		switch (v.getId()) {
		case R.id.linear_aa_gather:
			showView(0,bundle);
			break;
		case R.id.linear_aa_payment:
			showView(1,bundle);
			break;
		default:
			break;
		}
	}
	/**
	 * 显示不同界面
	 * @param flag
	 */
	public void showView(int flag,Bundle extras) {
		switch (flag) {
		case 0:
			container.removeAllViews();
			btn_aa_payment.setImageResource(R.drawable.aa_person_icon_n);
			btn_aa_gather.setImageResource(R.drawable.aa_mycheck_icon_p);
			container.addView(getLocalActivityManager().startActivity(
					"aagather",
					new Intent(AADetailActivityGroup.this, AAGatheringDetailActivity.class).putExtras(extras))
					.getDecorView());
			break;
		case 1:
			container.removeAllViews();
			btn_aa_gather.setImageResource(R.drawable.aa_mycheck_icon_n);
			btn_aa_payment.setImageResource(R.drawable.aa_person_icon_p);
			container.addView(getLocalActivityManager().startActivity(
					"aapayment",
					new Intent(AADetailActivityGroup.this, AAPaymentPersonActivity.class).putExtras(extras))
					.getDecorView());
			break;

		default:
			break;
		}
	}

}
