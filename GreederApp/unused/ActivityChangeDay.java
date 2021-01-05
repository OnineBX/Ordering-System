package com.hithing.hsc;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import com.hithing.app.DialogActivity;
import com.hithing.hsc.bll.android.AndGrdDaoManager;
import com.hithing.hsc.bll.manage.GreederDaoManager;
import com.hithing.hsc.bll.util.WorkContext;
import com.hithing.hsc.entity.OrderItemValue.RemarkItem;
import com.hithing.hsc.global.StringKey;

public class ActivityChangeDay extends DialogActivity {

	private   int                tableId;                  
	
	private   GreederDaoManager	 grdManager;			    //Greeder框架Dao管理器
	
	private   TextView           cashingTable;              //收银台
	private   TextView           changeDate;                // 下一个营业日
	
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		final Intent intent  = getIntent();
		grdManager = new AndGrdDaoManager(this);
		
		String cashingHall  = WorkContext.getInstance().getHall().name;
	    RemarkItem    tableItem  = (RemarkItem) intent.getSerializableExtra(StringKey.INTENT_EXTRA_KEY_TABLE);
	    
		title.setText(tableItem.content);
			
		tableId = intent.getExtras().getInt(StringKey.INTENT_EXTRA_KEY_TABLE);
			
			
		SimpleDateFormat s   = new SimpleDateFormat("yyyy-MM-dd");
		Calendar now =Calendar.getInstance(); 
		now.set(Calendar.DATE,now.get(Calendar.DATE) + 1);
		String dateStr = s.format(now.getTime());   

		
		cashingTable   = (TextView) findViewById(R.id.txv_changeday_table);
	    changeDate     =  (TextView) findViewById(R.id.txv_changeday_date);
		cashingTable.setText(cashingHall);
		changeDate.setText(dateStr);
		
	}

	@Override
	protected void loadViewStub() {
		content.setLayoutResource(R.layout.act_changeday_main);
		content.inflate();
	}

	@Override
	protected Intent getDataFromDialog() {
		// TODO Auto-generated method stub
		return null;
	}

	
}
