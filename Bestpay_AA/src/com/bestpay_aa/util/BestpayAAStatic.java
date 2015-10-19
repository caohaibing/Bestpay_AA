package com.bestpay_aa.util;

/**
 * @author zhangx
 * 
 */
public class BestpayAAStatic {
	/**
	 * AA付款action
	 */
	public  static String PAYACTION="com.bestpay_aa.activity.action.pay";
	/**
	 * 广播收款数量
	 */
	public  static String ACTION_AAGATHERING = "com.bestpay_aa.activity.action.gathering";
	/**
	 * AA收款 待收款记录数
	 */
	public static int AAGATHERING_NUM = 0;
	
	/**
	 * 总余额
	 */
	public  static String BALANCE="balance";
	/**
	 * 可提现余额
	 */
	public  static String CASHBALANCE="cashbalance";
	/**
	 * 不可提现余额
	 */
	public  static String NONCASHBALANCE="noncashbalance";
	
	/**
	 * 状态-成功
	 */
	public  static String S0C="S0C";
	
	/**
	 * 状态-等待付款
	 */
	public  static String S0A="S0A";
	
	/**
	 * 状态-失效
	 */
	public  static String S0X="S0X";
}
