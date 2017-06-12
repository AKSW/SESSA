package org.aksw.sessa.query.processing;

import org.aksw.sessa.query.models.NGramHierarchy;


public interface QueryProcessingInterface {

  NGramHierarchy processQuery(String query);
}
