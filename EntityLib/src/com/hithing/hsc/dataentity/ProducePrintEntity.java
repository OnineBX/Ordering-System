/**========================================
 * File:	ProducePrintEntity.java
 * Package:	com.hithing.hsc.entity
 * Create:	by Leopard
 * Date:	2012-2-7:下午5:39:43
 **======================================*/
package com.hithing.hsc.dataentity;

import android.text.TextUtils.TruncateAt;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

/**
 * <p>ProducePrintEntity</p>
 * 	
 * <p>出品部门打印档口关联实体类,映射数据库中的数据表tb_proPrint</p>
 *
 * @author Leopard
 * 
 */
@DatabaseTable(tableName = "tb_proPrint")
public final class ProducePrintEntity extends BaseEntity {
	public final static String PRODUCE_FIELD_NAME = "pp_produce";
	
	@DatabaseField(columnName = "pk_pp_id", id = true)
	private int id;								//出品部门和打印档口关联标识
	
	@DatabaseField(columnName = "pp_produce", canBeNull = false)
	private int produce;						//出品部门标识
	
	@DatabaseField(columnName = "pp_print", canBeNull = false)
	private int print;							//打印档口标识

	/**
	 * <p>ProducePrintEntity</p>
	 *
	 * <p>构造函数</p>
	 */
	public ProducePrintEntity() {
		// TODO Auto-generated constructor stub
	}
	
	public void setId(int id){
		this.id = id;
	}
	
	public void setProduce(int produce){
		this.produce = produce;
	}
	
	public void setPrint(int print){
		this.print = print;
	}
	
	public int getPrint(){
		return print;
	}

}
