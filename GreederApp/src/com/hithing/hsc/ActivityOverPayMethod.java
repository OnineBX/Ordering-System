package com.hithing.hsc;

import java.util.LinkedList;
import java.util.List;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.google.protobuf.GeneratedMessage;
import com.hithing.app.ProtobufActivity;
import com.hithing.hsc.bll.android.AndGrdDaoManager;
import com.hithing.hsc.bll.control.CashCtrl;
import com.hithing.hsc.bll.control.PayCtrl;
import com.hithing.hsc.bll.manage.CashingManager;
import com.hithing.hsc.bll.manage.DinnerTableManager;
import com.hithing.hsc.bll.manage.FoodManager;
import com.hithing.hsc.bll.manage.GreederDaoManager;
import com.hithing.hsc.bll.util.CashContext;
import com.hithing.hsc.bll.util.WorkContext;
import com.hithing.hsc.bll.util.MomsgFactory.MomsgType;
import com.hithing.hsc.global.StringKey;
import com.hithing.widget.PayMethodItemsAdapter;

public class ActivityOverPayMethod extends ProtobufActivity {
	
    private String					   	receivableTotal;		//应收总金额
	
	private PayMethodItemsAdapter 	   	adapter;
	private int 						curPayMethodId;      //当前选择的付款方式标识
	
	private PayCtrl     payCtrl;    
	private CashCtrl			 cashCtrl;	
	private CashContext          cashContext = CashContext.INSTANCE;
	private RadioButton changeRb;                                //当前找赎按钮
	private RadioButton actualRb;                                //当前实收按钮
	private TextView    payMethodTxv;                             //显示找赎或实收数据
	private RadioGroup  modeRdg;                                  //找赎和实收的集合
	
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
		CashingManager		cashManager	 = new CashingManager(cashContext.getPayMethodData());
		FoodManager foodMgr = new FoodManager(grdManager.getFoodDao(), 
				grdManager.getFoodPriceDao());
		
		//modified by leo temp
//		title.setText(String.format(getString(R.string.cashing_txv_changeday_paypoint)) + WorkContext.getInstance().getHall().name + String.format(getString(R.string.cashing_txv_changeday_class)) + cashContext.INSTANCE.getCurrentShiftName() +
//				String.format(getString(R.string.setcashing_txv_header_waitor_text)) + cashContext.INSTANCE.getCurrentCasherName());
		
		//加载标题
		receivableTotal = intent.getStringExtra(StringKey.INTENT_EXTRA_KEY_TOTAL);
		//加载付款方式列表				
	    payCtrl = new PayCtrl("0.00", cashManager);						
		List<Integer> ids = new LinkedList<Integer>();		
		ids.add(R.id.paymethod_chb_name);
		ids.add(R.id.paymethod_txv_code);
		ids.add(R.id.paymethod_edt_money);
		adapter  = new PayMethodItemsAdapter(this, payCtrl.getPayMethodData(), payCtrl.getPayData(), R.layout.act_paymethod_method_item, ids);
		ListView methodLsv = (ListView)findViewById(R.id.cashing_lsv_priceitems);
		methodLsv.addHeaderView(getLayoutInflater().inflate(R.layout.act_cashing_antipay_bill_header, 
				methodLsv, false));
		methodLsv.setAdapter(adapter);
		initView();
	}

	protected void initView() {
		 
		modeRdg       = (RadioGroup) findViewById(R.id.paymethod_rdg_mode);
        changeRb     = (RadioButton) findViewById(R.id.paymethod_rdb_change);
        actualRb     = (RadioButton) findViewById(R.id.paymethod_rdb_actual);
        payMethodTxv  = (TextView) findViewById(R.id.tv_paymethod);
        
	}
	
	
	
	@Override
	public void onMomsgResult(MomsgType rspType, GeneratedMessage msg) {
		// TODO Auto-generated method stub

	}

}
