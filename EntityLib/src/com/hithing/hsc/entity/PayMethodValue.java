/**========================================
 * File:	PayMethodValue.java
 * Package:	com.hithing.hsc.entity
 * Create:	by Leopard
 * Date:	2012-3-27:下午5:09:06
 **======================================*/
package com.hithing.hsc.entity;

import java.io.Serializable;

import com.hithing.hsc.entity.AdapterItem.OptionalAdapterItem;
import com.hithing.hsc.entity.MoMsg.CMsgInitCashierRsp.CMsgPayMethod;

/**
 * <p>PayMethodValue</p>
 * 	
 * <p>付款方式实体类</p>
 *
 * @author Leopard
 * 
 */
public final class PayMethodValue extends OptionalAdapterItem implements Serializable{	
	private static final long serialVersionUID = 3378783188681369982L;
	
	private String 	code;				//付款方式编码		
	private String 	money;				//付款金额		
	/**
	 * <p>PayMethodValue</p>
	 *
	 * <p>构造函数</p>
	 */
	public PayMethodValue(CMsgPayMethod pm) {
		super(pm.getId(), pm.getName().toStringUtf8());
		this.code 		= pm.getCode();
	}	
	
	public String getMoney(){
		return money;
	}	
	
	public String getCode(){
		return code;
	}
	
	public void setMoney(String value){
		money = value;
	}
}
