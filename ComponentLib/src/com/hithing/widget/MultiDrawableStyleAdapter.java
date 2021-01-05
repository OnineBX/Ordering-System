package com.hithing.widget;

import java.util.Map;

import com.hithing.hsc.entity.SearchableAdapterItem.StyledAdapterItem;

import android.content.Context;
import android.content.res.Resources;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

/**
 * <p>MultiDrawableStyleAdapter</p>
 * 	
 * <p>多背景图风格数据项适配器</p>
 *
 * 用于绑定数据到AdapterView，使该AdapterView显示不同背景图风格的数据项
 *
 * @author wxm
 * 
 */
public class MultiDrawableStyleAdapter extends AbsMultiStyleAdapter {	
	
	/**
	 * <p>MultiDrawableStyleAdapter</p>
	 *
	 * <p>构造函数</p>
	 * @param context
	 * @param data
	 * @param resId
	 */
	public MultiDrawableStyleAdapter(Context context,
			Map<Integer, StyledAdapterItem> data, int resId) {
		super(context, data, resId);		
	}			

	@Override
	protected void refreshViewStyle(View sourceView, int style) {
		if(sourceView instanceof TextView){
			((TextView)sourceView).setCompoundDrawablesWithIntrinsicBounds(0, style, 0, 0);
		}
	}

	@Override
	protected View createViewFromResource(int position, View convertView,
			ViewGroup parent, int resource) {
		// TODO Auto-generated method stub
		return null;
	}
}
