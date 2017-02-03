

//*-------Most of the code in these classes was taken from http://www.netinstructions.com/how-to-make-a-simple-web-crawler-in-java/ and rewriten to cover our projects needs-------*

/**
 *
 * @author George Makrakis
 */
public class Main
{

    public static void main(String[] args)
    {
        Spider spider = new Spider();
        spider.search("https://www.mylife.com/", "Mary");
    }
    
}
