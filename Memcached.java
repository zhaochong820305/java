
//package client;

import com.danga.MemCached.MemCachedClient;
import com.danga.MemCached.SockIOPool;
 

public class Memcached {
    
    public static void main(String[] args) {
        
        /**SockIOPool **/
        String[] servers = { "127.0.0.1:11211" };
        //String cachename = "_productNum";

        SockIOPool pool = SockIOPool.getInstance();
       // SockIOPool pool = SockIOPool.getInstance();
        pool.setServers(servers);
        pool.setFailover(true);
        pool.setInitConn(10);
        pool.setMinConn(5);
        pool.setMaxConn(250);
        pool.setMaintSleep(30);
        pool.setNagle(false);
        pool.setSocketTO(3000);
        pool.setAliveCheck(true);
        pool.initialize();
        
        /**MemcachedClient**/
        MemCachedClient memCachedClient = new MemCachedClient();
        for (int i = 0; i < 10; i++) {
             
            boolean success = memCachedClient.set("" + i, "Hello!"+i);
           
            String result = (String) memCachedClient.get("" + i);
            System.out.println("set( "+i+" ): "+success);
            System.out.println("get( "+i+" ): --"+ result);
        }
    }
    
}
