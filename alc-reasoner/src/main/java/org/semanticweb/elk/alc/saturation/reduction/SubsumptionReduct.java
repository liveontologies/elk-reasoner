/**
 * 
 */
package org.semanticweb.elk.alc.saturation.reduction;
/*
 * #%L
 * ALC Reasoner
 * $Id:$
 * $HeadURL:$
 * %%
 * Copyright (C) 2011 - 2014 Department of Computer Science, University of Oxford
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

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.semanticweb.elk.alc.indexing.hierarchy.IndexedClass;

/**
 * Contains direct subsumers and equivalent classes for a specific class.
 * 
 * @author Pavel Klinov
 *
 * pavel.klinov@uni-ulm.de
 */
public class SubsumptionReduct {

	final List<IndexedClass> equivalent = new ArrayList<IndexedClass>(1);
	
	final List<IndexedClass> directSubsumers = new ArrayList<IndexedClass>(4);
	
	public Collection<IndexedClass> getEquivalentClasses() {
		return equivalent;
	}
	
	public Collection<IndexedClass> getDirectSubsumers() {
		return directSubsumers;
	}
}
