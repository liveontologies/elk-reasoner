/*
 * #%L
 * elk-reasoner
 * 
 * $Id: ElkDataProperty.java 295 2011-08-10 11:43:29Z mak@aifb.uni-karlsruhe.de $
 * $HeadURL: https://elk-reasoner.googlecode.com/svn/trunk/elk-reasoner/src/main/java/org/semanticweb/elk/syntax/interfaces/ElkDataProperty.java $
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
 * @author Markus Kroetzsch, Aug 8, 2011
 */
package org.semanticweb.elk.owl.interfaces;

import org.semanticweb.elk.owl.iris.ElkIri;
import org.semanticweb.elk.owl.predefined.PredefinedElkDataPropertyFactory;
import org.semanticweb.elk.owl.visitors.ElkDataPropertyVisitor;

/**
 * Corresponds to an
 * <a href= "http://www.w3.org/TR/owl2-syntax/#Data_Properties">Data
 * Property</a> in the OWL 2 specification.
 * 
 * @author Markus Kroetzsch
 */
public interface ElkDataProperty extends ElkDataPropertyExpression, ElkEntity {

	/**
	 * Accept an {@link ElkDataPropertyVisitor}.
	 * 
	 * @param visitor
	 *            the visitor that can work with this object type
	 * @param <O>
	 *            the type of the output of the visitor
	 * @return the output of the visitor
	 */
	public abstract <O> O accept(ElkDataPropertyVisitor<O> visitor);

	/**
	 * A factory for creating instances
	 * 
	 * @author Yevgeny Kazakov
	 *
	 */
	interface Factory extends PredefinedElkDataPropertyFactory {

		/**
		 * Create an {@link ElkDataProperty}.
		 * 
		 * @param iri
		 *            the {@link ElkIri} for which the object should be created
		 * @return an {@link ElkDataProperty} corresponding to the input
		 */
		public ElkDataProperty getDataProperty(ElkIri iri);

	}

}
