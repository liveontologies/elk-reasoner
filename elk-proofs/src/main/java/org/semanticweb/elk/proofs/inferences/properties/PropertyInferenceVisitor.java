/**
 * 
 */
package org.semanticweb.elk.proofs.inferences.properties;
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

import org.semanticweb.elk.proofs.transformations.lemmas.ReflexivityElimination;

/**
 * @author Pavel Klinov
 *
 * pavel.klinov@uni-ulm.de
 */
public interface PropertyInferenceVisitor<I, O> {

	public O visit(SubPropertyChainAxiom inf, I input);
	
	public O visit(SubPropertyChainLemma inf, I input);
	
	public O visit(ReflexiveComposition inf, I input);
	
	public O visit(ReflexivityViaSubsumption inf, I input);
	
	public O visit(SubsumptionViaRightReflexivity inf, I input);
	
	public O visit(SubsumptionViaLeftReflexivity inf, I input);
	
	public O visit(ToldReflexivity inf, I input);
	
	public O visit(ReflexivityElimination inf, I input);
}
