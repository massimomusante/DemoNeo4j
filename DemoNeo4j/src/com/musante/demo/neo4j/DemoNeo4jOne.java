/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.musante.demo.neo4j;

import java.util.Calendar;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Relationship;
import org.neo4j.graphdb.Transaction;

/**
 *
 * @author maxx
 */
public class DemoNeo4jOne extends DemoNeo4j
{

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args)
    {
        DemoNeo4jOne demo = new DemoNeo4jOne();
        demo.startup();
        // do the demos
        demo.demoOne();
        // clean exit
        demo.shutdown();
    }

    /**
     * The first demo (hello-&gt;world)
     */
    private void demoOne()
    {
        long t1 = Calendar.getInstance().getTimeInMillis();
        try (final Transaction t = db.beginTx())
        {
            Node n1 = db.createNode();
            n1.setProperty("text", "Hello");
            Node n2 = db.createNode();
            n2.setProperty("text", "World");
            Relationship r = n1.createRelationshipTo(n2, DemoRelationship.linked_to);
            r.setProperty("text", "to all the");
            System.out.println(n1.getProperty("text") + " " + r.getProperty("text") + " " + n2.getProperty("text"));
            r.delete();
            n1.delete();
            n2.delete();
            t.success();
        }
        long t2 = Calendar.getInstance().getTimeInMillis();
        System.out.println("Demo One: " + (t2 - t1));
    }
}
