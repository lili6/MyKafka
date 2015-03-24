package lili.flume.source;

import lili.flume.DefaultEvent;
import org.apache.commons.lang.NullArgumentException;
import org.apache.flume.Event;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

/**
 * Created by lili on 2015/3/24.
 * 将收到的数据进行压缩，并传给FileChannel，由Sink进行处理
 */
public class ZipSourceProcessor {
	/**
	 * Logger for this class
	 */
	private static final Logger logger = LoggerFactory.getLogger(ZipSourceProcessor.class);

	private static final char SEPAREATE_CHAR = 0x01;

	public List<Event> process(final Event event) {
		if (logger.isDebugEnabled()) {
			logger.debug("Event - start"); //$NON-NLS-1$
		}

		if (null == event) {
			throw new NullArgumentException("event is null!");
		}
		List<Event> events = null;

		try {
			ByteArrayInputStream bais = new ByteArrayInputStream(event.getBody());
			ZipInputStream zis = new ZipInputStream(bais);
			events = buildEvents(zis, event);
			zis.close();
			bais.close();

		} catch (IOException e) {
			logger.error("Event is droped!", e); //$NON-NLS-1$
			return new ArrayList<Event>();
		} catch (Exception e) {
			logger.error("Event", e);
			throw new NullArgumentException("event is null!");
		}

		if (logger.isDebugEnabled()) {
			logger.debug("Event - end"); //$NON-NLS-1$
		}
		return events;
	}
	public List<Event> buildEvents(ZipInputStream zis, Event event) throws IOException {
		List<Event> events = new ArrayList<Event>();
		ZipEntry zip = zis.getNextEntry();
		if (zip == null)
			return events;
		String name = zip.getName();
		if (zip.isDirectory()) {
			events.addAll(buildEvents(zis, event));
		} else {
			ByteArrayOutputStream baos = new ByteArrayOutputStream(1024*4);

			byte b[] = new byte[1024*4];
			int aa = 0;
			while ((aa = zis.read(b)) != -1) {
				baos.write(b, 0, aa);
			}
			if (name.endsWith(".log")) {
				String logtype = name.replace(".log", "");

				Map<String, String> headers = new HashMap<String, String>(event.getHeaders());
				headers.put("logtype", logtype);
				DefaultEvent e = new DefaultEvent(headers, baos.toByteArray());
				events.add(e);
			} else {
				if (logger.isDebugEnabled()) {
					logger.debug(name + " does not end with .log, So skip it!"); //$NON-NLS-1$
				}
			}
			baos.close();
			events.addAll(buildEvents(zis, event));
		}
		return events;
	}
}
