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

public class ECG5SecView extends View {
	private final static String TAG = "ECG5SecView";

	Paint myPaint = new Paint();
	Path trace = new Path();
	int iIndex = 0;
	float fGen = 65536 / 600;
	public static float ecgSize = 1;
	public float fPixelPerAmp;

	public ECG5SecView(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
		iIndex = Integer.valueOf(this.getTag().toString());
		Log.e("tina", "tag = "+ iIndex);
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
		canvas.drawRect(new RectF(0, 0, iECGWidth, iECGHeight * 6), myPaint);

		int ecg_grid1 = getResources().getColor(R.color.ecg_grid1);
		int ecg_grid2 = getResources().getColor(R.color.ecg_grid2);
		int ecg_grid3 = getResources().getColor(R.color.ecg_grid3);
		float iGridHeight = iECGHeight/5; // always display 5 seconds
		float iCell = iECGHeight / 25;
		float iSmallCell = iECGHeight / 125;
		Log.e("tina", "ecgSize = "+ecgSize+"");
		for (float i = 0; i < iECGHeight * 6; i += iGridHeight) {// 粗直線
			myPaint.setColor(ecg_grid3);
			myPaint.setStrokeWidth(3);
			canvas.drawLine(0, i, iECGWidth, i, myPaint);
			myPaint.setStrokeWidth(1);
			for (float j = i; j < i + iGridHeight - iCell + 1; j += iCell) { // 直線

				myPaint.setColor(ecg_grid2);
				myPaint.setStrokeWidth(2);
				canvas.drawLine(0, j, iECGWidth, j, myPaint);
				myPaint.setStrokeWidth(1);
				myPaint.setColor(ecg_grid1);
				for (float k = iSmallCell; k < iCell - iSmallCell + 1; k += iSmallCell) {
					canvas.drawLine(0, j + k, iECGWidth, j + k, myPaint);// 細直線
				}
			}
		}

		myPaint.setStyle(Paint.Style.STROKE);
		myPaint.setStrokeWidth(1);
		for (int i = iECGWidth; i > 0; i -= iCell) {// 橫線
			myPaint.setStrokeWidth(2);
			canvas.drawLine(i, 0, i, iECGHeight * 6, myPaint);
			myPaint.setStrokeWidth(1);
			for (int k = 1; k < 5; k++) {// 細衡線
				canvas.drawLine(i - k * iSmallCell, 0, i - k * iSmallCell, iECGHeight * 6, myPaint);
			}
		}

		
			myPaint.setColor(Color.BLACK);
			myPaint.setStrokeWidth(4);

			// canvas.drawLine(iECGWidth - iCell * 3, iCell, iECGWidth - iCell *
			// 3, iCell + iSmallCell, myPaint);
			// canvas.drawLine(iECGWidth - iCell * 3, iCell + iSmallCell * 3,
			// iECGWidth - iCell * 3, iCell + iSmallCell * 4, myPaint);
			// canvas.drawLine(iECGWidth - iCell * 1, iCell + iSmallCell * 1,
			// iECGWidth - iCell * 1, iCell + iSmallCell * 3, myPaint);
			// canvas.drawLine(iECGWidth - iCell * 3, iCell + iSmallCell * 1,
			// iECGWidth - iCell * 1, iCell + iSmallCell * 1, myPaint);
			// canvas.drawLine(iECGWidth - iCell * 3, iCell + iSmallCell * 3,
			// iECGWidth - iCell * 1, iCell + iSmallCell * 3, myPaint);
			canvas.drawLine(iECGWidth - iCell * (3 + (ecgSize - 1) * 2), iCell, iECGWidth - iCell * (3 + (ecgSize - 1) * 2), iCell + iSmallCell, myPaint);
			canvas.drawLine(iECGWidth - iCell * (3 + (ecgSize - 1) * 2), iCell + iSmallCell * 3, iECGWidth - iCell * (3 + (ecgSize - 1) * 2), iCell + iSmallCell * 4, myPaint);
			canvas.drawLine(iECGWidth - iCell * 1, iCell + iSmallCell * 1, iECGWidth - iCell * 1, iCell + iSmallCell * 3, myPaint);
			canvas.drawLine(iECGWidth - iCell * (3 + (ecgSize - 1) * 2), iCell + iSmallCell * 1, iECGWidth - iCell * 1, iCell + iSmallCell * 1, myPaint);
			canvas.drawLine(iECGWidth - iCell * (3 + (ecgSize - 1) * 2), iCell + iSmallCell * 3, iECGWidth - iCell * 1, iCell + iSmallCell * 3, myPaint);
			canvas.save();
			myPaint.setStyle(Paint.Style.FILL_AND_STROKE);
			myPaint.setStrokeWidth(2);
			myPaint.setTextSize(24);
			// canvas.rotate(90, iECGWidth - iCell * 3 - iSmallCell * 2, iCell);
			canvas.rotate(90, iECGWidth - iCell * (3 + (ecgSize - 1) * 2) - iSmallCell * 2, iCell);
			// canvas.drawText("1 mV", iECGWidth - iCell * 3 - iSmallCell * 2,
			// iCell, myPaint);
			canvas.drawText("1 mV", iECGWidth - iCell * (3 + (ecgSize - 1) * 2) - iSmallCell * 2, iCell, myPaint);
			canvas.restore();
		

		myPaint.setStyle(Paint.Style.STROKE);
		myPaint.setColor(Color.rgb(0, 0, 0));
		myPaint.setStrokeWidth(5);
		float fPos_forloop = 0;
		float fPos = 0;
		for (int index_x = 0; index_x < 6; index_x++) {
			iIndex = index_x;
			int iDrawSt = (iIndex * 5 * 256);
			int iDrawCnt = ((iIndex + 1) * 5 * 256);
			if (iDrawCnt > RecordDetail.iEcgCount)
				iDrawCnt = RecordDetail.iEcgCount;
			Log.d(TAG, "iEcgCount=" + RecordDetail.iEcgCount  );
			iDrawCnt -= iDrawSt;

			//Log.d(TAG, "Index=" + iIndex + ", iDrawSt=" + iDrawSt + ", iDrawCnt=" + iDrawCnt);

			trace.rewind();
			if (iDrawSt > 0)
				iDrawSt -= 1;
			float fAmp = RecordDetail.rawData[iDrawSt] * fPixelPerAmp;// 起始資料點
																		// x值
			Log.d(TAG, iDrawSt+". rawData=" + RecordDetail.rawData[iDrawSt]  );
			if (fAmp > iBaseLine)
				fAmp = iBaseLine;
			else if (fAmp < 0 - iBaseLine)
				fAmp = 0 - iBaseLine;
			trace.moveTo(iBaseLine + fAmp, fPos_forloop);// 起始資料點 ，y值改為可異動
			for (int i = 0; i < iDrawCnt; i++) {
				fPos = i * (iECGHeight / 5) / 256 + fPos_forloop;
				fAmp = RecordDetail.rawData[i + iDrawSt] * fPixelPerAmp;
				if (fAmp > iBaseLine)
					fAmp = iBaseLine;
				else if (fAmp < 0 - iBaseLine)
					fAmp = 0 - iBaseLine;

				trace.lineTo(iBaseLine + fAmp, fPos);

			}
			fPos_forloop = fPos;
			canvas.drawPath(trace, myPaint);
			canvas.save();

		}

		myPaint.setStyle(Paint.Style.FILL_AND_STROKE);
		myPaint.setStrokeWidth(1);
		myPaint.setTextSize(36);
		canvas.rotate(90, 30, iECGHeight - 50);
		//canvas.drawText(String.valueOf(5), 30, iECGHeight- 50, myPaint);
		canvas.drawText(String.valueOf(5)+"("+getResources().getString(R.string.sec)+")", -110, iECGHeight- 50, myPaint);//0923
		for (int index_x = 2; index_x < 7; index_x++)// 畫秒數
		{
			Log.d("alex","drawText index_x="+index_x);
			canvas.translate(iECGHeight, 0);
			//canvas.drawText(String.valueOf((index_x) * 5), 0, iECGHeight-50, myPaint);
			canvas.drawText(String.valueOf((index_x) * 5)+"("+getResources().getString(R.string.sec)+")", -140, iECGHeight-50, myPaint);//0923
		}
		canvas.restore();

	}

}
