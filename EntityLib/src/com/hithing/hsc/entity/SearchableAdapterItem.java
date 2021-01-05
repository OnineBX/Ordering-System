/**========================================
 * File:	SearchableAdapterItem.java
 * Package:	com.hithing.widget
 * Create:	by Leopard
 * Date:	2012-5-9:上午9:24:33
 **======================================*/
package com.hithing.hsc.entity;

import com.hithing.hsc.dataentity.FoodEntity;

import android.graphics.Bitmap;
import android.widget.ImageView;

/**
 * <p>SearchableAdapterItem</p>
 * 	
 * <p>可检索的适配器数据项</p>
 *
 * @author Leopard
 * 
 */
public class SearchableAdapterItem {
	private final int	DEFAULT_TYPE = 0;	//默认类别，如果数据项没有用于检索的类别，则设置为该值

	private String 		content;			//数据项内容,用于显示和检索
	private String 		index;				//数据项索引,用于检索
	private int 		type;				//数据项类别，用于检索
	
	/**
	 * <p>SearchableAdapterItem</p>
	 *
	 * <p>构造函数</p>
	 */
	public SearchableAdapterItem(){
		
	}
	
	/**
	 * <p>SearchableAdapterItem</p>
	 *
	 * <p>构造函数</p>
	 */
	public SearchableAdapterItem(String content, String index, int type) {		
		this.content 	= content;
		this.index		= index;
		this.type		= type;
	}

	public SearchableAdapterItem(String content, String index){
		this.content	= content;
		this.index		= index;
		this.type		= DEFAULT_TYPE;
	}
	
	public String getContent(){
		return content;
	}
	
	public String getIndex(){
		return index;
	}
	
	public int getType(){
		return type;
	}
	
	/**
	 * <p>StyledAdapterItem</p>
	 * 	
	 * <p>类型适配器数据项类</p>
	 *
	 * 用于MultiStyleAdapter的数据项
	 * @author Leopard
	 *
	 */
	public static class StyledAdapterItem extends SearchableAdapterItem{
						
		protected int 		style;				//数据项风格		
		
		public StyledAdapterItem(int type , int style , String content, String index){
			super(content, index, type);			
			this.style 		= style;			
		}				
		
		public void setStyle(int style){
			this.style = style;
		}		
		
		public int getStyle(){
			return style;
		}					
				
	}
	
	/**
	 * <p>FoodAdapterItem</p>
	 * 	
	 * <p>菜品适配器数据项</p>
	 *
	 * 用于在AdapterView中显示数据项，包括菜品名称，菜品检索码，菜品类别，菜品价格，菜品图片名称
	 * @author leo
	 *
	 */
	public static class FoodAdapterItem extends StyledAdapterItem{
		private String price;
		private String photoName;
		
//		public FoodAdapterItem(int type , String price , String content, String index){
//			super(type, type, content, index);
//			this.price		= price;
//			this.photoName	= null;
//		}
		
		public FoodAdapterItem(FoodEntity ent, int style, String price){
			super(ent.getFoodSubSort().getId(), style, ent.getName(), ent.getIndex());
			this.photoName 	= ent.getPhotoName();
			this.price		= price;
		}		
		
		public String getPhotoName(){
			return photoName;
			
		}
		
		public String getPrice(){
			return price;
		}
	}
}
