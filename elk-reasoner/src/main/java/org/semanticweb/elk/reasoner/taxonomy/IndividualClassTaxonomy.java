/*
 * #%L
 * ELK Reasoner
 * 
 * $Id$
 * $HeadURL$
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
package org.semanticweb.elk.reasoner.taxonomy;

import java.util.Collection;

import org.semanticweb.elk.owl.interfaces.ElkClass;
import org.semanticweb.elk.owl.interfaces.ElkNamedIndividual;

/**
 * Abstract taxonomy for classes and their instances (individuals). The main
 * purpose of this is to provide an "interface" that is specific enough for
 * engines to modify the data without needing to refer to a particular
 * implementation, such as {@link ConcurrentClassTaxonomy}.
 * 
 * @author Markus Kroetzsch
 * 
 */
public abstract class IndividualClassTaxonomy implements
		InstanceTaxonomy<ElkClass, ElkNamedIndividual> {

	abstract NonBottomClassNode getCreate(Collection<ElkClass> members);

	abstract void addUnsatisfiableClass(ElkClass elkClass);
}
