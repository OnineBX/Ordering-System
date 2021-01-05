/**========================================
 * File:	WorkContext.java
 * Package:	com.hithing.hsc.app
 * Create:	by Leopard
 * Date:	2011-12-7:下午05:14:16
 **======================================*/
package com.hithing.hsc.bll.util;

import java.util.Observable;


/**
 * <p>WorkContext</p>
 * 	
 * <p>工作环境上下文</p>
 *
 * 用于保存系统运行时所需的全局参数
 *
 * @author Leopard
 * 
 */
public class WorkContext extends Observable{
	private static final WorkContext INSTANCE = new WorkContext();
	private int 			userId;					//当前登录用户标识
	private String 			compCode;				//当前企业编码
	private String 			termCode;				//当前终端号
	private CompoundItem 	hall;					//当前分厅	
	
	/**
	 * <p>getInstance</p>
	 *
	 * <p>得到类唯一实例</p>
	 * 
	 * @return
	 */
	public static WorkContext getInstance(){
		return INSTANCE;
	}		
	
	//设置用户因在登录时确定,无法更改,故不做观察
	public void setUserId(int userId){
		this.userId = userId;
	}
	
	//设置后需重启生效
	public void setCompCode(String compCode){
		this.compCode = compCode;		
	}
	
	public void setTermCode(String termCode){
		this.termCode = termCode;
		setChanged();
		notifyObservers(UpdateType.TERMSEQ_INFO);
	}
	
	public void setHall(CompoundItem hall){
		this.hall.id 	= hall.id;
		this.hall.name 	= hall.name;
		setChanged();
		notifyObservers(UpdateType.HALL_INFO);
	}
	
	public int getUserId(){
		return userId;
	}
	
	public String getCompCode(){
		return compCode;
	}
	
	public String getTermCode(){
		return termCode;
	}
	
	public CompoundItem getHall(){
		return hall;
	}

	/**
	 * <p>WorkContext</p>
	 *
	 * <p>构造函数</p>
	 */
	private WorkContext() {
		hall = new CompoundItem();
	}	
	
	/**
	 * <p>UpdateType</p>
	 * 	
	 * <p>更新数据信息类型</p>
	 *
	 * @author Leopard
	 *
	 */
	public enum UpdateType{
		HALL_INFO,
		TERMSEQ_INFO;
	}
		
}
