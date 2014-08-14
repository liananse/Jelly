package com.medialab.jelly.http;

import java.io.File;
import java.net.URLEncoder;

public class PostParameter implements java.io.Serializable
{
	String name;
	String value = "";
	private File file = null;

	private static final long serialVersionUID = -8708108746980739212L;

	public PostParameter(String name, String value)
	{
		this.name = name;
		if (value != null)
			this.value = value;
	}

	public PostParameter(String name, long value)
	{
		this.name = name;
		this.value = String.valueOf(value);
	}

	public PostParameter(String name, double value)
	{
		this.name = name;
		this.value = String.valueOf(value);
	}

	public PostParameter(String name, int value)
	{
		this.name = name;
		this.value = String.valueOf(value);
	}

	public PostParameter(String name, File file)
	{
		this.name = name;
		this.file = file;
	}

	public String getName()
	{
		return name;
	}

	public String getValue()
	{
		return value;
	}

	public File getFile()
	{
		return file;
	}

	public boolean isFile()
	{
		return null != file;
	}

	public static PostParameter[] getParameterArray(String name, String value)
	{
		return new PostParameter[] { new PostParameter(name, value) };
	}

	public static PostParameter[] getParameterArray(String name, int value)
	{
		return getParameterArray(name, String.valueOf(value));
	}

	public static PostParameter[] getParameterArray(String name1, String value1, String name2, String value2)
	{
		return new PostParameter[] { new PostParameter(name1, value1), new PostParameter(name2, value2) };
	}

	public static PostParameter[] getParameterArray(String name1, int value1, String name2, int value2)
	{
		return getParameterArray(name1, String.valueOf(value1), name2, String.valueOf(value2));
	}

	@Override
	public int hashCode()
	{
		int result = name.hashCode();
		result = 31 * result + value.hashCode();
		result = 31 * result + (file != null ? file.hashCode() : 0);
		return result;
	}

	@Override
	public boolean equals(Object obj)
	{
		if (null == obj)
		{
			return false;
		}
		if (this == obj)
		{
			return true;
		}
		if (obj instanceof PostParameter)
		{
			PostParameter that = (PostParameter) obj;

			if (file != null ? !file.equals(that.file) : that.file != null)
				return false;

			return this.name.equals(that.name) && this.value.equals(that.value);
		}
		return false;
	}

	@Override
	public String toString()
	{
		return "PostParameter{" + "name='" + name + '\'' + ", value='" + value + '\'' + ", file=" + file + '}';
	}

	public int compareTo(Object o)
	{
		int compared;
		PostParameter that = (PostParameter) o;
		compared = name.compareTo(that.name);
		if (0 == compared)
		{
			compared = value.compareTo(that.value);
		}
		return compared;
	}

	public static String encodeParameters(PostParameter[] httpParams)
	{
		if (null == httpParams)
		{
			return "";
		}
		StringBuffer buf = new StringBuffer();
		for (int j = 0; j < httpParams.length; j++)
		{
			if (httpParams[j].isFile())
			{
				throw new IllegalArgumentException("parameter [" + httpParams[j].name + "]should be text");
			}
			if (j != 0)
			{
				buf.append("&");
			}
			try
			{
				buf.append(URLEncoder.encode(httpParams[j].name, "UTF-8")).append("=").append(URLEncoder.encode(httpParams[j].value, "UTF-8"));
			}
			catch (java.io.UnsupportedEncodingException neverHappen)
			{
			}
		}
		return buf.toString();

	}

}