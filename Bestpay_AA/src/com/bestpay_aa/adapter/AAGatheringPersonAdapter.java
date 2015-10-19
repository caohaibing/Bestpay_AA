package com.bestpay_aa.adapter;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bestpay_aa.activity.R;
import com.bestpay_aa.bean.Person;

/**
 * AA付款联系人
 * @author zhouchaoxin
 *
 */
public class AAGatheringPersonAdapter extends BaseAdapter {
	
	private ViewHolder holder;
	/**
	 * 通讯录联系人
	 */
	private List<Person> mContactsList;
	 
	private Context mContext;
	/**
	 * 保存checkbox是否被选中的状态
	 */
	public Map<Integer,Boolean> checkedMap;
	
	
	
	public List<Person> getmContactsList() {
		return mContactsList;
	}

	public void setmContactsList(List<Person> mContactsList) {
		this.mContactsList = mContactsList;
	}

	public AAGatheringPersonAdapter(Context context,List<Person> contactsList,Map<String,Boolean> checked) {
		
		this.mContext = context;
		this.mContactsList = contactsList;
		
		checkedMap = new HashMap<Integer, Boolean>(); 
		for(int i=0;i<contactsList.size();i++){ 
			
        		Log.i("lock", "getName()======"+contactsList.get(i).getName());
        		
				if (checked.containsKey(contactsList.get(i).getID())) {

						
						checkedMap.put(i, true);

				}else{
					
					checkedMap.put(i, false);
				}
              
        } 
	}

	@Override
	public int getCount() {
		return mContactsList.size();
	}

	@Override
	public Object getItem(int position) {
		return mContactsList.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		LayoutInflater mInflater = LayoutInflater.from(mContext);
		if (convertView == null) {
			holder = new ViewHolder();
			convertView = (LinearLayout) mInflater.inflate(R.layout.contactslistitem, null);
			holder.checkBox = (CheckBox)convertView.findViewById(R.id.checkBox);
			holder.name = (TextView)convertView.findViewById(R.id.name);
			holder.phone = (TextView)convertView.findViewById(R.id.phone);
			convertView.setTag(holder);
		} else {
			
			holder = (ViewHolder) convertView.getTag();
		}	
		
		
			
		holder.checkBox.setChecked(checkedMap.get(position)); 
		
		holder.name.setText(mContactsList.get(position).getName());
		holder.phone.setText(mContactsList.get(position).getPhone());
		
		
		return convertView;
	}

	
	
	class ViewHolder {
		
		LinearLayout linearItem;
		CheckBox checkBox;
		TextView name;
		TextView phone;
		TextView number;
	}
}
