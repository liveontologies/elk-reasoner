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

public enum PredefinedElkIri {

	OWL_THING(new ElkFullIri(PredefinedElkPrefix.OWL.get(), "Thing")), //

	OWL_NOTHING(new ElkFullIri(PredefinedElkPrefix.OWL.get(), "Nothing")), //

	OWL_TOP_OBJECT_PROPERTY(new ElkFullIri(PredefinedElkPrefix.OWL.get(),
			"TopObjectProperty")), //

	OWL_BOTTOM_OBJECT_PROPERTY(new ElkFullIri(PredefinedElkPrefix.OWL.get(),
			"BottomObjectProperty")), //

	OWL_TOP_DATA_PROPERTY(new ElkFullIri(PredefinedElkPrefix.OWL.get(),
			"TopDataProperty")), //

	OWL_BOTTOM_DATA_PROPERTY(new ElkFullIri(PredefinedElkPrefix.OWL.get(),
			"BottomDataProperty")), //

	RDF_PLAIN_LITERAL(new ElkFullIri(PredefinedElkPrefix.RDF.get(),
			"PlainLiteral"))//
	;

	private final ElkIri iri;

	private PredefinedElkIri(ElkIri iri) {
		this.iri = iri;
	}

	public ElkIri get() {
		return this.iri;
	}

	/**
	 * Defines an ordering on IRIs starting with {@link #OWL_NOTHING},
	 * {@link #OWL_THING}, followed by the remaining IRIs in alphabetical order.
	 * 
	 * @param firstIri
	 *            the fist {@link ElkIri} to compare with the second
	 * @param secondIri
	 *            the second {@link ElkIri} to compare with the first
	 * @return a negative integer, zero, or a positive integer as the first IRI
	 *         is less, equal, or greater than the second IRI in the specified
	 *         order
	 */
	public static int compare(ElkIri firstIri, ElkIri secondIri) {
		boolean isOwl0 = firstIri.equals(OWL_THING.get())
				|| firstIri.equals(OWL_NOTHING.get());
		boolean isOwl1 = secondIri.equals(OWL_THING.get())
				|| secondIri.equals(OWL_NOTHING.get());

		if (isOwl0 == isOwl1)
			return firstIri.compareTo(secondIri);
		// else
		return isOwl0 ? -1 : 1;
	}
}
