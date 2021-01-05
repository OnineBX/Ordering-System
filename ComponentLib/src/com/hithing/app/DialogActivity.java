/**========================================
 * File:	DialogActivity.java
 * Package:	com.hithing.app
 * Create:	by Leopard
 * Date:	2012-2-7:上午11:13:22
 **======================================*/
package com.hithing.app;

import com.hithing.hsc.component.R;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewStub;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.FrameLayout.LayoutParams;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * <p>DialogActivity</p>
 * 	
 * <p>功能描述</p>
 *
 * @author Leopard
 * 
 */
public abstract class DialogActivity extends Activity {
	
	protected ViewStub 		content;						//用于载入对话框内容
	protected TextView 		title;							//标题
	private   LinearLayout 	rootContainer;					//根视图容器

	public void onClick(View v){
		String text = ((Button)v).getText().toString();
		
		//点击确定按钮
		if(text == getString(R.string.comlib_btn_ok_text)){
			this.setResult(RESULT_OK, getDataFromDialog());
		}
		
		if(text == getString(R.string.comlib_btn_cancel_text)){
			this.setResult(RESULT_CANCELED, null);
		}		
		this.finish();
		
	}
	
	protected abstract Intent getDataFromDialog();
	/* (non-Javadoc)
	 * @see android.app.Activity#onCreate(android.os.Bundle)
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.cmn_dialog_main);
		
		content = (ViewStub)findViewById(R.id.dialog_stub_content);
	 	title	= (TextView)findViewById(R.id.dialog_title);

//	 	rootContainer = (LinearLayout)((ViewGroup)((ViewGroup)content.getRootView()).getChildAt(0)).getChildAt(0);	 //for android 2.2
	 	rootContainer = (LinearLayout)content.getParent().getParent();
		loadViewStub();
				
	}

	protected void setDialogWidth(int width) {
		Log.e("rootView", String.valueOf(rootContainer.getHeight()));
		rootContainer.setLayoutParams(new FrameLayout.LayoutParams(width,LayoutParams.WRAP_CONTENT));
	}
	
	protected abstract void loadViewStub();
	
	public interface ICallerDisposable{
		void disposeResult();
	}

}
