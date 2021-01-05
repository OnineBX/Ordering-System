/**========================================
 * File:	HallSortEntity.java
 * Package:	com.hithing.hsc.entity
 * Create:	by Leopard
 * Date:	2011-11-28:下午03:15:01
 **======================================*/
package com.hithing.hsc.dataentity;

import android.R.integer;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

/**
 * <p>HallSortEntity</p>	
 * 	
 * <p>分厅大类数据实体类,映射数据库中的分厅大类表tb_hallSort</p>	
 *
 *@author Leopard
 * 
 */
@DatabaseTable(tableName = "tb_hallSort")
public final class HallSortEntity extends BaseEntity {

	@DatabaseField(columnName = "pk_hs_id",		id = true)
	private int 	id;							//分厅大类标识
	
	@DatabaseField(columnName = "hs_name",		canBeNull = false)
	private String 	name;						//分厅大类名称
	
	/**
	 * <p>HallSortEntity()</p>
	 * 
	 * 构造函数
	 */
	public HallSortEntity() {
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
