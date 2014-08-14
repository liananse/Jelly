package com.medialab.jelly.ui.adapter;

import java.io.File;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.util.LruCache;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.ImageView;

import com.medialab.jelly.R;
import com.medialab.jelly.SearchImageActivity;
import com.medialab.jelly.resultmodel.SearchImageResultModel;
import com.medialab.jelly.util.UDataStorage;
import com.medialab.jelly.util.UUtils;
import com.medialab.jelly.util.view.SquareImageView;

/**
 * GridView的适配器，负责异步从网络上下载图片展示在照片墙上。
 * 
 */
public class ImageWallAdapter extends ArrayAdapter<SearchImageResultModel> implements OnClickListener {

	private static final String TAG = ImageWallAdapter.class.getSimpleName();
	/**
	 * 记录所有正在下载或等待下载的任务。
	 */
	private Set<BitmapWorkerTask> taskCollection;

	/**
	 * 图片缓存技术的核心类，用于缓存所有下载好的图片，在程序内存达到设定值时会将最少最近使用的图片移除掉。
	 */
	private LruCache<String, Bitmap> mMemoryCache;

	/**
	 * GridView的实例
	 */
	private GridView mPhotoWall;

	private String referer;
	
	private List<SearchImageResultModel> searchImageList = new ArrayList<SearchImageResultModel>();
	
	private SearchImageActivity activity;
	
	private String imagePath ="";
	
	public ImageWallAdapter(SearchImageActivity context, int textViewResourceId, List<SearchImageResultModel> imageList,GridView photoWall,String ref) {
		super(context, textViewResourceId, imageList);
		this.mPhotoWall = photoWall;
		this.taskCollection = new HashSet<BitmapWorkerTask>();
		// 获取应用程序最大可用内存
		int maxMemory = (int) Runtime.getRuntime().maxMemory();
		int cacheSize = maxMemory / 8;
		// 设置图片缓存大小为程序最大可用内存的1/8
		this.mMemoryCache = new LruCache<String, Bitmap>(cacheSize) {
			@Override
			protected int sizeOf(String key, Bitmap bitmap) {
				return bitmap.getByteCount();
			}
		};
		
		this.searchImageList = imageList;
		
		this.referer = ref;
		
		this.activity = context;
		
		loadBitmaps();
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		final SearchImageResultModel searchImageModel = getItem(position);
		View view;
		if (convertView == null) {
			view = LayoutInflater.from(getContext()).inflate(R.layout.stock_image_wall, null);
		} else {
			view = convertView;
		}
		final SquareImageView photo = (SquareImageView) view.findViewById(R.id.stock_image_wall_photo);
		// 给ImageView设置一个Tag，保证异步加载图片时不会乱序
		photo.setTag(searchImageModel.getThumbURL());
		setImageView(searchImageModel.getThumbURL(), photo);
		
		return view;
	}

	/**
	 * 给ImageView设置图片。首先从LruCache中取出图片的缓存，设置到ImageView上。如果LruCache中没有该图片的缓存，
	 * 就给ImageView设置一张默认图片。
	 * 
	 * @param imageUrl
	 *            图片的URL地址，用于作为LruCache的键。
	 * @param imageView
	 *            用于显示图片的控件。
	 */
	private void setImageView(String imageUrl, ImageView imageView) {
		Bitmap bitmap = getBitmapFromMemoryCache(imageUrl);
		if (bitmap != null) {
			imageView.setImageBitmap(bitmap);
		}else{
			imageView.setImageResource(R.drawable.empty_photo);
		}
	}

	/**
	 * 将一张图片存储到LruCache中。
	 * 
	 * @param key
	 *            LruCache的键，这里传入图片的URL地址。
	 * @param bitmap
	 *            LruCache的键，这里传入从网络上下载的Bitmap对象。
	 */
	public void addBitmapToMemoryCache(String key, Bitmap bitmap) {
		if (getBitmapFromMemoryCache(key) == null) {
			mMemoryCache.put(key, bitmap);
		}
	}

	/**
	 * 从LruCache中获取一张图片，如果不存在就返回null。
	 * 
	 * @param key
	 *            LruCache的键，这里传入图片的URL地址。
	 * @return 对应传入键的Bitmap对象，或者null。
	 */
	public Bitmap getBitmapFromMemoryCache(String key) {
		return mMemoryCache.get(key);
	}

	/**
	 * 加载Bitmap对象。此方法会在LruCache中检查所有屏幕中可见的ImageView的Bitmap对象，
	 * 如果发现任何一个ImageView的Bitmap对象不在缓存中，就会开启异步线程去下载图片。
	 * 
	 * @param firstVisibleItem
	 *            第一个可见的ImageView的下标
	 * @param visibleItemCount
	 *            屏幕中总共可见的元素数
	 */
	private void loadBitmaps() {
		try {
			for (int i = 0; i < searchImageList.size(); i++) {
				
				SearchImageResultModel searchImageModel = searchImageList.get(i);
				Bitmap bitmap = getBitmapFromMemoryCache(searchImageModel.getThumbURL());
				if (bitmap == null) {
					BitmapWorkerTask task = new BitmapWorkerTask();
					taskCollection.add(task);
					task.execute(searchImageModel.getThumbURL());
				} else {
					ImageView imageView = (ImageView) mPhotoWall.findViewWithTag(searchImageModel.getThumbURL());
					if (imageView != null && bitmap != null) {
						imageView.setImageBitmap(bitmap);
						imageView.setOnClickListener(ImageWallAdapter.this);//绑定点击事件
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 取消所有正在下载或等待下载的任务。
	 */
	public void cancelAllTasks() {
		if (taskCollection != null) {
			for (BitmapWorkerTask task : taskCollection) {
				task.cancel(false);
			}
		}
	}

	/**
	 * 异步下载图片的任务。
	 * 
	 */
	class BitmapWorkerTask extends AsyncTask<String, Void, Bitmap> {

		/**
		 * 图片的URL地址
		 */
		private String imageUrl;
		
		@Override
		protected Bitmap doInBackground(String... params) {
			imageUrl = params[0];
			// 在后台开始下载图片
			Bitmap bitmap = downloadBitmap(params[0]);
			if (bitmap != null) {
				// 图片下载完成后缓存到LrcCache中
				addBitmapToMemoryCache(params[0], bitmap);
			}
			return bitmap;
		}

		@Override
		protected void onPostExecute(Bitmap bitmap) {
			super.onPostExecute(bitmap);
			// 根据Tag找到相应的ImageView控件，将下载好的图片显示出来。
			ImageView imageView = (ImageView) mPhotoWall.findViewWithTag(imageUrl);
			if (imageView != null && bitmap != null) {
				imageView.setImageBitmap(bitmap);
				imageView.setOnClickListener(ImageWallAdapter.this);//绑定点击事件
			}
			taskCollection.remove(this);
		}

		/**
		 * 建立HTTP请求，并获取Bitmap对象。
		 * 
		 * @param imageUrl
		 *            图片的URL地址
		 * @return 解析后的Bitmap对象
		 */
		private Bitmap downloadBitmap(String imageUrl) {
			Bitmap bitmap = null;
			HttpURLConnection con = null;
			try {
				URL url = new URL(imageUrl);
				con = (HttpURLConnection) url.openConnection();
				con.setRequestMethod("GET");
				con.setConnectTimeout(5 * 1000);
				Log.d(TAG, "Referer:"+referer);
				con.addRequestProperty("Referer", referer);
				bitmap = BitmapFactory.decodeStream(con.getInputStream());
			} catch (Exception e) {	
				e.printStackTrace();
			} finally {
				if (con != null) {
					con.disconnect();
				}
			}
			return bitmap;
		}

	}

	@Override
	public void onClick(View v) {
		saveImageToPath(v);
	}
	
	public void saveImageToPath(View v){
		
		String url = (String) v.getTag();
		if(url!=null && !url.equals("")){
			
			if(searchImageList!=null){
				for(SearchImageResultModel searchImageModel : searchImageList){
					if(searchImageModel.getThumbURL().equals(url)){
						if(searchImageModel.getHasLarge()==1){
							url = searchImageModel.getLargeImageUrl();//有大图的时候下载大图
							break;
						}
					}
				}
			}
			
			File dir = UDataStorage.getExternalStoragePublicDirectory(UDataStorage.Dir_Pictures);
			imagePath = dir.getAbsolutePath() + "/"+System.currentTimeMillis()+".jpg";
			
			Bitmap bitmap = getBitmapFromMemoryCache(url);
			if(bitmap == null){
				//如果图片不在缓存里面，重新下载，此时不需要异步
				HttpURLConnection con = null;
				try {
					URL u = new URL(url);
					con = (HttpURLConnection) u.openConnection();
					con.setRequestMethod("GET");
					con.setConnectTimeout(5 * 1000);
					con.addRequestProperty("Referer", referer);
					bitmap = BitmapFactory.decodeStream(con.getInputStream());
				} catch (Exception e) {	
					e.printStackTrace();
				} finally {
					if (con != null) {
						con.disconnect();
					}
				}
			}
			
//			if (bitmap!=null && UImageManager.saveBtimapToFile(bitmap, imagePath))
//			{
//				UUtils.showServerMessageToast(getContext(), "图片已保存到"+imagePath);
//			}else{
//				UUtils.showServerMessageToast(getContext(), "图片保存失败！");
//			}
//			
//			new AlertDialog.Builder(this.getContext()).setTitle("确认使用这张图片吗？")
//			.setIcon(android.R.drawable.ic_dialog_info)
//			.setPositiveButton("确定", new DialogInterface.OnClickListener() {
//				@Override
//				public void onClick(DialogInterface dialog, int which) {
//					if(!imagePath.equals("")){
//						returnImagePath();
//					}
//				}
//			}).setNegativeButton("取消", new DialogInterface.OnClickListener() {
//				
//				@Override
//				public void onClick(DialogInterface dialog, int which) {
//					// 点击“返回”后的操作,这里不设置没有任何操作 
//					imagePath = "";
//					dialog.cancel();
//				}
//			}).show();
			
			if (bitmap != null) {
				Uri uri = Uri.parse(MediaStore.Images.Media.insertImage(getContext().getContentResolver(), bitmap, null, null));
				
				Intent intent = activity.getIntent();
				
				intent.setData(uri);
				
				activity.setResult(Activity.RESULT_OK, intent);
				activity.finish();
			}
			
		}else{
			UUtils.showServerMessageToast(getContext(), "对不起，没有找到该图片！！");
		}
		
	}
	
	public void returnImagePath() {
		Log.d(TAG, "imagePath:"+imagePath);
		Intent in = activity.getIntent();
		Bundle bn = in.getExtras();
		bn.putString("imagePath", imagePath);  
		in.putExtras(bn);  
		activity.setResult(Activity.RESULT_OK, in);
		activity.finish();
	}
	
}
