/**========================================
 * File:	TimeItemEntity.java
 * Package:	com.hithing.hsc.dataentity
 * Create:	by Leopard
 * Date:	2012-5-23:上午10:21:46
 **======================================*/
package com.hithing.hsc.dataentity;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

/**
 * <p>TimeItemEntity</p>
 * 	
 * <p>计时项目实体类,映射数据库中的表tb_timeItem</p>
 *
 * @author Leopard
 * 
 */

@DatabaseTable(tableName = "tb_timeItem")
public final class TimeItemEntity extends BaseEntity {

	/**
	 * <p>TimeItemEntity</p>
	 *
	 * <p>构造函数</p>
	 */
	public TimeItemEntity() {
		// TODO Auto-generated constructor stub
	}
	
	@DatabaseField(columnName = "pk_ti_id", id = true)
	private int 			id;									//计时项目标识

	@DatabaseField(columnName = "ti_name", 	width = 20, canBeNull = false, unique = true)
	private String 			name;								//计时项目名称
	
	@DatabaseField(columnName = "ti_price", width = 10, canBeNull = false)
	private String 			price;								//单价
	
	@DatabaseField(columnName = "ti_unit_time", width = 10, canBeNull = false)
	private String 			unitTime;							//单位时长
	
	@DatabaseField(columnName = "ti_unit", width = 10, canBeNull = false)
	private TimeItemUnit	unit;								//计时单位
	
	public void setId(int id){
		this.id = id;
	}
	
	public void setName(String name){
		this.name = name;
	}
	
	public void setPrice(String price){
		this.price = price;
	}
	
	public void setUnitTime(String unitTime){
		this.unitTime = unitTime;
	}
	
	public void setUnit(TimeItemUnit unit){
		this.unit = unit;
	}
	/**
	 * <p>TimeUnit</p>
	 * 	
	 * <p>计时项目时间单位枚举</p>
	 *
	 * @author Leopard
	 *
	 */
	public enum TimeItemUnit {
		HOUR(1), MINUTE(2);
		
		private int value;								//时间单位值		
		
		private TimeItemUnit(int value) { this.value = value; }		
		public int getValue(){ return value; }		
		public static TimeItemUnit valueOf(int value){
			switch (value) {
			case 1:				
				return HOUR;
			case 2:
				return MINUTE;
			default:
				return null;
			}			
		}
		
	}
}
