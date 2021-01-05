/**========================================
 * File:	AbstractOptionalAdapter.java
 * Package:	com.hithing.widget
 * Create:	by Leopard
 * Date:	2012-3-28:上午9:20:50
 **======================================*/
package com.hithing.widget;

import java.util.List;

import com.hithing.hsc.entity.AdapterItem.OptionalAdapterItem;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

/**
 * <p>AbstractOptionalAdapter</p>
 * 	
 * <p>功能描述</p>
 *
 * @author Leopard
 * 
 */
public abstract class AbstractOptionalAdapter<T extends OptionalAdapterItem> extends BaseAdapter {
	protected LayoutInflater 	inflater;
	
	private List<T> 			data;
	private List<T> 			checkedData;				//选中的数据
		
	private int 				resource;					//数据项布局
	
	private Checker				checker;
	/**
	 * <p>AbstractOptionalAdapter</p>
	 *
	 * <p>构造函数</p>
	 */
	public AbstractOptionalAdapter(Context context, List<T> data, List<T> checkedData, int resource) {
		this.inflater 		= (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);		
		this.data 			= data;		
		this.checkedData 	= checkedData;
		this.resource 		= resource;
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
		return data.get(position);
	}

	/* (non-Javadoc)
	 * @see android.widget.Adapter#getItemId(int)
	 */
	public long getItemId(int position) {
		return data.get(position).getId();
	}

	public List<T> getCheckedData(){
		return checkedData;
	}
	
	public Checker getChecker(CheckType type){
		if(checker == null){
			switch (type) {
			case CHECK_TYPE_SINGLECHECK:
				checker = new SingleChecker();
				break;
			case CHECK_TYPE_MULTICHECK:
				checker = new MultiChecker();
				break;			
			}
			
		}
		return checker;
	}
	
	/* (non-Javadoc)
	 * @see android.widget.Adapter#getView(int, android.view.View, android.view.ViewGroup)
	 */
	public View getView(int position, View convertView, ViewGroup parent) {
		
		return createViewFromResource(position, convertView, parent, resource);
	}

	protected abstract View createViewFromResource(int position, View convertView, ViewGroup parent, int resource);
	
	/**
	 * <p>Checker</p>
	 * 	
	 * <p>标记类,用于为CompoundButton类控件执行标记操作</p>
	 *
	 * @author Leopard
	 *
	 */
	public abstract class Checker{
		
		public Checker(){
			
		}
		/**
		 * <p>check</p>
		 *
		 * <p>功能描述</p>
		 * 
		 * @param position 标记位置
		 * @param checked  标记结果
		 */
		public abstract void check(int position, boolean checked);	
		/**
		 * <p>check</p>
		 *
		 * <p>功能描述</p>
		 * 
		 * @param position 标记位置
		 * @param checked  标记结果
		 * @param isMovable 是否从标记数据集合中移除或添加
		 */
		public abstract void check(int position, boolean checked, boolean isMovable);
	}
	
	public enum CheckType{CHECK_TYPE_SINGLECHECK,CHECK_TYPE_MULTICHECK};
	
	private final class SingleChecker extends Checker{
		private final int FIRST_ELEMENT_POSITION = 0;
		
		@Override
		public void check(int position, boolean checked) {						
			check(position, checked, true);
		}
		
		@Override
		public void check(int position, boolean checked, boolean isMovable) {
			T item = data.get(position);			
			
			if(!checkedData.isEmpty()){
				checkedData.get(FIRST_ELEMENT_POSITION).setChecked(false);
				if(isMovable){
					checkedData.remove(FIRST_ELEMENT_POSITION);
				}
			}			
			item.setChecked(true);
			if(isMovable){
				checkedData.add(item);
			}				
			
			notifyDataSetChanged();
			
		}
		
	}
	
	private final class MultiChecker extends Checker{

		@Override
		public void check(int position, boolean checked) {
			check(position, checked, true);		
		}

		@Override
		public void check(int position, boolean checked, boolean isMovable) {
			T item = data.get(position);			
			item.setChecked(checked);
			if(isMovable){
				if(!checked){					
					checkedData.remove(item);
				}else{				
					checkedData.add(item);			
				}	
			}
													
			notifyDataSetChanged();	
			
		}
		
	}	
	
}
