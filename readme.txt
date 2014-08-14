// SharedPreferences 使用

// 保存数据到SharedPreferences
SharedPreferences.Editor mEditor = UTools.Storage.getSharedPreEditor(LoginActivity.this,UConstants.BASE_PREFS_NAME);
mEditor.putString(UConstants.SELF_USER_ID,String.valueOf(mModel.data.user.uid));
mEditor.commit();

// 从SharedPreferences获取数据
SharedPreferences sp = UTools.Storage.getSharedPreferences(LoginActivity.this, UConstants.BASE_PREFS_NAME);
String baidu_uid = sp.getString(UConstants.SELF_USER_ID, "");

// 友盟统计
// UMengEventID 友盟的事件Id
MobclickAgent.onEvent(mContext, UMengEventID.LOGIN_AND_REGISTER_VIEW_LOGIN);

HashMap<String,String> map = new HashMap<String,String>();
map.put("type","book");
map.put("quantity","3"); 
MobclickAgent.onEvent(mContext, "purchase", map);

// jelly.keystore
MD5: C6:8D:B3:E7:A9:8C:35:72:57:A1:11:FE:82:DA:7A:59
SHA1: F8:B0:96:08:EA:B3:C7:BD:71:F1:AA:C8:AC:94:99:80:6E:A8:04:F4

// 打包前确认
1、appVersion 包括manifest里面以及HHttpUtility里面
2、channel 包括manifest里面以及HHttpUtility里面

// FontManager.java 字体管理，可以使用多种字体显示文字
// UToast.java Toast提示放到这里
// UConfig.java 服务器地址及请求连接
// UConstants.java 用到的常量如sharedPreferences AppName Channel
// UDataCleanManager.java 清除数据缓存等，一般退出登录的时候使用，用来清理文件数据库及缓存中的数据
// UTools.java 工具类，获取一些基本信息都在这里
// UTools.Storage 里面是保存图片的地址 配合 UDataStorage 和UImageManager使用

// http包，网络请求
// model包，用到的model
// resultdatamodel，返回数据中对data的解析
// resultmodel，对返回数据的解析，包括result和message
// push包，推送用小米推送
// controller包，基础滑动ViewController
// viewController包，特定View的ViewController 比如感谢卡，问题，答案等

// UConfig,这里放网络请求的地址
// UConstants,这里放一些常量

// SquareRoundedImageView 正方形的圆形Imageview
// SquareImageView 正方形ImageView

// SimpleStackController 里面onShow方法暂时屏蔽掉显示答案的界面，在分享答案时返回有问题，后面看看有没有更好的解决方案或者屏蔽掉会不会出问题
   暂时修改了下QuestionViewController的setQuestionAndAnswer方法

用到的开源项目
1、afinal 用于图片加载
2、gson 用于json解析
3、rebound 用于滑动的弹簧动画，facebook开源项目
4、otto http://blog.chengyunfeng.com/?p=450 用于组件间通讯
5、guava http://www.ibm.com/developerworks/cn/java/j-lo-googlecollection/



