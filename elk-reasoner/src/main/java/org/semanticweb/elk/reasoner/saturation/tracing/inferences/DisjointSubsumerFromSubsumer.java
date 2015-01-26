/**
 * 
 */
package org.semanticweb.elk.reasoner.saturation.tracing.inferences;
/*
 * #%L
 * ELK Reasoner
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

import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedClassExpression;
import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedDisjointClassesAxiom;
import org.semanticweb.elk.reasoner.saturation.conclusions.implementation.DecomposedSubsumerImpl;
import org.semanticweb.elk.reasoner.saturation.conclusions.implementation.DisjointSubsumerImpl;
import org.semanticweb.elk.reasoner.saturation.conclusions.interfaces.DisjointSubsumer;
import org.semanticweb.elk.reasoner.saturation.conclusions.interfaces.Subsumer;
import org.semanticweb.elk.reasoner.saturation.tracing.inferences.visitors.InferenceVisitor;

/**
 * Represents an inference of the {@link DisjointSubsumer} from a regular subsumer.
 * 
 * @author Pavel Klinov
 *
 * pavel.klinov@uni-ulm.de
 */
public class DisjointSubsumerFromSubsumer extends DisjointSubsumerImpl implements Inference {

	public DisjointSubsumerFromSubsumer(IndexedDisjointClassesAxiom axiom,
			IndexedClassExpression member) {
		super(axiom, member);
	}
	
	public Subsumer<?> getPremise() {
		return new DecomposedSubsumerImpl<IndexedClassExpression>(getMember());
	}

	@Override
	public <I, O> O acceptTraced(InferenceVisitor<I, O> visitor, I parameter) {
		return visitor.visit(this, parameter);
	}

	@Override
	public IndexedClassExpression getInferenceContextRoot(
			IndexedClassExpression rootWhereStored) {
		return rootWhereStored;
	}

}
