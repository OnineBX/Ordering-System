/**========================================
 * File:	OrderDataHelper.java
 * Package:	com.hithing.hsc.bll.control
 * Create:	by Leopard
 * Date:	2012-5-7:上午9:48:28
 **======================================*/
package com.hithing.hsc.bll.util;

import java.util.EnumMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.hithing.hsc.bll.control.OfferCtrl.OfferFactorsKey;
import com.hithing.hsc.entity.MoMsg.CMsgBillPricingReq.CMsgOrdpriceInfo;
import com.hithing.hsc.entity.OrderItemKey;
import com.hithing.hsc.entity.OrderItemValue;

/**
 * <p>OrderDataHelper</p>
 * 	
 * <p>用于管理账单数据</p>
 *
 * @author Leopard
 * 
 */
public enum BillDataCache {
	INSTANCE;
	
	private Map<OrderItemKey, OrderItemValue> 		orderItems;					//账单菜品数据
	private Map<OrderItemKey, OrderItemValue>		unOrderItems;				//未下单菜品数据
	private Map<OrderItemKey, OrderItemValue>		offeredItems;				//定价菜品数据
	private EnumMap<OfferFactorsKey, String>		offeredFactors;				//账单定价因子数据
		
	private BillDataCache() {
		orderItems 		= new LinkedHashMap<OrderItemKey, OrderItemValue>();
		unOrderItems 	= new LinkedHashMap<OrderItemKey, OrderItemValue>();
		offeredItems	= new LinkedHashMap<OrderItemKey, OrderItemValue>();
		offeredFactors	= new EnumMap<OfferFactorsKey, String>(OfferFactorsKey.class);
		
	}	
	
	public Map<OrderItemKey, OrderItemValue> getOrderItems(){
		return orderItems;
	}
	
	public Map<OrderItemKey, OrderItemValue> getUnOrderItems(){
		return unOrderItems;
	}
	
	public Map<OrderItemKey, OrderItemValue> getOfferItems(){
		return offeredItems;
	}
	
	public EnumMap<OfferFactorsKey, String> getOfferedFactors(){
		return offeredFactors;
	}
	
}
