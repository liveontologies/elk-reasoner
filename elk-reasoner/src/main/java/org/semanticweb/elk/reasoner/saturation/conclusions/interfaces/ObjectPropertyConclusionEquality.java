package org.semanticweb.elk.reasoner.saturation.conclusions.interfaces;

/*
 * #%L
 * ELK Reasoner
 * $Id:$
 * $HeadURL:$
 * %%
 * Copyright (C) 2011 - 2015 Department of Computer Science, University of Oxford
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

import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedObject;
import org.semanticweb.elk.reasoner.saturation.conclusions.visitors.ObjectPropertyConclusionVisitor;
import org.semanticweb.elk.reasoner.saturation.inferences.properties.SubPropertyChain;

public class ObjectPropertyConclusionEquality implements
		ObjectPropertyConclusionVisitor<Void, ObjectPropertyConclusion> {

	private final Object object_;

	private ObjectPropertyConclusionEquality(Object object) {
		this.object_ = object;
	}

	public static boolean equals(ObjectPropertyConclusion first, Object second) {
		return first == null ? second == null : first.accept(
				new ObjectPropertyConclusionEquality(second), null) == second;
	}

	private static boolean equals(IndexedObject first, IndexedObject second) {
		return first == second;
	}

	@Override
	public SubPropertyChain visit(SubPropertyChain conclusion, Void input) {
		if (object_ == conclusion)
			return conclusion;
		if (object_ instanceof SubPropertyChain) {
			SubPropertyChain result = (SubPropertyChain) object_;
			if (equals(result.getSubChain(), conclusion.getSubChain())
					&& equals(result.getSuperChain(),
							conclusion.getSuperChain()))
				return result;
		}
		return null;
	}

}
