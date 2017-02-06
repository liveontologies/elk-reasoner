/**
 * 
 */
package org.semanticweb.elk.reasoner.tracing;

import org.semanticweb.elk.reasoner.indexing.classes.IndexedAxiomInferenceConclusionVisitor;

/*
 * #%L
 * ELK Reasoner
 * $Id:$
 * $HeadURL:$
 * %%
 * Copyright (C) 2011 - 2016 Department of Computer Science, University of Oxford
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

import org.semanticweb.elk.reasoner.indexing.model.ElkClassAssertionAxiomConversion;
import org.semanticweb.elk.reasoner.indexing.model.ElkDeclarationAxiomConversion;
import org.semanticweb.elk.reasoner.indexing.model.ElkDifferentIndividualsAxiomBinaryConversion;
import org.semanticweb.elk.reasoner.indexing.model.ElkDifferentIndividualsAxiomNaryConversion;
import org.semanticweb.elk.reasoner.indexing.model.ElkDisjointClassesAxiomBinaryConversion;
import org.semanticweb.elk.reasoner.indexing.model.ElkDisjointClassesAxiomNaryConversion;
import org.semanticweb.elk.reasoner.indexing.model.ElkDisjointUnionAxiomBinaryConversion;
import org.semanticweb.elk.reasoner.indexing.model.ElkDisjointUnionAxiomEquivalenceConversion;
import org.semanticweb.elk.reasoner.indexing.model.ElkDisjointUnionAxiomNaryConversion;
import org.semanticweb.elk.reasoner.indexing.model.ElkDisjointUnionAxiomOwlNothingConversion;
import org.semanticweb.elk.reasoner.indexing.model.ElkDisjointUnionAxiomSubClassConversion;
import org.semanticweb.elk.reasoner.indexing.model.ElkEquivalentClassesAxiomEquivalenceConversion;
import org.semanticweb.elk.reasoner.indexing.model.ElkEquivalentClassesAxiomSubClassConversion;
import org.semanticweb.elk.reasoner.indexing.model.ElkEquivalentObjectPropertiesAxiomConversion;
import org.semanticweb.elk.reasoner.indexing.model.ElkObjectPropertyAssertionAxiomConversion;
import org.semanticweb.elk.reasoner.indexing.model.ElkObjectPropertyDomainAxiomConversion;
import org.semanticweb.elk.reasoner.indexing.model.ElkObjectPropertyRangeAxiomConversion;
import org.semanticweb.elk.reasoner.indexing.model.ElkReflexiveObjectPropertyAxiomConversion;
import org.semanticweb.elk.reasoner.indexing.model.ElkSameIndividualAxiomConversion;
import org.semanticweb.elk.reasoner.indexing.model.ElkSubClassOfAxiomConversion;
import org.semanticweb.elk.reasoner.indexing.model.ElkSubObjectPropertyOfAxiomConversion;
import org.semanticweb.elk.reasoner.indexing.model.ElkTransitiveObjectPropertyAxiomConversion;
import org.semanticweb.elk.reasoner.indexing.model.IndexedAxiomInference;
import org.semanticweb.elk.reasoner.saturation.inferences.ClassInferenceConclusionVisitor;
import org.semanticweb.elk.reasoner.saturation.properties.inferences.ObjectPropertyInference;
import org.semanticweb.elk.reasoner.saturation.properties.inferences.ObjectPropertyInferenceConclusionVisitor;
import org.semanticweb.elk.reasoner.saturation.properties.inferences.PropertyRangeInherited;
import org.semanticweb.elk.reasoner.saturation.properties.inferences.SubPropertyChainExpandedSubObjectPropertyOf;
import org.semanticweb.elk.reasoner.saturation.properties.inferences.SubPropertyChainTautology;

/**
 * Creates all {@link Conclusion}s for the visited {@link TracingInference}s
 * using the provided {@link Conclusion.Factory} and visits them using the
 * provided {@link Conclusion.Visitor}.
 * 
 * @author Yevgeny Kazakov
 *
 * @param <O>
 *            the type of the output
 */
public class TracingInferenceConclusionVisitor<O>
		extends ClassInferenceConclusionVisitor<O>
		implements TracingInference.Visitor<O> {

	private final IndexedAxiomInference.Visitor<O> indexedAxiomDelegate_;

	private final ObjectPropertyInference.Visitor<O> objectPropertyDelegate_;

	public TracingInferenceConclusionVisitor(
			Conclusion.Factory conclusionFactory,
			Conclusion.Visitor<O> conclusionVisitor) {
		super(conclusionFactory, conclusionVisitor);
		this.objectPropertyDelegate_ = new ObjectPropertyInferenceConclusionVisitor<O>(
				conclusionFactory, conclusionVisitor);
		this.indexedAxiomDelegate_ = new IndexedAxiomInferenceConclusionVisitor<O>(
				conclusionFactory, conclusionVisitor);
	}

	public TracingInferenceConclusionVisitor(
			Conclusion.Visitor<O> conclusionVisitor) {
		this(new ConclusionBaseFactory(), conclusionVisitor);
	}

	@Override
	public O visit(ElkClassAssertionAxiomConversion inference) {
		return indexedAxiomDelegate_.visit(inference);
	}

	@Override
	public O visit(ElkDeclarationAxiomConversion inference) {
		return indexedAxiomDelegate_.visit(inference);
	}

	@Override
	public O visit(ElkDifferentIndividualsAxiomBinaryConversion inference) {
		return indexedAxiomDelegate_.visit(inference);
	}

	@Override
	public O visit(ElkDifferentIndividualsAxiomNaryConversion inference) {
		return indexedAxiomDelegate_.visit(inference);
	}

	@Override
	public O visit(ElkDisjointClassesAxiomBinaryConversion inference) {
		return indexedAxiomDelegate_.visit(inference);
	}

	@Override
	public O visit(ElkDisjointClassesAxiomNaryConversion inference) {
		return indexedAxiomDelegate_.visit(inference);
	}

	@Override
	public O visit(ElkDisjointUnionAxiomBinaryConversion inference) {
		return indexedAxiomDelegate_.visit(inference);
	}

	@Override
	public O visit(ElkDisjointUnionAxiomEquivalenceConversion inference) {
		return indexedAxiomDelegate_.visit(inference);
	}

	@Override
	public O visit(ElkDisjointUnionAxiomNaryConversion inference) {
		return indexedAxiomDelegate_.visit(inference);
	}

	@Override
	public O visit(ElkDisjointUnionAxiomOwlNothingConversion inference) {
		return indexedAxiomDelegate_.visit(inference);
	}

	@Override
	public O visit(ElkDisjointUnionAxiomSubClassConversion inference) {
		return indexedAxiomDelegate_.visit(inference);
	}

	@Override
	public O visit(ElkEquivalentClassesAxiomEquivalenceConversion inference) {
		return indexedAxiomDelegate_.visit(inference);
	}

	@Override
	public O visit(ElkEquivalentClassesAxiomSubClassConversion inference) {
		return indexedAxiomDelegate_.visit(inference);
	}

	@Override
	public O visit(ElkEquivalentObjectPropertiesAxiomConversion inference) {
		return indexedAxiomDelegate_.visit(inference);
	}

	@Override
	public O visit(ElkObjectPropertyAssertionAxiomConversion inference) {
		return indexedAxiomDelegate_.visit(inference);
	}

	@Override
	public O visit(ElkObjectPropertyDomainAxiomConversion inference) {
		return indexedAxiomDelegate_.visit(inference);
	}

	@Override
	public O visit(ElkObjectPropertyRangeAxiomConversion inference) {
		return indexedAxiomDelegate_.visit(inference);
	}

	@Override
	public O visit(ElkReflexiveObjectPropertyAxiomConversion inference) {
		return indexedAxiomDelegate_.visit(inference);
	}

	@Override
	public O visit(ElkSameIndividualAxiomConversion inference) {
		return indexedAxiomDelegate_.visit(inference);
	}

	@Override
	public O visit(ElkSubClassOfAxiomConversion inference) {
		return indexedAxiomDelegate_.visit(inference);
	}

	@Override
	public O visit(ElkSubObjectPropertyOfAxiomConversion inference) {
		return indexedAxiomDelegate_.visit(inference);
	}

	@Override
	public O visit(ElkTransitiveObjectPropertyAxiomConversion inference) {
		return indexedAxiomDelegate_.visit(inference);
	}

	@Override
	public O visit(PropertyRangeInherited inference) {
		return objectPropertyDelegate_.visit(inference);
	}

	@Override
	public O visit(SubPropertyChainExpandedSubObjectPropertyOf inference) {
		return objectPropertyDelegate_.visit(inference);
	}

	@Override
	public O visit(SubPropertyChainTautology inference) {
		return objectPropertyDelegate_.visit(inference);
	}

}
