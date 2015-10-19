package com.bestpay_aa.activity;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.bestpay_aa.adapter.AAGatheringPersonAdapter;
import com.bestpay_aa.adapter.AAGatheringRecordAdapter;
import com.bestpay_aa.bean.AaGatheringPayBean;
import com.bestpay_aa.bean.AaPaymentBean;
import com.bestpay_aa.net.HttpRequest;
import com.bestpay_aa.net.HttpSSLRequest;
import com.bestpay_aa.util.ApplicationVar;
import com.bestpay_aa.util.BestpayAAStatic;
import com.bestpay_aa.util.COMMON;
import com.bestpay_aa.util.LogPrint;
import com.bestpay_aa.util.MyApp;
import com.bestpay_aa.util.Util;
import com.bestpay_aa.view.CustomToast;

/**
 * AA收款
 * 
 * @author zhouchaoxin
 * 
 */
public class AAGatheringActivity extends Activity {
	/**
	 * 存放付款数据
	 */
	private ListView listView;
	/**
	 * 
	 */
	private AAGatheringRecordAdapter recordAdapter;
	/**
	 * AA付款记录数
	 */
	private ArrayList<AaGatheringPayBean> transferRecordList;

	/**
	 * 最大返回记录数
	 */
	private final int TRANSACTION_COUNT = 8;
	/**
	 * 
	 */
	private final int START_PAGE = 0;
	/**
	 * 前面跳过的记录数
	 */
	private int start_record = START_PAGE;
	/**
	 * 是否有更多
	 */
	boolean isHaveMore = true;
	/**
	 * 是否滚动标识
	 */
	boolean isScrollFlag = true;
	/**
	 * 每页的记录
	 */
	private ArrayList<AaGatheringPayBean> transferRecordList2;

	LinearLayout loadind;
	LinearLayout not_more;
//	LinearLayout have_more;
	/**
	 * 没有记录时的默认页面
	 */
	LinearLayout linear_default;
	
	LinearLayout linear_listview;

	String productNo = "";
	String location = "";
	/**
	 * 对话框提示信息
	 */
	String prompt;
	/**
	 * /**
	 * 标识是从确认发起收款页面返回的
	 */
	private String page = "";
	/**
	 * 付款是否完成
	 */
	private boolean isSuccess;
	
	/**
	 * 屏幕宽
	 */
	private int disWidth;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		LogPrint.Print("lock","AAGatheringActivity====onCreate");
		setContentView(R.layout.aa_gathering);
		disWidth=getDisplayWidth();
		
		LogPrint.Print("lock","disWidth===="+disWidth);
		transferRecordList = new ArrayList<AaGatheringPayBean>();
		
		Bundle b = getIntent().getExtras();
		if (b != null) {
			productNo = b.getString("PRODUCTNO");
			location = b.getString("LOCATION");
		} else {
			// 测试使用
			productNo = "13311100936";
			location = "01";
		}
		
		listView = (ListView) findViewById(R.id.listview);
		loadind = (LinearLayout) findViewById(R.id.loadind);
		not_more = (LinearLayout) findViewById(R.id.not_more);
//		have_more = (LinearLayout) findViewById(R.id.have_more);
		linear_listview = (LinearLayout)findViewById(R.id.linear_listview);
		linear_default = (LinearLayout)findViewById(R.id.linear_default);
//		have_more.setOnClickListener(new OnClickListener() {
//			@Override
//			public void onClick(View v) {
//				
//				getData();
//			}
//		});

		
		recordAdapter = new AAGatheringRecordAdapter(AAGatheringActivity.this,transferRecordList,disWidth);
		listView.setAdapter(recordAdapter);

		listView.setOnItemClickListener(itemClick);
		listView.setOnScrollListener(onScroll);
	}

	
	@Override
	protected void onResume() {
		
		super.onResume();
		LogPrint.Print("lock","disWidth===="+disWidth);
		LogPrint.Print("lock","AAGatheringActivity====onResume");
		LogPrint.Print("lock","AAGatheringActivity====isHaveMore=="+isHaveMore);
		MyApp app = (MyApp) getApplication();
		page = app.getPage();
		//如果是从确认发起收款返回的
		if (page != null && !page.equals("") && page.equals("sureAA")) {
			
			isHaveMore = true;
			if (transferRecordList!= null && transferRecordList.size() > 0) {
				
				transferRecordList.clear();
				
			}
			if (start_record > 0) {
				
				start_record = 0;
			}
			 //获取数据
			 getData();
		     recordAdapter = new AAGatheringRecordAdapter(AAGatheringActivity.this,transferRecordList,disWidth);
			 listView.setAdapter(recordAdapter);
			 
		}else{
			
			//获取数据
			 getData();
		}
		
		app.setPage("");
		
		
	}
	/*
	 * private void getData2() {
	 * 	ArrayList<AaPaymentBean> paymentBeans = new ArrayList<AaPaymentBean>();
		AaGatheringPayBean bean = new AaGatheringPayBean();
		bean.setTheme("大餐1");
		bean.setAmount("25.00");
		bean.setCreateTime("2014-03-11 13:51:52");
		bean.setMark("无");
		bean.setLaunchorderId("1564646164864161646");
		// --------------

		AaPaymentBean paymetnBean = new AaPaymentBean();
		paymetnBean.setTheme("大餐1");
		paymetnBean.setPerMoney("100");
		paymetnBean.setCreatetime("2014-03-11 13:51:52");
		paymetnBean.setTransStat("S0A");
		paymetnBean.setRemark("无");
		paymetnBean.setSerialNo("1564646164864161646");
		paymetnBean.setTargetProductNo("18616989041");
		paymetnBean.setPayname("赵明");
		paymentBeans.add(paymetnBean);
		bean.setPaymentorders(paymentBeans);
		// -------------------

		transferRecordList.add(bean);

		bean = new AaGatheringPayBean();
		bean.setTheme("大餐2");
		bean.setAmount("25.00");
		bean.setCreateTime("2014-03-11 13:51:52");
		bean.setMark("无");
		bean.setLaunchorderId("1564646164864161646");

		// -------------------
		paymetnBean = new AaPaymentBean();
		paymetnBean.setTheme("大餐2");
		paymetnBean.setPerMoney("35");
		paymetnBean.setCreatetime("2014-03-11 14:51:52");
		paymetnBean.setTransStat("S0C");
		paymetnBean.setRemark("无");
		paymetnBean.setSerialNo("88631316446131679231");
		paymetnBean.setPayname("钱明");
		paymentBeans.add(paymetnBean);
		bean.setPaymentorders(paymentBeans);
		// ----------------

		transferRecordList.add(bean);

		bean = new AaGatheringPayBean();
		bean.setTheme("大餐3");
		bean.setAmount("25.00");
		bean.setCreateTime("2014-03-11 13:51:52");
		bean.setMark("无");
		bean.setLaunchorderId("1564646164864161646");

		// --------------------

		paymetnBean = new AaPaymentBean();
		paymetnBean.setTheme("大餐3");
		paymetnBean.setPerMoney("55");
		paymetnBean.setCreatetime("2014-03-11 14:51:52");
		paymetnBean.setTransStat("S0C");
		paymetnBean.setRemark("无");
		paymetnBean.setSerialNo("21631315468679894");
		paymetnBean.setPayname("孙明");
		paymentBeans.add(paymetnBean);
		bean.setPaymentorders(paymentBeans);
		// ---------------------------

		transferRecordList.add(bean);

		bean = new AaGatheringPayBean();
		bean.setTheme("大餐4");
		bean.setAmount("25.00");
		bean.setCreateTime("2014-03-11 13:51:52");
		bean.setMark("无");
		bean.setLaunchorderId("1564646164864161646");

		// --------------------
		paymetnBean = new AaPaymentBean();
		paymetnBean.setTheme("大餐4");
		paymetnBean.setPerMoney("55");
		paymetnBean.setCreatetime("2014-03-11 14:51:52");
		paymetnBean.setTransStat("S0X");
		paymetnBean.setRemark("无");
		paymetnBean.setSerialNo("21631315468679894");
		paymetnBean.setPayname("李明");
		paymentBeans.add(paymetnBean);
		bean.setPaymentorders(paymentBeans);
		transferRecordList.add(bean);
	}*/

	/**
	 * 获取收款记录数据
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
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}

				List<NameValuePair> json_params = Util.getJsonHttpParams(
						AAGatheringActivity.this, "queryAAReceivables", "", "");
				json_params.add(new BasicNameValuePair("PRODUCTNO", params[0]));
				json_params.add(new BasicNameValuePair("FROMDATE", params[1]));// 开始日期
				json_params.add(new BasicNameValuePair("TODATE", params[2]));// 截至日期
				json_params.add(new BasicNameValuePair("MAXRECORD", params[3]));// 最大返回记录数
				json_params
						.add(new BasicNameValuePair("STARTRECORD", params[4]));// 前面跳过的记录数

				LogPrint.Print("lock", "MaxRecord==========" + params[3]);
				LogPrint.Print("lock", "StartRecord==========" + params[4]);
				String result = HttpSSLRequest.httpRequest(json_params,
						AAGatheringActivity.this);

				System.out.println("交易记录的应答参数为===============" + result);
				return result;
			}

			@Override
			protected void onPostExecute(String result) {
				super.onPostExecute(result);
				if (result != null) {
					if (result.equals(COMMON.TIMEOUT)) {
						prompt = "连接超时!请检查网络连接！";
						CustomToast toast = new CustomToast(
								AAGatheringActivity.this, prompt);
						toast.show();
						isScrollFlag = true;
					} else if (result.equals(COMMON.IOEXCEPTION)) {
						prompt = "I/O异常！";
						CustomToast toast = new CustomToast(
								AAGatheringActivity.this, prompt);
						toast.show();
						isScrollFlag = true;
					} else if (result.equals(COMMON.EXCEPTION)) {
						prompt = "其它异常！";
						CustomToast toast = new CustomToast(
								AAGatheringActivity.this, prompt);
						toast.show();
						isScrollFlag = true;
					} else if (result.equals("")) {
						prompt = "其它异常！";
						CustomToast toast = new CustomToast(
								AAGatheringActivity.this, prompt);
						toast.show();
						isScrollFlag = true;
					} else {
						try {

							JSONObject jsonObj = new JSONObject(result);
							String errorCode = jsonObj.getString("ERRORCODE");
							String errorMsg = jsonObj.getString("ERRORMSG");
							if (errorCode.equals("000000")) {
								start_record += TRANSACTION_COUNT;
								LogPrint.Print("lock","AAGatheringActivity-----start_record==="+start_record);
								JSONArray jsonArray = jsonObj.getJSONArray("RECEIVABLESLIST");
								LogPrint.Print("lock","jsonArray.length()========"+ jsonArray.length());

								transferRecordList2 = new ArrayList<AaGatheringPayBean>();

								if (jsonArray.length() < TRANSACTION_COUNT) {
									
									
									if(jsonArray.length() != 0){
										isHaveMore = false;
										CustomToast toast = new CustomToast(
												AAGatheringActivity.this, "^_^就这么多啦~");
										toast.show(300);
									}
									
									/*if (jsonArray.length() == 0) {
										
										loadind.setVisibility(View.GONE);
										isHaveMore = false;
										have_more.setVisibility(View.GONE);
										
									}else{
										
										loadind.setVisibility(View.GONE);
										isHaveMore = false;
										have_more.setVisibility(View.GONE);
										CustomToast toast = new CustomToast(AAGatheringActivity.this,"^_^就这么多啦~");
										toast.show(300);
									}*/
								}

								transferRecordList2 = AaGatheringPayBean.getTransBeans(jsonArray);
								
								transferRecordList.addAll(transferRecordList2);
								//未查询到记录
								if (transferRecordList.size() < 1) {
									
									mHandler.sendEmptyMessage(101010);
									
								}else{
									
									mHandler.sendEmptyMessage(101011);
								}
								BestpayAAStatic.AAGATHERING_NUM = 0;

								for (int i = 0; i < transferRecordList.size(); i++) {

									ArrayList<AaPaymentBean> paymentBeans = transferRecordList.get(i).getPaymentorders();
									if (paymentBeans != null) {

										for (int j = 0; j < paymentBeans.size(); j++) {
											// 交易未完成
											if (paymentBeans.get(j).getTransStat().equals("S0A")) {

												isSuccess = true;
												break;
												
											}else{
												
												isSuccess = false;
											}
										}
									}
									if (isSuccess) {
										
										BestpayAAStatic.AAGATHERING_NUM += 1;
									}
									
								}
								
//								if (isHaveMore) {
//
//									have_more.setVisibility(View.VISIBLE);
//								}
								recordAdapter.reData();
								isScrollFlag = true;
								// 发送广播收款数量
								Intent intent = new Intent(BestpayAAStatic.ACTION_AAGATHERING);
								intent.putExtra("GATHERING_NUM",BestpayAAStatic.AAGATHERING_NUM+"");
								sendBroadcast(intent);
								loadind.setVisibility(View.GONE);
							} else {
								loadind.setVisibility(View.GONE);
								/*if (isHaveMore) {

									have_more.setVisibility(View.VISIBLE);
								}*/
								prompt = errorMsg + "(" + errorCode + ")";
								CustomToast toast = new CustomToast(
										AAGatheringActivity.this, prompt);
								toast.show();
								isScrollFlag = true;
							}
						} catch (JSONException e) {
							loadind.setVisibility(View.GONE);
//							if (isHaveMore) {
//
//								have_more.setVisibility(View.VISIBLE);
//							}
							prompt = "数据格式错误（JSON）";
							CustomToast toast = new CustomToast(
									AAGatheringActivity.this, prompt);
							toast.show();
							isScrollFlag = true;
						}
					}
				} else {
					loadind.setVisibility(View.GONE);
//					if (isHaveMore) {
//
//						have_more.setVisibility(View.VISIBLE);
//					}
					prompt = "连接超时!请检查网络连接！";
					CustomToast toast = new CustomToast(
							AAGatheringActivity.this, prompt);
					toast.show();
					isScrollFlag = true;
				}
			}
		};
		
		if (isHaveMore) {
			isScrollFlag = false;
			task.execute(productNo, "", "", TRANSACTION_COUNT + "",start_record + "");
		}
		
	}

	/**
	 * listView每项点击
	 */
	private OnItemClickListener itemClick = new OnItemClickListener() {

		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position,
				long id) {
			
			// 跳转到详情操作
			Intent intent = new Intent(AAGatheringActivity.this,AADetailActivityGroup.class);
			Bundle b = new Bundle();
			// 账单金额
			String tempMoney = transferRecordList.get(position).getAmount();
			// 参与人数
			String tempPerson = String.valueOf(transferRecordList.get(position)
					.getPaymentorders().size());
			// 主题
			b.putString("THEME", transferRecordList.get(position).getTheme());
			// 全额
			b.putString("PERMONEY", tempMoney);
			// 参与人数
			b.putString("PARTICIPANTSNUM", String.valueOf((Integer.parseInt(tempPerson)+1)));
			// 人均金额
			String personMoney = personMoneyProcess(tempMoney, String.valueOf((Integer.parseInt(tempPerson)+1)));
			b.putString("personmoney", personMoney);

			// 已付人员
			String paid = paidPerson(transferRecordList.get(position)
					.getPaymentorders());
			b.putString("HAVEPAIED", paid);
			// 未付人员
			String notPaid = notPaidPerson(transferRecordList.get(position)
					.getPaymentorders());
			b.putString("NOTPAID", notPaid);

			// 备注
			b.putString("REMARK", transferRecordList.get(position).getMark());
			// 交易编号
			b.putString("SERIALNO", transferRecordList.get(position)
					.getLaunchorderId());

			// 交易状态
			String state = getState(transferRecordList.get(position)
					.getPaymentorders());
			b.putString("TRANSSTAT", state);

			// 交易时间
			b.putString("TRANSDATE", transferRecordList.get(position)
					.getCreateTime());

			
			//参与人员名单
			b.putParcelableArrayList("paymentorders", transferRecordList.get(position).getPaymentorders());
			intent.putExtras(b);
			AAGatheringActivity.this.startActivity(intent);
		}

	};

	/**
	 * 查出已付人员
	 * 
	 * @param payment
	 * @return
	 */
	private String paidPerson(ArrayList<AaPaymentBean> payments) {

		StringBuffer sbPaid = new StringBuffer();

		if (payments != null) {
			for (int i = 0; i < payments.size(); i++) {

				AaPaymentBean payment = payments.get(i);
				if (payment.getTransStat().equals("S0C")) {

					sbPaid.append(payment.getPayname() + "；");

				}
			}

		}

		return sbPaid.toString();
	}

	/**
	 * 查出未付人员
	 * 
	 * @param payment
	 * @return
	 */
	private String notPaidPerson(ArrayList<AaPaymentBean> payments) {

		StringBuffer sbNotPaid = new StringBuffer();
		if (payments != null) {
			for (int i = 0; i < payments.size(); i++) {

				AaPaymentBean payment = payments.get(i);
				if (payment.getTransStat().equals("S0A")) {

					sbNotPaid.append(payment.getPayname() + "；");

				}
			}
		}

		return sbNotPaid.toString();
	}

	/**
	 * 查询交易状态
	 * 
	 * @param payments
	 * @return
	 */
	private String getState(ArrayList<AaPaymentBean> payments) {
		// 已完成数量
		int success = 0;
		// 状态提示
		String stateInfo = "";
		// 发起收款对应的付款数量
		int sumNumber = payments.size();
		for (int i = 0; i < payments.size(); i++) {

			AaPaymentBean paymentBen = payments.get(i);
			// 付款交易状态
			String state = paymentBen.getTransStat();

			if (state.equals("S0A"))// 未完成
			{
				// 等待付款
				stateInfo = "未完成";
			} else if (state.equals("S0X")) // 交易关闭
			{
				// 交易关闭
				stateInfo = "交易关闭";

			} else if (state.equals("S0C"))// 完成
			{
				// 交易完成
				// stateInfo = "交易完成";
				success++;
			}

		}

		if (success == sumNumber) {

			stateInfo = "交易完成";
		}

		return stateInfo;
	}

	/**
	 * 
	 */
	private OnScrollListener onScroll = new OnScrollListener() {

		private int lastVisibleIndex = 0;

		@Override
		public void onScrollStateChanged(AbsListView view, int scrollState) {
            //回调顺序如下    
            //第1次：scrollState = SCROLL_STATE_TOUCH_SCROLL(1) 正在滚动    
            //第2次：scrollState = SCROLL_STATE_FLING(2) 手指做了抛的动作（手指离开屏幕前，用力滑了一下）    
            //第3次：scrollState = SCROLL_STATE_IDLE(0) 停止滚动  
			
			//当滚到最后一行且停止滚动时，执行加载
			if (scrollState == OnScrollListener.SCROLL_STATE_IDLE
					&& lastVisibleIndex == transferRecordList.size() - 1
					&& isScrollFlag) {
				
				getData();
			}
		}

		//滚动时一直回调，直到停止滚动时才停止回调。单击时回调一次。    
        //firstVisibleItem：当前能看见的第一个列表项ID（从0开始）    
        //visibleItemCount：当前能看见的列表项个数（小半个也算）    
        //totalItemCount：列表项共数  
		@Override
		public void onScroll(AbsListView view, int firstVisibleItem,
				int visibleItemCount, int totalItemCount) {

			lastVisibleIndex = firstVisibleItem + visibleItemCount - 1;
		}
	};

	/**
	 * 发起收款按钮事件
	 */
	private OnClickListener onClick = new OnClickListener() {

		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.btn_gathering:
				Intent intent = new Intent();
				intent.setClass(AAGatheringActivity.this,
						AAGatheringSponsorActivity.class);
				startActivity(intent);
				break;

			default:
				break;
			}
		}
	};

	/**
	 * 计算人均金额 得到的金额四舍五入
	 */
	private String personMoneyProcess(String tempMoey, String tempperson) {

		int person = Integer.parseInt(String.valueOf(tempperson));
		double personMoney = Double.parseDouble(String.valueOf(tempMoey));

		double successMoney = (personMoney / person);
		
		if (String.valueOf(successMoney).indexOf(".") != -1) {
			String temp = String.valueOf(successMoney);
			String processMoney = temp.substring(String.valueOf(successMoney).indexOf(".")+1, temp.length());
			if (processMoney.length() == 2) {
				
				return String.valueOf(successMoney);
			}else{
				
				double money = (Math.round(successMoney*100 + 0.5)/100.0); 
				return replaceMoney(money);
			}
			
		}
		else{
			
			double money = (Math.round(successMoney*100 + 0.5)/100.0); 
			return replaceMoney(money);
		}

	}

	/**
	 * 人均金额处理
	 * 
	 * @param money
	 * @return
	 */
	private static String replaceMoney(double money) {

		StringBuffer temp = new StringBuffer(String.valueOf(money));
		String str = temp.toString();
		//最后一位是否包含1
		if (str.endsWith("1")) {
			
			temp.replace(temp.lastIndexOf("1"), temp.length(), "0");
			
		}
		
		return temp.toString();
	}
	
	/**
	 * 更新UI界面
	 */
	private Handler mHandler = new Handler(){
		
		public void handleMessage(android.os.Message msg) {
			
			switch (msg.what) {
			case 101010:
				linear_default.setVisibility(View.VISIBLE);
				linear_listview.setVisibility(View.GONE);
				break;
			case 101011:
				linear_default.setVisibility(View.GONE);
				linear_listview.setVisibility(View.VISIBLE);
				break;
			default:
				break;
			}
			
		};
		
	};
	
	/**
	 * 获取屏幕宽度
	 * @return
	 */
	private int getDisplayWidth() {
		// 获取屏幕宽度
		DisplayMetrics outMetrics = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(outMetrics);
		return outMetrics.widthPixels;
	}
}
