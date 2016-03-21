/**
 * 
 */
package org.semanticweb.elk.reasoner.stages;
/*
 * #%L
 * ELK Proofs Package
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

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import org.semanticweb.elk.owl.exceptions.ElkException;
import org.semanticweb.elk.owl.implementation.ElkObjectFactoryImpl;
import org.semanticweb.elk.owl.interfaces.ElkAxiom;
import org.semanticweb.elk.owl.interfaces.ElkClass;
import org.semanticweb.elk.owl.interfaces.ElkClassExpression;
import org.semanticweb.elk.owl.interfaces.ElkEntity;
import org.semanticweb.elk.owl.interfaces.ElkNamedIndividual;
import org.semanticweb.elk.owl.interfaces.ElkObjectFactory;
import org.semanticweb.elk.owl.interfaces.ElkSubClassOfAxiom;
import org.semanticweb.elk.owl.predefined.PredefinedElkClass;
import org.semanticweb.elk.owl.visitors.AbstractElkEntityVisitor;
import org.semanticweb.elk.owl.visitors.ElkSubObjectPropertyExpressionVisitor;
import org.semanticweb.elk.proofs.expressions.AxiomExpression;
import org.semanticweb.elk.proofs.expressions.CachingExpressionFactory;
import org.semanticweb.elk.proofs.expressions.Expression;
import org.semanticweb.elk.proofs.expressions.ExpressionFactory;
import org.semanticweb.elk.proofs.expressions.entries.StructuralEquivalenceChecker;
import org.semanticweb.elk.proofs.expressions.lemmas.ElkLemma;
import org.semanticweb.elk.proofs.expressions.lemmas.ElkSubClassOfLemma;
import org.semanticweb.elk.proofs.inferences.AbstractInferenceVisitor;
import org.semanticweb.elk.proofs.inferences.InconsistencyInference;
import org.semanticweb.elk.proofs.inferences.Inference;
import org.semanticweb.elk.proofs.inferences.InferenceReader;
import org.semanticweb.elk.proofs.inferences.InferenceVisitor;
import org.semanticweb.elk.proofs.inferences.classes.UnsatisfiabilityInference;
import org.semanticweb.elk.proofs.inferences.mapping.ExpressionMapper;
import org.semanticweb.elk.proofs.inferences.mapping.InferenceMapper;
import org.semanticweb.elk.proofs.inferences.mapping.SatisfiabilityChecker;
import org.semanticweb.elk.proofs.inferences.mapping.TracingInput;
import org.semanticweb.elk.proofs.transformations.lemmas.BaseExpressionVisitor;
import org.semanticweb.elk.reasoner.indexing.conversion.ElkPolarityExpressionConverter;
import org.semanticweb.elk.reasoner.indexing.model.IndexedClassExpression;
import org.semanticweb.elk.reasoner.indexing.model.IndexedPropertyChain;
import org.semanticweb.elk.reasoner.tracing.RecursiveTraceUnwinder;
import org.semanticweb.elk.reasoner.tracing.TraceStore;
import org.semanticweb.elk.util.collections.Operations;


/**
 * Inference reader which works directly with the reasoner to request low-level
 * inferences for expressions and maps them to the user-level inferences showed
 * in proofs.
 * 
 * @author Pavel Klinov
 * 
 *         pavel.klinov@uni-ulm.de
 */
public class ReasonerInferenceReader implements InferenceReader {

	final AbstractReasonerState reasoner;
		
	private final ExpressionFactory expressionFactory_;
	
	private final ExpressionMapper expressionMapper_;
	
	public ReasonerInferenceReader(AbstractReasonerState r) {
		reasoner = r;
		// this expression factory will guarantee pointer equality for structurally equivalent expressions
		expressionFactory_ = new CachingExpressionFactory(this);
		expressionMapper_ = new ExpressionMapper(reasoner.getExpressionConverter(), reasoner.getSubPropertyConverter());
	}
	
	public AxiomExpression<?> initialize(ElkClassExpression sub, ElkClassExpression sup) throws ElkException {
		// trace it
		if (reasoner.isInconsistent()) {
			return initializeForInconsistency();
		}
		else {
			reasoner.explainConclusion(sub, sup);
			// create and return the first derived expression which corresponds to the initial subsumption
			AxiomExpression<ElkSubClassOfAxiom> expr = expressionFactory_.create(new ElkObjectFactoryImpl().getSubClassOfAxiom(sub, sup)); 
			
			//return filterOutOneStepCycles(expr);
			return expr;
		}
	}
	
	public AxiomExpression<?> initializeForInconsistency() throws ElkException {
		final ElkObjectFactory factory = new ElkObjectFactoryImpl();
		ElkEntity inconsistent = reasoner.explainInconsistency();
		ElkAxiom axiom = inconsistent.accept(new AbstractElkEntityVisitor<ElkAxiom>() {

			@Override
			public ElkAxiom visit(ElkClass elkClass) {
				return factory.getSubClassOfAxiom(elkClass, PredefinedElkClass.OWL_NOTHING);
			}

			@Override
			public ElkAxiom visit(ElkNamedIndividual elkNamedIndividual) {
				return factory.getClassAssertionAxiom(PredefinedElkClass.OWL_NOTHING, elkNamedIndividual);
			}
			
		});
		
		//return filterOutOneStepCycles(expressionFactory_.create(axiom));
		return expressionFactory_.create(axiom);
	}
	
	/*private <E extends ElkAxiom> DerivedAxiomExpression<E> filterOutOneStepCycles(DerivedAxiomExpression<E> root) {
		return new TransformedAxiomExpression<OneStepCyclicInferenceFilter, E>(root, new OneStepCyclicInferenceFilter());
	}*/
	
	public ExpressionFactory getExpressionFactory() {
		return expressionFactory_;
	}
	
	@Deprecated
	public ElkPolarityExpressionConverter getExpressionConverter() {
		return reasoner.getExpressionConverter();		
	}	
	
	@Deprecated
	public ElkSubObjectPropertyExpressionVisitor<? extends IndexedPropertyChain> getSubPropertyConverter() {
		return reasoner.getSubPropertyConverter();		
	}
			
	public TraceStore.Reader getTraceReader() {
		return reasoner.getTraceState().getTraceStore().getReader();
	}
	
	@Override
	public Iterable<Inference> getInferences(Expression expression) throws ElkException {
		Inference auxInf = createAuxiliaryInference(expression);
		Iterable<Inference> exprInf = createExpressionInferences(expression); 
		
		return auxInf == null ? exprInf : Operations.concat(Collections.singleton(auxInf), exprInf);
	}
	
	// in case we're explaining expressions other than the submitted one, for example, if the ontology is inconsistent or the subclass is unsatisfiable
	private Inference createAuxiliaryInference(Expression expression) throws ElkException {
		if (reasoner.isInconsistent()) {
			AxiomExpression<?> inconsistencyConclusion = initializeForInconsistency();
			// the ontology is inconsistent so everything follows
			if (!expression.equals(inconsistencyConclusion)) {
				return new InconsistencyInference(expression, inconsistencyConclusion);
			}
		}
		
		return expression.accept(new AuxInferenceCreator(new SatisfiabilityChecker() {

			@Override
			public boolean isSatisfiable(IndexedClassExpression ice) {
				return reasoner.isSatisfiable(ice);
			}
		}), null);
	}
	
	private Iterable<Inference> createExpressionInferences(Expression expression) {
		// first, transform the expression into inputs for the trace reader
		Iterable<TracingInput> inputs = expressionMapper_.convertExpressionToTracingInputs(expression);
		InferenceMapper mapper = new InferenceMapper(new RecursiveTraceUnwinder(getTraceReader()), expressionFactory_);
		final List<Inference> userInferences = new LinkedList<Inference>();
		InferenceVisitor<Void, Void> collector = new AbstractInferenceVisitor<Void, Void>() {

			@Override
			protected Void defaultVisit(Inference inference, Void input) {
				userInferences.add(inference);
				return null;
			}

		};

		// second, map inferences
		mapper.map(inputs, collector);

		return userInferences;
	}
	
	
	/**
	 * 
	 * @author	Pavel Klinov
	 * 			pavel.klinov@uni-ulm.de
	 *
	 */
	private class AuxInferenceCreator extends BaseExpressionVisitor<Void, Inference> {
		
		private final SatisfiabilityChecker entailmentChecker_;
		
		AuxInferenceCreator(SatisfiabilityChecker checker) {
			entailmentChecker_ = checker;
		}

		@Override
		protected Inference defaultLemmaVisit(ElkLemma l, Void input) {
			return null;
		}

		@Override
		public Inference visit(ElkSubClassOfLemma lemma, Void input) {
			IndexedClassExpression sub = lemma.getSubClass().accept(reasoner.getExpressionConverter());
			
			if (!entailmentChecker_.isSatisfiable(sub)) {
				ElkObjectFactory factory = new ElkObjectFactoryImpl();
				AxiomExpression<ElkSubClassOfAxiom> premise = expressionFactory_.create(factory.getSubClassOfAxiom(lemma.getSubClass(), PredefinedElkClass.OWL_NOTHING));
				
				return new UnsatisfiabilityInference(lemmaExpression, premise);
			}
			
			return super.visit(lemma, input);
		}

		@Override
		public Inference visit(ElkSubClassOfAxiom axiom) {
			IndexedClassExpression sub = axiom.getSubClassExpression().accept(reasoner.getExpressionConverter());
			
			if (!entailmentChecker_.isSatisfiable(sub) && !isNothing(axiom.getSuperClassExpression())) {
				ElkObjectFactory factory = new ElkObjectFactoryImpl();
				AxiomExpression<ElkSubClassOfAxiom> premise = expressionFactory_.create(factory.getSubClassOfAxiom(axiom.getSubClassExpression(), PredefinedElkClass.OWL_NOTHING));
				
				return new UnsatisfiabilityInference(axiomExpression, premise);
			}
			
			return super.visit(axiom);
		}
		
		private boolean isNothing(ElkClassExpression ce) {
			return StructuralEquivalenceChecker.equal(ce, PredefinedElkClass.OWL_NOTHING);
		}

		@Override
		protected Inference defaultLogicalVisit(ElkAxiom axiom) {
			return null;
		}

	}
}
