/**========================================
 * File:	FoodPrice.java
 * Package:	com.hithing.hsc.entity
 * Create:	by Leopard
 * Date:	2011-11-28:下午06:04:36
 **======================================*/
package com.hithing.hsc.dataentity;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

/**
 * <p>FoodPrice</p>
 * 	
 * <p>食品单价数据实体类,映射数据库中的表tb_foodPrice</p>
 *
 * @author Leopard
 * 
 */

@DatabaseTable(tableName = "tb_foodPrice")
public final class FoodPriceEntity extends BaseEntity {
	
	public final static String FOODID_FIELD_NAME = "fk_food_id";

	@DatabaseField(columnName = "pk_fp_id",		id = true)
	private int				id;							//菜品单价标识
	
	@DatabaseField(columnName = "fk_fu_id",	  	foreign = true,	canBeNull = false)
	private FoodUnitEntity 	unit;						//计价单位
	
	@DatabaseField(columnName = "fp_price",	  	width = 20,		defaultValue = "0.0")
	private String 			price;						//价格
	
	@DatabaseField(columnName = "fk_food_id", 	foreign = true, canBeNull = false)
	private FoodEntity		food;						//菜品
	
	/**
	 * <p>FoodPrice</p>
	 *
	 * <p>构造函数</p>
	 */
	public FoodPriceEntity() {
		// TODO Auto-generated constructor stub
	}
	
	public void setId(int id){
		this.id = id;
	}
	
	public void setUnit(FoodUnitEntity unit){
		this.unit = unit;
	}
	
	public void setPrice(String price){
		this.price = price;
	}
	
	public void setFood(FoodEntity food){
		this.food = food;
	}
	
	public int getId(){
		return this.id;
	}
	
	public String getPrice(){
		return this.price;
	}
	
	public FoodUnitEntity getUnit(){
		return this.unit;
	}

	public FoodEntity getFood(){
		return this.food;
	}		
}
