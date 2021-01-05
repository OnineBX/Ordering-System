/**========================================
 * File:	ResourceVersionManager.java
 * Package:	com.hithing.hsc.bll.manage
 * Create:	by Leopard
 * Date:	2011-12-26:下午12:48:41
 **======================================*/
package com.hithing.hsc.bll.manage;

import java.io.IOException;
import java.net.UnknownHostException;
import java.sql.SQLException;
import java.util.List;
import java.util.concurrent.Callable;

import android.graphics.Shader.TileMode;
import android.util.Log;

import com.hithing.hsc.bll.manage.GreederDaoManager;
import com.hithing.hsc.bll.util.ITransportable.IAsynSendable;
import com.hithing.hsc.bll.util.MomsgFactory;
import com.hithing.hsc.bll.util.MomsgFactory.MomsgType;
import com.hithing.hsc.dal.network.Client.EoprMomsg;
import com.hithing.hsc.dataentity.DinTabTypeTimItemEntity;
import com.hithing.hsc.dataentity.DinnerTableEntity;
import com.hithing.hsc.dataentity.DinnerTableTypeEntity;
import com.hithing.hsc.dataentity.DemandEntity;
import com.hithing.hsc.dataentity.FoodEntity;
import com.hithing.hsc.dataentity.FoodFrsortEntity;
import com.hithing.hsc.dataentity.FoodMainSortEntity;
import com.hithing.hsc.dataentity.FoodPriceEntity;
import com.hithing.hsc.dataentity.FoodRecipeEntity;
import com.hithing.hsc.dataentity.FoodRecipeSortEntity;
import com.hithing.hsc.dataentity.FoodSubSortEntity;
import com.hithing.hsc.dataentity.FoodUnitEntity;
import com.hithing.hsc.dataentity.HallEntity;
import com.hithing.hsc.dataentity.HallFoodMainSortEntity;
import com.hithing.hsc.dataentity.HallSortEntity;
import com.hithing.hsc.dataentity.ProducePrintEntity;
import com.hithing.hsc.dataentity.TimeItemEntity;
import com.hithing.hsc.dataentity.TimeItemEntity.TimeItemUnit;
import com.hithing.hsc.entity.MoMsg.CMsgRefreshResReq;
import com.hithing.hsc.entity.MoMsg.CMsgRefreshResReq.CMsgTermVersion;
import com.hithing.hsc.entity.MoMsg.CMsgRefreshResRsp;
import com.hithing.hsc.entity.MoMsg.CMsgRefreshResRsp.CMsgDinTabType;
import com.hithing.hsc.entity.MoMsg.CMsgRefreshResRsp.CMsgDinTabTypeTimeItem;
import com.hithing.hsc.entity.MoMsg.CMsgRefreshResRsp.CMsgDinTable;
import com.hithing.hsc.entity.MoMsg.CMsgRefreshResRsp.CMsgFood;
import com.hithing.hsc.entity.MoMsg.CMsgRefreshResRsp.CMsgFoodFoodRecType;
import com.hithing.hsc.entity.MoMsg.CMsgRefreshResRsp.CMsgFoodMainSort;
import com.hithing.hsc.entity.MoMsg.CMsgRefreshResRsp.CMsgFoodPrice;
import com.hithing.hsc.entity.MoMsg.CMsgRefreshResRsp.CMsgFoodRecType;
import com.hithing.hsc.entity.MoMsg.CMsgRefreshResRsp.CMsgFoodRecipe;
import com.hithing.hsc.entity.MoMsg.CMsgRefreshResRsp.CMsgFoodSubSort;
import com.hithing.hsc.entity.MoMsg.CMsgRefreshResRsp.CMsgFoodUnit;
import com.hithing.hsc.entity.MoMsg.CMsgRefreshResRsp.CMsgFooddemand;
import com.hithing.hsc.entity.MoMsg.CMsgRefreshResRsp.CMsgHall;
import com.hithing.hsc.entity.MoMsg.CMsgRefreshResRsp.CMsgHallFmsort;
import com.hithing.hsc.entity.MoMsg.CMsgRefreshResRsp.CMsgHallSort;
import com.hithing.hsc.entity.MoMsg.CMsgRefreshResRsp.CMsgOpeReason;
import com.hithing.hsc.entity.MoMsg.CMsgRefreshResRsp.CMsgProsecPort;
import com.hithing.hsc.entity.MoMsg.CMsgRefreshResRsp.CMsgTimeItem;
import com.hithing.hsc.dataentity.OperateReasonEntity.OperateType;
import com.hithing.hsc.dataentity.OperateReasonEntity;
import com.j256.ormlite.dao.Dao;

/**
 * <p>ResourceVersionManager</p>
 * 	
 * <p>资源版本控制类,用于维护本地数据库数据与服务器数据一致性</p>
 *
 * @author Leopard
 * 
 */
public final class ResourceVersionManager implements IAsynSendable{		
	
	private final int 			REFRESH_TYPE_INSERT 	= 1;						//插入刷新类型
	private final int 			REFRESH_TYPE_UPDATE 	= REFRESH_TYPE_INSERT + 1;	//修改刷新类型
	private final int 			REFRESH_TYPE_FREEZE 	= REFRESH_TYPE_UPDATE + 1;	//冻结刷新类型
	private final int 			REFRESH_TYPE_DELETE 	= REFRESH_TYPE_FREEZE + 1;	//删除刷新类型
	
	private final int			DEFAULT_REFRESH_COUNT 	= 0;						//默认刷新数目
	
	private GreederDaoManager 	grdManager;
	private CMsgRefreshResRsp 	rsp;
	
	/**
	 * <p>ResourceVersionManager</p>
	 *
	 * <p>构造函数</p>
	 */
	public ResourceVersionManager() {
		
	}
	
	/**
	 * <p>ResourceVersionManager</p>
	 *
	 * <p>构造函数</p>
	 * @param manager Greeder框架数据访问对象管理类
	 * @param rsp	     资源刷新返回消息
	 */
	public ResourceVersionManager(GreederDaoManager manager, CMsgRefreshResRsp rsp){
		this.grdManager = manager;
		this.rsp		= rsp;
	}
	
	/**
	 * <p>refreshResource</p>
	 *
	 * <p>资源刷新</p>
	 * 
	 * @param executor 用于并发执行餐台开台的接口
	 * @param version  版本信息
	 */
	public void refreshResource(final CMsgTermVersion version){
		executor.execute(new Runnable() {			
			@Override
			public void run() {				
				try {
					CMsgRefreshResReq.Builder 	builder = MomsgFactory.createMomsgBuilder(MomsgType.REFRESHRES_REQ);					
					CMsgRefreshResReq 			req		= builder.setTversion(version).build();
					client.sendDataAsyn(MomsgType.REFRESHRES_REQ.getValue(), req.toByteArray());					
				} catch (UnknownHostException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * <p>refreshResourceForResult</p>
	 *
	 * <p>同步资源刷新,并返回结果</p>
	 * 
	 * @param version
	 */
	public static CMsgRefreshResRsp refreshResourceForResult(CMsgTermVersion version){		
		try {
			CMsgRefreshResReq.Builder 	builder = MomsgFactory.createMomsgBuilder(MomsgType.REFRESHRES_REQ);
			CMsgRefreshResReq 			req		= builder.setTversion(version).build();
			client.sendDataAsyn(MomsgType.REFRESHRES_REQ.getValue(), req.toByteArray());
			EoprMomsg msg = client.receiveDataAsyn();
			return (CMsgRefreshResRsp) MomsgFactory.createMomsg(MomsgType.valueOf(msg.getType()), msg.getMomsg());
		} catch (IOException e) {			
			e.printStackTrace();
			return null;
		}
		
	}
	
	/**
	 * <p>getRefreshItemCount</p>
	 *
	 * <p>获得刷新数据条目的数量</p>
	 * 
	 * @return 要刷新的数据条目总数
	 */
	public int getRefreshItemCount(){
		int result = rsp.getHallSortCount() 		+ rsp.getHallCount() 			+ rsp.getDinTabTypeCount() 	+ 
					 rsp.getDinTableCount()			+ rsp.getFoodMainSortCount()	+ rsp.getHallFmsortCount()	+ 
					 rsp.getFoodSubSortCount()		+ rsp.getFoodUnitCount()		+ rsp.getFoodCount()		+ 
					 rsp.getFoodPriceCount()		+ rsp.getFoodRecTypeCount()		+ rsp.getFoodRecipeCount()	+ 
					 rsp.getFoodFoodRecTypeCount()	+ rsp.getFoodDemandCount()		+ rsp.getOpeReasonCount()	+
					 rsp.getProsecPortCount()		+ rsp.getTimeItemCount()		+ rsp.getDtttitemCount();	
		return result;
	}
	
	/**
	 * <p>RefreshHallSort</p>
	 *
	 * <p>刷新分厅大类数据</p>
	 * 
	 * @return 刷新数目
	 * @throws Exception 
	 */
	@SuppressWarnings("unchecked")
	public int RefreshHallSort() throws Exception{		
				
		final Dao<HallSortEntity, Integer> dao = grdManager.getHallSortDao();
		return	dao.callBatchTasks(new Callable<Integer>() {
	
					@Override
					public Integer call() throws Exception {
						List<CMsgHallSort> 	data 	= rsp.getHallSortList();
						HallSortEntity 		hsEnt;
						int 				count	= 0;
						for(CMsgHallSort hs : data){
							switch (hs.getActype()) {
								case REFRESH_TYPE_INSERT:
									hsEnt = new HallSortEntity();
									hsEnt.setDao(dao);
									hsEnt.setId(hs.getId());
									hsEnt.setName(hs.getName().toStringUtf8());					
									hsEnt.create();		
									break;
								case REFRESH_TYPE_UPDATE:
									hsEnt = dao.queryForId(hs.getId());
									hsEnt.setDao(dao);
									hsEnt.setName(hs.getName().toStringUtf8());
									hsEnt.update();
									break;
								case REFRESH_TYPE_DELETE:
									hsEnt = dao.queryForId(hs.getId());
									hsEnt.setDao(dao);
									hsEnt.delete();
									break;
							}	
							count++;
						}
						return count;
					}
					
				});					
	}		
	
	/**
	 * <p>refreshHall</p>
	 *
	 * <p>刷新分厅数据</p>
	 * 
	 * @return 刷新数目
	 * @throws Exception 
	 */
	@SuppressWarnings("unchecked")
	public int refreshHall() throws Exception{		
		
		final Dao<HallEntity, Integer> 		hallDao = grdManager.getHallDao();			
		return	hallDao.callBatchTasks(new Callable<Integer>() {

					@Override
					public Integer call() throws Exception {
						List<CMsgHall> 					data 	= rsp.getHallList();
						HallEntity 						hallEnt;
						Dao<HallSortEntity, Integer>	hsDao	= grdManager.getHallSortDao();
						int								count	= 0;
						for(CMsgHall hall : data){
							switch (hall.getActype()) {
								case REFRESH_TYPE_INSERT:
									hallEnt = new HallEntity();
									hallEnt.setDao(hallDao);
									hallEnt.setId(hall.getId());
									hallEnt.setName(hall.getName().toStringUtf8());
									hallEnt.setCode(hall.getCode());						
									hallEnt.setSort(hsDao.queryForId(hall.getHallsortid()));
									hallEnt.create();		
									break;
								case REFRESH_TYPE_UPDATE:
									hallEnt = hallDao.queryForId(hall.getId());
									hallEnt.setDao(hallDao);
									hallEnt.setName(hall.getName().toStringUtf8());
									hallEnt.setCode(hall.getCode());
									hallEnt.setSort(hsDao.queryForId(hall.getHallsortid()));
									hallEnt.update();
									break;
								case REFRESH_TYPE_DELETE:
									hallEnt = hallDao.queryForId(hall.getId());
									hallEnt.setDao(hallDao);
									hallEnt.delete();
									break;
							}		
							count++;
						}
						return count;
					}
				});			
	}
	/**
	 * <p>refreshDinnerTableType</p>
	 *
	 * <p>刷新餐台类型数据</p>
	 * 
	 * @return 刷新数目
	 * @throws Exception 
	 */
	@SuppressWarnings("unchecked")
	public int refreshDinnerTableType() throws Exception{						
		final Dao<DinnerTableTypeEntity, Integer> dttDao 	= grdManager.getDinnerTableTypeDao();
		 
		return	dttDao.callBatchTasks(new Callable<Integer>() {
					@Override
					public Integer call() throws Exception {
						List<CMsgDinTabType> 			data 	= rsp.getDinTabTypeList();
						Dao<HallEntity, Integer> 	hallDao = grdManager.getHallDao();
						DinnerTableTypeEntity 		dttEnt;
						int							count	= 0;
						for(CMsgDinTabType type : data){
							switch(type.getActype()){
							case REFRESH_TYPE_INSERT:
								dttEnt = new DinnerTableTypeEntity();
								dttEnt.setDao(dttDao);
								dttEnt.setId(type.getId());
								dttEnt.setCode(type.getCode());
								dttEnt.setName(type.getName().toStringUtf8());					
								dttEnt.setHall(hallDao.queryForId(type.getHallid()));	
								dttEnt.create();
								break;
							case REFRESH_TYPE_UPDATE:
								dttEnt = dttDao.queryForId(type.getId());
								dttEnt.setDao(dttDao);
								dttEnt.setId(type.getId());
								dttEnt.setCode(type.getCode());
								dttEnt.setName(type.getName().toStringUtf8());					
								dttEnt.setHall(hallDao.queryForId(type.getHallid()));	
								dttEnt.update();
								break;
							case REFRESH_TYPE_DELETE:
								dttEnt = dttDao.queryForId(type.getId());
								dttEnt.setDao(dttDao);
								dttEnt.delete();
								break;
							}
							count++;
						}
						return count;
					}
				});						
	}
	
	/**
	 * <p>refreshDinnerTable</p>
	 *
	 * <p>刷新餐台数据</p>
	 * 
	 * @return 刷新数目
	 * @throws Exception 
	 */
	@SuppressWarnings("unchecked")
	public int refreshDinnerTable() throws Exception{				
		final Dao<DinnerTableEntity, Integer> 		dtDao 	= grdManager.getDinnerTableDao();		
		return dtDao.callBatchTasks(new Callable<Integer>() {

			@Override
			public Integer call() throws Exception {
				DinnerTableEntity 	dtEnt;
				List<CMsgDinTable> 	data 	= rsp.getDinTableList();
				int					count	= 0;
				
				Dao<DinnerTableTypeEntity, Integer> 	dttDao 	= grdManager.getDinnerTableTypeDao();				
				for(CMsgDinTable table : data){
					switch(table.getActype()){
					case REFRESH_TYPE_INSERT:
						dtEnt = new DinnerTableEntity();
						dtEnt.setDao(dtDao);
						dtEnt.setId(table.getId());
						dtEnt.setCode(table.getCode());
						dtEnt.setName(table.getName().toStringUtf8());
						dtEnt.setIndex(table.getIndex());
						dtEnt.setType(dttDao.queryForId(table.getDttid()));
						dtEnt.create();
						break;
					case REFRESH_TYPE_UPDATE:
						dtEnt = dtDao.queryForId(table.getId());
						dtEnt.setDao(dtDao);
						dtEnt.setId(table.getId());
						dtEnt.setCode(table.getCode());
						dtEnt.setName(table.getName().toStringUtf8());
						dtEnt.setIndex(table.getIndex());
						dtEnt.setType(dttDao.queryForId(table.getDttid()));
						dtEnt.update();
						break;
					case REFRESH_TYPE_DELETE:
						dtEnt = dtDao.queryForId(table.getId());
						dtEnt.setDao(dtDao);
						dtEnt.delete();
						break;
					}
					count++;
				}
				return count;
			}
		});			
	}
	/**
	 * <p>refreshFoodMainSort</p>
	 *
	 * <p>刷新菜品大类数据</p>
	 * 
	 * @return 刷新数目
	 * @throws Exception 
	 */
	@SuppressWarnings("unchecked")
	public int refreshFoodMainSort() throws Exception{
		
		final	Dao<FoodMainSortEntity, Integer> fmsDao = grdManager.getFoodMainSortDao();	
		return	fmsDao.callBatchTasks(new Callable<Integer>() {

					@Override
					public Integer call() throws Exception {
						FoodMainSortEntity 			fmsEnt;
						List<CMsgFoodMainSort> 	data 	= rsp.getFoodMainSortList();
						int							count	= 0;
						for(CMsgFoodMainSort sort : data){
							switch(sort.getActype()){
							case REFRESH_TYPE_INSERT:
								fmsEnt = new FoodMainSortEntity();
								fmsEnt.setDao(fmsDao);
								fmsEnt.setId(sort.getId());					
								fmsEnt.setName(sort.getName().toStringUtf8());
								fmsEnt.create();
								break;
							case REFRESH_TYPE_UPDATE:
								fmsEnt = fmsDao.queryForId(sort.getId());
								fmsEnt.setDao(fmsDao);
								fmsEnt.setId(sort.getId());
								fmsEnt.setName(sort.getName().toStringUtf8());					
								fmsEnt.update();
								break;
							case REFRESH_TYPE_DELETE:
								fmsEnt = fmsDao.queryForId(sort.getId());
								fmsEnt.setDao(fmsDao);
								fmsEnt.delete();
								break;
							}
							count++;
						}
						return count;
					}
				});						
	}
	
	/**
	 * <p>refreshHallFoodMainSort</p>
	 *
	 * <p>刷新分厅菜品大类关联数据</p>
	 *
	 * @return 刷新数目
	 * @throws Exception 
	 */
	@SuppressWarnings("unchecked")
	public int refreshHallFoodMainSort() throws Exception{
					
		final Dao<HallFoodMainSortEntity, Integer> 	hfmsDao = grdManager.getHallFoodMainSortDao();
			
		return	hfmsDao.callBatchTasks(new Callable<Integer>() {

					@Override
					public Integer call() throws Exception {
						HallFoodMainSortEntity 		hfmsEnt;
						List<CMsgHallFmsort> 	data 	= rsp.getHallFmsortList();	
						int							count	= 0;
						Dao<HallEntity, Integer> 				hallDao = grdManager.getHallDao();
						Dao<FoodMainSortEntity, Integer>		fmsDao	= grdManager.getFoodMainSortDao();						
						for(CMsgHallFmsort hfms : data){
							switch(hfms.getActype()){
							case REFRESH_TYPE_INSERT:
								hfmsEnt = new HallFoodMainSortEntity();
								hfmsEnt.setDao(hfmsDao);
								hfmsEnt.setId(hfms.getId());
								hfmsEnt.setHall(hallDao.queryForId(hfms.getHallid()));
								hfmsEnt.setFoodMainSort(fmsDao.queryForId(hfms.getFmsid()));
								hfmsEnt.create();
								break;
							case REFRESH_TYPE_UPDATE:
								hfmsEnt = hfmsDao.queryForId(hfms.getId());
								hfmsEnt.setDao(hfmsDao);
								hfmsEnt.setId(hfms.getId());
								hfmsEnt.setHall(hallDao.queryForId(hfms.getHallid()));
								hfmsEnt.setFoodMainSort(fmsDao.queryForId(hfms.getFmsid()));
								hfmsEnt.update();
								break;
							case REFRESH_TYPE_DELETE:
								hfmsEnt = hfmsDao.queryForId(hfms.getId());
								hfmsEnt.setDao(hfmsDao);
								hfmsEnt.delete();
								break;
							}
							count++;
						}
						return count;
					}
				});			
	}
	
	/**
	 * <p>refreshFoodSubSort</p>
	 *
	 * <p>刷新菜品小类数据</p>
	 * 
	 * @return 刷新数目
	 * @throws Exception 
	 */
	@SuppressWarnings("unchecked")
	public int refreshFoodSubSort() throws Exception{
				
		final Dao<FoodSubSortEntity, Integer> 	fssDao = grdManager.getFoodSubSortDao();		
		return	fssDao.callBatchTasks(new Callable<Integer>() {

					@Override
					public Integer call() throws Exception {
						FoodSubSortEntity 			fssEnt;
						List<CMsgFoodSubSort> 	data 	= rsp.getFoodSubSortList();
						int							count	= 0;
						
						Dao<FoodMainSortEntity, Integer>	fmsDao = grdManager.getFoodMainSortDao();
						for(CMsgFoodSubSort sort : data){
							switch (sort.getActype()) {
								case REFRESH_TYPE_INSERT:						
									fssEnt = new FoodSubSortEntity();
									fssEnt.setDao(fssDao);
									fssEnt.setId(sort.getId());
									fssEnt.setName(sort.getName().toStringUtf8());						
									fssEnt.setFoodMainSort(fmsDao.queryForId(sort.getFmsid()));
									fssEnt.setProduce(sort.getPsid());
									fssEnt.create();						
									break;
								case REFRESH_TYPE_UPDATE:
									fssEnt = fssDao.queryForId(sort.getId());
									fssEnt.setDao(fssDao);
									fssEnt.setName(sort.getName().toStringUtf8());						
									fssEnt.setFoodMainSort(fmsDao.queryForId(sort.getFmsid()));
									fssEnt.setProduce(sort.getPsid());
									fssEnt.update();
									break;
								case REFRESH_TYPE_DELETE:
									fssEnt = fssDao.queryForId(sort.getId());
									fssEnt.setDao(fssDao);
									fssEnt.delete();
									break;
							}		
							count++;
						}
						return count;
					}
				});			
	}
	
	/**
	 * <p>RefreshFoodUnit</p>
	 *
	 * <p>刷新菜品计价单位数据</p>
	 * 
	 * @return 刷新数目
	 * @throws Exception 
	 */
	@SuppressWarnings("unchecked")
	public int RefreshFoodUnit() throws Exception{		
		final Dao<FoodUnitEntity, Integer> dao = grdManager.getFoodUnitDao();
		return dao.callBatchTasks(new Callable<Integer>() {

			@Override
			public Integer call() throws Exception {
				FoodUnitEntity 			fuEnt;
				List<CMsgFoodUnit> 	data 	= rsp.getFoodUnitList();
				int						count	= 0;
				for(CMsgFoodUnit unit : data){
					switch (unit.getActype()) {
						case REFRESH_TYPE_INSERT:
							fuEnt = new FoodUnitEntity();
							fuEnt.setDao(dao);
							fuEnt.setId(unit.getId());					
							fuEnt.setName(unit.getName().toStringUtf8());					
							fuEnt.create();		
							break;
						case REFRESH_TYPE_UPDATE:
							fuEnt = dao.queryForId(unit.getId());
							fuEnt.setDao(dao);
							fuEnt.setName(unit.getName().toStringUtf8());
							fuEnt.update();
							break;
						case REFRESH_TYPE_DELETE:
							fuEnt = dao.queryForId(unit.getId());
							fuEnt.setDao(dao);
							fuEnt.delete();
							break;
					}
					count++;
				}
				return count;
			}
		});			
	}		
	
	/**
	 * <p>refreshFood</p>
	 *
	 * <p>刷新菜品数据</p>
	 * 
	 * @return 刷新数目
	 * @throws Exception 
	 */
	@SuppressWarnings("unchecked")
	public int refreshFood() throws Exception{		
		final Dao<FoodEntity, Integer> 		foodDao = grdManager.getFoodDao();		
		return foodDao.callBatchTasks(new Callable<Integer>() {

				   @Override
				   public Integer call() throws Exception {
					   FoodEntity 			foodEnt;
					   List<CMsgFood> 	data 	= rsp.getFoodList();
					   int					count	= 0;
					   
					   Dao<FoodSubSortEntity, Integer>	fssDao 	= grdManager.getFoodSubSortDao();
					   for(CMsgFood food : data){				
							switch (food.getActype()) {
								case REFRESH_TYPE_INSERT:
									foodEnt = new FoodEntity();
									foodEnt.setDao(foodDao);
									foodEnt.setId(food.getId());
									foodEnt.setCode(food.getCode());
									foodEnt.setName(food.getName().toStringUtf8());
									foodEnt.setIndex(food.getIndex());
									foodEnt.setIsStock(food.getIsStock());
									foodEnt.setPhotoName(food.getPhotoname());
									foodEnt.setRemark(food.getRemark().toStringUtf8());
									foodEnt.setFoodSubSort(fssDao.queryForId(food.getFoodsubsortid()));									
									foodEnt.create();											
									break;
								case REFRESH_TYPE_UPDATE:
									foodEnt = foodDao.queryForId(food.getId());
									foodEnt.setDao(foodDao);
									foodEnt.setId(food.getId());
									foodEnt.setCode(food.getCode());
									foodEnt.setName(food.getName().toStringUtf8());
									foodEnt.setIndex(food.getIndex());									
									foodEnt.setIsStock(food.getIsStock());
									foodEnt.setPhotoName(food.getPhotoname());
									foodEnt.setRemark(food.getRemark().toStringUtf8());
									foodEnt.setFoodSubSort(fssDao.queryForId(food.getFoodsubsortid()));						
									foodEnt.update();
									break;
								case REFRESH_TYPE_DELETE:
									foodEnt = foodDao.queryForId(food.getId());
									foodEnt.setDao(foodDao);
									foodEnt.delete();
									break;
							}
							count++;
						}
					   return count;
				   }
			   });								   	
	}
	
	/**
	 * <p>refreshFoodPrice</p>
	 *
	 * <p>刷新菜品价格数据</p>
	 * 
	 * @return 刷新数目
	 * @throws Exception 
	 */
	@SuppressWarnings("unchecked")
	public int refreshFoodPrice() throws Exception{		
		final Dao<FoodPriceEntity, Integer>	fpDao	= grdManager.getFoodPriceDao();			
		return fpDao.callBatchTasks(new Callable<Integer>() {

				   @Override
				   public Integer call() throws Exception {
					   FoodPriceEntity 			fpEnt;
					   List<CMsgFoodPrice> 	data = rsp.getFoodPriceList();
					   int 						count = 0;
						
					   Dao<FoodEntity, Integer> 		foodDao = grdManager.getFoodDao();
					   Dao<FoodUnitEntity, Integer>	fuDao 	= grdManager.getFoodUnitDao();
					   
					   for(CMsgFoodPrice price : data){
							switch (price.getActype()) {
								case REFRESH_TYPE_INSERT:
									fpEnt = new FoodPriceEntity();
									fpEnt.setDao(fpDao);
									fpEnt.setId(price.getId());
									fpEnt.setPrice(price.getPrice());
									fpEnt.setUnit(fuDao.queryForId(price.getFoodUnitid()));
									fpEnt.setFood(foodDao.queryForId(price.getFoodid()));
									fpEnt.create();		
									break;
								case REFRESH_TYPE_UPDATE:
									fpEnt = fpDao.queryForId(price.getId());
									fpEnt.setDao(fpDao);
									fpEnt.setId(price.getId());
									fpEnt.setPrice(price.getPrice());
									fpEnt.setUnit(fuDao.queryForId(price.getFoodUnitid()));
									fpEnt.setFood(foodDao.queryForId(price.getFoodid()));
									fpEnt.update();
									break;
								case REFRESH_TYPE_DELETE:
									fpEnt = fpDao.queryForId(price.getId());
									fpEnt.setDao(fpDao);
									fpEnt.delete();
									break;
							}
							count++;
						}
					   return count;
					}
			   });					   		
	}
	
	/**
	 * <p>refreshFoodRecSort</p>
	 *
	 * <p>刷新菜品做法类别数据</p>
	 * 
	 * @return 刷新数目
	 * @throws Exception 
	 */
	public int refreshFoodRecSort() throws Exception{		
		final Dao<FoodRecipeSortEntity, Integer>	dao	= grdManager.getFoodRecipeSortDao();
		return	dao.callBatchTasks(new Callable<Integer>() {

					@Override
					public Integer call() throws Exception {
						FoodRecipeSortEntity 	frsEnt;
						List<CMsgFoodRecType> 	data = rsp.getFoodRecTypeList();
						int						count = 0;
						for(CMsgFoodRecType sort : data){
							switch (sort.getActype()) {
								case REFRESH_TYPE_INSERT:
									frsEnt = new FoodRecipeSortEntity();
									frsEnt.setDao(dao);
									frsEnt.setId(sort.getId());
									frsEnt.setName(sort.getName().toStringUtf8());
									frsEnt.create();		
									break;
								case REFRESH_TYPE_UPDATE:
									frsEnt = dao.queryForId(sort.getId());
									frsEnt.setDao(dao);
									frsEnt.setId(sort.getId());
									frsEnt.setName(sort.getName().toStringUtf8());
									frsEnt.update();
									break;
								case REFRESH_TYPE_DELETE:
									frsEnt = dao.queryForId(sort.getId());
									frsEnt.setDao(dao);
									frsEnt.delete();
									break;
							}
							count++;
						}
						return count;
					}
				});			
	}
	
	/**
	 * <p>refreshFoodRecipe</p>
	 *
	 * <p>刷新菜品做法数据</p>
	 * 
	 * @return 刷新数目
	 * @throws Exception 
	 */
	public int refreshFoodRecipe() throws Exception{				
		final Dao<FoodRecipeEntity, Integer>			frDao	= grdManager.getFoodRecipeDao();			
		return	frDao.callBatchTasks(new Callable<Integer>() {

					@Override
					public Integer call() throws Exception {
						FoodRecipeEntity 			frEnt;
						List<CMsgFoodRecipe> 	data = rsp.getFoodRecipeList();
						int							count = 0;
						
						Dao<FoodRecipeSortEntity, Integer> 		frsDao 	= grdManager.getFoodRecipeSortDao();
						for(CMsgFoodRecipe recipe : data){
							switch (recipe.getActype()) {
								case REFRESH_TYPE_INSERT:
									frEnt = new FoodRecipeEntity();
									frEnt.setDao(frDao);
									frEnt.setId(recipe.getId());
									frEnt.setName(recipe.getName().toStringUtf8());
									frEnt.setSort(frsDao.queryForId(recipe.getFoodRecTypeid()));
									frEnt.setMarkup(recipe.getMarkup());									
									frEnt.create();		
									break;
								case REFRESH_TYPE_UPDATE:
									frEnt = frDao.queryForId(recipe.getId());
									frEnt.setDao(frDao);
									frEnt.setId(recipe.getId());
									frEnt.setName(recipe.getName().toStringUtf8());
									frEnt.setSort(frsDao.queryForId(recipe.getFoodRecTypeid()));
									frEnt.setMarkup(recipe.getMarkup());
									frEnt.update();
									break;
								case REFRESH_TYPE_DELETE:
									frEnt = frDao.queryForId(recipe.getId());
									frEnt.setDao(frDao);
									frEnt.delete();
									break;
							}
							count++;
						}
						return count;
					}					
				});			
	}
	
	/**
	 * <p>refreshFoodFrsort</p>
	 *
	 * <p>刷新菜品-做法类别数据</p>
	 * 
	 * @return 刷新数目
	 * @throws Exception 
	 */
	public int refreshFoodFrsort() throws Exception{						
		final Dao<FoodFrsortEntity, Integer>			frrsDao = grdManager.getFoodFrsortDao();
			
		return	frrsDao.callBatchTasks(new Callable<Integer>() {

					@Override
					public Integer call() throws Exception {
						FoodFrsortEntity 				ffrsEnt;
						List<CMsgFoodFoodRecType> 	data = rsp.getFoodFoodRecTypeList();
						int								count = 0;
						
						Dao<FoodRecipeSortEntity, Integer> 		frsDao 	= grdManager.getFoodRecipeSortDao();
						Dao<FoodEntity, Integer>				foodDao = grdManager.getFoodDao();
						for(CMsgFoodFoodRecType ffrs : data){
							switch (ffrs.getActype()) {
								case REFRESH_TYPE_INSERT:
									ffrsEnt = new FoodFrsortEntity();
									ffrsEnt.setDao(frrsDao);
									ffrsEnt.setId(ffrs.getId());
									ffrsEnt.setFood(foodDao.queryForId(ffrs.getFoodid()));
									ffrsEnt.setRecipeSort(frsDao.queryForId(ffrs.getFoodrectypeid()));						
									ffrsEnt.create();		
									break;
								case REFRESH_TYPE_UPDATE:
									ffrsEnt = frrsDao.queryForId(ffrs.getId());
									ffrsEnt.setDao(frrsDao);
									ffrsEnt.setId(ffrs.getId());
									ffrsEnt.setFood(foodDao.queryForId(ffrs.getFoodid()));
									ffrsEnt.setRecipeSort(frsDao.queryForId(ffrs.getFoodrectypeid()));						
									ffrsEnt.update();
									break;
								case REFRESH_TYPE_DELETE:
									ffrsEnt = frrsDao.queryForId(ffrs.getId());
									ffrsEnt.setDao(frrsDao);
									ffrsEnt.delete();
									break;
							}	
							count++;
						}
						return count;
					}
				});									
	}
	
	/**
	 * <p>refreshDemand</p>
	 *
	 * <p>刷新菜品要求数据</p>
	 * 
	 * @return 刷新数目
	 * @throws Exception 
	 */
	public int refreshDemand() throws Exception{				
		final Dao<DemandEntity, Integer>	dao	= grdManager.getDemandDao();
		return	dao.callBatchTasks(new Callable<Integer>() {

					@Override
					public Integer call() throws Exception {
						DemandEntity 				fdEnt;
						List<CMsgFooddemand> 	data 	= rsp.getFoodDemandList();
						int							count 	= 0;
						for(CMsgFooddemand delivery : data){
							switch (delivery.getActype()) {
								case REFRESH_TYPE_INSERT:
									fdEnt = new DemandEntity();
									fdEnt.setDao(dao);
									fdEnt.setId(delivery.getId());
									fdEnt.setName(delivery.getName().toStringUtf8());
									fdEnt.setType(delivery.getType());
									fdEnt.create();		
									break;
								case REFRESH_TYPE_UPDATE:
									fdEnt = dao.queryForId(delivery.getId());
									fdEnt.setDao(dao);
									fdEnt.setId(delivery.getId());
									fdEnt.setName(delivery.getName().toStringUtf8());
									fdEnt.setType(delivery.getType());
									fdEnt.update();
									break;
								case REFRESH_TYPE_DELETE:
									fdEnt = dao.queryForId(delivery.getId());
									fdEnt.setDao(dao);
									fdEnt.delete();
									break;
							}
							count++;
						}
						return count;
					}
				});			
	}
	
	/**
	 * <p>refreshOperateReason</p>
	 *
	 * <p>刷新操作原因数据</p>
	 * 
	 * @return 刷新数目
	 * @throws Exception 
	 */
	public int refreshOperateReason() throws Exception{		
		final Dao<OperateReasonEntity, Integer>	dao	= grdManager.getOperateReasonDao();
		return	dao.callBatchTasks(new Callable<Integer>() {
	
					@Override
					public Integer call() throws Exception {
						OperateReasonEntity orEnt;
						List<CMsgOpeReason> 	data 	= rsp.getOpeReasonList();
						int					count 	= 0;
						for(CMsgOpeReason reason : data){
							switch (reason.getActype()) {
								case REFRESH_TYPE_INSERT:
									orEnt = new OperateReasonEntity();
									orEnt.setDao(dao);
									orEnt.setId(reason.getId());
									orEnt.setTitle(reason.getTitle().toStringUtf8());
									orEnt.setType(OperateType.valueOf(reason.getType()));
									orEnt.create();		
									break;
								case REFRESH_TYPE_UPDATE:
									orEnt = dao.queryForId(reason.getId());
									orEnt.setDao(dao);
									orEnt.setId(reason.getId());
									orEnt.setTitle(reason.getTitle().toStringUtf8());
									orEnt.setType(OperateType.valueOf(reason.getType()));
									orEnt.update();
									break;
								case REFRESH_TYPE_DELETE:
									orEnt = dao.queryForId(reason.getId());
									orEnt.setDao(dao);
									orEnt.delete();
									break;
							}	
							count++;
						}
						return count;
					}
				});			
	}
	
	/**
	 * <p>refreshProducePrint</p>
	 *
	 * <p>刷新出品部门和打印档口关联数据</p>
	 *
	 * @return 刷新数目
	 * @throws Exception 
	 */
	@SuppressWarnings("unchecked")
	public int refreshProducePrint() throws Exception{				
		final Dao<ProducePrintEntity, Integer> 	dao = grdManager.getProducePrintDao();
		return	dao.callBatchTasks(new Callable<Integer>() {

					@Override
					public Integer call() throws Exception {
						ProducePrintEntity 		ppEnt;
						List<CMsgProsecPort> 	data 	= rsp.getProsecPortList();
						int						count	= 0;
						for(CMsgProsecPort pp : data){
							switch(pp.getActype()){
							case REFRESH_TYPE_INSERT:
								ppEnt = new ProducePrintEntity();
								ppEnt.setDao(dao);
								ppEnt.setId(pp.getId());
								ppEnt.setPrint(pp.getPortid());
								ppEnt.setProduce(pp.getPsid());
								ppEnt.create();
								break;
							case REFRESH_TYPE_UPDATE:
								ppEnt = dao.queryForId(pp.getId());
								ppEnt.setDao(dao);
								ppEnt.setId(pp.getId());
								ppEnt.setPrint(pp.getPortid());
								ppEnt.setProduce(pp.getPsid());
								ppEnt.update();
								break;
							case REFRESH_TYPE_DELETE:
								ppEnt = dao.queryForId(pp.getId());
								ppEnt.setDao(dao);
								ppEnt.delete();
								break;
							}
							count++;
						}
						return count;
					}
				});			
	}	
	
	/**
	 * <p>refreshTimeItem</p>
	 *
	 * <p>刷新计时项目数据</p>
	 * 
	 * @return 刷新数目
	 * @throws Exception
	 */
	public int refreshTimeItem() throws Exception{
		final Dao<TimeItemEntity, Integer> 	dao = grdManager.getTimeItemDao();
		return	dao.callBatchTasks(new Callable<Integer>() {

					@Override
					public Integer call() throws Exception {
						TimeItemEntity 		tiEnt;
						List<CMsgTimeItem> 	data 	= rsp.getTimeItemList();
						int						count	= 0;
						for(CMsgTimeItem ti : data){
							switch(ti.getActype()){
							case REFRESH_TYPE_INSERT:
								tiEnt = new TimeItemEntity();
								tiEnt.setDao(dao);
								tiEnt.setId(ti.getId());
								tiEnt.setName(ti.getName().toStringUtf8());
								tiEnt.setPrice(ti.getPrice());
								tiEnt.setUnit(TimeItemUnit.valueOf(ti.getUnit()));
								tiEnt.setUnitTime(ti.getTime());
								tiEnt.create();
								break;
							case REFRESH_TYPE_UPDATE:
								tiEnt = dao.queryForId(ti.getId());
								tiEnt.setDao(dao);
								tiEnt.setId(ti.getId());
								tiEnt.setName(ti.getName().toStringUtf8());
								tiEnt.setPrice(ti.getPrice());
								tiEnt.setUnit(TimeItemUnit.valueOf(ti.getUnit()));
								tiEnt.setUnitTime(ti.getTime());
								tiEnt.update();
								break;
							case REFRESH_TYPE_DELETE:
								tiEnt = dao.queryForId(ti.getId());
								tiEnt.setDao(dao);
								tiEnt.delete();
								break;
							}
							count++;
						}
						return count;
					}
				});	
	}
	
	/**
	 * <p>refreshDttypeTitem</p>
	 *
	 * <p>刷新餐台类型和计时项目关联数据</p>
	 * 
	 * @return 刷新数目
	 * @throws Exception
	 */
	public int refreshDttypeTitem() throws Exception{
		final Dao<DinTabTypeTimItemEntity, Integer> 	dao 	= grdManager.getDinTabTypeTimItemDao();
		final Dao<DinnerTableTypeEntity, Integer>		dttDao 	= grdManager.getDinnerTableTypeDao();
		final Dao<TimeItemEntity, Integer>				tiDao	= grdManager.getTimeItemDao();
		return	dao.callBatchTasks(new Callable<Integer>() {

					@Override
					public Integer call() throws Exception {
						DinTabTypeTimItemEntity 		dttTiEnt;
						List<CMsgDinTabTypeTimeItem> 	data 	= rsp.getDtttitemList();
						int						count	= 0;
						for(CMsgDinTabTypeTimeItem dttTi : data){
							switch(dttTi.getActype()){
							case REFRESH_TYPE_INSERT:
								dttTiEnt = new DinTabTypeTimItemEntity();
								dttTiEnt.setDao(dao);
								dttTiEnt.setId(dttTi.getId());
								dttTiEnt.setDinnerTableType(dttDao.queryForId(dttTi.getDintabtypeid()));
								dttTiEnt.setTimeItem(tiDao.queryForId(dttTi.getTimeitemid()));
								dttTiEnt.create();
								break;
							case REFRESH_TYPE_UPDATE:
								dttTiEnt = dao.queryForId(dttTi.getId());
								dttTiEnt.setDao(dao);
								dttTiEnt.setId(dttTi.getId());
								dttTiEnt.setDinnerTableType(dttDao.queryForId(dttTi.getDintabtypeid()));
								dttTiEnt.setTimeItem(tiDao.queryForId(dttTi.getTimeitemid()));								
								dttTiEnt.update();
								break;
							case REFRESH_TYPE_DELETE:
								dttTiEnt = dao.queryForId(dttTi.getId());
								dttTiEnt.setDao(dao);
								dttTiEnt.delete();
								break;
							}
							count++;
						}
						return count;
					}
				});	
	}
}
