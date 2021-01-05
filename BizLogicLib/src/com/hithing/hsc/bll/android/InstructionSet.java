/**========================================
 * File:	InstructionSet.java
 * Package:	com.hithing.hsc.bll.android
 * Create:	by Leopard
 * Date:	2012-3-12:上午10:10:01
 **======================================*/
package com.hithing.hsc.bll.android;

import android.content.Context;
import android.content.res.Resources;
import android.util.Log;

/**
 * <p>InstructionSet</p>
 * 	
 * <p>功能描述</p>
 *
 * @author Leopard
 * 
 */
public enum InstructionSet {
	INSTANCE;
	
	public String PRINT_BIG_FONT;						//大字体
	public String PRINT_LONG_SMALL_FONT;				//拉长小字体
	public String PRINT_SHORT_SMALL_FONT;				//普通小字体
	public String PRINT_LEFT_ALIGN;						//左对齐
	public String PRINT_CENTER_ALIGN;					//中间对齐
	public String PRINT_RIGHT_ALIGN;					//右对齐
	public String PRINT_CUT_PAPER;						//切纸
	public String PRINT_END;							//结束打印
	public String PRINT_ENTER;							//换行符
	public String PRINT_SPLIT_STRING;					//分隔符
	public String PRINT_SHORT_BLANK;					//短留空
	public String PRINT_LONG_BLANK;						//长留空
	public String PRINT_BUZZER;							//蜂鸣

	/**
	 * <p>InstructionSet</p>
	 *
	 * <p>构造函数</p>
	 */
	InstructionSet() {
		
	}	
	
	public void initInstance(Context context){
//		Resources resources = context.getResources();
//		PRINT_BIG_FONT 			= resources.getString(R.string.print_big_font);
//		PRINT_LONG_SMALL_FONT 	= resources.getString(R.string.print_long_small_font);
//		PRINT_SHORT_SMALL_FONT	= resources.getString(R.string.print_short_small_font);
//		PRINT_LEFT_ALIGN		= resources.getString(R.string.print_left_align);
//		PRINT_CENTER_ALIGN		= resources.getString(R.string.print_center_align);
//		PRINT_RIGHT_ALIGN		= resources.getString(R.string.print_right_align);
//		PRINT_CUT_PAPER			= resources.getString(R.string.print_cut_paper);
//		PRINT_END				= resources.getString(R.string.print_end);
//		PRINT_ENTER				= resources.getString(R.string.print_enter);
//		PRINT_SPLIT_STRING		= resources.getString(R.string.print_split_string);
//		PRINT_SHORT_BLANK		= resources.getString(R.string.print_short_blank);
//		PRINT_LONG_BLANK		= resources.getString(R.string.print_long_blank);
		PRINT_BIG_FONT 			= "\u001D\u0021\u0011";
		PRINT_LONG_SMALL_FONT 	= "\u001D\u0021\u0001";
		PRINT_SHORT_SMALL_FONT	= "\u001D\u0021\u0000";
		PRINT_LEFT_ALIGN		= "\u001b\u0061\u0000";
		PRINT_CENTER_ALIGN		= "\u001B\u0061\u0001";
		PRINT_RIGHT_ALIGN		= "\u001B\u0061\u0002";
		PRINT_CUT_PAPER			= "\u001D\u0056\u0000";
		PRINT_END				= "\u0007";
		PRINT_ENTER				= "\n";
		PRINT_SPLIT_STRING		= "--------------------------------------------";
		PRINT_SHORT_BLANK		= "\n\n\n\n\n\n\n\n\n";
		PRINT_LONG_BLANK		= "\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n";	
		PRINT_BUZZER			= "\u001B\u0043\u0010\u0002\u0003";
	}

}
