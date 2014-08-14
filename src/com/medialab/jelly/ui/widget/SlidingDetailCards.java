package com.medialab.jelly.ui.widget;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Queue;

import android.content.Context;
import android.content.res.Resources;
import android.database.DataSetObserver;
import android.support.v4.view.MotionEventCompat;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.widget.Scroller;

import com.facebook.rebound.SimpleSpringListener;
import com.facebook.rebound.Spring;
import com.facebook.rebound.SpringListener;
import com.facebook.rebound.SpringSystem;
import com.facebook.rebound.SpringUtil;
import com.google.common.collect.Lists;
import com.medialab.jelly.R;
import com.medialab.jelly.util.DragState;

public class SlidingDetailCards<ObjectType, ViewType extends View & SlideableView>
		extends ViewGroup {

	private static final String TAG = SlidingDetailCards.class.getSimpleName();
	private final SlidingDetailCardsAdapter<ObjectType, ViewType> adapter;
	private int cardHeight;
	private int cardWidth;
	private final SlidingDetailCards<ObjectType, ViewType>.VisibleCardState cards = new VisibleCardState();
	private float currentAlpha;
	private final View detailView;
	private final int detailViewHeight;
	private final int detailViewSpacingFromTop;
	private final int detailViewWidth;
	private final DragState dragState;

	private Spring expandSpring;
	private boolean isOpened = false;
	private final int leftMargin;
	private boolean needsExpandRefresh = false;
	private boolean needsInitialize = true;
	private final int pixelsBetweenViews;

	private final Queue<View> recycledViews = new ArrayDeque();
	private Spring scrollSpring;
	private final Scroller scroller;
	private final SpringSystem springSystem;

	private final List<SlidingDetailCards<ObjectType, ViewType>.ClosedTranslation> translations = Lists
			.newArrayListWithCapacity(3);
	private final int yBumpPerObj;

	public SlidingDetailCards(
			Context paramContext,
			SlidingDetailCardsAdapter<ObjectType, ViewType> paramSlidingDetailCardsAdapter,
			SpringSystem paramSpringSystem) {
		super(paramContext);
		// TODO Auto-generated constructor stub
		Resources localResources = paramContext.getResources();
		this.leftMargin = localResources
				.getDimensionPixelSize(R.dimen.card_left_margin);
		this.springSystem = paramSpringSystem;
		this.scroller = new Scroller(paramContext);
		this.adapter = paramSlidingDetailCardsAdapter;

		this.dragState = new DragState(ViewConfiguration.get(paramContext)
				.getScaledTouchSlop());

		this.adapter.registerDataSetObserver(new DataSetObserver() {
			public void onChanged() {
				if (!SlidingDetailCards.this.needsInitialize) {
					int i = SlidingDetailCards.this.cards.getCurrentIndex();
					if ((i < 0)
							&& (SlidingDetailCards.this.adapter.getCount() > 0))
						i = 0;
					SlidingDetailCards.this.cards.setCurrentIndex(i);
				}
			}

			public void onInvalidated() {
				SlidingDetailCards.this.initialize();
			}
		});
		this.detailViewWidth = localResources
				.getDimensionPixelSize(R.dimen.sliding_detail_view_width);
		this.detailViewHeight = localResources
				.getDimensionPixelSize(R.dimen.sliding_detail_view_height);
		this.detailViewSpacingFromTop = localResources
				.getDimensionPixelSize(R.dimen.sliding_detail_view_from_top);
		this.pixelsBetweenViews = (int) (localResources
				.getDimension(R.dimen.card_left_margin) / 2.0F);
		this.yBumpPerObj = localResources
				.getDimensionPixelSize(R.dimen.card_y_bump_closed);
		this.detailView = this.adapter.getDetailView();
		this.detailView.setLayerType(LAYER_TYPE_HARDWARE, null);
		setLayerType(LAYER_TYPE_HARDWARE, null);
		this.detailView.setLayerType(LAYER_TYPE_HARDWARE, null);
		addView(this.detailView);
		setClipChildren(false);
	}

	private int convertScrollToPageIndex(int paramInt) {
		int i = Math
				.max(0,
						Math.min(
								(paramInt - getLeftXOfOpenedDetailCards() + getMeasuredWidth() / 2)
										/ (this.cardWidth + this.pixelsBetweenViews),
								-1 + this.adapter.getCount()));
		return i;
	}

	private int getDetailViewLeft() {
		return (this.cardWidth - this.detailViewWidth) / 2;
	}

	private SpringListener getExpandSpringListener() {
		return new SimpleSpringListener() {
			public void onSpringActivate(Spring paramSpring) {
			}

			public void onSpringAtRest(Spring paramSpring) {
				if (paramSpring.getEndValue() == 1.0D)
					SlidingDetailCards.this.cards.hideMaskingViews();
			}

			public void onSpringEndStateChange(Spring paramSpring) {
				Log.d(TAG, "Spring end state changed to "
						+ paramSpring.getEndValue());
				if (paramSpring.getEndValue() == 0.0D)
					SlidingDetailCards.this.cards.showMaskingViews();
			}

			public void onSpringUpdate(Spring paramSpring) {
				Log.d(TAG, "Updating expand spring");
				float f1 = 1.0F - (float) paramSpring.getCurrentValue();
				int j = SlidingDetailCards.this.translations.size();

				for (int i = 0;; i++) {
					if (i >= j)
						return;
					Object localObject2 = (SlidingDetailCards.ClosedTranslation) SlidingDetailCards.this.translations
							.get(i);
					Object localObject1 = ((SlidingDetailCards.ClosedTranslation) localObject2).movingView;
					float f2 = (float) SpringUtil
							.mapValueFromRangeToRange(
									paramSpring.getCurrentValue(),
									0.0D,
									1.0D,
									((SlidingDetailCards.ClosedTranslation) localObject2).xTranslationStart,
									0.0D);
					float f3 = (float) SpringUtil
							.mapValueFromRangeToRange(
									paramSpring.getCurrentValue(),
									0.0D,
									1.0D,
									((SlidingDetailCards.ClosedTranslation) localObject2).yTranslationStart,
									0.0D);
					((View) localObject1).setTranslationX(f2);
					((View) localObject1).setTranslationY(f3);
					((View) localObject1)
							.setScaleX(1.0F
									- f1
									* (1.0F - ((SlidingDetailCards.ClosedTranslation) localObject2).scaleFactor));
					((View) localObject1)
							.setScaleY(1.0F
									- f1
									* (1.0F - ((SlidingDetailCards.ClosedTranslation) localObject2).scaleFactor));
					if (((SlidingDetailCards.ClosedTranslation) localObject2).maskingView != null) {
						View localView = ((SlidingDetailCards.ClosedTranslation) localObject2).maskingView;
						localView.setX(((View) localObject1).getX());
						localView.setY(((View) localObject1).getY());
						localView
								.setScaleX(1.0F
										- f1
										* (1.0F - ((SlidingDetailCards.ClosedTranslation) localObject2).scaleFactor));
						localView
								.setScaleY(1.0F
										- f1
										* (1.0F - ((SlidingDetailCards.ClosedTranslation) localObject2).scaleFactor));
						localView.setAlpha(f1);
					}
				}
			}
		};
	}

	private int getLeftEdgeOfIndex(int paramInt) {
		return paramInt * (this.cardWidth + this.pixelsBetweenViews)
				+ getLeftXOfOpenedDetailCards();
	}

	private int getLeftXOfOpenedDetailCards() {
		return this.leftMargin;
	}

	private int getScrollPositionForPage(int paramInt) {
		int i;
		if (paramInt > 0) {
			if (paramInt < -1 + this.adapter.getCount())
				i = getLeftEdgeOfIndex(paramInt)
						- (getMeasuredWidth() - this.cardWidth) / 2;
			else
				i = getLeftEdgeOfIndex(-1 + this.adapter.getCount())
						- (getMeasuredWidth() - (this.cardWidth + getLeftXOfOpenedDetailCards()));
		} else
			i = 0;
		return i;
	}

	private SpringListener getScrollSpringListener() {
		return new SimpleSpringListener() {
			public void onSpringUpdate(Spring paramSpring) {
				SlidingDetailCards.this.scrollTo(
						(int) paramSpring.getCurrentValue(), 0);
			}
		};
	}

	private void snapToCurrentScroll() {
		int i = getScrollX();
		Object[] arrayOfObject = new Object[1];
		arrayOfObject[0] = Integer.valueOf(i);
		Log.d(TAG, String.format("End scroll value: %d", arrayOfObject));
		int j = convertScrollToPageIndex(i);
		float f = -1.0F
				* (float) this.dragState.getXVelocityInPixelsPerSecond();
		if (j == this.cards.getCurrentIndex())
			if (f <= 450.0F) {
				if (f < -450.0F)
					j = Math.max(0, j - 1);
			} else
				j = Math.min(-1 + this.adapter.getCount(), j + 1);
		this.cards.setCurrentIndex(j);
		j = getScrollPositionForPage(this.cards.getCurrentIndex());
		this.scrollSpring.setVelocity(f);
		this.scrollSpring.setEndValue(j);
	}

	public void destroy() {
		if ((this.expandSpring != null) && (this.scrollSpring != null)
				&& (this.dragState != null)) {
			this.expandSpring.destroy();
			this.scrollSpring.destroy();
			this.dragState.destroy();
		}
		this.expandSpring = null;
		this.scrollSpring = null;
	}

	public SlidingDetailCardsAdapter<ObjectType, ViewType> getAdapter() {
		return this.adapter;
	}

	public void initialize() {
		int i = 0;
		if (getMeasuredWidth() > 0) {
			this.needsInitialize = false;
			this.translations.clear();
			if (this.scrollSpring == null)
				this.scrollSpring = this.springSystem.createSpring()
						.addListener(getScrollSpringListener());
			boolean bool;
			if (this.adapter.getStartObject() == null)
				bool = false;
			else
				bool = true;
			this.isOpened = bool;
			if (!this.isOpened) {
				this.scrollSpring.setCurrentValue(0.0D).setAtRest();
				VisibleCardState localVisibleCardState = this.cards;
				if (this.adapter.getCount() == 0)
					i = -1;
				localVisibleCardState.setCurrentIndex(i);
			} else {
				this.cards.setCurrentIndex(this.adapter
						.getIndexForObject(this.adapter.getStartObject()));
			}
			if (this.expandSpring == null)
				this.expandSpring = this.springSystem.createSpring()
						.addListener(getExpandSpringListener());
			if (this.adapter.getStartObject() == null)
				this.expandSpring.setCurrentValue(0.0D).setAtRest();
			else
				this.expandSpring.setCurrentValue(1.0D).setAtRest();
			float f;
			if (!this.isOpened)
				f = 0.0F;
			else
				f = 1.0F;
			setSlidingDetailSubviewAlpha(f);
			if (this.isOpened) {
				int j = getScrollPositionForPage(this.cards.getCurrentIndex());
				this.scrollSpring.setCurrentValue(j).setAtRest();
			}
			this.needsExpandRefresh = true;
		} else {
			this.needsInitialize = true;
		}
	}

	public boolean isExpandable() {
		return this.adapter.isExpandable();
	}

	public boolean isOpened() {
		return this.isOpened;
	}

	@Override
	public boolean onInterceptTouchEvent(MotionEvent paramMotionEvent) {
		// TODO Auto-generated method stub
		boolean i = true;
		int j = MotionEventCompat.getActionMasked(paramMotionEvent);
		if ((j != 3) && (j != 1)) {
			switch (j) {
			case 0:
				if (!this.isOpened)
					return i;
				// break label146;
				this.dragState.startPotentialDrag(this,
						DragState.Orientation.HORIZONTAL,
						DragState.Direction.ANY, paramMotionEvent.getX(),
						paramMotionEvent.getY(), i);
				Log.d(TAG, "Intercept down");
				break;
			case 2:
				if (this.dragState.isActuallyDragging)
					return i;
				// break label146;
				if (this.dragState.shouldStartDrag(paramMotionEvent.getX(),
						paramMotionEvent.getY())) {

					this.dragState.startActualDrag(paramMotionEvent.getX(),
							paramMotionEvent.getY());
					return i;
					// break label119;
				}
			case 1:
			}
			i = false;
			// break label146;
			return i;
			// label119: this.dragState.startActualDrag(paramMotionEvent.getX(),
			// paramMotionEvent.getY());
		} else {
			this.dragState.stopDrag();
			i = false;
		}
		return i;
	}

	//
	@Override
	protected void onLayout(boolean changed, int l, int t, int r, int b) {
		// TODO Auto-generated method stub
		int k = getDetailViewLeft();
		int j = k + this.detailView.getMeasuredWidth();
		int i = this.detailViewSpacingFromTop;
		int m = i + this.detailView.getMeasuredHeight();
		this.detailView.layout(k, i, j, m);
		j = this.cards.getVisibleStart();
		i = this.cards.getVisibleEnd();
		for (j = j;; j++) {
			if ((j < 0) || (j > i)) {
				if (this.needsExpandRefresh) {
					this.needsExpandRefresh = false;
					this.cards.precalculateClosedTranslations();
					this.expandSpring.setCurrentValue(this.expandSpring
							.getCurrentValue());
				}
				return;
			}
			View localView = this.cards.getCardAtIndex(j);
			m = getLeftEdgeOfIndex(j);
			k = m + localView.getMeasuredWidth();
			localView.layout(m, 0, k, this.cardHeight);
			localView = this.cards.getMaskingCardForIndex(j);
			if (localView == null)
				continue;
			localView.layout(m, 0, k, this.cardHeight);
		}
	}

	@Override
	protected void onMeasure(int paramInt1, int paramInt2) {
		// TODO Auto-generated method stub
		int i = this.cardHeight + 2 * this.yBumpPerObj;
		super.onMeasure(
				View.MeasureSpec.makeMeasureSpec(
						View.MeasureSpec.getSize(paramInt1), MeasureSpec.EXACTLY),
				View.MeasureSpec.makeMeasureSpec(i, MeasureSpec.EXACTLY));
		if (this.needsInitialize)
			initialize();
		int j = View.MeasureSpec.makeMeasureSpec(this.cardHeight, MeasureSpec.EXACTLY);
		int k = View.MeasureSpec.makeMeasureSpec(this.cardWidth, MeasureSpec.EXACTLY);
		int m = this.cards.getVisibleStart();
		i = this.cards.getVisibleEnd();
		for (m = m;; m++) {
			if ((m < 0) || (m > i)) {
				this.detailView.measure(View.MeasureSpec.makeMeasureSpec(
						this.detailViewWidth, MeasureSpec.EXACTLY), View.MeasureSpec
						.makeMeasureSpec(this.detailViewHeight, MeasureSpec.EXACTLY));
				return;
			}
			this.cards.getCardAtIndex(m).measure(k, j);
			View localView = this.cards.getMaskingCardForIndex(m);
			if (localView == null)
				continue;
			localView.measure(k, j);
		}
	}

	@Override
	public boolean onTouchEvent(MotionEvent paramMotionEvent) {
		// TODO Auto-generated method stub
		switch (MotionEventCompat.getActionMasked(paramMotionEvent)) {
		case 0:
			if ((!this.isOpened) || (this.scroller.isFinished()))
				break;
			this.scroller.abortAnimation();
			break;
		case 1:
			if (!this.isOpened)
				break;
			snapToCurrentScroll();
			this.dragState.stopDrag();
			break;
		case 2:
			if (!this.isOpened)
				break;
			float f = -1.0F
					* (paramMotionEvent.getX() - this.dragState.getLastX());
			this.dragState.addMotionEventToDrag(paramMotionEvent);
			this.scrollSpring.setCurrentValue(f + getScrollX()).setAtRest();
		}
		return super.onTouchEvent(paramMotionEvent);
	}

	public void setCardWidthAndHeight(int paramInt1, int paramInt2) {
		this.cardWidth = paramInt1;
		this.cardHeight = paramInt2;
	}

	public void setSlidingDetailSubviewAlpha(float paramFloat) {
		this.currentAlpha = paramFloat;
		this.detailView.setAlpha(1.0F - paramFloat);
		if (this.cards.getVisibleStart() >= 0)
			((SlideableView) this.cards.getCardAtIndex(this.cards
					.getVisibleStart())).setSlidingAlpha(this.currentAlpha);
	}

	public void slideClose() {
		if (this.isOpened) {
			this.isOpened = false;
			snapToCurrentScroll();
			if (this.expandSpring != null)
				this.expandSpring.setEndValue(0.0D);
		}
	}

	public void slideOpen() {
		if (!this.isOpened) {
			this.isOpened = true;
			if (this.expandSpring != null)
				this.expandSpring.setEndValue(1.0D);
		}
	}

	class ClosedTranslation {
		public View maskingView;
		public View movingView;
		public float scaleFactor;
		public float xTranslationStart;
		public float yTranslationStart;

		ClosedTranslation(SlidingDetailCards<ObjectType, ViewType> context) {
		}
	}

	private class VisibleCardState {
		private int currentIndex = -1;
		private int endIndex = -1;
		private final Map<Integer, SlidingDetailCards<ObjectType, ViewType>.VisibleCardState.PositionedCard> indexToView = new HashMap();
		private final List<Integer> indiciesToKill = new ArrayList();
		private final View[] maskingViews = new View[2];
		private int startIndex = -1;

		public VisibleCardState() {
		}

		private int getCenterForCurrentPage() {
			int i;
			if (this.currentIndex != 0) {
				if (this.currentIndex != SlidingDetailCards.this.adapter
						.getCount())
					i = SlidingDetailCards.this
							.getScrollPositionForPage(this.currentIndex)
							+ SlidingDetailCards.this.getMeasuredWidth() / 2;
				else
					i = SlidingDetailCards.this
							.getScrollPositionForPage(this.currentIndex)
							+ SlidingDetailCards.this.getMeasuredWidth() / 2;
			} else
				i = SlidingDetailCards.this.getMeasuredWidth() / 2;
			return i;
		}

		private void handlePotentiallyNewObjects() {
			Iterator localIterator = null;
			if (!SlidingDetailCards.this.needsInitialize) {
				Log.d(TAG, "Handling potentially new objects");
				this.indiciesToKill.clear();
				localIterator = this.indexToView.keySet().iterator();
			}
			while (true) {

				int j = 0;
				if (!localIterator.hasNext()) {

					localIterator = this.indiciesToKill.iterator();
					while (true) {

						if (!localIterator.hasNext()) {
							int m = 0;
							if ((this.startIndex >= 0) && (this.endIndex >= 0))
								j = 1 + (this.endIndex - this.startIndex);
							for (int i = 0;; i++) {
								if (i >= j) {
									setupLayers();
									if (m != 0) {
										// SlidingDetailCards.access$802(
										// SlidingDetailCards.this, true);
									}
									return;
								}
								int k = i + this.startIndex;
								if (!this.indexToView.containsKey(Integer
										.valueOf(k))) {
									ViewType localView = (ViewType) SlidingDetailCards.this.adapter
											.getView(
													k,
													(View) SlidingDetailCards.this.recycledViews
															.poll(),
													SlidingDetailCards.this);
									initializeNewView(k, localView);
									SlidingDetailCards.this.addView(localView);
									// n = 1;
									m = 1;
								} else {
									if (SlidingDetailCards.this.adapter
											.getItemId(k) == ((PositionedCard) this.indexToView
											.get(Integer.valueOf(k))).id)
										continue;
									initializeNewView(
											k,
											SlidingDetailCards.this.adapter
													.getView(
															k,
															((PositionedCard) this.indexToView
																	.get(Integer
																			.valueOf(k))).view,
															SlidingDetailCards.this));
									// n = 1;
									m = 1;
								}
								if ((i <= 0) || (i > this.maskingViews.length))
									continue;
								k = i - 1;
								if (this.maskingViews[k] != null)
									continue;
								// this.maskingViews[k] = SlidingDetailCards
								// .access$200(SlidingDetailCards.this)
								// .getMaskingView(k + 1);
								this.maskingViews[k] = SlidingDetailCards.this.adapter
										.getMaskingView(k + 1);
								if (this.maskingViews[k] == null)
									continue;
								this.maskingViews[k].setPivotY(0.0F);
								this.maskingViews[k].setLayerType(2, null);
								SlidingDetailCards.this
										.addView(this.maskingViews[k]);
								// int n = 1;
								m = 1;
							}
						}
						Integer localInteger = (Integer) localIterator.next();
						ViewType localObject = ((PositionedCard) this.indexToView
								.get(localInteger)).view;
						SlidingDetailCards.this.removeView((View) localObject);
						SlidingDetailCards.this.recycledViews.add(localObject);
						this.indexToView.remove(localInteger);
					}
				}
				Object localObject = (Integer) localIterator.next();
				if ((((Integer) localObject).intValue() >= this.startIndex)
						&& (((Integer) localObject).intValue() <= this.endIndex))
					continue;
				this.indiciesToKill.add((Integer) localObject);
			}
		}

		private void initializeNewView(int paramInt, ViewType paramViewType) {
			this.indexToView
					.put(Integer.valueOf(paramInt),
							new PositionedCard(paramViewType,
									SlidingDetailCards.this.adapter
											.getItemId(paramInt)));
			if (paramInt != this.startIndex)
				((SlideableView) paramViewType).setSlidingAlpha(1.0F);
			else
				((SlideableView) paramViewType)
						.setSlidingAlpha(SlidingDetailCards.this.currentAlpha);
			paramViewType.setTranslationY(0.0F);
			paramViewType.setScaleX(1.0F);
			paramViewType.setScaleY(1.0F);
			paramViewType.setPivotY(0.0F);
			paramViewType.setLayerType(2, null);
		}

		private void setupLayers() {
			if (!this.indexToView.isEmpty())
				;
			for (int i = this.endIndex;; i--) {
				if ((this.startIndex < 0) || (this.endIndex < 0)
						|| (i < this.startIndex)) {
					SlidingDetailCards.this
							.bringChildToFront(SlidingDetailCards.this.detailView);
					return;
				}
				View localView = ((PositionedCard) this.indexToView.get(Integer
						.valueOf(i))).view;
				SlidingDetailCards.this.bringChildToFront(localView);
				if (getMaskingCardForIndex(i) == null)
					continue;
				SlidingDetailCards.this
						.bringChildToFront(getMaskingCardForIndex(i));
			}
		}

		public ViewType getCardAtIndex(int paramInt) {
			return ((PositionedCard) this.indexToView.get(Integer
					.valueOf(paramInt))).view;
		}

		public int getCurrentIndex() {
			return this.currentIndex;
		}

		public View getMaskingCardForIndex(int paramInt) {
			View localView;
			if ((this.startIndex < 0) || (this.endIndex < 0)
					|| (paramInt <= this.startIndex)
					|| (paramInt > this.endIndex)
					|| (paramInt > this.startIndex + this.maskingViews.length))
				localView = null;
			else
				localView = this.maskingViews[(-1 + (paramInt - this.startIndex))];
			return localView;
		}

		public int getVisibleEnd() {
			return this.endIndex;
		}

		public int getVisibleStart() {
			return this.startIndex;
		}

		public void hideMaskingViews() {
			int j = this.maskingViews.length;
			for (int i = 0;; i++) {
				if (i >= j)
					return;
				if (this.maskingViews[i] == null)
					continue;
				this.maskingViews[i].setVisibility(8);
			}
		}

		public void precalculateClosedTranslations() {
			SlidingDetailCards.this.translations.clear();
			int i = SlidingDetailCards.this.cards.getVisibleStart();
			int k = SlidingDetailCards.this.cards.getVisibleEnd();
			SlidingDetailCards.ClosedTranslation localClosedTranslation1 = new SlidingDetailCards.ClosedTranslation(
					SlidingDetailCards.this);
			localClosedTranslation1.yTranslationStart = (2 * SlidingDetailCards.this.yBumpPerObj);
			localClosedTranslation1.xTranslationStart = (getCenterForCurrentPage()
					- SlidingDetailCards.this.detailView.getMeasuredWidth() / 2 - SlidingDetailCards.this
					.getDetailViewLeft());
			localClosedTranslation1.scaleFactor = 1.0F;
			localClosedTranslation1.movingView = SlidingDetailCards.this.detailView;
			localClosedTranslation1.maskingView = null;
			SlidingDetailCards.this.translations
					.add(0, localClosedTranslation1);
			if (SlidingDetailCards.this.cards.getCurrentIndex() >= 0)
				k = 1 + (k - i);
			for (int j = 0;; j++) {
				if (j >= k)
					return;
				View localView1 = SlidingDetailCards.this.cards
						.getCardAtIndex(i + j);
				int n = SlidingDetailCards.this.getLeftEdgeOfIndex(i + j);
				int m = (2 - j) * SlidingDetailCards.this.yBumpPerObj;
				float f = 1.0F - 0.1F * j;
				int i1 = getCenterForCurrentPage()
						- SlidingDetailCards.this.cardWidth
						/ 2
						+ (int) (SlidingDetailCards.this.cardWidth * (1.0F - f) / 2.0F)
						- n;
				View localView2 = getMaskingCardForIndex(i + j);
				SlidingDetailCards.ClosedTranslation localClosedTranslation2 = new SlidingDetailCards.ClosedTranslation(
						SlidingDetailCards.this);
				localClosedTranslation2.xTranslationStart = i1;
				localClosedTranslation2.yTranslationStart = m;
				localClosedTranslation2.scaleFactor = f;
				localClosedTranslation2.movingView = localView1;
				localClosedTranslation2.maskingView = localView2;
				SlidingDetailCards.this.translations.add(j + 1,
						localClosedTranslation2);
			}
		}

		public void setCurrentIndex(int paramInt) {
			int i = -1;
			this.currentIndex = paramInt;
			int j;
			if (this.currentIndex >= 0)
				j = Math.max(
						0,
						Math.min(
								-2
										+ (-1 + SlidingDetailCards.this.adapter
												.getCount()), -1
										+ this.currentIndex));
			else
				j = i;
			this.startIndex = j;
			if (this.currentIndex >= 0)
				i = Math.min(-1 + SlidingDetailCards.this.adapter.getCount(),
						2 + this.startIndex);
			this.endIndex = i;

			handlePotentiallyNewObjects();
			precalculateClosedTranslations();
		}

		public void showMaskingViews() {
			int j = this.maskingViews.length;
			for (int i = 0;; i++) {
				if (i >= j)
					return;
				if (this.maskingViews[i] == null)
					continue;
				this.maskingViews[i].setVisibility(0);
			}
		}

		private class PositionedCard {
			private final long id;
			private final ViewType view;

			public PositionedCard(ViewType view, long id) {
				this.view = view;
				this.id = id;
			}
		}
	}

}
