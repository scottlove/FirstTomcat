package WordsServer;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletInputStream;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.sql.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import com.google.gson.* ;



/**
 * Created by scotlov on 9/2/14.
 */
@WebServlet(
        name = "WordsServlet",
        urlPatterns = {"/words"},
        loadOnStartup = 0
)
public class WordsServlet extends HttpServlet {

    private static final String DEFAULT_USER = "Guest";




    @Override
    public void init() throws ServletException
    {
        System.out.println("Servlet " + this.getServletName() + " has started.");


    }

    private class person {
        public String emailAddress;
        public String firstName;
        public int age;


    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        response.setHeader("Access-Control-Allow-Origin", "*");
        ServletInputStream reader = request.getInputStream();
        int contentLen = request.getContentLength();
        if (contentLen >=1)
        {
            byte buffer[] = new byte[ contentLen ];
            int len = reader.readLine( buffer, 0, buffer.length );
            String data = new String(buffer,0,len) ;
            Gson gson = new Gson();
            person b = gson.fromJson(data,person.class);
            String result = b.firstName + ',' + b.age + ',' + b.emailAddress;
            response.getWriter().write(result);
        }
        else
        {

            response.getWriter().write("failure");
        }

    }


    private class wordCount {
        public String word;
        public Integer count;
        private wordCount(String word, Integer count)  {
            this.word = word;
            this.count = count;
        }

    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        ServletContext c = this.getServletContext() ;
        DBFactory.initialize(c);

        Connection connection = DBFactory.connect()     ;
        String query = "SELECT word, count FROM words";

        try{
            Statement stmt = connection.createStatement() ;
            ResultSet rs = stmt.executeQuery(query);
            //PrintWriter writer = response.getWriter();
            Collection <wordCount> words = new ArrayList();



            while (rs.next())
            {
                words.add(new wordCount(rs.getString("word"),rs.getInt("count")));    ;
                //writer.append(rs.getString("word") + ":" + rs.getString("word" )   )       ;
            }

            Gson gson = new Gson();
            String json = gson.toJson(words)   ;
            response.setHeader("Access-Control-Allow-Origin", "*");
            response.setContentType("application/json");
            response.getWriter().write(json) ;
            connection.close();
        }
        catch (Exception ex)
        {
            System.err.println(ex.getMessage())  ;

        }




    }

    protected void doGet1(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String user = request.getParameter("user");
        if(user == null)
            user = WordsServlet.DEFAULT_USER;

        response.setContentType("text/html");
        response.setCharacterEncoding("UTF-8");

        PrintWriter writer = response.getWriter();
        writer.append("<!DOCTYPE html>\r\n")
                .append("<html>\r\n")
                .append("    <head>\r\n")
                .append("        <title>Hello User Application</title>\r\n")
                .append("    </head>\r\n")
                .append("    <body>\r\n")
                .append("        Hello, ").append(user).append("!<br/><br/>\r\n")
                .append("        <form action=\"words\" method=\"POST\">\r\n")
                .append("            Enter your name:<br/>\r\n")
                .append("            <input type=\"text\" name=\"user\"/><br/>\r\n")
                .append("            <input type=\"submit\" value=\"Submit\"/>\r\n")
                .append("        </form>\r\n")
                .append("    </body>\r\n")
                .append("</html>\r\n");
    }
}
