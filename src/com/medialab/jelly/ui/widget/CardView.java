package com.medialab.jelly.ui.widget;

import android.content.Context;
import android.support.v4.view.MotionEventCompat;
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
import com.medialab.jelly.R;
import com.medialab.jelly.controller.DismissableViewController;
import com.medialab.jelly.controller.StarfishScreenUtils;
import com.medialab.jelly.util.DragState;

public abstract class CardView<SlidingObjectType, MainViewType extends View & SlideableMainView, SlidingViewType extends View & SlideableView>
		extends ViewGroup {

	private static final String TAG = CardView.class.getSimpleName();
	private DismissableViewController answerCompositionController;
	private Spring answerCompositionSpring;
	private final int cardLeftMargin;
	private boolean destroyed = false;
	private SlidingDetailCards<SlidingObjectType, SlidingViewType> detailCards;
	private Spring detailCardsFadeInSpring;
	private Spring detailCardsSpring;
	private final DragState dragState;
	private boolean firstLayout = true;
	private final View.OnClickListener hideDetailCardsClickListener;
	private MainViewType mainView;
	private final StarfishScreenUtils screenUtils;

	public CardView(Context paramContext,
			StarfishScreenUtils paramStarfishScreenUtils,
			SpringSystem paramSpringSystem) {
		super(paramContext);
		// TODO Auto-generated constructor stub
		this.screenUtils = paramStarfishScreenUtils;
		this.dragState = new DragState(ViewConfiguration.get(paramContext)
				.getScaledTouchSlop());
		this.cardLeftMargin = getResources().getDimensionPixelSize(
				R.dimen.card_left_margin);
		this.detailCardsSpring = paramSpringSystem.createSpring();
		this.answerCompositionSpring = paramSpringSystem.createSpring();
		this.detailCardsFadeInSpring = paramSpringSystem.createSpring();
		this.hideDetailCardsClickListener = new View.OnClickListener() {
			public void onClick(View paramView) {
				CardView.this.detailCards.slideClose();
				CardView.this.detailCardsSpring.setEndValue(0.0D);
			}
		};

		setClipChildren(false);
	}

	private SpringListener getAnswerCompositionSpringListener() {
		return new SimpleSpringListener() {
			public void onSpringAtRest(Spring paramSpring) {
				if (paramSpring.getEndValue() < 1.0D) {
					CardView.this.killAnswerComposition(true);
					CardView.this.showDetailCards();
				}
			}

			public void onSpringEndStateChange(Spring paramSpring) {
				if ((paramSpring.getEndValue() == 1.0D)
						&& (CardView.this.answerCompositionController != null)) {
					CardView.this.answerCompositionController
							.onShow(CardView.this);
					CardView.this
							.addView(CardView.this.answerCompositionController
									.getView());
				}
			}

			public void onSpringUpdate(Spring paramSpring) {
				float f = (float) SpringUtil.mapValueFromRangeToRange(
						paramSpring.getCurrentValue(), 0.0D, 1.0D,
						CardView.this.getMeasuredHeight(), 0.0D);
				CardView.this.answerCompositionController.getView()
						.setTranslationY(f);
				if ((paramSpring.getEndValue() == 0.0D)
						&& (CardView.this.answerCompositionController.getView()
								.getY() > CardView.this.getMeasuredHeight()))
					paramSpring.setAtRest();
			}
		};
	}

	private SpringListener getDetailCardsSpringListener() {
		return new SimpleSpringListener() {
			boolean hasFlippedAlpha = false;

			public void onSpringEndStateChange(Spring paramSpring) {
				this.hasFlippedAlpha = false;
				if (paramSpring.getEndValue() < 1.0D) {
					CardView.this.detailCards.setClickable(true);
					((SlideableMainView) CardView.this.mainView)
							.resetMainViewClickListener();
				} else {
					CardView.this.detailCards.setClickable(false);
					((SlideableMainView) CardView.this.mainView)
							.setMainViewClickListener(CardView.this.hideDetailCardsClickListener);
					CardView.this.detailCards.slideOpen();
				}
			}

			public void onSpringUpdate(Spring paramSpring) {
				float f = (float) SpringUtil.mapValueFromRangeToRange(
						paramSpring.getCurrentValue(), 0.0D, 1.0D,
						CardView.this.getDetailClosedTopLine(CardView.this
								.getHeight()), CardView.this
								.getDetailOpenTopLine(CardView.this.mainView
										.getMeasuredHeight()));
				CardView.this.detailCards.setTranslationY(f);
				if (paramSpring.getCurrentValue() >= 0.2D) {
					if (!this.hasFlippedAlpha) {
						CardView.this.detailCards
								.setSlidingDetailSubviewAlpha(1.0F);
						this.hasFlippedAlpha = true;
					}
					Log.d(TAG, "cardView > = 0.2");
				} else {
					
					Log.d(TAG, "cardView < 0.2");
					CardView.this.detailCards
							.setSlidingDetailSubviewAlpha(Math
									.max(0.0F, (float) paramSpring
											.getCurrentValue() / 0.2F));
				}
			}
		};
	}

	private SpringListener getDetailFadeSpringListener() {
		return new SimpleSpringListener() {
			public void onSpringUpdate(Spring paramSpring) {
				CardView.this.detailCards.setAlpha((float) paramSpring
						.getCurrentValue());
			}
		};
	}

	private int getTopThresholdForClosing() {
		return 20 + getHeight() / 2;
	}

	private void handleFinishedDrag() {
		if (!this.destroyed) {
			float f = getDetailClosedTopLine(getHeight())
					- getDetailOpenTopLine(this.mainView.getMeasuredHeight());
			double d1 = -1.0D * this.dragState.getYVelocityInPixelsPerSecond()
					/ f;
			double d2 = this.dragState.getYVelocityInPixelsPerSecond();
			if (((this.detailCards.getY() >= getTopThresholdForClosing()) || (d2 >= 450.0D))
					&& (d2 * -1.0D <= 450.0D)) {
				this.detailCardsSpring.setEndValue(0.0D);
			} else {
				this.detailCardsSpring.setEndValue(1.0D);
				this.detailCards.slideOpen();
			}
			this.detailCardsSpring.setVelocity(d1);
		}
	}

	private void killAnswerComposition(boolean paramBoolean) {
		if (paramBoolean)
			this.answerCompositionController.onHide(this);
		if (this.answerCompositionController.getView().getParent() == this)
			removeView(this.answerCompositionController.getView());
		this.answerCompositionController = null;
	}

	public void destroy() {
		this.destroyed = true;
		this.answerCompositionSpring.destroy();
		this.detailCardsFadeInSpring.destroy();
		this.detailCardsSpring.destroy();
		this.detailCards.destroy();
		((SlideableMainView) this.mainView).destroy();
		this.dragState.destroy();
	}

	public void endNewAnswerCompositionImmediately() {
		if (this.answerCompositionController != null)
			killAnswerComposition(false);
	}

	public void endNewAnswerCreation() {
		if (this.answerCompositionController != null)
			this.answerCompositionSpring.setEndValue(0.0D);
	}

	protected abstract int getAnswerCompositionTopLine(int paramInt);

	protected SlidingDetailCards<SlidingObjectType, SlidingViewType> getCurrentDetailCards() {
		return this.detailCards;
	}

	protected MainViewType getCurrentMainView() {
		return this.mainView;
	}

	protected abstract int getDetailClosedTopLine(int paramInt);

	protected abstract int getDetailOpenTopLine(int paramInt);

	public void hideDetailCards() {
		this.detailCardsFadeInSpring.setEndValue(0.0D);
	}

	public boolean isDismissable() {
		boolean i;
		if ((this.detailCards.isOpened())
				|| (this.answerCompositionController != null))
			i = false;
		else
			i = true;
		return i;
	}

	public boolean isDisplayingAnswerComposition() {
		boolean i;
		if (this.answerCompositionController == null)
			i = false;
		else
			i = true;
		return i;
	}

	@Override
	public boolean onInterceptTouchEvent(MotionEvent paramMotionEvent) {
		// TODO Auto-generated method stub
		boolean i = true;
		int j = MotionEventCompat.getActionMasked(paramMotionEvent);
		if ((j != 3) && (j != 1)) {
			switch (j) {
			case 0:
				if (!this.detailCards.isExpandable())
					break;
				DragState localDragState = this.dragState;
				SlidingDetailCards localSlidingDetailCards = this.detailCards;
				DragState.Orientation localOrientation = DragState.Orientation.VERTICAL;
				DragState.Direction localDirection = DragState.Direction.ANY;
				float f2 = paramMotionEvent.getX();
				float f1 = paramMotionEvent.getY();
				if (isDisplayingAnswerComposition())
					i = false;
				localDragState.startPotentialDrag(localSlidingDetailCards,
						localOrientation, localDirection, f2, f1, i);
				break;
			case 1:
				break;
			case 2:
				if (this.dragState.isActuallyDragging)
					return i;
				// break label202;
				if (this.dragState.shouldStartDrag(paramMotionEvent.getX(),
						paramMotionEvent.getY())) {
					this.dragState.startActualDrag(paramMotionEvent.getX(),
							paramMotionEvent.getY());
					this.detailCards.slideClose();
					return i;
					// break label168;
				}
			}
			i = false;
			return i;
			// label168: this.dragState.startActualDrag(paramMotionEvent.getX(),
			// paramMotionEvent.getY());
			// this.detailCards.slideClose();
		} else {
			this.dragState.stopDrag();
			i = false;
		}
		return i;
	}

	@Override
	protected void onLayout(boolean changed, int paramInt1, int paramInt2,
			int paramInt3, int paramInt4) {
		// TODO Auto-generated method stub
		int j = paramInt4 - paramInt2;
		int i = this.mainView.getMeasuredHeight();
		int k = this.mainView.getMeasuredWidth();
		this.mainView
				.layout(this.cardLeftMargin, 0, k + this.cardLeftMargin, i);
		int m = this.detailCards.getMeasuredWidth();
		k = this.detailCards.getMeasuredHeight();
		this.detailCards.layout(0, 0, m, k);
		if (this.firstLayout) {
			this.detailCards.setTranslationY(getDetailClosedTopLine(j));
			this.firstLayout = false;
		}
		if (this.answerCompositionController != null) {
			k = Math.min(getAnswerCompositionTopLine(i), j
					- this.answerCompositionController.getView()
							.getMeasuredHeight());
			i = k
					+ this.answerCompositionController.getView()
							.getMeasuredHeight();
			m = this.cardLeftMargin;
			j = m
					+ this.answerCompositionController.getView()
							.getMeasuredWidth();
			this.answerCompositionController.getView().layout(m, k, j, i);
		}
	}

	@Override
	protected void onMeasure(int paramInt1, int paramInt2) {
		// TODO Auto-generated method stub
		int k = View.MeasureSpec.makeMeasureSpec(
				View.MeasureSpec.getSize(paramInt1), MeasureSpec.EXACTLY);
		int j = View.MeasureSpec.getSize(paramInt1) - 2 * this.cardLeftMargin;
		int i = this.screenUtils.getDisplayCardHeight(j - 2
				* this.cardLeftMargin);

		this.mainView.measure(
				View.MeasureSpec.makeMeasureSpec(j, MeasureSpec.EXACTLY),
				View.MeasureSpec.makeMeasureSpec(i, MeasureSpec.EXACTLY));
		this.detailCards.setCardWidthAndHeight(j, i);
		this.detailCards.measure(k,
				View.MeasureSpec.makeMeasureSpec(i * 2, MeasureSpec.AT_MOST));
		if (this.answerCompositionController != null) {
			k = View.MeasureSpec.getSize(paramInt2);
			this.answerCompositionController.getView().setMinimumHeight(
					k - getAnswerCompositionTopLine(i));
			this.answerCompositionController.getView().measure(
					View.MeasureSpec.makeMeasureSpec(j, MeasureSpec.EXACTLY),
					View.MeasureSpec.makeMeasureSpec(k, MeasureSpec.AT_MOST));
		}
		super.onMeasure(paramInt1, paramInt2);
	}

	@Override
	public boolean onTouchEvent(MotionEvent paramMotionEvent) {
		// TODO Auto-generated method stub
		switch (MotionEventCompat.getActionMasked(paramMotionEvent)) {
		case 0:
			System.out.println("CardView action_down");
			break;
		case 1:
		case 3:
			if (this.dragState.isActuallyDragging)
				handleFinishedDrag();
			this.dragState.stopDrag();
			break;
		case 2:
			if ((!this.dragState.isActuallyDragging)
					&& (this.dragState.shouldStartDrag(paramMotionEvent.getX(),
							paramMotionEvent.getY()))) {
				this.dragState.startActualDrag(paramMotionEvent.getX(),
						paramMotionEvent.getY());
				this.detailCards.slideClose();
			}
			this.dragState.addMotionEventToDrag(paramMotionEvent);
			if (!this.dragState.isActuallyDragging)
				break;
			float f = getDetailClosedTopLine(getHeight())
					- getDetailOpenTopLine(this.mainView.getMeasuredHeight());
			double d = -1.0F * (this.dragState.getLastYDiff() / f)
					+ this.detailCardsSpring.getCurrentValue();
			this.detailCardsSpring.setCurrentValue(d).setAtRest();
		}
		return super.onTouchEvent(paramMotionEvent);
	}

	public void popUpDetailCards() {
		post(new Runnable() {
			public void run() {
				CardView.this.detailCardsSpring.setEndValue(1.0D);
			}
		});
	}

	protected void resetToStartState() {
		this.detailCardsFadeInSpring.setCurrentValue(0.0D).setAtRest();
	}

	public void setMainViewAndSlidingCards(
			MainViewType paramMainViewType,
			SlidingDetailCards<SlidingObjectType, SlidingViewType> paramSlidingDetailCards) {
		if (paramMainViewType != this.mainView) {
			this.mainView = paramMainViewType;
			this.mainView.setLayerType(View.LAYER_TYPE_HARDWARE, null);
			addView(this.mainView);
		}
		if (paramSlidingDetailCards != this.detailCards) {
			this.detailCards = paramSlidingDetailCards;
			addView(paramSlidingDetailCards);
		}
		this.detailCards.setOnClickListener(new View.OnClickListener() {
			public void onClick(View paramView) {
				if (CardView.this.detailCards.isExpandable())
					CardView.this.detailCardsSpring.setEndValue(1.0D);
			}
		});
		((SlideableMainView) paramMainViewType).resetMainViewClickListener();
		this.detailCardsSpring.setCurrentValue(0.0D).setAtRest()
				.addListener(getDetailCardsSpringListener());
		this.detailCardsFadeInSpring.setCurrentValue(0.0D).setAtRest()
				.addListener(getDetailFadeSpringListener());
		this.answerCompositionSpring.setCurrentValue(0.0D).setAtRest()
				.addListener(getAnswerCompositionSpringListener());
	}

	public void showDetailCards() {
		this.detailCardsFadeInSpring.setEndValue(1.0D);
	}

	public void startNewAnswerCreation(
			DismissableViewController paramDismissableViewController) {
		if (this.answerCompositionController == null) {
			this.answerCompositionController = paramDismissableViewController;
			this.answerCompositionSpring.setCurrentValue(0.0D)
					.setEndValue(1.0D);
			hideDetailCards();
		}
	}

}
