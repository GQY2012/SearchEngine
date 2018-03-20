

package demo;

import java.io.*;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.queryparser.classic.MultiFieldQueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Version;
import org.wltea.analyzer.lucene.IKAnalyzer;

public class Search{
	static String fields[] = { "title", "description", "keywords", "content" };
	IndexSearcher searcher;
	Analyzer analyzer;
	ScoreDoc[] hits;
	Document[] hit_docs;
	File indexDir = new File("C:\\Users\\95850\\Desktop\\DIC");
	
	public Search() throws Exception {
		analyzer = new IKAnalyzer();
		IndexReader reader = DirectoryReader.open(FSDirectory.open(indexDir)); 
		searcher = new IndexSearcher(reader); 
	} 
	public String get_content(int i, String field){
		return hit_docs[i].get(field);
	}
	
	public int do_search(String qs,int topN) throws Exception {
		MultiFieldQueryParser parser = new MultiFieldQueryParser(Version.LUCENE_44, fields, analyzer);
		Query q = parser.parse(qs); 
		hits = searcher.search(q, topN).scoreDocs;
		int n = hits.length;
		hit_docs = new Document[hits.length];
		for(int i = 0;i < hits.length;i++) {
			hit_docs[i]= searcher.doc(hits[i].doc); 
		}
		return n;
	}
}