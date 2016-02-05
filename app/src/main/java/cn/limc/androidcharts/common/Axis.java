/*
 * Axis.java
 * Android-Charts
 *
 * Created by limc on 2014.
 *
 * Copyright 2011 limc.cn All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */


package cn.limc.androidcharts.common;

import cn.limc.androidcharts.view.GridChart;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;

/** 
 * <p>en</p>
 * <p>jp</p>
 * <p>cn</p>
 *
 * @author limc 
 * @version v1.0 2014/06/24 18:59:50 
 *  
 */
public abstract class Axis extends Quadrant implements IAxis {
	public static final int DEFAULT_LINE_COLOR = Color.RED;
	public static final float DEFAULT_LINE_WIDTH = 1f;
	public static final int DEFAULT_POSITION = AXIS_X_POSITION_BOTTOM;
	protected int lineColor = DEFAULT_LINE_COLOR;
	protected float lineWidth = DEFAULT_LINE_WIDTH;
	protected int position = DEFAULT_POSITION;
	
	public Axis(GridChart inChart ,int position){
		super(inChart);
		this.paddingTop = 0;
		this.paddingLeft = 0;
		this.paddingBottom = 0;
		this.paddingRight = 0;
		
		this.position = position;
	}
	
	/* (non-Javadoc)
	 * 
	 * @param canvas 
	 * @see cn.limc.IAxis#drawAxis(android.graphics.Canvas)
	 */
	public void drawAxis(Canvas canvas) {
		float postX,postY;
		Paint mPaint = new Paint();
		mPaint.setColor(lineColor);
		mPaint.setStrokeWidth(lineWidth);
		if (position == AXIS_X_POSITION_BOTTOM) {
			postY = lineWidth / 2;
			postX = getQuadrantStartX();
			canvas.drawLine(postX,postY, postX + getQuadrantWidth(),postY , mPaint);
		} else if (position == AXIS_X_POSITION_TOP) {
			postY = getQuadrantHeight() - lineWidth / 2;
			postX = getQuadrantStartX();
			canvas.drawLine(postX,postY, postX + getQuadrantWidth(),postY , mPaint);
		} else if (position == AXIS_Y_POSITION_LEFT) {
			postX = getQuadrantWidth() - lineWidth / 2;
			postY = getQuadrantStartX();
			canvas.drawLine(postX,postY, postX,postY + getQuadrantHeight() , mPaint);
		} else {
			postX = lineWidth / 2;
			postY = getQuadrantStartY();
			canvas.drawLine(postX,postY, postX,postY + getQuadrantHeight() , mPaint);
		}
	}

	/**
	 * @return the lineColor
	 */
	public int getLineColor() {
		return lineColor;
	}

	/**
	 * @param lineColor the lineColor to set
	 */
	public void setLineColor(int lineColor) {
		this.lineColor = lineColor;
	}

	/**
	 * @return the lineWidth
	 */
	public float getLineWidth() {
		return lineWidth;
	}

	/**
	 * @param lineWidth the lineWidth to set
	 */
	public void setLineWidth(float lineWidth) {
		this.lineWidth = lineWidth;
	}

	/**
	 * @return the position
	 */
	public int getPosition() {
		return position;
	}

	/**
	 * @param position the position to set
	 */
	public void setPosition(int position) {
		this.position = position;
	}

	/* (non-Javadoc)
	 * 
	 * @return 
	 * @see cn.limc.IQuadrant#getQuadrantStartX()
	 */
	public float getQuadrantStartX() {
		if (position == AXIS_X_POSITION_BOTTOM) {
			return 2*inChart.getBorderWidth();
		} else if (position == AXIS_X_POSITION_TOP) {
			return 2*inChart.getBorderWidth();
		} else if (position == AXIS_Y_POSITION_LEFT) {
			return 2*inChart.getBorderWidth();
		} else {
			return inChart.getDataQuadrant().getQuadrantEndX();
		}
	}

	/* (non-Javadoc)
	 * 
	 * @return 
	 * @see cn.limc.IQuadrant#getQuadrantStartY()
	 */
	public float getQuadrantStartY() {
		if (position == AXIS_X_POSITION_BOTTOM) {
			return inChart.getDataQuadrant().getQuadrantEndY();
		} else if (position == AXIS_X_POSITION_TOP) {
			return 2*inChart.getBorderWidth();
		} else if (position == AXIS_Y_POSITION_LEFT) {
			return 2*inChart.getBorderWidth();
		} else {
			return 2*inChart.getBorderWidth();
		}
	}
}
