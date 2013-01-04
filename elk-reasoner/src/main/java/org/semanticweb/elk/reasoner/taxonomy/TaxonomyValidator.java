/**
 * 
 */
package org.semanticweb.elk.reasoner.taxonomy;
/*
 * #%L
 * ELK Reasoner
 * $Id:$
 * $HeadURL:$
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

import org.semanticweb.elk.owl.interfaces.ElkObject;
import org.semanticweb.elk.reasoner.taxonomy.model.Taxonomy;

/**
 * @author Pavel Klinov
 *
 * pavel.klinov@uni-ulm.de
 */
public interface TaxonomyValidator<T extends ElkObject> {

	/**
	 * @param taxonomy Taxonomy to be validated
	 * @throws InvalidTaxonomyException if the taxonomy isn't valid according to the logic of this validator
	 */
	public void validate(final Taxonomy<T> taxonomy) throws InvalidTaxonomyException;
}
