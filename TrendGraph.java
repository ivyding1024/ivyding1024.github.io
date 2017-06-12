package com.mdbiomedical.app.vion.vian_health.view;

import com.mdbiomedical.app.vion.vian_health.R;
import com.mdbiomedical.app.vion.vian_health.util.DeviceConstant;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Paint.Style;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;

public class TrendGraph  extends View {

	Paint myPaint = new Paint();
	Path path = new Path();
	DashPathEffect dash=new DashPathEffect(new float[] {10,5}, 0);
	DashPathEffect dashV=new DashPathEffect(new float[] {10,10}, 0);
	Rect bounds = new Rect();
	int iPeriod=0;

	final int iaPeriod[]={Color.rgb(82, 170, 205), Color.rgb(43, 92, 144), Color.rgb(198, 94, 55)
			, Color.rgb(143, 56, 45)};
	
	
	public boolean baPeriodEnabled[]={true, true, true, true};
	public int iaSysBP[][]=new int[31][4]; 
	public int iaDiaBP[][]=new int[31][4];
	public int iaPulse[][]=new int[31][4];
	public String saBarText[]=new String[31];
	public int iDataCount=7;
	public int iSepPos=0;
	public String sSepText="";
	
	public TrendGraph(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
	}

	public void setPeriod(int period) {
		iPeriod=period;
	}
	
	@Override
	protected void onDraw(Canvas canvas) {
		
		// TODO Auto-generated method stub
		super.onDraw(canvas);
		
		int iWidth=this.getWidth();
		int iHeight=this.getHeight();
		float fBpBottom=iHeight*356/590;
		float fPulseBottom=iHeight*560/590;
		
		myPaint.setColor(Color.WHITE);
		myPaint.setStyle(Paint.Style.FILL_AND_STROKE); 
		canvas.drawRect(new RectF(0,0,iWidth, iHeight), myPaint);
		
		myPaint.setColor(Color.rgb(147, 147, 147));			
		myPaint.setStrokeWidth(3);
		canvas.drawLine(0, 0, iWidth, 0, myPaint);
		canvas.drawLine(0, fBpBottom, iWidth, fBpBottom, myPaint);
		canvas.drawLine(0, fPulseBottom, iWidth, fPulseBottom, myPaint);

		
		final int iUpBP=200, iLowBP=40, iBpStep=20;
		int iGridCnt=(200-iLowBP)/iBpStep;
		float fHeightPerStep=fBpBottom/((iUpBP-iLowBP)/iBpStep);		
		
		int iMax=0, iMin=300;
		for(int i=0; i<iDataCount; i++) {
			for(int j=0; j<4; j++) {
				if(baPeriodEnabled[j]==false)
					continue;
				
				if(iMax<iaPulse[i][j])
					iMax=iaPulse[i][j];
				if(iMin>iaPulse[i][j])
					iMin=iaPulse[i][j];
			}
		}
		
		if(iMin==0)
			iMin=60;
		if(iMax==0)
			iMax=80;
		
		final int iPulseStep=20;
		int iUpPulse=((int)(iMax/iPulseStep)+2)*iPulseStep, iLowPulse=((int)(iMin/iPulseStep)-1)*iPulseStep;
		int iGridPCnt=(iUpPulse-iLowPulse)/iPulseStep;
		float fHeightPerPStep=(fPulseBottom-fBpBottom)/iGridPCnt;	
		
		
		
		
		myPaint.setStyle(Paint.Style.FILL_AND_STROKE); 
		myPaint.setStrokeWidth(2);
		myPaint.setTextSize((int) (DeviceConstant.screenHeight * 0.02f));
		myPaint.setTextAlign(Paint.Align.RIGHT);
		myPaint.getTextBounds("180" ,0, 3, bounds);
		int height = bounds.height();
		float fLeft=bounds.width()+30;
		float fChartWidth=iWidth-fLeft;
		
		// draw BP axis unit
		for(int j=1; j<iGridCnt; j++)	
			canvas.drawText(String.valueOf(iUpBP-j*iBpStep), fLeft-15, fHeightPerStep*j+height/2, myPaint);
		
		// draw Pulse axis unit
		for(int j=1; j<iGridPCnt; j++) 
			canvas.drawText(String.valueOf(iUpPulse-j*iPulseStep), fLeft-15, fBpBottom+fHeightPerPStep*j+height/2, myPaint);
		
		// draw BP horizontal dash lines
		myPaint.setColor(Color.rgb(173, 173, 173));
		myPaint.setStrokeWidth(2);
		myPaint.setStyle(Style.STROKE);
		myPaint.setPathEffect(dash);	
		for(int j=1; j<iGridCnt; j++) 	
			canvas.drawLine(fLeft, fHeightPerStep*j, iWidth, fHeightPerStep*j, myPaint);
			
		// draw Pulse horizontal dash lines
		for(int j=1; j<iGridPCnt; j++) 	
			canvas.drawLine(fLeft, fBpBottom+fHeightPerPStep*j, iWidth, fBpBottom+fHeightPerPStep*j, myPaint);
				
		myPaint.setPathEffect(null);
		
		// draw X axis date
final float fBottomSpace=10f;	
		myPaint.setStyle(Style.FILL_AND_STROKE);
		myPaint.setStrokeWidth(1);
		
		myPaint.setTextSize((int) (DeviceConstant.screenHeight * 0.017f));
		myPaint.setTextAlign(Paint.Align.CENTER);
		float fSplitWidth=fChartWidth/iDataCount;
		try{
			canvas.drawText(saBarText[0], fLeft+fSplitWidth/2, fPulseBottom+fBottomSpace+height, myPaint);	
		}
		catch(Exception e)
		{
			
		}
		
		for(int i=1; i<iDataCount; i++) {
			canvas.drawLine(fLeft+fSplitWidth*i, fPulseBottom+fBottomSpace
					, fLeft+fSplitWidth*i, fPulseBottom+fBottomSpace+height, myPaint);
			try{
				canvas.drawText(saBarText[i], fLeft+fSplitWidth*i+fSplitWidth/2, fPulseBottom+fBottomSpace+height, myPaint);
			}
			catch(Exception e)
			{
				
			}
			
		}

		// draw separate vertical line
		if(iSepPos>=0) {
			float fSepPos=fLeft+fSplitWidth*iSepPos;
			myPaint.setColor(Color.rgb(131, 131, 131));		
			myPaint.setStrokeWidth(3);
			myPaint.setStyle(Style.STROKE);
			myPaint.setPathEffect(dashV);
		
			canvas.drawLine(fSepPos, 0, fSepPos, fPulseBottom, myPaint);
			
			myPaint.setPathEffect(null);
			
final float fIndShift=30;
final float fIndHeight=30;			
			fSepPos+=30;
			myPaint.setStrokeWidth(1);
			myPaint.setStyle(Style.FILL_AND_STROKE);
			path.rewind();//clear all line
			path.setFillType(Path.FillType.EVEN_ODD);
			path.moveTo(fSepPos, fIndShift);
			path.lineTo(fSepPos+fIndHeight, fIndShift+fIndHeight/3);
			path.lineTo(fSepPos, fIndShift+fIndHeight);
			path.lineTo(fSepPos, fIndShift);
			path.close();

			canvas.drawPath(path, myPaint);
			
			myPaint.setTextSize((int) (DeviceConstant.screenHeight * 0.02f));
			myPaint.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.BOLD));
			myPaint.setTextAlign(Paint.Align.LEFT);			
			canvas.drawText(sSepText, fSepPos+fIndHeight+20, fIndShift+fIndHeight, myPaint);
			myPaint.setTypeface(null);
		}
		
		float fDataWidth=(float)(iWidth-fLeft)/iDataCount;
		float fPeriodWidth=(float)(iWidth-fLeft)/iDataCount/4;
		float fBarW;
		fBarW=5;
		Log.d("alex","fBarW="+fBarW);
//Cancel circle radious with different size.
//		switch(iPeriod) {
//		case AnalysisView.WEEK:		
//			fBarW=(fPeriodWidth-16)/2;
//			break;
//		case AnalysisView.MONTH:		
//			fBarW=(fPeriodWidth-8)/2;
//			break;
//		case AnalysisView.YEAR:	
//		default:
//			fBarW=(fPeriodWidth-10)/2;
//			break;
//		}
		
		// draw BP bars & Pulse point
		myPaint.setStrokeWidth(3);
		myPaint.setStyle(Paint.Style.FILL_AND_STROKE); 
		for(int i=0; i<iDataCount; i++) {
			for(int j=0; j<4; j++) {
				if(baPeriodEnabled[j]==false)
					continue;
				
				if(iaDiaBP[i][j]<iLowBP)
					iaDiaBP[i][j]=iLowBP;				
				
				myPaint.setColor(iaPeriod[j]);
				// draw BP bars
				float fMax=(iUpBP-iaSysBP[i][j])*fHeightPerStep/iBpStep+fBarW;
				float fMin=(iUpBP-iaDiaBP[i][j])*fHeightPerStep/iBpStep-fBarW;
				if(iaSysBP[i][j]>iLowBP) {
//					canvas.drawRect(fLeft+i*fDataWidth+fPeriodWidth*j+fPeriodWidth/2-fBarW, fMax
//								, fLeft+i*fDataWidth+fPeriodWidth*j+fPeriodWidth/2+fBarW, fMin, myPaint);		
					canvas.drawCircle(fLeft+i*fDataWidth+fPeriodWidth*j+fPeriodWidth/2, fMax, fBarW, myPaint);
					canvas.drawCircle(fLeft+i*fDataWidth+fPeriodWidth*j+fPeriodWidth/2, fMin, fBarW, myPaint);
				}
				
				// draw Pulse point
				fMax=fBpBottom+(iUpPulse-iaPulse[i][j])*fHeightPerPStep/iPulseStep;
				canvas.drawCircle(fLeft+i*fDataWidth+fPeriodWidth*j+fPeriodWidth/2, fMax, fBarW, myPaint);				
			}
		}
	}

	

}
