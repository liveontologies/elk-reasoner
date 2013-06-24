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
import java.util.Collections;
import java.util.EnumMap;
import java.util.HashSet;
import java.util.Set;
import org.semanticweb.elk.owl.interfaces.ElkDatatype;
import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedDatatypeExpression;
import org.semanticweb.elk.reasoner.saturation.rules.DatatypeRule;

/**
 * Simple (and inefficient) storage and retrieval of datatype expressions. All
 * rules belonging to the same datatype hierarchy (and thus sharing a value
 * space among themselves) are stored in a single collection.
 *
 * @author Pospishnyi Oleksandr
 */
public class SimpleDatatypeIndex implements DatatypeIndex {

	EnumMap<ElkDatatype.ELDatatype, Set<DatatypeRule>> datatypeRules;

	@Override
	public void addDatatypeRule(DatatypeRule rule) {
		if (datatypeRules == null) {
			datatypeRules = new EnumMap<ElkDatatype.ELDatatype, Set<DatatypeRule>>
				(ElkDatatype.ELDatatype.class);
		}
		ElkDatatype.ELDatatype rootDatatype = 
			rule.getValueSpace().getDatatype().getRootValueSpaceDatatype();
		Set<DatatypeRule> rulesByDatatype =
			datatypeRules.get(rootDatatype);
		if (rulesByDatatype == null) {
			rulesByDatatype = new HashSet<DatatypeRule>();
			datatypeRules.put(rootDatatype, rulesByDatatype);
		}
		rulesByDatatype.add(rule);
	}

	@Override
	public boolean removeDatatypeRule(DatatypeRule rule) {
		boolean success = false;
		if (rule != null && datatypeRules != null) {
			ElkDatatype.ELDatatype rootDatatype =
				rule.getValueSpace().getDatatype().getRootValueSpaceDatatype();
			Set<DatatypeRule> rulesByDatatype =
				datatypeRules.get(rootDatatype);
			if (rulesByDatatype != null) {
				success = rulesByDatatype.remove(rule);
				if (rulesByDatatype.isEmpty()) {
					datatypeRules.remove(rootDatatype);
				}
			}
		}
		return success;
	}

	@Override
	public Collection<DatatypeRule> getDatatypeRulesFor(IndexedDatatypeExpression ide) {
		if (datatypeRules != null) {
			ElkDatatype.ELDatatype rootDatatype =
				ide.getValueSpace().getDatatype().getRootValueSpaceDatatype();
			Set<DatatypeRule> rulesByDatatype =
				datatypeRules.get(rootDatatype);
			if (rulesByDatatype == null) {
				return Collections.EMPTY_LIST;
			} else {
				if (rootDatatype.getParent() != null) {
					//using all rules for rdfs:Literal datatype
					Set<DatatypeRule> rdfsLiteralRules = datatypeRules.get(rootDatatype.getParent());
					if (rdfsLiteralRules != null) {
						rulesByDatatype.addAll(rdfsLiteralRules);
					}
				}
				return rulesByDatatype;
			}
		}
		return Collections.EMPTY_LIST;
	}
}
