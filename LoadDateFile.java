
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class LoadDateFile {
	static String url = "jdbc:mysql://127.0.0.1:3306/test?"
            + "useUnicode=true&characterEncoding=GB2312&useSSL=false";
    String sql="";
    static String user="root";
    static String password ="root";
    static Connection conn = null;
    static Statement stmt = null;
    
	public static void main(String[] args) {
	
	    try {
	    	//System.out.println("start;");
	    	Class.forName("com.mysql.jdbc.Driver");
	    	//System.out.println("com.mysql;");
			conn = DriverManager.getConnection(url,user,password);
			System.out.println("conn:");
	        stmt = conn.createStatement();
	        // System.out.println("load date infile");
	        String sql = "LOAD DATA INFILE  '/txt/test.txt'  INTO TABLE test  LINES TERMINATED BY '\\r\\n' ";
	        //sql="SELECT * FROM test";
	        // sql = "load data infile 'E:/mysqlsql/test.txt' replace into table test character set GBK fields terminated by ',' enclosed by '\'' lines terminated by '\r\n'";
	        System.out.println(sql);
	        boolean result = stmt.execute(sql);
	
	        System.out.println("LoadÖ´ÐÐ½á¹û£º" + result);
	        
	    }catch(Exception e){
	        e.printStackTrace();
	    }
	}

}
