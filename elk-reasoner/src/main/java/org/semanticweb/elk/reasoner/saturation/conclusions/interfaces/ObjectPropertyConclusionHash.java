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
import org.semanticweb.elk.util.hashing.HashGenerator;
import org.semanticweb.elk.util.hashing.Hasher;

public class ObjectPropertyConclusionHash implements
		ObjectPropertyConclusionVisitor<Void, Integer>,
		Hasher<ObjectPropertyConclusion> {

	private static final ObjectPropertyConclusionVisitor<Void, Integer> INSTANCE_ = new ObjectPropertyConclusionHash();

	private static int combinedHashCode(int... hashes) {
		return HashGenerator.combineListHash(hashes);
	}

	public static int hashCode(ObjectPropertyConclusion conclusion) {
		return conclusion == null ? 0 : conclusion.accept(INSTANCE_, null);
	}

	private static int hashCode(Class<?> c) {
		return c.hashCode();
	}

	private static int hashCode(IndexedObject o) {
		return o.hashCode();
	}

	@Override
	public int hash(ObjectPropertyConclusion object) {
		return hashCode(object);
	}

	@Override
	public Integer visit(SubPropertyChain conclusion, Void input) {
		return combinedHashCode(hashCode(SubPropertyChain.class),
				hashCode(conclusion.getSubChain()),
				hashCode(conclusion.getSuperChain()));
	}

}
