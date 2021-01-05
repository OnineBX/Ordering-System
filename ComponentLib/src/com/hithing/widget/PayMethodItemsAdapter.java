/**========================================
 * File:	PayMethodItemsAdapter.java
 * Package:	com.hithing.widget
 * Create:	by Leopard
 * Date:	2012-3-27:下午4:34:04
 **======================================*/
package com.hithing.widget;

import java.util.List;

import android.content.Context;
import android.text.TextWatcher;
import android.view.View;
import android.view.View.OnFocusChangeListener;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;

import com.hithing.hsc.entity.PayMethodValue;

/**
 * <p>PayMethodItemsAdapter</p>
 * 	
 * <p>付款方式项目数据适配器</p>
 *
 * @author Leopard
 * 
 */
public final class PayMethodItemsAdapter extends AbstractOptionalAdapter<PayMethodValue>{
	private List<Integer> 			childIds;
	private OnFocusChangeListener 	listener;	
	
	public PayMethodItemsAdapter(Context context, List<PayMethodValue> data,
			List<PayMethodValue> checkedData, int resource, List<Integer> ids) {
		super(context, data, checkedData, resource);
		childIds = ids;
		if(context instanceof OnFocusChangeListener){
			listener = (OnFocusChangeListener)context;
		}		
	}			

	@Override
	protected View createViewFromResource(int position, View convertView,
			ViewGroup parent, int resource) {
		ViewHolder holder = null;
		PayMethodValue item = (PayMethodValue) getItem(position);
		ViewGroup convertViewGroup;
		if(convertView == null){			
			holder = new ViewHolder();
			convertView = inflater.inflate(resource, parent, false);
			convertViewGroup 	= (ViewGroup)convertView;
			holder.nameChb		= (CheckBox)convertViewGroup.findViewById(childIds.get(ViewNumber.PAY_ITEM_NUMBER_NAME));			
			holder.codeTxv  	= (TextView)convertViewGroup.findViewById(childIds.get(ViewNumber.PAY_ITEM_NUMBER_CODE));
			holder.moneyEdt 	= (EditText)convertViewGroup.findViewById(childIds.get(ViewNumber.PAY_ITEM_NUMBER_MONEY));
			holder.moneyEdt.setOnFocusChangeListener(listener);			
			convertView.setTag(holder);		
		}else{
			holder = (ViewHolder)convertView.getTag();
		}
		holder.nameChb.setText(item.getName());
		holder.nameChb.setChecked(item.isChecked());
		holder.codeTxv.setText(item.getCode());
		holder.moneyEdt.setText(item.getMoney());
		holder.moneyEdt.setEnabled(item.isChecked());
		holder.nameChb.setTag(item.getId());	
		holder.moneyEdt.setTag(item.getId());
		return convertView;
	}

	private class ViewHolder{
		public CheckBox	nameChb;
		public TextView codeTxv;
		public EditText moneyEdt;	
	}
	
	private class ViewNumber{		
		public static final int	PAY_ITEM_NUMBER_NAME 	 = 0;							//付款类型名称序号
		public static final int	PAY_ITEM_NUMBER_CODE 	 = PAY_ITEM_NUMBER_NAME + 1;	//账单菜品数量序号		
		public static final int	PAY_ITEM_NUMBER_MONEY	 = PAY_ITEM_NUMBER_CODE + 1;	//付款金额序号
	}
}
