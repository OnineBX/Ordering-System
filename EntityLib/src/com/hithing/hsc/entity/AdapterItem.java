/**========================================
 * File:	AdapterItem.java
 * Package:	com.hithing.widget
 * Create:	by Leopard
 * Date:	2012-3-28:上午9:39:20
 **======================================*/
package com.hithing.hsc.entity;

import java.io.Serializable;

/**
 * <p>AdapterItem</p>
 * 	
 * <p>用于放置所有的Adapter基类数据项</p>
 *
 * @author Leopard
 * 
 */
public class AdapterItem {

	/**
	 * <p>OptionalItem</p>
	 * 	
	 * <p>选择项类</p>
	 *
	 * @author Leopard
	 *
	 */
	public static class OptionalAdapterItem implements Serializable{
		private static final long serialVersionUID = 352701541742085203L;
		
		protected int 		id;
		protected String 	name;
		protected Boolean 	checked;			
		
		public OptionalAdapterItem(){
			
		}
		
		public OptionalAdapterItem(int id, String name){
			this.id 	 = id;
			this.name 	 = name;
			this.checked = false;
		}
		
		public int getId(){
			return id;
		}
		
		public String getName(){
			return name;
		}
		
		public Boolean isChecked(){
			return checked;
		}
		
		public void setChecked(boolean value){
			checked = value;
		}
		
	}	
}
