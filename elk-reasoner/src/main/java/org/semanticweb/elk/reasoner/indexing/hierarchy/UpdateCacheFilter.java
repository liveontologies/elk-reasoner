/*
 * #%L
 * ELK Reasoner
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
package org.semanticweb.elk.reasoner.indexing.hierarchy;


/**
 * @author Frantisek Simancik
 *
 */
class UpdateCacheFilter implements IndexedObjectFilter {
	
	protected final IndexedObjectCache objectCache;
	protected int increment, positiveIncrement, negativeIncrement;

	UpdateCacheFilter(IndexedObjectCache objectCache) {
		this.objectCache = objectCache;
		increment = 0;
		positiveIncrement = 0;
		negativeIncrement = 0;
	}
	
	void setIncrements(int increment, int positiveIncrement, int negativeIncrement) {
		this.increment = increment;
		this.positiveIncrement = positiveIncrement;
		this.negativeIncrement = negativeIncrement;
	}

	public IndexedClassExpression filter(IndexedClassExpression ice) {
		IndexedClassExpression result = objectCache.filter(ice);

		if (!result.occurs() && increment > 0)
			objectCache.add(result);
		
		result.updateOccurrenceNumbers(increment, positiveIncrement, negativeIncrement);
		
		if (!result.occurs() && increment < 0)
			objectCache.remove(result);
		
		return result;
	}

	public IndexedPropertyChain filter(IndexedPropertyChain ipc) {
		IndexedPropertyChain result = objectCache.filter(ipc);
		
		if (!result.occurs() && increment > 0)
			objectCache.add(result);
		
		result.updateOccurrenceNumber(increment);
		
		if (!result.occurs() && increment < 0)
			objectCache.remove(result);
		
		return result;
	}
}
