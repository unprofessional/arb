package com.devcru.arb.geostorage;

public class BoundingBox
{
	public double x1, y1, x2, y2;
	
	public BoundingBox(double x1, double y1, double x2, double y2) {
		this.x1 = x1; this.y1 = y1;
		this.x2 = x2; this.y2 = y2;
	}
	public boolean contains(double x, double y) {
		return (x >= x1 && x < x2 && y >= y1 && y < y2);
	}
	public boolean intersects(BoundingBox bbox) {
		return !(this.x1 > bbox.x2 || this.x2 < bbox.x1 ||
				 this.y1 > bbox.y2 || this.y2 < bbox.y1);
	}
}
