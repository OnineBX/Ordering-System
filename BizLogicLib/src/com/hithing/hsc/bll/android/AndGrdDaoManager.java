/**========================================
 * File:	AndGrdDaoManager.java
 * Package:	com.hithing.hsc.dal.database
 * Create:	by Leopard
 * Date:	2011-12-15:下午10:41:32
 **======================================*/
package com.hithing.hsc.bll.android;

import java.sql.SQLException;

import android.content.Context;

import com.hithing.hsc.bll.manage.GreederDaoManager;
import com.hithing.hsc.dal.database.GreederSqliteHelper;
import com.hithing.hsc.dataentity.DinTabTypeTimItemEntity;
import com.hithing.hsc.dataentity.DinnerTableEntity;
import com.hithing.hsc.dataentity.DinnerTableTypeEntity;
import com.hithing.hsc.dataentity.DemandEntity;
import com.hithing.hsc.dataentity.FoodEntity;
import com.hithing.hsc.dataentity.FoodFrsortEntity;
import com.hithing.hsc.dataentity.FoodMainSortEntity;
import com.hithing.hsc.dataentity.FoodPriceEntity;
import com.hithing.hsc.dataentity.FoodRecipeEntity;
import com.hithing.hsc.dataentity.FoodRecipeSortEntity;
import com.hithing.hsc.dataentity.FoodSubSortEntity;
import com.hithing.hsc.dataentity.FoodUnitEntity;
import com.hithing.hsc.dataentity.HallEntity;
import com.hithing.hsc.dataentity.HallFoodMainSortEntity;
import com.hithing.hsc.dataentity.HallSortEntity;
import com.hithing.hsc.dataentity.OperateReasonEntity;
import com.hithing.hsc.dataentity.ProducePrintEntity;
import com.hithing.hsc.dataentity.TimeItemEntity;
import com.j256.ormlite.android.apptools.OpenHelperManager;
import com.j256.ormlite.dao.Dao;

/**
 * <p>AndGrdDaoManager</p>
 * 	
 * <p>功能描述</p>
 *
 * @author Leopard
 * 
 */
public final class AndGrdDaoManager extends GreederDaoManager {

	private final GreederSqliteHelper sqliteHelper;	
	
	/**
	 * <p>AndGrdDaoManager</p>
	 *
	 * <p>构造函数</p>
	 */		
	public AndGrdDaoManager(Context context){
		sqliteHelper = OpenHelperManager.getHelper(context, GreederSqliteHelper.class);		
	}

	/* (non-Javadoc)
	 * @see com.hithing.hsc.dal.database.GreederDaoManager#getDinnerTableTypeDao()
	 */
	@Override
	public Dao<DinnerTableTypeEntity, Integer> getDinnerTableTypeDao(){
				
		try {
			return sqliteHelper.getDao(DinnerTableTypeEntity.class);
		} catch (SQLException e) {			
			e.printStackTrace();
			return null;
		}
	}

	@Override
	public Dao<DinnerTableEntity, Integer> getDinnerTableDao(){
		
		try {
			return sqliteHelper.getDao(DinnerTableEntity.class);
		} catch (SQLException e) {			
			e.printStackTrace();
			return null;
		}
	}	

	@Override
	public Dao<HallSortEntity, Integer> getHallSortDao() {
		
		try {
			return sqliteHelper.getDao(HallSortEntity.class);
		} catch (SQLException e) {			
			e.printStackTrace();
			return null;
		}		
	}	
	
	@Override
	public Dao<HallEntity, Integer> getHallDao(){
		
		try {
			return sqliteHelper.getDao(HallEntity.class);
		} catch (SQLException e) {			
			e.printStackTrace();
			return null;
		}
	}
	
	@Override
	public Dao<FoodMainSortEntity, Integer> getFoodMainSortDao() {
		
		try {
			return sqliteHelper.getDao(FoodMainSortEntity.class);
		} catch (SQLException e) {		
			e.printStackTrace();
			return null;
		}		
	}
	
	@Override
	public Dao<FoodSubSortEntity, Integer> getFoodSubSortDao() {
		try {
			return sqliteHelper.getDao(FoodSubSortEntity.class);
		} catch (SQLException e) {			
			e.printStackTrace();
			return null;
		}
		
	}	
	
	@Override
	public Dao<FoodUnitEntity, Integer> getFoodUnitDao() {
		try {
			return sqliteHelper.getDao(FoodUnitEntity.class);
		} catch (SQLException e) {		
			e.printStackTrace();
			return null;
		}		
	}	
	
	@Override
	public Dao<FoodEntity, Integer> getFoodDao() {
		try {
			return sqliteHelper.getDao(FoodEntity.class);
		} catch (SQLException e) {		
			e.printStackTrace();
			return null;
		}		
	}	

	@Override
	public Dao<FoodPriceEntity, Integer> getFoodPriceDao() {
		try {
			return sqliteHelper.getDao(FoodPriceEntity.class);
		} catch (SQLException e) {			
			e.printStackTrace();
			return null;
		}		
	}	
	
	@Override
	public Dao<HallFoodMainSortEntity, Integer> getHallFoodMainSortDao() {
		try {
			return sqliteHelper.getDao(HallFoodMainSortEntity.class);
		} catch (SQLException e) {			
			e.printStackTrace();
			return null;
		}
		
	}
	
	@Override
	public Dao<DemandEntity, Integer> getDemandDao() {
		try {
			return sqliteHelper.getDao(DemandEntity.class);
		} catch (SQLException e) {			
			e.printStackTrace();
			return null;
		}
	}

	@Override
	public Dao<FoodRecipeEntity, Integer> getFoodRecipeDao() {
		try {
			return sqliteHelper.getDao(FoodRecipeEntity.class);
		} catch (SQLException e) {			
			e.printStackTrace();
			return null;
		}
	}	
	
	@Override
	public Dao<FoodRecipeSortEntity, Integer> getFoodRecipeSortDao() {
		try {
			return sqliteHelper.getDao(FoodRecipeSortEntity.class);
		} catch (SQLException e) {			
			e.printStackTrace();
			return null;
		}		
	}
	
	@Override
	public Dao<FoodFrsortEntity, Integer> getFoodFrsortDao() {
		try {
			return sqliteHelper.getDao(FoodFrsortEntity.class);
		} catch (SQLException e) {		
			e.printStackTrace();
			return null;
		}		
	}			

	@Override
	public Dao<OperateReasonEntity, Integer> getOperateReasonDao() {
		try {
			return sqliteHelper.getDao(OperateReasonEntity.class);
		} catch (SQLException e) {		
			e.printStackTrace();
			return null;
		}	
	}

	@Override
	public Dao<ProducePrintEntity, Integer> getProducePrintDao() {
		try {
			return sqliteHelper.getDao(ProducePrintEntity.class);
		} catch (SQLException e) {		
			e.printStackTrace();
			return null;
		}	
	}
	
	@Override
	public Dao<TimeItemEntity, Integer> getTimeItemDao() {
		
		try {
			return sqliteHelper.getDao(TimeItemEntity.class);
		} catch (SQLException e) {			
			e.printStackTrace();
			return null;
		}
	}
	
	@Override
	public Dao<DinTabTypeTimItemEntity, Integer> getDinTabTypeTimItemDao() {
		
		try {
			return sqliteHelper.getDao(DinTabTypeTimItemEntity.class);
		} catch (SQLException e) {			
			e.printStackTrace();
			return null;
		}
	}
	
	@Override
	public void release() {
		OpenHelperManager.releaseHelper();				
	}		
	
}
