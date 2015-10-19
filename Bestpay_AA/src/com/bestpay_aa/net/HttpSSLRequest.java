package com.bestpay_aa.net;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.params.ConnRoutePNames;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;

import com.bestpay_aa.util.COMMON;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;

public class HttpSSLRequest {

	public static final String SUCCESS = "000000";
	
	
	/**
	 * 	AA生产环境
	 */
	public static final String URL = "https://client.bestpay.com.cn/MEPF_INF2/aapay";	
    /**
     * AA测试环境
     */
	//public static final String URL = "http://121.33.197.198:7090/MEPF_INF2/aapay";

	 //生产环境
	//public static final String PARTNERID = "000016900000";
	//测试环境
	public static final String PARTNERID = "test000009200000";
	
	public static String httpRequest(List<NameValuePair> params, Context ctx) {
		System.out.println(params.toString());
		String uriAPI = URL;
		HttpPost httpost = new HttpPost(uriAPI);
		HttpParams httpParameters;
		int timeoutConnection = 60000; // ���ӳ�ʱ
		int timeoutSocket = 60000; // ����ʱ
		int connectCount = 0; // ���Ӵ���
		boolean flag = true;
		String resultStr = "";

		// getLocalHost(ctx);
		// getWifiInfo(ctx);
		// initNetworkInfo(ctx);

		httpParameters = new BasicHttpParams();
		HttpConnectionParams.setConnectionTimeout(httpParameters,
				timeoutConnection);
		HttpConnectionParams.setSoTimeout(httpParameters, timeoutSocket);
		DefaultHttpClient httpclient = new MyHttpClient_New(ctx);

		try {
			httpost.setEntity(new UrlEncodedFormEntity(params, HTTP.UTF_8));
			HttpHost proxy = getProxy(ctx);
			if (proxy != null) {
				SharedPreferences settings = ctx.getSharedPreferences(
						COMMON.SETTING_INFO, 0);
				String proxyFlag = settings.getString("proxyFlag", "");

				if ("".equals(proxyFlag)) {
					proxyFlag = "0";
				}
				do {
					// �ж��ϴ����ӷ�ʽ��proxyFlag = 0 ����?proxyFlag = 0 �������
					if ("0".equals(proxyFlag)) {
						// �����Ӵ������2ʱ����������
						if (connectCount != 2) {
							String result = connectWithProxy(ctx, httpclient,
									httpost, proxy);
							connectCount++;
							// ��ʱ
							if ("".equals(result)) {

							}
							// ��������Ӳ��ɹ����������������
							else if ("707".equals(result)) {
								String result1 = connectWithoutProxy(ctx,
										httpclient, httpost);
								connectCount++;
								if (!"".equals(result1)) {
									flag = false;
									resultStr = result1;
									proxyFlag = "1";
								}
							}
							// ��������ӳɹ���ֱ���˳�ѭ��
							else if (!"".equals(result)) {
								flag = false;
								resultStr = result;
							}
						}
						// �����Ӵ������2ʱ���˳�ѭ��
						else {
							flag = false;
						}
					} else if ("1".equals(proxyFlag)) {
						String result = connectWithoutProxy(ctx, httpclient,
								httpost);
						connectCount++;
						// ����������ӳɹ���ֱ���˳�ѭ��
						if (!"".equals(result)) {
							flag = false;
							resultStr = result;
						}
						// ����������Ӳ��ɹ��������������
						else {
							proxyFlag = "0";
						}
					}
				} while (flag);

				settings.edit().putString("proxyFlag", proxyFlag).commit();
				return resultStr;
			} else {
				return connectWithoutProxy(ctx, httpclient, httpost);
			}
		} catch (UnsupportedEncodingException e) {

			e.printStackTrace();
		}

		return "";
	}

	public static String connectWithoutProxy(Context ctx,
			DefaultHttpClient httpclient, HttpPost httpost) {
		try {
			HttpResponse httpResponse = null;
			httpResponse = httpclient.execute(httpost);
			java.util.Date date = new java.util.Date();
			DateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			System.out.println(format.format(date));

			HttpEntity entity = httpResponse.getEntity();
			// ��״̬��Ϊ200 ok
			if (httpResponse.getStatusLine().getStatusCode() == 200) {
				// ȡ����Ӧ�ִ�
				String strResult = EntityUtils.toString(entity);
				// Util.printSystemLog("response", strResult);
				System.out.println(strResult);
				date = new java.util.Date();
				format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
				return strResult;
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return "";
	}

	public static String connectWithProxy(Context ctx,
			DefaultHttpClient httpclient, HttpPost httpost, HttpHost proxy) {
		try {
			HttpResponse httpResponse = null;
			httpclient.getParams().setParameter(ConnRoutePNames.DEFAULT_PROXY,
					proxy); // ���ô��?wapȡ�ֻ��
			httpResponse = httpclient.execute(proxy, httpost); // ���ô��?wapȡ�ֻ��
			java.util.Date date = new java.util.Date();
			DateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

			HttpEntity entity = httpResponse.getEntity();
			int s = httpResponse.getStatusLine().getStatusCode();
			// ��״̬��Ϊ200 ok
			if (httpResponse.getStatusLine().getStatusCode() == 200) {
				// ȡ����Ӧ�ִ�
				String strResult = EntityUtils.toString(entity);
				// Util.printSystemLog("response", strResult);
				date = new java.util.Date();
				format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
				return strResult;
			} else if (httpResponse.getStatusLine().getStatusCode() == 707) {
				date = new java.util.Date();
				format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
				return "707";
			} else {
				date = new java.util.Date();
				format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
				return "";
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return "";
	}

	public static HttpHost getProxy(Context mContext) {
		ConnectivityManager cm = (ConnectivityManager) mContext
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo ni = cm.getActiveNetworkInfo();
		if (ni != null && ni.isAvailable()
				&& ni.getType() == ConnectivityManager.TYPE_MOBILE) {
			// saveCrashInfoToFile(String.valueOf(ni.getType()),mContext);
			String proxyHost = android.net.Proxy.getDefaultHost();
			int port = android.net.Proxy.getDefaultPort();
			if (proxyHost != null) {
				System.out.println("proxy:"
						+ android.net.Proxy.getDefaultHost() + "|"
						+ android.net.Proxy.getDefaultPort() + "|"
						+ ni.getType());
				return new HttpHost(proxyHost, port);
			}
		}
		return null;
	}

	public static void getWifiInfo(Context context) {
		WifiManager wifi = (WifiManager) context
				.getSystemService(Context.WIFI_SERVICE);
		WifiInfo info = wifi.getConnectionInfo();
		StringBuffer buffer = new StringBuffer();
		buffer.append("HiddenSSID=" + info.getHiddenSSID() + "\n");
		buffer.append("IpAddress=" + info.getIpAddress() + "\n");
		buffer.append("LinkSpeed=" + info.getLinkSpeed() + "\n");
		buffer.append("NetworkId=" + info.getNetworkId() + "\n");
		buffer.append("Rssi=" + info.getRssi() + "\n");
		buffer.append("SSID=" + info.getSSID() + "\n");
		buffer.append("MacAddress=" + info.getMacAddress() + "\n");
	}

	public static void initNetworkInfo(Context context) {
		ConnectivityManager mag = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		// �˴������ǰ��������
		StringBuffer buffer = new StringBuffer();
		buffer.append("\nActive:\n");
		NetworkInfo info = mag.getActiveNetworkInfo();
		buffer.append("ExtraInfo=" + info.getExtraInfo() + "\n");
		buffer.append("SubtypeName=" + info.getSubtypeName() + "\n");
		buffer.append("TypeName=" + info.getTypeName() + "\n");

		buffer.append("\nWifi:\n");
		NetworkInfo wifiInfo = mag
				.getNetworkInfo(ConnectivityManager.TYPE_WIFI);

		buffer.append("ExtraInfo=" + wifiInfo.getExtraInfo() + "\n");
		buffer.append("SubtypeName=" + wifiInfo.getSubtypeName() + "\n");
		buffer.append("TypeName=" + wifiInfo.getTypeName() + "\n");
		NetworkInfo mobInfo = mag
				.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
		buffer.append("\nMobile:\n");
		buffer.append("ExtraInfo=" + mobInfo.getExtraInfo() + "\n");
		buffer.append("SubtypeName=" + mobInfo.getSubtypeName() + "\n");
		buffer.append("TypeName=" + mobInfo.getTypeName() + "\n");

	}

}
