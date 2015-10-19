package com.bestpay_aa.activity;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.bestpay_aa.bean.AaPaymentBean;
import com.bestpay_aa.net.HttpSSLRequest;
import com.bestpay_aa.util.BestpayAAStatic;
import com.bestpay_aa.util.COMMON;
import com.bestpay_aa.util.MyApp;
import com.bestpay_aa.util.Util;
import com.bestpay_aa.view.CustomDialog2;
import com.bestpay_aa.view.CustomProgressBarDialog_1;
import com.bestpay_aa.view.CustomToast;

import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView.OnItemClickListener;

/**
 * AA付款
 * 
 * @author zhangxue
 * 
 */
public class AAPaymentActivity extends BaseActivity implements
		OnItemClickListener, OnScrollListener {
	public final int RESET_REQUEST_CODE = 140310;
	public final static int NOT_MORE_RECORD = 14030701;
	private final int TRANSACTION_COUNT = 10;
	private final int START_PAGE = 0;
	private int start_record = START_PAGE;// 前面跳过的记录数
	int i = 0;
	int notpayNum=0;
	boolean isHaveMore = true;
	boolean isScrollFlag = true;
	ArrayList<AaPaymentBean> transferRecordList = new ArrayList<AaPaymentBean>();// 总记录
	ArrayList<AaPaymentBean> transferRecordList2 = new ArrayList<AaPaymentBean>();// 每页的记录
	ListView listview;
	TransferRecordAdapter transferRecordAdapter;
	LinearLayout loadind;
	LinearLayout not_more;
//	LinearLayout have_more;
	LinearLayout emptyData;
	LinearLayout list_lyt;

	String productNo = "";
	String location = "";
	String theme = "";
	String perMoney ="";//需支付金额
	String targetProductNo ="";//转入账户号 
	String remark = "";
	String serialNo = "";//AA收款发起流水号
	String transStat = "";
	String transDate = "";
	String failureTime = "";
	String fundstype = "";
	ScrollView sv ;
	private String  balance=""; //账户总金额
	private String cashbalance = ""; //可提现余额
	private String noncashbalance = ""; //可提现余额
	private SharedPreferences pref;
	SharedPreferences.Editor  editor;
	String paymentorderid ="";
	String nowDate;
	String prompt;
	Bundle b ;
	private Boolean  needpayflag=false; //是否需要支付
	String isAccountOpen = "";
	private String partnerId = ""; // 业管平台商户编码
	private String partnerOrderId = "";// 农村金融业务订单号
	private String orderId = "";// 业务管理平台订单号
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		pref = PreferenceManager.getDefaultSharedPreferences(this);
		editor = pref.edit();
		balance = pref.getString(BestpayAAStatic.BALANCE, "0");
		cashbalance = pref.getString(BestpayAAStatic.CASHBALANCE, "0");
		noncashbalance = pref.getString(BestpayAAStatic.NONCASHBALANCE, "0");
		Intent intent = getIntent();
		b = intent.getExtras();
		if (b != null) {
			productNo = b.getString("PRODUCTNO");
			location = b.getString("LOCATION");
			String tt = b.getString("TRANSABILITYFLAG");
			String sq = b.getString("NOTICE");
			if (!TextUtils.isEmpty(sq)) {
				dialogPrompt = sq;
				showDialog(DIALOG_ERROR2);
			}
			if (!TextUtils.isEmpty(tt) && tt.equals("0")) {
				dialogPrompt = "您当前为非实名认证用户，为了保障您资金的安全，请您进行实名认证后再进行转账";
				showDialog(DIALOG_ERROR2);
			}
		}
		setContentView(R.layout.aa_gathering_pay);
		listview = (ListView) findViewById(R.id.listview);
		loadind = (LinearLayout) findViewById(R.id.loadind);
		not_more = (LinearLayout) findViewById(R.id.not_more);
//		have_more = (LinearLayout) findViewById(R.id.have_more);
		emptyData = (LinearLayout) findViewById(R.id.emptyData);
		list_lyt = (LinearLayout) findViewById(R.id.list_lyt);
		listview.setOnItemClickListener(this);
		listview.setOnScrollListener(this);
		transferRecordAdapter =new TransferRecordAdapter();
		listview.setAdapter(transferRecordAdapter);
//		have_more.setOnClickListener(new OnClickListener() {
//			@Override
//			public void onClick(View v) {
//				 getData();
//			}
//		});
		IntentFilter filter = new IntentFilter(Intent.ACTION_MAIN);  
	    registerReceiver(broadcastReceiver, filter);
	}
	@Override
	protected void onResume() {
		super.onResume();
		MyApp app = (MyApp) getApplication();
		if(app.isRefreshPayFlag()){
			emptyData.setVisibility(View.GONE);
			transferRecordList.clear();
			transferRecordAdapter.reData();
			start_record = 0;
			isHaveMore= true;
			getData();
		}
		app.setRefreshPayFlag(true);
	}

	BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {  
		@Override  
		public void onReceive(Context context, Intent intent) { 
			String action =intent.getAction();
			if(action.equals(Intent.ACTION_MAIN)){
				Boolean flag = intent.getExtras().getBoolean("QUERYACCOUNT");
				if(flag !=null && flag == true){
					balance=intent.getExtras().getString("BALANCE");  
					cashbalance=intent.getExtras().getString("CASHACCOUNTBALANCE");  
					noncashbalance=intent.getExtras().getString("NONCASHBALANCE");  
					editor.putString(BestpayAAStatic.BALANCE, balance);
					editor.putString(BestpayAAStatic.CASHBALANCE, cashbalance);
					editor.putString(BestpayAAStatic.NONCASHBALANCE, noncashbalance);
					editor.commit();
				}
			}
		 }  
	}; 
	
	/**
	 * 获取付款列表
	 * @return 付款列表
	 * @author zhangxue
	 */
	private void getData() {

		AsyncTask<String, Integer, String> task = new AsyncTask<String, Integer, String>() {
			@Override
			protected void onPreExecute() {
//				have_more.setVisibility(View.GONE);
				loadind.setVisibility(View.VISIBLE);
			}

			@Override
			protected String doInBackground(String... params) {

				List<NameValuePair> json_params = Util.getJsonHttpParams(
						AAPaymentActivity.this, "queryAAPaymentOrder", "",
						"");
				json_params.add(new BasicNameValuePair("PRODUCTNO", params[0]));
				json_params.add(new BasicNameValuePair("FROMDATE", params[1]));// 开始日期
				json_params.add(new BasicNameValuePair("TODATE", params[2]));// 截至日期
				json_params.add(new BasicNameValuePair("MAXRECORD", params[3]));// 最大返回记录数
				json_params
						.add(new BasicNameValuePair("STARTRECORD", params[4]));// 前面跳过的记录数
				String result = "";
				result = HttpSSLRequest.httpRequest(json_params,
						AAPaymentActivity.this);
				System.out.println("交易记录的应答参数为===============" + result);
				return result;
			}

			@Override
			protected void onPostExecute(String result) {
				super.onPostExecute(result);
				if (result != null && !"".equals(result)) {
					if (result.equals(COMMON.TIMEOUT)) {
						prompt = "连接超时!请检查网络连接！";
						CustomToast toast = new CustomToast(
								AAPaymentActivity.this, prompt);
						toast.show();
						isScrollFlag = true;
					} else if (result.equals(COMMON.IOEXCEPTION)) {
						prompt = "I/O异常！";
						CustomToast toast = new CustomToast(
								AAPaymentActivity.this, prompt);
						toast.show();
						isScrollFlag = true;
					} else if (result.equals(COMMON.EXCEPTION)) {
						prompt = "其它异常！";
						CustomToast toast = new CustomToast(
								AAPaymentActivity.this, prompt);
						toast.show();
						isScrollFlag = true;
					} else if (result.equals("")) {
						prompt = "其它异常！";
						CustomToast toast = new CustomToast(
								AAPaymentActivity.this, prompt);
						toast.show();
						isScrollFlag = true;
					} else {
						try {
							loadind.setVisibility(View.GONE);
							JSONObject jsonObj = new JSONObject(result);
							String errorCode = jsonObj.getString("ERRORCODE");
							String errorMsg = jsonObj.getString("ERRORMSG");
							if (errorCode.equals("000000")) {
								start_record +=  TRANSACTION_COUNT;
								JSONArray jsonArray = jsonObj
										.getJSONArray("PAYMENTORDERLIST");
								transferRecordList2 = new ArrayList<AaPaymentBean>();// 每页的记录
								
								if (jsonArray.length() < TRANSACTION_COUNT) {
									isHaveMore = false;
//									have_more.setVisibility(View.GONE);
									if(jsonArray.length() != 0){
										CustomToast toast = new CustomToast(
												AAPaymentActivity.this, "^_^就这么多啦~");
										toast.show(3000);
									}
								}
								
								transferRecordList2 = AaPaymentBean
										.getTransBeans(jsonArray);
								transferRecordList.addAll(transferRecordList2);
								notpayNum= 0;
								for (int i = 0; i < transferRecordList.size(); i++) {
									if(transferRecordList.get(i).getTransStat().equals("S0A")){
										Date failtime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(transferRecordList.get(i).getFailuretime());
										if((failtime.getTime() > new Date().getTime())){
											notpayNum += 1;
										}
									}
								}
								loadind.setVisibility(View.GONE);
//								if (isHaveMore)
//									have_more.setVisibility(View.VISIBLE);
								if(transferRecordList == null || transferRecordList.size() == 0){
									emptyData.setVisibility(View.VISIBLE);
									list_lyt.setVisibility(View.GONE);
								}else{
									list_lyt.setVisibility(View.VISIBLE);
									emptyData.setVisibility(View.GONE);
								}
								transferRecordAdapter.reData();
								isScrollFlag = true;
								Intent intent = new Intent(BestpayAAStatic.PAYACTION);  
								intent.putExtra("NOTPAY", notpayNum+"");
								sendBroadcast(intent);
							} else {
								loadind.setVisibility(View.GONE);
//								if (isHaveMore)
//									have_more.setVisibility(View.VISIBLE);
								prompt = errorMsg + "(" + errorCode + ")";
								CustomToast toast = new CustomToast(
										AAPaymentActivity.this, prompt);
								toast.show();
								isScrollFlag = true;
							}
						} catch (JSONException e) {
							loadind.setVisibility(View.GONE);
//							if (isHaveMore)
//								have_more.setVisibility(View.VISIBLE);
							prompt = "数据格式错误（JSON）";
							CustomToast toast = new CustomToast(
									AAPaymentActivity.this, prompt);
							toast.show();
							isScrollFlag = true;
						} catch (ParseException e) {
							e.printStackTrace();
							dialogPrompt = "时间解析错误，请返回重试";
							showDialog(DIALOG_ERROR2);
						}
					}
				} else {
					loadind.setVisibility(View.GONE);
//					if (isHaveMore)
//						have_more.setVisibility(View.VISIBLE);
					prompt = "连接超时!请检查网络连接！";
					CustomToast toast = new CustomToast(AAPaymentActivity.this,
							prompt);
					toast.show();
					isScrollFlag = true;
				}
			}
		};
		if (isHaveMore) {
			isScrollFlag = false;
			task.execute(productNo, "", "",
					TRANSACTION_COUNT + "", start_record+"");
		}
	}

	class TransferRecordAdapter extends BaseAdapter {
		ViewHolder holder;

		public void reData() {
			notifyDataSetChanged();
		}

		@Override
		public int getCount() {
			return transferRecordList.size();
		}

		@Override
		public Object getItem(int position) {
			return transferRecordList.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			LayoutInflater mInflater = LayoutInflater
					.from(AAPaymentActivity.this);
			if (convertView == null) {
				holder = new ViewHolder();
				convertView = mInflater.inflate(R.layout.aa_gathering_pay_item,
						null);
				convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}

			holder.themeTv = (TextView) convertView.findViewById(R.id.theme);
			holder.image_state = (ImageView) convertView.findViewById(R.id.image_state);
			holder.perMoneyTv = (TextView) convertView
					.findViewById(R.id.perMoney);
			holder.transDateTv = (TextView) convertView
					.findViewById(R.id.transDate);
			if(transferRecordList.get(position).getTheme().length()>5){
				holder.themeTv.setText(transferRecordList.get(position).getTheme().substring(0, 5)+"...");
			}else{
				holder.themeTv.setText(transferRecordList.get(position).getTheme());
			}
			holder.perMoneyTv.setTextColor(Color.parseColor("#f74c00"));
			holder.perMoneyTv.setText(COMMON.convert(transferRecordList.get(position).getPerMoney()) + "元");
			try {
				holder.transDateTv.setText(new SimpleDateFormat("MM-dd")
					.format(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
						.parse(transferRecordList.get(position)
						.getCreatetime())));
			} catch (Exception e) {
				e.printStackTrace();
				holder.transDateTv.setText("");
			}
			if(!TextUtils.isEmpty(transferRecordList.get(position).getTransStat())){
				if(BestpayAAStatic.S0A.equals(transferRecordList.get(position).getTransStat())){
					try {
						Date failtime=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(transferRecordList.get(position).getFailuretime());
						Date nowtime=new Date();
						if((failtime.getTime() - nowtime.getTime())<24*60*60*1000){
							holder.image_state.setVisibility(View.VISIBLE);
							holder.image_state.setImageResource(R.drawable.kuaiguoqi);
						}else{
							holder.image_state.setVisibility(View.GONE);
						}
					} catch (ParseException e) {
						e.printStackTrace();
						holder.image_state.setVisibility(View.GONE);
					}
				}else if("S0C".equals(transferRecordList.get(position).getTransStat())){
					holder.image_state.setVisibility(View.VISIBLE);
					holder.image_state.setImageResource(R.drawable.havepaid);
				}else{
					holder.image_state.setVisibility(View.VISIBLE);
					holder.image_state.setImageResource(R.drawable.faild);
				}
			}
			return convertView;
		}

	}

	class ViewHolder {
		TextView themeTv;
		TextView perMoneyTv;
		TextView transDateTv;
		ImageView image_state;
	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, final int position,
			long arg3) {
		// 跳转到详情操作
		theme = transferRecordList.get(position).getTheme();
		perMoney = transferRecordList.get(position).getPerMoney();
		remark = transferRecordList.get(position).getRemark();
		serialNo = transferRecordList.get(position).getSerialNo();
		transStat = transferRecordList.get(position).getTransStat();
		transDate = transferRecordList.get(position).getCreatetime();
		failureTime = transferRecordList.get(position).getFailuretime();
		targetProductNo = transferRecordList.get(position).getTargetProductNo();
		fundstype = transferRecordList.get(position).getFundstype();
		if(!TextUtils.isEmpty(fundstype) 
					&& !TextUtils.isEmpty(targetProductNo)
					&& !TextUtils.isEmpty(perMoney)){
			if(BestpayAAStatic.S0A.equals(transStat)){
				needpayflag =true;
				transfer(targetProductNo, perMoney);
			}else{	
				needpayflag =false;
				Intent intent = new Intent(AAPaymentActivity.this,
						AaPaymentItemActivity.class);
				Bundle b = new Bundle();
				b.putString("PRODUCTNO", productNo);
				b.putString("LOCATION", location);
				b.putString("THEME", theme);
				b.putString("PERMONEY", perMoney);
				b.putString("REMARK", remark);
				b.putString("SERIALNO", serialNo);
				b.putString("TRANSSTAT",transStat);
				b.putString("TRANSDATE", transDate);
				b.putString("FAILURETIME", failureTime);
				b.putString("FUNDSTYPE",fundstype); 
				b.putString("Shouji", targetProductNo);// 转入账户
				b.putInt("NOTPAYNUM", notpayNum);
				b.putBoolean("NEEDPAYFLAG", needpayflag);
				intent.putExtras(b);
				AAPaymentActivity.this.startActivity(intent);
			}
			
		}
	}

	// 计算listView所有Item的高的方法
	private void setListViewHeightBasedOnChildren(ListView listView) {
		ListAdapter listAdapter = listView.getAdapter();
		if (listAdapter == null) {
			return;
		}
		int totalHeight = 0;
		for (int i = 0; i < listAdapter.getCount(); i++) {
			View listItem = listAdapter.getView(i, null, listView);
			listItem.measure(0, 0);
			totalHeight += listItem.getMeasuredHeight();
		}

		ViewGroup.LayoutParams params = listView.getLayoutParams();
		params.height = totalHeight
				+ (listView.getDividerHeight() * (listAdapter.getCount() - 1));
		listView.setLayoutParams(params);
	}

	private String statusToDesc(String stat) {
		String statusDesc = "";
		if(!TextUtils.isEmpty(stat)){
			if("S0A".equals(stat)){
				statusDesc ="等待付款";
			}else if("S0C".equals(stat)){
				statusDesc ="已付款";
			}else if("S0X".equals(stat)){
				statusDesc ="交易关闭";
			}else{
				statusDesc="未知";
			}
		}else{
			statusDesc="未知";
		}
		return statusDesc;
	}

	private int lastVisibleIndex = 0;

	@Override
	public void onScroll(AbsListView view, int firstVisibleItem,
			int visibleItemCount, int totalItemCount) {
		lastVisibleIndex = firstVisibleItem + visibleItemCount - 1;
	}

	@Override
	public void onScrollStateChanged(AbsListView view, int scrollState) {
		if (scrollState == OnScrollListener.SCROLL_STATE_IDLE
				&& lastVisibleIndex == transferRecordList.size() - 1
				&& isScrollFlag) {
			 getData();
		}
	}

	private String dialogPrompt = ""; // 对话框关联提示语
	private static final int DIALOG_ERROR2 = 1415; // 错误提示框
	private static final int DIALOG_INF_PAY3 = 105;
	@Override
	protected Dialog onCreateDialog(int id) {
		switch (id) {
		case DIALOG_ERROR2:
			CustomDialog2.Builder customBuilder = new CustomDialog2.Builder(
					AAPaymentActivity.this);
			customBuilder
					.setTitle(dialogPrompt)
					.setMessage("")
					.setPositiveButton("确定",
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int which) {
									AAPaymentActivity.this
											.removeDialog(DIALOG_ERROR2);
								}
							});
			return customBuilder.create();
		case DIALOG_INF_PAY3:
			CustomProgressBarDialog_1.Builder note = new CustomProgressBarDialog_1.Builder(
					AAPaymentActivity.this, false);
			note = new CustomProgressBarDialog_1.Builder(
					AAPaymentActivity.this, false);
			note.setTitle("请稍候").setMessage(dialogPrompt).setCancel(true);
			return note.create();
		}
		return null;
	}
	@Override
	protected void onDestroy() {
		super.onDestroy();
		unregisterReceiver(broadcastReceiver);
	}
	/**
	 * 付款预判
	 * @author zhangxue
	 * @param target
	 * @param jine
	 */
	private void transfer(final String target, final String jine) {

		AsyncTask<String, Integer, String> task = new AsyncTask<String, Integer, String>() {
			@Override
			protected void onPreExecute() {
				dialogPrompt = "请稍候...";
				showDialog(DIALOG_INF_PAY3);
			}

			@Override
			protected String doInBackground(String... params) {
				List<NameValuePair> json_params = Util.getJsonHttpParams(
						AAPaymentActivity.this, "aaPredictTransfer", "", "");
				json_params.add(new BasicNameValuePair("PRODUCTNO", params[0]));
				json_params.add(new BasicNameValuePair("TRANSFERTYPE",
						params[1]));
				json_params.add(new BasicNameValuePair("SOURCEBANKCARDNO",
						params[2]));
				json_params.add(new BasicNameValuePair("SOURCEACCOUNTNAME",
						params[3]));
				json_params.add(new BasicNameValuePair("SOURCEIDTYPE",
						params[4]));
				json_params.add(new BasicNameValuePair("SOURCEID", params[5]));
				json_params.add(new BasicNameValuePair("DESTBANKCARDNO",
						params[6]));
				json_params.add(new BasicNameValuePair("DESTACCOUNTNAME",
						params[7]));
				json_params
						.add(new BasicNameValuePair("DESTIDTYPE", params[8]));
				json_params.add(new BasicNameValuePair("DESTID", params[9]));
				json_params.add(new BasicNameValuePair("DESTPRODUCTNO",
						params[10])); // 转入方账号
				json_params
						.add(new BasicNameValuePair("TXNAMOUNT", params[11])); // 交易金额
				json_params
						.add(new BasicNameValuePair("PARTNERID", params[12]));
				json_params.add(new BasicNameValuePair("LOCATION", params[13])); // 归属地信息
				json_params.add(new BasicNameValuePair("CUSTOMERNAME",
						params[14])); // 转出方翼支付账户用户名
				json_params
						.add(new BasicNameValuePair("TXNPASSWD", params[15])); // 支付密码
				json_params.add(new BasicNameValuePair("DESTCUSTOMERNAME",
						params[16])); // 转入方翼支付账户用户名
				json_params.add(new BasicNameValuePair("TRANSREASON",
						params[17])); // 转账理由
				json_params.add(new BasicNameValuePair("PAYMENTORDERID",
						params[18])); // 
				json_params.add(new BasicNameValuePair("TXNAMOUNTFLAG",
						params[19])); // 交易金额描述标志
				String result = "";
				result = HttpSSLRequest.httpRequest(json_params,
						AAPaymentActivity.this);

				System.out.println("AA付款调用转账预判接口web端的应答参数为==============="
						+ result);
				return result;
			}

			@Override
			protected void onPostExecute(String result) {
				AAPaymentActivity.this.removeDialog(DIALOG_INF_PAY3);
				super.onPostExecute(result);
				if (result != null) {
					// 出错时的处理
					if (result.equals(COMMON.TIMEOUT)) {
						dialogPrompt = "连接超时!请检查网络连接！";
						showDialog(DIALOG_ERROR2);
					} else if (result.equals(COMMON.IOEXCEPTION)) {
						dialogPrompt = "I/O异常！";
						showDialog(DIALOG_ERROR2);
					} else if (result.equals(COMMON.EXCEPTION)) {
						dialogPrompt = "其它异常！";
						showDialog(DIALOG_ERROR2);
					} else if (result.equals("")) {
						dialogPrompt = "连接超时!请检查网络连接！";
						showDialog(DIALOG_ERROR2);
					} else {
						try {
							JSONObject jsonObj = new JSONObject(result);
							String errorCode = jsonObj.getString("ERRORCODE");
							String errorMsg = jsonObj.getString("ERRORMSG");

							if (errorCode.equals("000000")) { // 成功
								isAccountOpen = jsonObj.getString("ISACCOUNT"); // 转入方是否开户，0为未开户，1为开户
								if("0".equals(isAccountOpen)){
									dialogPrompt = "收款方未开户！";
									showDialog(DIALOG_ERROR2);
								}else{
									partnerId = jsonObj.getString("PARTNERID"); // 业管平台商户编码
									partnerOrderId = jsonObj.getString("PARTNERORDERID"); // 农村金融业务订单号
									orderId = jsonObj.getString("ORDERID"); // 业务管理平台订单号
									Intent intent = new Intent(AAPaymentActivity.this,
											AaPaymentItemActivity.class);
									Bundle b = new Bundle();
									b.putString("PRODUCTNO", productNo);
									b.putString("LOCATION", location);
									b.putString("THEME", theme);
									b.putString("PERMONEY", perMoney);
									b.putString("REMARK", remark);
									b.putString("SERIALNO", serialNo);
									b.putString("TRANSSTAT",transStat);
									b.putString("TRANSDATE", transDate);
									b.putString("FAILURETIME", failureTime);
									b.putString("FUNDSTYPE",fundstype); 
									b.putString("Shouji", targetProductNo);// 转入账户
									b.putInt("NOTPAYNUM", notpayNum);
									b.putBoolean("NEEDPAYFLAG", needpayflag);
									b.putString("PARTNERID", partnerId);
									b.putString("PARTNERORDERID", partnerOrderId);
									b.putString("ORDERID", orderId);
									intent.putExtras(b);
									AAPaymentActivity.this.startActivity(intent);
								}
									
							} else if (errorCode.equals("Z00026")) { // 转入方没开通翼支付
								// 转入方开通，方可发起收款请求
							} else if (errorCode.equals("200020")||errorCode.equals("200133")) { // 账户余额不足
								needpayflag =true;
								Intent intent = new Intent(AAPaymentActivity.this,
										AaPaymentItemActivity.class);
								Bundle b = new Bundle();
								b.putString("PRODUCTNO", productNo);
								b.putString("LOCATION", location);
								b.putString("THEME", theme);
								b.putString("PERMONEY", perMoney);
								b.putString("REMARK", remark);
								b.putString("SERIALNO", serialNo);
								b.putString("TRANSSTAT",transStat);
								b.putString("TRANSDATE", transDate);
								b.putString("FAILURETIME", failureTime);
								b.putString("FUNDSTYPE",fundstype); 
								b.putString("Shouji", targetProductNo);// 转入账户
								b.putInt("NOTPAYNUM", notpayNum);
								b.putBoolean("NEEDPAYFLAG", needpayflag);
								intent.putExtras(b);
								AAPaymentActivity.this.startActivity(intent);
							}else if (errorCode.equals("Z00018")) { // 转出方未开通实名认证
								dialogPrompt = "您当前为非实名认证用户，为了保障您资金的安全，请您进行实名认证后再进行转账";
								showDialog(DIALOG_ERROR2);
							} else if (errorCode.equals("Z00015")) {// 交易超过当日限额
								showNoticeDialog(R.string.text5,R.string.text51);	
							} else if (errorCode.equals("Z00032")) {
								showNoticeDialog(R.string.text7,R.string.text71);
							} else if (errorCode.equals("Z00033")) {
								showNoticeDialog(R.string.text8,R.string.text81);		
							} else if (!errorMsg.equals("成功")) {
								dialogPrompt = errorMsg + "(" + errorCode
										+ ")";
								showDialog(DIALOG_ERROR2);
							} else{
								dialogPrompt = "未知";
								showDialog(DIALOG_ERROR2);
							}
						} catch (JSONException e) {
							Log.e("OrderBean", "Json to entity Error");
							e.printStackTrace();
						}
					}
				} else {

				}
			}
		};

		task.execute(productNo, "8", "", "", "", "", "", "", "", "", target,
				perMoney+"|0", HttpSSLRequest.PARTNERID, location, "", "",
				"", "",serialNo, "1|2");// 生产环境000016900000测试环境test000009200000
	}
	private void showNoticeDialog(int i1, int i2) {
		Resources res = getResources();
		final CustomDialog2 dialog = new CustomDialog2.Builder(
				AAPaymentActivity.this)
				.setTitle(res.getString(i1))
				.setMessage(res.getString(i2))
				.setCancel(true)
				.setPositiveButton("确定", new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
					}
				}).create();
		dialog.show();
		dialog.setCancelable(false);
	}
}
