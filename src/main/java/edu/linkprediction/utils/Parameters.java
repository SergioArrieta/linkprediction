package edu.linkprediction.utils;

import edu.linkprediction.similarityMetrics.*;
import edu.uci.ics.jung.graph.Graph;

import java.util.ArrayList;
import java.util.List;

public class Parameters {

    public static final String[] MOBILEMEDIA_NAMES = {
            "mobilemedia1",
            "mobilemedia2",
            "mobilemedia3",
            "mobilemedia4",
            "mobilemedia5",
            "mobilemedia6",
            "mobilemedia7.",
            "mobilemedia8"
    };

    public static final String[] SUBSCRIBER_DB_NAMES = {
            "sdb1",
            "sdb2",
            "sdb3",
            "sdb4",
            "sdb5",
            "sdb6",
            "sdb7.",
            "sdb8",
            "sdb9",
            "sdb10"
    };

    public static final String[] HIBERNATE_NAMES = {
          //  "hibernate-core-5.0.0.final",
           // "hibernate-core-5.1.0.final",
            "hibernate-core-5.2.0.final",
            "hibernate-core-5.3.0.final"
          //  "hibernate-core-5.4.0.final"
    };

    public static final String[] MOBILEMEDIA = {
            "src/main/resources/sistemas/MobileMedia-odem/mobilemedia1.odem",
            "src/main/resources/sistemas/MobileMedia-odem/mobilemedia2.odem",
            "src/main/resources/sistemas/MobileMedia-odem/mobilemedia3.odem",
            "src/main/resources/sistemas/MobileMedia-odem/mobilemedia4.odem",
            "src/main/resources/sistemas/MobileMedia-odem/mobilemedia5.odem",
            "src/main/resources/sistemas/MobileMedia-odem/mobilemedia6.odem",
            "src/main/resources/sistemas/MobileMedia-odem/mobilemedia7.odem",
            "src/main/resources/sistemas/MobileMedia-odem/mobilemedia8.odem"
    };

    public static final String[] SUBSCRIBER_DB = {
            "src/main/resources/sistemas/subscriberDB-odem/sdb1.odem",
            "src/main/resources/sistemas/subscriberDB-odem/sdb2.odem",
            "src/main/resources/sistemas/subscriberDB-odem/sdb3.odem",
            "src/main/resources/sistemas/subscriberDB-odem/sdb4.odem",
            "src/main/resources/sistemas/subscriberDB-odem/sdb5.odem",
            "src/main/resources/sistemas/subscriberDB-odem/sdb6.odem",
            "src/main/resources/sistemas/subscriberDB-odem/sdb7.odem",
            "src/main/resources/sistemas/subscriberDB-odem/sdb8.odem",
            "src/main/resources/sistemas/subscriberDB-odem/sdb9.odem",
            "src/main/resources/sistemas/subscriberDB-odem/sdb10.odem"
    };

    public static final String[] ANT = {
            "src/main/resources/sistemas/apache-ant/apache-ant-1.1.odem",
            "src/main/resources/sistemas/apache-ant/apache-ant-1.2.odem",
            "src/main/resources/sistemas/apache-ant/apache-ant-1.3.odem",
            "src/main/resources/sistemas/apache-ant/apache-ant-1.4.odem",
            "src/main/resources/sistemas/apache-ant/apache-ant-1.5.odem",
            "src/main/resources/sistemas/apache-ant/apache-ant-1.5.2.odem",
            "src/main/resources/sistemas/apache-ant/apache-ant-1.6.0.odem",
            "src/main/resources/sistemas/apache-ant/apache-ant-1.7.0.odem",
            "src/main/resources/sistemas/apache-ant/apache-ant-1.8.0.odem",
            "src/main/resources/sistemas/apache-ant/apache-ant-1.9.0.odem",
            "src/main/resources/sistemas/apache-ant/apache-ant-1.10.0.odem"
    };

    public static final String[] DERBY = {
            "src/main/resources/sistemas/derby/derby1.odem",
            "src/main/resources/sistemas/derby/derby2.odem",
            "src/main/resources/sistemas/derby/derby3.odem",
            "src/main/resources/sistemas/derby/derby4.odem",
            "src/main/resources/sistemas/derby/derby5.odem",
            "src/main/resources/sistemas/derby/derby6.odem",
            "src/main/resources/sistemas/derby/derby7.odem",
            "src/main/resources/sistemas/derby/derby8.odem",
            "src/main/resources/sistemas/derby/derby9.odem",
            "src/main/resources/sistemas/derby/derby10.odem",
            "src/main/resources/sistemas/derby/derby11.odem",
            "src/main/resources/sistemas/derby/derby12.odem",
            "src/main/resources/sistemas/derby/derby13.odem",
            "src/main/resources/sistemas/derby/derby14.odem"
    };

    public static final String[] HIBERNATE = {
         //   "src/main/resources/sistemas/Hibernate/hibernate-core-5.0.0.final.odem",
           // "src/main/resources/sistemas/Hibernate/hibernate-core-5.1.0.final.odem",
            "src/main/resources/sistemas/Hibernate/hibernate-core-5.2.0.final.odem",
            "src/main/resources/sistemas/Hibernate/hibernate-core-5.3.0.final.odem"
         //   "src/main/resources/sistemas/Hibernate/hibernate-core-5.4.0.final.odem"
    };

    public static List<Similarity> getSimilaritiesSmallSystems(Graph<String, Integer> graph){
        List<Similarity> similarities = new ArrayList();
        similarities.add(new AdamicAdar(Utils.getThresholdsList(3, 0.1, 0.2)));
        similarities.add(new CoeficienteDeJaccard(Utils.getThresholdsList(3, 0.1, 0.1)));
        similarities.add(new CommonNeighbors(Utils.getThresholdsList(3, 0.1, 1)));
        similarities.add(new HubPromoted(Utils.getThresholdsList(3, 0.1, 0.3)));
        similarities.add(new HubDepressed(Utils.getThresholdsList(3, 0.1, 0.1)));
        similarities.add(new PreferentialAttachment(Utils.getThresholdsList(3, 0.1, 20)));
        similarities.add(new ResourceAllocation(Utils.getThresholdsList(3, 0.1, 0.05)));
        similarities.add(new Sorensen(Utils.getThresholdsList(3, 0.1, 0.15)));
        similarities.add(new Katz(Utils.getThresholdsList(3, 0.1, 0.01)));
        similarities.add(new SimRank(graph,Utils.getThresholdsList(3, 0.1, 0.05)));
        return similarities;
    }

    public static List<Similarity> getSimilaritiesMidSystems(Graph<String, Integer> graph){
        List<Similarity> similarities = new ArrayList();
        similarities.add(new AdamicAdar(Utils.getThresholdsList(3, 0.1, 0.2)));
        similarities.add(new CoeficienteDeJaccard(Utils.getThresholdsList(3, 0.1, 0.1)));
        similarities.add(new CommonNeighbors(Utils.getThresholdsList(3, 0.1, 1)));
        similarities.add(new HubPromoted(Utils.getThresholdsList(3, 0.1, 0.3)));
        similarities.add(new HubDepressed(Utils.getThresholdsList(3, 0.1, 0.1)));
        similarities.add(new PreferentialAttachment(Utils.getThresholdsList(3, 0.1, 20)));
        similarities.add(new ResourceAllocation(Utils.getThresholdsList(3, 0.1, 0.05)));
        similarities.add(new Sorensen(Utils.getThresholdsList(3, 0.1, 0.15)));
        similarities.add(new Katz(Utils.getThresholdsList(3, 0.1, 0.01)));
        similarities.add(new SimRank(graph,Utils.getThresholdsList(3, 0.1, 0.05)));
        return similarities;
    }

    public static List<Similarity> getSimilaritiesBigSystems(Graph<String, Integer> graph){
        List<Similarity> similarities = new ArrayList();
        similarities.add(new AdamicAdar(Utils.getThresholdsList(15, 0.1, 0.8)));
        similarities.add(new CoeficienteDeJaccard(Utils.getThresholdsList(15, 0.1, 0.1)));
        similarities.add(new CommonNeighbors(Utils.getThresholdsList(15, 0.1, 10)));
        similarities.add(new HubPromoted(Utils.getThresholdsList(15, 0.1, 0.3)));
        similarities.add(new HubDepressed(Utils.getThresholdsList(15, 0.1, 0.1)));
        similarities.add(new PreferentialAttachment(Utils.getThresholdsList(15, 0.1, 350)));
        similarities.add(new ResourceAllocation(Utils.getThresholdsList(15, 0.1, 0.15)));
        similarities.add(new Sorensen(Utils.getThresholdsList(15, 0.1, 0.1)));
        similarities.add(new Katz(Utils.getThresholdsList(15, 0.1, 0.5)));
        similarities.add(new SimRank(graph,Utils.getThresholdsList(15, 0.1, 0.01)));
        return similarities;
    }

    public static List<Similarity> getSimilaritiesBigSystemsClass(Graph<String, Integer> graph){
        List<Similarity> similarities = new ArrayList();
        similarities.add(new AdamicAdar(Utils.getThresholdsList(15, 0.1, 0.8)));
        similarities.add(new CoeficienteDeJaccard(Utils.getThresholdsList(15, 0.1, 0.1)));
        similarities.add(new CommonNeighbors(Utils.getThresholdsList(15, 0.1, 10)));
        similarities.add(new HubPromoted(Utils.getThresholdsList(15, 0.1, 0.3)));
        similarities.add(new HubDepressed(Utils.getThresholdsList(15, 0.1, 0.1)));
        similarities.add(new PreferentialAttachment(Utils.getThresholdsList(15, 0.1, 350)));
        similarities.add(new ResourceAllocation(Utils.getThresholdsList(15, 0.1, 0.15)));
        similarities.add(new Sorensen(Utils.getThresholdsList(15, 0.1, 0.1)));
        similarities.add(new Katz(Utils.getThresholdsList(15, 0.1, 0.5)));
        similarities.add(new SimRank(graph,Utils.getThresholdsList(15, 0.1, 0.01)));
        return similarities;
    }
}
