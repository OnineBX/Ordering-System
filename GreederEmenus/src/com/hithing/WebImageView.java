package com.hithing;

import java.io.File;
import java.io.FileOutputStream;

import com.hithing.R;
import com.hithing.R.drawable;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.ImageView;

public class WebImageView extends ImageView {

	private final static String LOG_TAG = WebImageView.class.getName();

	private Handler handler = new Handler();
	private boolean isLoading;

	public WebImageView(Context context) {
		super(context);
	}

	public WebImageView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public WebImageView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	public void setImageUrl(final File cacheFile) {
		if (cacheFile.exists()) {
			Bitmap bm = BitmapFactory.decodeFile(cacheFile.getAbsolutePath());
			if (bm != null){
				setImageBitmap(bm);
			}
			else{
				setImageResource(R.drawable.ic_launcher);
			bm = null;
			}
		} else {
			setImageResource(R.drawable.ic_launcher);
		}
	}

	public void setImageUrl(final String url, final File cacheFile) {
		if (!isLoading)
			if (cacheFile.exists()) {
				Bitmap bm = BitmapFactory.decodeFile(cacheFile
						.getAbsolutePath());
				if (bm != null) {
					setImageBitmap(bm);
				} else
					loadImage(url, cacheFile);
				bm = null;
			} else
				loadImage(url, cacheFile);
	}

	private void loadImage(final String url, final File cacheFile) {
		ThreadPoolFactory.getInstance().execute(new Runnable() {
			public void run() {
				try {
					isLoading = true;
					isLoading = !IOUtils.copyUrl(url, new FileOutputStream(
							cacheFile));
					handler.post(new Runnable() {
						public void run() {
							Bitmap bm = BitmapFactory.decodeFile(cacheFile
									.getAbsolutePath());
							if (bm != null) {
								setImageBitmap(bm);
							}
							bm = null;
							isLoading = false;
						}
					});
				} catch (Exception e) {
					Log.e(LOG_TAG, "loading image error" , e);
				}

			}
		});
	}
}
