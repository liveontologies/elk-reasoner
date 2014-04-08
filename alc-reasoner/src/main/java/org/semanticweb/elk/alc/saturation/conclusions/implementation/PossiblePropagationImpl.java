/**
 * 
 */
package org.semanticweb.elk.alc.saturation.conclusions.implementation;

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

import org.semanticweb.elk.alc.indexing.hierarchy.IndexedObjectProperty;
import org.semanticweb.elk.alc.indexing.hierarchy.IndexedObjectSomeValuesFrom;
import org.semanticweb.elk.alc.saturation.conclusions.interfaces.PossiblePropagation;
import org.semanticweb.elk.alc.saturation.conclusions.visitors.LocalPossibleConclusionVisitor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * An implementation of {@link PossiblePropagation}
 * 
 * @author Pavel Klinov
 * 
 *         pavel.klinov@uni-ulm.de
 * 
 * @author "Yevgeny Kazakov"
 */
public class PossiblePropagationImpl extends AbstractLocalPossibleConclusion
		implements PossiblePropagation {

	// logger for this class
	static final Logger LOGGER_ = LoggerFactory
			.getLogger(PossiblePropagationImpl.class);

	private final IndexedObjectProperty relation_;

	private final IndexedObjectSomeValuesFrom carry_;

	public PossiblePropagationImpl(IndexedObjectProperty relation,
			IndexedObjectSomeValuesFrom carry) {
		relation_ = relation;
		carry_ = carry;
	}

	@Override
	public IndexedObjectProperty getRelation() {
		return this.relation_;
	}

	@Override
	public IndexedObjectSomeValuesFrom getCarry() {
		return this.carry_;
	}

	@Override
	public <I, O> O accept(LocalPossibleConclusionVisitor<I, O> visitor,
			I input) {
		return visitor.visit(this, input);
	}

	@Override
	public String toString() {
		return PossiblePropagation.NAME + "(" + relation_ + " " + carry_ + ")";
	}

}
