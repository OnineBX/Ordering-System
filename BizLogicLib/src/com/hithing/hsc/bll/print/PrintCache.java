/**========================================
 * File:	PrintCache.java
 * Package:	com.hithing.hsc.bll.print
 * Create:	by Leopard
 * Date:	2012-3-12:上午10:23:49
 **======================================*/
package com.hithing.hsc.bll.print;

import java.util.HashMap;
import java.util.List;
import java.util.Set;

import android.text.TextUtils;
import android.util.Log;

import com.hithing.hsc.bll.print.PrintManager.PrintTemplate;
import com.hithing.hsc.entity.OrderItemValue;


/**
 * <p>PrintCache</p>
 * 	
 * <p>打印缓存,用于保存格式化的打印数据</p>
 *
 * @author Leopard
 * 
 */
public class PrintCache extends HashMap<Integer, PrintCache.PrintItem> {
	private static final PrintCache INSTANCE = new PrintCache();
	
	public static PrintCache getInstance(){		
		return INSTANCE;
	}
	
	private PrintCache(){						
		
	}	
	
	public void initInstance(Set<Integer> portIds){			
		for (int portId : portIds) {
			put(portId, new PrintItem(PrintManager.INSTANCE.getTemplate(portId)));
		}
		Log.e("PrintCache", String.format("initInstance-port's count=%d", size()));
	}
	
	/**
	 * <p>Initialize</p>
	 *
	 * <p>初始化打印缓存</p>
	 * 
	 * @param addedInfo
	 */
	public void initData(List<Object> addedInfo){
		for (PrintItem item : values()) {
			item.initialize(addedInfo);			
		}
				
	}
	
	/**
	 * 清空打印缓存
	 */
	public void clear(){
		for (PrintItem item : values()) {
			item.prepare();
		}
	}

	/**
	 * <p>PrintItem</p>
	 * 	
	 * <p>打印条目:用于格式化打印内容</p>
	 *
	 * @author Leopard
	 *
	 */
	public static final class PrintItem {
		private PrintTemplate template;					//内容模板
		private List<Object>  addedInfo;				//内容附加信息:参数
		private StringBuilder builder;					//内容构建器
		private int	  		  ORIGINAL_LENGTH;			//原始长度
		
		public PrintItem(PrintTemplate template){
			this.template 	= template;				
		}				
		
		/**
		 * <p>initialize</p>
		 *
		 * <p>初始化打印条目</p>
		 * 
		 * @param addedInfo
		 */
		public void initialize(List<Object> addedInfo){
			this.addedInfo = addedInfo;
			this.prepare();
			ORIGINAL_LENGTH = builder.length();
		}
		
		/**
		 * <p>prepare</p>
		 *
		 * <p>清空并准备打印条目</p>
		 *
		 */
		public void prepare(){
//			builder.delete(0, builder.length());		用此方法内存回收时会出现问题,不采用
//			builder.append(String.format(template.toString(), addedInfo.toArray()));
			builder = null;
			builder = new StringBuilder(String.format(template.toString(), addedInfo.toArray()));
		}

		/**
		 * <p>addData</p>
		 *
		 * <p>向打印条目中加入数据</p>
		 * 
		 * @param value
		 */
		public void addData(OrderItemValue value){
			String primaryStr = String.format(template.getPrimary(), value.getFood().content,value.getCountWithUnit());
			builder.insert(builder.length() - template.getTailLength(), primaryStr);
			String description = value.getDescription();
			if(!TextUtils.isEmpty(description)){
				String additiveStr = String.format(template.getAdditive(), description);
				builder.insert(builder.length() - template.getTailLength(), additiveStr);
			}
		}

		/**
		 * <p>hasData</p>
		 *
		 * <p>是否加入了数据</p>
		 * 
		 * @return
		 */
		public Boolean hasData(){
			return builder.length() > ORIGINAL_LENGTH;
		}
		
		/* (non-Javadoc)
		 * @see java.lang.Object#toString()
		 */
		@Override
		public String toString() {				
			return builder.toString();
		}
				
	}			
}
