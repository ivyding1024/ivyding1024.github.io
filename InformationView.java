package com.mdbiomedical.app.vion.vian_health.view;

import android.app.Activity;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;

import com.mdbiomedical.app.vion.vian_health.R;
import com.mdbiomedical.app.vion.vian_health.util.ChangeView;
import com.mdbiomedical.app.vion.vian_health.util.DeviceConstant;
import com.mdbiomedical.app.vion.vian_health.util.UIUtils;

public class InformationView extends Activity {
	//GestureImageView ivInformation;
	protected void onDestroy() {  
        super.onDestroy();  
        System.gc();  
    }  
	//sandy0914
			public static DisplayMetrics dm = new DisplayMetrics();
			//
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.information_activity);

		//ivInformation= (GestureImageView) findViewById(R.id.ivInformation);
		//if(DeviceConstant.screenWidth>1080)
		//	ivInformation.setBackgroundResource(R.drawable.info_w_bg);
		//ImageView ivInformation= (ImageView) findViewById(R.id.ivInformation);
		//	ivInformation.setBackgroundResource(R.drawable.information_w_bg);
		//sandy0914
				getWindowManager().getDefaultDisplay().getMetrics(dm);
				DeviceConstant.screenWidth = dm.widthPixels;
				DeviceConstant.screenHeight = dm.heightPixels;
				DeviceConstant.screenDPI = dm.densityDpi;
				
		ImageView iv_toturial_menu;
		iv_toturial_menu = (ImageView) findViewById(R.id.iv_toturial_menu);
		TextView tv_tutorial_title = (TextView) findViewById(R.id.tv_tutorial_title);

		tv_tutorial_title.setTextSize(TypedValue.COMPLEX_UNIT_PX,
				(int) (DeviceConstant.screenHeight * 0.03f));

		// ¦^¤W¤@­¶
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
		
		
		/*
		ImageView imageView;
		imageView=(ImageView)findViewById(R.id.ivInformation);
        imageView.setOnTouchListener(new OnTouchListener()    {
        	// These matrices will be used to move and zoom image 
        	Matrix matrix = new Matrix(); 
        	Matrix savedMatrix = new Matrix(); 

        	// We can be in one of these 3 states 
        	static final int NONE = 0; 
        	static final int DRAG = 1; 
        	static final int ZOOM = 2; 
        	int mode = NONE; 

        	// Remember some things for zooming 
        	PointF start = new PointF(); 
        	PointF mid = new PointF(); 
        	float oldDist = 1f; 

        	float mCurrentScale = 1.0f;
        	@Override 
        	public boolean onTouch(View v, MotionEvent event) { 
        	 ImageView view = (ImageView) v; 

        	 view.setScaleType(ImageView.ScaleType.MATRIX);
        	 switch (event.getAction() & MotionEvent.ACTION_MASK) { 
        	 case MotionEvent.ACTION_DOWN: 
        	  savedMatrix.set(matrix); 
        	  start.set(event.getX(), event.getY()); 
        	  mode = DRAG; 
        	  break; 
        	 case MotionEvent.ACTION_POINTER_DOWN: 
        	  oldDist = spacing(event); 
        	  if (oldDist > 10f) { 
        	   savedMatrix.set(matrix); 
        	   midPoint(mid, event); 
        	   mode = ZOOM; 
        	  } 
        	  break; 
        	 case MotionEvent.ACTION_UP: 
        	 case MotionEvent.ACTION_POINTER_UP: 
        	  mode = NONE; 
        	  break; 
        	 case MotionEvent.ACTION_MOVE:

        	  if (mode == DRAG) { 
        	   // ...     
        	      Log.d("", "DRAG=" + event.getX());
        	   matrix.set(savedMatrix); 
        	   matrix.postTranslate(event.getX() - start.x, event.getY() - start.y);     
        	  } else if (mode == ZOOM) { 
        	      float newDist = spacing(event);
        	      Log.d("", "newDist=" + newDist);
        	      if (newDist > 5f) {
        	          matrix.set(savedMatrix);
        	        float  scale = newDist / oldDist; // setting the scaling of the
        	                                      // matrix...if scale > 1 means
        	                                      // zoom in...if scale < 1 means
        	                                      // zoom out
        	          matrix.postScale(scale, scale, mid.x, mid.y);
        	      }
        	  } 
         	 view.setImageMatrix(matrix);
        	  break; 
        	 } 

        	 return true; // indicate event was handled 
        	} 
        	//** Show an event in the LogCat view, for debugging / 

        	//** Determine the space between the first two fingers / 
        	private float spacing(MotionEvent event) { 
        	 float x = event.getX(0) - event.getX(1); 
        	 float y = event.getY(0) - event.getY(1); 
        	 return FloatMath.sqrt(x * x + y * y); 
        	} 

        	//** Calculate the mid point of the first two fingers / 
        	private void midPoint(PointF point, MotionEvent event) { 
        	 float x = event.getX(0) + event.getX(1); 
        	 float y = event.getY(0) + event.getY(1); 
        	 point.set(x / 2, y / 2);
        	 
        	}
        
        });
*/
	}

	@Override
	public void onBackPressed() {
		
		setResult(RESULT_OK);
		finish();
		overridePendingTransition(R.anim.slide_no, R.anim.slide_out_left);
	}
	
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		HomeView.home_pressed="disable";
	
	}
	
	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		Log.d("alex", "on Pause");
		HomeView.home_pressed = "wait";
		}
}