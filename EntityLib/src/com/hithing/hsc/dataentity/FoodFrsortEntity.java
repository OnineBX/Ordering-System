/**========================================
 * File:	FoodFrsortEntity.java
 * Package:	com.hithing.hsc.entity
 * Create:	by Leopard
 * Date:	2012-1-10:下午03:43:11
 **======================================*/
package com.hithing.hsc.dataentity;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

/**
 * <p>FoodFrsortEntity</p>
 * 	
 * <p>菜品与菜品做法关联数据表,映射数据库中的数据表tb_foodFrsort</p>
 *
 * @author Leopard
 * 
 */
@DatabaseTable(tableName = "tb_foodFrsort")
public final class FoodFrsortEntity extends BaseEntity {
	public static final String FOODID_FIELD_NAME = "fk_food_id";
	
	@DatabaseField(columnName = "pk_ffrs_id", id = true)
	private int 					id;						//菜品与菜品做法关联标识
	
	@DatabaseField(columnName = "fk_food_id", foreign = true, canBeNull = false)
	private FoodEntity 				food;
	
	@DatabaseField(columnName = "fk_frs_id",  foreign = true, canBeNull = false)
	private FoodRecipeSortEntity 	recipeSort;
	/**
	 * <p>FoodFrsortEntity</p>
	 *
	 * <p>构造函数</p>
	 */
	public FoodFrsortEntity() {
		// TODO Auto-generated constructor stub
	}
	
	public void setId(int id){
		this.id = id;
	}
	
	public void setFood(FoodEntity food){
		this.food = food;
	}
	
	public void setRecipeSort(FoodRecipeSortEntity recipeSort){
		this.recipeSort = recipeSort;
	}
	
	public FoodRecipeSortEntity getFoodRecipeSort(){
		return recipeSort;
	}

}
