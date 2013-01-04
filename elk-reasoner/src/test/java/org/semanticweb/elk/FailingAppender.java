package org.semanticweb.elk;

import org.apache.log4j.AppenderSkeleton;
import org.apache.log4j.spi.LoggingEvent;
import org.semanticweb.elk.owl.exceptions.ElkRuntimeException;

/**
 * An {@link Appender} that throws {@link ElkRuntimeException} on received
 * messages.
 * 
 * @author "Yevgeny Kazakov"
 * 
 */
public class FailingAppender extends AppenderSkeleton {

	@Override
	protected void append(LoggingEvent event) {
		throw new ElkRuntimeException(event.getRenderedMessage());
	}

	@Override
	public void close() {
		return;
	}

	@Override
	public boolean requiresLayout() {
		return false;
	}

}
