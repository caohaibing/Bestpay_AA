package com.bestpay_aa.util;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import com.bestpay_aa.net.AAURL;
import com.bestpay_aa.net.MD5Helper;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.preference.PreferenceManager;


/**
 * 公用类
 * @author zhouchaoxin
 *
 */
public class Util {
	private static String VERSION = "000001";
	private static long KEEP;
	public static final int MOBILE = 1;
	public static final int WIFI = 2;
	public static final int NONETWORK = -1;
	public static final int NONETWORK_ = -2;
	
	public static final String key = "TRAFFICFINES@tisson.cn"; 
	/**
	 * 签名
	 * @param sig
	 * @return
	 */
	public static String  sign(String sig){
			  String sign = MD5Helper.MD5Encode(sig + key);
			  return sign;
    }
	
	/**
	 * 返回指定URL
	 * @param url 接口名
	 * @return
	 */
	public static String getUrl(String url) {
		if (url == null || url.equals("")) {
			
			url = AAURL.URL;
		}else{
			
			url = AAURL.URL + "/" + url;
		}
		return url;
	}
	
	//是否有效联网
	public static boolean isNetworkAvailable(Context context) {
		// 网络连接的管理
		ConnectivityManager cm = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		if (cm == null)
			return false;
		// 网络的状态信息
		NetworkInfo netinfo = cm.getActiveNetworkInfo();
		if (netinfo == null) {
			return false;
		}
		if (netinfo.isConnected()) {// 网络是否已被打开
			return true;
		}
		return false;
	}
	/**
	 * 联网方式
	 * @param context
	 * @return
	 */
	public static boolean isConnectForNet(Context context) {
		if (getState_Bestpay(context) > 0)
			return true;
		else
			return false;
	}
	public static int getState_Bestpay(Context context) {
		ConnectivityManager connectivity = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		if (connectivity != null) {
			NetworkInfo activeNetInfo = connectivity.getActiveNetworkInfo();
			if (activeNetInfo != null && activeNetInfo.isAvailable()) {
				LogPrint.Print("State",
						"XYZ_NetInfo_netWork:" + activeNetInfo.getExtraInfo()
								+ "|" + activeNetInfo.getTypeName());
				if ("WIFI".equalsIgnoreCase(activeNetInfo.getTypeName())) {
					return WIFI;
				} else if ("MOBILE".equalsIgnoreCase(activeNetInfo
						.getTypeName())) {
					return MOBILE;
				} else if (activeNetInfo.getTypeName().contains("MOBILE")
						|| activeNetInfo.getTypeName().contains("mobile")) {
					return MOBILE;
				}
			} else
				return NONETWORK;
		}
		return NONETWORK_;
	}
	
	/**
	 * @Title: getJsonHttpParams
	 * @Description: TODO(返回系统请求参数(SYSREQ))
	 * @param @param ctx
	 * @param @param method 方法名称
	 * @param @param encrypt 加密方式
	 * @param @param sessionkey 用户会话码
	 * @param @return 系统请求列表
	 * @return List<NameValuePair> 返回类型
	 * @throws
	 */
	public static List<NameValuePair> getJsonHttpParams(Context ctx,
			String method, String encrypt, String sessionkey) {

		List<NameValuePair> json_params = new ArrayList<NameValuePair>();
		json_params.add(new BasicNameValuePair("method", method));
		json_params.add(new BasicNameValuePair("ENCRYPT", encrypt));
		json_params.add(new BasicNameValuePair("TIMESTAMP", getTimeStamp(ctx)));
		json_params.add(new BasicNameValuePair("SESSIONKEY", sessionkey));
		json_params.add(new BasicNameValuePair("V", VERSION));
		json_params.add(new BasicNameValuePair("SIG", ""));

		return json_params;
	}
	/**
	 * @Title: getTimeStamp
	 * @Description: TODO(生成时间戳并返回,也可认为是呼叫序列号。格式为SSSSSS 例如:
	 *               003031。SSSSSS为客户端生成的序列号
	 *               ，每次接口调用时增加1。在同一个用户会话状态下请求传递的SSSSSS需保证递增)
	 * @param @param ctx
	 * @param @return 设定文件
	 * @return String 返回类型
	 * @throws
	 */
	public static String getTimeStamp(Context ctx) {
		SharedPreferences pref = PreferenceManager
				.getDefaultSharedPreferences(ctx);
		long keep = pref.getLong("SEQUENCEID", 0);
		long result = ++keep;
		KEEP = result;
		SharedPreferences.Editor editor = pref.edit();
		editor.putLong("SEQUENCEID", result);
		editor.commit();
		return String.format("%06d", result);
	}
	/**
	 * 判断字符是否为数字
	 * 
	 * @param str
	 * @return
	 */
	public static boolean isNumeric(String str) {
		Pattern pattern = Pattern.compile("[0-9]*");
		Matcher isNum = pattern.matcher(str);
		if (!isNum.matches()) {
			return false;
		}
		return true;
	}
	public static String yuan2fen(String str) {
		double d = 0;
		try {
			d = Double.parseDouble(str);
		} catch (Exception e) {
			// TODO: handle exception
			return d + "";
		}
		d = d * 100;
		d=Math.round(d);
		int i = (int) d;
		String valueStr = String.valueOf(i);
		if (!valueStr.contains("."))
			return valueStr;
		else {
			int index = valueStr.indexOf(".");
			return (String) valueStr.subSequence(0, index - 1);
		}
	}
	
	public static boolean isFen(String str) {
		boolean b = true;
		if (str.contains("."))
			if (str.substring(str.indexOf(".")).length() > 3) {
				b = false;
			}
		return b;
	}
	
	/**
	 * 处理手机号码
	 * @param num
	 * @return
	 */
	public static String formatTelNum(String num) {
		return num.replaceAll("-", "");
	}
	
	/**
	 * 判断手机前3位
	 * 
	 * @param str
	 *            手机号的前3位
	 * @return
	 */
	public static boolean judgeTMobile(String str) {
		boolean legal = false; // 合法性

		if (str.length() >= 3) {
			if ("133".equals(str.substring(0, 3))
					|| "153".equals(str.substring(0, 3))
					|| "180".equals(str.substring(0, 3))
					|| "189".equals(str.substring(0, 3))
					|| "181".equals(str.substring(0, 3))) { // 电信段
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
					|| "147".equals(str.substring(0, 3))) { // 移动段
				legal = true;
			} else if ("130".equals(str.substring(0, 3))
					|| "131".equals(str.substring(0, 3))
					|| "132".equals(str.substring(0, 3))
					|| "145".equals(str.substring(0, 3))
					|| "155".equals(str.substring(0, 3))
					|| "156".equals(str.substring(0, 3))
					|| "185".equals(str.substring(0, 3))
					|| "186".equals(str.substring(0, 3))) { // 联通段
				legal = true;
			} else if("+86".equals(str.substring(0, 3))){
				
				legal = true;
				
			}else {
				legal = false;
			}
		}
		return legal;
	}
}
