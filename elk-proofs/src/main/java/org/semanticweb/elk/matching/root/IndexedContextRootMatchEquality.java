package org.semanticweb.elk.matching.root;

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

public class IndexedContextRootMatchEquality
		implements IndexedContextRootMatch.Visitor<Boolean> {

	private static class DefaultVisitor
			extends IndexedContextRootMatchDummyVisitor<Boolean> {

		@Override
		protected Boolean defaultVisit(IndexedContextRootMatch match) {
			return false;
		}

		boolean equals(Object first, Object second) {
			return first.equals(second);
		}

	}

	private final IndexedContextRootMatch other_;

	public IndexedContextRootMatchEquality(IndexedContextRootMatch other) {
		this.other_ = other;
	}

	@Override
	public Boolean visit(final IndexedContextRootClassExpressionMatch match) {
		return other_.accept(new DefaultVisitor() {
			@Override
			public Boolean visit(IndexedContextRootClassExpressionMatch other) {
				return equals(other.getValue(), match.getValue());
			}
		});
	}

	@Override
	public Boolean visit(final IndexedContextRootIndividualMatch match) {
		return other_.accept(new DefaultVisitor() {
			@Override
			public Boolean visit(IndexedContextRootIndividualMatch other) {
				return equals(other.getValue(), match.getValue());
			}
		});
	}

	@Override
	public Boolean visit(final IndexedContextRootRangeHasValueMatch match) {
		return other_.accept(new DefaultVisitor() {
			@Override
			public Boolean visit(IndexedContextRootRangeHasValueMatch other) {
				return equals(other.getValue(), match.getValue());
			}
		});
	}

	@Override
	public Boolean visit(
			final IndexedContextRootRangeSomeValuesFromMatch match) {
		return other_.accept(new DefaultVisitor() {
			@Override
			public Boolean visit(
					IndexedContextRootRangeSomeValuesFromMatch other) {
				return equals(other.getValue(), match.getValue());
			}
		});
	}

}
