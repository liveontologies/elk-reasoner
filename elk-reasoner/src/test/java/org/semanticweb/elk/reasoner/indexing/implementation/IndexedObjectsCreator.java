/**
 * 
 */
package org.semanticweb.elk.reasoner.indexing.implementation;

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

import org.semanticweb.elk.owl.interfaces.ElkObjectProperty;
import org.semanticweb.elk.reasoner.indexing.implementation.CachedIndexedComplexPropertyChainImpl;
import org.semanticweb.elk.reasoner.indexing.implementation.CachedIndexedObjectPropertyImpl;
import org.semanticweb.elk.reasoner.indexing.modifiable.ModifiableIndexedComplexPropertyChain;
import org.semanticweb.elk.reasoner.indexing.modifiable.ModifiableIndexedObjectProperty;
import org.semanticweb.elk.reasoner.indexing.modifiable.ModifiableIndexedPropertyChain;

/**
 * A utility class to create indexed objects for other tests
 * 
 * @author Pavel Klinov
 * 
 *         pavel.klinov@uni-ulm.de
 */
public class IndexedObjectsCreator {

	public static ModifiableIndexedObjectProperty createIndexedObjectProperty(
			ElkObjectProperty prop, ModifiableIndexedPropertyChain[] toldSubs,
			ModifiableIndexedObjectProperty[] toldSupers, boolean reflexive) {

		ModifiableIndexedObjectProperty property = new CachedIndexedObjectPropertyImpl(
				prop);

		for (ModifiableIndexedPropertyChain sub : toldSubs) {
			property.addToldSubPropertyChain(sub);
			sub.addToldSuperObjectProperty(property);
		}

		for (ModifiableIndexedObjectProperty sup : toldSupers) {
			property.addToldSuperObjectProperty(sup);
			sup.addToldSubPropertyChain(property);
		}

		if (reflexive) {
			property.updateReflexiveOccurrenceNumber(1);
		}

		return property;
	}

	public static ModifiableIndexedPropertyChain createIndexedChain(
			ModifiableIndexedObjectProperty left,
			ModifiableIndexedPropertyChain right,
			ModifiableIndexedObjectProperty[] toldSupers) {

		ModifiableIndexedComplexPropertyChain chain = new CachedIndexedComplexPropertyChainImpl(
				left, right);

		for (ModifiableIndexedObjectProperty sup : toldSupers) {
			chain.addToldSuperObjectProperty(sup);
			sup.addToldSubPropertyChain(chain);
		}

		left.addLeftChain(chain);
		right.addRightChain(chain);

		return chain;
	}

}