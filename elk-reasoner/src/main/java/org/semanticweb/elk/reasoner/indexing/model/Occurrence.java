/*-
 * #%L
 * ELK Reasoner Core
 * $Id:$
 * $HeadURL:$
 * %%
 * Copyright (C) 2011 - 2017 Department of Computer Science, University of Oxford
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
package org.semanticweb.elk.reasoner.indexing.model;

public enum Occurrence {

	/**
	 * ObjectComplementOf occurs negatively.
	 */
	NEGATIVE_OCCURRENCE_OF_OBJECT_COMPLEMENT_OF,
	/**
	 * TopObjectProperty occurs negatively.
	 */
	NEGATIVE_OCCURRENCE_OF_TOP_OBJECT_PROPERTY,
	/**
	 * DataHasValue occurs.
	 */
	OCCURRENCE_OF_DATA_HAS_VALUE,
	/**
	 * DisjointUnion that contains more than one disjunct occurs.
	 */
	OCCURRENCE_OF_DISJOINT_UNION,
	/**
	 * ObjectOneOf that contains one individual occurs.
	 */
	OCCURRENCE_OF_NOMINAL,
	/**
	 * ObjectPropertyAssertion occurs.
	 */
	OCCURRENCE_OF_OBJECT_PROPERTY_ASSERTION,
	/**
	 * ObjectPropertyRange occurs.
	 */
	OCCURRENCE_OF_OBJECT_PROPERTY_RANGE,
	/**
	 * An expression that is completely unsupported.
	 */
	OCCURRENCE_OF_UNSUPPORTED_EXPRESSION,
	/**
	 * BottomObjectProperty occurs positively.
	 */
	POSITIVE_OCCURRENCE_OF_BOTTOM_OBJECT_PROPERTY,
	/**
	 * ObjectUnionOf or ObjectOneOf that contains more than one individual
	 * occurs.
	 */
	POSITIVE_OCCURRENCE_OF_OBJECT_UNION_OF;

}
