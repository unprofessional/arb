package com.devcru.arb.geostorage;

import java.util.ArrayList;

public class Quadtree
{
	public static class DataPoint {
		private Node parentNode;
		public double x, y;
		public DataPoint(double x, double y) {
			this.x = x;
			this.y = y;
		}
		public Node getParentNode() {
			return parentNode;
		}
	}
	
	public static class Node
	{
		private Quadtree tree;
		private Node parent;
		private Node[] children;
		private int depth = 0;
		private BoundingBox bbox;
		private ArrayList<DataPoint> dataPoints = new ArrayList<DataPoint>();
		private int numDataPoints = 0;
		
		public Node(Node parent, BoundingBox bbox) {
			this.parent = parent;
			this.children = null;
			this.bbox = bbox;
			if (this.parent != null) {
				this.depth = this.parent.depth + 1;
				this.tree = this.parent.tree;
			}
		}
		
		public int getDepth() {
			return depth;
		}

		public void testSplit() {
			if (depth < tree.maxDepth && dataPoints.size() > tree.maxDataPoints) {
				split();
			}
		}
		public void testMerge() {
			if (depth >= tree.minDepth && dataPoints.size() < tree.minDataPoints) {
				merge();
			}
		}
		
		public void split() {
			if (children == null) {
				children = new Node[4];
				double	x1 = bbox.x1, y1 = bbox.y1,
						x2 = bbox.x2, y2 = bbox.y2,
						xc = (bbox.x1+bbox.x2)/2.0,
						yc = (bbox.y1+bbox.y2)/2.0;
				children[0] = new Node(this, new BoundingBox(x1, y1, xc, yc)); // Top Left
				children[1] = new Node(this, new BoundingBox(xc, y1, x2, yc)); // Top Right
				children[2] = new Node(this, new BoundingBox(x1, yc, xc, y2)); // Bottom Left
				children[3] = new Node(this, new BoundingBox(xc, yc, x2, y2)); // Bottom Right
				
				for (int i=0; i<4; i++) {
					Node n = children[i];
					for (int j=0; j<dataPoints.size(); j++) {
						DataPoint p = dataPoints.get(j);
						if (n.bbox.contains(p.x, p.y)) {
							dataPoints.remove(j);
							j--;
							n._addPoint(p);
							n.numDataPoints++;
						}
					}
				}
				
				/*System.out.println("Node [d="+depth+"] split: "+numDataPoints+" data points ["+
					children[0].numDataPoints+", "+
					children[1].numDataPoints+", "+
					children[2].numDataPoints+", "+
					children[3].numDataPoints+
				"]");*/
			}
		}
		public void merge() {
			if (children != null) {
				for (int i=0; i<4; i++) {
					Node n = children[i];
					n.merge();
					
					for (int j=0; j<n.dataPoints.size(); j++) {
						DataPoint p = n.dataPoints.get(j);
						this._addPoint(p);
					}
					n.dataPoints.clear();
					n.numDataPoints = 0;
				}
				children = null;

				// System.out.println("Node [d="+depth+"] merged: "+numDataPoints+" data points");
			}
		}

		private void _addPoint(DataPoint p) {
			dataPoints.add(p);
			p.parentNode = this;
		}
		public boolean addPoint(DataPoint p) {
			if (bbox.contains(p.x, p.y)) {
				if (children == null) {
					_addPoint(p);
					
					Node n = this;
					while (n != null) {
						n.numDataPoints++;
						n = n.parent;
					}
					
					testSplit();

					return true;
				}
				else {
					for (int i=0; i<4; i++) {
						Node n = children[i];
						if (n.addPoint(p))
							return true;
					}
				}
			}
			return false;
		}
		public boolean removePoint(DataPoint p) {
			if (this.dataPoints.remove(p)) {
				Node n = this;
				while (n != null) {
					n.numDataPoints--;
					n = n.parent;
				}
				
				p.parentNode = null;
				
				if (parent != null) {
					parent.testMerge();
				}
				
				return true;
			}
			return false;
		}

		public ArrayList<DataPoint> getDataPoints() {
			return dataPoints;
		}
		public int getNumDataPoints() {
			return numDataPoints;
		}
		public boolean hasChildren() {
			return (this.children != null);
		}
		public Node getChild(int i) {
			return this.children == null ? null : this.children[i];
		}
		public BoundingBox getBoundingBox() {
			return bbox;
		}
	}
	
	private Node root;
	private int minDepth = 0;
	private int maxDepth = 12;
	private int minDataPoints = 10;
	private int maxDataPoints = 100;
	
	public Quadtree(double size) {
		root = new Node(null, new BoundingBox(0.0, 0.0, size, size));
		root.tree = this;
	}

	public void addPoint(DataPoint point) {
		root.addPoint(point);
	}
	public void removePoint(DataPoint p) {
		if (p.parentNode != null) {
			p.parentNode.removePoint(p);
		}
	}
	
	public Node getRoot() {
		return root;
	}
	public int getMaxDepth() {
		return maxDepth;
	}
	public void setMaxDepth(int maxDepth) {
		this.maxDepth = maxDepth;
	}
	public int getMinDepth() {
		return minDepth;
	}
	public void setMinDepth(int minDepth) {
		this.minDepth = minDepth;
	}
	public int getMaxDataPoints() {
		return maxDataPoints;
	}
	public void setMaxDataPoints(int maxDataPoints) {
		this.maxDataPoints = maxDataPoints;
	}
	public int getMinDataPoints() {
		return minDataPoints;
	}
	public void setMinDataPoints(int minDataPoints) {
		this.minDataPoints = minDataPoints;
	}
}