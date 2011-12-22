package com.osu.sc.mapframework;

import com.google.android.maps.GeoPoint;

public class GeoreferencedPoint extends GeoPoint
{
	public double x;
	public double y;
	public GeoreferencedPoint(int latitudeE6, int longitudeE6, double x, double y)
	{
		super(latitudeE6, longitudeE6);
		this.x = x;
		this.y = y;	
	}
}