/**========================================
 * File:	OperateReasonEntity.java
 * Package:	com.hithing.hsc.entity
 * Create:	by Leopard
 * Date:	2012-2-7:下午3:24:24
 **======================================*/
package com.hithing.hsc.dataentity;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

/**
 * <p>OperateReasonEntity</p>
 * 	
 * <p>操作原因实体类,映射数据库中的数据表tb_opeReason</p>
 *
 * @author Leopard
 * 
 */
@DatabaseTable(tableName = "tb_opeReason")
public final class OperateReasonEntity extends BaseEntity {
	public final static String OPERATETYPE_FIELD_NAME = "or_type";
	
	@DatabaseField(columnName = "pk_or_id", id = true)
	private int 	id;									//操作原因标识
	
	@DatabaseField(columnName = "or_title", width = 20, canBeNull = false)
	private String 	title;								//操作原因标题
	
	@DatabaseField(columnName = "or_type",	canBeNull = false)
	private OperateType type;							//操作原因类型:1-赠送,2-取消
	
	public enum OperateType{
		present(1), cancel(2);
		
		private int value;
		OperateType(int value) { this.value = value; }
		
		public static OperateType valueOf(int value){
			switch(value){
			case 1:
				return present;
			case 2:
				return cancel;
			default:
				return null;
			}
		}
	};

	/**
	 * <p>OperateReasonEntity</p>
	 *
	 * <p>构造函数</p>
	 */
	public OperateReasonEntity() {
		// TODO Auto-generated constructor stub
	}
	
	public void setId(int id){
		this.id = id;
	}
	
	public void setTitle(String title){
		this.title = title;
	}
	
	public void setType(OperateType type){
		this.type = type;
	}
	
	public int getId(){
		return id;
	}
	
	public String getTitle(){
		return title;
	}

	public OperateType getType(){
		return type;
	}
}
