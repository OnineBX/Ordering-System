package com.hithing.adapter;

import java.io.File;
import java.util.Map;

import android.app.Activity;
import android.content.Context;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.hithing.ActivityMenus;
import com.hithing.R;
import com.hithing.WebImageView;
import com.hithing.hsc.bll.android.AndGrdDaoManager;
import com.hithing.hsc.bll.control.FoodCtrl;
import com.hithing.hsc.bll.control.OrderCtrl;
import com.hithing.hsc.bll.manage.FoodManager;
import com.hithing.hsc.bll.manage.GreederDaoManager;
import com.hithing.hsc.entity.OrderItemKey;
import com.hithing.hsc.entity.OrderItemValue;
import com.hithing.hsc.entity.SearchableAdapterItem.FoodAdapterItem;
import com.hithing.widget.AbsMultiStyleAdapter;

public class MenusAdapter extends AbsMultiStyleAdapter<FoodAdapterItem>{	
	
	private final String cacheDir;
	private FoodCtrl foodCtrl;
	
	private Context mContext;
	public MenusAdapter(Context context, Map<Integer, FoodAdapterItem> data,
			int resId) {
		super(context, data, resId);	
		this.mContext = context;
		cacheDir = context.getCacheDir().getAbsolutePath();
		
		Context dataContext = ((ActivityMenus)context).mainContext;
		GreederDaoManager grdMgr = new AndGrdDaoManager(dataContext);
		foodCtrl = new OrderCtrl(0, new FoodManager(grdMgr.getFoodDao(), grdMgr.getFoodPriceDao()));
	}

	@Override
	protected void refreshViewStyle(View sourceView, int style) {
		sourceView.setBackgroundColor(style);
	}
	
	public FoodAdapterItem getItemByKey(int id){
		return data.get(id);
	}

	@Override
	protected View createViewFromResource(int position, View convertView,ViewGroup parent, int resource) {
		ViewHolder holder;
		FoodAdapterItem item = (FoodAdapterItem)getItem(position);
		if(convertView == null){
			holder = new ViewHolder();
			convertView = inflater.inflate(resource, parent, false);
//			// 给ImageView设置资源
//			imageView = new ImageView(mContext);
//			// 设置布局 图片120×120显示
//			imageView.setLayoutParams(new GridView.LayoutParams(200, 150));
//			// 设置显示比例类型
//			imageView.setScaleType(ImageView.ScaleType.FIT_CENTER);
			holder.mImag = (WebImageView) convertView.findViewById(R.id.image);
			holder.mName = (TextView) convertView.findViewById(R.id.name);
			holder.mPrice = (TextView) convertView.findViewById(R.id.price);
			convertView.setTag(holder);
			convertView.setId(item.getStyle());	
		}else{
			holder = (ViewHolder)convertView.getTag();
		}
		holder.mPrice.setText(item.getPrice());
		holder.mName.setText(item.getContent());
		if(item.getPhotoName().equals("N/A")){
			holder.mImag.setImageResource(R.drawable.ic_launcher);
		}else {						
			holder.mImag.setImageUrl(imageOperate(item.getPhotoName(),(int)getItemId(position)));
		}
				
		return convertView;
	}

	public File imageOperate(String fileName, int fpid){				
		//如果需要取的图片名与文件中的文件名一样，即在本地中存在，不需要到服务器上下载，否则到服务器下载
		File file = new File(cacheDir,fileName);
		if(!file.exists()){
			foodCtrl.loadFoodImage(fpid);
		}
		return file;
	}

	
	public final class ViewHolder {
		WebImageView mImag;
		TextView mName;
		TextView mPrice;
	}
}
