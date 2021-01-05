/**========================================
 * File:	OfferCtrl.java
 * Package:	com.hithing.hsc.bll.control
 * Create:	by Leopard
 * Date:	2012-5-7:上午11:38:51
 **======================================*/
package com.hithing.hsc.bll.control;


import java.io.IOException;
import java.math.BigDecimal;
import java.net.UnknownHostException;
import java.util.EnumMap;
import java.util.EnumSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;


import android.util.Log;

import com.hithing.hsc.bll.manage.FoodManager;
import com.hithing.hsc.bll.util.BillDataCache;
import com.hithing.hsc.bll.util.MomsgFactory;
import com.hithing.hsc.bll.util.SelfComparableItem;
import com.hithing.hsc.bll.util.MomsgFactory.MomsgType;
import com.hithing.hsc.entity.MoMsg.CMsgBillPricingReq;
import com.hithing.hsc.entity.MoMsg.CMsgBillPricingReq.CMsgBillElement;
import com.hithing.hsc.entity.MoMsg.CMsgBillPricingReq.CMsgOrdpriceInfo;
import com.hithing.hsc.entity.OrderItemKey;
import com.hithing.hsc.entity.OrderItemValue;
import com.hithing.hsc.entity.OrderItemKey.FoodStatus;
import com.hithing.hsc.entity.OrderItemValue.CalculateConst;

/**
 * <p>OfferCtrl</p>
 * 	
 * <p>菜品账单价格控制类,用于完成各种与价格操作有关的操作</p>
 *
 * @author Leopard
 * 
 */
public final class OfferCtrl extends FoodCtrl{	
	private Map<OrderItemKey, OrderItemValue> 	offeritems;					//定价的菜品集合
	private EnumMap<OfferFactorsKey, String>	offerFactors;				//定价的因子集合
	
	private SelfComparableItem<String>			service;					//服务费率
	private SelfComparableItem<String> 			lowest;						//最低消费
	private SelfComparableItem<String>			discountRatio;				//折扣率
	private SelfComparableItem<String>			discountMoney;				//折扣金额
	private SelfComparableItem<DiscountMode>	orderDiscountMode;			//账单折扣类型										
	/**
	 * <p>OfferCtrl</p>
	 *
	 * <p>构造函数</p>
	 */
	public OfferCtrl(int tableId, FoodManager foodMgr) {
		super(tableId, foodMgr);	
		offeritems 			= BillDataCache.INSTANCE.getOfferItems();
		offerFactors		= BillDataCache.INSTANCE.getOfferedFactors();
		
//		service 			= new SelfComparableItem<String>(CalculateConst.DECIMAL_ZERO_STRING);									
//		lowest 				= new SelfComparableItem<String>(CalculateConst.DECIMAL_ZERO_STRING);										
//		discountRatio 		= new SelfComparableItem<String>(CalculateConst.DECIMAL_ONE_STRING);								
//		discountMoney 		= new SelfComparableItem<String>(CalculateConst.DECIMAL_ZERO_STRING);			
//		orderDiscountMode	= new SelfComparableItem<DiscountMode>(DiscountMode.ALL_WITH_EXCEPTION);
		resetData();
	}	
	
	/**
	 * <p>offerBill</p>
	 *
	 * <p>菜单定价</p>
	 * 保存进行过打折、赠送、取消、修改单价等的菜品数据
	 * 
	 * @param authCode 授权码
	 */
	public void offerBill(final int authCode){
		executor.execute(new Runnable() {
			
			@Override
			public void run() {
				try {
					CMsgBillPricingReq.Builder 	builder 	= MomsgFactory.createMomsgBuilder(MomsgType.BILLPRICING_REQ);
					
					CMsgOrdpriceInfo.Builder 	priceBuilder;
					OrderItemKey 				key;
					OrderItemValue 				value;
					for (Entry<OrderItemKey, OrderItemValue> entry : offeritems.entrySet()) {
						key = entry.getKey();
						value = entry.getValue();
						priceBuilder = CMsgOrdpriceInfo.newBuilder().setOrderId(key.serialId).
																	 setFoodpriceId(key.priceId).
																	 setOrdCount(value.getCount()).
																	 setOrdPrice(value.getFoodPrice()).
																	 setDisRadio(value.getRatioDiscount()).
																	 setDisMoney(value.getMoneyDiscount()).
																	 setOrdfoodType(key.status.getValue());	
						builder.addOrdpriceinfo(priceBuilder);
					}
					
					//设置账单元素
					CMsgBillElement.Builder elementbuilder;
					if(isBillOfferChanged()){
						elementbuilder = CMsgBillElement.newBuilder();
						if(lowest.isChanged()){						
							elementbuilder = elementbuilder.setBillLow(lowest.getValue());
						}
						if(service.isChanged()){						
							elementbuilder = elementbuilder.setBillSer(service.getValue());
						}
						if(discountMoney.isChanged()){						
							elementbuilder = elementbuilder.setDisMoney(discountMoney.getValue());
						}
						if(discountRatio.isChanged()){						
							elementbuilder = elementbuilder.setDisRatio(discountRatio.getValue());							
						}
						if(orderDiscountMode.isChanged()){						
							elementbuilder = elementbuilder.setDisType(orderDiscountMode.getValue().value);
						}
						builder.setBillelement(elementbuilder);
					}
										
					CMsgBillPricingReq req = builder.setAuthcode(authCode).
													 setTableid(tableId).
													 build();
				
					client.sendDataAsyn(MomsgType.BILLPRICING_REQ.getValue(), req.toByteArray());
				} catch (UnknownHostException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		});
	}
	
	/**
	 * <p>setTable</p>
	 *
	 * <p>设置餐台</p>
	 * 
	 * @param tableId
	 */
	public void setTable(int tableId){
		if(this.tableId != tableId){
			this.tableId 	= tableId;
			resetData();
		}
	}	
	
	public void setService(String value){
		if(this.service == null){
			service = new SelfComparableItem<String>(value);			
		}else {
			this.service.setValue(value);
		}		
		if(service.isChanged()){
			offerFactors.put(OfferFactorsKey.SERVICE_COST_KEY, value);
		}else {
			offerFactors.remove(OfferFactorsKey.SERVICE_COST_KEY);
		}
	}
		
	public String getService(){
		return service.getValue();
	}
	
	public String getFormattedService(){
		BigDecimal result = new BigDecimal(service.getValue());
		result = result.multiply(new BigDecimal(100));
		return String.format("%s%%", result.setScale(0).toString());
	}
		
	public void setLowest(String value){
		if(this.lowest == null){
			this.lowest = new SelfComparableItem<String>(value);
		}else {
			this.lowest.setValue(value);
		}		
		if(lowest.isChanged()){
			offerFactors.put(OfferFactorsKey.LOWEST_CONSUMPTION_KEY, value);
		}else {
			offerFactors.remove(OfferFactorsKey.LOWEST_CONSUMPTION_KEY);
		}
	}
		
	public String getLowest(){
		return lowest.getValue();
	}
	
	
	/**
	 * <p>clearDiscount</p>
	 *
	 * <p>取消折扣</p>
	 *
	 */
	public void clearDiscount(){
		if(hasFoods()){
			discountMoney.setValue(CalculateConst.DECIMAL_ZERO_STRING);
			discountRatio.setValue(CalculateConst.DECIMAL_ONE_STRING);
			OrderItemValue foodValue;
			for(Entry<OrderItemKey, OrderItemValue> entry : orderItems.entrySet()){				
				foodValue 	= entry.getValue();
				foodValue.setMoneyDiscount(discountMoney.getValue());				
				foodValue.setRatioDiscount(discountRatio.getValue());
				foodValue.setDiscounted(false);						//菜品设置为未设置单道折扣
			}
		}		
	}
	
	/* (non-Javadoc)
	 * @see com.hithing.hsc.bll.control.FoodCtrl#specifyFoodCount(com.hithing.hsc.entity.OrderItemKey, int)
	 */
	@Override
	public void specifyFoodCount(OrderItemKey foodKey, int foodCount) {		
		super.specifyFoodCount(foodKey, foodCount);
		updateOfferItems(foodKey, orderItems.get(foodKey));
	}

	public void setDiscountMode(DiscountMode mode){		
		if(hasFoods()){
			if(this.orderDiscountMode == null){
				this.orderDiscountMode = new SelfComparableItem<DiscountMode>(mode);
			}else {
				this.orderDiscountMode.setValue(mode);
				dispatchDiscount(EnumSet.of(DiscountType.MONEY, DiscountType.RATIO));
			}						
			if(orderDiscountMode.isChanged()){
				offerFactors.put(OfferFactorsKey.DISCOUNT_MODE_KEY, mode.name());
			}else {
				offerFactors.remove(OfferFactorsKey.DISCOUNT_MODE_KEY);
			}
		}		
	}
	
	public void setDiscountRatio(String value){		
		if(hasFoods()){
			if(this.discountRatio == null){
				this.discountRatio = new SelfComparableItem<String>(value);
			}else {
				this.discountRatio.setValue(value);
				dispatchDiscount(EnumSet.of(DiscountType.RATIO));
				//变更后的折扣如果与原折扣相同，则清除受账单折扣影响的定价菜品
				validateOfferItems();
			}			
			if(discountRatio.isChanged()){
				offerFactors.put(OfferFactorsKey.DISCOUNT_RATIO_KEY, value);
			}else {
				offerFactors.remove(OfferFactorsKey.DISCOUNT_RATIO_KEY);
			}					
		}				
	}
	
	public String getDiscountRatio(){
//		BigDecimal result = new BigDecimal(discountRatio);
//		result = result.multiply(new BigDecimal(100));
//		return String.format("%%%s", result.setScale(0).toString());
		return discountRatio.getValue();
	}
	
	public void setDiscountMoney(String value){		
		if(hasFoods()){
			if(this.discountMoney == null){
				this.discountMoney = new SelfComparableItem<String>(value);
			}else {
				this.discountMoney.setValue(value);
				dispatchDiscount(EnumSet.of(DiscountType.MONEY));
				//变更后的折扣如果与原折扣相同，则清除受账单折扣影响的定价菜品
				validateOfferItems();
			}
			
			if(discountMoney.isChanged()){
				offerFactors.put(OfferFactorsKey.DISCOUNT_MONEY_KEY, value);				
			}else {
				offerFactors.remove(OfferFactorsKey.DISCOUNT_MONEY_KEY);
			}			
		}	
	}
	
	public String getDiscountMoney(){
		return discountMoney.getValue();
	}
	
	/**
	 * <p>modifyFoodPrice</p>
	 *
	 * <p>修改菜品单价</p>
	 * 
	 * @param foodKey 菜品键值
	 * @param price   菜品单价
	 */
	public void modifyFoodPrice(OrderItemKey foodKey, String price){
		OrderItemValue foodValue = orderItems.get(foodKey);
		updateOfferItems(foodKey,foodValue);			
		foodValue.setFoodPrice(price);
	}
	
	/**
	 * <p>modifyDiscountRatio</p>
	 *
	 * <p>修改单道折扣率</p>
	 * 
	 * @param foodKey 菜品标识
	 * @param ratio   折扣率
	 */
	public void modifyDiscountRatio(OrderItemKey foodKey, String ratio){
		OrderItemValue foodValue = orderItems.get(foodKey);
		updateOfferItems(foodKey,foodValue);
		foodValue.setRatioDiscount(ratio);
		foodValue.setDiscounted(true);
	}
	
	/**
	 * <p>modifyDiscountMoney</p>
	 *
	 * <p>功能描述</p>
	 *
	 */
	public void modifyDiscountMoney(OrderItemKey foodKey, String money){
		OrderItemValue foodValue = orderItems.get(foodKey);
		updateOfferItems(foodKey,foodValue);
		foodValue.setMoneyDiscount(money);
		foodValue.setDiscounted(true);
	}		

	/**
	 * <p>refreshService</p>
	 *
	 * <p>刷新服务费金额</p>
	 * 
	 * @return 服务费
	 */
	public String refreshService(){
		BigDecimal serviceDec = new BigDecimal(refreshConsume());		
		serviceDec = serviceDec.multiply(new BigDecimal(service.getValue())).
				setScale(CalculateConst.DECIMAL_MONEY_SCALE,BigDecimal.ROUND_HALF_DOWN);		
		return serviceDec.toString();
	}	
	
	/**
	 * <p>presentFood</p>
	 *
	 * <p>账单中添加赠送菜品</p>
	 * 
	 * @param key	   账单菜品键
	 * @param count	   赠送数目
	 * @param reason   赠送原因
	 */
	public void presentFood(OrderItemKey key, int count, String reason){		
		try {			
			OrderItemValue value = orderItems.get(key);
			
			if(value.getCount() == count){						//全部赠送的情况
				key.status = FoodStatus.FOOD_STATUS_PRESENT;				
			}else{												//赠送一部分的情况
				value.setCount(value.getCount() - count);
				//在账单中添加赠送菜品
				OrderItemKey 	presentKey 		= new OrderItemKey(FoodStatus.FOOD_STATUS_PRESENT, key.priceId, key.serialId);
				OrderItemValue 	presentValue 	= (OrderItemValue)value.clone();
				presentValue.setCount(count);			
				orderItems.put(presentKey, presentValue);
				//加入到定价菜品中
				offeritems.put(presentKey, presentValue);
			}			
			//加入到定价菜品中
			offeritems.put(key, value);			
		} catch (CloneNotSupportedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}		
	}
	
	/**
	 * <p>cancelFood</p>
	 *
	 * <p>账单中取消菜品</p>
	 * 
	 * @param key 		菜品键
	 * @param count		取消数量
	 * @param reason	取消原因
	 */
	public void cancelFood(OrderItemKey key, int count, String reason){
		try {
			OrderItemValue orderedValue = orderItems.get(key);
			int orderedCount = orderedValue.getCount();
			if(orderedCount == count){							//取消了全部菜品的情况
				key.status = FoodStatus.FOOD_STATUS_CANCEL;
			}else {												//取消了部分菜品的情况							
				//修改已落单菜品数量为未取消的数量
				orderedValue.setCount(orderedCount - count);
				//添加取消菜品
				OrderItemKey 	cancelKey 	= new OrderItemKey(FoodStatus.FOOD_STATUS_CANCEL,key.priceId,key.serialId);
				OrderItemValue 	cancelValue;
				cancelValue = (OrderItemValue)orderedValue.clone();
				cancelValue.setCount(count);
				cancelValue.setRemark(reason);
				orderItems.put(cancelKey, cancelValue);	
				offeritems.put(cancelKey, cancelValue);
			}
			//加入到定价菜品中
			offeritems.put(key, orderedValue);
		} catch (CloneNotSupportedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	/**
	 * <p>refreshConsume</p>
	 *
	 * <p>刷新消费合计</p>
	 * 
	 * @return 消费合计金额
	 */
	public String refreshConsume(){
		BigDecimal consumeDec = new BigDecimal("0.00");
		//菜品价格总额
		OrderItemKey foodKey;
		for(Entry<OrderItemKey, OrderItemValue> entry : orderItems.entrySet()){
			foodKey = entry.getKey();
			if(foodKey.status == FoodStatus.FOOD_STATUS_DONE ||
				foodKey.status == FoodStatus.FOOD_STATUS_UNDO){
				consumeDec = consumeDec.add(new BigDecimal(entry.getValue().getRealCost()));
			}
		}				
		return consumeDec.toString();
	}
	
	/* (non-Javadoc)
	 * @see com.hithing.hsc.bll.control.FoodCtrl#resetData()
	 */
	@Override
	public void resetData() {		
		super.resetData();
		offeritems.clear();
		offerFactors.clear();
		
		service 			= null;									
		lowest 				= null;										
		discountRatio 		= null;								
		discountMoney 		= null;			
		orderDiscountMode	= null;
	}

	/* (non-Javadoc)
	 * @see com.hithing.hsc.bll.control.FoodCtrl#refreshTotal()
	 */
	@Override
	public String refreshTotal() {
		BigDecimal totalDec = new BigDecimal(refreshConsume());					
		totalDec = totalDec.add(new BigDecimal(refreshService())).
				setScale(CalculateConst.DECIMAL_MONEY_SCALE,BigDecimal.ROUND_HALF_DOWN);	
		return totalDec.toString();
	}
	
	/**
	 * <p>isOfferChanged</p>
	 *
	 * <p>账单定价元素是否发生变化</p>
	 * 
	 * @return
	 */
	public boolean isOfferChanged(){				
		if(offeritems.size() != 0){			
			return true;
		}		
		
		return isBillOfferChanged();		
	}
	
	//账单元素是否定价
	private boolean isBillOfferChanged(){
		// temp code
		if(lowest == null
		   || service == null
		   || discountMoney == null
		   || discountRatio == null
		   || orderDiscountMode == null ){
			return false;
		}
		
		if(lowest.isChanged()
		   || service.isChanged()
		   || discountMoney.isChanged()
		   || discountRatio.isChanged()
		   || orderDiscountMode.isChanged()){			
			return true;
		}
		return false;
	}
	
	//将打折信息分配到菜品
	private void dispatchDiscount(EnumSet<DiscountType> type){
		//计算单道菜品的折扣金额
		BigDecimal orderDiscountMoney 	= new BigDecimal(discountMoney.getValue());
		BigDecimal divisor				= new BigDecimal(getAllFoodsCount());
		String foodDiscountMoney 		= orderDiscountMoney.divide(divisor, 
				CalculateConst.DECIMAL_MONEY_SCALE, BigDecimal.ROUND_HALF_DOWN).toString();
		
		OrderItemValue	foodValue;
		switch (orderDiscountMode.getValue()) {		
		case ALL_NO_EXCEPTION:			//照单折扣的情况
			for(Entry<OrderItemKey, OrderItemValue> entry : orderItems.entrySet()){				
				foodValue 	= entry.getValue();
				//对未设置过单道折扣的菜品进行账单级别的打折
				if(!foodValue.isDiscounted()){
					if(type.contains(DiscountType.RATIO)){	
						foodValue.setRatioDiscount(discountRatio.getValue());
					}
					if(type.contains(DiscountType.MONEY)){
						foodValue.setMoneyDiscount(foodDiscountMoney);
					}
					updateOfferItems(entry.getKey(), foodValue);
				}								
			}
			break;
		case ALL_WITH_EXCEPTION:		//全单折扣的情况
			for(Entry<OrderItemKey, OrderItemValue> entry : orderItems.entrySet()){				
				foodValue 	= entry.getValue();				
				if(foodValue.canDiscount() && !foodValue.isDiscounted()){
					if(type.contains(DiscountType.RATIO)){						
						foodValue.setRatioDiscount(discountRatio.getValue());
					}
					if(type.contains(DiscountType.MONEY)){
						foodValue.setMoneyDiscount(foodDiscountMoney);
					}	
					updateOfferItems(entry.getKey(), foodValue);
				}
			}
			break;
		}		
	}		
	
	private void updateOfferItems(OrderItemKey key, OrderItemValue value) {
		if(!offeritems.containsKey(key)){
			offeritems.put(key, value);
		}		
	}
	
	//检测定价菜品是否有效
	private void validateOfferItems(){
		if(!discountMoney.isChanged() && !discountRatio.isChanged()){
			Iterator<Entry<OrderItemKey, OrderItemValue>> itor = offeritems.entrySet().iterator();
			Entry<OrderItemKey, OrderItemValue> item;
			while(itor.hasNext()){
				item = itor.next();
				if(!item.getValue().isDiscounted()){
					itor.remove();
					offeritems.remove(item.getKey());
				}
			}
		}
	}
	
	/**
	 * <p>DiscountMode</p>
	 * 	
	 * <p>ALL_WITH_EXCEPTION-全单折扣;ALL_NO_EXCEPTION-照单折扣</p>
	 *
	 * @author Leopard
	 *
	 */
	public enum DiscountMode {
		ALL_WITH_EXCEPTION(0), 
		ALL_NO_EXCEPTION(1);
		
		private int value;
		private DiscountMode(int value) {
			this.value = value;
		}
		public int getValue(){ return value; }
		public static DiscountMode valueOf(int value){
			switch (value) {
			case 0:
				return ALL_WITH_EXCEPTION;				
			case 1:
				return ALL_NO_EXCEPTION;
			default:
					return null;
			}
		}
	}
		
	/**
	 * <p>ModifyKey</p>
	 * 	
	 * <p>账单的可修改键枚举</p>
	 * COUNT-菜品数量;PRICE-菜品单价;RATIO-折扣率;MONEY-折扣金额;SERVICE-服务费;LOWEST-最低消费
	 *
	 * @author Leopard
	 *
	 */
	public enum ModifyKey { COUNT, PRICE, RATIO, MONEY, SERVICE, LOWEST }		
	
	/**
	 * <p>OfferFactorsKey</p>
	 * 	
	 * <p>定价因子键枚举</p>
	 *
	 * 用于记录账单定价因子集合
	 * @author leo
	 *
	 */
	public enum OfferFactorsKey {
		SERVICE_COST_KEY		,				//服务费率键 
		DISCOUNT_RATIO_KEY		,				//折扣率键 
		DISCOUNT_MONEY_KEY		,				//折扣金额键
		LOWEST_CONSUMPTION_KEY	, 				//最低消费键
		DISCOUNT_MODE_KEY		; 				//折扣模式键
	}
	
	//RATIO-折扣率类型;MONEY-折扣金额类型
	private enum DiscountType { RATIO, MONEY }	
}
