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
import org.semanticweb.elk.owl.interfaces.ElkClassExpression;
import org.semanticweb.elk.owl.interfaces.ElkSubClassOfAxiom;
import org.semanticweb.elk.proofs.expressions.Expression;
import org.semanticweb.elk.proofs.expressions.derived.DerivedAxiomExpression;
import org.semanticweb.elk.proofs.expressions.derived.DerivedExpressionFactory;
import org.semanticweb.elk.proofs.expressions.derived.DerivedExpressionFactoryWithCaching;
import org.semanticweb.elk.proofs.inferences.AbstractInferenceVisitor;
import org.semanticweb.elk.proofs.inferences.Inference;
import org.semanticweb.elk.proofs.inferences.InferenceVisitor;
import org.semanticweb.elk.proofs.inferences.mapping.ExpressionMapper;
import org.semanticweb.elk.proofs.inferences.mapping.InferenceMapper;
import org.semanticweb.elk.proofs.inferences.mapping.SatisfiabilityChecker;
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
	
	public ReasonerInferenceReader(AbstractReasonerState r) {
		reasoner = r;
		// this expression factory will guarantee pointer equality for structurally equivalent expressions
		expressionFactory_ = new DerivedExpressionFactoryWithCaching(this);
	}
	
	public DerivedAxiomExpression<ElkSubClassOfAxiom> initialize(ElkClassExpression sub, ElkClassExpression sup) throws ElkException {
		// trace it
		reasoner.explainSubsumption(sub, sup);
		// create and return the first derived expression which corresponds to the initial subsumption
		return expressionFactory_.create(new ElkObjectFactoryImpl().getSubClassOfAxiom(sub, sup));
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
		Iterable<TracingInput> inputs = ExpressionMapper.convertExpressionToTracingInputs(expression, reasoner.getIndexObjectConverter(), new SatisfiabilityChecker() {

			@Override
			public boolean isSatisfiable(IndexedClassExpression ice) {
				return reasoner.isSatisfiable(ice);
			}
		});
		
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
