package com.bestpay_aa.bean;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.v4.os.ParcelableCompat;

/**
 * 通讯录联系人
 * @author zhouchaoxin
 *
 */
public class Person implements Parcelable {

	/**
	 * 联系人姓名
	 */
	String PAYNAME;
	/**
	 * 联系人电话
	 */
	String PAYPRODUCTNO;
	
	/**
	 * 唯一标识
	 */
	String ID;
	
	
	public String getID() {
		return ID;
	}
	public void setID(String iD) {
		ID = iD;
	}
	public String getName() {
		return this.PAYNAME;
	}
	public void setName(String name) {
		this.PAYNAME = name;
	}
	public String getPhone() {
		return this.PAYPRODUCTNO;
	}
	public void setPhone(String phone) {
		this.PAYPRODUCTNO = phone;
	}
	
	
	@Override
	public int describeContents() {
		
		return 0;
	}
	@Override
	public void writeToParcel(Parcel dest, int flags) {
	
		dest.writeString(PAYNAME);  
        dest.writeString(PAYPRODUCTNO);
	}
	
	public static final Parcelable.Creator<Person> CREATOR = new Creator<Person>()  
		    {  
		        public Person createFromParcel(Parcel source)  
		        {  
		            Person person = new Person();  
		            person.PAYNAME = source.readString();  
		            person.PAYPRODUCTNO = source.readString();  
		            return person;  
		        }  
		        public Person[] newArray(int size)  
		        {  
		            return new Person[size];  
		        }  
		    }; 
	
	
	
	
}
