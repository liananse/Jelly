package com.medialab.jelly.model;

import java.io.Serializable;

public class AddFriendModel implements Serializable{
	
	public int uid;
	public String avatarName;
	public String nickName;
	/**
	 * 1 待审核--接受  2已申请--已申请  3待添加--添加
	 */
	public int type;
	public String from;
}
