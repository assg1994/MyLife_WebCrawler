
//*-------Most of the code in these classes was taken from http://www.netinstructions.com/how-to-make-a-simple-web-crawler-in-java/ and rewriten to cover our projects needs-------*
/**
 *
 * @author George Makrakis
 */
public class Main
{

    public static void main(String[] args)
    {
        Searching search = new Searching();
        search.search("https://www.mylife.com/john-brown/");
    }

}
