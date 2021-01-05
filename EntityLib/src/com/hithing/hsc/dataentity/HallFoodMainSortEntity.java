/**========================================
 * File:	HallFoodMainSortEntity.java
 * Package:	com.hithing.hsc.entity
 * Create:	by Leopard
 * Date:	2011-12-28:下午04:08:25
 **======================================*/
package com.hithing.hsc.dataentity;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

/**
 * <p>HallFoodMainSortEntity</p>
 * 	
 * <p>分厅菜品大类关联实体类,映射数据库中的表tb_hallFms</p>
 *
 * @author Leopard
 * 
 */
@DatabaseTable(tableName = "tb_hallFms")
public final class HallFoodMainSortEntity extends BaseEntity {
	
	public static final String HALLID_FIELD_NAME = "fk_hall_id";
	
	@DatabaseField(columnName = "pk_hfms_id" , id = true )
	private int id;
	
	@DatabaseField(columnName = "fk_hall_id" , foreign = true , canBeNull = true)
	private HallEntity hall;
	
	@DatabaseField(columnName = "fk_fms_id"  , foreign = true , canBeNull = true)
	private FoodMainSortEntity foodMainSort;

	/**
	 * <p>HallFoodMainSortEntity</p>
	 *
	 * <p>构造函数</p>
	 */
	public HallFoodMainSortEntity() {
		// TODO Auto-generated constructor stub
	}
	
	public void setId(int id){
		this.id = id;
	}
	
	public void setHall(HallEntity hall){
		this.hall = hall;
	}
	
	public void setFoodMainSort(FoodMainSortEntity foodMainSort){
		this.foodMainSort = foodMainSort;
	}

	public FoodMainSortEntity getFoodMainSort(){
		return foodMainSort;
	}
}
