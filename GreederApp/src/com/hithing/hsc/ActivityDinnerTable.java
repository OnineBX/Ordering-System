/**========================================
 * File:	ActivityDinnerTable.java
 * Package:	com.hithing.hsc.base
 * Create:	by Leopard
 * Date:	2011-12-19:下午01:46:43
 **======================================*/
package com.hithing.hsc;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Set;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputType;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Filter;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.protobuf.GeneratedMessage;
import com.hithing.app.ProtobufActivity;
import com.hithing.hsc.bll.android.AndGrdDaoManager;
import com.hithing.hsc.bll.control.DinnerTableCtrl;
import com.hithing.hsc.bll.control.DinnerTableCtrl.DinnerTableState;
import com.hithing.hsc.bll.manage.DinnerTableManager;
import com.hithing.hsc.bll.manage.DinnerTableTypeManager;
import com.hithing.hsc.bll.manage.GreederDaoManager;
import com.hithing.hsc.bll.util.MomsgFactory.MomsgType;
import com.hithing.hsc.bll.util.MomsgHelper.IntervalSyncType;
import com.hithing.hsc.dataentity.DinnerTableEntity;
import com.hithing.hsc.dataentity.DinnerTableTypeEntity;
import com.hithing.hsc.entity.MoMsg.CMsgFoundingRsp;
import com.hithing.hsc.entity.MoMsg.CMsgIntervalSyncRsp;
import com.hithing.hsc.entity.MoMsg.CMsgIntervalSyncRsp.CMsgTableStatus;
import com.hithing.hsc.entity.MoMsg.CMsgOrderCancelRsp;
import com.hithing.hsc.entity.SearchableAdapterItem.StyledAdapterItem;
import com.hithing.hsc.global.StringKey;
import com.hithing.widget.AbsMultiStyleAdapter;
import com.hithing.widget.AbsMultiStyleAdapter.FilterMode;
import com.hithing.widget.MultiColorStyleAdapter;

/**
 * <p>ActivityDinnerTable</p>
 * 	
 * <p>处理餐台操作的活动,包括开台、转台、并台、取消、预约</p>
 *
 * @author Leopard
 * 
 */
public final class ActivityDinnerTable extends ProtobufActivity implements View.OnClickListener, TextWatcher{			
	public final static String      TAG        				= "table";       
	
	private final int				DIALOG_TABLE_FOUNDING	= 1;						//对话框类型:开台
	private final int				DEFAULT_CUSTOMER_COUNT 	= 0;						//默认开台人数
	
	private boolean					isStatusInit			= false;					//餐台状态是否初始化
	private int						tableSpareColor;									//餐桌空闲时的颜色
	private int						tableUsingColor;									//餐桌使用时的颜色
	
	private DinnerTableCtrl			currentTable			= new DinnerTableCtrl();	//当前选中的餐台标识
	private String					currentTableName;									//当前餐台名称
	
	private LayoutInflater			layoutInflater;
	private MenuInflater			menuInflater;
	private Resources				resources;
	
	private GreederDaoManager 		grdManager;											//Greeder框架数据访问管理器	
	
	private GridView 				tableGdv;											//餐台容器	
	private AbsMultiStyleAdapter	tableAdapter;										//餐台数据适配器
	private EditText				customsEdt;											//输入开台人数文本框
	private EditText				searchEdt;											//搜索文本框
	private ProgressDialog			lodingDlg;											//初始化餐台状态进度对话框
	private String					defTableSortId;										//默认的餐台类别标识,用于空查询
		
	@Override
	public void onClick(View v) {
		tableAdapter.setFilterMode(FilterMode.Type);
		
		String 	idStr 		= v.getTag().toString();		
		Filter 	filter 	= tableAdapter.getFilter();
		filter.filter(idStr);
		tableGdv.setAdapter(tableAdapter);	
		
	}		

	public void onFilter(View v) {
		tableAdapter.setFilterMode(FilterMode.Style);
		String text = ((Button)v).getText().toString();
		
		Filter filter = tableAdapter.getFilter();		
		
		//过滤出正在使用的餐台
		if(text == getString(R.string.table_btn_using_text)){
			filter.filter(String.valueOf(resources.getColor(R.color.table_using_background)));
		}
		
		//过滤出空闲的餐台
		if(text == getString(R.string.table_btn_spare_text)){
			filter.filter(String.valueOf(resources.getColor(R.color.table_spare_background)));
		}
		
		tableGdv.setAdapter(tableAdapter);				
	}					
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);
		setContentView(R.layout.act_dinnertable_main);						
		
		lodingDlg = ProgressDialog.show(this, null, getString(R.string.table_dlg_loding_message));
		
		layoutInflater 	= getLayoutInflater();
		menuInflater	= getMenuInflater();
		resources 		= getResources();
		grdManager 		= new AndGrdDaoManager(this);
		
		tableSpareColor = resources.getColor(R.color.table_spare_background);
		tableUsingColor = resources.getColor(R.color.table_using_background);
		
		tableGdv = (GridView)findViewById(R.id.dinnertable_gdv_table);
		tableGdv.setOnItemClickListener(new OnItemClickListener() {
			int vId;
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position,
					long id) {
				
				currentTableName 	= (String) ((TextView)view).getText();
				currentTable.setTableId((int)id);				
				vId = view.getId();				
				
				if(vId == tableSpareColor){									//点击空闲状态餐桌的情况
					showDialog(DIALOG_TABLE_FOUNDING);
				}
				
				if(vId == tableUsingColor){									//点击使用状态餐桌的情况
					Intent intent = new Intent(ActivityDinnerTable.this, ActivityWaiterOrder.class);				
					intent.putExtra(StringKey.INTENT_EXTRA_KEY_TABLE,(int)id);	
					intent.putExtra(StringKey.INTENT_EXTRA_KEY_CALLER, TAG);
					startActivity(intent);
				}			
			}
		});
		
		registerForContextMenu(tableGdv);									//为tableGdv注册上下文菜单-长按时弹出
		
		searchEdt = (EditText)findViewById(R.id.dinnertable_edt_search);
		searchEdt.addTextChangedListener(this);
		
		customsEdt = new EditText(this);			
		customsEdt.setInputType(InputType.TYPE_CLASS_NUMBER);
		customsEdt.setHint(R.string.table_edt_customers_hint);		
		
		initViews();														//初始化界面		
	}	

	/* (non-Javadoc)
	 * @see com.hithing.app.ProtobufActivity#onResume()
	 */
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		msgHelper.changeIntervalSyncType(IntervalSyncType.TABLE,wContext.getHall().id);
		
	}

	@Override
	public void onMomsgResult(MomsgType rspType, GeneratedMessage msg) {
				
		switch (rspType) {		
		case INTERVALSYNC_RSP:
			Log.d("Table.onMomsgResult","INTERVALSYNC_RSP");			
			CMsgIntervalSyncRsp isRsp = (CMsgIntervalSyncRsp)msg;			
			if(isRsp.getStatus() == MOMSG_RSP_STATUS_SUCCESS){
				List<CMsgTableStatus> tsList = isRsp.getTableStatusList();
				for(CMsgTableStatus ts : tsList){					
					switch (DinnerTableState.valueOf(ts.getStatus())) {					
					case using:
						tableAdapter.setItem(ts.getId(),tableUsingColor);
						break;
					case spare:
						tableAdapter.setItem(ts.getId(), tableSpareColor);
						break;
					}
				}
				tableAdapter.notifyDataSetChanged();											
			}
//			//初始化餐台状态的情况
			if(isStatusInit == false){
				lodingDlg.dismiss();
				isStatusInit = true;
			}
			break;
		case FOUNDING_RSP:			
			CMsgFoundingRsp fRsp = (CMsgFoundingRsp)msg;
			int tableId = currentTable.getTableId(); 
			if(fRsp.getStatus() == MOMSG_RSP_STATUS_SUCCESS){
				tableAdapter.setItem(tableId, tableUsingColor);				
				Intent intent = new Intent(this, ActivityWaiterOrder.class);				
				intent.putExtra(StringKey.INTENT_EXTRA_KEY_TABLE,tableId);
				intent.putExtra(StringKey.INTENT_EXTRA_KEY_CALLER, TAG);
				startActivity(intent);
			}
			tableAdapter.notifyDataSetChanged();
			break;
		case UNFOUNDING_RSP:
			Log.d("Table.onMomsgResult", "UNFOUNDING_RSP");
			CMsgOrderCancelRsp ocRsp = (CMsgOrderCancelRsp)msg;
			if(ocRsp.hasStatus()){
				Log.e("Table.onMomsgResult", "rsp.status----->" + ocRsp.getStatus());
			}
			break;
		default:
			break;
		}		
	}	
	
	/* (non-Javadoc)
	 * @see android.app.Activity#onCreateDialog(int)
	 */
	@Override
	protected Dialog onCreateDialog(int id) {
		Dialog dlg;
		switch (id) {
		case DIALOG_TABLE_FOUNDING:
			dlg = new AlertDialog.Builder(this).setTitle(R.string.table_dlg_apply_title).
			setView(customsEdt).setPositiveButton(R.string.common_btn_ok_text, new OnClickListener() {
				
				@Override
				public void onClick(DialogInterface dialog, int which) {
					String customsStr = customsEdt.getText().toString();
					if(!TextUtils.isEmpty(customsStr)){
						currentTable.founding(Integer.valueOf(customsStr));
					}else{
						currentTable.founding(DEFAULT_CUSTOMER_COUNT);				
					}
					
				}
			}).setNegativeButton(R.string.common_btn_cancel_text, null).create();
			return dlg;
		default:
			break;
		}
		
		return super.onCreateDialog(id);
	}
	
	/* (non-Javadoc)
	 * @see android.app.Activity#onPrepareDialog(int, android.app.Dialog)
	 */
	@Override
	protected void onPrepareDialog(int id, Dialog dialog) {
		
		switch (id) {
		case DIALOG_TABLE_FOUNDING:
			AlertDialog applyDlg = (AlertDialog)dialog;
			customsEdt.setText(null);
			applyDlg.setTitle(String.format("%s - %s",getString(R.string.table_dlg_apply_title),currentTableName));					
			break;			
		default:
			break;
		}
		
		super.onPrepareDialog(id, dialog);
	}		

	/* (non-Javadoc)
	 * @see android.app.Activity#onCreateContextMenu(android.view.ContextMenu, android.view.View, android.view.ContextMenu.ContextMenuInfo)
	 */
	@Override
	public void onCreateContextMenu(ContextMenu menu, View v,
			ContextMenuInfo menuInfo) {
		
		menu.setHeaderTitle(R.string.common_cmn_operate_title);
		AdapterContextMenuInfo info = (AdapterContextMenuInfo)menuInfo;
		currentTable.setTableId((int)info.id);		
		StyledAdapterItem item = (StyledAdapterItem)tableAdapter.getItem(info.position);
		
		if(item.getStyle() == tableUsingColor){									//餐台开台的情况				
			menuInflater.inflate(R.menu.act_dinnertable_menu_using, menu);
		}			
			
		super.onCreateContextMenu(menu, v, menuInfo);
	}
		
	/* (non-Javadoc)
	 * @see android.app.Activity#onContextItemSelected(android.view.MenuItem)
	 */
	@Override
	public boolean onContextItemSelected(MenuItem item) {				
		
		switch (item.getItemId()) {
		case R.id.dinnertable_menu_using_cancel:		//取消开台			
			currentTable.unFounding();			
			break;

		default:
			break;
		}
		return super.onContextItemSelected(item);
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
		
		String filterStr = s.toString();		
				
		Filter 	filter 	= tableAdapter.getFilter();
		if(TextUtils.isEmpty(filterStr)){
			tableAdapter.setFilterMode(FilterMode.Type);
			filter.filter(defTableSortId);
		}else {
			tableAdapter.setFilterMode(FilterMode.Index);
			filter.filter(s.toString());
		}		
		tableGdv.setAdapter(tableAdapter);
		
	}	

	/* (non-Javadoc)
	 * @see android.app.Activity#onDestroy()
	 */
	@Override
	protected void onDestroy() {		
		super.onDestroy();
		grdManager.release();
	}			
	
	private void initViews(){
					
		//初始化标题栏
		TextView title = (TextView)findViewById(R.id.cmn_txv_title);		
		title.setText(wContext.getHall().name);
		
		//初始化餐台类型标签栏
		LinearLayout 				tableTypeContainer 	= (LinearLayout)findViewById(R.id.dinnertable_container_table_type);
		DinnerTableTypeManager 		dttMgr				= new DinnerTableTypeManager(grdManager.getDinnerTableTypeDao());
		List<DinnerTableTypeEntity> dttList 			= dttMgr.getAllTypesByHallId(wContext.getHall().id);
		Set<Integer> 				dttIdSet			= new HashSet<Integer>();			
					
		int index = 0;
		for(DinnerTableTypeEntity ent : dttList){
			Button btn = (Button)layoutInflater.inflate(R.layout.tabbar_item, tableTypeContainer, false);
			btn.setText(ent.getName());
			btn.setTag(ent.getId());
			btn.setOnClickListener(this);   
			tableTypeContainer.addView(btn,index);
			
			dttIdSet.add(ent.getId());				
			index++;
		}
		
		//初始化餐台数据
		DinnerTableManager 		tableMgr 	= new DinnerTableManager(grdManager.getDinnerTableDao());
		List<DinnerTableEntity> tableList 	= tableMgr.getAllTablesByTypes(dttIdSet);
		
		HashMap<Integer, StyledAdapterItem> dtMap = new LinkedHashMap<Integer, StyledAdapterItem>();
		StyledAdapterItem 	item;
		 
		for(DinnerTableEntity ent : tableList){
			item = new StyledAdapterItem(ent.getType().getId(), tableSpareColor, ent.getName(), ent.getIndex());
			dtMap.put(ent.getId(),item);
		}
		
		tableAdapter = new MultiColorStyleAdapter(this, dtMap, R.layout.act_dinnertable_table_item);	
		tableGdv.setAdapter(tableAdapter);						
		
		// 默认选中第一个餐台类型		
		if(!dttList.isEmpty()){
			defTableSortId = tableTypeContainer.getChildAt(0).getTag().toString();	//初始化默认餐台类型标识
			if(!tableList.isEmpty()){
				onClick(tableTypeContainer.getChildAt(0));
			}			
		}			
		
	}	
	
}
