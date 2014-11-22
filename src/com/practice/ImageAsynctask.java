package com.practice;

import com.practice.TripBaseAdapter.ViewHolder;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.widget.ImageView;

public class ImageAsynctask extends AsyncTask<Void, Void, Void> {
    private String imagepath;
    private ViewHolder viewHolder;
    private Bitmap bm;
    private ImageView imageView;
    // 初始化
    public ImageAsynctask(ImageView imageView,String path,ViewHolder viewHolder) {
        this.imagepath=path;
        this.viewHolder=viewHolder;
        this.imageView=imageView;
    }
    @Override
    protected Void doInBackground(Void... params) {
    	if (imageView.getTag() != null && imageView.getTag().equals(imagepath)) {
    		bm = decodeBitmap(imagepath,300);
    	}
        return null;
    }       
    
    @Override
	protected void onPostExecute(Void result) {
		// TODO Auto-generated method stub
		super.onPostExecute(result);
		/*viewHolder.photoImageView.getTag();
		viewHolder.photoImageView.setImageBitmap(bm);
		System.out.println("********异步图片"+viewHolder.photoImageView.getTag());*/
		if (imageView.getTag() != null && imageView.getTag().equals(imagepath)) {
			imageView.setImageBitmap(bm);			
		}
	}
    
	public void onProgressUpdate(Void... voids) {
    }
    
    public static Bitmap decodeBitmap(String path, int compareSize) {
		BitmapFactory.Options options = new BitmapFactory.Options();
		options.inJustDecodeBounds = true;
		// 通过这个bitmap获取图片的宽和高
		Bitmap bitmap = BitmapFactory.decodeFile(path, options);
		if (bitmap == null) {
			System.out.println("bitmap为空");
		}
		float realWidth = options.outWidth;
		float realHeight = options.outHeight;
		System.out.println("真实图片高度：" + realHeight + "宽度:" + realWidth);
		// 计算缩放比
		int scale = (int) ((realHeight > realWidth ? realHeight : realWidth) / compareSize);
		if (scale <= 0) {
			scale = 1;
		}
		options.inSampleSize = scale;
		options.inJustDecodeBounds = false;
		// 注意这次要把options.inJustDecodeBounds 设为 false,这次图片是要读取出来的。
		bitmap = BitmapFactory.decodeFile(path, options);
		int w = bitmap.getWidth();
		int h = bitmap.getHeight();
		System.out.println("缩略图高度：" + h + "宽度:" + w);
		return bitmap;
	}
 }