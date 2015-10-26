package com.devcru.arb.geostorage;

import java.io.DataOutputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map.Entry;

public class QuestionStorage extends GeoStorage<Integer>  {
	
	private static QuestionStorage instance = null;
	public static QuestionStorage getInstance() {
		if (instance == null) instance = new QuestionStorage();
		return instance;
	}
	
	public class Question extends GeoStorage<Integer>.DataPoint
	{
		String text;
		String answer = null;
		
		public Question(String text, double latitude, double longitude) {
			super(latitude, longitude);
			this.text = text;
		}
		public String getText() {
			return this.text;
		}
		public String getAnswer() {
			return answer;
		}
		public void setAnswer(String text) {
			answer = text;
		}
	}
	
	private Integer nextKey = 0;
	
	public QuestionStorage() {
		System.out.println("[QS] Instance created.");
	}
	
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
		System.out.println("[QS] Inserted question [key="+key+"]");
	}
	public Integer putNext(DataPoint value) {
		Integer key = nextKey;
		super.put(key, value);
		System.out.println("[QS] Inserted question [key="+nextKey+"]");
		findNextKey();
		return key;
	}
	public void remove(Integer key) {
		super.remove(key);
		System.out.println("[QS] Removed question [key="+key+"]");
	}
	public Question get(Integer key) {
		return (Question) super.get(key);
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
	public Question findRandom(double latitude, double longitude, double radius) {
		double x = GeoStorage.lon2x(longitude);
		double y = GeoStorage.lat2y(latitude);
		
		ArrayList<Quadtree.Node> nodes = getNodesInRange(x, y, radius, getQuadtree().getRoot());
		
		//double r2 = radius * radius;
		ArrayList<Quadtree.DataPoint> ptsInRange = new ArrayList<Quadtree.DataPoint>();
		
		int searchCount = 0;
		for (int i=0; i<nodes.size(); i++) {
			Quadtree.Node node = nodes.get(i);
			ArrayList<Quadtree.DataPoint> pts = node.getDataPoints();
			for (int j=0; j<pts.size(); j++) {
				Quadtree.DataPoint p = pts.get(j);
				Question q = (Question) p;
				if (q.answer == null) {
					/*double dx = p.x - x;
					double dy = p.y - y;
					double d2 = dx*dx + dy*dy;
					if (d2 <= r2) {
						ptsInRange.add(p);
					}*/
					double dist = GeoStorage.getGeoDistance(q.getLatitude(), q.getLongitude(), latitude, longitude);
					searchCount++;
					if (dist <= radius) {
						ptsInRange.add(p);
					}
				}
			}
		}
		
		System.out.print("[QS] Searched "+nodes.size()+" overlapping nodes, "+searchCount+" data points, found "+ptsInRange.size()+" in range.");
		
		if (ptsInRange.size() > 0) {
			Question q = (Question) ptsInRange.get((int)Math.floor(Math.random() * ptsInRange.size()));
			System.out.print(" Picked random at depth "+q.getParentNode().getDepth()+".");
			return q;
		}
		
		System.out.print("\n");
		return null;
	}
	
	public void writeToFile() throws FileNotFoundException {
		try {
			DataOutputStream out = new DataOutputStream(new FileOutputStream("stuff.txt"));
			Iterator<Entry<Integer, DataPoint>> it = this.getData().entrySet().iterator();
			out.writeBytes("{");
			while (it.hasNext()) {
				Entry<Integer, DataPoint> entry = it.next();
				Integer key = entry.getKey();
				DataPoint value = entry.getValue();
				out.writeBytes('"'+key+'"'+":"+'"'+value+'"');
				if (it.hasNext()) out.writeBytes(",");
			}
			out.writeBytes("}");
			out.close();
		}
		catch (Exception e) {
			
		}
	}
	
}
