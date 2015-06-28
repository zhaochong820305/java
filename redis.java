import java.util.HashMap;
import java.util.*;
//import java.awt.List;
import org.*;
import redis.clients.jedis.Jedis;
public class redis {

	/**
	 * @param args
	 */
	private static  Jedis jedis;
	public void setup(){
		jedis = new Jedis("127.0.0.1",6379);
		//jedis.auth("admin");
		
	}
	//@Test
	public  void testString(){
		jedis.set("name", "xinxin");//add
		System.out.println(jedis.get("name"));
		
		jedis.append("name", " is my lover");//connect
		System.out.println(jedis.get("name"));
		
		jedis.set("name", "zz");
		System.out.println(jedis.get("name"));
		String[] aas = {"name"};
		jedis.del(aas);//del
		System.out.println(jedis.get("name"));
		
		//jedis.set("name","liuling");
		//jedis.set("age","23");
		//jedis.set("qq","94174566");
		//,"age","23","qq","94174566");
		String[] aa =  {"name","liuling","age","23","qq","476777389"};
		jedis.mset(aa);
		jedis.incr("age");//add+1
		System.out.println(jedis.get("name") + "-" + jedis.get("age") + "-" + jedis.get("qq"));
		
	}
	//@SuppressWarnings("unchecked")
	public void testMap(){
		Map user = new HashMap();
		user.put("name", "minxr");
		user.put("pwd", "value");
		jedis.hmset("user", user);
		String[] str1 = {"name"};
		List rsmap =(List) jedis.hmget("user", str1);
		System.out.println(rsmap);
		
		
		String[] str2 = {"pwd"};
		jedis.hdel("user", str2);
		System.out.println(jedis.hmget("user",str2));
		System.out.println(jedis.hlen("user"));
		System.out.println(jedis.exists("user"));
		System.out.println(jedis.hkeys("user"));
		System.out.println(jedis.hvals("user"));
		
		Iterator iter = jedis.hkeys("user").iterator();
		while(iter.hasNext()){
			String key = iter.next().toString();
			String[] aa = {key};
			System.out.println(key+":"+jedis.hmget("user", aa));
		}
		
	}
	public static void main(String[] args) {
		redis r1 = new redis();
		r1.setup();
		r1.testString();
		
		r1.testMap();
	}

}
