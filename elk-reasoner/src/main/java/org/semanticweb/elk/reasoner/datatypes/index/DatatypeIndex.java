/*
 * #%L
 * ELK Reasoner
 * $Id:$
 * $HeadURL:$
 * %%
 * Copyright (C) 2011 - 2013 Department of Computer Science, University of Oxford
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
package org.semanticweb.elk.reasoner.datatypes.index;

import java.util.Collection;
import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedDatatypeExpression;
import org.semanticweb.elk.reasoner.saturation.rules.DatatypeRule;

/**
 * General interface for storage and retrieval of datatype expressions.
 *
 * @author Pospishnyi Oleksandr
 */
public interface DatatypeIndex {

	/**
	 * Add new rule to this datatype index
	 *
	 * @param rule {@link DatatypeRule} to add for this index
	 */
	public void addDatatypeRule(DatatypeRule rule);

	/**
	 * Remove already stored rule from this datatype index
	 *
	 * @param rule {@link DatatypeRule} to remove for this index
	 * @return true if this index contained the specified rule.
	 */
	public boolean removeDatatypeRule(DatatypeRule rule);

	/**
	 * Search this datatype index for all {@link DatatypeRule}s that must be
	 * applied for this {@link IndexedDatatypeExpression}.
	 *
	 * @param ide {@link IndexedDatatypeExpression} for which a search of
	 * relevant {@link DatatypeRule}s will be conducted in the index
	 * @return a collection of relevant datatype rules
	 */
	public Collection<DatatypeRule> getDatatypeRulesFor(IndexedDatatypeExpression ide);
}
