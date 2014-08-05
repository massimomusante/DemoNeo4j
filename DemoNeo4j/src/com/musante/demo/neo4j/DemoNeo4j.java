/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.musante.demo.neo4j;

import java.util.Calendar;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.factory.GraphDatabaseFactory;

/**
 *
 * @author maxx
 */
public class DemoNeo4j
{
    protected final static String DBPATH = "./localdb";
    protected GraphDatabaseService db = null;
    
    /**
     * Shuts down the database
     */
    protected void shutdown()
    {
        long t1 = Calendar.getInstance().getTimeInMillis();
        db.shutdown();
        long t2 = Calendar.getInstance().getTimeInMillis();
        System.out.println("Shutdown: " + (t2 - t1));        
    }
    
    /**
     * Starts up the database 
     * (also register shutdown hook)
     */
    protected void startup()
    {
        long t1 = Calendar.getInstance().getTimeInMillis();
        // instantiate the database
        db = new GraphDatabaseFactory().newEmbeddedDatabase(DBPATH);
        // register shutdown hook
        Runtime.getRuntime().addShutdownHook(new Thread()
        {
            @Override
            public void run()
            {
                db.shutdown();
            }
        });
        long t2 = Calendar.getInstance().getTimeInMillis();
        System.out.println("Startup: " + (t2 - t1));
    }
}