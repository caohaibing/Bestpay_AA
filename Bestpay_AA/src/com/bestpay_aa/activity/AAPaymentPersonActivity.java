package com.bestpay_aa.activity;

import java.util.ArrayList;

import com.bestpay_aa.bean.AaPaymentBean;
import com.bestpay_aa.util.BestpayAAStatic;
import com.bestpay_aa.util.COMMON;
import com.bestpay_aa.util.ShakeListener;
import com.bestpay_aa.util.ShakeListener.OnShakeListener;

import android.app.Service;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Vibrator;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

/**
 * AA参与人员名单
 * 
 * @author zhangxue
 * 
 */
public class AAPaymentPersonActivity extends BaseActivity {

	/**
	 * 参与人员名单
	 */
	private ArrayList<AaPaymentBean> payments;
	private ArrayList<String> statList =new ArrayList<String>();
	ListView listview;
	PayPersonAdapter payPersonAdapter;
	RelativeLayout shakeRL;
	ImageView shake_icon;
	String payProductNo;// 付款方
	String smsContent;
	ShakeListener mShakeListener = null; 
	Vibrator mVibrator;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		Bundle b = getIntent().getExtras();
		if (b != null) {
			payments = b.getParcelableArrayList("paymentorders");
			payments = sort(payments);
			statList=getstatList();
		}
		setContentView(R.layout.aa_paymetnperson);
		listview = (ListView) findViewById(R.id.listview);
		shakeRL = (RelativeLayout) findViewById(R.id.shake_rlt);
		if(statList.contains(BestpayAAStatic.S0A)){
			shakeRL.setVisibility(View.VISIBLE);
		}else{
			shakeRL.setVisibility(View.GONE);
		}
		shake_icon = (ImageView) findViewById(R.id.shake_icon);
		payPersonAdapter = new PayPersonAdapter();
		mShakeListener = new ShakeListener(this); 
		mShakeListener.setOnShakeListener(new shakeLitener()); 
		listview.setAdapter(payPersonAdapter);
		listview.setSelector(android.R.color.transparent);
		System.out.println("BRAND:"+Build.BRAND+",MODEL:"+Build.MODEL+",MANUFACTURER:"+Build.MANUFACTURER);
		shake_icon.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
			    payProductNo="";
			    for (int i = 0; i < payments.size(); i++) {
			    	if(null != payments.get(i).getTransStat() 
						&& BestpayAAStatic.S0A.equals(payments.get(i).getTransStat())){
			    		if(null != payments.get(i).getPayproductno() 
							&& !"".equals(payments.get(i).getPayproductno())
							&& !payProductNo.contains(payments.get(i).getPayproductno())){
			    			if(Build.BRAND.equalsIgnoreCase("htc")||Build.BRAND.equalsIgnoreCase("Meizu")
									|| Build.MODEL.equalsIgnoreCase("unknown")
									||  Build.MODEL.contains("HTC")){
			    				payProductNo +=payments.get(i).getPayproductno()+";";
			    			}else{
			    				payProductNo +=payments.get(i).getPayproductno()+",";
			    			}
			    		}
			    	}
			    }
				if (!TextUtils.isEmpty(payProductNo)) {
					Uri uri = Uri.parse("smsto:" + payProductNo.substring(0,payProductNo.length()-1).replace(" ", ""));  
				    Intent sendIntent = new Intent(Intent.ACTION_VIEW, uri);  
				    sendIntent.putExtra("sms_body", smsContent.trim());  
				    startActivity(sendIntent); 
				}
			}
		});
	}
	@Override
	protected void onResume() {
		super.onResume();
		mShakeListener.start();
	}
	@Override
	protected void onPause() {
		super.onPause();
		mShakeListener.stop();
	}
	/**
	 * 获取状态列表
	 * @return
	 */
	private ArrayList<String> getstatList() {
		for (int i = 0; i < payments.size(); i++) {
			statList.add(payments.get(i).getTransStat());
		}
		return statList;
	}
	/**
	 * 按照状态排序
	 * @param payments2
	 * @return
	 */
 	private ArrayList<AaPaymentBean> sort(ArrayList<AaPaymentBean> payments2) {
 		ArrayList<AaPaymentBean> list= new ArrayList<AaPaymentBean>();
		if(null!=payments2  && payments2.size()>0){
			for (AaPaymentBean aaPaymentBean : payments2) {
				if(BestpayAAStatic.S0A.equals(aaPaymentBean.getTransStat())){
					list.add(aaPaymentBean);
				}
			}
			for (AaPaymentBean aaPaymentBean : payments2) {
				if(!BestpayAAStatic.S0A.equals(aaPaymentBean.getTransStat())){
					list.add(aaPaymentBean);
				}
			}
		}
		return list;
		
	}
	class PayPersonAdapter extends BaseAdapter {
		ViewHolder holder;

		public void reData() {
			notifyDataSetChanged();
		}

		@Override
		public int getCount() {
			return payments.size();
		}

		@Override
		public Object getItem(int position) {
			return payments.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			LayoutInflater mInflater = LayoutInflater
					.from(AAPaymentPersonActivity.this);
			if (convertView == null) {
				holder = new ViewHolder();
				convertView = mInflater.inflate(
						R.layout.aa_payment_person_item, null);
				convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}

			holder.paynameTv = (TextView) convertView
					.findViewById(R.id.payname);
			holder.shoukuanBt = (Button) convertView
					.findViewById(R.id.shoukuan);
			holder.haspaid = (ImageView) convertView
					.findViewById(R.id.haspaid);
			holder.shoukuanBt.setTag(position);
			holder.paynameTv.setText(payments.get(position).getPayname());
			if (!TextUtils.isEmpty(payments.get(position).getTransStat())
					&& "S0A".equals(payments.get(position).getTransStat())) {
				holder.shoukuanBt.setVisibility(View.VISIBLE);
				holder.haspaid.setVisibility(View.GONE);
			} else if(!TextUtils.isEmpty(payments.get(position).getTransStat())
					&& "S0C".equals(payments.get(position).getTransStat())){
				holder.shoukuanBt.setVisibility(View.GONE);
				holder.haspaid.setVisibility(View.VISIBLE);
			}else {
				holder.shoukuanBt.setVisibility(View.GONE);
				holder.haspaid.setVisibility(View.GONE);
			}
			smsContent = getResources().getString(R.string.sms_cuikuan1)
					+ COMMON.convert(payments.get(position).getPerMoney()) 
					+ getResources().getString(R.string.sms_cuikuan2);
			holder.shoukuanBt.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					
					payProductNo = payments.get(Integer.valueOf(v.getTag().toString())).getPayproductno();
					if (!TextUtils.isEmpty(payProductNo)) {
						Uri uri = Uri.parse("smsto:" + payProductNo);  
					    Intent sendIntent = new Intent(Intent.ACTION_VIEW, uri);  
					    sendIntent.putExtra("sms_body", smsContent.trim());  
					    startActivity(sendIntent); 
					}

				}
			});
			return convertView;
		}

	}

	class ViewHolder {
		TextView paynameTv;
		ImageView haspaid;
		Button shoukuanBt;
	}

	
	private class shakeLitener implements OnShakeListener{ 
		   @Override 
		   public void onShake() { 
			   if(statList.contains(BestpayAAStatic.S0A)){
				   mVibrator = ( Vibrator ) getApplication().getSystemService(Service.VIBRATOR_SERVICE); 
	               mVibrator.vibrate( new long[]{100,300},-1);
				   payProductNo="";
				   for (int i = 0; i < payments.size(); i++) {
					if(null != payments.get(i).getTransStat() 
							&& BestpayAAStatic.S0A.equals(payments.get(i).getTransStat())){
						if(null != payments.get(i).getPayproductno() 
								&& !"".equals(payments.get(i).getPayproductno())
								&& !payProductNo.contains(payments.get(i).getPayproductno())){
							if(Build.BRAND.equalsIgnoreCase("htc")||Build.BRAND.equalsIgnoreCase("Meizu")
									|| Build.MODEL.equalsIgnoreCase("unknown")
									||  Build.MODEL.contains("HTC")){
			    				payProductNo +=payments.get(i).getPayproductno()+";";
			    			}else{
			    				payProductNo +=payments.get(i).getPayproductno()+",";
			    			}
						}
					}
				   }
				   if (!TextUtils.isEmpty(payProductNo)) {
					   Uri uri = Uri.parse("smsto:" + payProductNo.substring(0,payProductNo.length()-1).replace(" ", ""));  
					    Intent sendIntent = new Intent(Intent.ACTION_VIEW, uri);  
					    sendIntent.putExtra("sms_body", smsContent.trim());  
					    startActivity(sendIntent); 
					}
			   }
			   mShakeListener.stop();
		   } 
	}	   
}
