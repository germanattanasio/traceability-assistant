/**
 * Copyright 2015 UNICEN. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package edu.isistan.carcha.lsa;

import it.uniroma1.dis.wsngroup.gexf4j.core.Edge;
import it.uniroma1.dis.wsngroup.gexf4j.core.EdgeType;
import it.uniroma1.dis.wsngroup.gexf4j.core.Graph;
import it.uniroma1.dis.wsngroup.gexf4j.core.Mode;
import it.uniroma1.dis.wsngroup.gexf4j.core.Node;
import it.uniroma1.dis.wsngroup.gexf4j.core.data.Attribute;
import it.uniroma1.dis.wsngroup.gexf4j.core.data.AttributeClass;
import it.uniroma1.dis.wsngroup.gexf4j.core.data.AttributeList;
import it.uniroma1.dis.wsngroup.gexf4j.core.data.AttributeType;
import it.uniroma1.dis.wsngroup.gexf4j.core.impl.GexfImpl;
import it.uniroma1.dis.wsngroup.gexf4j.core.impl.StaxGraphWriter;
import it.uniroma1.dis.wsngroup.gexf4j.core.impl.data.AttributeListImpl;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.Calendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import edu.isistan.carcha.lsa.model.Entity;

/**
 * The Class GephiTraceability.
 */
public class GephiTraceability {
    
	/** The graph. */
	private Graph graph;
	
	/** The gexf. */
	private GexfImpl gexf;
	
	/** The node map. */
	private Map<String,Node> nodeMap;
	
	/** The edges. */
	private Set<String> edges;

	
	/** The filename. */
	private String filename;
	
	/**
	 * Instantiates a new gephi traceability.
	 *
	 * @param filename the filename
	 */
	public GephiTraceability(String filename) {
		this.nodeMap = new HashMap<String,Node>();
		this.edges = new HashSet<String>();
		this.filename = filename;
		init();
	}


	/**
	 * Adds the node.
	 *
	 * @param concern the concern
	 * @return the string
	 */
	public String addNode(Entity concern) {
		//create a unique hash code based on node features
		int hashCode = concern.hashCode();
		String name = concern.getType()+" "+concern.getClassification();
		Node ret = graph.createNode(String.valueOf(hashCode)).setLabel(name);
		nodeMap.put(concern.hashCode()+"", ret);
		
		//fill the node attributes
		List<Attribute> attributes = graph.getAttributeLists().get(0);
		for (Attribute attr : attributes) {
			if (attr.getId() == "0") 
				ret.getAttributeValues().addValue(attr,concern.getType().getType());
			else if (attr.getId() == "1") 
				ret.getAttributeValues().addValue(attr,concern.getType()+" "+concern.getClassification());
			else
				ret.getAttributeValues().addValue(attr,concern.getLabel());
		}
		return String.valueOf(hashCode);
	}

	
	/**
	 * Adds the edge.
	 *
	 * @param fromId the from id
	 * @param toId the to id
	 * @param linkWeight the link weight
	 * @return the string
	 */
	public String addEdge(int fromId, int toId, Double linkWeight) {
		String edgeString = fromId+"-"+toId;
		if (edges.contains(edgeString)) {
			return edgeString;
		}
		edges.add(edgeString);
		Node fromNode = nodeMap.get(fromId+"");
		Node toNode = nodeMap.get(toId+"");
		Edge edge = fromNode.connectTo(toNode).setEdgeType(EdgeType.DIRECTED);
		//fill the edge attributes
		List<Attribute> attributes = graph.getAttributeLists().get(1);
		for (Attribute attr : attributes) {
			if (attr.getId() == "0") 
				edge.getAttributeValues().addValue(attr,String.valueOf(linkWeight));
		}
		return edge.getId();
	}

	/**
	 * Inits the.
	 */
	private void init() {
		gexf = new GexfImpl();
		Calendar date = Calendar.getInstance();

		gexf.getMetadata()
			.setLastModified(date.getTime())
			.setCreator("Carcha")
			.setDescription("Traceability between requirement and architectural concerns");

		graph = gexf.getGraph();
		graph.setDefaultEdgeType(EdgeType.DIRECTED).setMode(Mode.STATIC);

		AttributeList nodeAttrList = new AttributeListImpl(AttributeClass.NODE);
		nodeAttrList.createAttribute("0", AttributeType.STRING, "type");
		nodeAttrList.createAttribute("1", AttributeType.STRING, "classification");
		nodeAttrList.createAttribute("2", AttributeType.STRING, "concern");

		graph.getAttributeLists().add(nodeAttrList);
		
		//add edge attributes at position 1
		AttributeList edgeAttrList = new AttributeListImpl(AttributeClass.EDGE);
		edgeAttrList.createAttribute("0", AttributeType.DOUBLE, "link");
		graph.getAttributeLists().add(edgeAttrList);
	}

	/**
	 * Save graph.
	 *
	 * @return the string
	 */
	public String saveGraph() {
		File f = new File(filename);
		try {
			f.createNewFile();
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		StaxGraphWriter graphWriter = new StaxGraphWriter();
		Writer out;
		try {
			out =  new FileWriter(f, false);
			graphWriter.writeToStream(gexf, out, "UTF-8");
			return filename;
		} catch (IOException e) {
			System.exit(0);
		}
		return null;
	}

}
