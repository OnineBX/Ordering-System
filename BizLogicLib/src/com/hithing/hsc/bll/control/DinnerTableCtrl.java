/**========================================
 * File:	DinnerTableCtrl.java
 * Package:	com.hithing.hsc.bll.control
 * Create:	by Leopard
 * Date:	2011-12-17:下午1:14:29
 **======================================*/
package com.hithing.hsc.bll.control;

import java.io.IOException;
import java.net.UnknownHostException;

import com.hithing.hsc.bll.util.ITransportable.IAsynSendable;
import com.hithing.hsc.bll.util.MomsgFactory;
import com.hithing.hsc.bll.util.MomsgFactory.MomsgType;
import com.hithing.hsc.entity.MoMsg.CMsgFoundingReq;
import com.hithing.hsc.entity.MoMsg.CMsgOrderCancelReq;

/**
 * <p>DinnerTableCtrl</p>
 * 	
 * <p>餐台控制类,用于提供餐台相关的各种业务操作</p>
 *
 * @author Leopard
 * 
 */
public class DinnerTableCtrl implements IAsynSendable{

	private int 		tableId;													//餐台标识	
	
	/**
	 * <p>DinnerTableCtrl</p>
	 *
	 * <p>构造函数</p>
	 */
	public DinnerTableCtrl(){
		
	}
	
	/**
	 * <p>DinnerTableCtrl</p>
	 *
	 * <p>构造函数</p>
	 */
	public DinnerTableCtrl(int id) {
		
		tableId = id;
	}

	/**
	 * <p>Apply</p>
	 *
	 * <p>开台</p>
	 * 
	 * @param executor 	用于并发执行餐台开台的接口
	 * @param customers 人数
	 */
	public void founding(final int customers){		
		executor.execute(new Runnable() {
			
			@Override
			public void run() {
				try {
					CMsgFoundingReq.Builder builder = MomsgFactory.createMomsgBuilder(MomsgType.FOUNDING_REQ);
					CMsgFoundingReq req = builder.setTableid(tableId).setCustomers(customers).build();						
					client.sendDataAsyn(MomsgType.FOUNDING_REQ.getValue(), req.toByteArray());
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
	 * <p>unFounding</p>
	 *
	 * <p>取消开台</p>
	 * 
	 */
	public void unFounding(){
		executor.execute(new Runnable() {
			
			@Override
			public void run() {				
				try {
					CMsgOrderCancelReq.Builder builder = MomsgFactory.createMomsgBuilder(MomsgType.UNFOUNDING_REQ);
					CMsgOrderCancelReq req = builder.setTableid(tableId).build();
					client.sendDataAsyn(MomsgType.UNFOUNDING_REQ.getValue(), req.toByteArray());
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
	
	public void setTableId(int id){
		tableId = id;
	}
	
	public int getTableId(){
		return tableId;
	}
	
	/**
	 * <p>DinnerTableState</p>
	 * 	
	 * <p>餐台状态:空闲,使用,预定,维修</p>
	 *
	 * @author Leopard
	 *
	 */
	public enum DinnerTableState{
		none(0)		,			//无状态		
		spare(1)	,			//空闲
		using(2)	,			//使用
		reserve(3)	,			//预定
		maintain(4)	;			//维修
		
		private int value;
		
		public int getValue(){
			return value;
		}
		
		public static DinnerTableState valueOf(int value){
			switch (value) {
			case 1:				
				return spare;
			case 2:
				return using;
			case 3:
				return reserve;
			case 4:
				return maintain;
			default:
				return none;
			}
		}
		
		private DinnerTableState(int value) {
			this.value = value;
		}
	}
}
