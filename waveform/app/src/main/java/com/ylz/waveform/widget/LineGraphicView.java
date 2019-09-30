package com.ylz.waveform.widget;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.Path;
import android.graphics.Point;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.WindowManager;
import com.ylz.waveform.R;
import java.util.ArrayList;

public class LineGraphicView extends View
{
	/**
	 * 公共部分
	 */
	private static final int CIRCLE_SIZE = 10;

	private static enum Linestyle
	{
		Line, Curve
	}

	private Context mContext;
	private Paint mPaint;
	private Resources res;
	private DisplayMetrics dm;

	/**
	 * data
	 */
	private Linestyle mStyle = Linestyle.Curve;

	private int canvasHeight;
	private int canvasWidth;
	private int bheight = 0;
	private int blwidh;
	private boolean isMeasure = true;
	/**
	 * Y轴最大值
	 */
	private int maxValue;
	/**
	 * Y轴间距值
	 */
	private int averageValue;
	private int marginTop = 40;
	private int marginBottom = 100;

	/**
	 * 曲线上总点数
	 */
	private Point[] mPoints;
	private Point[] m1Points;
	private Point[] m2Points;
	private Point[] m3Points;
	/**
	 * 纵坐标值
	 */
	private ArrayList<Double> yRawData;
	private ArrayList<Double> y1RawData;
	private ArrayList<Double> y2RawData;
	private ArrayList<Double> y3RawData;
	/**
	 * 横坐标值
	 */
	private ArrayList<String> xRawDatas;
	private ArrayList<Integer> xList = new ArrayList<Integer>();// 记录每个x的值
	private int spacingHeight;

	public LineGraphicView(Context context)
	{
		this(context, null);
	}

	public LineGraphicView(Context context, AttributeSet attrs)
	{
		super(context, attrs);
		this.mContext = context;
		initView();
	}

	private void initView()
	{
		this.res = mContext.getResources();
		this.mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
		dm = new DisplayMetrics();
		WindowManager wm = (WindowManager) mContext.getSystemService(Context.WINDOW_SERVICE);
		wm.getDefaultDisplay().getMetrics(dm);
	}

	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh)
	{
		if (isMeasure)
		{
			this.canvasHeight = getHeight();
			this.canvasWidth = getWidth();
			if (bheight == 0)
				bheight = (int) (canvasHeight - marginBottom);
			blwidh = dip2px(30);
			isMeasure = false;
		}
	}


	@Override
	protected void onDraw(Canvas canvas)
	{
		mPaint.setColor(res.getColor(R.color.color));

		drawAllXLine(canvas);
		// 画直线（纵向）
		drawAllYLine(canvas);
		// 点的操作设置
		mPoints = getPoints();
		m1Points = getPoints1();
		m2Points = getPoints2();
		m3Points = getPoints3();

		mPaint.setColor(res.getColor(R.color.color));
		mPaint.setStrokeWidth(dip2px(1f));
		mPaint.setStyle(Style.STROKE);
		mPaint.setAlpha(128);
		if (mStyle == Linestyle.Curve)
		{
			drawScrollLine(canvas);
		}
		else
		{
			drawLine(canvas);
		}

		mPaint.setStyle(Style.STROKE);
		for (int i = 0; i < mPoints.length; i++)
		{
			canvas.drawCircle(mPoints[i].x, mPoints[i].y, CIRCLE_SIZE / 2, mPaint);
		}
	}

	/**
	 *  画所有横向表格，包括X轴
	 */
	private void drawAllXLine(Canvas canvas)
	{
		for (int i = 0; i < spacingHeight + 1; i++)
		{
			canvas.drawLine(blwidh, bheight - (bheight / spacingHeight) * i + marginTop, (canvasWidth - blwidh),
					bheight - (bheight / spacingHeight) * i + marginTop, mPaint);// Y坐标
			drawText(String.valueOf(averageValue * i), blwidh / 2, bheight - (bheight / spacingHeight) * i + marginTop,
					canvas);
		}
	}

	/**
	 * 画所有纵向表格，包括Y轴
	 */
	private void drawAllYLine(Canvas canvas)
	{
		for (int i = 0; i < yRawData.size(); i++)
		{
			xList.add(blwidh + (canvasWidth - blwidh) / yRawData.size() * i);
			canvas.drawLine(blwidh + (canvasWidth - blwidh) / yRawData.size() * i, marginTop, blwidh
					+ (canvasWidth - blwidh) / yRawData.size() * i, bheight + marginTop, mPaint);
			drawText(xRawDatas.get(i), blwidh + (canvasWidth - blwidh) / yRawData.size() * i, bheight + dip2px(26),
					canvas);// X坐标
		}
	}

	private void drawScrollLine(Canvas canvas)
	{
		drawScrollLine1(canvas);
		drawScrollLine2(canvas);
		drawScrollLine3(canvas);
		drawScrollLine4(canvas);
	}
	private void drawScrollLine1(Canvas canvas)
	{
		Point startp = new Point();
		Point endp = new Point();
		for (int i = 0; i < mPoints.length - 1; i++)
		{
			startp = mPoints[i];
			endp = mPoints[i + 1];
			int wt = (startp.x + endp.x) / 2;
			Point p3 = new Point();
			Point p4 = new Point();
			p3.y = startp.y;
			p3.x = wt;
			p4.y = endp.y;
			p4.x = wt;

			Path path = new Path();
			path.moveTo(startp.x, startp.y);
			path.cubicTo(p3.x, p3.y, p4.x, p4.y, endp.x, endp.y);
			canvas.drawPath(path, mPaint);
		}
	}
	private void drawScrollLine2(Canvas canvas)
	{
		Point startp = new Point();
		Point endp = new Point();

		for (int i = 0; i < m1Points.length - 1; i++)
		{
			startp = m1Points[i];
			endp = m1Points[i + 1];
			int wt = (startp.x + endp.x) / 2;
			Point p3 = new Point();
			Point p4 = new Point();
			p3.y = startp.y;
			p3.x = wt;
			p4.y = endp.y;
			p4.x = wt;

			Path path = new Path();
			path.moveTo(startp.x, startp.y);
			path.cubicTo(p3.x, p3.y, p4.x, p4.y, endp.x, endp.y);
			mPaint.setColor(res.getColor(R.color.red));
			mPaint.setAlpha(180);
			canvas.drawPath(path, mPaint);
		}
	}
	private void drawScrollLine3(Canvas canvas)
	{
		Point startp = new Point();
		Point endp = new Point();

		for (int i = 0; i < m2Points.length - 1; i++)
		{
			startp = m2Points[i];
			endp = m2Points[i + 1];
			int wt = (startp.x + endp.x) / 2;
			Point p3 = new Point();
			Point p4 = new Point();
			p3.y = startp.y;
			p3.x = wt;
			p4.y = endp.y;
			p4.x = wt;

			Path path = new Path();
			path.moveTo(startp.x, startp.y);
			path.cubicTo(p3.x, p3.y, p4.x, p4.y, endp.x, endp.y);
			mPaint.setColor(res.getColor(R.color.blue));
			mPaint.setAlpha(180);
			canvas.drawPath(path, mPaint);
		}
	}

	private void drawScrollLine4(Canvas canvas)
	{
		Point startp = new Point();
		Point endp = new Point();
		for (int i = 0; i < m3Points.length - 1; i++)
		{
			startp = m3Points[i];
			endp = m3Points[i + 1];
			int wt = (startp.x + endp.x) / 2;
			Point p3 = new Point();
			Point p4 = new Point();
			p3.y = startp.y;
			p3.x = wt;
			p4.y = endp.y;
			p4.x = wt;

			Path path = new Path();
			path.moveTo(startp.x, startp.y);
			path.cubicTo(p3.x, p3.y, p4.x, p4.y, endp.x, endp.y);
			mPaint.setColor(res.getColor(R.color.green));
			mPaint.setAlpha(180);
			canvas.drawPath(path, mPaint);
		}
	}

	private void drawLine(Canvas canvas)
	{
		Point startp = new Point();
		Point endp = new Point();
		for (int i = 0; i < mPoints.length - 1; i++)
		{
			startp = mPoints[i];
			endp = mPoints[i + 1];
			canvas.drawLine(startp.x, startp.y, endp.x, endp.y, mPaint);
		}
	}

	private void drawText(String text, int x, int y, Canvas canvas)
	{
		Paint p = new Paint(Paint.ANTI_ALIAS_FLAG);
		p.setTextSize(dip2px(12));
		p.setColor(res.getColor(R.color.black));
		p.setTextAlign(Paint.Align.LEFT);
		canvas.drawText(text, x, y, p);
	}

	private Point[] getPoints()
	{
		Point[] points = new Point[yRawData.size()];
		for (int i = 0; i < yRawData.size(); i++)
		{
			int ph = bheight - (int) (bheight * (yRawData.get(i) / maxValue));

			points[i] = new Point(xList.get(i), ph + marginTop);
		}
		return points;
	}

	private Point[] getPoints1()
	{
		Point[] points = new Point[y1RawData.size()];
		for (int i = 0; i < y1RawData.size(); i++)
		{
			int ph = bheight - (int) (bheight * (y1RawData.get(i) / maxValue));

			points[i] = new Point(xList.get(i), ph + marginTop);
		}
		return points;
	}

	private Point[] getPoints2()
	{
		Point[] points = new Point[y2RawData.size()];
		for (int i = 0; i < y2RawData.size(); i++)
		{
			int ph = bheight - (int) (bheight * (y2RawData.get(i) / maxValue));

			points[i] = new Point(xList.get(i), ph + marginTop);
		}
		return points;
	}

	private Point[] getPoints3()
	{
		Point[] points = new Point[y3RawData.size()];
		for (int i = 0; i < y3RawData.size(); i++)
		{
			int ph = bheight - (int) (bheight * (y3RawData.get(i) / maxValue));

			points[i] = new Point(xList.get(i), ph + marginTop);
		}
		return points;
	}

	public void setData(ArrayList<Double> yRawData,ArrayList<Double> y1RawData,ArrayList<Double> y2RawData,ArrayList<Double> y3RawData,
						ArrayList<String> xRawData, int maxValue, int averageValue)
	{
		this.maxValue = maxValue;
		this.averageValue = averageValue;
		this.mPoints = new Point[yRawData.size()];
		this.xRawDatas = xRawData;
		this.yRawData = yRawData;
		this.y1RawData = y1RawData;
		this.y2RawData = y2RawData;
		this.y3RawData = y3RawData;
		this.spacingHeight = maxValue / averageValue;
	}

	public void setTotalvalue(int maxValue)
	{
		this.maxValue = maxValue;
	}

	public void setPjvalue(int averageValue)
	{
		this.averageValue = averageValue;
	}

	public void setMargint(int marginTop)
	{
		this.marginTop = marginTop;
	}

	public void setMarginb(int marginBottom)
	{
		this.marginBottom = marginBottom;
	}

	public void setMstyle(Linestyle mStyle)
	{
		this.mStyle = mStyle;
	}

	public void setBheight(int bheight)
	{
		this.bheight = bheight;
	}

	/**
	 * 根据手机的分辨率从 dp 的单位 转成为 px(像素)
	 */
	private int dip2px(float dpValue)
	{
		return (int) (dpValue * dm.density + 0.5f);
	}

}
