package lili.flume;

import com.google.common.collect.Lists;
import org.apache.flume.Event;
import org.apache.flume.agent.embedded.EmbeddedAgent;
import org.apache.flume.event.EventBuilder;

import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by lili on 15/3/22.
 */
public class EmbedAgentClient {
    public static void main(String[] args) throws Exception{

        Map<String, String> properties = new HashMap<String, String>();
        properties.put("channel.type", "memory");
        properties.put("channel.capacity", "200");
//        properties.put("sinks", "sink1 sink2");
        properties.put("sinks", "sink1");
        properties.put("sink1.type", "avro");
//        properties.put("sink2.type", "avro");
        properties.put("sink1.hostname", "slave1");
        properties.put("sink1.port", "4141");
//        properties.put("sink2.hostname", "collector2.apache.org");
//        properties.put("sink2.port", "5565");
//        properties.put("processor.type", "load_balance");
        properties.put("processor.type", "failover");
        properties.put("sink1.batch-size", "100");



        EmbeddedAgent agent = new EmbeddedAgent("myagent");

        agent.configure(properties);
        agent.start();
        Event event = EventBuilder.withBody("Hello Flume!Good morning.....",
                Charset.forName("UTF-8"));

        List<Event> events = new ArrayList<Event>();

        events.add(event);
        events.add(event);
        events.add(event);

        agent.putAll(events);

        agent.stop();

    }

}
