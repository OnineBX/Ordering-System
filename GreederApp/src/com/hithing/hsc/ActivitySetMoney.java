/**========================================
 * File:	ActivitySetMoney.java
 * Package:	com.hithing.hsc
 * Create:	by Leopard
 * Date:	2012-3-17:下午3:41:12
 **======================================*/
package com.hithing.hsc;

import java.math.BigDecimal;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.hithing.app.DialogActivity;
import com.hithing.hsc.entity.OrderItemValue.RemarkItem;
import com.hithing.hsc.global.StringKey;

/**
 * <p>ActivitySetMoney</p>
 * 	
 * <p>用于设置金额的活动</p>
 *
 * @author Leopard
 * 
 */
public final class ActivitySetMoney extends DialogActivity {	
	public static final String 	INTENT_EXTRA_KEY_MONEY = "money";			//Intent参数键-金额
	
	private BigDecimal			money;										//菜品金额
	private BigDecimal			stepValue;									//每次改变的金额
	
	private EditText 			moneyEdt;									//设置金额文本框
	
	public void onAdjust(View v){		
		switch (v.getId()) {
		case R.id.setmoney_btn_count_add:
			money = money.add(stepValue);			
			break;
		case R.id.setmoney_btn_count_reduce:
			if(money.intValue() > 0){
				money = money.subtract(stepValue);				
			}
			break;		
		}
		moneyEdt.setText(money.toString());
	}
	
	/* (non-Javadoc)
	 * @see com.hithing.app.DialogActivity#onCreate(android.os.Bundle)
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		final Intent intent = getIntent();
		//初始化标题
		title.setText(intent.getStringExtra(StringKey.INTENT_EXTRA_KEY_TITLE));
		moneyEdt 	= (EditText)findViewById(R.id.setmoney_edt_count);
		money 		= new BigDecimal(intent.getStringExtra(INTENT_EXTRA_KEY_MONEY));
		moneyEdt.setText(money.toString());
		
		stepValue 	= new BigDecimal("1.00");
	}

	/* (non-Javadoc)
	 * @see com.hithing.app.DialogActivity#getDataFromDialog()
	 */
	@Override
	protected Intent getDataFromDialog() {
		String result = "0.00";
		if(!TextUtils.isEmpty(moneyEdt.getText())){
			
			result = moneyEdt.getText().toString();
		}
		Intent intent = new Intent();
		intent.putExtra(INTENT_EXTRA_KEY_MONEY, result);
		return intent;
	}

	/* (non-Javadoc)
	 * @see com.hithing.app.DialogActivity#loadViewStub()
	 */
	@Override
	protected void loadViewStub() {
		content.setLayoutResource(R.layout.act_setmoney_content);
		content.inflate();
	}

}
