/**========================================
 * File:	OrderItemsAdapter.java
 * Package:	com.hithing.widget
 * Create:	by Leopard
 * Date:	2012-1-6:上午11:01:01
 **======================================*/
package com.hithing.widget;

import java.util.List;
import java.util.Map;

import com.hithing.hsc.entity.OrderItemKey;
import com.hithing.hsc.entity.OrderItemValue;


import android.content.Context;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

/**
 * <p>OrderItemsAdapter</p>
 * 	
 * <p>点单条目数据适配器</p>
 *
 * @author Leopard
 * 
 */
public class OrderItemsAdapter extends AbstractOrderAdapter {	
	/**
	 * <p>OrderItemsAdapter</p>
	 *
	 * <p>构造函数</p>
	 */
	public OrderItemsAdapter(Context context, Map<OrderItemKey, OrderItemValue> data, int resId,
			List<Integer> childIds) {
		super(context, data, resId, childIds);		
	}

	@Override
	protected View createViewFromResource(int position, View convertView,
			ViewGroup parent, int resource) {
		ViewHolder holder = null;
		OrderItemValue item = (OrderItemValue)getItem(position);		
		OrderItemKey 	key = keys[position];
		ViewGroup convertViewGroup;
		if(convertView == null){			
			holder = new ViewHolder();
			convertView = inflater.inflate(resource, parent, false);
			convertViewGroup 		  = (ViewGroup)convertView;
			holder.foodNameTxv  	  = (TextView)convertViewGroup.findViewById(childIds.get(ViewNumber.ORDER_ITEM_NUMBER_NAME));
			holder.foodCountTxv 	  = (TextView)convertViewGroup.findViewById(childIds.get(ViewNumber.ORDER_ITEM_NUMBER_COUNT));			
			holder.foodCostTxv 		  = (TextView)convertViewGroup.findViewById(childIds.get(ViewNumber.ORDER_ITEM_NUMBER_COST));
			holder.foodDescriptionTxv = (TextView)convertViewGroup.findViewById(childIds.get(ViewNumber.ORDER_ITEM_NUMBER_DESCRIPTION));				
			convertView.setTag(holder);		
//			Log.d("GetView", String.format("创建第%d个项目", position));
		}else{
			holder = (ViewHolder)convertView.getTag();			
		}		
//		Log.e("OrderItemAdapter.createViewFromResource", "position=" + position);
		
		holder.foodNameTxv.setText(item.getFood().content);
		holder.foodCountTxv.setText(item.getCountWithUnit());		
		holder.foodCostTxv.setText(item.getApplyCost());
		holder.foodDescriptionTxv.setText(item.getDescription());				
		holder.foodCountTxv.setTag(position);
		convertView.setBackgroundColor(statusMap.get(key.status));		
		return convertView;
	}
	
	private class ViewHolder{
		public TextView foodNameTxv;
		public TextView foodCountTxv;		
		public TextView foodCostTxv;
		public TextView foodDescriptionTxv;		
	}
	
	private class ViewNumber{
		public static final int	ORDER_ITEM_NUMBER_NAME 			= 0;									//账单菜品名称序号
		public static final int	ORDER_ITEM_NUMBER_COUNT 		= ORDER_ITEM_NUMBER_NAME  	+ 1;		//账单菜品数量序号	
		public static final int	ORDER_ITEM_NUMBER_COST			= ORDER_ITEM_NUMBER_COUNT	+ 1;		//账单菜品总价序号
		public static final int	ORDER_ITEM_NUMBER_DESCRIPTION 	= ORDER_ITEM_NUMBER_COST 	+ 1;		//账单菜品备注序号
	}
}
