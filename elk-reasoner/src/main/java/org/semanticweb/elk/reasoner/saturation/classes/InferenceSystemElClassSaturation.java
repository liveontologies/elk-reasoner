/*
 * #%L
 * ELK Reasoner
 * 
 * $Id$
 * $HeadURL$
 * %%
 * Copyright (C) 2011 - 2012 Department of Computer Science, University of Oxford
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
package org.semanticweb.elk.reasoner.saturation.classes;

import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedClassExpression;
import org.semanticweb.elk.reasoner.saturation.rulesystem.InferenceSystem;

/**
 * Inference system for EL class saturation.
 * 
 * @author Markus Kroetzsch
 * 
 */
public final class InferenceSystemElClassSaturation extends InferenceSystem<ContextElClassSaturation> {
	
	static final boolean OPTIMIZE_DECOMPOSITIONS = true;

	public InferenceSystemElClassSaturation() {
		
		add(new RuleInitialization<ContextElClassSaturation>());
		add(new RuleSubsumption<ContextElClassSaturation>());
		add(new RuleDecomposition<ContextElClassSaturation>());
		add(new RuleConjunctionPlus<ContextElClassSaturation>());
//		add(new RuleExistentialPlus<ContextElClassSaturation>());
		add(new RuleExistentialPlusWithoutPropagations<ContextElClassSaturation>());
		add(new RuleRoleComposition<ContextElClassSaturation>());
		add(new RuleBottom<ContextElClassSaturation>());
		add(new RuleDisjoint<ContextElClassSaturation>());
		add(new RuleReflexiveRole<ContextElClassSaturation>());
	}

	@Override
	public final ContextElClassSaturation createContext(IndexedClassExpression root) {
		return new ContextElClassSaturation(root);
	}

}
