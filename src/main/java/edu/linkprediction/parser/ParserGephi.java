package edu.linkprediction.parser;

import java.awt.BorderLayout;
import java.awt.Color;
import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.swing.JFrame;

import org.gephi.graph.api.Edge;
import org.gephi.graph.api.GraphController;
import org.gephi.graph.api.GraphModel;
import org.gephi.graph.api.Node;
import org.gephi.preview.api.G2DTarget;
import org.gephi.preview.api.PreviewController;
import org.gephi.preview.api.PreviewModel;
import org.gephi.preview.api.PreviewProperty;
import org.gephi.preview.api.RenderTarget;
import org.gephi.preview.types.DependantOriginalColor;
import org.gephi.preview.types.EdgeColor;
import org.gephi.project.api.Project;
import org.gephi.project.api.ProjectController;
import org.gephi.project.api.Workspace;
import org.jdom2.JDOMException;
import org.openide.util.Lookup;

import edu.linkprediction.parser.gephi.PreviewSketch;
import edu.uci.ics.jung.graph.Graph;

public class ParserGephi extends Parser {

	Set<String> clases; // se puede reemplazar por outRelations.keySet();
	public Map<String, Map<String, Float>> outRelations;
	public Map<String, Map<String, Float>> inRelations;
	Map<String, String> classNamespace;
	public Map<String, Set<String>> namespaces;
	Map<String, String> containerAttributes;

	public Map<String, Integer> hierarchy_levels;

	public Graph<String, Integer> graph;

	// Todo lo de gephi para las stats
	private ProjectController projectController;
	private Workspace workspace;
	private GraphModel graphModel;
	private GraphController graphController;
	private Project project;
	private org.gephi.graph.api.Graph gephiGraph;

	boolean computed = false;

	int total_dependencies = -1;

	public ParserGephi(String path, XmlParser xml) throws JDOMException, IOException {

		clases = new HashSet<String>();
		outRelations = new HashMap<String, Map<String, Float>>();
		inRelations = new HashMap<String, Map<String, Float>>();
		classNamespace = new HashMap<String, String>();
		namespaces = new HashMap<String, Set<String>>();
		containerAttributes = new HashMap<String, String>();

		xml.set(clases, outRelations, inRelations, classNamespace, namespaces, containerAttributes);

		xml.parseArchive(path);
		// System.out.println(namespaces);
        //graph = createGraph();

		// Init a project - and therefore a workspace
		graphController = Lookup.getDefault().lookup(GraphController.class);
		projectController = Lookup.getDefault().lookup(ProjectController.class);
		projectController.newProject();
		project = projectController.getCurrentProject();
		workspace = projectController.getCurrentWorkspace();

		// Get a graph model - it exists because we have a workspace
		graphModel = graphController.getGraphModel(workspace);
		
		createdGraph();
		
		hierarchy_levels = new HashMap<String, Integer>();
		
	}

	@Override
	public void createdGraph() {
		System.out.println("Creating gephi graph... " + new Date());

		org.gephi.graph.api.Graph g = graphModel.getDirectedGraph();

		for (String i : clases) {
			Node n = graphModel.factory().newNode(i);
			n.setLabel(i);
			n.setSize(10f);
			n.setColor(Color.GREEN);
			n.setX((float) ((0.01 + Math.random()) * 1000) - 500);
			n.setY((float) ((0.01 + Math.random()) * 1000) - 500);
			g.addNode(n);
			// g.setId(n,i); //Supuestamente asi le seteo el id al nodo...
		}

		for (String c : clases) {
			// System.out.println(c);

			Node ni = g.getNode(c);

			if (ni == null) { // si no exist�a lo agrego, en esos casos en los que es de clase de java. Va al
								// nodo, pero no va a la lista de clases.
				ni = graphModel.factory().newNode(c);
				// ni.setLabel(c);
				ni.setSize(10f);
				ni.setColor(Color.GREEN);
				ni.setX((float) ((0.01 + Math.random()) * 1000) - 500);
				ni.setY((float) ((0.01 + Math.random()) * 1000) - 500);
				g.addNode(ni);
				// g.setId(ni,c); //Supuestamente asi le seteo el id al nodo...
			}

			Map<String, Float> rel = outRelations.get(c);
			if (rel != null) {
            //System.out.println(rel);
				for (String o : rel.keySet()) {

					Node nj = g.getNode(o);

					if (nj == null) {
						nj = graphModel.factory().newNode(o);
						nj.setLabel(o);
						// nj.setLabel(c);
						nj.setSize(10f);
						nj.setColor(Color.GREEN);
						nj.setX((float) ((0.01 + Math.random()) * 1000) - 500);
						nj.setY((float) ((0.01 + Math.random()) * 1000) - 500);
						g.addNode(nj);
						// g.setId(nj,o); //Supuestamente asi le seteo el id al nodo...
					}

                    //if(ni != null && nj != null){
					Edge e = graphModel.factory().newEdge(ni, nj);
					// e.setWeight(outRelations.get(c).get(o)); //En el graph general va a tener una
					// relaci�n de 1
					g.addEdge(e);
                    //}
				}
			}

		}

		System.out.println("Finishing gephi graph... " + new Date());
		super.graphContainer = g;
	}
	
	public void showGraph() {
		
		PreviewController previewController = Lookup.getDefault().lookup(PreviewController.class);
		PreviewModel previewModel = previewController.getModel();
		EdgeColor color= new EdgeColor(Color.BLACK);
		
		previewModel.getProperties().putValue(PreviewProperty.SHOW_NODE_LABELS, Boolean.FALSE);
		previewModel.getProperties().putValue(	PreviewProperty.EDGE_COLOR, color);
		previewModel.getProperties().putValue(PreviewProperty.NODE_LABEL_COLOR,
				new DependantOriginalColor(Color.WHITE));
		previewModel.getProperties().putValue(PreviewProperty.EDGE_CURVED, Boolean.TRUE);
		previewModel.getProperties().putValue(PreviewProperty.EDGE_OPACITY, 50);
		previewModel.getProperties().putValue(PreviewProperty.EDGE_RADIUS, 10f);
		previewModel.getProperties().putValue(PreviewProperty.BACKGROUND_COLOR, Color.WHITE);
		previewController.refreshPreview();

		// New Processing target, get the PApplet
		G2DTarget target = (G2DTarget) previewController.getRenderTarget(RenderTarget.G2D_TARGET);
		final PreviewSketch previewSketch = new PreviewSketch(target);
		previewController.refreshPreview();

		// Add the applet to a JFrame and display
		JFrame frame = new JFrame("Test Preview");
		frame.setLayout(new BorderLayout());

		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.add(previewSketch, BorderLayout.CENTER);

		frame.setSize(1024, 768);
		frame.setVisible(true);
	}

}
