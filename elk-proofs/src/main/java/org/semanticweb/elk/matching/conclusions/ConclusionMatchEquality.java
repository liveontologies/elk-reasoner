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

import java.util.List;

import org.semanticweb.elk.owl.comparison.ElkObjectEquality;
import org.semanticweb.elk.owl.interfaces.ElkObject;
import org.semanticweb.elk.reasoner.tracing.Conclusion;

public class ConclusionMatchEquality
		implements ConclusionMatch.Visitor<Boolean> {

	private final ConclusionMatch other_;

	private ConclusionMatchEquality(ConclusionMatch other) {
		this.other_ = other;
	}

	private static class DefaultVisitor
			extends ConclusionMatchDummyVisitor<Boolean> {

		@Override
		protected Boolean defaultVisit(ConclusionMatch conclusionMatch) {
			return false;
		}

		static boolean equals(ConclusionMatch first, ConclusionMatch second) {
			return first.equals(second);
		}

		static boolean equals(IndexedContextRootMatch first,
				IndexedContextRootMatch second) {
			return first.equals(second);
		}

		static boolean equals(SubsumerMatch first, SubsumerMatch second) {
			return first.equals(second);
		}

		static boolean equals(IndexedContextRootMatchChain first,
				IndexedContextRootMatchChain second) {
			boolean result = false;
			for (;;) {
				if (first == null) {
					return (second == null) && result;
				}
				// else
				if (second == null) {
					return false;
				}
				// else
				result &= equals(first.getHead(), second.getHead());
				first = first.getTail();
				second = second.getTail();
			}
		}

		static boolean equals(Conclusion first, Conclusion second) {
			return first.equals(second);
		}

		static boolean equals(int first, int second) {
			return first == second;
		}

		static boolean equals(ElkObject first, ElkObject second) {
			return first.equals(second);
		}

		static boolean equals(List<? extends ElkObject> first,
				List<? extends ElkObject> second) {
			return ElkObjectEquality.equals(first, second);
		}
	}

	public static boolean equals(ConclusionMatch first,
			ConclusionMatch second) {
		return first.accept(new ConclusionMatchEquality(second));
	}

	@Override
	public Boolean visit(
			final IndexedDisjointClassesAxiomMatch1 conclusionMatch) {
		return other_.accept(new DefaultVisitor() {
			@Override
			public Boolean visit(IndexedDisjointClassesAxiomMatch1 other) {
				return equals(other.getParent(), conclusionMatch.getParent());
			}
		});
	}

	@Override
	public Boolean visit(
			final IndexedDisjointClassesAxiomMatch2 conclusionMatch) {
		return other_.accept(new DefaultVisitor() {
			@Override
			public Boolean visit(IndexedDisjointClassesAxiomMatch2 other) {
				return equals(other.getParent(), conclusionMatch.getParent())
						&& equals(other.getMemberMatches(),
								conclusionMatch.getMemberMatches());
			}
		});
	}

	@Override
	public Boolean visit(final IndexedSubClassOfAxiomMatch1 conclusionMatch) {
		return other_.accept(new DefaultVisitor() {
			@Override
			public Boolean visit(IndexedSubClassOfAxiomMatch1 other) {
				return equals(other.getParent(), conclusionMatch.getParent());
			}
		});
	}

	@Override
	public Boolean visit(final IndexedSubClassOfAxiomMatch2 conclusionMatch) {
		return other_.accept(new DefaultVisitor() {
			@Override
			public Boolean visit(IndexedSubClassOfAxiomMatch2 other) {
				return equals(other.getParent(), conclusionMatch.getParent())
						&& equals(other.getSubClassMatch(),
								conclusionMatch.getSubClassMatch())
						&& equals(other.getSuperClassMatch(),
								conclusionMatch.getSuperClassMatch());
			}
		});
	}

	@Override
	public Boolean visit(final IndexedEquivalentClassesAxiomMatch1 conclusionMatch) {
		return other_.accept(new DefaultVisitor() {
			@Override
			public Boolean visit(IndexedEquivalentClassesAxiomMatch1 other) {
				return equals(other.getParent(), conclusionMatch.getParent());
			}
		});
	}

	@Override
	public Boolean visit(final IndexedEquivalentClassesAxiomMatch2 conclusionMatch) {
		return other_.accept(new DefaultVisitor() {
			@Override
			public Boolean visit(IndexedEquivalentClassesAxiomMatch2 other) {
				return equals(other.getParent(), conclusionMatch.getParent())
						&& equals(other.getFirstMemberMatch(),
								conclusionMatch.getFirstMemberMatch())
						&& equals(other.getSecondMemberMatch(),
								conclusionMatch.getSecondMemberMatch());
			}
		});
	}

	@Override
	public Boolean visit(
			final IndexedSubObjectPropertyOfAxiomMatch1 conclusionMatch) {
		return other_.accept(new DefaultVisitor() {
			@Override
			public Boolean visit(IndexedSubObjectPropertyOfAxiomMatch1 other) {
				return equals(other.getParent(), conclusionMatch.getParent());
			}
		});
	}

	@Override
	public Boolean visit(
			final IndexedSubObjectPropertyOfAxiomMatch2 conclusionMatch) {
		return other_.accept(new DefaultVisitor() {
			@Override
			public Boolean visit(IndexedSubObjectPropertyOfAxiomMatch2 other) {
				return equals(other.getParent(), conclusionMatch.getParent())
						&& equals(other.getSubPropertyChainMatch(),
								conclusionMatch.getSubPropertyChainMatch())
						&& equals(other.getSuperPropertyMatch(),
								conclusionMatch.getSuperPropertyMatch());
			}
		});
	}

	@Override
	public Boolean visit(
			final IndexedObjectPropertyRangeAxiomMatch1 conclusionMatch) {
		return other_.accept(new DefaultVisitor() {
			@Override
			public Boolean visit(IndexedObjectPropertyRangeAxiomMatch1 other) {
				return equals(other.getParent(), conclusionMatch.getParent());
			}
		});
	}

	@Override
	public Boolean visit(
			final IndexedObjectPropertyRangeAxiomMatch2 conclusionMatch) {
		return other_.accept(new DefaultVisitor() {
			@Override
			public Boolean visit(IndexedObjectPropertyRangeAxiomMatch2 other) {
				return equals(other.getParent(), conclusionMatch.getParent())
						&& equals(other.getPropertyMatch(),
								conclusionMatch.getPropertyMatch())
						&& equals(other.getRangeMatch(),
								conclusionMatch.getRangeMatch());
			}
		});
	}

	@Override
	public Boolean visit(final BackwardLinkMatch1 conclusionMatch) {
		return other_.accept(new DefaultVisitor() {
			@Override
			public Boolean visit(BackwardLinkMatch1 other) {
				return equals(other.getParent(), conclusionMatch.getParent())
						&& equals(other.getRelationMatch(),
								conclusionMatch.getRelationMatch())
						&& equals(other.getSourceMatch(),
								conclusionMatch.getSourceMatch());
			}
		});
	}

	@Override
	public Boolean visit(final BackwardLinkMatch2 conclusionMatch) {
		return other_.accept(new DefaultVisitor() {
			@Override
			public Boolean visit(BackwardLinkMatch2 other) {
				return equals(other.getParent(), conclusionMatch.getParent())
						&& equals(other.getDestinationMatch(),
								conclusionMatch.getDestinationMatch());
			}
		});
	}

	@Override
	public Boolean visit(
			final SubClassInclusionComposedMatch1 conclusionMatch) {
		return other_.accept(new DefaultVisitor() {
			@Override
			public Boolean visit(SubClassInclusionComposedMatch1 other) {
				return equals(other.getParent(), conclusionMatch.getParent())
						&& equals(other.getDestinationMatch(),
								conclusionMatch.getDestinationMatch())
						&& equals(other.getSubsumerMatch(),
								conclusionMatch.getSubsumerMatch());
			}
		});
	}

	@Override
	public Boolean visit(
			final SubClassInclusionDecomposedMatch1 conclusionMatch) {
		return other_.accept(new DefaultVisitor() {
			@Override
			public Boolean visit(SubClassInclusionDecomposedMatch1 other) {
				return equals(other.getParent(), conclusionMatch.getParent())
						&& equals(other.getDestinationMatch(),
								conclusionMatch.getDestinationMatch());
			}
		});
	}

	@Override
	public Boolean visit(
			final SubClassInclusionDecomposedMatch2 conclusionMatch) {
		return other_.accept(new DefaultVisitor() {
			@Override
			public Boolean visit(SubClassInclusionDecomposedMatch2 other) {
				return equals(other.getParent(), conclusionMatch.getParent())
						&& equals(other.getSubsumerMatch(),
								conclusionMatch.getSubsumerMatch());
			}
		});
	}

	@Override
	public Boolean visit(final ForwardLinkMatch1 conclusionMatch) {
		return other_.accept(new DefaultVisitor() {
			@Override
			public Boolean visit(ForwardLinkMatch1 other) {
				return equals(other.getParent(), conclusionMatch.getParent())
						&& equals(other.getDestinationMatch(),
								conclusionMatch.getDestinationMatch())
						&& equals(other.getChainStartPos(),
								conclusionMatch.getChainStartPos())
						&& equals(other.getFullChainMatch(),
								conclusionMatch.getFullChainMatch());
			}
		});
	}

	@Override
	public Boolean visit(final ForwardLinkMatch2 conclusionMatch) {
		return other_.accept(new DefaultVisitor() {
			@Override
			public Boolean visit(ForwardLinkMatch2 other) {
				return equals(other.getParent(), conclusionMatch.getParent())
						&& equals(other.getTargetMatch(),
								conclusionMatch.getTargetMatch())
						&& equals(other.getIntermediateRoots(),
								conclusionMatch.getIntermediateRoots());
			}
		});
	}

	@Override
	public Boolean visit(final PropagationMatch1 conclusionMatch) {
		return other_.accept(new DefaultVisitor() {
			@Override
			public Boolean visit(PropagationMatch1 other) {
				return equals(other.getParent(), conclusionMatch.getParent())
						&& equals(other.getCarryMatch(),
								conclusionMatch.getCarryMatch());
			}
		});
	}

	@Override
	public Boolean visit(final PropagationMatch2 conclusionMatch) {
		return other_.accept(new DefaultVisitor() {
			@Override
			public Boolean visit(PropagationMatch2 other) {
				return equals(other.getParent(), conclusionMatch.getParent())
						&& equals(other.getRelationMatch(),
								conclusionMatch.getRelationMatch());
			}
		});
	}

	@Override
	public Boolean visit(final PropagationMatch3 conclusionMatch) {
		return other_.accept(new DefaultVisitor() {
			@Override
			public Boolean visit(PropagationMatch3 other) {
				return equals(other.getParent(), conclusionMatch.getParent())
						&& equals(other.getDestinationMatch(),
								conclusionMatch.getDestinationMatch());
			}
		});
	}

	@Override
	public Boolean visit(final PropertyRangeMatch1 conclusionMatch) {
		return other_.accept(new DefaultVisitor() {
			@Override
			public Boolean visit(PropertyRangeMatch1 other) {
				return equals(other.getParent(), conclusionMatch.getParent())
						&& equals(other.getPropertyMatch(),
								conclusionMatch.getPropertyMatch());
			}
		});
	}

	@Override
	public Boolean visit(final PropertyRangeMatch2 conclusionMatch) {
		return other_.accept(new DefaultVisitor() {
			@Override
			public Boolean visit(PropertyRangeMatch2 other) {
				return equals(other.getParent(), conclusionMatch.getParent())
						&& equals(other.getRangeMatch(),
								conclusionMatch.getRangeMatch());
			}
		});
	}

	@Override
	public Boolean visit(final SubPropertyChainMatch1 conclusionMatch) {
		return other_.accept(new DefaultVisitor() {
			@Override
			public Boolean visit(SubPropertyChainMatch1 other) {
				return equals(other.getParent(), conclusionMatch.getParent())
						&& equals(other.getFullSuperChainMatch(),
								conclusionMatch.getFullSuperChainMatch())
						&& equals(other.getSuperChainStartPos(),
								conclusionMatch.getSuperChainStartPos());
			}
		});
	}

	@Override
	public Boolean visit(final SubPropertyChainMatch2 conclusionMatch) {
		return other_.accept(new DefaultVisitor() {
			@Override
			public Boolean visit(SubPropertyChainMatch2 other) {
				return equals(other.getParent(), conclusionMatch.getParent())
						&& equals(other.getFullSubChainMatch(),
								conclusionMatch.getFullSubChainMatch())
						&& equals(other.getSubChainStartPos(),
								conclusionMatch.getSubChainStartPos());
			}
		});
	}

}
