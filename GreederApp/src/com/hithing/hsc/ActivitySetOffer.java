/**========================================
 * File:	ActivitySetOffer.java
 * Package:	com.hithing.hsc
 * Create:	by Leopard
 * Date:	2012-2-17:上午9:31:38
 **======================================*/
package com.hithing.hsc;

import java.util.EnumSet;
import java.util.LinkedList;
import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.KeyEvent;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnCreateContextMenuListener;
import android.view.ViewGroup;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.Button;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.google.protobuf.GeneratedMessage;
import com.hithing.app.ProtobufActivity;
import com.hithing.app.ProtobufActivityGroup.IChildResultHandlable;
import com.hithing.hsc.bll.android.AndGrdDaoManager;
import com.hithing.hsc.bll.control.OfferCtrl;
import com.hithing.hsc.bll.control.OfferCtrl.DiscountMode;
import com.hithing.hsc.bll.manage.DinnerTableManager;
import com.hithing.hsc.bll.manage.FoodManager;
import com.hithing.hsc.bll.manage.GreederDaoManager;
import com.hithing.hsc.bll.util.MomsgFactory.MomsgType;
import com.hithing.hsc.dataentity.DinnerTableEntity;
import com.hithing.hsc.dataentity.OperateReasonEntity.OperateType;
import com.hithing.hsc.entity.MoMsg.CMsgBillPricingRsp;
import com.hithing.hsc.entity.MoMsg.CMsgViewBillRsp;
import com.hithing.hsc.entity.MoMsg.CMsgViewBillRsp.CMsgOrderElement;
import com.hithing.hsc.entity.MoMsg.CMsgViewBillRsp.CMsgOrderFood;
import com.hithing.hsc.entity.OrderItemKey;
import com.hithing.hsc.entity.OrderItemKey.FoodStatus;
import com.hithing.hsc.entity.OrderItemValue;
import com.hithing.hsc.entity.OrderItemValue.Builder;
import com.hithing.hsc.global.IntegerConst;
import com.hithing.hsc.global.StringKey;
import com.hithing.widget.OfferItemsAdapter;

/**
 * <p>
 * ActivitySetOffer
 * </p>
 * 
 * <p>
 * 用于打折的活动
 * </p>
 * 
 * @author Leopard
 * 
 */
public class ActivitySetOffer extends ProtobufActivity implements
		OnClickListener, TextWatcher {
    
	private final int REQUEST_CODE_ACTCU = 1;                      	// 设置数量和单位活动请求码
	private final int REQUEST_CODE_ACTFP = REQUEST_CODE_ACTCU + 1; 	// 设置金额活动请求码
	private final int REQUEST_CODE_ACTFR = REQUEST_CODE_ACTFP + 1; 	// 设置折扣率活动请求码
	private final int REQUEST_CODE_ACTFM = REQUEST_CODE_ACTFR + 1; 	// 设置折扣金额活动请求码

	private final int REQUEST_CODE_ACTOR = REQUEST_CODE_ACTFM + 1; 	// 设置账单折扣率活动请求码
	private final int REQUEST_CODE_ACTOM = REQUEST_CODE_ACTOR + 1; 	// 设置折扣金额活动请求码
	private final int REQUEST_CODE_ACTOS = REQUEST_CODE_ACTOM + 1; 	// 设置服务费率活动请求码
	private final int REQUEST_CODE_ACTOL = REQUEST_CODE_ACTOS + 1; 	// 设置最低消费金额活动请求码
	private final int REQUEST_CODE_ACTOC = REQUEST_CODE_ACTOL + 1; 	// 设置取消折扣率活动请求码
	private final int REQUEST_CODE_ACTOP = REQUEST_CODE_ACTOC + 1; 	// 设置赠送菜品活动请求码
	private final int REQUEST_CODE_ACTPC = REQUEST_CODE_ACTOP + 1; 	// 设置取消菜品活动请求码
	
	private final int DIALOG_TYPE_PROMPT = 1;						//对话框类型-提示	
	private final int MENUINFO_POSITION  = 1;                      	//menu的位置position	
	
	private int 				tableId;                            // 菜品账单关联餐台标识
	private int 				authCode;							// 用户授权吗

	private MenuInflater         menuInflater;                     	// menu菜单
	private OfferCtrl            offerCtrl;                        	// 账单标价控制类实例
	private OfferItemsAdapter    offerAdapter;                     	// 账单数据适配器
	
	private OrderItemKey         curOfferItemKey;                  	// 账单当前菜品键
	private OrderItemValue       curOfferItemValue;                	// 账单当前菜品值
	
	private Button               lowestBtn;                        	// 最低消费
	private Button               serviceBtn;                       	// 服务费按钮
	private Button               discountRatioBtn;                 	// 折扣率
	private Button               discountMoneyBtn;                 	// 折扣金额
	private Button               cancleDiscBtn;                    	// 取消折扣	
	private TextView             txvConsumeTotal;                  	// 消费合计
	private TextView             txvService;                       	// 服务费
	private TextView             txvSum;                           	// 合计
	private RadioButton          allWithRbtn;                      	// 全单按钮
	private RadioButton          allNoRbtn;                        	// 照单按钮
	private ProgressDialog		 waitingDialog;					   	// 等待对话框
	
	
	private Intent cuIntent;                                       // 用于开启设置数量和单位活动
	private Intent smIntent;                                       // 用于开启设置金额的活动
	private Intent srIntent;                                       // 用于开启设置比率的活动
	private Intent rmIntent;                                       // 用于开启设置赠送和取消菜品的活动
	private IChildResultHandlable parentHandler;                   // 可处理子活动结果的父活动句柄

	public void onSingleOperateItem(View v) {
		int id = v.getId(); 
		int position = Integer.parseInt(((ViewGroup) v.getParent()).getTag()
				.toString());
		curOfferItemKey = offerAdapter.getItemKey(position);
		curOfferItemValue = (OrderItemValue) offerAdapter.getItem(position);
		switch (id) {
		case R.id.setoffer_txv_countunit:                         // 点击份数单位的情况
			                                                      // 只有未下单的菜品才可以设置份数和单位
			cuIntent.putExtra(StringKey.INTENT_EXTRA_KEY_COUNT,
					curOfferItemValue.getCount());
			cuIntent.putExtra(StringKey.INTENT_EXTRA_KEY_PRICE,
					curOfferItemKey.priceId);
			cuIntent.putExtra(StringKey.INTENT_EXTRA_KEY_FOOD,
					curOfferItemValue.getFood());
			startActivityForResult(cuIntent, REQUEST_CODE_ACTCU);
			break;
		case R.id.setoffer_txv_price:                              // 点击菜品单价的情况
			smIntent.putExtra(StringKey.INTENT_EXTRA_KEY_TITLE,
					curOfferItemValue.getFood().content);
			smIntent.putExtra(ActivitySetMoney.INTENT_EXTRA_KEY_MONEY,
					curOfferItemValue.getFoodPrice());
			startActivityForResult(smIntent, REQUEST_CODE_ACTFP);
			break;
		case R.id.setoffer_txv_discountratio:                      // 点击菜品折扣率的情况
			srIntent.putExtra(StringKey.INTENT_EXTRA_KEY_TITLE,
					curOfferItemValue.getFood().content);
			srIntent.putExtra(ActivitySetRatio.INTENT_EXTRA_KEY_RATIO,
					curOfferItemValue.getRatioDiscount());
			startActivityForResult(srIntent, REQUEST_CODE_ACTFR);
			break;
		case R.id.setoffer_txv_discountmoney:                       // 点击菜品折扣金额的情况
			smIntent.putExtra(StringKey.INTENT_EXTRA_KEY_TITLE,
					curOfferItemValue.getFood().content);
			smIntent.putExtra(ActivitySetMoney.INTENT_EXTRA_KEY_MONEY,
					curOfferItemValue.getMoneyDiscount());
			startActivityForResult(smIntent, REQUEST_CODE_ACTFM);
			break;
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.hithing.app.ProtobufActivity#onCreate(android.os.Bundle)
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);

		setContentView(R.layout.act_setoffer_main);
        
		menuInflater = getMenuInflater();
		
		Activity parentActivity = getParent();
		if (parentActivity != null
				&& parentActivity instanceof IChildResultHandlable) {
			parentHandler = (IChildResultHandlable) parentActivity;
		}

		GreederDaoManager grdManager = new AndGrdDaoManager(this);
		FoodManager foodMgr = new FoodManager(grdManager.getFoodDao(),
				grdManager.getFoodPriceDao());

		Intent intent = getIntent();
		tableId = intent.getIntExtra(StringKey.INTENT_EXTRA_KEY_TABLE,
				IntegerConst.DEFAULT_INTEGER_VALUE);
		authCode = intent.getIntExtra(StringKey.INTENT_EXTRA_KEY_AUTHCODE, IntegerConst.DEFAULT_INTEGER_VALUE);
		offerCtrl = new OfferCtrl(tableId, foodMgr);
		offerCtrl.refresh();
		

		ListView orderLsv = (ListView) findViewById(R.id.setoffer_lsv_offeritems);
	    ViewGroup header = (ViewGroup) getLayoutInflater().inflate(
				R.layout.act_setoffer_header, orderLsv, false);
		orderLsv.addHeaderView(header);
		List<Integer> ids = new LinkedList<Integer>();
		ids.add(R.id.setoffer_txv_food);
		ids.add(R.id.setoffer_txv_countunit);
		ids.add(R.id.setoffer_txv_price);
		ids.add(R.id.setoffer_txv_prediscount);
		ids.add(R.id.setoffer_txv_discountratio);
		ids.add(R.id.setoffer_txv_discountmoney);
		ids.add(R.id.setoffer_txv_postdiscount);
		offerAdapter = new OfferItemsAdapter(this, offerCtrl.getOrderData(),
				R.layout.act_setoffer_offer_item, ids);
		orderLsv.setAdapter(offerAdapter);
//		orderLsv.setVisibility(View.GONE);
//		offerAdapter.notifyDataSetChanged();
//		orderLsv.setVisibility(View.VISIBLE);
		registerForContextMenu(orderLsv);
        orderLsv.setOnCreateContextMenuListener(new OnCreateContextMenuListener() {

			@Override
			public void onCreateContextMenu(ContextMenu menu, View v,
					ContextMenuInfo menuInfo) {
				AdapterContextMenuInfo info = (AdapterContextMenuInfo) menuInfo;
				curOfferItemKey       = (OrderItemKey) offerAdapter.getItemKey(info.position - MENUINFO_POSITION);
				curOfferItemValue     = (OrderItemValue) offerAdapter.getItem(info.position - MENUINFO_POSITION);
				if(curOfferItemKey.status == FoodStatus.FOOD_STATUS_DONE){
					menu.setHeaderTitle(R.string.common_cmn_operate_title);
					menuInflater.inflate(R.menu.act_waiteroffer_cmn_offer, menu);
					if(curOfferItemKey.status == FoodStatus.FOOD_STATUS_PRESENT){
						menu.removeItem(R.id.opefood_cmi_format_present);
					}
				}
			}});
		
		cuIntent = new Intent(this, ActivityCountUnit.class);
		smIntent = new Intent(this, ActivitySetMoney.class);
		srIntent = new Intent(this, ActivitySetRatio.class);
		rmIntent = new Intent(this, ActivityOperateFood.class);
		
		initview();
	}

	@Override
	public boolean onContextItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.opefood_cmi_format_present:     //当有已下单的菜品时才有赠送的对话框
				 rmIntent.putExtra(StringKey.INTENT_EXTRA_KEY_FOOD,
				 curOfferItemValue.getFood().content);
				 rmIntent.putExtra(StringKey.INTENT_EXTRA_KEY_TYPE, OperateType.present);
				 rmIntent.putExtra(StringKey.INTENT_EXTRA_KEY_COUNT,
				 curOfferItemValue.getCount());
				 rmIntent.putExtra(StringKey.INTENT_EXTRA_KEY_UNIT,
				 curOfferItemValue.getUnit()); 
				 startActivityForResult(rmIntent,REQUEST_CODE_ACTOP); 
				 
			break;
		case R.id.opefood_cmi_format_cancel:
				rmIntent.putExtra(StringKey.INTENT_EXTRA_KEY_FOOD,
				curOfferItemValue.getFood().content);
				rmIntent.putExtra(StringKey.INTENT_EXTRA_KEY_TYPE, OperateType.cancel);
				rmIntent.putExtra(StringKey.INTENT_EXTRA_KEY_COUNT,
				curOfferItemValue.getCount());
				rmIntent.putExtra(StringKey.INTENT_EXTRA_KEY_UNIT,
				curOfferItemValue.getUnit()); 
				startActivityForResult(rmIntent, REQUEST_CODE_ACTPC);
			break;
		default:
			break;
		}
		return super.onContextItemSelected(item);
	}

	@Override
	public void onMomsgResult(MomsgType rspType, GeneratedMessage msg) {
		switch (rspType) {
		case INTERVALSYNC_RSP:
			Log.d("SetOffer.onMomsgResult", "INTERVALSYNC_RSP");
			break;
		case VIEWBILL_RSP:
			Log.d("SetOffer.onMomsgResult", "VIEWORDER_RSP");
			CMsgViewBillRsp billRsp = (CMsgViewBillRsp)msg;
			if (billRsp.hasStatus()) {
				if (billRsp.getStatus() == MOMSG_RSP_STATUS_SUCCESS) {											

					OrderItemKey key;
					Builder valueBuilder;
//					int count;					
					
					for(CMsgOrderFood food : billRsp.getOrderFoodList()){
						valueBuilder = new Builder().setCanDiscount(food.getCandiscount()).
											  setFoodPrice(food.getFoodprice()).
											  setRatioDiscount(food.getDisratio()).
											  setMoneyDiscount(food.getDismoney()).
											  setRemark(food.getRemark()).
											  setCount(food.getFoodnum());
						key = new OrderItemKey(FoodStatus.valueOf(food.getType()),
								food.getFoodpriceid(),food.getOrderid());
						offerCtrl.loadFood(key, valueBuilder);													
					}
					offerAdapter.refresh();
					// 初始化账单级别的参数
					if(billRsp.hasOrderElement()){
						CMsgOrderElement orderElement = billRsp.getOrderElement();
						offerCtrl.setService(orderElement.getService());						
						offerCtrl.setLowest(orderElement.getIsLowest());
						offerCtrl.setDiscountRatio(orderElement.getDisRatio());						
						offerCtrl.setDiscountMoney(orderElement.getDisMoney());
						offerCtrl.setDiscountMode(DiscountMode.valueOf(orderElement.getDisType()));
						updateOrderBtnText(EnumSet.of(OrderOfferType.SERVICE, OrderOfferType.LOWEST, 
								OrderOfferType.RATIO, OrderOfferType.MONEY));
						initConsumeTotalAndServiceAndSum();
						notifyParentHandleResult(StringKey.INTENT_EXTRA_KEY_TOTAL, offerCtrl.refreshTotal());
					}														
					}					
				}
			waitingDialog.dismiss();
			break;
		case BILLPRICING_RSP:
			Log.e("SetOffer.onMomsgResult", "BILLPRICING_RSP");
			CMsgBillPricingRsp bpRsp = (CMsgBillPricingRsp)msg;
			if(bpRsp.hasStatus()){
				if(bpRsp.getStatus() == MOMSG_RSP_STATUS_SUCCESS){
					this.setResult(RESULT_OK);
					this.finish();
				}
			}			
			break;
		default:
			break;
		}
	}	

	@Override
	public void onClick(View v) {
		int id = v.getId();
		GreederDaoManager grdManager = new AndGrdDaoManager(this);
		DinnerTableManager tablemgr = new DinnerTableManager(
				grdManager.getDinnerTableDao());
		DinnerTableEntity ent = tablemgr.getTableById(tableId);
		switch (id) {

		case R.id.setoffer_btn_service: // 服务费率
			srIntent.putExtra(StringKey.INTENT_EXTRA_KEY_TITLE, ent.getName());
			srIntent.putExtra(ActivitySetRatio.INTENT_EXTRA_KEY_RATIO, offerCtrl.getService());
			startActivityForResult(srIntent, REQUEST_CODE_ACTOS);
			break;

		case R.id.setoffer_btn_lowest: // 最低消费
			smIntent.putExtra(StringKey.INTENT_EXTRA_KEY_TITLE, ent.getName());
			smIntent.putExtra(ActivitySetMoney.INTENT_EXTRA_KEY_MONEY,
					offerCtrl.getLowest());
			startActivityForResult(smIntent, REQUEST_CODE_ACTOL);
			break;

		case R.id.setoffer_btn_discountratio: // 点击菜品折扣率的情况
			srIntent.putExtra(StringKey.INTENT_EXTRA_KEY_TITLE, ent.getName());
			srIntent.putExtra(ActivitySetRatio.INTENT_EXTRA_KEY_RATIO,
					offerCtrl.getDiscountRatio());
			startActivityForResult(srIntent, REQUEST_CODE_ACTOR);
			break;

		case R.id.setoffer_btn_discountmoney: // 点击菜品折扣金额的情况
			smIntent.putExtra(StringKey.INTENT_EXTRA_KEY_TITLE, ent.getName());
			smIntent.putExtra(ActivitySetMoney.INTENT_EXTRA_KEY_MONEY,
					offerCtrl.getDiscountMoney());
			startActivityForResult(smIntent, REQUEST_CODE_ACTOM);
			break;

		case R.id.setoffer_btn_canceldiscount: // 取消菜品折扣率的情况
			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			builder.setTitle(R.string.setoffer_dlg_cancledisc_title)
					.setPositiveButton(R.string.common_btn_ok_text,
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog, int id) {
									offerCtrl.clearDiscount();
									updateOrderBtnText(EnumSet.of(OrderOfferType.RATIO, OrderOfferType.MONEY));
									offerAdapter.refresh(); 				
								}
							})
					.setNegativeButton(R.string.common_btn_cancel_text, new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int id) {
							dialog.cancel();
						}
					}).show();
			AlertDialog alert = builder.create();
			break;
		default:
			break;
		}
	}

	/* (non-Javadoc)
	 * @see android.app.Activity#onKeyDown(int, android.view.KeyEvent)
	 */
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if(keyCode == KeyEvent.KEYCODE_BACK){
			if(offerCtrl.isOfferChanged()){
				Bundle bundle = new Bundle();
				bundle.putString(StringKey.BUNDLE_KEY_MESSAGE, getString(R.string.setoffer_dlg_prompt_back_message));
				showDialog(DIALOG_TYPE_PROMPT, bundle);
			}
		}
		return super.onKeyDown(keyCode, event);
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Activity#onNewIntent(android.content.Intent)
	 */
	@Override
	protected void onNewIntent(Intent intent) {
		// TODO Auto-generated method stub
		super.onNewIntent(intent);
		tableId = intent.getIntExtra(StringKey.INTENT_EXTRA_KEY_TABLE, 0);		
		offerCtrl.setTable(tableId);
		offerCtrl.refresh();
		waitingDialog.show();
	}	

	/* (non-Javadoc)
	 * @see android.app.Activity#onCreateDialog(int, android.os.Bundle)
	 */
	@Override
	protected Dialog onCreateDialog(int id, Bundle args) {
		switch(id){
		case DIALOG_TYPE_PROMPT:
			AlertDialog dlg = new AlertDialog.Builder(this).setTitle(R.string.common_dlg_prompt_title).
															setMessage(args.getString(StringKey.BUNDLE_KEY_MESSAGE)).
															setPositiveButton(R.string.common_btn_ok_text, new DialogInterface.OnClickListener() {
																
																@Override
																public void onClick(DialogInterface dialog, int which) {
																	offerCtrl.offerBill(authCode);
																	setResult(RESULT_OK);
																	finish();
																}
															}).
															setNegativeButton(R.string.common_btn_cancel_text, new DialogInterface.OnClickListener() {
																
																@Override
																public void onClick(DialogInterface dialog, int which) {
																	onBackPressed();																	
																}
															}).
															create();
			return dlg;
		}
		return super.onCreateDialog(id, args);
	}

	/* (non-Javadoc)
	 * @see android.app.Activity#onPrepareDialog(int, android.app.Dialog, android.os.Bundle)
	 */
	@Override
	protected void onPrepareDialog(int id, Dialog dialog, Bundle args) {
		// TODO Auto-generated method stub
		super.onPrepareDialog(id, dialog, args);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Activity#onActivityResult(int, int,
	 * android.content.Intent)
	 */
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		String value;
		switch (requestCode) {		
		case REQUEST_CODE_ACTCU:
			if (resultCode == RESULT_OK) {
				// 更新账单菜品份数
				offerCtrl.specifyFoodCount(curOfferItemKey,
						data.getIntExtra(StringKey.INTENT_EXTRA_KEY_COUNT, 1));
				// 更新账单菜品计价单位
				int checkedId = data.getIntExtra(
						StringKey.INTENT_EXTRA_KEY_PRICE, -1);
				if (curOfferItemKey.priceId != checkedId) {
					offerCtrl.specifyFoodPrice(curOfferItemKey, checkedId);					
				}
			}
			break;
		case REQUEST_CODE_ACTFP:
			if (resultCode == RESULT_OK) {
				offerCtrl.modifyFoodPrice(curOfferItemKey,
								data.getStringExtra(ActivitySetMoney.INTENT_EXTRA_KEY_MONEY));
			}
			break;
		case REQUEST_CODE_ACTFR:
			if (resultCode == RESULT_OK) {
				offerCtrl.modifyDiscountRatio(curOfferItemKey,
								data.getStringExtra(ActivitySetRatio.INTENT_EXTRA_KEY_RATIO));
			}
			break;//
		case REQUEST_CODE_ACTFM:
			if (resultCode == RESULT_OK) {
				offerCtrl.modifyDiscountMoney(curOfferItemKey,
								data.getStringExtra(ActivitySetMoney.INTENT_EXTRA_KEY_MONEY));
			}
			break;

		case REQUEST_CODE_ACTOS:												//服务费率
			if (resultCode == RESULT_OK) {
				value = data.getStringExtra(ActivitySetRatio.INTENT_EXTRA_KEY_RATIO);
				offerCtrl.setService(value);													
				updateOrderBtnText(EnumSet.of(OrderOfferType.SERVICE));  		//在button上显示刷新的service数据
			}
			break;

		case REQUEST_CODE_ACTOL:												//最低消费
			if (resultCode == RESULT_OK) {
				value = data.getStringExtra(ActivitySetMoney.INTENT_EXTRA_KEY_MONEY);
				offerCtrl.setLowest(value);														
				updateOrderBtnText(EnumSet.of(OrderOfferType.LOWEST));  		//在button上显示最低消费金额							
			}
			break;

		case REQUEST_CODE_ACTOR:												//账单折扣率
			if (resultCode == RESULT_OK) {
				value = data.getStringExtra(ActivitySetRatio.INTENT_EXTRA_KEY_RATIO);
				offerCtrl.setDiscountRatio(value);												
				updateOrderBtnText(EnumSet.of(OrderOfferType.RATIO));  		//在button上显示实时折扣率					
			}
			break;
			
		case REQUEST_CODE_ACTOM:												//账单折扣金额
			if (resultCode == RESULT_OK) {
				value = data.getStringExtra(ActivitySetMoney.INTENT_EXTRA_KEY_MONEY);
				offerCtrl.setDiscountMoney(value);					
				updateOrderBtnText(EnumSet.of(OrderOfferType.MONEY));  		//在button上显示实时折扣金额
			}
			break;

		case REQUEST_CODE_ACTOC: 
			if (resultCode == RESULT_OK) {
				offerCtrl.setDiscountRatio(data
								.getStringExtra(ActivitySetRatio.INTENT_EXTRA_KEY_RATIO));
			}
			break;

		case REQUEST_CODE_ACTOP: 
			if (resultCode == RESULT_OK) {
				offerCtrl.presentFood(curOfferItemKey, data.getIntExtra(StringKey.INTENT_EXTRA_KEY_COUNT, curOfferItemValue.getCount()), 
						data.getStringExtra(ActivityOperateFood.INTENT_EXTRA_KEY_REASON));
			}
			break;
			
		case REQUEST_CODE_ACTPC: 
			if (resultCode == RESULT_OK) {
				offerCtrl.cancelFood(curOfferItemKey, data.getIntExtra(StringKey.INTENT_EXTRA_KEY_COUNT, curOfferItemValue.getCount()), 
						data.getStringExtra(ActivityOperateFood.INTENT_EXTRA_KEY_REASON));
			}
			break;
		}
		offerAdapter.refresh();
		// 存在可处理子活动结果的父活动
		notifyParentHandleResult(StringKey.INTENT_EXTRA_KEY_TOTAL, offerCtrl.refreshTotal());
	}

     private void initview() { 
    	serviceBtn 			= (Button) findViewById(R.id.setoffer_btn_service); 
     	lowestBtn 			= (Button) findViewById(R.id.setoffer_btn_lowest);
     	discountRatioBtn 	= (Button) findViewById(R.id.setoffer_btn_discountratio);
     	discountMoneyBtn 	= (Button) findViewById(R.id.setoffer_btn_discountmoney);
     	cancleDiscBtn 		= (Button) findViewById(R.id.setoffer_btn_canceldiscount);
		txvConsumeTotal 	= (TextView) findViewById(R.id.setoffer_txv_consumetotal);
    	txvService      	= (TextView) findViewById(R.id.setoffer_txv_service);
    	txvSum          	= (TextView) findViewById(R.id.setoffer_txv_sum);
    	allWithRbtn      	= (RadioButton) findViewById(R.id.setoffer_rbn_allwithexception);
    	allNoRbtn        	= (RadioButton) findViewById(R.id.setoffer_rbn_allnoexception);
    	
    	waitingDialog		= new ProgressDialog(this);
    	waitingDialog.setMessage(getString(R.string.setoffer_dlg_waiting_message));
    	
    	RadioGroup allModeRdg = (RadioGroup) findViewById(R.id.setoffer_rdp_allmode);
    	
     	serviceBtn.setOnClickListener(this);
 		serviceBtn.addTextChangedListener(this);
 		lowestBtn.setOnClickListener(this);
 		discountRatioBtn.setOnClickListener(this);
 		discountRatioBtn.addTextChangedListener(this);
 		discountMoneyBtn.setOnClickListener(this);
 		discountMoneyBtn.addTextChangedListener(this);
 		cancleDiscBtn.setOnClickListener(this);
 		cancleDiscBtn.addTextChangedListener(this); 		 		
    	
    	allModeRdg.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
			
			@Override
			public void onCheckedChanged(RadioGroup group, int checkedId) {
				if(checkedId == allWithRbtn.getId()){
					offerCtrl.setDiscountMode(DiscountMode.ALL_WITH_EXCEPTION);
				} else if(checkedId == allNoRbtn.getId()){
					offerCtrl.setDiscountMode(DiscountMode.ALL_NO_EXCEPTION);
				}
				offerAdapter.refresh();
				// 存在可处理子活动结果的父活动
				notifyParentHandleResult(StringKey.INTENT_EXTRA_KEY_TOTAL, offerCtrl.refreshTotal());
				
			}
		});
    	
	 }
	
     //初始化消费合计/服务费/合计
     private void initConsumeTotalAndServiceAndSum(){
    	String tvxformat = getString(R.string.setoffer_txv_text_format);
    	
    	String consumeTotalText = String.format(tvxformat,
				getString(R.string.setoffer_txv_consumetotal),
				offerCtrl.refreshConsume());
    	txvConsumeTotal.setText(consumeTotalText);
    	
    	String consumeServiceText = String.format(tvxformat,
				getString(R.string.setoffer_txv_service),
				offerCtrl.refreshService());
     	txvService.setText(consumeServiceText);
     	
     	String sumText = String.format(tvxformat,
				getString(R.string.setoffer_txv_sum),
				offerCtrl.refreshTotal());
     	txvSum.setText(sumText);
     }
     
  // 更新账单设置按钮文本ModifyKey
 	private void updateOrderBtnText(EnumSet<OrderOfferType> typeSet) {
 		String btnformat = getString(R.string.setoffer_btn_text_format);
 		
 		if(typeSet.contains(OrderOfferType.SERVICE)){
 			// 服务费
 			String serviceText = String.format(btnformat,
 					getString(R.string.setoffer_btn_service_text),
 					offerCtrl.getService());
 			serviceBtn.setText(serviceText);			
 		}
 		
 		if(typeSet.contains(OrderOfferType.LOWEST)){
 		// 最低消费
 		String lowestText = String.format(btnformat,
 				getString(R.string.setoffer_btn_lowest_text),
 				offerCtrl.getLowest());
 		lowestBtn.setText(lowestText);
 		}

 		if(typeSet.contains(OrderOfferType.RATIO)){
 		// 折扣率
 		String discountRatioText = String.format(btnformat,
 				getString(R.string.setoffer_btn_discountratio_text),
 				offerCtrl.getDiscountRatio());		
 		discountRatioBtn.setText(discountRatioText);
 		}
 		
 		if(typeSet.contains(OrderOfferType.MONEY)){
 			// 折扣金额
 			String discountMoneyText = String.format(btnformat,
 					getString(R.string.setoffer_btn_discountmoney_text),
 					offerCtrl.getDiscountMoney());
 			discountMoneyBtn.setText(discountMoneyText);
 		}	
 	}
     
	// 通知父活动处理子活动结果
	private void notifyParentHandleResult(String key, String value) {
		Intent result = new Intent();			
		result.putExtra(key, (String)value);
		
		if (parentHandler != null) {
			if(key == StringKey.INTENT_EXTRA_KEY_TOTAL){
				parentHandler.onChildActivityResult(IntegerConst.CHILD_RESULT_CODE_TOTAL, result);
			}			
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
		initConsumeTotalAndServiceAndSum();
		
	}
	
	private enum OrderOfferType {SERVICE, LOWEST, RATIO, MONEY}

}
