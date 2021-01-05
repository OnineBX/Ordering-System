/**========================================
 * File:	MultiColorStyleAdapter.java
 * Package:	com.hithing.widget
 * Create:	by Leopard
 * Date:	2012-4-24:上午11:16:34
 **======================================*/
package com.hithing.widget;

import java.util.Map;

import com.hithing.hsc.entity.SearchableAdapterItem.StyledAdapterItem;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

/**
 * <p>MultiColorStyleAdapter</p>
 * 	
 * <p>多种颜色风格的适配器,为AdapterView提供不同颜色风格的数据项</p>
 *
 * @author Leopard
 * 
 */
public final class MultiColorStyleAdapter extends AbsMultiStyleAdapter<StyledAdapterItem> {

	/**
	 * <p>MultiColorStyleAdapter</p>
	 *
	 * <p>构造函数</p>
	 * @param context Apk上下文
	 * @param data	数据集合
	 * @param resId	数据项布局
	 */
	public MultiColorStyleAdapter(Context context,
			Map<Integer, StyledAdapterItem> data, int resId) {
		super(context, data, resId);
		// TODO Auto-generated constructor stub
	}

	@Override
	protected void refreshViewStyle(View sourceView, int style) {
		sourceView.setBackgroundColor(style);
	}

	/* (non-Javadoc)
	 * @see com.hithing.widget.AbsMultiStyleAdapter#createViewFromResource(int, android.view.View, android.view.ViewGroup, int)
	 */
	@Override
	protected View createViewFromResource(int position, View convertView,
			ViewGroup parent, int resource) {
		ViewHolder holder = null;
		StyledAdapterItem item = (StyledAdapterItem)getItem(position);
		if(convertView == null){
			holder = new ViewHolder();
			convertView = inflater.inflate(resource, parent, false);
			holder.t = (TextView)convertView;
			convertView.setTag(holder);
			convertView.setId(item.getStyle());			
		}else{
			holder = (ViewHolder)convertView.getTag();
		}
		holder.t.setText(item.getContent());
		return convertView;
	}

	private static class ViewHolder{
		public TextView t;
	}
}
