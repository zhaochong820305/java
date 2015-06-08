import java.io.*;
import java.net.*;
import java.nio.ByteBuffer;
import java.text.*;
import java.util.Date;
public class uploadServer {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO 
		int iport=8025;
		try {
			ServerSocket ss = new ServerSocket(iport);
			System.out.println("Server is run listen:"+iport);
			while(true)
			{
				Socket so = ss.accept();
				new ServerThread(so).start();
			}
			
		} catch (IOException e) {
			// TODO
			e.printStackTrace();
		}
	}

}
class ServerThread extends Thread{
	Socket so1;
	public ServerThread(Socket so1){
		this.so1 = so1;
	}
	
	public void run(){
		FileOutputStream fos = null;
		try{
			BufferedReader br = new BufferedReader(
					new InputStreamReader(so1.getInputStream()));
			PrintStream ps = new PrintStream(so1.getOutputStream());
			
			String fileName="";// = br.readLine();
			long fileLen=0 ;//= Long.parseLong(br.readLine());
			//ByteBuffer readBuf = ByteBuffer.allocate(6);
			char[] cbuf =new char[6];
			String Scmd ="";
			  br.read(cbuf, 0, 6);
			  Scmd = String.valueOf(cbuf);
			if(Scmd.equals("UPLOAD")==false)
			{
				System.out.println("upload cmd is error");
				return;
			}
			
			String str = br.readLine();
		
			String[] sArray = str.split("\\|");
			
			
			if (sArray.length>=2)
			{
				//Scmd = sArray[0];
				fileName = sArray[0];
				fileLen = Long.parseLong(sArray[1]);
			}
			
			
			
				File dir = new File("upload");
				dir.mkdir();
				
				File file = new File(dir, fileName);
				
				if (file.exists() ){//&& file.length()==fileLen
					ps.println("Exist");
					return;
				}
				else{
					ps.println(file.length());
				}
				
				String time = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
				String ip = so1.getInetAddress().getHostAddress();
				System.out.println(time+" "+ip+(file.exists()?"start reupload":"start upload file:"+file.getName()));
				long start = System.currentTimeMillis();
				InputStream in = so1.getInputStream();
				fos = new FileOutputStream(file,true);
				byte[] buffer = new byte[1024];
				int len;
				while((len = in.read(buffer))!=-1){
					fos.write(buffer,0,len);
					if(file.length()==fileLen)
					{
						break;
					}	
					
				}
				fos.close();
				long end = System.currentTimeMillis();
				time = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
				System.out.println(time+" "+ip+"upload file end "+ file.getName()+" time long:"+(end-start)+"ms");
				ps.println("upload finish is true");
				so1.close();
			
		}catch  (IOException ex) 
		{
			if (fos != null)
			try{
				fos.close();
			}catch(IOException e1){
				e1.printStackTrace();
			}	
		}
	}
}
