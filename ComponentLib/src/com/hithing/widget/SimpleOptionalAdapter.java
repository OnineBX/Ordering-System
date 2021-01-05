/**========================================
 * File:	SimpleOptionalAdapter.java
 * Package:	com.hithing.widget
 * Create:	by Leopard
 * Date:	2012-1-12:上午10:53:10
 **======================================*/
package com.hithing.widget;

import java.util.LinkedList;
import java.util.List;
import java.util.zip.Checksum;

import com.hithing.hsc.component.R;
import com.hithing.hsc.entity.AdapterItem.OptionalAdapterItem;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CompoundButton;

/**
 * <p>SimpleOptionalAdapter</p>
 * 	
 * <p>功能描述</p>
 *
 * @author Leopard
 * 
 */
public final class SimpleOptionalAdapter extends AbstractOptionalAdapter<OptionalAdapterItem> {			
	
	public SimpleOptionalAdapter(Context context, List<OptionalAdapterItem> data,
			List<OptionalAdapterItem> checkedData, int resource) {
		super(context, data, checkedData, resource);
	}		

	@Override
	protected View createViewFromResource(int position, View convertView,
			ViewGroup parent, int resource) {
		ViewHolder holder = null;
		OptionalAdapterItem item = (OptionalAdapterItem)getItem(position);
		if(convertView == null){
			holder = new ViewHolder();
			convertView = inflater.inflate(resource, parent, false);			
			holder.b = (CompoundButton)convertView;			
			convertView.setTag(R.id.optional_item_tag_holder,holder);					
			Log.d("GetView", String.format("创建第%d个项目", position));
		}else{
			holder = (ViewHolder)convertView.getTag(R.id.optional_item_tag_holder);
			Log.d("GetView", String.format("重用第%d个项目", position));
		}
		holder.b.setText(item.getName());
		holder.b.setId(item.getId());
		holder.b.setChecked(item.isChecked());
		holder.b.setTag(R.id.optional_item_tag_position, position);
		return convertView;
	}		

	private class ViewHolder{
		public CompoundButton b;
	}
}
