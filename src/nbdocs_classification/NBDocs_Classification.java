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
    long[][] freqtable = new long[89527][2];    
    long numberPositive = 0;
    long numberNegative = 0;
    long numberDocs = 0,correctClass = 0;
    
    void updateFreq(int word,int freq,int sentiment)
    {
        freqtable[word][sentiment] = freqtable[word][sentiment] + freq;
        if (sentiment == 1) {
            numberPositive = numberPositive + freq;
        } else {
            numberNegative = numberNegative + freq;
        }
    }
    
    public void updateStats(int estimate, int actual)
    {
        if(estimate==actual)
            correctClass++;
        numberDocs++;
    }
    
     public double returnAccuracy()
    {
        return ((double)correctClass/numberDocs)*100;
    }
    
}



public class NBDocs_Classification {

    /**
     * @param args the command line arguments
     */
    Compute compute = new Compute();
    int vocab = 89527;
    double probpos, probneg;
    public static void main(String[] args) throws IOException {
        int i;
        NBDocs_Classification docClassify = new NBDocs_Classification();
        String input = FilesUtil.readTextFile("data/train/labeledBow.txt");
        StringTokenizer fileToken = new StringTokenizer(input, "\n"); //"!*^.
        
        while (fileToken.hasMoreTokens())
        {
            docClassify.newDocument(fileToken.nextToken());
        }
        
        docClassify.calculateProb();
        String test = FilesUtil.readTextFile("data/test/Labeledtest.txt");
        
        StringTokenizer trainFileToken = new StringTokenizer(test, "\n"); //"!*^.
        
        while (trainFileToken.hasMoreTokens())
        {
            docClassify.testExample(trainFileToken.nextToken());
        }
        //System.out.println(test);
       //docClassify.testExample(test);
        //docClassify.printtemp();
          docClassify.printAccuracy();
   }
    public void printAccuracy()
    {
        System.out.println(compute.returnAccuracy() + "%"); 
    }
    public void calculateProb()
    {
       // probpos = Math.log10((double)compute.numberPositive) - Math.log((double)(compute.numberNegative + compute.numberPositive));
        //probneg = Math.log10((double)compute.numberNegative) - Math.log((double)(compute.numberNegative + compute.numberPositive));
        double total = (double)(compute.numberNegative + compute.numberPositive);
        probpos = ((double)compute.numberPositive)/total;
        probneg = ((double)compute.numberNegative)/total;
    }
    
    public void printtemp()
    {
        int i;
        for(i=70; i < 75; i++)
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
            sentiment = 0; //Negative -> 0
             
        
        while(docToken.hasMoreTokens())
        {
            currentToken = docToken.nextToken();
            StringTokenizer immediate = new StringTokenizer(currentToken,":");
            word = Integer.parseInt(immediate.nextToken());
            freq = Integer.parseInt(immediate.nextToken());
            compute.updateFreq(word,freq,sentiment);
        }
    }
    public void testExample(String token)
    {
        int estimateSentiment,word=0,freq=1,actualSentiment;
        double pPositive = 0,pNegative = 0, resultProb = 0;
        String currentToken,temp= "temp",temp2="temp";
        StringTokenizer docToken = new StringTokenizer(token, " ");
        if(Character.getNumericValue(docToken.nextToken().charAt(0)) >= 7)
                actualSentiment = 1;
        else 
            actualSentiment = 0;
        while(docToken.hasMoreTokens())
        {
            currentToken = docToken.nextToken();
            StringTokenizer immediate = new StringTokenizer(currentToken,":\n");
            word = Integer.parseInt(immediate.nextToken());
            try {
                freq = Integer.parseInt(immediate.nextToken());
            } catch (Exception e) {
                System.out.println("Extra newline is present");
            }
          
            pPositive = pPositive + (double)freq*( Math.log10(((double)(compute.freqtable[word][1] + 1))) - Math.log10((double)compute.numberPositive + vocab));
            pNegative = pNegative + (double)freq*(Math.log10(((double)(compute.freqtable[word][0] + 1))) - Math.log10(((double)compute.numberNegative + vocab)));
            //System.out.println(Math.log10(((double)(compute.freqtable[word][0] + 1))) + "|||" + Math.log10(((double)compute.numberNegative + vocab)));
            
        }
        
        if((pPositive + Math.log10(probpos)) >= pNegative + Math.log10(probneg))
        {
            
            estimateSentiment = 1;
        }
        else
            estimateSentiment = 0;
     compute.updateStats(estimateSentiment,actualSentiment);
    }
  }
    
    

