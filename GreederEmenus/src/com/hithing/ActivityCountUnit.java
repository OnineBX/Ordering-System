/**========================================
 * File:	ActivityCountUnit.java
 * Package:	com.hithing.hsc
 * Create:	by Leopard
 * Date:	2012-2-2:下午2:04:40
 **======================================*/
package com.hithing;

import java.util.List;

import com.hithing.app.DialogActivity;
import com.hithing.hsc.bll.android.AndGrdDaoManager;
import com.hithing.hsc.bll.manage.FoodManager;
import com.hithing.hsc.bll.manage.GreederDaoManager;
import com.hithing.hsc.dataentity.FoodPriceEntity;
import com.hithing.hsc.entity.OrderItemValue.RemarkItem;
import com.hithing.util.StringKey;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

/**
 * <p>ActivityCountUnit</p>
 * 	
 * <p>为账单菜品设置数量和单位</p>
 *
 * @author Leopard
 * 
 */
public final class ActivityCountUnit extends DialogActivity {			
	
	private final int 			DEFAULT_COUNT = 1;						//默认数量
	private final int 			DEFAULT_UNIT  = 0;						//默认单位
	
	private int					count;									//菜品数量	
	
	private GreederDaoManager	grdManager;								//Greeder框架Dao管理器
	
	private EditText			countEdt;								//设置菜品数量编辑框
	private RadioGroup 			unitContainer;							//菜品单位容器	
	
	public void onOperate(View v){
		count = Integer.parseInt(countEdt.getText().toString());
		switch (v.getId()) {
		case R.id.countunit_btn_count_add:						
			countEdt.setText(String.valueOf(++count));
			break;
		case R.id.countunit_btn_count_reduce:
			if(count > 0){
				countEdt.setText(String.valueOf(--count));
			}
			break;		
		}
	}
	
	/* (non-Javadoc)
	 * @see android.app.Activity#onCreate(android.os.Bundle)
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);								
		
		grdManager = new AndGrdDaoManager(this);
		
//		setContentView(R.layout.cmn_dialog_main);
		
		
		final Intent intent = getIntent();
		
		//初始化数量编辑框
		count 	 = intent.getIntExtra(StringKey.INTENT_EXTRA_KEY_COUNT, DEFAULT_COUNT);
		countEdt = (EditText)findViewById(R.id.countunit_edt_count);
		countEdt.setText(String.valueOf(count));
		
		
		RemarkItem 	food 			 = (RemarkItem)intent.getSerializableExtra(StringKey.INTENT_EXTRA_KEY_FOOD);
		
		//初始化标题
		title.setText(food.content);
		
		//初始化unit容器
		int 		priceId 		 = intent.getIntExtra(StringKey.INTENT_EXTRA_KEY_PRICE, DEFAULT_UNIT);
		unitContainer	 			 = (RadioGroup)findViewById(R.id.countunit_container_unit);
		final FoodManager foodMgr 	 = new FoodManager(grdManager.getFoodDao(), grdManager.getFoodPriceDao()); 
		List<FoodPriceEntity> prices = foodMgr.getFoodPrices(food.id);
		String content;
		RadioButton child;
		final LayoutInflater inflater = LayoutInflater.from(this);
		for(FoodPriceEntity ent : prices){			
			content = String.format("%s/%s", ent.getPrice(),ent.getUnit().getName());
			child = (RadioButton)inflater.inflate(R.layout.cmn_radio_item, unitContainer, false);
			child.setText(content);
			child.setId(ent.getId());									//保存priceId
			unitContainer.addView(child);
		}	
		unitContainer.check(priceId);					//初始化默认计价单位		
				
	}	
	
	@Override
	protected void loadViewStub() {
				
		content.setLayoutResource(R.layout.act_countunit_content);
		content.inflate();
		
	}

	@Override
	protected Intent getDataFromDialog() {
	
		if(TextUtils.isEmpty(countEdt.getText())){
			count = 0;
		}
		Intent intent = new Intent();
		intent.putExtra(StringKey.INTENT_EXTRA_KEY_COUNT, count);
		intent.putExtra(StringKey.INTENT_EXTRA_KEY_PRICE, unitContainer.getCheckedRadioButtonId());
		return intent;	
	}
}
