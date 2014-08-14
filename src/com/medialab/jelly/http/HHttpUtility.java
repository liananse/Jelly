package com.medialab.jelly.http;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.zip.GZIPInputStream;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.conn.ConnectTimeoutException;
import org.apache.http.conn.params.ConnRouteParams;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.util.ByteArrayBuffer;
import org.apache.http.util.EncodingUtils;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.text.TextUtils;

import com.medialab.jelly.util.UConstants;
import com.medialab.jelly.util.UException;
import com.medialab.jelly.util.UTools;

public class HHttpUtility {
	
	public static final String BOUNDARY = "7cd4a6d158c";
	public static final String MP_BOUNDARY = "--" + BOUNDARY;
	public static final String END_MP_BOUNDARY = "--" + BOUNDARY + "--";
	public static final String MULTIPART_FORM_DATA = "multipart/form-data";
	
	// 超时时间
	private static final int SET_CONNECTION_TIMEOUT = 10000;
	private static final int SET_SOCKET_TIMEOUT = 20000;

	public static void setHeader(HttpUriRequest request) throws UException
	{
		request.setHeader("User-Agent", System.getProperties().getProperty("http.agent") + UConstants.APP_NAME);
	}

	/**
	 * Get a HttpClient object which is setting correctly .
	 * 
	 * @param context
	 *            : context of activity
	 * @return HttpClient: HttpClient object
	 */
	public static HttpClient getHttpClient(Context context)
	{
		BasicHttpParams httpParameters = new BasicHttpParams();
		// Set the default socket timeout (SO_TIMEOUT) // in
		// milliseconds which is the timeout for waiting for data.
		HttpConnectionParams.setConnectionTimeout(httpParameters, HHttpUtility.SET_CONNECTION_TIMEOUT);
		HttpConnectionParams.setSoTimeout(httpParameters, HHttpUtility.SET_SOCKET_TIMEOUT);
		HttpClient client = new DefaultHttpClient(httpParameters);
		WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
		if (!wifiManager.isWifiEnabled())
		{
			Uri uri = Uri.parse("content://telephony/carriers/preferapn");
			Cursor mCursor = context.getContentResolver().query(uri, null, null, null, null);
			if (mCursor != null && mCursor.moveToFirst())
			{
				String proxyStr = mCursor.getString(mCursor.getColumnIndex("proxy"));
				if (proxyStr != null && proxyStr.trim().length() > 0)
				{
					HttpHost proxy = new HttpHost(proxyStr, 80);
					client.getParams().setParameter(ConnRouteParams.DEFAULT_PROXY, proxy);
				}
				mCursor.close();
			}
		}
		return client;
	}
	
	public static String openUrl(Context context, String url, String method, Map<String, String> params) throws UException
	{
		String rlt = "";
		String file = "";

		try 
		{
			Set<String> keySet = params.keySet();
			if (keySet != null)
			{
				for (Iterator<String> iterator = keySet.iterator(); iterator.hasNext();)
				{
					String key = iterator.next();
					if (key.equals("avatar") || key.equals("image1") || key.equals("questionPic") || key.equals("answerPic"))
					{
						file = params.get(key);
					}
				}
			}
			
			if (TextUtils.isEmpty(file))
			{
				rlt = openUrl(context, url, method, params, null);
			}
			else
			{
				List<HFileModel> files = new ArrayList<HFileModel>();
				if (UConstants.isDataLoaderDebug) {
					System.out.println(file);
				}
				String[] mHfiles = file.split("\\|");
				if (UConstants.isDataLoaderDebug) {
					System.out.println("http//" + mHfiles[0]);
				}
				
				for (int i = 0; i < mHfiles.length; i++)
				{
					
					HFileModel _help = new HFileModel(mHfiles[i]);
					files.add(_help);
				}

				rlt = openUrl(context, url, method, params, files);
			}
		}
		catch (UnsupportedEncodingException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
		catch (Exception e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return rlt;
	}

	/**
	 * author zenghui
	 * 
	 * 2013-1-25
	 */
	public static String openUrl(Context context, String url, String method, Map<String, String> params, List<HFileModel> files) throws Exception
	{
		String result = "";
		
		try {
			HttpClient client = getHttpClient(context);
			HttpUriRequest request = null;
			ByteArrayOutputStream bos = null;

			appendBasicParams(context, params);
			if (method.equals("GET"))
			{
				url = encodeUrl(url, params);
				if (UConstants.isDataLoaderDebug) {
					System.out.println("get:" + url);
				}
				HttpGet get = new HttpGet(url);
				request = get;
			}
			else if (method.equals("POST"))
			{
				HttpPost post = new HttpPost(url);
				byte[] data = null;
				bos = new ByteArrayOutputStream(1024 * 50);
				
				if (files != null && files.size() > 0)
				{
					paramToUpload(bos, params);
					post.setHeader("transfer-coding", "chunked");
					post.setHeader("Content-Type", MULTIPART_FORM_DATA + "; boundary=" + BOUNDARY);
					imageContentToUpload(context, bos, files);
				}
				else
				{
					post.setHeader("Content-Type", "application/x-www-form-urlencoded");
					String postParam = encodeParameters(params);
					if (UConstants.isDataLoaderDebug) {
						System.out.println("post:" + url + "\nparams:" + postParam);
					}
					data = postParam.getBytes("UTF-8");
					bos.write(data);
				}
				
				data = bos.toByteArray();
				bos.close();
				ByteArrayEntity formEntity = new ByteArrayEntity(data);
				post.setEntity(formEntity);
				request = post;
			}
			else if (method.equals("DELETE"))
			{
				request = new HttpDelete(url);
			}
			setHeader(request);
			HttpResponse response = client.execute(request);
			StatusLine status = response.getStatusLine();
			int statusCode = status.getStatusCode();

			if (statusCode != 200)
			{
				result = read(response);
				throw new UException(String.format(status.toString()), statusCode);
			}
			result = read(response);
		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			
			e.printStackTrace();
		} catch (ConnectTimeoutException e) {
			// TODO: handle exception
			//  指的是服务器请求超时
			
			result = "ConnecTimeoutException";
		} catch (SocketTimeoutException e) {
			// TODO: handle exception
			// 指的是服务器响应超时
			result = "SocketTimeoutException";
			
		} catch (IOException e) { 
            e.printStackTrace(); 
        } 
		return result;
	}

	/**
	 * author zenghui
	 * 
	 * 2013-1-25
	 */
	public static String encodeParameters(Map<String, String> params)
	{
		StringBuilder sb = new StringBuilder();
		try
		{
			Set<String> keySet = params.keySet();
			if (keySet != null)
			{
				boolean firstParam = true;
				for (Iterator<String> iterator = keySet.iterator(); iterator.hasNext();)
				{
					String key = iterator.next();
					if (firstParam)
					{
						firstParam = false;
					}
					else
					{
						sb.append("&");
					}
					sb.append(key).append("=").append(URLEncoder.encode(params.get(key), "UTF-8"));
				}
			}
		}
		catch (Exception e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return sb.toString();

	}

	/**
	 * author zenghui
	 * 
	 * 2013-1-25
	 */
	public static String encodeUrl(String url, Map<String, String> params)
	{
		StringBuilder sb = new StringBuilder();
		try
		{
			// TODO Auto-generated method stub
			Set<String> keySet = params.keySet();
			if (keySet != null)
			{
				if (url.indexOf("?") == -1)
				{
					sb.append(url).append("?");
					boolean firstParam = true;
					for (Iterator<String> iterator = keySet.iterator(); iterator.hasNext();)
					{
						String key = iterator.next();
						if (firstParam)
						{
							firstParam = false;
						}
						else
						{
							sb.append("&");
						}
						sb.append(key).append("=").append(URLEncoder.encode(params.get(key), "UTF-8"));
					}
				}
				else
				{
					boolean firstParm = false;
					sb.append(url);
					int i = url.indexOf("?");
					int len = url.length();
					if (i + 1 == len)
					{
						firstParm = true;
					}

					for (Iterator<String> iterator = keySet.iterator(); iterator.hasNext();)
					{
						String key = iterator.next();
						if (firstParm)
						{
							sb.append(key).append("=").append(URLEncoder.encode(params.get(key), "UTF-8"));
							firstParm = false;
						}
						else
						{
							sb.append("&").append(key).append("=").append(URLEncoder.encode(params.get(key), "UTF-8"));
						}
					}
				}
			}
		}
		catch (Exception e)
		{
		}

		String u = sb.toString();
		if (UConstants.isDataLoaderDebug) {
			System.out.println("encode:" + url);
		}
		return u;
	}

	/**
	 * author zenghui
	 * 
	 * 2013-1-25
	 */
	public static void appendBasicParams(Context context, Map<String, String> params)
	{
		// TODO Auto-generated method stub
		params.put("platform", "android");
		params.put("device", Build.MODEL);
		params.put("appVersion", UTools.OS.getAppVersion(context));
		params.put("systemVersion", UTools.OS.getOSVersion());
		params.put("channel", UTools.OS.getAppChannel());
		params.put("origin", "");
		params.put("phonecode", UTools.OS.getPhoneCode(context));
		// language not need Temporarily
		params.put("lang", UTools.OS.getLanguage());
		params.put("countrycode", UTools.OS.getCountryCode());
		params.put("appName", UTools.OS.getAppName());
		params.put("accessToken", UTools.OS.getAccessToken(context));
		params.put("deviceName", "");
//		params.put("time", String.valueOf(System.currentTimeMillis()));
		params.put("network_type", String.valueOf(UTools.OS.getNetWorkType(context)));
		params.put("screen_width", String.valueOf(context.getResources().getDisplayMetrics().widthPixels));
	}

	/**
	 * Read http requests result from response .
	 * 
	 * @param response
	 *            : http response by executing httpclient
	 * 
	 * @return String : http response content
	 */
	private static String read(HttpResponse response) throws UException
	{
		String result = "";
		HttpEntity entity = response.getEntity();
		InputStream inputStream;
		try
		{
			inputStream = entity.getContent();
			ByteArrayOutputStream content = new ByteArrayOutputStream();

			Header header = response.getFirstHeader("Content-Encoding");
			if (header != null && header.getValue().toLowerCase().indexOf("gzip") > -1)
			{
				inputStream = new GZIPInputStream(inputStream);
			}

			// Read response into a buffered stream
			int readBytes = 0;
			byte[] sBuffer = new byte[512];
			while ((readBytes = inputStream.read(sBuffer)) != -1)
			{
				content.write(sBuffer, 0, readBytes);
			}
			// Return result from buffered stream
			result = new String(content.toByteArray());
			return result;
		}
		catch (IllegalStateException e)
		{
			throw new UException(e);
		}
		catch (IOException e)
		{
			throw new UException(e);
		}
	}

	/**
	 * author zenghui
	 * 
	 * 2013-1-25
	 */
	public static String get(Context context, String url, Map<String, String> params) throws Exception
	{
		if (params == null)
		{
			params = new HashMap<String, String>();
		}
		return openUrl(context, url, "GET", params);
	}

	/**
	 * author zenghui
	 * 
	 * 2013-1-25
	 */
	public static String post(Context context, String url, Map<String, String> params) throws Exception
	{
		if (params == null)
		{
			params = new HashMap<String, String>();
		}
		return openUrl(context, url, "POST", params);
	}

	/**
	 * author zenghui
	 * 
	 * 2013-1-25
	 */
	public static String getNetResource(String sourceUrl)
	{
		String myString = "";
		try
		{
			URL myURL = new URL(sourceUrl);
			URLConnection ucon = myURL.openConnection();
			InputStream is = ucon.getInputStream();
			BufferedInputStream bis = new BufferedInputStream(is);
			ByteArrayBuffer baf = new ByteArrayBuffer(50);
			int current = 0;
			while ((current = bis.read()) != -1)
			{
				baf.append((byte) current);
			}
			myString = EncodingUtils.getString(baf.toByteArray(), "UTF-8");
		}
		catch (Exception e)
		{
			myString = e.getMessage();
		}
		return myString;
	}

	
	private static void paramToUpload(OutputStream baos, Map<String, String> params) throws UException
	{
		Set<String> keySet = params.keySet();
		if (keySet != null)
		{
			for (Iterator<String> iterator = keySet.iterator(); iterator.hasNext();)
			{
				String key1 = iterator.next();
				if (key1.equals("avatar") || key1.equals("image1") || key1.equals("questionPic") || key1.equals("answerPic"))
				{
					continue;
				}
				StringBuilder temp = new StringBuilder(10);
				temp.setLength(0);
				temp.append(MP_BOUNDARY).append("\r\n");
				temp.append("content-disposition: form-data; name=\"").append(key1).append("\"\r\n\r\n");
				temp.append(params.get(key1)).append("\r\n");
				byte[] res = temp.toString().getBytes();
				// Trace.log(temp.toString());
				try
				{
					baos.write(res);
				}
				catch (IOException e)
				{
					throw new UException(e);
				}
			}
		}
		
	}
	
	private static void imageContentToUpload(Context context, OutputStream out, List<HFileModel> files) throws UException
	{
		int length = files.size();
		byte[][] data = new byte[length][];
		for (int i = 0; i < length - 1; i++)
		{
			String filePath = files.get(i).value;
			File file = new File(filePath);

			String postFileParam = MP_BOUNDARY + "\r\n" + "Content-Disposition: form-data; name=\"" + files.get(i).key + "\"; filename=\"" + file.getName() + "\"\r\n" + "Content-Type: application/octet-stream" + "\r\n" + "Content-Transfer-Encoding: binary\r\n\r\n";

			byte[] split_data = ("\r\n" + MP_BOUNDARY + "\r\n").getBytes();
			byte[] postContentData = postFileParam.getBytes();
			byte[] fileData = UTools.Storage.readBinary(context, files.get(i).value);
			data[i] = new byte[postContentData.length + 4 + fileData.length + split_data.length];

			System.arraycopy(postContentData, 0, data[i], 0, postContentData.length);
			System.arraycopy(fileData, 0, data[i], postContentData.length, fileData.length);
			System.arraycopy(split_data, 0, data[i], postContentData.length + fileData.length, split_data.length);
		}

		// the last file
		int k = length - 1;
		File file = new File(files.get(k).value);

		String postFileParam = "";
		if (0 == length - 1)
		{
			postFileParam = MP_BOUNDARY + "\r\n" + "Content-Disposition: form-data; name=\"" + files.get(k).key + "\"; filename=\"" + file.getName() + "\"\r\n" + "Content-Type: application/octet-stream" + "\r\n" + "Content-Transfer-Encoding: binary\r\n\r\n";
		}
		else
		{
			postFileParam = "Content-Disposition: form-data; name=\"" + files.get(k).key + "\"; filename=\"" + file.getName() + "\"\r\n" + "Content-Type: application/octet-stream" + "\r\n" + "Content-Transfer-Encoding: binary\r\n\r\n";
		}

		byte[] split_data = ("\r\n" + END_MP_BOUNDARY + "\r\n").getBytes();
		byte[] postContentData = postFileParam.getBytes();
		byte[] fileData = UTools.Storage.readBinary(context, files.get(k).value);
		data[k] = new byte[postContentData.length + fileData.length + split_data.length];

		System.arraycopy(postContentData, 0, data[k], 0, postContentData.length);
		System.arraycopy(fileData, 0, data[k], postContentData.length, fileData.length);
		System.arraycopy(split_data, 0, data[k], postContentData.length + fileData.length, split_data.length);

		try
		{
			for (int i = 0; i < data.length; i++)
			{
				for (int j = 0; j < data[i].length; j++)
				{
					byte bByte = data[i][j];
					out.write(bByte);
				}
			}
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
	}
}
