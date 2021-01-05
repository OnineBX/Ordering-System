/**========================================
 * File:	CashContext.java
 * Package:	com.hithing.hsc.bll.util
 * Create:	by Leopard
 * Date:	2012-5-15:下午2:53:51
 **======================================*/
package com.hithing.hsc.bll.util;

import java.util.Calendar;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import java.io.IOException;
import java.net.UnknownHostException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;

import android.util.Log;

import com.hithing.hsc.bll.util.MomsgFactory.MomsgType;
import com.hithing.hsc.dal.network.Client.EoprMomsg;
import com.hithing.hsc.dal.network.TcpClient;
import com.hithing.hsc.entity.MoMsg.CMsgInitCashierReq;
import com.hithing.hsc.entity.MoMsg.CMsgInitCashierRsp;
import com.hithing.hsc.entity.MoMsg.CMsgInitCashierRsp.CMsgPayMethod;
import com.hithing.hsc.entity.MoMsg.CMsgInitCashierRsp.CMsgSpotShift;
import com.hithing.hsc.entity.PayMethodValue;

/**
 * <p>CashContext</p>
 * 	
 * <p>收银上下文</p>
 * 用于保存运行收银系统的全局变量,并提供相关操作
 *
 * @author Leopard
 * 
 */
public enum CashContext implements ITransportable{
	INSTANCE;
	private final int		EMPTY_INTEGER 	= 0;			//空整型
	private final String	EMPTY_STRING	= "";			//空字符串
	
	private CompoundItem	currentShift;					//当前班次
	private CompoundItem	currentCasher;					//当班收银员
	private CompoundItem	cashSpot;						//收银点
	
	List<CMsgSpotShift>		shiftList;						//班次信息集合	
	List<CMsgPayMethod>		payMethodList;					//付款方式集合
	
	private CashContext() {		
		shiftList		= new LinkedList<CMsgSpotShift>();		
		currentShift	= new CompoundItem();
		currentCasher 	= new CompoundItem();		
		cashSpot		= new CompoundItem();					
	}
	
	/**
	 * <p>InitCashData</p>
	 *
	 * <p>采用同步方式初始化收银数据</p>
	 * @param isClose 调用后是否关闭连接
	 *
	 */
	public void initializeSync(boolean isClose){		
		try {
			clear();
			CMsgInitCashierReq.Builder 	builder = MomsgFactory.createMomsgBuilder(MomsgType.INITCASHIER_REQ);
			CMsgInitCashierReq 			req 	= builder.build();	
			if(!client.isConnectedSync()){
				client.connectSync();
			}			
			EoprMomsg msg = client.sendDataSyncForResult(MomsgType.INITCASHIER_REQ.getValue(), req.toByteArray());
			if(isClose){
				client.closeSync();
			}			
			CMsgInitCashierRsp rsp = (CMsgInitCashierRsp)MomsgFactory.createMomsg(MomsgType.valueOf(msg.getType()), msg.getMomsg());
			
			shiftList 			= rsp.getSpotShiftsList();
			payMethodList		= rsp.getPayMethodsList();			
			currentShift.id 	= rsp.getShiftId();
			currentShift.name	= rsp.getShiftName().toStringUtf8();
			cashSpot.id			= rsp.getSpotId();
			cashSpot.name		= rsp.getSpotName().toStringUtf8();			
			
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}						
	}
	
	/**
	 * <p>getNextBizDay</p>
	 *
	 * <p>获得下一个营业日</p>
	 * 
	 * @return 下一个营业日的字符串格式
	 * @throws ParseException 解析异常
	 */
	public static String getNextBizDay(String date){												
		try {
			SimpleDateFormat format = new SimpleDateFormat("yyyy-mm-dd");
			Calendar 		bizDay = Calendar.getInstance();
			
			bizDay.setTime(format.parse(date));				
			bizDay.add(Calendar.DATE, 1);
			String result = format.format(bizDay.getTime());		
			return result;
		} catch (ParseException e) {			
			e.printStackTrace();
			return null;
		}						
	}
	
	/**
	 * <p>getShiftValues</p>
	 *
	 * <p>功能描述</p>
	 * 
	 * @return 班次信息数组
	 */
	public CharSequence[] getShiftValues(){
		List<CharSequence> result = new LinkedList<CharSequence>();
		for(CMsgSpotShift shift : shiftList){
			result.add(String.format("%s(%s-%s)", shift.getName().toStringUtf8(), shift.getBtime(), shift.getEtime()));			
		}
		return result.toArray(new CharSequence[0]);
	}	
	
	/**
	 * <p>setCurrentShift</p>
	 *
	 * <p>设置当前班次</p>
	 * 
	 * @param index 班次索引
	 */
	public void setCurrentShift(int index){
		CMsgSpotShift shift = shiftList.get(index);
		currentShift.id 	= shift.getId();
		currentShift.name 	= shift.getName().toStringUtf8();
	}
	
	/**
	 * <p>getCurrentShiftName</p>
	 *
	 * <p>获得当前班次名</p>
	 * 
	 * @return
	 */
	public String getCurrentShiftName(){
		return currentShift.name;
	}
	
	/**
	 * <p>getCurrentShiftId</p>
	 *
	 * <p>获得当前班次标识</p>
	 * 
	 * @return
	 */
	public int getCurrentShiftId(){		
		return currentShift.id;
	}
	
	/**
	 * <p>setCurrentCasher</p>
	 *
	 * <p>设置当前收银员信息</p>
	 * 
	 * @param authCode 	授权码
	 * @param name		姓名
	 */
	public void setCurrentCasher(int authCode, String name){
		currentCasher.id 	= authCode;
		currentCasher.name 	= name;
	}
	
	/**
	 * <p>getCurrentCasherName</p>
	 *
	 * <p>获得当前收银员姓名</p>
	 * 
	 * @return 收银员姓名
	 */
	public String getCurrentCasherName(){
		return currentCasher.name;
	}
	
	/**
	 * <p>getCurrentCasherId</p>
	 *
	 * <p>获得当前收银员标识</p>
	 * 
	 * @return 收银员标识
	 */
	public int getCurrentCasherId(){
		return currentCasher.id;
	}
	
	/**
	 * <p>getCashSpot</p>
	 *
	 * <p>获得当前收银点名称</p>
	 * 
	 * @return 收银点名称
	 */
	public String getCashSpotName(){
		return cashSpot.name;
	}
	
	/**
	 * <p>getCashSpotId</p>
	 *
	 * <p>获得当前收银点标识</p>
	 * 
	 * @return
	 */
	public int getCashSpotId(){
		return cashSpot.id;
	}
		
	public List<CMsgPayMethod> getPayMethodData(){
		return payMethodList;
	}
	
	//清空数据
	private void clear(){
		cashSpot.id 		= EMPTY_INTEGER;
		cashSpot.name 		= EMPTY_STRING;
		currentShift.id		= EMPTY_INTEGER;
		currentShift.name	= EMPTY_STRING;
		shiftList			= null;
	}	
}
