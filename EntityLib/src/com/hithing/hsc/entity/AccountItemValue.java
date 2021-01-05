/**========================================
 * File:	AccountItemValue.java
 * Package:	com.hithing.hsc.entity
 * Create:	by leo
 * Date:	2012-7-23:下午7:56:31
 **======================================*/
package com.hithing.hsc.entity;

/**
 * <p>AccountItemValue</p>
 * 	
 * <p>功能描述</p>
 *
 * @author leo
 * 
 */
public final class AccountItemValue extends SearchableAdapterItem{
	//table:content;
	//index:payCode;
//	private String payCode;
	private String 	billCode;
//	private String table;
	private int		person;
	private String 	foundingTime;
	private String 	payBillTime;
	private String 	money;
	private String 	shift;

	
	public static class Builder{
		private String 	payCode;
		private String 	billCode;
		private String 	table;
		private int 	person;
		private String 	foundingTime;
		private String 	payBillTime;
		private String 	money;
		private String 	shift;
		
		public Builder(String payCode, String table){
			this.payCode 	= payCode;
			this.table		= table;
		}
		
//		public Builder setPayCode(String value){
//			this.payCode = value;
//			return this;
//		}
		
		public Builder setBillCode(String value){
			this.billCode = value;
			return this;
		}
		
//		public Builder setTable(String value){
//			this.table = value;
//			return this;
//		}
		
		public Builder setPerson(int value){
			this.person = value;
			return this;
		}
		
		public Builder setFoundingTime(String value){
			this.foundingTime = value;
			return this;
		}
		
		public Builder setPayBillTime(String value){
			this.payBillTime = value;
			return this;
		}
		
		public Builder setMoney(String value){
			this.money = value;
			return this;
		}
		
		public Builder setShift(String value){
			this.shift = value;
			return this;
		}
		
		public AccountItemValue build(){
			return new AccountItemValue(this);
		}
	}
	/**
	 * <p>AccountItemValue</p>
	 *
	 * <p>构造函数</p>
	 */
	public AccountItemValue(Builder builder) {
		super(builder.table, builder.payCode);
		this.billCode 		= builder.billCode;
		this.person			= builder.person;
		this.foundingTime	= builder.foundingTime;
		this.payBillTime	= builder.payBillTime;
		this.money			= builder.money;
		this.shift			= builder.shift;
	}
		
	
	public String getBillCode(){
		return billCode;
	}	
	
	public int getPerson(){
		return person;
	}
	
	public String getFoundingTime(){
		return foundingTime;
	}
	
	public String getPayBillTime(){
		return payBillTime;
	}
	
	public String getMoney(){
		return money;
	}
	
	public String getShift(){
		return shift;
	}


	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {		
		return "0";
	}

}
