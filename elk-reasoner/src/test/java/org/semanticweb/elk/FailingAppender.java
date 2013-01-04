package org.semanticweb.elk;
/*
 * #%L
 * ELK Reasoner
 * $Id:$
 * $HeadURL:$
 * %%
 * Copyright (C) 2011 - 2013 Department of Computer Science, University of Oxford
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */

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
