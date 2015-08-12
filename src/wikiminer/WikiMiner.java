/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package wikiminer;
import bean.Evidence;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import ner.NER;
import tagger.POSTagger;

/**
 *
 * @author samuelpeeters
 */
public class WikiMiner {
    private static final boolean replaceHe = true;
    private static final String fileLocation = "/Users/samuelpeeters/Google Drive/School/COM2/Challenge/SummerSchoolData/IE-Challenge/Wiki-scientists/scientists-TEXT.txt";
    /**
     * @param args the command line arguments
     */
    
    public static void main(String[] args) throws ClassNotFoundException {
        File file = new File(fileLocation); //insert file location here
        try {
            mine(file, replaceHe);
            // TODO code application logic here
        } catch (IOException ex) {
            Logger.getLogger(WikiMiner.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public static void mine(File file, boolean replaceHe) throws IOException, ClassNotFoundException{   //output type may need to be changed
        String rest = deserializeString(file);
        String current;
        String[] split;
        NER ner = new NER();
        POSTagger tag = new POSTagger();
        rest = rest.split("====PAGE-START====",2)[1]; //remove first page start
        do{
            List<Evidence> evidence;
            if(rest.contains("====PAGE-START====")){
                split = rest.split("====PAGE-START====",2);
                rest = split[1];
                current = split[0]; 
            }
            else{
                current = rest;
                rest = null;
            }
            //get subject
            current = current.split("https://en.wikipedia.org/wiki/",2)[1];
            split = current.split("\n",2);
            String subject = split[0].replaceAll("_", " ");
            //replace he/she in split[2]
            if (replaceHe) current = replaceHe(current, subject);
            //remove lines starting with = 
            current = current.replaceAll("(?m)^=.*", "");
            //create Article object
            Article article = new Article(current, subject); 
            article.createPhrases();
            //article.dropPhrases();   //removes phrases with less than 2 entities
            //postag
            tag.Tag(article);
            evidence = ner.annotateArticle(article);
            for(Evidence ev : evidence){
                System.out.println(ev.toString());
            }
        }
        while(rest != null);
    }
    
    public static String replaceHe(String text, String subject){
            text = text.replace("He ", " " + subject + " ");
            text = text.replace(" he ", " " + subject + " ");
            text = text.replace("She ", " " + subject + " ");
            text = text.replace(" she ", " " + subject + " ");
            return text;
    }
    
    public static String deserializeString(File file) throws IOException {
      int len;
      char[] chr = new char[4096];
      final StringBuffer buffer = new StringBuffer();
      final FileReader reader = new FileReader(file);
      try {
          while ((len = reader.read(chr)) > 0) {
              buffer.append(chr, 0, len);
          }
      } finally {
          reader.close();
      }
      return buffer.toString();
  }
    
}
