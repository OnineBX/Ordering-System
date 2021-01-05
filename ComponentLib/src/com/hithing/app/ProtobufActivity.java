/**========================================
 * File:	ProtobufActivity.java
 * Package:	com.hithing.widget
 * Create:	by Leopard
 * Date:	2011-12-7:上午11:12:57
 **======================================*/
package com.hithing.app;

import com.hithing.app.MultiCallBackHandler.IMomsgResultable;
import com.hithing.hsc.bll.util.MomsgHelper;
import com.hithing.hsc.bll.util.WorkContext;
import com.hithing.hsc.bll.util.MomsgHelper.IntervalSyncType;

import android.app.Activity;
import android.os.Bundle;

/**
 * <p>ProtobufActivity</p>
 * 	
 * <p>继承自该活动的子活动可处理使用protobuf协议定义的消息</p>
 *
 * @author Leopard
 * 
 */
public abstract class ProtobufActivity extends Activity implements IMomsgResultable {				
	
	protected final	int						MOMSG_RSP_STATUS_SUCCESS 	= 1;							//成功返回消息状态
	
	protected final MomsgHelper 			msgHelper 					= MomsgHelper.INSTANCE;
	protected final	WorkContext 			wContext					= WorkContext.getInstance();	//工作环境上下文	
	protected final	MultiCallBackHandler 	handler						= new MultiCallBackHandler();	//多回调目标句柄				
	
	public MultiCallBackHandler getHandler(){
		return handler;
	}
	
	/* (non-Javadoc)
	 * @see android.app.Activity#onCreate(android.os.Bundle)
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {					
		
		super.onCreate(savedInstanceState);
		handler.addCallBack(this);
				
		//如果已经开启了接收消息任务,则切换消息返回到当前活动
		if(msgHelper.isReceivingMomsg()){
			msgHelper.resetReceivingMomsg(handler);
		}		
		
	}	
	
	/* (non-Javadoc)
	 * @see android.app.Activity#onResume()
	 */
	@Override
	protected void onResume() {		
		super.onResume();
		if(msgHelper.isReceivingMomsg()){
			msgHelper.resetReceivingMomsg(handler);
		}
		//如果已经初始化，则切换定期同步类型为默认类型		
		if(msgHelper.isInitialized() && getParent() == null){
			msgHelper.changeIntervalSyncType(IntervalSyncType.DEFAULT, msgHelper.INVALID_ID);
		}
	}		
		
}
