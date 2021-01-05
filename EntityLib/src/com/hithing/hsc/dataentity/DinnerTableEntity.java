/**====================================
 * File:	DinnerTableEntity.java
 * Package:	com.hithing.hsc.entity
 * Create:	by Leopard
 * Date:	2011-11-28:下午02:33:53
 * ==================================*/
package com.hithing.hsc.dataentity;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

/**
 * <p>DinnerTableEntity</p>
 * 	
 * <p>餐台实体类,映射数据库中的数据表tb_dinTable</p>
 *
 * @author Leopard
 * 
 */

@DatabaseTable(tableName = "tb_dinTable")
public final class DinnerTableEntity extends BaseEntity {
	
	public static final String DINNERTABLETYPE_FIELD_NAME = "fk_dtt_id";	
	
	@DatabaseField(columnName = "pk_dt_id",		id = true)
	private int 					id;										//餐台标识
	
	@DatabaseField(columnName = "dt_code",		unique = true)
	private String					code;									//餐台编码
	
	@DatabaseField(columnName = "dt_name")
	private String 					name;									//餐台名称
	
	@DatabaseField(columnName = "dt_index",		canBeNull = false)
	private String					index;									//餐台索引
	
	@DatabaseField(columnName = "fk_dtt_id",	foreign = true,		canBeNull = false)
	private DinnerTableTypeEntity 	type;									//餐台类型
	
	/**
	 * <p>DinnerTableEntity</p>
	 *
	 * <p>构造函数</p>
	 */
	public DinnerTableEntity() {
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
	
	public void setIndex(String index){
		this.index = index;
	}
	
	public void setType(DinnerTableTypeEntity type){
		this.type = type;
	}
	
	public int getId(){
		return id;
	}
	
	public String getCode(){
		return code;
	}
	
	public String getName(){
		return name;
	}
	
	public String getIndex(){
		return index;
	}
	
	public DinnerTableTypeEntity getType(){
		return type;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {		
		return name;
	}
	
	
}
