package com.medialab.jelly.ui.view;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ListView;

import com.medialab.jelly.R;
import com.medialab.jelly.ui.drawable.NotificationDivderDrawable;

public class NotificationListView extends ListView{

	public NotificationListView(Context context) {
		super(context);
		setCacheColorHint(getResources().getColor(17170445));
	    setFadingEdgeLength(0);
	    setOverScrollMode(2);
	    setDivider(new NotificationDivderDrawable(context.getResources().getColor(R.color.notification_divider_color), getResources().getDimensionPixelSize(R.dimen.notification_left_to_picture)));
	    setDividerHeight(getResources().getDimensionPixelSize(R.dimen.notification_divider_height));
	}
	public NotificationListView(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
		setCacheColorHint(getResources().getColor(17170445));
	    setFadingEdgeLength(0);
	    setOverScrollMode(2);
	    setDivider(new NotificationDivderDrawable(context.getResources().getColor(R.color.notification_divider_color), getResources().getDimensionPixelSize(R.dimen.notification_left_to_picture)));
	    setDividerHeight(getResources().getDimensionPixelSize(R.dimen.notification_divider_height));
	}

}
