package org.aksw.sessa.main.propagator.interfaces;

import java.util.List;

import org.aksw.sessa.main.datastructures.ResultDataStruct;
import org.neo4j.graphdb.GraphDatabaseService;

public interface PropagatorInterface {
public void PropagateInit(GraphDatabaseService db, List<ResultDataStruct> results);

public List<ResultDataStruct> getFinalResults();
}
