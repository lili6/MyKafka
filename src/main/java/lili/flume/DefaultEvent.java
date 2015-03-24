package lili.flume;

import org.apache.flume.Event;

import java.util.HashMap;
import java.util.Map;

public class DefaultEvent implements Event {

	private Map<String, String> headers;
	private byte[] body;

	public DefaultEvent() {
		super();
	}

	public DefaultEvent(byte[] body) {
		super();
		this.body = body;
	}

	public DefaultEvent(Map<String, String> headers, byte[] body) {
		super();
		this.headers = headers;
		this.body = body;
	}

	@Override
	public Map<String, String> getHeaders() {
		return this.headers;
	}

	@Override
	public void setHeaders(Map<String, String> headers) {
		this.headers = headers;
	}

	@Override
	public byte[] getBody() {
		return this.body;
	}

	@Override
	public void setBody(byte[] body) {
		this.body = body;
	}

	public void addHeader(String key, String value) {
		if (null == headers) {
			headers = new HashMap<String, String>();
		}
		headers.put(key, value);
	}

	public String getHeader(String key) {
		if (null == headers) {
			headers = new HashMap<String, String>();
		}
		return headers.get(key);
	}

	@Override
	public String toString() {
		Integer bodyLen = null;
		if (body != null)
			bodyLen = body.length;
		return "[Event headers = " + headers + ", body.length = " + bodyLen + " ]";
	}

}
