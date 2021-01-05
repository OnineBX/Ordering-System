/**========================================
 * File:	ActivityOperateFood.java
 * Package:	com.hithing.hsc.order
 * Create:	by Leopard
 * Date:	2012-2-7:上午10:40:55
 **======================================*/
package com.hithing.hsc;

import java.util.LinkedList;
import java.util.List;

import com.hithing.app.DialogActivity;
import com.hithing.hsc.bll.android.AndGrdDaoManager;
import com.hithing.hsc.bll.manage.GreederDaoManager;
import com.hithing.hsc.bll.manage.OperateReasonManager;
import com.hithing.hsc.dataentity.OperateReasonEntity;
import com.hithing.hsc.dataentity.OperateReasonEntity.OperateType;
import com.hithing.hsc.entity.AdapterItem.OptionalAdapterItem;
import com.hithing.hsc.global.StringKey;
import com.hithing.widget.AbstractOptionalAdapter.CheckType;
import com.hithing.widget.AbstractOptionalAdapter.Checker;
import com.hithing.widget.SimpleOptionalAdapter;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;

/**
 * <p>ActivityOperateFood</p>
 * 	
 * <p>功能描述</p>
 *
 * @author Leopard
 * 
 */
public class ActivityOperateFood extends DialogActivity {
	public static final String		INTENT_EXTRA_KEY_REASON = "reason";
	
	private final int 				DEFAULT_COUNT 			= 1;
	private final int				REASON_DEFAULT_ID 		= 0;
	private final int				REASON_DEFAULT_POSITION = 0;
	private final int				REASON_FIRST_POSITION	= 0;	//保存原因的列表项位置			
	
	private OperateType				type;							//操作类型
	private int 					count;							//要操作的菜品数量
	private SimpleOptionalAdapter			reasonAdapter;					//操作原因数据适配器
	private EditText				reasonEdt;
	
	public void onRadioClick(View v){
		int position = Integer.parseInt(v.getTag(R.id.optional_item_tag_position).toString());
		Checker checker = reasonAdapter.getChecker(CheckType.CHECK_TYPE_SINGLECHECK);
		checker.check(position, true);
		
		if(position == REASON_DEFAULT_POSITION){
			reasonEdt.setVisibility(View.VISIBLE);
		}else {
			reasonEdt.setVisibility(View.GONE);
		}
		
	}

	@Override
	protected Intent getDataFromDialog() {
		Intent intent = new Intent();
		intent.putExtra(StringKey.INTENT_EXTRA_KEY_COUNT, count);

		OptionalAdapterItem reason = reasonAdapter.getCheckedData().get(REASON_FIRST_POSITION);
		String reasonString = getString(R.string.opefood_edt_reason_default_text);
		if(reason.getId() == REASON_DEFAULT_ID){
			if(!TextUtils.isEmpty(reasonEdt.getText())){
				reasonString = reasonEdt.getText().toString();
			}			
		}else {
			reasonString = reason.getName();
		}
		intent.putExtra(INTENT_EXTRA_KEY_REASON, reasonString);
		intent.putExtra(StringKey.INTENT_EXTRA_KEY_TYPE, type);
		return intent;
	}

	@Override
	protected void loadViewStub() {				
		content.setLayoutResource(R.layout.act_operatefood_content);
		content.inflate();		
	}

	/* (non-Javadoc)
	 * @see com.hithing.app.DialogActivity#onCreate(android.os.Bundle)
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		
		Intent intent = getIntent();
		
		final String 	food 	= intent.getStringExtra(StringKey.INTENT_EXTRA_KEY_FOOD);	
		type					= (OperateType)intent.getSerializableExtra(StringKey.INTENT_EXTRA_KEY_TYPE);		
		final int		total 	= intent.getIntExtra(StringKey.INTENT_EXTRA_KEY_COUNT, DEFAULT_COUNT);
		final String	unit	= intent.getStringExtra(StringKey.INTENT_EXTRA_KEY_UNIT);		
		
		final String	format	= getTitleFormat(type);				
		count = total;
		if(!TextUtils.isEmpty(format)){		
			title.setText(String.format(format, food, total, unit, count, unit));
		}
		
		//显示数量设置控件和信息提示
		if(count > DEFAULT_COUNT){			
			
			SeekBar	countSkb = (SeekBar)findViewById(R.id.operatefood_skb_count);
			countSkb.setVisibility(View.VISIBLE);
			countSkb.setMax(count);
			countSkb.setProgress(count);
			countSkb.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
				
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
					count = progress;
					title.setText(String.format(format, food, total, unit, count, unit));					
				}
			});
		}
		
		//载入原因
		GridView reasonGdv = (GridView)findViewById(R.id.operatefood_gdv_reason);
		List<OperateReasonEntity> reasonList = getReasonList(type);
		LinkedList<OptionalAdapterItem> reasonData 		= new LinkedList<OptionalAdapterItem>();
		LinkedList<OptionalAdapterItem> dCheckedData 	= new LinkedList<OptionalAdapterItem>();
		OptionalAdapterItem defReasonItem = new OptionalAdapterItem(REASON_DEFAULT_ID, getString(R.string.opefood_rdb_reason_text));
		defReasonItem.setChecked(true);
		reasonData.add(defReasonItem);
		dCheckedData.add(defReasonItem);
		for(OperateReasonEntity ent : reasonList){
			
			reasonData.add(new OptionalAdapterItem(ent.getId(), ent.getTitle()));
		}
		reasonAdapter = new SimpleOptionalAdapter(this, reasonData, dCheckedData, R.layout.cmn_child_radio_item);
		reasonGdv.setAdapter(reasonAdapter);
		
		reasonEdt = (EditText)findViewById(R.id.operatefood_edt_reason);
	}		
	
	//根据操作类型得到标题格式
	private String getTitleFormat(OperateType type){
		switch (type) {
		case present:
			return getString(R.string.opefood_txv_title_format_present);
		case cancel:
			return getString(R.string.opefood_txv_title_format_cancel);	
		default:
			return null;
		}
	}
	
	//根据操作类型得到原因
	private List<OperateReasonEntity> getReasonList(OperateType type){
		GreederDaoManager grdManager = new AndGrdDaoManager(this);
		OperateReasonManager reasonMgr = new OperateReasonManager(grdManager.getOperateReasonDao());		
		switch (type) {
		case present:
			return reasonMgr.getPresentReason();			
		case cancel:
			return reasonMgr.getCancelReason();
		default:
			return new LinkedList<OperateReasonEntity>();
		}		
	}

}
