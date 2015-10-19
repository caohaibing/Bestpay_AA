package com.bestpay_aa.adapter;

import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bestpay_aa.activity.R;
import com.bestpay_aa.bean.AaGatheringPayBean;
import com.bestpay_aa.bean.AaPaymentBean;
import com.bestpay_aa.util.LogPrint;

/**
 * AA付款记录adapter
 * @author zhouchaoxin
 *
 */
public class AAGatheringRecordAdapter extends BaseAdapter {
	

	private List<AaGatheringPayBean> mTransferRecordList;
	 
	 
	private Context mContext;
	/**
	 * 已完成数量
	 */
	private int success;
	/**
	  * 发起收款对应的付款数量
	  */
	private int sumNumber;
	/**
	 * 状态提示
	 */
	private String stateInfo;
	/**
	 * 状态图
	 */
	private Bitmap bm;
	/**
	 * 快过期
	 */
	private Bitmap bm_k;
	/**
	 * 分享图
	 */
	private Bitmap bm_share;
	/**
	 * 分享到的手机号
	 */
	String payProductNo;
	/**
	 * 分享的内容
	 */
	String smsShareContent;
	/**
	 * 屏幕宽
	 */
	private int disWidth;
	
	public AAGatheringRecordAdapter(Context context,List<AaGatheringPayBean> transferRecordList,int screenWidth) {
		
		this.mContext = context;
		this.mTransferRecordList = transferRecordList;
		
		smsShareContent = context.getResources().getString(R.string.mw_share_msg);
		disWidth=screenWidth;
	}
	public void reData() {
		notifyDataSetChanged();
	}

	@Override
	public int getCount() {
		return mTransferRecordList.size();
	}

	@Override
	public Object getItem(int position) {
		return mTransferRecordList.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
	    ViewHolder holder;
		LayoutInflater mInflater = LayoutInflater.from(mContext);
		if (convertView == null) {
			holder = new ViewHolder();
			convertView = mInflater.inflate(R.layout.aa_gathering_item,null);
			convertView.setTag(holder);
			
		} else {
			
			holder = (ViewHolder) convertView.getTag();
		}

		holder.themeTv = (TextView) convertView.findViewById(R.id.theme);
		holder.perMoneyTv = (TextView) convertView
				.findViewById(R.id.perMoney);
		holder.transDateTv = (TextView) convertView
				.findViewById(R.id.transDate);
		holder.transStatTv = (TextView) convertView
				.findViewById(R.id.transStat);
		holder.image_state = (ImageView)convertView.findViewById(R.id.image_state);
		
		holder.image_state_k = (ImageView)convertView.findViewById(R.id.image_state_k);
		
		holder.image_share = (ImageView)convertView.findViewById(R.id.image_share);
		
		holder.themeTv.setText(mTransferRecordList.get(position).getTheme().length() > 5 ?mTransferRecordList.get(position).getTheme().substring(0, 5)+"...":mTransferRecordList.get(position).getTheme());
		
		
		holder.perMoneyTv.setTextColor(mContext.getResources().getColor(R.color.color_money));
		holder.perMoneyTv.setText(mTransferRecordList.get(position).getAmount() + "元");
		try {
			String createTime  = new SimpleDateFormat("MM-dd").format(
					new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(mTransferRecordList.get(position).getCreateTime()));
			holder.transDateTv.setText(createTime);
		} catch (Exception e) {
			e.printStackTrace();
			holder.transDateTv.setText("");
		}

		getState(mTransferRecordList.get(position).getPaymentorders());
		
		holder.transStatTv.setText(statusToDesc(stateInfo,String.valueOf(success),String.valueOf(sumNumber)));
		holder.image_state.setVisibility(View.VISIBLE);
		holder.image_state.setImageBitmap(bm);
		//holder.image_state.setImageResource(resouceId);
		
		holder.image_state_k.setVisibility(View.VISIBLE);
		holder.image_state_k.setImageBitmap(bm_k);
		
		//分享
		holder.image_share.setVisibility(View.VISIBLE);
		holder.image_share.setImageBitmap(bm_share);
		holder.image_share.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
				payProductNo="";
				Uri uri = Uri.parse("smsto:" + payProductNo);  
			    Intent sendIntent = new Intent(Intent.ACTION_VIEW, uri);
			    sendIntent.putExtra("sms_body",smsShareContent);
				mContext.startActivity(sendIntent);
			}
		});
		//默认值，防止数据重复
		stateInfo = "";
		success = 0;
		sumNumber = 0;
		bm = null;
		bm_k = null;
		bm_share = null;
		return convertView;
	}

	/**
	 * 显示交易状态、未付款人数、要付款人数
	 * @param stat 交易状态
	 * @param notpayAmount 未付款人数
	 * @param payamount 要付款人数
	 * @return
	 */
	private String statusToDesc(String stat,String notpayAmount, String payamount) {
		
		String statusDesc = "";
		//statusDesc = stat + " "+notpayAmount+"/"+payamount;
		statusDesc = notpayAmount+"/"+payamount;
		return statusDesc;
	}
	
	class ViewHolder {
		/**
		 * 主题
		 */
		TextView themeTv;
		/**
		 * 金额
		 */
		TextView perMoneyTv;
		/**
		 *  发起付款时间
		 */
		TextView transDateTv;
		/**
		 * 状态
		 */
		TextView transStatTv;
		/**
		 * 图片显示状态
		 */
		ImageView image_state;
		/**
		 * 分享
		 */
		ImageView image_share;
		/**
		 * 快过期
		 */
		ImageView image_state_k;
	}
	
	
	private void getState(ArrayList<AaPaymentBean> mPaymentorders){
		if (mPaymentorders != null) {
			
			sumNumber = mPaymentorders.size();
			for (int i = 0; i < mPaymentorders.size(); i++) {
				
				AaPaymentBean paymentBen = mPaymentorders.get(i);
				//付款交易状态
				String state = paymentBen.getTransStat();
				if (state.equals("S0A"))//未完成
				{
					//等待付款
					stateInfo = "未完成";
					
					
					
					try {
						//失效时间
						Date failtime=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(paymentBen.getFailuretime());
						LogPrint.Print("lock","failtime.getTime() ================="+failtime.getTime());
						//发起时间
						//Date createTime=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(paymentBen.getCreatetime());
					    //LogPrint.Print("lock","createTime.getTime()================="+createTime.getTime());
						Date nowtime=new Date();
						LogPrint.Print("lock","failtime.getTime() - nowtime.getTime()=="+(failtime.getTime() - nowtime.getTime()));
						//快过期
						if((failtime.getTime() - nowtime.getTime())<24*60*60*1000){
							InputStream is = mContext.getResources().openRawResource(R.drawable.faild_k);  
							Bitmap bitMap = BitmapFactory.decodeStream(is);
							if (disWidth >= 720) {
								
								bm_k = setBitmap(bitMap, 1);
							}else{
								
								bm_k = bitMap;
							}
							
						}
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
				else if(state.equals("S0X")) //交易关闭
				{
					//交易关闭
					stateInfo = "交易关闭";
					InputStream is = mContext.getResources().openRawResource(R.drawable.faild);  
					Bitmap bitMap = BitmapFactory.decodeStream(is);
					if (disWidth >= 720) {
						
						bm = setBitmap(bitMap,2);
						
					}else{
						
						bm = bitMap;
					}
					
					
				}
				else if(state.equals("S0C"))//完成
				{
					//交易完成
					//stateInfo = "交易完成";
					success++;
					
				}
			}
			
			if (success == sumNumber) {
				
				stateInfo = "交易完成";
				InputStream is_share = mContext.getResources().openRawResource(R.drawable.share);  
				Bitmap bitMap_share = BitmapFactory.decodeStream(is_share);
				if (disWidth >= 720) {
					
					bm_share = setBitmap(bitMap_share, 4);
				}else{
					
					bm_share = bitMap_share;
				}
				
				
				InputStream is = mContext.getResources().openRawResource(R.drawable.success_all);  
				Bitmap bitMap = BitmapFactory.decodeStream(is);
		        if (disWidth >= 720) {
					
		        	bm = setBitmap(bitMap,3);
				}else{
					
					bm = bitMap;
				}
				
		        
			}
		}
	}
	/**
	 * 
	 * @param bitMap
	 * @param type图片类型(1、快过期 2、交易关闭 3、完成 4、分享)
	 * @return
	 */
	private Bitmap setBitmap(Bitmap bitMap,int type){
		
		int width = bitMap.getWidth();
        int height = bitMap.getHeight();
        
        int newWidth = 0;
        int newHeight = 0;
        // 设置想要的大小
        switch (type) {
		case 1:
			newWidth = 120;
			newHeight = 40;
			break;
		case 2:
		case 3:
			newWidth = 300;
			newHeight = 100;
			break;
		case 4:
			newWidth = 40;
			newHeight = 40;
			break;
		default:
			break;
		}
        // 计算缩放比例
        float scaleWidth = ((float) newWidth) / width;
        float scaleHeight = ((float) newHeight) / height;
        // 取得想要缩放的matrix参数
        Matrix matrix = new Matrix();
        matrix.postScale(scaleWidth, scaleHeight);
        // 得到新的图片
        return Bitmap.createBitmap(bitMap, 0, 0, width, height, matrix, true);
	}
}
