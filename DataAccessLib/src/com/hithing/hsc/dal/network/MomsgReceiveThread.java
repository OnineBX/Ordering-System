/**========================================
 * File:	MomsgReceiveThread.java
 * Package:	com.hithing.hsc.dal.network
 * Create:	by Leopard
 * Date:	2011-12-12:下午03:23:06
 **======================================*/
package com.hithing.hsc.dal.network;

import java.util.concurrent.Semaphore;

import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.hithing.hsc.dal.network.Client.EoprMomsg;

/**
 * <p>MomsgReceiveThread</p>
 * 	
 * <p>接受消息线程,网络不通时改线程将被阻塞</p>
 *
 * @author Leopard
 * 
 */
public final class MomsgReceiveThread implements Runnable {	
	
	Client 		client;
	Handler 	handler;	
	/**
	 * <p>MomsgReceiveThread</p>
	 *
	 * <p>构造函数</p>
	 */
	public MomsgReceiveThread(Client client , Handler handler) {
		
		this.client		= client;
		this.handler 	= handler;
	}

	public void setHandler(Handler handler){
		this.handler = handler;
	}	
	
	/* (non-Javadoc)
	 * @see java.lang.Runnable#run()
	 */
	@Override
	public void run() {
		EoprMomsg eMsg = null; 
		while(true){			
			try {														
				eMsg = client.receiveDataAsyn();
				Message msg = new Message();
				msg.what 	= eMsg.getType();
				msg.obj		= eMsg.getMomsg();
				handler.sendMessage(msg);
			} catch (Exception e) {
				e.printStackTrace();
				client.closeAsyn();
				client.connectAsyn();			
			}
		}
	}
	
}