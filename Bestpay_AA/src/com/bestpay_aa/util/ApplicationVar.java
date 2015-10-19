package com.bestpay_aa.util;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;

/**
 * 用来保存变量是否第一次登录
 * @author zhouchaoxin
 *
 */
public class ApplicationVar{
	/**
	 * 是否第一次登录状态存储
	 * @param context
	 * @param isFirstLogin
	 */
	public static void setIsFirstLogin(Context context,boolean isFirstLogin){
		
		try{
			SharedPreferences sharedPreferences = context.getSharedPreferences("com.bestpay.aa", Activity.MODE_PRIVATE);
			SharedPreferences.Editor editor = sharedPreferences.edit();
			editor.putBoolean("isFirstLogin", isFirstLogin);
			editor.commit();
		}catch(Exception e){
			e.printStackTrace();
		}
		
	}
	
	/**
	 * 获取第一次登录状态
	 * @param context
	 * @return
	 */
    public static boolean getIsFirstLogin(Context context){
    	try{
    		SharedPreferences sharedPreferences = context.getSharedPreferences("com.bestpay.aa", Activity.MODE_PRIVATE);
    		return sharedPreferences.getBoolean("isFirstLogin", false);
    	}catch(Exception e){
    		e.printStackTrace();
    	}
    	return false;
    }
	
}
