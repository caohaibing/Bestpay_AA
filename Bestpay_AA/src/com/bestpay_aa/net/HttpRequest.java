package com.bestpay_aa.net;

import java.io.IOException;
import java.util.List;
import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.params.ConnRoutePNames;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import android.annotation.SuppressLint;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import com.bestpay_aa.util.LogPrint;
import com.bestpay_aa.util.Util;

/**
 * http请求
 * 
 * @author zhouchaoxin
 * 
 */
@SuppressLint("NewApi")
public class HttpRequest {

	public static final String SUCCESS = "000000";
	public final static String SESSIONKEY_FAIL = "010040";

	public static List<NameValuePair> oldParams;

	/**
	 * 2011.08.24 客户端定义的错误码 ERRORCODE_CTNET ERRORCODE_NOMATCH
	 */
	public static String ERRORCODE_CTNET = "990001";
	public static String ERRORCODE_NOMATCH = "990002";
	public static String ERRORCODE_OUT = "超时";
	public static String ERRORCODE_707 = "707";

	public static String httpRequest(String url, List<NameValuePair> params,
			Context ctx) throws ClientProtocolException, IOException {
		if (Util.getState_Bestpay(ctx) == Util.WIFI
				|| Util.getState_Bestpay(ctx) == Util.MOBILE) {
			LogPrint.Print("bestpay", "bestpay_request:" + params.toString());
			System.out.println("bestpay_request:" + params.toString());
			String uriAPI = url;

			LogPrint.Print("bestpay", "bestpay_url:" + uriAPI);
			HttpPost httpost = new HttpPost(uriAPI);

			DefaultHttpClient httpclient = new MyHttpClient_New(ctx);

			httpclient.getParams().setParameter(
					HttpConnectionParams.CONNECTION_TIMEOUT,
					new Integer(20 * 1000));

			httpclient.getParams().setParameter(
					HttpConnectionParams.SO_TIMEOUT, new Integer(20 * 1000));
			HttpResponse httpResponse = null;
			httpost.setEntity(new UrlEncodedFormEntity(params, HTTP.UTF_8));
			/**
			 * 设置代理
			 */
			HttpHost proxy = getProxy(ctx);

			if (proxy != null) {
				httpclient.getParams().setParameter(
						ConnRoutePNames.DEFAULT_PROXY, proxy); // 设置代理，wap取手机号

				try {
					httpResponse = httpclient.execute(proxy, httpost); // 设置代理，wap取手机号
				} catch (Exception e) {
					httpost.abort();
					throw new IOException(e);
				}
			} else {
				try {
					httpResponse = httpclient.execute(httpost);
				} catch (Exception e) {
					httpost.abort();
					throw new IOException(e);
				}
			}
			if (httpResponse != null) {
				HttpEntity entity = httpResponse.getEntity();
				LogPrint.Print("bestpay", "StatusCode:"
						+ httpResponse.getStatusLine().getStatusCode());
				if (httpResponse.getStatusLine().getStatusCode() == 200) {

					String strResult = EntityUtils.toString(entity);
					LogPrint.Print("bestpay", "bestpay_response:" + strResult);
					httpclient.getConnectionManager().shutdown();
					return strResult;
				} else {
					try {
						httpResponse = httpclient.execute(httpost);
					} catch (Exception e) {
						httpost.abort();
						throw new IOException(e);
					}
					if (httpResponse != null) {
						entity = httpResponse.getEntity();
						if (httpResponse.getStatusLine().getStatusCode() == 200) {
							String strResult = EntityUtils.toString(entity);
							LogPrint.Print("bestpay", "bestpay_response:"
									+ strResult);
							httpclient.getConnectionManager().shutdown();
							return strResult;
						} else {
							return "{\"ERRORCODE\":\""
									+ httpResponse.getStatusLine()
											.getStatusCode()
									+ "\",\"ERRORMSG\":\"返回的数据位空，请重试。\"}";
						}
					}
				}
			}
		} else {
			return "{\"ERRORCODE\":\"" + ERRORCODE_NOMATCH
					+ "\",\"ERRORMSG\":\"无可用网络\"}";
		}
		return "网络超时，请重新链接！";
	}

	public static HttpHost getProxy(Context mContext) {
		ConnectivityManager cm = (ConnectivityManager) mContext
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo ni = cm.getActiveNetworkInfo();
		if (ni != null && ni.isAvailable()
				&& ni.getType() == ConnectivityManager.TYPE_MOBILE) {
			String proxyHost = android.net.Proxy.getDefaultHost();
			int port = android.net.Proxy.getDefaultPort();
			if (proxyHost != null) {
				LogPrint.Print(
						"lmn_proxy",
						"lmn_bestpay_proxy:"
								+ android.net.Proxy.getDefaultHost() + "|"
								+ android.net.Proxy.getDefaultPort() + "|"
								+ ni.getType());

				return new HttpHost(proxyHost, port);
			}
		}

		return null;
	}

}
