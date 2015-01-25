package org.semanticweb.elk.proofs.transformations.lemmas;
/*
 * #%L
 * ELK Proofs Package
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

import java.util.ArrayList;
import java.util.List;

import org.semanticweb.elk.owl.implementation.ElkObjectFactoryImpl;
import org.semanticweb.elk.owl.interfaces.ElkObjectFactory;
import org.semanticweb.elk.owl.interfaces.ElkReflexiveObjectPropertyAxiom;
import org.semanticweb.elk.owl.interfaces.ElkSubObjectPropertyOfAxiom;
import org.semanticweb.elk.proofs.expressions.derived.DerivedAxiomExpression;
import org.semanticweb.elk.proofs.expressions.derived.DerivedExpressionFactory;
import org.semanticweb.elk.proofs.inferences.Inference;
import org.semanticweb.elk.proofs.inferences.mapping.Deindexer;
import org.semanticweb.elk.proofs.inferences.properties.SubPropertyChainAxiom;
import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedBinaryPropertyChain;
import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedObjectProperty;
import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedPropertyChain;
import org.semanticweb.elk.reasoner.indexing.visitors.IndexedPropertyChainVisitor;
import org.semanticweb.elk.reasoner.saturation.tracing.inferences.properties.LeftReflexiveSubPropertyChainInference;
import org.semanticweb.elk.reasoner.saturation.tracing.inferences.properties.ObjectPropertyInference;
import org.semanticweb.elk.reasoner.saturation.tracing.inferences.properties.RightReflexiveSubPropertyChainInference;
import org.semanticweb.elk.reasoner.saturation.tracing.inferences.properties.ToldSubPropertyInference;
import org.semanticweb.elk.reasoner.saturation.tracing.inferences.visitors.AbstractObjectPropertyInferenceVisitor;

/**
 * 
 * TODO
 * 
 * @author	Pavel Klinov
 * 			pavel.klinov@uni-ulm.de
 *
 */
class LemmaFreePropertyInferenceFactory extends AbstractObjectPropertyInferenceVisitor<DerivedAxiomExpression<ElkSubObjectPropertyOfAxiom>, Inference> {
	
	private final DerivedAxiomExpression<ElkSubObjectPropertyOfAxiom> expr_;
	
	private final ElkObjectFactory elkFactory_ = new ElkObjectFactoryImpl();
	
	private final DerivedExpressionFactory exprFactory_;
	
	LemmaFreePropertyInferenceFactory(DerivedAxiomExpression<ElkSubObjectPropertyOfAxiom> conclusion, DerivedExpressionFactory exprFactory) {
		expr_ = conclusion;
		exprFactory_ = exprFactory;
	}

	@Override
	protected Inference defaultTracedVisit(ObjectPropertyInference inference, DerivedAxiomExpression<ElkSubObjectPropertyOfAxiom> input) {
		return null;
	}

	@Override
	public Inference visit(LeftReflexiveSubPropertyChainInference inference, DerivedAxiomExpression<ElkSubObjectPropertyOfAxiom> premise) {
		return new ReflexivityElimination(expr_, premise, createReflexivityPremise(inference.getReflexivePremise().getPropertyChain()));
	}

	@Override
	public Inference visit(final RightReflexiveSubPropertyChainInference inference, final DerivedAxiomExpression<ElkSubObjectPropertyOfAxiom> premise) {
		return inference.getReflexivePremise().getPropertyChain().accept(new IndexedPropertyChainVisitor<Inference>() {

			@Override
			public Inference visit(IndexedObjectProperty iop) {
				return new ReflexivityElimination(expr_, premise, createReflexivityPremise(iop));
			}

			@Override
			public Inference visit(IndexedBinaryPropertyChain ipc) {
				return new ReflexivityElimination(expr_, premise, createReflexivityPremises(ipc));
			}
		});
	}

	@Override
	public Inference visit(ToldSubPropertyInference inference, DerivedAxiomExpression<ElkSubObjectPropertyOfAxiom> premise) {
		if (inference.getSuperPropertyChain() instanceof IndexedObjectProperty) {
			return createSubChainInference(inference.getSubPropertyChain(), (IndexedObjectProperty) inference.getSuperPropertyChain(), premise);	
		}

		return null;
	}

	private DerivedAxiomExpression<ElkReflexiveObjectPropertyAxiom> createReflexivityPremise(IndexedObjectProperty ipc) {
		return exprFactory_.create(elkFactory_.getReflexiveObjectPropertyAxiom(((IndexedObjectProperty) ipc).getElkObjectProperty()));
	}

	private Iterable<DerivedAxiomExpression<ElkReflexiveObjectPropertyAxiom>> createReflexivityPremises(IndexedBinaryPropertyChain ipc) {
		List<DerivedAxiomExpression<ElkReflexiveObjectPropertyAxiom>> premises = new ArrayList<DerivedAxiomExpression<ElkReflexiveObjectPropertyAxiom>>();
		IndexedPropertyChain top = ipc;
		
		for (;;) {
			if (top instanceof IndexedObjectProperty) {
				premises.add(createReflexivityPremise(((IndexedObjectProperty) top)));
				break;
			}
			else {
				premises.add(createReflexivityPremise(((IndexedBinaryPropertyChain) top).getLeftProperty()));
				top = ((IndexedBinaryPropertyChain) top).getRightProperty();
			}
		}
		
		return premises;
	}
	
	private Inference createSubChainInference(	IndexedPropertyChain subChain, 
												IndexedObjectProperty superChain, 
												DerivedAxiomExpression<ElkSubObjectPropertyOfAxiom> firstPremise) {
		return new SubPropertyChainAxiom(expr_, firstPremise, 
				exprFactory_.create(elkFactory_.getSubObjectPropertyOfAxiom(Deindexer.deindex(subChain), 
						((IndexedObjectProperty) superChain).getElkObjectProperty())));
	}

}