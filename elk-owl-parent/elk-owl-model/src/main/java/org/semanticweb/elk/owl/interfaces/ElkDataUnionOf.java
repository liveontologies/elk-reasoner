/*
 * #%L
 * elk-reasoner
 * 
 * $Id: ElkDataIntersectionOf.java 295 2011-08-10 11:43:29Z mak@aifb.uni-karlsruhe.de $
 * $HeadURL: https://elk-reasoner.googlecode.com/svn/trunk/elk-reasoner/src/main/java/org/semanticweb/elk/syntax/interfaces/ElkDataIntersectionOf.java $
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

import org.semanticweb.elk.owl.visitors.ElkDataUnionOfVisitor;

/**
 * Corresponds to a
 * <a href= "http://www.w3.org/TR/owl2-syntax/#Union_of_Data_Ranges" >Union of
 * Data Ranges</a> in the OWL 2 specification.
 * 
 * @author Markus Kroetzsch
 */
public interface ElkDataUnionOf extends ElkDataRange {

	/**
	 * Get the list of data ranges that this expression refers to. The order of
	 * data ranges does not affect the semantics but it is relevant to the
	 * syntax of OWL.
	 * 
	 * @return list of data ranges
	 */
	public List<? extends ElkDataRange> getDataRanges();

	/**
	 * Accept an {@link ElkDataUnionOfVisitor}.
	 * 
	 * @param visitor
	 *            the visitor that can work with this object type
	 * @param <O>
	 *            the type of the output of the visitor
	 * @return the output of the visitor
	 */
	public <O> O accept(ElkDataUnionOfVisitor<O> visitor);

	/**
	 * A factory for creating instances
	 * 
	 * @author Yevgeny Kazakov
	 *
	 */
	interface Factory {

		/**
		 * Create an {@link ElkDataUnionOf}.
		 * 
		 * @param first
		 *            the first {@link ElkDataRange} for which the object should
		 *            be created
		 * @param second
		 *            the second {@link ElkDataRange} for which the object
		 *            should be created
		 * @param other
		 *            other {@link ElkDataRange}s for which the object should be
		 *            created
		 * @return an {@link ElkDataUnionOf} corresponding to the input
		 */
		public ElkDataUnionOf getDataUnionOf(ElkDataRange first,
				ElkDataRange second, ElkDataRange... other);

		/**
		 * Create an {@link ElkDataUnionOf}.
		 * 
		 * @param ranges
		 *            the {@link ElkDataRange}s for which the object should be
		 *            created
		 * @return an {@link ElkDataUnionOf} corresponding to the input
		 */
		public ElkDataUnionOf getDataUnionOf(
				List<? extends ElkDataRange> ranges);

	}

}
