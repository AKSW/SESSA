package org.aksw.sessa.main.uinterface;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;

import org.aksw.sessa.main.Initializer.Initializer;
import org.aksw.sessa.main.datastructures.ListFunctions;
import org.aksw.sessa.main.datastructures.ResultDataStruct;
import org.aksw.sessa.main.importer.Neo4j;
import org.aksw.sessa.main.indexer.ESNode;
import org.aksw.sessa.main.ngramgenerator.NGramModel;
import org.aksw.sessa.main.propagator.Propagator;
import org.aksw.sessa.main.serverproperties.Pathvariables;
import org.aksw.sessa.main.urimapper.Mapper;
import org.elasticsearch.search.SearchHit;
import org.neo4j.graphdb.GraphDatabaseService;

public class Uiclass {
    private Pathvariables Instance;
    public Uiclass() throws IOException{
            this.Instance=new Pathvariables();
    }
    private File[] rdffileiterator(){
        File folder = new File(this.Instance.getrdf());
        File[] listOfFiles = folder.listFiles();
        return listOfFiles;
    }
    private String graphpath(){
        
        return this.Instance.getgraph();
    }
    public static void main(String[] args) throws IOException {
        /*Cluster Initialization*/
        ESNode esnode=new ESNode();
        esnode.startCluster("DBpediacluster");
       
        /*Indexing of classes*/
        esnode.rdfcluster("./resources/dbpedia_3Eng_class.ttl","classes");
        
        /*Indexing of Properties*/
        esnode.rdfcluster("./resources/dbpedia_3Eng_property.ttl","properties");
        
        /*Enriching them with surfaceforms*/
        esnode.rdfcluster("./resources/en_surface_forms.ttl", "surfaceforms");
        
        /*Indexing DBpedia labels*/
        esnode.rdfcluster("./resources/dbpedia_labels.ttl", "dbpedialabels");
        
        esnode.datatypeindex("./resources/datatypes", "datatypes");
        
        /*A simple query and results*/
        SearchHit[] results=esnode.transportclient("8000kilometre", "datatypes");
        for (SearchHit hit : results) {
            Map<String,Object> result = hit.getSource();
            System.out.println(result.values());
        }
        System.out.println("=================================");
        String Query = "Bristol City FC gender";
        NGramModel ngrams = new NGramModel();
        ngrams.createNGramModel(Query);
        
        Mapper mappings = new Mapper();
        mappings.BuildMappings(esnode,ngrams.getNGramMod());
        Initializer init= new Initializer();
        init.initiate(mappings.getMappings(),ngrams.getNGramMod());
        
        /* extracting paths where the graphdb has to be formed*/
        Uiclass pathsetter =new Uiclass();
        File[] listoffiles = pathsetter.rdffileiterator();
        String graphpath = pathsetter.graphpath();
        
        /* Formation of graph database at specified path*/
        Neo4j graphdb = new Neo4j(graphpath);
        GraphDatabaseService gdb=graphdb.getgdbservice();
        System.out.println("asfasf");
        for (File file : listoffiles) { 
          if (file.isFile()) /*extracting all the files in the specified folder*/ {
               graphdb.graphdbform( pathsetter.Instance.getrdf()+'/'+file.getName());
           }
        }   
        Propagator finalresults = new Propagator();
        finalresults.PropagateInit(gdb,init.getResultsList());       
        List<ResultDataStruct> actual= init.getResultsList();
        
        /* Sorting the final nodes according to explanation score and energy score  */
        ListFunctions.sortresults(actual);
        System.out.println(" Retrieving results in decreasing order of relevancy.... ");
        System.out.println();
        for(int i=actual.size()-1;i>=0;i--){
        	
            System.out.println(" URI : "+actual.get(i).getURI()+", Colors : "+actual.get(i).getImage()+"}");
        }
    }
}
