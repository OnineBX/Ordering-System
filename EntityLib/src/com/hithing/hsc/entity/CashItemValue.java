/**========================================
 * File:	CashItemValue.java
 * Package:	com.hithing.hsc.entity
 * Create:	by Leopard
 * Date:	2012-3-16:下午2:51:35
 **======================================*/
package com.hithing.hsc.entity;

/**
 * <p>CashItemValue</p>
 * 	
 * <p>收银条目实体类</p>
 *
 * @author Leopard
 * 
 */
public final class CashItemValue extends SearchableAdapterItem{

	private int tableId;
	private String tableCode;
	/**
	 * <p>CashItemValue</p>
	 *
	 * <p>构造函数</p>
	 */
	public CashItemValue(int tableId, String tableCode, String tableName, String index) {
		super(tableName, index);
		this.tableId = tableId;
		this.tableCode = tableCode;
	}
	
	public int getTableId(){
		return tableId;
	}
	
	public String getTableCode(){
		return tableCode;
	}
	
	public String getTableName(){
		return getContent();
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {		
		return String.valueOf(tableId);
	}

	
}
