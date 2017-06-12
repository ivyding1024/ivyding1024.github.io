package com.mdbiomedical.app.vion.vian_health.view;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.support.v4.widget.DrawerLayout;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.ImageView;
import android.widget.TextView;

import com.mdbiomedical.app.vion.vian_health.R;
import com.mdbiomedical.app.vion.vian_health.service.MainService;
import com.mdbiomedical.app.vion.vian_health.util.ChangeView;
import com.mdbiomedical.app.vion.vian_health.util.DeviceConstant;
import com.mdbiomedical.app.vion.vian_health.util.UIUtils;

public class TutorialStepView extends Activity {
	boolean menuOut = false;
	Intent intent;

	private ViewPager mViewPager;
	private ArrayList<View> mPageViews;
	private ImageView mImageView;
	private ImageView[] mImageViews;
	private ViewGroup mainViewGroup;
	private ViewGroup indicatorViewGroup;
	LayoutInflater mInflater;
	MainService mainService;
	int[] resId = { R.layout.tutorial_page00, R.layout.tutorial_page01, R.layout.tutorial_page02, R.layout.tutorial_page03, R.layout.tutorial_page04, R.layout.tutorial_page05,
			R.layout.tutorial_page06, R.layout.tutorial_page07, R.layout.tutorial_page08 };
	int oldNumber = 0;

	// GestureImageView ivInformation;
	protected void onDestroy() {
		super.onDestroy();
		System.gc();
	}
	//sandy0914
		public static DisplayMetrics dm = new DisplayMetrics();
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.tutorial_step_activity);
		TextView tv_tutorial_title = (TextView) findViewById(R.id.tv_tutorial_title);
		//sandy0914
		getWindowManager().getDefaultDisplay().getMetrics(dm);
		DeviceConstant.screenWidth = dm.widthPixels;
		DeviceConstant.screenHeight = dm.heightPixels;
		DeviceConstant.screenDPI = dm.densityDpi;
		
		tv_tutorial_title.setTextSize(TypedValue.COMPLEX_UNIT_PX, (int) (DeviceConstant.screenHeight * 0.03f));

		mainService = new MainService(this.getApplicationContext());

		mInflater = getLayoutInflater();

		mPageViews = new ArrayList<View>();
		mPageViews.add(mInflater.inflate(resId[1], null));
		mPageViews.add(mInflater.inflate(resId[2], null));
		mPageViews.add(mInflater.inflate(resId[3], null));
		mPageViews.add(mInflater.inflate(resId[4], null));
		mPageViews.add(mInflater.inflate(resId[5], null));
		mPageViews.add(mInflater.inflate(resId[6], null));
		mPageViews.add(mInflater.inflate(resId[7], null));
		mPageViews.add(mInflater.inflate(resId[8], null));

		mImageViews = new ImageView[8];

		mainViewGroup = (ViewGroup) mInflater.inflate(R.layout.tutorial_step_activity, null);

		mViewPager = (ViewPager) mainViewGroup.findViewById(R.id.myviewpager);

		indicatorViewGroup = (ViewGroup) mainViewGroup.findViewById(R.id.mybottomviewgroup);

		for (int i = 0; i < 8; i++) {
			mImageView = new ImageView(TutorialStepView.this);
			mImageView.setLayoutParams(new LayoutParams(20, 20));
			mImageView.setPadding(20, 0, 20, 0);

			if (i == 0) {
				mImageView.setBackgroundResource(R.drawable.page_indicator_focused);
			} else {
				mImageView.setBackgroundResource(R.drawable.page_indicator);
			}

			mImageViews[i] = mImageView;

			// 加入小點
			indicatorViewGroup.addView(mImageViews[i]);
		}

		setContentView(mainViewGroup);

		mViewPager.setAdapter(new MyPagerAdapter());
		mViewPager.setOnPageChangeListener(new OnPageChangeListener() {

			@Override
			public void onPageSelected(int arg0) {
				for (int i = 0; i < mImageViews.length; i++) {
					if (i == arg0) {
						mImageViews[i].setBackgroundResource(R.drawable.page_indicator_focused);
						// mPageViews.add(mInflater.inflate(resId[i], null));
					} else {
						mImageViews[i].setBackgroundResource(R.drawable.page_indicator);
					}
				}

				if (arg0 > 0 && arg0 < 8) {
					// mPageViews.set(arg0 + 1, mInflater.inflate(resId[arg0] +
					// 2, null));
					// mPageViews.set(arg0 - 1, mInflater.inflate(resId[arg0],
					// null));

				}
				if (arg0 > 1 && arg0 < 7) {
					// mPageViews.set(arg0 - 2, mInflater.inflate(resId[0],
					// null));
					// mPageViews.set(arg0 + 2, mInflater.inflate(resId[0],
					// null));
				}
				// }

				// Log.e("vion", String.valueOf(arg0) + " " + "圖片張數: " +
				// mImageViews.length);
			}

			@Override
			public void onPageScrolled(int arg0, float arg1, int arg2) {

			}

			@Override
			public void onPageScrollStateChanged(int arg0) {

			}
		});
		ImageView iv_toturial_menu;
		iv_toturial_menu = (ImageView) findViewById(R.id.iv_toturial_menu);
		// 回上一頁
		iv_toturial_menu.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {

				try {
					ChangeView.onBack();
				} catch (Exception e) {
					finish();
				}

			}
		});

	}

	class MyPagerAdapter extends PagerAdapter {

		@Override
		public int getCount() {
			return mPageViews.size();
		}

		@Override
		public boolean isViewFromObject(View arg0, Object arg1) {
			return arg0 == arg1;
		}

		@Override
		public int getItemPosition(Object object) {
			return super.getItemPosition(object);
		}

		@Override
		public void destroyItem(View arg0, int arg1, Object arg2) {
			((ViewPager) arg0).removeView(mPageViews.get(arg1));
		}

		@Override
		public Object instantiateItem(View arg0, int arg1) {
			((ViewPager) arg0).addView(mPageViews.get(arg1));
			return mPageViews.get(arg1);
		}

		@Override
		public void restoreState(Parcelable arg0, ClassLoader arg1) {

		}

		@Override
		public Parcelable saveState() {
			return null;
		}

		@Override
		public void startUpdate(View arg0) {

		}

		@Override
		public void finishUpdate(View arg0) {

		}

	}

	@Override
	public void onBackPressed() {
		// if (DeviceConstant.statusBarHeight != 0) {
		// View view =
		// getWindow().getDecorView().findViewById(android.R.id.content);
		// try {
		// Bitmap leftSideImage = UIUtils.creatMenuLeftSidePic(view);
		//
		// if (leftSideImage != null) {
		// HomeView.iv_home_left_side_pic.setVisibility(View.VISIBLE);
		// HomeView.iv_home_left_side_pic.setImageBitmap(leftSideImage);
		// }
		// } catch (Exception e) {
		// HomeView.iv_home_left_side_pic.setVisibility(View.INVISIBLE);
		// }
		//
		// } else {
		// HomeView.iv_home_left_side_pic.setImageResource(R.drawable.transparent_10x10);
		// }

		setResult(RESULT_OK);
		finish();
		overridePendingTransition(R.anim.slide_no, R.anim.slide_out_left);
	}

	@Override
	public void onResume() {
		// TODO LC: preliminary support for views transitions
		this.overridePendingTransition(R.anim.left_in, R.anim.left_out);
		super.onResume();
		HomeView.home_pressed = "disable";
	}

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		Log.d("alex", "on Pause");
		HomeView.home_pressed = "wait";
	}

}