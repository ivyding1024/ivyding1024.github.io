package com.mdbiomedical.app.vion.vian_health.view;

import com.mdbiomedical.app.vion.vian_health.R;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;

//import android.support.v4.view.GestureDetectorCompat;

public class HrTrendView extends View {
	private final static String TAG = "HrTrendView";
	final int ciDispTime = 30;
	// private GestureDetectorCompat mDetector;

	Paint myPaint = new Paint();
	Path path = new Path();
	int start_i = 0;
	public HrTrendView(Context context, AttributeSet attrs) {
		super(context, attrs);
		// mDetector = new GestureDetectorCompat(context, new
		// MyGestureListener());
		// TODO Auto-generated constructor stub

	}

	public void clear(){
		start_i=0;
		
	}
	/*
	 * @Override public boolean onTouchEvent(MotionEvent e) { return
	 * mDetector.onTouchEvent(e); }
	 */
	@Override
	protected void onDraw(Canvas canvas) {
		// TODO Auto-generated method stub
		super.onDraw(canvas);

		int iHrWidth = RecordDetail.iHrWidth;
		int iHrHeight = RecordDetail.iHrHeight;
		int iGraphLeft = (int) (iHrWidth * 0.11);
		int iGraphRight = (int) (iHrWidth * 0.87);
		int iGraphTop = (int) (iHrHeight * 0.25);
		int iGraphBottom = (int) (iHrHeight * 0.85);
		int iGraphWidth = iGraphRight - iGraphLeft;
		int iGraphHeight = iGraphBottom - iGraphTop;

		myPaint.setStyle(Paint.Style.FILL_AND_STROKE);
		myPaint.setColor(Color.GRAY);
		myPaint.setStrokeWidth(3);
		canvas.drawLine(iGraphLeft, iGraphTop, iGraphLeft, iGraphBottom, myPaint);
		canvas.drawLine(iGraphLeft, iGraphBottom, iGraphRight, iGraphBottom, myPaint);
		myPaint.setStrokeWidth(1);
		for (int i = 0; i < 4; i++)
			canvas.drawLine(iGraphLeft, iGraphTop + iGraphHeight * i / 4, iGraphRight, iGraphTop + iGraphHeight * i / 4, myPaint);

		for (int i = 1; i <= 30; i++) {
			if (i % 10 == 0)
				canvas.drawLine(iGraphLeft + iGraphWidth * i / 30, iGraphBottom, iGraphLeft + iGraphWidth * i / 30, iGraphBottom - 20, myPaint);
			else
				canvas.drawLine(iGraphLeft + iGraphWidth * i / 30, iGraphBottom, iGraphLeft + iGraphWidth * i / 30, iGraphBottom - 10, myPaint);
		}

		myPaint.setTextSize(20);
		canvas.drawText("40", iGraphLeft - 30, iGraphBottom + 10, myPaint);
		canvas.drawText("85", iGraphLeft - 30, iGraphBottom - iGraphHeight / 2 + 10, myPaint);
		canvas.drawText("130", iGraphLeft - 40, iGraphTop + 10, myPaint);
		canvas.drawText("("+getResources().getString(R.string.hr)+")", iGraphLeft - 40, iGraphTop - 20, myPaint);

		canvas.drawText("10", iGraphLeft + iGraphWidth / 3 - 15, iGraphBottom + 25, myPaint);
		canvas.drawText("20", iGraphLeft + iGraphWidth * 2 / 3 - 15, iGraphBottom + 25, myPaint);
		canvas.drawText("30 ("+getResources().getString(R.string.sec)+")", iGraphLeft + iGraphWidth - 75, iGraphBottom + 25, myPaint);

		//myPaint.setColor(Color.GREEN);
		myPaint.setColor(Color.rgb(79, 137, 144));
		myPaint.setStrokeWidth(15);
		myPaint.setStyle(Paint.Style.STROKE);

		Log.d(TAG, "RecordDetail.iHrCnt=" + RecordDetail.iHrCnt);

		float fPos, fX;
		path.rewind();
		if(RecordDetail.iAnalysisType == RecordsView.TYPE_BP)
		{
			return;
		}
		
		if (start_i == 0) {
			for (int i = 1; i < RecordDetail.iHrCnt; i++) {
				if (RecordDetail.HRDataTimestamp[i] > 384)
				{
					start_i = i;
					break;
				}
			}

		}
		//else if
		if(start_i !=0 ) {
				fPos = (RecordDetail.HRData[start_i] - 40) * iGraphHeight / (130 - 40);
				if (fPos > iGraphHeight)
					fPos = iGraphHeight;
				else if (fPos < 0)
					fPos = 0;
				path.moveTo(iGraphLeft+5, iGraphBottom - fPos);
			
			
			for (int i = start_i; i < RecordDetail.iHrCnt; i++) {


				fPos = (RecordDetail.HRData[i] - 40) * iGraphHeight / (130 - 40);
				if (fPos > iGraphHeight)
					fPos = iGraphHeight;
				else if (fPos < 0)
					fPos = 0;
				if(RecordDetail.HRDataTimestamp[i]>4224)
				{
					fX = iGraphLeft+10 + (4224 - RecordDetail.HRDataTimestamp[start_i]) * iGraphWidth / 128 / 30;
					path.lineTo(fX, iGraphBottom - fPos);
					canvas.drawPath(path, myPaint);
					return;
				}
				fX = iGraphLeft+5 + (RecordDetail.HRDataTimestamp[i] - RecordDetail.HRDataTimestamp[start_i]) * iGraphWidth / 128 / 30;

				
				path.lineTo(fX, iGraphBottom - fPos);
				// Log.d(TAG, "x="+fX+", y="+(iGraphBottom-fPos));
			}
		}
		canvas.drawPath(path, myPaint);

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
