/**========================================
 * File:	AbstractOrderAdapter.java
 * Package:	com.hithing.widget
 * Create:	by Leopard
 * Date:	2012-3-13:上午11:18:36
 **======================================*/
package com.hithing.widget;

import java.security.KeyStore.Entry;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

import com.hithing.hsc.component.R;
import com.hithing.hsc.entity.OrderItemKey;
import com.hithing.hsc.entity.OrderItemValue;
import com.hithing.hsc.entity.OrderItemKey.FoodStatus;

import android.content.Context;
import android.content.res.Resources;
import android.renderscript.Sampler.Value;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

/**
 * <p>AbstractOrderAdapter</p>
 * 	
 * <p>功能描述</p>
 *
 * @author Leopard
 * 
 */
public abstract class AbstractOrderAdapter extends BaseAdapter {	
	
	protected LayoutInflater 					inflater;	
	protected List<Integer> 					childIds;																//数据项布局中的子项	
	protected OrderItemKey[] 					keys;																	//键值数组
	protected EnumMap<FoodStatus, Integer>		statusMap;																//订单菜品逻辑状态与实际状态映射(将状态映射为颜色)
	
	private Map<OrderItemKey,OrderItemValue> 	data;																	//账单数据
	private int 								resource;																//数据项布局

	/**
	 * <p>AbstractOrderAdapter</p>
	 *
	 * <p>构造函数</p>
	 */
	public AbstractOrderAdapter(Context context, Map<OrderItemKey, OrderItemValue> data, int resId,
			List<Integer> childIds) {
		inflater  		= (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		resource  		= resId;
		this.data 		= data;
		this.keys 		= data.keySet().toArray(new OrderItemKey[0]);	
		this.childIds 	= childIds;
		
		final Resources resources = context.getResources();
		statusMap = new EnumMap<FoodStatus, Integer>(FoodStatus.class);
		statusMap.put(FoodStatus.FOOD_STATUS_UNDO, resources.getColor(R.color.order_item_undo_background));
		statusMap.put(FoodStatus.FOOD_STATUS_DONE, resources.getColor(R.color.order_item_done_background));
		statusMap.put(FoodStatus.FOOD_STATUS_PRESENT, resources.getColor(R.color.order_item_present_background));
		statusMap.put(FoodStatus.FOOD_STATUS_DISCOUNT, resources.getColor(R.color.order_item_discount_background));
		statusMap.put(FoodStatus.FOOD_STATUS_CANCEL, resources.getColor(R.color.order_item_cancel_background));		
	}

	/* (non-Javadoc)
	 * @see android.widget.Adapter#getCount()
	 */
	public int getCount() {
		return data.size();
	}

	/* (non-Javadoc)
	 * @see android.widget.Adapter#getItem(int)
	 */
	public Object getItem(int position) {		
		return data.get(keys[position]);
	}

	/**
	 * <p>getItemKey</p>
	 *
	 * <p>获得position位置的账单菜品项的键</p>
	 * 
	 * @param position 位置
	 * @return OrderItemKey账单菜品键
	 */
	public OrderItemKey getItemKey(int position){
		return keys[position];
	}
	
	/* (non-Javadoc)
	 * @see android.widget.Adapter#getItemId(int)
	 */
	public long getItemId(int position) {
		
		return keys[position].priceId;
	}

	/* (non-Javadoc)
	 * @see android.widget.BaseAdapter#getItemViewType(int)
	 */
	@Override
	public int getItemViewType(int position) {
		return statusMap.get(keys[position].status);
	}

	/* (non-Javadoc)
	 * @see android.widget.BaseAdapter#getViewTypeCount()
	 */
	@Override
	public int getViewTypeCount() {
		
		return 4;//unfinished code
	}

	/* (non-Javadoc)
	 * @see android.widget.Adapter#getView(int, android.view.View, android.view.ViewGroup)
	 */
	public View getView(int position, View convertView, ViewGroup parent) {
		
		return createViewFromResource(position, convertView, parent, resource);
	}
	
	/**
	 * <p>refresh</p>
	 *
	 * <p>数据更新</p>
	 *
	 */
	public void refresh(){				
		this.keys = data.keySet().toArray(new OrderItemKey[0]);
		this.notifyDataSetChanged();	
				
	}
	
	protected abstract View createViewFromResource(int position, View convertView, ViewGroup parent, int resource);		

}
