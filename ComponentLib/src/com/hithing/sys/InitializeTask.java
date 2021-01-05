/**========================================
 * File:	InitializeTask.java
 * Package:	com.hithing.sys
 * Create:	by Leopard
 * Date:	2012-2-28:上午9:37:57
 **======================================*/
package com.hithing.sys;


import com.google.protobuf.GeneratedMessage;
import com.hithing.hsc.bll.android.GreederSettingsHelper;
import com.hithing.hsc.bll.android.ResourceVersionHelper;
import com.hithing.hsc.bll.control.SystemCtrl;
import com.hithing.hsc.bll.manage.ResourceVersionManager;
import com.hithing.hsc.bll.util.MomsgHelper;
import com.hithing.hsc.component.R;
import com.hithing.hsc.entity.MoMsg.CMsgLoginRsp;
import com.hithing.hsc.entity.MoMsg.CMsgRefreshResRsp;
import com.hithing.hsc.entity.MoMsg.CMsgRefreshResReq.CMsgTermVersion;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.res.Resources;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;

/**
 * <p>InitializeTask</p>
 * 	
 * <p>初始化异步任务</p>
 *
 * @author Leopard
 * 
 */
public final class InitializeTask extends AsyncTask<Handler, 
													String, com.hithing.sys.InitializeTask.InitResult<GeneratedMessage>> {
	private final	int				MOMSG_RSP_STATUS_SUCCESS 	= 1;		//成功返回消息状态
	private final	int				MAX_INIT_TIMES				= 10;		//最大验证次数
	private final	int				INTERVAL_TIME				= 5*1000;	//每次验证间隔时间
	
	private Context					context;								//运行上下文
	private ProgressDialog 			initDlg;								//初始化对话框	
	private GreederSettingsHelper 	gsHelper;
	private MomsgHelper				msgHelper					= MomsgHelper.INSTANCE;
	private Handler					handler;
	
	/**
	 * <p>InitializeTask</p>
	 *
	 * <p>构造函数</p>
	 * @param context 应用程序上下文
	 * @param handler 用于回调的句柄
	 */
	public InitializeTask(Context context, Handler handler) {
		this.context = context;
		this.handler = handler;				
		gsHelper	 = GreederSettingsHelper.INSTANCE;
		initDlg 	 = new ProgressDialog(context);
		initDlg.setTitle(R.string.comlib_dlg_init_title);
		initDlg.setProgressStyle(ProgressDialog.STYLE_SPINNER);
		
	}

	/* (non-Javadoc)
	 * @see android.os.AsyncTask#onPreExecute()
	 */
	@Override
	protected void onPreExecute() {
		initDlg.show();													
	}

	/* (non-Javadoc)
	 * @see android.os.AsyncTask#onPostExecute(java.lang.Object)
	 */
	@Override
	protected void onPostExecute(InitResult<GeneratedMessage> result) {
		initDlg.dismiss();			
		Message msg = new Message();
		msg.what = result.getResultType().getValue();
		msg.obj = result.getResultMsg();
		handler.sendMessage(msg);
	}

	/* (non-Javadoc)
	 * @see android.os.AsyncTask#onProgressUpdate(Progress[])
	 */
	@Override
	protected void onProgressUpdate(String... values) {
		initDlg.setMessage(values[0]);
	}

	@Override
	protected InitResult<GeneratedMessage> doInBackground(Handler... params) {		
											
		try {
			Resources resources	= context.getResources();		
			String defValueStr 	= resources.getString(R.string.default_null_string);
			int	   defValueInt 	= resources.getInteger(R.integer.default_null_integer); 
			//与服务器建立连接			
			publishProgress(resources.getString(R.string.comlib_dlg_init_message_connect));			
			String 	host = gsHelper.getServer(defValueStr);				
			int 	port = gsHelper.getPort(defValueInt);			
			msgHelper.initInstance(host, port);						
			
			
			//登录
			publishProgress(resources.getString(R.string.comlib_dlg_init_message_login));

			CMsgLoginRsp loginRsp = null;			
			SystemCtrl sysCtrl = new SystemCtrl(gsHelper.getCompany(defValueStr), 
					gsHelper.getTerminal(defValueStr), gsHelper.getAuthCode(defValueStr));
			int i;
			for(i = 0; i < MAX_INIT_TIMES; i++){		
				loginRsp = sysCtrl.loginForResult();
				if(loginRsp != null){
					if(loginRsp.hasStatus()){
						if(loginRsp.getStatus() != MOMSG_RSP_STATUS_SUCCESS){
							return new InitResult<GeneratedMessage>(InitResultType.login, loginRsp);				
						}
						break;
					}
				}
				//延迟一段时间后,再次尝试
				Thread.sleep(INTERVAL_TIME);
			}
			//i达到最大连接次数,表明登录未成功,则返回
			if(i == MAX_INIT_TIMES){
				return new InitResult<GeneratedMessage>(InitResultType.login, loginRsp);
			}
											
			//登录成功则刷新数据
			publishProgress(resources.getString(R.string.comlib_dlg_init_message_refresh));
			ResourceVersionHelper rvHelper = new ResourceVersionHelper(context); 
			CMsgTermVersion version = CMsgTermVersion.newBuilder().
					  								  setHallSort(rvHelper.getHallSortVersion(defValueInt)).
					  								  setHall(rvHelper.getHallVersion(defValueInt)).
					  								  setDinTabType(rvHelper.getDinnerTableTypeVersion(defValueInt)).
					  								  setDinTable(rvHelper.getDinnerTableVersion(defValueInt)).
					  								  setFoodMainSort(rvHelper.getFoodMainSortVersion(defValueInt)).
					  								  setHallFmsort(rvHelper.getHallFoodMainSortVersion(defValueInt)).
					  								  setFoodSubSort(rvHelper.getFoodSubSortVersion(defValueInt)).
					  								  setFoodUnit(rvHelper.getFoodUnitVersion(defValueInt)).
					  								  setFood(rvHelper.getFoodVersion(defValueInt)).
					  								  setFoodPrice(rvHelper.getFoodPriceVersion(defValueInt)).
					  								  setFoodRecType(rvHelper.getFoodRecipeSortVersion(defValueInt)).
					  								  setFoodRecipe(rvHelper.getFoodRecipeVersion(defValueInt)).
					  								  setFoodFoodRecType(rvHelper.getFoodFrsortVersion(defValueInt)).					  								  					  								 
					  								  setFoodDemand(rvHelper.getFoodDeliveryVersion(defValueInt)).
					  								  setOpeReason(rvHelper.getOperateReasonVersion(defValueInt)).
					  								  setProsecPort(rvHelper.getProducePrintVersion(defValueInt)).
					  								  setTimeItem(rvHelper.getTimeItemVersion(defValueInt)).
					  								  setDinTabTypeTimeItem(rvHelper.getDttypeTitemVersion(defValueInt)).
					  								  build();
			CMsgRefreshResRsp resRsp = null;
			for(i = 0; i < MAX_INIT_TIMES; i++){
				resRsp = ResourceVersionManager.refreshResourceForResult(version);
				if(resRsp != null){
					if(resRsp.hasStatus()){
						if(resRsp.getStatus() == MOMSG_RSP_STATUS_SUCCESS){
							break;
						}
					}						
				}
				resRsp = null;
				//延迟一段时间后,再次尝试
				Thread.sleep(INTERVAL_TIME);
			}
			//同步接收资源刷新消息成功后,开启接收消息线程
			if(i != MAX_INIT_TIMES){
				
				publishProgress(resources.getString(R.string.comlib_dlg_init_message_receiving));
								
				//开启接收线程和定期同步消息
				if(!msgHelper.isReceivingMomsg()){
					msgHelper.startReceivingMomsg(params[0]);
				}				
				msgHelper.startIntervalSync();
				
			}
								
						
			return new InitResult<GeneratedMessage>(InitResultType.resource, resRsp);
			
		} catch (Exception e) {
			e.printStackTrace();
			return new InitResult<GeneratedMessage>(InitResultType.connect, null);
		}
		
		
	}
	
	/**
	 * <p>InitResultType</p>
	 * 	
	 * <p>初始化返回结果类型</p>
	 *
	 * @author Leopard
	 *
	 */
	public enum InitResultType {
		none(0), connect(1), login(2), resource(3);
		
		private int value;
		InitResultType(int value){
			this.value = value;
		}
		public int	getValue() { return value; }
		
		public static InitResultType valueOf(int value){
			switch (value) {
			case 1:				
				return connect;
			case 2:
				return login;
			case 3:
				return resource;
			default:
				return none;
			}
		}
	}
	
	public final static class InitResult<T extends GeneratedMessage>{
		private InitResultType 	resultType;
		private T				resultMsg;
		
		public InitResult(InitResultType type, T msg){
			resultType 	= type;
			resultMsg 	= msg;
		}
		
		public InitResultType getResultType(){
			return resultType;
		}
		
		public T getResultMsg(){
			return resultMsg;
		}
	}

}
