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
package org.semanticweb.elk.reasoner.taxonomy;

import org.semanticweb.elk.owl.interfaces.ElkClass;
import org.semanticweb.elk.owl.interfaces.ElkNamedIndividual;
import org.semanticweb.elk.owl.predefined.PredefinedElkClass;
import org.semanticweb.elk.owl.util.Comparators;
import org.semanticweb.elk.reasoner.taxonomy.model.InstanceTaxonomy;
import org.semanticweb.elk.reasoner.taxonomy.model.Taxonomy;

/**
 * @author Frantisek Simancik
 * @author Pavel Klinov
 * 
 */
public class PredefinedTaxonomy {
	public static final Taxonomy<ElkClass> INCONSISTENT_CLASS_TAXONOMY;
	public static final InstanceTaxonomy<ElkClass, ElkNamedIndividual> INCONSISTENT_INDIVIDUAL_TAXONOMY;

	static {
		MockInstanceTaxonomy<ElkClass, ElkNamedIndividual> taxonomy = new MockInstanceTaxonomy<ElkClass, ElkNamedIndividual>(
				PredefinedElkClass.OWL_THING, PredefinedElkClass.OWL_NOTHING,
				Comparators.ELK_CLASS_COMPARATOR,
				Comparators.ELK_NAMED_INDIVIDUAL_COMPARATOR);

		taxonomy.makeInconsistent();
		INCONSISTENT_CLASS_TAXONOMY = taxonomy;
		INCONSISTENT_INDIVIDUAL_TAXONOMY = taxonomy;
	}
}