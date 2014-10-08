/**
 * 
 */
package org.semanticweb.elk.proofs.inferences;
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

import org.semanticweb.elk.proofs.inferences.classes.ClassInitialization;
import org.semanticweb.elk.proofs.inferences.classes.ClassSubsumption;
import org.semanticweb.elk.proofs.inferences.classes.ConjunctionComposition;
import org.semanticweb.elk.proofs.inferences.classes.ConjunctionDecomposition;
import org.semanticweb.elk.proofs.inferences.classes.DisjointnessContradiction;
import org.semanticweb.elk.proofs.inferences.classes.DisjunctionComposition;
import org.semanticweb.elk.proofs.inferences.classes.ExistentialComposition;
import org.semanticweb.elk.proofs.inferences.classes.ExistentialCompositionViaChain;
import org.semanticweb.elk.proofs.inferences.classes.InconsistentDisjointness;
import org.semanticweb.elk.proofs.inferences.classes.NegationContradiction;
import org.semanticweb.elk.proofs.inferences.classes.ReflexiveExistentialComposition;
import org.semanticweb.elk.proofs.inferences.classes.ThingInitialization;
import org.semanticweb.elk.proofs.inferences.properties.ChainSubsumption;
import org.semanticweb.elk.proofs.inferences.properties.ReflexiveComposition;
import org.semanticweb.elk.proofs.inferences.properties.ReflexivityViaSubsumption;
import org.semanticweb.elk.proofs.inferences.properties.SubsumptionViaReflexivity;
import org.semanticweb.elk.proofs.inferences.properties.ToldReflexivity;

/**
 * A skeleton implementation.
 * 
 * @author Pavel Klinov
 *
 * pavel.klinov@uni-ulm.de
 */
public abstract class AbstractInferenceVisitor<I ,O> implements InferenceVisitor<I, O> {

	protected abstract O defaultVisit(Inference inference, I input);

	@Override
	public O visit(AssertedInference inf, I input) {
		return defaultVisit(inf, input);
	}
	
	@Override
	public O visit(ChainSubsumption inf, I input) {
		
		return defaultVisit(inf, input);
	}

	@Override
	public O visit(ReflexiveComposition inf, I input) {
		
		return defaultVisit(inf, input);
	}

	@Override
	public O visit(ReflexivityViaSubsumption inf, I input) {
		
		return defaultVisit(inf, input);
	}

	@Override
	public O visit(SubsumptionViaReflexivity inf, I input) {
		
		return defaultVisit(inf, input);
	}

	@Override
	public O visit(ToldReflexivity inf, I input) {
		
		return defaultVisit(inf, input);
	}

	@Override
	public O visit(ClassInitialization inf, I input) {
		
		return defaultVisit(inf, input);
	}

	@Override
	public O visit(ThingInitialization inf, I input) {
		
		return defaultVisit(inf, input);
	}

	@Override
	public O visit(ClassSubsumption inf, I input) {
		
		return defaultVisit(inf, input);
	}

	@Override
	public O visit(ConjunctionComposition inf, I input) {
		
		return defaultVisit(inf, input);
	}

	@Override
	public O visit(ConjunctionDecomposition inf, I input) {
		
		return defaultVisit(inf, input);
	}

	@Override
	public O visit(DisjointnessContradiction inf, I input) {
		
		return defaultVisit(inf, input);
	}

	@Override
	public O visit(DisjunctionComposition inf, I input) {
		
		return defaultVisit(inf, input);
	}

	@Override
	public O visit(ExistentialComposition inf, I input) {
		
		return defaultVisit(inf, input);
	}

	@Override
	public O visit(ExistentialCompositionViaChain inf, I input) {
		
		return defaultVisit(inf, input);
	}

	@Override
	public O visit(InconsistentDisjointness inf, I input) {
		
		return defaultVisit(inf, input);
	}

	@Override
	public O visit(NegationContradiction inf, I input) {
		
		return defaultVisit(inf, input);
	}

	@Override
	public O visit(ReflexiveExistentialComposition inf, I input) {
		
		return defaultVisit(inf, input);
	}
	
	
}
