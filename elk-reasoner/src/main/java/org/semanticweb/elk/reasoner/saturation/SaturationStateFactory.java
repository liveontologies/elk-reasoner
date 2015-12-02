/**
 * 
 */
package org.semanticweb.elk.reasoner.saturation;

/*
 * #%L
 * ELK Reasoner
 * $Id:$
 * $HeadURL:$
 * %%
 * Copyright (C) 2011 - 2015 Department of Computer Science, University of Oxford
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

import org.semanticweb.elk.reasoner.indexing.model.OntologyIndex;
import org.semanticweb.elk.reasoner.saturation.context.Context;

/**
 * A simple factory for creating saturation states
 * 
 * @author Pavel Klinov
 * 
 *         pavel.klinov@uni-ulm.de
 */
public class SaturationStateFactory {

	/**
	 * Creates a new {@link SaturationState}
	 * 
	 * @param ontologyIndex
	 * @return the new state
	 */
	public static SaturationState<? extends Context> createSaturationState(
			OntologyIndex ontologyIndex) {
		return new ReferenceSaturationState(ontologyIndex);
		//return new MapSaturationState<ExtendedContext>(ontologyIndex, new MainContextFactory(), ontologyIndex.getIndexedClassExpressions().size());
	}
}
