/**========================================
 * File:	GreederSqliteConfigureUtil.java
 * Package:	com.hithing.hsc.dal.database
 * Create:	by Leopard
 * Date:	2011-12-3:下午02:27:12
 **======================================*/
package com.hithing.hsc.dal.database;

import java.io.IOException;
import java.sql.SQLException;

import com.hithing.hsc.dataentity.DinTabTypeTimItemEntity;
import com.hithing.hsc.dataentity.DinnerTableEntity;
import com.hithing.hsc.dataentity.DinnerTableTypeEntity;
import com.hithing.hsc.dataentity.DemandEntity;
import com.hithing.hsc.dataentity.FoodEntity;
import com.hithing.hsc.dataentity.FoodFrsortEntity;
import com.hithing.hsc.dataentity.FoodPriceEntity;
import com.hithing.hsc.dataentity.FoodRecipeEntity;
import com.hithing.hsc.dataentity.FoodRecipeSortEntity;
import com.hithing.hsc.dataentity.FoodSubSortEntity;
import com.hithing.hsc.dataentity.FoodUnitEntity;
import com.hithing.hsc.dataentity.HallEntity;
import com.hithing.hsc.dataentity.HallFoodMainSortEntity;
import com.hithing.hsc.dataentity.HallSortEntity;
import com.hithing.hsc.dataentity.FoodMainSortEntity;
import com.hithing.hsc.dataentity.OperateReasonEntity;
import com.hithing.hsc.dataentity.ProducePrintEntity;
import com.hithing.hsc.dataentity.TimeItemEntity;
import com.j256.ormlite.android.apptools.OrmLiteConfigUtil;

/**
 * <p>GreederSqliteConfigureUtil</p>
 * 	
 * <p>用于创建ormlite_config.txt文件</p>
 *
 * @author Leopard
 * 
 */
public final class GreederSqliteConfigureUtil extends OrmLiteConfigUtil {

	private static final Class<?>[] classes = new Class[]{
		HallSortEntity.class,
		HallEntity.class,
		DinnerTableTypeEntity.class,
		DinnerTableEntity.class,
		FoodMainSortEntity.class,
		HallFoodMainSortEntity.class,
		FoodSubSortEntity.class,
		FoodUnitEntity.class,
		FoodEntity.class,
		FoodPriceEntity.class,
		FoodRecipeSortEntity.class,
		FoodRecipeEntity.class,
		FoodFrsortEntity.class,
		DemandEntity.class,
		OperateReasonEntity.class,
		ProducePrintEntity.class,
		TimeItemEntity.class,
		DinTabTypeTimItemEntity.class,
	};
	/**
	 * <p>GreederSqliteConfigureUtil</p>
	 *
	 * <p>构造函数</p>
	 */
	public GreederSqliteConfigureUtil() {
		
	}

	/**
	 * <p>main</p>
	 *
	 * <p>程序入口</p>
	 * @param args
	 * @throws IOException 
	 * @throws SQLException 
	 */
	public static void main(String[] args) throws SQLException, IOException {
		writeConfigFile("greeder_sqlite_config.txt",classes);
	}

}
