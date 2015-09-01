/**
 * 
 */
package org.semanticweb.elk.reasoner.saturation.inferences;

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

import org.semanticweb.elk.owl.interfaces.ElkAxiom;
import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedClassExpression;
import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedDisjointClassesAxiom;
import org.semanticweb.elk.reasoner.saturation.IndexedContextRoot;
import org.semanticweb.elk.reasoner.saturation.conclusions.implementation.ComposedSubsumerImpl;
import org.semanticweb.elk.reasoner.saturation.conclusions.interfaces.ComposedSubsumer;
import org.semanticweb.elk.reasoner.saturation.conclusions.interfaces.DisjointSubsumer;
import org.semanticweb.elk.reasoner.saturation.inferences.visitors.DisjointSubsumerInferenceVisitor;

/**
 * Represents an inference of the {@link DisjointSubsumer} from a regular
 * subsumer.
 * 
 * @author Pavel Klinov
 *
 *         pavel.klinov@uni-ulm.de
 */
public class DisjointSubsumerFromSubsumer extends
		AbstractDisjointSubsumerInference {

	public DisjointSubsumerFromSubsumer(IndexedContextRoot inferenceRoot,
			IndexedClassExpression member, IndexedDisjointClassesAxiom axiom,
			ElkAxiom reason) {
		super(inferenceRoot, member, axiom, reason);
	}

	@Override
	public IndexedContextRoot getInferenceRoot() {
		return getConclusionRoot();
	}

	public ComposedSubsumer getPremise() {
		return new ComposedSubsumerImpl<IndexedClassExpression>(
				getInferenceRoot(), getMember());
	}

	@Override
	public <I, O> O accept(DisjointSubsumerInferenceVisitor<I, O> visitor,
			I input) {
		return visitor.visit(this, input);
	}

}
