/**========================================
 * File:	InitializeCash.java
 * Package:	com.hithing.sys
 * Create:	by leo
 * Date:	2012-7-6:下午2:15:24
 **======================================*/
package com.hithing.sys;

import com.hithing.hsc.bll.android.AndGrdDaoManager;
import com.hithing.hsc.bll.control.CashCtrl;
import com.hithing.hsc.bll.control.UserCtrl;
import com.hithing.hsc.bll.control.UserCtrl.AuthenticateType;
import com.hithing.hsc.bll.manage.DinnerTableManager;
import com.hithing.hsc.bll.manage.GreederDaoManager;
import com.hithing.hsc.bll.util.CashContext;
import com.hithing.hsc.bll.util.MomsgHelper;
import com.hithing.hsc.component.R;
import com.hithing.hsc.entity.MoMsg.CMsgAuthvalidateRsp;
import com.hithing.hsc.entity.MoMsg.CMsgIntervalSyncRsp;
import com.hithing.hsc.entity.MoMsg.CMsgIntervalSyncRsp.CMsgBillTable;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.res.Resources;
import android.os.AsyncTask;

/**
 * <p>InitializeCash</p>
 * 	
 * <p>功能描述</p>
 *
 * @author leo
 * 
 */
public final class InitializeCash extends AsyncTask<Integer, String, CashCtrl> {
	private final int 		MOMSG_RSP_STATUS_SUCCESS 	= 1;		//成功返回消息状态
	
	private Context			context;								//运行上下文
	private ProgressDialog 	initDlg;								//初始化过程对话框	
	
	public InitializeCash(Context context){
		this.context = context;
		initDlg 	 = new ProgressDialog(context);
		initDlg.setTitle(R.string.comlib_dlg_initcash_title);
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
	 * @see android.os.AsyncTask#onProgressUpdate(Progress[])
	 */
	@Override
	protected void onProgressUpdate(String... values) {
		initDlg.setMessage(values[0]);
	}

	/* (non-Javadoc)
	 * @see android.os.AsyncTask#onPostExecute(java.lang.Object)
	 */
	@Override
	protected void onPostExecute(CashCtrl result) {
		initDlg.dismiss();
		if(context instanceof INeedInitializable){
			((INeedInitializable)context).doPostInitialized(result);
		}
	}

	@Override
	protected CashCtrl doInBackground(Integer... params) {		
		Resources 	resources 	= context.getResources();
		CashContext cashContext = CashContext.INSTANCE;
		
		//验证用户权限
		publishProgress(resources.getString(R.string.comlib_dlg_initcash_message_authcode));
		int authCode = params[0];
		UserCtrl userCtrl = new UserCtrl(authCode);
		CMsgAuthvalidateRsp avRsp = userCtrl.validateAuthForResult(AuthenticateType.AUTH_TYPE_CASH,false);
		if(avRsp.hasStatus()){
			if(avRsp.getStatus() != MOMSG_RSP_STATUS_SUCCESS){
				return null;
			}
		}
		
		cashContext.setCurrentCasher(authCode, avRsp.getWorkname().toStringUtf8());		
		
		//初始化收银环境
		publishProgress(resources.getString(R.string.comlib_dlg_initcash_message_context));
		cashContext.initializeSync(false);
		
		final GreederDaoManager grdManager = new AndGrdDaoManager(context);
		DinnerTableManager 	tableManager = new DinnerTableManager(grdManager.getDinnerTableDao());
		CashCtrl cashCtrl       = new CashCtrl(cashContext.getCashSpotId(),cashContext.getCurrentCasherId(), tableManager);
		
		//初始化收银账单
		publishProgress(resources.getString(R.string.comlib_dlg_initcash_message_bill));
		CMsgIntervalSyncRsp rsp = MomsgHelper.INSTANCE.sendIntervalSyncForResult(true);	
		if(rsp != null){
			if(rsp.hasStatus()){
				if(rsp.getStatus() == MOMSG_RSP_STATUS_SUCCESS){
					cashCtrl.clearCashItems();
					for (CMsgBillTable table : rsp.getBillTableList()) {					
						cashCtrl.loadCashItem(table.getBillid(), table.getTableid());				
					}					
				}
			}
		}		
		return cashCtrl;		
	}

}
