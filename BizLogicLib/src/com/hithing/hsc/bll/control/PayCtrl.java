/**========================================
 * File:	PayCtrl.java
 * Package:	com.hithing.hsc.bll.control
 * Create:	by Leopard
 * Date:	2012-5-2:上午11:47:56
 **======================================*/
package com.hithing.hsc.bll.control;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import android.util.Log;

import com.hithing.hsc.bll.manage.CashingManager;
import com.hithing.hsc.entity.PayMethodValue;

/**
 * <p>PayCtrl</p>
 * 	
 * <p>支付控制类,提供与支付有关的各种操作</p>
 *
 * @author Leopard
 * 
 */
public final class PayCtrl {
	private final String			DEFAULT_ACTUAL_MONEY = "0.00";	//默认初始金额
	
	private int						defPmId;						//默认的付款方式标识
	
	private BigDecimal				receivableTotal;				//应收金额
	private BigDecimal				actualTotal;					//实收金额
	private List<PayMethodValue> 	payData;						//实际付款数据集合
	private List<PayMethodValue>	payMethodData;					//付款方式数据集合
	private Map<Integer, Integer>	locationMap;					//支付数据在付款方式数据集合中的位置与值对应关系
	private CashingManager			cashManager;					//收银数据管理器
	
	/**
	 * <p>PayCtrl</p>
	 *
	 * <p>构造函数</p>
	 */
	public PayCtrl(String total, CashingManager cashMgr) {
		receivableTotal = new BigDecimal(total);
		actualTotal		= new BigDecimal(DEFAULT_ACTUAL_MONEY);
		payData 		= new LinkedList<PayMethodValue>();
		payMethodData	= new LinkedList<PayMethodValue>();
		locationMap		= new HashMap<Integer, Integer>();
		cashManager 	= cashMgr;
		defPmId			= cashMgr.getDefaultPayMethod();
		initPayMethData();		
	}

	/**
	 * <p>addPay</p>
	 *
	 * <p>添加付款方式及其支付金额</p>
	 * 
	 * @param pmId	付款方式标识
	 * @param money 付款金额
	 */
	public void addPay(int pmId, String money){
		//数据中不存在标识为pmId的付款方式,加入到数据中							
		PayMethodValue value = payMethodData.get(locationMap.get(pmId));
		if(!payData.contains(value)){
			value.setMoney(money);			
			payData.add(value);			
			actualTotal = actualTotal.add(new BigDecimal(money));			
		}						
	}
	
	/**
	 * <p>removePay</p>
	 *
	 * <p>移除付款方式及其支付金额</p>
	 * 
	 * @param location
	 */
	public void removePay(int pmId){		
		int location = getPayMethodLocation(pmId);		
		PayMethodValue value = payMethodData.get(location);
		if(payData.contains(value)){
			actualTotal = actualTotal.subtract(new BigDecimal(value.getMoney()));
			value.setMoney(null);
			payData.remove(value);			
		}						
	}
	
	/**
	 * <p>modifyPay</p>
	 *
	 * <p>修改付款方式的支付金额</p>
	 * 
	 * @param pmId  付款方式标识
	 * @param money	支付金额
	 */
	public void modifyPay(int pmId, String money){		
		PayMethodValue value = payMethodData.get(getPayMethodLocation(pmId));
		actualTotal = actualTotal.subtract(new BigDecimal(value.getMoney()));
		value.setMoney(money);		
		actualTotal = actualTotal.add(new BigDecimal(money));			
	}
	
	/**
	 * <p>getChange</p>
	 *
	 * <p>得到找赎金额</p>
	 * 
	 * @return 返回找赎金额
	 */
	public String getChange(){		
		return actualTotal.subtract(receivableTotal).toString();
	}
	
	/**
	 * <p>getActual</p>
	 *
	 * <p>得到实收金额</p>
	 * 
	 * @return 返回实收金额
	 */
	public String getActual(){		
		return actualTotal.toString();
	}
		
	/**
	 * <p>getPayData</p>
	 *
	 * <p>获得实际支付数据集合</p>
	 * 
	 * @return 实际支付数据集合
	 */
	public List<PayMethodValue> getPayData(){
		for (PayMethodValue value : payData) {
			if(value.getMoney() == DEFAULT_ACTUAL_MONEY){
				payData.remove(value);
			}
		}
		return payData;
	}
	
	/**
	 * <p>getPayMethodData</p>
	 *
	 * <p>获得支付数据集合</p>
	 * 
	 * @return 支付数据集合
	 */
	public List<PayMethodValue> getPayMethodData(){
		return payMethodData;
	}
	
	/**
	 * <p>getPayMethodLocation</p>
	 *
	 * <p>获得付款方式在支付数据集合中的位置</p>
	 * 
	 * @param pmId 付款方式标识
	 * @return 付款方式位置
	 */
	public int getPayMethodLocation(int pmId){
		return locationMap.get(pmId);
	}		
	
	//初始化付款方式数据集合
	private void initPayMethData(){		
		int pmId;
		for(PayMethodValue value : cashManager.getAllPayMethods()){			
			payMethodData.add(value);
			pmId = value.getId();
			locationMap.put(pmId, payMethodData.indexOf(value));			
			//是默认的付款方式,加入到实际支付数据集合
			if(pmId == defPmId){
				value.setChecked(true);
				addPay(pmId, receivableTotal.toString());
			}			
		}		
	}
}
