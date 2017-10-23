/*
 * #%L
 * ELK Utilities for Logging
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
package org.semanticweb.elk.util.logging;

import org.slf4j.Logger;
import org.slf4j.Marker;

/**
 * A tiny wrapper to compensate the lack of a generic log method in SLF4J
 * 
 * @author Pavel Klinov
 *
 * pavel.klinov@uni-ulm.de
 * @author Peter Skocovsky
 */
public class LoggerWrap {

	public static boolean isEnabledFor(Logger logger, LogLevel level) {
		switch (level) {
        case TRACE:
            return logger.isTraceEnabled();
        case DEBUG:
            return logger.isDebugEnabled();
        case INFO:
            return logger.isInfoEnabled();
        case WARN:
            return logger.isWarnEnabled();
        case ERROR:
            return logger.isErrorEnabled();
		}
		
		return false;
	}
	
	public static void log(Logger logger, LogLevel level, String format, Object[] argArray) {
	    switch (level) {
	        case TRACE:
	            logger.trace(format, argArray);
	            break;
	        case DEBUG:
	            logger.debug(format, argArray);
	            break;
	        case INFO:
	            logger.info(format, argArray);
	            break;
	        case WARN:
	            logger.warn(format, argArray);
	            break;
	        case ERROR:
	            logger.error(format, argArray);
	            break;
	    }
	}

	public static void log(final Logger logger, final LogLevel level,
			final Marker marker, final String message) {
		switch (level) {
		case TRACE:
			logger.trace(marker, message);
			break;
		case DEBUG:
			logger.debug(marker, message);
			break;
		case INFO:
			logger.info(marker, message);
			break;
		case WARN:
			logger.warn(marker, message);
			break;
		case ERROR:
			logger.error(marker, message);
			break;
		}
	}

	public static void log(Logger logger, LogLevel level, String message) {
	    switch (level) {
	        case TRACE:
	            logger.trace(message);
	            break;
	        case DEBUG:
	            logger.debug(message);
	            break;
	        case INFO:
	            logger.info(message);
	            break;
	        case WARN:
	            logger.warn(message);
	            break;
	        case ERROR:
	            logger.error(message);
	            break;
	    }
	}
	
}
