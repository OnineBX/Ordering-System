/**========================================
 * File:	CashItemsAdapter.java
 * Package:	com.hithing.widget
 * Create:	by Leopard
 * Date:	2012-3-16:下午2:24:03
 **======================================*/
package com.hithing.widget;

import java.util.List;
import java.util.Map;

import com.hithing.hsc.entity.CashItemValue;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

/**
 * <p>CashItemsAdapter</p>
 * 	
 * <p>收银条目适配器</p>
 *
 * @author Leopard
 * 
 */
public final class CashItemsAdapter extends AbstractMapAdapter<Integer, CashItemValue> {

	/**
	 * <p>CashItemsAdapter</p>
	 *
	 * <p>构造函数</p>
	 * @param context
	 * @param data
	 * @param resId
	 * @param childIds
	 */
	public CashItemsAdapter(Context context,
			Map<Integer, CashItemValue> data, int resId,
			List<Integer> childIds) {
		super(context, data, Integer.class, resId, childIds);		
	}

	@Override
	protected View createViewFromResource(int position, View convertView,
			ViewGroup parent, int resource) {
		ViewHolder holder = null;
		CashItemValue item = (CashItemValue)getItem(position);
		ViewGroup convertViewGroup;
		if(convertView == null){			
			holder = new ViewHolder();
			convertView = inflater.inflate(resource, parent, false);
			convertViewGroup 	= (ViewGroup)convertView;			
			holder.codeTxv  	= (TextView)convertViewGroup.findViewById(childIds.get(ViewNumber.OFFER_ITEM_NUMBER_CODE));
			holder.nameTxv 		= (TextView)convertViewGroup.findViewById(childIds.get(ViewNumber.OFFER_ITEM_NUMBER_NAME));						
			convertView.setTag(holder);		
		}else{
			holder = (ViewHolder)convertView.getTag();
		}
		holder.codeTxv.setText(item.getTableCode());
		holder.nameTxv.setText(item.getTableName());				
		return convertView;
	}
	
	private class ViewHolder{
		public TextView codeTxv;
		public TextView nameTxv;	
	}
	
	private class ViewNumber{		
		public static final int	OFFER_ITEM_NUMBER_CODE 	= 0;							//账单菜品名称序号
		public static final int	OFFER_ITEM_NUMBER_NAME 	= OFFER_ITEM_NUMBER_CODE + 1;	//账单菜品数量序号
	}
}
