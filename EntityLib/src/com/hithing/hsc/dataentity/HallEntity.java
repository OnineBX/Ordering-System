/**========================================
 * File:	HallEntity.java
 * Package:	com.hithing.hsc.entity
 * Create:	by Leopard
 * Date:	2011-11-28:下午03:41:57
 **======================================*/
package com.hithing.hsc.dataentity;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

/**
 * <p>HallEntity</p>
 * 	
 * 分厅数据实体类,映射数据库中的分厅表tb_hall
 *
 * @author Leopard
 * 
 */

@DatabaseTable(tableName = "tb_hall")
public final class HallEntity extends BaseEntity {

	@DatabaseField(columnName = "pk_hall_id",	id = true)
	private int 			id;					//分厅标识
	
	@DatabaseField(columnName = "hall_code",	width = 8,		canBeNull = false,		unique = true)
	private String 			code;				//分厅编码
	
	@DatabaseField(columnName = "hall_name",	width = 20,		canBeNull = false)
	private String 			name;				//分厅名称
	
	@DatabaseField(columnName = "fk_hs_id",		foreign = true,	canBeNull = false)
	private HallSortEntity 	sort;				//所属分厅大类
	
	/**
	 * <p>HallEntity</p>
	 *
	 * 构造函数
	 */
	public HallEntity() {
		// TODO Auto-generated constructor stub
	}

	public void setId(int id){
		this.id = id;
	}		
	
	public void setCode(String code){
		this.code = code;
	}
	
	public void setName(String name){
		this.name = name;
	}
	
	public void setSort(HallSortEntity sort){
		this.sort = sort;
	}
	
	public int getId(){
		return id;
	}
	
	public String getName(){
		return name;
	}
	
}
