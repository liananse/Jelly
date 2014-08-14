package com.medialab.jelly.ui.widget;

import android.view.View;

public abstract interface SlideableMainView
{
  public abstract void destroy();

  public abstract void resetMainViewClickListener();

  public abstract void setMainViewClickListener(View.OnClickListener paramOnClickListener);
}