/**========================================
 * File:	CashCtrl.java
 * Package:	com.hithing.hsc.bll.control
 * Create:	by Leopard
 * Date:	2012-3-15:下午4:04:56
 **======================================*/
package com.hithing.hsc.bll.control;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.UnknownHostException;
import java.util.EnumMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.google.protobuf.ByteString;
import com.hithing.hsc.bll.control.OfferCtrl.DiscountMode;
import com.hithing.hsc.bll.control.OfferCtrl.OfferFactorsKey;
import com.hithing.hsc.bll.manage.DinnerTableManager;
import com.hithing.hsc.bll.util.ITransportable.IAsynSendable;
import com.hithing.hsc.bll.util.MomsgFactory;
import com.hithing.hsc.bll.util.MomsgFactory.MomsgType;
import com.hithing.hsc.entity.CashItemValue;
import com.hithing.hsc.entity.MoMsg.CMsgBillPricingReq;
import com.hithing.hsc.entity.MoMsg.CMsgBillReq;
import com.hithing.hsc.entity.MoMsg.CMsgCheckAccountReq;
import com.hithing.hsc.entity.MoMsg.CMsgBillPricingReq.CMsgBillElement;
import com.hithing.hsc.entity.MoMsg.CMsgBillPricingReq.CMsgOrdpriceInfo;
import com.hithing.hsc.entity.MoMsg.CMsgBillReq.CMsgPayMethod;
import com.hithing.hsc.entity.MoMsg.CMsgClearSpotReq;
import com.hithing.hsc.entity.MoMsg.CMsgComplexReq;
import com.hithing.hsc.entity.OrderItemKey;
import com.hithing.hsc.entity.OrderItemValue;
import com.hithing.hsc.dataentity.DinnerTableEntity;
import com.hithing.hsc.entity.PayMethodValue;

/**
 * <p>CashCtrl</p>
 * 	
 * <p>收银控制类,用于完成与收银相关的各种操作</p>
 *
 * @author Leopard
 * 
 */
public final class CashCtrl implements IAsynSendable{	
	
	private final int					DEFAULT_CASHITEM_INDEX		= 0;	
	private int 						cashSpotId;									//收银点标识
	private int							casherAuthCode;								//收银员授权码
	
	private DinnerTableManager			tableManager;
	private Map<Integer, CashItemValue> cashItems;	
	private Map<Integer, Integer>		tableBillMap;								//餐台-账单映射

	public CashCtrl(int spotId, int authCode){
		this.cashSpotId 	= spotId;
		this.casherAuthCode = authCode;
	}
	
	/**
	 * <p>CashCtrl</p>
	 *
	 * <p>构造函数</p>
	 */
	public CashCtrl(int spotId, int authCode, DinnerTableManager tableManager) {
		this(spotId, authCode);
		this.tableManager 	= tableManager;
		cashItems 			= new LinkedHashMap<Integer, CashItemValue>();	
		tableBillMap		= new LinkedHashMap<Integer, Integer>();
	}

	/**
	 * <p>refreshCheckedOrder</p>
	 *
	 * <p>刷新已结帐的账单</p>
	 *	更新当前营业日的未结帐的账单
	 */
	public void refreshCheckedOrder(){
		
	}		
	
	public void setCashSpotId(int id){
		this.cashSpotId = id;
	}
	
	/**
	 * <p>payBill</p>
	 *
	 * <p>埋单</p>
	 * 
	 * @param tableId	要埋单的餐台标识
	 * @param authCode	处理埋单的人员授权码
	 */
	public void payBill(final int tableId, final int shiftId, final List<PayMethodValue> payData){
		executor.execute(new Runnable() {
			
			@Override
			public void run() {								
				try {
					CMsgBillReq req = produceBillReq(tableId, shiftId, payData);
					client.sendDataAsyn(MomsgType.PAYBILL_REQ.getValue(), req.toByteArray());
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
	 * <p>payBillWithOffer</p>
	 *
	 * <p>伴有账单定价的埋单</p>
	 * 此操作经常发生在收银时，收银员对账单定价后执行埋单操作
	 * 
	 * @param tableId 餐台标识
	 * @param shiftId 班次标识
	 * @param payData 付款数据
	 * @param offerData 定价元素数据
	 * @param offerItems 定价菜品数据
	 */
	public void payBillWithOffer(final int tableId, 
								 final int shiftId, 
								 final List<PayMethodValue> payData,
								 final EnumMap<OfferFactorsKey, String> offerFactors,
								 final Map<OrderItemKey, OrderItemValue> offerItems){
		executor.execute(new Runnable() {
			
			@Override
			public void run() {				
				try {
					CMsgComplexReq.Builder 	builder = MomsgFactory.createMomsgBuilder(MomsgType.COMPLEX_REQ);
					ByteArrayOutputStream dataCache = new ByteArrayOutputStream();
					//加入账单定价数据
					CMsgBillPricingReq offerReq = produceOfferReq(casherAuthCode, tableId, offerFactors, offerItems);
					dataCache.write(offerReq.toByteArray());
					//加入埋单数据
					CMsgBillReq billReq = produceBillReq(tableId, shiftId, payData);
					dataCache.write(billReq.toByteArray());
					CMsgComplexReq			req		= builder.setCount(2).setData(ByteString.copyFrom(dataCache.toByteArray())).build();
					client.sendDataAsyn(MomsgType.COMPLEX_REQ.getValue(), req.toByteArray());
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
	 * <p>getCashData</p>
	 *
	 * <p>得到收银项目数据</p>
	 * 
	 * @return
	 */
	public Map<Integer, CashItemValue> getCashData(){
		return cashItems;
	}
	
	/**
	 * <p>loadCashItem</p>
	 *
	 * <p>载入收银项目</p>
	 * 
	 * @param billId 客帐号
	 * @param value	  收银项目值
	 */
	public void loadCashItem(int billId, int tableId){
		DinnerTableEntity dtEnt = tableManager.getTableById(tableId);		
		CashItemValue value = new CashItemValue(tableId, dtEnt.getCode(), dtEnt.getName(),dtEnt.getIndex());
		cashItems.put(billId, value);
		tableBillMap.put(tableId, billId);
	}
	
	/**
	 * <p>clearCashItems</p>
	 *
	 * <p>清空收银项目</p>
	 * 
	 */
	public void clearCashItems(){
		cashItems.clear();
		tableBillMap.clear();
	}
	
	/**
	 * <p>getDefaultCashItem</p>
	 *
	 * <p>得到默认的收银条目</p>
	 * 
	 * @param defId 默认的餐台标识
	 * @return 收银条目
	 */
	public CashItemValue getDefaultCashItem(int defId){
		CashItemValue result = null;
		if(cashItems.size() > 0){
			if(!tableBillMap.containsKey(defId)){				
				result = cashItems.values().toArray(new CashItemValue[0])[DEFAULT_CASHITEM_INDEX];
			}
		} 
		return result;
	}
		
	/**
	 * <p>clearForNextBizDay</p>
	 *
	 * <p>清机,结束当前营业日,同时将下一天设置为当前营业日</p>
	 * 
	 * @param state 清机状态：1-返回当前营业日；2-设置下一日为当前营业日
	 */
	public void clearForNextBizDay(final ClearSpotState state){
		executor.execute(new Runnable() {
			
			@Override
			public void run() {				
				try {
					CMsgClearSpotReq.Builder 	builder = MomsgFactory.createMomsgBuilder(MomsgType.CLEARSPOT_REQ);				
					CMsgClearSpotReq 			req 	= builder.setCashspotid(cashSpotId).
																  setStateid(state.getValue()).
																  setAuthcode(casherAuthCode).
																  build();					
					client.sendDataAsyn(MomsgType.CLEARSPOT_REQ.getValue(), req.toByteArray());
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
	 * <p>viewAccount</p>
	 *
	 * <p>查看帐目</p>
	 * 查看收银员经手的当前营业日下的所有已结帐目
	 *
	 */
	public void viewAccounts(){		
		executor.execute(new Runnable() {
			
			@Override
			public void run() {				
				try {
					CMsgCheckAccountReq.Builder builder = MomsgFactory.createMomsgBuilder(MomsgType.CHECKACCOUNT_REQ);
					CMsgCheckAccountReq 		req		= builder.setAuthcode(casherAuthCode).
																  setCashspotid(cashSpotId).
																  build();
					client.sendDataAsyn(MomsgType.CHECKACCOUNT_REQ.getValue(), req.toByteArray());
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
	 * <p>ClearSpotState</p>
	 * 	
	 * <p>清机所处状态</p>
	 *
	 * @author leo
	 *
	 */
	public enum ClearSpotState{
		VIEW_CURRENT_BIZDAY(1),				//查看当前营业日状态
		CHANGE_CURRENT_BIZDAY(2);			//更改当前营业日状态
		
		private int value;
		private ClearSpotState(int value) {
			this.value = value;
		}
		
		public int getValue(){
			return value;
		}
	}
	
	//生成埋单请求消息
	private CMsgBillReq produceBillReq(int tableId, int shiftId, List<PayMethodValue> payData){
		CMsgBillReq.Builder builder = MomsgFactory.createMomsgBuilder(MomsgType.PAYBILL_REQ);
		CMsgPayMethod.Builder pmBuilder;
		for (PayMethodValue value : payData) {						
			pmBuilder = CMsgPayMethod.newBuilder().
									  setPayMetid(value.getId()).
									  setPayMoney(value.getMoney());
			builder.addPaymethod(pmBuilder);
		}
		CMsgBillReq req =  builder.setAuthcode(casherAuthCode).
								   setTableid(tableId).
								   setShiftid(shiftId).
								   build();
		return req;
	}
	
	//生成定价请求消息
	private CMsgBillPricingReq produceOfferReq(int authcode,
											   int tableId,
											   EnumMap<OfferFactorsKey, String> offerData, 
											   Map<OrderItemKey, OrderItemValue> offeritems){
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
		for (Entry<OfferFactorsKey, String> entry : offerData.entrySet()) {			
				elementbuilder = CMsgBillElement.newBuilder();
				switch (entry.getKey()) {
				case SERVICE_COST_KEY:
					elementbuilder = elementbuilder.setBillSer(entry.getValue());
					break;
				case DISCOUNT_RATIO_KEY:
					elementbuilder = elementbuilder.setDisRatio(entry.getValue());
					break;
				case DISCOUNT_MONEY_KEY:
					elementbuilder = elementbuilder.setDisMoney(entry.getValue());
					break;
				case LOWEST_CONSUMPTION_KEY:
					elementbuilder = elementbuilder.setBillLow(entry.getValue());
					break;
				case DISCOUNT_MODE_KEY:
					elementbuilder = elementbuilder.setDisType(DiscountMode.valueOf(entry.getValue()).getValue());
					break;
				}													
				builder.setBillelement(elementbuilder);
		}
		
							
		CMsgBillPricingReq req = builder.setAuthcode(authcode).
										 setTableid(tableId).
										 build();
		return req;
	}
}
