package com.mdbiomedical.app.vion.vian_health.view;

import com.mdbiomedical.app.vion.vian_health.R;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

public class ECG30SecView extends View {
	private final static String TAG = "ECG5SecView";

	Paint myPaint = new Paint();
	Path trace = new Path();
	int iIndex = 0;
	float fGen = 65536 / 600;
	public static float ecgSize = 1;
	public float fPixelPerAmp;

	public ECG30SecView(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
		iIndex = Integer.valueOf(this.getTag().toString());
	}

	@Override
	protected void onDraw(Canvas canvas) {
		// TODO Auto-generated method stub
		super.onDraw(canvas);
		ecgSize = RecordDetail.ecgSize;
		// setLayerType(View.LAYER_TYPE_SOFTWARE, null);
		int iECGWidth = RecordDetail.iECGWidth;
		int iECGHeight = RecordDetail.iECGHeight;
		int iBaseLine = iECGWidth / 2;
		float fMvAmp = iECGHeight * 2 / 25;
		fPixelPerAmp = fMvAmp / fGen * ecgSize;
		myPaint.setColor(Color.WHITE);
		myPaint.setStyle(Paint.Style.FILL_AND_STROKE);
		canvas.drawRect(new RectF(0, 0, iECGWidth, iECGHeight), myPaint);

		int ecg_grid1 = getResources().getColor(R.color.ecg_grid1);
		int ecg_grid2 = getResources().getColor(R.color.ecg_grid2);
		int ecg_grid3 = getResources().getColor(R.color.ecg_grid3);
		float iGridHeight = iECGHeight / 5; // always display 5 seconds
		float iCell = iECGHeight / 25;
		float iSmallCell = iECGHeight / 125;
		for (float i = 0; i < iECGHeight; i += iGridHeight) {
			myPaint.setColor(ecg_grid3);
			myPaint.setStrokeWidth(3);
			canvas.drawLine(0, i, iECGWidth, i, myPaint);
			myPaint.setStrokeWidth(1);
			for (float j = i; j < i + iGridHeight - iCell + 1; j += iCell) { // minus
																				// 5
																				// to
																				// skip
																				// fraction
				myPaint.setColor(ecg_grid2);
				myPaint.setStrokeWidth(2);
				canvas.drawLine(0, j, iECGWidth, j, myPaint);
				myPaint.setStrokeWidth(1);
				myPaint.setColor(ecg_grid1);
				for (float k = iSmallCell; k < iCell - iSmallCell + 1; k += iSmallCell) {
					canvas.drawLine(0, j + k, iECGWidth, j + k, myPaint);
				}
			}
		}

		myPaint.setStyle(Paint.Style.STROKE);
		myPaint.setStrokeWidth(1);
		for (int i = iECGWidth; i > 0; i -= iCell) {
			myPaint.setStrokeWidth(2);
			canvas.drawLine(i, 0, i, iECGHeight, myPaint);
			myPaint.setStrokeWidth(1);
			for (int k = 1; k < 5; k++) {
				canvas.drawLine(i - k * iSmallCell, 0, i - k * iSmallCell, iECGHeight, myPaint);
			}
		}

		if (iIndex == 0) {
			myPaint.setColor(Color.BLACK);
			myPaint.setStrokeWidth(4);

//			canvas.drawLine(iECGWidth - iCell * 3, iCell, iECGWidth - iCell * 3, iCell + iSmallCell, myPaint);
//			canvas.drawLine(iECGWidth - iCell * 3, iCell + iSmallCell * 3, iECGWidth - iCell * 3, iCell + iSmallCell * 4, myPaint);
//			canvas.drawLine(iECGWidth - iCell * 1, iCell + iSmallCell * 1, iECGWidth - iCell * 1, iCell + iSmallCell * 3, myPaint);
//			canvas.drawLine(iECGWidth - iCell * 3, iCell + iSmallCell * 1, iECGWidth - iCell * 1, iCell + iSmallCell * 1, myPaint);
//			canvas.drawLine(iECGWidth - iCell * 3, iCell + iSmallCell * 3, iECGWidth - iCell * 1, iCell + iSmallCell * 3, myPaint);
			canvas.drawLine(iECGWidth - iCell * (3 + (ecgSize - 1) * 2), iCell, iECGWidth - iCell * (3 + (ecgSize - 1) * 2), iCell + iSmallCell, myPaint);
			canvas.drawLine(iECGWidth - iCell * (3 + (ecgSize - 1) * 2), iCell + iSmallCell * 3, iECGWidth - iCell * (3 + (ecgSize - 1) * 2), iCell + iSmallCell * 4, myPaint);
			canvas.drawLine(iECGWidth - iCell * 1, iCell + iSmallCell * 1, iECGWidth - iCell * 1, iCell + iSmallCell * 3, myPaint);
			canvas.drawLine(iECGWidth - iCell * (3 + (ecgSize - 1) * 2), iCell + iSmallCell * 1, iECGWidth - iCell * 1, iCell + iSmallCell * 1, myPaint);
			canvas.drawLine(iECGWidth - iCell * (3 + (ecgSize - 1) * 2), iCell + iSmallCell * 3, iECGWidth - iCell * 1, iCell + iSmallCell * 3, myPaint);
			canvas.save();
			myPaint.setStyle(Paint.Style.FILL_AND_STROKE);
			myPaint.setStrokeWidth(2);
			myPaint.setTextSize(24);
			//canvas.rotate(90, iECGWidth - iCell * 3 - iSmallCell * 2, iCell);
			canvas.rotate(90, iECGWidth - iCell * (3 + (ecgSize - 1) * 2) - iSmallCell * 2, iCell);
			//canvas.drawText("1 mV", iECGWidth - iCell * 3 - iSmallCell * 2, iCell, myPaint);
			canvas.drawText("1 mV", iECGWidth - iCell * (3 + (ecgSize - 1) * 2) - iSmallCell * 2, iCell, myPaint);
			canvas.restore();
		}

		myPaint.setStyle(Paint.Style.STROKE);
		myPaint.setColor(Color.rgb(0, 0, 0));
		myPaint.setStrokeWidth(5);
		int iDrawSt = (iIndex * 5 * 256);
		int iDrawCnt = ((iIndex + 1) * 5 * 256);
		if (iDrawCnt > RecordDetail.iEcgCount)
			iDrawCnt = RecordDetail.iEcgCount;
		iDrawCnt -= iDrawSt;

		Log.d(TAG, "Index=" + iIndex + ", iDrawSt=" + iDrawSt + ", iDrawCnt=" + iDrawCnt);

		trace.rewind();
		float fAmp = RecordDetail.rawData[iDrawSt] * fPixelPerAmp;
		if (fAmp > iBaseLine)
			fAmp = iBaseLine;
		else if (fAmp < 0 - iBaseLine)
			fAmp = 0 - iBaseLine;
		trace.moveTo(iBaseLine + fAmp, 0);
		for (int i = 0; i < iDrawCnt; i++) {
			float fPos = i * (iECGHeight / 5) / 256;
			fAmp = RecordDetail.rawData[i + iDrawSt] * fPixelPerAmp;
			if (fAmp > iBaseLine)
				fAmp = iBaseLine;
			else if (fAmp < 0 - iBaseLine)
				fAmp = 0 - iBaseLine;

			trace.lineTo(iBaseLine + fAmp, fPos);
		}
		canvas.drawPath(trace, myPaint);

		canvas.save();
		myPaint.setStyle(Paint.Style.FILL_AND_STROKE);
		myPaint.setStrokeWidth(1);
		myPaint.setTextSize(36);
		canvas.rotate(90, 30, iECGHeight - 50);
		canvas.drawText(String.valueOf((iIndex + 1) * 5), 30, iECGHeight - 50, myPaint);
		canvas.restore();
	}

}
