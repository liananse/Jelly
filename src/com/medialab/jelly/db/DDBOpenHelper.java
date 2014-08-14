package com.medialab.jelly.db;

import java.lang.ref.SoftReference;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.text.TextUtils;

import com.medialab.jelly.model.JellyUser;
import com.medialab.jelly.model.Notification;

public class DDBOpenHelper extends SQLiteOpenHelper {

	public static final String DATABASE_NAME = "jelly.db";
	public static final int DATABASE_VERSION = 1;
	public static final String USER_TABLE_NAME = "user_info";
	public static final String NOTIFICATION_TABLE_NAME = "notification_list";

	public DDBOpenHelper(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	private static DDBOpenHelper instance;

	public final static DDBOpenHelper getInstance(Context context) {
		return (instance == null) ? new DDBOpenHelper(context) : instance;
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		// TODO Auto-generated method stub
		db.execSQL(getCreateTable(JellyUser.class, USER_TABLE_NAME, "uid", true));
		db.execSQL(getCreateTable(Notification.class, NOTIFICATION_TABLE_NAME, "activityId", true));
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// TODO Auto-generated method stub

	}

	
	/**
	 * 插入单个对象类
	 * 
	 * @param o
	 *            对象
	 * @param tableName
	 *            是否需要额外添加表明
	 */
	public void insertOnlyClassData(Object o, String tableName) {
		if (o == null) {
			return;
		}
		SQLiteDatabase w = getWritableDatabase();
		w.beginTransaction();
		ContentValues cv = new ContentValues();
		Class<?> classzz = o.getClass();
		if (TextUtils.isEmpty(tableName)) {
			tableName = classzz.getSimpleName();
		}
		Field[] field = classzz.getDeclaredFields();
		String typeName;
		for (Field f : field) {
			typeName = f.getName();
			if ("java.lang.String".contains(f.getType().getName())
					|| "int".contains(f.getType().getName())
					|| "long".contains(f.getType().getName())) {
				cv.put(typeName,
						String.valueOf(getFieldValueByName(typeName, o)));
			} else if ("boolean".contains(f.getType().getName())) {
				boolean bool = (Boolean) getFieldValueByName(typeName, o);
				cv.put(typeName, bool);
			} else {
				cv.put(typeName, SerializableInterface
						.serialize(getFieldValueByName(typeName, o)));
			}
		}
		w.replace(tableName, null, cv);
		w.setTransactionSuccessful();
		w.endTransaction();
	}
	
	/**
	 * @param list
	 * @param tableName
	 */
	public <T> void insertData(List<T> list, String tableName) {

		SQLiteDatabase w = getWritableDatabase();
		w.beginTransaction();
		ContentValues cv;
		for (Object o : list) {
			cv = new ContentValues();
			Class<?> classzz = o.getClass();
			if (TextUtils.isEmpty(tableName)) {
				tableName = classzz.getSimpleName();
			}
			Field[] field = classzz.getDeclaredFields();
			String typeName;
			for (Field f : field) {
				typeName = f.getName();
				if ("java.lang.String".contains(f.getType().getName())
						|| "int".contains(f.getType().getName())
						|| "long".contains(f.getType().getName())) {
					cv.put(typeName,
							String.valueOf(getFieldValueByName(typeName, o)));
				} else if ("boolean".contains(f.getType().getName())) {
					boolean bool = (Boolean) getFieldValueByName(typeName, o);
					cv.put(typeName, bool);
				} else {
					cv.put(typeName, SerializableInterface
							.serialize(getFieldValueByName(typeName, o)));
				}
			}
//			w.replace(tableName, null, cv);
			try {
				w.insertOrThrow(tableName, null, cv);
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		w.setTransactionSuccessful();
		w.endTransaction();
	}
	
	/**
	 * 查询数据
	 * 
	 * @param columns
	 * @param selection
	 * @param selectionArgs
	 * @param orderBy
	 * @return
	 */
	public List<Object> query(String table_name, Class<?> cls,
			String selection, String orderBy, String groupBy) {

		Field[] field = cls.getDeclaredFields();
		int N = field.length;
		String[] columns = new String[N];
		for (int i = 0; i < N; i++) {
			columns[i] = field[i].getName();
		}
		List<Object> array = null;
		SQLiteDatabase read = getReadableDatabase();
		if (!read.isOpen()) {
			read = getReadableDatabase();
		}
		Cursor cursor = null;
		if (TextUtils.isEmpty(table_name)) {
			table_name = cls.getSimpleName();
		}
		cursor = read.query(table_name, columns, selection, null, groupBy,
				null, orderBy);
		if (cursor == null) {
			// read.close();
		} else {
			String[] ss = cursor.getColumnNames();
			Object object = null;
			array = new ArrayList<Object>();
			while (cursor.moveToNext()) {
				try {
					object = cls.newInstance();
					Object o = null;
					for (String s : ss) {
						Class<?> classzz = cls.getDeclaredField(s).getType();
						if ("java.lang.String".contains(classzz.getName())) {
							o = cursor.getString(cursor.getColumnIndex(s));
						} else if ("int".contains(classzz.getName())) {
							o = cursor.getInt(cursor.getColumnIndex(s));
						} else if ("long".contains(classzz.getName())) {
							o = cursor.getLong(cursor.getColumnIndex(s));
						} else if ("boolean".contains(classzz.getName())) {
							long boolLong = cursor.getLong(cursor
									.getColumnIndex(s));
							if (boolLong == 0) {
								o = false;
							} else {
								o = true;
							}
						} else {
							byte[] bytes = cursor.getBlob(cursor
									.getColumnIndex(s));
							o = SerializableInterface.deserialize(bytes);
						}
						setter(object, updateFrist(s), o, classzz);
					}
					array.add(object);
				} catch (InstantiationException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IllegalAccessException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (NoSuchFieldException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			}
			cursor.close();
		}

		return array;
	}
	
	public String getCreateTable(Class<?> cls, String tableName, String key,
			boolean isInt) {
		StringBuffer sb = getStrBuf();
		sb.setLength(0);
		if (TextUtils.isEmpty(tableName)) {
			tableName = cls.getSimpleName();
		}
		if (!isInt) {
			sb.append("CREATE TABLE IF NOT EXISTS " + tableName + "(" + key
					+ " TEXT PRIMARY KEY,");
		} else {
			sb.append("CREATE TABLE IF NOT EXISTS " + tableName + "(" + key
					+ " INTEGER PRIMARY KEY,");
		}

		Field[] field = cls.getDeclaredFields();
		for (Field f : field) {
			if (f.getName().equals(key)) {
				continue;
			} else if ("java.lang.String".contains(f.getType().getName())) {

				sb.append(f.getName() + " TEXT,");
			} else if ("int".contains(f.getType().getName())
					|| "boolean".contains(f.getType().getName())) {

				sb.append(f.getName() + " INTEGER,");
			} else if ("long".contains(f.getType().getName())) {
				sb.append(f.getName() + " LONG,");
			} else {// 其他类型,以二进制存储
				sb.append(f.getName() + " BLOB,");
			}
		}

		sb.setLength(sb.length() - 1);
		sb.append(");");
		return sb.toString();
	}

	private static SoftReference<StringBuffer> strBfeCache;

	private static StringBuffer getStrBuf() {
		StringBuffer result = null;
		if (strBfeCache != null)
			result = strBfeCache.get();
		if (result == null) {
			result = new StringBuffer();
			strBfeCache = new SoftReference<StringBuffer>(result);
		}
		return result;
	}
	
	/**
	 * 使用反射根据属性名称获取属性值
	 * 
	 * @param fieldName
	 *            属性名称
	 * @param o
	 *            操作对象
	 * @return Object 属性值
	 */

	private Object getFieldValueByName(String fieldName, Object o) {
		String firstLetter = fieldName.substring(0, 1).toUpperCase();
		String getter = "";
		getter = "get" + firstLetter + fieldName.substring(1);
		Method method = null;
		try {
			method = o.getClass().getMethod(getter, new Class[] {});
		} catch (NoSuchMethodException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			getter = "is" + firstLetter + fieldName.substring(1);
			try {
				method = o.getClass().getMethod(getter, new Class[] {});
			} catch (NoSuchMethodException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
				return null;
			}
		}
		Object value;
		try {
			value = method.invoke(o, new Object[] {});
			return value;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;

	}
	
	/**
	 * @param obj
	 *            操作的对象
	 * @param att
	 *            操作的属性
	 * @param value
	 *            设置的值
	 * @param type
	 *            参数的属性
	 * */
	public static void setter(Object obj, String att, Object value,
			Class<?> type) {
		try {
			Method method = obj.getClass().getMethod("set" + att, type);
			method.invoke(obj, value);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 将首字母改成大写
	 * 
	 * @param fldName
	 * @return
	 */
	public static String updateFrist(String fldName) {
		String first = fldName.substring(0, 1).toUpperCase();
		String rest = fldName.substring(1, fldName.length());
		String newStr = new StringBuffer(first).append(rest).toString();
		return newStr;
	}
	
	public void updateReadState(int activityId) {

		// Gets the data repository in write mode
		SQLiteDatabase db = getWritableDatabase();
		db.beginTransaction();

		ContentValues values = new ContentValues();

		values.put("readState", 1);

		String whereClause = "";
		
		whereClause = "activityId=?";
		String[] valuesClause = {
				String.valueOf(activityId)
		};
		db.update(NOTIFICATION_TABLE_NAME, values, whereClause, valuesClause);

		db.setTransactionSuccessful();
		db.endTransaction();
		db.close();
	}

}
