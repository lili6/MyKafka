package lili.flume.sink;

import org.apache.flume.Event;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by lili on 2015/3/24.
 */
public class DemoSinkProcessor {
	private static final Logger logger = LoggerFactory.getLogger(DemoSinkProcessor.class);


	public void process(Event event) {
		if (event != null) {
			// String line = EventHelper.dumpEvent(event);
			// logger.debug(line);
			logger.debug("============sink process begin...：：：" );

			String project = event.getHeaders().get("project");
			String logtype = event.getHeaders().get("logtype");
			String appkey = event.getHeaders().get("appkey");
			String topic = project + "-" + appkey + "-" +logtype;
			if (logger.isInfoEnabled()) {
				logger.info("Event - Headers ={}", event.getHeaders()); //$NON-NLS-1$
			}

			byte[] body = event.getBody();

			logger.debug("收到的数据为：：：{}",new String(body) );
			logger.debug("============sink process end...：：：" );

		}
	}
}
