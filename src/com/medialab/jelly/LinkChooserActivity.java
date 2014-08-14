package com.medialab.jelly;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.webkit.URLUtil;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.EditText;
import android.widget.TextView;

import com.medialab.jelly.util.KeyboardUtils;
import com.medialab.jelly.util.LinkUtils;

public class LinkChooserActivity extends BaseActivity implements OnClickListener,OnFocusChangeListener  {

	private WebView mLinkChooserWebView;
	private TextView mLinkChooserUse;
	private TextView mLinkChooserSearch;
	private EditText mLinkChooserEditText;
	private String url = "";
	
	private boolean autoOverrideUrlFlag = false;//是否是浏览器点击产生的跳转
	
//	private final static String QUICKSEARCH_G = "http://www.google.com/m?q=%s";
//	private final static String QUERY_PLACE_HOLDER = "%s";
	private final static String QUICKSEARCH_G = "http://www.baidu.com/s?wd=%s";
	private final static String QUERY_PLACE_HOLDER = "%s";
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.link_chooser);
		
		mLinkChooserWebView = (WebView) this.findViewById(R.id.link_chooser_content);
		mLinkChooserWebView.getSettings().setJavaScriptEnabled(true);
		
		// 创建WebViewClient对象
		WebViewClient wvc = new WebViewClient() {
			
			@Override
			public boolean shouldOverrideUrlLoading(WebView view, String url) {
				// 使用自己的WebView组件来响应Url加载事件，而不是使用默认浏览器加载页面
				autoOverrideUrlFlag = true;
				mLinkChooserEditText.setText(url);
				autoOverrideUrlFlag = false;
				
				mLinkChooserWebView.loadUrl(url);
				return true;
			}
			
			@Override
			public void onPageFinished(WebView view, String url) {
				super.onPageFinished(view, url);
				mLinkChooserSearch.setVisibility(View.GONE);//隐藏搜索按钮
				mLinkChooserUse.setVisibility(View.VISIBLE);//显示确定按钮
			}
			
			@Override
			public void onLoadResource(WebView view, String url) {
				super.onLoadResource(view, url);
			}
			
		};
		
		// 设置WebViewClient对象
		mLinkChooserWebView.setWebViewClient(wvc);
		
		mLinkChooserUse = (TextView) this.findViewById(R.id.link_chooser_use);
		mLinkChooserSearch = (TextView)this.findViewById(R.id.link_chooser_search);
		
		mLinkChooserUse.setOnClickListener(this);
		mLinkChooserSearch.setOnClickListener(this);
		
		mLinkChooserEditText = (EditText) this.findViewById(R.id.link_chooser_link);
	    mLinkChooserEditText.addTextChangedListener(watcher);
	    mLinkChooserEditText.setOnFocusChangeListener(this);
	}
	
	private TextWatcher watcher = new TextWatcher() {
        
		@Override    
		public void onTextChanged(CharSequence s, int start, int before,     
				int count) {     
			//内容变化时，转换搜索、确定按钮
			if(!autoOverrideUrlFlag){//除去浏览器点击自动跳转的情况
				mLinkChooserUse.setVisibility(View.GONE);
				mLinkChooserSearch.setVisibility(View.VISIBLE);
			}
			if(!mLinkChooserEditText.getText().toString().equals("")){
			}else{
			}
		}
		
        @Override    
        public void afterTextChanged(Editable s) {     
            // TODO Auto-generated method stub     
        }   
          
        @Override 
        public void beforeTextChanged(CharSequence s, int start, int count,  
                int after) {  
            // TODO Auto-generated method stub  
        }
    };  
	
	@Override
	public void onStart() {
		super.onStart();
	}
	
	/**
	 * 
	 * @param url
	 */
	private void loadWebContent(String u) {
		
		//判断输入内容是否是个网址，如果是网址，补全网址，如果不是网址的话调用google搜索
		boolean isUrl = LinkUtils.isUrl(u);
		if(isUrl){
			url = URLUtil.guessUrl(u);
		}else{
			url=URLUtil.composeSearchUrl(u, QUICKSEARCH_G, QUERY_PLACE_HOLDER);
		}
		mLinkChooserWebView.loadUrl(url);
		
		//链接变为当前页面的链接
		autoOverrideUrlFlag = true;
		mLinkChooserEditText.setText(url);
		autoOverrideUrlFlag = false;
	}
	
	private String getUrl(){
		return mLinkChooserEditText.getText().toString();
	}

	@Override
	public void onClick(View v) {
		switch(v.getId()){
		case R.id.link_chooser_use:
			returnUrl();
			this.finish();
			break;
		case R.id.link_chooser_search:
			url = mLinkChooserEditText.getText().toString();
			
			//隐藏键盘
			KeyboardUtils.hideKeyboard(this, mLinkChooserEditText);
	        
			mLinkChooserWebView.setVisibility(View.VISIBLE);
	        //加载网页
			loadWebContent(url);
			
			break;
		}
	}
	
	@Override
	public void onFocusChange(View v, boolean hasFocus) {
		switch(v.getId()){
		case R.id.link_chooser_link:
			if(mLinkChooserEditText.hasFocus() && !mLinkChooserEditText.getText().toString().equals("")){
			}else{
			}
			break;
		}
	}
	
	public void returnUrl() {
		Uri uri = Uri.parse(getUrl());
		Intent intent = new Intent();
		intent.setData(uri);
		LinkChooserActivity.this.setResult(RESULT_OK, intent);
	}

}
