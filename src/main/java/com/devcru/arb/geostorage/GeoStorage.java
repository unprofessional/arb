package com.devcru.arb.geostorage;

import java.util.HashMap;
import java.util.Iterator;

public class GeoStorage<K>
{
	public class DataPoint extends Quadtree.DataPoint
	{
		private K key;
		public DataPoint(double latitude, double longitude) {
			super(lon2x(longitude), lat2y(latitude));
		}
		public K getKey() {
			return key;
		}
		public double getLatitude() {
			return y2lat(this.y);
		}
		public double getLongitude() {
			return x2lon(this.x);
		}
	}
	
	public static double lon2x(double longitude) {
		return ((longitude + 180.0) / 360.0) % 1.0;
	}
	public static double lat2y(double latitude) {
		return ((latitude + 90.0) / 180.0) % 1.0;
	}
	public static double x2lon(double x) {
		return x * 360.0 - 180.0;
	}
	public static double y2lat(double y) {
		return y * 180.0 - 90.0;
	}
	private static final double EARTH_CIRC = 40075.0;
	private static final double EARTH_HALF_CIRC = 40075.0 / 2.0;
	public static double getGeoDistance(double lat1, double lon1, double lat2, double lon2) {
		double x1 = lon2x(lon1) * EARTH_CIRC;
		double y1 = lat2y(lat1) * EARTH_HALF_CIRC;
		double x2 = lon2x(lon2) * EARTH_CIRC;
		double y2 = lat2y(lat2) * EARTH_HALF_CIRC;
		double dx = (x2-x1);
		double dy = (y2-y1);
		return dx*dx+dy*dy;
	}
	
	private HashMap<K, DataPoint> data = new HashMap<K, DataPoint>();
	private Quadtree quadtree;
	
	public GeoStorage() {
		quadtree = new Quadtree(1.0);
	}
	public HashMap<K, DataPoint> getData() {
		return data;
	}
	public Quadtree getQuadtree() {
		return quadtree;
	}
	
	public void put(K key, DataPoint value) {
		quadtree.addPoint(value);
		data.put(key, value);
		value.key = key;
	}
	public void remove(K key) {
		DataPoint value = data.remove(key);
		quadtree.removePoint(value);
		value.key = null;
	}
	public void remove(Iterator<K> it, K key) {
		DataPoint value = data.get(key);
		quadtree.removePoint(value);
		it.remove();
		value.key = null;
	}
	public DataPoint get(K key) {
		return data.get(key);
	}
}
