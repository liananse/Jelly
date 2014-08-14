package com.medialab.jelly.ui.widget;

import java.util.List;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.google.common.collect.Lists;

public abstract class SlidingDetailCardsAdapter<ObjectType, ViewType extends View>
		extends BaseAdapter {

	private final Context context;
	protected List<ObjectType> objects = Lists.newArrayList();

	private ObjectType startObject;

	public SlidingDetailCardsAdapter(Context paramContext) {
		this.context = paramContext;
	}

	protected abstract ViewType convertObjectToNewView(
			ObjectType paramObjectType, ViewGroup paramViewGroup);

	protected abstract void convertObjectToRecycledView(
			ObjectType paramObjectType, ViewGroup paramViewGroup,
			ViewType paramViewType);

	protected Context getContext() {
		return this.context;
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return this.objects.size();
	}

	protected abstract View getDetailView();

	public int getIndexForObject(ObjectType paramObjectType) {
		return this.objects.indexOf(paramObjectType);
	}

	@Override
	public ObjectType getItem(int position) {
		// TODO Auto-generated method stub
		return this.objects.get(position);
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return 0;
	}

	public abstract View getMaskingView(int paramInt);

	public ObjectType getStartObject() {
		return this.startObject;
	}

	@Override
	public ViewType getView(int paramInt, View paramView,
			ViewGroup paramViewGroup) {
		// TODO Auto-generated method stub
		ViewType localView;
		if (paramView != null) {
			convertObjectToRecycledView(getItem(paramInt), paramViewGroup,
					(ViewType) paramView);
			localView = (ViewType) paramView;
		} else {
			localView = convertObjectToNewView(getItem(paramInt),
					paramViewGroup);
		}
		return localView;
	}

	public boolean hasStableIds() {
		return true;
	}

	protected abstract boolean isExpandable();

	protected void refreshObjects(List<ObjectType> paramList) {
		this.objects.clear();
		this.objects.addAll(paramList);
		notifyDataSetChanged();
	}

	protected void setNewObjects(List<ObjectType> paramList,
			ObjectType paramObjectType) {
		this.objects.clear();
		this.objects.addAll(paramList);
		this.startObject = paramObjectType;
		notifyDataSetInvalidated();
	}

}
