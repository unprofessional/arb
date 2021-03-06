package com.devcru.arb.geostorage;

import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

public class QuestionStorage extends GeoStorage<Long>  {
	
	private static QuestionStorage instance = null;
	public static QuestionStorage getInstance() {
		if (instance == null) instance = new QuestionStorage();
		return instance;
	}

	public class Question extends GeoStorage<Long>.DataPoint
	{
		String text;
		Answer answer = null;
		
		public Question(String text, double latitude, double longitude) {
			super(latitude, longitude);
			this.text = text;
		}
		public String getText() {
			return this.text;
		}
		public Answer getAnswer() {
			return answer;
		}
		public void setAnswer(Answer answer) {
			this.answer = answer;
		}
	}
	
	public class Answer
	{
		String text;
		double latitude;
		double longitude;
		
		public Answer(String text, double latitude, double longitude) {
			this.text = text;
			this.latitude = latitude;
			this.longitude = longitude;
		}
		public String getText() {
			return text;
		}
		public double getLatitude() {
			return latitude;
		}
		public double getLongitude() {
			return longitude;
		}
	}
	
	private Long nextKey = 0L;
	
	public QuestionStorage() {
		System.out.println("[QS] Instance created.");
	}
	
	private void findNextKey() {
		do {
			nextKey++;
		} while (this.get(nextKey) != null);
	}
	public Long getNextKey() {
		return nextKey;
	}
	public void put(Long key, DataPoint value) {
		super.put(key, value);
		
		if (key.equals(nextKey)) {
			nextKey = key;
			findNextKey();
		}
		System.out.println("[QS] Inserted question [key="+key+"]");
	}
	public Question putNext(Question value) {
		Long key = nextKey;
		super.put(key, value);
		System.out.println("[QS] Inserted question [key="+nextKey+"]");
		findNextKey();
		return value;
	}
	public void remove(Long key) {
		super.remove(key);
		System.out.println("[QS] Removed question [key="+key+"]");
	}
	public Question get(Long key) {
		return (Question) super.get(key);
	}
	public void clear() {
		super.clear();
		nextKey = 0L;
		System.out.println("[QS] Cleared all questions.");
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
	
	public void writeToFile() {
		try {
			System.out.println("[QS] Writing to file.");
			
			FileWriter out = new FileWriter("QuestionStorage.json");
			Iterator<Entry<Long, DataPoint>> it = this.getData().entrySet().iterator();
			
			final String quot = "\"";
			
			out.write("{");
			while (it.hasNext()) {
				Entry<Long, DataPoint> entry = it.next();
				Question q = (Question) entry.getValue();
				Long key = entry.getKey();
				String text = q.getText();
				double lat = q.getLatitude();
				double lon = q.getLongitude();
				Answer answer = q.getAnswer();
				
				out.write(quot+key+quot+":"+"{");
				
				out.write(
					 quot+"text"+quot+":"+quot+text+quot+","
					+quot+ "lat"+quot+":"+lat+","
					+quot+ "lon"+quot+":"+lon
				);
				if (answer != null) {
					out.write(
						quot+"answer"+quot+":{"
							+quot+"text"+quot+":"+quot+answer.getText()+quot+","
							+quot+ "lat"+quot+":"+answer.getLatitude()+","
							+quot+ "lon"+quot+":"+answer.getLongitude()
						+"}"
					);
				}
				
				out.write("}");
				
				if (it.hasNext()) out.write(",");
			}
			out.write("}");
			
			out.flush();
			out.close();
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}
	public void readFromFile() {
		try {
			System.out.println("[QS] Reading from file.");

			this.clear();
			
			ObjectMapper mapper = new ObjectMapper();
			JsonNode root = mapper.readTree(new File("QuestionStorage.json"));
			
			Iterator<Map.Entry<String, JsonNode>> it = root.fields();
			while (it.hasNext()) {
				Map.Entry<String, JsonNode> field = it.next();
				
				long key = Long.parseLong(field.getKey());
				JsonNode child = field.getValue();

				Question q = new Question(
					child.get("text").asText(),
					child.get("lat").asDouble(0.0),
					child.get("lon").asDouble(0.0)
				);
				
				JsonNode ansNode = child.get("answer");
				if (ansNode != null) {
					q.setAnswer(new Answer(
						ansNode.get("text").asText(),
						ansNode.get("lat").asDouble(0.0),
						ansNode.get("lon").asDouble(0.0)
					));
				}
				
				this.put(key, q);
			}
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}
}
