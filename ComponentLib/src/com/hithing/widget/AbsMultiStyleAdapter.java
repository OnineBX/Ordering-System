/**========================================
 * File:	AbsMultiStyleAdapter.java
 * Package:	com.hithing.widget
 * Create:	by Leopard
 * Date:	2012-4-24:上午10:06:22
 **======================================*/
package com.hithing.widget;

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;

import com.hithing.hsc.entity.SearchableAdapterItem.StyledAdapterItem;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

/**
 * <p>AbsMultiStyleAdapter</p>
 * 	
 * <p>功能描述</p>
 *
 * @author Leopard
 * 
 */
public abstract class AbsMultiStyleAdapter<T extends StyledAdapterItem> extends
		AbstractMapAdapter<Integer, T> implements Filterable {
	protected Map<Integer, T> 							unFilteredData;		//原始数据集合
	private MultiStyleFilter							filter;				//过滤器
	private FilterMode									filterMode;			//过滤模式
	
	public AbsMultiStyleAdapter(Context context,
			Map<Integer, T> data, int resId) {
		super(context, data, Integer.class, resId, null);			
	}

	/* (non-Javadoc)
	 * @see com.hithing.widget.AbstractMapAdapter#getItemId(int)
	 */
	@Override
	public long getItemId(int position) {		
		return keys[position];
	}

	/* (non-Javadoc)
	 * @see android.widget.BaseAdapter#getItemViewType(int)
	 */
	@Override
	public int getItemViewType(int position) {		
		return data.get(keys[position]).getStyle();
	}		

	
	
	/* (non-Javadoc)
	 * @see com.hithing.widget.AbstractMapAdapter#getView(int, android.view.View, android.view.ViewGroup)
	 */
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View view = super.getView(position, convertView, parent);
		int style = getItemViewType(position);
		refreshViewStyle(view, style);
		return view;
	}

	//刷新视图类型方法
	protected abstract void refreshViewStyle(View sourceView, int style);

	/**
	 * <p>setItem</p>
	 *
	 * <p>功能描述</p>
	 * 
	 * @param key
	 * @param style
	 */
	public void setItem(int key , int style){		
		T item = unFilteredData.get(key);
		if(item != null){
			item.setStyle(style);
			unFilteredData.put(key, item);
		}	
						
	}
	
	/* (non-Javadoc)
	 * @see android.widget.Filterable#getFilter()
	 */
	@Override
	public Filter getFilter() {
		if(filter == null){
			filter = new MultiStyleFilter();
		}
		return filter;
	}

	/**
	 * <p>setFilterMode</p>
	 *
	 * <p>设置过滤模式</p>
	 * 
	 * @param mode
	 */
	public void setFilterMode(FilterMode mode){
		filterMode = mode;
	}		
	
	
	
	public enum FilterMode {Style , Type , Index}
	
	/**
	 * <p>MultiStyleFilter</p>
	 * 	
	 * <p>用于MultiStyleAdapter的数据过滤器</p>
	 *
	 * @author Leopard
	 *
	 */
	private class MultiStyleFilter extends Filter {

		@Override
		protected FilterResults performFiltering(CharSequence constraint) {			
				FilterResults results = new FilterResults();
				
			if(unFilteredData == null){
				unFilteredData = data;
			}

			if (constraint == null || constraint.length() == 0) {								//过滤条件为空则返回全部数据
				Map<Integer, T> list = unFilteredData;
                results.values = list;
                results.count = list.size();
			}else{
				String constraintString = constraint.toString().toLowerCase();
				Map<Integer, T> unFilteredValues = unFilteredData;
				Map<Integer, T> newValues = new LinkedHashMap<Integer, T>();
				
				Iterator<Entry<Integer, T>> itor = unFilteredValues.entrySet().iterator();
				Entry<Integer, T> item;				
				while(itor.hasNext()){
					item = itor.next();
					switch (filterMode) {
					case Style:
						if(item.getValue().getStyle() == Integer.valueOf(constraintString)){
							newValues.put(item.getKey(), item.getValue());
						}
						break;
					case Type:
						if(item.getValue().getType() == Integer.valueOf(constraintString)){
							newValues.put(item.getKey(), item.getValue());
						}					
						break;
					case Index:
						if(item.getValue().getIndex().contains(constraintString) ||
						   item.getValue().getContent().contains(constraintString)){
							newValues.put(item.getKey(), item.getValue());
						}
						break;
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

			data = (Map<Integer, T>) results.values;
			keys = data.keySet().toArray(new Integer[0]);				
			
            if (results.count > 0) {
            	notifyDataSetChanged();
            } else {
                notifyDataSetInvalidated();
            }			
		}
		
	}
	
}
