/**========================================
 * File:	TcpClient.java
 * Package:	com.hithing.hsc.dal.network
 * Create:	by Leopard
 * Date:	2011-12-7:上午10:46:49
 **======================================*/
package com.hithing.hsc.dal.network;

import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Observable;
import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;

import android.R.integer;
import android.util.Log;

/**
 * <p>TcpClient</p>
 * 	
 * <p>网络通讯客户端</p>
 *
 * @author Leopard
 *
 */
public enum TcpClient implements Client {
	INSTANCE;
	
	private final int				SOCKET_CONNECT_TIMEOUT		= 5000;						//Socket连接超时时间
	private final int				SOCKET_MAX_CONNECT_TIMES	= 10;						//最大连接次数		
	private final int 				MESSAGE_HEAD_LENGTH 		= 20;						//消息头长度
	private final int				MESSAGE_RESERVED_WORD 		= 1;						//保留字
	private final int 				MESSAGE_TERMINATION_VERSION = 1;						//终端版本号
	private final int				BUFFER_SIZE 				= 2048;
	
	private WorkSocket 				asynSocket;												//用于异步收发消息的套接字
	private WorkSocket				syncSocket;												//用于同步收发消息的套接字
	private WorkStream				asynStream			= new WorkStream();					//网络异步传输数据流
	private WorkStream				syncStream			= new WorkStream();					//网络同步传输数据流
	private ByteArrayOutputStream	outStreamAsyn 		= new ByteArrayOutputStream();		//保存异步输入信息的数据流
	private ByteArrayOutputStream	outStreamSync		= new ByteArrayOutputStream();		//保存同步输入信息的数据流
	
	private SocketAddress 			serverAddr;												//服务器Socket地址
	private Map<Integer,Integer>	messageQueue 	= new HashMap<Integer,Integer>();		//异步消息队列
		
	private int  					messageSeq 		= new Random().nextInt(Integer.MAX_VALUE); 	//消息流水号				
	private byte[]					buffer			= new byte[BUFFER_SIZE];
	
	TcpClient() {	
		
		asynSocket = new WorkSocket();
		syncSocket = new WorkSocket();
	}		
	
	public EoprMomsg receiveDataAsyn() throws IOException {		
		EoprMomsg resultMsg = receive(asynStream.in, outStreamAsyn);
		Log.d("inputDataFromServer", String.format("removed message %d", messageQueue.remove(resultMsg.getSeq())));	
		return resultMsg;
	}		
	
	public void sendDataAsyn(int type , byte[] data) throws IOException {
		if(messageQueue.containsValue(type)){			
			return;
		}	
		int seqValue = send(asynStream.out, type, data); 
		if(seqValue != -1){			
			//将已发消息加入到消息队列
			messageQueue.put(seqValue, type);	
			Log.d("outputDataFromServer", String.format("add message %d", type));
		}		
	}					
	
	@Override
	public void open(String host, int port) throws Exception {
		
		serverAddr = new InetSocketAddress(host, port);				
		if(!connectAsyn()){
			throw new Exception("can not connect to server!");
		}		
	}
	
	@Override
	public boolean connectAsyn() {
		if(connect(asynSocket, asynStream)){			
			messageQueue.clear();
			return true;
		}
		return false;		
	}
	
	public void closeAsyn(){
		close(asynSocket, asynStream);
	}	
		
	@Override
	public boolean connectSync() {		
		return connect(syncSocket, syncStream);
	}

	@Override
	public void closeSync() {
		close(syncSocket, syncStream);
		
	}	
	
	//与服务器之间建立Socket连接
	private boolean connect(WorkSocket wSocket, WorkStream stream){		
		for(int i = 1 ; i < SOCKET_MAX_CONNECT_TIMES ; i++){			
			try {
				Thread.sleep(5*1000);
				wSocket.create();
				wSocket.connect(serverAddr, SOCKET_CONNECT_TIMEOUT);				
				stream.create(wSocket);				
				Log.e("initInstance", String.format("TcpClient is connected at %d times!", i));
				return true;							
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} 
		}
		return false;
	}
	
	//关闭与服务器之间的socket连接
	private void close(WorkSocket wSocket, WorkStream stream){		
		try {
			stream.destory();
			wSocket.destory();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}					
	}
	
	//发送数据到服务器
	private int send(DataOutputStream out, int type, byte[] data) throws IOException{
		int seqValue = getNextMessageSequence();
		out.writeInt(MESSAGE_HEAD_LENGTH + data.length);		//写入消息长度	
		out.writeInt(type);										//写入消息类型		
		out.writeInt(seqValue);									//写入消息流水号
		out.writeInt(MESSAGE_TERMINATION_VERSION);				//写入终端版本号
		out.writeInt(MESSAGE_RESERVED_WORD);					//写入保留字
		out.write(data);										//写入消息体								
		out.flush();
		return seqValue;				
	}
	
	//从服务器接收数据
	private EoprMomsg receive(DataInputStream in, ByteArrayOutputStream outStream) throws IOException{
		int length	= in.readInt();
		int	type	= in.readInt();
		int	seq		= in.readInt();
		int	version = in.readInt();
		int	reserve	= in.readInt();			
		
		//读取消息体
		final int 				bodyLength 		= length - MESSAGE_HEAD_LENGTH;
		int						remainLength 	= bodyLength;		
		int						count			= 0;
		outStream.reset();	
		
		while(remainLength > 0){
			if(remainLength < BUFFER_SIZE){							//接收最后一个包的情况
				count = in.read(buffer , 0 , remainLength);				
			}else{				
				count = in.read(buffer); 							//接收其他包的情况									
			}			
			outStream.write(buffer, 0, count);
			remainLength = remainLength - count;
		}		
		return new EoprMomsg(type, seq, outStream.toByteArray());
	}
	
	@Override
	public EoprMomsg sendDataSyncForResult(int type, byte[] data)
			throws UnknownHostException, IOException {		
		if(send(syncStream.out, type, data) != -1){
			return receive(syncStream.in, outStreamSync);
		}
		return null;
	}
	
	@Override
	public boolean isConnectedSync() {		
		return syncSocket.isConnected();
	}	
	
	//保证产生不同的序列号:因++操作符非原子操作,故使用synchronized关键字
	private synchronized int getNextMessageSequence(){
		return messageSeq == Integer.MAX_VALUE ? messageSeq = 1 : ++messageSeq;
	}	
	
	/**
	 * <p>WorkStream</p>
	 * 	
	 * <p>工作用的流对象</p>
	 *
	 * @author leo
	 *
	 */
	private class WorkStream{
		DataInputStream 		in;				//输入流
		DataOutputStream 		out;			//输出流		
		
		/**
		 * <p>create</p>
		 *
		 * <p>创建数据流</p>
		 * 
		 * @param socket
		 * @throws IOException
		 */
		public void create(WorkSocket wSocket) throws IOException{
			in 	= new DataInputStream(wSocket.getInputStream());
			out = new DataOutputStream(wSocket.getOutputStream());
		}
		
		/**
		 * <p>destory</p>
		 *
		 * <p>销毁数据流</p>
		 * 
		 * @throws IOException
		 */
		public void destory() throws IOException{
			if(in != null){	in.close();	}
			if(out != null){ out.close(); }			
		}
	}	
	
	/**
	 * <p>WorkSocket</p>
	 * 	
	 * <p>工作用Socket</p>
	 *
	 * @author leo
	 *
	 */
	private class WorkSocket{
		private Socket socket;		
		
		public void create(){
			socket = new Socket();
		}
		
		public void connect(SocketAddress addr, int timeout) throws IOException{
			socket.connect(addr, timeout);
		}
		
		public void destory() throws IOException {
			if(socket != null){
				socket.close();
				socket = null;
				
			}			
		}
		
		public boolean isConnected(){
			if(socket != null){
				if(!socket.isClosed() && socket.isConnected()){				
					return true;
				}
			}
			return false;
		}
		
		public InputStream getInputStream() throws IOException{
			return socket.getInputStream();
		}
		
		public OutputStream getOutputStream() throws IOException{
			return socket.getOutputStream();
		}
	}
}
