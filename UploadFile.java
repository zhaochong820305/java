import java.io.*;
import java.net.*;
public class UploadFile {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO
		try
		{
			Socket so = new Socket("192.168.0.156",8022);
			BufferedReader br = new BufferedReader(
					new InputStreamReader(so.getInputStream()));
			PrintStream ps = new PrintStream(so.getOutputStream());
			File file = getFile();
			ps.println("UPLOAD"+file.getName()+"|"+file.length());
			//ps.println(file.length());
			
			String msg = br.readLine();
			if("Exist".equals(msg))
			{
				System.out.println("the file is exist,thie file is not upload");
				return;
			}
			long finishLen = Long.parseLong(msg);
			
			FileInputStream fis = new FileInputStream(file);
			OutputStream out = so.getOutputStream();
			byte[] buffer  = new byte[1024];
			int len;
			fis.skip(finishLen);
			while((len = fis.read(buffer))!=-1)
				out.write(buffer,0,len);
				
			fis.close();
			System.out.println(br.readLine());
			
			so.close();
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
