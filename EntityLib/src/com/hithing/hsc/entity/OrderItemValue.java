/**========================================
 * File:	OrderItemValue.java
 * Package:	com.hithing.hsc.entity
 * Create:	by Leopard
 * Date:	2012-1-13:下午8:10:54
 **======================================*/
package com.hithing.hsc.entity;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map.Entry;

import android.text.TextUtils;
import android.util.Log;


/**
 * <p>OrderItem</p>
 * 	
 * <p>账单条目实体类</p>
 *
 * @author Leopard
 *
 */
public final class OrderItemValue  implements Cloneable, Serializable{						

	/**
	 * 
	 */
	private static final long serialVersionUID = -3687325751280174257L;
	private RemarkItem					food;				//菜品名称
	private String 						foodPrice;			//菜品价格
	private int 						count;				//菜品份数
	private String 						foodUnit;			//菜品单位					
	private String						realCost;			//菜品实收总价
	private String						applyCost;			//菜品应收总价
	private HashMap<Integer, String> 	recipe;				//菜品做法
	private RemarkItem					demand;				//菜品要求
	private boolean 					canDiscount;		//菜品是否可以打折
	private String 						ratioDiscount;		//菜品折扣率
	private String						moneyDiscount;		//菜品金额折扣
	private boolean						discounted;			//菜品是否设置了单道折扣
	private String						remark;				//菜品备注
	private String						description;		//菜品描述
	private int							produce;			//菜品出品档口
			
	public static class Builder{
		private RemarkItem					food;				//菜品名称
		private String 						foodPrice;			//菜品价格
		private int 						count;				//菜品份数
		private String 						foodUnit;			//菜品单位					
		private String						cost;				//菜品总价
		private HashMap<Integer, String> 	recipe;				//菜品做法
		private RemarkItem					demand;				//菜品要求
		private boolean 					canDiscount;		//菜品是否可以打折
		private String 						ratioDiscount;		//菜品折扣率
		private String						moneyDiscount;		//菜品金额折扣
		private boolean						isDiscount;			//菜品是否设置了单道折扣
		private String						remark;				//菜品备注
		private String						description;		//菜品描述
		private int							produce;			//菜品出品档口
		
		public Builder(){
			this.recipe 		= new HashMap<Integer, String>();
			this.count 			= 1;
			this.ratioDiscount	= CalculateConst.DECIMAL_ONE_STRING;
			this.moneyDiscount	= CalculateConst.DECIMAL_ZERO_STRING;
			this.canDiscount 	= true;	
			this.isDiscount		= false;
		}
	
		
		public Builder setFood(RemarkItem food){
			this.food = food;			
			return this;
		}
		
		public Builder setFoodPrice(String foodPrice){
			this.foodPrice = foodPrice;
			return this;
		}
		
		public Builder setCount(int count){
			this.count = count;
			return this;
		}
		
		public Builder setFoodUnit(String foodUnit){
			this.foodUnit = foodUnit;
			return this;
		}				
		
		public Builder setRemark(String remark){
			this.remark = remark;
			return this;
		}
		
//		public Builder setDescription(String description){
//			this.description = description;
//			return this;
//		}
		
		public Builder setProduce(int produce){
			this.produce = produce;
			return this;
		}
		
		public Builder setCanDiscount(boolean value){
			canDiscount = value;
			return this;
		}
		
		public Builder setRatioDiscount(String value){
			ratioDiscount 	= value;			
			return this;
		}
		
		public Builder setMoneyDiscount(String value){
			moneyDiscount = value;
			return this;
		}
		
		public Builder setIsDiscount(boolean value){
			isDiscount = value;
			return this;
		}
		
		public OrderItemValue build(){
			return new OrderItemValue(this);
		}
	}
	
	public OrderItemValue(Builder builder){
		this.food 			= builder.food;
		this.foodPrice 		= builder.foodPrice;			
		this.count 			= builder.count;
		this.foodUnit 		= builder.foodUnit;
//		this.realCost 		= builder.foodPrice;
		this.recipe 		= builder.recipe;		
		this.canDiscount 	= builder.canDiscount;
		this.ratioDiscount	= builder.ratioDiscount;
		this.moneyDiscount	= builder.moneyDiscount;
		this.discounted		= builder.isDiscount;
		this.produce		= builder.produce;
		this.remark			= builder.remark;		
		countApplyCost();
		countRealCost();
		description();
	}	
	
	
	
	/* (non-Javadoc)
	 * @see java.lang.Object#clone()
	 */
	@Override
	public Object clone() throws CloneNotSupportedException {											 
		OrderItemValue value = null;
		value = (OrderItemValue)super.clone();
		value.food = (RemarkItem) food.deepCopy();
		
		return value;
	}
	
	public void increment(int count){		
		this.count = this.count + count;
		countApplyCost();
		countRealCost();
	}
	
	public void setCount(int value){
		count = value;
		countApplyCost();
		countRealCost();
	}
	
	public void setFoodPrice(String value){
		foodPrice = value;
		countApplyCost();
		countRealCost();
	}
	
	public void setDemand(RemarkItem value){
		demand = value;
		description();
	}
	
	public void setRemark(String value){
		remark = value;		
		description();
	}
	
	public void setFoodUnit(String value){
		foodUnit = value;
	}
	
	public void setFoodRecipe(HashMap<Integer, String> value){
		recipe = value;
		description();
	}			
	
	public void setProduce(int value){
		this.produce = value;
	}
	
//	public void setCanDiscount(Boolean value){
//		canDiscount = value;
//	}
//	
	public void setRatioDiscount(String value){
		this.ratioDiscount = value;
		countRealCost();
	}
//	
	
	public void setMoneyDiscount(String value){
		this.moneyDiscount = value;
		countRealCost();
	}
	
	public void setDiscounted(boolean value){
		discounted = value;
	}
	
	/**
	 * <p>setRatioDiscount</p>
	 *
	 * <p>将菜品总价清零,用于赠送菜品的情况</p>
	 * 
	 * @param ratio
	 */
	public void resetCost(){
		realCost = "0.0";
	}
	
	public RemarkItem getFood(){
		return food;
	}
	
	public String getFoodPrice(){
		return foodPrice;
	}		
	
	public int getCount(){
		return count;
	}	
	
	public String getUnit(){
		return foodUnit;
	}
	
	public String getCountWithUnit(){						
		return String.format("%d%s", count,foodUnit);
	}
	
	public HashMap<Integer, String> getRecipe(){
		return recipe;
	}
	
	public RemarkItem getDemand(){
		return demand;
	}		
	
	public String getApplyCost(){
		return applyCost;
	}
	
	public String getRealCost(){
		return realCost;
	}
	
	public String getRemark(){
		return remark;
	}
		
	
	
	public String getDescription(){
		return description;
	}
	
	public int getProduce(){
		return produce;
	}
	
	public boolean canDiscount(){
		return canDiscount;
	}

	public String getRatioDiscount(){
//		BigDecimal result = new BigDecimal(ratioDiscount);
//		result = result.multiply(new BigDecimal("100")).setScale(0);
//		return String.format("%%%s", result.toString());
		return ratioDiscount;
	}

	public String getMoneyDiscount(){
		return moneyDiscount;
	}
	
	public boolean isDiscounted(){
		return discounted;
	}
	
	private void description(){
		StringBuilder descriptionBuilder = new StringBuilder();
		
		//添加做法内容
		if(recipe != null){
			for(Entry<Integer, String> entry : recipe.entrySet()){
				descriptionBuilder.append(entry.getValue());
				descriptionBuilder.append(",");
			}						
		}
		
		//添加要求内容
		if(demand != null){
			descriptionBuilder.append(demand.content);
			descriptionBuilder.append(",");
		}						
		
		//添加备注内容			
		if(!TextUtils.isEmpty(remark)){
						
			descriptionBuilder.append(remark);
		}else {
			//去掉末尾的','
			if(descriptionBuilder.length() != 0){
				descriptionBuilder = descriptionBuilder.deleteCharAt(descriptionBuilder.length() - 1);				
			}	
		}
		
		description = descriptionBuilder.toString();			
	}
	
	private void countApplyCost(){
		BigDecimal price 	= new BigDecimal(foodPrice);
		BigDecimal result 	= price.multiply(new BigDecimal(count));
		applyCost = result.toString();
	}
	
	private void countRealCost() {			
		BigDecimal price 	= new BigDecimal(foodPrice);
		BigDecimal result 	= price.multiply(new BigDecimal(count));		
		
		if(canDiscount){
			result = result.multiply(new BigDecimal(ratioDiscount)).subtract(new BigDecimal(moneyDiscount)).setScale(2);			
		}
		realCost = result.toString();					
	}
			
	
	/**
	 * <p>RemarkItem</p>
	 * 	
	 * <p>描述条目类,用于记录内容及其标识</p>
	 *
	 * @author Leopard
	 *
	 */
	public static class RemarkItem implements Serializable{			
		/**
		 * 
		 */
		private static final long serialVersionUID = 1268583860933530420L;
		public int 		id;			//描述标识
		public String 	content;	//描述内容
		
		public RemarkItem(){
			
		}
		
		public RemarkItem(int id, String content){
			this.id 		= id;
			this.content 	= content;
		}
		
		/**
		 * <p>deepCopy</p>
		 *
		 * <p>深拷贝</p>
		 * 
		 * @return 深拷贝生成的对象
		 */
		public Object deepCopy(){
			Object result = null;
			try {
				ByteArrayOutputStream bos = new ByteArrayOutputStream();
				ObjectOutputStream oos = new ObjectOutputStream(bos);
				oos.writeObject(this);  
				ByteArrayInputStream bis = new ByteArrayInputStream(bos.toByteArray());  
				ObjectInputStream ois = new ObjectInputStream(bis);  
				result = ois.readObject();  
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (ClassNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} 
			return result;
		}
	}
	
	/**
	 * <p>CalculateConst</p>
	 * 	
	 * <p>用于金额计算的常量</p>
	 *
	 * @author Leopard
	 *
	 */
	public static class CalculateConst{
		public final static int								DECIMAL_MONEY_SCALE = 2;
		public final static String							DECIMAL_ZERO_STRING = "0.00";				//用于表示价格0.00或比率0%
		public final static String							DECIMAL_ONE_STRING	= "1.00";				//用于表示比率100%
	}
}				

