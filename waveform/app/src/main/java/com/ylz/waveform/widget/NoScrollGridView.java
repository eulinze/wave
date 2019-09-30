package com.ylz.waveform.widget;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.widget.GridView;

public class NoScrollGridView extends GridView {
	/**
	 * @param context
	 *            自定义无滚动条且会绘制网格线的gridview
	 */

	public NoScrollGridView(Context context) {
		super(context);

	}

	public NoScrollGridView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public NoScrollGridView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		// TODO Auto-generated constructor stub
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		int expandSpec = MeasureSpec.makeMeasureSpec(Integer.MAX_VALUE >> 2,
				MeasureSpec.AT_MOST);
		super.onMeasure(widthMeasureSpec, expandSpec);
	}

	@SuppressLint("NewApi")
	@Override
	protected void dispatchDraw(Canvas canvas) {
		// TODO Auto-generated method stub
		super.dispatchDraw(canvas);
		if (this.getNumColumns() == 2) {
			int oneW = this.getWidth() / this.getNumColumns();
			Paint paint = new Paint();
			paint.setColor(Color.parseColor("#eeeeee"));
			paint.setStyle(Paint.Style.STROKE);
			for (int i = 1, j = getNumColumns(); i < j; i++) {
				canvas.drawLine(oneW * i, 0, oneW * i, this.getHeight(),
						paint);
			}
			int y;
			if (getChildCount() % getNumColumns() == 0) {
				y = getChildCount() / getNumColumns();
			} else {
				y = getChildCount() / getNumColumns() + 1;
			}
			if (y == 0) {
				return;
			}
			int oneH = this.getHeight() / y;
			for (int i = 1; i < y; i++) {
				canvas.drawLine(0, oneH * i, this.getWidth(), oneH * i,
						paint);
			}
		}

	}

}
