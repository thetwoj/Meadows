package com.osu.sc.mapframework;

import android.graphics.PointF;

//TODO add fields for time, duration etc.
public class MeetingPoint 
{
	public PointF mapLoc;
	public int mapId;
	public MeetingPoint(int mapId, PointF mapLoc)
	{
		this.mapLoc = mapLoc;
		this.mapId = mapId;
	}
}
