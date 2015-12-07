/**
 * 
 */
package org.semanticweb.elk.reasoner.tracing;

/*
 * #%L
 * ELK Reasoner
 * $Id:$
 * $HeadURL:$
 * %%
 * Copyright (C) 2011 - 2013 Department of Computer Science, University of Oxford
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

import java.util.Collection;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Set;

import org.semanticweb.elk.owl.interfaces.ElkAxiom;
import org.semanticweb.elk.reasoner.indexing.classes.ResolvingModifiableIndexedObjectFactory;
import org.semanticweb.elk.reasoner.indexing.conversion.ElkAxiomConverter;
import org.semanticweb.elk.reasoner.indexing.conversion.ElkAxiomConverterImpl;
import org.semanticweb.elk.reasoner.indexing.model.IndexedAxiom;
import org.semanticweb.elk.reasoner.indexing.model.ModifiableIndexedAxiomInference;
import org.semanticweb.elk.reasoner.indexing.model.ModifiableIndexedObject;
import org.semanticweb.elk.reasoner.indexing.model.ModifiableIndexedObjectCache;
import org.semanticweb.elk.reasoner.saturation.conclusions.model.ClassConclusion;
import org.semanticweb.elk.util.collections.ArrayHashSet;

/**
 * A collections of objects for tracing contexts and keeping the relevant
 * information about the state of tracing.
 * 
 * @author Pavel Klinov
 * 
 *         pavel.klinov@uni-ulm.de
 * 
 * @author "Yevgeny Kazakov"
 * 
 * TODO: save only acyclic inferences
 */
public class TraceState extends SynchronizedModifiableInferenceSet<Inference> {

	private final Queue<ClassConclusion> toTrace_ = new LinkedList<ClassConclusion>();

	private final Set<ElkAxiom> indexedAxioms_ = new ArrayHashSet<ElkAxiom>();

	private final ModifiableIndexedObject.Factory indexedObjectFactory_;

	private final ElkAxiomConverter elkAxiomConverter_;

	public TraceState(ModifiableIndexedObjectCache cache) {
		this.indexedObjectFactory_ = new ResolvingModifiableIndexedObjectFactory(
				cache) {
			@Override
			protected <T extends ModifiableIndexedAxiomInference> T filter(
					T input) {
				produce(input);
				return input;
			}
		};
		this.elkAxiomConverter_ = new ElkAxiomConverterImpl(
				indexedObjectFactory_);
	}

	public Collection<? extends ClassConclusion> getToTrace() {
		return toTrace_;
	}

	public void addToTrace(ClassConclusion conclusion) {
		toTrace_.add(conclusion);
	}

	public void clearToTrace() {
		toTrace_.clear();
	}

	@Override
	public Iterable<? extends Inference> getInferences(Conclusion conclusion) {
		if (conclusion instanceof IndexedAxiom) {
			indexAxiom(((IndexedAxiom) conclusion).getOriginalAxiom());			
		}
		return super.getInferences(conclusion);
	}

	synchronized void indexAxiom(ElkAxiom axiom) {
		if (!indexedAxioms_.add(axiom)) {
			// already done
			return;
		}
		// else index axiom
		axiom.accept(elkAxiomConverter_);
	}

}
