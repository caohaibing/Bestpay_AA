package com.bestpay_aa.activity;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import com.bestpay_aa.dialog.CustomDialog;
import com.bestpay_aa.dialog.PaySuccessDialog;
import com.bestpay_aa.net.HttpSSLRequest;
import com.bestpay_aa.util.BestpayAAStatic;
import com.bestpay_aa.util.COMMON;
import com.bestpay_aa.util.MyApp;
import com.bestpay_aa.util.Util;
import com.bestpay_aa.view.CustomDialog2;
import com.bestpay_aa.view.CustomProgressBarDialog_1;
import com.bestpay_aa.view.PassInput;
import com.huateng.encrypt.PinkeyEncrypt;

import android.app.Dialog;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.ContactsContract;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

/**
 * @ClassName :
 * @Description : <类描述>
 * @Developer : <zx>
 * @Modifier : <修改者姓名>
 * @EditContent : <修改内容>
 * @EditDate : 2014-03-07 下午2:59:00 [created]
 */
public class AaPaymentItemActivity extends BaseActivity implements OnClickListener{
	private static final int DIALOG_INF_PAY3 = 105;
	private static final int PAY_SUCESSFUL2 = 106;
	private static final int DIALOG_ERROR_PAY2 = 8091; // 错误提示
	private static final int DIALOG_ERRORPWD_PAY2 = 84964; // 密码错误提示

	public final int LOGIN_TYPE_C=1;
	public final int LOGIN_TYPE_G=2;
	String productNo = "";
	String location = "";
	String theme = "";
	String perMoney ="";//需支付金额
	String remark = "";
	String serialNo = "";
	String transStat = "";
	String transDate = "";
	String failureTime = "";
	int notpayNum =0;
	ScrollView sv ;
	private String zhanghu = ""; // 转入方的账户
	private String partnerId = ""; // 业管平台商户编码
	private String partnerOrderId = "";// 农村金融业务订单号
	private String orderId = "";// 业务管理平台订单号
	private String  balance=""; //账户总金额
	private Boolean  needpayflag=false; //是否需要支付
	private Boolean  paySucflag=false; //支付成功返回
	private String cashbalance = ""; //可提现余额
	private String noncashbalance = ""; //不可提现余额
	String fundstype = "";//AA付款资金类型
	String isAccountOpen = "";
//	LinearLayout notice_lyt;
	LinearLayout paylyt;
	LinearLayout rechargelyt;
	Button payButton;
	Button rechargeButton;
	Button del;
	TextView aaTitle;
	TextView themeTv;
	TextView perMoneyTv;
	TextView remarkTv;
	TextView transDateTv;
	TextView failureDateTv;
	TextView failureDateText;
	TextView gatherNoTv;
	private PassInput pi; // 支付密码
	private String psw = "";  //密码输入框中的输入密码
	private String dialogPrompt = "";// 对话框提示语
	private SharedPreferences pref;
	SharedPreferences.Editor  editor;
	String paymentorderid ="";
	String gatherName ="";
	private PaySuccessDialog.Builder paysucDialog ;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		pref = PreferenceManager.getDefaultSharedPreferences(this);
		editor = pref.edit();
		Intent intent = getIntent();
		Bundle b = intent.getExtras();
		if (b != null) {
			productNo = b.getString("PRODUCTNO");
			location = b.getString("LOCATION");
			theme=b.getString("THEME");
			perMoney=b.getString("PERMONEY");
			remark = b.getString("REMARK");
			serialNo = b.getString("SERIALNO");
			transStat = b.getString("TRANSSTAT");
			transDate = b.getString("TRANSDATE");
			failureTime = b.getString("FAILURETIME");
			zhanghu = b.getString("Shouji");
			cashbalance =b.getString("CASHBALANCE");
			fundstype = b.getString("FUNDSTYPE");
			notpayNum = b.getInt("NOTPAYNUM");
			needpayflag =b.getBoolean("NEEDPAYFLAG");
			partnerId =b.getString("PARTNERID");
			orderId =b.getString("ORDERID");
			partnerOrderId =b.getString("PARTNERORDERID");
		}
			
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.aa_gathering_pay_details);
	}
	@Override
	protected void onResume() {
		super.onResume();
		paySucflag=false;
		balance = pref.getString(BestpayAAStatic.BALANCE, "0");
		cashbalance = pref.getString(BestpayAAStatic.CASHBALANCE, "0");
		noncashbalance = pref.getString(BestpayAAStatic.NONCASHBALANCE, "0");
		initView();
	}
	private void initView() {
		sv = (ScrollView) findViewById(R.id.sv);
		del= (Button) findViewById(R.id.del_wty);//删除按钮
		paylyt=(LinearLayout) findViewById(R.id.paylyt);
		rechargelyt=(LinearLayout) findViewById(R.id.rechargelyt);
		payButton=(Button) findViewById(R.id.payButton);
		rechargeButton=(Button) findViewById(R.id.rechargeButton);
		themeTv= (TextView) findViewById(R.id.theme);
		perMoneyTv= (TextView) findViewById(R.id.perMoney);
		remarkTv= (TextView) findViewById(R.id.remark);
		transDateTv= (TextView) findViewById(R.id.transDate);
		failureDateTv= (TextView) findViewById(R.id.failureDate);
		failureDateText= (TextView) findViewById(R.id.failureDateText);
		gatherNoTv= (TextView) findViewById(R.id.gatherNo);
		pi = (PassInput) findViewById(R.id.password_xls);
 		aaTitle= (TextView) findViewById(R.id.aaTitle);
		aaTitle.setText("账单详情");
		payButton.setOnClickListener(this);
		rechargeButton.setOnClickListener(this);
		del.setOnClickListener(this);
		pi.setOnFocusListener(sv, this);
		themeTv.setText(theme);
		perMoneyTv.setText(COMMON.convert(perMoney) +"元");
		remarkTv.setText(remark);
		try {
			transDateTv.setText(new SimpleDateFormat("MM-dd HH:mm")
				.format(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(transDate)));
		} catch (ParseException e) {
			e.printStackTrace();
			transDateTv.setText("未知");
		}
		try {
			failureDateTv.setText(new SimpleDateFormat("MM-dd HH:mm")
				.format(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(failureTime)));
		} catch (ParseException e) {
			e.printStackTrace();
			failureDateTv.setText("未知");
		}
		if(zhanghu !=null  && !zhanghu.equals("")){
			gatherName =getContactNamebyPhoneNo(this, zhanghu);
		}
		if(!"".equals(gatherName)){
			gatherNoTv.setText(zhanghu+" "+gatherName);
		}else{
			gatherNoTv.setText(zhanghu);
		}
		paylyt.setVisibility(View.GONE);
		rechargelyt.setVisibility(View.GONE);
		if(transStat.equals(BestpayAAStatic.S0C) ){
			failureDateText.setText("付款时间:");
		}else{
			failureDateText.setText("过期时间:");
		}
		if(needpayflag){
			cashbalance=TextUtils.isEmpty(cashbalance)?"0":cashbalance;
			perMoney=TextUtils.isEmpty(perMoney)?"0":perMoney;
			if(Double.valueOf(perMoney) > Double.valueOf(cashbalance)){
				paylyt.setVisibility(View.GONE);
				rechargelyt.setVisibility(View.VISIBLE);
			}else{
				paylyt.setVisibility(View.VISIBLE);
				rechargelyt.setVisibility(View.GONE);
			}
		}else{
			paylyt.setVisibility(View.GONE);
			rechargelyt.setVisibility(View.GONE);
		}
	}


	@Override
	public void onClick(View v) {
		switch (v.getId()) {
			case R.id.payButton:
				pi.closeWindow();  //关掉安全密码输入法
				cashbalance=TextUtils.isEmpty(cashbalance)?"0":cashbalance;
				perMoney=TextUtils.isEmpty(perMoney)?"0":perMoney;
				if(Double.valueOf(balance) > Double.valueOf(perMoney)){
					//调接口支付
					payMoney();
				}else{
					paylyt.setVisibility(View.GONE);
					rechargelyt.setVisibility(View.VISIBLE);
				}
				break;
			case R.id.del_wty:  //支付密码的删除按钮
				pi.setPasswordText("");
				break;
			case R.id.rechargeButton: // 账户充值
				recharge();
				break;	
		default:
			break;
		}
	}
	private static final int RECHARGE_SUCESSFUL2 = 1470;
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == COMMON.REQUESTCODE) {
			switch (resultCode) {
			case 0:
				System.out.println("成功处理订单");
				queryBalanceAgain2();
				dialogPrompt = "充值成功";
				showDialog(RECHARGE_SUCESSFUL2);
				break;

			default:
//				AaPaymentItemActivity.this.finish();
				break;
			}
		}
	}
	private CustomDialog.Builder  customBuilder;
	@Override
	protected Dialog onCreateDialog(int id) {
		switch (id) {
		case DIALOG_INF_PAY3:
			CustomProgressBarDialog_1.Builder note = new CustomProgressBarDialog_1.Builder(
					AaPaymentItemActivity.this, false);
			note = new CustomProgressBarDialog_1.Builder(
					AaPaymentItemActivity.this, false);
			note.setTitle("请稍候").setMessage(dialogPrompt).setCancel(true);
			return note.create();
			
		case DIALOG_ERROR_PAY2:
			CustomDialog2.Builder customBuilder2 = new CustomDialog2.Builder(
					AaPaymentItemActivity.this);
			customBuilder2
			.setTitle(dialogPrompt)
			.setMessage("")
			.setPositiveButton("确定",
					new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog,
								int which) {
							AaPaymentItemActivity.this
									.removeDialog(DIALOG_ERROR_PAY2);												
							AaPaymentItemActivity.this.finish();
						}
					});
			return customBuilder2.create();	
		case DIALOG_ERRORPWD_PAY2:
				customBuilder2 = new CustomDialog2.Builder(
					AaPaymentItemActivity.this);
			customBuilder2
			.setTitle(dialogPrompt)
			.setMessage("")
			.setPositiveButton("确定",
					new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog,
								int which) {
							AaPaymentItemActivity.this
									.removeDialog(DIALOG_ERRORPWD_PAY2);												
						}
					});
			return customBuilder2.create();	
		case PAY_SUCESSFUL2: //支付成功
			paysucDialog = new PaySuccessDialog.Builder(AaPaymentItemActivity.this);
			return paysucDialog.create();
		case RECHARGE_SUCESSFUL2: // 充值成功，给个反馈
			customBuilder2 = new CustomDialog2.Builder(AaPaymentItemActivity.this);
			customBuilder2
					.setTitle(dialogPrompt)
					.setMessage("")
					.setPositiveButton("确定",
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int which) {
									AaPaymentItemActivity.this
											.removeDialog(RECHARGE_SUCESSFUL2);
									AaPaymentItemActivity.this.finish();
								}
							});
			return customBuilder2.create();	
		case DIALOG_PAY_CANCEL:
			dialogPrompt = "确定要放弃此次付款吗？";
			customBuilder = new CustomDialog.Builder(AaPaymentItemActivity.this);
			customBuilder
					.setTitle("提示")
					.setMessage(dialogPrompt)
					.setNegativeButton("取消",
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int which) {
									dialog.dismiss();
								}
							})
					.setPositiveButton("确定",
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int which) {
									MyApp app = (MyApp) getApplication();
									app.setRefreshPayFlag(false);
									dialog.dismiss();
									
									AaPaymentItemActivity.this.finish();

								}
							});
			return customBuilder.create();	
		default:
			break;
		}
		return super.onCreateDialog(id);
	}
	
	/**
	 * 输入密码进行支付
	 * @author zhangxue
	 * 
	 */
	private void payMoney() {
		AsyncTask<String, Integer, String> task = new AsyncTask<String, Integer, String>() {
			@Override
			protected void onPreExecute() {
				dialogPrompt = "正在进行支付，请稍候...";
				showDialog(DIALOG_INF_PAY3);
			}

			@Override
			protected String doInBackground(String... params) {

				List<NameValuePair> json_params = Util.getJsonHttpParams(AaPaymentItemActivity.this,
						"aaTransfer", "", "");
				json_params.add(new BasicNameValuePair("PRODUCTNO", params[0]));
				json_params.add(new BasicNameValuePair("TXNPASSWD", params[1]));
				json_params.add(new BasicNameValuePair("TRANSFERTYPE", params[2]));
				json_params.add(new BasicNameValuePair("SOURCEBANKCARDNO", params[3]));
				json_params.add(new BasicNameValuePair("SOURCEACCOUNTNAME", params[4]));
				json_params.add(new BasicNameValuePair("SOURCEIDTYPE", params[5]));
				json_params.add(new BasicNameValuePair("SOURCEID", params[6]));
				json_params.add(new BasicNameValuePair("DESTBANKCARDNO", params[7]));
				json_params.add(new BasicNameValuePair("DESTACCOUNTNAME", params[8]));
				json_params.add(new BasicNameValuePair("DESTIDTYPE", params[9]));
				json_params.add(new BasicNameValuePair("DESTID", params[10]));
				json_params.add(new BasicNameValuePair("DESTPRODUCTNO", params[11]));
				json_params.add(new BasicNameValuePair("TXNAMOUNT", params[12]));
				json_params.add(new BasicNameValuePair("PARTNERID", params[13]));
				json_params.add(new BasicNameValuePair("PARTNERORDERID", params[14]));
				json_params.add(new BasicNameValuePair("ORDERID", params[15]));
				json_params.add(new BasicNameValuePair("LOCATION", params[16]));
				json_params.add(new BasicNameValuePair("CUSTOMERNAME", params[17]));
				json_params.add(new BasicNameValuePair("DESTCUSTOMERNAME", params[18]));
				json_params.add(new BasicNameValuePair("TRANSREASON", params[19]));
				json_params.add(new BasicNameValuePair("TXNAMOUNTFLAG", params[20]));
				json_params.add(new BasicNameValuePair("PAYMENTORDERID", params[21]));
				String result = "";
				result = HttpSSLRequest.httpRequest(json_params, AaPaymentItemActivity.this);
				
				System.out.println("AA收款支付时web端的应答参数为======================" + result);
				return result;
			}

			@Override
			protected void onPostExecute(String result) {
				AaPaymentItemActivity.this.removeDialog(DIALOG_INF_PAY3);
				super.onPostExecute(result);
				if(result!=null){
					if (result.equals(COMMON.TIMEOUT)) {
						dialogPrompt = "连接超时!请检查网络连接！";
						showDialog(DIALOG_ERROR_PAY2);
					} else if (result.equals(COMMON.IOEXCEPTION)) {
						dialogPrompt = "I/O异常！";
						showDialog(DIALOG_ERROR_PAY2);
					} else if (result.equals(COMMON.EXCEPTION)) {
						dialogPrompt = "网络访问异常，请重试";
						showDialog(DIALOG_ERROR_PAY2);
					}else if(result.equals("")){
						dialogPrompt = "网络访问异常，请重试";
						showDialog(DIALOG_ERROR_PAY2);
					}else{
						try {
							JSONObject jsonObj = new JSONObject(result);
							String errorCode = jsonObj.getString("ERRORCODE");
							String errorMsg = jsonObj.getString("ERRORMSG");
							if (errorCode.equals("000000")) { //成功
								Intent intent = new Intent(BestpayAAStatic.PAYACTION);  
								intent.putExtra("NOTPAY", notpayNum-1+"");
								sendBroadcast(intent);
								queryBalanceAgain2();
								paymentorderid =serialNo;
								needpayflag=false;
								paySucflag= true;
								transStat =BestpayAAStatic.S0C;
								MyApp app = (MyApp) getApplication();
								app.setRefreshPayFlag(true);
								showDialog(PAY_SUCESSFUL2);
							}else if(errorCode.equals("200022")){//密码错误提示
								dialogPrompt = errorMsg+"("+errorCode+")";
								showDialog(DIALOG_ERRORPWD_PAY2);
							}else{
								dialogPrompt  = errorMsg+"("+errorCode+")";
								showDialog(DIALOG_ERROR_PAY2);
							}
						}catch (JSONException e) {
							Log.e("OrderBean", "Json to entity Error");
							e.printStackTrace();
						}
					}
				}else{
					dialogPrompt = "连接超时!请检查网络连接！";
					showDialog(DIALOG_ERROR_PAY2);
				}	
			}
		};
		psw = pi.getPassword();
		String transTypeZB = ""; //转账类型
		if(psw != null && !"".equals(psw)) {
			if(psw.length() == 6){
				PinkeyEncrypt pKeyEncrypt = new PinkeyEncrypt();
				String passwordCiphertText = pKeyEncrypt.encrypt(psw,
						"00000"+productNo);
				transTypeZB = "0";
				task.execute(productNo,passwordCiphertText,transTypeZB,"","","","","","","","", zhanghu,formatMoney(perMoney),partnerId,partnerOrderId,orderId,location,
						"","","","1|2",serialNo);
			}else{		
				this.prompt(AaPaymentItemActivity.this, "请输入六位数密码");
			}
		}else {			
			this.prompt(AaPaymentItemActivity.this, "请输入密码");
		}
	}
	
	
	private void recharge() { // 充值，走翼支付客户端的充值渠道
		Intent intent = new Intent(Intent.ACTION_MAIN);
		ComponentName componentName = new ComponentName(
				"com.chinatelecom.bestpayclient",
				"com.chinatelecom.bestpayclient.PaymentActivity");
		intent.setComponent(componentName);

		Bundle mBundle = new Bundle();
		mBundle.putInt("TYPE", 2);
		mBundle.putBoolean("isInFromPay", false);
		mBundle.putString("PRODUCTNO", productNo);
		intent.putExtras(mBundle);
		startActivityForResult(intent, COMMON.REQUESTCODE);
	}
	
	private void queryBalanceAgain2() { // 再次去查询余额，刷新当前界面

		AsyncTask<String, Integer, String> task = new AsyncTask<String, Integer, String>() {
			@Override
			protected void onPreExecute() {
				dialogPrompt = "正在刷新界面...";
				showDialog(DIALOG_INF_PAY3);
			}

			@Override
			protected String doInBackground(String... params) {
				String produceNo = params[0];
				String location = params[1];

				List<NameValuePair> json_params = Util.getJsonHttpParams(
						AaPaymentItemActivity.this, "queryAATransferBalance", "",
						"");
				json_params.add(new BasicNameValuePair("PRODUCTNO", produceNo));
				json_params.add(new BasicNameValuePair("LOCATION", location));
				String result = "";
				result = HttpSSLRequest.httpRequest(json_params,
						AaPaymentItemActivity.this);

				return result;
			}

			@Override
			protected void onPostExecute(String result) {
				AaPaymentItemActivity.this.removeDialog(DIALOG_INF_PAY3);
				super.onPostExecute(result);
				if (result != null) {
					// 出错时的处理 s
					if (result.equals(COMMON.TIMEOUT)) {
						dialogPrompt = "连接超时!请检查网络连接!";
						showDialog(DIALOG_ERROR_PAY2);
					} else if (result.equals(COMMON.IOEXCEPTION)) {
						dialogPrompt = "I/O异常!";
						showDialog(DIALOG_ERROR_PAY2);
					} else if (result.equals(COMMON.EXCEPTION)) {
						dialogPrompt = "其他异常!";
						showDialog(DIALOG_ERROR_PAY2);
					} else if (result.equals("")) {
						dialogPrompt = "连接超时!请检查网络连接!";
						showDialog(DIALOG_ERROR_PAY2);
					} else {
						try {
							JSONObject jsonObj = new JSONObject(result);
							String errorCode = jsonObj.getString("ERRORCODE");
							String errorMsg = jsonObj.getString("ERRORMSG");
							if (errorCode.equals("000000")) {// 成功
								balance = jsonObj.getString("BALANCE"); // 账户总余额
								
								cashbalance = jsonObj.getString("CASHBALANCE"); // 可提现余额		
								noncashbalance = jsonObj.getString("NONCASHBALANCE"); // 不可提现余额
								editor.putString(BestpayAAStatic.BALANCE, balance);
								editor.putString(BestpayAAStatic.CASHBALANCE, cashbalance);
								editor.putString(BestpayAAStatic.NONCASHBALANCE, noncashbalance);
								editor.commit();		
								if (balance != null
										&& !"".equals(balance.trim())) { // 可提现余额

									// 刷新当期界面
									removeDialog(DIALOG_ERROR_PAY2);
									initView();
								} else {
									dialogPrompt = "解析异常，接口数据错误";
									showDialog(DIALOG_ERROR_PAY2);
								}
							} else if (errorCode.equals("Z00004")) { // 未开户
								dialogPrompt = "您当前尚未开通翼支付账户，开户后即可付款";
								showDialog(DIALOG_ERROR_PAY2);
							} else if (errorCode.equals("Z00016")) {
								// 账户余额不足
//								dialogPrompt = "亲，您的账户余额不足";
//								showDialog(DIALOG_ERROR_PAY2);
								paylyt.setVisibility(View.GONE);
								rechargelyt.setVisibility(View.VISIBLE);
							} else {
								dialogPrompt = errorMsg + "(" + errorCode
										+ ")";
								showDialog(DIALOG_ERROR_PAY2);
							}
						} catch (JSONException e) {
							dialogPrompt = "解析异常，接口数据错误（JSON）";
							showDialog(DIALOG_ERROR_PAY2);
							e.printStackTrace();
						}
					}
				} else {
					dialogPrompt = "连接超时!请检查网络连接!";
					showDialog(DIALOG_ERROR_PAY2);
				}
			}
		};
		task.execute(productNo, location);
	}
	private String statusToDesc(String stat) {
		String statusDesc = "";
		if(!TextUtils.isEmpty(stat)){
			if(BestpayAAStatic.S0A.equals(stat)){
				statusDesc ="等待付款";
			}else if(BestpayAAStatic.S0C.equals(stat)){
				statusDesc ="已付款";
			}else if(BestpayAAStatic.S0X.equals(stat)){
				statusDesc ="交易关闭";
			}else{
				statusDesc="未知";
			}
		}else{
			statusDesc="未知";
		}
		return statusDesc;
	}
	
	private final int DIALOG_PAY_CANCEL = 13210;
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (needpayflag== true && keyCode == KeyEvent.KEYCODE_BACK) {
			showDialog(DIALOG_PAY_CANCEL);
			return false;
		}else if(!paySucflag){
			MyApp app= (MyApp)getApplication();
			app.setRefreshPayFlag(false);
		}
		return super.onKeyDown(keyCode, event);
	}
	
	/**
	 * 格式化你的转账金额
	 * @param transferMoney
	 * @return
	 */
	private String formatMoney(String transferMoney) { 
		String str = "";
		//转化成元为单位
		if(!TextUtils.isEmpty(transferMoney)) {//转账金额
			if(fundstype.equals("0")){
				//全额付款，优先可提现
				Double one = Double.parseDouble(COMMON.convert(transferMoney));
				Double two = Double.parseDouble(COMMON.convert(cashbalance)); // 可提现余额
				if(two <= 0) { //可提现余额=0
					str = "0|" + transferMoney;
				} else if(two >= one) { //可提现余额足以应付
					str =  transferMoney+"|0";
				} else if(two > 0 && two < one) {//可提现余额不够
					//可提现余额+差额 (不可提现补足)
					str = two + "|" + Util.yuan2fen(String.valueOf(one - two)) ;
				}
			}else if(fundstype.equals("1")){
				//可提现付款
				str=transferMoney+"|0";
			}
		}
		return str;
	}
	
	public String getContactNamebyPhoneNo(Context context, String phoneNum) {
		 String contactName = "";
		 ContentResolver resolver = context.getContentResolver();
		 Cursor cursor = resolver.query(
			 ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null,
			 ContactsContract.CommonDataKinds.Phone.NUMBER + "=?",
			 new String[] { phoneNum },null);
		 if (cursor.moveToFirst()) {
			 contactName = cursor.getString(cursor.getColumnIndex(
					 ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
		 }
		 cursor.close();
		 return contactName;
	}
	
}
