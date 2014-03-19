package org.semanticweb.elk.reasoner.saturation.conclusions.implementation;
/*
 * #%L
 * ALC Reasoner
 * $Id:$
 * $HeadURL:$
 * %%
 * Copyright (C) 2011 - 2014 Department of Computer Science, University of Oxford
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

import org.semanticweb.elk.alc.indexing.hierarchy.IndexedClassExpression;
import org.semanticweb.elk.alc.indexing.hierarchy.IndexedObjectProperty;
import org.semanticweb.elk.alc.saturation.Root;
import org.semanticweb.elk.reasoner.saturation.conclusions.interfaces.PropagatedComposedSubsumer;
import org.semanticweb.elk.reasoner.saturation.conclusions.visitors.ExternalDeterministicConclusionVisitor;

public class PropagatedComposedSubsumerImpl extends
		AbstractPropagatedConclusion implements PropagatedComposedSubsumer {

	private final IndexedClassExpression expression_;

	public PropagatedComposedSubsumerImpl(IndexedObjectProperty relation,
			Root sourceRoot, IndexedClassExpression subsumer) {
		super(relation, sourceRoot);
		this.expression_ = subsumer;
	}

	@Override
	public <I, O> O accept(
			ExternalDeterministicConclusionVisitor<I, O> visitor, I input) {
		return visitor.visit(this, input);
	}

	@Override
	public IndexedClassExpression getExpression() {
		return expression_;
	}

	@Override
	public String toString() {
		return PropagatedComposedSubsumer.NAME + "(" + getRelation() + ": "
				+ getSourceRoot() + ": " + getExpression() + ")";
	}

}
