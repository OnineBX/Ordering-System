/**========================================
 * File:	GreederDaoManager.java
 * Package:	com.hithing.hsc.dal.database
 * Create:	by Leopard
 * Date:	2011-12-15:下午10:24:28
 **======================================*/
package com.hithing.hsc.bll.manage;


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
import com.j256.ormlite.dao.Dao;

/**
 * <p>GreederDaoManager</p>
 * 	
 * <p>Greeder框架数据访问对象管理器</p>
 *
 * @author Leopard
 * 
 */
public abstract class GreederDaoManager {

	/**
	 * <p>GreederDaoManager</p>
	 *
	 * <p>构造函数</p>
	 */
	public GreederDaoManager() {
		// TODO Auto-generated constructor stub
	}

	/**
	 * <p>getDinnerTableTypeDao</p>
	 *
	 * <p>获得Ormlite框架下的餐台类型数据访问对象</p>
	 * 
	 * @return 餐台类型Dao
	 */
	public abstract Dao<DinnerTableTypeEntity, Integer> getDinnerTableTypeDao();
	
	/**
	 * <p>getDinnerTableDao</p>
	 *
	 * <p>获得Ormlite框架下的餐台数据访问对象</p>
	 * 
	 * @return 餐台Dao
	 */
	public abstract Dao<DinnerTableEntity, Integer> getDinnerTableDao();
	
	/**
	 * <p>getHallSortDao</p>
	 *
	 * <p>获得Ormlite框架下的分厅大类数据访问对象</p>
	 * 
	 * @return
	 */
	public abstract Dao<HallSortEntity, Integer> getHallSortDao();
	
	/**
	 * <p>getHallDao</p>
	 *
	 * <p>获得Ormlite框架下的分厅数据访问对象</p>
	 * 
	 * @return 分厅Dao
	 */
	public abstract Dao<HallEntity, Integer> getHallDao();
	
	/**
	 * <p>getFoodMainSortDao</p>
	 *
	 * <p>获得Ormlite框架下的菜品大类数据访问对象</p>
	 * 
	 * @return 菜品大类Dao
	 */
	public abstract Dao<FoodMainSortEntity, Integer> getFoodMainSortDao();	
	
	/**
	 * <p>getHallFoodMainSortDao</p>
	 *
	 * <p>获得Ormlite框架下的分厅菜品大类关联数据访问对象</p>
	 * 
	 * @return 分厅菜品大类Dao
	 */
	public abstract Dao<HallFoodMainSortEntity, Integer> getHallFoodMainSortDao();
	
	/**
	 * <p>getFoodSubSortDao</p>
	 *
	 * <p>获得Ormlite框架下的菜品小类数据访问对象</p>
	 * 
	 * @return 菜品小类Dao
	 */
	public abstract Dao<FoodSubSortEntity, Integer> getFoodSubSortDao();
	
	/**
	 * <p>getFoodUnitDao</p>
	 *
	 * <p>获得Ormlite框架下的菜品计价单位数据访问对象</p>
	 * 
	 * @return 菜品计价单位Dao
	 */
	public abstract Dao<FoodUnitEntity, Integer> getFoodUnitDao();
	
	/**
	 * <p>getFoodDao</p>
	 *
	 * <p>获得Ormlite框架下的菜品数据访问对象</p>
	 * 
	 * @return 菜品Dao
	 */
	public abstract Dao<FoodEntity, Integer> getFoodDao();
	
	/**
	 * <p>getFoodPriceDao</p>
	 *
	 * <p>获得Ormlite框架下的菜品价格数据访问对象</p>
	 * 
	 * @return 菜品价格Dao
	 */
	public abstract Dao<FoodPriceEntity, Integer> getFoodPriceDao();
	
	/**
	 * <p>getFoodRecipeTypeDao</p>
	 *
	 * <p>获得Ormlite框架下的菜品做法类别访问对象</p>
	 * 
	 * @return 菜品做法类别Dao
	 */
	public abstract Dao<FoodRecipeSortEntity, Integer> getFoodRecipeSortDao();
	
	/**
	 * <p>getFoodRecipeDao</p>
	 *
	 * <p>获得Ormlite框架下的菜品做法数据访问对象</p>
	 * 
	 * @return 菜品做法Dao
	 */
	public abstract Dao<FoodRecipeEntity, Integer> getFoodRecipeDao();
	
	/**
	 * <p>getFoodFrsortDao</p>
	 *
	 * <p>获得Ormlite框架下的菜品-做法类别关联数据访问对象</p>
	 * 
	 * @return 菜品-做法类别关联Dao
	 */
	public abstract Dao<FoodFrsortEntity, Integer> getFoodFrsortDao();
	
	/**
	 * <p>getFoodDeliveryDao</p>
	 *
	 * <p>获得Ormlite框架下的菜品传菜要求数据访问对象</p>
	 * 
	 * @return 传菜要求Dao
	 */
	public abstract Dao<DemandEntity, Integer> getDemandDao();
	
	/**
	 * <p>getOperateReasonDao</p>
	 *
	 * <p>获得Ormlite框架下的操作原因数据访问对象</p>
	 * 
	 * @return 操作原因Dao
	 */
	public abstract Dao<OperateReasonEntity, Integer> getOperateReasonDao();
	
	/**
	 * <p>getProducePrintDao</p>
	 *
	 * <p>获得Ormlite框架下的出品部门和打印档口关联数据访问对象</p>
	 * 
	 * @return 出品部门和打印档口关联Dao
	 */
	public abstract Dao<ProducePrintEntity, Integer> getProducePrintDao();
	
	/**
	 * <p>getTimeItemDao</p>
	 *
	 * <p>获得Ormlite框架下的计时项目数据访问对象</p>
	 * 
	 * @return 计时项目Dao
	 */
	public abstract Dao<TimeItemEntity, Integer> getTimeItemDao();
	
	/**
	 * <p>getDinTabTypeTimItemDao</p>
	 *
	 * <p>获得Ormlite框架下的餐台类型与计时项目关联数据访问对象</p>
	 * 
	 * @return 餐台类型与计时项目关联Dao
	 */
	public abstract Dao<DinTabTypeTimItemEntity, Integer> getDinTabTypeTimItemDao();
	
	/**
	 * <p>release</p>
	 *
	 * <p>释放GreederDaoManager使用的Dao管理者</p>
	 *
	 */	
	public abstract void release();
}
