/**========================================
 * File:	PrintTask.java
 * Package:	com.hithing.hsc.bll.print
 * Create:	by Leopard
 * Date:	2012-3-12:上午10:47:49
 **======================================*/
package com.hithing.hsc.bll.print;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.Callable;
import java.util.concurrent.CompletionService;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorCompletionService;
import java.util.concurrent.Executors;

import android.util.Log;

import com.hithing.hsc.bll.print.PrintManager.PrintPort;
import com.hithing.hsc.dal.network.PrintClient;

/**
 * <p>PrintTask</p>
 * 	
 * <p>打印任务,一个打印机对应一个打印任务</p>
 *
 * @author Leopard
 * 
 */
public final class PrintTask extends HashMap<String, LinkedList<String>> {
	private final int					MAX_TRY_PRINT_TIMES = 5;					//尝试打印的最大次数
	private static final 				PrintTask INSTANCE 	= new PrintTask();
	
	private HashSet<String> 			taskablePrinterSet;							//有任务的打印机集合	
	
	private Executor					executor;									//执行打印任务的执行器
	private CompletionService<Boolean>	comService;									//可返回结果的并发服务
	
	/**
	 * <p>getInstance</p>
	 *
	 * <p>得到打印任务的单件实例</p>
	 * 
	 * @param 打印机地址集合
	 * @return
	 */
	public static PrintTask getInstance(){		
		return INSTANCE;
	}
	
	private PrintTask() {		
		taskablePrinterSet = new HashSet<String>();			
	}
	
	/**
	 * <p>initInstance</p>
	 *
	 * <p>初始化打印任务实例</p>
	 * 
	 * @param addrs
	 */
	public void initInstance(Collection<PrintPort> addrs){
		clear();
		//在此初始化打印队列
		String addr;
		for (PrintPort port : addrs) {
			addr = port.getPrinterAddr();
			if(!containsKey(addr)){
				put(addr, new LinkedList<String>());				
			}			
		}
		//初始化打印任务执行器
		executor = Executors.newFixedThreadPool(size());
		comService = new ExecutorCompletionService<Boolean>(executor);
	}
	
	/**
	 * <p>addTask</p>
	 *
	 * <p>为指定打印机添加打印任务</p>
	 * 
	 * @param printerAddr 打印机地址
	 * @param content	  打印任务内容
	 */
	public void addTask(String printerAddr, String content){
		taskablePrinterSet.add(printerAddr);
		get(printerAddr).add(content);			
	}
	
	/**
	 * <p>execute</p>
	 *
	 * <p>同步执行打印任务,有多个打印机时,按顺序打印</p>
	 * @throws Exception 
	 *
	 */
	public void execute() throws Exception{
		
		Queue<String> dataList;	
		PrintClient client = null;
		String data;
		int printTimes = 0;
		final HashSet<String> printerSet = (HashSet<String>) taskablePrinterSet.clone();
		for (String printer : printerSet) {
			try {				
				Log.e("printTask.execute", String.format("%s is printing!", printer));
				client = new PrintClient(printer);
				dataList = get(printer);			
				client.open();
				while(!dataList.isEmpty()){
					synchronized (PrintTask.this) {
						data = dataList.poll();
						while(printTimes < MAX_TRY_PRINT_TIMES){						
							if(client.print(data)){		//如果打印成功							
								printTimes = 0;			//重置尝试打印次数
								break;
							}
							//打印不成功,则打印次数增加一次,继续尝试打印
							printTimes++;
						}
						//如果尝试了最大尝试次数后仍未成功打印,则将打印数据重新加入到队列,并抛出异常
						if(printTimes == MAX_TRY_PRINT_TIMES){
							dataList.offer(data);		//数据重新加入到队列
							throw new Exception();
						}	
					}									
				}								
				Log.e("execute", "ok!");
				// 成功打印所有任务则在具有打印任务的打印机集合中移除该项
				taskablePrinterSet.remove(printer);
			} finally {
				if(client != null){
					client.close();
				}					
			}		
		}						
	}
	
	/**
	 * <p>executeSync</p>
	 *
	 * <p>并发执行任务,有多个打印机时,同时打印</p>
	 * @throws Exception 
	 *
	 */
	public void executeSync() throws Exception{	
		
		final HashSet<String> printerSet = (HashSet<String>) taskablePrinterSet.clone();
		Log.e("printerSet.size", String.valueOf(printerSet.size()));
		for (final String printer : printerSet) {						
			comService.submit(new Callable<Boolean>() {
								
				@Override
				public Boolean call() throws Exception {								
					PrintClient client = new PrintClient(printer);
					try {
						Log.e("printTask.execute", String.format("%s is printing!", printer));
						
						//构建打印内容
						Queue<String> dataList = get(printer);						
						StringBuilder printData = new StringBuilder();
						for (String data : dataList) {
							printData.append(data);
						}						
						
						//执行打印动作
						int printTimes = 0;					//当前尝试次数
						client.open();
						while(printTimes < MAX_TRY_PRINT_TIMES){						
							if(client.print(printData.toString())){		//如果打印成功,清空队列并结束循环
								synchronized (PrintTask.this){
									dataList.clear();
								}
								// 成功打印所有任务则在具有打印任务的打印机集合中移除该项
								taskablePrinterSet.remove(printer);
								Log.e("executeSync", "ok!");
								return true;
							}
							//打印不成功,则打印次数增加一次,继续尝试打印
							printTimes++;
						}					
						//如果尝试了最大尝试次数后仍未成功打印,则返回假,打印失败													
						return false;																
					}catch (Exception e) {
						return false;
					}finally {
						if(client != null){
							client.close();
						}					
					}						
				}									
			});						
		}		
		Log.e("executeSync", "waiting for task finished!");
		
		//阻塞executeSync,直到打印线程结束
		boolean result = true;
		for(int i = 0;i < printerSet.size();i++){
			if(!comService.take().get()){
				result = false;
			}			
			Log.e("executeSync", String.format("Task %d result is %b!", i,result));
		}
		if(!result){
			throw new Exception();
		}
	}
	
	/**
	 * <p>isFinished</p>
	 *
	 * <p>任务是否完成</p>
	 * 
	 * @return
	 */
	public synchronized boolean isFinished(){
		int count = 0;
		for (LinkedList<String> list : values()) {
			count += list.size();
		}		
		return count == 0;
	}
	
}
