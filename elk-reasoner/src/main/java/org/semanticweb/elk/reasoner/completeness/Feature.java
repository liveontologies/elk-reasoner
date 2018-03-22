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
package org.semanticweb.elk.reasoner.completeness;

import org.semanticweb.elk.owl.predefined.ElkPolarity;

/**
 * A syntactic element, such as a keyword or an entity, occurrences of which are
 * important for completeness of reasoning results.
 * 
 * @author Yevgeny Kazakov
 */
public enum Feature {

	/**
	 * ObjectComplementOf occurs negatively.
	 */
	NEGATIVE_OCCURRENCE_OF_OBJECT_COMPLEMENT_OF("ObjectComplementOf",
			ElkPolarity.NEGATIVE),
	/**
	 * TopObjectProperty occurs negatively.
	 */
	NEGATIVE_OCCURRENCE_OF_TOP_OBJECT_PROPERTY("owl:TopObjectProperty",
			ElkPolarity.NEGATIVE),
	/**
	 * DataHasValue occurs.
	 */
	OCCURRENCE_OF_DATA_HAS_VALUE("DataHasValue", ElkPolarity.DUAL),
	/**
	 * DisjointUnion that contains more than one disjunct occurs.
	 */
	OCCURRENCE_OF_DISJOINT_UNION("DisjointUnion", ElkPolarity.DUAL),
	/**
	 * ObjectOneOf that contains one individual occurs.
	 */
	OBJECT_ONE_OF("ObjectOneOf", ElkPolarity.DUAL),
	/**
	 * ObjectPropertyAssertion occurs.
	 */
	OCCURRENCE_OF_OBJECT_PROPERTY_ASSERTION("ObjectPropertyAssertion",
			ElkPolarity.DUAL),
	/**
	 * ObjectPropertyRange occurs.
	 */
	OCCURRENCE_OF_OBJECT_PROPERTY_RANGE("ObjectPropertyRange",
			ElkPolarity.DUAL),	
	/**
	 * BottomObjectProperty occurs positively.
	 */
	POSITIVE_OCCURRENCE_OF_BOTTOM_OBJECT_PROPERTY("owl:BottomObjectProperty",
			ElkPolarity.DUAL),
	/**
	 * ObjectUnionOf or ObjectOneOf that contains more than one individual
	 * occurs.
	 */
	POSITIVE_OCCURRENCE_OF_OBJECT_UNION_OF("owl:ObjectUnionOf",
			ElkPolarity.POSITIVE),
	/**
	 * An expression that is completely unsupported.
	 */
	OCCURRENCE_OF_UNSUPPORTED_EXPRESSION("Unsupported Expression",
			ElkPolarity.DUAL);

	private final String constructor_;

	private final ElkPolarity polarity_;

	Feature(String constructor, ElkPolarity polarity) {
		this.constructor_ = constructor;
		this.polarity_ = polarity;
	}

	public String getConstructor() {
		return constructor_;
	}

	public ElkPolarity getPolarity() {
		return polarity_;
	}

	@Override
	public String toString() {
		switch (polarity_) {
		case POSITIVE:
			return "Positive occurrences of " + constructor_;
		case NEGATIVE:
			return "Negative occurrences of " + constructor_;
		default:
			return "Occurrences of " + constructor_;
		}
	}

}
