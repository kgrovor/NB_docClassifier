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
    long numberDocs = 0;
    long correctlyPos = 0,wronglyPos = 0, correctlyNeg = 0, wronglyNeg = 0;
    
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
        if(actual == 1)
        {
            if(estimate == 1)
                correctlyPos++;
            else 
                wronglyNeg++;
        }
        else if(actual == 0)
        {
            if (estimate == 0) {
                correctlyNeg++;
            } else {
                wronglyPos++;
            }
        }
       numberDocs++;
    }
    
     public double positivePrecision()
    {
        return ((double)correctlyPos/(correctlyPos + wronglyPos))*100;
    }
     public double negativePrecision()
    {
        return ((double)correctlyNeg/(correctlyNeg + wronglyNeg))*100;
    }
     public double positiveRecall()
     {
         return ((double)correctlyPos/(correctlyPos + wronglyNeg))*100;
     }
     public double negativeRecall()
     {
         return ((double)correctlyNeg/(correctlyNeg + wronglyPos))*100;
     }
    
}



public class NBDocs_Classification {

    /**
     * @param args the command line arguments
     */
    public NBDocs_Classification(String train,String test, String type) throws IOException{
        String input = FilesUtil.readTextFile(train);
        StringTokenizer fileToken = new StringTokenizer(input, "\n"); //"!*^.
        
        while (fileToken.hasMoreTokens())
        {
            newDocument(fileToken.nextToken());
        }
        
        calculateProb();
        //String test = FilesUtil.readTextFile("data/test/Labeledtest.txt");
        String testdata = FilesUtil.readTextFile(test);
        
        StringTokenizer trainFileToken = new StringTokenizer(testdata, "\n"); //"!*^.
        
        while (trainFileToken.hasMoreTokens())
        {
            testInstance(trainFileToken.nextToken());
        }
          printStats(type);
    }

    
    Compute compute = new Compute();
    int vocab = 89527;
    double probpos, probneg;
    public static void main(String[] args) throws IOException {
        int i;
        NBDocs_Classification docClassify = new NBDocs_Classification("data/train/labeledBow.txt", "data/test/Labeledtest.txt"," Basic ");
        NBDocs_Classification stopwordClassify = new NBDocs_Classification("data/no_stopwords_train.txt", "data/no_stopwords_test.txt"," Stopword Removed ");

          
   }
    public void printStats(String type)
    {
        System.out.println("For Positive Sentiment of" + type + "NB, Precision is " + compute.positivePrecision() + "% and Recall is " + compute.positiveRecall() + "%"); 
        System.out.println("For Negative Sentiment of" + type + "NB, Precision is " + compute.negativePrecision() + "% and Recall is " + compute.negativeRecall() + "%"); 
    }
    public void calculateProb()
    {
        double total = (double)(compute.numberNegative + compute.numberPositive);
        probpos = ((double)compute.numberPositive)/total;
        probneg = ((double)compute.numberNegative)/total;
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
    public void testInstance(String token)
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
    
    

