/**========================================
 * File:	FoodMainSortEntity.java
 * Package:	com.hithing.hsc.entity
 * Create:	by Leopard
 * Date:	2011-12-25:下午1:59:54
 **======================================*/
package com.hithing.hsc.dataentity;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

/**
 * <p>FoodMainSortEntity</p>
 * 	
 * <p>菜品主类别实体类,映射数据库中的数据表tb_foodMainSort</p>
 *
 * @author Leopard
 * 
 */

@DatabaseTable(tableName = "tb_foodMainSort")
public final class FoodMainSortEntity extends BaseEntity {

	@DatabaseField(columnName = "pk_fms_id" , id = true)
	private int 	id;
	
	@DatabaseField(columnName = "fms_name" ,  width = 20 , canBeNull = true , unique = true)
	private String 	name;
	/**
	 * <p>FoodMainSortEntity</p>
	 *
	 * <p>构造函数</p>
	 */
	public FoodMainSortEntity() {
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
	
	public String getName(){
		return name;
	}

}
