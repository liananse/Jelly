package com.medialab.jelly.fragment;

import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.medialab.jelly.R;

public class FLoadingProgressBarFragment extends DialogFragment{
	
	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		int style = DialogFragment.STYLE_NO_TITLE, theme = R.style.dialog;
		setStyle(style, theme);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		View mPanel = inflater.inflate(R.layout.dialog_fragment_loading_progressbar, container, false);
		
		// TODO Auto-generated method stub
		return mPanel;
	}
	
	

}
