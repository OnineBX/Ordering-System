/**========================================
 * File:	FoodSubSortManager.java
 * Package:	com.hithing.hsc.bll.manage
 * Create:	by Leopard
 * Date:	2011-12-29:下午01:16:09
 **======================================*/
package com.hithing.hsc.bll.manage;

import java.sql.SQLException;
import java.util.LinkedList;
import java.util.List;

import android.util.Log;

import com.hithing.hsc.dataentity.DinnerTableEntity;
import com.hithing.hsc.dataentity.FoodSubSortEntity;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.stmt.PreparedQuery;
import com.j256.ormlite.stmt.QueryBuilder;

/**
 * <p>FoodSubSortManager</p>
 * 	
 * <p>菜品小类管理类</p>
 *
 * @author Leopard
 * 
 */
public final class FoodSubSortManager {

	private Dao<FoodSubSortEntity, Integer> dao;
	/**
	 * <p>FoodSubSortManager</p>
	 *
	 * <p>构造函数</p>
	 */
	public FoodSubSortManager(Dao<FoodSubSortEntity, Integer> dao) {
		this.dao = dao;
	}

	/**
	 * <p>getFoodSubSorts</p>
	 *
	 * <p>得到某菜品大类下的所有菜品小类</p>
	 * 
	 * @param foodMainSortId 菜品大类标识
	 * @return 菜品小类列表
	 */
	public List<FoodSubSortEntity> getFoodSubSorts(int foodMainSortId){//unused method
		try {
			return dao.queryForEq(FoodSubSortEntity.FOODMAINSORT_FIELD_NAME, foodMainSortId);
		} catch (SQLException e) {			
			e.printStackTrace();
			return new LinkedList<FoodSubSortEntity>();
		}
	}
	
	/**
	 * <p>getFoodSubSorts</p>
	 *
	 * <p>得到菜品大类集合内的所有菜品小类</p>
	 * 
	 * @param mainSortSet 菜品大类集合
	 * @return 菜品小类列表
	 */
	public List<FoodSubSortEntity> getFoodSubSorts(Iterable<Integer> mainSortSet){
		try {
			QueryBuilder<FoodSubSortEntity, Integer> builder = dao.queryBuilder();
			PreparedQuery<FoodSubSortEntity> pQuery = builder.where().in(FoodSubSortEntity.FOODMAINSORT_FIELD_NAME, mainSortSet).prepare();
			return dao.query(pQuery);
		} catch (SQLException e) {			
			Log.e("DinnerTableManager.getAllTablesByTypes", e.getMessage());
			return new LinkedList<FoodSubSortEntity>();
		}
	}
	
}
