/**========================================
 * File:	MomsgHelper.java
 * Package:	com.hithing.hsc.bll.manage
 * Create:	by Leopard
 * Date:	2011-12-7:上午10:46:49
 **======================================*/
package com.hithing.hsc.bll.util;

import java.io.IOException;
import java.net.UnknownHostException;
import java.util.Observable;
import java.util.Observer;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import android.R.bool;
import android.os.Handler;
import android.util.Log;

import com.hithing.hsc.entity.MoMsg.CMsgIntervalSyncReq;
import com.hithing.hsc.entity.MoMsg.CMsgIntervalSyncRsp;
import com.hithing.hsc.bll.util.ITransportable.IAsynSendable;
import com.hithing.hsc.bll.util.MomsgFactory.MomsgType;
import com.hithing.hsc.dal.network.Client;
import com.hithing.hsc.dal.network.Client.EoprMomsg;
import com.hithing.hsc.dal.network.MomsgReceiveThread;
import com.hithing.hsc.dal.network.TcpClient;

/**
 * <p>MomsgHelper</p>
 * 	
 * <p>用于处理protobuf协议的消息,如发送,接收</p>
 *
 * @author Leopard
 * 
 */
public enum MomsgHelper implements Observer, IAsynSendable{
	INSTANCE;
	
	public final int 			INVALID_ID		= 0;							//定期同步协议的非法标识
	
	private ExecutorService		receiveExecutor;								//接收消息执行线程		
	
	private TimerTask 			intervalTask	= null;							//定期同步任务
	private Timer 				interval		= new Timer();					//定时器
	
	private MomsgReceiveThread	recMomsgThread;
	private CMsgIntervalSyncReq isReq;											//定期同步消息				
	
	/**
	 * <p>MomsgHelper</p>
	 *
	 * <p>构造函数</p>
	 */	
	
	MomsgHelper() {
		receiveExecutor = Executors.newSingleThreadExecutor();
	}
	
	@Override
	public void update(Observable observable, Object data) {
		
			startIntervalSync();	
	}
	
	/**
	 * <p>initInstance</p>
	 *
	 * <p>初始化MomsgHelper实例</p>
	 * 
	 * @param host 服务器主机
	 * @param port 服务器端口
	 * @throws Exception 
	 */
	public void initInstance(String host , int port) throws Exception{		
		client.open(host, port);
		CMsgIntervalSyncReq.Builder builder = MomsgFactory.createMomsgBuilder(MomsgType.INTERVALSYNC_REQ);
		isReq = builder.setType(IntervalSyncType.DEFAULT.getValue()).build();
	}	
	
	/**
	 * <p>isInitialized</p>
	 *
	 * <p>MomsgHelper实例是否已经初始化</p>
	 * 
	 * @return
	 */
	public boolean isInitialized(){
		return isReq != null;
	}
	
	/**
	 * <p>clearInstance</p>
	 *
	 * <p>清空MomsgHelper实例</p>
	 *
	 */
	public void clearInstance(){
		
		client.closeAsyn();		
	}	
	
	/**
	 * <p>startReceivingMomsg</p>
	 *
	 * <p>开启接收消息线程</p>
	 * 
	 * @param handler 将接收的消息发送给当前Greeder活动的句柄
	 */
	public void startReceivingMomsg(Handler handler){
		recMomsgThread 	= new MomsgReceiveThread(client, handler);		
		receiveExecutor.execute(recMomsgThread);		
	}
	
	/**
	 * <p>resetReceivingMomsg</p>
	 *
	 * <p>进入不同Greeder活动时,重置用于处理接收消息的句柄</p>
	 * 
	 * @param handler 将接收的消息发送给当前Greeder活动的句柄
	 */
	public void resetReceivingMomsg(Handler handler){
		recMomsgThread.setHandler(handler);
	}
	
	/**
	 * <p>isReceivingMomsg</p>
	 *
	 * <p>是否已经开始接收消息</p>
	 * 
	 * @return 已开始接收消息返回真,否则返回假
	 */
	public boolean isReceivingMomsg(){
		
		return (recMomsgThread == null)?false:true;
	}
	
	/**
	 * <p>startIntervalSync</p>
	 *
	 * <p>开启定期同步消息,同时作为TCP连接的心跳包,保持TCP长连接</p>
	 *
	 */
	public void startIntervalSync(){
					
		if(intervalTask != null){
			intervalTask.cancel();
		}										
		intervalTask = new TimerTask() {
			
			@Override
			public void run() {
				Log.d("MomsgHelper.startIntervalSync", "INTERVALSYNC_REQ,type=" + isReq.getType());
				try {
					client.sendDataAsyn(MomsgType.INTERVALSYNC_REQ.getValue(), isReq.toByteArray());					
				} catch (UnknownHostException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					
				}
			}
		};						
		
		interval.schedule(intervalTask, 0, 5000);						
	}
	
	/**
	 * <p>changeIntervalSyncType</p>
	 *
	 * <p>更改定期同步的类型</p>
	 * 定期同步是一个通用消息,始终处于周期性发送状态,定期同步为每个界面返回的消息不同,根据type来区分.
	 * 
	 * @param type 类型
	 * @param param 参数标识
	 */
	public void changeIntervalSyncType(IntervalSyncType type, int param){
		CMsgIntervalSyncReq.Builder builder = CMsgIntervalSyncReq.newBuilder(isReq);
		builder.setType(type.getValue());
		builder.setId(param);
		isReq = builder.build();
	}
	
	/**
	 * <p>sendIntervalSyncForResult</p>
	 *
	 * <p>同步收发定期同步消息，当定期同步协议用于初始化某些数据时使用</p>
	 * 
	 * @param isClose 是否在同步结束后关闭连接
	 * @return 定期同步返回消息
	 */
	public CMsgIntervalSyncRsp sendIntervalSyncForResult(boolean isClose){		
		try {
			if(!client.isConnectedSync()){
				client.connectSync();
			}
			EoprMomsg msg = client.sendDataSyncForResult(MomsgType.INTERVALSYNC_REQ.getValue(), isReq.toByteArray());			
			if(isClose){
				client.closeSync();
			}
			return (CMsgIntervalSyncRsp)MomsgFactory.createMomsg(MomsgType.INTERVALSYNC_RSP, msg.getMomsg());
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
	
	/**
	 * <p>IntervalSyncType</p>
	 * 	
	 * <p>定期同步类型:用于标识不同界面时的定期同步类型</p>
	 *
	 * @author Leopard
	 *
	 */
	public enum IntervalSyncType{
		DEFAULT(1),				//默认类型,定期同步实现心跳机制,不返回业务数据
		TABLE(2),				//餐台类型,返回餐台状态业务数据
		CASH(3);				//收银类型，返回收银账单数据
		
		private int value;		//枚举变量值
		private IntervalSyncType(int value) {
			this.value = value;
		}
		
		public int getValue(){ return value; }
	}
	
}
