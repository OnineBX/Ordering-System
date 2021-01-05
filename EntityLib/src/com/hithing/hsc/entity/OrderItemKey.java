/**========================================
 * File:	OrderItemKey.java
 * Package:	com.hithing.hsc.entity
 * Create:	by Leopard
 * Date:	2012-1-13:下午8:06:15
 **======================================*/
package com.hithing.hsc.entity;

import java.io.Serializable;

import android.R.integer;

/**
 * <p>OrderItemKey</p>
 * 	
 * <p>订单项目键类,用于区分订单项目</p>
 *
 * @author Leopard
 *
 */
public final class OrderItemKey implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 6896818191146797045L;

	private static final int DEFAULT_SERIAL_ID = -1;
	
	public FoodStatus 			status;				//菜品状态:用颜色表示
	public int 					priceId;			//菜品单价标识:一个菜品因计价单位不同可能拥有多个单价
	public int 					serialId;			//菜品所属的落单标识
	
	/**
	 * <p>OrderItemKey</p>
	 *
	 * <p>构造函数</p>
	 * @param status  账单菜品状态
	 * @param priceId 账单菜品价格标识
	 */
	public OrderItemKey(FoodStatus status, int priceId){
		this.status 	= status;
		this.priceId 	= priceId;
		this.serialId	= DEFAULT_SERIAL_ID;
	}
	
	/**
	 * <p>OrderItemKey</p>
	 *
	 * <p>构造函数</p>
	 * @param status   账单菜品状态
	 * @param priceId  账单菜品价格标识
	 * @param serialId 账单菜品所属的账单流水号
	 */
	public OrderItemKey(FoodStatus status, int priceId, int serialId){
		this.status 	= status;
		this.priceId 	= priceId;
		this.serialId	= serialId;
	}	
	
	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object o) {
		if(o == this){
			return true;
		}
		if(!(o instanceof OrderItemKey)){
			return false;
		}
		
		OrderItemKey key = (OrderItemKey)o;
		return (key.status == status) && (key.priceId == priceId) && (key.serialId == serialId);
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		int result = 17;
		result = 31 * result + status.hashCode();
		result = 31 * result + priceId;
		result = 31 * result + serialId;
		return result;
	}
	
	/**
	 * <p>FoodStatus</p>
	 * 	
	 * <p>菜品状态枚举类型</p>
	 *
	 * @author Leopard
	 *
	 */
	public enum FoodStatus{
		FOOD_STATUS_UNDO(0)	,			//未落状态
		FOOD_STATUS_DONE(1)	,			//已落状态
		FOOD_STATUS_PRESENT(2)	,		//赠送状态
		FOOD_STATUS_CANCEL(3),			//取消状态
		FOOD_STATUS_DISCOUNT(4);		//打折状态
		
		private int value;
		private FoodStatus(int value) {
			this.value = value;
		}
		public int getValue(){
			return value;
		}
		
		public static FoodStatus valueOf(int value){
			switch (value) {
			case 0:
				return FOOD_STATUS_UNDO;				
			case 1:
				return FOOD_STATUS_DONE;
			case 2:
				return FOOD_STATUS_PRESENT;
			case 3:
				return FOOD_STATUS_CANCEL;
			case 4:
				return FOOD_STATUS_DISCOUNT;
			default:
					return null;
			}
		}
	};
}		
