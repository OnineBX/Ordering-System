/**========================================
 * File:	ActivityPayMethod.java
 * Package:	com.hithing.hsc
 * Create:	by Leopard
 * Date:	2012-3-27:上午9:37:06
 **======================================*/
package com.hithing.hsc;

import java.util.LinkedList;
import java.util.List;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.hithing.app.DialogActivity;
import com.hithing.hsc.bll.android.AndGrdDaoManager;
import com.hithing.hsc.bll.control.PayCtrl;
import com.hithing.hsc.bll.manage.CashingManager;
import com.hithing.hsc.bll.manage.DinnerTableManager;
import com.hithing.hsc.bll.manage.GreederDaoManager;
import com.hithing.hsc.bll.util.CashContext;
import com.hithing.hsc.dataentity.DinnerTableEntity;
import com.hithing.hsc.entity.PayMethodValue;
import com.hithing.hsc.global.StringKey;
import com.hithing.widget.AbstractOptionalAdapter.CheckType;
import com.hithing.widget.AbstractOptionalAdapter.Checker;
import com.hithing.widget.PayMethodItemsAdapter;

/**
 * <p>ActivityPayMethod</p>
 * 	
 * <p>用于设置付款方式的活动</p>
 *
 * @author Leopard
 * 
 */
public final class ActivityPayMethod extends DialogActivity implements View.OnFocusChangeListener,TextWatcher{
	private final int 		INVALID_ID  = 0;					//非法的标识
	private final String 	DEFAULT_PAY_MONEY = "0.00";			//默认的付款金额
	
	private String					   	receivableTotal;		//应收总金额
	
	private PayMethodItemsAdapter 	   	adapter;
	private int 						curPayMethodId;      //当前选择的付款方式标识
	
	private PayCtrl     payCtrl;            
	
	private RadioButton changeRb;                                //当前找赎按钮
	private RadioButton actualRb;                                //当前实收按钮
	private TextView    payMethodTxv;                             //显示找赎或实收数据
	private RadioGroup  modeRdg;                                  //找赎和实收的集合
	
	@SuppressWarnings("rawtypes")
	public void onSelectClick(View v){
		CheckBox selectedChb = (CheckBox)v;
		curPayMethodId = Integer.parseInt(v.getTag().toString());
		
		boolean checked = selectedChb.isChecked();
		Checker checker = adapter.getChecker(CheckType.CHECK_TYPE_MULTICHECK);
		checker.check(payCtrl.getPayMethodLocation(curPayMethodId), checked, false);
		if(checked){
			payCtrl.addPay(curPayMethodId, DEFAULT_PAY_MONEY);
		}else{
			payCtrl.removePay(curPayMethodId);
		}
		adapter.notifyDataSetChanged();
	}
	
	/* (non-Javadoc)
	 * @see com.hithing.app.DialogActivity#onCreate(android.os.Bundle)
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {		
		super.onCreate(savedInstanceState);				
		
		GreederDaoManager grdManager = new AndGrdDaoManager(this);
		Intent intent = getIntent();						
		
		//加载标题
		int tableId = intent.getIntExtra(StringKey.INTENT_EXTRA_KEY_TABLE, INVALID_ID);
		receivableTotal = intent.getStringExtra(StringKey.INTENT_EXTRA_KEY_TOTAL);
		String titleStr = "INVALID_TABLE";
		if(tableId != INVALID_ID){
			DinnerTableManager tableMgr = new DinnerTableManager(grdManager.getDinnerTableDao());
			DinnerTableEntity tableEnt = tableMgr.getTableById(tableId);
			titleStr = String.format(getString(R.string.pmethod_dlg_title_text), //应收金额标题栏
					tableEnt.getName(),tableEnt.getCode(),receivableTotal);
		}
		title.setText(titleStr);
		
		//加载付款方式列表
		CashingManager cashingManager = new CashingManager(CashContext.INSTANCE.getPayMethodData());
		
		payCtrl = new PayCtrl(receivableTotal, cashingManager);						
		
		List<Integer> ids = new LinkedList<Integer>();
		ids.add(R.id.paymethod_chb_name);
		ids.add(R.id.paymethod_txv_code);
		ids.add(R.id.paymethod_edt_money);
		adapter = new PayMethodItemsAdapter(this, payCtrl.getPayMethodData(), payCtrl.getPayData(), R.layout.act_paymethod_method_item, ids);
		ListView methodLsv = (ListView)findViewById(R.id.paymethod_lsv_method);
		methodLsv.addHeaderView(getLayoutInflater().inflate(R.layout.act_paymethod_method_header, 
				methodLsv, false));
		methodLsv.setAdapter(adapter);
		
//		methodLsv.setVisibility(View.GONE);
//		adapter.notifyDataSetChanged();
//		methodLsv.setVisibility(View.VISIBLE);
		
		initView();
	}

	 protected void initView() {
		 
		modeRdg       = (RadioGroup) findViewById(R.id.paymethod_rdg_mode);
        changeRb     = (RadioButton) findViewById(R.id.paymethod_rdb_change);
        actualRb     = (RadioButton) findViewById(R.id.paymethod_rdb_actual);
        payMethodTxv  = (TextView) findViewById(R.id.tv_paymethod);
        
        modeRdg.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
			
			@Override
			public void onCheckedChanged(RadioGroup group, int checkedId) {
				if(checkedId == changeRb.getId()){
					payMethodTxv.setText(payCtrl.getChange());
				}else if (checkedId == actualRb.getId()){
					payMethodTxv.setText(payCtrl.getActual());
				}
			}
		});
		
	}

	/* (non-Javadoc)
	 * @see com.hithing.app.DialogActivity#getDataFromDialog()
	 */
	@Override
	protected Intent getDataFromDialog() {			
		Intent result = new Intent();		
		result.putExtra(StringKey.INTENT_EXTRA_KEY_PAYMENT, (LinkedList<PayMethodValue>)payCtrl.getPayData());
		CheckBox printChb = (CheckBox)findViewById(R.id.paymethod_chb_print);
		result.putExtra(StringKey.INTENT_EXTRA_KEY_PRINT, printChb.isChecked());
		return result;
	}
		
	

		@Override
		public void onFocusChange(View v, boolean hasFocus) {
			EditText moneyEdt = (EditText)v;
	        if(hasFocus){	 
	        	curPayMethodId = Integer.parseInt(v.getTag().toString());
	        	moneyEdt.addTextChangedListener(this);		        	
	        }else{
	        	moneyEdt.removeTextChangedListener(this);	        	
	        }
			
		}	

	/* (non-Javadoc)
	 * @see com.hithing.app.DialogActivity#loadViewStub()
	 */
	@Override
	protected void loadViewStub() {
		Float value = new Float(getResources().getDimension(R.dimen.default_dialog_width_small));
		setDialogWidth(value.intValue());
		content.setLayoutResource(R.layout.act_paymethod_content);
		content.inflate();		
}

	@Override
	public void beforeTextChanged(CharSequence s, int start, int count,
			int after) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onTextChanged(CharSequence s, int start, int before, int count) {
		if(curPayMethodId != INVALID_ID){
			String value = "0.00";			
			if(!TextUtils.isEmpty(s)){
				value = s.toString().trim();
			}
			payCtrl.modifyPay(curPayMethodId, value);
			//更新即时找赎或实收金额
			if(modeRdg.getCheckedRadioButtonId() == R.id.paymethod_rdb_change){
				payMethodTxv.setText(payCtrl.getChange());
			}else {
				payMethodTxv.setText(payCtrl.getActual());
			}
			
			
		}
				
	}

	@Override
	public void afterTextChanged(Editable s) {
			
	}

}
