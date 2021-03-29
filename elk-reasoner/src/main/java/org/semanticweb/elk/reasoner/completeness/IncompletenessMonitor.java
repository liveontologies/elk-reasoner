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
 * Keeps of properties that can cause incompleteness of a reasoning task, for
 * example, occurrence of some unsupported constructors in the ontology, or
 * violation of some global restrictions.
 * 
 * @author Peter Skocovsky
 * @author Yevgeny Kazakov
 */
public interface IncompletenessMonitor {

	/**
	 * @return {@code true} if potential problem that can result in
	 *         incompleteness has been detected by this
	 *         {@link IncompletenessMonitor}
	 */
	boolean isIncompletenessDetected();

	/**
	 * Produces log status messages explaining possible incompleteness of the
	 * results. The messages are incremental, that is, only changes compared to
	 * the previous call of {@link #logStatus(Logger)} are printed. In
	 * particular, if this method is called two times immediately after another,
	 * then the second call will not produce any messages. This has been done to
	 * minimize log pollution.
	 * 
	 * @param logger
	 *                   the logger using which the messages are printed
	 */
	void logStatus(Logger logger);

	/**
	 * @return {@code true} if calling {@link #logStatus(Logger)} would produce
	 *         at least one log message and {@code false} otherwise.
	 * @param logger
	 *            the logger for which change of status is checked
	 */
	boolean isStatusChanged(Logger logger);

}
