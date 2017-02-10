
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
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
    private int pagesVisited = 0;
    private String pageToVisit = null;

    /**
     * Our main launching point for the Crawler's functionality. Internally it
     * makes an HTTP request and parse the response (the web page).
    */
    public void search(String url)
    {
        //Crawler cr = new Crawler();
        //cr.crawl(url);

        pageToVisit = url;

        while (pagesVisited < MAX_PAGES_TO_SEARCH)
        {
            String currentUrl = pageToVisit;
            Crawler cr = new Crawler();
            //if (pagesToVisit.isEmpty())
            //{
            //    currentUrl = url;
            //    pagesVisited.add(url);
            //}
            //else
            //{
            //    currentUrl = this.nextUrl();
            //}
            cr.crawl(currentUrl);

            boolean done = cr.getData();
            if (done)
            {
                System.out.println("Data successfully collected");
                //break;
            }
            pageToVisit = cr.getNextLink();
            ++pagesVisited;
        }
        //Import data into database
        try
        {
            
            DB db = new DB();
            for(int i=0;i<3000;i++)
            {
                String sql = "INSERT INTO profiles (name, city, state, age, work) VALUES " + "(?,?,?,?,?);";
                PreparedStatement stmt = db.conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
                try
                {
                    stmt.setString(1, Crawler.names.get(i));
                    stmt.setString(2, Crawler.city.get(i));
                    stmt.setString(3, Crawler.state.get(i));
                    stmt.setString(4, Crawler.age.get(i));
                    stmt.setString(5, Crawler.work.get(i));
                    stmt.execute();
                }
                catch(IndexOutOfBoundsException ex)
                {
                    ex.printStackTrace();
                }
            }
            System.out.println("All data inserted");
        }
        catch (SQLException ex)
        {
            ex.printStackTrace();
        }
        System.out.println("\n--Done-- Visited " + pagesVisited + " web page(s)");
    }

    public void search(String url, String searchWord)
    {
        //Crawler cr = new Crawler();
        //cr.crawl(url);

        pageToVisit = url;

        while (pagesVisited < MAX_PAGES_TO_SEARCH)
        {
            String currentUrl = pageToVisit;
            Crawler cr = new Crawler();
            //if (pagesToVisit.isEmpty())
            //{
            //    currentUrl = url;
            //    pagesVisited.add(url);
            //}
            //else
            //{
            //    currentUrl = this.nextUrl();
            //}
            cr.crawl(currentUrl);
            boolean success = cr.searchForWord(searchWord);
            if (success)
            {
                System.out.println(String.format("--Success-- Word %s found at %s", searchWord, currentUrl));
                break;
            }

            boolean done = cr.getData();
            if (done)
            {
                System.out.println("Data successfully collected");
                //break;
            }
            pageToVisit = cr.getNextLink();
            ++pagesVisited;
        }
        
        /**
         * Returns the next URL to visit (in the order that they were found). We
         * also do a check to make sure this method doesn't return a URL that
         * has already been visited.
         *
         * private String nextUrl() 
         * { 
            * String nextUrl; 
            * do 
            * { 
            * nextUrl =pagesToVisit.remove(0);
            * }while (pagesVisited.contains(nextUrl));
            * pagesVisited.add(nextUrl); return nextUrl;
         * }
         */
    }
}
