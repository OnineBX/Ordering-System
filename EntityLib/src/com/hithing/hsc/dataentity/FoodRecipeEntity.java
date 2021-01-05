/**========================================
 * File:	FoodRecipeEntity.java
 * Package:	com.hithing.hsc.entity
 * Create:	by Leopard
 * Date:	2011-12-31:下午04:14:24
 **======================================*/
package com.hithing.hsc.dataentity;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

/**
 * <p>FoodRecipeEntity</p>
 * 	
 * <p>菜品做法实体类,映射数据库中的数据表tb_foodRecipe</p>
 *
 * @author Leopard
 * 
 */

@DatabaseTable(tableName = "tb_foodRecipe")
public final class FoodRecipeEntity extends BaseEntity {
	public final static String RECIPESORT_FIELD_NAME = "fk_frs_id";

	@DatabaseField(columnName = "pk_fr_id" , id = true)
	private int 					id;							//菜品做法标识
	
	@DatabaseField(columnName = "fr_name" ,  width = 20 , canBeNull = true)
	private String 					name;						//菜品做法名称
	
	@DatabaseField(columnName = "fk_frs_id" , foreign = true , canBeNull = false)
	private FoodRecipeSortEntity 	sort;						//菜品做法类别
	
	@DatabaseField(columnName = "frt_markup", defaultValue="0.00")
	private String					markup;						//做法加价		
	
	/**
	 * <p>FoodRecipeEntity</p>
	 *
	 * <p>构造函数</p>
	 */
	public FoodRecipeEntity() {
		// TODO Auto-generated constructor stub
	}

	public void setId(int id){
		this.id = id;
	}
	
	public void setName(String name){
		this.name = name;
	}
	
	public void setSort(FoodRecipeSortEntity sort){
		this.sort = sort;
	}
	
	public void setMarkup(String markup){
		this.markup = markup;
	}
	
	public int getId(){
		return id;		
	}
	
	public String getName(){
		return name;
	}
}
