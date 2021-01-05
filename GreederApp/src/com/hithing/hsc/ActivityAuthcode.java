/**========================================
 * File:	ActivityAuthcode.java
 * Package:	com.hithing.hsc
 * Create:	by Leopard
 * Date:	2012-2-18:上午10:12:43
 **======================================*/
package com.hithing.hsc;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.EditText;

import com.hithing.app.DialogActivity;
import com.hithing.hsc.global.StringKey;

/**
 * <p>ActivityAuthcode</p>
 * 	
 * <p>用于输入授权码的活动</p>
 *
 * @author Leopard
 * 
 */
public class ActivityAuthcode extends DialogActivity {	
	public final static String 	INTENT_EXTRA_KEY_AUTHCODE 	= "authcode";			//Intent参数键-授权码	
	public final static int		INVALID_AUTHCODE 			= 0;					//非法的授权码
	
	private NeedAuthcodeType 	type;											//需要输入授权码的类型
	private EditText 			authcodeEdt;									//授权码文本编辑框

	/* (non-Javadoc)
	 * @see com.hithing.app.DialogActivity#getDataFromDialog()
	 */
	@Override
	protected Intent getDataFromDialog() {
		Intent intent = new Intent();
		intent.putExtra(StringKey.INTENT_EXTRA_KEY_TYPE, type);
				
		if(!TextUtils.isEmpty(authcodeEdt.getText())){
			intent.putExtra(INTENT_EXTRA_KEY_AUTHCODE, Integer.parseInt(authcodeEdt.getText().toString()));
		}
		
		return intent;
	}

	/* (non-Javadoc)
	 * @see com.hithing.app.DialogActivity#loadViewStub()
	 */
	@Override
	protected void loadViewStub() {
		content.setLayoutResource(R.layout.act_authcode_content);
		content.inflate();	
	}
	
	
	
	/* (non-Javadoc)
	 * @see com.hithing.app.DialogActivity#onCreate(android.os.Bundle)
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {		
		super.onCreate(savedInstanceState);
		
		Intent intent = getIntent();
		authcodeEdt = (EditText)findViewById(R.id.authcode_edt_code);
		type = (NeedAuthcodeType)intent.getSerializableExtra(StringKey.INTENT_EXTRA_KEY_TYPE);
		switch (type) {
		case placeorder:
			title.setText(R.string.acode_txv_title_placeorder);
			break;
		case priororder:
			title.setText(R.string.acode_txv_title_priororder);
			break;
		case setoffer:
			title.setText(R.string.acode_txv_title_setoffer);
			break;	
		case cancelorder:
			title.setText(R.string.acode_txv_title_cancelorder);
			break;
		case cash:
			title.setText(R.string.acode_txv_title_cash);
			break;
		}
		
	}

	//需要输入授权码的类型
	public enum NeedAuthcodeType {
		placeorder	, 	//落单
		priororder	, 	//先落
		setoffer	, 	//优惠
		cancelorder	, 	//全单取消
		cash,			//收银
	}

}
