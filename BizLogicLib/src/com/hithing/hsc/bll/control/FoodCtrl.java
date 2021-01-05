/**========================================
 * File:	FoodCtrl.java
 * Package:	com.hithing.hsc.bll.control
 * Create:	by Leopard
 * Date:	2012-5-7:下午8:05:16
 **======================================*/
package com.hithing.hsc.bll.control;

import java.io.IOException;
import java.math.BigDecimal;
import java.net.UnknownHostException;
import java.sql.SQLException;
import java.util.Map;
import java.util.Map.Entry;


import android.util.Log;

import com.hithing.hsc.bll.manage.FoodManager;
import com.hithing.hsc.bll.util.ITransportable.IAsynSendable;
import com.hithing.hsc.bll.util.MomsgFactory;
import com.hithing.hsc.bll.util.MomsgFactory.MomsgType;
import com.hithing.hsc.bll.util.BillDataCache;
import com.hithing.hsc.dataentity.FoodEntity;
import com.hithing.hsc.dataentity.FoodPriceEntity;
import com.hithing.hsc.entity.MoMsg.CMsgImageLoadReq;
import com.hithing.hsc.entity.MoMsg.CMsgViewBillReq;
import com.hithing.hsc.entity.OrderItemKey;
import com.hithing.hsc.entity.OrderItemValue;
import com.hithing.hsc.entity.OrderItemKey.FoodStatus;
import com.hithing.hsc.entity.OrderItemValue.Builder;
import com.hithing.hsc.entity.OrderItemValue.RemarkItem;

/**
 * <p>FoodCtrl</p>
 * 	
 * <p>功能描述</p>
 *
 * @author Leopard
 * 
 */
public abstract class FoodCtrl implements IAsynSendable{
	protected Map<OrderItemKey, OrderItemValue> 		orderItems;		//账单数据	
	protected int				tableId;								//与菜品订单绑定的餐台标识
	protected FoodManager		foodMgr;								//菜品数据管理类		
	
	/**
	 * <p>FoodCtrl</p>
	 *
	 * <p>构造函数</p>
	 * 
	 * @param tableId    餐台标识
	 * @param foodMgr 	   菜品管理类实例	
	 */
	public FoodCtrl(int tableId, FoodManager foodMgr) {
		this.tableId	= tableId;
		this.foodMgr	= foodMgr;
		
		BillDataCache	dataCache = BillDataCache.INSTANCE;
		orderItems 		= dataCache.getOrderItems();		
	}

	/**
	 * <p>setTable</p>
	 *
	 * <p>变换餐台</p>
	 * 
	 * @param tableId
	 */
	public void setTable(int tableId){
		this.tableId = tableId;
	}
	
	/**
	 * <p>resetData</p>
	 *
	 * <p>重置账单数据</p>
	 *
	 */
	public void resetData(){
		orderItems.clear();		
	}
	
	/**
	 * <p>refresh</p>
	 *
	 * <p>发送刷新账单数据请求</p>
	 *
	 */
	public void refresh(){	
		resetData();		
		executor.execute(new Runnable() {
			
			@Override
			public void run() {				
				try {
					CMsgViewBillReq.Builder builder = MomsgFactory.createMomsgBuilder(MomsgType.VIEWBILL_REQ);
					CMsgViewBillReq req			 = builder.setTableid(tableId).build();
					client.sendDataAsyn(MomsgType.VIEWBILL_REQ.getValue(), req.toByteArray());	
					
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
	 * <p>loadFood</p>
	 *
	 * <p>载入已经落单的菜品</p>
	 * 
	 * @param key	账单菜品键
	 * @param valueBuilder 账单菜品构建器
	 */
	public void loadFood(OrderItemKey key, Builder valueBuilder){
		FoodPriceEntity fpEnt = foodMgr.getFoodPrice(key.priceId);
		try {
			fpEnt.refresh();
			fpEnt.getFood().refresh();
			fpEnt.getFood().getFoodSubSort().refresh();
			fpEnt.getUnit().refresh();	
			OrderItemValue value = valueBuilder.setFood(new RemarkItem(fpEnt.getFood().getId(), fpEnt.getFood().getName())).												
												setFoodUnit(fpEnt.getUnit().getName()).
												setProduce(fpEnt.getFood().getFoodSubSort().getProduce()).												
												build();						
			orderItems.put(key, value);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}		
	}	
	
	/**
	 * <p>hasFoods</p>
	 *
	 * <p>账单中是否存在菜品</p>
	 * 
	 * @return 存在菜品返回真,否则返回假
	 */
	public boolean hasFoods(){
		return !orderItems.isEmpty();
	}
	
	/**
	 * <p>getOrderData</p>
	 *
	 * <p>得到订单数据</p>
	 * 
	 * @return 订单数据集合
	 */
	public Map<OrderItemKey, OrderItemValue> getOrderData(){
		return orderItems;
	}
	
	/**
	 * <p>getAllFoodsCount</p>
	 *
	 * <p>得到账单中所有菜品数量</p>
	 * 
	 * @return 菜品数量
	 */
	public int getAllFoodsCount(){
		return orderItems.size();
	}		
	
	/**
	 * <p>ModifyFoodCount</p>
	 *
	 * <p>修改账单菜品份数</p>
	 * 
	 * @param foodKey	菜品键
	 * @param foodCount 菜品数量
	 */
	public void specifyFoodCount(OrderItemKey foodKey, int foodCount){
		OrderItemValue foodValue = orderItems.get(foodKey);
		if(foodCount == 0){
			orderItems.remove(foodKey);
		}else {
			foodValue.setCount(foodCount);
		}				
	}
	
	/**
	 * <p>specifyFoodPrice</p>
	 *
	 * <p>修改账单菜品价格</p>
	 * 
	 * @param foodKey 菜品键
	 * @param priceId 菜品价格标识
	 */
	public void specifyFoodPrice(OrderItemKey foodKey, int priceId){
		FoodPriceEntity price = foodMgr.getFoodPrice(priceId);				
		
		//更新Value
		OrderItemValue foodValue = orderItems.get(foodKey);			
		foodValue.setFoodPrice(price.getPrice());				
		foodValue.setFoodUnit(price.getUnit().getName());

		//更新Key
		OrderItemKey newKey = new OrderItemKey(foodKey.status, priceId, foodKey.serialId);
		orderItems.remove(foodKey);
		orderItems.put(newKey, foodValue);						
	}
	
	/**
	 * <p>refreshTotal</p>
	 *
	 * <p>刷新账单总额,默认情况下为菜品不打折时的总价格</p>
	 * 
	 * @return 账单总额
	 */
	public String refreshTotal(){		
		BigDecimal totalDec = new BigDecimal("0.00");
		OrderItemKey foodKey;
		for(Entry<OrderItemKey, OrderItemValue> entry : orderItems.entrySet()){
			foodKey = entry.getKey();
			if(foodKey.status != FoodStatus.FOOD_STATUS_PRESENT &&
				foodKey.status != FoodStatus.FOOD_STATUS_CANCEL){
				totalDec = totalDec.add(new BigDecimal(entry.getValue().getApplyCost()));
			}
		}				
		return totalDec.toString();
	}	
	
	/**
	 * <p>loadFoodImage</p>
	 *
	 * <p>加载菜品图片</p>
	 * 
	 * @param foodId 菜品单价标识
	 */
	public void loadFoodImage(final int fpId){
		executor.execute(new Runnable() {
			
			@Override
			public void run() {				
				try {
					int foodId = 0;
					FoodEntity foodEnt = foodMgr.getFoodByPriceId(fpId);
					if(foodEnt != null){
						foodId = foodEnt.getId();
					}					
					CMsgImageLoadReq.Builder 	builder = MomsgFactory.createMomsgBuilder(MomsgType.IMAGELOAD_REQ);
					CMsgImageLoadReq 			req		= builder.setFoodId(foodId).build();
					client.sendDataAsyn(MomsgType.IMAGELOAD_REQ.getValue(), req.toByteArray());
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
		
}
