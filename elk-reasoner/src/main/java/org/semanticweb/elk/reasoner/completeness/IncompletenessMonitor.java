/*-
 * #%L
 * ELK Reasoner Core
 * $Id:$
 * $HeadURL:$
 * %%
 * Copyright (C) 2011 - 2017 Department of Computer Science, University of Oxford
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
package org.semanticweb.elk.reasoner.completeness;

import org.slf4j.Logger;

/**
 * Monitors incompleteness.
 * <p>
 * It is not a "completeness" monitor, so that it can be split to a number of
 * partial monitors. Partial monitors usually detect incompleteness, so if they
 * implement this interface, they fulfill the contract. If the contract was
 * "return {@code true} when the reasoning is complete" and a partial monitor
 * detects some particular cause of incompleteness, if it reported that
 * reasoning is not incomplete, it wouldn't mean that it is complete, because
 * there may be other cause monitored by another partial monitor.
 * 
 * @author Peter Skocovsky
 */
public interface IncompletenessMonitor {

	/**
	 * @return {@code true} when the reasoning may be incomplete; {@code false}
	 *         when the reasoning is definitely complete.
	 */
	boolean isIncomplete();

	/**
	 * Logs messages about incompleteness reasons that appeared after the last
	 * time this method was called to the provided logger.
	 * 
	 * @param logger
	 * @return Whether some incompleteness reasons appeared after the last time
	 *         this method was called.
	 */
	boolean logNewIncompletenessReasons(Logger logger);

}
