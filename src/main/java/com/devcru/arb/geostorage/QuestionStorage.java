package com.devcru.arb.geostorage;

import java.util.ArrayList;

public class QuestionStorage extends GeoStorage<Integer>
{
	public class Question extends GeoStorage<Integer>.DataPoint
	{
		String text;
		
		public Question(String text, double longitude, double latitude) {
			super(latitude, longitude);
			this.text = text;
		}
	}
	
	private Integer nextKey = 0;
	
	private void findNextKey() {
		do {
			nextKey++;
		} while (this.get(nextKey) != null);
		
	}
	public Integer getNextKey() {
		return nextKey;
	}
	public void put(Integer key, DataPoint value) {
		super.put(key, value);
		
		if (key.equals(nextKey)) {
			nextKey = key;
			findNextKey();
		}
	}
	public Integer putNext(DataPoint value) {
		super.put(nextKey, value);
		findNextKey();
		return nextKey;
	}
	

	private ArrayList<Quadtree.Node> getNodesInRange(double x, double y, double radius, Quadtree.Node node) {
		ArrayList<Quadtree.Node> nodes = new ArrayList<Quadtree.Node>();
		BoundingBox bbox = new BoundingBox(x-radius, y-radius, x+radius, y+radius);
		
		if (node.getDataPoints().size() > 0 && bbox.intersects(node.getBoundingBox())) {
			nodes.add(node);
		}
		
		if (node.hasChildren()) {
			for (int i=0; i<4; i++) {
				ArrayList<Quadtree.Node> nodes2 = getNodesInRange(x, y, radius, node.getChild(i));
				nodes.addAll(nodes2);
			}
		}
		return nodes;
	}
	public Question findRandom(double x, double y, double radius) {
		ArrayList<Quadtree.Node> nodes = getNodesInRange(x, y, radius, getQuadtree().getRoot());
		
		double r2 = radius * radius;
		ArrayList<Quadtree.DataPoint> ptsInRange = new ArrayList<Quadtree.DataPoint>();
		
		int searchCount = 0;
		for (int i=0; i<nodes.size(); i++) {
			Quadtree.Node node = nodes.get(i);
			ArrayList<Quadtree.DataPoint> pts = node.getDataPoints();
			for (int j=0; j<pts.size(); j++) {
				Quadtree.DataPoint p = pts.get(j);
				double dx = p.x - x;
				double dy = p.y - y;
				double d2 = dx*dx + dy*dy;
				searchCount++;
				if (d2 <= r2) {
					ptsInRange.add(p);
				}
			}
		}
		
		System.out.print("Searched "+nodes.size()+" overlapping nodes, "+searchCount+" data points, found "+ptsInRange.size()+" in range.");
		
		if (ptsInRange.size() > 0) {
			Question q = (Question) ptsInRange.get((int)Math.floor(Math.random() * ptsInRange.size()));
			System.out.println(" Picked random at depth "+q.getParentNode().getDepth()+".");
			return q;
		}
		
		System.out.print("\n");
		return null;
	}
}
