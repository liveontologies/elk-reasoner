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
	R_SUB("R_sub"),
	R_AND_COMPOSITION("R_and+"),
	R_AND_DECOMPOSITION("R_and-"),
	R_CONTRADITION_FROM_DISJOINTNESS("R_disj_bot"),
	R_OR_COMPOSITION("R_or+"),
	R_EXIST_COMPOSITION("R_exist+"),
	R_EXIST_CHAIN_COMPOSITION("R_o"),
	R_INCONSISTENT_DISJOINTNESS("R_incons_disj"),
	R_CONTRADITION_FROM_NEGATION("R_bot_neg"),
	R_REFLEXIVE_EXISTENTIAL("R_exist_reflex"),
	/*role inference rules*/
	R_CHAIN_SUBSUMPTION("R_role_sub"),
	R_REFLEXIVE_COMPOSITION("R_reflex_chain"),
	R_REFLEXIVITY_VIA_SUBSUMPTION("R_reflex_sub"),
	R_SUBSUMPTION_VIA_REFLEXIVITY("R_sub_reflex"),
	R_TOLD_REFLEXIVITY("R_reflex");
	
	private final String text_;
	
	private InferenceRule(String t) {
		text_ = t;
	}

	@Override
	public String toString() {
		return text_;
	}
	
}
