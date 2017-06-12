package com.mdbiomedical.app.vion.vian_health.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.ImageView;

public class ECGView extends ImageView {
	private final static String TAG = "ECGView";
	
		Paint paint = new Paint();
		Path trace = new Path();
		int iCnt = 0;
		int iECGWidth, iECGHeight, iBaseLine;
		float fGridHeight, fCell;
		float fMvAmp;
		float fGen = 65536 / 600;
		float fPixelPerSample;
		public float fPixelPerAmp;
		//public static int ecgSize = 3;
		public static int ecgSize = 1;
		public boolean bBkgd = false;
		public int temp_max = 0;
		int start_i = 0;

		// private GestureDetectorCompat mDetector;

		public ECGView(Context context, AttributeSet attrs) {
			super(context, attrs);
			// mDetector = new GestureDetectorCompat(context, new
			// MyGestureListener());

		}

		/*
		 * @Override public boolean onTouchEvent(MotionEvent e) { return
		 * mDetector.onTouchEvent(e); }
		 */
		public void drawECGBkgd(Canvas canvas) {
//			iECGWidth = HomeView.iECGWidth1;
//			iECGHeight = HomeView.iECGHeight1;
			iECGHeight = HomeView.iECGWidth1;
			iECGWidth= HomeView.iECGHeight1;
			Log.d("sandy", "iECGWidth1="+iECGHeight);
			Log.d("sandy", "iECGHeight1="+iECGWidth);
			iBaseLine = iECGWidth / 2;
			//iBaseLine = iECGHeight / 2;
			fMvAmp = iECGHeight * 2 / 5 / 2;//sandy
			//fMvAmp = iECGHeight * 2 / 5 / 5;//1mv_px
			fPixelPerAmp = fMvAmp / fGen * ecgSize;//(1mv/122)
			
			//fPixelPerSample = (float) iECGHeight / (5 * 256);
			fPixelPerSample = (float) iECGHeight / (2 * 256);//(all data)per value/per px
			//fGridHeight = iECGHeight / 5;//sandy
			
			//fCell = iECGHeight / 25;
			//Log.d("sandy", "fCell="+fCell);
			temp_max = 0;
		}

		@Override
		public void onDraw(Canvas canvas) {
			super.onDraw(canvas);
			if (bBkgd == false) {
				drawECGBkgd(canvas);
				bBkgd = true;
			}
			fPixelPerAmp = fMvAmp / fGen * ecgSize;
			// fPixelPerSample=(float)iECGHeight/(5*256/ecgSize);
			//fPixelPerSample = (float) iECGHeight / (5 * 256);
			fPixelPerSample = (float) iECGHeight / (2 * 256);
			paint.setStyle(Paint.Style.STROKE);
			paint.setColor(Color.rgb(255, 255, 255));
			paint.setStrokeWidth(5);
			
			
			//show 2 sec
			short iDrawCnt = (short) (HomeView.displayCount - HomeView.displayCount % (256 * 2));
			int iLineSt = iDrawCnt;
			//Log.e("sandy", "iLineSt="+iLineSt);
			float fAmp =( HomeView.displayData[iDrawCnt] * fPixelPerAmp);//sandy add"-"
			//float fAmp = 196 * fPixelPerAmp;
			if (fAmp > iBaseLine)
				fAmp = iBaseLine;
			else if (fAmp < 0 - iBaseLine)
				fAmp = 0 - iBaseLine;
			trace.rewind();
			//trace.moveTo(iBaseLine + fAmp, 0);
			trace.moveTo(0, iBaseLine - fAmp);//sandy
			//iBaseLine = iECGWidth / 2;
			for (int i = 0; iDrawCnt < HomeView.ecgCount-768 && i <= 256 * 2; iDrawCnt++, i++) {//sandy
				
				float fPos = i * fPixelPerSample;

				fAmp =( HomeView.displayData[iDrawCnt] * fPixelPerAmp);//sandy add"-"
				//fAmp = 196 * fPixelPerAmp;
				if (fAmp > iBaseLine)
					fAmp = iBaseLine;
				else if (fAmp < 0 - iBaseLine)
					fAmp = 0 - iBaseLine;

				// Log.d("ECG", "fAmp="+fAmp+ ", fPos="+fPos);
				//trace.lineTo(iBaseLine + fAmp, fPos);
				trace.lineTo(fPos, iBaseLine - fAmp);//sandy
				if (iLineSt == 512) {
					temp_max = iLineSt;
					//ecgSize = 3;
					ecgSize = 1;
				}
				if (temp_max < iLineSt) {
					temp_max = iLineSt;
//					if (HomeView.ecgSize == 3 || HomeView.ecgSize == 4)// HomeView.ecgSize==1,2就是1一倍;3=兩倍;4=3倍
//						ecgSize = 3;
//					if (HomeView.ecgSize == 5)
//						ecgSize = 4;
//					if (HomeView.ecgSize == 6)
//						ecgSize = 5;
					if (HomeView.ecgSize == 1 || HomeView.ecgSize == 2)// HomeView.ecgSize==1,2就是1一倍;3=兩倍;4=3倍
						ecgSize = 1;
					if (HomeView.ecgSize == 3)
						ecgSize = 2;
					if (HomeView.ecgSize == 4)
						ecgSize = 3;
					fPixelPerAmp = fMvAmp / fGen * ecgSize;
					// fPixelPerSample=(float)iECGHeight/(5*256/ecgSize);
				}
			}
			canvas.drawPath(trace, paint);

			final int iSpaceCnt = 8;//20  clear大小
//ECG畫線空白clear
			if (iDrawCnt > 256 * 2 + iSpaceCnt) {

				// int iPreCnt=iDrawCnt-256*5/ecgSize+iSpaceCnt;
				// int iPreLineSt=iLineSt-256*5/ecgSize;
				int iPreCnt = iDrawCnt - 256 * 2 + iSpaceCnt;
				int iPreLineSt = iLineSt - 256 * 2;
				fAmp =( HomeView.displayData[iPreCnt] * fPixelPerAmp);//sandy add"-"
				//fAmp = 196 * fPixelPerAmp;
				if (fAmp > iBaseLine)
					fAmp = iBaseLine;
				else if (fAmp < 0 - iBaseLine)
					fAmp = 0 - iBaseLine;

				trace.rewind();
				trace.moveTo((iPreCnt - iPreLineSt) * fPixelPerSample,iBaseLine - fAmp);//sandy
				for (; iPreCnt < iLineSt; iPreCnt++) {
					float fPos = (iPreCnt - iPreLineSt) * fPixelPerSample;
					fAmp = (HomeView.displayData[iPreCnt] * fPixelPerAmp);//sandy add"-"
					if (fAmp > iBaseLine)
						fAmp = iBaseLine;
					else if (fAmp < 0 - iBaseLine)
						fAmp = 0 - iBaseLine;

					// Log.d("ECG", "fAmp="+fAmp+ ", fPos="+fPos);
					//trace.lineTo(iBaseLine + fAmp, fPos);
					trace.lineTo( fPos,iBaseLine - fAmp);//sandy
				}

				canvas.drawPath(trace, paint);
			}
			
			//imv line
			
//			paint.setColor(Color.BLACK);
//			paint.setStrokeWidth(4);
//			float iCell = iECGHeight / 10;
//			float iSmallCell = iECGHeight / 50;
//			
//			canvas.drawLine(iECGWidth - iCell * (3 + (ecgSize - 1) * 2), iCell, iECGWidth - iCell * (3 + (ecgSize - 1) * 2), iCell + iSmallCell, paint);
//			canvas.drawLine(iECGWidth - iCell * (3 + (ecgSize - 1) * 2), iCell + iSmallCell * 3, iECGWidth - iCell * (3 + (ecgSize - 1) * 2), iCell + iSmallCell * 4, paint);
//			canvas.drawLine(iECGWidth - iCell * 1, iCell + iSmallCell * 1, iECGWidth - iCell * 1, iCell + iSmallCell * 3, paint);
//			canvas.drawLine(iECGWidth - iCell * (3 + (ecgSize - 1) * 2), iCell + iSmallCell * 1, iECGWidth - iCell * 1, iCell + iSmallCell * 1, paint);
//			canvas.drawLine(iECGWidth - iCell * (3 + (ecgSize - 1) * 2), iCell + iSmallCell * 3, iECGWidth - iCell * 1, iCell + iSmallCell * 3, paint);
//
//			paint.setStyle(Paint.Style.FILL_AND_STROKE);
//			paint.setStrokeWidth(2);
//			paint.setTextSize(24);
            //imv
//			canvas.save();
//			canvas.rotate(90, iECGWidth - iCell * (3 + (ecgSize - 1) * 2) - iSmallCell * 2, iCell);
//			canvas.drawText("1 mV", iECGWidth - iCell * (3 + (ecgSize - 1) * 2) - iSmallCell * 2, iCell, paint);
//			canvas.restore();
//			String s;
//			canvas.save();
//			canvas.rotate(90, iECGWidth - fCell, iECGHeight - fGridHeight);
//			paint.setStyle(Paint.Style.FILL_AND_STROKE);
//			paint.setTextSize(98);
//			if (HomeView.bpPulseRate != 0)
//				s = String.valueOf(HomeView.bpPulseRate);
//			else
//				s = "- -";
//			canvas.drawText(s, iECGWidth - fCell + 100, iECGHeight - fGridHeight + 42, paint);// 畫出心跳值
			

		//	canvas.restore();

			HomeView.iDrawCnt = iDrawCnt;
			iCnt++;
			// Log.d("ECG",
			// "iCnt="+iCnt+" , iLineSt="+iLineSt+" , diff="+(HomeView.ecgCount-iLineSt)+", pos="+(HomeView.ecgCount-iLineSt)*fHeightPerCout);
		}

		/*
		 * private class MyGestureListener extends
		 * GestureDetector.SimpleOnGestureListener {
		 * 
		 * @Override public boolean onDoubleTap(MotionEvent event) {
		 * 
		 * HomeView.isECGDoubleTap = true;
		 * 
		 * return true; }
		 * 
		 * @Override public boolean onDown(MotionEvent event) { return true; } }
		 */
	}