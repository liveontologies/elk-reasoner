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
import org.semanticweb.elk.proofs.expressions.AxiomExpression;
import org.semanticweb.elk.proofs.expressions.ExpressionFactory;
import org.semanticweb.elk.proofs.inferences.Inference;
import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedComplexPropertyChain;
import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedObjectProperty;
import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedPropertyChain;
import org.semanticweb.elk.reasoner.indexing.visitors.IndexedPropertyChainVisitor;
import org.semanticweb.elk.reasoner.saturation.inferences.properties.AbstractObjectPropertyInferenceVisitor;
import org.semanticweb.elk.reasoner.saturation.inferences.properties.LeftReflexiveSubPropertyChainInference;
import org.semanticweb.elk.reasoner.saturation.inferences.properties.ObjectPropertyInference;
import org.semanticweb.elk.reasoner.saturation.inferences.properties.RightReflexiveSubPropertyChainInference;
import org.semanticweb.elk.reasoner.saturation.inferences.properties.ToldSubProperty;
import org.semanticweb.elk.reasoner.saturation.tracing.SideConditionLookup;

/**
 * 
 * TODO
 * 
 * @author	Pavel Klinov
 * 			pavel.klinov@uni-ulm.de
 *
 */
abstract class PropertyInferenceVisitor extends AbstractObjectPropertyInferenceVisitor<AxiomExpression<ElkSubObjectPropertyOfAxiom>, Inference> {
	
	private final ElkObjectFactory elkFactory_ = new ElkObjectFactoryImpl();
	
	private final ExpressionFactory exprFactory_;
	
	PropertyInferenceVisitor(ExpressionFactory exprFactory) {
		exprFactory_ = exprFactory;
	}

	@Override
	protected Inference defaultTracedVisit(ObjectPropertyInference inference, AxiomExpression<ElkSubObjectPropertyOfAxiom> input) {
		return null;
	}

	@Override
	public Inference visit(LeftReflexiveSubPropertyChainInference inference, AxiomExpression<ElkSubObjectPropertyOfAxiom> premise) {
		return createReflexivityEliminationInference(createReflexivityPremise(inference.getReflexivePremise().getRelation()));
	}

	@Override
	public Inference visit(final RightReflexiveSubPropertyChainInference inference, final AxiomExpression<ElkSubObjectPropertyOfAxiom> premise) {
		return inference.getReflexivePremise().getRelation().accept(new IndexedPropertyChainVisitor<Inference>() {

			@Override
			public Inference visit(IndexedObjectProperty iop) {
				return createReflexivityEliminationInference(createReflexivityPremise(iop));
			}

			@Override
			public Inference visit(IndexedComplexPropertyChain ipc) {
				return createReflexivityEliminationInference(createReflexivityPremises(ipc));
			}
		});
	}

	@Override
	public Inference visit(ToldSubProperty inference, AxiomExpression<ElkSubObjectPropertyOfAxiom> premise) {
		// using only told hierarchy here
		if (inference.getSuperChain() instanceof IndexedObjectProperty && 
				inference.getSubChain().getToldSuperProperties().contains(inference.getSuperChain())) {
			ElkSubObjectPropertyOfAxiom axiom = (ElkSubObjectPropertyOfAxiom) new SideConditionLookup().lookup(inference);
			
			return createSubChainInference(exprFactory_.createAsserted(axiom));
		}

		return null;
	}

	private AxiomExpression<ElkReflexiveObjectPropertyAxiom> createReflexivityPremise(IndexedObjectProperty ipc) {
		return exprFactory_.create(elkFactory_.getReflexiveObjectPropertyAxiom(((IndexedObjectProperty) ipc).getElkEntity()));
	}

	private Iterable<AxiomExpression<ElkReflexiveObjectPropertyAxiom>> createReflexivityPremises(IndexedComplexPropertyChain ipc) {
		List<AxiomExpression<ElkReflexiveObjectPropertyAxiom>> premises = new ArrayList<AxiomExpression<ElkReflexiveObjectPropertyAxiom>>();
		IndexedPropertyChain top = ipc;
		
		for (;;) {
			if (top instanceof IndexedObjectProperty) {
				premises.add(createReflexivityPremise(((IndexedObjectProperty) top)));
				break;
			}
			else {
				premises.add(createReflexivityPremise(((IndexedComplexPropertyChain) top).getFirstProperty()));
				top = ((IndexedComplexPropertyChain) top).getSuffixChain();
			}
		}
		
		return premises;
	}
	
	protected abstract Inference createReflexivityEliminationInference(AxiomExpression<ElkReflexiveObjectPropertyAxiom> premise);
	
	protected abstract Inference createReflexivityEliminationInference(Iterable<AxiomExpression<ElkReflexiveObjectPropertyAxiom>> premises);
	
	protected abstract Inference createSubChainInference(AxiomExpression<ElkSubObjectPropertyOfAxiom> premise);

}