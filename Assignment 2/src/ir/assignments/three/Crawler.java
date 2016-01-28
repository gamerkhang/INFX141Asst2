package ir.assignments.three;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Set;
import java.util.regex.Pattern;

import org.apache.http.Header;

import edu.uci.ics.crawler4j.crawler.Page;
import edu.uci.ics.crawler4j.crawler.WebCrawler;
import edu.uci.ics.crawler4j.parser.HtmlParseData;
import edu.uci.ics.crawler4j.url.WebURL;
import java.util.Collection;
/**
 * Created by VGDC_1 on 1/18/2016.
 *
 * Derek Edrich, Khang Tran, Carl Pacheco, Brett Lenz
 *
 * Example used: https://github.com/yasserg/crawler4j/tree/master/src/test/java/edu/uci/ics/crawler4j/examples/basic
 */
public class Crawler extends WebCrawler {
	/**
	 * This method is for testing purposes only. It does not need to be used
	 * to answer any of the questions in the assignment. However, it must
	 * function as specified so that your crawler can be verified programatically.
	 * 
	 * This methods performs a crawl starting at the specified seed URL. Returns a
	 * collection containing all URLs visited during the crawl.
	 */
//	public static Collection<String> crawl(String seedURL) {
//		// TODO find where to set user agent name
//
//		return null;
//	}

	private static final Pattern IMAGE_EXTENSIONS = Pattern.compile(".*\\.(bmp|gif|jpg|png)$");

	/**
	 * You should implement this function to specify whether the given url
	 * should be crawled or not (based on your crawling logic).
	 */
	@Override
	public boolean shouldVisit(Page referringPage, WebURL url) {
		String href = url.getURL().toLowerCase();
		// Ignore the url if it has an extension that matches our defined set of image extensions.
		if (IMAGE_EXTENSIONS.matcher(href).matches()) {
			return false;
		}

		// Only accept the url if it is in the "www.ics.uci.edu" domain and protocol is "http".
		//		return href.startsWith("http://www.ics.uci.edu/");
		return href.contains(".ics.uci.edu/");
	}

	/**
	 * This function is called when a page is fetched and ready to be processed
	 * by your program.
	 */
	@Override
	public void visit(Page page) {
		int docid = page.getWebURL().getDocid();
		String url = page.getWebURL().getURL();
		String domain = page.getWebURL().getDomain();
		String path = page.getWebURL().getPath();
		String subDomain = page.getWebURL().getSubDomain();
		String parentUrl = page.getWebURL().getParentUrl();
		String anchor = page.getWebURL().getAnchor();

		System.out.println("Docid: " + docid);
		System.out.println("URL: " + url);/*
		System.out.println("Domain: " + domain);
		System.out.println("Sub-domain: " + subDomain);
		System.out.println("Path: " + path);
		System.out.println("Parent page: " + parentUrl);
		System.out.println("Anchor text: " + anchor);
*/
		if (page.getParseData() instanceof HtmlParseData) {
			HtmlParseData htmlParseData = (HtmlParseData) page.getParseData();
			String text = htmlParseData.getText();
			String html = htmlParseData.getHtml();
			Set<WebURL> links = htmlParseData.getOutgoingUrls();

			try {
				//http://stackoverflow.com/questions/4499562/how-to-save-the-data-into-file-in-java
				FileWriter fOut = new FileWriter("pages/" + docid + ".txt");
				BufferedWriter writer = new BufferedWriter(fOut);
				writer.write(text);
				writer.close();

				if (ControlCrawler.subDomainMap.containsKey(subDomain))
					ControlCrawler.subDomainMap.put(subDomain, ControlCrawler.subDomainMap.get(subDomain)+1);
				else
					ControlCrawler.subDomainMap.put(subDomain, 1);
			}
			catch(IOException e)
			{
				System.out.println(e.getMessage());
			}


			System.out.println("Text length: " + text.length());
			System.out.println("Html length: " + html.length());
			System.out.println("Number of outgoing links: " + links.size());
		}

		Header[] responseHeaders = page.getFetchResponseHeaders();
		if (responseHeaders != null) {
			System.out.println("Response headers:");
			for (Header header : responseHeaders) {
				System.out.println("\t" + header.getName() + ": {}" + header.getValue());
			}
		}

		System.out.println("=============");
	}
}
