/*
 * #%L
 * elk-reasoner
 * 
 * $Id: ElkObject.java 265 2011-08-04 09:45:18Z mak@aifb.uni-karlsruhe.de $
 * $HeadURL: https://elk-reasoner.googlecode.com/svn/trunk/elk-reasoner/src/main/java/org/semanticweb/elk/syntax/ElkObject.java $
 * %%
 * Copyright (C) 2011 Oxford University Computing Laboratory
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
package org.semanticweb.elk.owlapi.wrapper;

import org.semanticweb.elk.owl.interfaces.ElkObject;
import org.semanticweb.elk.owl.predefined.AbstractElkObject;
import org.semanticweb.owlapi.model.OWLObject;

/**
 * Implements the {@link ElkObject} interface by wrapping instances of objects
 * from OWLAPI.
 * 
 * @author Yevgeny Kazakov
 * 
 * @param <T>
 *            the type of the wrapped object
 */
public abstract class ElkObjectWrap<T> extends AbstractElkObject implements ElkObject {

	/**
	 * The converter for converting sub-objects.
	 */
	protected static OwlConverter converter = OwlConverter.getInstance();

	/**
	 * The {@link OWLObject} for which {@link ElkObjectWrap} is created. Must be
	 * initialized by constructors of subclasses.
	 */
	protected final T owlObject;

	public ElkObjectWrap(T owlObject) {
		this.owlObject = owlObject;
	}
	
	public T getOwlObject() {
		return owlObject;
	}

}
