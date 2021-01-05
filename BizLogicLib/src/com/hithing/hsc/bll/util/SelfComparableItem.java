/**========================================
 * File:	SelfComparableItem.java
 * Package:	com.hithing.hsc.bll.util
 * Create:	by leo
 * Date:	2012-7-11:下午5:44:30
 **======================================*/
package com.hithing.hsc.bll.util;

/**
 * <p>SelfComparableItem</p>
 * 	
 * <p>可进行自比较的项</p>
 *
 * @author leo
 * 
 */
public class SelfComparableItem <T> {
	private T orgin;
	private T current;
	/**
	 * <p>SelfComparableItem</p>
	 *
	 * <p>构造函数</p>
	 */
	public SelfComparableItem(T value) {
		orgin 	= value;
		current = value;
	}	
	/**
	 * <p>isChanged</p>
	 *
	 * <p>是否发生变化</p>
	 * 
	 * @return 发生变化返回true，未发生变化返回false
	 */
	public boolean isChanged(){		
		return !orgin.equals(current);
	}
	
	public void setValue(T value){
		current = value;
	}
	
	public T getValue(){
		return current;
	}
}
