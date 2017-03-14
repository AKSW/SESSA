
package org.aksw.sessa.main.Initializer.interfaces;

import java.util.List;
import java.util.Map;
import java.util.Set;

import org.aksw.hawk.datastructures.Answer;
import org.aksw.sessa.main.datastructures.MapperDataStruct;
import org.apache.jena.query.Query;
import org.aksw.sessa.main.datastructures.NGramStruct;
import org.aksw.sessa.main.datastructures.ResultDataStruct;

public interface InitializerInterface {
    public void initiate (Map<Integer,MapperDataStruct> urimaps, List<NGramStruct> ngrams);
    public List<ResultDataStruct> getResultsList();
    public Set<String> totalUrilist();
    public void addLggresult();
    public void setLggQuery();
    public Query getLggQuery();
	public void addLggHawkresult(List<Answer> answerlist);
}
