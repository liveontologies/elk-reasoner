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

/**
 * Corresponds to an <a href=
 * "http://www.w3.org/TR/owl2-syntax/#Intersection_of_Data_Ranges" >Intersection
 * of Data Ranges<a> in the OWL 2 specification.
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
	public List<? extends ElkDataRange> getClassExpressions();

}
