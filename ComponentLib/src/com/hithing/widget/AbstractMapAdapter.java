/**========================================
 * File:	AbstractMapAdapter.java
 * Package:	com.hithing.widget
 * Create:	by Leopard
 * Date:	2012-3-27:下午4:37:49
 **======================================*/
package com.hithing.widget;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.hithing.hsc.entity.SearchableAdapterItem;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;

/**
 * <p>AbstractMapAdapter</p>
 * 	
 * <p>功能描述</p>
 *
 * @author Leopard
 * 
 */
public abstract class AbstractMapAdapter<K,V extends SearchableAdapterItem> extends BaseAdapter implements Filterable{
	protected LayoutInflater 	inflater;	
	protected List<Integer> 	childIds;
	protected Map<K, V> 		data;
	protected Map<K, V> 		unFilteredData;		//原始数据集合
	protected K[] 				keys;		
	private	  int				resource;
	
	private	  Class<K>			keyClazz;			//键类型
	
	private   Filter			filter;				//过滤器
	/**
	 * <p>AbstractMapAdapter</p>
	 *
	 * <p>Map型数据适配器</p>
	 */
	public AbstractMapAdapter(Context context, Map<K, V> data, Class<K> clazz, int resId,
			List<Integer> childIds) {
		inflater  		= (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		resource  		= resId;
		keyClazz		= clazz;
		this.data 		= data;
		this.keys 		= data.keySet().toArray((K[])Array.newInstance(clazz, 0));		
		this.childIds 	= childIds;		
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
	 * <p>得到数据项键值</p>
	 * 
	 * @param position 数据项位置
	 * @return 数据项键值
	 */
	public K getItemKey(int position){
			return keys[position];
	}
	
	/* (non-Javadoc)
	 * @see android.widget.Adapter#getItemId(int)
	 */
	public long getItemId(int position) {
		
		return Integer.parseInt(getItem(position).toString());
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
		this.keys 		= data.keySet().toArray((K[])Array.newInstance(keyClazz, 0));
		this.notifyDataSetChanged();
	}
	
	/* (non-Javadoc)
	 * @see android.widget.Filterable#getFilter()
	 */
	@Override
	public Filter getFilter() {
		if(filter == null){
			filter = new BaseFilter();
		}
		return filter;
	}
	
	protected abstract View createViewFromResource(int position, View convertView, ViewGroup parent, int resource);
		
	/**
	 * <p>MultiStyleFilter</p>
	 * 	
	 * <p>用于MultiStyleAdapter的数据过滤器</p>
	 *
	 * @author Leopard
	 *
	 */
	private class BaseFilter extends Filter {

		@Override
		protected FilterResults performFiltering(CharSequence constraint) {			
				FilterResults results = new FilterResults();
				
			if(unFilteredData == null){
				unFilteredData = data;
			}

			if (constraint == null || constraint.length() == 0) {								//过滤条件为空则返回全部数据
				Map<K, V> list = unFilteredData;
                results.values = list;
                results.count = list.size();
			}else{
				String constraintString = constraint.toString().toLowerCase();
				Map<K, V> unFilteredValues = unFilteredData;
				Map<K, V> newValues = new LinkedHashMap<K, V>();
				
				Iterator<Entry<K, V>> itor = unFilteredValues.entrySet().iterator();
				Entry<K, V> item;				
				while(itor.hasNext()){
					item = itor.next();					
					if(item.getValue().getIndex().contains(constraintString) ||
					   item.getValue().getContent().contains(constraintString)){
						newValues.put(item.getKey(), item.getValue());
					}									
				}
				results.values = newValues;
				results.count = newValues.size();
			}
			return results;
			
		}

		@SuppressWarnings("unchecked")
		@Override
		protected void publishResults(CharSequence constraint,
				FilterResults results) {

			data = (Map<K, V>) results.values;
			keys = data.keySet().toArray((K[])Array.newInstance(keyClazz, 0));				
			
            if (results.count > 0) {
            	notifyDataSetChanged();
            } else {
                notifyDataSetInvalidated();
            }			
		}
		
	}
}
