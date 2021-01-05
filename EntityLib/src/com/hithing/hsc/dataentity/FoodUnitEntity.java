/**========================================
 * File:	FoodUnitEntity.java
 * Package:	com.hithing.hsc.entity
 * Create:	by Leopard
 * Date:	2011-12-27:下午01:34:00
 **======================================*/
package com.hithing.hsc.dataentity;

import java.sql.SQLException;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

/**
 * <p>FoodUnitEntity</p>
 * 	
 * <p>菜品单位实体类,映射数据库中的数据表tb_foodUnit</p>
 *
 * @author Leopard
 * 
 */

@DatabaseTable(tableName = "tb_foodUnit")
public class FoodUnitEntity extends BaseEntity {
	
	@DatabaseField(columnName = "pk_fu_id" , id = true)
	private int id;
	
	@DatabaseField(columnName = "fu_name" ,  width = 10 , canBeNull = true , unique = true)
	private String 	name;
	
	/**
	 * <p>FoodUnitEntity</p>
	 *
	 * <p>构造函数</p>
	 */
	public FoodUnitEntity() {
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
		try {
			this.refresh();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return this.name;
	}
}
