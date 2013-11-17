/*
 * #%L
 * ELK Reasoner
 * *
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
package org.semanticweb.elk.reasoner.datatypes.handlers;

import org.semanticweb.elk.owl.interfaces.ElkDataRange;
import org.semanticweb.elk.owl.interfaces.ElkLiteral;
import org.semanticweb.elk.reasoner.datatypes.valuespaces.ValueSpace;

/**
 * Datatype handler interface. The main mission for classes implementing this
 * interface is to convert single literals and data ranges into the ELKs
 * internal representation of value space.
 * 
 * @author Pospishnyi Oleksandr
 * @author "Yevgeny Kazakov"
 * @author Pavel Klinov
 */
public interface DatatypeHandler {

	public ValueSpace<?> createValueSpace(ElkLiteral literal);

	public ValueSpace<?> createValueSpace(ElkDataRange dataRange);
}
