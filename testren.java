import java.io.IOException;
import java.util.Arrays;
import java.io.StringReader;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.Collator;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import java.util.TreeMap;

import org.wltea.analyzer.core.IKSegmenter;
import org.wltea.analyzer.core.Lexeme;

import java.io.*;
public class testren {
	 static Connection conn = null ;
	 //Statement stmt = null ;
     //static String url = "jdbc:mysql://10.17.12.228:13306/dataone?"
     //      + "useUnicode=true&characterEncoding=gb2312&useSSL=false";
     static String url = "jdbc:mysql://127.0.0.1:3306/dataone?"
             + "useUnicode=true&characterEncoding=gb2312&useSSL=false";
     String sql="";
     static String user="root";
     static String password ="root"; //"root";//"bluscn993711";// "root";//"bluscn993711";
     //static String password ="bluscn993711";
     static String sqlvalue="";
     static String mapsql="";
     static int ierror =0;
     static String key2="";
     static String key3="";
     static String key4="";
     static String key6="";
     static String keysqll="";
	 public static void main(String[] args) {
		 // TODO Auto-generated method stub
		 
		 try{
		 System.out.println("start fenci");
		 
		 Scanner s = new Scanner(System.in);
		 String strin = null;
	     System.out.println("请输入要执行的条数：");
	     strin = s.next();
	     System.out.println("您输入的是：");
	     System.out.println(strin);
	     Date date=new Date();
	     DateFormat format=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	     String stime=format.format(date);
	     long start = System.currentTimeMillis();
	     System.out.println("start write time");      
		 //Class.forName("com.mysql.jdbc.Driver");// mysql jdbc
		 Class.forName("com.mysql.fabric.jdbc.JDBC4FabricMySQLConnectionProxy");
		 conn = DriverManager.getConnection(url,user,password);
		 //System.out.println("conn");
		 
		 //SelectSql("");
		 Map m1=SelectMap("","dictrootcause");//rootcause
		 Map m2=SelectMap("","dictsymptom");//dictsymptom
		 Map m3=SelectMap("","dictdisease");//disease
		 Map m4=SelectMap("","dicttcmsymptom");//disease
		 Map m5=SelectMap("","dicttherapy");//disease
		 Map m6=SelectMap("","dictmedicine");//disease
		 Map m7=SelectMap("","dictacupoint");//disease
		 //System.out.print("m1 \t" + m1.size());
		 //System.out.print("m2 \t" + m2.size());
		 //System.out.print("m3 \t" + m3.size());
		 //System.out.print("m4 \t" + m4.size());
		 //System.out.print("m5 \t" + m5.size());
		 //System.out.print("m6 \t" + m6.size());
		 int times = Integer.parseInt(strin)/1000;
		 System.out.println("执行次数："+times);  
		 for(int i=0;i<times;i++)
		 {
		 try{
			 Statement statement=conn.createStatement();
			 Statement statement1=conn.createStatement();
			 String str = "";			
			 String sql1 = " select * from diagnosecase where  record_state=0 limit 0,1000 ";
			 ResultSet rs = statement.executeQuery(sql1); 
			 	
			 String ssql1=" INSERT INTO indexrootcause (`item_id`,`case_id`) VALUES";
			 String ssql2=" INSERT INTO indexsymptom (`item_id`,`case_id`) VALUES";
			 String ssql3=" INSERT INTO indexdisease (`item_id`,`case_id`) VALUES";
			 String ssql4=" INSERT INTO indextcmsymptom (`item_id`,`case_id`) VALUES";
			 String ssql5=" INSERT INTO indextherapy (`item_id`,`case_id`) VALUES";
			 String ssql6=" INSERT INTO indexmedicine (`item_id`,`case_id`) VALUES";
			 String ssql7=" INSERT INTO indexacupoint (`item_id`,`case_id`) VALUES";
			 String ssqlno = " INSERT INTO `inprocesscase`(`case_id`,`type`,`auto_dict_words`,`auto_nondict_words`,`parse_time`)VALUES";
			 String sqltemp = " INSERT INTO `dictsecasetemp`(`case_id`,`symptom`,`disease`, `tcm_symptom`,`medicine`)VALUES";
			 String scaseid="";
			 String startid="";
			 String case_id="";
			 int inum=0;
			 while(rs.next()){ 
				 if(inum==0)
				 {
					 startid=rs.getString("case_id");
				 }
				 case_id=rs.getString("case_id");
				 System.out.println("runstart:"+inum);	
					   //System.out.println(rs.getString("case_id"));
		               //str = str+rs.getString("case_id")+" 手机号 ："
		               //        +rs.getString("root_cause");
					   if(rs.getString("record_state").equals("0")||rs.getString("record_state").equals("2") )
					   {
						   
						   String sssql1= maprun(rs.getString("root_cause"),m1,rs.getString("case_id"),"indexrootcause","1");
						   if(sssql1.length()>0)
						   {
							   ssql1+=sssql1;
							   if(sqlvalue.length()>0)
							   {
								   ssqlno+=sqlvalue;
							   }
						   }
						   
						   String sssql2=maprun(rs.getString("symptom"),m2,rs.getString("case_id"),"indexsymptom","2");
						   if(sssql2.length()>0)
						   {
							   ssql2+=sssql2;
							   if(sqlvalue.length()>0)
							   {
								   ssqlno+=sqlvalue;
							   }
						   }
						   
						   String sssql3=maprun(rs.getString("disease"),m3,rs.getString("case_id"),"indexdisease","3");
						   if(sssql3.length()>0)
						   {
							   ssql3+=sssql3;
							   if(sqlvalue.length()>0)
							   {
								   ssqlno+=sqlvalue;
							   }
						   }
						   
						   String sssql4=maprun(rs.getString("tcm_symptom"),m4,rs.getString("case_id"),"indextcmsymptom","4");
						   if(sssql4.length()>0)
						   {
							   ssql4+=sssql4;
							   if(sqlvalue.length()>0)
							   {
								   ssqlno+=sqlvalue;
							   }
						   }
						   
						   String sssql5=maprun(rs.getString("therapy"),m5,rs.getString("case_id"),"indextherapy","5");
						   if(sssql5.length()>0)
						   {
							   ssql5+=sssql5;
							   if(sqlvalue.length()>0)
							   {
								   ssqlno+=sqlvalue;
							   }
						   }
						   
						   String sssql6=maprun(rs.getString("medicine"),m6,rs.getString("case_id"),"indexmedicine","6");
						   if(sssql6.length()>0)
						   {
							   ssql6+=sssql6;
							   if(sqlvalue.length()>0)
							   {
								   ssqlno+=sqlvalue;
							   }
						   }
						   
						   //System.out.println("ssql6:"+ssql6);
						   String sssql7="";
						   if(rs.getString("acupoint")!=null && rs.getString("acupoint").length()>0)
						   {
							   sssql7=maprun(rs.getString("acupoint"),m7,rs.getString("case_id"),"indexacupoint","7");
							   if(sssql7.length()>0)
							   {
								   ssql7+=sssql7;
								   if(sqlvalue.length()>0)
								   {
									   ssqlno+=sqlvalue;
								   }
							   }
							   //System.out.println("acupoint运行完毕");
						   }
						   if(keysqll.length()>0)
						   {
							   sqltemp+=keysqll;
						   }
						   if((inum+2) % 100==1)
						   {
							   System.out.println("ssqlno语句正在执行");
							     if(ssqlno.indexOf("),")>0)
							 	 {
							    	 try{
							    	 ssqlno = ssqlno.substring(0,ssqlno.length()-1)+";";
							 		 //System.out.println(ssqlno);
							 		 
								 	 statement1.executeUpdate(ssqlno);
								 	 ssqlno = "INSERT INTO `inprocesscase`(`case_id`,`type`,`auto_dict_words`,`auto_nondict_words`,`parse_time`)VALUES";
							    	 }catch(Exception e){
							    		 System.out.println(ssqlno);
							    		 logtxt("inprocesscase执行有错误；请手动在sqlyog中执行："+ssqlno);
							    		 ierror++;
							    	 }
							     }
							     
						   }
						   //最后一步处理state=0;
						   scaseid+="'"+rs.getString("case_id")+"',";
						   inum++;
					   }
		         }
			 	 rs.close();
			 	 System.out.println("root_cause语句正在执行");
			 	 if(ssql1.indexOf("),")>0)
			 	 {
			 		 ssql1 = ssql1.substring(0,ssql1.length()-1)+";";
			 		 //System.out.println(ssql1);
				 	 statement1.executeUpdate(ssql1);
			 	 }
			 	 System.out.println("更新总数："+((i+1)*1000));
			     if(ssql2.indexOf("),")>0)
			 	 {
			 		 ssql2 = ssql2.substring(0,ssql2.length()-1)+";";
			 		 //System.out.println(ssql2);
				 	 statement1.executeUpdate(ssql2);
			 	 }
			     if(ssql3.indexOf("),")>0)
			 	 {
			 		 ssql3 = ssql3.substring(0,ssql3.length()-1)+";";
			 		 //System.out.println(ssql3);
				 	 statement1.executeUpdate(ssql3);
			 	 }
			     if(ssql4.indexOf("),")>0)
			 	 {
			 		 ssql4 = ssql4.substring(0,ssql4.length()-1)+";";
			 		 //System.out.println(ssql4);
				 	 statement1.executeUpdate(ssql4);
			 	 }
			     if(ssql5.indexOf("),")>0)
			 	 {
			 		 ssql5 = ssql5.substring(0,ssql5.length()-1)+";";
			 		 //System.out.println(ssql5);
				 	 statement1.executeUpdate(ssql5);
			 	 }
			     //if(inum % 100==1)
				   
			     if(ssql7.indexOf("),")>0)
			 	 {
			 		 ssql7 = ssql7.substring(0,ssql7.length()-1)+";";
			 		 //System.out.println(ssql7);
				 	 statement1.executeUpdate(ssql7);
			 	 }
			     if(ssql6.indexOf("),")>0)
			 	 {
			    	//System.out.println("medicine语句执行开始"+ssql6);
			 		ssql6 = ssql6.substring(0,ssql6.length()-1)+";";
			 		// System.out.println(ssql6);
			 		statement1.executeUpdate(ssql6);
				 	System.out.println("medicine语句执行完成");
			 	 }
			     System.out.println("temp开始执行！");
			     
			     //if(sqltemp.indexOf("),")>0)
			     //{
			     //	 sqltemp = sqltemp.substring(0,sqltemp.length()-1)+";";
			     //	 //System.out.println(sqltemp);
			     //	 statement1.executeUpdate(sqltemp);
			     //}
			     ssql1="";
			     ssql2="";
			     ssql3="";
			     ssql4="";
			     ssql5="";			     
			     ssql6="";
			     ssql7="";
			     ssqlno="";
			     sqltemp="";
			     System.out.println("temp执行完成！");
			     //ssqlno+=sqlvalue+",";
			     long end = System.currentTimeMillis();
			     date=new Date();
			     format=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			     String etime=format.format(date);
			     
			     if(scaseid.length()>0)
			     {
				     scaseid = scaseid.substring(0,scaseid.length()-1);
				     String upsql ="UPDATE diagnosecase SET record_state=1  where case_id in ("+scaseid+");";
				     //System.out.println(upsql);
				     statement.executeUpdate(upsql);
					 System.out.println("更新caseid状态成功：本次更新条数："+inum);
					 System.out.println("更新总数："+((i+1)*1000));
					 
					 
					 //记录更新日志
				     upsql ="INSERT INTO `eventlog`(`opname`,`user`,`opmethod`,`begintime`,`beginrecord`,`endtime`,`endrecord`,`okcount`)";
				     upsql +="VALUES ('java自动','admin','立即启动:执行:1000条数据','"+stime+"','"+startid+"','"+etime+"','"+case_id+"','"+inum+"'       );";
				     //System.out.println(upsql);
				     statement.executeUpdate(upsql);
				     System.out.println("记录更新日志成功："+((i+1)*1000));
			     }else
			     {
			    	 System.out.println("数据已经更新完成！");
			     }
			     
				 System.out.println(str);
				 
			     System.out.println("运行时间：" + (end - start) + "毫秒");
			     if(ierror>0){
			    	 System.out.println("错误记录条数："+ierror+"请手动运行log.txt 中的sql进行增加");
			     }
			 }catch(Exception e){
	             e.printStackTrace();
	         }
		 }
		}catch(Exception e){
			e.printStackTrace();
		}
	 }
	 public static void logtxt (String str){
		 	  try {
				   File f = new File(".\\log.txt");
				   if(f.exists()){
				    System.out.print("文件存在");
				   }else{
				    System.out.print("文件不存在");
				    f.createNewFile();//不存在则创建
				   }BufferedWriter output = new BufferedWriter(new FileWriter(f));
				   output.write(str);
				   output.close();
			  } catch (Exception e) {
			   //e.printStackTrace();
			  }
	 }
	 private static String[] insert(String[] arr, String str)
	 {
		 int size = arr.length;
		 
		 String[] tmp = new String[size + 1];
		 
		 System.arraycopy(arr, 0, tmp, 0, size);
		 
		 tmp[size] = str;
		 
		 return tmp;
	 }
	 public static String maprun(String skey,Map m1,String case_id,String tablename,String types)
	 {
		 sqlvalue="";
		 String dict_words = "";
		 String nondict_words = "";	
		 String mapsql1="";
		 
		 Statement statement = null;
		 try {
			statement = conn.createStatement();
		 } catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		 }
		 
		 if(skey.equals("") || skey.length() <= 0)
         {
			 return "";
         }else
         {
      	     //rs.getString("root_cause").split(",");
      	     String[] strs = skey.split("[ ;,?()*'\"]");
      	     //String[] arr = {"1"};
      	     //Arrays.sort(strs);
      	      
             for(String s : strs){
              	if(m1.containsKey(s)){
              		//System.out.println("key:"+s+"value:"+m1.get(s));
              		//System.out.println("caseid:"+case_id+"value:"+m1.get(s));
              		mapsql1 +="('"+m1.get(s)+"','"+case_id+"'),";
              		dict_words+=s+"|";
              		//arr=insert(arr, s); 
              		 
              		//String ssql="INSERT INTO indexrootcause (`item_id`,`case_id`) VALUES ('"+m1.get(s)+"','"+case_id+"')";
              		//try {
					//	int ivalue = statement.executeUpdate(ssql);
					//} catch (SQLException e) {
						// TODO Auto-generated catch block
					//	e.printStackTrace();
					//}
              	}else{
              		//System.out.println("没有查到字典："+s);
              		StringReader sr=new StringReader(s);
     	      		IKSegmenter ik=new IKSegmenter(sr, true);
     	      		Lexeme lex=null;
     	      		try {
     					while((lex=ik.next())!=null){
     						//System.out.print(lex.getLexemeText()+"|");
     						if(m1.containsKey(lex.getLexemeText()))
     		              	{
     							mapsql1 +="('"+m1.get(lex.getLexemeText())+"','"+case_id+"'),";
     							dict_words+=lex.getLexemeText()+"|";
     							//arr=insert(arr, lex.getLexemeText()); 
     		              	}else
     		              	{
     		              		//System.out.println("没有查到字典："+lex.getLexemeText());
     		              		nondict_words+=lex.getLexemeText()+"|";
     		              	}		              	
     					}
     				} catch (IOException e) {
     					// TODO Auto-generated catch block
     					e.printStackTrace();
     				}
              	}
                  //System.out.println(s);
             } 
        	  
               
      		 Date date=new Date();
      		 DateFormat format=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
      		 String time=format.format(date);
      		 if(dict_words.length()>0)
			 {
     			dict_words = dict_words.substring(0,dict_words.length()-1);
			 }
      		 if(dict_words.length()!=0 || nondict_words.length()!=0)
      		 {
      			 sqlvalue="('"+case_id+"','"+types+"','"+dict_words+"','"+nondict_words+"','"+time+"'),";
      		 }
      		 
      		 String[] arr = dict_words.split("[ ;,?()*'\"|]");     	     
      		 String str=null;
      		 Comparator cmp = Collator.getInstance(java.util.Locale.CHINA);
      		 TreeMap tree=new TreeMap(cmp); 
      		 if(arr.length>0){      			 
      			  Arrays.sort(arr,cmp);
      			  str=getStr(arr ); 
      			  if(str.length()>0)
      			  {
      				  str = str.substring(0,str.length()-1);
      			  }
      		 }
      		 
      		 if(types.equals("2"))
      		 {
      			key2="";      			
      			key2=str;
      		 }
      		 if(types.equals("3"))
     		 {
      			key3="";
     			key3=str;
     		 }
      		 if(types.equals("4"))
     		 {
      			key4="";
     			key4=str;
     		 }
      		 if(types.equals("6"))
     		 {
      			key6="";
     			key6=str;
     			keysqll="('"+case_id+"','"+key2+"','"+key3+"','"+key4+"','"+key6+"'),";
     		 }
          }  
		  return mapsql1;
	 }
	 public static String getStr(String[]args){ 
			String str=""; 
			for(int i=0;i<args.length;i++){				
		                 str+=(String)args[i]+"|";
			}	 
			return str; 
	 }
	 public Connection ConnectMysql(){
         try{
		     Class.forName("com.mysql.jdbc.Driver");
		     conn = (Connection) DriverManager.getConnection(url);
		     if (!conn.isClosed()) {
		         System.out.println("Succeeded connecting to the Database!");
		     } else {
		         System.out.println("Falled connecting to the Database!");
		     }
		 }catch(Exception ex){ 
		     ex.printStackTrace();
		 }
		  return conn;
	}
	
	 public static  void SelectSql(String sql){
	     try{
			 Statement statement=conn.createStatement();
			 String str = "";			
			 String sql1 = "select * from diagnosecase limit 2 ";
			 ResultSet rs = statement.executeQuery(sql1); 
			 //System.out.println("runok");			 
			 while(rs.next()){  
	               str = str+rs.getString("case_id")+" 手机号 ："
	                       +rs.getString("root_cause");
	         }
			 System.out.println(str);
		 }catch(Exception e){
             e.printStackTrace();
         }	 
		 
	 }
	 
	 public static   Map  SelectMap(String sql,String tablename){
		 Map m1 = new HashMap();
	     try{
			 Statement statement=conn.createStatement();
			 String str = "";			
			 String sql1 = "select * from  "+tablename;
			 ResultSet rs = statement.executeQuery(sql1); 
			 //System.out.println("runok");
			  
			 while(rs.next()){  
	               //str = str+rs.getString("case_id")+" 手机号 ："
	               //        +rs.getString("root_cause");formal_id
				   if(rs.getString("formal_id")==null || rs.getString("formal_id").length()==0)
				   {
					   m1.put(rs.getString("word"),rs.getString("item_id"));
				   }else
				   {
					   m1.put(rs.getString("word"),rs.getString("formal_id"));
				   }
	               
	         }
			 return m1;
			 //System.out.println(str);
			 
		 }catch(Exception e){
             e.printStackTrace();
             return m1;
         }	 
		 
	  } 
	 
	 

}
