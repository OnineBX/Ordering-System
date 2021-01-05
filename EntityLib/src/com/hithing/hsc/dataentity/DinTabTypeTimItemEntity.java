/**========================================
 * File:	DinTabTypeTimItemEntity.java
 * Package:	com.hithing.hsc.dataentity
 * Create:	by Leopard
 * Date:	2012-5-23:下午3:03:39
 **======================================*/
package com.hithing.hsc.dataentity;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

/**
 * <p>DinTabTypeTimItemEntity</p>
 * 	
 * <p>餐台类型-计时项目关联实体,对应数据库表tb_dttypeTitem</p>
 *
 * @author Leopard
 * 
 */
@DatabaseTable(tableName = "tb_dttypeTitem")
public final class DinTabTypeTimItemEntity extends BaseEntity {	
	
	
	@DatabaseField(columnName = "pk_dttti_id", id = true)
	private int 					id;					//关联标识
	
	@DatabaseField(columnName = "fk_dtt_id", 	foreign = true, canBeNull = false)
	private DinnerTableTypeEntity 	type;				//餐台类型
	
	@DatabaseField(columnName = "fk_ti_id",		foreign = true, canBeNull = false)
	private TimeItemEntity 			item;				//计时项目
	
	/**
	 * <p>DinTabTypeTimItemEntity</p>
	 *
	 * <p>构造函数</p>
	 */
	public DinTabTypeTimItemEntity() {
		// TODO Auto-generated constructor stub
	}

	public void setId(int id){
		this.id = id;
	}
	
	public void setDinnerTableType(DinnerTableTypeEntity type){
		this.type = type;
	}
	
	public void setTimeItem(TimeItemEntity item){
		this.item = item;
	}
	
	public TimeItemEntity getTimeItem(){
		return item;
	}
}
