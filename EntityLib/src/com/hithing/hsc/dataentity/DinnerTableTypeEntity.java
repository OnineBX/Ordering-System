/**========================================
 * File:	DinnerTableTypeEntity.java
 * Package:	com.hithing.hsc.entity
 * Create:	by Leopard
 * Date:	2011-11-28:下午05:15:42
 **======================================*/
package com.hithing.hsc.dataentity;

import java.util.Collection;
import java.util.List;

import com.j256.ormlite.dao.ForeignCollection;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.field.ForeignCollectionField;
import com.j256.ormlite.table.DatabaseTable;

/**
 * <p>DinnerTableTypeEntity</p>
 * 	
 * <p>餐台类型实体类,映射数据库中的餐台类型表tb_dinTabType</p>
 *
 * @author Leopard
 * 
 */

@DatabaseTable(tableName = "tb_dinTabType")
public final class DinnerTableTypeEntity extends BaseEntity {
	
	public static final String HALL_FIELD_NAME 	= "fk_hall_id";
	public static final String ID_FIELD_NAME	= "pk_dtt_id";
	
	@DatabaseField(columnName = "pk_dtt_id",	id = true)
	private int									id;						//餐台类型标识
	
	@DatabaseField(columnName = "dtt_code",		width = 8,		canBeNull = false,		unique = true)
	private String 								code;					//餐台类型编码
	
	@DatabaseField(columnName = "dtt_name",		width = 20,		canBeNull = false)
	private String 								name;					//餐台类型名称
	
	@DatabaseField(columnName = "fk_hall_id",	foreign = true,	canBeNull = false)
	private HallEntity 							hall;					//餐台类型所属分厅
	
	@ForeignCollectionField(eager = false)
	private ForeignCollection<DinTabTypeTimItemEntity> timeItmes;		//相关的计时项目集合
	
	/**
	 * <p>DinnerTableTypeEntity</p>
	 *
	 * <p>构造函数</p>
	 */
	public DinnerTableTypeEntity() {
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
	
	public void setHall(HallEntity hall){
		this.hall = hall;
	}
	
	public int getId(){
		return id;
	}
	
	public String getName(){
		return name;
	}
	
	public ForeignCollection<DinTabTypeTimItemEntity> getTimeItems(){
		return timeItmes;
	}
	
}
