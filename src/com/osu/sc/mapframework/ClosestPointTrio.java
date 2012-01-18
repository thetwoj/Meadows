package com.osu.sc.mapframework;

public class ClosestPointTrio
{
	public GeoreferencedPoint first;
	public GeoreferencedPoint second;
	public GeoreferencedPoint third;
	public ClosestPointTrio(GeoreferencedPoint first, GeoreferencedPoint second, GeoreferencedPoint third)
	{
		this.first = first;
		this.second = second;
		this.third = third;
	}
}
