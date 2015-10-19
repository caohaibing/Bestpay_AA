package com.bestpay_aa.util;
/**
 * @ClassName    : 
 * @Description  : <类描述>
 * @Developer    : <zcx> 
 * @Modifier     : <修改者姓名>
 * @EditContent  : <修改内容>
 * @EditDate    : 2013-4-28 上午10:57:44 [created]
 */
public class COMMON {
	public static final String  TIMEOUT="timeout";//超时异常
	public static final String  IOEXCEPTION="ioexception";//IO异常
	public static final String  EXCEPTION="exception";//其它异常
	public static final String SETTING_INFO = "setting_infos";
	public static final String key = "TRAFFICFINES@tisson.cn"; 
	public static final int GET_CONTACTS = 110;
	public static final int REQUESTCODE = 0;
	
	//public static boolean isRefreshAccount = false;  //默认为不去调用余额查询接口
	
	public static final String CHINA_TELECOM = "中国电信";
	public static final String CHINA_MOBILE = "中国移动";
	public static final String CHINA_UNICOM = "中国联通";
	
	 //签名
	 public static String  sign(String sig) {
		  String sign = MD5Helper.MD5Encode(sig + key);
		  return sign;
	 }	
	 //将数字转换为金额
	 public static String  convert(String s){
		  String tran="";
		  //截取最后两位
		  if(s!=null){
			  try {
				  int i =Integer.parseInt(s);
				  if(s.length()==1){
					  tran="0.0"+s;
				  }else if(s.length()==2){
					  tran="0."+s;
				  }else if(s.length()>2){
					  int length = s.length();
					  String wei = s.substring(length-2);
					  String yuan = s.substring(0,length-2);
					  tran = yuan+"."+wei;
				  }
			} catch (Exception e) {
				tran="0.00";
			}
		  }else{
			  tran="0.00";
		  }
		 return tran;
	 }
}
