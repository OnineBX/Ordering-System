/**========================================
 * File:	FoodEntity.java
 * Package:	com.hithing.hsc.entity
 * Create:	by Leopard
 * Date:	2011-11-28:下午05:40:28
 **======================================*/
package com.hithing.hsc.dataentity;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

/**
 * <p>FoodEntity</p>
 * 	
 * <p>菜品数据实体类,映射数据库中的表tb_food</p>
 *
 * @author Leopard
 * 
 */

@DatabaseTable(tableName = "tb_food")
public final class FoodEntity extends BaseEntity {

	public final static String FOODSUBSORT_FIELD_NAME = "fk_fss_id";
	public final static String FOODISSTOCK_FIELD_NAME = "food_is_stock";
	
	@DatabaseField(columnName = "pk_food_id",	id = true)
	private int					id;								//菜品标识
	
	@DatabaseField(columnName = "food_code",	width = 8,	canBeNull = false,	unique = true)
	private String 				code;							//菜品编码
	
	@DatabaseField(columnName = "food_name",	width = 20,	canBeNull = false)
	private String 				name;							//菜品名称
	
	@DatabaseField(columnName = "food_index",	canBeNull = false)
	private String				index;							//菜品索引
	
	@DatabaseField(columnName = "food_is_stock",canBeNull = false)
	private boolean				isStock;						//是否纳入库存管理
	
	@DatabaseField(columnName = "food_pho_name",defaultValue = "N/A")
	private String				photoName;						//菜品图片名
	
	@DatabaseField(columnName = "food_remark")
	private String				remark;
	
	@DatabaseField(columnName = "fk_fss_id",	foreign = true,	canBeNull = false)
	private FoodSubSortEntity 	subSort;						//所属菜品小类
	
	/**
	 * <p>FoodEntity</p>
	 *
	 * <p>构造函数</p>
	 */
	public FoodEntity() {
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
	
	public void setIsStock(boolean value){
		this.isStock = value;
	}
	
	public void setPhotoName(String value){
		this.photoName = value;
	}
	
	public void setRemark(String value){
		this.remark = value;
	}
	
	public void setFoodSubSort(FoodSubSortEntity subSort){
		this.subSort = subSort;
	}
	
	public int getId(){
		return this.id;
	}
	
	public String getName(){
		return this.name;
	}
	
	public String getIndex(){
		return this.index;
	}
	
	public boolean isStock(){
		return isStock;
	}
	
	public String getPhotoName(){
		return photoName;
	}
	
	public String getRemark(){
		return remark;
	}
	
	public FoodSubSortEntity getFoodSubSort(){
		return this.subSort;
	}
}
