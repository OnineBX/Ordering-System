/**========================================
 * File:	UserCtrl.java
 * Package:	com.hithing.hsc.bll.control
 * Create:	by Leopard
 * Date:	2011-12-10:下午03:03:50
 **======================================*/
package com.hithing.hsc.bll.control;

import java.io.IOException;
import java.net.UnknownHostException;

import com.google.protobuf.ByteString;
import com.google.protobuf.GeneratedMessage;
import com.hithing.hsc.bll.util.MomsgFactory;
import com.hithing.hsc.bll.util.MomsgFactory.MomsgType;
import com.hithing.hsc.bll.util.MomsgHelper.IAsynSendable;
import com.hithing.hsc.dal.network.Client.EoprMomsg;
import com.hithing.hsc.entity.MoMsg.CMsgLoginReq;
import com.hithing.hsc.entity.MoMsg.CMsgLoginRsp;
import com.hithing.hsc.entity.MoMsg.CMsgModifyPwdReq;


/**
 * <p>UserCtrl</p>
 * 	
 * <p>用户控制类,提供用户对象各种操作</p>
 * 
 * @author Leopard
 *
 */
public final class UserCtrl implements IAsynSendable{
	
	private int 		userId;	
		
	/**
	 * <p>UserCtrl</p>
	 *
	 * <p>构造函数</p>
	 */	
	public UserCtrl(int userId) {
		
		this.userId 	= userId;		
	}
	
	/**
	 * <p>login</p>
	 *
	 * <p>用户登录</p>
	 * 
	 * @param executor 用于并发执行用户登录过程的接口
	 */
	public void login(final String password) {
								
		executor.execute(new Runnable() {				
				@Override
				public void run() {
					try {
						CMsgLoginReq.Builder builder = MomsgFactory.createMomsgBuilder(MomsgType.LOGIN_REQ);
						CMsgLoginReq req = builder.setUserid(userId).setPwd(ByteString.copyFrom(password, "utf-8")).build();				
						client.outputDataToServer(MomsgType.LOGIN_REQ.getValue(), req.toByteArray());
					}  catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			});		
	}
	
	/**
	 * <p>loginForResult</p>
	 *
	 * <p>同步登录并返回验证结果</p>
	 * 
	 * @param password 用户密码
	 * @return 用户登录返回消息
	 */
	public CMsgLoginRsp loginForResult(String password){						
		try {
			CMsgLoginReq.Builder builder = MomsgFactory.createMomsgBuilder(MomsgType.LOGIN_REQ);
			CMsgLoginReq req = builder.setUserid(userId).setPwd(ByteString.copyFrom(password, "utf-8")).build();
			client.outputDataToServer(MomsgType.LOGIN_REQ.getValue(), req.toByteArray());
			EoprMomsg msg = client.inputDataFromServer();
			return (CMsgLoginRsp) MomsgFactory.createMomsg(MomsgType.valueOf(msg.getType()), msg.getMomsg());
		} catch (IOException e) {			
			e.printStackTrace();			
			return null;
		}
		
	}
	
	/**
	 * <p>modifyPassword</p>
	 *
	 * <p>修改密码</p>
	 * 
	 * @param password 新密码
	 * @param authcode 授权码
	 */
	public void modifyPassword(final String password, final int authcode){
		executor.execute(new Runnable() {				
			@Override
			public void run() {
//				try {
					//unfinished code
//					CMsgModifyPwdReq.Builder builder = MomsgFactory.createMomsgBuilder(reqType);
//					CMsgModifyPwdReq req = builder.setPwd(ByteString.copyFrom(password.getBytes())).setAuthcode(authcode).build();									
//					client.outputDataToServer(MomsgType.LOGIN_REQ.getValue(), req.toByteArray());
//				}  catch (IOException e) {
//					// TODO Auto-generated catch block
//					e.printStackTrace();
//				}
			}
		});		
	}

}
