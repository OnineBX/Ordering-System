/**========================================
 * File:	ProtobufActivityGroup.java
 * Package:	com.hithing.app
 * Create:	by Leopard
 * Date:	2012-3-15:下午7:11:38
 **======================================*/
package com.hithing.app;

import com.hithing.app.MultiCallBackHandler.IMomsgResultable;

import android.app.Activity;
import android.app.ActivityGroup;
import android.app.LocalActivityManager;
import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

/**
 * <p>ProtobufActivityGroup</p>
 * 	
 * <p>继承自该活动组的活动组可以处理使用protobuf协议定义的消息</p>
 *	使用当前活动的handler处理消息
 * @author Leopard
 * 
 */
public abstract class ProtobufActivityGroup extends ActivityGroup implements IMomsgResultable{		
	protected final	int				MOMSG_RSP_STATUS_SUCCESS 	= 1;
	
	private LocalActivityManager 	actManager = getLocalActivityManager();	
	
	public void startActivityInContainer(ViewGroup container, String id, Intent intent){
		Activity curActivity = getCurrentActivity();		
		
		//当前活动为空或为新活动的情况
		if(curActivity == null || curActivity.getIntent() != intent){			
			View child = actManager.startActivity(id,intent).getDecorView();
			container.removeAllViews();
			container.addView(child);			
			curActivity = getCurrentActivity();						
			if(curActivity instanceof ProtobufActivity){
				((ProtobufActivity)curActivity).getHandler().addCallBack(this);
			}
		}else {
			actManager.startActivity(id,intent);			
		}
	}		

	/**
	 * <p>IChildResultHandlable</p>
	 * 	
	 * <p>可处理子活动返回结果接口</p>
	 *
	 * @author Leopard
	 *
	 */
	public interface IChildResultHandlable{
		void onChildActivityResult(int childResultCode, Intent data);
	}
		
}
