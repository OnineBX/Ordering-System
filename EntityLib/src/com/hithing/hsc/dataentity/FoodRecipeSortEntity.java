/**========================================
 * File:	FoodRecipeSortEntity.java
 * Package:	com.hithing.hsc.entity
 * Create:	by Leopard
 * Date:	2012-1-10:下午03:30:38
 **======================================*/
package com.hithing.hsc.dataentity;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

/**
 * <p>FoodRecipeSortEntity</p>
 * 	
 * <p>菜品做法类别实体类,映射数据库中的数据表tb_foodRecSort</p>
 *
 * @author Leopard
 * 
 */
@DatabaseTable(tableName = "tb_foodRecSort")
public final class FoodRecipeSortEntity extends BaseEntity {

	@DatabaseField(columnName = "pk_frs_id", id = true)
	private int 	id;							//菜品做法类别标识
	
	@DatabaseField(columnName = "frs_name",  width = 20, canBeNull = false)
	private String 	name;						//菜品做法类别名称
	/**
	 * <p>FoodRecipeSortEntity</p>
	 *
	 * <p>构造函数</p>
	 */
	public FoodRecipeSortEntity() {
		// TODO Auto-generated constructor stub
	}
	
	public void setId(int id){
		this.id = id;
	}

	public void setName(String name){
		this.name = name;
	}
	
	public int getId(){
		return id;
	}
}
