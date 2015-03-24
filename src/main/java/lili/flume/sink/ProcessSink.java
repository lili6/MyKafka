package lili.flume.sink;

import kafka.common.MessageSizeTooLargeException;
import lili.flume.ZooKeeperWatcher;
import org.apache.flume.*;
import org.apache.flume.conf.Configurable;
import org.apache.flume.sink.AbstractSink;
import org.apache.zookeeper.KeeperException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

/**
 * Created by lili on 2015/3/24.
 */
public class ProcessSink extends AbstractSink implements Configurable {
	/**
	 * Logger for this class
	 */
	private static final Logger logger = LoggerFactory.getLogger(ProcessSink.class);

	private ZooKeeperWatcher zkWatcher;
	private DemoSinkProcessor sinkProcessor;

	@Override
	public Status process() throws EventDeliveryException {
//		if (logger.isDebugEnabled()) {
//			logger.debug("<no args> - start"); //$NON-NLS-1$
//		}
		Status status = Status.READY;
		Channel channel = getChannel();
		Transaction tx = null;
		try {
			tx = channel.getTransaction();
			tx.begin();

			Event event = channel.take();
			if (null != event) {
				logger.info("project - " + event.getHeaders().get("project")); //$NON-NLS-1$
				sinkProcessor.process(event);
			} else {
				status = Status.BACKOFF;
			}

			tx.commit();
		}
		catch (MessageSizeTooLargeException me){
			logger.error("can't process events, drop it!", me);
			if (tx != null) {
				tx.commit();// commit to drop bad event, otherwise it will enter dead loop.
			}
		}
		catch (Exception re) {
			if (tx != null) {
				tx.rollback();
			}
			throw new EventDeliveryException(re);
		} catch (Throwable e) {
			logger.error("can't process events, drop it!", e);
			if (tx != null) {
				tx.commit();// commit to drop bad event, otherwise it will enter dead loop.
			}

		}
		finally {
			if (tx != null) {
				tx.close();
			}
		}

//		if (logger.isDebugEnabled()) {
//			logger.debug("<no args> - end"); //$NON-NLS-1$
//		}
		return status;
	}

	@Override
	public void configure(Context context) {
		if (logger.isDebugEnabled()) {
			logger.debug("ProcessSink Context configure - start"); //$NON-NLS-1$
		}

		sinkProcessor = new DemoSinkProcessor();
		String zkConnect = context.getString("zkConnect","localhost:2181");
		logger.info(zkConnect);
		zkWatcher = new ZooKeeperWatcher(ZooKeeperWatcher.ProcessorType.SINK);
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

		if (logger.isDebugEnabled()) {
			logger.debug("ProcessSink Context configure - end"); //$NON-NLS-1$
		}
	}

}
