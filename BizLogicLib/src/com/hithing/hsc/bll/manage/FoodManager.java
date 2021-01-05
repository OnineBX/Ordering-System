/**========================================
 * File:	FoodManager.java
 * Package:	com.hithing.hsc.bll.manage
 * Create:	by Leopard
 * Date:	2011-12-28:下午03:37:08
 **======================================*/
package com.hithing.hsc.bll.manage;

import java.sql.SQLException;
import java.util.LinkedList;
import java.util.List;

import android.util.Log;

import com.hithing.hsc.dataentity.FoodEntity;
import com.hithing.hsc.dataentity.FoodPriceEntity;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.stmt.PreparedQuery;
import com.j256.ormlite.stmt.QueryBuilder;

/**
 * <p>FoodManager</p>
 * 	
 * <p>菜品管理类,用于管理菜品数据</p>
 *
 * @author Leopard
 * 
 */
public final class FoodManager {

	private Dao<FoodEntity, Integer> foodDao;
	private Dao<FoodPriceEntity, Integer> fpDao;
	/**
	 * <p>FoodManager</p>
	 *
	 * <p>构造函数</p>
	 */
	public FoodManager(Dao<FoodEntity, Integer> dao, Dao<FoodPriceEntity, Integer> fpDao) {
		this.foodDao 	= dao;
		this.fpDao		= fpDao;
	}
		
	/**
	 * <p>getFoodNameById</p>
	 *
	 * <p>功能描述</p>
	 * 
	 * @param foodId
	 * @return
	 */
//	public String getFoodNameById(int foodId){
//		try {
//			return dao.queryForId(foodId).getName();
//		} catch (SQLException e) {			
//			e.printStackTrace();
//			return null;
//		}
//	}
	
	/**
	 * <p>getAllFoodsBySubSorts</p>
	 *
	 * <p>得到菜品小类集合内的所有菜品</p>
	 * 
	 * @param subSortSet 菜品小类集合
	 * @return 菜品列表
	 */
	public List<FoodEntity> getAllFoodsBySubSorts(Iterable<Integer> subSortSet ){
		try {
			QueryBuilder<FoodEntity, Integer> builder = foodDao.queryBuilder();
			PreparedQuery<FoodEntity> pQuery = builder.where().in(FoodEntity.FOODSUBSORT_FIELD_NAME, subSortSet).prepare();
			return foodDao.query(pQuery);
		} catch (SQLException e) {			
			e.printStackTrace();
			return new LinkedList<FoodEntity>();
		}
	}
	
	/**
	 * <p>getFoodPrices</p>
	 *
	 * <p>得到某个菜品的价格集合</p>
	 * 
	 * @param foodId
	 * @return
	 */
	public List<FoodPriceEntity> getFoodPrices(int foodId){
		
		try {
			return fpDao.queryForEq(FoodPriceEntity.FOODID_FIELD_NAME, foodId);
		} catch (SQLException e) {			
			e.printStackTrace();
			return new LinkedList<FoodPriceEntity>();
		}
	}	
	
	/**
	 * <p>getFoodPrice</p>
	 *
	 * <p>得到某个菜品计价单位的价格信息</p>
	 * 
	 * @param foodPriceId
	 * @return
	 */
	public FoodPriceEntity getFoodPrice(int foodPriceId){
		try {
			return fpDao.queryForId(foodPriceId);
		} catch (SQLException e) {			
			e.printStackTrace();
			return null;
		}
	}
	
	/**
	 * <p>getDefaultPrice</p>
	 *
	 * <p>得到某个菜品的默认单价</p>
	 * 
	 * @param foodId 菜品标识
	 * @return 菜品默认单价
	 */
	public FoodPriceEntity getDefaultPrice(int foodId){		
		try {							
			FoodPriceEntity fpEnt = getFoodPrices(foodId).get(FoodPriceEntity.FIRST_RECORD_LOCATION);
			fpEnt.refresh();
			return fpEnt;		
		} catch (SQLException e) {			
			e.printStackTrace();
			return null;
		}			
	}	
	
	/**
	 * <p>getFood</p>
	 *
	 * <p>根据菜品标识得到菜品信息</p>
	 * 
	 * @param foodId 菜品标识
	 * @return 菜品实体
	 */
	public FoodEntity getFood(int foodId){
		try {
			return foodDao.queryForId(foodId);
		} catch (SQLException e) {			
			e.printStackTrace();
			return null;
		}
	}
	
	/**
	 * <p>getStockFoods</p>
	 *
	 * <p>得到可以纳入库存管理的菜品集合</p>
	 * 
	 * @return 库存菜品集合
	 */
	public List<FoodEntity> getStockFoods(){
		try {
			return foodDao.queryForEq(FoodEntity.FOODISSTOCK_FIELD_NAME, true);
		} catch (SQLException e) {			
			e.printStackTrace();
			return new LinkedList<FoodEntity>();
		}
	}
	
	/**
	 * <p>getFoodByPriceId</p>
	 *
	 * <p>根据菜品单价标识得到菜品实体</p>
	 * 
	 * @param fpId 菜品单价标识
	 * @return 菜品实体
	 */
	public FoodEntity getFoodByPriceId(int fpId){
		FoodEntity foodEnt = null;
		try {			
			FoodPriceEntity fpEnt = fpDao.queryForId(fpId);
			if(fpEnt != null){
				foodEnt = fpEnt.getFood();
				foodEnt.refresh();
				foodEnt.getFoodSubSort().refresh();
				foodEnt.getFoodSubSort().getFoodMainSort().refresh();
				return fpEnt.getFood();
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return foodEnt;
	}
		
}
