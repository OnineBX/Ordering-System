/**========================================
 * File:	ResourceVersionHelper.java
 * Package:	com.hithing.hsc.bll.android
 * Create:	by Leopard
 * Date:	2011-12-26:下午01:22:36
 **======================================*/
package com.hithing.hsc.bll.android;

import android.R.integer;
import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

/**
 * <p>ResourceVersionHelper</p>
 * 	
 * <p>资源版本帮助者,用于维护资源版本号</p>
 *
 * @author Leopard
 * 
 */
public final class ResourceVersionHelper {	
	
	private SharedPreferences 	preferences;
	private Editor 				editor;
	
	private final String 		VERSION_PREFERENCES 			= "com.hithing.hsc.version";			//保存版本信息的偏好键名
	private final String		VERSION_HALLSORT_NAME			= "hallSort_version";					//分厅大类版本名称
	private final String		VERSION_HALL_NAME				= "hall_version";						//分厅版本名称
	private final String		VERSION_DINNERTABLE_TYPE_NAME	= "dinTabType_version";					//餐台类别版本名称
	private final String 		VERSION_DINNERTABLE_NAME		= "dinTable_version";					//餐台版本名称
	private final String 		VERSION_FOOD_MAINSORT_NAME		= "foodMainSort_version";				//菜品大类版本名称
	private final String		VERSION_HALL_FOODMAINSORT_NAME	= "hallFoodMainSort_version";			//分厅-菜品大类关联版本名称
	private final String		VERSION_FOOD_SUBSORT_NAME		= "foodSubSort_version";				//菜品小类版本名称
	private final String		VERSION_FOOD_UNIT_NAME			= "foodUnit_version";					//菜品计价单位版本名称
	private final String		VERSION_FOOD_NAME				= "food_version";						//菜品版本名称
	private final String		VERSION_FOOD_PRICE_NAME			= "foodPrice_version";					//菜品单价版本名称
	private final String		VERSION_FOOD_RECSORT_NAME		= "foodRecSort_version";				//菜品做法类别版本名称
	private final String		VERSION_FOOD_RECIPE_NAME		= "foodRecipe_version";					//菜品做法版本名称
	private final String		VERSION_FOOD_FRSORT_NAME		= "foodFrsort_version";					//菜品-做法类别关联版本名称
	private final String		VERSION_FOOD_DEMAND_NAME		= "foodDemand_version";					//菜品要求版本名称
	private final String		VERSION_OPERATE_REASON_NAME		= "operateReason_version";				//操作原因版本名称
	private final String		VERSION_PRODUCE_PRINT_NAME		= "producePrint_version";				//出品部门和打印档口关联版本名称
	private final String		VERSION_TIME_ITEM_NAME			= "timeItem_version";					//计时项目版本名称
	private final String		VERSION_DTTYPE_TITEM_NAME		= "dttypeTitem_version";				//餐台类型和计时项目版本名称
	
	/**
	 * <p>ResourceVersionHelper</p>
	 *
	 * <p>构造函数</p>
	 */
	public ResourceVersionHelper(Context context) {
	
		preferences = context.getSharedPreferences(VERSION_PREFERENCES, Activity.MODE_PRIVATE);	
		editor		= preferences.edit();
	}	
	
	/**
	 * <p>getHallSortVersion</p>
	 *
	 * <p>获得当前分厅大类版本号</p>
	 * 
	 * @param defValue 默认值
	 * @return 分厅大类版本号
	 */
	public int getHallSortVersion(int defValue){
		return preferences.getInt(VERSION_HALLSORT_NAME, defValue);
	}
	
	/**
	 * <p>getHallVersion</p>
	 *
	 * <p>获得当前分厅版本号</p>
	 * 
	 * @param defValue 默认值
	 * @return 分厅版本号
	 */
	public int getHallVersion(int defValue){
		return preferences.getInt(VERSION_HALL_NAME, defValue);
	}
	
	/**
	 * <p>getDinnerTableTypeVersion</p>
	 *
	 * <p>获得当前餐台类型版本号</p>
	 * 
	 * @param defValue 默认值
	 * @return 餐台类型版本号
	 */
	public int getDinnerTableTypeVersion(int defValue){
		return preferences.getInt(VERSION_DINNERTABLE_TYPE_NAME, defValue);
	}
	
	/**
	 * <p>getDinnerTableVersion</p>
	 *
	 * <p>获得当前餐台版本号</p>
	 * 
	 * @param defValue 默认值
	 * @return 餐台版本号
	 */
	public int getDinnerTableVersion(int defValue){
		return preferences.getInt(VERSION_DINNERTABLE_NAME, defValue);
	}
	
	/**
	 * <p>getFoodMainSortVersion</p>
	 *
	 * <p>获得当前菜品大类版本号</p>
	 * 
	 * @param defValue 默认值
	 * @return 菜品大类版本号
	 */
	public int getFoodMainSortVersion(int defValue){
		return preferences.getInt(VERSION_FOOD_MAINSORT_NAME, defValue);
	}
	
	/**
	 * <p>getHallFoodMainSortVersion</p>
	 *
	 * <p>获得当前分厅菜品大类关联版本号</p>
	 * 
	 * @param defValue 默认值
	 * @return 分厅菜品大类关联版本号
	 */
	public int getHallFoodMainSortVersion(int defValue){
		return preferences.getInt(VERSION_HALL_FOODMAINSORT_NAME, defValue);
	}
	
	/**
	 * <p>getFoodSubSortVersion</p>
	 *
	 * <p>获得当前菜品小类版本号</p>
	 * 
	 * @param defValue 默认值
	 * @return 菜品小类版本号
	 */
	public int getFoodSubSortVersion(int defValue){
		return preferences.getInt(VERSION_FOOD_SUBSORT_NAME, defValue);
	}
	
	/**
	 * <p>getFoodUnitVersion</p>
	 *
	 * <p>获得当前菜品单位版本号</p>
	 * 
	 * @param defValue 默认值
	 * @return 菜品单位版本号
	 */
	public int getFoodUnitVersion(int defValue){
		return preferences.getInt(VERSION_FOOD_UNIT_NAME, defValue);
	}
	
	/**
	 * <p>getFoodVersion</p>
	 *
	 * <p>获得当前菜品版本号</p>
	 * 
	 * @param defValue 默认值
	 * @return 菜品版本号
	 */
	public int getFoodVersion(int defValue){
		return preferences.getInt(VERSION_FOOD_NAME, defValue);
	}
	
	/**
	 * <p>getFoodPrice</p>
	 *
	 * <p>获得当前菜品价格版本号</p>
	 * 
	 * @param defValue 默认值
	 * @return 菜品价格版本号
	 */
	public int getFoodPriceVersion(int defValue){
		return preferences.getInt(VERSION_FOOD_PRICE_NAME, defValue);
	}
	
	/**
	 * <p>getFoodRecipeTypeVersion</p>
	 *
	 * <p>获得当前菜品做法类别版本号</p>
	 * 
	 * @param defValue 默认值
	 * @return 菜品做法类别版本号
	 */
	public int getFoodRecipeSortVersion(int defValue){
		return preferences.getInt(VERSION_FOOD_RECSORT_NAME, defValue);
	}
	
	/**
	 * <p>getFoodRecipe</p>
	 *
	 * <p>获得当前菜品做法版本号</p>
	 * 
	 * @param defValue 默认值
	 * @return 菜品做法版本号
	 */
	public int getFoodRecipeVersion(int defValue){
		return preferences.getInt(VERSION_FOOD_RECIPE_NAME, defValue);
	}
	
	/**
	 * <p>getFoodFrtypeVersion</p>
	 *
	 * <p>获得当前菜品-做法类别关联版本号</p>
	 * 
	 * @param defValue 默认值
	 * @return 菜品-做法类别版本号
	 */
	public int getFoodFrsortVersion(int defValue){
		return preferences.getInt(VERSION_FOOD_FRSORT_NAME, defValue);
	}
	
	/**
	 * <p>getFoodDelivery</p>
	 *
	 * <p>获得当前菜品传菜要求版本号</p>
	 * 
	 * @param defValue 默认值
	 * @return 菜品传菜要求版本号
	 */
	public int getFoodDeliveryVersion(int defValue){
		return preferences.getInt(VERSION_FOOD_DEMAND_NAME, defValue);
	}
	
	/**
	 * <p>getOperateReasonVersion</p>
	 *
	 * <p>获得当前操作原因版本号</p>
	 * 
	 * @param defValue 默认值
	 * @return 操作原因版本号
	 */
	public int getOperateReasonVersion(int defValue){
		return preferences.getInt(VERSION_OPERATE_REASON_NAME, defValue);
	}
	
	/**
	 * <p>getProducePrintVersion</p>
	 *
	 * <p>获得当前出品部门和打印档口关联版本号</p>
	 * 
	 * @param defValue 默认值
	 * @return 出品部门和打印档口关联版本号
	 */
	public int getProducePrintVersion(int defValue){
		return preferences.getInt(VERSION_PRODUCE_PRINT_NAME, defValue);
	}
	
	/**
	 * <p>getTimeItemVersion</p>
	 *
	 * <p>获得当前计时项目版本号</p>
	 * 
	 * @param defValue
	 * @return 计时项目版本号
	 */
	public int getTimeItemVersion(int defValue){
		return preferences.getInt(VERSION_TIME_ITEM_NAME, defValue);
	}
	
	/**
	 * <p>getDttypeTitemVersion</p>
	 *
	 * <p>获得餐台类型和计时项目关联版本号</p>
	 * 
	 * @param defValue
	 * @return 餐台类型和计时项目关联版本号
	 */
	public int getDttypeTitemVersion(int defValue){
		return preferences.getInt(VERSION_DTTYPE_TITEM_NAME, defValue);
	}
	
	/**
	 * <p>setHallSortVersion</p>
	 *
	 * <p>设置分厅大类版本号</p>
	 * 
	 * @param value 版本号值
	 */
	public void setHallSortVersion(int value){		
		editor.putInt(VERSION_HALLSORT_NAME, value);
		editor.commit();
	}
	
	/**
	 * <p>setHallVersion</p>
	 *
	 * <p>设置分厅版本号</p>
	 * 
	 * @param value 版本号值
	 */
	public void setHallVersion(int value){		
		editor.putInt(VERSION_HALL_NAME, value);
		editor.commit();
	}
	
	/**
	 * <p>setDinnerTableTypeVersion</p>
	 *
	 * <p>设置餐台类型版本号</p>
	 * 
	 * @param value 版本号值
	 */
	public void setDinnerTableTypeVersion(int value){		
		editor.putInt(VERSION_DINNERTABLE_TYPE_NAME, value);
		editor.commit();
	}
	
	/**
	 * <p>setDinnerTableVersion</p>
	 *
	 * <p>设置餐台版本号</p>
	 * 
	 * @param value 版本号值
	 */
	public void setDinnerTableVersion(int value){		
		editor.putInt(VERSION_DINNERTABLE_NAME, value);
		editor.commit();
	}
	
	/**
	 * <p>setFoodMainSortVersion</p>
	 *
	 * <p>设置菜品大类版本号</p>
	 * 
	 * @param value 版本号值
	 */
	public void setFoodMainSortVersion(int value){		
		editor.putInt(VERSION_FOOD_MAINSORT_NAME, value);
		editor.commit();
	}
	
	/**
	 * <p>setHallFoodMainSortVersion</p>
	 *
	 * <p>设置分厅菜品大类关联版本号</p>
	 * 
	 * @param value 版本号值
	 */
	public void setHallFoodMainSortVersion(int value){
		editor.putInt(VERSION_HALL_FOODMAINSORT_NAME, value);
		editor.commit();
	}
	
	/**
	 * <p>setFoodSubSortVersion</p>
	 *
	 * <p>设置菜品小类版本号</p>
	 * 
	 * @param value 版本号值
	 */
	public void setFoodSubSortVersion(int value){		
		editor.putInt(VERSION_FOOD_SUBSORT_NAME, value);
		editor.commit();
	}
	
	/**
	 * <p>setFoodUnitVersion</p>
	 *
	 * <p>设置菜品单位版本号</p>
	 * 
	 * @param value 版本号值
	 */
	public void setFoodUnitVersion(int value){		
		editor.putInt(VERSION_FOOD_UNIT_NAME, value);
		editor.commit();
	}
	
	/**
	 * <p>setFoodVersion</p>
	 *
	 * <p>设置菜品版本号</p>
	 * 
	 * @param value 版本号值
	 */
	public void setFoodVersion(int value){		
		editor.putInt(VERSION_FOOD_NAME, value);
		editor.commit();
	}
	
	/**
	 * <p>setFoodPriceVersion</p>
	 *
	 * <p>设置菜品价格版本号</p>
	 * 
	 * @param value 版本号值
	 */
	public void setFoodPriceVersion(int value){		
		editor.putInt(VERSION_FOOD_PRICE_NAME, value);
		editor.commit();
	}
	
	/**
	 * <p>setFoodRecipeTypeVersion</p>
	 *
	 * <p>设置菜品做法类别版本号</p>
	 * 
	 * @param value 版本号值
	 */
	public void setFoodRecipeSortVersion(int value){
		editor.putInt(VERSION_FOOD_RECSORT_NAME, value);
		editor.commit();
	}
	
	/**
	 * <p>setFoodRecipe</p>
	 *
	 * <p>设置菜品做法版本号</p>
	 * 
	 * @param value 版本号值
	 */
	public void setFoodRecipeVersion(int value){
		editor.putInt(VERSION_FOOD_RECIPE_NAME, value);
		editor.commit();
	}
	
	/**
	 * <p>setFoodFrtypeVersion</p>
	 *
	 * <p>设置菜品-做法类别版本号</p>
	 * 
	 * @param value 版本号值
	 */
	public void setFoodFrsortVersion(int value){
		editor.putInt(VERSION_FOOD_FRSORT_NAME, value);
		editor.commit();
	}
	
	/**
	 * <p>setFoodDeliveryVersion</p>
	 *
	 * <p>设置菜品传菜要求版本号</p>
	 * 
	 * @param value 版本号值
	 */
	public void setFoodDeliveryVersion(int value){
		editor.putInt(VERSION_FOOD_DEMAND_NAME, value);
		editor.commit();
	}
	
	/**
	 * <p>setOperateReasonVersion</p>
	 *
	 * <p>设置操作原因版本号</p>
	 * 
	 * @param value 版本号值
	 */
	public void setOperateReasonVersion(int value){
		editor.putInt(VERSION_OPERATE_REASON_NAME, value);
		editor.commit();
	}
	
	/**
	 * <p>setProducePrintVersion</p>
	 *
	 * <p>设置出品部门和打印档口关联版本号</p>
	 * 
	 * @param value 版本号值
	 */
	public void setProducePrintVersion(int value){
		editor.putInt(VERSION_PRODUCE_PRINT_NAME, value);
		editor.commit();
	}
	
	/**
	 * <p>setTimeItemVersion</p>
	 *
	 * <p>设置计时项目版本号</p>
	 * 
	 * @param value 版本号值
	 */
	public void setTimeItemVersion(int value){
		editor.putInt(VERSION_TIME_ITEM_NAME, value);
		editor.commit();
	}
	
	/**
	 * <p>setDttypeTitemVersion</p>
	 *
	 * <p>设置餐台类型与计时项目关联版本号</p>
	 * 
	 * @param value 版本号值
	 */
	public void setDttypeTitemVersion(int value){
		editor.putInt(VERSION_DTTYPE_TITEM_NAME, value);
		editor.commit();
	}
}
