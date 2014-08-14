package com.medialab.jelly.controller;

import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;

public abstract interface ViewController
{
  public abstract View getView();

  public abstract boolean onBackPressed();

  public abstract void onHide(ViewGroup paramViewGroup);

  public abstract void onShow(ViewGroup paramViewGroup);

  public abstract void resumeState(Bundle paramBundle);

  public abstract void saveState(Bundle paramBundle);
}
