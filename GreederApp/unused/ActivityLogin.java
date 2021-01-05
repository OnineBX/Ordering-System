/**========================================
 * File:	ActivityLogin.java
 * Package:	com.hithing.hsc
 * Create:	by Leopard
 * Date:	2012-2-28:下午4:42:43
 **======================================*/
package com.hithing.hsc;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.widget.EditText;
import android.widget.TextView;

import com.hithing.app.DialogActivity;

/**
 * <p>ActivityLogin</p>
 * 	
 * <p>功能描述</p>
 *
 * @author Leopard
 * 
 */
public final class ActivityLogin extends DialogActivity {

	public final static String INTENT_EXTRA_KEY_USER 		= "user";				//Intent参数键-用户标识
	public final static String INTENT_EXTRA_KEY_PASSWORD 	= "password";			//Intent参数键-密码
	
	private EditText userIdEdt;					//用户标识编辑框
	private EditText passwordEdt;				//密码编辑框
	
	/* (non-Javadoc)
	 * @see com.hithing.app.DialogActivity#onCreate(android.os.Bundle)
	 * Secure.getString(getBaseContext().getContentResolver(), Secure.ANDROID_ID);
	 * 机器码
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		
		//初始化标题
		TextView title = (TextView)findViewById(R.id.dialog_title);
		title.setText(R.string.login_dlg_title);
		
		userIdEdt 	= (EditText)findViewById(R.id.login_user_edit);
		passwordEdt = (EditText)findViewById(R.id.login_password_edit);		
	}

	/* (non-Javadoc)
	 * @see com.hithing.app.DialogActivity#getDataFromDialog()
	 */
	@Override
	protected Intent getDataFromDialog() {
		int userId = 0;
		if(!TextUtils.isEmpty(userIdEdt.getText())){
			userId = Integer.parseInt(userIdEdt.getText().toString());						
		}
		Intent intent = new Intent();
		intent.putExtra(INTENT_EXTRA_KEY_USER, userId);
		intent.putExtra(INTENT_EXTRA_KEY_PASSWORD, passwordEdt.getText().toString());
		return intent;
	}

	/* (non-Javadoc)
	 * @see com.hithing.app.DialogActivity#loadViewStub()
	 */
	@Override
	protected void loadViewStub() {
		content.setLayoutResource(R.layout.act_login_content);
		content.inflate();

	}

}
