/*
 * #%L
 * ELK Reasoner
 * $Id:$
 * $HeadURL:$
 * %%
 * Copyright (C) 2011 - 2016 Department of Computer Science, University of Oxford
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

import java.util.Comparator;

import org.semanticweb.elk.owl.interfaces.ElkEntity;
import org.semanticweb.elk.owl.predefined.PredefinedElkIris;
import org.semanticweb.elk.reasoner.taxonomy.model.ComparatorKeyProvider;

/**
 * {@link ComparatorKeyProvider} for
 * {@link org.semanticweb.elk.owl.interfaces.ElkObjectProperty
 * ElkObjectProperty}.
 * 
 * @author Peter Skocovsky
 */
public class ElkObjectPropertyKeyProvider extends ElkEntityKeyProvider
		implements ComparatorKeyProvider<ElkEntity> {

	/**
	 * The instance of this class.
	 */
	public static final ElkObjectPropertyKeyProvider INSTANCE = new ElkObjectPropertyKeyProvider();

	/**
	 * The comparator for
	 * {@link org.semanticweb.elk.owl.interfaces.ElkObjectProperty
	 * ElkObjectProperty}-es. Defines an ordering starting with
	 * {@link PredefinedElkIris#OWL_BOTTOM_OBJECT_PROPERTY},
	 * {@link PredefinedElkIris#OWL_TOP_OBJECT_PROPERTY}, followed by the
	 * remaining IRIs in alphabetical order.
	 */
	private static final Comparator<ElkEntity> COMPARATOR = new Comparator<ElkEntity>() {
		@Override
		public int compare(ElkEntity o1, ElkEntity o2) {
			boolean isOwl0 = o1.getIri()
					.equals(PredefinedElkIris.OWL_TOP_OBJECT_PROPERTY)
					|| o1.getIri().equals(
							PredefinedElkIris.OWL_BOTTOM_OBJECT_PROPERTY);
			boolean isOwl1 = o2.getIri()
					.equals(PredefinedElkIris.OWL_TOP_OBJECT_PROPERTY)
					|| o2.getIri().equals(
							PredefinedElkIris.OWL_BOTTOM_OBJECT_PROPERTY);

			if (isOwl0 == isOwl1) {
				return o1.getIri().compareTo(o2.getIri());
			} else {
				return isOwl0 ? -1 : 1;
			}
		}
	};

	@Override
	public Comparator<ElkEntity> getComparator() {
		return COMPARATOR;
	}

}
