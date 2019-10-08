package com.ylz.waveform.presswavecore.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

import com.ylz.waveform.presswavecore.enums.PressUnitEnum;
import com.ylz.waveform.presswavecore.model.Point;
import com.ylz.waveform.presswavecore.model.db.LocalWavePoint;
import com.ylz.waveform.presswavecore.utils.ScreenUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class PointView extends View {

    private int paddingTop ;
    private int paddingBottom ;
    private int paddingLeft ;
    private int paddingRight ;
    private int standardX = 1000;
    private int standardY = 5;
    private int width;
    private int height;
    private float oneGridWidth;
    private float oneGridHeight;
    private int markLong ;
    private int markShort ;
    private int arrowWidth ;
    private int arrowHeight ;
    private int startX;
    private  int endX;
    private int startY;
    private int endY;
    private int xGridCount = 10;
    private int yGridCount = 5;
    private int xOneGirdMarkCount = 5;
    private int yOneGridMarkCount = 5;
    private int backGridColor = Color.GRAY;
    private int markTextSize ;
    private PressUnitEnum standardYUnit = PressUnitEnum.MPA;
    private Paint mPaint;
    private int strokeWidth;
    private List<? extends Point> pointList = new ArrayList<>();

    public PointView(Context context) {
        super(context);
    }

    public PointView(Context context,  AttributeSet attrs) {
        super(context, attrs);
        paddingTop = ScreenUtils.dip2px(context,15);
        paddingBottom = ScreenUtils.dip2px(context,20);
        paddingLeft = ScreenUtils.dip2px(context,20);
        paddingRight = ScreenUtils.dip2px(context,15);

        markLong = ScreenUtils.dip2px(context,5);
        markShort = ScreenUtils.dip2px(context,2);
        arrowWidth = ScreenUtils.dip2px(context,5);
        arrowHeight = ScreenUtils.dip2px(context,5);

        markTextSize = ScreenUtils.dip2px(context,10);

        strokeWidth = ScreenUtils.dip2px(context,1);

    }
    public void init(){
        width = getMeasuredWidth();
        height = getMeasuredHeight();
        startX = paddingLeft;
        endX = width - paddingRight;
        startY = paddingTop;
        endY = height - paddingBottom;
        oneGridWidth = (endX - startX)/xGridCount;
        oneGridHeight = (endY - startY)/yGridCount;
        //初始化画笔
        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setDither(true);
    }
    public PointView(Context context,  AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        init();
        //drawBackground(canvas);
        drawAxisY(canvas);
        drawAxisX(canvas);
        drawMarkGrid(canvas);
        drawPoint(canvas,pointList);
    }

    private void drawBackground(Canvas canvas){
        mPaint.setColor(backGridColor);
        mPaint.setStyle(Paint.Style.FILL);
        canvas.drawRect(0,0,width,height,mPaint);

    }
    private void drawAxisY(Canvas canvas){
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setColor(Color.WHITE);
        mPaint.setStrokeWidth(strokeWidth);
        mPaint.setStrokeJoin(Paint.Join.ROUND);
        canvas.drawLine(startX,endY,startX,0,mPaint);
        canvas.drawLine(startX,0,startX-arrowWidth,arrowHeight,mPaint);
        canvas.drawLine(startX,0,startX+arrowWidth,arrowHeight,mPaint);
    }
    private void drawAxisX(Canvas canvas){
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setColor(Color.WHITE);
        mPaint.setStrokeWidth(strokeWidth);
        mPaint.setStrokeJoin(Paint.Join.ROUND);
        canvas.drawLine(startX,endY,width,endY,mPaint);
        canvas.drawLine(width,endY,width-arrowHeight,endY-arrowHeight,mPaint);
        canvas.drawLine(width,endY,width-arrowHeight,endY+arrowWidth,mPaint);

    }
    private void drawMarkGrid(Canvas canvas){
        mPaint.setColor(Color.WHITE);
        int xIndex =0;
        float xMark = oneGridWidth/xOneGirdMarkCount;
        for(float x = startX;x<endX;x=x+xMark){//x轴
            if(xIndex%xOneGirdMarkCount == 0){
                canvas.drawLine(x,endY,x,startY,mPaint);
                String markText = String.valueOf(xIndex*(standardX/xGridCount)/xOneGirdMarkCount);
                mPaint.setTextAlign(Paint.Align.CENTER);
                mPaint.setTextSize(markTextSize);
                Paint.FontMetrics fm = mPaint.getFontMetrics();
                canvas.drawText(markText,x,endY+Math.abs(fm.ascent),mPaint);

            }else{
                canvas.drawLine(x,endY,x,endY-markShort,mPaint);
            }
            xIndex++;
        }
        float yMark = oneGridHeight/yOneGridMarkCount;
        int yIndex = 0;
        for(float y = endY;y>startY;y=y-yMark){//y轴

            if(yIndex%yOneGridMarkCount == 0){
                canvas.drawLine(startX,y,endX,y,mPaint);
                //绘制y刻度文字
                if(yIndex != 0){
                    String markText = String.valueOf(yIndex*(standardY/yGridCount)/yOneGridMarkCount);
                    mPaint.setTextAlign(Paint.Align.RIGHT);
                    mPaint.setTextSize(markTextSize);
                    Paint.FontMetrics fm = mPaint.getFontMetrics();
                    float distance = (fm.bottom - fm.top)/2 - fm.bottom;
                    canvas.drawText(markText,startX,y+distance,mPaint);
                }

            }else{
                canvas.drawLine(startX,y,startX+markShort,y,mPaint);
            }
            yIndex++;
        }
        mPaint.setTextAlign(Paint.Align.RIGHT);
        mPaint.setTextSize(markTextSize);
        Paint.FontMetrics fm = mPaint.getFontMetrics();
        canvas.drawText(standardYUnit.toString(),startX,Math.abs(fm.top),mPaint);
    }
    public void drawPoint(Canvas canvas,List<? extends Point> pointList){
        mPaint.setStyle(Paint.Style.FILL);
        mPaint.setColor(Color.CYAN);

        for(Object object :pointList){
            Point point = (Point) object;
            float x = convertX(point.getX());
            float y = convertY(point.getY(),point.getPressUnit());
            canvas.drawPoint(x,y,mPaint);
        }
    }

    private float convertX(float x){
        return startX+ x*(endX-startX)/standardX;
    }
    private float convertY(float y,int pressUnitKey){
        float currentUnitValue = convertCurrentUnitValue(y,pressUnitKey);
        int validateHeight = endY - startY;

        return startY + validateHeight - currentUnitValue*validateHeight/standardY;
    }
    private float convertCurrentUnitValue(float value,int pressUnitKey){
        PressUnitEnum pressUnitEnum = PressUnitEnum.getEnumByKey(pressUnitKey);
        switch (pressUnitEnum){
            case PA:
                switch (standardYUnit){
                    case BA:
                        value = value/100000;
                        break;
                    case KPA:
                        value = value/1000;
                        break;
                    case MPA:
                        value = value/1000000;
                        break;
                }
                break;
            case KPA:
                switch (standardYUnit){
                    case PA:
                        value = value * 1000;
                        break;
                    case BA:
                        value = value/100;
                        break;
                    case MPA:
                        value = value/1000;
                        break;
                }
                break;
            case BA:
                switch (standardYUnit){
                    case PA:
                        value = value * 100000;
                        break;
                    case KPA:
                        value = value * 100;
                        break;
                    case MPA:
                        value = value/10;
                        break;
                }
                break;
            case MPA:
                switch (standardYUnit){
                    case PA:
                        value = value * 1000000;
                        break;
                    case BA:
                        value = value * 10;
                        break;
                    case KPA:
                        value = value * 1000;
                        break;
                }
                break;
        }
        return value;
    }


    public int generateRandomX(){
        Random random = new Random();
        int number = random.nextInt(standardX);
        return number;
    }

    public PressUnitEnum generateRandomUnit(){
        Random random = new Random();
        int key = random.nextInt(2);
        return PressUnitEnum.getEnumByKey(key+1);
    }

    public int generateRandomY( PressUnitEnum pressUnitEnum){
        int y ;
        Random random = new Random();
        int bound = 0;
        switch (pressUnitEnum){
            case PA:
                bound = standardY*1000000;
                break;
            case KPA:
                bound = standardY*1000;
                break;
            case BA:
                bound = standardY * 10;
                break;
            case MPA:
                bound = standardY ;
                break;
        }
        y = random.nextInt(bound);
        return y;
    }

    public List<LocalWavePoint> generatePointList(){
        List<LocalWavePoint> pointList = new ArrayList<>();
        for(int i=0;i<10;i++){

            float x = generateRandomX();
            PressUnitEnum pressUnitEnum = generateRandomUnit();
            float y = generateRandomY(pressUnitEnum);
            LocalWavePoint point = new LocalWavePoint();
            point.setX(x);
            point.setY(y);
            point.setPressUnit(pressUnitEnum.getKey());
            pointList.add(point);
        }
        return pointList;
    }

    public void setPointList(List<? extends Point> pointList) {
        this.pointList = pointList;
    }
}
