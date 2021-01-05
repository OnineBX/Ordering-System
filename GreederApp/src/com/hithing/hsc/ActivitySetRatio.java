/**========================================
 * File:	ActivitySetRatio.java
 * Package:	com.hithing.hsc
 * Create:	by Leopard
 * Date:	2012-3-19:下午2:13:34
 **======================================*/
package com.hithing.hsc;

import java.math.BigDecimal;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;

import com.hithing.app.DialogActivity;
import com.hithing.hsc.global.StringKey;

/**
 * <p>ActivitySetRatio</p>
 * 	
 * <p>设置比率活动,如折扣率</p>
 *
 * @author Leopard
 * 
 */
public final class ActivitySetRatio extends DialogActivity {
	public final static String INTENT_EXTRA_KEY_RATIO = "ratio";
	
	private String 		formater;
	private String		name;
	private SeekBar 	ratioSkb;
	private int			ratio;
	private BigDecimal 	zoom;							//缩放倍数	
	
	public void onAdjust(View v){		
		switch (v.getId()) {
		case R.id.setratio_btn_count_add:
			ratioSkb.setProgress(++ratio);					
			break;
		case R.id.setratio_btn_count_reduce:
			if(ratio > 0){
				ratioSkb.setProgress(--ratio);				
			}
			break;		
		}		
	}
	
	/* (non-Javadoc)
	 * @see com.hithing.app.DialogActivity#onCreate(android.os.Bundle)
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {		
		super.onCreate(savedInstanceState);
		
		formater 	= getString(R.string.setratio_title_text_format);
		zoom	 	= new BigDecimal("100");		
		final Intent intent = getIntent();
		
		name	= intent.getStringExtra(StringKey.INTENT_EXTRA_KEY_TITLE);		
		ratio	= string2int(intent.getStringExtra(INTENT_EXTRA_KEY_RATIO));		
		
		ratioSkb = (SeekBar)findViewById(R.id.setratio_skb_count);
		ratioSkb.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
			
			@Override
			public void onStopTrackingTouch(SeekBar seekBar) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void onStartTrackingTouch(SeekBar seekBar) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void onProgressChanged(SeekBar seekBar, int progress,
					boolean fromUser) {
				ratio = progress;
				title.setText(String.format(formater, name, int2string(ratio)));
			}
		});		
		
		if(ratio == 0){
			title.setText(String.format(formater, name, int2string(ratio)));
		}else{
			ratioSkb.setProgress(ratio);
		}
	}

	/* (non-Javadoc)
	 * @see com.hithing.app.DialogActivity#getDataFromDialog()
	 */
	@Override
	protected Intent getDataFromDialog() {			
		Intent intent = new Intent();
		intent.putExtra(INTENT_EXTRA_KEY_RATIO, int2string(ratio));
		return intent;
	}

	/* (non-Javadoc)
	 * @see com.hithing.app.DialogActivity#loadViewStub()
	 */
	@Override
	protected void loadViewStub() {
		content.setLayoutResource(R.layout.act_setratio_content);
		content.inflate();

	}

	//将比率转化为整数
	private int string2int(String value){
		BigDecimal result = new BigDecimal(value);
		return result.multiply(zoom).intValue();
	}
	
	//将整数转化为比率
	private String int2string(int value){
		BigDecimal result = new BigDecimal(value);
		return result.divide(zoom).setScale(2).toString();
	}
}
