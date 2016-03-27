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

import org.semanticweb.elk.owl.comparison.ElkObjectEquality;
import org.semanticweb.elk.owl.interfaces.ElkObject;

public class IndexedContextRootMatchEquality
		implements IndexedContextRootMatch.Visitor<Boolean> {

	private final IndexedContextRootMatch other_;

	private IndexedContextRootMatchEquality(IndexedContextRootMatch other) {
		this.other_ = other;
	}

	private static class DefaultVisitor
			extends DummyIndexedContextRootMatchVisitor<Boolean> {

		@Override
		protected Boolean defaultVisit(IndexedContextRootMatch match) {
			return false;
		}

		boolean equals(ElkObject first, ElkObject second) {
			return ElkObjectEquality.equals(first, second);
		}

	}

	public static boolean equals(IndexedContextRootMatch first,
			IndexedContextRootMatch second) {
		return first.accept(new IndexedContextRootMatchEquality(second));
	}

	@Override
	public Boolean visit(final IndexedClassExpressionMatch match) {
		return other_.accept(new DefaultVisitor() {
			@Override
			public Boolean visit(IndexedClassExpressionMatch other) {
				return equals(other.getValue(), match.getValue());
			}
		});
	}

	@Override
	public Boolean visit(final IndexedRangeFillerMatch match) {
		return other_.accept(new DefaultVisitor() {
			@Override
			public Boolean visit(IndexedRangeFillerMatch other) {
				return equals(other.getValue(), match.getValue());
			}
		});
	}

}
