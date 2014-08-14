package com.medialab.jelly.util.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputConnection;
import android.widget.EditText;

public class NoScrollEditText extends EditText {
	public NoScrollEditText(Context paramContext) {
		super(paramContext);
	}

	public NoScrollEditText(Context paramContext, AttributeSet paramAttributeSet) {
		super(paramContext, paramAttributeSet);
	}

	public NoScrollEditText(Context paramContext,
			AttributeSet paramAttributeSet, int paramInt) {
		super(paramContext, paramAttributeSet, paramInt);
	}

	public InputConnection onCreateInputConnection(EditorInfo paramEditorInfo) {
		InputConnection localInputConnection = super
				.onCreateInputConnection(paramEditorInfo);
		int i = 0xFF & paramEditorInfo.imeOptions;
		if ((i & 0x6) != 0) {
			paramEditorInfo.imeOptions = (i ^ paramEditorInfo.imeOptions);
			paramEditorInfo.imeOptions = (0x6 | paramEditorInfo.imeOptions);
		}
		if ((0x40000000 & paramEditorInfo.imeOptions) != 0)
			paramEditorInfo.imeOptions = (0xBFFFFFFF & paramEditorInfo.imeOptions);
		return localInputConnection;
	}
}