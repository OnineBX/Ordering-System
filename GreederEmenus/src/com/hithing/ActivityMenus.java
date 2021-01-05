package com.hithing;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.res.Resources;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Filter;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Toast;
import com.google.protobuf.GeneratedMessage;
import com.hithing.adapter.MenusAdapter;
import com.hithing.app.ProtobufActivity;
import com.hithing.hsc.bll.android.AndGrdDaoManager;
import com.hithing.hsc.bll.android.GreederSettingsHelper;
import com.hithing.hsc.bll.control.DinnerTableCtrl;
import com.hithing.hsc.bll.control.OrderCtrl;
import com.hithing.hsc.bll.manage.DinnerTableManager;
import com.hithing.hsc.bll.manage.DinnerTableTypeManager;
import com.hithing.hsc.bll.manage.FoodMainSortManager;
import com.hithing.hsc.bll.manage.FoodManager;
import com.hithing.hsc.bll.manage.GreederDaoManager;
import com.hithing.hsc.bll.util.CompoundItem;
import com.hithing.hsc.bll.util.MomsgFactory.MomsgType;
import com.hithing.hsc.bll.util.WorkContext;
import com.hithing.hsc.dataentity.DinnerTableEntity;
import com.hithing.hsc.dataentity.DinnerTableTypeEntity;
import com.hithing.hsc.dataentity.FoodMainSortEntity;
import com.hithing.hsc.entity.OrderItemKey;
import com.hithing.hsc.entity.OrderItemValue;
import com.hithing.hsc.entity.MoMsg.CMsgFoundingRsp;
import com.hithing.hsc.entity.MoMsg.CMsgImageLoadRsp;
import com.hithing.hsc.entity.MoMsg.CMsgOrderRsp;
import com.hithing.hsc.entity.MoMsg.CMsgViewBillRsp;
import com.hithing.hsc.entity.MoMsg.CMsgViewBillRsp.CMsgOrderFood;
import com.hithing.hsc.entity.OrderItemKey.FoodStatus;
import com.hithing.hsc.entity.OrderItemValue.Builder;
import com.hithing.hsc.entity.OrderItemValue.RemarkItem;
import com.hithing.hsc.entity.SearchableAdapterItem.FoodAdapterItem;
import com.hithing.hsc.entity.SearchableAdapterItem.StyledAdapterItem;
import com.hithing.util.DialogPromptType;
import com.hithing.util.StringKey;
import com.hithing.widget.AbsMultiStyleAdapter;
import com.hithing.widget.MultiColorStyleAdapter;
import com.hithing.widget.OrderItemsAdapter;
import com.hithing.widget.AbsMultiStyleAdapter.FilterMode;

public class ActivityMenus extends ProtobufActivity implements TextWatcher {
	private final int DIALOG_TYPE_PROMPT = 1;					 //对话框类型-提示信息对话框
	private final int DIALOG_ORDER_PROMPT = 2;					 //落单提示信息对话框
	private final int DIALOG_ORDER_FINISH = 3;					 //落单提示信息对话框
	private final int MOMSG_RSP_STATUS_ADDMENUS_SUCCESS = 2;     //在已开的台再 添加菜品
	private Dialog mDialog;                                      // 加载数据进度条对话框
	protected static final int STOP_NOTIFIER = 000;
	private ListView orderLsv;                                   // 详细账单
	private GridView foodGdv;                                    // 菜品视图
	private GridView popGridView;                                // pop列表
	WorkContext wContext = WorkContext.getInstance();
	public Context mainContext;                                  // 工作环境上下文
	private final int REQUEST_CODE_ACTRD = 1;	                 //做法和要求活动返回码	
	private final int REQUEST_CODE_ACTCU = REQUEST_CODE_ACTRD + 1;	 //数量和单位活动返回码	
	private final int REQUEST_CODE_ACTAU = REQUEST_CODE_ACTCU + 1;	//输入授权码活动返回码
	private final int REQUEST_CODE_ACTDD = REQUEST_CODE_ACTAU + 1;	//显示菜品详细信息活动
	private AbsMultiStyleAdapter subSortAdapter;                 // 菜品小类数据适配器
	private MenusAdapter foodAdapter;                            // 菜品数据适配器
	private OrderItemsAdapter orderAdapter;                      // 菜品订单条目数据适配器
	private ArrayAdapter<DinnerTableEntity> tableAdapter;		 		             //餐台数据适配器
	private final int DEFAULT_CUSTOMER_COUNT 	= 0;             //默认开台人数
	private OrderCtrl orderCtrl;                                 // 账单控制类实例
	private FoodManager foodManager;                             // 菜品数据管理器
	FoodMainSortManager fmsMgr;
	private EditText searchEdt;                                  // 搜索编辑框
	private TextView countInfoTxv;                               // 菜品个数和总价信息视图
	private OrderItemKey curOrderItemKey;	 	                 //账单当前菜品键
	private OrderItemValue curOrderItemValue; 			         //账单当前菜品值	
	private Intent cuIntent;	                                 //用于开启设置数量和单位活动
	private Intent auIntent; 		                             //用于开启输入授权码活动
	private Bundle promptArgs = new Bundle();	 	             //提示对话框参数
	private PopupWindow popupWindow;
	private View popupWindow_view;
	private GreederDaoManager grdManager;
	private ImageWorkDataCache cache = ImageWorkDataCache.INSTANCE; // 工作数据缓存
	private DinnerTableCtrl	 currentTable  = new DinnerTableCtrl();	//当前选中的餐台标识
	private List<FoodMainSortEntity> fmsList;
	private LinearLayout mainSortContainer;
	private File imageCache;                                     // 图片缓存
	private int tableSpareColor;		                         //餐桌空闲时的颜色
	private int	tableUsingColor; 	                             //餐桌使用时的颜色
    private Resources resources;
	private Button orders;
	private int tableId;
	private EditText authcodeEdt;					             //授权码文本编辑框
	private AutoCompleteTextView tableAuto;
    private EditText peoples;
    private HashMap<Integer, StyledAdapterItem> dtMap;            //餐台数据
    private LayoutInflater inflater;
    private String authCode;                                      //落单授权码
    private long priceId;
    private List<DinnerTableEntity> tableList;
    
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.progerss_backgroud);
		LoadData data = new LoadData();
		data.execute();
		// 创建本地缓存文件夹
		imageCache = new File(this.getCacheDir().getAbsolutePath());
		if (!imageCache.exists()) {
			imageCache.mkdir();
		}
	}

	// 网络通讯准备工作
	public void prepare() {
		try {
			// 配置全局环境变量
			wContext.setCompCode("yuanhe");
			wContext.setTermCode("1001");
			CompoundItem hall = new CompoundItem();
			hall.id = 1;
			hall.name = "缘何茶馆中餐厅";
			wContext.setHall(hall);

					try {
//						 msgHelper.initInstance("114.215.31.48", 20000);
						msgHelper.initInstance("192.168.10.253", 10527);
					} catch (Exception e) {
						e.printStackTrace();
					}
					msgHelper.startReceivingMomsg(handler);
					msgHelper.startIntervalSync();

			Context mainContext = null;
			try {
				mainContext = this.createPackageContext("com.hithing.hsc",
						CONTEXT_IGNORE_SECURITY);
			} catch (NameNotFoundException e) {
				e.printStackTrace();
			}

			// 初始化数据访问对象管理者
			cache.initInstance(mainContext);
			grdManager = new AndGrdDaoManager(mainContext);
			foodManager = new FoodManager(grdManager.getFoodDao(),grdManager.getFoodPriceDao());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void initView(){
		countInfoTxv = (TextView) findViewById(R.id.waiterorder_txv_countinfo_total);
		mainSortContainer = (LinearLayout) findViewById(R.id.menus_container_mainsort);
		fmsMgr = new FoodMainSortManager(grdManager.getHallFoodMainSortDao());
		fmsList = fmsMgr.getAllFoodMainSorts(wContext.getHall().id);
		final Set<Integer> fmsIdSet = new HashSet<Integer>();
		Button btn;
		for (FoodMainSortEntity ent : fmsList) {
			btn = (Button) LayoutInflater.from(ActivityMenus.this).inflate(
					R.layout.act_menus_maintype_item, mainSortContainer, false);
			btn.setText(ent.getName());
			btn.setTag(ent.getId());
			mainSortContainer.addView(btn);
			fmsIdSet.add(ent.getId());
		}

		// 初始化菜品小类
		subSortAdapter = new MultiColorStyleAdapter(ActivityMenus.this,cache.getFoodSubSortData(), R.layout.act_menus_small_item);
		// 初始化菜品
		foodGdv = (GridView) findViewById(R.id.gv_foods);
		foodAdapter = new MenusAdapter(ActivityMenus.this, cache.getFoodData(),R.layout.gv_menu_item);
		foodGdv.setAdapter(foodAdapter);

		orderLsv = (ListView) findViewById(R.id.list_order_info);
		final Intent rmIntent 	= new Intent(this, ActivityRecipeDemand.class);
		orderLsv.setOnItemClickListener(new OnItemClickListener() {		
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position,long id) {				
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
		
		foodGdv.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View item,
					int position, long id) {
				// 在账单中添加未下单的菜品
				addFood((int)id);
				// 如果搜索框有内容,则清空:处理搜索的情况,如果未搜索则不能置空
				if (!TextUtils.isEmpty(searchEdt.getText().toString())) {
					searchEdt.setText(null);
				}
			}
		});

		searchEdt = (EditText) findViewById(R.id.order_edt_search);
		searchEdt.addTextChangedListener(this);

		cuIntent = new Intent(this, ActivityCountUnit.class);
		
		foodGdv.setOnItemLongClickListener(new OnItemLongClickListener() {

			@Override
			public boolean onItemLongClick(AdapterView<?> parent, View item,
					int position, long id) {
				FoodAdapterItem foodAdapterItem = (FoodAdapterItem) parent.getItemAtPosition(position);
				String image = ActivityMenus.this.getCacheDir().getAbsolutePath() + "/" + foodAdapterItem.getPhotoName();
				String foodName = foodAdapterItem.getContent();
				String price = foodAdapterItem.getPrice();
				String remark = foodManager.getFoodByPriceId((int) id).getRemark();
				String sort = foodManager.getFoodByPriceId((int)id).getFoodSubSort().getName();
				String mainSort = foodManager.getFoodByPriceId((int)id).getFoodSubSort().getFoodMainSort().getName();
				priceId = id;
				
				Intent intent = new Intent(ActivityMenus.this,ActivityDishDetail.class);
				intent.putExtra("price", price);
				intent.putExtra("name", foodName);
				intent.putExtra("img", image);
				intent.putExtra("remark", remark);
				intent.putExtra("sort", sort);
				intent.putExtra("mainSort", mainSort);
				intent.putExtra("priceId", priceId);
				
				startActivityForResult(intent, REQUEST_CODE_ACTDD);
				return false;
			}
		});

		// 发送更新菜品订单请求
		List<Integer> ids = new LinkedList<Integer>();
		ids.add(R.id.waiterorder_txv_food_name);
		ids.add(R.id.waiterorder_txv_food_count);
		ids.add(R.id.waiterorder_txv_food_cost);
		ids.add(R.id.waiterorder_txv_food_description);
		
		orderCtrl = new OrderCtrl(tableId, foodManager);		
		orderAdapter = new OrderItemsAdapter(this, orderCtrl.getOrderData(),R.layout.lv_order_item, ids);
		orderLsv.setAdapter(orderAdapter);
        orderLsv.setBackgroundColor(Color.TRANSPARENT);  //设置透明
		orderCtrl.refresh(); // 发送菜单查看消息
		
		//落单
		orders = (Button) findViewById(R.id.orders);
		orders.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				String text = ((Button)v).getText().toString();
				//点击落单的情况
				if(text == getString(R.string.worder_btn_placeorder_text)){	
					dialog(DIALOG_ORDER_PROMPT);
					   }
			}
		});

		resources 		= getResources();
		tableSpareColor = resources.getColor(R.color.table_spare_background);
		tableUsingColor = resources.getColor(R.color.table_using_background);
		DinnerTableTypeManager 		dttMgr				= new DinnerTableTypeManager(grdManager.getDinnerTableTypeDao());
		List<DinnerTableTypeEntity> dttList 			= dttMgr.getAllTypesByHallId(wContext.getHall().id);
		Set<Integer> 				dttIdSet			= new HashSet<Integer>();			
					
		int index = 0;
		for(DinnerTableTypeEntity ent : dttList){
			dttIdSet.add(ent.getId());				
			index++;
		}
		
		//初始化餐台数据
		DinnerTableManager 		tableMgr 	= new DinnerTableManager(grdManager.getDinnerTableDao());
		tableList 	= tableMgr.getAllTablesByTypes(dttIdSet);
		
		dtMap = new LinkedHashMap<Integer, StyledAdapterItem>();
		StyledAdapterItem 	item;
		for(DinnerTableEntity ent : tableList){
			item = new StyledAdapterItem(ent.getType().getId(), tableSpareColor, ent.getName(), ent.getIndex());
			dtMap.put(ent.getId(),item);
		}
		tableAdapter = new ArrayAdapter<DinnerTableEntity>(this, android.R.layout.simple_dropdown_item_1line, tableList);
	}
	
  public void addFood(int priceId){
	// 在账单中添加未下单的菜品
		orderCtrl.addFood((int) priceId);
		orderAdapter.refresh();
		orderLsv.setSelection(orderAdapter.getCount() - 1);
		refreshTotalInfo();
}
	
	// 刷新账单汇总信息
	private void refreshTotalInfo() {
		countInfoTxv.setText(String.valueOf(orderCtrl.getAllFoodsCount())+"/"+orderCtrl.refreshTotal());
	}

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
				countInfoTxv.setText(String.valueOf(orderCtrl.getAllFoodsCount())+"/"+orderCtrl.refreshTotal());
			}
			break;
		case REQUEST_CODE_ACTDD:
			if(resultCode == RESULT_OK){
				addFood((int)priceId);
			}
			break;
		}
	}
	
	protected void dialog(int id) {
		switch (id) {
	   case DIALOG_ORDER_PROMPT:
		inflater = getLayoutInflater();
		View layout = inflater.inflate(R.layout.act_authcode_content,null);
		tableAuto = (AutoCompleteTextView)layout.findViewById(R.id.table_auto);
		authcodeEdt = (EditText)layout.findViewById(R.id.authcode_edt_code);
		peoples = (EditText)layout.findViewById(R.id.table_num_people);
		tableAuto.setAdapter(tableAdapter);
		tableAuto.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View item, int position,long id) {
				DinnerTableEntity entity = (DinnerTableEntity) parent.getItemAtPosition(position);
				tableId = entity.getId();
				currentTable.setTableId(tableId);
			}
		});
		
		new AlertDialog.Builder(this).setTitle("落单").setView(layout)
			.setPositiveButton("确定",new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog,
								int which) {
							authCode = authcodeEdt.getText().toString();
							String numPeople = peoples.getText().toString();
							
							//设置餐台人数
							if(!TextUtils.isEmpty(numPeople) &&!TextUtils.isEmpty(authCode)){
								if(authCode.equals("1001")){
									currentTable.founding(Integer.valueOf(numPeople));
								}else {
								Toast.makeText(ActivityMenus.this, "验证码有误，请重新输入!", Toast.LENGTH_LONG).show();
								dialog(DIALOG_ORDER_PROMPT);
								}
							}else {
								Toast.makeText(ActivityMenus.this, "请输入就餐人数和验证码!", Toast.LENGTH_LONG).show();
							    dialog(DIALOG_ORDER_PROMPT);
							}
						}
					}).setNegativeButton("取消", null).create().show();
		break;
		case DIALOG_ORDER_FINISH:
			new AlertDialog.Builder(this).setTitle("落单完成").setMessage("落单成功，正在打印账单！")
			.setPositiveButton("确定",new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					orderCtrl.resetData();
					orderAdapter.refresh();
				}
			}).show();
			break;
		}
	}
	
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
	
	@Override
	public void onMomsgResult(MomsgType rspType, GeneratedMessage msg) {
		switch (rspType) {
		case IMAGELOAD_RSP:   //获取并保存图片
			CMsgImageLoadRsp imageRsp = (CMsgImageLoadRsp) msg;
			if (imageRsp.hasStatus()) {
				if (imageRsp.getStatus() == MOMSG_RSP_STATUS_SUCCESS) {
					saveImageToLocal(imageRsp.getData().toByteArray(),imageRsp.getName());
				}
			}
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
		case FOUNDING_RSP:			//开台
			CMsgFoundingRsp fRsp = (CMsgFoundingRsp)msg;
			orderCtrl.setTable(tableId);    //设置餐台
			Log.e("frsp-->", "frsp--->" + fRsp.getStatus());
			if(fRsp.getStatus() == MOMSG_RSP_STATUS_SUCCESS){
				orderCtrl.placeOrder(Integer.parseInt(authCode));
			}else if(fRsp.getStatus() == MOMSG_RSP_STATUS_ADDMENUS_SUCCESS){ //开台后再次添加菜品
				orderCtrl.placeOrder(Integer.parseInt(authCode));
			}
			break;
		case ORDER_RSP:
			Log.e("WaiterOrder.onMomsgResult", "ORDER_RSP");
			promptArgs.putSerializable(StringKey.BUNDLE_KEY_TYPE, DialogPromptType.PROMPT_VALIDATEAUTH);
			CMsgOrderRsp rsp = (CMsgOrderRsp)msg;	
			Log.e("rsp", "rsp--->" + rsp.getStatus());
			if(rsp.hasStatus()){
				if(rsp.getStatus() != MOMSG_RSP_STATUS_SUCCESS){			//下单不成功										
					showDialog(DIALOG_TYPE_PROMPT, promptArgs);
				}else{														//下单成功则打印
					//刷新账单
					dialog(DIALOG_ORDER_FINISH);
					//如果是楼面模块调用，则开启打印服务										暂时屏蔽
//					if(caller.equals(ActivityDinnerTable.TAG)){						
//						piIntent.putExtra(StringKey.INTENT_EXTRA_KEY_TABLE, tableName);
//						piIntent.putExtra(StringKey.INTENT_EXTRA_KEY_USER, rsp.getName().toStringUtf8());
//						piIntent.putExtra(StringKey.INTENT_EXTRA_KEY_TIME, rsp.getTime());
//						piIntent.putExtra(StringKey.INTENT_EXTRA_KEY_FOOD, (HashMap<OrderItemKey, OrderItemValue>)orderCtrl.getUnOrderData());
//						startService(piIntent);																	
//					}					
					
//					NeedAuthcodeType type = (NeedAuthcodeType)auIntent.getSerializableExtra(StringKey.INTENT_EXTRA_KEY_TYPE);					
					//处理落单的情况					
//					if(type == NeedAuthcodeType.placeorder){				//落单							
//						onBackPressed();
//					} 		
//					orderCtrl.clearUndoData();
				}
			}
			break;	
		}
	}

	public void saveImageToLocal(byte[] temp, String name) {
		if (temp != null) {
			File file = new File(imageCache, name);
			FileOutputStream outStream;
			try {
				outStream = new FileOutputStream(file);
				outStream.write(temp);
				outStream.close();
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	// 主菜单点击监听
	public void onMainSortClick(View v) {
		subSortAdapter.setFilterMode(FilterMode.Type);
		int id = Integer.valueOf(v.getTag().toString());
		Filter filter = subSortAdapter.getFilter();
		filter.filter(String.valueOf(id));
		show(v);
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
		}
	}	
	
	/**
	 * 创建PopupWindow
	 */
	protected void initPopuptWindow() {
		// 获取自定义布局文件pop.xml的视图
		popupWindow_view = getLayoutInflater().inflate(R.layout.gv_second_menu,null);
		// 创建PopupWindow实例,500, 250分别是宽度和高度
		popupWindow = new PopupWindow(popupWindow_view, 500, 250, true);
		// 设置动画效果
		popupWindow.setAnimationStyle(R.style.AnimationFade);
		// 设置点击其他地方 popupWindow消失
		popupWindow.setOutsideTouchable(true);
		// 点击外部消失
		popupWindow_view.setOnTouchListener(new OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				if (popupWindow != null && popupWindow.isShowing()) {
					popupWindow.dismiss();
					popupWindow = null;
				}
				return false;
			}
		});
		popGridView = (GridView) popupWindow_view.findViewById(R.id.second_menus);
		popGridView.setAdapter(subSortAdapter);
		popGridView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View v,int position, long id) {
				foodAdapter.setFilterMode(FilterMode.Type);
				Filter filter = foodAdapter.getFilter();
				filter.filter(String.valueOf(id));
				foodGdv.setAdapter(foodAdapter);
				popupWindow.dismiss();
			}
		});
	}

	/***
	 * 获取PopupWindow实例
	 */
	private void getPopupWindow() {
		if (null != popupWindow) {
			popupWindow.dismiss();
			return;
		} else {
			initPopuptWindow();
		}
	}

	// 显示popuwindow
	public void show(View v) {
		getPopupWindow();
		popupWindow.showAtLocation(v, 0, 380, 146);
	}

	// 加载应用程序数据
	private void loadDataInBackground() {
		// 载入菜品信息
				Log.e("loadDataInBackground", "loding data started!"); // test
				FoodMainSortManager fmsMgr = new FoodMainSortManager(grdManager
						.getHallFoodMainSortDao());
				Set<Integer> fmsIdSet = fmsMgr.getAllFoodMainSortIds(wContext
						.getHall().id);
				Set<Integer> fssIdSet = cache.lodingFoodSubSortData(fmsIdSet);
				cache.lodingFoodData(fssIdSet);
				cache.lodingPrintData(wContext.getHall().id);
				Log.e("loadDataInBackground", "loding data stopped!"); // test
	}

	@Override
	public void onTextChanged(CharSequence s, int start, int before, int count) {
		if (foodGdv.getVisibility() == View.GONE) {
			foodGdv.setVisibility(View.VISIBLE);
		}
		Filter filter = foodAdapter.getFilter();
		foodAdapter.setFilterMode(FilterMode.Index);
		filter.filter(s.toString());
		foodGdv.setAdapter(foodAdapter);
	}

	// 显示连接数据进度条
	public void showRoundProcessDialog(Context mContext, int layout) {
		mDialog = new AlertDialog.Builder(mContext).create();
		mDialog.show();
		mDialog.setCancelable(false);
		mDialog.setContentView(layout);
	}

	class LoadData extends AsyncTask<Integer, Integer, String>{
		@Override
		protected String doInBackground(Integer... params) {
			prepare();
			loadDataInBackground();
			return null;
		}
		
		@Override
		protected void onPreExecute() {
			showRoundProcessDialog(ActivityMenus.this, R.layout.loading_process_dialog_anim); // 进度条
		super.onPreExecute();
		}
		
		@Override
		protected void onPostExecute(String result) {
		setContentView(R.layout.act_menus_main);
		initView();
		mDialog.dismiss();
		super.onPostExecute(result);
		}
	}
  @Override
protected void onDestroy() {
	msgHelper.clearInstance();
	grdManager.release();
	android.os.Process.killProcess(android.os.Process.myPid());	
}	
  @Override
	public void afterTextChanged(Editable s) {}

	@Override
	public void beforeTextChanged(CharSequence s, int start, int count,int after) {}

}