package org.semanticweb.elk.matching.conclusions;

/*
 * #%L
 * ELK Proofs Package
 * $Id:$
 * $HeadURL:$
 * %%
 * Copyright (C) 2011 - 2016 Department of Computer Science, University of Oxford
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

import org.semanticweb.elk.owl.comparison.ElkObjectHash;
import org.semanticweb.elk.owl.interfaces.ElkObject;
import org.semanticweb.elk.util.hashing.HashGenerator;
import org.semanticweb.elk.util.hashing.Hasher;

public class SubsumerMatchHash
		implements SubsumerMatch.Visitor<Integer>, Hasher<SubsumerMatch> {

	private static final SubsumerMatchHash INSTANCE_ = new SubsumerMatchHash();

	// forbid construction; only static methods should be used
	private SubsumerMatchHash() {

	}

	public static int hashCode(SubsumerMatch conclusion) {
		return conclusion == null ? 0 : conclusion.accept(INSTANCE_);
	}

	public static SubsumerMatch.Visitor<Integer> getHashVisitor() {
		return INSTANCE_;
	}

	private static int combinedHashCode(int... hashes) {
		return HashGenerator.combineListHash(hashes);
	}

	private static int hashCode(Class<?> c) {
		return c.hashCode();
	}

	private static int hashCode(ElkObject elkObject) {
		return ElkObjectHash.hashCode(elkObject);
	}

	private static int hashCode(int n) {
		return n;
	}

	@Override
	public int hash(SubsumerMatch object) {
		return hashCode(object);
	}

	@Override
	public Integer visit(SubsumerGeneralMatch subsumerMatch) {
		return combinedHashCode(hashCode(SubsumerGeneralMatch.class),
				hashCode(subsumerMatch.getGeneralMatch()));
	}

	@Override
	public Integer visit(SubsumerPartialConjunctionMatch subsumerMatch) {
		return combinedHashCode(hashCode(SubsumerPartialConjunctionMatch.class),
				hashCode(subsumerMatch.getFullConjunctionMatch()),
				hashCode(subsumerMatch.getConjunctionPrefixLength()));
	}

}
