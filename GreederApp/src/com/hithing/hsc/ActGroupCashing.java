/**========================================
 * File:	ActGroupCashing.java
 * Package:	com.hithing.hsc
 * Create:	by Leopard
 * Date:	2012-3-14:上午9:01:47
 **======================================*/
package com.hithing.hsc;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.Filter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.protobuf.GeneratedMessage;
import com.hithing.app.ProtobufActivityGroup;
import com.hithing.app.ProtobufActivityGroup.IChildResultHandlable;
import com.hithing.hsc.bll.control.CashCtrl;
import com.hithing.hsc.bll.control.CashCtrl.ClearSpotState;
import com.hithing.hsc.bll.control.OfferCtrl;
import com.hithing.hsc.bll.control.OfferCtrl.OfferFactorsKey;
import com.hithing.hsc.bll.util.BillDataCache;
import com.hithing.hsc.bll.util.CashContext;
import com.hithing.hsc.bll.util.MomsgFactory.MomsgType;
import com.hithing.hsc.bll.util.MomsgHelper;
import com.hithing.hsc.bll.util.MomsgHelper.IntervalSyncType;
import com.hithing.hsc.bll.util.SelfComparableItem;
import com.hithing.hsc.bll.util.WorkContext;
import com.hithing.hsc.component.R;
import com.hithing.hsc.entity.CashItemValue;
import com.hithing.hsc.entity.MoMsg.CMsgComplexRsp;
import com.hithing.hsc.entity.OrderItemKey;
import com.hithing.hsc.entity.OrderItemValue;
import com.hithing.hsc.entity.MoMsg.CMsgBillRsp;
import com.hithing.hsc.entity.MoMsg.CMsgClearSpotRsp;
import com.hithing.hsc.entity.MoMsg.CMsgIntervalSyncRsp;
import com.hithing.hsc.entity.MoMsg.CMsgIntervalSyncRsp.CMsgBillTable;
import com.hithing.hsc.entity.PayMethodValue;
import com.hithing.hsc.global.IntegerConst;
import com.hithing.hsc.global.StringKey;
import com.hithing.sys.INeedInitializable;
import com.hithing.sys.InitializeCash;
import com.hithing.widget.CashItemsAdapter;

/**
 * <p>ActGroupCashing</p>
 * 	
 * <p>功能描述</p>
 *
 * @author Leopard
 * 
 */
public final class ActGroupCashing extends ProtobufActivityGroup implements IChildResultHandlable, TextWatcher, INeedInitializable{
	public final static String 	 TAG 			        = "cash";
	
	
	private final String		 SETOFFER_ACTIVITY_ID	= "setoffer";
	
	private final int            DIALOG_TYPE_CLEAR      = 1;    					//对话框类型-清机
	private final int            DIALOG_TYPE_SHFIT      = DIALOG_TYPE_CLEAR + 1;    //对话框类型—换班
	private final int			 DIALOG_TYPE_AUTH_ERROR	= DIALOG_TYPE_SHFIT + 1;	//对话框类型-授权码错误
	
	private final int			 REQUEST_CODE_ACTAU		= 5;						//输入授权码活动请求码
	private final int			 REQUEST_CODE_ACTPM		= REQUEST_CODE_ACTAU + 1;	//选择付款方式活动请求码	
	private final int			 REQUEST_CODE_ACTAP     = REQUEST_CODE_ACTPM + 1;   //选择反结方式活动请求码
	private final int            REQUEST_CODE_ACTOP     = REQUEST_CODE_ACTAP + 1;   //选择过脚方式活动请求码
	
	private int					 tableId				= 0;						//当前选中的餐台标识
	private boolean				 isBillInited			= false;					//账单是否初始化
	private String				 totalAmount;										//当前选中的账单总金额
	
	private MenuInflater		 menuInflater;		
	private CashCtrl			 cashCtrl;											//收银控制类实例
	private MomsgHelper			 msgHelper				= MomsgHelper.INSTANCE;		//消息帮助者实例
	
	private EditText			 searchEdt;										    //搜索编辑框
	private TextView 			 title;												//标题
	private CashContext          cashContext			= CashContext.INSTANCE;
	
	private ViewGroup			 container;											//定价活动容器
	private ListView			 cashLsv;											//收银项目列表视图
	private CashItemsAdapter 	 cashAdapter;										//收银项目适配器
	private View				 vClearSpot;										//清机对话框视图
	
	private Intent 				 soIntent;											//用于启动打折活动
	private Intent				 pmIntent;											//用于启动付款方式的活动
	private Intent				 jnIntent;											//用于启动入单的活动	
	private Intent               apIntent;                                          //用于启动反结的活动
    private Intent               opIntent;                                          //用于启动过脚的活动
    private Intent				 maIntent;											//用于启动我的帐目活动
	
    private EnumMap<OfferFactorsKey, String>	 	offerFactors;					//定价元素集合
    private Map<OrderItemKey, OrderItemValue>		offerItems;						//定价菜品集合
    
	public void onOperate(View v){
		String text = ((TextView)v).getText().toString();            
        
        //埋单的情况
        if(text == getString(R.string.cashing_txv_paybill_text)){
        	pmIntent.putExtra(StringKey.INTENT_EXTRA_KEY_TABLE, tableId);
        	pmIntent.putExtra(StringKey.INTENT_EXTRA_KEY_TOTAL, totalAmount);
        	startActivityForResult(pmIntent, REQUEST_CODE_ACTPM);
        }
        //入单的情况
        if(text == getString(R.string.cashing_txv_joinbill_text)){
        	jnIntent.putExtra(StringKey.INTENT_EXTRA_KEY_TABLE, tableId);
        	jnIntent.putExtra(StringKey.INTENT_EXTRA_KEY_CALLER, TAG);
        	startActivity(jnIntent);
        }        
	}
	
	/* (non-Javadoc)
	 * @see android.app.ActivityGroup#onCreate(android.os.Bundle)
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.act_cashing_main);	
		
		InitializeCash initCash = new InitializeCash(this);
		initCash.execute(getIntent().getIntExtra(StringKey.INTENT_EXTRA_KEY_AUTHCODE, IntegerConst.DEFAULT_INTEGER_VALUE));
		
		menuInflater = getMenuInflater();
		
		//初始化标题栏
		title  = (TextView) findViewById(R.id.cmn_txv_title);  
		
		soIntent 	= new Intent(this, ActivitySetOffer.class);
		jnIntent    = new Intent(this, ActivityWaiterOrder.class);
		apIntent    = new Intent(this, ActivityAntiPayBill.class);
		opIntent    = new Intent(this, ActivityOverPayMethod.class);
		maIntent	= new Intent(this, ActivityMyAccount.class);
		container 	= (ViewGroup) findViewById(R.id.cashing_container_setoffer);		
		startActivityInContainer(container, SETOFFER_ACTIVITY_ID, soIntent);
		
		final LayoutInflater inflater = getLayoutInflater();
		vClearSpot = inflater.inflate(R.layout.act_cashing_clearspot, null, false);		
        
		cashLsv = (ListView)findViewById(R.id.cashing_lsv_tables);
		cashLsv.addHeaderView(inflater.inflate(R.layout.act_cashing_header, cashLsv, false));														
		
		searchEdt = (EditText) findViewById(R.id.edt_header_search);
		searchEdt.addTextChangedListener(this);		
		
		pmIntent = new Intent(this, ActivityPayMethod.class);	
		
		offerFactors 	= BillDataCache.INSTANCE.getOfferedFactors();	
		offerItems 		= BillDataCache.INSTANCE.getOfferItems();
	}			

	/* (non-Javadoc)
	 * @see android.app.Activity#onNewIntent(android.content.Intent)
	 */
	@Override
	protected void onNewIntent(Intent intent) {		
		super.onNewIntent(intent);
		soIntent.putExtra(StringKey.INTENT_EXTRA_KEY_TABLE, tableId);
		startActivityInContainer(container, SETOFFER_ACTIVITY_ID, soIntent);
	}

	@Override
	protected void onResume() {		
		super.onResume();
		msgHelper.changeIntervalSyncType(IntervalSyncType.CASH,WorkContext.getInstance().getHall().id);
	}

	@Override
	public void onMomsgResult(MomsgType rspType, GeneratedMessage msg) {
		switch (rspType) {
		case INTERVALSYNC_RSP:
			Log.d("ActGroupCashing.onMomsgResult","INTERVALSYNC_RSP");	
			if(isBillInited){
				CMsgIntervalSyncRsp isRsp = (CMsgIntervalSyncRsp)msg;
				cashCtrl.clearCashItems();
				for (CMsgBillTable table : isRsp.getBillTableList()) {					
					cashCtrl.loadCashItem(table.getBillid(), table.getTableid());				
				}
				cashAdapter.refresh();
				verifyDefaultBill();
			}											
			break;		
		case CLEARSPOT_RSP:
			Log.d("ActGroupCashing.onMomsgResult", "CLEARSPOT_RSP");
			CMsgClearSpotRsp csRsp = (CMsgClearSpotRsp)msg;
			if(csRsp.hasStatus()){
				if(csRsp.getStatus() == MOMSG_RSP_STATUS_SUCCESS){
					if(csRsp.hasBusDate()){
						((TextView)vClearSpot.findViewById(R.id.cashing_clearspot_txv_nextdate)).
						setText(CashContext.getNextBizDay(csRsp.getBusDate()));
					}
				}
			}
			break;
		case PAYBILL_RSP:
			Log.d("ActGroupCashing.onMomsgResult", "PAYBILL_RSP");
			CMsgBillRsp pbRsp = (CMsgBillRsp)msg;
			Log.e("ActGroupCashing.onMomsgResult", "prRsp.status--->" + pbRsp.getStatus());
			break;
		case COMPLEX_RSP:
			CMsgComplexRsp cRsp = (CMsgComplexRsp)msg;
			Log.e("ActGroupCashing.onMomsgResult", "cRsp.status---->" + cRsp.getStatus());
			break;
		}		
	}

	/* (non-Javadoc)
	 * @see android.app.Activity#onCreateOptionsMenu(android.view.Menu)
	 */
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		menuInflater.inflate(R.menu.act_cashing_menu_main, menu);//cashing_menu_main_changeday清机
		return super.onCreateOptionsMenu(menu);
	}

	/* (non-Javadoc)
	 * @see android.app.Activity#onOptionsItemSelected(android.view.MenuItem)
	 */
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.cashing_menu_main_myaccounts:
			startActivity(maIntent);
			break;
		case R.id.cashing_menu_main_changeday:     		//点击清机的情况
			cashCtrl.clearForNextBizDay(ClearSpotState.VIEW_CURRENT_BIZDAY);
			showDialog(DIALOG_TYPE_CLEAR);
			break;			
		case R.id.cashing_menu_main_changeshift:     	//点击换班的情况
			showDialog(DIALOG_TYPE_SHFIT);
			break;
			
		case R.id.cashing_menu_main_antipay:     		//点击反结的情况
			apIntent.putExtra(StringKey.INTENT_EXTRA_KEY_TABLE, tableId);
			startActivityForResult(apIntent, REQUEST_CODE_ACTAP);
			break;
			
		case R.id.cashing_menu_main_paymethod:     		//点击换班的情况
			opIntent.putExtra(StringKey.INTENT_EXTRA_KEY_TABLE, tableId);
			startActivityForResult(opIntent, REQUEST_CODE_ACTOP);
			break;
		default:
			break;
		}
		return super.onOptionsItemSelected(item);
	}
	
	/* (non-Javadoc)
	 * @see android.app.Activity#onCreateDialog(int) 
	 */
	@Override
	protected Dialog onCreateDialog(int id) {
		Dialog dlg;
		switch (id) {
		case DIALOG_TYPE_AUTH_ERROR:
			dlg = new AlertDialog.Builder(this).setTitle(R.string.common_dlg_error_authcode_title).				
					setPositiveButton(R.string.common_btn_ok_text, new DialogInterface.OnClickListener() {
				
				@Override
				public void onClick(DialogInterface dialog, int which) {
					onBackPressed();
				}
			}).create(); 	
			return dlg;		
		case DIALOG_TYPE_CLEAR:
			AlertDialog cpDlg = new AlertDialog.Builder(this).
				  setTitle(String.format("%s:%s", getString(R.string.cashing_dlg_clearspot_title),cashContext.getCashSpotName())).
				  setView(vClearSpot).
				  setPositiveButton(R.string.common_btn_ok_text,
						 new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int id) {
								cashCtrl.clearForNextBizDay(ClearSpotState.CHANGE_CURRENT_BIZDAY);							
							}
						 }).
				  setNegativeButton(R.string.common_btn_cancel_text, 
						 new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int id) {
								dialog.cancel();
							}
						}).create();
			return cpDlg;
		case DIALOG_TYPE_SHFIT:
			AlertDialog shiftDlg = new AlertDialog.Builder(this).
					setTitle(R.string.cashing_dlg_changeshift_title).			
					setSingleChoiceItems(cashContext.getShiftValues(), 0 , 
							new DialogInterface.OnClickListener() {
				
								@Override
								public void onClick(DialogInterface dialog, int which) {									
									cashContext.setCurrentShift(which);
									refreshTitle();
									dialog.dismiss();									
								}
							}).
					setNegativeButton(R.string.common_btn_cancel_text, 
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog, int id) {
									dialog.cancel();
								}
							}).create();
			return shiftDlg;
		default:
			break;
		}
		return super.onCreateDialog(id);
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		switch (requestCode) {
		case REQUEST_CODE_ACTPM:
			if(resultCode == RESULT_OK){
				List<PayMethodValue> payment = (ArrayList<PayMethodValue>)data.getSerializableExtra(StringKey.INTENT_EXTRA_KEY_PAYMENT);
				if(!payment.isEmpty()){					
					if(offerFactors.isEmpty() && offerItems.isEmpty()){						//如果没有进行菜品定价，直接埋单
						cashCtrl.payBill(tableId, cashContext.getCurrentShiftId(), payment);
					}else {
						Toast.makeText(this, "此版本无法在账单定价后埋单！", Toast.LENGTH_SHORT).show();
						
//						OfferCtrl offerCtrl = new OfferCtrl(tableId, null);
//						offerCtrl.offerBill(cashContext.getCurrentCasherId());
//						cashCtrl.payBill(tableId, cashContext.getCurrentShiftId(), payment);
//						cashCtrl.payBillWithOffer(tableId, cashContext.getCurrentShiftId(), payment, offerFactors, offerItems);
					}
				}else{							//没有付款信息，埋单失败
					
				}
			}			
			break;
		case REQUEST_CODE_ACTAP:
			break;		
		}
		super.onActivityResult(requestCode, resultCode, data);
	}

	@Override
	public void onChildActivityResult(int childResultCode, Intent data) {
		switch (childResultCode) {
		case IntegerConst.CHILD_RESULT_CODE_TOTAL:
			totalAmount = data.getStringExtra(StringKey.INTENT_EXTRA_KEY_TOTAL);
			break;
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
		
		cashLsv.setVisibility(View.VISIBLE);
		Filter filter = cashAdapter.getFilter();
		filter.filter(s.toString());
		cashLsv.setAdapter(cashAdapter);
	}	
	
	@Override
	public void doPostInitialized(Object param) {
		if(param == null){
			showDialog(DIALOG_TYPE_AUTH_ERROR);
			return;
		}else {
			if(param instanceof CashCtrl){
				cashCtrl = (CashCtrl)param;
			}
		}		
		
		//初始化收银账单列表
		List<Integer> ids = new LinkedList<Integer>();
		ids.add(R.id.cashing_txv_tablecode);
		ids.add(R.id.cashing_txv_tablename);
		cashAdapter = new CashItemsAdapter(this, cashCtrl.getCashData(), R.layout.act_cashing_table_item, ids);
		cashLsv.setAdapter(cashAdapter);
		cashLsv.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position,
					long id) {				
				int selectedId = (int)id;
				if(tableId != selectedId){
					tableId = selectedId;
					soIntent.putExtra(StringKey.INTENT_EXTRA_KEY_TABLE, tableId);
					startActivityInContainer(container, SETOFFER_ACTIVITY_ID, soIntent);
				}				
				//如果搜索框有内容,则清空:处理搜索的情况,如果未搜索则不能置空
				if(!TextUtils.isEmpty(searchEdt.getText().toString())){
					searchEdt.setText(null);	
				}		
			}
		});
		
		cashAdapter.refresh();
		verifyDefaultBill();
		refreshTitle();
		isBillInited = true;			
	}	
	
	//刷新标题
	private void refreshTitle(){
		String titleFormat = getString(R.string.cashing_txv_title_text_format);
		
		title.setText(String.format(titleFormat, cashContext.getCashSpotName(),
				cashContext.getCurrentShiftName(),cashContext.getCurrentCasherName()));
	}
	
	//验证默认账单是否变化
	private void verifyDefaultBill(){				
		//选中第一个收银项目
		CashItemValue defValue = cashCtrl.getDefaultCashItem(tableId);		
		if(defValue != null){
			//当前餐台标识发生变化时，更新账单					
			tableId = defValue.getTableId();
			soIntent.putExtra(StringKey.INTENT_EXTRA_KEY_TABLE, tableId);
			startActivityInContainer(container, SETOFFER_ACTIVITY_ID, soIntent);				
		}
	}
}
