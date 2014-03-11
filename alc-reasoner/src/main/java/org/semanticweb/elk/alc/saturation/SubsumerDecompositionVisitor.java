package org.semanticweb.elk.alc.saturation;

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

import org.semanticweb.elk.alc.indexing.hierarchy.IndexedClass;
import org.semanticweb.elk.alc.indexing.hierarchy.IndexedObjectIntersectionOf;
import org.semanticweb.elk.alc.indexing.hierarchy.IndexedObjectSomeValuesFrom;
import org.semanticweb.elk.alc.indexing.hierarchy.IndexedObjectUnionOf;
import org.semanticweb.elk.alc.indexing.visitors.IndexedClassExpressionVisitor;
import org.semanticweb.elk.owl.predefined.PredefinedElkClass;
import org.semanticweb.elk.reasoner.saturation.conclusions.implementation.BackwardLinkImpl;
import org.semanticweb.elk.reasoner.saturation.conclusions.implementation.ClashImpl;
import org.semanticweb.elk.reasoner.saturation.conclusions.implementation.DecomposedSubsumerImpl;
import org.semanticweb.elk.reasoner.saturation.conclusions.implementation.DisjunctionImpl;

/**
 * An {@link IndexedClassExpressionVisitor} applying decomposition rules using a
 * given {@link SubsumerDecompositionRuleVisitor} using given
 * {@link ContextPremises} and producing conclusions using a given
 * {@link ConclusionProducer}
 * 
 * @author "Yevgeny Kazakov"
 * 
 */
public class SubsumerDecompositionVisitor implements
		IndexedClassExpressionVisitor<Void> {

	/**
	 * the {@link Root} of the {@link Context} in which the rules are applied
	 */
	private final Root root_;

	private final ConclusionProducer producer_;

	SubsumerDecompositionVisitor(Root root, ConclusionProducer producer) {
		this.root_ = root;
		this.producer_ = producer;
	}

	@Override
	public Void visit(IndexedClass element) {
		if (element.getElkClass() == PredefinedElkClass.OWL_NOTHING)
			producer_.produce(root_, ClashImpl.getInstance());
		return null;
	}

	@Override
	public Void visit(IndexedObjectIntersectionOf element) {
		producer_.produce(root_,
				new DecomposedSubsumerImpl(element.getFirstConjunct()));
		producer_.produce(root_,
				new DecomposedSubsumerImpl(element.getSecondConjunct()));
		return null;
	}

	@Override
	public Void visit(IndexedObjectSomeValuesFrom element) {
		Root fillerRoot = new Root(element.getFiller());
		// TODO: take into account propagations
		producer_.produce(fillerRoot,
				new BackwardLinkImpl(root_, element.getRelation()));
		return null;
	}

	@Override
	public Void visit(IndexedObjectUnionOf element) {
		producer_.produce(root_, new DisjunctionImpl(element));
		return null;
	}

}
