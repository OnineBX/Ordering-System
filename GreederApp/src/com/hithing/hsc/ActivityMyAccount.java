package com.hithing.hsc;

import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import com.google.protobuf.GeneratedMessage;
import com.hithing.app.ProtobufActivity;
import com.hithing.hsc.bll.control.CashCtrl;
import com.hithing.hsc.bll.util.CashContext;
import com.hithing.hsc.bll.util.MomsgFactory.MomsgType;
import com.hithing.hsc.entity.AccountItemValue;
import com.hithing.hsc.entity.MoMsg.CMsgCheckAccountRsp;
import com.hithing.hsc.entity.MoMsg.CMsgCheckAccountRsp.CMsgBillInfo;
import com.hithing.hsc.entity.MoMsg.CMsgCheckAccountRsp.CMsgTotalInfo;
import com.hithing.widget.AccountAdapter;

import android.os.Bundle;
import android.app.Activity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;
import android.widget.TextView;

public class ActivityMyAccount extends ProtobufActivity {

	private CashContext          			cashContext		= CashContext.INSTANCE;			//收银上下文
	
	private Map<String, AccountItemValue> 	accountData;									//帐目数据
	private AccountAdapter 					adapter;
	private TextView 						summaryTxv;										//对账信息
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_myaccount_content);
        
       //初始化标题栏
       TextView title  = (TextView) findViewById(R.id.cmn_txv_title);  
       String titleFormat = getString(R.string.cashing_txv_title_text_format);
 		
       title.setText(String.format(titleFormat, cashContext.getCashSpotName(),
 				cashContext.getCurrentShiftName(),cashContext.getCurrentCasherName()));
       CashCtrl cashCtrl = new CashCtrl(cashContext.getCashSpotId(), cashContext.getCurrentCasherId());
       cashCtrl.viewAccounts();
       
       ListView accoutLsv =  (ListView)findViewById(R.id.myaccount_lsv_accountitems);
       accountData = new LinkedHashMap<String, AccountItemValue>();
       List<Integer> childIds = new LinkedList<Integer>();
       childIds.add(R.id.myaccount_txv_paycode);
       childIds.add(R.id.myaccount_txv_billcode);
       childIds.add(R.id.myaccount_txv_table);
       childIds.add(R.id.myaccount_txv_person);
       childIds.add(R.id.myaccount_txv_foundingtime);
       childIds.add(R.id.myaccount_txv_paybilltime);
       childIds.add(R.id.myaccount_txv_money);
       childIds.add(R.id.myaccount_txv_shift);
       adapter = new AccountAdapter(this, accountData, String.class, R.layout.act_myaccount_account_item, childIds);
       accoutLsv.setAdapter(adapter);       
       summaryTxv = (TextView)findViewById(R.id.myaccount_txv_summary);
       
    }

	@Override
	public void onMomsgResult(MomsgType rspType, GeneratedMessage msg) {
		switch (rspType) {
		case INTERVALSYNC_RSP:
			Log.d("ActivityMyAccount.onMomsgResult", "INTERVALSYNC_RSP");
			break;
		case CHECKACCOUNT_RSP:
			Log.d("ActivityMyAccount.onMomsgResult", "CHECKACCOUNT_RSP");			
			CMsgCheckAccountRsp caRsp = (CMsgCheckAccountRsp)msg;			
			if(caRsp.hasStatus()){				
				if(caRsp.getStatus() == MOMSG_RSP_STATUS_SUCCESS){
					AccountItemValue value;
					for (CMsgBillInfo info : caRsp.getBillinfoList()) {
						value = new AccountItemValue.Builder(info.getPayBillCode(), info.getTableName().toStringUtf8()).
													 setBillCode(info.getBillCode()).
													 setPerson(info.getBillCustomers()).
													 setFoundingTime(info.getBillTime()).
													 setPayBillTime(info.getPayBillTime()).
													 setMoney(info.getPayMoney()).
													 setShift(info.getShiftName().toStringUtf8()).
													 build();
						accountData.put(info.getPayBillCode(), value);						
					}					
					adapter.refresh();
					StringBuilder details = new StringBuilder();
					for(CMsgTotalInfo tInfo : caRsp.getTotalinfoList()) {
						details.append(String.format("%s:%s", tInfo.getPayName().toStringUtf8(),tInfo.getPayMoney()));
						Log.e("onMomsgResult", "money=" + tInfo.getPayMoney());
						details.append("\n");
					}
					summaryTxv.setText(String.format(getString(R.string.myaccount_txv_summary_format), 
							caRsp.getBusCasSpoTime(),caRsp.getBillinfoCount(),caRsp.getTotalMoney(),details));
				}
			}			
			break;
		}
		
	}
   
}
