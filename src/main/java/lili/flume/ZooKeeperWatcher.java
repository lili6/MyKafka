package lili.flume;

import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooKeeper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

/**
 * Created by lili on 2015/3/24.
 */
public class ZooKeeperWatcher  implements Watcher {
	/**
	 * Logger for this class
	 */
	private static final Logger logger = LoggerFactory.getLogger(ZooKeeperWatcher.class);

	private ZooKeeper zooKeeper = null;

	private ProcessorType type;

	public ZooKeeperWatcher() {
		super();
	}
	public ZooKeeperWatcher(ProcessorType type) {
		this.type = type;
	}


	public void connect(String hosts) throws IOException,
			InterruptedException, KeeperException {
		if (logger.isDebugEnabled()) {
			logger.debug("zk connect - start"); //$NON-NLS-1$
		}
		zooKeeper = new ZooKeeper(hosts, 10000, this);
		logger.debug("================zookeeper state::{}", zooKeeper.getState());
		logger.debug("================zookeeper is alive::{}", zooKeeper.getState().isAlive());
	}
	@Override
	public void process(WatchedEvent watchedEvent) {
		 //do nothing..
		logger.debug("zk watcher do something...");
	}

	public enum ProcessorType {
		SOURCE("/flume/test/source"), SINK("/flume/test/sink");

		private String value;

		ProcessorType(String value) {
			this.value = value;
		}

		public String getValue() {
			return this.value;
		}
	}
}

