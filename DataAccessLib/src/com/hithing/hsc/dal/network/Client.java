/**========================================
 * File:	Client.java
 * Package:	com.hithing.hsc.dal.network
 * Create:	by Leopard
 * Date:	2011-12-7:下午02:18:24
 **======================================*/
package com.hithing.hsc.dal.network;

import java.io.IOException;
import java.net.UnknownHostException;
import java.util.Observer;

import android.R.bool;


/**
 * <p>Client</p>
 * 	
 * <p>网络通信客户端接口</p>
 *
 * 为TCP客户端或UDP客户端提供统一的调用接口
 * 
 * @author Leopard
 * 
 */
public interface Client {
	
	/**
	 * 
	 * <p>sendDataAsync</p>
	 *
	 * <p>发送字节到服务器</p>
	 * 
	 * @param bytes
	 * @throws IOException 
	 * @throws UnknownHostException 
	 */
	public void sendDataAsyn(int type , byte[] data) throws UnknownHostException, IOException;
	
	/**
	 * <p>outputBytesToServer</p>
	 * 
	 * <p>从服务器异步接收字节</p>
	 * 
	 * @return 返回可操作的消息
	 * @throws IOException 
	 */
	public EoprMomsg receiveDataAsyn() throws IOException;
	
	/**
	 * <p>sendDataSync</p>
	 *
	 * <p>同步发送字节到服务器</p>
	 * 
	 * @param type
	 * @param data
	 * @throws UnknownHostException
	 * @throws IOException
	 */
	public EoprMomsg sendDataSyncForResult(int type , byte[] data) throws UnknownHostException, IOException;	
	
	/**
	 * <p>open</p>
	 * 
	 * <p>打开异步或异步客户端到服务器的连接</p>
	 * 
	 * @param host 服务器主机名
	 * @param port 服务器端口号
	 * @throws Exception 
	 * @throws IOException
	 */
	public void open(String host , int port) throws Exception;
	
	/**
	 * <p>closeAsyn</p>
	 * 
	 * <p>关闭异步客户端到服务器的连接</p>
	 * 
	 */
	public void closeAsyn();
	
	/**	 
	 * <p>connectAsyn</p>
	 *
	 * <p>建立异步客户端到服务器的连接</p>
	 * @return 
	 *
	 */
	public boolean connectAsyn();		
	/**
	 * <p>connectSync</p>
	 *
	 * <p>建立同步客户端到服务器的连接</p>
	 * 
	 * @return
	 */
	public boolean connectSync();
	
	/**
	 * <p>closeSync</p>
	 *
	 * <p>关闭同步客户端到服务器的连接</p>
	 *
	 */
	public void closeSync();
	
	/**
	 * <p>isConnectedSync</p>
	 *
	 * <p>同步客户端是否连接</p>
	 * 
	 * @return 连接返回true，未连接返回false
	 */
	public boolean isConnectedSync();
	
	/**
	 * <p>EoprMomsg</p>
	 * 	
	 * <p>封装的用于操作的protobuf元数据</p>
	 *
	 * @author Leopard
	 *
	 */
	public static class EoprMomsg{
		
		private int type;
		private int seq;
		private byte[] momsg;
		
		public EoprMomsg(int type , int seq , byte[] momsg) {
			
			this.type 	= type;
			this.seq 	= seq;
			this.momsg	= momsg;
			
		}
		
		public int getType(){
			return type;
		}
		
		public int getSeq(){
			return seq;
		}
		
		public byte[] getMomsg(){
			return momsg;
		}
	}
		
}
