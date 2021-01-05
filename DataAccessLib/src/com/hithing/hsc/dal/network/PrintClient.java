/**========================================
 * File:	PrintClient.java
 * Package:	com.hithing.hsc.dal.network
 * Create:	by Leopard
 * Date:	2012-2-27:下午4:16:40
 **======================================*/
package com.hithing.hsc.dal.network;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;

import android.util.Log;


/**
 * <p>PrintClient</p>
 * 	
 * <p>打印客户端抽象类</p>
 *
 * @author Leopard
 * 
 */
public final class PrintClient {				
	
	private final int			CONNECT_TIMEOUT 		= 5000;	
	private final int			MESSAGE_RESERVED_WORD	= 1;	
	private final int	 		MAX_CONNECT_TIMES 		= 5;
	private final int	 		CONNECT_INTERVAL		= 5000;
	private final int			INTEGER_MASK_CODE		= 0xff;
	
	private InetSocketAddress 	socketAddr;
	private Socket				socket;
	private DataInputStream 	in;										//网络输入数据流
	private DataOutputStream	out;									//网络输出数据流
	
	/**
	 * <p>PrintClient</p>
	 *
	 * <p>构造函数</p>
	 * @param addr 打印服务器地址,格式为192.168.1.242:12345
	 */
	public PrintClient(String addr) {
		String[] addrSet 	= addr.split(":", 2);
		socketAddr 			= new InetSocketAddress(addrSet[0], Integer.parseInt(addrSet[1]));		
	}
	
	public String getAddr(){
		return String.format("%s:%d", socketAddr.getHostName(),socketAddr.getPort());
	}
	
	public void open() throws IOException {
		for(int i = 1;i <= MAX_CONNECT_TIMES;i++){
			try {				
				socket 	= new Socket();	
				socket.connect(socketAddr, CONNECT_TIMEOUT);
				in		= new DataInputStream(socket.getInputStream());
				out		= new DataOutputStream(socket.getOutputStream());				
				return;
			} catch  (IOException e){
				Log.e("execute()", String.format("connect failed %d times!", i)); //test code
				close();		//打开套接字失败则关闭
				try {
					Thread.sleep(CONNECT_INTERVAL);
				} catch (InterruptedException ex) {
					
				}
				e.printStackTrace();
			}			
		}
		
		throw new IOException();
	}
	
	public void close(){
		if(socket != null){			
			try {						
				socket.shutdownOutput();
				socket.shutdownInput();
				socket.close();				
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			socket = null;
		}
	}
	
	public Boolean print(String value) {		
		byte[] 	buffer;	
		boolean result = true;		
		try {
			buffer = value.getBytes("GBK");
			out.writeInt(MESSAGE_RESERVED_WORD);
			out.writeInt(buffer.length);				
			out.write(buffer);
			out.flush();				
			//同步返回打印结果			
			in.readInt();					//tag=2
			in.readInt();					//length=1
			//返回
			if((in.read() & INTEGER_MASK_CODE) == 1){
				result = false; 
				Log.e("Print Result", String.valueOf(result));
			}			
		} catch (Exception e) {
			e.printStackTrace();
			result = false;
		}
		return result;		
	}

//	/* (non-Javadoc)
//	 * @see java.lang.Object#equals(java.lang.Object)
//	 */
//	@Override
//	public boolean equals(Object o) {
//		if(o == this){
//			return true;
//		}
//		if(!(o instanceof PrintClient)){
//			return false;
//		}
//		
//		PrintClient client = (PrintClient)o;
//		return client.socketAddr.equals(socketAddr);
//	}
//
//	/* (non-Javadoc)
//	 * @see java.lang.Object#hashCode()
//	 */
//	@Override
//	public int hashCode() {
//		int result = 17;
//		result = 31 * result + socketAddr.hashCode();		
//		return result;
//	}
		
}
