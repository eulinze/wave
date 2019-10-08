package com.ylz.waveform.activity.presswave.widget;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.util.AttributeSet;
import android.util.Log;
import android.util.LruCache;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import com.ylz.waveform.R;
import com.ylz.waveform.activity.presswave.enums.PressUnitEnum;
import com.ylz.waveform.activity.presswave.listener.UploadDataListener;
import com.ylz.waveform.activity.presswave.localService.BlueService;
import com.ylz.waveform.activity.presswave.model.db.LocalWavePoint;
import com.ylz.waveform.activity.presswave.model.db.ServerWavePoint;
import com.ylz.waveform.activity.presswave.utils.ScreenUtils;
import com.ylz.waveform.activity.presswave.utils.ToastUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class PointSurfaceView extends SurfaceView implements SurfaceHolder.Callback, UploadDataListener, ServerLineViewWrap.OnCompareLineListener, BlueService.BlueCallback {
    private final String TAG = "PointView";
    private SurfaceHolder surfaceHolder;
    private int paddingTop = ScreenUtils.dip2px(15);
    private int paddingBottom = ScreenUtils.dip2px(20);
    private int paddingLeft = ScreenUtils.dip2px(20);
    private int paddingRight = ScreenUtils.dip2px(15);
    private int standardX = 1000;
    private int standardY = 5;
    private int width;
    private int height;
    private float oneGridWidth;
    private float oneGridHeight;
    private int markLong = ScreenUtils.dip2px(5);
    private int markShort = ScreenUtils.dip2px(2);
    private int arrowWidth = ScreenUtils.dip2px(5);
    private int arrowHeight = ScreenUtils.dip2px(5);
    private int startX;
    private  int endX;
    private int startY;
    private int endY;
    private int xGridCount = 10;
    private int yGridCount = 5;
    private int xOneGirdMarkCount = 5;
    private int yOneGridMarkCount = 5;
    private int backGridColor = Color.GRAY;
    private int markTextSize = ScreenUtils.dip2px(10);
    private PressUnitEnum standardYUnit = PressUnitEnum.MPA;
    private Paint mPaint;
    private int strokeWidth = ScreenUtils.dip2px(1);

    private List<LocalWavePoint> pointList = new ArrayList<>();
    private DrawBackgroundThread drawBackgroundThread;
    private DrawPointThread drawPointThread;

    private Bitmap bitmap = null;
    private Canvas boardCanvas = null;

    private LruCache<String,LocalWavePoint> lruCache;
    private static final int LRU_CACHE_MAX_SIZE = 100;

    private static Object object = new Object();

    public PointSurfaceView(Context context) {
        super(context);
    }

    public PointSurfaceView(Context context, AttributeSet attrs) {
        super(context, attrs);
        surfaceHolder = this.getHolder();
        surfaceHolder.addCallback(this);

        lruCache = new LruCache<>(LRU_CACHE_MAX_SIZE);
        ServerLineViewWrap.setOnCompareLineListener(this);
    }

    public PointSurfaceView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        surfaceHolder = holder;
        drawBackgroundThread = new DrawBackgroundThread();
        drawPointThread = new DrawPointThread();
        ToastUtils.toast("surfaceCreated");
        Log.i(TAG, "surfaceCreated: ");
        width = getWidth();
        height = getHeight();
        bitmap = Bitmap.createBitmap(this.getWidth(), this.getHeight(), Bitmap.Config.ARGB_8888);
        boardCanvas = new Canvas(bitmap);
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
        drawBackgroundThread.start();
        //为了防止背景的drawRect覆盖画已经绘制的点，所以要等绘制完背景再绘制点
        try {
            drawBackgroundThread.join();//先执行完绘制背景，再执行其他线程
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        drawPointThread.start();

    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        ToastUtils.toast("surfaceChanged");
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        ToastUtils.toast("surfaceDestroyed");
    }

    @Override
    public void upload(List<LocalWavePoint> pointList) {
        new ReceiveThread(pointList).start();
    }

    @Override
    public void compare(List<ServerWavePoint> pointList) {
        Log.i(TAG, "compare: "+pointList);
        clearPoints();
        Thread drawCompareLineThread = new Thread(new DrawCompareLineRunnable(pointList));
        drawCompareLineThread.start();
    }
    public void clearPoints(){
        Thread clearCanvasThread  =  new Thread(new ClearCanvasRunnable());
        clearCanvasThread.start();
        try {
            clearCanvasThread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        drawBackgroundThread = new DrawBackgroundThread();
        drawBackgroundThread.start();
        try {
            drawBackgroundThread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
    @Override
    public void onShowPoints(List<LocalWavePoint> pointList) {
        new ReceiveThread(pointList).start();
    }

    class ClearCanvasRunnable implements Runnable{

        @Override
        public void run() {
            clearCanvas();
        }
    }
    public void clearCanvas(){
        synchronized (object){
            Canvas canvas = surfaceHolder.lockCanvas();
            //canvas.drawColor(Color.BLACK);
            canvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);
            surfaceHolder.unlockCanvasAndPost(canvas);
        }

    }
    class DrawBackgroundThread extends Thread{
        @Override
        public void run() {
            drawBackground();
        }
    }
    public  void drawBackground(){
        synchronized (object){
            drawBackground(boardCanvas);
            drawAxisY(boardCanvas);
            drawAxisX(boardCanvas);
            drawMarkGrid(boardCanvas);
            Canvas canvas = surfaceHolder.lockCanvas();
            canvas.drawBitmap(bitmap,0,0,null);
            surfaceHolder.unlockCanvasAndPost(canvas);
        }

    }
    public  void receive(List<LocalWavePoint> uploadPointList){
        synchronized (object){
            while (!pointList.isEmpty()){
                try {
                    Log.i(TAG,"上传进入等待");
                    object.wait();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            pointList = uploadPointList;
            Log.i(TAG,"赋值成功");
            object.notifyAll();
        }


    }
    class DrawCompareLineRunnable implements Runnable{
        private List<ServerWavePoint> serverWavePointList;

        public DrawCompareLineRunnable(List<ServerWavePoint> serverWavePointList) {
            this.serverWavePointList = serverWavePointList;
        }

        @Override
        public void run() {
            drawCompareLine(serverWavePointList);
        }
    }
    private void drawCompareLine(List<ServerWavePoint> serverWavePointList){
        synchronized (object){
            Path path = new Path();
            path.moveTo(startX,endY);
            for(ServerWavePoint point:serverWavePointList){
                float x = convertX(point.getX());
                float y = convertY(point.getY(),point.getPressUnit());
                path.lineTo(x,y);
            }
            mPaint.setStyle(Paint.Style.STROKE);
            mPaint.setColor(Color.YELLOW);
            boardCanvas.drawPath(path,mPaint);
            Canvas canvas = surfaceHolder.lockCanvas();
            canvas.drawBitmap(bitmap,0,0,null);
            surfaceHolder.unlockCanvasAndPost(canvas);
        }
    }
    public  void drawPoints(){
        synchronized (object){
            while (pointList.isEmpty()){
                Log.i(TAG,"绘制进入等待");
                try {
                    object.wait();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            mPaint.setStyle(Paint.Style.FILL);
            mPaint.setColor(Color.CYAN);

            for(LocalWavePoint myPoint :pointList){
                Log.i(TAG, "drawPoints: "+myPoint.getX());
                float x = convertX(myPoint.getX());
                float y = convertY(myPoint.getY(),myPoint.getPressUnit());
                boardCanvas.drawPoint(x,y,mPaint);
                lruCache.put(myPoint.toString(),myPoint);
            }
            Canvas canvas = surfaceHolder.lockCanvas();
            if(null != canvas){
                canvas.drawBitmap(bitmap,0,0,null);
                surfaceHolder.unlockCanvasAndPost(canvas);
                Log.i(TAG,"绘制成功");
            }


            pointList.clear();
            object.notifyAll();
        }


    }
    class DrawPointThread extends Thread{

        @Override
        public void run() {
            while (true){
                drawPoints();
            }
        }
    }
    class ReceiveThread extends Thread{
        private List<LocalWavePoint> uploadPointList;

        public ReceiveThread(List<LocalWavePoint> uploadPointList) {
            this.uploadPointList = uploadPointList;
        }

        @Override
        public void run() {
            try {
                sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            receive(uploadPointList);
        }
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

    public LruCache<String, LocalWavePoint> getLruCache() {
        return lruCache;
    }


}
