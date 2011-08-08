/*
 * #%L
 * elk-reasoner
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
 * @author Yevgeny Kazakov, Apr 8, 2011
 */
package org.semanticweb.elk.syntax.interfaces;

import org.semanticweb.elk.syntax.implementation.ElkClassImpl;

/**
 * Corresponds to a <a href=
 * "http://www.w3.org/TR/owl2-syntax/#Classes">Class<a> in the OWL 2
 * specification.
 * 
 * @author Yevgeny Kazakov
 */
public interface ElkClass extends ElkClassExpression, ElkEntity {

	/**
	 * TODO Find a better architecture for this.
	 */
	public static final ElkClass ELK_OWL_THING = 
		ElkClassImpl.create("owl:Thing");
	/**
	 * TODO Find a better architecture for this.
	 */
	public static final ElkClass ELK_OWL_NOTHING = 
		ElkClassImpl.create("owl:Nothing");
	
}
