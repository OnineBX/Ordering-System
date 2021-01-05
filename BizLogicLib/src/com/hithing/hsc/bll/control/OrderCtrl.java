/**========================================
 * File:	OrderCtrl.java
 * Package:	com.hithing.hsc.bll.control
 * Create:	by Leopard
 * Date:	2011-12-28:上午10:04:32
 **======================================*/
package com.hithing.hsc.bll.control;

import java.io.IOException;
import java.net.UnknownHostException;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;


import android.util.Log;

import com.google.protobuf.ByteString;
import com.hithing.hsc.bll.manage.FoodManager;
import com.hithing.hsc.bll.util.BillDataCache;
import com.hithing.hsc.bll.util.MomsgFactory;
import com.hithing.hsc.bll.util.MomsgFactory.MomsgType;
import com.hithing.hsc.dataentity.FoodEntity;
import com.hithing.hsc.dataentity.FoodPriceEntity;
import com.hithing.hsc.entity.MoMsg.CMsgOrderReq;
import com.hithing.hsc.entity.MoMsg.CMsgOrderReq.CMsgfoodSet;
import com.hithing.hsc.entity.OrderItemKey;
import com.hithing.hsc.entity.OrderItemKey.FoodStatus;
import com.hithing.hsc.entity.OrderItemValue;
import com.hithing.hsc.entity.OrderItemValue.Builder;
import com.hithing.hsc.entity.OrderItemValue.RemarkItem;

/**
 * <p>OrderCtrl</p>
 * 	
 * <p>菜品订单控制类,用于完成各种菜单操作</p>
 *
 * @author Leopard
 * 
 */
public final class OrderCtrl extends FoodCtrl {
	protected Map<OrderItemKey, OrderItemValue>			unOrderItems;	//未下单菜品数据	
	
	public OrderCtrl(int tableId, FoodManager foodMgr) {
		super(tableId, foodMgr);
		unOrderItems 	= BillDataCache.INSTANCE.getUnOrderItems();
		resetData();
	}			
	
	/**
	 * <p>placeOrder</p>
	 *
	 * <p>发送落单消息请求</p>
	 * 
	 * @param authCode 授权码
	 *
	 */
	public void placeOrder(final int authCode){	
		Log.e("placeOrder", "ok!");
		executor.execute(new Runnable() {						
			@Override
			public void run() {				
				try {
					CMsgOrderReq.Builder orderBuilder 	= MomsgFactory.createMomsgBuilder(MomsgType.ORDER_REQ);
					
					//普通点单的菜品
					CMsgfoodSet.Builder foodBuilder = null;				
					
					OrderItemKey 			key;
					OrderItemValue 			value;
					
					for(Entry<OrderItemKey, OrderItemValue> entry : unOrderItems.entrySet()){
						key 		= entry.getKey();
						value 		= entry.getValue();							
						foodBuilder = CMsgfoodSet.newBuilder().setFoodPriceid(key.priceId).						 								   
						 								   setFoodNum(value.getCount()).						 								   
						 								   setFoodPrice(foodMgr.getFoodPrice(key.priceId).getPrice()).						 								   
						 								   setFoodRemark(ByteString.copyFrom(value.getDescription().getBytes()));
						orderBuilder.addFoodset(foodBuilder);																													
																	
					}
					
					CMsgOrderReq orderReq = orderBuilder.setTableId(tableId).setAuthId(authCode).build();
					client.sendDataAsyn(MomsgType.ORDER_REQ.getValue(), orderReq.toByteArray());
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
	 * <p>getUndoData</p>
	 *
	 * <p>得到未下单的数据</p>
	 * 
	 * @return
	 */
	public Map<OrderItemKey, OrderItemValue> getUnOrderData(){		
		return unOrderItems;
	}
	/**
	 * <p>clearUndoData</p>
	 *
	 * <p>清空未下单数据</p>
	 *
	 */
	public void clearUndoData(){
		unOrderItems.clear();
	}
	
	/**
	 * <p>addFood</p>
	 *
	 * <p>账单中添加菜品</p>
	 * 
	 * @param foodPriceId	菜品价格标识,因一个菜品有多个计价单位,即多种价格,所以用此区分
	 */
	public void addFood(int priceId){
		OrderItemKey 	key 	= new OrderItemKey(FoodStatus.FOOD_STATUS_UNDO, priceId);
		OrderItemValue 	value 	= createOrUpdateItem(key);
		orderItems.put(key, value);
		unOrderItems.put(key, value);
	}			
	
	/**
	 * <p>cancelFood</p>
	 *
	 * <p>取消账单中未下单得菜品</p>
	 * 
	 * @param key 	菜品键
	 */
	public void cancelFood(OrderItemKey key){		
		orderItems.remove(key);		
		unOrderItems.remove(key);					
	}					
	
	/**
	 * <p>specifyFoodRecipe</p>
	 *
	 * <p>设定菜品做法</p>
	 * 
	 * @param recipe
	 */
	public void specifyFoodRecipe(OrderItemKey foodKey, HashMap<Integer, String> recipe){
		
		OrderItemValue foodValue = orderItems.get(foodKey);		
		foodValue.setFoodRecipe(recipe);		
	}
	
	/**
	 * <p>specifyDemand</p>
	 *
	 * <p>设定传菜要求</p>
	 * 
	 * @param foodKey
	 */
	public void specifyDemand(OrderItemKey foodKey, RemarkItem demand){
		
		OrderItemValue foodValue = orderItems.get(foodKey);		
		foodValue.setDemand(demand);		
	}
	
	//收银相关的方法
								
	
	/**
	 * <p>hasUndoFoods</p>
	 *
	 * <p>账单中是否存在未下单数据</p>
	 * 
	 * @return 存在下单数据返回真,否则返回假
	 */
	public boolean hasUndoFoods(){
		return !unOrderItems.isEmpty();
	}
	
	/**
	 * <p>getUndoFoodsCount</p>
	 *
	 * <p>得到账单中所有未落单菜品数量</p>
	 * 
	 * @return 菜品数量
	 */
	public int getUndoFoodsCount(){
		return unOrderItems.size();
	}	
	
	/* (non-Javadoc)
	 * @see com.hithing.hsc.bll.control.FoodCtrl#resetData()
	 */
	@Override
	public void resetData() {		
		super.resetData();
		unOrderItems.clear();
	}
	
	
	/* (non-Javadoc)
	 * @see com.hithing.hsc.bll.control.FoodCtrl#specifyFoodCount(com.hithing.hsc.entity.OrderItemKey, int)
	 */
	@Override
	public void specifyFoodCount(OrderItemKey foodKey, int foodCount) {		
		super.specifyFoodCount(foodKey, foodCount);
		
		//如果菜品数量为0时，移除在未落单的对应项
		if(!orderItems.containsKey(foodKey)){
			unOrderItems.remove(foodKey);
		}
	}

	/* (non-Javadoc)
	 * @see com.hithing.hsc.bll.control.FoodCtrl#specifyFoodPrice(com.hithing.hsc.entity.OrderItemKey, int)
	 */
	@Override
	public void specifyFoodPrice(OrderItemKey foodKey, int priceId) {		
		super.specifyFoodPrice(foodKey, priceId);
		unOrderItems.remove(foodKey);
		OrderItemKey 	newKey 		= new OrderItemKey(foodKey.status, priceId, foodKey.serialId);
		OrderItemValue 	newValue 	= orderItems.get(newKey);
		unOrderItems.put(newKey, newValue);
	}

	//创建或更新订单条目
	private OrderItemValue createOrUpdateItem(OrderItemKey key){
		
		OrderItemValue 	value = (OrderItemValue)orderItems.get(key);
		
		if(value != null){						//已经加入到帐单的情况
			value.increment(1);
		}else{									//未加入到帐单的情况
			FoodPriceEntity fpEnt = foodMgr.getFoodPrice(key.priceId);
			try {
				fpEnt.getUnit().refresh();
				FoodEntity foodEnt = fpEnt.getFood();
				foodEnt.refresh();
				foodEnt.getFoodSubSort().refresh(); //unfinished code				
				value = new Builder().setFood(new RemarkItem(foodEnt.getId(), foodEnt.getName())).
									  setFoodPrice(fpEnt.getPrice()).
									  setFoodUnit(fpEnt.getUnit().getName()).
									  setProduce(foodEnt.getFoodSubSort().getProduce()).
									  build();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}			
			
		}
		return value;
	}
}
