/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.musante.demo.neo4j;

import org.neo4j.graphdb.RelationshipType;

/**
 *
 * @author maxx
 */
public enum PersonRelationships implements RelationshipType
{
    child_of,
    married_with,
    initial_setup
}
