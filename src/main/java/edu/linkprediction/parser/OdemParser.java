package edu.linkprediction.parser;

import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.jdom2.Attribute;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.input.SAXBuilder;

public class OdemParser extends XmlParser{

	Map<String, String> containerAttributes;
	
	@SuppressWarnings("unchecked")
	public void set(Object...objects){
		clases = (Set<String>)objects[0];
		outRelations = (Map<String, Map<String, Float>>)objects[1];
		inRelations = (Map<String, Map<String, Float>>)objects[2];
		classNamespace = (Map<String, String>)objects[3];
		namespaces = (Map<String, Set<String>>)objects[4];
		containerAttributes = (Map<String, String>)objects[5];
	}
	
	public void parseArchive(String xmlSource) throws JDOMException, IOException {
//		System.out.println("Starts parsing... "+new Date());
		SAXBuilder jdomBuilder = new SAXBuilder();
		jdomBuilder.setFeature("http://apache.org/xml/features/nonvalidating/load-external-dtd", false);
		Document jdomDocument = jdomBuilder.build(xmlSource);

		Element container = jdomDocument.getRootElement().getChild("context").getChild("container");

		List<Attribute> attsContainer = container.getAttributes();
		for(Attribute a : attsContainer){
			containerAttributes.put(a.getName(), a.getValue());
		}

		List<Element> rootNode = container.getChildren("namespace");
		for(Element n:rootNode){ //namespaces
			List<Element> list = n.getChildren("type");
			String namespace = n.getAttributeValue("name");
			for(Element e:list){  //classes 
				String c = e.getAttributeValue("name");
				
				if(c.contains("$"))
					c = c.substring(0,c.indexOf("$"));
				
				classNamespace.put(c, namespace);
				//    			System.out.println(c);
				Set<String> ns = namespaces.get(namespace);
				if(ns==null){
					ns = new HashSet<String>();
					ns.add(c);
					namespaces.put(namespace,ns);
				}
				else
					ns.add(c);

				clases.add(c);
				List<Element> dependencies = e.getChild("dependencies").getChildren();
				
				Map<String,Float> dep = outRelations.get(c);
				if(dep == null){
					dep = new HashMap<String,Float>(); //out
					outRelations.put(c,dep);
				}
					
				for(Element d:dependencies){ //dependencies
					String cd = d.getAttributeValue("name");
					
					if(cd.contains("$"))
						cd = cd.substring(0,cd.indexOf("$"));
					
					dep.put(cd, 1.0f);
					Map<String,Float> in = inRelations.get(cd);
					if(in==null){ //in
						in = new HashMap<String,Float>();
						in.put(c,1.0f);
						inRelations.put(cd, in);
					}
					else
						in.put(c,1.0f);
				}
				outRelations.put(c, dep);    			
			}
		}
		
		 inRelations.keySet().retainAll(clases); //todas las que está eliminando son de afuera...
	        for(String c:clases){
	            outRelations.get(c).keySet().retainAll(clases);
	            Map<String,Float> i = inRelations.get(c);
	            if(i!=null)
	                i.keySet().retainAll(clases);
	        }
			
	}
	
	@Override
	public String toString() {
		return "odem";
	}

	@Override
	public String getSuffix() {
		return "odem";
	}
}
