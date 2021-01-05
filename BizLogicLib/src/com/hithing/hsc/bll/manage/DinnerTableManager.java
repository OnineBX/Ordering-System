/**========================================
 * File:	DinnerTableManager.java
 * Package:	com.hithing.hsc.bll.manage
 * Create:	by Leopard
 * Date:	2011-12-16:下午12:13:38
 **======================================*/
package com.hithing.hsc.bll.manage;

import java.sql.SQLException;
import java.util.LinkedList;
import java.util.List;

import android.util.Log;

import com.hithing.hsc.dataentity.DinTabTypeTimItemEntity;
import com.hithing.hsc.dataentity.DinnerTableEntity;
import com.hithing.hsc.dataentity.DinnerTableTypeEntity;
import com.hithing.hsc.dataentity.TimeItemEntity;
import com.j256.ormlite.dao.CloseableWrappedIterable;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.stmt.PreparedQuery;
import com.j256.ormlite.stmt.QueryBuilder;

/**
 * <p>DinnerTableManager</p>
 * 	
 * <p>餐台管理类</p>
 *
 * @author Leopard
 * 
 */
public final class DinnerTableManager {
	Dao<DinnerTableEntity, Integer> 		dtDao;
	
	/**
	 * <p>DinnerTableManager</p>
	 *
	 * <p>构造函数</p>
	 */
	public DinnerTableManager(Dao<DinnerTableEntity, Integer> dtDao){
		this.dtDao = dtDao;
	}		
	
	/**
	 * <p>getAllTablesByType</p>
	 *
	 * <p>得到同一餐台类型的所有餐台</p>
	 * 
	 * @param type 餐台类型
	 * @return 餐台列表
	 */
	public List<DinnerTableEntity>  getAllTablesByType(int type){//unused method
		try {
			return dtDao.queryForEq(DinnerTableEntity.DINNERTABLETYPE_FIELD_NAME, type);
		} catch (SQLException e) {			
			e.printStackTrace();
			return new LinkedList<DinnerTableEntity>();
		}
	}
	
	/**
	 * <p>getAllTablesByTypes</p>
	 *
	 * <p>得到餐台类型集合内的所有餐台</p>
	 * 
	 * @param typeSet 餐台类型集合
	 * @return 餐台列表
	 */
	public List<DinnerTableEntity> getAllTablesByTypes(Iterable<Integer> typeSet){		
		try {
			QueryBuilder<DinnerTableEntity, Integer> builder = dtDao.queryBuilder();
			PreparedQuery<DinnerTableEntity> pQuery = builder.where().in(DinnerTableEntity.DINNERTABLETYPE_FIELD_NAME, typeSet).prepare();
			return dtDao.query(pQuery);
		} catch (SQLException e) {			
			Log.e("DinnerTableManager.getAllTablesByTypes", e.getMessage());
			return new LinkedList<DinnerTableEntity>();
		}
	}
	
	/**
	 * <p>getTableById</p>
	 *
	 * <p>根据餐台标识得到餐台实体</p>
	 * 
	 * @param id 餐台标识
	 * @return 餐台实体
	 */
	public DinnerTableEntity getTableById(int id){
		try {
			return dtDao.queryForId(id);
		} catch (SQLException e) {			
			e.printStackTrace();
			return null;
		}
	}
	
	/**
	 * <p>getAvailiableTimeItem</p>
	 *
	 * <p>得到 用的计时项目</p>
	 * 
	 * @param tableId 餐台标识
	 * @return 计时项目集合
	 */
	public List<TimeItemEntity> getAvailiableTimeItem(int tableId){
		List<TimeItemEntity> result = new LinkedList<TimeItemEntity>();
		try {
			DinnerTableEntity dtEnt = dtDao.queryForId(tableId);
			if(dtEnt != null){
				DinnerTableTypeEntity dttEnt = dtEnt.getType();
				dttEnt.refresh();
				CloseableWrappedIterable<DinTabTypeTimItemEntity> iterable = 
						dttEnt.getTimeItems().getWrappedIterable();				
				for(DinTabTypeTimItemEntity ent: iterable){
					TimeItemEntity tiEnt = ent.getTimeItem();
					tiEnt.refresh();
					result.add(tiEnt);
				}	
				iterable.close();
			}			
		} catch (SQLException e) {		
			e.printStackTrace();			
		}
		return result;
	}
	
}
