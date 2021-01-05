/**========================================
 * File:	SystemCtrl.java
 * Package:	com.hithing.hsc.bll.control
 * Create:	by Leopard
 * Date:	2012-5-24:下午6:22:37
 **======================================*/
package com.hithing.hsc.bll.control;

import java.io.IOException;

import com.google.protobuf.ByteString;
import com.hithing.hsc.bll.util.ITransportable.IAsynSendable;
import com.hithing.hsc.bll.util.MomsgFactory;
import com.hithing.hsc.bll.util.MomsgFactory.MomsgType;
import com.hithing.hsc.dal.network.Client.EoprMomsg;
import com.hithing.hsc.entity.MoMsg.CMsgAuthvalidateRsp;
import com.hithing.hsc.entity.MoMsg.CMsgLoginReq;
import com.hithing.hsc.entity.MoMsg.CMsgLoginRsp;

/**
 * <p>SystemCtrl</p>
 * 	
 * <p>系统控制类,用于完成一些系统级别的操作</p>
 *
 * @author Leopard
 * 
 */
public final class SystemCtrl implements IAsynSendable{
	
	private String compCode;
	private String termCode;
	private String authCode;
	
	/**
	 * <p>SystemCtrl</p>
	 *
	 * <p>构造函数</p>
	 */
	public SystemCtrl(String compCode, String termCode, String authCode) {
		this.compCode = compCode;
		this.termCode = termCode;
		this.authCode = authCode;
	}

	/**
	 * <p>loginForResult</p>
	 *
	 * <p>同步登录并返回验证结果</p>
	 * 
	 * @param password 用户密码
	 * @return 用户登录返回消息
	 */
	public CMsgLoginRsp loginForResult(){
		try {
			CMsgLoginReq.Builder builder = MomsgFactory.createMomsgBuilder(MomsgType.LOGIN_REQ);
			CMsgLoginReq req = builder.setCompanyseq(compCode).
									   setTermseq(termCode).
									   setAuthcode(authCode).
									   build();
			client.sendDataAsyn(MomsgType.LOGIN_REQ.getValue(), req.toByteArray());
			EoprMomsg msg = client.receiveDataAsyn();
			return (CMsgLoginRsp) MomsgFactory.createMomsg(MomsgType.valueOf(msg.getType()), msg.getMomsg());
		} catch (IOException e) {			
			e.printStackTrace();			
			return null;
		}
	}	
}
