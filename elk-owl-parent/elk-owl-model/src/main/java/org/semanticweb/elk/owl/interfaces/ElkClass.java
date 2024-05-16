/*
 * #%L
 * elk-reasoner
 * 
 * $Id: ElkClass.java 295 2011-08-10 11:43:29Z mak@aifb.uni-karlsruhe.de $
 * $HeadURL: https://elk-reasoner.googlecode.com/svn/trunk/elk-reasoner/src/main/java/org/semanticweb/elk/syntax/interfaces/ElkClass.java $
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
 * @author Markus Kroetzsch, Aug 8, 2011
 */
package org.semanticweb.elk.owl.interfaces;

import org.semanticweb.elk.owl.iris.ElkIri;
import org.semanticweb.elk.owl.predefined.PredefinedElkClassFactory;
import org.semanticweb.elk.owl.visitors.ElkClassVisitor;

/**
 * Corresponds to a
 * <a href= "http://www.w3.org/TR/owl2-syntax/#Classes">Class</a> in the OWL 2
 * specification.
 * 
 * @author Markus Kroetzsch
 */
public interface ElkClass extends ElkClassExpression, ElkEntity {

	/**
	 * Accept an {@link ElkClassVisitor}.
	 * 
	 * @param visitor
	 *            the visitor that can work with this object type
	 * @param <O>
	 *            the type of the output of the visitor           
	 * @return the output of the visitor
	 */
	public <O> O accept(ElkClassVisitor<O> visitor);

	/**
	 * A factory for creating instances
	 * 
	 * @author Yevgeny Kazakov
	 *
	 */
	interface Factory extends PredefinedElkClassFactory {

		/**
		 * Create an {@link ElkClass}.
		 * 
		 * @param iri
		 *            the {@link ElkIri} for which the object should be created
		 * @return an {@link ElkClass} corresponding to the input
		 */
		public ElkClass getClass(ElkIri iri);

	}

}
