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

public class IndexedContextRootMatchHash
		implements IndexedContextRootMatch.Visitor<Integer>,
		Hasher<IndexedContextRootMatch> {

	private static final IndexedContextRootMatchHash INSTANCE_ = new IndexedContextRootMatchHash();

	// forbid construction; only static methods should be used
	private IndexedContextRootMatchHash() {

	}

	public static int hashCode(IndexedContextRootMatch match) {
		return match == null ? 0 : match.accept(INSTANCE_);
	}

	public static IndexedContextRootMatch.Visitor<Integer> getHashVisitor() {
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

	@Override
	public int hash(IndexedContextRootMatch match) {
		return hashCode(match);
	}

	@Override
	public Integer visit(IndexedClassExpressionMatch match) {
		return combinedHashCode(hashCode(IndexedClassExpressionMatch.class),
				hashCode(match.getValue()));
	}

	@Override
	public Integer visit(IndexedRangeFillerMatch match) {
		return combinedHashCode(hashCode(IndexedRangeFillerMatch.class),
				hashCode(match.getValue()));
	}

}
