/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.musante.demo.neo4j;

import java.util.Calendar;
import org.neo4j.cypher.javacompat.ExecutionEngine;
import org.neo4j.graphdb.Transaction;

/**
 *
 * @author maxx
 */
public class DemoNeo4jThree extends DemoNeo4j
{
    private static final String DEL_QUERY = "optional match n-[r]->m delete r,n,m;" ;           

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args)
    {
        DemoNeo4jThree demo = new DemoNeo4jThree();
        demo.startup();
        // do the demos
        demo.demoThree();
        // clean exit
        demo.shutdown();
    }
    
    private void demoThree()
    {
        long t1 = Calendar.getInstance().getTimeInMillis();
        try (final Transaction t = db.beginTx())
        {
            // TODO empty the DB from nodes and relationships
            ExecutionEngine engine = new ExecutionEngine(db);
            engine.execute(DEL_QUERY);
            t.success();
        }
        long t2 = Calendar.getInstance().getTimeInMillis();
        System.out.println("Demo Three (delete): " + (t2 - t1));
    }
}
