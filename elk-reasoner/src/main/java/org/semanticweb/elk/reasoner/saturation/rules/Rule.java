package org.semanticweb.elk.reasoner.saturation.rules;

import org.semanticweb.elk.reasoner.saturation.SaturationState;

/*
 * #%L
 * ELK Reasoner
 * $Id:$
 * $HeadURL:$
 * %%
 * Copyright (C) 2011 - 2012 Department of Computer Science, University of Oxford
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

/**
 * A rule that can be applied to elements of a particular type within a
 * {@link SaturationState.AbstractWriter}.
 * 
 * @author "Yevgeny Kazakov"
 * 
 * @param <E>
 *            the type of elements to which the rule can be applied
 */
public interface Rule<E> {

	/**
	 * Applying the rule to an element within a {@link SaturationState.Writer}
	 * 
	 * @param engine
	 *            a {@link SaturationState.Writer} which could be changed as a
	 *            result of this rule's application
	 * @param element
	 *            the element to which the rule is applied
	 */
	public void apply(SaturationState.Writer engine, E element);

	/**
	 * @return the name of this rule
	 */
	public String getName();

}
