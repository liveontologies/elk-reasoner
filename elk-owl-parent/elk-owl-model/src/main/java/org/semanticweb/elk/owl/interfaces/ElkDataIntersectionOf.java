/*
 * #%L
 * elk-reasoner
 * 
 * $Id: ElkDataIntersectionOf.java 297 2011-08-10 14:42:57Z mak@aifb.uni-karlsruhe.de $
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

import org.semanticweb.elk.owl.visitors.ElkDataIntersectionOfVisitor;

/**
 * Corresponds to an
 * <a href= "http://www.w3.org/TR/owl2-syntax/#Intersection_of_Data_Ranges" >
 * Intersection of Data Ranges<a> in the OWL 2 specification.
 * 
 * @author Markus Kroetzsch
 */
public interface ElkDataIntersectionOf extends ElkDataRange {

	/**
	 * Get the list of data ranges that this expression refers to. The order of
	 * data ranges does not affect the semantics but it is relevant to the
	 * syntax of OWL.
	 * 
	 * @return list of data ranges
	 */
	public List<? extends ElkDataRange> getDataRanges();

	/**
	 * Accept an {@link ElkDataIntersectionOfVisitor}.
	 * 
	 * @param visitor
	 *            the visitor that can work with this object type
	 * @return the output of the visitor
	 */
	public <O> O accept(ElkDataIntersectionOfVisitor<O> visitor);

	/**
	 * A factory for creating instances
	 * 
	 * @author Yevgeny Kazakov
	 *
	 */
	interface Factory {

		/**
		 * Create an {@link ElkDataIntersectionOf}.
		 * 
		 * @param first
		 *            the first {@link ElkDataRange} for which the object should
		 *            be created
		 * @param second
		 *            the second {@link ElkDataRange} for which the object
		 *            should be created
		 * @param other
		 *            the {@link ElkDataRange} for which the object should be
		 *            created
		 * @return an {@link ElkDataIntersectionOf} corresponding to the input
		 */
		public ElkDataIntersectionOf getDataIntersectionOf(
				ElkDataRange first, ElkDataRange second,
				ElkDataRange... other);

		/**
		 * Create an {@link ElkDataIntersectionOf}.
		 * 
		 * @param ranges
		 *            the {@link ElkDataRange}s for which the object should be
		 *            created
		 * @return an {@link ElkDataIntersectionOf} corresponding to the input
		 */
		public ElkDataIntersectionOf getDataIntersectionOf(
				List<? extends ElkDataRange> ranges);

	}

}
