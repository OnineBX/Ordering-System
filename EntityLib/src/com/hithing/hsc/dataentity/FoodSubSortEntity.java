/**========================================
 * File:	FoodSubSortEntity.java
 * Package:	com.hithing.hsc.entity
 * Create:	by Leopard
 * Date:	2011-12-25:下午2:32:55
 **======================================*/
package com.hithing.hsc.dataentity;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

/**
 * <p>FoodSubSortEntity</p>
 * 	
 * <p>菜品小类实体类,映射数据库中的数据表tb_foodSubSort</p>
 *
 * @author Leopard
 * 
 */
@DatabaseTable(tableName = "tb_foodSubSort")
public final class FoodSubSortEntity extends BaseEntity {

	public final static String FOODMAINSORT_FIELD_NAME = "fk_fms_id";
	
	@DatabaseField(columnName = "pk_fss_id" , id = true)
	private int 				id;								//菜品小类标识
	
	@DatabaseField(columnName = "fss_name" ,  width = 20 , unique = true , canBeNull = true)
	private String 				name;							//菜品小类名称
	
	@DatabaseField(columnName = "fk_fms_id" , foreign = true , canBeNull = false)
	private FoodMainSortEntity 	mainSort;						//菜品主类别
	
	@DatabaseField(columnName = "fss_produce" , canBeNull = false)
	private int					produce;						//出品部门标识
	/**
	 * <p>FoodSubSortEntity</p>
	 *
	 * <p>构造函数</p>
	 */
	public FoodSubSortEntity() {
		// TODO Auto-generated constructor stub
	}
	
	public void setId(int id){
		this.id = id;
	}
	
	public void setName(String name){
		this.name = name;
	}

	public void setFoodMainSort(FoodMainSortEntity mainSort){
		this.mainSort = mainSort;
	}
	
	public void setProduce(int produce){
		this.produce = produce;
	}
	
	public int getId(){
		return this.id;
	}
	
	public String getName(){
		return this.name;
	}
	
	public FoodMainSortEntity getFoodMainSort(){
		return this.mainSort;
	}
	
	public int getProduce(){
		return this.produce;
	}
}
