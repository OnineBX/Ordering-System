/**========================================
 * File:	GreederSettingsHelper.java
 * Package:	com.hithing.hsc.bll.android
 * Create:	by Leopard
 * Date:	2011-11-30:下午01:41:35
 **======================================*/
package com.hithing.hsc.bll.android;

import java.sql.SQLException;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.hithing.hsc.dal.database.GreederSqliteHelper;
import com.hithing.hsc.dataentity.HallEntity;
import com.hithing.hsc.dataentity.HallSortEntity;
import com.j256.ormlite.android.apptools.OpenHelperManager;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.preference.PreferenceManager;

/**
 * <p>GreederSettingsHelper</p>
 * 	
 * <p>个性化配置类,用于管理系统个性化信息</p>
 *
 * @author Leopard
 * 
 */
public enum GreederSettingsHelper {
	INSTANCE;
	
	private final String		PREFERENCE_HOST_KEY 	= "pref_host";		//偏好数据键-主机
	private final String		PREFERENCE_PORT_KEY 	= "pref_port";		//偏好数据键-端口
	private final String		PREFERENCE_COMPANY_KEY 	= "pref_company";	//偏好数据键-企业编码
	private final String		PREFERENCE_TERMINAL_KEY	= "pref_terminal";	//偏好数据键-终端编码
	private final String		PREFERENCE_AUTHCODE_KEY	= "pref_authcode";	//偏好数据键-终端授权码
	
	private Context 			context; 
	private GreederSqliteHelper sqliteHelper;
	private SharedPreferences 	preferences;
	private Editor				editor;
	/**
	 * <p>GreederSettingsHelper</p>
	 *
	 * <p>构造函数</p>
	 */
	GreederSettingsHelper() {				
		
	}

	/**
	 * 
	 * <p>initInstance</p>
	 *
	 * <p>初始化GreederSettingsHelper实例</p>
	 * 
	 * @param context
	 */
	public void initInstance(Context context){
		this.context  	= context;
		preferences 	= PreferenceManager.getDefaultSharedPreferences(context);
		editor			= preferences.edit();
	}
	
	/**	 
	 * <p>getHallSortSelections</p>
	 *
	 * <p>获得可供选择的分厅大类信息</p>
	 * 
	 * @return 返回分厅大类Map集合
	 * @throws SQLException 
	 */
	public Map<CharSequence, CharSequence> getHallSortSelections() throws SQLException{		
		
		sqliteHelper = OpenHelperManager.getHelper(context, GreederSqliteHelper.class);			
		Map<CharSequence, CharSequence> hallSorts = new LinkedHashMap<CharSequence, CharSequence>();
		List<HallSortEntity> list = sqliteHelper.getDao(HallSortEntity.class).queryForAll();
		for(HallSortEntity ent : list){
			hallSorts.put(ent.getName(), String.valueOf(ent.getId()));
		}
		OpenHelperManager.releaseHelper();		
		return hallSorts;
	}
	
	/**	 
	 * <p>getHallSelections</p>
	 *
	 * <p>获得某分厅大类下可供选择的分厅信息</p>
	 * @param sort 分厅大类标识
	 * @return 返回分厅Map集合
	 * @throws SQLException 
	 */
	public Map<CharSequence, CharSequence> getHallSelections(int sort) throws SQLException{
		
		sqliteHelper = OpenHelperManager.getHelper(context, GreederSqliteHelper.class);	
		
		Map<CharSequence, CharSequence> halls = new LinkedHashMap<CharSequence, CharSequence>();
		List<HallEntity> list = sqliteHelper.getDao(HallEntity.class).queryForEq("fk_hs_id", sort);
		for (HallEntity ent : list) {
			halls.put(ent.getName(), String.valueOf(ent.getId()));
		}			
		OpenHelperManager.releaseHelper();
		return halls;
	}
	
	public String getServer(String defServer){		
		
		return preferences.getString(PREFERENCE_HOST_KEY, defServer);
	}
	
	public int getPort(int defPort){
		int port = Integer.parseInt(preferences.getString(PREFERENCE_PORT_KEY, String.valueOf(defPort)));
		return port;		
	}
	
	public String getCompany(String defCompany){		
		return preferences.getString(PREFERENCE_COMPANY_KEY, defCompany);
	}
	
	public String getTerminal(String defTerm){		
		return preferences.getString(PREFERENCE_TERMINAL_KEY, defTerm);
	}
	
	public String getAuthCode(String defAuth){
		return preferences.getString(PREFERENCE_AUTHCODE_KEY, defAuth);
	}
		
	public int getHall(int defHall){
		int hall = Integer.parseInt(preferences.getString("pref_hall", String.valueOf(defHall)));
		return hall;
	}
	
	public void setHost(String host){
		editor.putString(PREFERENCE_HOST_KEY, host);
		editor.commit();
	}
	
	public void setPort(int port){
		editor.putString(PREFERENCE_PORT_KEY, String.valueOf(port));
		editor.commit();
	}
	
	public void setCompany(String company){
		editor.putString(PREFERENCE_COMPANY_KEY, company);
		editor.commit();
	}
	
	public void setTerminal(String terminal){
		editor.putString(PREFERENCE_TERMINAL_KEY, terminal);
		editor.commit();
	}
	
	public void setAuthcode(String authcode){
		editor.putString(PREFERENCE_AUTHCODE_KEY, authcode);
		editor.commit();
	}
	
	/**
	 * <p>clear</p>
	 *
	 * <p>清空设置</p>
	 *
	 */
	public void clear(){
		editor.clear();
	}
}
