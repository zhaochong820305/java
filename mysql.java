import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Connection;
public class mysql {
	 public static void main(String[] args) throws Exception {
	        Connection conn = null;
	        String sql;
	        // MySQL JDBC URL ：jdbc:mysql://server：port/database?user=value
	        // useUnicode and characterEncoding
	        
	        String url = "jdbc:mysql://192.168.111.128:3306/test?"
	                + "user=root&password=root&useUnicode=true&characterEncoding=UTF8";
	 
	        try {
	            
	            // Class.forName
	            Class.forName("com.mysql.jdbc.Driver");// mysql jdbc
	            // or:
	            // com.mysql.jdbc.Driver driver = new com.mysql.jdbc.Driver();
	            // or：
	            // new com.mysql.jdbc.Driver();
	 
	            System.out.println("MySQL jdbc add sesucc");
	            //Connection datebase 
	            conn = DriverManager.getConnection(url);
	            
	            // Statement executeUpdate create insert delete 
	            Statement stmt = conn.createStatement();
	            System.out.println("create tables is true");
	            sql = " create table student(NO char(20),name varchar(20),primary key(NO))";
	            int result =0;// stmt.executeUpdate(sql);// executeUpdate return is -1 false
	            System.out.println("create tables student is true");
	            if (result != -1) {
	                System.out.println("create tables is true");
	                sql = "insert into student(NO,name) values('201206','zc2')";
	                result = stmt.executeUpdate(sql);
	                sql = "insert into student(NO,name) values('201205','zs3')";
	                result = stmt.executeUpdate(sql);
	                sql = "select * from student";
	                ResultSet rs = stmt.executeQuery(sql);// executeQuery return rs or null
	                System.out.println("hao \t name");
	                while (rs.next()) {
	                    System.out
	                            .println(rs.getString(1) + "\t" + rs.getString(2));//  int getInt()
	                }
	            }
	        } catch (SQLException e) {
	            System.out.println("MySQL op is false");
	            e.printStackTrace();
	        } catch (Exception e) {
	            e.printStackTrace();
	        } finally {
	            conn.close();
	        }
	 
	    }
}

