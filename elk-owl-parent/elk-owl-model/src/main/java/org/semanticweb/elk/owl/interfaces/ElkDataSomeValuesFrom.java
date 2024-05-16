/*
 * #%L
 * elk-reasoner
 * 
 * $Id: ElkDataSomeValuesFrom.java 295 2011-08-10 11:43:29Z mak@aifb.uni-karlsruhe.de $
 * $HeadURL: https://elk-reasoner.googlecode.com/svn/trunk/elk-reasoner/src/main/java/org/semanticweb/elk/syntax/interfaces/ElkDataSomeValuesFrom.java $
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

import java.util.List;

import org.semanticweb.elk.owl.visitors.ElkDataSomeValuesFromVisitor;

/**
 * Corresponds to an
 * <a href= "http://www.w3.org/TR/owl2-syntax/#Existential_Quantification_2">
 * Existential Quantification Data Property Restriction</a> in the OWL 2
 * specification.
 * 
 * @author Markus Kroetzsch
 */
public interface ElkDataSomeValuesFrom
		extends ElkDataPropertyListRestrictionQualified {

	/**
	 * Accept an {@link ElkDataSomeValuesFromVisitor}.
	 * 
	 * @param visitor
	 *            the visitor that can work with this object type
	 * @param <O>
	 *            the type of the output of the visitor
	 * @return the output of the visitor
	 */
	public <O> O accept(ElkDataSomeValuesFromVisitor<O> visitor);

	/**
	 * A factory for creating instances
	 * 
	 * @author Yevgeny Kazakov
	 *
	 */
	interface Factory {

		/**
		 * Create an {@link ElkDataSomeValuesFrom}
		 * 
		 * @param range
		 *            the {@link ElkDataRange} for which the object should be
		 *            created
		 * @param first
		 *            the first {@link ElkDataPropertyExpression} for which the
		 *            object should be created
		 * @param other
		 *            other {@link ElkDataPropertyExpression}s for which the
		 *            object should be created
		 * @return an {@link ElkDataSomeValuesFrom} corresponding to the input
		 */
		public ElkDataSomeValuesFrom getDataSomeValuesFrom(
				ElkDataRange range,
				ElkDataPropertyExpression first,
				ElkDataPropertyExpression... other);

		/**
		 * Create an {@link ElkDataSomeValuesFrom}
		 * @param properties
		 *            the {@link ElkDataPropertyExpression}s for which the
		 *            object should be created
		 * @param range
		 *            the {@link ElkDataRange} for which the object should be
		 *            created
		 * 
		 * @return an {@link ElkDataSomeValuesFrom} corresponding to the input
		 */
		public ElkDataSomeValuesFrom getDataSomeValuesFrom(
				List<? extends ElkDataPropertyExpression> properties,
				ElkDataRange range);

	}

}
