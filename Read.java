import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.FileInputStream;  
import java.io.InputStreamReader; 
import java.util.regex.Pattern;
import java.util.regex.*; 
import java.util.Hashtable;
import java.util.Iterator;
public class Read 
{

	/**
	 * @param args
	 */
	
	public static void main(String[] args){	
		try {   
                String encoding = "GBK"; // 字符编码(可解决中文乱码问题 )   
	            File file = new File("d:/java/1.txt");   
	            if (file.isFile() && file.exists()) {   
	               InputStreamReader read = new InputStreamReader(   
	                      new FileInputStream(file), encoding);   
	                BufferedReader bufferedReader = new BufferedReader(read);   
	                String lineTXT = null;   
	                Hashtable hash=new Hashtable();
	                while ((lineTXT = bufferedReader.readLine()) != null) {   
	                    //System.out.println(lineTXT.toString().trim());
	                	String regEx="[。？！?.!,: -\"]";
	                	Pattern p = Pattern.compile(regEx);
	                	//Matcher m  = p.matcher(lineTXT);
	                	/*按照句子结束符分割句子*/
	                    String[] substrs = p.split(lineTXT);
	                	for (int i=0;i<substrs.length;i++)
	                	{
	                		String sstr =substrs[i];
	                		//System.out.println(substrs[i]);
	                		if( sstr.length()>0)
	                		{
		                		if(hash.containsKey(sstr) )
		                		{
		                			int subCout =Integer.parseInt(""+hash.get(sstr))+1;
		                			hash.put(sstr, ""+subCout);
		                		}
		                		else
		                		{
		                			hash.put(sstr, "1");
		                		}
	                		}
	                	}
	                	
	                }
//			           Enumeration enum1=hash.elements();
//			           System.out.print("The element of hash is: ");
//			            while(enum1.hasMoreElements())
//			           System.out.print(enum1.nextElement()+enum1.nextElement()+" ");
			                for(Iterator   it   =   hash.keySet().iterator();   it.hasNext();   )   {   
	                            //从ht中取  
	                            String   key   =   (String)   it.next();   
	                            Object   value   =   hash.get(key);   
	                            //放进hm中  
	                            //hm.put(key, value);        
	                            System.out.println(key+":"+value+" ");
	                        } 
			                
			                read.close();   
			          }else{   
			               System.out.println("找不到指定的文件！");   
			            }   
			       } catch (Exception e) {   
		            System.out.println("读取文件内容操作出错");   
			           e.printStackTrace();   
			        }   
		}

    }

