/**========================================
 * File:	DemandEntity.java
 * Package:	com.hithing.hsc.entity
 * Create:	by Leopard
 * Date:	2011-12-31:下午04:19:42
 **======================================*/
package com.hithing.hsc.dataentity;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

/**
 * <p>DemandEntity</p>
 * 	
 * <p>菜品传菜要求实体类,映射数据库中的数据表tb_foodDemand</p>
 *
 * @author Leopard
 * 
 */
@DatabaseTable(tableName = "tb_foodDemand")
public class DemandEntity extends BaseEntity {
	public final static String DEMAND_TYPE_FIELD_NAME = "dmd_type";
	
	@DatabaseField(columnName = "pk_dmd_id" , id = true)
	private int 	id;								//菜品传菜要求标识
	
	@DatabaseField(columnName = "dmd_name" ,  width = 20 , unique = true , canBeNull = true)
	private String 	name;							//菜品传菜要求名称
	
	@DatabaseField(columnName = "dmd_type" ,  canBeNull = false)
	private int 	type;							//菜品传菜类型:1-菜品传菜要求;2-菜品订单传菜要求
	/**
	 * <p>DemandEntity</p>
	 *
	 * <p>构造函数</p>
	 */
	public DemandEntity() {
		// TODO Auto-generated constructor stub
	}

	public void setId(int id){
		this.id = id;
	}
	
	public void setName(String name){
		this.name = name;
	}
	
	public void setType(int type){
		this.type = type;
	}
	
	public int getId(){
		return id;
	}
	
	public String getName(){
		return name;
	}
}
