/**========================================
 * File:	FoodRecipeManager.java
 * Package:	com.hithing.hsc.bll.manage
 * Create:	by Leopard
 * Date:	2012-1-12:下午04:39:12
 **======================================*/
package com.hithing.hsc.bll.manage;

import java.sql.SQLException;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import com.hithing.hsc.dataentity.FoodFrsortEntity;
import com.hithing.hsc.dataentity.FoodRecipeEntity;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.stmt.PreparedQuery;
import com.j256.ormlite.stmt.QueryBuilder;

/**
 * <p>FoodRecipeManager</p>
 * 	
 * <p>菜品做法管理类</p>
 *
 * @author Leopard
 * 
 */
public final class FoodRecipeManager {
	private Dao<FoodFrsortEntity, Integer> ffrsDao;
	private Dao<FoodRecipeEntity, Integer> frDao;
	/**
	 * <p>FoodRecipeManager</p>
	 *
	 * <p>构造函数</p>
	 */
	public FoodRecipeManager(Dao<FoodRecipeEntity, Integer> frDao, Dao<FoodFrsortEntity, Integer> ffrsDao) {
		this.frDao 		= frDao;
		this.ffrsDao 	= ffrsDao;
	}

	public List<FoodRecipeEntity> getFoodRecipe(int foodId){
		Set<Integer> idSet = new HashSet<Integer>();
		try {
			List<FoodFrsortEntity> ffrsList = ffrsDao.queryForEq(FoodFrsortEntity.FOODID_FIELD_NAME, foodId);
			for(FoodFrsortEntity ent : ffrsList){
				idSet.add(ent.getFoodRecipeSort().getId());
			}
			
			QueryBuilder<FoodRecipeEntity, Integer> builder = frDao.queryBuilder();
			PreparedQuery<FoodRecipeEntity> pQuery = builder.where().in(FoodRecipeEntity.RECIPESORT_FIELD_NAME, idSet).prepare();
			return frDao.query(pQuery);
			
		} catch (SQLException e) {			
			e.printStackTrace();
			return new LinkedList<FoodRecipeEntity>();
		}
	}
}
