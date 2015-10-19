package com.bestpay_aa.activity;

import java.util.List;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;
import android.annotation.SuppressLint;
import android.app.ActivityGroup;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.bestpay_aa.dialog.CustomDialog;
import com.bestpay_aa.net.HttpSSLRequest;
import com.bestpay_aa.util.BestpayAAStatic;
import com.bestpay_aa.util.COMMON;
import com.bestpay_aa.util.LogPrint;
import com.bestpay_aa.util.MyApp;
import com.bestpay_aa.util.Util;
import com.bestpay_aa.view.CustomDialog2;
import com.bestpay_aa.view.CustomProgressBarDialog_1;
import com.bestpay_aa.view.CustomToast;

/**
 * AA收款应用主界面
 * 
 * @author zhouchaoxin
 * 
 */
public class MainActivity extends ActivityGroup implements OnClickListener {

	/**
	 * 动态显示界面
	 */
	private LinearLayout container;
	/**
	 * 收款
	 */
	private LinearLayout linear_aa_gather;
	/**
	 * 付款
	 */
	private LinearLayout linear_aa_payment;
	/**
	 * title名称
	 */
	private TextView textView;
	/**
	 * 负款
	 */
	private TextView textAApayment;
	/**
	 * 收款
	 */
	private TextView text_aa_gather;
	/**
	 * 手机号
	 */
	private String productno = "";
	/**
	 * 归属地
	 */
	private String location = ""; 
	private int whichtab = 0; 
	/**
	 * 获取不到productno,结束activity
	 */
	private final int DIALOG_NO_PRODUCTNO = 1001;
	

	/**
	 * 发起付款
	 */
	private Button btn_gathering;
	
	String dialogPrompt = ""; // 对话框提示语
	String balance = ""; // 账户总余额
	String cashAccountBalance = ""; // 可提现余额
	String nonCashBalance = ""; // 不可提现余额
	String wordsCode = "";// 不可转出原因编码
	String transabilityFlag = ""; // 是否可转：0，不可转，1.可以转
	private static final int DIALOG_INF_0 = 10000; // 查询账户状态对话框的提示
	private SharedPreferences pref;
	SharedPreferences.Editor  editor;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		setContentView(R.layout.aa_activity_group);
		super.onCreate(savedInstanceState);
		//===============调试模式需要在正式发布时关闭================
		//启动/关闭 调试模式
        LogPrint.isPrintLogMsg(false);
		
		pref = PreferenceManager.getDefaultSharedPreferences(this);
		editor = pref.edit();
		Bundle b = this.getIntent().getExtras();
		if (b != null) {
			productno = b.getString("PRODUCTNO");// 电信产品号
			location = b.getString("LOCATION"); // 地区码
			whichtab =b.getInt("WHICHTAB",0);
			
			MyApp app = (MyApp) getApplication();
			app.setProd(productno);
			app.setLocat(location);
		} else {
			productno = "13560368254";
			location = "";

			MyApp app = (MyApp) getApplication();
			app.setProd(productno);
			app.setLocat(location);
		}

		if (productno == null || "".equals(productno)) {
			showDialog(DIALOG_NO_PRODUCTNO);
			return;
		}

		container = (LinearLayout) findViewById(R.id.container);
		linear_aa_gather = (LinearLayout) findViewById(R.id.linear_aa_gather);
		linear_aa_payment = (LinearLayout) findViewById(R.id.linear_aa_payment);
		btn_gathering = (Button) findViewById(R.id.btn_gathering);
		textView = (TextView) findViewById(R.id.text_name);
		textAApayment = (TextView) findViewById(R.id.text_aa_payment);
		text_aa_gather = (TextView)findViewById(R.id.text_aa_gather);
		btn_gathering.setOnClickListener(this);
		linear_aa_gather.setOnClickListener(this);
		linear_aa_payment.setOnClickListener(this);
		//getAccountState();
		IntentFilter filter = new IntentFilter();
		filter.addAction(BestpayAAStatic.PAYACTION);
		filter.addAction(BestpayAAStatic.ACTION_AAGATHERING);
	    registerReceiver(broadcastReceiver, filter);
	}
	@Override
	protected void onResume() {
		super.onResume();
		MyApp app = (MyApp) getApplication();
		productno = app.getProd();
		location = app.getLocat();
		/*if (app.getPage() != null && !app.getPage().equals("") && app.getPage().equals("sureAA")) {
			showView(0);
			//text_aa_gather.setTextColor(getResources().getColor(R.color.color_mw_text_p));
			//textAApayment.setTextColor(getResources().getColor(R.color.color_mw_text_n));
			app.setPage("sureAA");
		}*/
		
	}
	
	BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {  
		@Override  
		public void onReceive(Context context, Intent intent) { 
			String action =intent.getAction();
			Message msg = new Message();
			if(action.equals(BestpayAAStatic.PAYACTION)){
				if(null != intent.getExtras()){
					String notpaynum=intent.getExtras().getString("NOTPAY");  
					if(!TextUtils.isEmpty(notpaynum) && Integer.parseInt(notpaynum) > 0){
						textAApayment.setText("我要付"+"("+notpaynum+")");
					}else{
						textAApayment.setText("我要付");
					}
				}
				showView(1);
			}else if(action.equals(BestpayAAStatic.ACTION_AAGATHERING)){
				
				String gathering_num = intent.getExtras().getString("GATHERING_NUM");
				if (gathering_num != null && !gathering_num.equals("")) {
					
					msg.arg1 = Integer.parseInt(gathering_num);
					msg.what = 10;
					mHandler.sendMessage(msg);
				}
				
				
			}
		 }  
	}; 


	/**
	 * 显示不同界面
	 * @param flag
	 */
	public void showView(int flag) {
		switch (flag) {
		case 0:
			container.removeAllViews();
			Bundle bundle =new Bundle();
			bundle.putString("PRODUCTNO", productno);
			bundle.putString("LOCATION", location);
			container.addView(getLocalActivityManager().startActivity(
					"aagather",
					new Intent(MainActivity.this, AAGatheringActivity.class).putExtras(bundle))
					.getDecorView());
			break;
		case 1:
			container.removeAllViews();
			Bundle b =new Bundle();
			b.putString("PRODUCTNO", productno);
			b.putString("LOCATION", location);
			b.putString("TRANSABILITYFLAG", transabilityFlag);
			b.putString("NOTICE", dialogPrompt);
			container.addView(getLocalActivityManager().startActivity(
					"aapayment",
					new Intent(MainActivity.this, AAPaymentActivity.class).putExtras(b))
					.getDecorView());
			break;

		default:
			break;
		}
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.linear_aa_gather:
			showView(0);
			textView.setText("AA收款");
			text_aa_gather.setTextColor(getResources().getColor(R.color.color_mw_text_p));
			textAApayment.setTextColor(getResources().getColor(R.color.color_mw_text_n));
			break;
		case R.id.linear_aa_payment:
			showView(1);
			textView.setText("AA收款");
			textAApayment.setTextColor(getResources().getColor(R.color.color_mw_text_p));
			text_aa_gather.setTextColor(getResources().getColor(R.color.color_mw_text_n));
			break;
		case R.id.btn_gathering:
			Intent intent = new Intent();
			intent.setClass(MainActivity.this,
					AAGatheringSponsorActivity.class);
			startActivity(intent);
			break;

		default:
			break;
		}

	}
	
	private static final int ERROR = 1314;
	@Override
	protected Dialog onCreateDialog(int id) {
		CustomDialog.Builder customBuilder = null;
		switch (id) {
		case DIALOG_NO_PRODUCTNO:
			customBuilder = new CustomDialog.Builder(MainActivity.this);
			customBuilder
					.setTitle(getString(R.string.mw_dialog_title))
					.setMessage("手机号码不存在，请退出程序！")
					.setPositiveButton(R.string.mw_sure,
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int which) {
									MainActivity.this.finish();
								}
							});
			return customBuilder.create();
		case DIALOG_INF_0: // 账户状态查询对话框
			CustomProgressBarDialog_1.Builder note = new CustomProgressBarDialog_1.Builder(
					MainActivity.this, false);
			note.setTitle("请稍候").setMessage(prompt).setCancel(true)
					.setCancelListener(new OnCancelListener() {
						@Override
						public void onCancel(DialogInterface dialog) {
							MainActivity.this.finish();
						}
					});
			return note.create();

		case ERROR: // 解决无网络状况下的应用终止问题
			CustomDialog2.Builder customBuilder2 = new CustomDialog2.Builder(
					MainActivity.this);
			customBuilder2
					.setTitle(dialogPrompt)
					.setMessage("")
					.setPositiveButton("确定",
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int which) {
									MainActivity.this.removeDialog(ERROR);
									MainActivity.this.finish();
								}
							});
			return customBuilder2.create();
		default:
			break;
		}
		return super.onCreateDialog(id);
	}
	
	/**
	 * 查询账户状态，我们可以得到以下信息：
	 * 
	 * 1，得到用户是否开户 transabilityFlag
	 * 
	 * 2，得到用户账户余额情况
	 */
	String prompt = "";
	private void getAccountState() {

		AsyncTask<String, Integer, String> task = new AsyncTask<String, Integer, String>() {
			@Override
			protected void onPreExecute() {
				prompt = "正在查询账户状态，请稍候...";
				showDialog(DIALOG_INF_0);
			}

			@Override
			protected String doInBackground(String... params) {
				String produceNo = params[0];
				String location = params[1];

				List<NameValuePair> json_params = Util.getJsonHttpParams(
						MainActivity.this, "queryAATransferBalance", "", "");
				json_params.add(new BasicNameValuePair("PRODUCTNO", produceNo));
				json_params.add(new BasicNameValuePair("LOCATION", location));
				String result = "";
				result = HttpSSLRequest.httpRequest(json_params,
						MainActivity.this);

				System.out.println("查询账户状态(余额)服务端返回的应答参数为：" + result);
				return result;
			}

			@Override
			protected void onPostExecute(String result) {
				MainActivity.this.removeDialog(DIALOG_INF_0);
				super.onPostExecute(result);
				Message message = mHandler.obtainMessage();
				message.what = 20;
				if (result != null) {
					// 出错时的处理
					if (result.equals(COMMON.TIMEOUT)) {
						dialogPrompt = "连接超时!请检查网络连接!";
						showDialog(ERROR);
					} else if (result.equals(COMMON.IOEXCEPTION)) {
						dialogPrompt = "I/O异常!";
						showDialog(ERROR);
					} else if (result.equals(COMMON.EXCEPTION)) {
						dialogPrompt = "其他异常!";
						showDialog(ERROR);
					} else if (result.equals("")) {
						dialogPrompt = "连接超时!请检查网络连接!";
						showDialog(ERROR);
					} else {
						try {
							JSONObject jsonObj = new JSONObject(result);
							String errorCode = jsonObj.getString("ERRORCODE");
							String errorMsg = jsonObj.getString("ERRORMSG");
							if (errorCode.equals("000000")) {// 成功
								message.arg1 = 1;
								mHandler.sendMessage(message);
								
								balance = jsonObj.getString("BALANCE"); // 账户总余额
								cashAccountBalance = jsonObj
										.getString("CASHBALANCE"); // 可提现余额
								nonCashBalance = jsonObj
										.getString("NONCASHBALANCE"); // 不可提现余额
								transabilityFlag = jsonObj
										.getString("TRANSABILITYFLAG");
								editor.putString(BestpayAAStatic.BALANCE, balance);
								editor.putString(BestpayAAStatic.CASHBALANCE, cashAccountBalance);
								editor.putString(BestpayAAStatic.NONCASHBALANCE, nonCashBalance);
								editor.commit();
								// 不可转出编码
								wordsCode = jsonObj
										.getString("TRANSABILITYCODE");
								if (transabilityFlag.equals("0")) { // 他有他不能转的理由，理由如下
									
									if (wordsCode.equals("Z00025")) {
										prompt(MainActivity.this, "暂时无法向本人转账");
										MainActivity.this.finish();
									} else if (wordsCode.equals("Z00015")) {// 交易超过当日限额
										showNoticeDialog(R.string.text5,R.string.text51);	
									} else if (wordsCode.equals("Z00018")) {
										showNoticeDialog(R.string.text6,R.string.text61);//非实名		
									} else if (wordsCode.equals("Z00032")) {
										showNoticeDialog(R.string.text7,R.string.text71);
									} else if (wordsCode.equals("Z00033")) {
										showNoticeDialog(R.string.text8,R.string.text81);		
									}
								}

								if (cashAccountBalance != null
										&& !"".equals(cashAccountBalance.trim())) { // 可提现余额

									System.out.println("账户总余额==============="
											+ balance);
									System.out.println("可提现余额=============="
											+ cashAccountBalance);
									System.out
											.println("不可提现余额===================="
													+ nonCashBalance);
									Intent intent = new Intent(Intent.ACTION_MAIN);  
									Bundle b=new Bundle();
									b.putString("BALANCE", balance);
									b.putString("CASHACCOUNTBALANCE", cashAccountBalance);
									b.putString("NONCASHBALANCE", nonCashBalance);
									b.putBoolean("QUERYACCOUNT", true);
									intent.putExtras(b);
									sendBroadcast(intent);
								} else {
									dialogPrompt = "解析异常，接口数据错误";
									showDialog(ERROR);
								}
							} else if (errorCode.equals("Z00004")) { // 未开户
								message.arg1 = 0;
								mHandler.sendMessage(message);
								dialogPrompt = "您当前尚未开通翼支付账户，开户后即可使用AA收款功能";
								showDialog(ERROR);
							} else if (errorCode.equals("Z00016")) {
								message.arg1 = 0;
								mHandler.sendMessage(message);
								// 账户余额不足
								// dialogPrompt = "亲，您的账户余额不足";
							} else if(errorCode.equals("-999101")){
								message.arg1 = 0;
								mHandler.sendMessage(message);
								dialogPrompt = "业务处理异常(" + errorCode + ")";
								showDialog(ERROR);
							}else {
								message.arg1 = 0;
								mHandler.sendMessage(message);
								dialogPrompt = errorMsg + "(" + errorCode + ")";
								showDialog(ERROR);
							}
						} catch (JSONException e) {
							message.arg1 = 0;
							mHandler.sendMessage(message);
							dialogPrompt = "解析异常，接口数据错误（JSON）";
							showDialog(ERROR);
							e.printStackTrace();
						}
					}
				} else {
					message.arg1 = 0;
					mHandler.sendMessage(message);
					dialogPrompt = "连接超时!请检查网络连接!";
					showDialog(ERROR);
				}
			}
		};
		task.execute(productno, location);
	}
	protected void prompt(Context context,String p) {
		CustomToast toast = new CustomToast(context,p);
		toast.show();
	}
	private void showNoticeDialog(int i1, int i2) {
		Resources res = getResources();
		final CustomDialog2 dialog = new CustomDialog2.Builder(
				MainActivity.this).setTitle("")
				.setTitle(res.getString(i1))
				.setMessage(res.getString(i2))
				.setCancel(true)
				.setPositiveButton("确定", new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
						MainActivity.this.finish();
					}
				}).create();
		dialog.show();
		dialog.setCancelable(false);
	}
	@Override
	protected void onDestroy() {
		super.onDestroy();
		unregisterReceiver(broadcastReceiver);
	}
	/**
	 * 更新界面UI
	 */
	@SuppressLint("HandlerLeak")
	private Handler mHandler = new Handler(){
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case 10:
				if(msg.arg1 > 0){
					text_aa_gather.setText("我要收"+"("+msg.arg1+")");
				}else{
					text_aa_gather.setText("我要收");
				}
				break;
			case 20: //查询账户状态是否成功
				if (msg.arg1 == 1) {
					
					showView(whichtab);
				}
				
				break;
			default:
				break;
			}
		}
		
	};
}
