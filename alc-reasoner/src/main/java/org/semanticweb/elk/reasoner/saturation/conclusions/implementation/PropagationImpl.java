/**
 * 
 */
package org.semanticweb.elk.reasoner.saturation.conclusions.implementation;

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
import org.semanticweb.elk.reasoner.saturation.conclusions.interfaces.Propagation;
import org.semanticweb.elk.reasoner.saturation.conclusions.visitors.LocalDeterministicConclusionVisitor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * An implementation of {@link Propagation}
 * 
 * @author Pavel Klinov
 * 
 *         pavel.klinov@uni-ulm.de
 * 
 * @author "Yevgeny Kazakov"
 */
public class PropagationImpl extends AbstractLocalDeterministicConclusion
		implements Propagation {

	// logger for this class
	static final Logger LOGGER_ = LoggerFactory
			.getLogger(PropagationImpl.class);

	private final IndexedObjectProperty relation_;

	private final IndexedObjectSomeValuesFrom carry_;

	public PropagationImpl(IndexedObjectProperty relation,
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
	public <I, O> O accept(LocalDeterministicConclusionVisitor<I, O> visitor,
			I input) {
		return visitor.visit(this, input);
	}

	@Override
	public String toString() {
		return Propagation.NAME + "(" + relation_ + " " + carry_ + ")";
	}

}
