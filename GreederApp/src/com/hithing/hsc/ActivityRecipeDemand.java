/**========================================
 * File:	ActivityRecipeDemand.java
 * Package:	com.hithing.hsc
 * Create:	by Leopard
 * Date:	2012-1-11:下午1:06:21
 **======================================*/
package com.hithing.hsc;

import java.io.Serializable;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import com.hithing.app.DialogActivity;
import com.hithing.hsc.bll.android.AndGrdDaoManager;
import com.hithing.hsc.bll.manage.DemandManager;
import com.hithing.hsc.bll.manage.FoodRecipeManager;
import com.hithing.hsc.bll.manage.GreederDaoManager;
import com.hithing.hsc.dataentity.DemandEntity;
import com.hithing.hsc.dataentity.FoodRecipeEntity;
import com.hithing.hsc.entity.AdapterItem.OptionalAdapterItem;
import com.hithing.hsc.entity.OrderItemValue.RemarkItem;
import com.hithing.hsc.global.StringKey;
import com.hithing.widget.AbstractOptionalAdapter.CheckType;
import com.hithing.widget.AbstractOptionalAdapter.Checker;
import com.hithing.widget.SimpleOptionalAdapter;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.TabHost;
import android.widget.TextView;

/**
 * <p>ActivityRecipeDemand</p>
 * 	
 * <p>功能描述</p>
 *
 * @author Leopard
 * 
 */
public final class ActivityRecipeDemand extends DialogActivity {
	
	private final int 					DEMAND_DEFAULT_ID = 0;								//默认的要求标识
	
	private GreederDaoManager			grdManager;											//Greeder框架Dao管理器
	
	private RemarkItem					food;												//菜品标识
	private HashMap<Integer, String> 	recipe;												//菜品做法
	private RemarkItem					demand;												//菜品传菜要求
	private String						remark;												//菜品备注
	
	private SimpleOptionalAdapter				recipeAdapter;										//菜品做法数据适配器
	private SimpleOptionalAdapter 			demandAdapter;										//菜品传菜要求数据适配器	
	private EditText 					remarkEdt;											//备注编辑框
	
	public void onCheckClick(View v){
		CheckBox recipeChb = (CheckBox)v;
		Checker checker = recipeAdapter.getChecker(CheckType.CHECK_TYPE_MULTICHECK);
		checker.check(Integer.parseInt(v.getTag(R.id.optional_item_tag_position).toString()), recipeChb.isChecked());
	}
	
	public void onRadioClick(View v){
		Checker checker = demandAdapter.getChecker(CheckType.CHECK_TYPE_SINGLECHECK);
		checker.check(Integer.parseInt(v.getTag(R.id.optional_item_tag_position).toString()), true);
	}
		
	/* (non-Javadoc)
	 * @see android.app.ActivityGroup#onCreate(android.os.Bundle)
	 */
	@SuppressWarnings("unchecked")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);		
		
		grdManager = new AndGrdDaoManager(this);
		
		//初始化参数
		Intent intent 			= getIntent();
		food 					= (RemarkItem)intent.getSerializableExtra(StringKey.INTENT_EXTRA_KEY_FOOD);
		recipe 					= (HashMap<Integer, String>)intent.getSerializableExtra(StringKey.INTENT_EXTEA_KEY_RECIPE);
		Serializable demandSrl 	= intent.getSerializableExtra(StringKey.INTENT_EXTRA_KEY_DEMAND);
		if(demandSrl != null){
			demand = (RemarkItem)demandSrl;
		}
		remark 					= intent.getStringExtra(StringKey.INTENT_EXTRA_KEY_REMARK);					
		
		TabHost host = (TabHost)findViewById(R.id.recipedemand_tabhost_main);
		host.setup();
		host.addTab(host.newTabSpec("recipe").setContent(R.id.recipedemand_tabchild_recipe).
				setIndicator(getString(R.string.recdam_tbi_recipe_text)));
		host.addTab(host.newTabSpec("demand").setContent(R.id.recipedemand_tabchild_demand).
				setIndicator(getString(R.string.recdam_tbi_demand_text)));
		
		//初始化标题
//		FoodManager foodMgr = new FoodManager(grdManager.getFoodDao(), grdManager.getFoodPriceDao());
		TextView title = (TextView)findViewById(R.id.dialog_title);
		title.setText(food.content);
		
		//初始化做法
		GridView recipeGvw 								= (GridView)findViewById(R.id.recipedemand_tab_gdv_recipe);
		FoodRecipeManager frMgr 						= new FoodRecipeManager(grdManager.getFoodRecipeDao(), grdManager.getFoodFrsortDao());
		List<FoodRecipeEntity> recipeList 				= frMgr.getFoodRecipe(food.id);		
		LinkedList<OptionalAdapterItem> recipeData 		= new LinkedList<OptionalAdapterItem>();
		LinkedList<OptionalAdapterItem> rCheckedData	= new LinkedList<OptionalAdapterItem>();				
		
		for(FoodRecipeEntity ent : recipeList){
			if(recipe.containsKey(ent.getId())){
				recipeData.add(new OptionalAdapterItem(ent.getId(), ent.getName()));
				recipeData.getLast().setChecked(true);
				rCheckedData.add(recipeData.getLast());
			}else{
				recipeData.add(new OptionalAdapterItem(ent.getId(), ent.getName()));	
			}				
		}
		recipeAdapter = new SimpleOptionalAdapter(this, recipeData, rCheckedData, R.layout.cmn_child_check__item);
		recipeGvw.setAdapter(recipeAdapter);
		
		//初始化要求
		GridView demandGvw 								= (GridView)findViewById(R.id.recipedemand_tab_gdv_demand);		
		DemandManager dmdMgr 							= new DemandManager(grdManager.getDemandDao());
		List<DemandEntity> demandList  					= dmdMgr.getFoodDemand();
		LinkedList<OptionalAdapterItem> demandData 		= new LinkedList<OptionalAdapterItem>();
		LinkedList<OptionalAdapterItem> dCheckedData 	= new LinkedList<OptionalAdapterItem>();
		
		//加入并设置默认要求
		boolean isDefDemand = true;
		if(demand != null){
			isDefDemand = false;
		}
		OptionalAdapterItem defDemandItem = new OptionalAdapterItem(DEMAND_DEFAULT_ID, getString(R.string.recdam_rdb_demand_text));
		defDemandItem.setChecked(isDefDemand);
		demandData.add(defDemandItem);
		if(isDefDemand) {
			dCheckedData.add(defDemandItem);
		}
		for(DemandEntity ent : demandList){
			if(demand != null){
				if(ent.getId() == demand.id){
					OptionalAdapterItem demandItem = new OptionalAdapterItem(ent.getId(), ent.getName());
					demandItem.setChecked(true);
					demandData.add(demandItem);					
					dCheckedData.add(demandItem);					
					continue;
				}
			}
			demandData.add(new OptionalAdapterItem(ent.getId(), ent.getName()));
		}
		demandAdapter = new SimpleOptionalAdapter(this, demandData, dCheckedData, R.layout.cmn_child_radio_item);
		demandGvw.setAdapter(demandAdapter);
		
		//初始化备注
		 remarkEdt = (EditText)findViewById(R.id.recipedemand_edt_remark);
		 remarkEdt.setText(remark);
	}

	@Override
	protected void loadViewStub() {
				
		content.setLayoutResource(R.layout.act_recipedemand_content);
		content.inflate();
		
	}

	@Override
	protected Intent getDataFromDialog() {		
			Intent intent = new Intent();
			
			//存入做法
			List<OptionalAdapterItem> recipeList = recipeAdapter.getCheckedData();
			recipe.clear();
			if(!recipeList.isEmpty()){
				for(OptionalAdapterItem item : recipeList){
					recipe.put(item.getId(), item.getName());
				}
			}
			intent.putExtra(StringKey.INTENT_EXTEA_KEY_RECIPE, recipe);
			
			//存入要求			
			List<OptionalAdapterItem> demandList = demandAdapter.getCheckedData();			
			
			OptionalAdapterItem  demandItem = demandList.get(0);
			if(demandItem.getId() != DEMAND_DEFAULT_ID){
				if(demand == null){
					demand = new RemarkItem();
				}
				demand.id 		= demandItem.getId();
				demand.content 	= demandItem.getName();
			}else {
				demand = null;
			}					
			intent.putExtra(StringKey.INTENT_EXTRA_KEY_DEMAND, demand);
			
			//存入备注			
			remark = remarkEdt.getText().toString();				
			intent.putExtra(StringKey.INTENT_EXTRA_KEY_REMARK, remark);		
			
			return intent;
	}
	
}
