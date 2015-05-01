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

import org.semanticweb.elk.owl.exceptions.ElkException;
import org.semanticweb.elk.owl.implementation.ElkObjectFactoryImpl;
import org.semanticweb.elk.owl.interfaces.ElkObjectFactory;
import org.semanticweb.elk.owl.interfaces.ElkSubObjectPropertyOfAxiom;
import org.semanticweb.elk.proofs.expressions.AxiomExpression;
import org.semanticweb.elk.proofs.expressions.ExpressionFactory;
import org.semanticweb.elk.proofs.expressions.ExpressionVisitor;
import org.semanticweb.elk.proofs.inferences.Inference;
import org.semanticweb.elk.proofs.inferences.mapping.Deindexer;
import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedObjectProperty;
import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedPropertyChain;
import org.semanticweb.elk.reasoner.saturation.tracing.inferences.properties.ObjectPropertyInference;
import org.semanticweb.elk.reasoner.saturation.tracing.inferences.util.TracingUtils;
import org.semanticweb.elk.reasoner.stages.ReasonerInferenceReader;
import org.semanticweb.elk.util.collections.Multimap;

/**
 * TODO
 * 
 * @author	Pavel Klinov
 * 			pavel.klinov@uni-ulm.de
 *
 */
class SubPropertyChainExpression implements AxiomExpression<ElkSubObjectPropertyOfAxiom> {

	private final AxiomExpression<ElkSubObjectPropertyOfAxiom> expr_;
	
	private final IndexedObjectProperty superProperty_;
	
	private final IndexedPropertyChain subChain_;
	
	private final ElkObjectFactory elkFactory_ = new ElkObjectFactoryImpl();
	
	private final ReasonerInferenceReader reader_;
	
	SubPropertyChainExpression(AxiomExpression<ElkSubObjectPropertyOfAxiom> expr, IndexedObjectProperty superProperty, IndexedPropertyChain subChain, ReasonerInferenceReader reader) {
		expr_ = expr;
		superProperty_ = superProperty;
		subChain_ = subChain;
		reader_ = reader;
	}
	
	@Override
	public ElkSubObjectPropertyOfAxiom getAxiom() {
		return expr_.getAxiom();
	}

	@Override
	public boolean isAsserted() {
		return expr_.isAsserted();
	}

	@Override
	public <I, O> O accept(ExpressionVisitor<I, O> visitor, I input) {
		return visitor.visit(this, input);
	}

	@Override
	public Iterable<? extends Inference> getInferences() throws ElkException {
		Multimap<IndexedPropertyChain, ObjectPropertyInference> superChainToInferences = TracingUtils.getSuperPropertyInferenceMultimap(reader_.getTraceReader(), subChain_);
		final List<Inference> inferences = new ArrayList<Inference>();
		ExpressionFactory exprFactory = reader_.getExpressionFactory();
		LemmaFreePropertyInferenceFactory roleInfFactory = new LemmaFreePropertyInferenceFactory(expr_, reader_.getExpressionFactory());
		
		for (IndexedPropertyChain superChain : superChainToInferences.keySet()) {
			if (superProperty_.getSaturated().getSubPropertyChains().contains(superChain)) {
				for (ObjectPropertyInference inf : superChainToInferences.get(superChain)) {
					AxiomExpression<ElkSubObjectPropertyOfAxiom> firstPremise = exprFactory.create(
							elkFactory_.getSubObjectPropertyOfAxiom(Deindexer.deindex(superChain), superProperty_.getElkEntity()));
					Inference result = inf.acceptTraced(roleInfFactory, firstPremise); 
					
					if (result != null) {
						inferences.add(result);
					}
				}
			}
		}
		
		return inferences;
	}

}
