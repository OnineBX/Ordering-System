package com.hithing.hsc;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.TextView;

import com.google.protobuf.GeneratedMessage;
import com.hithing.app.ProtobufActivity;
import com.hithing.hsc.bll.android.AndGrdDaoManager;
import com.hithing.hsc.bll.control.CashCtrl;
import com.hithing.hsc.bll.control.OfferCtrl;
import com.hithing.hsc.bll.manage.CashingManager;
import com.hithing.hsc.bll.manage.DinnerTableManager;
import com.hithing.hsc.bll.manage.FoodManager;
import com.hithing.hsc.bll.manage.GreederDaoManager;
import com.hithing.hsc.bll.util.CashContext;
import com.hithing.hsc.bll.util.BillDataCache;
import com.hithing.hsc.bll.util.WorkContext;
import com.hithing.hsc.bll.util.MomsgFactory.MomsgType;
import com.hithing.hsc.entity.CashItemValue;
import com.hithing.hsc.global.StringKey;
import com.hithing.widget.CashItemsAdapter;
import com.hithing.widget.OfferItemsAdapter;

/**
 * <p>ActivityAntiPayBill</p>
 * 	
 * <p>功能描述</p>
 *
 * @author wxm
 * 
 */

public class ActivityAntiPayBill extends ProtobufActivity {

	private OfferItemsAdapter    offerAdapter;                                    //数据适配器
	private CashItemsAdapter 	 cashAdapter;
	private OfferCtrl            offerCtrl; 
	private CashCtrl			 cashCtrl;	
	private CashContext          cashContext = CashContext.INSTANCE;
	
	private ListView			 orderLsv;										  //付款单项目列表视图
	private ListView             antiLsv;                                         //反结项目列表视图 
	private ListView             payLsv;                                          //付款项目列表视图
	
	@SuppressWarnings("unused")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.act_cashing_antipay);
		
		//初始化标题栏
		final TextView title  = (TextView) findViewById(R.id.cmn_txv_title);  
		
		Intent intent = getIntent();
		
		GreederDaoManager grdManager = new AndGrdDaoManager(this);
		DinnerTableManager 	tableManager = new DinnerTableManager(grdManager.getDinnerTableDao());
//		CashingManager		cashManager	 = new CashingManager(grdManager.getPayMethodDao());
		FoodManager foodMgr = new FoodManager(grdManager.getFoodDao(), 
				grdManager.getFoodPriceDao());
		
		cashCtrl    = new CashCtrl(cashContext.getCashSpotId(), cashContext.getCurrentCasherId(), tableManager);
		
		//modified by leo temp
//		title.setText(String.format(getString(R.string.cashing_txv_changeday_paypoint)) + WorkContext.getInstance().getHall().name + String.format(getString(R.string.cashing_txv_changeday_class)) + cashContext.INSTANCE.getCurrentShiftName() +
//				String.format(getString(R.string.setcashing_txv_header_waitor_text)) + cashContext.INSTANCE.getCurrentCasherName());
		
		int tableId = intent.getExtras().getInt(StringKey.INTENT_EXTRA_KEY_TABLE);
		
		orderLsv = (ListView) findViewById(R.id.cashing_lsv_orderitems);
		orderLsv.addHeaderView(getLayoutInflater().inflate(R.layout.act_cashing_antipay_header, orderLsv, false));
		List<Integer> ids = new LinkedList<Integer>();
		ids.add(R.id.antipay_txv_table);
		ids.add(R.id.antipay_txv_payorder);
//		offerAdapter = new OfferItemsAdapter(this, BillDataCache.INSTANCE.getOrderItems(), R.layout.act_cashing_table_item, ids);
		cashAdapter = new CashItemsAdapter(this,cashCtrl.getCashData(), R.layout.act_cashing_table_item, ids);
		orderLsv.setAdapter(cashAdapter);
		orderLsv.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {

				
			}
		});
		
		
		
		antiLsv = (ListView) findViewById(R.id.cashing_lsv_descitems);
		antiLsv.addHeaderView(getLayoutInflater().inflate(R.layout.act_cashing_antipay_foodtype_header, antiLsv, false));
//		List<Integer> ads = new LinkedList<Integer>();
//		ads.add(R.id.antipay_tvx_foodtype);
//		ads.add(R.id.antipay_tvx_countunit);
//		ads.add(R.id.antipay_tvx_price); 
//		ads.add(R.id.antipay_tvx_prediscount);
//		ads.add(R.id.antipay_tvx_discountratio);
//		ads.add(R.id.antipay_tvx_discountmoney);
//		ads.add(R.id.antipay_tvx_postdiscount);
//		ads.add(R.id.antipay_tvx_waitor);
		
		List<Integer> ads = new LinkedList<Integer>();
		ads.add(R.id.setoffer_txv_food);
		ads.add(R.id.setoffer_txv_countunit);
		ads.add(R.id.setoffer_txv_price);
		ads.add(R.id.setoffer_txv_prediscount);
		ads.add(R.id.setoffer_txv_discountratio);
		ads.add(R.id.setoffer_txv_discountmoney);
		ads.add(R.id.setoffer_txv_postdiscount);
		
		offerAdapter = new OfferItemsAdapter(this, BillDataCache.INSTANCE.getOrderItems(), R.layout.act_setoffer_offer_item, ads);
		Log.e("log", "BillDataCache.INSTANCE.getOrderItems()------------>:" + BillDataCache.INSTANCE.getOrderItems().size());
//		cashAdapter = new CashItemsAdapter(this, cashCtrl.getCashData(), R.layout.act_cashing_table_item, ads);
		antiLsv.setAdapter(offerAdapter);
		
		antiLsv.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				
				Log.e("log", "log---------->;");
			}
		});
		
		
		
		payLsv = (ListView) findViewById(R.id.cashing_lsv_priceitems);
		payLsv.addHeaderView(getLayoutInflater().inflate(R.layout.act_cashing_antipay_bill_header, payLsv, false));
		List<Integer> cds = new LinkedList<Integer>();
		cds.add(R.id.cashing_txv_paymethod);
		cds.add(R.id.cashing_txv_paymoney);
		cashAdapter = new CashItemsAdapter(this, cashCtrl.getCashData(), R.layout.act_cashing_table_item, cds);
		payLsv.setAdapter(cashAdapter);
		
		payLsv.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				// TODO Auto-generated method stub
				
			}
		});
		
	}

	@Override
	public void onMomsgResult(MomsgType rspType, GeneratedMessage msg) {
		
		
	}

}
