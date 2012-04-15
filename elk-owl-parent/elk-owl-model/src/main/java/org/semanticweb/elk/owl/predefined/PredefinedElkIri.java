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
package org.semanticweb.elk.owl.predefined;

import org.semanticweb.elk.owl.iris.ElkFullIri;
import org.semanticweb.elk.owl.iris.ElkIri;

public class PredefinedElkIri {
	
	public static final ElkIri OWL_THING = new ElkFullIri(
			PredefinedElkPrefix.OWL, "Thing");
	
	public static final ElkIri OWL_NOTHING = new ElkFullIri(
			PredefinedElkPrefix.OWL, "Nothing");
	
	public static final ElkIri OWL_TOP_OBJECT_PROPERTY = new ElkFullIri(
			PredefinedElkPrefix.OWL, "TopObjectProperty");

	public static final ElkIri OWL_BOTTOM_OBJECT_PROPERTY = new ElkFullIri(
			PredefinedElkPrefix.OWL, "BottomObjectProperty");

	public static final ElkIri OWL_TOP_DATA_PROPERTY = new ElkFullIri(
			PredefinedElkPrefix.OWL, "TopDataProperty");

	public static final ElkIri OWL_BOTTOM_DATA_PROPERTY = new ElkFullIri(
			PredefinedElkPrefix.OWL, "BottomDataProperty");

	public static final ElkIri RDF_PLAIN_LITERAL = new ElkFullIri(
			PredefinedElkPrefix.RDF, "PlainLiteral");


	/**
	 * Defines an ordering on IRIs starting with OWL_NOTHING, OWL_THING,
	 * followed by the remaining IRIs in alphabetical order.  
	 */
	public static int compare(ElkIri arg0, ElkIri arg1) {
		boolean isOwl0 = arg0.equals(OWL_THING)
						|| arg0.equals(OWL_NOTHING);
		boolean isOwl1 = arg1.equals(OWL_THING)
						|| arg1.equals(OWL_NOTHING);
		
		if (isOwl0 == isOwl1)
			return arg0.compareTo(arg1);
		else
			return isOwl0 ? -1 : 1; 
	}
}
