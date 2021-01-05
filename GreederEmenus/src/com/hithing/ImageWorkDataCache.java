/**========================================
 * File:	WorkDataCache.java
 * Package:	com.hithing.hsc
 * Create:	by Leopard
 * Date:	2012-1-14:上午1:17:42
 **======================================*/
package com.hithing;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import android.content.Context;
import android.content.res.Resources;
import android.provider.ContactsContract.CommonDataKinds.Im;
import android.provider.MediaStore.Images;
import android.service.wallpaper.WallpaperService.Engine;
import android.util.Log;
import android.widget.ImageView;

import com.hithing.adapter.MenusAdapter;
import com.hithing.hsc.bll.android.AndGrdDaoManager;
import com.hithing.hsc.bll.android.InstructionSet;
import com.hithing.hsc.bll.manage.FoodManager;
import com.hithing.hsc.bll.manage.FoodSubSortManager;
import com.hithing.hsc.bll.manage.GreederDaoManager;
import com.hithing.hsc.bll.print.PrintCache;
import com.hithing.hsc.bll.print.PrintManager;
import com.hithing.hsc.bll.print.PrintTask;
import com.hithing.hsc.bll.print.PrintManager.PrintPort;
import com.hithing.hsc.dataentity.FoodSubSortEntity;
import com.hithing.hsc.dataentity.FoodEntity;
import com.hithing.hsc.dataentity.FoodPriceEntity;
import com.hithing.hsc.entity.MoMsg.CMsgImageLoadRsp;
import com.hithing.hsc.entity.SearchableAdapterItem.FoodAdapterItem;
import com.hithing.hsc.entity.SearchableAdapterItem.StyledAdapterItem;

/**
 * <p>WorkDataCache</p>
 * 	
 * <p>功能描述</p>
 *
 * @author Leopard
 * 
 */
public enum ImageWorkDataCache {
	INSTANCE;
	
	private Resources 			resources;
	private GreederDaoManager 	grdManager;
	
	private Map<Integer, StyledAdapterItem> fssMap 		= new ConcurrentHashMap<Integer, StyledAdapterItem>();
	private Map<Integer, FoodAdapterItem> foodMap 	= new ConcurrentHashMap<Integer, FoodAdapterItem>();	
	private Map<Integer, PrintPort> 		portMap		= new ConcurrentHashMap<Integer, PrintPort>();				//打印档口信息			
    
	/**
	 * <p>WorkDataCache</p>
	 *
	 * <p>构造函数</p>
	 */
	ImageWorkDataCache() {
		
	}		
	
	/**
	 * <p>initInstance</p>
	 *
	 * <p>根据运行上下文初始化实例</p>
	 * 
	 * @param context
	 */
	public void initInstance(Context context){
		resources = context.getResources();
		grdManager = new AndGrdDaoManager(context);
		InstructionSet.INSTANCE.initInstance(context);		//初始化打印指令集
	}
	
	/**
	 * <p>lodingFoodSubSortData</p>
	 *
	 * <p>载入菜品小类数据</p>
	 * 
	 * @param fmsIdSet 菜品大类集合
	 * @param sortColor 菜品小类类型颜色
	 * @return 菜品小类集合
	 */
	public synchronized Set<Integer> lodingFoodSubSortData(Set<Integer>	fmsIdSet){				
		fssMap.clear();
		
		Set<Integer> resultSet	= new HashSet<Integer>();
		
		int foodSubSortColor 	= resources.getColor(R.color.food_subsort_background);
				
		//初始化菜品小类
		FoodSubSortManager 					fssManager 	= new FoodSubSortManager(grdManager.getFoodSubSortDao());
		List<FoodSubSortEntity> 			fssList 	= fssManager.getFoodSubSorts(fmsIdSet);		
		StyledAdapterItem 	fssItem;		
		for(FoodSubSortEntity ent : fssList){
			fssItem = new StyledAdapterItem(ent.getFoodMainSort().getId(), foodSubSortColor, ent.getName(), null);
			fssMap.put(ent.getId(),fssItem);
			resultSet.add(ent.getId());
		}
		
		return resultSet;
	}
	
	/**
	 * <p>lodingFoodData</p>
	 *
	 * <p>载入菜品数据</p>
	 * 
	 * @param fssIdSet 菜品小类集合
	 */
	public synchronized void lodingFoodData(Set<Integer> fssIdSet){
		foodMap.clear();
		int foodColor = resources.getColor(R.color.food_background);
		FoodManager							foodManager = new FoodManager(grdManager.getFoodDao(),grdManager.getFoodPriceDao());
		List<FoodEntity> 					foodList 	= foodManager.getAllFoodsBySubSorts(fssIdSet);
		FoodAdapterItem foodItem;		
		FoodPriceEntity fpEnt;
		for(FoodEntity ent: foodList){
			fpEnt = foodManager.getDefaultPrice(ent.getId());
			String price = fpEnt.getPrice() + "￥/"+fpEnt.getUnit().getName();
			foodItem = new FoodAdapterItem(ent,0,price);
			foodMap.put(fpEnt.getId(), foodItem);	
		}		
		
	}
	
	/**
	 * <p>lodingPrintData</p>
	 *
	 * <p>载入打印数据</p>
	 * 
	 * @param hallId 分厅标识
	 */
	public synchronized void lodingPrintData(int hallId){
		portMap.clear();
		
		portMap.put(1, new PrintPort("厨房分单",true,"192.168.10.240:9527"));			//厨房
		portMap.put(2, new PrintPort("水吧分单",false,"192.168.8.243:9527"));		//吧台
		portMap.put(3, new PrintPort("收银录入总单",false,"192.168.8.243:9527"));		//收银
		portMap.put(4, new PrintPort("传菜总单",false,"192.168.10.240:9527"));		//传菜
				
		PrintCache.getInstance().initInstance(portMap.keySet());
		PrintManager.INSTANCE.initInstance(grdManager.getProducePrintDao(), portMap);
		PrintTask.getInstance().initInstance(portMap.values());
	}
	/**
	 * <p>getFoodSubSortData</p>
	 *
	 * <p>获得菜品小类数据,用于AdapterView显示</p>
	 * 
	 * @return 菜品小类数据
	 */
	public synchronized Map<Integer, StyledAdapterItem> getFoodSubSortData(){
		return fssMap;
	}
	
	/**
	 * <p>getFoodData</p>
	 *
	 * <p>获得菜品数据,用于AdapterView显示</p>
	 * 
	 * @return 菜品数据
	 */
	public synchronized Map<Integer, FoodAdapterItem> getFoodData(){
		return foodMap;
	}			
	
	public synchronized Map<Integer, PrintPort> getPortData(){
		return portMap;
	}		

}
