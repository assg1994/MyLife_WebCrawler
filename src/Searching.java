import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author George Makrakis
 */
public class Searching
{

    private static final int MAX_PAGES_TO_SEARCH = 150;//every page has 20 profiles loaded, so if we want to get 3000 records we have to search a maximum of 150 pages
    private Set<String> pagesVisited = new HashSet<String>();
    private List<String> pagesToVisit = new LinkedList<String>();

    /**
     * Our main launching point for the Crawler's functionality. Internally it
     * makes an HTTP request and parse the response (the web page).
     * 
     * @param url - The starting point of the crawler
     * @param searchWord - The word or string that you are searching for
     */
    public void search(String url, String searchWord)
    {
        while (pagesVisited.size() < MAX_PAGES_TO_SEARCH)
        {
            String currentUrl;
            Crawler cr = new Crawler();
            if (pagesToVisit.isEmpty())
            {
                currentUrl = url;
                pagesVisited.add(url);
            }
            else
            {
                currentUrl = this.nextUrl();
            }
            cr.crawl(currentUrl);
            boolean success = cr.searchForWord(searchWord);
            if (success)
            {
                System.out.println(String.format("--Success-- Word %s found at %s", searchWord, currentUrl));
                break;
            }
            pagesToVisit.addAll(cr.getLinks());
        }
        System.out.println("\n--Done-- Visited " + pagesVisited.size() + " web page(s)");
    }

    /**
     * Returns the next URL to visit (in the order that they were found). We
     * also do a check to make sure this method doesn't return a URL that has
     * already been visited.
     */
    private String nextUrl()
    {
        String nextUrl;
        do
        {
            nextUrl = pagesToVisit.remove(0);
        }
        while (pagesVisited.contains(nextUrl));
        pagesVisited.add(nextUrl);
        return nextUrl;
    }
}
