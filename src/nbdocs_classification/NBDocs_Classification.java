package nbdocs_classification;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;


import java.util.*;

class FilesUtil {
    public static String readTextFile(String fileName) throws IOException {
        String content = new String(Files.readAllBytes(Paths.get(fileName).toAbsolutePath()));
        return content;
    }

}
class Compute
{
    int[][] freqtable = new int[89527][2];
    void updateFreq(int word,int freq,int sentiment)
    {
        freqtable[word][sentiment] = freqtable[word][sentiment] + freq;
    }
    
}

public class NBDocs_Classification {

    /**
     * @param args the command line arguments
     */
    Compute compute = new Compute();
    public static void main(String[] args) throws IOException {
        int i;
        NBDocs_Classification docClassify = new NBDocs_Classification();
        String input = FilesUtil.readTextFile("data/train/labeledBow.txt");
        StringTokenizer fileToken = new StringTokenizer(input, "\n"); //"!*^.
        
        while (fileToken.hasMoreTokens())
        {
            docClassify.newDocument(fileToken.nextToken());
        }
        docClassify.printtemp();
   }
    public void printtemp()
    {
        int i;
        for(i=0; i < 5; i++)
        {
            
            System.out.println(compute.freqtable[i][1]);
        }
    }
    public void newDocument(String token)
    {
        int sentiment,word,freq;
        String currentToken;
        StringTokenizer docToken = new StringTokenizer(token, " ");
        if(Character.getNumericValue(docToken.nextToken().charAt(0)) >= 7)
                sentiment = 1;
        else 
            sentiment = 0;
             
        
        while(docToken.hasMoreTokens())
        {
            currentToken = docToken.nextToken();
            StringTokenizer immediate = new StringTokenizer(currentToken,":");
            word = Integer.parseInt(immediate.nextToken());
            freq = Integer.parseInt(immediate.nextToken());
            compute.updateFreq(word,freq,sentiment);
        }
    }
  }
    
    

