package com.medialab.jelly.ui.drawing;

public class TimePoint {
	public long time;
	public float x;
	public float y;

	public TimePoint() {
	}

	public TimePoint(float paramFloat1, float paramFloat2, long paramLong) {
		this.x = paramFloat1;
		this.y = paramFloat2;
		this.time = paramLong;
	}

	public TimePoint(TimePoint paramTimePoint) {
		this.x = paramTimePoint.x;
		this.y = paramTimePoint.y;
		this.time = paramTimePoint.time;
	}

	public final void set(float paramFloat1, float paramFloat2, long paramLong) {
		this.x = paramFloat1;
		this.y = paramFloat2;
		this.time = paramLong;
	}

	public final void set(TimePoint paramTimePoint) {
		this.x = paramTimePoint.x;
		this.y = paramTimePoint.y;
		this.time = paramTimePoint.time;
	}
}
