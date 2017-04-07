package org.aksw.sessa.main.Initializer;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.aksw.hawk.datastructures.Answer;
import org.aksw.sessa.main.datastructures.MapperDataStruct;
import org.apache.jena.rdf.model.RDFNode;
import org.aksw.sessa.main.datastructures.NGramStruct;
import org.aksw.sessa.main.datastructures.ResultDataStruct;
import org.apache.jena.query.Query;

/* This is the class for activating the nodes initiiated from the mappings.  */
public class Initializer {
    /* Initiating the list of activated nodes. */
    private List<ResultDataStruct> propagator=new ArrayList<ResultDataStruct>();
    private List<String> totalUrilist = new ArrayList<String>();
	private Sparqlquery query = new Sparqlquery();
    
    
    public void initiate(Map<Integer,MapperDataStruct> urimaps, List<NGramStruct> ngrams){
        List<String> URI;
        List<String> LabelList;
        List<Double> EnergyScoreList;
        for (Map.Entry<Integer, MapperDataStruct> entry : urimaps.entrySet()) {
                int index = entry.getKey();
                URI = entry.getValue().getURIList();
                EnergyScoreList = entry.getValue().getEnergyScore();
                LabelList = entry.getValue().getLabelList();
                totalUrilist = entry.getValue().getURIList();

                /* For each URI in the list of mappings corresponding to respective ngrams, It 
                    activates the node and inserts in the list */
                for(int i=0;i<URI.size();i++){
                    
                    /* Calculating the explaination score */
                    Double explaination_score=(double) (ngrams.get(index).getEnd()-ngrams.get(index).getBegin()+1);
                           
                    /* Activating the new node */
                    ResultDataStruct result=new ResultDataStruct(URI.get(i),explaination_score,EnergyScoreList.get(i),ngrams.get(index).getBegin(),ngrams.get(index).getEnd());
                    result.setActivation("Initial");
                    /* Adding to activated node to the list */
                    result.setImage("No image");
                    this.propagator.add(result);
                }

        } 
    }
    
    /* Retrieving the activation list */
    public List<ResultDataStruct> getResultsList(){   	  	
    	return this.propagator;}
    
    public void addLggresult(){		
		List<RDFNode> lggUrilist = query.getSparqlUrilist();
        for(int i=0;i<lggUrilist.size();i++){
            
            /* Activating the new node */
            ResultDataStruct resultLgg=new ResultDataStruct(lggUrilist.get(i).toString());
            /* Adding to activated node to the list */
            resultLgg.setImage("No image");
            this.propagator.add(resultLgg);
        }
    }
    
	@SuppressWarnings("null")
	public void addLggHawkresult(List<Answer> answerlist){
    	Set<RDFNode> answerS = null;
    	for (Answer answer : answerlist) {
    		answerS.addAll(answer.answerSet);
    	}
    	if(answerS.isEmpty() != true){
    		List<RDFNode> lggHawkUrilist = new ArrayList<RDFNode>(answerS);
    		for(int i=0;i<lggHawkUrilist.size();i++){           
    			/* Activating the new node */
    			ResultDataStruct resultLgg=new ResultDataStruct(lggHawkUrilist.get(i).toString());
    			/* Adding to activated node to the list */
    			resultLgg.setImage("No image");
    			this.propagator.add(resultLgg);
    		} 
    	}
    }
    
    public void setLggQuery(){
    	query.setQuery(totalUrilist());
    }
    
    public Query getLggQuery(){
		return query.getQuery();
    }
    
    
    
    /* Set Uri list */
    public Set<String> totalUrilist(){
    	Set<String> h = new HashSet<>(totalUrilist);  
    	   return h;
    	}
}
