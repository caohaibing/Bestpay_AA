package com.bestpay_aa.activity;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnKeyListener;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.bestpay_aa.adapter.AASpinnerAdapter;
import com.bestpay_aa.bean.Person;
import com.bestpay_aa.bean.SerializableMap;
import com.bestpay_aa.util.LogPrint;
import com.bestpay_aa.util.Util;
import com.bestpay_aa.view.CustomToast;


/**
 * 发起付款
 * @author zhouchaoxin
 *
 */
public class AAGatheringSponsorActivity extends BaseActivity {

	/**
	 * 确定付款
	 */
	private Button positiveButton;
	/**
	 * 主题
	 */
	private EditText theme;
	
	/**
	 * 账单金额
	 */
	private EditText allmoney;
	
	/**
	 * 金额限制提示
	 */
	private LinearLayout linear_limit1;
	/**
	 * 参与人数
	 */
	private EditText participantsNum;
	
	/**
	 * 参与人数
	 */
	private EditText get_person;
	
	/**
	 * 人数限制提示
	 */
	private LinearLayout linear_limit2;
	/**
	 * 备注
	 */
	private EditText remark;
	/**
	 * 人数限制提示
	 */
	private TextView participantsToastNum;
	/**
	 * 参与人数和应收付款提示
	 */
	private LinearLayout linear_personAndMoney;
	/**
	 * 主题时间(用于显示)
	 */
	private TextView themetime;
	/**
	 * 对话框提示信息
	 */
	private String mMsg;
	/**
	 * 获取联系人
	 */
	private Button btn_get_person;
	/**
	 * 联系人姓名
	 */
	private String name;
	/**
	 * 联系人电话
	 */
	private String numPhone;
	/**
	 * (暂时未用)
	 */
	private List<HashMap<String, Object>> datas;
	/**
	 * (暂时未用)
	 */
	String[] typenumArray = null;
	
	/**
	 * 调用本机通讯录(暂时未用)
	 */
	private final int DIALG_CONTENT = 0;
	
	/**
	 *调用日期控件
	 */
	private final int DATE_PICKER_ID = 1;
	/**
	 * 保存联系人
	 */
	private ArrayList<Person> persions;
	/**
	 * 用与资金源
	 */
	private static String[] mItems;
	private Spinner spinner;
	private AASpinnerAdapter adapter; 
	/**
	 * 资金源
	 */
	private String moneySource;
	/**
	 * 主题时间(用于传值)
	 */
	private String timeTheme;
	/**
	 * 参与人员姓名
	 */
	private ArrayList<String> personName;
	
	private StringBuilder strPersonName;
	
	private RelativeLayout add_person;
	/**
	 * 提示参与人数
	 */
	private TextView personAndMoney_person;
	/**
	 * 提示参与金额
	 */
	private TextView personAndMoney_money;
	
	private LinearLayout linear_personAndMoney_hint;
	/**
	 * 总额范围
	 */
	private TextView money_recharge;
	/**
	 * 主题提示
	 */
	private TextView thme_recharge;
	/**
	 * 此Map用于接收选中联系人的ID和状态
	 */
	private SerializableMap map;
	
	/**
	 * 记录已选择人数
	 */
	private HashMap<Object,Person> personMap;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
	
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.aa_gathering_sponsor);
		mItems = getResources().getStringArray(R.array.mw_spinnername);
		map = new SerializableMap();
		personMap = new HashMap<Object, Person>();
		
		initView();
		
		personName = new ArrayList<String>(); 
		
		Person person1 = new Person();
		person1.setName("张三");
		person1.setPhone("13560368254");
		Person person2 = new Person();
		person2.setName("李四");
		person2.setPhone("13560368254");
		persions = new ArrayList<Person>();
		
		persions.add(person1);
		persions.add(person2);
	}
	
	/**
	 * 初始化布局VIEW
	 */
	private void initView(){
		theme = (EditText)findViewById(R.id.theme);
		allmoney = (EditText)findViewById(R.id.allmoney);
		participantsNum = (EditText)findViewById(R.id.participantsNum);
		
		get_person = (EditText)findViewById(R.id.get_person);
		remark = (EditText)findViewById(R.id.remark);
		participantsToastNum = (TextView)findViewById(R.id.participantsToastNum);
		//linear_limit1 = (LinearLayout)findViewById(R.id.linear_limit1);
		
		money_recharge = (TextView)findViewById(R.id.money_recharge);
		thme_recharge = (TextView)findViewById(R.id.thme_recharge);
		
		linear_limit2 = (LinearLayout)findViewById(R.id.linear_limit2);
		linear_personAndMoney = (LinearLayout)findViewById(R.id.linear_personAndMoney);
		spinner = (Spinner)findViewById(R.id.spinner);
		
		add_person = (RelativeLayout)findViewById(R.id.add_person);
		
		themetime = (TextView)findViewById(R.id.themetime);
		themetime.setOnClickListener(onClick);
		//将可选内容与ArrayAdapter连接起来  
        adapter = new AASpinnerAdapter(this, mItems);
        spinner.setAdapter(adapter);
        //添加事件Spinner事件监听    
        spinner.setOnItemSelectedListener(new SpinnerSelectedListener());  
        //设置默认值  
        spinner.setVisibility(View.VISIBLE);
         
        //btn_get_person = (Button)findViewById(R.id.btn_get_person);
		
		positiveButton = (Button)findViewById(R.id.positiveButton);
		
		personAndMoney_person = (TextView)findViewById(R.id.personAndMoney_person);
		personAndMoney_money = (TextView)findViewById(R.id.personAndMoney_money);
		
		linear_personAndMoney_hint = (LinearLayout)findViewById(R.id.linear_personAndMoney_hint);
		
		theme.addTextChangedListener(new TextWatcher() {
			
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				
			}
			
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
				
			}
			
			@Override
			public void afterTextChanged(Editable s) {
				
				if (s.toString().length() < 1) {
					
					thme_recharge.setText("请输入主题");
					thme_recharge.setVisibility(View.VISIBLE);
					
				}else{
					
					thme_recharge.setVisibility(View.INVISIBLE);
				}
				
			}
		});
		
		allmoney.addTextChangedListener(new TextWatcher() {
			
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				
				
			}
			
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
				
				
			}
			
			@Override
			public void afterTextChanged(Editable s) {
				if(recharge()){
					money_recharge.setVisibility(View.INVISIBLE);
					get_person.setOnClickListener(onClick);
					if (!participantsNum.getText().toString().equals("") && !participantsNum.getText().toString().equals("0")) {
						
						String particiNum = participantsNum.getText().toString();
						int num = Integer.parseInt(particiNum);
						String personMoney = personMoneyProcess();
						//personAndMoney_hint.setVisibility(View.VISIBLE);
						linear_personAndMoney.setVisibility(View.VISIBLE);
						if (Double.parseDouble(personMoney) >= 1.00) {
							personAndMoney_person.setText(String.valueOf(num-1));
							personAndMoney_money.setText(String.valueOf(personMoney));
							//personAndMoney_hint.setText("向其它"+(num-1)+"人，每人收"+personMoney+"元");
							positiveButton.setOnClickListener(onClick);
							positiveButton.setBackgroundResource(R.drawable.custembtn);
							linear_personAndMoney_hint.setVisibility(View.GONE);
						}else{
							
							//personAndMoney_hint.setText("每人平均收款金额不能少于1.0元");
							linear_personAndMoney_hint.setVisibility(View.VISIBLE);
							linear_personAndMoney.setVisibility(View.GONE);
							positiveButton.setOnClickListener(null);
							positiveButton.setBackgroundResource(R.drawable.scan_cancel);
						}
					}
					
				}else{
					money_recharge.setVisibility(View.VISIBLE);
					linear_personAndMoney.setVisibility(View.GONE);
					linear_personAndMoney_hint.setVisibility(View.GONE);
					get_person.setOnClickListener(null);
					positiveButton.setOnClickListener(null);
					positiveButton.setBackgroundResource(R.drawable.scan_cancel);
				}
				

			}
		});
		participantsNum.addTextChangedListener(new TextWatcher() {
			
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				
			}
			
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
				
			}
			
			@Override
			public void afterTextChanged(Editable s) {
			
				if (s.toString().length() < 1) {
					LogPrint.Print("lock","1s.toString()======"+s.toString());
					participantsToastNum.setText("参与人数不能少于1人");
					linear_limit2.setVisibility(View.VISIBLE);
					//personAndMoney_hint.setVisibility(View.INVISIBLE);
					linear_personAndMoney.setVisibility(View.GONE);
					linear_personAndMoney_hint.setVisibility(View.GONE);
					positiveButton.setOnClickListener(null);
					positiveButton.setBackgroundResource(R.drawable.scan_cancel);
					
				}else if(Integer.parseInt(s.toString()) > 30){
					LogPrint.Print("lock","2s.toString()======"+s.toString());
					participantsToastNum.setText("一次AA最多30人参与");
					linear_limit2.setVisibility(View.VISIBLE);
					//personAndMoney_hint.setVisibility(View.INVISIBLE);
					linear_personAndMoney.setVisibility(View.GONE);
					linear_personAndMoney_hint.setVisibility(View.GONE);
					positiveButton.setOnClickListener(null);
					positiveButton.setBackgroundResource(R.drawable.scan_cancel);
					
				}else if(Integer.parseInt(s.toString()) == 1 || Integer.parseInt(s.toString()) == 0){
					LogPrint.Print("lock","3s.toString()======"+s.toString());
					participantsToastNum.setText("参与人数不能少于1人");
					linear_limit2.setVisibility(View.VISIBLE);
					//personAndMoney_hint.setVisibility(View.INVISIBLE);
					linear_personAndMoney.setVisibility(View.GONE);
					linear_personAndMoney_hint.setVisibility(View.GONE);
					positiveButton.setOnClickListener(null);
					positiveButton.setBackgroundResource(R.drawable.scan_cancel);
					
				}else{
					
					linear_limit2.setVisibility(View.INVISIBLE);
					LogPrint.Print("lock","4s.toString()======"+s.toString());
					String particiNum = participantsNum.getText().toString();
					int num = Integer.parseInt(particiNum);
					String personMoney = personMoneyProcess();
					//personAndMoney_hint.setVisibility(View.VISIBLE);
					linear_personAndMoney.setVisibility(View.VISIBLE);
					if (Double.parseDouble(personMoney) >= 1.00) {
						
						personAndMoney_person.setText(String.valueOf(num-1));
						personAndMoney_money.setText(String.valueOf(personMoney));
						//personAndMoney_hint.setText("向其它"+(num-1)+"人，每人收"+personMoney+"元");
						positiveButton.setOnClickListener(onClick);
						positiveButton.setBackgroundResource(R.drawable.custembtn);
						
						linear_personAndMoney_hint.setVisibility(View.GONE);
					}else{
						
						//personAndMoney_hint.setText("每人平均收款金额不能少于1.0元");
						
						linear_personAndMoney_hint.setVisibility(View.VISIBLE);
						
						linear_personAndMoney.setVisibility(View.GONE);
						positiveButton.setOnClickListener(null);
						positiveButton.setBackgroundResource(R.drawable.scan_cancel);
					}
					
					
					
				}
			}
		});
	}
		
	/**
	 * spinner监听器
	 * @author zhouchaoxin
	 *
	 */
	private class SpinnerSelectedListener implements OnItemSelectedListener{

		@Override
		public void onItemSelected(AdapterView<?> parent, View view,
				int position, long id) {
			
			moneySource = mItems[position];
		}

		@Override
		public void onNothingSelected(AdapterView<?> parent) {
			
			
		}
		
		
	}
	
	/**
	 * 确定收款\查找联系人\获取主题时间(处理事件)
	 */
	private OnClickListener onClick = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			Intent intent = new Intent();
			Bundle extras = new Bundle();
			switch (v.getId()) {
			case R.id.positiveButton:
				if (otherRecharge() && recharge()) {
					
					String personMoney = personMoneyProcess();
					
					extras.putString("theme", theme.getText().toString());
					extras.putString("themetime", timeTheme);
					extras.putString("participantsNum", participantsNum.getText().toString());
					extras.putString("allmoney", allmoney.getText().toString());
					extras.putString("remark",remark.getText().toString());
					extras.putString("personMoney",personMoney);
					extras.putSerializable("persions", persions);
					extras.putString("moneySource", moneySource);
					intent.putExtras(extras);
					intent.setClass(AAGatheringSponsorActivity.this, AAGatheringSureActivity.class);
					startActivity(intent);
					AAGatheringSponsorActivity.this.finish();
					
				}
				
				
				break;
			case R.id.get_person:
				
				intent.setClass(AAGatheringSponsorActivity.this, CopyContactsListMultiple.class);
				
				intent.putExtra("map", map);
				
				intent.putExtra("personMap", personMap);
			
				startActivityForResult(intent, 110);
				
				break;
			/*case 1000:
				intent.setClass(AAGatheringSponsorActivity.this, CopyContactsListMultiple.class);
				startActivityForResult(intent, 110);
				break;*/
			case R.id.themetime:
				showDialog(DATE_PICKER_ID);
				break;
			default:
				break;
			}
			
		}
	};
	
	/**
	 * 检测输入
	 * @return
	 */
	private boolean otherRecharge(){
		 //主题为空
		if (theme.getText().toString().equals("") || theme.getText().toString() == null) {
			mMsg = "请输入主题";
			/*CustomToast toast = new CustomToast(AAGatheringSponsorActivity.this, mMsg);
			toast.show(300);*/
			thme_recharge.setText(mMsg);
			return false;
		}
		//主题时间
		/*if (themetime.getText().toString().equals("") || themetime.getText().toString() == null) {
			mMsg = "请输入主题时间";
			CustomToast toast = new CustomToast(AAGatheringSponsorActivity.this, mMsg);
			toast.show(300);
			return false;
		}*/
		//单独处理账单金额
		if(!recharge()){
			
			return false;
		}
		if (participantsNum.getText().toString().equals("") || participantsNum.getText().toString() == null) {
			
			mMsg = "请输入参与人数";
			CustomToast toast = new CustomToast(AAGatheringSponsorActivity.this, mMsg);
			toast.show(300);
			return false;
		}else{
			
			String tempNum = String.valueOf(participantsNum.getText().toString().trim());
			if (Integer.parseInt(tempNum) > 30) {
				
				linear_limit2.setVisibility(View.VISIBLE);
				return false;
			}else if (Integer.parseInt(tempNum) <=0 ) {
				linear_limit2.setVisibility(View.VISIBLE);
				participantsToastNum.setText("参与人数不能少于1人");
				return false;
			}else{
				
				linear_limit2.setVisibility(View.INVISIBLE);
			}
		}
		
		
		return true;
		
	}
	
	/**
	 * 判断充值金额的合法性
	 */
	private boolean recharge() {
		String rechargCount = allmoney.getText().toString();
		if (rechargCount.equals("")) {
			mMsg = getString(R.string.m_account_net_bank_dialog_note3);
			/*CustomToast toast = new CustomToast(AAGatheringSponsorActivity.this, mMsg);
			toast.show(300);*/
			money_recharge.setText(mMsg);
			return false;
		} else if ((rechargCount.startsWith("0") && !rechargCount
				.startsWith("0."))) {
			
			//mMsg = getString(R.string.m_start_with_zero);
			mMsg = "账单总额不得少于1元";
			/*CustomToast toast = new CustomToast(AAGatheringSponsorActivity.this,mMsg);
			toast.show(300);*/
			money_recharge.setText(mMsg);
			return false;
		} else if (Util.yuan2fen(rechargCount).equals("0.0")
				|| Util.yuan2fen(rechargCount).equals("0")) {
			mMsg = getString(R.string.m_account_net_bank_dialog_note5);
			/*CustomToast toast = new CustomToast(AAGatheringSponsorActivity.this, mMsg);
			toast.show(300);*/
			money_recharge.setText(mMsg);
			return false;
		} else if (!Util.isFen(rechargCount)) {
			mMsg = getString(R.string.m_account_net_bank_dialog_note5);
			/*CustomToast toast = new CustomToast(AAGatheringSponsorActivity.this,mMsg);
			toast.show(300);*/
			money_recharge.setText(mMsg);
			return false;
		}else if (rechargCount.startsWith(".") || rechargCount.endsWith(".")) {
			
			mMsg = getString(R.string.m_account_net_bank_dialog_note5);
			/*CustomToast toast = new CustomToast(AAGatheringSponsorActivity.this, mMsg);
			toast.show(300);*/
			money_recharge.setText(mMsg);
			return false;
		}
		/* 账单限额暂时不用
		 * else if (Double.parseDouble(String.valueOf(rechargCount)) > 2000) {
			mHandler.sendEmptyMessage(20);
			return false;
		}*/
		//linear_limit1.setVisibility(View.INVISIBLE);
		get_person.setOnClickListener(onClick);
		return true;
	}
	
	
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		/**
		 * 处理调用联系人的返回操作
		 */
		switch (requestCode) {
		case 110:
			System.out.println(resultCode);
			personName.clear();
			if (map.getMap() != null && map.getMap().size() > 0) {
				
				map.getMap().clear();
			}
			if (personMap != null && personMap.size() > 0) {
				
				personMap.clear();
			}
			strPersonName = new StringBuilder();
			
			Message msg = new Message();
			msg.what = 10;
			
			if (data != null && data.getExtras() != null) {
				persions = (ArrayList<Person>) data.getExtras().getSerializable("GET_CONTACT");
				map = (SerializableMap)data.getExtras().getSerializable("map");
				personMap = (HashMap<Object, Person>)data.getExtras().getSerializable("personMap");
				int size = persions.size();
				for (Person person : persions) {
					String phone = person.getPhone();
					String name = person.getName();
					strPersonName.append(name+"，");
					personName.add(name);
					LogPrint.Print("lock","name======"+name);
				}
				msg.arg1 = size+1;
				String name = strPersonName.replace(strPersonName.lastIndexOf("，"), strPersonName.length(), " ").toString();
				msg.obj = name;
				strPersonName = null;
				mHandler.sendMessage(msg);
			}else{
				
				msg.arg1 = 0;
				mHandler.sendMessage(msg);
			}

			break;
		}
	}
	/**
	 * 处理界面更新
	 */
	private Handler mHandler = new Handler(){
		
		public void handleMessage(android.os.Message msg) {
			
			switch (msg.what) {
			case 10:
				//RelativeLayout.LayoutParams lp=new RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT,LayoutParams.WRAP_CONTENT); 
				//lp.addRule(RelativeLayout.ALIGN_PARENT_RIGHT, RelativeLayout.TRUE); 
				
				participantsNum.setText(String.valueOf(msg.arg1));
				String str = (String)msg.obj;
				get_person.setVisibility(View.VISIBLE);
				//get_person.setLayoutParams(lp);
				get_person.setText(str);
				break;
			case 20:
				//linear_limit1.setVisibility(View.VISIBLE);
				break;
			default:
				break;
			}
			
			
		};
	};
	//创建监听器(使用匿名内部类)
	DatePickerDialog.OnDateSetListener  onDateSetListener = new DatePickerDialog.OnDateSetListener() {
		
		//参数依次为：DatePicker控件，年，月，日。
	    //特别注意，月份是从0到11，而日期则是从1开始
		@Override
		public void onDateSet(DatePicker view, int year, int monthOfYear,
				int dayOfMonth) {
			
			LogPrint.Print("lock","DatePicker==="+year+"-"+(monthOfYear+1)+"-"+dayOfMonth);
			themetime.setText((monthOfYear+1)+"-"+dayOfMonth);
			timeTheme = year+"-"+(monthOfYear+1)+"-"+dayOfMonth;
		}
	};
      
	@Override
	protected Dialog onCreateDialog(int id) {
		switch (id) {
		case DIALG_CONTENT:
			//创建Dialog
		    AlertDialog.Builder builder = new AlertDialog.Builder(this);
		    builder.setTitle("选择一个号码");
		    builder.setSingleChoiceItems(typenumArray,-1, new DialogInterface.OnClickListener() {
		     @Override
		     public void onClick(DialogInterface dialog, int which) {
		      numPhone = datas.get(which).get("numPhone").toString();
		     }
		    });
		    builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
		     @Override
		     public void onClick(DialogInterface dialog, int which) {
		       //textView.setText(numPhone + " "+ name);
		     }
		     });
		    builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
		     @Override
		     public void onClick(DialogInterface dialog, int which) {
		      Intent intent = new Intent();
		      intent.setAction(Intent.ACTION_PICK);
		      intent.setData(android.provider.ContactsContract.Contacts.CONTENT_URI);
		      startActivityForResult(intent, 110);
		      }
		     });
		    // dialog的返回键处理
		    builder.setOnKeyListener(new OnKeyListener() {
		     @Override
		     public boolean onKey(DialogInterface arg0,
		       int arg1, KeyEvent arg2) {
		      if (arg1 == KeyEvent.KEYCODE_BACK && arg2.getRepeatCount() == 0) {
		       Intent intent = new Intent();
		       intent.setAction(Intent.ACTION_PICK);
		       intent.setData(android.provider.ContactsContract.Contacts.CONTENT_URI);
		       startActivityForResult(intent, 110);
		       return true;
		      }
		      return true;
		     }
		    });
		    builder.create();
		    
		return builder.show();

		case DATE_PICKER_ID:
			//获取一个日历对象
			Calendar dateAndTime = Calendar.getInstance(Locale.CHINA); 
			int v_year = dateAndTime.get(Calendar.YEAR); 
			int v_month = dateAndTime.get(Calendar.MONTH); 
			int v_day = dateAndTime.get(Calendar.DAY_OF_MONTH);
			return new DatePickerDialog(this,onDateSetListener,v_year,v_month,v_day);
		default:
			break;
		}
		return super.onCreateDialog(id);
	}

	/**
	 * 计算人均金额
	 * 得到的金额四舍五入
	 */
	private String personMoneyProcess(){
		String tempMoey = "";
		if (!allmoney.getText().toString().equals("")) {
			
			tempMoey = allmoney.getText().toString();
		}else{
			
			tempMoey = "0";
		}
		
		String tempperson = participantsNum.getText().toString();
		
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
	 * @param money
	 * @return
	 */
	private static String replaceMoney(double money){
		
		StringBuffer temp = new StringBuffer(String.valueOf(money));
		String str = temp.toString();
		//最后一位是否包含1
		if (str.endsWith("1")) {
			
			temp.replace(temp.lastIndexOf("1"), temp.length(), "0");
		}
		
		return temp.toString();
	}
	

	
}
