/*
 * #%L
 * ELK OWL Object Interfaces
 * 
 * $Id$
 * $HeadURL$
 * %%
 * Copyright (C) 2011 Department of Computer Science, University of Oxford
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
package org.semanticweb.elk.owl.util;

import java.util.Comparator;

import org.semanticweb.elk.owl.interfaces.ElkClass;
import org.semanticweb.elk.owl.interfaces.ElkNamedIndividual;
import org.semanticweb.elk.owl.predefined.PredefinedElkIris;

public class Comparators {

	public static Comparator<ElkClass> ELK_CLASS_COMPARATOR = new Comparator<ElkClass>() {
		@Override
		public int compare(ElkClass o1, ElkClass o2) {
			return PredefinedElkIris.compare(o1.getIri(), o2.getIri());
		}
	};
	
	public static Comparator<ElkNamedIndividual> ELK_NAMED_INDIVIDUAL_COMPARATOR = new Comparator<ElkNamedIndividual>() {
		@Override
		public int compare(ElkNamedIndividual o1, ElkNamedIndividual o2) {
			return o1.getIri().compareTo(o2.getIri());
		}
	};

}
