package com.medialab.jelly.util;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.sourceforge.pinyin4j.PinyinHelper;
import net.sourceforge.pinyin4j.format.HanyuPinyinCaseType;
import net.sourceforge.pinyin4j.format.HanyuPinyinOutputFormat;
import net.sourceforge.pinyin4j.format.HanyuPinyinToneType;
import net.sourceforge.pinyin4j.format.HanyuPinyinVCharType;
import net.sourceforge.pinyin4j.format.exception.BadHanyuPinyinOutputFormatCombination;

import org.json.JSONObject;

import android.content.Context;

import com.medialab.jelly.R;

public class JellyTextUtils {

	public static String computeHowLongAgo(Context ctx, long time) {

		Calendar nowCalendar = Calendar.getInstance();
		nowCalendar.setTime(new Date());
		int nowYear = nowCalendar.get(Calendar.YEAR);
		int nowDay = nowCalendar.get(Calendar.DAY_OF_YEAR);
		int nowMinutes = nowCalendar.get(Calendar.MINUTE);
		int nowHour = nowCalendar.get(Calendar.HOUR_OF_DAY);

		Calendar timeCalendar = Calendar.getInstance();
		timeCalendar.setTimeInMillis(time);
		int beforeYear = timeCalendar.get(Calendar.YEAR);
		int beforeDay = timeCalendar.get(Calendar.DAY_OF_YEAR);
		int beforeHour = timeCalendar.get(Calendar.HOUR_OF_DAY);
		int beforeMinutes = timeCalendar.get(Calendar.MINUTE);

		if (timeCalendar.after(nowCalendar))
			return ctx.getString(R.string.now);

		int interYear = nowYear - beforeYear;
		if (interYear > 0) {
			return ctx.getString(
					R.string.days_ago,
					numerical(0, nowDay, beforeDay, nowYear, beforeYear,
							timeCalendar));
		} else {
			int interval = nowDay - beforeDay;
			if (interval > 1) {
				return ctx.getString(R.string.days_ago, interval);
			} else if (interval == 1) {
				return ctx.getString(R.string.yesterday);
			} else {
				int interHour = nowHour - beforeHour;
				if (interHour > 0) {
					if (interHour > 1) {
						return ctx.getString(R.string.hours_ago, interHour);
					} else {
						if ((60 - beforeMinutes) + nowMinutes > 60) {
							return ctx.getString(R.string.hours_ago, 1);
						} else {
							return ctx
									.getString(R.string.minutes_ago, (60 - beforeMinutes + nowMinutes));
						}
					}
				} else {
					int interMinutes = nowMinutes - beforeMinutes;

					if (interMinutes > 0) {
						return ctx
								.getString(R.string.minutes_ago, interMinutes);
					} else {
						return ctx.getString(R.string.now);
					}
				}
			}
		}
	}
	
	public static int numerical(int maxDays, int d1, int d2, int y1, int y2,
			Calendar calendar) {
		int day = d1 - d2;
		int betweenYears = y1 - y2;
		List<Integer> d366 = new ArrayList<Integer>();

		if (calendar.getActualMaximum(Calendar.DAY_OF_YEAR) == 366) {
			day += 1;
		}

		for (int i = 0; i < betweenYears; i++) {
			// 当年 + 1 设置下一年中有多少天
			calendar.set(Calendar.YEAR, (calendar.get(Calendar.YEAR)) + 1);
			maxDays = calendar.getActualMaximum(Calendar.DAY_OF_YEAR);
			// 第一个 366 天不用 + 1 将所有366记录，先不进行加入然后再少加一个
			if (maxDays != 366) {
				day += maxDays;
			} else {
				d366.add(maxDays);
			}
			// 如果最后一个 maxDays 等于366 day - 1
			if (i == betweenYears - 1 && betweenYears > 1 && maxDays == 366) {
				day -= 1;
			}
		}

		for (int i = 0; i < d366.size(); i++) {
			// 一个或一个以上的366天
			if (d366.size() >= 1) {
				day += d366.get(i);
			}
		}
		return day;
	}

	public static String getPingYin(String src) {
		if (src == null || src.equals("")) {
			return "";
		}
		char[] t1 = null;
		t1 = src.toCharArray();
		String[] t2 = new String[t1.length];
		HanyuPinyinOutputFormat t3 = new HanyuPinyinOutputFormat();
		t3.setCaseType(HanyuPinyinCaseType.LOWERCASE);
		t3.setToneType(HanyuPinyinToneType.WITHOUT_TONE);
		t3.setVCharType(HanyuPinyinVCharType.WITH_V);
		String t4 = "";
		int t0 = t1.length;
		try {
			for (int i = 0; i < t0; i++) {
				// 判断是否为汉字字�?
				if (java.lang.Character.toString(t1[i]).matches(
						"[\\u4E00-\\u9FA5]+")) {
					t2 = PinyinHelper.toHanyuPinyinStringArray(t1[i], t3);
					if (t2 == null) {
						return "";
					}
					t4 += t2[0];
				} else {
					t4 += java.lang.Character.toString(t1[i]);
				}
			}
			return t4;
		} catch (BadHanyuPinyinOutputFormatCombination e1) {
			e1.printStackTrace();
		}
		return t4;
	}

	// 返回中文的首字母
	public static String getPinYinHeadChar(String str) {
		if (str == null || str.equals("")) {
			return "";
		}
		String convert = "";
		for (int j = 0; j < str.length(); j++) {
			char word = str.charAt(j);
			String[] pinyinArray = PinyinHelper.toHanyuPinyinStringArray(word);
			if (pinyinArray != null) {
				convert += pinyinArray[0].charAt(0);
				break;
			} else {
				convert += word;
				break;
			}
		}
		return convert.toUpperCase();
	}

	public static boolean isNormalName(String name) {
		Pattern p = Pattern.compile("([A-Za-z0-9\u4E00-\u9FA5]+)");
		Matcher m = p.matcher(name);
		if (m.matches()) {
			return true;
		} else {
			return false;
		}
	}

	public static boolean isSuccess(JSONObject json) {
		String result = json.optString("result", "");
		if (result.equals("success")) {
			return true;
		} else {
			return false;
		}
	}

}
