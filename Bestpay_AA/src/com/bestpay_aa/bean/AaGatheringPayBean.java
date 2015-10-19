package com.bestpay_aa.bean;

import java.text.SimpleDateFormat;
import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextUtils;
import android.util.Log;
public class AaGatheringPayBean implements Parcelable{
	/**
	 * 收款流水号
	 */
	private String launchorderId;
	/**
	 * 收款翼支付账户
	 */
	private String productno;
	/**
	 * 收款总金额
	 */
	private String amount; 
	/**
	 * 主题
	 */
	private String theme;
	/**
	 * 付款人姓名
	 */
	private String payname;
	/**
	 * 备注
	 */
	private String mark;
	/**
	 * 发起付款时间
	 */
	private String createTime;
	/**
	 * 付款明细列表
	 */
	private ArrayList<AaPaymentBean> paymentorders;
	
	public AaGatheringPayBean() {
		super();
	}
	public AaGatheringPayBean(JSONObject data) {
		try {
			if(data.has("THEME")){
				
				this.theme = data.getString("THEME");
			}
			if(data.has("PAYNAME")){
				
				this.payname = data.getString("PAYNAME");
			}
			if(data.has("AMOUNT")){
				
				try {
					this.amount   = String.format("%.2f", data.getDouble("AMOUNT")/100);
				} catch (Exception e) {
					this.amount   = "";
				}
			}
			if(data.has("LAUNCHORDERID")){
				
				this.launchorderId  = data.getString("LAUNCHORDERID");
			}
			if(data.has("PRODUCTNO")){
				
				this.productno  = data.getString("PRODUCTNO");
			}
			if(data.has("MARK")){
				
				this.mark  = data.getString("MARK");
			}
				
			if(data.has("CREATETIME")){
				
				
				try {
					if(!TextUtils.isEmpty(data.getString("CREATETIME"))){
					this.createTime  = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(
							new SimpleDateFormat("yyyyMMddHHmmss").parse(data.getString("CREATETIME")));
					}
					
				} catch (Exception e) {
					e.printStackTrace();
					this.createTime  ="无";
				}
			}
			if (data.has("PAYMENTORDERLIST")) {
				
				JSONArray array = data.getJSONArray("PAYMENTORDERLIST");
				paymentorders = new ArrayList<AaPaymentBean>(array.length());
				
				for (int i = 0; i < array.length(); i++) {
					
					paymentorders.add(new AaPaymentBean(array.getJSONObject(i)));
				}
			}
				
			
		} catch (JSONException e) {
			e.printStackTrace();
			Log.e("AaGatheringPayBean", e.getMessage());
		}
	}
	
	public static ArrayList<AaGatheringPayBean> getTransBeans(final JSONArray array){
		ArrayList<AaGatheringPayBean> details = new ArrayList<AaGatheringPayBean>(array.length());
		
		try {
			for(int index=0; index<array.length();index++){
				details.add(new AaGatheringPayBean(array.getJSONObject(index)));
			}
		} catch (JSONException e) {
			e.printStackTrace();
			Log.d("TransactionBean.getTransBreans()", e.getMessage());
		}
		
		return details;
	}
	public String getLaunchorderId() {
		return launchorderId;
	}
	public void setLaunchorderId(String launchorderId) {
		this.launchorderId = launchorderId;
	}
	public String getProductno() {
		return productno;
	}
	public void setProductno(String productno) {
		this.productno = productno;
	}
	public String getAmount() {
		return amount;
	}
	public void setAmount(String amount) {
		this.amount = amount;
	}
	public String getTheme() {
		return theme;
	}
	public void setTheme(String theme) {
		this.theme = theme;
	}
	public String getMark() {
		return mark;
	}
	public void setMark(String mark) {
		this.mark = mark;
	}
	public String getCreateTime() {
		return createTime;
	}
	public void setCreateTime(String createTime) {
		this.createTime = createTime;
	}
	public ArrayList<AaPaymentBean> getPaymentorders() {
		return paymentorders;
	}
	public void setPaymentorders(ArrayList<AaPaymentBean> paymentorders) {
		this.paymentorders = paymentorders;
	}
	
	public String getPayname() {
		return payname;
	}
	public void setPayname(String payname) {
		this.payname = payname;
	}
	@Override
	public int describeContents() {
		// TODO Auto-generated method stub
		return 0;
	}
	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeString(launchorderId);  
		dest.writeString(productno);  
		dest.writeString(theme);  
		dest.writeString(payname);  
        dest.writeString(amount);  
        dest.writeString(mark);  
        dest.writeString(createTime);  
        dest.writeSerializable(paymentorders);  
	}

}
