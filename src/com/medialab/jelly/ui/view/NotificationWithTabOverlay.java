package com.medialab.jelly.ui.view;

import java.util.ArrayList;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Rect;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.medialab.jelly.R;
import com.medialab.jelly.SettingActivity;
import com.medialab.jelly.ui.notification.FriendsAddListFragment;
import com.medialab.jelly.ui.notification.FriendsFragment;
import com.medialab.jelly.ui.notification.NotificationFragment;
import com.medialab.jelly.ui.notification.NotificationFragmentAdapter;
import com.medialab.jelly.util.FontManager;
import com.medialab.jelly.util.UConstants;
import com.medialab.jelly.util.UTools;
import com.medialab.jelly.util.view.UMengEventID;
import com.squareup.otto.Bus;
import com.umeng.analytics.MobclickAgent;

public class NotificationWithTabOverlay extends ViewGroup{

	private final Rect borderPadding = new Rect();
	private final View settingsRow;
	private final int settingsRowHeight;
	private final TextView settingsRowText;
	private final int topNavButtonHeight;
	private final int topNavLeftPadding;
	private final ImageView topTriangle;
	private final int topTriangleHeight;
	private final int topTriangleWidth;
	
	private final LinearLayout overlayLinearLayout;

	private ViewPager mViewPager;
	private ArrayList<Fragment> fragmentsList;
	
	public NotificationWithTabOverlay(Bus paramBus, Context paramContext) {
		super(paramContext);
		// TODO Auto-generated constructor stub
		this.overlayLinearLayout = ((LinearLayout) LinearLayout.class
				 .cast(((LayoutInflater) paramContext
						 .getSystemService("layout_inflater")).inflate(
						 R.layout.notification_with_friends_overlay, this, false)));
		
		mViewPager = (ViewPager) overlayLinearLayout.findViewById(R.id.pager);
		fragmentsList = new ArrayList<Fragment>();

		Fragment notificationFragment = new NotificationFragment(paramBus);
		Fragment friendsAddListFragment = new FriendsAddListFragment(paramBus);
		Fragment friendsFragment = new FriendsFragment(paramBus);
		
		fragmentsList.add(notificationFragment);
		fragmentsList.add(friendsFragment);
		fragmentsList.add(friendsAddListFragment);
		
		mViewPager.setAdapter(new NotificationFragmentAdapter(((FragmentActivity)paramContext).getSupportFragmentManager(), fragmentsList));
		mViewPager.setCurrentItem(0);
		mViewPager.setOffscreenPageLimit(3);
		mViewPager.setOnPageChangeListener(new MyOnPageChangeListener());
		
		initView(this.overlayLinearLayout);
		
		this.settingsRow = ((LinearLayout) LinearLayout.class
				.cast(((LayoutInflater) paramContext
						.getSystemService("layout_inflater")).inflate(
						R.layout.notifications_settings_row, this, false)));
		((ImageView) ImageView.class.cast(this.settingsRow
				.findViewById(R.id.settings_icon)))
				.setOnClickListener(new View.OnClickListener() {
					public void onClick(View paramView) {
						Intent localIntent = new Intent();
						localIntent.setClass(getContext(),
								SettingActivity.class);
						getContext().startActivity(localIntent);
						
						MobclickAgent.onEvent(getContext(), UMengEventID.NOTIFICATION_SETTING);
					}
				});
		this.settingsRowText = ((TextView) TextView.class.cast(this.settingsRow
				.findViewById(R.id.notifications_settings_row_text)));
		FontManager.setTypeface(this.settingsRowText, FontManager.Weight.HUAKANG);
		this.settingsRowText.setTextColor(paramContext.getResources().getColor(
				R.color.thank_card_color));
		this.settingsRowHeight = paramContext.getResources()
				.getDimensionPixelSize(R.dimen.settings_row_height);
		this.topTriangleWidth = getResources().getDimensionPixelSize(
				R.dimen.notification_triangle_popover_width);
		this.topTriangleHeight = (this.topTriangleWidth / 2);
		this.topNavLeftPadding = getResources().getDimensionPixelSize(
				R.dimen.top_nav_left_right_padding);
		this.topNavButtonHeight = getResources().getDimensionPixelSize(
				R.dimen.top_nav_button_height);
		this.topTriangle = new ImageView(paramContext);
		this.topTriangle
				.setImageResource(R.drawable.notification_popover_triangle);
		this.topTriangle.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
		addView(overlayLinearLayout);
		addView(this.settingsRow);
		addView(this.topTriangle);
	}
	
	public class MyOnClickListener implements View.OnClickListener {
        private int index = 0;

        public MyOnClickListener(int i) {
            index = i;
        }

        @Override
        public void onClick(View v) {
            mViewPager.setCurrentItem(index);
        }
    };
    
    public class MyOnPageChangeListener implements OnPageChangeListener {

        @Override
        public void onPageSelected(int arg0) {
            switch (arg0) {
            case 0:
            	mNumOfActivity.setImageResource(R.drawable.action_list_icon);
            	mActivityTitle.setTextColor(getResources().getColor(R.color.white));
            	
            	mNumOfNewFriend.setTextColor(getResources().getColor(R.color.activity_title_unselected));
            	mNewFriendTitle.setTextColor(getResources().getColor(R.color.activity_title_unselected));
            	
            	mNumOfFriend.setTextColor(getResources().getColor(R.color.activity_title_unselected));
            	mFriendTitle.setTextColor(getResources().getColor(R.color.activity_title_unselected));
            	
            	if (mActivityUnReadTips.getVisibility() == View.VISIBLE) {
            		((NotificationFragment) ((NotificationFragmentAdapter) mViewPager.getAdapter()).getItem(0)).refreshMethod();
            	}
                break;
            case 2:
            	mNumOfActivity.setImageResource(R.drawable.action_list_unselect_icon);
            	mActivityTitle.setTextColor(getResources().getColor(R.color.activity_title_unselected));
            	
            	mNumOfNewFriend.setTextColor(getResources().getColor(R.color.white));
            	mNewFriendTitle.setTextColor(getResources().getColor(R.color.white));
            	
            	mNumOfFriend.setTextColor(getResources().getColor(R.color.activity_title_unselected));
            	mFriendTitle.setTextColor(getResources().getColor(R.color.activity_title_unselected));
            	
            	if (mNewFriendUnReadTips.getVisibility() == View.VISIBLE) {
            		((FriendsAddListFragment) ((NotificationFragmentAdapter) mViewPager.getAdapter()).getItem(2)).refreshMethod();
            	}
                break;
            case 1:
            	mNumOfActivity.setImageResource(R.drawable.action_list_unselect_icon);
            	mActivityTitle.setTextColor(getResources().getColor(R.color.activity_title_unselected));
            	
            	mNumOfNewFriend.setTextColor(getResources().getColor(R.color.activity_title_unselected));
            	mNewFriendTitle.setTextColor(getResources().getColor(R.color.activity_title_unselected));
            	
            	mNumOfFriend.setTextColor(getResources().getColor(R.color.white));
            	mFriendTitle.setTextColor(getResources().getColor(R.color.white));
            	
            	if (mFriendUnReadTips.getVisibility() == View.VISIBLE) {
            		((FriendsFragment) ((NotificationFragmentAdapter) mViewPager.getAdapter()).getItem(1)).refreshMethod();
            	}
                break;
            }
        }

        @Override
        public void onPageScrolled(int arg0, float arg1, int arg2) {
        }

        @Override
        public void onPageScrollStateChanged(int arg0) {
        }
    }
    
	
	private View mFriendsWhoAddYouTab;
	private View mAddContactsFriendsTab;
	private View mFriendsTab;
	
	private ImageView mNumOfActivity;
	private TextView mActivityTitle;
	
	private TextView mNumOfNewFriend;
	private TextView mNewFriendTitle;
	
	private TextView mNumOfFriend;
	private TextView mFriendTitle;
	
	private int largeTextSize;
	private int mediumTextSize;
	
	private ImageView mActivityUnReadTips;
	private ImageView mNewFriendUnReadTips;
	private ImageView mFriendUnReadTips;
	
	private void initView(View view)
	{
		SharedPreferences sp = UTools.Storage.getSharedPreferences(getContext(), UConstants.BASE_PREFS_NAME);
		int numOfNewFriend = sp.getInt(UConstants.LOCAL_NUM_OF_NEW_FRIEND, 0);
		int numOfFriend = sp.getInt(UConstants.LOCAL_NUM_OF_FRIEND, 0);
		
		largeTextSize = getContext().getResources().getDimensionPixelSize(R.dimen.large_text_size);
		mediumTextSize = getContext().getResources().getDimensionPixelOffset(R.dimen.medium_text_size);
		
		mActivityUnReadTips = (ImageView) view.findViewById(R.id.activity_unread_tips);
		mNewFriendUnReadTips = (ImageView) view.findViewById(R.id.new_friends_unread_tips);
		mFriendUnReadTips = (ImageView) view.findViewById(R.id.friends_unread_tips);
		
		mFriendsWhoAddYouTab = view.findViewById(R.id.friends_who_add_you_tab);
		mAddContactsFriendsTab = view.findViewById(R.id.add_contact_friends_tab);
		mFriendsTab = view.findViewById(R.id.friends_tab);
		
		mNumOfActivity = (ImageView) view.findViewById(R.id.num_of_activity);
		mActivityTitle = (TextView) view.findViewById(R.id.activity_tab_title);
		
		mActivityTitle.setTextSize(0, mediumTextSize);
		
		mActivityTitle.setText(getResources().getText(R.string.notification_tab_activity));
		
		FontManager.setTypeface(mActivityTitle, FontManager.Weight.HUAKANG);
		
		mNumOfNewFriend = (TextView) view.findViewById(R.id.num_of_new_friends);
		mNewFriendTitle = (TextView) view.findViewById(R.id.new_friends_tab_title);
		
		mNumOfNewFriend.setTextSize(0, largeTextSize);
		mNewFriendTitle.setTextSize(0, mediumTextSize);
		
		mNumOfNewFriend.setText("" + numOfNewFriend);
		mNewFriendTitle.setText(getResources().getText(R.string.notification_tab_new_friend));
		
		FontManager.setTypeface(mNumOfNewFriend, FontManager.Weight.HUAKANG);
		FontManager.setTypeface(mNewFriendTitle, FontManager.Weight.HUAKANG);
		
		mNumOfFriend = (TextView) view.findViewById(R.id.num_of_friends);
		mFriendTitle = (TextView) view.findViewById(R.id.friends_tab_title);
		
		mNumOfFriend.setTextSize(0, largeTextSize);
		mFriendTitle.setTextSize(0, mediumTextSize);
		
		mNumOfFriend.setText("" + numOfFriend);
		mFriendTitle.setText(getResources().getText(R.string.notification_tab_friend));
		
		FontManager.setTypeface(mNumOfFriend, FontManager.Weight.HUAKANG);
		FontManager.setTypeface(mFriendTitle, FontManager.Weight.HUAKANG);
		
		mNumOfActivity.setImageResource(R.drawable.action_list_icon);
    	mActivityTitle.setTextColor(getResources().getColor(R.color.white));
    	
    	mNumOfNewFriend.setTextColor(getResources().getColor(R.color.activity_title_unselected));
    	mNewFriendTitle.setTextColor(getResources().getColor(R.color.activity_title_unselected));
    	
    	mNumOfFriend.setTextColor(getResources().getColor(R.color.activity_title_unselected));
    	mFriendTitle.setTextColor(getResources().getColor(R.color.activity_title_unselected));
    	
		mFriendsWhoAddYouTab.setOnClickListener(new MyOnClickListener(0));
		mAddContactsFriendsTab.setOnClickListener(new MyOnClickListener(2));
		mFriendsTab.setOnClickListener(new MyOnClickListener(1));
	}

	public int getMeasuredListviewSize() {
		return this.overlayLinearLayout.getMeasuredHeight();
	}

	@Override
	protected void onLayout(boolean changed, int paramInt1, int paramInt2,
			int paramInt3, int paramInt4) {
		// TODO Auto-generated method stub
		int i = paramInt4 - paramInt2;
		int j = paramInt3 - paramInt1;
		this.settingsRow.layout(0, i - this.settingsRowHeight, j, i);
		i = this.topTriangle.getMeasuredHeight();
		int k = i + this.overlayLinearLayout.getMeasuredHeight();
		this.overlayLinearLayout.layout(0, i, j, k);
		k = this.topTriangle.getMeasuredWidth();
		j = this.topNavButtonHeight / 2 + this.topNavLeftPadding - k / 2;
		k = j + k;
		if (this.overlayLinearLayout.getBackground() != null)
			this.overlayLinearLayout.getBackground().getPadding(this.borderPadding);
		i += this.borderPadding.top;
		this.topTriangle.layout(j, 0 + this.borderPadding.top, k,
				this.topTriangle.getMeasuredHeight() + this.borderPadding.top);
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		// TODO Auto-generated method stub
		int j = View.MeasureSpec.getSize(widthMeasureSpec);
		int i = View.MeasureSpec.getSize(heightMeasureSpec)
				- this.topTriangle.getMeasuredHeight();
		j = View.MeasureSpec.makeMeasureSpec(j, MeasureSpec.EXACTLY);
		this.topTriangle.measure(View.MeasureSpec.makeMeasureSpec(
				this.topTriangleWidth, MeasureSpec.EXACTLY), View.MeasureSpec
				.makeMeasureSpec(this.topTriangleHeight, MeasureSpec.EXACTLY));
		this.settingsRow.measure(j, View.MeasureSpec.makeMeasureSpec(
				this.settingsRowHeight, MeasureSpec.EXACTLY));
		this.overlayLinearLayout.measure(j, View.MeasureSpec.makeMeasureSpec(i
				- this.settingsRowHeight, MeasureSpec.EXACTLY));
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
	}

	public void setNumberOfThanks(int numOfThanks, int numOfNewFriend, int numOfFriend) {
		TextView localTextView = this.settingsRowText;
		Resources localResources = getResources();
		int i = R.plurals.number_of_thank_you_cards;
		Object[] arrayOfObject = new Object[1];
		arrayOfObject[0] = Integer.valueOf(numOfThanks);
		localTextView.setText(localResources.getQuantityString(i, numOfThanks,
				arrayOfObject));
		
		mNumOfNewFriend.setText(numOfNewFriend + "");
		mNumOfFriend.setText(numOfFriend + "");
		
		SharedPreferences.Editor mEditor = UTools.Storage.getSharedPreEditor(getContext(),UConstants.BASE_PREFS_NAME);
		mEditor.putInt(UConstants.LOCAL_NUM_OF_NEW_FRIEND, numOfNewFriend);
		mEditor.putInt(UConstants.LOCAL_NUM_OF_FRIEND, numOfFriend);
		mEditor.commit();
	}
	
	public void setNewActivtiyFriednTips(boolean hasNewActivity, boolean hasNewFriend, boolean hasFriend) {
		if (hasNewActivity) {
			mActivityUnReadTips.setVisibility(View.VISIBLE);
		} else {
			mActivityUnReadTips.setVisibility(View.INVISIBLE);
		}
		
		if (hasNewFriend) {
			mNewFriendUnReadTips.setVisibility(View.VISIBLE);
		} else {
			mNewFriendUnReadTips.setVisibility(View.INVISIBLE);
		}
		
		if (hasFriend) {
			mFriendUnReadTips.setVisibility(View.VISIBLE);
		} else {
			mFriendUnReadTips.setVisibility(View.INVISIBLE);
		}
	}
	
	public void setNumberOfFriendAndNewFriend(int numOfNewFriend, int numOfFriend) {
		mNumOfNewFriend.setText(numOfNewFriend + "");
		mNumOfFriend.setText(numOfFriend + "");
	}

}
