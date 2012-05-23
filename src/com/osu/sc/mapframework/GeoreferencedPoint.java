package com.osu.sc.mapframework;

public class GeoreferencedPoint extends GeoPoint
{
	public double x;
	public double y;
	public boolean isChosen;
	public float distanceToLocation;
	public GeoreferencedPoint(double lat, double lon, double x, double y)
	{
		super(lat, lon);
		this.x = x;
		this.y = y;
		this.isChosen = false;
		this.distanceToLocation = Float.MAX_VALUE;
	}
}