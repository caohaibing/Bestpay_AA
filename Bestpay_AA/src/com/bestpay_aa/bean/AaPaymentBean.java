package com.bestpay_aa.bean;

import java.text.SimpleDateFormat;
import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.bestpay_aa.util.COMMON;

import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextUtils;
import android.util.Log;
public class AaPaymentBean implements Parcelable{
	private String theme;// 主题
	private String perMoney;// 人均金额
	private String remark;// 备注
	private String serialNo;// 付款流水号
	private String transStat;// 交易状态
	private String createtime;// 发起时间
	private String failuretime;// 失效时间
	private String targetProductNo;// 收款方
	private String payproductno;// 付款方
	private String payname;// 付款人员姓名
	private String fundstype;// 资金类型
	
	public AaPaymentBean() {
		super();
	}
	private AaPaymentBean(Parcel source){
    	this.theme=source.readString();
    	this.perMoney=source.readString();
    	this.remark=source.readString();
    	this.serialNo=source.readString();
    	this.transStat=source.readString();
    	this.createtime=source.readString();
    	this.failuretime=source.readString();
    	this.targetProductNo=source.readString();
    	this.payproductno=source.readString();
    	this.payname=source.readString();
    	this.fundstype =source.readString();
    }
	public AaPaymentBean(JSONObject data) {
		try {
			if(data.has("THEME"))
				this.theme = data.getString("THEME");
			if(data.has("PAYMONEY"))
				this.perMoney   = data.getString("PAYMONEY");
			if(data.has("MARK"))
				this.remark  = data.getString("MARK");
			if(data.has("PAYMENTORDERID"))
				this.serialNo  = data.getString("PAYMENTORDERID");
			if(data.has("STAT"))
				this.transStat  = data.getString("STAT");
			if (data.has("CREATETIME"))
				try {
					if(!TextUtils.isEmpty(data.getString("CREATETIME"))){
					this.createtime  = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(
							new SimpleDateFormat("yyyyMMddHHmmss").parse(data.getString("CREATETIME")));
					}
					
				} catch (Exception e) {
					e.printStackTrace();
					this.createtime  ="无";
				}
			if (data.has("FAILURETIME"))
				try {
					if(!TextUtils.isEmpty(data.getString("FAILURETIME"))){
					this.failuretime  = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(
							new SimpleDateFormat("yyyyMMddHHmmss").parse(data.getString("FAILURETIME")));
					}
					
				} catch (Exception e) {
					e.printStackTrace();
					this.failuretime  ="无";
				}
			if (data.has("PRODUCTNO"))
				this.targetProductNo  = data.getString("PRODUCTNO");
			if (data.has("PAYPRODUCTNO"))
				this.payproductno  = data.getString("PAYPRODUCTNO");
			if (data.has("PAYNAME"))
				this.payname  = data.getString("PAYNAME");
			if (data.has("FUNDSTYPE"))
				this.fundstype  = data.getString("FUNDSTYPE");
		} catch (JSONException e) {
			e.printStackTrace();
			Log.e("AaGatheringPayBean", e.getMessage());
		}finally{
			if(TextUtils.isEmpty(theme))  
				theme   = "无"; 
			if(TextUtils.isEmpty(perMoney))  
				perMoney   = "0"; 
			if(TextUtils.isEmpty(remark))  
				remark   = "无"; 
			if(TextUtils.isEmpty(serialNo))  
				serialNo   = "无"; 
			if(TextUtils.isEmpty(transStat))  
				transStat   = "无"; 
			if(TextUtils.isEmpty(createtime))  
				createtime   = "无"; 
			if(TextUtils.isEmpty(failuretime))  
				failuretime   = "无"; 
			if(TextUtils.isEmpty(targetProductNo))  
				targetProductNo   = ""; 
			if(TextUtils.isEmpty(payproductno))  
				payproductno   = ""; 
			if(TextUtils.isEmpty(payname))  
				payname   = ""; 
			if(TextUtils.isEmpty(fundstype))  
				fundstype   = ""; 
		}
	}
	
	public static ArrayList<AaPaymentBean> getTransBeans(final JSONArray array){
		ArrayList<AaPaymentBean> details = new ArrayList<AaPaymentBean>(array.length());
		
		try {
			for(int index=0; index<array.length();index++){
				details.add(new AaPaymentBean(array.getJSONObject(index)));
			}
		} catch (JSONException e) {
			e.printStackTrace();
			Log.d("TransactionBean.getTransBreans()", e.getMessage());
		}
		
		return details;
	}
	
	public String getTheme() {
		return theme;
	}

	public void setTheme(String theme) {
		this.theme = theme;
	}

	public String getPerMoney() {
		return perMoney;
	}
	public void setPerMoney(String perMoney) {
		this.perMoney = perMoney;
	}

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

	public String getSerialNo() {
		return serialNo;
	}

	public void setSerialNo(String serialNo) {
		this.serialNo = serialNo;
	}

	public String getTransStat() {
		return transStat;
	}

	public void setTransStat(String transStat) {
		this.transStat = transStat;
	}
	
	
	public String getTargetProductNo() {
		return targetProductNo;
	}
	public void setTargetProductNo(String targetProductNo) {
		this.targetProductNo = targetProductNo;
	}
	
	public String getPayproductno() {
		return payproductno;
	}
	public void setPayproductno(String payproductno) {
		this.payproductno = payproductno;
	}
	public String getPayname() {
		return payname;
	}
	public void setPayname(String payname) {
		this.payname = payname;
	}
	public String getFundstype() {
		return fundstype;
	}
	public void setFundstype(String fundstype) {
		this.fundstype = fundstype;
	}
	
	public String getCreatetime() {
		return createtime;
	}
	public void setCreatetime(String createtime) {
		this.createtime = createtime;
	}
	public String getFailuretime() {
		return failuretime;
	}
	public void setFailuretime(String failuretime) {
		this.failuretime = failuretime;
	}
	@Override
	public int describeContents() {
		// TODO Auto-generated method stub
		return 0;
	}
	@Override
	public void writeToParcel(Parcel dest, int arg1) {
	    dest.writeString(theme);  
        dest.writeString(perMoney);  
        dest.writeString(remark);  
        dest.writeString(serialNo);  
        dest.writeString(transStat);  
        dest.writeString(createtime);  
        dest.writeString(failuretime);  
        dest.writeString(targetProductNo);  
        dest.writeString(payproductno);  
        dest.writeString(payname);  
        dest.writeString(fundstype);  
	}
	
	public static final Parcelable.Creator<AaPaymentBean> CREATOR = new Creator<AaPaymentBean>(){  
        public AaPaymentBean createFromParcel(Parcel source){  
            return new AaPaymentBean(source);  
        }  
        public AaPaymentBean[] newArray(int size){  
            return new AaPaymentBean[size];  
        }  
    };  
}
