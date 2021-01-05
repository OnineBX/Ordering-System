/**========================================
 * File:	PrintManager.java
 * Package:	com.hithing.hsc.bll.manage
 * Create:	by Leopard
 * Date:	2012-3-7:上午9:23:08
 **======================================*/
package com.hithing.hsc.bll.print;

import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import android.util.Log;

import com.hithing.hsc.bll.android.InstructionSet;
import com.hithing.hsc.dataentity.ProducePrintEntity;
import com.j256.ormlite.dao.Dao;

/**
 * <p>PrintManager</p>
 * 	
 * <p>打印档口管理类</p>
 *
 * @author Leopard
 * 
 */
public enum PrintManager {
	INSTANCE;
	
	private Dao<ProducePrintEntity, Integer> 	dao;
	private Map<Integer, PrintPort>				printPorts;								//打印档口信息集合,格式(打印档口标识,打印档口信息)
	private Map<Integer, PrintTemplate>			templates;								//打印模板集合
	
	/**
	 * <p>PrintManager</p>
	 *
	 * <p>构造函数</p>
	 * @param dao 出品部门-打印档口数据访问对象
	 * @param ports 打印档口集合
	 */
	PrintManager() {
		//在此初始化打印模板:后续可考虑将打印模板存入数据库
		
		//用模板为所有打印档口加载任务容器
		//加载指令集
		InstructionSet instructionSet = InstructionSet.INSTANCE;
		
		//定义公用部分-主内容		
		StringBuilder primary = new StringBuilder(instructionSet.PRINT_ENTER);		
		primary.append(instructionSet.PRINT_BIG_FONT);									//大字体
		primary.append("%s(%s)");														//1.%s为菜品名称;2.%s为菜品份数	
		primary.append(instructionSet.PRINT_ENTER);
		//定义公共部分-副内容
		StringBuilder additive = new StringBuilder(instructionSet.PRINT_LONG_SMALL_FONT);
//		additive.append(instructionSet.PRINT_LONG_SMALL_FONT);							//长小字体
		additive.append("%s");															//1.%s为菜品描述
		additive.append(instructionSet.PRINT_ENTER);
						
		//分单头
		StringBuilder header = new StringBuilder(instructionSet.PRINT_SHORT_SMALL_FONT);	//短小字体								
		header.append(instructionSet.PRINT_CENTER_ALIGN);									//左对齐
		header.append(instructionSet.PRINT_BIG_FONT);		
		header.append("%s");														//1.%s为餐台名称
		header.append(instructionSet.PRINT_ENTER);
		header.append(instructionSet.PRINT_SHORT_SMALL_FONT);
		header.append(instructionSet.PRINT_LEFT_ALIGN);
		header.append("   打单时间：%s");														//2.%s为打单时间
		header.append("服务员：%s");															//3.%s为服务员姓名						
		header.append(instructionSet.PRINT_ENTER);											//换行符
		header.append(instructionSet.PRINT_SPLIT_STRING);									//分隔符	
		
		//分单尾
		StringBuilder tail = new StringBuilder(instructionSet.PRINT_SHORT_SMALL_FONT);	//短小字体		
		tail.append(instructionSet.PRINT_SPLIT_STRING);									//分隔符
		tail.append(instructionSet.PRINT_ENTER);
		tail.append(instructionSet.PRINT_SHORT_BLANK);									//短留空
		tail.append(instructionSet.PRINT_CUT_PAPER);
		tail.append(instructionSet.PRINT_END);
		
		//定义厨房分单打印模板											
		PrintTemplate kitchenTemplate = new PrintTemplate(header.toString(),primary.toString(),additive.toString(),tail.toString());								
		
		//定义水吧分单打印模板						
		PrintTemplate barTemplate = new PrintTemplate(header.toString(),primary.toString(),additive.toString(),tail.toString());				
				
		//总单头
		StringBuilder totalHeader = new StringBuilder("\n\n");
		totalHeader.append(instructionSet.PRINT_CENTER_ALIGN);
		totalHeader.append(instructionSet.PRINT_BIG_FONT);						//大字体
		totalHeader.append("【%s】");												//1.%s为餐台名称
		totalHeader.append(instructionSet.PRINT_ENTER);
		totalHeader.append(instructionSet.PRINT_LONG_SMALL_FONT);				//小字体
		totalHeader.append("★★★%s★★★");	
		totalHeader.append(instructionSet.PRINT_ENTER);	
		totalHeader.append(instructionSet.PRINT_RIGHT_ALIGN);
		totalHeader.append("总单%6s下单");											//2.%s为总单名;3.%s为服务员姓名
		totalHeader.append(instructionSet.PRINT_ENTER);
		totalHeader.append(instructionSet.PRINT_LEFT_ALIGN);
		totalHeader.append(instructionSet.PRINT_SHORT_SMALL_FONT);
		totalHeader.append(instructionSet.PRINT_SPLIT_STRING);			
		
		//总单尾
		StringBuilder totalTail = new StringBuilder(instructionSet.PRINT_SHORT_SMALL_FONT);		
		totalTail.append(instructionSet.PRINT_SPLIT_STRING);				
		totalTail.append(instructionSet.PRINT_LONG_BLANK);
		totalTail.append(instructionSet.PRINT_CUT_PAPER);		
		totalTail.append(instructionSet.PRINT_END);
		
		//定义传菜总单打印模板:传菜单加入蜂鸣器
		PrintTemplate deliverTemplate = new PrintTemplate(instructionSet.PRINT_BUZZER + totalHeader.toString(), primary.toString(), additive.toString(), totalTail.toString());
		//定义收银总单打印模板
		PrintTemplate cashTemplate = new PrintTemplate(totalHeader.toString(), primary.toString(), additive.toString(), totalTail.toString());
		
		templates = new HashMap<Integer, PrintManager.PrintTemplate>();
		templates.put(1, kitchenTemplate);
		templates.put(2, barTemplate);
		templates.put(3, cashTemplate);
		templates.put(4, deliverTemplate);		
	}
	
	/**
	 * <p>initInstance</p>
	 *
	 * <p>初始化打印管理者实例</p>
	 * 
	 * @param dao
	 * @param ports
	 */
	public void initInstance(Dao<ProducePrintEntity, Integer> dao, Map<Integer, PrintPort> ports){
		this.dao 		= dao;
		this.printPorts = ports;		
	}
	
	/**
	 * <p>getPrintPort</p>
	 *
	 * <p>根据菜品出品部门得到打印档口</p>
	 * 
	 * @param produce
	 * @return 与出品部门对应的打印档口集合
	 */
	public List<ProducePrintEntity> getPrintPorts(int produce){
		try {			
			return dao.queryForEq(ProducePrintEntity.PRODUCE_FIELD_NAME, produce);
		} catch (SQLException e) {			
			e.printStackTrace();
			return new LinkedList<ProducePrintEntity>();
		}
	}
	
	/**
	 * <p>getPrintPort</p>
	 *
	 * <p>根据打印档口标识得到打印档口</p>
	 * 
	 * @param portId
	 * @return
	 */
	public PrintPort getPrintPort(int portId){		
		return printPorts.get(portId);
	}		
	
	/**
	 * <p>getTemplate</p>
	 *
	 * <p>根据打印档口标识得到打印模板</p>
	 * 
	 * @param portId 打印档口标识
	 * @return 打印模板
	 */
	public PrintTemplate getTemplate(int portId){
		return templates.get(portId);
	}
	
	/**
	 * <p>PrintPort</p>
	 * 	
	 * <p>打印档口</p>
	 *
	 * @author Leopard
	 *
	 */
	public final static class PrintPort {
		private String				portName;				//打印档口名
		private Boolean 			separable;				//是否逐条打印
		private String			 	printerAddr;			//打印机地址		
			
		public PrintPort(String name, Boolean separable, String printAddr){
			this.portName 	 = name;
			this.separable 	 = separable;
			this.printerAddr = printAddr;			
		}
		
		public String getName(){
			return portName;
		}
		
		public Boolean isSeparable(){
			return separable;
		}
		
		public String getPrinterAddr(){
			return printerAddr;
		}
		
	}

	/**
	 * <p>PrintTemplate</p>
	 * 	
	 * <p>打印模板</p>
	 *
	 * @author Leopard
	 * 
	 */
	public static final class PrintTemplate {

		private String header;				//头
		private String primary;				//主内容
		private String additive;			//副内容
		private String tail;				//尾
		
		public PrintTemplate(String header, String primary, String additive, String tail){
			this.header 	= header;
			this.primary 	= primary;
			this.additive 	= additive;
			this.tail 		= tail;
		}

		/* (non-Javadoc)
		 * @see java.lang.Object#toString()
		 */
		@Override
		public String toString() {
			return String.format("%s%s", header, tail);
		}		
		
		public String getHeader(){
			return this.header;
		}

		public String getTail(){
			return this.tail;
		}
		
		public String getPrimary(){
			return this.primary;
		}
		
		public String getAdditive(){
			return this.additive;
		}

		
		public int getTailLength(){
			return tail.length();
		}

	}
	
}
