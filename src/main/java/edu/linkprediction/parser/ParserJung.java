package edu.linkprediction.parser;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Paint;
import java.awt.Stroke;
import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.swing.JFrame;

import org.apache.commons.collections15.Transformer;
import org.jdom2.JDOMException;

import edu.uci.ics.jung.algorithms.layout.CircleLayout;
import edu.uci.ics.jung.algorithms.layout.Layout;
import edu.uci.ics.jung.graph.DirectedSparseGraph;
import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.visualization.BasicVisualizationServer;
import edu.uci.ics.jung.visualization.renderers.Renderer.VertexLabel.Position;

public class ParserJung extends Parser {

	Set<String> clases; // se puede reemplazar por outRelations.keySet();
	public Map<String, Map<String, Float>> outRelations;
	public Map<String, Map<String, Float>> inRelations;
	Map<String, String> classNamespace;
	public Map<String, Set<String>> namespaces;
	Map<String, String> containerAttributes;

	public Map<String, Integer> hierarchy_levels;

	public Graph<String, Integer> graph;

	public ParserJung(String path, XmlParser xml) throws JDOMException, IOException {
		clases = new HashSet<String>();
		outRelations = new HashMap<String, Map<String, Float>>();
		inRelations = new HashMap<String, Map<String, Float>>();
		classNamespace = new HashMap<String, String>();
		namespaces = new HashMap<String, Set<String>>();
		containerAttributes = new HashMap<String, String>();

		xml.set(clases, outRelations, inRelations, classNamespace, namespaces, containerAttributes);

		xml.parseArchive(path);

		createGraph();
	}

	@Override
	public void createGraph() {

		System.out.println("Starts building jung graph... " + new Date());
		int numberEdge = 0;
		Graph<String, Integer> g = new DirectedSparseGraph<String, Integer>();

		for (String c : clases)
			g.addVertex(c);

		for (String c : clases) {
			// System.out.println(c);
			Map<String, Float> rel = outRelations.get(c);
			if (rel != null) {
				// System.out.println(rel);
				for (String o : rel.keySet()) {
					g.addEdge(numberEdge, c, o);
					numberEdge++;
				}
			}

//				rel = inRelations.get(c);
//				if(rel!=null){
//					//				System.out.println(rel);
//					for(String o:rel.keySet()){
//						g.addEdge(numberEdge,o,c);
//						numberEdge++;
//					}
//				}
		}
		// System.out.println(g+"\n");
		System.out.println("Finishing gephi graph... " + new Date());
		super.graphContainer = g;
	}

	@Override
	public void showGraph() {
		
		// SimpleGraphView2 sgv = new SimpleGraphView2(); // This builds the graph
		// Layout<V, E>, VisualizationComponent<V,E>
		
		Graph<String, Integer> sgv = (Graph<String, Integer>) super.graphContainer;
		Layout<String, Integer> layout = new CircleLayout(sgv);
		layout.setSize(new Dimension(300, 300));
		BasicVisualizationServer<String, Integer> vv = new BasicVisualizationServer<String, Integer>(layout);
		vv.setPreferredSize(new Dimension(350, 350));
		
		// Setup up a new vertex to paint transformer...
		Transformer<String, Paint> vertexPaint = new Transformer<String, Paint>() {
			public Paint transform(String i) {
				return Color.GREEN;
			}
		};
		
		// Set up a new stroke Transformer for the edges
		float dash[] = { 10.0f };
		final Stroke edgeStroke = new BasicStroke(1.0f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 10.0f, dash,
				0.0f);
		Transformer<Integer, Stroke> edgeStrokeTransformer = new Transformer<Integer, Stroke>() {
			public Stroke transform(Integer s) {
				return edgeStroke;
			}
		};
		
		vv.getRenderContext().setVertexFillPaintTransformer(vertexPaint);
		vv.getRenderContext().setEdgeStrokeTransformer(edgeStrokeTransformer);
		//vv.getRenderContext().setVertexLabelTransformer(new ToStringLabeller());
		//vv.getRenderContext().setEdgeLabelTransformer(new ToStringLabeller());
		vv.getRenderer().getVertexLabelRenderer().setPosition(Position.CNTR);

		JFrame frame = new JFrame("Jung Graph");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().add(vv);
		frame.pack();
		frame.setVisible(true);

	}
	
	
	public void showUP() {
		Graph<String, Integer> sgv = (Graph<String, Integer>) super.graphContainer;

		 Layout < Integer , String > layout = 	 new CircleLayout( sgv);
				 layout . setSize ( new Dimension (350 , 350) );
				 BasicVisualizationServer < Integer , String > vv =	new BasicVisualizationServer < Integer , String >( layout );
				 vv . setPreferredSize ( new Dimension (350 , 350) );
				 JFrame frame = new JFrame (" Jung Graph ");
				 frame . setDefaultCloseOperation ( JFrame . EXIT_ON_CLOSE );
				 frame . getContentPane () . add ( vv );
				 frame . pack () ;
				 frame . setVisible ( true );
	}

}
