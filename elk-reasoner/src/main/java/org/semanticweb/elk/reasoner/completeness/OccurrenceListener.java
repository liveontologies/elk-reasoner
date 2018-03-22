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

/**
 * Notified when the number of occurrences for a {@link Feature} has changed.
 * 
 * @author Peter Skocovsky
 * @author Yevgeny Kazakov
 */
public interface OccurrenceListener {

	/**
	 * Is triggered when the number of occurrences of a given {@link Feature} is
	 * changed by the given increment
	 * 
	 * @param feature
	 * @param increment
	 */
	public void occurrenceChanged(Feature feature, int increment);

}
