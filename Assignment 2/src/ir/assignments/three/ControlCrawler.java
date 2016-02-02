package ir.assignments.three;

/**
 * Created by VGDC_1 on 1/18/2016.
 *
 * Derek Edrich, Khang Tran, Carl Pacheco, Brett Lenz
 *
 * Example used: https://github.com/yasserg/crawler4j/tree/master/src/test/java/edu/uci/ics/crawler4j/examples/basic
 */

import com.sleepycat.je.txn.LockerFactory;
import edu.uci.ics.crawler4j.crawler.CrawlConfig;
import edu.uci.ics.crawler4j.crawler.CrawlController;
import edu.uci.ics.crawler4j.fetcher.PageFetcher;
import edu.uci.ics.crawler4j.robotstxt.RobotstxtConfig;
import edu.uci.ics.crawler4j.robotstxt.RobotstxtServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.util.*;

public class ControlCrawler {
    private static Logger logger = LoggerFactory.getLogger(ControlCrawler.class);
    static HashMap<String, Integer> subDomainMap;

    public static void main(String[] args) throws Exception {

        long start = System.currentTimeMillis();
        if (args.length != 2) {
            System.out.println("Needed parameters: ");
            System.out.println("\t rootFolder (it will contain intermediate crawl data)");
            System.out.println("\t numberOfCrawlers (number of concurrent threads)");
            return;
        }


    /*
     * crawlStorageFolder is a folder where intermediate crawl data is
     * stored.
     */
        String crawlStorageFolder = args[0];

        /*
     * numberOfCrawlers shows the number of concurrent threads that should
     * be initiated for crawling.
     */
        int numberOfCrawlers = Integer.parseInt(args[1]);

        CrawlConfig config = new CrawlConfig();

        config.setCrawlStorageFolder(crawlStorageFolder);
        config.setFollowRedirects(false);

        File folder = new File("pages");
        if (folder.exists()) {
            String[] files = folder.list();
            for (String file: files)
            {
                File currentFile = new File(folder.getPath(),file);
                currentFile.delete();
            }
        }
        else {
            if (!folder.mkdirs()) {
                throw new Exception("couldn't create the storage folder: " + folder.getAbsolutePath() + " does it already exist ?");
            } else {
                System.out.println("Created folder: " + folder.getAbsolutePath());
            }
        }

        subDomainMap = new HashMap<String, Integer>();
    /*
     * Be polite: Make sure that we don't send more than 1 request per
     * second (1000 milliseconds between requests).
     */
        config.setPolitenessDelay(1000);

    /*
     * You can set the maximum crawl depth here. The default value is -1 for
     * unlimited depth
     */
        config.setMaxDepthOfCrawling(-1);

    /*
     * You can set the maximum number of pages to crawl. The default value
     * is -1 for unlimited number of pages
     */
        config.setMaxPagesToFetch(-1);


        config.setUserAgentString("UCI Inf141-CS121 crawler 34363846 47508988 76382638 47911659");

        /**
         * Do you want crawler4j to crawl also binary data ?
         * example: the contents of pdf, or the metadata of images etc
         */
        config.setIncludeBinaryContentInCrawling(false);

    /*
     * This config parameter can be used to set your crawl to be resumable
     * (meaning that you can resume the crawl from a previously
     * interrupted/crashed crawl). Note: if you enable resuming feature and
     * want to start a fresh crawl, you need to delete the contents of
     * rootFolder manually.
     */
        config.setResumableCrawling(false);

    /*
     * Instantiate the controller for this crawl.
     */
        PageFetcher pageFetcher = new PageFetcher(config);
        RobotstxtConfig robotstxtConfig = new RobotstxtConfig();
        RobotstxtServer robotstxtServer = new RobotstxtServer(robotstxtConfig, pageFetcher);
        CrawlController controller = new CrawlController(config, pageFetcher, robotstxtServer);

            /*
     * For each crawl, you need to add some seed urls. These are the first
     * URLs that are fetched and then the crawler starts following links
     * which are found in these pages
     */
        controller.addSeed("http://www.ics.uci.edu/");

    /*
     * Start the crawl. This is a blocking operation, meaning that your code
     * will reach the line after this only when crawling is finished.
     */
        controller.start(Crawler.class, numberOfCrawlers);

        System.out.println("Runtime: " + (System.currentTimeMillis()-start) + "ms");

        //Writes subdomains to text files
        List<String> sortedSubDomains  = new ArrayList<String>(subDomainMap.keySet());

        sortedSubDomains.sort((String a, String b) -> {
            return a.compareTo(b);
        });

        File subDomains = new File("subdomains.txt");

        if (subDomains.exists())
            subDomains.delete();

        FileWriter fOut = new FileWriter("subdomains.txt");

        for (String subDomain: sortedSubDomains)
        {
            fOut.write(subDomain + ", " + subDomainMap.get(subDomain) + "\n");
        }
        fOut.close();

        // Processing sub domains - word counts

        List<String> word = new ArrayList<>();
        int maxIndex = 0;
        int maxWordCount = 0;
        String[] files = folder.list();

        HashSet<String> stopWords = new HashSet<>(Arrays.asList("a", "about", "above", "after", "again", "against", "all", "am", "an", "and", "any", "are", "aren't", "as", "at", "be", "because", "been", "before", "being", "below", "between", "both", "but", "by", "can't", "cannot", "could", "couldn't", "did", "didn't", "do", "does", "doesn't", "doing", "don't", "down", "during", "each", "few", "for", "from", "further", "had", "hadn't", "has", "hasn't", "have", "haven't", "having", "he", "he'd", "he'll", "he's", "her", "here", "here's", "hers", "herself", "him", "himself", "his", "how", "how's", "i", "i'd", "i'll", "i'm", "i've", "if", "in", "into", "is", "isn't", "it", "it's", "its", "itself", "let's", "me", "more", "most", "mustn't", "my", "myself", "no", "nor", "not", "of", "off", "on", "once", "only", "or", "other", "ought", "our", "ours", "ourselves", "out", "over", "own", "same", "shan't", "she", "she'd", "she'll", "she's", "should", "shouldn't", "so", "some", "such", "than", "that", "that's", "the", "their", "theirs", "them", "themselves", "then", "there", "there's", "these", "they", "they'd", "they'll", "they're", "they've", "this", "those", "through", "to", "too", "under", "until", "up", "very", "was", "wasn't", "we", "we'd", "we'll", "we're", "we've", "were", "weren't", "what", "what's", "when", "when's", "where", "where's", "which", "while", "who", "who's", "whom", "why", "why's", "with", "won't", "would", "wouldn't", "you", "you'd", "you'll", "you're", "you've", "your", "yours", "yourself", "yourselves"));

        //for (String file: files)
        for(int i = 0; i < files.length; i++)
        {
            List<String> words = Utilities.tokenizeFile(new File("pages/"+ files[i]));
            word.addAll(words);

            if(words.size() > maxWordCount)
            {
                maxWordCount = words.size();
                maxIndex = i;
            }
        }

        System.out.println("Largest file: " + files[maxIndex] + ' ' + maxWordCount);

        word.removeAll(stopWords);

        //done Filter based on stopwords
        List<Frequency> frequencies = WordFrequencyCounter.computeWordFrequencies(word);

        frequencies.sort((Frequency a, Frequency b) -> {
            if (Integer.compare(b.getFrequency(),a.getFrequency())== 0)
            {
                return a.getText().compareTo(b.getText());
            }
            else
                return Integer.compare(b.getFrequency(),a.getFrequency());
        });

        File commonWords = new File("CommonWords.txt");
        FileWriter cwOut = new FileWriter("CommonWords.txt");

        if (commonWords.exists())
            commonWords.delete();
            
        // Check to make to sure this is correct cause I added this through the github website

//        for (int i = 0; i < 1; i++)
//            cwOut.write(frequencies.get(i).getText() + " " + frequencies.get(i).getFrequency());

        System.out.println(frequencies);

        cwOut.close();
    }
}
