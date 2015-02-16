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

/**
 * @author Pavel Klinov
 *
 * pavel.klinov@uni-ulm.de
 */
public enum InferenceRule {
	/*class inference rules*/
	R_SUB("Subsumption inference"),
	R_AND_COMPOSITION("Conjunction composition"),
	R_AND_DECOMPOSITION("Conjunction decomposition"),
	R_CONTRADITION_FROM_DISJOINTNESS("owl:Nothing inference from disjointness"),
	R_OR_COMPOSITION("Disjunction composition"),
	R_EXIST_COMPOSITION("Existential inference"),
	R_EXIST_CHAIN_COMPOSITION("Existential inference via property composition"),
	R_INCONSISTENT_DISJOINTNESS("owl:Nothing inference from inconsistent disjointness axiom"),
	R_CONTRADITION_FROM_NEGATION("owl:Nothing inference from negation"),
	R_REFLEXIVE_EXISTENTIAL("Existential inference for reflexive property"),
	R_UNSATISFIABILITY("Inference from class unsatisfiability"),
	/*inconsistency*/
	R_INCONSISTENCY("Inconsistency inference"),
	/*role inference rules*/
	R_CHAIN_SUBSUMPTION("Property subsumption inference"),
	R_REFLEXIVE_COMPOSITION("Composition of reflexive properties"),
	R_REFLEXIVITY_VIA_SUBSUMPTION("Reflexivity inference from subsumption"),
	R_SUBSUMPTION_VIA_REFLEXIVITY("Property subsumption inference from reflexivity"),
	R_TOLD_REFLEXIVITY("Reflexivity inference"),
	R_REFLEXIVITY_ELIMINATION("Reflexivity elimination");
	
	private final String text_;
	
	private InferenceRule(String t) {
		text_ = t;
	}

	@Override
	public String toString() {
		return text_;
	}
	
}
