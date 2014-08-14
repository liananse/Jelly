package com.medialab.jelly.ui.drawing;

import android.graphics.PointF;

public class LineSegment {
	public final PointF endPoint;
	public final PointF startPoint;

	public LineSegment() {
		this.startPoint = new PointF();
		this.endPoint = new PointF();
	}

	public LineSegment(PointF paramPointF1, PointF paramPointF2) {
		this.startPoint = paramPointF1;
		this.endPoint = paramPointF2;
	}

	public LineSegment(LineSegment paramLineSegment) {
		this.startPoint = new PointF();
		this.endPoint = new PointF();
		changePoints(paramLineSegment);
	}

	public void changePoints(float paramFloat1, float paramFloat2,
			float paramFloat3, float paramFloat4) {
		this.startPoint.set(paramFloat1, paramFloat2);
		this.endPoint.set(paramFloat3, paramFloat4);
	}

	public void changePoints(PointF paramPointF1, PointF paramPointF2) {
		this.startPoint.set(paramPointF1);
		this.endPoint.set(paramPointF2);
	}

	public void changePoints(LineSegment paramLineSegment) {
		this.startPoint.set(paramLineSegment.startPoint);
		this.endPoint.set(paramLineSegment.endPoint);
	}

	public void changePoints(TimePoint paramTimePoint1,
			TimePoint paramTimePoint2) {
		this.startPoint.set(paramTimePoint1.x, paramTimePoint1.y);
		this.endPoint.set(paramTimePoint2.x, paramTimePoint2.y);
	}

	public String toString() {
		Object[] arrayOfObject = new Object[2];
		arrayOfObject[0] = this.startPoint.toString();
		arrayOfObject[1] = this.endPoint.toString();
		return String.format("Start: %s, End: %s", arrayOfObject);
	}
}
