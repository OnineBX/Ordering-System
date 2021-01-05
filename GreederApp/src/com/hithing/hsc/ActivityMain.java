package com.hithing.hsc;

import java.util.Observable;
import java.util.Observer;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.res.Resources;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.google.protobuf.GeneratedMessage;
import com.hithing.app.ProtobufActivity;
import com.hithing.hsc.ActivityAuthcode.NeedAuthcodeType;
import com.hithing.hsc.bll.android.AndGrdDaoManager;
import com.hithing.hsc.bll.android.GreederSettingsHelper;
import com.hithing.hsc.bll.android.ResourceVersionHelper;
import com.hithing.hsc.bll.control.UserCtrl;
import com.hithing.hsc.bll.control.UserCtrl.AuthenticateType;
import com.hithing.hsc.bll.manage.FoodMainSortManager;
import com.hithing.hsc.bll.manage.GreederDaoManager;
import com.hithing.hsc.bll.manage.HallManager;
import com.hithing.hsc.bll.manage.ResourceVersionManager;
import com.hithing.hsc.bll.util.CashContext;
import com.hithing.hsc.bll.util.CompoundItem;
import com.hithing.hsc.bll.util.MomsgFactory.MomsgType;
import com.hithing.hsc.bll.util.MomsgHelper.IntervalSyncType;
import com.hithing.hsc.bll.util.WorkContext.UpdateType;
import com.hithing.hsc.entity.MoMsg.CMsgAuthvalidateRsp;
import com.hithing.hsc.entity.MoMsg.CMsgRefreshResRsp;
import com.hithing.hsc.global.StringKey;
import com.hithing.sys.InitializeTask;
import com.hithing.sys.InitializeTask.InitResultType;

public class ActivityMain extends ProtobufActivity implements OnClickListener, Observer{	
	
	private GreederDaoManager 		grdManager;
	private ResourceVersionHelper 	rvHelper;													//资源版次刷新帮助者
	private GreederSettingsHelper 	gsHelper;
	private WorkDataCache			wDataCache;													//工作数据缓存
	private Resources				resources;
	private LayoutInflater			inflater;	
	
	private final int				DIALOG_TYPE_ERROR 	 	= 1;
	private final int				DIALOG_TYPE_FAILED	 	= DIALOG_TYPE_ERROR    	 + 1;
	private final int				DIALOG_TYPE_INIT_BASE	= DIALOG_TYPE_FAILED   	 + 1;	//对话框类型-基本初始设置
	private final int               DIALOG_TYPE_INIT_AUTH   = DIALOG_TYPE_INIT_BASE  + 1;   //对话框类型-授权初始设置
	private final int				DIALOG_TYPE_ABOUT	 	= DIALOG_TYPE_INIT_AUTH  + 1;	//对话框类型-关于

	private final int				REQUEST_CODE_ACTAU			= 1;						//活动请求码-授权		
	
	private View					vInit;													//初始化对话框视图
	private View                    vLogin;                                                 //用于登陆的对话框视图
	
	private Intent					auIntent				= new Intent();					//用于启动授权活动	
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);        
        grdManager 	= new AndGrdDaoManager(this);
        rvHelper 	= new ResourceVersionHelper(this);
        resources	= getResources();
        gsHelper	= GreederSettingsHelper.INSTANCE;
        gsHelper.initInstance(this);					//初始化	GreederSettingsHelper实例  ,单件只初始化一次即可
        wDataCache	= WorkDataCache.INSTANCE;
        wDataCache.initInstance(this);
      
        inflater	= LayoutInflater.from(this);
        vInit		= inflater.inflate(R.layout.act_main_initial_setting, null, false); 
        vLogin      = inflater.inflate(R.layout.act_main_initialdlg_content, null, false);
        
                
        if(isFirstRun()){
        	showDialog(DIALOG_TYPE_INIT_BASE);
        }else{
        	initApplication();
        	InitializeTask initTask = new InitializeTask(ActivityMain.this, createInitHandler());
        	initTask.execute(handler);
        }         
               
    }           

	public void onSwitch(View v) {
        String text = ((Button)v).getText().toString();           
        Intent intent = null;        
        if(text == getString(R.string.main_btn_stock_text)){        	
        	intent = new Intent(this,ActivityStock.class);         	
        }
        
        if(text == getString(R.string.main_btn_waiter_text)){
        	intent = new Intent(this,ActivityDinnerTable.class);
        }
        
        if(text == getString(R.string.main_btn_serving_text)){            	
        	return;
        }
        
        if(text == getString(R.string.main_btn_cashier_text)){  
        	auIntent.setClass(this, ActivityAuthcode.class);        	
        	auIntent.putExtra(StringKey.INTENT_EXTRA_KEY_TYPE, NeedAuthcodeType.cash);
        	startActivityForResult(auIntent, REQUEST_CODE_ACTAU);
        	return;
        }
        
        startActivity(intent);        
    }
    
    @Override
	public void onClick(DialogInterface dialog, int which) {
    	switch(which){		
		case -2:															//点击取消按钮
			android.os.Process.killProcess(android.os.Process.myPid());		//关闭应用程序
			break;
		}
		
	}

    @Override
	public void update(Observable observable, Object data) {
    	if(((UpdateType)data) == UpdateType.HALL_INFO){
    		loadDataInBackground();
    	}
		
	}
    
	/* (non-Javadoc)
	 * @see android.app.Activity#onCreateDialog(int)
	 */
	@Override
	protected Dialog onCreateDialog(int id) {
		Dialog dlg;		
		
		switch(id){
			case DIALOG_TYPE_ERROR:
				dlg = new AlertDialog.Builder(this).setTitle(R.string.login_dlg_error_title).				
						setNegativeButton(R.string.common_btn_ok_text, this).create(); 
				return dlg;							
			case DIALOG_TYPE_FAILED:
				dlg = new AlertDialog.Builder(this).setTitle(R.string.login_dlg_failed_title).						
						setPositiveButton(R.string.common_btn_ok_text, new OnClickListener() {
							
							@Override
							public void onClick(DialogInterface dialog, int which) {
								
								if(isFirstRun()){
									showDialog(DIALOG_TYPE_INIT_BASE);
								}else{
									showDialog(DIALOG_TYPE_INIT_AUTH);
								}
								
							}
						}).
						create(); 
				return dlg;			
			case DIALOG_TYPE_INIT_BASE:
				dlg = new AlertDialog.Builder(this).setTitle(R.string.init_dlg_title).
						setView(vInit).setNegativeButton(R.string.common_btn_cancel_text, this).
						setPositiveButton(R.string.common_btn_next_text, new OnClickListener() {
					
							@Override
							public void onClick(DialogInterface dialog, int which) {
								String host = ((EditText)vInit.findViewById(R.id.init_edt_server)).getText().toString();
								String port = ((EditText)vInit.findViewById(R.id.init_edt_port)).getText().toString();
								String shop = ((EditText)vInit.findViewById(R.id.init_edt_shop)).getText().toString();
								if(TextUtils.isEmpty(host) 
								   || TextUtils.isEmpty(port)
								   || TextUtils.isEmpty(shop)){
									showDialog(DIALOG_TYPE_FAILED);
								}else{
									gsHelper.setHost(host);
									gsHelper.setPort(Integer.parseInt(port));
									gsHelper.setCompany(shop);
									showDialog(DIALOG_TYPE_INIT_AUTH);
									initApplication();
								}
							}
						}).create();
				return dlg;			
			case DIALOG_TYPE_ABOUT:
				dlg = new AlertDialog.Builder(this).setTitle(R.string.main_omi_about).setMessage(R.string.main_dlg_about_message).
				   setPositiveButton(R.string.common_btn_ok_text, null).
				   create();
				return dlg;
			case DIALOG_TYPE_INIT_AUTH:																
				dlg = new AlertDialog.Builder(this).setTitle(R.string.login_dlg_title).setView(vLogin).
				   setPositiveButton(R.string.common_btn_ok_text, new OnClickListener() {
					
					@Override
					public void onClick(DialogInterface dialog, int which) {						
						String terminalStr = ((EditText)vLogin.findViewById(R.id.auth_user_edit)).getText().toString();
						String authCodStr  = ((EditText)vLogin.findViewById(R.id.auth_password_edit)).getText().toString();
						if(!TextUtils.isEmpty(terminalStr)&& !TextUtils.isEmpty(authCodStr)){
							gsHelper.setTerminal(terminalStr);
							gsHelper.setAuthcode(authCodStr);
							InitializeTask initTask = new InitializeTask(ActivityMain.this, createInitHandler());
							initTask.execute(handler);
						}
						
						
					}
				}).setNegativeButton(R.string.common_btn_cancel_text, new OnClickListener() {
					
					@Override
					public void onClick(DialogInterface dialog, int which) {
						gsHelper.clear();
						android.os.Process.killProcess(android.os.Process.myPid());		//关闭应用程序
						
					}
				}).create();								
                return dlg;			
		}
		
		return super.onCreateDialog(id);
	}		
	
	/* (non-Javadoc)
	 * @see android.app.Activity#onPrepareDialog(int, android.app.Dialog)
	 */
	@Override
	protected void onPrepareDialog(int id, Dialog dialog) {
		
		switch (id) {
		case DIALOG_TYPE_FAILED:
			AlertDialog failedDlg = (AlertDialog)dialog;
			if(isFirstRun()){
				failedDlg.setTitle(R.string.init_dlg_failed_title);				
			}else {
				failedDlg.setTitle(R.string.login_dlg_failed_title);
			}
			break;
		default:
			break;
		}
		
		super.onPrepareDialog(id, dialog);
	}

	@Override
	public void onMomsgResult(MomsgType rspType, GeneratedMessage msg) {
		
		switch (rspType) {
		case INTERVALSYNC_RSP:
			Log.d("Main.onMomsgResult","INTERVALSYNC_RSP");
			break;
		}		
		
	}			

	/* (non-Javadoc)
	 * @see android.app.Activity#onCreateOptionsMenu(android.view.Menu)
	 */
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater menuInflater = getMenuInflater();
		menuInflater.inflate(R.menu.act_main_menu_main, menu);
		return super.onCreateOptionsMenu(menu);
	}

	/* (non-Javadoc)
	 * @see android.app.Activity#onOptionsItemSelected(android.view.MenuItem)
	 */
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {		
		switch (item.getItemId()) {
		case R.id.main_menu_main_syssetting:
			Intent intent = new Intent(this, ActivitySetting.class);
			startActivity(intent);
			return true;		
		case R.id.main_menu_main_about:			
			showDialog(DIALOG_TYPE_ABOUT);
			return true;		
		}
		return false;
	}

	/* (non-Javadoc)
	 * @see android.app.Activity#onActivityResult(int, int, android.content.Intent)
	 */
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		
		switch (requestCode) {
		case REQUEST_CODE_ACTAU:			//验证是否具有收银权限
			if(resultCode == RESULT_OK){								
				Intent cashIntent = new Intent(this, ActGroupCashing.class);
				cashIntent.putExtra(StringKey.INTENT_EXTRA_KEY_AUTHCODE, 
						data.getIntExtra(ActivityAuthcode.INTENT_EXTRA_KEY_AUTHCODE, ActivityAuthcode.INVALID_AUTHCODE));
				startActivity(cashIntent);				
			}
			break;		
		}
	}

	/* (non-Javadoc)
	 * @see android.app.Activity#onDestroy()
	 */
	@Override
	protected void onDestroy() {		
		super.onDestroy();
		
		Log.d("onDestory", "Greeder is cancling!");	
		msgHelper.clearInstance();
		grdManager.release();
		android.os.Process.killProcess(android.os.Process.myPid());	
	}
		
	//初始化应用程序
    private void initApplication(){    	    			    	    	
		final int defHallId = Integer.valueOf(resources.getString(R.string.setting_pref_list_null));
		//初始化工作环境上下文
		wContext.setCompCode(gsHelper.getCompany(resources.getString(R.string.default_null_string)));
		wContext.setTermCode(gsHelper.getTerminal(resources.getString(R.string.default_null_string)));	
		CompoundItem hall = new CompoundItem();
		hall.id = gsHelper.getHall(defHallId);
		HallManager hallManager = new HallManager(grdManager.getHallDao());
		if(hall.id != defHallId){
			hall.name	= hallManager.getHallNameById(hall.id);
		}
		wContext.setHall(hall);		
		//初始化数据后,加入观察者
		wContext.addObserver(msgHelper);
		wContext.addObserver(this);
    }    	   
    
    //加载应用程序数据
    private void loadDataInBackground(){
    	//载入菜品信息
		ExecutorService executor = Executors.newSingleThreadExecutor();
		executor.execute(new Runnable() {
			
			@Override
			public void run() {					
				Log.e("loadDataInBackground", "loding data started!");	//test code
				FoodMainSortManager fmsMgr = new FoodMainSortManager(grdManager.getHallFoodMainSortDao());
				Set<Integer> fmsIdSet = fmsMgr.getAllFoodMainSortIds(wContext.getHall().id);
				Set<Integer> fssIdSet = wDataCache.lodingFoodSubSortData(fmsIdSet);
				wDataCache.lodingFoodData(fssIdSet);
				wDataCache.lodingPrintData(wContext.getHall().id);
				Log.e("loadDataInBackground", "loding data stopped!");	//test code
			}
		});
		executor.shutdown();	
    }    	
	
	//系统是否是第一次运行
	private boolean isFirstRun(){
		String defComp = resources.getString(R.string.default_null_string);
		String compCode = gsHelper.getCompany(defComp);		
		return compCode.equalsIgnoreCase(defComp);
	}	
	
	private Handler createInitHandler(){
		return new Handler(){

				@Override
				public void handleMessage(Message msg) {
					InitResultType initResultType = InitResultType.valueOf(msg.what);
					switch (initResultType) {
					case connect:
						showDialog(DIALOG_TYPE_ERROR);
						break;
					case login:
						showDialog(DIALOG_TYPE_FAILED);
						break;
					case resource:
						setContentView(R.layout.main);
						if(msg.obj != null){//返回成功,则处理资源刷新数据
							final CMsgRefreshResRsp resRsp = (CMsgRefreshResRsp)msg.obj;
							ResourceVersionManager resMgr = new ResourceVersionManager(grdManager,resRsp);
							final int count = resMgr.getRefreshItemCount();
							if(count != 0){//若有新版本数据,显示一个确认更新对话框									
								AlertDialog dlg = new AlertDialog.Builder(ActivityMain.this).
										setTitle(R.string.common_dlg_prompt_title).
									    setMessage(R.string.refresh_adlg_message).
									    setPositiveButton(R.string.common_btn_ok_text, new OnClickListener() {							
										    @Override
										    public void onClick(DialogInterface dialog, int which) {
											    RefreshTask task = new RefreshTask(count);
											    task.execute(resRsp);														  
										    }
									    }).
									    setNegativeButton(R.string.common_btn_cancel_text, new OnClickListener() {
										
										  @Override
										  public void onClick(DialogInterface dialog, int which) {
											  //取消则加载数据
											  loadDataInBackground();												
										  }
									  }).create();
								dlg.show();
							}else{//没有新版本数据则加载数据										
								loadDataInBackground();
							}
						}else {//返回为空代表初始化失败,让用户重登录.考虑到网络不稳定因素,登录成功可能也不能正常返回资源刷新数据
							showDialog(DIALOG_TYPE_FAILED);
						}
						break;					
					}
				}			
		};
	}
	
	class RefreshTask extends AsyncTask<CMsgRefreshResRsp, Integer, String>{
		
		private ProgressDialog lodingDlg;
		
		public RefreshTask(int count){
			lodingDlg = new ProgressDialog(ActivityMain.this);
			lodingDlg.setTitle(R.string.refresh_pdlg_title);
			lodingDlg.setMax(count);
			lodingDlg.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);			
		}
		
		@Override
		protected String doInBackground(CMsgRefreshResRsp... params) {
			try {
				CMsgRefreshResRsp resRsp = params[0];
				ResourceVersionManager resMgr = new ResourceVersionManager(grdManager,resRsp);
				int refreshItemsCount = 0;
				
				//刷新分厅大类数据													
				refreshItemsCount = resMgr.RefreshHallSort();
				
				rvHelper.setHallSortVersion(resRsp.getHallSortVersion());
				publishProgress(refreshItemsCount);			
				
				//刷新分厅数据			
				refreshItemsCount += resMgr.refreshHall();
				rvHelper.setHallVersion(resRsp.getHallVersion());
				publishProgress(refreshItemsCount);
				
				//刷新餐台类型数据			
				refreshItemsCount += resMgr.refreshDinnerTableType();
				rvHelper.setDinnerTableTypeVersion(resRsp.getDinTabTypeVersion());
				publishProgress(refreshItemsCount);
				
				//刷新餐台数据			
				refreshItemsCount += resMgr.refreshDinnerTable();
				rvHelper.setDinnerTableVersion(resRsp.getDinTableVersion());
				publishProgress(refreshItemsCount);
				
				//刷新菜品大类数据		
				refreshItemsCount += resMgr.refreshFoodMainSort();
				rvHelper.setFoodMainSortVersion(resRsp.getFoodMainSortVersion());			
				publishProgress(refreshItemsCount);
				
				//刷新分厅菜品大类关联数据
				refreshItemsCount += resMgr.refreshHallFoodMainSort();
				rvHelper.setHallFoodMainSortVersion(resRsp.getHallFmsortVersion()); 			
				publishProgress(refreshItemsCount);
				
				//刷新菜品小类数据		
				refreshItemsCount += resMgr.refreshFoodSubSort();
				rvHelper.setFoodSubSortVersion(resRsp.getFoodSubSortVersion());
				publishProgress(refreshItemsCount);
				
				//刷新菜品计价单位数据			
				refreshItemsCount += resMgr.RefreshFoodUnit();
				rvHelper.setFoodUnitVersion(resRsp.getFoodUnitVersion());
				publishProgress(refreshItemsCount);
				
				//刷新菜品数据
				refreshItemsCount += resMgr.refreshFood();
				rvHelper.setFoodVersion(resRsp.getFoodVersion());
				publishProgress(refreshItemsCount);
				
				//刷新菜品价格数据			
				refreshItemsCount += resMgr.refreshFoodPrice();
				rvHelper.setFoodPriceVersion(resRsp.getFoodPriceVersion());
				publishProgress(refreshItemsCount);
				
				//刷新菜品做法要求类别数据
				refreshItemsCount += resMgr.refreshFoodRecSort();
				rvHelper.setFoodRecipeSortVersion(resRsp.getFoodRecTypeVersion());
				publishProgress(refreshItemsCount);
							
				//刷新菜品做法数据
				refreshItemsCount += resMgr.refreshFoodRecipe();
				rvHelper.setFoodRecipeVersion(resRsp.getFoodRecipeVersion());
				publishProgress(refreshItemsCount);
				
				//刷新菜品-做法类别关联数据
				refreshItemsCount += resMgr.refreshFoodFrsort();
				rvHelper.setFoodFrsortVersion(resRsp.getFoodFoodRecTypeVersion());
				publishProgress(refreshItemsCount);
				
				//刷新菜品传菜要求数据
				refreshItemsCount += resMgr.refreshDemand();
				rvHelper.setFoodDeliveryVersion(resRsp.getFoodDemandVersion());
				publishProgress(refreshItemsCount);
				
				//刷新操作原因数据
				refreshItemsCount += resMgr.refreshOperateReason();
				rvHelper.setOperateReasonVersion(resRsp.getOpeReasonVersion());
				publishProgress(refreshItemsCount);
				
				//刷新出品部门和打印档口关联数据
				refreshItemsCount += resMgr.refreshProducePrint();
				rvHelper.setProducePrintVersion(resRsp.getProsecPortVersion());
				publishProgress(refreshItemsCount);
				
				//刷新计时项目数据
				refreshItemsCount += resMgr.refreshTimeItem();
				rvHelper.setTimeItemVersion(resRsp.getTimeItemVersion());
				publishProgress(refreshItemsCount);
				
				//刷新餐台类型盒计时项目关联数据
				refreshItemsCount += resMgr.refreshDttypeTitem();
				rvHelper.setDttypeTitemVersion(resRsp.getDinTabTypeTimeItemVersion());
				publishProgress(refreshItemsCount);
								
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return null;
		}

		/* (non-Javadoc)
		 * @see android.os.AsyncTask#onPreExecute()
		 */
		@Override
		protected void onPreExecute() {
			lodingDlg.show();
		}

		/* (non-Javadoc)
		 * @see android.os.AsyncTask#onProgressUpdate(Progress[])
		 */
		@Override
		protected void onProgressUpdate(Integer... values) {
			lodingDlg.setProgress(values[0]);
		}

		/* (non-Javadoc)
		 * @see android.os.AsyncTask#onPostExecute(java.lang.Object)
		 */
		@Override
		protected void onPostExecute(String result) {
			lodingDlg.dismiss();			
			loadDataInBackground();		//数据刷新后加载数据
		}
		
	}	
	
}