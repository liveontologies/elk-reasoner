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
/**
 * @author Yevgeny Kazakov, Jul 3, 2011
 */
package org.semanticweb.elk.syntax;

import org.semanticweb.elk.syntax.interfaces.ElkClass;
import org.semanticweb.elk.syntax.interfaces.ElkEntity;
import org.semanticweb.elk.syntax.interfaces.ElkNamedIndividual;
import org.semanticweb.elk.syntax.interfaces.ElkObjectProperty;

/**
 * Visitor pattern interface for instances of {@link ElkEntity}.
 * 
 * @author Yevgeny Kazakov
 *
 */
public interface ElkEntityVisitor<O> {
	
	O visit(ElkClass elkClass);

	O visit(ElkObjectProperty elkObjectProperty);
	
	O visit(ElkNamedIndividual elkNamedIndividual);

}
