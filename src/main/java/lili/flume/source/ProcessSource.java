package lili.flume.source;

import lili.flume.ZooKeeperWatcher;
import org.apache.flume.ChannelException;
import org.apache.flume.Context;
import org.apache.flume.Event;
import org.apache.flume.event.EventBuilder;
import org.apache.flume.instrumentation.SourceCounter;
import org.apache.flume.source.AvroSource;
import org.apache.flume.source.avro.AvroFlumeEvent;
import org.apache.flume.source.avro.Status;
import org.apache.zookeeper.KeeperException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by lili on 2015/3/24.
 */
public class ProcessSource extends AvroSource{
	/**
	 * Logger for this class
	 */
	private static final Logger logger = LoggerFactory.getLogger(ProcessSource.class);

	protected static final int DEFAULT_BATCH = 100;
	protected SourceCounter sourceCounter;

	private ZooKeeperWatcher zkWatcher;

	private String processorsDir;

	private int batch;

	private ZipSourceProcessor  zipSourceProcessor = null;
	@Override
	public void configure(Context context) {
		logger.debug("configure---Context  start..");
		super.configure(context);

		String zkConnect = context.getString("zkConnect","localhost:2181");
		logger.debug("===================zkConnect:::{}",zkConnect);
		batch = context.getInteger("batch.size", DEFAULT_BATCH);
		logger.debug("===================batch:::{}",batch);
		this.zkWatcher = new ZooKeeperWatcher(ZooKeeperWatcher.ProcessorType.SOURCE);
		zipSourceProcessor = new ZipSourceProcessor();
		try {
			zkWatcher.connect(zkConnect);
			// "/flume-ng/config/processors/sinks/"
		} catch (IOException e) {
			logger.error("Context", e); //$NON-NLS-1$
		} catch (InterruptedException e) {
			logger.error("Context", e); //$NON-NLS-1$
		} catch (KeeperException e) {
			logger.error("Context", e); //$NON-NLS-1$
		}

		if (sourceCounter == null) {
			sourceCounter = new SourceCounter(getName());
		}

		if (logger.isDebugEnabled()) {
			logger.debug("Context - end"); //$NON-NLS-1$
		}
	}


	@SuppressWarnings("unchecked")
	@Override
	public Status append(AvroFlumeEvent avroEvent) {

		if (logger.isDebugEnabled()) {
			logger.debug("AvroFlumeEvent - start"); //$NON-NLS-1$
		}

		logger.debug("Avro source {}: Received avro event: {}", getName(), avroEvent);
		Map<String, String> map = new HashMap<String, String>();
		try {
			map = (Map<String, String>) ReflectionUtils.invokeMethod(this, "toStringMap", new Class[] { Map.class }, new Object[] { avroEvent.getHeaders() });
		} catch (InvocationTargetException e) {
			logger.error("AvroFlumeEvent", e); //$NON-NLS-1$
		}
		Event event = EventBuilder.withBody(avroEvent.getBody().array(), map);

//		Map<String, SourceProcessorInterface> processorsMap = SourceProcessorFactory.getSourceProcessor(event.getHeaders().get("project"));
		List<Event> events = new ArrayList<Event>();


		List<Event> evts = zipSourceProcessor.process(event);
		events.addAll(evts);

		sourceCounter.incrementAppendReceivedCount();
		sourceCounter.incrementEventReceivedCount();

		try {
			getChannelProcessor().processEventBatch(events);
		} catch (ChannelException ex) {
			logger.error("AvroFlumeEvent", ex); //$NON-NLS-1$

			logger.warn("Avro source " + getName() + ": Unable to process event. " + "Exception follows.", ex);

			return Status.FAILED;
		}
		sourceCounter.incrementAppendAcceptedCount();
		sourceCounter.addToEventAcceptedCount(events.size());

		if (logger.isDebugEnabled()) {
			logger.debug("AvroFlumeEvent - end"); //$NON-NLS-1$
		}
		return Status.OK;
	}

}
