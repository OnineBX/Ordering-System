/**========================================
 * File:	TimingCtrl.java
 * Package:	com.hithing.hsc.bll.control
 * Create:	by Leopard
 * Date:	2012-5-22:下午2:18:13
 **======================================*/
package com.hithing.hsc.bll.control;

import com.hithing.hsc.bll.util.ITransportable.IAsynSendable;

/**
 * <p>TimingCtrl</p>
 * 	
 * <p>计时消费控制类,用于完成计时项目的各种操作</p>
 *
 * @author Leopard
 * 
 */
public final class TimingCtrl implements IAsynSendable{
	private int 	tableId;							//餐台标识
	private long	startTime;							//开始时间
	private long	cumulativeTime;						//累积时间
	
	/**
	 * <p>TimingCtrl</p>
	 *
	 * <p>构造函数</p>
	 */
	public TimingCtrl(int tableId) {
		this.tableId = tableId;
	}
	
	/**
	 * <p>loadCurrentTimeInfo</p>
	 *
	 * <p>载入当前计时信息</p>
	 *	一次消费过程可能需要多个计时,该方法只返回当前计时信息.此方法将发送异步消息,调用后需异步接收消息
	 */
	public void loadCurrentTimingInfo(){
		
	}

}
