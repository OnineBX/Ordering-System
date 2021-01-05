/**========================================
 * File:	DinnerTableTypeManager.java
 * Package:	com.hithing.hsc.bll.manage
 * Create:	by Leopard
 * Date:	2011-12-15:下午03:47:10
 **======================================*/
package com.hithing.hsc.bll.manage;

import java.sql.SQLException;
import java.util.LinkedList;
import java.util.List;

import com.hithing.hsc.dataentity.DinnerTableTypeEntity;
import com.j256.ormlite.dao.Dao;

/**
 * <p>DinnerTableTypeManager</p>
 * 	
 * <p>餐台类型管理类</p>
 *
 * @author Leopard
 * 
 */
public class DinnerTableTypeManager {
	
	private Dao<DinnerTableTypeEntity, Integer> dao;
					
	/**
	 * <p>DinnerTableTypeManager</p>
	 *
	 * <p>构造函数</p>
	 */
	public DinnerTableTypeManager(Dao<DinnerTableTypeEntity, Integer> dao) {
		this.dao = dao;
	}
	
	/**
	 * <p>getAllTypesByHallId</p>
	 *
	 * <p>返回某分厅下的所有餐台类型</p>
	 * 
	 * @param hallId 分厅标识
	 * @return 标识为hallId的分厅下所有的餐台类型
	 * @throws SQLException 
	 */
	public List<DinnerTableTypeEntity> getAllTypesByHallId(int hallId) {
	
		try {			
			return dao.queryForEq("fk_hall_id", hallId);			
			
		} catch (SQLException e) {
			
			e.printStackTrace();
			//返回一个不含任何元素的List
			return new LinkedList<DinnerTableTypeEntity>();
		}
		
		
	}

}
