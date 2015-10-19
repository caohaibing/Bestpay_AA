package com.bestpay_aa.activity;

import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Base64;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

import com.bestpay_aa.bean.Person;
import com.bestpay_aa.dialog.CustomDialog;
import com.bestpay_aa.dialog.GatherSuccessDialog;
import com.bestpay_aa.net.HttpSSLRequest;
import com.bestpay_aa.util.COMMON;
import com.bestpay_aa.util.LogPrint;
import com.bestpay_aa.util.MyApp;
import com.bestpay_aa.util.Util;
import com.bestpay_aa.view.CustomProgressBarDialog_1;
import com.google.gson.Gson;

/**
 * 确认发起付款
 * @author zhouchaoxin
 *
 */
public class AAGatheringSureActivity extends BaseActivity {
	/**
	 * 主题
	 */
	private TextView theme;
	/**
	 * 主题时间(用于显示)
	 */
	private TextView themetime;
	/**
	 * 资金源(注意大小写)
	 */
	private TextView moneysource;
	/**
	 * 账单金额
	 */
	private TextView allmoney;
	/**
	 * 参与人数
	 */
	private TextView participantsNum;
	/**
	 * 人员名单
	 */
	private TextView personname;
	/**
	 * 人均金额
	 */
	
	private TextView personmoney;
	/**
	 * 备注
	 */
	private TextView remark;
	
	/**
	 * 确认发起付款
	 */
	private Button positiveButton;
	
	/**
	 * 放弃收款提示
	 */
	private final int DIALOG_PAY_CANCEL = 30;
	/**
	 * 等待对话框
	 */
	private final int DIALOG_PROGRESSBAR = 31;
	/**
	 * 请求服务器时，返回结果提示对话框
	 */
	private final int DIALOG_RESULT = 32;
	
	private final int DIALOG_GAHTER_SUCCESS = 33;
	
	private CustomDialog.Builder  customBuilder;
	private CustomProgressBarDialog_1.Builder progressBar; 
	
	private GatherSuccessDialog.Builder gatherDialog;
	/**
	 * 联系人员名单
	 */
	private ArrayList<Person> persions;
	/**
	 * 辅助处理人员名单
	 */
	StringBuffer sb = new StringBuffer();
	/**
	 * 对话框提示语
	 */
	private String dialogPrompt = "";
	/**
	 * 人均金额(向服务器传递)
	 */
	private String tempPersonmoney = "";
	
	MyApp app = null;
	/**
	 * 资金源
	 */
	private int moneySource;
	/**
	 * 主题时间(用于传值)
	 */
	private String timeTheme;
	/**
	 * 总额(向服务器传递)
	 */
	private String tempAllMoney;
	/**
	 * 标识发起成功确认页
	 */
	private String isSureAA;
	@Override
	public void onCreate(Bundle savedInstanceState) {
	
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.aa_gathering_sure);
		
		initView();
		
		intentGetExtras();
		app = (MyApp) getApplication();
	}
	
	/**
	 * 初始化布局VIEW
	 */
	private void initView(){
		theme = (TextView)findViewById(R.id.theme);
		themetime = (TextView)findViewById(R.id.themetime);
		moneysource = (TextView)findViewById(R.id.moneysource);
		allmoney = (TextView)findViewById(R.id.allmoney);
		participantsNum = (TextView)findViewById(R.id.participantsNum);
		personname = (TextView)findViewById(R.id.personname);
		personmoney = (TextView)findViewById(R.id.personmoney);
		remark = (TextView)findViewById(R.id.remark);
		positiveButton = (Button)findViewById(R.id.positiveButton);
		positiveButton.setOnClickListener(onClick);
	}
	
	/**
	 * 获取Activity传值
	 */
	private void intentGetExtras(){
		
		Bundle extras = getIntent().getExtras();
		if (extras != null) {
			
			theme.setText(extras.getString("theme"));
			
			//timeTheme = extras.getString("themetime");
			//String time = timeTheme.substring(5, timeTheme.length());
			//themetime.setText(time);
			//moneysource.setText(extras.getString("moneySource"));
			participantsNum.setText(extras.getString("participantsNum"));
			
			tempAllMoney =  Util.yuan2fen(extras.getString("allmoney"));
			allmoney.setText(extras.getString("allmoney")+" 元");
			
			if (!extras.getString("remark").equals("") && extras.getString("remark") != null) {
				
				remark.setText(extras.getString("remark"));
			}
			
			/*if (extras.getString("moneySource").equals("全额资金")) {
				
				moneySource = 0;
				
			}else{
				
				moneySource = 1;
			}*/
			tempPersonmoney =  Util.yuan2fen(extras.getString("personMoney"));
			personmoney.setText(extras.getString("personMoney")+" 元");
			
			
			persions = (ArrayList<Person>)extras.getSerializable("persions");
			for (Person persion : persions) {
				sb.append(persion.getName()+"，");
			}
			sb.replace(sb.lastIndexOf("，"), sb.length(), "");
			personname.setText(sb.toString());
		}
		
	}
	
	
	
	/**
	 * 处理事件
	 */
	private OnClickListener onClick = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			//确认发起付款
			case R.id.positiveButton:
				LaunchAAPayment();
				break;

			default:
				break;
			}
			
		}
	};
	@Override
	protected Dialog onCreateDialog(int id) {
		switch (id) {
		case DIALOG_PAY_CANCEL:
			dialogPrompt = "确定要放弃此次收款吗？";
			customBuilder = new CustomDialog.Builder(AAGatheringSureActivity.this);
			customBuilder
					.setTitle(getResources().getString(R.string.mw_dialog_title))
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

									dialog.dismiss();
									
									AAGatheringSureActivity.this.finish();

								}
							});
			return customBuilder.create();
			
		case DIALOG_RESULT:
			
			customBuilder = new CustomDialog.Builder(AAGatheringSureActivity.this);
			customBuilder
					.setTitle(getResources().getString(R.string.mw_dialog_title))
					.setMessage(dialogPrompt)
					.setPositiveButton("确定",
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int which) {

									dialog.dismiss();
									
									AAGatheringSureActivity.this.finish();

								}
							});
			return customBuilder.create();
			
			
		case DIALOG_PROGRESSBAR:
			progressBar = new CustomProgressBarDialog_1.Builder(
					AAGatheringSureActivity.this, false);
			progressBar.setTitle(getResources().getString(R.string.mw_dialog_title)).setMessage(dialogPrompt).setCancel(true);
			
			return progressBar.create();
			
		case DIALOG_GAHTER_SUCCESS:
			
			gatherDialog = new GatherSuccessDialog.Builder(AAGatheringSureActivity.this,mHandler);
			return gatherDialog.create();

		default:
			break;
		}
		return super.onCreateDialog(id);
		
		
	
	}
	
	
	/**
	 * AA收款发起收款接口
	 */
	private void LaunchAAPayment() {

		AsyncTask<String, Integer, String> task = new AsyncTask<String, Integer, String>() {
			@Override
			protected void onPreExecute() {
				dialogPrompt = "请稍候......";
				showDialog(DIALOG_PROGRESSBAR);
			}

			@Override
			protected String doInBackground(String... params) {
				String produceNo = params[0];
		
				List<NameValuePair> json_params = Util.getJsonHttpParams(
						AAGatheringSureActivity.this, "launchAAPayment", "","");
				
				json_params.add(new BasicNameValuePair("PRODUCTNO", produceNo));
				json_params.add(new BasicNameValuePair("THEME", theme.getText().toString()));
				//json_params.add(new BasicNameValuePair("THEMETIME", replaceDate(timeTheme)));
				json_params.add(new BasicNameValuePair("MONEY",tempPersonmoney));
				//新增，总额字段
				json_params.add(new BasicNameValuePair("TOTALAMT",tempAllMoney));
				
				json_params.add(new BasicNameValuePair("MARK", remark.getText().toString()));
				json_params.add(new BasicNameValuePair("PAYINFO", objectTojson(persions)));
				//json_params.add(new BasicNameValuePair("FUNDSTYPE", String.valueOf(moneySource)));
				json_params.add(new BasicNameValuePair("FUNDSTYPE", String.valueOf(1)));

				
				String result = "";
				result = HttpSSLRequest.httpRequest(json_params,
						AAGatheringSureActivity.this);

				return result;
			}

			@Override
			protected void onPostExecute(String result) {
				removeDialog(DIALOG_PROGRESSBAR);
				super.onPostExecute(result);
				if (result != null) {
					try {
						JSONObject  jsonObj = new JSONObject(result);
						String errorCode = jsonObj.getString("ERRORCODE");
						String errorMsg = jsonObj.getString("ERRORMSG");
						if(errorCode.equals("000000")){
							//直接提示发起付款成功
							//mHandler.sendEmptyMessage(10);
							//dialogPrompt = errorMsg;
							//showDialog(DIALOG_RESULT);
							showDialog(DIALOG_GAHTER_SUCCESS);
							
						}
						else if (errorCode.equals(COMMON.TIMEOUT)) {
							dialogPrompt = errorMsg;
							showDialog(DIALOG_RESULT);
						} else if (errorCode.equals(COMMON.IOEXCEPTION)) {
							dialogPrompt = errorMsg;
							showDialog(DIALOG_RESULT);
							
						} else if (errorCode.equals(COMMON.EXCEPTION)) {
							dialogPrompt = errorMsg;
							showDialog(DIALOG_RESULT);
						}else if(errorCode.equals("019999")){
							dialogPrompt = errorMsg;
							showDialog(DIALOG_RESULT);
						}
					} catch (JSONException e) {
						
						e.printStackTrace();
					}
					
					
				} else {
					
					dialogPrompt = "返回数据为空";
					showDialog(DIALOG_RESULT);
				}
			}
		};
		
		task.execute(app.getProd());
	}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			
			showDialog(DIALOG_PAY_CANCEL);
			return false;
		}
		return super.onKeyDown(keyCode, event);
	}
	
	/**
	 * 人员名单封装json对象,并进行bese64加密
	 * @param arr
	 * @return
	 */
	private String objectTojson(ArrayList<Person> arr) {
		try {
			Gson gson = new Gson();
			String str = gson.toJson(arr);

			LogPrint.Print("lock", str);
			return BASE64Encode(str);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "";

	}
	/**
	 * 处理主题时间
	 * @param date
	 * @return
	 */
	private String replaceDate(String date) {

		try {
			SimpleDateFormat simple = new SimpleDateFormat("yyyy-mm-dd");
			java.util.Date ctime = simple.parse(date);
			String time = simple.format(ctime);
			if (time.contains("-")) {

				String str = time.replace("-", "");

				return str;
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		return "";
	}
	/**
	 * Bese64加密
	 * @param origin
	 * @return
	 */
	public static String BASE64Encode(String origin){
		byte[] bytes = null;
		String str = "";
		try {
			if(origin != null){
				bytes = origin.getBytes("utf-8");
				str = Base64.encodeToString(bytes, Base64.DEFAULT);
			}			
		} catch (UnsupportedEncodingException e) {
			
			e.printStackTrace();
		}		
		return str;
	}
	/**
	 * 处理界面UI
	 */
	private Handler mHandler = new Handler(){
		@Override
		public void handleMessage(Message msg) {
			Intent intent = new Intent();
			Bundle bundle = new Bundle();
			switch (msg.what) {
			case 10:
				//MyApp app = (MyApp) getApplication();
				isSureAA = "sureAA";
				app.setPage(isSureAA);
				AAGatheringSureActivity.this.finish();
				intent.setClass(AAGatheringSureActivity.this, MainActivity.class);
				bundle.putString("PRODUCTNO", app.getProd());
				bundle.putString("LOCATION", app.getLocat());
				bundle.putString("WHICHTAB", "0");
				startActivity(intent);
				break;

			default:
				break;
			}
		}
		
	};
}
