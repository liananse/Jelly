package com.medialab.jelly.util;

import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;

public class DragState {
	private Direction direction;
	public boolean isActuallyDragging;
	public boolean isDraggable = true;
	public boolean isPotentialDragStarted;
	private float lastX;
	private float lastY;
	private float lastYDiff;
	private Orientation orientation;
	private final float slop;
	private float startingYTranslation;
	private final VelocityTracker velocityTracker = VelocityTracker.obtain();
	public View viewDragging;
	public float x_start;
	public float y_start;

	public DragState(float paramFloat) {
		this.slop = paramFloat;
	}

	public void addMotionEventToDrag(MotionEvent paramMotionEvent) {
		this.lastX = paramMotionEvent.getX();
		float f = this.lastY;
		this.lastY = paramMotionEvent.getY();
		this.lastYDiff = (this.lastY - f);
		this.velocityTracker.addMovement(paramMotionEvent);
	}

	public void destroy() {
		this.velocityTracker.recycle();
	}

	public int getCurrentYTranslation(float paramFloat) {
		return (int) (paramFloat - this.y_start + this.startingYTranslation);
	}

	public float getLastX() {
		return this.lastX;
	}

	public float getLastYDiff() {
		return this.lastYDiff;
	}

	public double getXVelocityInPixelsPerSecond() {
		this.velocityTracker.computeCurrentVelocity(1000);
		return this.velocityTracker.getXVelocity();
	}

	public double getYVelocityInPixelsPerSecond() {
		this.velocityTracker.computeCurrentVelocity(1000);
		return this.velocityTracker.getYVelocity();
	}

	public boolean shouldStartDrag(float paramFloat1, float paramFloat2) {
		boolean i = true;
		if ((this.isDraggable) && (this.viewDragging != null)) {
			float f1 = paramFloat1 - this.x_start;
			float f2 = paramFloat2 - this.y_start;
			if (this.orientation != Orientation.ANY) {
				if (this.orientation != Orientation.HORIZONTAL) {
					if (this.orientation != Orientation.VERTICAL)
						throw new RuntimeException(
								"Couldn't figure out if we should start drag or not");
					if (this.direction != Direction.ANY) {
						if (this.direction != Direction.POSITIVE) {
							if (this.direction == Direction.NEGATIVE) {
								f2 *= -1.0F;
								if ((f2 > Math.abs(f1)) && (f2 > this.slop))
									return true;
							}
						} else if ((f2 > Math.abs(f1)) && (f2 > this.slop))
							return true;
					} else if ((Math.abs(f2) > Math.abs(f1))
							&& (Math.abs(f2) > this.slop))
						return true;
					i = false;
				} else {
					if (this.direction != Direction.ANY) {
						if (this.direction != Direction.POSITIVE) {
							if (this.direction == Direction.NEGATIVE) {
								f1 *= -1.0F;
								if ((f1 > Math.abs(f2)) && (f1 > this.slop))
									return true;
							}
						} else if ((f1 > Math.abs(f2)) && (f1 > this.slop))
							return true;
					} else if ((Math.abs(f1) > Math.abs(f2))
							&& (Math.abs(f1) > this.slop))
						return true;
					i = false;
				}
			} else if ((float) Math.sqrt(f1 * f1 + f2 * f2) <= this.slop)
				i = false;
		} else {
			i = false;
		}
		return i;
	}

	public void startActualDrag(float paramFloat1, float paramFloat2) {
		this.isActuallyDragging = true;
		this.lastX = paramFloat1;
		this.lastY = paramFloat2;
		this.y_start = paramFloat2;
		this.x_start = paramFloat1;
	}

	public void startPotentialDrag(View paramView,
			Orientation paramOrientation, Direction paramDirection,
			float paramFloat1, float paramFloat2, boolean paramBoolean) {
		this.orientation = paramOrientation;
		this.direction = paramDirection;
		this.viewDragging = paramView;
		this.isPotentialDragStarted = true;
		this.x_start = paramFloat1;
		this.y_start = paramFloat2;
		this.lastX = this.x_start;
		this.lastY = this.y_start;
		this.lastYDiff = 0.0F;
		this.isDraggable = paramBoolean;
		this.startingYTranslation = this.viewDragging.getTranslationY();
	}

	public void stopDrag() {
		this.velocityTracker.clear();
		this.isActuallyDragging = false;
		this.isPotentialDragStarted = false;
		this.viewDragging = null;
		this.x_start = -1.0F;
		this.y_start = -1.0F;
		this.isDraggable = false;
	}

	public static enum Direction {
		POSITIVE, NEGATIVE, ANY
	}

	public static enum Orientation {
		HORIZONTAL, VERTICAL, ANY
	}
}