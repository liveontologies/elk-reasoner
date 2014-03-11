/*
 * #%L
 * ELK Reasoner
 * 
 * $Id$
 * $HeadURL$
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
package org.semanticweb.elk.reasoner.saturation.conclusions.implementation;

import org.semanticweb.elk.alc.indexing.hierarchy.IndexedClassExpression;
import org.semanticweb.elk.reasoner.saturation.conclusions.interfaces.Subsumer;

/**
 * An implementation of {@link Subsumer}
 * 
 * @author Frantisek Simancik
 * @author "Yevgeny Kazakov"
 * 
 */
abstract class AbstractSubsumer extends AbstractConclusion implements Subsumer {

	/**
	 * the implied {@code IndexedClassExpression} represented by this
	 * {@link Subsumer}
	 */
	private final IndexedClassExpression expression_;

	public AbstractSubsumer(IndexedClassExpression expression) {
		if (expression == null)
			throw new NullPointerException("Subsumer cannot be null!");
		this.expression_ = expression;
	}

	@Override
	public IndexedClassExpression getExpression() {
		return expression_;
	}

	@Override
	public String toString() {
		return Subsumer.NAME + "(" + expression_.toString() + ")";
	}
}
