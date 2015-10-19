package com.bestpay_aa.activity;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import com.bestpay_aa.bean.AaPaymentBean;
import com.bestpay_aa.dialog.CustomDialog;
import com.bestpay_aa.net.HttpSSLRequest;
import com.bestpay_aa.util.COMMON;
import com.bestpay_aa.util.Util;
import com.bestpay_aa.view.CustomDialog2;
import com.bestpay_aa.view.CustomProgressBarDialog_1;
import com.bestpay_aa.view.PassInput;
import com.huateng.encrypt.PinkeyEncrypt;

import android.app.Dialog;
import android.content.ComponentName;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.DialogInterface.OnCancelListener;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

/**
 * 付款账单详情
 * @author zhouchaoxin
 *
 */
public class AAGatheringDetailActivity extends BaseActivity{

	/**
	 * 主题
	 */
	private TextView theme;
	/**
	 * 全额
	 */
	private TextView allmoney;
	/**
	 * 人均
	 */
	private TextView perMoney;
	/**
	 * 参与人数
	 */
	private TextView participantsNum;
	/**
	 * 已付
	 */
	private TextView havePaied;
	/**
	 * 未付
	 */
	private TextView notPaid;
	/**
	 * 备注
	 */
	private TextView remark;
	/**
	 * 交易编号
	 */
	private TextView serialNo;
	/**
	 * 交易状态
	 */
	private TextView transStat;
	/**
	 * 交易时间
	 */
	private TextView transDate;
	/**
	 * 关闭收款单
	 */
	private Button positiveButton;
	/**
	 * 参与人数点击需要的布局
	 */
	private LinearLayout linear_person;
	/**
	 * 未付款人员
	 */
	StringBuffer sbNotPaid;
	/**
	 * 已付款人员
	 */
	StringBuffer sbPaid;
	/**
	 * 参与人员名单
	 */
	private ArrayList<AaPaymentBean> payments;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.aa_gathering_details);
		initView();
		Bundle b = getIntent().getExtras();
		if (b != null) {
			
			theme.setText(b.getString("THEME"));
			allmoney.setText(b.getString("PERMONEY")+" 元");
			perMoney.setText(b.getString("personmoney")+" 元");
			participantsNum.setText(b.getString("PARTICIPANTSNUM") +" 人");
			
			
			/*if (!b.getString("HAVEPAIED").equals("") && b.getString("HAVEPAIED") != null) {
				
				sbPaid = new StringBuffer(b.getString("HAVEPAIED"));
				sbPaid.replace(sbPaid.lastIndexOf("；"), sbPaid.length(), "");
				havePaied.setText(sbPaid.toString());
			}*/
			
			
			/*if (!b.getString("NOTPAID").equals("") && b.getString("NOTPAID") != null) {
				
				sbNotPaid = new StringBuffer(b.getString("NOTPAID"));
				sbNotPaid.replace(sbNotPaid.lastIndexOf("；"), sbNotPaid.length(), "");
				notPaid.setText(sbNotPaid.toString());
			}*/
			
			
			remark.setText(b.getString("REMARK"));
			//serialNo.setText(b.getString("SERIALNO"));
			//transStat.setText(b.getString("TRANSSTAT"));\
			try {
				String createTime  = new SimpleDateFormat("MM-dd HH:mm").format(
						new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(b.getString("TRANSDATE")));
				transDate.setText(createTime);
			} catch (Exception e) {
				e.printStackTrace();
			}
			
			
			if (b.getParcelableArrayList("paymentorders") != null) {
				
				payments = b.getParcelableArrayList("paymentorders");
			}
		}
		
		
	}
	/**
	 * 初始化控件VIEW
	 */
	private void initView(){
		
		theme = (TextView)findViewById(R.id.theme);
		allmoney = (TextView)findViewById(R.id.allmoney);
		perMoney = (TextView)findViewById(R.id.perMoney);
		participantsNum = (TextView)findViewById(R.id.participantsNum);
		//havePaied = (TextView)findViewById(R.id.havePaied);
		//notPaid = (TextView)findViewById(R.id.notPaid);
		remark = (TextView)findViewById(R.id.remark);
		//serialNo = (TextView)findViewById(R.id.serialNo);
		//transStat = (TextView)findViewById(R.id.transStat);
		transDate = (TextView)findViewById(R.id.transDate);
		//positiveButton = (Button)findViewById(R.id.positiveButton);
		
		//positiveButton.setVisibility(View.VISIBLE);
		//positiveButton.setOnClickListener(onClick);
		//positiveButton.setVisibility(View.GONE);
		
		linear_person = (LinearLayout)findViewById(R.id.linear_person);
		linear_person.setOnClickListener(null);
		
		
	}
	/**
	 * 关闭收款事件
	 */
	private OnClickListener onClick = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.linear_person:
				Intent intent = new Intent();
				if (payments != null) {
					
					intent.putParcelableArrayListExtra("paymentorders", payments);
					intent.setClass(AAGatheringDetailActivity.this, AAPaymentPersonActivity.class);
					startActivity(intent);
				}
				break;
			default:
				break;
			}
			
		}
	};
	
	
	@Override
	protected Dialog onCreateDialog(int id) {
		CustomDialog.Builder customBuilder = null;
		switch (id) {
		case 0:
			customBuilder = new CustomDialog.Builder(AAGatheringDetailActivity.this);
			customBuilder
					.setTitle(getString(R.string.mw_dialog_title))
					.setMessage("确定要关闭该收款单吗？")
					.setPositiveButton(R.string.mw_sure,
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int which) {
									AAGatheringDetailActivity.this.finish();
								}
							}).setNegativeButton("取消", new DialogInterface.OnClickListener() {
								
								@Override
								public void onClick(DialogInterface dialog, int which) {
									
									dismissDialog(0);
									
								}
							});
					
			return customBuilder.create();
		default:
			break;
		}
		return super.onCreateDialog(id);
	
	}
	
}
