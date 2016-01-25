package ir.assignments.three;

/**
 * Created by VGDC_1 on 1/18/2016.
 *
 * Derek Edrich, Khang Tran, Carl Pacheco, Brett Lenz
 *
 * Example used: https://github.com/yasserg/crawler4j/tree/master/src/test/java/edu/uci/ics/crawler4j/examples/basic
 */


import edu.uci.ics.crawler4j.crawler.CrawlConfig;
import edu.uci.ics.crawler4j.crawler.CrawlController;
import edu.uci.ics.crawler4j.fetcher.PageFetcher;
import edu.uci.ics.crawler4j.robotstxt.RobotstxtConfig;
import edu.uci.ics.crawler4j.robotstxt.RobotstxtServer;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

public class ControlCrawler {
    static HashMap<String, Integer> subDomainMap;

    public static void main(String[] args) throws Exception {
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
        config.setMaxDepthOfCrawling(0);

    /*
     * You can set the maximum number of pages to crawl. The default value
     * is -1 for unlimited number of pages
     */
        config.setMaxPagesToFetch(1000);


        config.setUserAgentString("UCI Inf141-CS121 crawler 34363846 47508988 76382638 47911659");

        /**
         * Do you want crawler4j to crawl also binary data ?
         * example: the contents of pdf, or the metadata of images etc
         */
        config.setIncludeBinaryContentInCrawling(false);

    /*   //TODO See if you want to use resumable crawling
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

        //TODO Print hashmap of subdomains to file
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
    }
}
