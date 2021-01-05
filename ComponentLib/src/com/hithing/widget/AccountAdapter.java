/**========================================
 * File:	AccountAdapter.java
 * Package:	com.hithing.widget
 * Create:	by leo
 * Date:	2012-7-23:下午6:57:53
 **======================================*/
package com.hithing.widget;

import java.util.List;
import java.util.Map;

import com.hithing.hsc.entity.AccountItemValue;
import com.hithing.hsc.entity.SearchableAdapterItem;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

/**
 * <p>AccountAdapter</p>
 * 	
 * <p>帐目适配器</p>
 *
 * @author leo
 * 
 */
public final class AccountAdapter extends AbstractMapAdapter<String, AccountItemValue> {
	public AccountAdapter(Context context,
			Map<String, AccountItemValue> data, Class<String> clazz,
			int resId, List<Integer> childIds) {
		super(context, data, clazz, resId, childIds);
		// TODO Auto-generated constructor stub		
	}	

	@Override
	protected View createViewFromResource(int position, View convertView,
			ViewGroup parent, int resource) {
		ViewHolder holder = null;
		AccountItemValue item = (AccountItemValue)getItem(position);				
		ViewGroup convertViewGroup;
		if(convertView == null){			
			holder = new ViewHolder();
			convertView = inflater.inflate(resource, parent, false);
			convertViewGroup 		= (ViewGroup)convertView;
			holder.payCodeTxv  	  	= (TextView)convertViewGroup.findViewById(childIds.get(ViewNumber.ACCOUNT_NUMBER_PAYCODE));
			holder.billCodeTxv 	  	= (TextView)convertViewGroup.findViewById(childIds.get(ViewNumber.ACCOUNT_NUMBER_BILLCODE));			
			holder.tableTxv 		= (TextView)convertViewGroup.findViewById(childIds.get(ViewNumber.ACCOUNT_NUMBER_TABLE));
			holder.personTxv		= (TextView)convertViewGroup.findViewById(childIds.get(ViewNumber.ACCOUNT_NUMBER_PERSON));
			holder.foundingTimeTxv	= (TextView)convertViewGroup.findViewById(childIds.get(ViewNumber.ACCOUNT_NUMBER_FOUNDINGTIME));
			holder.payBillTimeTxv	= (TextView)convertViewGroup.findViewById(childIds.get(ViewNumber.ACCOUNT_NUMBER_PAYBILLTIME));
			holder.moneyTxv			= (TextView)convertViewGroup.findViewById(childIds.get(ViewNumber.ACCOUNT_NUMBER_MONEY));
			holder.shiftTxv			= (TextView)convertViewGroup.findViewById(childIds.get(ViewNumber.ACCOUNT_NUMBER_SHIFT));
			convertView.setTag(holder);		
//			Log.d("GetView", String.format("创建第%d个项目", position));
		}else{
			holder = (ViewHolder)convertView.getTag();			
		}		
		holder.payCodeTxv.setText(item.getIndex());
		holder.billCodeTxv.setText(item.getBillCode());
		holder.tableTxv.setText(item.getContent());
		holder.personTxv.setText(String.valueOf(item.getPerson()));
		holder.foundingTimeTxv.setText(item.getFoundingTime());
		holder.payBillTimeTxv.setText(item.getPayBillTime());
		holder.moneyTxv.setText(item.getMoney());
		holder.shiftTxv.setText(item.getShift());		
		return convertView;
	}

	private static class ViewHolder{
		public TextView payCodeTxv;				//付款单号
		public TextView billCodeTxv;			//客帐号
		public TextView tableTxv;				//餐台名称
		public TextView personTxv;				//人数
		public TextView foundingTimeTxv;		//开台时间
		public TextView payBillTimeTxv;			//埋单时间
		public TextView moneyTxv;				//金额
		public TextView shiftTxv;				//班次
	}
	
	private class ViewNumber{
		public static final int	ACCOUNT_NUMBER_PAYCODE 			= 0;										//帐目付款单号序号
		public static final int	ACCOUNT_NUMBER_BILLCODE 		= ACCOUNT_NUMBER_PAYCODE  		+ 1;		//帐目客帐号序号
		public static final int	ACCOUNT_NUMBER_TABLE			= ACCOUNT_NUMBER_BILLCODE		+ 1;		//帐目餐台名称序号
		public static final int	ACCOUNT_NUMBER_PERSON 			= ACCOUNT_NUMBER_TABLE 			+ 1;		//帐目人数序号
		public static final int ACCOUNT_NUMBER_FOUNDINGTIME		= ACCOUNT_NUMBER_PERSON			+ 1;		//帐目开台时间序号
		public static final int ACCOUNT_NUMBER_PAYBILLTIME		= ACCOUNT_NUMBER_FOUNDINGTIME 	+ 1;		//帐目埋单时间序号
		public static final int ACCOUNT_NUMBER_MONEY			= ACCOUNT_NUMBER_PAYBILLTIME	+ 1;		//帐目金额序号
		public static final int ACCOUNT_NUMBER_SHIFT			= ACCOUNT_NUMBER_MONEY			+ 1;		//帐目班次序号
	}
}
