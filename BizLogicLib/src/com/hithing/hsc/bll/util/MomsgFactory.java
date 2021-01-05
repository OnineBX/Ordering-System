/**========================================
 * File:	MomsgFactory.java
 * Package:	com.hithing.hsc.bll.manage
 * Create:	by Leopard
 * Date:	2011-12-6:下午03:03:50
 **======================================*/
package com.hithing.hsc.bll.util;

import com.google.protobuf.GeneratedMessage;
import com.google.protobuf.InvalidProtocolBufferException;
import com.hithing.hsc.entity.MoMsg.CMsgAuthvalidateReq;
import com.hithing.hsc.entity.MoMsg.CMsgAuthvalidateRsp;
import com.hithing.hsc.entity.MoMsg.CMsgBillPricingReq;
import com.hithing.hsc.entity.MoMsg.CMsgBillPricingRsp;
import com.hithing.hsc.entity.MoMsg.CMsgBillReq;
import com.hithing.hsc.entity.MoMsg.CMsgBillRsp;
import com.hithing.hsc.entity.MoMsg.CMsgCheckAccountReq;
import com.hithing.hsc.entity.MoMsg.CMsgCheckAccountRsp;
import com.hithing.hsc.entity.MoMsg.CMsgClearSpotReq;
import com.hithing.hsc.entity.MoMsg.CMsgClearSpotRsp;
import com.hithing.hsc.entity.MoMsg.CMsgComplexReq;
import com.hithing.hsc.entity.MoMsg.CMsgComplexRsp;
import com.hithing.hsc.entity.MoMsg.CMsgFoundingReq;
import com.hithing.hsc.entity.MoMsg.CMsgFoundingRsp;
import com.hithing.hsc.entity.MoMsg.CMsgImageLoadReq;
import com.hithing.hsc.entity.MoMsg.CMsgImageLoadRsp;
import com.hithing.hsc.entity.MoMsg.CMsgInitCashierReq;
import com.hithing.hsc.entity.MoMsg.CMsgInitCashierRsp;
import com.hithing.hsc.entity.MoMsg.CMsgIntervalSyncReq;
import com.hithing.hsc.entity.MoMsg.CMsgIntervalSyncRsp;
import com.hithing.hsc.entity.MoMsg.CMsgLoginReq;
import com.hithing.hsc.entity.MoMsg.CMsgLoginRsp;
import com.hithing.hsc.entity.MoMsg.CMsgOrderCancelReq;
import com.hithing.hsc.entity.MoMsg.CMsgOrderCancelRsp;
import com.hithing.hsc.entity.MoMsg.CMsgOrderReq;
import com.hithing.hsc.entity.MoMsg.CMsgOrderRsp;
import com.hithing.hsc.entity.MoMsg.CMsgRefreshResReq;
import com.hithing.hsc.entity.MoMsg.CMsgRefreshResRsp;
import com.hithing.hsc.entity.MoMsg.CMsgViewBillReq;
import com.hithing.hsc.entity.MoMsg.CMsgViewBillRsp;

/**
 * <p>MomsgFactory</p>
 * 	
 * <p>消息工厂类</p>
 *
 * 用于创建各种protobuf协议下定义的消息实体
 *
 * @author Leopard
 * 
 */
public final class MomsgFactory {
	private static WorkContext wContext = WorkContext.getInstance();
	/**
	 * <p>MomsgFactory</p>
	 *
	 * <p>构造函数</p>
	 */
	public MomsgFactory() {
		
	}
	
	/**
	 * <p>createMomsg</p>
	 *
	 * <p>创建protobuf协议定义的业务消息</p>
	 * 
	 * @param type 请求消息类型
	 * @param params 消息参数
	 * @return 返回消息体
	 */	
	@SuppressWarnings("unchecked")
	public static <T> T createMomsgBuilder(MomsgType reqType){		
		
		//根据类型创建构造器
		switch(reqType){
		case LOGIN_REQ:
			return (T)CMsgLoginReq.newBuilder().setCompanyseq(wContext.getCompCode()).setTermseq(wContext.getTermCode());
		case INTERVALSYNC_REQ:
			return (T)CMsgIntervalSyncReq.newBuilder().setCompanyseq(wContext.getCompCode()).setTermseq(wContext.getTermCode());
		case REFRESHRES_REQ:
			return (T)CMsgRefreshResReq.newBuilder().setCompanyseq(wContext.getCompCode()).setTermseq(wContext.getTermCode());
		case FOUNDING_REQ:			
			return (T)CMsgFoundingReq.newBuilder().setCompanyseq(wContext.getCompCode()).setTermseq(wContext.getTermCode());
		case VIEWBILL_REQ:
			return (T)CMsgViewBillReq.newBuilder().setCompanyseq(wContext.getCompCode()).setTermseq(wContext.getTermCode());
		case ORDER_REQ:
			return (T)CMsgOrderReq.newBuilder().setCompanySeq(wContext.getCompCode()).setTermSeq(wContext.getTermCode());
		case AUTHVALIDATE_REQ:
			return (T)CMsgAuthvalidateReq.newBuilder().setCompanyseq(wContext.getCompCode()).setTermseq(wContext.getTermCode());
		case INITCASHIER_REQ:
			return (T)CMsgInitCashierReq.newBuilder().setCompanyseq(wContext.getCompCode()).setTermseq(wContext.getTermCode()).
					setHallid(wContext.getHall().id);
		case CLEARSPOT_REQ:
			return (T)CMsgClearSpotReq.newBuilder().setCompanyseq(wContext.getCompCode()).setTermseq(wContext.getTermCode());
		case BILLPRICING_REQ:
			return (T)CMsgBillPricingReq.newBuilder().setCompanyseq(wContext.getCompCode()).setTermseq(wContext.getTermCode());
		case PAYBILL_REQ:
			return (T)CMsgBillReq.newBuilder().setCompanyseq(wContext.getCompCode()).setTermseq(wContext.getTermCode());
		case COMPLEX_REQ:
			return (T)CMsgComplexReq.newBuilder();
		case UNFOUNDING_REQ:
			return (T)CMsgOrderCancelReq.newBuilder().setCompanyseq(wContext.getCompCode()).setTermseq(wContext.getTermCode());
		case IMAGELOAD_REQ:
			return (T)CMsgImageLoadReq.newBuilder().setCompanyseq(wContext.getCompCode()).setTermseq(wContext.getTermCode());
		case CHECKACCOUNT_REQ:
			return (T)CMsgCheckAccountReq.newBuilder().setCompanyseq(wContext.getCompCode()).setTermseq(wContext.getTermCode());
		}		
		
		return null;						
	}						
	
	/**
	 * <p>createMomsg</p>
	 * 
	 * <p>创建protobuf协议定义的业务消息</p>
	 * 
	 * @param rspType 返回消息类型
	 * @param data 消息字节数组
	 * @return 
	 * @return 返回一个通用的消息对象
	 */
	public static GeneratedMessage createMomsg(MomsgType rspType , byte[] data) {
		
		try {
			switch(rspType){
				case LOGIN_RSP:				
					return CMsgLoginRsp.parseFrom(data);
				case INTERVALSYNC_RSP:
					return CMsgIntervalSyncRsp.parseFrom(data);
				case REFRESHRES_RSP:
					return CMsgRefreshResRsp.parseFrom(data);
				case FOUNDING_RSP:
					return CMsgFoundingRsp.parseFrom(data);
				case VIEWBILL_RSP:
					return CMsgViewBillRsp.parseFrom(data);
				case ORDER_RSP:
					return CMsgOrderRsp.parseFrom(data);
				case AUTHVALIDATE_RSP:
					return CMsgAuthvalidateRsp.parseFrom(data);
				case INITCASHIER_RSP:
					return CMsgInitCashierRsp.parseFrom(data);
				case CLEARSPOT_RSP:
					return CMsgClearSpotRsp.parseFrom(data);
				case BILLPRICING_RSP:
					return CMsgBillPricingRsp.parseFrom(data);
				case PAYBILL_RSP:
					return CMsgBillRsp.parseFrom(data);
				case COMPLEX_RSP:
					return CMsgComplexRsp.parseFrom(data);
				case UNFOUNDING_RSP:
					return CMsgOrderCancelRsp.parseFrom(data);
				case IMAGELOAD_RSP:
					return CMsgImageLoadRsp.parseFrom(data);
				case CHECKACCOUNT_RSP:
					return CMsgCheckAccountRsp.parseFrom(data);
			}
		} catch (InvalidProtocolBufferException e) {			
			e.printStackTrace();
		}
		return null;
	}
	
	public enum MomsgType{
		LOGIN_REQ(1) 			, LOGIN_RSP(2) 		 	, INTERVALSYNC_REQ(3)  	, INTERVALSYNC_RSP(4) 	,
		REFRESHRES_REQ(5)  		, REFRESHRES_RSP(6) 	, FOUNDING_REQ(7)  		, FOUNDING_RSP(8) 		, 
		VIEWBILL_REQ(9) 		, VIEWBILL_RSP(10)	 	, ORDER_REQ(11)			, ORDER_RSP(12) 		,
		AUTHVALIDATE_REQ(13)	, AUTHVALIDATE_RSP(14)	, INITCASHIER_REQ(17)	, INITCASHIER_RSP(18)	,
		CLEARSPOT_REQ(19)		, CLEARSPOT_RSP(20)		, BILLPRICING_REQ(27)	, BILLPRICING_RSP(28)	,
		PAYBILL_REQ(29)			, PAYBILL_RSP(30)		, COMPLEX_REQ(31)		, COMPLEX_RSP(32)		, 
		UNFOUNDING_REQ(33)		, UNFOUNDING_RSP(34)	, IMAGELOAD_REQ(35)		, IMAGELOAD_RSP(36)		,
		CHECKACCOUNT_REQ(37)	, CHECKACCOUNT_RSP(38)	;


		private int value;								//消息值
		MomsgType(int value) { this.value = value; }
		public int getValue() { return value; }
		
		public static MomsgType valueOf(int value){
			switch(value){
			case 2:
				return LOGIN_RSP;
			case 4:
				return INTERVALSYNC_RSP;			
			case 6:
				return REFRESHRES_RSP;
			case 8:
				return FOUNDING_RSP;
			case 10:
				return VIEWBILL_RSP;	
			case 12:
				return ORDER_RSP;	
			case 14:
				return AUTHVALIDATE_RSP;	
			case 18:
				return INITCASHIER_RSP;
			case 20:
				return CLEARSPOT_RSP;
			case 28:
				return BILLPRICING_RSP;
			case 30:
				return PAYBILL_RSP;
			case 32:
				return COMPLEX_RSP;
			case 34:
				return UNFOUNDING_RSP;
			case 36:
				return IMAGELOAD_RSP;
			case 38:
				return CHECKACCOUNT_RSP;
			default:
				return null;
			}
		}
	}
	
	
}
