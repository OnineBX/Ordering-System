/**========================================
 * File:	MultiTargetHandler.java
 * Package:	com.hithing.app
 * Create:	by Leopard
 * Date:	2012-3-16:上午9:03:17
 **======================================*/
package com.hithing.app;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import com.google.protobuf.GeneratedMessage;
import com.hithing.hsc.bll.util.MomsgFactory;
import com.hithing.hsc.bll.util.MomsgFactory.MomsgType;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;

/**
 * <p>MultiTargetHandler</p>
 * 	
 * <p>多目标的消息处理句柄</p>
 *
 * @author Leopard
 * 
 */
public final class MultiCallBackHandler extends Handler {
	private Set<IMomsgResultable> callBackSet = new HashSet<IMomsgResultable>();
	/**
	 * <p>MultiTargetHandler</p>
	 *
	 * <p>构造函数</p>
	 */
	public MultiCallBackHandler() {
		// TODO Auto-generated constructor stub
	}
	
	/**
	 * <p>addTarget</p>
	 *
	 * <p>添加回调目标</p>
	 * 
	 * @param target
	 */
	public void addCallBack(IMomsgResultable callback){
		callBackSet.add(callback);
	}
	
	/**
	 * <p>removeCallBack</p>
	 *
	 * <p>移除回调目标</p>
	 * 
	 * @param callback
	 */
	public void removeCallBack(IMomsgResultable callback){
		callBackSet.remove(callback);
	}
	
	/* (non-Javadoc)
	 * @see android.os.Handler#handleMessage(android.os.Message)
	 */
	@Override
	public void handleMessage(Message msg) {		
		super.handleMessage(msg);
		MomsgType rspType = MomsgType.valueOf(msg.what);
		GeneratedMessage gMsg = MomsgFactory.createMomsg(rspType, (byte[])msg.obj);	
		for (IMomsgResultable callback : callBackSet) {			
			callback.onMomsgResult(rspType, gMsg);
		}
	}

	/**
	 * <p>IMomsgResultable</p>
	 * 	
	 * <p>Protobuf消息结果接口,该接口定义了可返回Protobuf类型消息结果的行为</p>
	 *
	 * @author Leopard
	 *
	 */
	public static interface IMomsgResultable{
		public void onMomsgResult(MomsgType rspType, GeneratedMessage msg);
	}
}
