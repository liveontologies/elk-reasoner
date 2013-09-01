/**
 * 
 */
package org.semanticweb.elk.util.logging;
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

import org.slf4j.Logger;

/**
 * A tiny wrapper to compensate the lack of a generic log method in SLF4J
 * 
 * @author Pavel Klinov
 *
 * pavel.klinov@uni-ulm.de
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
	
	/**
	 * A wrapping method so we can switch to using SLF4J markers when they are supported by
	 * the logging frameworks that we care about, e.g., log4j
	 */
	public static void log(Logger logger, LogLevel level, String type, String message) {
		log(logger, level, ElkMessage.serialize(type, message));
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
