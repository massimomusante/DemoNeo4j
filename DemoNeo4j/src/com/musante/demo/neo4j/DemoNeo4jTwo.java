/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.musante.demo.neo4j;

import java.util.Calendar;
import java.util.HashMap;
import org.neo4j.cypher.javacompat.ExecutionEngine;
import org.neo4j.cypher.javacompat.ExecutionResult;
import org.neo4j.graphdb.Label;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.ResourceIterable;
import org.neo4j.graphdb.Transaction;

/**
 * @author maxx
 */
public class DemoNeo4jTwo extends DemoNeo4j
{
    private static final int INITIAL_FILL = 100;
    private static final float PICK_RATIO = 0.2F;
    private static final double BIRTH_RATIO = 0.2F;
    private static final int MAX_CHLDREN = 8;
    
    private static final String GENERATION_QUERY = "match (n:person) "
                                                   + "where not (n-[:married_with]->(:person)) "
                                                   + "and n.sex={sex} "
                                                   + "and rand()>{pick} "
                                                   + "and n.generation={generation} "
                                                   + "return n;";
    
    private String[] mNames = {"Jhon","Jack","Charlie","Don"};
    private String[] fNames = {"Alice","Mary","Cally","Shara"};
    private String[] families = {"Potter","Smith","Doe","Green"};
    
    private final Label rootLabel = new Label()
    {
        @Override
        public String name()
        {
            return "root";
        }
    };
    
    private final Label personLabel = new Label()
    {

        @Override
        public String name()
        {
            return "person";
        }
    };
    
    /**
     * @param args 
     */
    public static void main(String[] args)
    {
        DemoNeo4jTwo demo = new DemoNeo4jTwo();
        demo.startup();
        // do the demos
        demo.fillDb();
        for(int j=0;j<10;j++)
        {
            demo.generation(j);
        }
        // clean exit
        demo.shutdown();
    }
    
    /**
     * Initial database population
     */
    private Node fillDb()
    {
        long t1 = Calendar.getInstance().getTimeInMillis();
        Node result;
        try (final Transaction t = db.beginTx())
        {
            // check for "people" root node
            ResourceIterable<Node> gotRoot = db.findNodesByLabelAndProperty(rootLabel, "type", "root");
            if(gotRoot==null || !gotRoot.iterator().hasNext())
            {
                Node root = db.createNode(rootLabel);
                root.setProperty("type", "root");
                // initial database fill
                for(int j=0;j<INITIAL_FILL;j++)
                {
                    Node person = randomPerson();
                    person.createRelationshipTo(root, PersonRelationships.initial_setup);
                }
                t.success();
                result = root;
            }
            else
            {
                result = gotRoot.iterator().next();
            }
        }
        long t2 = Calendar.getInstance().getTimeInMillis();
        System.out.println("Initial Fill: " + (t2 - t1));
        return result;
    }
    
    /**
     * Make a random person node
     * @return 
     */
    private Node randomPerson()
    {
        String family = families[(int)(Math.random()*families.length)];
        return randomPerson(family, 0);
    }

    /**
     * Make a random person node
     * @return 
     */
    private Node randomPerson(String family, int generation)
    {
        Node person = db.createNode(personLabel);
        String sex = Math.random()>.5?"M":"F";
        String name;
        if("F".equals(sex))
        {
            name = fNames[(int)(Math.random()*fNames.length)];
        }
        else
        {
            name = mNames[(int)(Math.random()*mNames.length)];
        }
        person.setProperty("sex", sex);
        person.setProperty("name", name);
        person.setProperty("family", family);
        person.setProperty("generation", generation);
        return person;
    }
    
    private void generation(int generation)
    {
        long t1 = Calendar.getInstance().getTimeInMillis();
        try (final Transaction t = db.beginTx())
        {
            ExecutionEngine engine = new ExecutionEngine(db);
            // pick some random nodes
            HashMap<String,Object> filter = new HashMap<>();
            filter.put("generation", generation);
            filter.put("pick", PICK_RATIO);
            filter.put("sex", "M");
            // pick males
            ExecutionResult males = engine.execute(GENERATION_QUERY, filter);
            // pick females
            filter.put("sex", "F");
            ExecutionResult females = engine.execute(GENERATION_QUERY, filter);
        
            while(males.columnAs("n").hasNext() && females.columnAs("n").hasNext())
            {
                // mate some of same level nodes            
                Node male = (Node) males.columnAs("n").next();
                Node female = (Node) females.columnAs("n").next();
                male.createRelationshipTo(female, PersonRelationships.married_with);
                // add random children to some random mated nodes
                if(Math.random()>BIRTH_RATIO)
                {
                    final double children = Math.random()*MAX_CHLDREN;
                    for(int j=1;j<children;j++)
                    {
                        Node c = randomPerson((String) male.getProperty("family"), generation + 1);
                        c.createRelationshipTo(male, PersonRelationships.child_of);
                        c.createRelationshipTo(female, PersonRelationships.child_of);
                    }
                }
            }
            t.success();
        }
        long t2 = Calendar.getInstance().getTimeInMillis();
        System.out.println("Generation " + generation + " :" + (t2 - t1));
    }        
}