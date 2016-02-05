/*
 * PeriodDataGridChart.java
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


package cn.limc.androidcharts.view;

import java.util.ArrayList;
import java.util.List;

import cn.limc.androidcharts.entity.IMeasurable;

import android.content.Context;
import android.graphics.PointF;
import android.util.AttributeSet;
import android.util.FloatMath;
import android.view.MotionEvent;

/** 
 * <p>en</p>
 * <p>jp</p>
 * <p>cn</p>
 *
 * @author limc 
 * @version v1.0 2014/06/19 14:44:11 
 *  
 */
public abstract class PeriodDataGridChart extends DataGridChart {
	

	public static final int DEFAULT_ALIGN_TYPE = ALIGN_TYPE_CENTER;
	public static final int DEFAULT_BIND_CROSS_LINES_TO_STICK = BIND_TO_TYPE_BOTH;
	
	protected int gridAlignType = DEFAULT_ALIGN_TYPE;
	protected int bindCrossLinesToStick = DEFAULT_BIND_CROSS_LINES_TO_STICK;

	/** 
	 * <p>Constructor of PeriodDataGridChart</p>
	 * <p>PeriodDataGridChart类对象的构造函数</p>
	 * <p>PeriodDataGridChartのコンストラクター</p>
	 *
	 * @param context 
	 */
	public PeriodDataGridChart(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
	}

	/** 
	 * <p>Constructor of PeriodDataGridChart</p>
	 * <p>PeriodDataGridChart类对象的构造函数</p>
	 * <p>PeriodDataGridChartのコンストラクター</p>
	 *
	 * @param context
	 * @param attrs
	 * @param defStyle 
	 */
	public PeriodDataGridChart(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		// TODO Auto-generated constructor stub
	}

	/** 
	 * <p>Constructor of PeriodDataGridChart</p>
	 * <p>PeriodDataGridChart类对象的构造函数</p>
	 * <p>PeriodDataGridChartのコンストラクター</p>
	 *
	 * @param context
	 * @param attrs 
	 */
	public PeriodDataGridChart(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
	}
	
	/**
	 * <p>
	 * initialize degrees on X axis
	 * </p>
	 * <p>
	 * X軸の目盛を初期化
	 * </p>
	 * <p>
	 * 初始化X轴的坐标值
	 * </p>
	 */
	protected void initAxisX() {
		List<String> titleX = new ArrayList<String>();
		if (null != stickData && stickData.size() > 0) {
			float average = getDisplayNumber() / this.getLongitudeNum();
			for (int i = 0; i < this.getLongitudeNum(); i++) {
				int index = (int) Math.floor(i * average);
				if (index > getDisplayNumber() - 1) {
					index = getDisplayNumber() - 1;
				}
				titleX.add(formatAxisXDegree(stickData.get(index).getDate()));
			}
			titleX.add(formatAxisXDegree(stickData.get(getDisplayNumber() - 1).getDate()));
		}
		super.setLongitudeTitles(titleX);
	}

	/**
	 * <p>
	 * initialize degrees on Y axis
	 * </p>
	 * <p>
	 * Y軸の目盛を初期化
	 * </p>
	 * <p>
	 * 初始化Y轴的坐标值
	 * </p>
	 */
	protected void initAxisY() {
		this.calcValueRange();
		List<String> titleY = new ArrayList<String>();
		double average = (maxValue - minValue) / this.getLatitudeNum();
		;
		// calculate degrees on Y axis
		for (int i = 0; i < this.getLatitudeNum(); i++) {
			String value = formatAxisYDegree(minValue + i * average);
			if (value.length() < super.getLatitudeMaxTitleLength()) {
				while (value.length() < super.getLatitudeMaxTitleLength()) {
					value = " " + value;
				}
			}
			titleY.add(value);
		}
		// calculate last degrees by use max value
		String value = formatAxisYDegree(maxValue);
		if (value.length() < super.getLatitudeMaxTitleLength()) {
			while (value.length() < super.getLatitudeMaxTitleLength()) {
				value = " " + value;
			}
		}
		titleY.add(value);

		super.setLatitudeTitles(titleY);
	}
	

	
	public float longitudePostOffset(){
		if (gridAlignType == ALIGN_TYPE_CENTER) {
			float stickWidth = dataQuadrant.getQuadrantPaddingWidth() / getDisplayNumber();
			return (this.dataQuadrant.getQuadrantPaddingWidth() - stickWidth)/ (longitudeTitles.size() - 1);
	    }else{
			return this.dataQuadrant.getQuadrantPaddingWidth()/ (longitudeTitles.size() - 1);
	    }
	}
	
	public float longitudeOffset(){
		if (gridAlignType == ALIGN_TYPE_CENTER) {
			float stickWidth = dataQuadrant.getQuadrantPaddingWidth() / getDisplayNumber();
			return dataQuadrant.getQuadrantPaddingStartX() + stickWidth / 2;
		}else{
			return dataQuadrant.getQuadrantPaddingStartX();
		}
	}
	
	
	protected PointF calcTouchedPoint(float x ,float y) {
		if (!isValidTouchPoint(x,y)) {
			return new PointF(0,0);
		}
		if (bindCrossLinesToStick == BIND_TO_TYPE_NONE) {
			return new PointF(x, y);
		} else if (bindCrossLinesToStick == BIND_TO_TYPE_BOTH) {
			PointF bindPointF = calcBindPoint(x, y);
			return bindPointF;
		} else if (bindCrossLinesToStick == BIND_TO_TYPE_HIRIZIONAL) {
			PointF bindPointF = calcBindPoint(x, y);
			return new PointF(bindPointF.x, y);
		} else if (bindCrossLinesToStick == BIND_TO_TYPE_VERTICAL) {
			PointF bindPointF = calcBindPoint(x, y);
			return new PointF(x, bindPointF.y);
		} else {
			return new PointF(x, y);
		}	
	}
	
	protected PointF calcBindPoint(float x ,float y) {
		float calcX = 0;
		float calcY = 0;
		
		int index = calcSelectedIndex(x,y);
		
		float stickWidth = dataQuadrant.getQuadrantPaddingWidth() / getDisplayNumber();
		IMeasurable stick = stickData.get(index);
		calcY = (float) ((1f - (stick.getHigh() - minValue)
				/ (maxValue - minValue))
				* (dataQuadrant.getQuadrantPaddingHeight()) + dataQuadrant.getQuadrantPaddingStartY());
		calcX = dataQuadrant.getQuadrantPaddingStartX() + stickWidth * (index - getDisplayFrom()) + stickWidth / 2;
		
		return new PointF(calcX,calcY);
	}
	/**
	 * <p>
	 * calculate the distance between two touch points
	 * </p>
	 * <p>
	 * 複数タッチしたポイントの距離
	 * </p>
	 * <p>
	 * 计算两点触控时两点之间的距离
	 * </p>
	 * 
	 * @param event
	 * @return float
	 *         <p>
	 *         distance
	 *         </p>
	 *         <p>
	 *         距離
	 *         </p>
	 *         <p>
	 *         距离
	 *         </p>
	 */
	protected float calcDistance(MotionEvent event) {
		if(event.getPointerCount() <= 1) {
			return 0f;
		}else{
			float x = event.getX(0) - event.getX(1);
			float y = event.getY(0) - event.getY(1);
			return (float)Math.sqrt(x * x + y * y);
		}
	}
	
	/**
	 * @return the gridAlignType
	 */
	public int getStickAlignType() {
		return gridAlignType;
	}

	/**
	 * @param gridAlignType the gridAlignType to set
	 */
	public void setStickAlignType(int stickAlignType) {
		this.gridAlignType = stickAlignType;
	}
}
