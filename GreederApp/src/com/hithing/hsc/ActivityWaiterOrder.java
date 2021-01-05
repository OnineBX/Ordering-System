/**========================================
 * File:	ActivityWaiterOrder.java
 * Package:	com.hithing.hsc
 * Create:	by Leopard
 * Date:	2011-12-21:下午2:25:01
 **======================================*/
package com.hithing.hsc;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.SystemClock;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnCreateContextMenuListener;
import android.view.View.OnTouchListener;
import android.widget.AdapterView;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.EditText;
import android.widget.Filter;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;

import com.google.protobuf.GeneratedMessage;
import com.hithing.app.ProtobufActivity;
import com.hithing.hsc.ActivityAuthcode.NeedAuthcodeType;
import com.hithing.hsc.R;
import com.hithing.hsc.app.DialogPromptType;
import com.hithing.hsc.bll.android.AndGrdDaoManager;
import com.hithing.hsc.bll.control.OrderCtrl;
import com.hithing.hsc.bll.control.UserCtrl;
import com.hithing.hsc.bll.control.UserCtrl.AuthenticateType;
import com.hithing.hsc.bll.manage.DinnerTableManager;
import com.hithing.hsc.bll.manage.FoodMainSortManager;
import com.hithing.hsc.bll.manage.FoodManager;
import com.hithing.hsc.bll.manage.GreederDaoManager;
import com.hithing.hsc.bll.util.MomsgFactory.MomsgType;
import com.hithing.hsc.bll.util.MomsgHelper.IntervalSyncType;
import com.hithing.hsc.dataentity.DinnerTableEntity;
import com.hithing.hsc.dataentity.FoodMainSortEntity;
import com.hithing.hsc.dataentity.TimeItemEntity;
import com.hithing.hsc.entity.MoMsg.CMsgAuthvalidateRsp;
import com.hithing.hsc.entity.MoMsg.CMsgBillPricingRsp;
import com.hithing.hsc.entity.MoMsg.CMsgOrderRsp;
import com.hithing.hsc.entity.MoMsg.CMsgViewBillRsp;
import com.hithing.hsc.entity.MoMsg.CMsgViewBillRsp.CMsgOrderFood;
import com.hithing.hsc.entity.OrderItemKey;
import com.hithing.hsc.entity.OrderItemKey.FoodStatus;
import com.hithing.hsc.entity.OrderItemValue;
import com.hithing.hsc.entity.OrderItemValue.Builder;
import com.hithing.hsc.entity.OrderItemValue.RemarkItem;
import com.hithing.hsc.entity.SearchableAdapterItem;
import com.hithing.hsc.global.StringKey;
import com.hithing.widget.AbsMultiStyleAdapter;
import com.hithing.widget.AbsMultiStyleAdapter.FilterMode;
import com.hithing.widget.MultiColorStyleAdapter;
import com.hithing.widget.OrderItemsAdapter;

/**
 * <p>ActivityWaiterOrder</p>
 * 	
 * <p>处理服务员点单的活动,包括处理加菜、减菜、做法、传菜要求和落单、估清等操作</p>
 *
 * @author Leopard
 * 
 */
public final class ActivityWaiterOrder extends ProtobufActivity implements TextWatcher, OnClickListener {		
	
	private final int				DIALOG_TYPE_PROMPT	 		 = 1;								//对话框类型-提示信息对话框	
	private final int				DIALOG_TYPE_OPERATE_RETRUN	 = DIALOG_TYPE_PROMPT 		  + 1;	//对话框类型-返回
	private final int               DIALOG_TYPE_CASH_RETRUN      = DIALOG_TYPE_OPERATE_RETRUN + 1;  //对话框类型-收银活动入单返回
	
	private final int				REQUEST_CODE_ACTRD 			 = 1;								//做法和要求活动返回码	
	private final int				REQUEST_CODE_ACTCU			 = REQUEST_CODE_ACTRD + 1;			//数量和单位活动返回码	
	private final int				REQUEST_CODE_ACTAU			 = REQUEST_CODE_ACTCU + 1;			//输入授权码活动返回码
	private final int				REQUEST_CODE_ACTSO			 = REQUEST_CODE_ACTAU + 1;			//账单定价活动返回码
	
	private int						tableId;
	private String					tableName;
	private String					caller;	
	
	private LayoutInflater			layoutInflater;
	private MenuInflater			menuInflater;	
	private GreederDaoManager 		grdManager;														//Greeder框架数据访问管理器
	private FoodManager				foodManager;													//菜品数据管理器
	private WorkDataCache			wDataCache 					 = WorkDataCache.INSTANCE;
	
	private OrderCtrl				orderCtrl;														//账单控制类实例
	private ListView				orderLsv;														//菜品订单条目列表视图
	private OrderItemsAdapter		orderAdapter;													//菜品订单条目数据适配器	
	private GridView				subSortGdv;														//菜品小类视图
	private AbsMultiStyleAdapter	subSortAdapter;													//菜品小类数据适配器
	private GridView				foodGdv;														//菜品视图
	private AbsMultiStyleAdapter	foodAdapter;													//菜品数据适配器
	private ImageButton				revertImgBtn;													//返回按钮
	private TextView				totalTxv;														//总价文本视图
	private TextView				countInfoTxv;													//菜品个数信息视图
	private EditText				searchEdt;														//搜索编辑框
		
	private PopupWindow             pWin;                                                           //计时内容显示
	private long                    timeWhenStopped;                                                //暂停时间
	private long                    runTime;                                                        //累计时间
	
	private OrderItemKey			curOrderItemKey;												//账单当前菜品键
	private OrderItemValue			curOrderItemValue;												//账单当前菜品值	
	private List<TimeItemEntity>    list;
	
	private Intent					tableIntent;	
	private Intent                  cashIntent;
	private Intent					auIntent;														//用于开启输入授权码活动
	private Intent					cuIntent;														//用于开启设置数量和单位活动	
	private Intent					soIntent;														//用于开启设置优惠的活动
	private Intent					piIntent;														//用于打印票据的服务
		
	private Bundle 					promptArgs 						= new Bundle();					//提示对话框参数
	
	public void onMainSortClick(View v){
		subSortAdapter.setFilterMode(FilterMode.Type);
		
		int 	id 		= Integer.valueOf(v.getTag().toString());
		Filter 	filter 	= subSortAdapter.getFilter();
		filter.filter(String.valueOf(id));
		subSortGdv.setAdapter(subSortAdapter);
		subSortGdv.setVisibility(View.VISIBLE);
		foodGdv.setVisibility(View.GONE);
		
	}
	
	public void onOrderItem(View v){
		switch (v.getId()){
		case R.id.waiterorder_txv_food_count:		//选中OrderItem数量的情况
			int position = Integer.parseInt(v.getTag().toString());
			curOrderItemKey = orderAdapter.getItemKey(position);
			curOrderItemValue = (OrderItemValue)orderAdapter.getItem(position);	
			//只有未下单的菜品才可以设置份数和单位
			if(curOrderItemKey.status == FoodStatus.FOOD_STATUS_UNDO){
				cuIntent.putExtra(StringKey.INTENT_EXTRA_KEY_COUNT, curOrderItemValue.getCount());
				cuIntent.putExtra(StringKey.INTENT_EXTRA_KEY_PRICE, curOrderItemKey.priceId);
				cuIntent.putExtra(StringKey.INTENT_EXTRA_KEY_FOOD, curOrderItemValue.getFood());
				startActivityForResult(cuIntent, REQUEST_CODE_ACTCU);
			}			
			
			break;

		default:
			break;
		}
	}	
	
	public void onOperate(View v){		
		String text = ((TextView)v).getText().toString();
				
		//点击落单的情况
		if(text == getString(R.string.worder_btn_placeorder_text)){	
			auIntent.putExtra(StringKey.INTENT_EXTRA_KEY_TYPE, NeedAuthcodeType.placeorder);
			startActivityForResult(auIntent, REQUEST_CODE_ACTAU);			
		}
		
		//点击先落的情况
		if(text == getString(R.string.worder_btn_priororder_text)){
			auIntent.putExtra(StringKey.INTENT_EXTRA_KEY_TYPE, NeedAuthcodeType.priororder);
			startActivityForResult(auIntent, REQUEST_CODE_ACTAU);
		}
	}
	
	@Override
	public void afterTextChanged(Editable s) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void beforeTextChanged(CharSequence s, int start, int count,
			int after) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onTextChanged(CharSequence s, int start, int before, int count) {
		if(foodGdv.getVisibility() == View.GONE){
			foodGdv.setVisibility(View.VISIBLE);
			subSortGdv.setVisibility(View.GONE);
		}
		Filter 	filter 	= foodAdapter.getFilter();
		foodAdapter.setFilterMode(FilterMode.Index);
		filter.filter(s.toString());
		foodGdv.setAdapter(foodAdapter);
	}
	
	/* (non-Javadoc)
	 * @see com.hithing.app.ProtobufActivity#onCreate(android.os.Bundle)
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);		
		setContentView(R.layout.act_waiterorder_main);				
		Intent	intent = getIntent();
		tableId 			= intent.getExtras().getInt(StringKey.INTENT_EXTRA_KEY_TABLE);
		caller				= intent.getExtras().getString(StringKey.INTENT_EXTRA_KEY_CALLER);
		tableIntent			= new Intent(ActivityWaiterOrder.this, ActivityDinnerTable.class);
		cashIntent          = new Intent(ActivityWaiterOrder.this, ActGroupCashing.class);
		layoutInflater 		= getLayoutInflater();		
		menuInflater		= getMenuInflater();		
		grdManager 			= new AndGrdDaoManager(this);
		foodManager			= new FoodManager(grdManager.getFoodDao(), grdManager.getFoodPriceDao());
//		DinnerTableManager tableMgr = new DinnerTableManager(grdManager.getDinnerTableDao());
//		list               = tableMgr.getAvailiableTimeItem(tableId);
		
		TextView  btnText = (TextView) findViewById(R.id.waiterorder_txv_orderdemand_time);
		btnText.setOnClickListener(this);
		
		btnText.setOnCreateContextMenuListener(new OnCreateContextMenuListener() {
			
			@Override
			public void onCreateContextMenu(ContextMenu menu, View v,
					ContextMenuInfo menuInfo) {
				menuInflater.inflate(R.menu.act_waiterorder_timecategory, menu);
//				int index = 0;
//				DinnerTableManager   tableMgr = new DinnerTableManager(grdManager.getDinnerTableDao());
//				List<TimeItemEntity> list     = tableMgr.getAvailiableTimeItem(tableId);
//				for(TimeItemEntity ent : list){
//					menuInflater.inflate(R.menu.act_waiterorder_timecategory, menu);
//					
//				}
				
				
				
			}
		});
		
		orderCtrl = new OrderCtrl(tableId, foodManager);
		
		revertImgBtn = (ImageButton)findViewById(R.id.waiterorder_btn_revert);
		revertImgBtn.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				subSortGdv.setVisibility(View.VISIBLE);
				foodGdv.setVisibility(View.GONE);				
			}
		});
		
		totalTxv 				= (TextView)findViewById(R.id.waiterorder_txv_total);
		countInfoTxv 			= (TextView)findViewById(R.id.waiterorder_txv_countinfo); 
		
		orderLsv				= (ListView)findViewById(R.id.waiterorder_lsv_orderitems);
		final Intent rmIntent 	= new Intent(this, ActivityRecipeDemand.class);
		orderLsv.setOnItemClickListener(new OnItemClickListener() {		
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position,
					long id) {				
				curOrderItemKey 	= orderAdapter.getItemKey(position);
				curOrderItemValue 	= (OrderItemValue)orderAdapter.getItem(position);
				//只有未下单的菜品才能够设置做法和要求
				if(curOrderItemKey.status == FoodStatus.FOOD_STATUS_UNDO){
					rmIntent.putExtra(StringKey.INTENT_EXTRA_KEY_FOOD, curOrderItemValue.getFood());							
					rmIntent.putExtra(StringKey.INTENT_EXTEA_KEY_RECIPE, curOrderItemValue.getRecipe());
					rmIntent.putExtra(StringKey.INTENT_EXTRA_KEY_DEMAND, curOrderItemValue.getDemand());
					rmIntent.putExtra(StringKey.INTENT_EXTRA_KEY_REMARK, curOrderItemValue.getRemark());
					startActivityForResult(rmIntent, REQUEST_CODE_ACTRD);
				}				
			}
		});
		registerForContextMenu(orderLsv);
		
		subSortGdv	= (GridView)findViewById(R.id.waiterorder_gdv_subsorts);		
		subSortGdv.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position,
					long id) {
					subSortGdv.setVisibility(View.GONE);
					foodGdv.setVisibility(View.VISIBLE);
					
					foodAdapter.setFilterMode(FilterMode.Type);
					
					Filter 	filter 	= foodAdapter.getFilter();
					filter.filter(String.valueOf(id));
					foodGdv.setAdapter(foodAdapter);
			}
		});
		
		foodGdv	= (GridView)findViewById(R.id.waiterorder_gdv_foods);
		foodGdv.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position,
					long id) {
				
				//在账单中添加未下单的菜品				
				orderCtrl.addFood((int)id);
				orderAdapter.refresh();
				orderLsv.setSelection(orderAdapter.getCount() - 1);
				refreshTotalInfo();
				//如果搜索框有内容,则清空:处理搜索的情况,如果未搜索则不能置空
				if(!TextUtils.isEmpty(searchEdt.getText().toString())){
					searchEdt.setText(null);	
				}								
			}
		});				
		
		searchEdt = (EditText)findViewById(R.id.waiterorder_edt_search);
		searchEdt.addTextChangedListener(this);

		auIntent = new Intent(this, ActivityAuthcode.class);
		cuIntent = new Intent(this, ActivityCountUnit.class);
		soIntent = new Intent(this, ActivitySetOffer.class);		
		soIntent.putExtra(StringKey.INTENT_EXTRA_KEY_TABLE, tableId);
		piIntent  = new Intent(this,ServicePrintInvoice.class);		
		
		initViews();
		initTimeChronometer();
				
	}

	public void onClick(View v){
		
		if(pWin.isShowing()){
			pWin.dismiss();
		}else{
			pWin.showAsDropDown(v);
		}
	}

	/* (non-Javadoc)
	 * @see android.app.Activity#onActivityResult(int, int, android.content.Intent)
	 */
	@SuppressWarnings("unchecked")
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		switch(requestCode){
		case REQUEST_CODE_ACTRD:
			if(resultCode == RESULT_OK){
				orderCtrl.specifyFoodRecipe(curOrderItemKey, (HashMap<Integer, String>)data.getSerializableExtra(StringKey.INTENT_EXTEA_KEY_RECIPE));
				orderCtrl.specifyDemand(curOrderItemKey, (RemarkItem)data.getSerializableExtra(StringKey.INTENT_EXTRA_KEY_DEMAND));
				curOrderItemValue.setRemark(data.getStringExtra(StringKey.INTENT_EXTRA_KEY_REMARK));
			}
			orderAdapter.notifyDataSetChanged();
			break;
		case REQUEST_CODE_ACTCU:
			if(resultCode == RESULT_OK){
				//更新账单菜品份数				
				orderCtrl.specifyFoodCount(curOrderItemKey, data.getIntExtra(StringKey.INTENT_EXTRA_KEY_COUNT, 1));
				
				//更新账单菜品计价单位
				int checkedId = data.getIntExtra(StringKey.INTENT_EXTRA_KEY_PRICE, -1);				
				if(curOrderItemKey.priceId != checkedId){
					orderCtrl.specifyFoodPrice(curOrderItemKey, checkedId);	
				}								
				orderAdapter.refresh();
				countInfoTxv.setText(String.format("%d/%d", orderCtrl.getAllFoodsCount() - orderCtrl.getUndoFoodsCount(),
						orderCtrl.getAllFoodsCount()));
				totalTxv.setText(orderCtrl.refreshTotal());
			}
			break;
		case REQUEST_CODE_ACTAU:
			if(resultCode == RESULT_OK){
				NeedAuthcodeType 	type 		= (NeedAuthcodeType)data.getSerializableExtra(StringKey.INTENT_EXTRA_KEY_TYPE);
				int 				authCode	= data.getIntExtra(ActivityAuthcode.INTENT_EXTRA_KEY_AUTHCODE, ActivityAuthcode.INVALID_AUTHCODE);
				promptArgs.putSerializable(StringKey.BUNDLE_KEY_TYPE, DialogPromptType.PROMPT_VALIDATEAUTH);
				if(authCode == ActivityAuthcode.INVALID_AUTHCODE){														
					showDialog(DIALOG_TYPE_PROMPT, promptArgs);
				}else{
					switch (type) {
					case placeorder:						//落单
					case priororder:						//先落
						orderCtrl.placeOrder(authCode);						
						break;
					case setoffer:							//定价
						soIntent.putExtra(StringKey.INTENT_EXTRA_KEY_AUTHCODE, authCode);
						UserCtrl userCtrl = new UserCtrl(authCode);
						userCtrl.validateAuth(AuthenticateType.AUTH_TYPE_OFFER);						
						break;
					}
				}				
			}
			break;	
		case REQUEST_CODE_ACTSO:
			if(resultCode == RESULT_OK){
				orderCtrl.refresh();
			}
			break;
		}
	}

	/* (non-Javadoc)
	 * @see android.app.Activity#onPrepareDialog(int, android.app.Dialog, android.os.Bundle)
	 */
	@Override
	protected void onPrepareDialog(int id, Dialog dialog, Bundle args) {				
		switch (id) {
		case DIALOG_TYPE_PROMPT:
			AlertDialog aDlg = (AlertDialog)dialog;
			DialogPromptType type = (DialogPromptType)args.getSerializable(StringKey.BUNDLE_KEY_TYPE);
			switch (type) {
			case PROMPT_VALIDATEAUTH:
				aDlg.setTitle(R.string.common_dlg_error_authcode_title);
				aDlg.setButton(DialogInterface.BUTTON_POSITIVE, getString(R.string.common_btn_ok_text), 
						new DialogInterface.OnClickListener() {
					
					@Override
					public void onClick(DialogInterface dialog, int which) {					
						startActivityForResult(auIntent, REQUEST_CODE_ACTAU);
					}
				});
				break;
			
			}		
			break;
		}
	}

	/* (non-Javadoc)
	 * @see android.app.Activity#onCreateDialog(int)
	 */
	@Override
	protected Dialog onCreateDialog(int id, Bundle bundle) {
		Dialog dlg;
		switch (id) {				
		case DIALOG_TYPE_PROMPT:
			dlg = new AlertDialog.Builder(this).setTitle(R.string.common_dlg_prompt_title).				
					setPositiveButton(R.string.common_btn_ok_text, null).
					setNegativeButton(R.string.common_btn_cancel_text, null).
					create(); 	
			return dlg;
		case DIALOG_TYPE_OPERATE_RETRUN:    //添加菜品时，来自餐台点餐
			dlg = new AlertDialog.Builder(this).setTitle(R.string.worder_dlg_prompt_return_title).
					setPositiveButton(R.string.common_btn_ok_text, new DialogInterface.OnClickListener() {
						
						@Override
						public void onClick(DialogInterface dialog, int which) {
							
							startActivity(tableIntent);							
						}
					}).
					setNegativeButton(R.string.common_btn_cancel_text, null).
					create();
			return dlg;	
		case DIALOG_TYPE_CASH_RETRUN:       //添加菜品时,是来自于收银的入单
			dlg = new AlertDialog.Builder(this).setTitle(R.string.worder_dlg_cash_return_title).
			setPositiveButton(R.string.common_btn_ok_text, new DialogInterface.OnClickListener() {
				
				@Override
				public void onClick(DialogInterface dialog, int which) {
					
					startActivity(cashIntent);							
				}
			}).
			setNegativeButton(R.string.common_btn_cancel_text, null).
			create();
	        return dlg;	
		default:
			break;
		}
		return super.onCreateDialog(id);
	}	
		
	/* (non-Javadoc)
	 * @see android.app.Activity#onCreateOptionsMenu(android.view.Menu)
	 */
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		menuInflater.inflate(R.menu.act_waiterorder_menu_main, menu);
		return super.onCreateOptionsMenu(menu);
	}
		
	/* (non-Javadoc)
	 * @see android.app.Activity#onOptionsItemSelected(android.view.MenuItem)
	 */
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		
		switch (item.getItemId()) {
		case R.id.waiterorder_menu_main_advance:			//点击高级菜单的情况
			auIntent.putExtra(StringKey.INTENT_EXTRA_KEY_TYPE, NeedAuthcodeType.setoffer);
			startActivityForResult(auIntent, REQUEST_CODE_ACTAU);	
			break;
		case R.id.waiterorder_menu_main_cancelorder:		//点击全单取消的情况
			auIntent.putExtra(StringKey.INTENT_EXTRA_KEY_TYPE, NeedAuthcodeType.cancelorder);
			startActivityForResult(auIntent, REQUEST_CODE_ACTAU);
			break;
		}
		
		return super.onOptionsItemSelected(item);
	}

	/* (non-Javadoc)
	 * @see android.app.Activity#onCreateContextMenu(android.view.ContextMenu, android.view.View, android.view.ContextMenu.ContextMenuInfo)
	 */
	@Override
	public void onCreateContextMenu(ContextMenu menu, View v,
			ContextMenuInfo menuInfo) {				          
		AdapterContextMenuInfo info = (AdapterContextMenuInfo)menuInfo;
		curOrderItemKey 	= orderAdapter.getItemKey(info.position);
		curOrderItemValue 	= (OrderItemValue)orderAdapter.getItem(info.position);
		
		if(curOrderItemKey.status == FoodStatus.FOOD_STATUS_UNDO){
			menu.setHeaderTitle(R.string.common_cmn_operate_title);
			menuInflater.inflate(R.menu.act_waiterorder_cmn_food, menu);
		}				
		super.onCreateContextMenu(menu, v, menuInfo);
	}

	/* (non-Javadoc)
	 * @see android.app.Activity#onContextItemSelected(android.view.MenuItem)
	 */
	@Override
	public boolean onContextItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.waiterorder_cmi_food_cancel:		//取消菜品
			orderCtrl.cancelFood(curOrderItemKey);		
			orderAdapter.refresh();
			break;		
		}
		return super.onContextItemSelected(item);
	}

	/* (non-Javadoc)
	 * @see com.hithing.app.ProtobufActivity#onMomsgResult(com.hithing.hsc.bll.manage.MomsgFactory.MomsgType, com.google.protobuf.GeneratedMessage)
	 */
	@Override
	public void onMomsgResult(MomsgType rspType, GeneratedMessage msg) {
		
		switch (rspType) {
		case INTERVALSYNC_RSP:
			Log.d("WaiterOrder.onMomsgResult", "INTERVALSYNC_RSP");
			break;
		case VIEWBILL_RSP:						//账单刷新消息
			Log.e("WaiterOrder.onMomsgResult", "VIEWORDER_RSP");			
			CMsgViewBillRsp billRsp = (CMsgViewBillRsp)msg;				
			if(billRsp.hasStatus()){
				if(billRsp.getStatus() == MOMSG_RSP_STATUS_SUCCESS){
					OrderItemKey 	key;
					Builder 	 	valueBuilder;
					for(CMsgOrderFood food : billRsp.getOrderFoodList()){
						valueBuilder = new Builder().setCanDiscount(food.getCandiscount()).
											  setFoodPrice(food.getFoodprice()).
											  setRatioDiscount(food.getDisratio()).
											  setMoneyDiscount(food.getDismoney()).
											  setRemark(food.getRemark()).
											  setCount(food.getFoodnum());
						key = new OrderItemKey(FoodStatus.valueOf(food.getType()),
								food.getFoodpriceid(),food.getOrderid());
						orderCtrl.loadFood(key, valueBuilder);						
					}
					orderAdapter.refresh();
					refreshTotalInfo();
				}
			}
			break;
		case ORDER_RSP:
			Log.e("WaiterOrder.onMomsgResult", "ORDER_RSP");
			CMsgOrderRsp rsp = (CMsgOrderRsp)msg;				
			if(rsp.hasStatus()){
				if(rsp.getStatus() != MOMSG_RSP_STATUS_SUCCESS){			//下单不成功										
					showDialog(DIALOG_TYPE_PROMPT, promptArgs);
				}else{														//下单成功则打印
					//刷新账单
					orderCtrl.refresh();
					
					//如果是楼面模块调用，则开启打印服务										暂时屏蔽
//					if(caller.equals(ActivityDinnerTable.TAG)){						
//						piIntent.putExtra(StringKey.INTENT_EXTRA_KEY_TABLE, tableName);
//						piIntent.putExtra(StringKey.INTENT_EXTRA_KEY_USER, rsp.getName().toStringUtf8());
//						piIntent.putExtra(StringKey.INTENT_EXTRA_KEY_TIME, rsp.getTime());
//						piIntent.putExtra(StringKey.INTENT_EXTRA_KEY_FOOD, (HashMap<OrderItemKey, OrderItemValue>)orderCtrl.getUnOrderData());
//						startService(piIntent);																	
//					}					
					
					NeedAuthcodeType type = (NeedAuthcodeType)auIntent.getSerializableExtra(StringKey.INTENT_EXTRA_KEY_TYPE);					
					//处理落单的情况					
					if(type == NeedAuthcodeType.placeorder){				//落单							
						onBackPressed();
					} 							
					orderCtrl.clearUndoData();
				}
			}
			break;		
		case AUTHVALIDATE_RSP:
			CMsgAuthvalidateRsp avRsp = (CMsgAuthvalidateRsp)msg;
			if(avRsp.hasStatus()){
				if(avRsp.getStatus() == MOMSG_RSP_STATUS_SUCCESS){
					startActivityForResult(soIntent, REQUEST_CODE_ACTSO);
				}else {										
					showDialog(DIALOG_TYPE_PROMPT, promptArgs);
				}
			}			
			break;
		}

	}		

	/* (non-Javadoc)
	 * @see android.app.Activity#onKeyDown(int, android.view.KeyEvent)
	 */
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		
		//处理账单中存在未下单菜品的情况 DIALOG_TYPE_CASH_RETRUN
		if(keyCode == KeyEvent.KEYCODE_BACK){
			 if(orderCtrl.hasUndoFoods()){
				 //调用者为收银活动的情况				 
				 if(caller.equals(ActGroupCashing.TAG)){					 
				     showDialog(DIALOG_TYPE_CASH_RETRUN);				 
				 }
				 //调用者为餐台活动的情况
				 if(caller.equals(ActivityDinnerTable.TAG)){					 
					 showDialog(DIALOG_TYPE_OPERATE_RETRUN);
				 }				 
			 }
		}		
		return super.onKeyDown(keyCode, event);
	}

	/* (non-Javadoc)
	 * @see com.hithing.app.ProtobufActivity#onDestroy()
	 */
	@Override
	protected void onDestroy() {		
		super.onDestroy();
		grdManager.release();		
	}
	
	private void initViews(){				
		
		//发送更新菜品订单请求		
		List<Integer> ids = new LinkedList<Integer>();
		ids.add(R.id.waiterorder_txv_food_name);
		ids.add(R.id.waiterorder_txv_food_count);		
		ids.add(R.id.waiterorder_txv_food_cost);
		ids.add(R.id.waiterorder_txv_food_description);	
		
		orderAdapter = new OrderItemsAdapter(this, orderCtrl.getOrderData(), R.layout.act_waiterorder_order_item, ids);
		orderLsv.setAdapter(orderAdapter);
				
		orderCtrl.refresh();							//发送菜单查看消息
		
		//初始化标题栏
		TextView title 					= (TextView)findViewById(R.id.cmn_txv_title);
		DinnerTableManager dtManager 	= new DinnerTableManager(grdManager.getDinnerTableDao());
		DinnerTableEntity table 		= dtManager.getTableById(tableId);
		try {
			table.getType().refresh();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		String info = String.format("%s-%s-%s", wContext.getHall().name, table.getType().getName(),table.getName());
		tableName 	= table.getName();
		
		title.setText(info);								
		
		//初始化菜品大类
		LinearLayout 				mainSortContainer	= (LinearLayout)findViewById(R.id.waiterorder_container_mainsort);
		FoodMainSortManager 		fmsManager 			= new FoodMainSortManager(grdManager.getHallFoodMainSortDao());
		final List<FoodMainSortEntity> 	fmsList 		= fmsManager.getAllFoodMainSorts(wContext.getHall().id);
		final Set<Integer> 			fmsIdSet			= new HashSet<Integer>();
		
		Button btn;
		for(FoodMainSortEntity ent : fmsList){			
			btn = (Button) layoutInflater.inflate(R.layout.act_waiterorder_maintype_item, mainSortContainer, false);
			btn.setText(ent.getName());
			btn.setTag(ent.getId());
			mainSortContainer.addView(btn);
			
			fmsIdSet.add(ent.getId());
		}
		
		//初始化菜品小类	
		subSortAdapter = new MultiColorStyleAdapter(ActivityWaiterOrder.this, wDataCache.getFoodSubSortData(), R.layout.act_waiterorder_small_item);
		subSortGdv.setAdapter(subSortAdapter);
		
		//初始化菜品		
		foodAdapter = new MultiColorStyleAdapter(ActivityWaiterOrder.this, wDataCache.getFoodData(), R.layout.act_waiterorder_small_item);
		foodGdv.setAdapter(foodAdapter);
		
		//默认选中第一个菜品大类
		if(!fmsList.isEmpty()){
			onMainSortClick(mainSortContainer.getChildAt(0));
		}	 
										
	}		
	
	//刷新账单汇总信息
	private void refreshTotalInfo(){
		totalTxv.setText(orderCtrl.refreshTotal());
		countInfoTxv.setText(String.format("%d/%d", orderCtrl.getAllFoodsCount() - orderCtrl.getUndoFoodsCount(), 
				orderCtrl.getAllFoodsCount()));   
	}
	
	private void initTimeChronometer() {
		View v = getLayoutInflater().inflate(R.layout.act_waiterorder_timing, null, false);
		final Chronometer chmeter = (Chronometer) v.findViewById(R.id.chm_timer);
		
		timeWhenStopped = 0;
		Button btn = (Button) v.findViewById(R.id.btn_start_pause);
		btn.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Button btnTimeCtrl = (Button) v;
				String btnText = btnTimeCtrl.getText().toString();
				if(btnText == "开始"){
					chmeter.start();
					btnTimeCtrl.setText("暂停");
				}else if(btnText == "暂停"){
					timeWhenStopped = chmeter.getBase() - SystemClock.elapsedRealtime(); 
					chmeter.stop();
					btnTimeCtrl.setText("继续");
				}else {
					chmeter.setBase(SystemClock.elapsedRealtime() + timeWhenStopped);
					chmeter.start();
					btnTimeCtrl.setText("暂停");
				}
				
				
			}
		});
		
		 pWin = new PopupWindow(v, 240, 180, true);   
	        pWin.setOutsideTouchable(true);
	        pWin.setBackgroundDrawable(new BitmapDrawable()); 
	        pWin.setTouchInterceptor(new OnTouchListener() {
				
				@Override
				public boolean onTouch(View v, MotionEvent event) {
					Log.e("onTouch", "clicked!");
					if (event.getAction() == MotionEvent.ACTION_OUTSIDE)
						pWin.dismiss();
					return false;
				}
			});
	    }
}
