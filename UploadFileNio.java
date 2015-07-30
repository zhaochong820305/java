import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.channels.SelectableChannel;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
import java.nio.channels.FileChannel;

public class UploadFileNio {

	public static int PORT_NUMBER = 9026;
	int iconn=1;

	public static void main(String args[]) throws Exception {
		new UploadFileNio().go();
	}

	public void go() throws Exception {
		int port = PORT_NUMBER;
		try{
			ServerSocketChannel serverChannel = ServerSocketChannel.open();
			ServerSocket serverSocket = serverChannel.socket();
			// Set the port the server channel will listen to
			serverSocket.bind(new InetSocketAddress(port));
			// Set nonblocking mode for the listening socket
			serverChannel.configureBlocking(false);
			System.out.println("Listening port: " + PORT_NUMBER);
			Selector selector = Selector.open();
	
			// Register the ServerSocketChannel with the Selector
			serverChannel.register(selector, SelectionKey.OP_ACCEPT);
			while (true) {
				// This may block for a long time. Upon returning, the // selected
				// set contains keys of the ready channels.
				int n = selector.select();
				if (n == 0) {
					// nothing to do
					continue;
				}
	
				// Get an iterator over the set of selected keys
				 
					Iterator it = selector.selectedKeys().iterator();
				 
				while(it.hasNext()) {
 
					// Look at each key in the selected set while (it.hasNext()) {
					SelectionKey key = (SelectionKey) it.next();
		
					// step1 Is a new connection coming in?
					if (key.isAcceptable()) {
					//if((key.readyOps()& SelectionKey.OP_ACCEPT)==SelectionKey.OP_ACCEPT){
						iconn++;
						ServerSocketChannel server = (ServerSocketChannel) key.channel();
						SocketChannel channel = server.accept();
						registerChannel(selector, channel, SelectionKey.OP_READ);
						//Socket socket = channel.socket(); 
						System.out.println("the clent:"+"["+ channel.getRemoteAddress()+"]");
						sayHello(channel, iconn);
						
					}
		
					// step2 Is there data to read on this channel?
					if( key.isReadable()){
					//else if( (key.readyOps()& SelectionKey.OP_READ) == SelectionKey.OP_READ){
						//ServerSocketChannel server = (ServerSocketChannel) key
						//		.channel();
						//SocketChannel channel1 = server.accept();
						//registerChannel(selector, channel1, SelectionKey.OP_READ);
						//readDataFromSocket(key);
						//readDataFile(key);
						receive(key);
					}
		
					// step3 Remove key from selected set; it's been handled
					it.remove();
				}
			}
		}catch (Exception ex)
		
		{
			System.out.println("connect error!" );
		}

	}

	protected void registerChannel(Selector selector,
			SelectableChannel channel, int ops) throws Exception {
		if (channel == null) {

			return; // could happen
		}
		// Set the new channel nonblocking
		channel.configureBlocking(false);
		// Register it with the selector
		channel.register(selector, ops);
	}

	// Use the same byte buffer for all channels. A single thread is //
	// servicing all the channels, so no danger of concurrent acccess.
	private ByteBuffer buffer = ByteBuffer.allocateDirect(1024);
	protected void receive(SelectionKey key) throws Exception{
		SocketChannel socketChannel = (SocketChannel)key.channel();
		try{
			
			ByteBuffer readBuf = ByteBuffer.allocate(4);
			socketChannel.read(readBuf);
			
			String fileName="";  
			long fileLen=0 ; 
			//int filenamelen=0;
			readBuf.flip();
			String str = getString(readBuf);
			if(str.substring(0,4).equals("ABAC"))
			{
//				try
//				{
//					filenamelen = (int) Long.parseLong(str.substring(4,6));
//				}
//				catch(Exception ex)
//				{
//					prints("file name is len error");
//					return;
//				}
				readBuf.clear();
				
				readBuf = ByteBuffer.allocate(33);
				socketChannel.read(readBuf);
				readBuf.flip();
				str = getString(readBuf);
				
				readBuf.flip();
				   String[]  sarray = str.split("\\|");
				   int ilen = sarray.length;
				   if (ilen>=3)
				   {
					    
						fileName = sarray[3].replaceFirst("^0*", ""); ;
						try
						{
							fileLen =Integer.parseInt(sarray[1],16) ;
						}
						catch(Exception ex)
						{
							prints("file is len error");
							return;
						}
						prints("file is big:"+fileLen);
						File directory = new File("upload");
						FileOutputStream fos = new FileOutputStream(directory.getCanonicalPath()+"\\"+fileName);
						
						//String erro="yes";
						//ByteBuffer buffer1 = ByteBuffer.allocate(3);
						//buffer1.put(getByteBuffer(erro));
						//buffer1.flip();
						
						FileChannel foc = fos.getChannel();
						foc.position(0);
						ByteBuffer buffer = ByteBuffer.allocate(1024*10);
						prints("this is receiving file");
						double byteRead=0;
						int byteAll =(int)fileLen;
						int b=0;
						while(fileLen>0)
						{
							if(fileLen<1024*10)
							{
								buffer = ByteBuffer.allocate((int)fileLen);
							}
							socketChannel.read(buffer);
							int position = buffer.position();
							buffer.flip();
							while(buffer.hasRemaining())
								foc.write(buffer);
							buffer.compact();
							fileLen=fileLen-position;
							byteRead += position;
							while(b>0)
							{
								System.out.print('\b');
								b--;
							}
							prints("this is finish:"+(int)(byteRead*100/byteAll)+"%");
							b=("finish:"+String.valueOf((int)(byteRead*100/byteAll))+"%.").getBytes().length;
							
						}
						
						prints("");
						prints(directory+"\\"+fileName+" the file is finish@");
						fos.close();
						//socketChannel.write(getByteBuffer("upload finish is true"));
				   }
			}
			else 
			{
				socketChannel.close();
				key.cancel();
				prints("exit run");
				System.exit(0);
			}
		}
		catch(Exception ex)
		{
			socketChannel.close();
			key.cancel();
			prints("exit run");
		}
	}
	
	protected void readDataFile(SelectionKey key) throws Exception {
		//try{
			SocketChannel socketChannel = (SocketChannel) key.channel();
			//ServerSocketChannel server = (ServerSocketChannel) key.channel();
			//SocketChannel channel = server.accept();
			socketChannel.configureBlocking(false);
			int count;
			
			String fileName="";  
			long fileLen=0 ;    
			buffer.clear(); // Empty buffer
			ByteBuffer buffer1 = ByteBuffer.allocateDirect(1024);
			// Loop while data is available; channel is nonblocking
			try
			{
			if ((count = socketChannel.read(buffer)) > 0) {
				buffer.flip(); // Make buffer readable
				// Send the data; don't assume it goes all at once
				//while (buffer.hasRemaining()) {ng
				String str = getString(buffer);
				   System.out.println("the clent:"+socketChannel.getRemoteAddress()+" data:"+str  );
				   //socketChannel.write(buffer);
				   String sCMD = str.substring(0,4);
				   if(sCMD.equals("ABAC"))
				   {
					   //prints("this is true ,this file is ready");
					   String[]  sArray = str.split("\\|");
					   int ilen = sArray.length;
					   if (ilen>=4)
					   {
						   //if(sArray[1].equals("0"))
						   //{							
								//Scmd = sArray[0];
								fileName = sArray[2];
								fileLen = Long.parseLong(sArray[3]);
								
								File dir = new File("upload");
								dir.mkdir();
								
								File file = new File(dir, fileName);
								
//								if (file.exists() ){//&& file.length()==fileLen
//									socketChannel.write(getByteBuffer("Exist"));
//									
//									return;
//								}
//								else{
//									socketChannel.write(getByteBuffer(""+file.length()));
//
//									//buffer1.clear();
//									//buffer1.put((""+file.length()).getBytes());
//									//buffer1.flip();
//									//socketChannel.write(buffer1);//socketChannel.write(buffer);
//								}
								String time = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
								SocketAddress ip = socketChannel.getRemoteAddress();
								System.out.println(time+" "+ip+(file.exists()?"start reupload":"start upload file:"+file.getName()));
								long start = System.currentTimeMillis();
								FileOutputStream fos = new FileOutputStream(dir+fileName);
								//InputStream in = socketChannel.getInputStream();
								FileChannel foc =fos.getChannel();
								fos = new FileOutputStream(file,true);
								//byte[] buffer = new byte[1024];
								int len;
								while((len = socketChannel.read(buffer))!=-1){
									//fos.write(buffer,0,len);
									if(file.length()==fileLen)
									{
										break;
									}	
									
								}
								fos.close();
								long end = System.currentTimeMillis();
								time = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
								System.out.println(time+" "+ip+"upload file end "+ file.getName()+" time long:"+(end-start)+"ms");
								//ps.println("upload finish is true");
								//so1.close();
						  // }
						   //else
						  // {
							   
						  // }
					   }
				   }
				   else
				   {
					   return;
				   }
					
				//}
				//System.out.println((char)buffer.get());
				//socketChannel.wait(10);
				
				// WARNING: the above loop is evil. Because
				// it's writing back to the same nonblocking
				// channel it read the data from, this code can
				// potentially spin in a busy loop. In real life
				// you'd do something more useful than this.
				buffer.clear(); // Empty buffer
	
			}
			if (count < 0) {
				// Close channel on EOF, invalidates the key
				socketChannel.close();
			}
		}catch (Exception ex)
		{	
			socketChannel.close();
			System.out.println("socketChannel.read error,sokect colose!" );
		}
	}
	
	 public static ByteBuffer getByteBuffer(String str)  
	    {  
	        return ByteBuffer.wrap(str.getBytes());  
	    }  
	 
	protected void prints(String str)
	{
		System.out.println(str);
	}
	protected void readDataFromSocket(SelectionKey key) throws Exception {
		//try{
			SocketChannel socketChannel = (SocketChannel) key.channel();
			socketChannel.configureBlocking(false);
			int count;
			buffer.clear(); // Empty buffer
			// Loop while data is available; channel is nonblocking
			try
			{
			if ((count = socketChannel.read(buffer)) > 0) {
				buffer.flip(); // Make buffer readable
				// Send the data; don't assume it goes all at once
				//while (buffer.hasRemaining()) {
				   System.out.println("the clent:"+socketChannel.getRemoteAddress()+" data:"+getString(buffer)  );
				   socketChannel.write(buffer);
				  // socketChannel.
				   //buffer.
					
				//}
				//System.out.println((char)buffer.get());
				//socketChannel.wait(10);
				
				// WARNING: the above loop is evil. Because
				// it's writing back to the same nonblocking
				// channel it read the data from, this code can
				// potentially spin in a busy loop. In real life
				// you'd do something more useful than this.
				buffer.clear(); // Empty buffer
	
			}
			if (count < 0) {
				// Close channel on EOF, invalidates the key
				socketChannel.close();
			}
		}catch (Exception ex)
		{	
			socketChannel.close();
			System.out.println("socketChannel.read error,sokect colose!" );
		}
	}
	public static String getString(ByteBuffer buffer)  
    {  
        Charset charset = null;  
        CharsetDecoder decoder = null;  
        CharBuffer charBuffer = null;  
        try  
        {  
            charset = Charset.forName("UTF-8");  
            decoder = charset.newDecoder();  
            // charBuffer = decoder.decode(buffer);
            charBuffer = decoder.decode(buffer.asReadOnlyBuffer());  
            return charBuffer.toString();  
        }  
        catch (Exception ex)  
        {  
            ex.printStackTrace();  
            return "";  
        }  
    }  

	private void sayHello(SocketChannel channel,int iconns) throws Exception {
		
		buffer.clear();
		//buffer.put(("the client:"+iconns+" Hi there!\r\n"+channel.getRemoteAddress()).getBytes());
		//iconn++;
		
		buffer.flip();
		channel.write(buffer);
	}
}

