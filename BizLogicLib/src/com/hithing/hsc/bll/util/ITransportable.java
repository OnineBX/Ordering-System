/**========================================
 * File:	ITransportable.java
 * Package:	com.hithing.hsc.bll.util
 * Create:	by leo
 * Date:	2012-7-6:下午12:01:54
 **======================================*/
package com.hithing.hsc.bll.util;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import com.hithing.hsc.dal.network.Client;
import com.hithing.hsc.dal.network.TcpClient;

/**
 * <p>ITransportable</p>
 * 	
 * <p>传送消息接口</p>
 *
 * @author leo
 * 
 */
public interface ITransportable {
	public Client client 		= TcpClient.INSTANCE;						//通信客户端
	
	public interface IAsynSendable extends ITransportable{
		public Executor executor = Executors.newCachedThreadPool();			//发送消息用线程池
	}
}
