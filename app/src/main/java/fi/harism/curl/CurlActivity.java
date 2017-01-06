/*
   Copyright 2013 Harri Smatt

   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.
 */

package fi.harism.curl;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.KeyEvent;
import android.view.WindowManager;

import java.util.ArrayList;
import java.util.List;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;

/**
 * Simple Activity for curl testing.
 * 
 * @author harism
 */
public class CurlActivity extends Activity {



	private CurlView mCurlView;

	private List<ItemData> list;

	private int index = 0;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		this.initData();
		this.initCurlView();
	}

	public static DisplayMetrics getDefaultDisplayMetrics(Context context) {
		WindowManager windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
		Display display = windowManager.getDefaultDisplay();
		DisplayMetrics dm = new DisplayMetrics();
		display.getMetrics(dm);
		return dm;
	}


	private void initCurlView(){
		int index = 0;
		mCurlView = (CurlView) findViewById(R.id.curl);
		mCurlView.setPageProvider(new PageProvider());
		mCurlView.setSizeChangedObserver(new SizeChangedObserver());
		mCurlView.setCurrentIndex(index);
		mCurlView.setBackgroundColor(0xFFC0C0C0);
		mCurlView.setAllowLastPageCurl(false);
		mCurlView.setCurlAnimatorListener(new CurlView.CurlAnimatorListener(){
			@Override
			public void onComplete(int i) {
				L.e("onComplete: "+(i==1?"SET_CURL_TO_LEFT":"SET_CURL_TO_RIGHT"));
				Observable.create(f->{
					//mRelativeLayout.setVisibility(View.VISIBLE);
					f.onCompleted();
				}).subscribeOn(AndroidSchedulers.mainThread())
						.observeOn(AndroidSchedulers.mainThread())
						.subscribe(f->{},e->e.printStackTrace());
			}

			@Override
			public void onStart(int i) {
				L.e("onStart:"+(i==1?"CURL_LEFT":"CURL_RIGHT"));
				Observable.create(f->{
					//mRelativeLayout.setVisibility(View.GONE);
					f.onCompleted();
				}).subscribeOn(AndroidSchedulers.mainThread())
						.observeOn(AndroidSchedulers.mainThread())
						.subscribe(f->{},e->e.printStackTrace());

			}

			@Override
			public void onTap(PointF pointF, PointF rawPointF){
				L.e("onTap:");
				int w = getDefaultDisplayMetrics(CurlActivity.this).widthPixels;
				if(rawPointF.x > w / 2.0){
					mCurlView.pageNext();
				}else{
					mCurlView.pagePrevious();
				}

			}
		});
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		switch (keyCode) {
			case KeyEvent.KEYCODE_VOLUME_DOWN: {
				setVolumeControlStream(0);
				mCurlView.pageNext();
				return true;
			}
			case KeyEvent.KEYCODE_VOLUME_UP: {
				setVolumeControlStream(0);
				mCurlView.pagePrevious();
				return true;
			}
		}
		return super.onKeyDown(keyCode, event);
	}

	private void initData(){
		list = new ArrayList<>();
		list.add(new ItemData(R.drawable.p0_480,"第0个"));
		list.add(new ItemData(R.drawable.p1_480,"第1个"));
		list.add(new ItemData(R.drawable.p2_480,"第2个"));
		list.add(new ItemData(R.drawable.p3_480,"第3个"));
		list.add(new ItemData(R.drawable.p4_480,"第4个"));
		list.add(new ItemData(R.drawable.p5_480,"第5个"));
		list.add(new ItemData(R.drawable.p6_480,"第6个"));
		list.add(new ItemData(R.drawable.p7_480,"第7个"));
		list.add(new ItemData(R.drawable.p8_480,"第8个"));
		list.add(new ItemData(R.drawable.p9_480,"第9个"));
	}

	@Override
	public void onPause() {
		super.onPause();
		mCurlView.onPause();
	}

	@Override
	public void onResume() {
		super.onResume();
		mCurlView.onResume();
	}

	@Override
	public Object onRetainNonConfigurationInstance() {
		return mCurlView.getCurrentIndex();
	}


	/**
	 * Bitmap provider.
	 */
	private class PageProvider implements CurlView.PageProvider {


		@Override
		public int getPageCount() {
			return list.size();
		}

		private Bitmap loadBitmap(int width, int height, int index) {
			Bitmap b = Bitmap.createBitmap(width, height,
					Bitmap.Config.ARGB_8888);
			b.eraseColor(0xFFFFFFFF);
			Canvas c = new Canvas(b);
			ItemData itemData = list.get(index % list.size());
			Drawable d = getResources().getDrawable(itemData.getResId());

			int margin = 7;
			int border = 3;
			Rect r = new Rect(margin, margin, width - margin, height - margin);

			int imageWidth = r.width() - (border * 2);
			int imageHeight = imageWidth * d.getIntrinsicHeight()
					/ d.getIntrinsicWidth();
			if (imageHeight > r.height() - (border * 2)) {
				imageHeight = r.height() - (border * 2);
				imageWidth = imageHeight * d.getIntrinsicWidth()
						/ d.getIntrinsicHeight();
			}

			r.left += ((r.width() - imageWidth) / 2) - border;
			r.right = r.left + imageWidth + border + border;
			r.top += ((r.height() - imageHeight) / 2) - border;
			r.bottom = r.top + imageHeight + border + border;

			Paint p = new Paint();
			p.setColor(0xFFC0C0C0);
			c.drawRect(r, p);
			r.left += border;
			r.right -= border;
			r.top += border;
			r.bottom -= border;

			d.setBounds(r);
			d.draw(c);

			return b;
		}


		@Override
		public void updatePage(CurlPage page, int width, int height, int index) {
			Bitmap front = loadBitmap(width, height, index);
			page.setBitmap(front, CurlPage.SIDE_FRONT);
			page.setBitmap(front, CurlPage.SIDE_BACK);
			//page.setColor(Color.argb(127, 170, 130, 255), CurlPage.SIDE_FRONT);
			page.setColor(Color.argb(100,255, 190, 150), CurlPage.SIDE_BACK);
		}
//		@Override
//		public void updatePage(CurlPage page, int width, int height, int index) {
//
//			switch (index) {
//			// First case is image on front side, solid colored back.
//			case 0: {
//				Bitmap front = loadBitmap(width, height, 0);
//				page.setBitmap(front, CurlPage.SIDE_FRONT);
//				page.setColor(Color.rgb(180, 180, 180), CurlPage.SIDE_BACK);
//				break;
//			}
//			// Second case is image on back side, solid colored front.
//			case 1: {
//				Bitmap back = loadBitmap(width, height, 2);
//				page.setBitmap(back, CurlPage.SIDE_BACK);
//				page.setColor(Color.rgb(127, 140, 180), CurlPage.SIDE_FRONT);
//				break;
//			}
//			// Third case is images on both sides.
//			case 2: {
//				Bitmap front = loadBitmap(width, height, 1);
//				Bitmap back = loadBitmap(width, height, 3);
//				page.setBitmap(front, CurlPage.SIDE_FRONT);
//				page.setBitmap(back, CurlPage.SIDE_BACK);
//				break;
//			}
//			// Fourth case is images on both sides - plus they are blend against
//			// separate colors.
//			case 3: {
//				Bitmap front = loadBitmap(width, height, 2);
//				Bitmap back = loadBitmap(width, height, 1);
//				page.setBitmap(front, CurlPage.SIDE_FRONT);
//				page.setBitmap(back, CurlPage.SIDE_BACK);
//				page.setColor(Color.argb(127, 170, 130, 255),
//						CurlPage.SIDE_FRONT);
//				page.setColor(Color.rgb(255, 190, 150), CurlPage.SIDE_BACK);
//				break;
//			}
//			// Fifth case is same image is assigned to front and back. In this
//			// scenario only one texture is used and shared for both sides.
//			case 4:
//				Bitmap front = loadBitmap(width, height, 0);
//				page.setBitmap(front, CurlPage.SIDE_BOTH);
//				page.setColor(Color.argb(127, 255, 255, 255),
//						CurlPage.SIDE_BACK);
//				break;
//			}
//		}

	}

	/**
	 * CurlView size changed observer.
	 */
	private class SizeChangedObserver implements CurlView.SizeChangedObserver {
		@Override
		public void onSizeChanged(int w, int h) {
			if (w > h) {
				mCurlView.setViewMode(CurlView.SHOW_TWO_PAGES);
				mCurlView.setMargins(.0f, .0f, .0f, .0f);
			} else {
				mCurlView.setViewMode(CurlView.SHOW_ONE_PAGE);
				mCurlView.setMargins(.0f, .0f, .0f, .0f);
			}
		}
	}


}