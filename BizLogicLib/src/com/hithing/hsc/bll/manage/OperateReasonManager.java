/**========================================
 * File:	OperateReasonManager.java
 * Package:	com.hithing.hsc.bll.manage
 * Create:	by Leopard
 * Date:	2012-2-9:下午2:30:08
 **======================================*/
package com.hithing.hsc.bll.manage;

import java.sql.SQLException;
import java.util.LinkedList;
import java.util.List;

import com.hithing.hsc.dataentity.OperateReasonEntity;
import com.hithing.hsc.dataentity.OperateReasonEntity.OperateType;
import com.j256.ormlite.dao.Dao;

/**
 * <p>OperateReasonManager</p>
 * 	
 * <p>操作菜品原因管理类,用于管理账单中菜品操作的原因</p>
 *
 * @author Leopard
 * 
 */
public final class OperateReasonManager {	
	
	private Dao<OperateReasonEntity, Integer> dao;

	/**
	 * <p>OperateReasonManager</p>
	 *
	 * <p>构造函数</p>
	 */
	public OperateReasonManager(Dao<OperateReasonEntity, Integer> dao) {
		this.dao = dao;
	}
	
	/**
	 * <p>getCancelReason</p>
	 *
	 * <p>得到所有取消菜品的原因</p>
	 * 
	 * @return 取消菜品原因集合
	 */
	public List<OperateReasonEntity> getCancelReason(){
		try {
			
			return dao.queryForEq(OperateReasonEntity.OPERATETYPE_FIELD_NAME, OperateType.cancel);
			
		} catch (SQLException e) {			
			e.printStackTrace();
			return new LinkedList<OperateReasonEntity>();
		}
	}
	
	/**
	 * <p>getPresentReason</p>
	 *
	 * <p>得到所有赠送菜品的原因</p>
	 * 
	 * @return 赠送菜品原因集合
	 */
	public List<OperateReasonEntity> getPresentReason(){
		try {
			
			return dao.queryForEq(OperateReasonEntity.OPERATETYPE_FIELD_NAME, OperateType.present);
			
		} catch (SQLException e) {			
			e.printStackTrace();
			return new LinkedList<OperateReasonEntity>();
		}
	}

}
