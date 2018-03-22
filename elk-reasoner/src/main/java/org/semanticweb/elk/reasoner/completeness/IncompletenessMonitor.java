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
	 * Prints log messages explaining incompleteness of the results. The
	 * messages are incremental, that is, only new explanations not reported
	 * during the previous call of {@link #explainIncompleteness(Logger)} are
	 * printed. In particular, if this method is called two times immediately
	 * after another, then the second call will not produce any messages. Also,
	 * if {@link #isIncompletenessDetected()} returns {@code false}, calling of
	 * this method does not produce any messages. This has been done to minimize
	 * log pollution.
	 * 
	 * @param logger
	 *            the logger using which the messages are printed
	 */
	void explainIncompleteness(Logger logger);

	/**
	 * @return {@code true} if some new explanations for the incompleteness have
	 *         been encountered after the last call of
	 *         {@link #explainIncompleteness(Logger)}, and {@code false}
	 *         otherwise. This method can be used to detect if
	 *         {@link #explainIncompleteness(Logger)} would print any messages
	 *         without calling the letter.
	 */
	boolean hasNewExplanation();

}
