/**========================================
 * File:	OfferItemsAdapter.java
 * Package:	com.hithing.widget
 * Create:	by Leopard
 * Date:	2012-2-24:下午5:18:18
 **======================================*/
package com.hithing.widget;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;

import junit.framework.Test;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.hithing.hsc.entity.OrderItemKey;
import com.hithing.hsc.entity.OrderItemValue;
import com.hithing.hsc.entity.OrderItemKey.FoodStatus;

/**
 * <p>OfferItemsAdapter</p>
 * 	
 * <p>用于账单折价的适配器</p>
 *
 * @author Leopard
 * 
 */
public class OfferItemsAdapter extends AbstractOrderAdapter {

	/**
	 * <p>OfferItemsAdapter</p>
	 *
	 * <p>构造函数</p>
	 * @param context
	 * @param data
	 * @param resId
	 * @param childIds
	 * @param statusMap
	 */
	public OfferItemsAdapter(Context context,
			Map<OrderItemKey, OrderItemValue> data, int resId,
			List<Integer> childIds) {
		super(context, data, resId, childIds);
	
	}

	@Override
	protected View createViewFromResource(int position, View convertView,
			ViewGroup parent, int resource) {
		ViewHolder holder = null;		
		OrderItemValue item = (OrderItemValue)getItem(position);
		OrderItemKey 	key = keys[position];
		ViewGroup convertViewGroup = null;
		if(convertView == null){			
			holder = new ViewHolder();
			convertView = inflater.inflate(resource, parent, false);
			convertViewGroup 		= (ViewGroup)convertView;			
			holder.nameTxv  	= (TextView)convertViewGroup.findViewById(childIds.get(ViewNumber.OFFER_ITEM_NUMBER_NAME));
			holder.countTxv 	= (TextView)convertViewGroup.findViewById(childIds.get(ViewNumber.OFFER_ITEM_NUMBER_COUNT));			
			holder.priceTxv			= (TextView)convertViewGroup.findViewById(childIds.get(ViewNumber.OFFER_ITEM_NUMBER_PRICE));
			holder.preDiscountTxv	= (TextView)convertViewGroup.findViewById(childIds.get(ViewNumber.OFFER_ITEM_NUMBER_PREDISCOUNT));
			holder.ratioDiscountTxv = (TextView)convertViewGroup.findViewById(childIds.get(ViewNumber.OFFER_ITEM_NUMBER_RATIODISCOUNT));
			holder.moneyDiscountTxv = (TextView)convertViewGroup.findViewById(childIds.get(ViewNumber.OFFER_ITEM_NUMBER_MONEYDISCOUNT));
			holder.postDiscountTxv	= (TextView)convertViewGroup.findViewById(childIds.get(ViewNumber.OFFER_ITEM_NUMBER_POSTDISCOUNT));
			convertView.setTag(holder);		
		}else{
			holder = (ViewHolder)convertView.getTag();
		}
		holder.nameTxv.setText(item.getFood().content);
		holder.countTxv.setText(item.getCountWithUnit());		
		holder.priceTxv.setText(item.getFoodPrice());
		holder.preDiscountTxv.setText(item.getApplyCost());
		holder.ratioDiscountTxv.setText(item.getRatioDiscount());
		holder.moneyDiscountTxv.setText(item.getMoneyDiscount());
		holder.postDiscountTxv.setText(item.getRealCost());		
		convertView.setTag(position);
		convertView.setBackgroundColor(statusMap.get(key.status));
		return convertView;
	}
	
	private class ViewHolder{
		public TextView nameTxv;
		public TextView countTxv;	
		public TextView priceTxv;
		public TextView preDiscountTxv;
		public TextView ratioDiscountTxv;
		public TextView moneyDiscountTxv;
		public TextView postDiscountTxv;
	}
	
	private class ViewNumber{		
		public static final int	OFFER_ITEM_NUMBER_NAME 			= 0;											//账单菜品名称序号
		public static final int	OFFER_ITEM_NUMBER_COUNT 		= OFFER_ITEM_NUMBER_NAME  		 		+ 1;	//账单菜品数量序号
		public static final int OFFER_ITEM_NUMBER_PRICE			= OFFER_ITEM_NUMBER_COUNT				+ 1;	//账单菜品单价序号
		public static final int OFFER_ITEM_NUMBER_PREDISCOUNT	= OFFER_ITEM_NUMBER_PRICE				+ 1;	//账单菜品折前金额序号
		public static final int OFFER_ITEM_NUMBER_RATIODISCOUNT = OFFER_ITEM_NUMBER_PREDISCOUNT			+ 1;	//账单菜品折扣率序号
		public static final int OFFER_ITEM_NUMBER_MONEYDISCOUNT = OFFER_ITEM_NUMBER_RATIODISCOUNT		+ 1;	//账单菜品金额折序号
		public static final int OFFER_ITEM_NUMBER_POSTDISCOUNT	= OFFER_ITEM_NUMBER_MONEYDISCOUNT		+ 1;	//账单菜品折后金额序号
	}

}
