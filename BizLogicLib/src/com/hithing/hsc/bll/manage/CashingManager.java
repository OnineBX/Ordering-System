/**========================================
 * File:	CashingManager.java
 * Package:	com.hithing.hsc.bll.manage
 * Create:	by Leopard
 * Date:	2012-3-28:上午11:04:37
 **======================================*/
package com.hithing.hsc.bll.manage;

import java.sql.SQLException;
import java.util.LinkedList;
import java.util.List;

import android.util.Log;

import com.hithing.hsc.dataentity.BaseEntity;
import com.hithing.hsc.entity.MoMsg.CMsgInitCashierRsp.CMsgPayMethod;
import com.hithing.hsc.entity.PayMethodValue;
import com.j256.ormlite.dao.Dao;

/**
 * <p>CashingManager</p>
 * 	
 * <p>收银数据管理类</p>
 *
 * @author Leopard
 * 
 */
public final class CashingManager {	
	private List<PayMethodValue> payMethodList;
	/**
	 * <p>CashingManager</p>
	 *
	 * <p>构造函数</p>
	 */
	public CashingManager(List<CMsgPayMethod> data) {
		payMethodList = convertToPayMethodList(data);
	}
	
	/**
	 * <p>getDefaultPayMethod</p>
	 *
	 * <p>得到默认的付款方式</p>
	 * 
	 * @return 默认的付款方式标识
	 */
	public int getDefaultPayMethod(){
		//unfinished code
		return 1;
	}
	
	/**
	 * <p>getAllPayMethods</p>
	 *
	 * <p>获得所有的付款方式信息</p>
	 * 
	 * @return
	 */
	public List<PayMethodValue> getAllPayMethods(){
		return payMethodList;
	}
	
	//将协议中的收银集合数据转换成收银实体列表
	private List<PayMethodValue> convertToPayMethodList(List<CMsgPayMethod> pmData){
		List<PayMethodValue> pmList = new LinkedList<PayMethodValue>();
		PayMethodValue value;
		for (CMsgPayMethod pm : pmData) {
			value = new PayMethodValue(pm);
			pmList.add(value);				
		}
		return pmList;
	}
	
}
