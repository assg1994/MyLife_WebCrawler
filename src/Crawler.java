import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
/*
* @author George Makrakis
 */
public class Crawler
{

    // We will use a fake USER_AGENT so the web server thinks the robot is a normal web browser.
    private static final String USER_AGENT = "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/535.1 (HTML, like Gecko) Chrome/13.0.782.112 Safari/535.1";
    private List<String> links = new LinkedList<String>();
    private String nextLink = null;
    private Document htmlDocument;

    //Lists that are used to store the parsed profile data
    public static List<String> names = new ArrayList<>();
    public static List<String> city = new ArrayList<>();
    public static List<String> state = new ArrayList<>();
    public static List<String> age = new ArrayList<>();
    public static List<String> work = new ArrayList<>();

    /**
     * This method does all the work. It makes an HTTP request, checks the
     * response, and then gets up all the links on the page. Perform a
     * searchForWord after the successful crawl
     */
    public boolean crawl(String url)
    {
        try
        {
            Connection connection = Jsoup.connect(url).userAgent(USER_AGENT);
            Document htmlDocument = connection.get();
            this.htmlDocument = htmlDocument;
            // 200 is the HTTP OK status code to show that everything is great.
            if (connection.response().statusCode() == 200)
            {
                System.out.println("\n--Visiting-- Received web page at " + url);
            }
            if (!connection.response().contentType().contains("text/html"))
            {
                System.out.println("--Failure-- Retrieved something other than HTML");
                return false;
            }
            //Every link (that is specified with href) on page will be stored them into links list (need only the last link of "Next" button)
            Elements linksOnPage = htmlDocument.select("a[href*=john]");
            System.out.println("Found (" + linksOnPage.size() + ") links related to the name John Brown");

            //The variable nextLink will be used every time to retrieve all wanted profile data 
            //incrementing pages like https://www.mylife.com/john-brown/2/.... to https://www.mylife.com/john-brown/150/....
            nextLink = linksOnPage.last().absUrl("href");
            nextLink = nextLink.replaceAll("\\s", ""); 
            
            //This character replacement using ReGex is used because in HTML page some links have these whitespace between characters;
            //links.add(linksOnPage.last().absUrl("href"));
            //Printing method to check that we get all proper links
            /*linksOnPage.stream().forEach((link) ->
            {
                links.add(link.absUrl("href"));
                System.out.println(links.toString());
            });*/
            return true;
        }
        catch (IOException ioe)
        {
            // We were not successful in our HTTP request
            return false;
        }
    }

    /**
     * Performs a search on the body of on the HTML document that is retrieved.
     * This method should only be called after a successful crawl
     */
    public boolean searchForWord(String searchWord)
    {
        //This method should only be used after a successful crawl of the current document.
        if (this.htmlDocument == null)
        {
            System.out.println("ERROR! Call crawl() before performing analysis on the document");
            return false;
        }
        System.out.println("Searching for the word " + searchWord + "...");
        String bodyText = htmlDocument.body().text();

        return bodyText.toLowerCase().contains(searchWord.toLowerCase());
    }

    public boolean getData()
    {

        //This method should only be used after a successful crawl of the current document.
        if (this.htmlDocument == null)
        {
            System.out.println("ERROR! Call crawl() before performing analysis on the document");
            return false;
        }
        //Get names related to John Brown
        Elements names = htmlDocument.select("h2[itemprop=\"name\"]");
        System.out.println(names.text());

        //Get location and age for every 'John Brown'
        Elements locationAges = htmlDocument.select("span[class=\"location-age\"]");
        System.out.println(locationAges.text());

        //Get work history
        //(Made this because every li in HTML code has one <h5> for education/work and because sometimes <span itemprop=".."> does not exist if theres no such data, 
        //and this caused problems with the number of records that were collected (usually smaller).So we used <h5> to be more accurate.)
        Elements h5=htmlDocument.select("h5");
        h5.stream().forEach((el)->
        {
          if(el.text().equals("EDUCATION / WORK HISTORY"))//Choose the element with this text sto be sure that whe are looking for jobs
          {
              //Here are the checks for the work data by checking the HTML elements for each purpose (<br> is used in every if we have education data also)
              if(el.siblingElements()!=null && el.siblingElements().toString().contains("<br>") && el.siblingElements().toString().contains("<span itemprop=\"worksFor\""))
              {
                  this.work.add(el.siblingElements().get(1).text());
                  System.out.println(el.siblingElements().get(1).text());  
              }
              else if(el.siblingElements()!=null && el.siblingElements().toString().contains("<span itemprop=\"worksFor\"")) 
              {                 
                  this.work.add(el.siblingElements().get(0).text());
                  System.out.println(el.siblingElements().get(0).text());                
              }   
              else
              {
                  this.work.add(" ");
              }
          }
        });
        names.stream().forEach((name)-> 
        {
                this.names.add(name.text());
                //System.out.println(this.names.toString());
        });

        /*works.stream().forEach((work)-> 
        {
                this.work.add(work.text());
                //System.out.println(this.work.toString());
        });*/

        locationAges.stream().forEach((locationAge)-> 
        {
                String[] splited=new String[3];
                Arrays.fill(splited, "");
                
                splited = locationAge.text().split("\\s*,\\s*");
                try
                {              
                    this.city.add(splited[0]);
                }
                catch(ArrayIndexOutOfBoundsException ex)
                {
                    System.out.println("No city provided");
                    this.city.add(" ");
                }
                
                try
                {              
                    this.state.add(splited[1]);
                }
                catch(ArrayIndexOutOfBoundsException ex)
                {
                    System.out.println("No state provided");
                    this.state.add(" ");
                }
                
                try
                {              
                    this.age.add(splited[2]);
                }
                catch(ArrayIndexOutOfBoundsException ex)
                {
                    System.out.println("No age provided");
                    this.age.add(" ");
                }
               
                
                //System.out.println(this.city.toString());
                //System.out.println(this.state.toString());
                //System.out.println(this.age.toString());
        });

        return true;
    }

    public List<String> getLinks()
    {
        return links;
    }

    public String getNextLink()
    {
        return nextLink;
    }
}
