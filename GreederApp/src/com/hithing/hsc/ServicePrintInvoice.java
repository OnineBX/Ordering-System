/**========================================
 * File:	ServicePrintInvoice.java
 * Package:	com.hithing.hsc
 * Create:	by Leopard
 * Date:	2012-3-1:上午9:23:09
 **======================================*/
package com.hithing.hsc;

import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map.Entry;
import java.util.concurrent.Semaphore;

import com.hithing.hsc.bll.print.PrintCache;
import com.hithing.hsc.bll.print.PrintCache.PrintItem;
import com.hithing.hsc.bll.print.PrintManager;
import com.hithing.hsc.bll.print.PrintTask;
import com.hithing.hsc.bll.print.PrintManager.PrintPort;
import com.hithing.hsc.entity.OrderItemKey;
import com.hithing.hsc.entity.OrderItemValue;
import com.hithing.hsc.dataentity.ProducePrintEntity;
import com.hithing.hsc.global.StringKey;

import android.app.AlertDialog;
import android.app.IntentService;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.WindowManager;

/**
 * <p>ServicePrintInvoice</p>
 * 	
 * <p>功能描述</p>
 *
 * @author Leopard
 * 
 */
public final class ServicePrintInvoice extends IntentService {

	private final static String WORK_THEAD_NAME 			= "GreederInvoicePrint";		//工作线程名,调试时用
	private final static int	ERROR_MESSAGE_TYPE_IO 		= 1;							//IO错误消息类型编号
	
	private PrintManager	manager							= PrintManager.INSTANCE;		//打印管理者
	private PrintCache		cache 							= PrintCache.getInstance();		//打印缓存
	private PrintTask		task							= PrintTask.getInstance();		//打印任务
	
	private ProgressDialog 	waitDlg;														//打印等待对话框
	private AlertDialog		warnDialog;														//警告信息对话框	
	private Handler			handler;
	private Semaphore		mutex 							= new Semaphore(0);				//主线程与工作线程同步锁
	
	public ServicePrintInvoice() {
		super(WORK_THEAD_NAME);
		// TODO Auto-generated constructor stub
	}	

	/* (non-Javadoc)
	 * @see android.app.IntentService#onCreate()
	 */
	@Override
	public void onCreate() {		
		super.onCreate();		
		Log.e("ServicePrintInvoice", "onCreate!");		
		
		waitDlg = new ProgressDialog(this);
		waitDlg.setProgressStyle(ProgressDialog.STYLE_SPINNER);
		waitDlg.setMessage(getString(R.string.worder_dlg_print_message));
		waitDlg.setTitle(R.string.worder_dlg_print_title);
//		waitDlg.setCancelable(false);
		waitDlg.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
		
		warnDialog = new AlertDialog.Builder(ServicePrintInvoice.this).
				 /*setCancelable(false).*/
			     setPositiveButton(R.string.common_btn_retry_text, new OnClickListener() {
					
					@Override
					public void onClick(DialogInterface dialog, int which) {
						mutex.release();
					}
				}).create();
		warnDialog.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
		
		handler = new Handler(){

			/* (non-Javadoc)
			 * @see android.os.Handler#handleMessage(android.os.Message)
			 */
			@Override
			public void handleMessage(Message msg) {				
				super.handleMessage(msg);
				switch(msg.what){
				case ERROR_MESSAGE_TYPE_IO:						
					warnDialog.setTitle(msg.obj.toString());					
					break;				
				}
				Log.e("handleMessage", "show warnDialog!");				
				warnDialog.show();
			}
			
		};
	}

	/* (non-Javadoc)
	 * @see android.app.IntentService#onDestroy()
	 */
	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		waitDlg.dismiss();
	}

	/* (non-Javadoc)
	 * @see android.app.IntentService#onStartCommand(android.content.Intent, int, int)
	 */
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		waitDlg.show();
		return super.onStartCommand(intent, flags, startId);
	}

	@Override
	protected void onHandleIntent(Intent intent) {						
		String tableName = intent.getStringExtra(StringKey.INTENT_EXTRA_KEY_TABLE);
		String userName	 = intent.getStringExtra(StringKey.INTENT_EXTRA_KEY_USER);
		String orderTime = intent.getStringExtra(StringKey.INTENT_EXTRA_KEY_TIME);
		HashMap<OrderItemKey, OrderItemValue> data = 
				(HashMap<OrderItemKey, OrderItemValue>)intent.getSerializableExtra(StringKey.INTENT_EXTRA_KEY_FOOD);		
		List<Object> addedInfo = new LinkedList<Object>();
		addedInfo.add(tableName);
		addedInfo.add(orderTime);		
		addedInfo.add(userName);		
		cache.initData(addedInfo);
		
		int 		portId;
		PrintPort 	port;
		PrintItem	pItem;
		//格式化打印元数据
		for (OrderItemValue value : data.values()) {
			//根据出品部门找到与当前菜品相关的打印档口编号
			for (ProducePrintEntity ent : manager.getPrintPorts(value.getProduce())) {
				portId 	= ent.getPrint();
				port 	= manager.getPrintPort(portId);
				pItem 	= cache.get(portId);
				pItem.addData(value);
				if(port.isSeparable()){		//如果打印档口是逐条打印,则将该档口的缓存条目加入到打印任务					
					task.addTask(port.getPrinterAddr(), pItem.toString());
					pItem.prepare();
				}
			}
		}
		//将不是逐条打印的打印条目加入到打印任务
		for (Entry<Integer, PrintItem> entry : cache.entrySet()) {
			port = manager.getPrintPort(entry.getKey());
			if(!port.isSeparable()){
				pItem = entry.getValue();
				if(pItem.hasData()){
					task.addTask(port.getPrinterAddr(), entry.getValue().toString());
				}				
			}
		}		
		//清空打印缓存
		cache.clear();
		
		while(!task.isFinished()){
			try {
				task.executeSync();
			} catch (Exception e) {
				try {																								
					Message msg = new Message();
					msg.what = ERROR_MESSAGE_TYPE_IO;
					msg.obj = getString(R.string.worder_dlg_error_printer_title);
					handler.sendMessage(msg);	
					mutex.acquire();												
					
					} catch (InterruptedException ex) {

					}
			}		
		}			
		stopSelf();		
	}	

}
