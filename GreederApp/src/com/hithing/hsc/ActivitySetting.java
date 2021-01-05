/**========================================
 * File:	ActivitySetting.java
 * Package:	com.hithing.hsc.app
 * Create:	by Leopard
 * Date:	2011-11-30:下午02:40:24
 **======================================*/
package com.hithing.hsc;

import java.util.Map;

import com.hithing.hsc.R;
import com.hithing.hsc.bll.android.GreederSettingsHelper;
import com.hithing.hsc.bll.util.CompoundItem;
import com.hithing.hsc.bll.util.WorkContext;

import android.os.Bundle;
import android.preference.EditTextPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.Preference.OnPreferenceChangeListener;

/**
 * <p>ActivitySetting</p>
 * 	
 * <p>设置系统运行所需要的参数以及个性化信息</p>
 *
 * @author Leopard
 * 
 */
public class ActivitySetting extends PreferenceActivity implements OnPreferenceChangeListener{	
	private final String PREFERENCE_HOST_KEY 		= "pref_host";					//偏好设置键值-服务器主机名
	private final String PREFERENCE_PORT_KEY 		= "pref_port";					//偏好设置键值-服务器端口
	private final String PREFERENCE_HALLSORT_KEY 	= "pref_hallsort";				//偏好设置键值-分厅大类
	private final String PREFERENCE_HALL_KEY		= "pref_hall";					//偏好设置键值-分厅
	
	private GreederSettingsHelper 	settingHelper 	= GreederSettingsHelper.INSTANCE;		//设置帮助者
	private WorkContext				wContext		= WorkContext.getInstance();			//工作环境上下文
	
	public boolean onPreferenceChange(Preference preference, Object newValue) {
		try{
			String key = preference.getKey();			
			//修改服务器主机的情况
			if(key.equalsIgnoreCase(PREFERENCE_HOST_KEY)){
				
			}
			
			//修改服务器端口的情况
			if(key.equalsIgnoreCase(PREFERENCE_PORT_KEY)){
				
			}			
			
			//修改分厅大类的情况
			if(key.equalsIgnoreCase(PREFERENCE_HALLSORT_KEY)){
				initHallSelections(newValue.toString());
			}
			
			//修改分厅的情况
			if(key.equalsIgnoreCase(PREFERENCE_HALL_KEY)){
				ListPreference lstpHall = (ListPreference)preference;	
				String newHallId 		= newValue.toString();	
				CompoundItem newHall 	= new CompoundItem();
				newHall.id 				= Integer.valueOf(newHallId);			
				newHall.name 			= lstpHall.getEntries()[lstpHall.findIndexOfValue(newHallId)].toString();
				wContext.setHall(newHall);				
			}	
			return true;
		}catch (Exception e) {
			return false;
		}		
	}	
		

	/**	 
	 * <p>initHallSelections</p>
	 *
	 * <p>初始化分厅选择项</p>
	 * @param sort 分厅大类标识
	 */
	private void initHallSelections(String sort){
								
		try {
			ListPreference lstpHall = (ListPreference)findPreference(PREFERENCE_HALL_KEY);
			Map<CharSequence, CharSequence> halls = settingHelper.getHallSelections(Integer.parseInt(sort));
			if(halls.isEmpty()){
				lstpHall.setEnabled(false);						
				lstpHall.setValue(getString(R.string.setting_pref_list_null));		
			}else{
				lstpHall.setEnabled(true);
				CharSequence[] a = new CharSequence[0];
				lstpHall.setEntries(halls.keySet().toArray(a));
				lstpHall.setEntryValues(halls.values().toArray(a));	
				//未设置分厅时,则将第一个分厅设置为当前分厅
				if(lstpHall.getValue().equalsIgnoreCase(getString(R.string.setting_pref_list_null))){	
					lstpHall.setValueIndex(Integer.parseInt(getString(R.string.setting_pref_list_default)));
				}
				
			}	
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);
		
		addPreferencesFromResource(R.xml.setting);
				
		try {
			//设置偏好项监听
			EditTextPreference edtpHost = (EditTextPreference)findPreference(PREFERENCE_HOST_KEY);
			edtpHost.setOnPreferenceChangeListener(this);
			EditTextPreference edtpPort = (EditTextPreference)findPreference(PREFERENCE_PORT_KEY);
			edtpPort.setOnPreferenceChangeListener(this);
			//初始化分厅大类可选择项数据
			ListPreference lstpHallsort = (ListPreference)findPreference(PREFERENCE_HALLSORT_KEY);
			lstpHallsort.setOnPreferenceChangeListener(this);		
			Map<CharSequence, CharSequence> hallsorts = settingHelper.getHallSortSelections();			
			if(hallsorts.isEmpty()){
				lstpHallsort.setEnabled(false);
			}else {
				CharSequence[] a = new CharSequence[0];
				lstpHallsort.setEntries(hallsorts.keySet().toArray(a));
				lstpHallsort.setEntryValues(hallsorts.values().toArray(a));
			}
			
			ListPreference lstpHall = (ListPreference)findPreference(PREFERENCE_HALL_KEY);
			lstpHall.setOnPreferenceChangeListener(this);		
			//初始化分厅可选择项数据		
			initHallSelections(lstpHallsort.getValue());
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}		
	}	

}
