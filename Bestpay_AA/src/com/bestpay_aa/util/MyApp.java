package com.bestpay_aa.util;

import android.app.Application;

public class MyApp extends Application {

	private String prod;  
	private String locat; 
	
	/**
	 * 标识是从确认发起收款页面返回的
	 */
	private String page;
	
	/**
	 * 刷新付款列表标记
	 */
	private boolean refreshPayFlag = true;
	
	public boolean isRefreshPayFlag() {
		return refreshPayFlag;
	}
	public void setRefreshPayFlag(boolean refreshPayFlag) {
		this.refreshPayFlag = refreshPayFlag;
	}
	public String getPage() {
		return page;
	}
	public void setPage(String page) {
		this.page = page;
	}
	public String getProd() {
		return prod;
	}
	public void setProd(String prod) {
		this.prod = prod;
	}
	public String getLocat() {
		return locat;
	}
	public void setLocat(String locat) {
		this.locat = locat;
	}
	
}
