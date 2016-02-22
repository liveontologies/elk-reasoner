package org.semanticweb.elk.reasoner.tracing;

/*
 * #%L
 * ELK Reasoner
 * $Id:$
 * $HeadURL:$
 * %%
 * Copyright (C) 2011 - 2015 Department of Computer Science, University of Oxford
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
import org.semanticweb.elk.reasoner.indexing.classes.IndexedAxiomBaseFactory;
import org.semanticweb.elk.reasoner.indexing.model.IndexedAxiom;
import org.semanticweb.elk.reasoner.indexing.model.IndexedClass;
import org.semanticweb.elk.reasoner.indexing.model.IndexedClassExpression;
import org.semanticweb.elk.reasoner.indexing.model.IndexedClassExpressionList;
import org.semanticweb.elk.reasoner.indexing.model.IndexedContextRoot;
import org.semanticweb.elk.reasoner.indexing.model.IndexedDeclarationAxiom;
import org.semanticweb.elk.reasoner.indexing.model.IndexedDefinitionAxiom;
import org.semanticweb.elk.reasoner.indexing.model.IndexedDisjointClassesAxiom;
import org.semanticweb.elk.reasoner.indexing.model.IndexedEntity;
import org.semanticweb.elk.reasoner.indexing.model.IndexedObjectProperty;
import org.semanticweb.elk.reasoner.indexing.model.IndexedObjectPropertyRangeAxiom;
import org.semanticweb.elk.reasoner.indexing.model.IndexedObjectSomeValuesFrom;
import org.semanticweb.elk.reasoner.indexing.model.IndexedPropertyChain;
import org.semanticweb.elk.reasoner.indexing.model.IndexedSubClassOfAxiom;
import org.semanticweb.elk.reasoner.indexing.model.IndexedSubObjectPropertyOfAxiom;
import org.semanticweb.elk.reasoner.saturation.conclusions.classes.SaturationConclusionBaseFactory;
import org.semanticweb.elk.reasoner.saturation.conclusions.model.BackwardLink;
import org.semanticweb.elk.reasoner.saturation.conclusions.model.ContextInitialization;
import org.semanticweb.elk.reasoner.saturation.conclusions.model.ClassInconsistency;
import org.semanticweb.elk.reasoner.saturation.conclusions.model.DisjointSubsumer;
import org.semanticweb.elk.reasoner.saturation.conclusions.model.ForwardLink;
import org.semanticweb.elk.reasoner.saturation.conclusions.model.Propagation;
import org.semanticweb.elk.reasoner.saturation.conclusions.model.PropertyRange;
import org.semanticweb.elk.reasoner.saturation.conclusions.model.SaturationConclusion;
import org.semanticweb.elk.reasoner.saturation.conclusions.model.SubClassInclusionComposed;
import org.semanticweb.elk.reasoner.saturation.conclusions.model.SubClassInclusionDecomposed;
import org.semanticweb.elk.reasoner.saturation.conclusions.model.SubContextInitialization;
import org.semanticweb.elk.reasoner.saturation.conclusions.model.SubPropertyChain;

public class ConclusionBaseFactory implements Conclusion.Factory {

	private static final Conclusion.Factory INSTANCE_ = new ConclusionBaseFactory();

	private final IndexedAxiom.Factory indexedAxiomFactory_ = new IndexedAxiomBaseFactory();

	private final SaturationConclusion.Factory saturationConclusionFactory_ = new SaturationConclusionBaseFactory();

	public static Conclusion.Factory getInstance() {
		return INSTANCE_;
	}

	@SuppressWarnings("static-method")
	protected <C extends Conclusion> C filter(C newConclusion) {
		// could be overridden in sub-classes
		return newConclusion;
	}

	@Override
	public BackwardLink getBackwardLink(IndexedContextRoot destination,
			IndexedObjectProperty relation, IndexedContextRoot source) {
		return filter(saturationConclusionFactory_.getBackwardLink(destination,
				relation, source));
	}

	@Override
	public ContextInitialization getContextInitialization(
			IndexedContextRoot root) {
		return filter(
				saturationConclusionFactory_.getContextInitialization(root));
	}

	@Override
	public ClassInconsistency getContradiction(IndexedContextRoot destination) {
		return filter(saturationConclusionFactory_.getContradiction(destination));
	}

	@Override
	public DisjointSubsumer getDisjointSubsumer(IndexedContextRoot root,
			IndexedClassExpressionList disjointExpressions, int position,
			ElkAxiom reason) {
		return filter(saturationConclusionFactory_.getDisjointSubsumer(root,
				disjointExpressions, position, reason));
	}

	@Override
	public ForwardLink getForwardLink(IndexedContextRoot root,
			IndexedPropertyChain forwardChain, IndexedContextRoot target) {
		return filter(saturationConclusionFactory_.getForwardLink(root,
				forwardChain, target));
	}

	@Override
	public IndexedDeclarationAxiom getIndexedDeclarationAxiom(
			ElkAxiom originalAxiom, IndexedEntity entity) {
		return filter(indexedAxiomFactory_
				.getIndexedDeclarationAxiom(originalAxiom, entity));
	}

	@Override
	public IndexedDefinitionAxiom getIndexedDefinitionAxiom(
			ElkAxiom originalAxiom, IndexedClass definedClass,
			IndexedClassExpression definition) {
		return filter(indexedAxiomFactory_.getIndexedDefinitionAxiom(
				originalAxiom, definedClass, definition));
	}

	@Override
	public IndexedDisjointClassesAxiom getIndexedDisjointClassesAxiom(
			ElkAxiom originalAxiom, IndexedClassExpressionList members) {
		return filter(indexedAxiomFactory_
				.getIndexedDisjointClassesAxiom(originalAxiom, members));
	}

	@Override
	public IndexedObjectPropertyRangeAxiom getIndexedObjectPropertyRangeAxiom(
			ElkAxiom originalAxiom, IndexedObjectProperty property,
			IndexedClassExpression range) {
		return filter(indexedAxiomFactory_.getIndexedObjectPropertyRangeAxiom(
				originalAxiom, property, range));
	}

	@Override
	public IndexedSubClassOfAxiom getIndexedSubClassOfAxiom(
			ElkAxiom originalAxiom, IndexedClassExpression subClass,
			IndexedClassExpression superClass) {
		return filter(indexedAxiomFactory_.getIndexedSubClassOfAxiom(
				originalAxiom, subClass, superClass));
	}

	@Override
	public IndexedSubObjectPropertyOfAxiom getIndexedSubObjectPropertyOfAxiom(
			ElkAxiom originalAxiom, IndexedPropertyChain subPropertyChain,
			IndexedObjectProperty superProperty) {
		return filter(indexedAxiomFactory_.getIndexedSubObjectPropertyOfAxiom(
				originalAxiom, subPropertyChain, superProperty));
	}

	@Override
	public Propagation getPropagation(IndexedContextRoot destination,
			IndexedObjectProperty relation, IndexedObjectSomeValuesFrom carry) {
		return filter(saturationConclusionFactory_.getPropagation(destination,
				relation, carry));
	}

	@Override
	public PropertyRange getPropertyRange(IndexedObjectProperty property,
			IndexedClassExpression range) {
		return filter(
				saturationConclusionFactory_.getPropertyRange(property, range));
	}

	@Override
	public SubClassInclusionComposed getSubClassInclusionComposed(
			IndexedContextRoot destination, IndexedClassExpression subsumer) {
		return filter(saturationConclusionFactory_
				.getSubClassInclusionComposed(destination, subsumer));
	}

	@Override
	public SubClassInclusionDecomposed getSubClassInclusionDecomposed(
			IndexedContextRoot destination, IndexedClassExpression subsumer) {
		return filter(saturationConclusionFactory_
				.getSubClassInclusionDecomposed(destination, subsumer));
	}

	@Override
	public SubContextInitialization getSubContextInitialization(
			IndexedContextRoot root, IndexedObjectProperty subRoot) {
		return filter(saturationConclusionFactory_
				.getSubContextInitialization(root, subRoot));
	}

	@Override
	public SubPropertyChain getSubPropertyChain(IndexedPropertyChain subChain,
			IndexedPropertyChain superChain) {
		return filter(saturationConclusionFactory_.getSubPropertyChain(subChain,
				superChain));
	}

}
