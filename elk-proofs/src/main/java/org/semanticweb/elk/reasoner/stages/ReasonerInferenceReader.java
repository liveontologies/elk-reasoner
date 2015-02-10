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
import org.semanticweb.elk.owl.predefined.PredefinedElkClass;
import org.semanticweb.elk.owl.visitors.AbstractElkEntityVisitor;
import org.semanticweb.elk.proofs.expressions.Expression;
import org.semanticweb.elk.proofs.expressions.derived.DerivedAxiomExpression;
import org.semanticweb.elk.proofs.expressions.derived.DerivedExpressionFactory;
import org.semanticweb.elk.proofs.expressions.derived.DerivedExpressionFactoryWithCaching;
import org.semanticweb.elk.proofs.inferences.AbstractInferenceVisitor;
import org.semanticweb.elk.proofs.inferences.Inference;
import org.semanticweb.elk.proofs.inferences.InferenceVisitor;
import org.semanticweb.elk.proofs.inferences.mapping.ExpressionMapper;
import org.semanticweb.elk.proofs.inferences.mapping.InferenceMapper;
import org.semanticweb.elk.proofs.inferences.mapping.EntailmentChecker;
import org.semanticweb.elk.proofs.inferences.mapping.TracingInput;
import org.semanticweb.elk.proofs.inferences.readers.InferenceReader;
import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexObjectConverter;
import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedClassExpression;
import org.semanticweb.elk.reasoner.saturation.tracing.RecursiveTraceUnwinder;
import org.semanticweb.elk.reasoner.saturation.tracing.TraceStore;


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
	
	private final DerivedExpressionFactory expressionFactory_;
	
	private final ExpressionMapper expressionMapper_;
	
	public ReasonerInferenceReader(AbstractReasonerState r) {
		reasoner = r;
		// this expression factory will guarantee pointer equality for structurally equivalent expressions
		expressionFactory_ = new DerivedExpressionFactoryWithCaching(this);
		expressionMapper_ = new ExpressionMapper(reasoner.getIndexObjectConverter(), new EntailmentChecker() {

			@Override
			public boolean isSatisfiable(IndexedClassExpression ice) {
				return reasoner.isSatisfiable(ice);
			}

			@Override
			public boolean isDerivedSubsumer(IndexedClassExpression subsumee, IndexedClassExpression subsumer) {
				return reasoner.isSubsumerDerived(subsumee, subsumer);
			}
		});
	}
	
	public DerivedAxiomExpression<?> initialize(ElkClassExpression sub, ElkClassExpression sup) throws ElkException {
		// trace it
		if (reasoner.isInconsistent()) {
			return initializeForInconsistency();
		}
		else {
			reasoner.explainSubsumption(sub, sup);
			// create and return the first derived expression which corresponds to the initial subsumption
			return expressionFactory_.create(new ElkObjectFactoryImpl().getSubClassOfAxiom(sub, sup));
		}
	}
	
	public DerivedAxiomExpression<?> initializeForInconsistency() throws ElkException {
		final ElkObjectFactory factory = new ElkObjectFactoryImpl();
		ElkEntity inconsistent = reasoner.explainInconsistency();
		ElkAxiom axiom = inconsistent.accept(new AbstractElkEntityVisitor<ElkAxiom>() {

			@Override
			public ElkAxiom visit(ElkClass elkClass) {
				return factory.getSubClassOfAxiom(elkClass, PredefinedElkClass.OWL_NOTHING);
			}

			@Override
			public ElkAxiom visit(ElkNamedIndividual elkNamedIndividual) {
				// representing class assertion using a nominal
				return factory.getSubClassOfAxiom(factory.getObjectOneOf(elkNamedIndividual), PredefinedElkClass.OWL_NOTHING);
			}
			
		});
		
		return expressionFactory_.create(axiom);
	}
	
	public DerivedExpressionFactory getExpressionFactory() {
		return expressionFactory_;
	}
	
	public IndexObjectConverter getIndexer() {
		return reasoner.getIndexObjectConverter();
	}
	
	public TraceStore.Reader getTraceReader() {
		return reasoner.getTraceState().getTraceStore().getReader();
	}
	
	@Override
	public Iterable<Inference> getInferences(Expression expression) throws ElkException {
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
	
}
