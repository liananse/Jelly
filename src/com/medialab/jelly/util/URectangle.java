package com.medialab.jelly.util;

public class URectangle {
	
	Integer height;
	Integer originalHeight;

	Integer originalWidth;

	Integer width;

	Float fx;
	Float fy;

	/**
	 * @return the fx
	 */
	public Float getFx()
	{

		return fx;
	}

	/**
	 * @param fx
	 *            the fx to set
	 */
	public void setFx(Float fx)
	{
		this.fx = fx;
	}

	/**
	 * @return the fy
	 */
	public Float getFy()
	{
		return fy;
	}

	/**
	 * @param fy
	 *            the fy to set
	 */
	public void setFy(Float fy)
	{
		this.fy = fy;
	}

	public URectangle()
	{
	}

	public URectangle(Integer height, Integer width)
	{
		this.height = height;
		this.width = width;
	}

	/**
	 * @return the height
	 */
	public Integer getHeight()
	{
		return height;
	}

	/**
	 * @return the originalHeight
	 */
	public Integer getOriginalHeight()
	{
		return originalHeight;
	}

	/**
	 * @return the originalWidth
	 */
	public Integer getOriginalWidth()
	{
		return originalWidth;
	}

	/**
	 * @return the width
	 */
	public Integer getWidth()
	{
		return width;
	}

	/**
	 * @param height
	 *            the height to set
	 */
	public void setHeight(Integer height)
	{
		this.height = height;
	}

	/**
	 * @param originalHeight
	 *            the originalHeight to set
	 */
	public void setOriginalHeight(Integer originalHeight)
	{
		this.originalHeight = originalHeight;
	}

	/**
	 * @param originalWidth
	 *            the originalWidth to set
	 */
	public void setOriginalWidth(Integer originalWidth)
	{
		this.originalWidth = originalWidth;
	}

	/**
	 * @param width
	 *            the width to set
	 */
	public void setWidth(Integer width)
	{
		this.width = width;
	}


}
