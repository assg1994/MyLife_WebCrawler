
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 *
 * @author George Makrakis
 */
public class DB
{

    public Connection conn = null;

    public DB()
    {
        try
        {
            Class.forName("org.postgresql.Driver");
            String url = "jdbc:postgresql://localhost:5432/MyLifeProfileData";
            conn = DriverManager.getConnection(url, "username", "password");
            System.out.println("conn built");
        }
        catch (SQLException e)
        {
            e.printStackTrace();
        }
        catch (ClassNotFoundException e)
        {
            e.printStackTrace();
        }
    }

    public ResultSet runSql(String sql) throws SQLException
    {
        Statement sta = conn.createStatement();
        return sta.executeQuery(sql);
    }

    public boolean runSql2(String sql) throws SQLException
    {
        Statement sta = conn.createStatement();
        return sta.execute(sql);
    }

    @Override
    protected void finalize() throws Throwable
    {
        if (conn != null || !conn.isClosed())
        {
            conn.close();
        }
    }
}
