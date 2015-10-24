package com.devcru.arb.geostorage;

import java.util.HashMap;
import java.util.Iterator;

public class GeoStorage<K>
{
	public class DataPoint extends Quadtree.DataPoint
	{
		private K key;
		public DataPoint(double x, double y) {
			super(x, y);
		}
		public K getKey() {
			return key;
		}
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
