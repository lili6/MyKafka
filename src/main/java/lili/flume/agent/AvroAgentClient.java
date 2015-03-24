package lili.flume.agent;

import org.apache.flume.Event;
import org.apache.flume.agent.embedded.EmbeddedAgent;
import org.apache.flume.event.SimpleEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Timestamp;
import java.util.*;

/**
 * Created by lili on 2015/3/24.
 */
public class AvroAgentClient {
	private static Logger log = LoggerFactory.getLogger("AvroAgentClient");
	private static EmbeddedAgent agent;


	private boolean initAgent() {
		System.out.println("===>开始创建 agent ！！！");
		Properties properties = new Properties();
		try {
			properties.load(ClassLoader.getSystemResourceAsStream("flume/agent.properties"));
			//		ClassLoader  = ClassLoader.getSystemClassLoader().getResource("agent.properties");
			Map<String, String> map = new HashMap<String, String>((Map) properties);
			agent = new EmbeddedAgent("AvroAgent");
			agent.configure(map);
			agent.start();
			log.debug("agent 创建成功!!");
			return true;
		} catch (Exception e) {
			log.error("创建AvroAgent 失败！！！");
			return false;
		}

	}
	public void postMsg(int i) throws Exception{
		Timestamp tServerTime = new Timestamp(System.currentTimeMillis());
		String key = UUID.randomUUID().toString().toUpperCase();
		System.out.println("key::" + key);
//		String msg = "key::" + key + " =====coming...";
		String msg = "====【Hello Flume】序号=【"+i+"】.";
		log.debug("====【Hello Flume】序号=【{}】",i);
		Event event = new SimpleEvent();
		event.getHeaders().put("project", "mobilecrash");
		Calendar calTime = Calendar.getInstance();
		calTime.setTime(tServerTime);
		calTime.add(Calendar.MILLISECOND, TimeZone.getDefault().getRawOffset());
		event.getHeaders().put("T", calTime.getTime().toGMTString());
		event.setBody(msg.getBytes());
		agent.put(event);
	}

	public static void main(String[] args) throws Exception{
		AvroAgentClient flumeAgent = new AvroAgentClient();
		if (!flumeAgent.initAgent()) {
			System.exit(0);
		}
		for (int i= 0; i<50; i++) {
			flumeAgent.postMsg(i);
			log.debug("第【" + i + "】次");
			Thread.sleep(6000);
		}
		agent.stop();
		log.debug("发数据结束!!");
	}
}
