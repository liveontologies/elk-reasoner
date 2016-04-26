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

public class SubsumerMatchEquality implements SubsumerMatch.Visitor<Boolean> {

	private final SubsumerMatch other_;

	private SubsumerMatchEquality(SubsumerMatch other) {
		this.other_ = other;
	}

	public static boolean equals(SubsumerMatch first, SubsumerMatch second) {
		return first.accept(new SubsumerMatchEquality(second));
	}

	private static class DefaultVisitor
			extends SubsumerMatchDummyVisitor<Boolean> {

		@Override
		protected Boolean defaultVisit(SubsumerMatch conclusionMatch) {
			return false;
		}

		static boolean equals(Object first, Object second) {
			return first.equals(second);
		}
		
		static boolean equals(int first, int second) {
			return first == second;
		}
	}

	@Override
	public Boolean visit(final SubsumerGeneralMatch subsumerMatch) {
		return other_.accept(new DefaultVisitor() {
			@Override
			public Boolean visit(SubsumerGeneralMatch other) {
				return equals(other.getGeneralMatch(),
						subsumerMatch.getGeneralMatch());
			}
		});
	}

	@Override
	public Boolean visit(final SubsumerPartialConjunctionMatch subsumerMatch) {
		return other_.accept(new DefaultVisitor() {
			@Override
			public Boolean visit(SubsumerPartialConjunctionMatch other) {
				return equals(other.getFullConjunctionMatch(),
						subsumerMatch.getFullConjunctionMatch())
						&& equals(other.getConjunctionPrefixLength(),
								subsumerMatch.getConjunctionPrefixLength());
			}
		});
	}

}
