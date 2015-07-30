import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintStream;
import java.net.Socket;


public class UploadThead {
	
public static void main(String[] args) {
		
		String hostname = "127.0.0.1";
		int portnumber = 9026;
		Socket clientSocket;
 
		try
		{
			int i=1;
			while(i<=20)
			{
				clientSocket= new Socket(hostname,portnumber);
				
				Thread th = new EchoThread2(clientSocket,i);
            	th.start();
            	i++;
            	//new EchoThread2(clientSocket,i);
			}
		 }
	    catch (Exception ex)
	    {
	    	System.out.println("connect error!" );
	    }

	}

}

class EchoThread2 extends Thread
{
	private Socket s;
	int iconn;
	
	public EchoThread2(Socket sIncoming,int iNum)
	{
		s = sIncoming;
		iconn = iNum;
	
	}
	public void run()
	{
		try
		{
			Socket so = s;
			BufferedReader br = new BufferedReader(
					new InputStreamReader(so.getInputStream()));
			PrintStream ps = new PrintStream(so.getOutputStream());
			//File file = getFile();
			File file =new File("d:\\java\\"+iconn+".txt");
			String slen=Integer.toHexString((int) file.length());
			
			if(slen.length()>8)
			{
				System.out.println("file is too len");
				return;
			}
			
			slen = addZeroForNum(slen, 8);
			
			String sname = file.getName();
			if(sname.length()>20)
			{
				System.out.println("file name is too long");
			}
			sname = addZeroForNum(sname,20);
//			
			String str = "ABAC"+"|"+slen+"|0|"+sname+"|";
			byte[] byBuffer = new byte[200];
			byBuffer= str.getBytes();
			OutputStream out1 = so.getOutputStream();
			//ps.println("ABAC1"+file.getName()+"|"+file.length());
			//ps.println(str);
			out1.write(byBuffer,0,byBuffer.length);
			
			//ps.print
			//ps.println(file.length());
			//FileInputStream fis1 = new FileInputStream(file);
			FileInputStream fis = new FileInputStream(file);
			byte[] buffers  = new byte[3];
			byte[] buffer  = new byte[1024];
			
			//String msg =""so.+ fis.read(buffers);//.//so.read(buffer);
			//if(msg.equals("exi"))
			//{
			//	System.out.println("the file is exist,thie file is not upload");
			//	return;
			//}
			//else if(msg.equals("yes"))
			{
				long finishLen = 0;
				
				
				OutputStream out = so.getOutputStream();
				
				int len;
				fis.skip(finishLen);
				while((len = fis.read(buffer))!=-1)
					out.write(buffer,0,len);
					
				fis.close();
				//System.out.println(br.readLine());
				
				so.close();
			}
		}
		catch  (Exception ex) 
		{
			
			System.out.println("file upload error");	
		}
	}
	
	public static String addZeroForNum(String str, int strLength) {
	    int strLen = str.length();
	    if (strLen < strLength) {
	        while (strLen < strLength) {
	            StringBuffer sb = new StringBuffer();
	            sb.append("0").append(str);// 左补0
	            // sb.append(str).append("0");//右补0
	            str = sb.toString();
	            strLen = str.length();
	        }
	    }

	    return str;
	}
	
	public static File getFile()
	{
		System.out.println("write file path!");
		BufferedReader br = new BufferedReader(
				new InputStreamReader(System.in));
		while(true)
		{
			File file;
			try {
				file = new File(br.readLine());
			
			if(!file.exists())
			{
				System.out.println("path error,reWrite!");
			}
			else if(file.isDirectory())
			{				
				System.out.println("file is open,reWrite!");
			}
			else
			{
				return file;
			}
			} catch (IOException e) {
				// TODO 
				e.printStackTrace();
			}
			
		}
		
	}
}


