package com.bestpay_aa.activity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.sourceforge.pinyin4j.PinyinHelper;
import android.app.Activity;
import android.app.Dialog;
import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build.VERSION;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.ContactsContract;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.bestpay_aa.adapter.AAGatheringPersonAdapter;
import com.bestpay_aa.bean.Person;
import com.bestpay_aa.bean.SerializableMap;
import com.bestpay_aa.dialog.CustomDialog;
import com.bestpay_aa.util.LogPrint;
import com.bestpay_aa.util.MyApp;
import com.bestpay_aa.util.Util;
import com.bestpay_aa.view.CustomProgressBarDialog_1;
import com.bestpay_aa.view.CustomToast;
import com.contacts.search.util.PinYin;

/**
 * 获取通讯录联系人
 * @author zhouchaoxin
 *
 */
public class CopyContactsListMultiple extends Activity implements
		OnClickListener {

	private final int UPDATE_LIST = 1;
	
	private final int LOADER_LIST = 2;
	/**
	 * 得到的所有联系人
	 */
	private ArrayList<Person> contactsList;
	/**
	 * 选择得到联系人(用于传值)
	 */
	private ArrayList<Person> getcontactsList;
	/**
	 * 全选
	 */
	private Button okbtn;
	/**
	 * 已选择
	 */
	private Button cancelbtn;
	/**
	 * 启动线程加载数据
	 */
	Thread getcontacts;
	private AAGatheringPersonAdapter personAdapter;
	private ListView listView;
	/**
	 * 对话框提示语
	 */
	private String dialogPrompt = "";
	/**
	 * 未找到任何联系人提示
	 */
	private final int DIALOG_PAY_CANCEL = 30;
	/**
	 * 等待对话框
	 */
	private final int DIALOG_PROGRESSBAR = 31;
	private CustomDialog.Builder  customBuilder;
	private CustomProgressBarDialog_1.Builder progressBar; 
	private Person person;
	
	/**
	 * 自动搜索提示
	 */
	private AutoCompleteTextView actv;
	/**
	 * 记录已选择人数
	 */
	private HashMap<Object,Person> personMap = null;
	
	/**
	 * 保存checkbox是否被选中的状态
	 */
	public Map<String,Boolean> checkedMap;
	/**
	 * 搜索出来的联系人
	 */
	ArrayList<Person>  searchPersons;
	/**
	 * 此Map用于接收选中联系人的ID和状态
	 */
	private SerializableMap map;
	
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.contactslist);
		contactsList = new ArrayList<Person>();
		getcontactsList = new ArrayList<Person>();
		personMap = new HashMap<Object, Person>();
		checkedMap = new HashMap<String, Boolean>(); 
		map = new SerializableMap();
		
		searchPersons = new ArrayList<Person>();
		
		listView = (ListView)findViewById(R.id.listView_person);
		actv = (AutoCompleteTextView)findViewById(R.id.actv);
		
		okbtn = (Button) findViewById(R.id.contacts_done_button);
		cancelbtn = (Button) findViewById(R.id.contact_back_button);
		
		
		okbtn.setOnClickListener(this);
		cancelbtn.setOnClickListener(this);
		
		if (getIntent().getExtras() != null) {
			
			if ((SerializableMap)getIntent().getExtras().get("map") != null) {
				
				map = (SerializableMap)getIntent().getExtras().get("map");
				if (map.getMap() != null) {
					
					if (map.getMap().size() > 0) {
						
						checkedMap = map.getMap();
						
					}
				}
				
			}
			if (getIntent().getExtras().get("personMap") != null) {
				
				personMap = (HashMap<Object, Person>) getIntent().getExtras().get("personMap");
				if (personMap != null) {
					
					if (personMap.size() > 0) {
						
						Message message = updateListHandler.obtainMessage();
						message.arg1 = personMap.size();
						message.what = 1100;
						updateListHandler.sendMessage(message);
						
					}
				}
				
			}
		}
		
		
		

		actv.addTextChangedListener(new TextWatcher() {
			
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				
				
			}
			
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
				
				
			}
			
			@Override
			public void afterTextChanged(Editable s) {
				
				if(s.toString().length()>0){
                    String mSearchaContactName = s.toString().trim();
                    searchPersons = search(mSearchaContactName, contactsList);
                    personAdapter = new AAGatheringPersonAdapter(CopyContactsListMultiple.this, searchPersons,checkedMap);
     			    
                    listView.setAdapter(personAdapter);
                    listView.setOnItemClickListener(itemClick);
                }else{
                	searchPersons.clear();
                	personAdapter = new AAGatheringPersonAdapter(CopyContactsListMultiple.this, contactsList,checkedMap);
     			    
                    listView.setAdapter(personAdapter);
                    listView.setOnItemClickListener(itemClick);
                }
			}
		});

	}
	
	@Override
	protected Dialog onCreateDialog(int id) {
		switch (id) {
		case DIALOG_PAY_CANCEL:
			customBuilder = new CustomDialog.Builder(CopyContactsListMultiple.this);
			customBuilder
					.setTitle("提示")
					.setMessage("未找到任何联系人")
					.setPositiveButton("确定",
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int which) {

									dialog.dismiss();
									
									CopyContactsListMultiple.this.finish();
									onDestroy();

								}
							});
			return customBuilder.create();
			
		case DIALOG_PROGRESSBAR:
			progressBar = new CustomProgressBarDialog_1.Builder(
					CopyContactsListMultiple.this, false);
			progressBar.setTitle("提示").setMessage(dialogPrompt).setCancel(true);
			
			return progressBar.create();
		}
		
		return super.onCreateDialog(id);
		
		
	};
	
	
	Handler updateListHandler = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {

			case UPDATE_LIST:
				if (progressBar != null) {
					removeDialog(DIALOG_PROGRESSBAR);
				}
				updateList();
				break;
			case 1001: //未找到任何联系人
				if (progressBar != null) {
					removeDialog(DIALOG_PROGRESSBAR);
				}
				showDialog(DIALOG_PAY_CANCEL);
				
				break;
			case LOADER_LIST:
				getcontacts = new Thread(new GetContacts());
				getcontacts.start();
				dialogPrompt = "请稍候......";
				showDialog(DIALOG_PROGRESSBAR);
				
				break;

			case 1100:
				cancelbtn.setText("已选择" + "(" + msg.arg1 + ")");
				break;
			}
		}
	};

	

	

	@Override
	protected void onResume() {
		super.onResume();
		
		if (contactsList.size() < 1) {
			Message msg1 = new Message();
			msg1.what = LOADER_LIST;
			updateListHandler.sendMessage(msg1);
			
		}else{
			
			if (personAdapter != null) {
				
				personAdapter.notifyDataSetChanged();
				listView.setAdapter(personAdapter);
				
			}else{
				
				Message msg1 = new Message();
				msg1.what = UPDATE_LIST;
				updateListHandler.sendMessage(msg1);
			}
			
		}
		

	}

	void updateList() {
		
		if (contactsList != null){
			
			personAdapter = new AAGatheringPersonAdapter(CopyContactsListMultiple.this, contactsList,checkedMap);
			listView.setAdapter(personAdapter);
			listView.setOnItemClickListener(itemClick);
			
		}
	}

	/**
	 * listView点击事件
	 */
	private OnItemClickListener itemClick = new OnItemClickListener() {

		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position,
				long id) {
			MyApp app = (MyApp) getApplication();
			LinearLayout holder = (LinearLayout) view;
			LinearLayout childView = (LinearLayout) holder.getChildAt(0);
			CheckBox checkBox = (CheckBox) childView.getChildAt(0);
			if (checkBox.isChecked() == false) {
				checkBox.setChecked(true);
				personAdapter.checkedMap.put(position, true);
				if (searchPersons.size() > 0) {

					String name = searchPersons.get(position).getName();
					String phone = searchPersons.get(position).getPhone();
					//去掉带有空格的号码
					phone = splitStr(phone);
					
					if(isPhone(phone,name)){
						//如果选中的号与登录使用的号码一样
						if (phone.equals(app.getProd())) {
							
							CustomToast toast = new CustomToast(CopyContactsListMultiple.this,"不能对自己发起付款");
							toast.show(300);
							if (checkBox.isChecked() == true) {
								checkBox.setChecked(false);
								personAdapter.checkedMap.put(position, false);
							}
						}else{
							
							if (personMap != null && personMap.size()+1 > 29) {
								//提示不能超过29人
								CustomToast toast = new CustomToast(CopyContactsListMultiple.this,"每笔AA最多30人参与");
								toast.show(300);
								if (checkBox.isChecked() == true) {
									checkBox.setChecked(false);
									personAdapter.checkedMap.put(position, false);
								}
								
							}else{
								
								Person person = new Person();
								person.setName(name);
								person.setPhone(phone);
								checkedMap.put(searchPersons.get(position).getID(), true);

								personMap.put(searchPersons.get(position).getID(), person);
								
							}
							cancelbtn.setText("已选择" + "(" + personMap.size() + ")");
							
						}
						
					}else{
						
						if (checkBox.isChecked() == true) {
							checkBox.setChecked(false);
							personAdapter.checkedMap.put(position, false);
						}
					}
					

				} else {

					String name = contactsList.get(position).getName();
					String phone = contactsList.get(position).getPhone();
					//去掉带有空格的号码
					phone = splitStr(phone);
					if (isPhone(phone,name)) {
						//如果选中的号与登录使用的号码一样
						if (phone.equals(app.getProd())) {
							
							CustomToast toast = new CustomToast(CopyContactsListMultiple.this,"不能对自己发起付款");
							toast.show(300);
							if (checkBox.isChecked() == true) {
								checkBox.setChecked(false);
								personAdapter.checkedMap.put(position, false);
							}
						}else{
							
							if (personMap != null && personMap.size()+1 > 29) {
								//提示不能超过29人
								CustomToast toast = new CustomToast(CopyContactsListMultiple.this,"每笔AA最多30人参与");
								toast.show(300);
								if (checkBox.isChecked() == true) {
									checkBox.setChecked(false);
									personAdapter.checkedMap.put(position, false);
								}
							}else{
								
								Person person = new Person();
								person.setName(name);
								person.setPhone(phone);
								checkedMap.put(contactsList.get(position).getID(), true);

								personMap.put(contactsList.get(position).getID(), person);
							}
							
							cancelbtn.setText("已选择" + "(" + personMap.size() + ")");
							
						}
						
					}else{
						
						if (checkBox.isChecked() == true) {
							checkBox.setChecked(false);
							personAdapter.checkedMap.put(position, false);
						}
					}
					
					
				}

			} else if (checkBox.isChecked() == true) {
				checkBox.setChecked(false);
				personAdapter.checkedMap.put(position, false);

				if (searchPersons.size() > 0) {

					Person person = (Person) searchPersons.get(position);
					if ((person.getName().toString()).indexOf("[") > 0) {
						String phoneNum = person
								.getPhone()
								.toString()
								.substring(
										0,
										(person.getPhone().toString())
												.indexOf("\n"));
						personMap.remove(person.getID());
						Log.d("remove_num", "" + phoneNum);
					} else {
						personMap.remove(person.getID());
					}

					checkedMap.remove(searchPersons.get(position).getID());
					cancelbtn.setText("已选择" + "(" + personMap.size() + ")");

				} else {

					Person person = (Person) contactsList.get(position);

					if ((person.getName().toString()).indexOf("[") > 0) {
						String phoneNum = person
								.getPhone()
								.toString()
								.substring(
										0,
										(person.getPhone().toString())
												.indexOf("\n"));
						personMap.remove(person.getID());
						Log.d("remove_num", "" + phoneNum);
					} else {
						personMap.remove(person.getID());
					}
					checkedMap.remove(contactsList.get(position).getID());
					cancelbtn.setText("已选择" + "(" + personMap.size() + ")");
				}

			}
		}
	};
	

	class GetContacts implements Runnable {
		@Override
		public void run() {
			ContentResolver cr = CopyContactsListMultiple.this.getContentResolver();
			Cursor cursor = null;
			try {
				
				Uri uri = ContactsContract.Contacts.CONTENT_URI;  
		           String[] projection = new String[] {  
		                    ContactsContract.Contacts._ID,  
		                    ContactsContract.Contacts.DISPLAY_NAME,  
		                    ContactsContract.Contacts.PHOTO_ID  
		            };  
		            String selection = ContactsContract.Contacts.IN_VISIBLE_GROUP + " = '1'";  
		            String[] selectionArgs = null;  
		            String sortOrder = ContactsContract.Contacts.DISPLAY_NAME + " COLLATE LOCALIZED ASC";  
		            cursor=managedQuery(uri, projection, selection, selectionArgs, sortOrder);  
		             //LogPrint.Print("lock", "cursor.getCount()======"+cursor.getCount());
		        if (cursor.getCount() > 0) {
		        	int personId = 0;
		        	while (cursor.moveToNext()){    
		                // 取得联系人名字    
		                int nameFieldColumnIndex = cursor.getColumnIndex(android.provider.ContactsContract.PhoneLookup.DISPLAY_NAME);    
		                String name = cursor.getString(nameFieldColumnIndex);    
		                // 取得联系人ID    
		                String contactId = cursor.getString(cursor.getColumnIndex(android.provider.ContactsContract.Contacts._ID));    
		                
		                Cursor phonecur = cr.query(android.provider.ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, android.provider.ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = "  + contactId, null, null);
		                
		                //Cursor  phonecur = managedQuery(android.provider.ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, android.provider.ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = "  + contactId, null, null);     
		                // 取得电话号码(可能存在多个号码)
		                //LogPrint.Print("lock", "phonecur.getCount()======"+phonecur.getCount());
		                if (phonecur.getCount() > 0) {
							
		                	while (phonecur.moveToNext()){    
			                    String strPhoneNumber = phonecur.getString(phonecur.getColumnIndex(android.provider.ContactsContract.CommonDataKinds.Phone.NUMBER));    
			                    if(strPhoneNumber.length()>4)
			                    	
			                    	person = new Person();
		                    		person.setName(name);
		                    		person.setPhone(strPhoneNumber);
			                    	person.setID(String.valueOf(personId++));
									contactsList.add(person);  
			
			                } 
		                	
		                	LogPrint.Print("lock", "VERSION.SDK_INT========"+VERSION.SDK_INT);
		                	//4.0以上的版本会自动关闭
		                	//if(VERSION.SDK_INT < 14)  { 
		                		
		                		if (phonecur != null){
									
									phonecur.close();
									
								} 
		                    //}
		                	
						}
		            }
		        	
		        	Message msg1 = new Message();
					msg1.what = UPDATE_LIST;
					updateListHandler.sendMessage(msg1);
				}else{
					//未找到任何联系人
					
					Message msg1 = new Message();
					msg1.what = 1001;
					updateListHandler.sendMessage(msg1);
				}
			} catch (Exception e) {
				
				e.printStackTrace();
			}finally{
				//4.0以上的版本会自动关闭
				if(VERSION.SDK_INT < 14) {  
					if (cursor != null) {
					
						cursor.close();
					}
				 } 
				
			}
		}
	}

	@Override
	protected void onPause() {
		super.onPause();

	}

	@Override
	protected void onDestroy() {
		contactsList.clear();
		getcontactsList.clear();
		personMap.clear();
		searchPersons.clear();
		checkedMap.clear();
		super.onDestroy();
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.contact_back_button:
			Intent i = new Intent();
			if (personMap.size() > 0) {
				
				Set keySet = personMap.keySet();
				Iterator iter = keySet.iterator();
				while (iter.hasNext()) {
					
					    String key =  String.valueOf(iter.next());
						getcontactsList.add(personMap.get(key));
					
				}
				
			}
			if (getcontactsList != null && getcontactsList.size() > 0) {
				
				if (getcontactsList.size() > 29) {
					getcontactsList.clear();
					CustomToast toast = new CustomToast(CopyContactsListMultiple.this,"每笔AA最多30人参与");
					toast.show(300);
					
				}else{
					map.setMap(checkedMap);
					Bundle b = new Bundle();
					b.putParcelableArrayList("GET_CONTACT", getcontactsList);
					b.putSerializable("map", map);
					b.putSerializable("personMap", personMap);
					i.putExtras(b);
					
					setResult(RESULT_OK, i);
					CopyContactsListMultiple.this.finish();
					onDestroy();
				}
			}
			break;
		default:
			break;
		}
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			Intent i = new Intent();
			if (personMap.size() > 0) {
				Set keySet = personMap.keySet();
				Iterator iter = keySet.iterator();
				while (iter.hasNext()) {
					    String key =  String.valueOf(iter.next());
						getcontactsList.add(personMap.get(key));
				}
			}
			
			if (getcontactsList != null && getcontactsList.size() > 0) {
				if (getcontactsList != null && getcontactsList.size() > 29) {
					getcontactsList.clear();
					//提示不能超过29人
					CustomToast toast = new CustomToast(CopyContactsListMultiple.this,"每笔AA最多30人参与");
					toast.show(300);
					return true;
					
				}else{
					map.setMap(checkedMap);
					Bundle b = new Bundle();
					b.putParcelableArrayList("GET_CONTACT", getcontactsList);
					b.putSerializable("map", map);
					b.putSerializable("personMap", personMap);
					i.putExtras(b);
					setResult(RESULT_OK, i);
				}
					
			}
		} 
		return super.onKeyDown(keyCode, event);
	}
	
	
	
	
	/**
	 * 按号码-首字母搜索联系人
	 * @param str
	 * @param allContacts
	 * @return
	 */
	  public static ArrayList<Person> search(final String str,
	      final ArrayList<Person> allContacts) {
	    ArrayList<Person> contactList = new ArrayList<Person>();
	    // 如果搜索条件以0 1 +开头则按号码搜索
	    if (str.startsWith("0") || str.startsWith("1") || str.startsWith("+")) {
	      for (Person contact : allContacts) {
	    	  
	    	  contact.setID(contact.getID());
	    	  
	        if (contact.getPhone() != null && contact.getName() != null) {
	          if (contact.getPhone().contains(str)
	              || contact.getName().contains(str)) {
	            
	            contactList.add(contact);
	          }
	        }
	      }
	      return contactList;
	    }
	    boolean isChinese = false;
        Pattern pattern = Pattern.compile("[\\u4E00-\\u9FA5]");
        Matcher matcher = pattern.matcher(str);
        if (matcher.find()) { // 如果是中文
            isChinese = true;
        }
 
        for (Person contact : allContacts) {
            if (contains(contact, str, isChinese)) {
                contactList.add(contact);
            } else if (contact.getPhone().contains(str)) {
                
                contactList.add(contact);
            }
        }
        return contactList;
	  }

	
	  
	  /**
	   * 首字母搜索
	   * @param contact
	   * @param search
	   * @param isChinese
	   * @return
	   */
	  public static boolean contains(Person contact, String search,boolean isChinese) {
	    if (TextUtils.isEmpty(contact.getName()) || TextUtils.isEmpty(search)) {
	      return false;
	    }

	    boolean flag = false;
	    Log.i("lock","search=---search.length()=="+search.length());
	    
	    
	    if (isChinese) {
            // 根据全拼中文查询
            Pattern pattern = Pattern.compile(search.replace("-", ""),
                    Pattern.CASE_INSENSITIVE);
            Matcher matcher = pattern.matcher(contact.getName());
            
            return matcher.find();
        }
	    // 简拼匹配,如果输入在字符串长度大于6就不按首字母匹配了
	    if (search.length() < 6) {
	    	//获得首字母字符串
	    	String firstLetters = getPinYinHeadChar(contact.getName());
	       
	        // 不区分大小写
	    	Pattern firstLetterMatcher = Pattern.compile("^" + search,
	          Pattern.CASE_INSENSITIVE);
	    	flag = firstLetterMatcher.matcher(firstLetters).find();
	    }
        if (!flag) { // 如果简拼已经找到了，就不使用全拼了
  	      // 全拼匹配
  	       //ChineseSpelling finder = ChineseSpelling.getInstance();
  	       //finder.setResource(contact.getName());
  	      // 不区分大小写
  	      Pattern pattern3 = Pattern
  	          .compile(search, Pattern.CASE_INSENSITIVE);
  	      Matcher matcher3 = pattern3.matcher(PinYin.getPinYin(contact
  	          .getName()));
  	      flag = matcher3.find();
  	    }
	    return flag;
	  }
	
	  
	/**
	 * 提取每个汉字的首字母
	 * 
	 * @param str
	 * @return String
	 */
	public static String getPinYinHeadChar(String str) {
		String convert = "";
		for (int j = 0; j < str.length(); j++) {
			char word = str.charAt(j);
			// 提取汉字的首字母
			String[] pinyinArray = PinyinHelper.toHanyuPinyinStringArray(word);
			if (pinyinArray != null) {
				convert += pinyinArray[0].charAt(0);
			} else {
				convert += word;
			}
		}
		return convert;
		
		
	}
	/**
	 * 检测手机号码是否合法
	 * @param numPhone
	 * @return
	 */
	private boolean isPhone(String numPhone,String name){
		
		if (Util.formatTelNum(numPhone).substring(0, 1).equals("0") || Util.judgeTMobile(Util.formatTelNum(numPhone).replace(" ", "").toString()) == false) {
			
			CustomToast toast = new CustomToast(CopyContactsListMultiple.this, name+"的手机号码格式有误");
			toast.show(300);
			return false;
		}
		return true;
		
	}
	/**
	 * 去掉带有空格的号码
	 * @param str 手机号码
	 * @return
	 */
	public static String splitStr(String str){
		StringBuilder tempString = new StringBuilder();
		if(str.contains(" ")){
			String[] temp = str.split(" ");
			for (int i = 0; i < temp.length; i++) {
				tempString.append(temp[i]);
			}
		}else{
			
			return str;
		}
		return tempString.toString();
	}
	
	
}
