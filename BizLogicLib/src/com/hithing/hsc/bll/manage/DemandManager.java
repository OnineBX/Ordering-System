/**========================================
 * File:	DemandManager.java
 * Package:	com.hithing.hsc.bll.manage
 * Create:	by Leopard
 * Date:	2012-1-12:下午04:12:10
 **======================================*/
package com.hithing.hsc.bll.manage;

import java.sql.SQLException;
import java.util.LinkedList;
import java.util.List;

import com.hithing.hsc.dataentity.DemandEntity;
import com.j256.ormlite.dao.Dao;

/**
 * <p>DemandManager</p>
 * 	
 * <p>菜品要求管理类</p>
 *
 * @author Leopard
 * 
 */
public final class DemandManager {
	private final int DEMAND_TYPE_FOOD 	= 1;
	private final int DEMAND_TYPE_ORDER = 2;
	
	private Dao<DemandEntity, Integer> dao;	
	/**
	 * <p>DemandManager</p>
	 *
	 * <p>构造函数</p>
	 */
	public DemandManager(Dao<DemandEntity, Integer> dao) {
		
		this.dao = dao;
	}

	public List<DemandEntity> getFoodDemand(){
		try {
			return dao.queryForEq(DemandEntity.DEMAND_TYPE_FIELD_NAME, DEMAND_TYPE_FOOD);
		} catch (SQLException e) {			
			e.printStackTrace();
			return new LinkedList<DemandEntity>();
		}
	}
	
//	public List<DemandEntity> getOrderDemand(){
//		
//	}
}
