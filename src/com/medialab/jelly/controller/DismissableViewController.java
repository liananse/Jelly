package com.medialab.jelly.controller;

import android.view.ViewGroup;

public abstract interface DismissableViewController extends ViewController
{
  public abstract boolean canBeDismissed();

  public abstract void onStartDragging();

  public abstract void onStopDragging();

  public abstract void reset(ViewGroup paramViewGroup);
}
