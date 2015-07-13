import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintStream;
import java.net.Socket;
public class UploadFile {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO
		try
		{
			Socket so = new Socket("172.24.0.122",9026);
			BufferedReader br = new BufferedReader(
					new InputStreamReader(so.getInputStream()));
			PrintStream ps = new PrintStream(so.getOutputStream());
			File file = getFile();
			int ilen = ("|"+file.getName()+"|"+file.length()+"|").length();
			String slen ="";
			if(ilen<10)
			{
				slen="0"+ilen;
			}
			else if(ilen > 99)				
			{
				System.out.println("file is name len is long");
				return;
			}else
			{
				slen=""+ilen;
			}
			String str = "ABAC"+slen+"|"+file.getName()+"|"+file.length()+"|";
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
			//String msg =""+ fis.read(buffers);//.//so.read(buffer);
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
