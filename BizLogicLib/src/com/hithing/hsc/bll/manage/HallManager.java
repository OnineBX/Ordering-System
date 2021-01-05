/**========================================
 * File:	HallManager.java
 * Package:	com.hithing.hsc.bll.manage
 * Create:	by Leopard
 * Date:	2011-12-23:下午03:48:21
 **======================================*/
package com.hithing.hsc.bll.manage;

import java.sql.SQLException;

import android.util.Log;

import com.hithing.hsc.dataentity.HallEntity;
import com.j256.ormlite.dao.Dao;

/**
 * <p>HallManager</p>
 * 	
 * <p>分厅管理类</p>
 *
 * @author Leopard
 * 
 */
public final class HallManager {
	private Dao<HallEntity, Integer> dao;
	/**
	 * <p>HallManager</p>
	 *
	 * <p>构造函数</p>
	 * @param dao 分厅数据访问对象
	 */
	public HallManager(Dao<HallEntity, Integer> dao){
		this.dao = dao;
	}
	
	/**
	 * <p>getHallNameById</p>
	 *
	 * <p>根据分厅标识得到分厅名称</p>
	 * 
	 * @param id 分厅标识
	 * @return 分厅名称
	 */
	public String getHallNameById(int id){
		try {			
			HallEntity ent = dao.queryForId(id);			
			return ent.getName(); 			
			
		} catch (SQLException e) {			
			e.printStackTrace();
			return null;
		}
	}
}
