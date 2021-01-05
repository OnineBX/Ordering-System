/**========================================
 * File:	FoodMainSortManager.java
 * Package:	com.hithing.hsc.bll.manage
 * Create:	by Leopard
 * Date:	2011-12-25:下午3:07:14
 **======================================*/
package com.hithing.hsc.bll.manage;

import java.sql.SQLException;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import com.hithing.hsc.dataentity.FoodMainSortEntity;
import com.hithing.hsc.dataentity.HallFoodMainSortEntity;
import com.j256.ormlite.dao.Dao;

/**
 * <p>FoodMainSortManager</p>
 * 	
 * <p>菜品大类管理类</p>
 *
 * @author Leopard
 * 
 */
public final class FoodMainSortManager {
	
	private Dao<HallFoodMainSortEntity, Integer> dao;	

	/**
	 * <p>FoodMainSortManager</p>
	 *
	 * <p>构造函数</p>
	 */
	public FoodMainSortManager(Dao<HallFoodMainSortEntity, Integer> dao) {
		
		this.dao = dao;
	}
	
	/**
	 * <p>getAllFoodMainSorts</p>
	 *
	 * <p>得到当前分厅下所有的菜品大类</p>
	 * 
	 * @return 菜品大类列表
	 */
	public List<FoodMainSortEntity> getAllFoodMainSorts(int hall){
		try {
			List<HallFoodMainSortEntity> 	data 	= dao.queryForEq(HallFoodMainSortEntity.HALLID_FIELD_NAME, hall);			
			List<FoodMainSortEntity> 		result	= new LinkedList<FoodMainSortEntity>();
			
			FoodMainSortEntity fmsEnt;
			for(HallFoodMainSortEntity ent : data){
				fmsEnt = ent.getFoodMainSort();
				fmsEnt.refresh();
				result.add(fmsEnt);
			}
			return result;
		} catch (SQLException e) {			
			e.printStackTrace();
			//返回一个不含任何元素的List
			return new LinkedList<FoodMainSortEntity>();
		}
	}
	
	/**
	 * <p>getAllFoodMainSortIds</p>
	 *
	 * <p>返回分厅下所有菜品大类的标识集合</p>
	 * 
	 * @param hall 分厅标识
	 * @return 菜品大类标识集合
	 */
	public Set<Integer> getAllFoodMainSortIds(int hall){
		List<HallFoodMainSortEntity> data;
		try {
			Set<Integer> result	= new HashSet<Integer>();
			data = dao.queryForEq(HallFoodMainSortEntity.HALLID_FIELD_NAME, hall);
						
			FoodMainSortEntity fmsEnt;
			for(HallFoodMainSortEntity ent : data){
				fmsEnt = ent.getFoodMainSort();			
				result.add(fmsEnt.getId());
			}
			return result;
		} catch (SQLException e) {			
			e.printStackTrace();
			return new HashSet<Integer>();
		}					
	}

}
