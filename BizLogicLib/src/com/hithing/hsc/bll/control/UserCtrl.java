/**========================================
 * File:	UserCtrl.java
 * Package:	com.hithing.hsc.bll.control
 * Create:	by leo
 * Date:	2012-6-26:上午11:57:52
 **======================================*/
package com.hithing.hsc.bll.control;

import java.io.IOException;
import java.net.UnknownHostException;

import com.hithing.hsc.bll.util.ITransportable.IAsynSendable;
import com.hithing.hsc.bll.util.MomsgFactory;
import com.hithing.hsc.bll.util.MomsgFactory.MomsgType;
import com.hithing.hsc.dal.network.Client.EoprMomsg;
import com.hithing.hsc.entity.MoMsg.CMsgAuthvalidateReq;
import com.hithing.hsc.entity.MoMsg.CMsgAuthvalidateRsp;

/**
 * <p>UserCtrl</p>
 * 	
 * <p>用户控制类，提供与用户有关的操作</p>
 *
 * @author leo
 * 
 */
public final class UserCtrl implements IAsynSendable{
	private int authCode;

	/**
	 * <p>UserCtrl</p>
	 *
	 * <p>构造函数</p>
	 */
	public UserCtrl(int authcode) {
		this.authCode = authcode;
	}

	/**
	 * <p>authValidate</p>
	 *
	 * <p>权限验证</p>
	 * @param type 权限类型
	 *
	 */
	public void validateAuth(final AuthenticateType type){
		executor.execute(new Runnable() {
			
			@Override
			public void run() {				
				try {
					CMsgAuthvalidateReq.Builder builder = MomsgFactory.createMomsgBuilder(MomsgType.AUTHVALIDATE_REQ);
					CMsgAuthvalidateReq req = builder.setAuthcode(authCode).
													  setRightid(type.getValue()).
													  build();
					client.sendDataAsyn(MomsgType.AUTHVALIDATE_REQ.getValue(), req.toByteArray());
				} catch (UnknownHostException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		});						
	}
	
	/**
	 * <p>validateAuthForResult</p>
	 *
	 * <p>同步权限验证</p>
	 * 
	 * @param authcode 验证码
	 * @return 验证应答消息
	 */
	public CMsgAuthvalidateRsp validateAuthForResult(final AuthenticateType type, boolean isClose){		
		try {
			CMsgAuthvalidateReq.Builder builder = MomsgFactory.createMomsgBuilder(MomsgType.AUTHVALIDATE_REQ);
			CMsgAuthvalidateReq req = builder.setAuthcode(authCode).
											  setRightid(type.getValue()).
											  build();
			if(!client.isConnectedSync()){
				client.connectSync();
			}	
			EoprMomsg msg = client.sendDataSyncForResult(MomsgType.AUTHVALIDATE_REQ.getValue(), req.toByteArray());
			if(isClose){
				client.closeSync();
			}	
			return (CMsgAuthvalidateRsp)MomsgFactory.createMomsg(MomsgType.valueOf(msg.getType()), msg.getMomsg());
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
	
	/**
	 * <p>AuthenticateType</p>
	 * 	
	 * <p>权限类型</p>
	 *
	 * @author leo
	 *
	 */
	public enum AuthenticateType{
		AUTH_TYPE_CASH(1)	,					//收银权限
		AUTH_TYPE_OFFER(7)	;					//定价权限
		int value;
		private AuthenticateType(int value) {
			this.value = value;
		}
		
		public int getValue(){ return value; }
	}
}
