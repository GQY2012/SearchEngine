package demo;  
  
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.file.Files;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.lucene.analysis.Analyzer;  
import org.apache.lucene.analysis.standard.StandardAnalyzer;  
import org.apache.lucene.document.Document;  
import org.apache.lucene.document.Field.Store;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;  
import org.apache.lucene.index.IndexWriter;  
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.store.IOContext;
import org.apache.lucene.store.RAMDirectory;
import org.apache.lucene.util.Version;  
import org.jsoup.*;
import org.wltea.analyzer.lucene.IKAnalyzer;

  
public class Indexer { 
	final private static String[] useless_tags = {"<a>","</a>","<em>","</em>","<span>","</span>","<strong>","</strong>","<iframe>","</iframe>","<&nbsp>"};
	final private static Pattern pt_title = Pattern.compile("(<title>)(.*)(</title>)");
    final private static Pattern pt_url = Pattern.compile("(<url>)(.*)(</url>)");
    final private static Pattern pt_HTML = Pattern.compile("<([^>]*)>");
    
    final private static File indexDir = new File("C:\\Users\\95850\\Desktop\\DIC");  
    // dataDir is the directory that hosts the text files that to be indexed  
    final private static File dataDir = new File("C:\\Users\\95850\\Desktop\\News");  
	// index per File, which contains lots of docs.
    private static Analyzer luceneAnalyzer;
    private static IndexWriterConfig indexWriterConfig;
    private static IndexWriter indexWriter;
    private static Directory ramDirectory;
    
    
    
    public static String removeUselessTags(String s){
	    StringBuilder s1 = new StringBuilder(s);
	    for(String tag:useless_tags){
	        if(s1.indexOf(tag) > -1){
	            s1.delete(s1.indexOf(tag), s1.indexOf(tag)+tag.length());
            }
        }
	    return s1.toString();
    }
    
	public static void Pre_Treatment(File file) throws IOException{
        BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(file),"UTF-8"));
        int count = 0;
        String content;
        while((content = br.readLine()) != null) {
            if(!content.equals("<doc>"))
                continue;
            if(count %400 == 0){
                System.out.print(".");
            }
            count++;
            Document document = new Document();
            document.add(new TextField("path", file  
                    .getCanonicalPath(), Store.YES)); 
            StringBuffer treated_content = new StringBuffer();
            while((content = br.readLine()) != null){
                content = removeUselessTags(content);
                if(content.length() == 0)
                    continue;
                if(content.equals("</doc>")) {
                    break;
                }
                if(content.charAt(0) != '<'){
                    treated_content.append(content);
                    continue;
                }
                if(content.charAt(1) == 'm'){
                	org.jsoup.nodes.Document doc = Jsoup.parse(content);
                	org.jsoup.select.Elements attribute = doc.select("meta");
                    if(attribute.size() == 0)
                        continue;
                    if(attribute.attr("name").equals("keywords"))
                        document.add(new StringField("keywords", attribute.attr("content"), Store.YES));
                    else if(attribute.attr("name").equals("description"))
                        document.add(new StringField("description", attribute.attr("content"), Store.YES));
                    else if(attribute.attr("name").equals("publishid"))
                        document.add(new StringField("publishid", attribute.attr("content"), Store.YES));
                    else if(attribute.attr("name").equals("subjectid"))
                        document.add(new StringField("subjectid", attribute.attr("content"), Store.YES));
                }
                else{
                    Matcher m = pt_title.matcher(content);
                    String tmp;
                    if(m.find()){
                        tmp = m.group(2).trim();
                        document.add(new StringField("title", tmp, Store.YES));
                    }
                    m = pt_url.matcher(content);
                    if(m.find()){
                        tmp = m.group(2).trim();
                        document.add(new StringField("url", tmp, Store.YES));
                    }
                }
            }
            Matcher m = pt_HTML.matcher(treated_content);
            StringBuffer sb = new StringBuffer();
            while(m.find()){
                m.appendReplacement(sb, "");
            }
            m.appendTail(sb);
            treated_content = sb;
            document.add(new TextField("contents", treated_content.toString(), Store.YES));
            indexWriter.addDocument(document); 
            indexWriter.commit();  
        }
        br.close();
    }
    
	public static void main(String[] args) throws Exception {    
        // 对文档进行分词  
		Directory fsDirectory = FSDirectory.open(indexDir);
		ramDirectory = new RAMDirectory();
       luceneAnalyzer = new IKAnalyzer();  
       File[] dataFiles = dataDir.listFiles();  
       indexWriterConfig = new IndexWriterConfig(  
                Version.LUCENE_44, luceneAnalyzer);  
        // 创建索引  
       indexWriter = new IndexWriter(fsDirectory,  
                indexWriterConfig);  
        long startTime = new Date().getTime();  
        for (int i = 0; i < dataFiles.length; i++) {  
            if (dataFiles[i].isFile()  
                    && dataFiles[i].getName().endsWith(".txt")) {  
                System.out.println("Indexing file "  
                        + dataFiles[i].getCanonicalPath());  
                Pre_Treatment(dataFiles[i]);
            }  
        }
        indexWriter.close();
        /*
        for(String file : ramDirectory.listAll()) {
        	fsDirectory.copy(ramDirectory, file, file, IOContext.DEFAULT);
       }*/
        fsDirectory.close();
        long endTime = new Date().getTime();  
        System.out.println("It takes " + (endTime - startTime)  
                + " milliseconds to create index for the files in directory "  
                + dataDir.getPath());  
    }  
  
}  