/**========================================
 * File:	GreederSqliteHelper.java
 * Package:	com.hithing.hsc.dal.database
 * Create:	by Leopard
 * Date:	2011-12-3:上午10:56:18
 **======================================*/
package com.hithing.hsc.dal.database;

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

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.j256.ormlite.android.apptools.OrmLiteSqliteOpenHelper;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;

/**
 * <p>GreederSqliteHelper</p>
 * 	
 * <p>用于创建和更新支持Greeder运行的Android Sqlite本地数据库,提供其他类调用的DAO对象</p>
 *
 * @author Leopard
 * 
 */
public final class GreederSqliteHelper extends OrmLiteSqliteOpenHelper {
	
	private static final String DATABASE_NAME 		= "greeder.db";	//数据库名
	private static final int 	DATABASE_VERSION 	= 1;			//数据库版本
	private static final String CONFIGFILE_ID_NAME	= "greeder_sqlite_config";
	
	/**
	 * <p>GreederSqliteHelper</p>
	 *
	 * <p>构造函数</p>
	 * @param context 上下文
	 */
	public GreederSqliteHelper(Context context) {		
		super(context, DATABASE_NAME, null, DATABASE_VERSION, 
				context.getResources().getIdentifier(CONFIGFILE_ID_NAME, "raw", context.getPackageName()));		
		// TODO Auto-generated constructor stub
	}	

	/* (non-Javadoc)
	 * @see com.j256.ormlite.android.apptools.OrmLiteSqliteOpenHelper#onCreate(android.database.sqlite.SQLiteDatabase, com.j256.ormlite.support.ConnectionSource)
	 */
	@Override
	public void onCreate(SQLiteDatabase db, ConnectionSource connectionSource) {
		
		try {
			TableUtils.createTableIfNotExists(connectionSource, HallSortEntity.class);
			TableUtils.createTableIfNotExists(connectionSource, HallEntity.class);
			TableUtils.createTableIfNotExists(connectionSource, DinnerTableTypeEntity.class);
			TableUtils.createTableIfNotExists(connectionSource, DinnerTableEntity.class);
			TableUtils.createTableIfNotExists(connectionSource, FoodMainSortEntity.class);
			TableUtils.createTableIfNotExists(connectionSource, HallFoodMainSortEntity.class);
			TableUtils.createTableIfNotExists(connectionSource, FoodSubSortEntity.class);
			TableUtils.createTableIfNotExists(connectionSource, FoodUnitEntity.class);
			TableUtils.createTableIfNotExists(connectionSource, FoodEntity.class);
			TableUtils.createTableIfNotExists(connectionSource, FoodPriceEntity.class);
			TableUtils.createTableIfNotExists(connectionSource, FoodRecipeSortEntity.class);
			TableUtils.createTableIfNotExists(connectionSource, FoodRecipeEntity.class);
			TableUtils.createTableIfNotExists(connectionSource, FoodFrsortEntity.class);
			TableUtils.createTableIfNotExists(connectionSource, DemandEntity.class);
			TableUtils.createTableIfNotExists(connectionSource, OperateReasonEntity.class);
			TableUtils.createTableIfNotExists(connectionSource, ProducePrintEntity.class);
			TableUtils.createTableIfNotExists(connectionSource, TimeItemEntity.class);
			TableUtils.createTableIfNotExists(connectionSource, DinTabTypeTimItemEntity.class);
			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

	/* (non-Javadoc)
	 * @see com.j256.ormlite.android.apptools.OrmLiteSqliteOpenHelper#onUpgrade(android.database.sqlite.SQLiteDatabase, com.j256.ormlite.support.ConnectionSource, int, int)
	 */
	@Override
	public void onUpgrade(SQLiteDatabase db, ConnectionSource connectionSource, int oldVersion,
			int newVersion) {
		// TODO Auto-generated method stub				
	}
	
}
