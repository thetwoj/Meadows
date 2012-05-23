package com.osu.sc.mapframework;

public class MapUtils 
{
	//Computes the length between 2 points.
	public static double pyth(GeoPoint first, GeoPoint second)
	{
		//Pythagorean theorem for geo points.
		return Math.sqrt((first.lat - second.lat) * (first.lat - second.lat) + (first.lon - second.lon) * (first.lon - second.lon));
	}
}
