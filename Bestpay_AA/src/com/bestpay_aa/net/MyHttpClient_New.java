package com.bestpay_aa.net;

/**
 * @author yansw
 * 2013.05.28����
 * �޸����ݣ�����Ҫ��֤�������˵�֤��
 * https����
 * 
 */
import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import org.apache.http.HttpVersion;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;
import org.apache.http.protocol.HTTP;

import android.content.Context;

public class MyHttpClient_New extends DefaultHttpClient {
	final Context context;

	public MyHttpClient_New(Context context) {
		this.context = context;
	}

	public MyHttpClient_New(Context context, HttpParams httpParams) {
		this.context = context;
	}

	@Override
	protected HttpParams createHttpParams() {
		// TODO Auto-generated method stub
		return super.createHttpParams();
	}

	@Override
	protected ClientConnectionManager createClientConnectionManager() {
		SchemeRegistry registry = new SchemeRegistry();
		registry.register(new Scheme("http", PlainSocketFactory
				.getSocketFactory(), 80));
		// Register for port 443 our SSLSocketFactory with our keystore
		// to the ConnectionManager
		try {
			KeyStore keyKeyStore = KeyStore.getInstance("BKS");
			SSLSocketFactory sf = new SSLSocketFactoryEx(keyKeyStore);

			registry.register(new Scheme("https", sf, 443));
			HttpParams params = new BasicHttpParams();
			HttpProtocolParams.setVersion(params, HttpVersion.HTTP_1_1);
			HttpProtocolParams.setContentCharset(params, HTTP.UTF_8);

			return new ThreadSafeClientConnManager(params, registry);

		} catch (Exception e) {
			return null;

		}
	}

	public class SSLSocketFactoryEx extends SSLSocketFactory {

		SSLContext sslContext = SSLContext.getInstance("TLS");

		public SSLSocketFactoryEx(KeyStore truststore)

		throws NoSuchAlgorithmException, KeyManagementException,

		KeyStoreException, UnrecoverableKeyException {

			super(truststore);

			try {

				sslContext.init(null,
						new TrustManager[] { new MyTrustManager() },
						new SecureRandom());
			} catch (Exception e) {
				throw new AssertionError(e);
			}
		}

		@Override
		public Socket createSocket(Socket socket, String host, int port,
				boolean autoClose) throws IOException, UnknownHostException {
			return sslContext.getSocketFactory().createSocket(socket, host,
					port, autoClose);
		}

		@Override
		public Socket createSocket() throws IOException {
			return sslContext.getSocketFactory().createSocket();
		}

	}

	private class MyTrustManager implements X509TrustManager {

		@Override
		public void checkClientTrusted(X509Certificate[] chain, String authType)

		throws CertificateException {
			// TODO Auto-generated method stub

		}

		@Override
		public void checkServerTrusted(X509Certificate[] chain, String authType)

		throws CertificateException {
			// TODO Auto-generated method stub

		}

		@Override
		public X509Certificate[] getAcceptedIssuers() {

			// TODO Auto-generated method stub

			return null;
		}
	}

}
