package com.medialab.jelly.controller;

import java.util.Map;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;

import com.facebook.rebound.SimpleSpringListener;
import com.facebook.rebound.Spring;
import com.facebook.rebound.SpringListener;
import com.facebook.rebound.SpringSystem;
import com.facebook.rebound.SpringUtil;
import com.google.common.collect.Maps;
import com.medialab.jelly.JellyApplication;
import com.medialab.jelly.ui.event.SpinnerHideEvent;
import com.medialab.jelly.ui.event.SpinnerShowEvent;
import com.medialab.jelly.util.DragState;
import com.squareup.otto.Bus;

public class SimpleStackController extends ViewGroup implements ViewController {

	private static final String TAG = SimpleStackController.class.getSimpleName();
	private DismissableViewController baseController;
	private DismissableViewController overlayController;
	private DismissableViewController pendingController;

	private boolean hasBaseController = false;
	private boolean hasShownSpinner = false;
	private final Map<DismissableViewController, InAndOutAnimator> registry = Maps
			.newHashMap();

	private Context context;
	private float heightToDismissAt;
	private final SpringSystem springSystem;
	private final int topNavHeight;
	private final int topNavIconMiddle;
	private float finalDragAngle;

	private final DragState dragState;
	private Spring dragSpring;
	private final Bus bus;

	public SimpleStackController(Context paramContext, StarfishScreenUtils paramStarfishScreenUtils) {
		super(paramContext);
		bus = JellyApplication.getBus();
		this.context = paramContext;
		// TODO Auto-generated constructor stub
		this.springSystem = SpringSystem.create();
		this.topNavIconMiddle = 0;
		this.topNavHeight = paramStarfishScreenUtils.getTopNavHeight();
		this.dragState = new DragState(ViewConfiguration.get(context)
				.getScaledTouchSlop());
		dragSpring = springSystem.createSpring().setCurrentValue(0.0f)
				.setAtRest();
	}

	private boolean showingSpinner = false;
	private boolean somethingAnimatingOut = false;

	private void removeSpinner() {
		Log.d(TAG, "removeSpinner method");
		this.bus.post(new SpinnerHideEvent());
		this.showingSpinner = false;
	}

	private void showSpinner() {
		Log.d(TAG, "showSpinner method");
		this.showingSpinner = true;
		this.hasShownSpinner = true;
		this.bus.post(new SpinnerShowEvent());
	}

	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	public void addBaseController(
			DismissableViewController paramDismissableViewController) {

		if (this.showingSpinner)
			removeSpinner();
		if (!somethingAnimatingOut) {
			if (paramDismissableViewController.getView().getParent() == this) {
				this.baseController = paramDismissableViewController;
				animateBaseControllerIn(this.baseController);
			} else {
				if ((this.baseController != null)
						&& (this.baseController.getView().getParent() == this))
					removeView(this.baseController.getView());
				paramDismissableViewController.getView().setLayerType(2, null);
//				 need ?????????View
//				 if (getChildCount() > 0) {
				addView(paramDismissableViewController.getView());
//				 }
				this.baseController = paramDismissableViewController;
				animateBaseControllerIn(this.baseController);
			}
		} else {
			this.pendingController = paramDismissableViewController;
		}
		this.hasBaseController = true;
		if (this.overlayController != null)
			bringChildToFront(this.overlayController.getView());
	}

	public void dismissCurrentBaseView() {
		if ((!hasCurrentBaseController()) || (this.baseController == null)) {
		} else {
			setFinalDragAngle((int) (Math.random() * getMeasuredWidth()));
			InAndOutAnimator localInAndOutAnimator = getAnimatorForController(this.baseController);
			if (localInAndOutAnimator == null)
				localInAndOutAnimator = new InAndOutAnimator(
						this.baseController);
			this.baseController = null;
			if (this.overlayController != null)
				localInAndOutAnimator.bringMovingViewOut(false,
						AnimationType.SLIDE, DismissalType.REMOVE, 0.0D, 0.0D);
			else
				localInAndOutAnimator.bringViewOut(AnimationType.SLIDE,
						DismissalType.REMOVE);
			this.hasBaseController = false;
			this.dragSpring.removeAllListeners();
		}
	}

	public void dismissCurrentOverlay(AnimationType paramAnimationType) {
		if (this.overlayController != null) {
			getAnimatorForController(this.overlayController).bringViewOut(
					paramAnimationType, DismissalType.REMOVE);
			this.overlayController = null;
			this.dragSpring.removeAllListeners();
			if (this.baseController != null)
				animateBaseControllerIn(this.baseController);
			return;
		}
		throw new RuntimeException("Can't dismiss null overlay");
	}

	public DismissableViewController getCurrentBaseController() {
		DismissableViewController localDismissableViewController;
		if (this.baseController != null)
			localDismissableViewController = this.baseController;
		else
			localDismissableViewController = this.pendingController;
		return localDismissableViewController;
	}

	public DismissableViewController getCurrentOverlayController() {
		return this.overlayController;
	}

	public boolean hasCurrentBaseController() {
		boolean bool;
		if (((this.baseController == null) || (!this.hasBaseController))
				&& (this.pendingController == null))
			bool = false;
		else
			bool = true;
		return bool;
	}
	
	public boolean hasCurrentOverlayController() {
		boolean bool;
		if (this.overlayController == null)
			bool = false;
		else
			bool = true;
		return bool;
	}

	private void animateBaseControllerIn(
			DismissableViewController paramDismissableViewController) {

		paramDismissableViewController.getView().setScaleX(0.0F);
		paramDismissableViewController.getView().setScaleY(0.0F);
		if (this.overlayController == null) {
			InAndOutAnimator localInAndOutAnimator = getAnimatorForController(paramDismissableViewController);
			if (localInAndOutAnimator == null)
				localInAndOutAnimator = new InAndOutAnimator(
						paramDismissableViewController);
			localInAndOutAnimator.bringViewIn(AnimationType.SCALE);

			paramDismissableViewController.onShow(this);
		}
	}

	private InAndOutAnimator getAnimatorForController(
			DismissableViewController paramDismissableViewController) {
		return (InAndOutAnimator) this.registry
				.get(paramDismissableViewController);
	}

	@Override
	protected void onLayout(boolean changed, int l, int t, int r, int b) {
		// TODO Auto-generated method stub
		int j = r - l;
		int i = b - t;
		if (this.baseController != null)
			this.baseController.getView().layout(0, 0, j, i);
		if (this.overlayController != null)
			this.overlayController.getView().layout(0, 0, j, i);
	}

	@Override
	protected void onMeasure(int paramInt1, int paramInt2) {
		// TODO Auto-generated method stub
		this.heightToDismissAt = (1.1F * View.MeasureSpec.getSize(paramInt2));
		int i = View.MeasureSpec.makeMeasureSpec(
				View.MeasureSpec.getSize(paramInt2), MeasureSpec.AT_MOST);
		if (this.baseController != null)
			this.baseController.getView().measure(paramInt1, i);
		if (this.overlayController != null) {
			this.overlayController.getView().measure(paramInt1, i);
		}
		super.onMeasure(paramInt1, paramInt2);
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		// TODO Auto-generated method stub
		Log.d(TAG, "on touch event");
		int action = event.getAction();
		switch (action) {
		case MotionEvent.ACTION_MOVE:
			this.dragState.addMotionEventToDrag(event);

			this.dragSpring.setCurrentValue(this.dragState
					.getCurrentYTranslation(event.getY())
					/ this.heightToDismissAt);
			break;
		case MotionEvent.ACTION_UP:
			Log.d(TAG, "action up");
		case MotionEvent.ACTION_CANCEL:
			if (this.dragState.viewDragging != null) {
				handleFinishedDrag((int) event.getX(), (int) event.getY());
			}
			Log.d(TAG, "action cancel");
			this.dragState.stopDrag();
			break;
		default:
			break;
		}

		return false;
	}

	public void removeController(
			DismissableViewController paramDismissableViewController) {
		removeView(paramDismissableViewController.getView());
	}

	@Override
	public boolean onInterceptTouchEvent(MotionEvent paramMotionEvent) {
		// TODO Auto-generated method stub
		boolean result = false;
		int action = paramMotionEvent.getAction();
		if (paramMotionEvent.getY() >= this.topNavHeight) {
			switch (action) {
			case MotionEvent.ACTION_DOWN:
				Log.d(TAG, "OnIntercept action down "
						+ paramMotionEvent.getPointerCount());
				if (paramMotionEvent.getPointerCount() == 1) {
					DismissableViewController localDismissableViewController = getControllerToReceiveTouches();
					if (localDismissableViewController == null)
						break;
					this.dragState.startPotentialDrag(
							localDismissableViewController.getView(),
							DragState.Orientation.VERTICAL,
							DragState.Direction.POSITIVE,
							paramMotionEvent.getX(), paramMotionEvent.getY(),
							localDismissableViewController.canBeDismissed());
					this.dragSpring.removeAllListeners();

					if (this.overlayController != localDismissableViewController)
						this.dragSpring
								.addListener(getDraggingBaseViewListener(this.baseController));
					else
						this.dragSpring.addListener(getDraggingOverlayListener(
								this.overlayController, this.baseController));
					setFinalDragAngle(paramMotionEvent.getX());
				}
				break;
			case MotionEvent.ACTION_MOVE:
				Log.d(TAG, "onIntercept action move "
						+ paramMotionEvent.getPointerCount());
				if (paramMotionEvent.getPointerCount() == 1) {
					Log.d(TAG, "onIntercept action move "
							+ paramMotionEvent.getX() + " "
							+ paramMotionEvent.getY());
					result = false;

					if (this.dragState.shouldStartDrag(paramMotionEvent.getX(),
							paramMotionEvent.getY())) {
						result = true;
					} else
						result = false;
				} else {
					result = false;
				}
				break;
			case MotionEvent.ACTION_UP:
				Log.d(TAG, "onIntercept action up "
						+ paramMotionEvent.getPointerCount());
				Log.d(TAG, "result up " + result);
				this.dragState.stopDrag();
				this.dragSpring.removeAllListeners();
				break;
			default:
				this.dragState.stopDrag();
				this.dragSpring.removeAllListeners();
				break;
			}
			return result;
		} else {
			return false;
		}
	}

//	private SpringListener getDraggingBaseViewListener(
//			final DismissableViewController paramDismissableViewController) {
//		return new SpringListener() {
//
//			@Override
//			public void onSpringUpdate(Spring paramSpring) {
//				// TODO Auto-generated method stub
//				float f = (float) SpringUtil.mapValueFromRangeToRange(
//						paramSpring.getCurrentValue(), 0.0D, 1.0D, 0.0D,
//						SimpleStackController.this.heightToDismissAt);
//				paramDismissableViewController.getView().setTranslationY(f);
//				paramDismissableViewController
//						.getView()
//						.setRotation(
//								SimpleStackController.this
//										.getRotationFromYTranslation(paramDismissableViewController
//												.getView()));
//			}
//
//			@Override
//			public void onSpringEndStateChange(Spring arg0) {
//				// TODO Auto-generated method stub
//
//			}
//
//			@Override
//			public void onSpringAtRest(Spring arg0) {
//				// TODO Auto-generated method stub
//
//			}
//
//			@Override
//			public void onSpringActivate(Spring arg0) {
//				// TODO Auto-generated method stub
//
//			}
//		};
//	}
	
	private SpringListener getDraggingBaseViewListener(
			DismissableViewController paramDismissableViewController) {
		return new StartDropDragListener(paramDismissableViewController) {

			@Override
			protected void onChildSpringUpdate(Spring paramSpring) {
				// TODO Auto-generated method stub
				float f = (float)SpringUtil.mapValueFromRangeToRange(paramSpring.getCurrentValue(), 0.0D, 1.0D, 0.0D, SimpleStackController.this.heightToDismissAt);
		        this.dragController.getView().setTranslationY(f);
		        this.dragController.getView().setRotation(SimpleStackController.this.getRotationFromYTranslation(this.dragController.getView()));
			}

		};
	}

//	private SpringListener getDraggingOverlayListener(
//			final DismissableViewController paramDismissableViewController2,
//			final DismissableViewController paramDismissableViewController1) {
//		return new SpringListener() {
//
//			@Override
//			public void onSpringUpdate(Spring paramSpring) {
//				// TODO Auto-generated method stub
//				if (paramDismissableViewController1 != null) {
//					float f = (float) SpringUtil.mapValueFromRangeToRange(
//							paramSpring.getCurrentValue(), 0.0D, 1.0D, 0.0D,
//							1.0D);
//					paramDismissableViewController1.getView().setScaleX(
//							Math.max(0.0F, f));
//					paramDismissableViewController1.getView().setScaleY(
//							Math.max(0.0F, f));
//				}
//				float f = (float) SpringUtil.mapValueFromRangeToRange(
//						paramSpring.getCurrentValue(), 0.0D, 1.0D, 0.0D,
//						SimpleStackController.this.heightToDismissAt);
//				paramDismissableViewController2.getView().setTranslationY(f);
//				paramDismissableViewController2
//						.getView()
//						.setRotation(
//								SimpleStackController.this
//										.getRotationFromYTranslation(paramDismissableViewController2
//												.getView()));
//			}
//
//			@Override
//			public void onSpringEndStateChange(Spring arg0) {
//				// TODO Auto-generated method stub
//
//			}
//
//			@Override
//			public void onSpringAtRest(Spring arg0) {
//				// TODO Auto-generated method stub
//
//			}
//
//			@Override
//			public void onSpringActivate(Spring arg0) {
//				// TODO Auto-generated method stub
//
//			}
//		};
//	}
	
	private SpringListener getDraggingOverlayListener(
			DismissableViewController paramDismissableViewController2,
			DismissableViewController paramDismissableViewController1) {
		
		return new OverlayStartDropDragListener(paramDismissableViewController1, paramDismissableViewController2) {

			@Override
			protected void onChildSpringUpdate(Spring paramSpring) {
				// TODO Auto-generated method stub
				if (dragBaseController != null) {
					float f = (float) SpringUtil.mapValueFromRangeToRange(
							paramSpring.getCurrentValue(), 0.0D, 1.0D, 0.0D,
							1.0D);
					dragBaseController.getView().setScaleX(
							Math.max(0.0F, f));
					dragBaseController.getView().setScaleY(
							Math.max(0.0F, f));
				}
				float f = (float) SpringUtil.mapValueFromRangeToRange(
						paramSpring.getCurrentValue(), 0.0D, 1.0D, 0.0D,
						SimpleStackController.this.heightToDismissAt);
				dragOverlayController.getView().setTranslationY(f);
				dragOverlayController
						.getView()
						.setRotation(
								SimpleStackController.this
										.getRotationFromYTranslation(dragOverlayController
												.getView()));
			}
		};
		
	}

	private void handleFinishedDrag(int paramInt1, int paramInt2) {
		double d = this.dragState.getYVelocityInPixelsPerSecond();
		if ((this.dragState.viewDragging.getY() <= getYCutoffForDismissal())
				&& ((d <= 450.0D) || (this.dragState.viewDragging.getY() <= getYCutoffForVelocityDismissal()))) {
			this.dragSpring.setEndValue(0.0D);
			d = this.dragState.getYVelocityInPixelsPerSecond()
					/ this.heightToDismissAt;
			this.dragSpring.setVelocity(d);
		} else {
			DismissableViewController localDismissableViewController = null;
			int i = 0;
			if ((this.overlayController != null)
					&& (this.dragState.viewDragging == this.overlayController
							.getView())) {
				localDismissableViewController = this.overlayController;
				i = 1;
				this.overlayController = null;
			}
			if (localDismissableViewController == null) {
				localDismissableViewController = this.baseController;
			}
			d = this.dragState.getYVelocityInPixelsPerSecond()
					/ this.heightToDismissAt;

			getAnimatorForController(localDismissableViewController)
					.bringMovingViewOut(true, AnimationType.SLIDE,
							DismissalType.REMOVE,
							1.0D - this.dragSpring.getCurrentValue(), -1.0D * d);
			if ((i != 0) && (this.baseController != null))
				animateBaseControllerIn(this.baseController);
			if (i == 0)
				this.hasBaseController = false;
			this.dragSpring.removeAllListeners();
			this.dragSpring.setCurrentValue(0.0D).setAtRest();
		}
	}

	private DismissableViewController getControllerToReceiveTouches() {
		DismissableViewController localDismissableViewController;
		if (this.overlayController == null)
			localDismissableViewController = this.baseController;
		else
			localDismissableViewController = this.overlayController;
		return localDismissableViewController;
	}

	private float getFinalDismissalRotation() {
		return 3.0F * this.finalDragAngle;
	}

	private void setFinalDragAngle(float paramFloat) {
		int i = getMeasuredWidth() / 2;
		this.finalDragAngle = (2.8698F + 11.454201F * (-1.0F * (i - paramFloat) / i));
	}

	private int getYCutoffForVelocityDismissal() {
		return getHeight() / 6;
	}

	private float getRotationFromYTranslation(View paramView) {
		int i = 0 + getYCutoffForDismissal();
		float f = paramView.getY() - 0.0F;
		return (1.0F - (i - f) / i) * this.finalDragAngle;
	}

	private int getYCutoffForDismissal() {
		return getHeight() / 2;
	}

	private SimpleStackListener listener;

	public void setListener(SimpleStackListener paramSimpleStackListener) {
		this.listener = paramSimpleStackListener;
	}

	public void setOverlayController(
			DismissableViewController paramDismissableViewController,
			AnimationType paramAnimationType) {
		if (this.showingSpinner)
			removeSpinner();
		if ((paramDismissableViewController != this.overlayController)
				&& (getAnimatorForController(paramDismissableViewController) == null)) {
			paramDismissableViewController.getView().setRotation(0.0F);
			if (this.overlayController == null) {
				if (this.baseController != null) {
					InAndOutAnimator localInAndOutAnimator = getAnimatorForController(this.baseController);
					if (localInAndOutAnimator != null)
						localInAndOutAnimator.bringViewOut(AnimationType.SCALE,
								DismissalType.HIDE);
				}
			} else
				getAnimatorForController(this.overlayController).bringViewOut(
						AnimationType.SLIDE, DismissalType.REMOVE);
			this.dragSpring.removeAllListeners();
			this.overlayController = paramDismissableViewController;
			new InAndOutAnimator(paramDismissableViewController)
					.bringViewIn(paramAnimationType);
			this.dragSpring.setCurrentValue(0.0D).setEndValue(1.0D).setAtRest();
			this.overlayController.getView().setLayerType(2, null);
			addView(this.overlayController.getView());
			paramDismissableViewController.onShow(this);
		}
	}

	private class InAndOutAnimator {
		private SimpleStackController.DismissalType dismissalType = SimpleStackController.DismissalType.REMOVE;
		private SimpleStackController.AnimationType incomingType;
		// need 修改
		private boolean isOutgoing = false;
		private SimpleStackController.AnimationType outgoingType;
		private float propertyFullyIn;
		private float propertyFullyOut;
		private final Spring spring;
		private boolean swiping = false;
		private double valueAtAnimationStart;
		private final DismissableViewController viewController;

		public InAndOutAnimator(DismissableViewController arg2) {
			SimpleStackController.this.registry.put(arg2, this);
			this.viewController = arg2;
			this.spring = SimpleStackController.this.springSystem
					.createSpring();
			this.spring.addListener(getInAndOutListener());
		}

		private SpringListener getInAndOutListener() {

			return new SimpleSpringListener() {
				@Override
				public void onSpringActivate(Spring spring) {
					// TODO Auto-generated method stub
					super.onSpringActivate(spring);
					if (!SimpleStackController.InAndOutAnimator.this.isOutgoing) {

						SimpleStackController.InAndOutAnimator.this.viewController
								.getView().setRotation(0.0F);
						if (SimpleStackController.InAndOutAnimator.this.incomingType != SimpleStackController.AnimationType.SLIDE) {
							if (SimpleStackController.InAndOutAnimator.this.incomingType != SimpleStackController.AnimationType.SCALE) {
								if (SimpleStackController.InAndOutAnimator.this.incomingType == SimpleStackController.AnimationType.NOTIFICATION) {
									SimpleStackController.InAndOutAnimator.this.viewController
											.getView()
											.setPivotY(
													SimpleStackController.this.topNavHeight);
									SimpleStackController.InAndOutAnimator.this.viewController
											.getView()
											.setPivotX(
													SimpleStackController.this.topNavIconMiddle);
									SimpleStackController.InAndOutAnimator.this.viewController
											.getView().setTranslationY(0.0F);
								}
							} else
								SimpleStackController.InAndOutAnimator.this.viewController
										.getView().setTranslationY(0.0F);
						} else {
							SimpleStackController.InAndOutAnimator.this.viewController
									.getView().setScaleX(1.0F);
							SimpleStackController.InAndOutAnimator.this.viewController
									.getView().setScaleY(1.0F);
						}
					}
					if (SimpleStackController.InAndOutAnimator.this.viewController
							.getView().getVisibility() != View.VISIBLE)
						SimpleStackController.InAndOutAnimator.this.viewController
								.getView().setVisibility(View.VISIBLE);
					SimpleStackController.InAndOutAnimator.this.viewController
							.onStartDragging();
				}

				@Override
				public void onSpringAtRest(Spring paramSpring) {
					// TODO Auto-generated method stub
					super.onSpringAtRest(spring);
					SimpleStackController.InAndOutAnimator.this.viewController
							.onStopDragging();
					if (SimpleStackController.InAndOutAnimator.this.isOutgoing) {
						// SimpleStackController.access$502(
						// SimpleStackController.this, false);
						SimpleStackController.InAndOutAnimator.this.isOutgoing = false;
						SimpleStackController.InAndOutAnimator.this.viewController
								.onHide(SimpleStackController.this);
						SimpleStackController.this.registry

								.remove(SimpleStackController.InAndOutAnimator.this.viewController);
						if (SimpleStackController.InAndOutAnimator.this.dismissalType == SimpleStackController.DismissalType.REMOVE) {
							SimpleStackController.InAndOutAnimator.this.viewController
									.reset(SimpleStackController.this);

							Log.d(TAG, "simplestackcontroller.this.listener");
							SimpleStackController.this.listener
									.didDismiss(
											SimpleStackController.InAndOutAnimator.this.viewController,
											SimpleStackController.InAndOutAnimator.this.swiping);
						}
						SimpleStackController.InAndOutAnimator.this.viewController
								.getView().setVisibility(8);
						paramSpring.destroy();
						if (SimpleStackController.this.pendingController != null) {
							SimpleStackController.this

									.addBaseController(SimpleStackController.this.pendingController);
							// SimpleStackController.access$1702(
							// SimpleStackController.this, null);
						}
					}
				}

				@Override
				public void onSpringUpdate(Spring paramSpring) {
					// TODO Auto-generated method stub
					SimpleStackController.AnimationType localAnimationType;
					if (!SimpleStackController.InAndOutAnimator.this.isOutgoing) {
						localAnimationType = SimpleStackController.InAndOutAnimator.this.incomingType;
					} else {
						localAnimationType = SimpleStackController.InAndOutAnimator.this.outgoingType;
					}
					float f1;
					if (localAnimationType != SimpleStackController.AnimationType.SLIDE) {
						if (localAnimationType != SimpleStackController.AnimationType.SCALE) {
							if (localAnimationType == SimpleStackController.AnimationType.NOTIFICATION) {
								f1 = (float) SpringUtil
										.mapValueFromRangeToRange(
												paramSpring.getCurrentValue(),
												0.0D,
												1.0D,
												SimpleStackController.InAndOutAnimator.this.propertyFullyOut,
												SimpleStackController.InAndOutAnimator.this.propertyFullyIn);
								SimpleStackController.InAndOutAnimator.this.viewController
										.getView()
										.setScaleX(Math.max(0.0F, f1));
								SimpleStackController.InAndOutAnimator.this.viewController
										.getView()
										.setScaleY(Math.max(0.0F, f1));
							} else {

							}
						} else {
							f1 = (float) SpringUtil
									.mapValueFromRangeToRange(
											paramSpring.getCurrentValue(),
											0.0D,
											1.0D,
											SimpleStackController.InAndOutAnimator.this.propertyFullyOut,
											SimpleStackController.InAndOutAnimator.this.propertyFullyIn);
							SimpleStackController.InAndOutAnimator.this.viewController
									.getView().setScaleX(Math.max(0.0F, f1));
							SimpleStackController.InAndOutAnimator.this.viewController
									.getView().setScaleY(Math.max(0.0F, f1));
						}
					} else if (!SimpleStackController.InAndOutAnimator.this.isOutgoing) {
						f1 = (float) SpringUtil
								.mapValueFromRangeToRange(
										paramSpring.getCurrentValue(),
										0.0D,
										1.0D,
										-1.0F
												* SimpleStackController.this.heightToDismissAt,
										0.0D);
						SimpleStackController.InAndOutAnimator.this.viewController
								.getView().setTranslationY(f1);
					} else {
						float f2 = (float) SpringUtil.mapValueFromRangeToRange(
								paramSpring.getCurrentValue(), 0.0D, 1.0D,
								SimpleStackController.this.heightToDismissAt,
								0.0D);
						f1 = (float) SpringUtil
								.mapValueFromRangeToRange(
										paramSpring.getCurrentValue(),
										SimpleStackController.InAndOutAnimator.this.valueAtAnimationStart,
										0.0D,
										SimpleStackController.InAndOutAnimator.this.propertyFullyIn,
										SimpleStackController.InAndOutAnimator.this.propertyFullyOut);
						SimpleStackController.InAndOutAnimator.this.viewController
								.getView().setRotation(f1);
						SimpleStackController.InAndOutAnimator.this.viewController
								.getView().setTranslationY(f2);
						if (SimpleStackController.InAndOutAnimator.this.viewController
								.getView().getY() > SimpleStackController.this
								.getMeasuredHeight())
							paramSpring.setAtRest();
					}

				}

			};
		}

		private void setStartAndEndProperties(
				SimpleStackController.AnimationType paramAnimationType) {
			if (paramAnimationType != SimpleStackController.AnimationType.SLIDE) {
				if (paramAnimationType != SimpleStackController.AnimationType.NOTIFICATION) {
					if (paramAnimationType == SimpleStackController.AnimationType.SCALE) {
						this.propertyFullyIn = 1.0F;
						this.propertyFullyOut = 0.0F;
					}
				} else {
					this.propertyFullyIn = 1.0F;
					this.propertyFullyOut = 0.0F;
				}
			} else {
				this.propertyFullyIn = this.viewController.getView()
						.getRotation();
				this.propertyFullyOut = SimpleStackController.this
						.getFinalDismissalRotation();
			}
		}

		public void bringViewIn(
				SimpleStackController.AnimationType paramAnimationType) {
			this.incomingType = paramAnimationType;
			this.valueAtAnimationStart = 0.0D;
			setStartAndEndProperties(this.incomingType);
			this.spring.setEndValue(1.0D);
			this.spring.setCurrentValue(0.0D);
		}

		public void bringViewOut(
				SimpleStackController.AnimationType paramAnimationType,
				SimpleStackController.DismissalType paramDismissalType) {
			bringMovingViewOut(false, paramAnimationType, paramDismissalType,
					1.0D, 0.0D);
		}

		public void bringMovingViewOut(boolean paramBoolean,
				SimpleStackController.AnimationType paramAnimationType,
				SimpleStackController.DismissalType paramDismissalType,
				double paramDouble1, double paramDouble2) {

			this.isOutgoing = true;
			this.swiping = paramBoolean;
			this.outgoingType = paramAnimationType;
			this.dismissalType = paramDismissalType;
			this.valueAtAnimationStart = paramDouble1;
			setStartAndEndProperties(this.outgoingType);
			this.spring.setEndValue(0.0D);
			this.spring.setCurrentValue(paramDouble1);
			this.spring.setVelocity(paramDouble2);
		}
	}

	public static enum DismissalType {
		HIDE, REMOVE
	}

	public static enum AnimationType {
		SLIDE, SCALE, NOTIFICATION
	}

	private abstract class StartDropDragListener extends SimpleSpringListener {
		DismissableViewController dragController;
		private boolean notifiedStarted = false;

		public StartDropDragListener(DismissableViewController arg2) {
			this.dragController = arg2;
		}

		protected abstract void onChildSpringUpdate(Spring paramSpring);

		public void onSpringAtRest(Spring paramSpring) {
			this.dragController.onStopDragging();
			this.notifiedStarted = false;
		}

		public final void onSpringUpdate(Spring paramSpring) {
			if (!this.notifiedStarted) {
				this.dragController.onStartDragging();
				this.notifiedStarted = true;
			}
			onChildSpringUpdate(paramSpring);
		}
	}
	
	private abstract class OverlayStartDropDragListener extends SimpleSpringListener {
		DismissableViewController dragOverlayController;
		DismissableViewController dragBaseController;
		private boolean notifiedStarted = false;

		public OverlayStartDropDragListener(DismissableViewController arg1, DismissableViewController arg2) {
			this.dragBaseController = arg1;
			this.dragOverlayController = arg2;
		}

		protected abstract void onChildSpringUpdate(Spring paramSpring);

		public void onSpringAtRest(Spring paramSpring) {
			this.dragOverlayController.onStopDragging();
			this.notifiedStarted = false;
		}

		public final void onSpringUpdate(Spring paramSpring) {
			if (!this.notifiedStarted) {
				this.dragOverlayController.onStartDragging();
				this.notifiedStarted = true;
			}
			onChildSpringUpdate(paramSpring);
		}
	}

	@Override
	public View getView() {
		// TODO Auto-generated method stub
		return this;
	}

	@Override
	public boolean onBackPressed() {
		// TODO Auto-generated method stub
		boolean bool = true;

		if (this.overlayController == null) {
			if (this.baseController == null) {
				bool = false;
			} else {
				bool = this.baseController.onBackPressed();
			}
		} else if (!this.overlayController.onBackPressed()) {
			dismissCurrentOverlay(AnimationType.SLIDE);
		}
		return bool;
	}

	@Override
	public void onHide(ViewGroup paramViewGroup) {
		// TODO Auto-generated method stub
		if (this.baseController != null)
			this.baseController.onHide(this);
		if (this.overlayController != null)
			this.overlayController.onHide(this);
		removeSpinner();
		this.bus.unregister(this);
	}

	@Override
	public void onShow(ViewGroup paramViewGroup) {
		// TODO Auto-generated method stub
		this.bus.register(this);
		
		Log.d(TAG, "hasShowSpinner " + this.hasShownSpinner + " " + hasCurrentBaseController());
		if ((!this.hasShownSpinner) && (!hasCurrentBaseController()))
			showSpinner();
//		if (this.baseController != null && !(this.baseController instanceof QuestionViewController))
//			this.baseController.onShow(this);
//		if (this.overlayController != null && !(this.overlayController instanceof QuestionViewController))
//			this.overlayController.onShow(this);
		
		if (this.baseController != null)
			this.baseController.onShow(this);
		if (this.overlayController != null)
			this.overlayController.onShow(this);
	}

	@Override
	public void resumeState(Bundle paramBundle) {
		// TODO Auto-generated method stub
	}

	@Override
	public void saveState(Bundle paramBundle) {
		// TODO Auto-generated method stub
	}
}
