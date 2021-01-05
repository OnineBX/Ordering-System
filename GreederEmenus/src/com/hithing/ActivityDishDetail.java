package com.hithing;

import java.util.LinkedList;
import java.util.List;

import javax.security.auth.PrivateCredentialPermission;

import com.hithing.hsc.bll.android.AndGrdDaoManager;
import com.hithing.hsc.bll.control.OrderCtrl;
import com.hithing.hsc.bll.manage.FoodManager;
import com.hithing.hsc.bll.manage.GreederDaoManager;
import com.hithing.hsc.dataentity.DinnerTableEntity;
import com.hithing.hsc.entity.SearchableAdapterItem.StyledAdapterItem;
import com.hithing.widget.OrderItemsAdapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

public class ActivityDishDetail extends Activity implements OnClickListener{

	private ImageButton backButton;
	private ImageButton addButton;
	private ImageView foodView;
	private TextView foodPrice;
	private TextView foodSort;
	private TextView foodType;
	private TextView foodName;
	private TextView foodIntroduction;
	long priceId;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.dish_detail);

		backButton = (ImageButton) findViewById(R.id.back);
		addButton = (ImageButton) findViewById(R.id.add_order);
		backButton.setOnClickListener(this);
		addButton.setOnClickListener(this);
		foodView = (ImageView) findViewById(R.id.foodImage);
		foodPrice = (TextView) findViewById(R.id.price);
		foodName = (TextView) findViewById(R.id.name);
		foodSort = (TextView) findViewById(R.id.sort);
		foodIntroduction = (TextView) findViewById(R.id.introduc);
		foodType =(TextView) findViewById(R.id.food_type);
		
		Intent intent  = getIntent();
		String price = intent.getStringExtra("price");
		String name = intent.getStringExtra("name");
		String image = intent.getStringExtra("img");
		String introduction = intent.getStringExtra("remark");
		String type = intent.getStringExtra("sort");
		String mainType = intent.getStringExtra("mainSort");
		priceId = (long) intent.getLongExtra("priceId", 0);
		
		foodPrice.setText(price);
		foodName.setText(name);
		foodSort.setText(mainType);
		Bitmap bmp = BitmapFactory.decodeFile(image);
		if(bmp != null){
			foodView.setImageBitmap(bmp);
		}else {
			foodView.setImageResource(R.drawable.ic_launcher);
		}
		foodIntroduction.setText(introduction);
		foodType.setText(type);
		
		
	}
	
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.back:
			onBackPressed();
			break;

		case R.id.add_order:
			setResult(RESULT_OK);
			finish();
			break;
		}
	}
	
}
