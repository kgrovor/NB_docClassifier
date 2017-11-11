package nbdocs_classification;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

 /**
 * 
 * Utility Class for File Input
 */
class FilesUtil {
    public static String readTextFile(String fileName) throws IOException {
        String content = new String(Files.readAllBytes(Paths.get(fileName).toAbsolutePath()));
        return content;
    }

}
/**
 * 
 * Performs all the mathematical calculations
 */
class Compute
{
    long[][] freqtable = new long[89527][2];    
    long numberPositive = 0;
    long numberNegative = 0;
    long numberDocs = 0;
    long correctlyPos = 0,wronglyPos = 0, correctlyNeg = 0, wronglyNeg = 0;
    long numberEqual = 0;
    /**
     * 
     * @param word Index of word
     * @param freq Frequency of given word
     * @param sentiment Positive (1) or Negative (0)
     * Method updates the frequency table and sets the number of positive/negative appearances variable
     */
    void updateFreq(int word,int freq,int sentiment)
    {
        freqtable[word][sentiment] = freqtable[word][sentiment] + freq;
        if (sentiment == 1) {
            numberPositive = numberPositive + freq;
        } else {
            numberNegative = numberNegative + freq;
        }
    }
    /**
     * 
     * @param estimate Estimated Rating
     * @param actual Actual Rating
     */
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
        if(actual == estimate)
        {
            numberEqual++;
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
     public double positivef1()
     {
         return ((2*positivePrecision()*positiveRecall())/(positivePrecision() + positiveRecall()))/100;
     }
      public double negativef1()
     {
         return (((2*negativePrecision()*negativeRecall())/(negativePrecision() + negativeRecall())))/100;
     }
      public double precision()
      {
          return ((double)numberEqual/numberDocs);
      }
    
}


/**
 * 
 * Main Class
 */
public class NBDocs_Classification {

    /**
     * @param train Path to training data file
     * @param test Path to testing data file
     * @param type String with type of classification
     * @param bool_freq Determines if Binary NB or Multivariate
     */
    public NBDocs_Classification(String train,String test, String type, int bool_freq) throws IOException{ 
        binary = bool_freq;
        String input = FilesUtil.readTextFile(train);
        StringTokenizer fileToken = new StringTokenizer(input, "\n"); //"!*^.
        
        while (fileToken.hasMoreTokens())
        {
            newDocument(fileToken.nextToken(),binary);
        }
        
        calculateProb();
        //String test = FilesUtil.readTextFile("data/test/Labeledtest.txt");
        String testdata = FilesUtil.readTextFile(test);
        
        StringTokenizer trainFileToken = new StringTokenizer(testdata, "\n"); //"!*^.
        
        while (trainFileToken.hasMoreTokens())
        {
            testInstance(trainFileToken.nextToken(), binary);
        }
          printStats(type);
    }

    
    Compute compute = new Compute();
    int vocab = 89527;
    double probpos, probneg;
    int binary;
    public static void main(String[] args) throws IOException {
        NBDocs_Classification docClassify = new NBDocs_Classification("../../data/train/labeledBow.txt", "../../data/test/Labeledtest.txt"," Basic ", 0);
        NBDocs_Classification stopwordClassify = new NBDocs_Classification("../../data/no_stopwords_train.txt", "../../data/no_stopwords_test.txt"," Stopword Removed ", 0);
        NBDocs_Classification binClassify = new NBDocs_Classification("../../data/train/labeledBow.txt", "../../data/test/Labeledtest.txt"," Binary ", 1);
        NBDocs_Classification binStopwordClassify = new NBDocs_Classification("../../data/no_stopwords_train.txt", "../../data/no_stopwords_test.txt"," Stopwords Removed Binary ", 1);
          
   }
    /**
     * 
     * Prints output 
     */
    
    public void printStats(String type)
    {
        System.out.println("For Positive Sentiment of" + type + "NB, Precision is " + compute.positivePrecision() + "% , Recall is " + compute.positiveRecall() + "%" + " and F1 measure is " + compute.positivef1()); 
        System.out.println("For Negative Sentiment of" + type + "NB, Precision is " + compute.negativePrecision() + "% , Recall is " + compute.negativeRecall() + "%" + " and F1 measure is " + compute.negativef1()); 
        System.out.println("Overall Accuracy is " + compute.precision()*100 + "%\n");
    }
    /**
     * Calculates P(+ve) / P(-ve) term of Naive Bayes
     */
    public void calculateProb()
    {
        double total = (double)(compute.numberNegative + compute.numberPositive);
        probpos = ((double)compute.numberPositive)/total;
        probneg = ((double)compute.numberNegative)/total;
    }
    /**
     * 
     * @param token Document data
     * @param bool_freq Binary vs Multivariate
     * This method adds a new training instance example to the data
     */
    public void newDocument(String token,int bool_freq)
    {
        int sentiment,word,freq;
        String currentToken;
        StringTokenizer docToken = new StringTokenizer(token, " ");
        if(Integer.parseInt(docToken.nextToken()) >= 7)
                sentiment = 1;
        else 
            sentiment = 0; //Negative -> 0
             
        
        while(docToken.hasMoreTokens())
        {
            currentToken = docToken.nextToken();
            StringTokenizer immediate = new StringTokenizer(currentToken,":");
            word = Integer.parseInt(immediate.nextToken());
            if(bool_freq == 0)
                freq = Integer.parseInt(immediate.nextToken());
            else 
                freq = 1;
            compute.updateFreq(word,freq,sentiment);
        }
    }
    /**
     * 
     * @param token Document data
     * @param bool_freq Binary vs Multivariate
     * This Method determines sentiment for a test example
     */
    public void testInstance(String token, int bool_freq)
    {
        int estimateSentiment,word=0,freq=1,actualSentiment ;
        double pPositive = 0,pNegative = 0, resultProb = 0;
        String currentToken,temp= "temp",temp2="temp";
        StringTokenizer docToken = new StringTokenizer(token, " ");
        if((Integer.parseInt(docToken.nextToken())) >= 7)
                actualSentiment = 1;
        else 
            actualSentiment = 0;
        while(docToken.hasMoreTokens())
        {
            currentToken = docToken.nextToken();
            StringTokenizer immediate = new StringTokenizer(currentToken,":\n");
            word = Integer.parseInt(immediate.nextToken());
            try {
                if(bool_freq == 0)
                    freq = Integer.parseInt(immediate.nextToken());
                else 
                    freq = 1;
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
    
    

