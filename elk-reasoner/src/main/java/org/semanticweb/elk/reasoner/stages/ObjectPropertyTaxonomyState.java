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
package org.semanticweb.elk.reasoner.stages;

import org.semanticweb.elk.owl.interfaces.ElkObjectProperty;
import org.semanticweb.elk.reasoner.taxonomy.model.UpdateableTaxonomy;

/**
 * Keeps track of state of the object property taxonomy. Currently contains only
 * the taxonomy.
 * 
 * @author Peter Skocovsky
 */
public class ObjectPropertyTaxonomyState {

	private UpdateableTaxonomy<ElkObjectProperty> taxonomy_ = null;

	public UpdateableTaxonomy<ElkObjectProperty> getTaxonomy() {
		return taxonomy_;
	}

	public Writer getWriter() {
		return new Writer();
	}

	public class Writer {

		void setTaxonomy(final UpdateableTaxonomy<ElkObjectProperty> taxonomy) {
			taxonomy_ = taxonomy;
		}

		void clearTaxonomy() {
			taxonomy_ = null;
		}

	}

}
