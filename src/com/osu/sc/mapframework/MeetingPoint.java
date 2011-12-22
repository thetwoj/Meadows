package com.osu.sc.mapframework;

import android.graphics.Point;

//TODO add fields for time, duration etc.
public class MeetingPoint 
{
	public Point mapLoc;
	public int mapId;
	public MeetingPoint(int mapId, Point mapLoc)
	{
		this.mapLoc = mapLoc;
		this.mapId = mapId;
	}
}
