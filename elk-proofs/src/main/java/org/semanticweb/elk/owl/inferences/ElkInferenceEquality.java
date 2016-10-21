package org.semanticweb.elk.owl.inferences;

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

public class ElkInferenceEquality implements ElkInference.Visitor<Boolean> {

	private static class DefaultVisitor
			extends ElkInferenceDummyVisitor<Boolean> {

		static boolean equals(int first, int second) {
			return first == second;
		}

		static boolean equals(Object first, Object second) {
			return first.equals(second);
		}

		@Override
		protected Boolean defaultVisit(ElkInference conclusion) {
			return false;
		}

	}

	public static boolean equals(ElkInference first, ElkInference second) {
		return first.accept(new ElkInferenceEquality(second));
	}

	private final ElkInference other_;

	ElkInferenceEquality(ElkInference other) {
		this.other_ = other;
	}

	@Override
	public Boolean visit(
			final ElkClassInclusionEmptyObjectOneOfOwlNothing inference) {
		return other_.accept(new DefaultVisitor() {
			@Override
			public Boolean visit(
					ElkClassInclusionEmptyObjectOneOfOwlNothing other) {
				return true;
			}
		});
	}

	@Override
	public Boolean visit(
			final ElkClassInclusionEmptyObjectUnionOfOwlNothing inference) {
		return other_.accept(new DefaultVisitor() {
			@Override
			public Boolean visit(
					ElkClassInclusionEmptyObjectUnionOfOwlNothing other) {
				return true;
			}
		});
	}

	@Override
	public Boolean visit(
			final ElkClassInclusionExistentialFillerExpansion inference) {
		return other_.accept(new DefaultVisitor() {
			@Override
			public Boolean visit(
					ElkClassInclusionExistentialFillerExpansion other) {
				return equals(other.getSubClass(), inference.getSubClass())
						&& equals(other.getSuperClass(),
								inference.getSuperClass())
						&& equals(other.getProperty(), inference.getProperty());
			}
		});
	}

	@Override
	public Boolean visit(
			final ElkClassInclusionExistentialOfObjectHasSelf inference) {
		return other_.accept(new DefaultVisitor() {
			@Override
			public Boolean visit(
					ElkClassInclusionExistentialOfObjectHasSelf other) {
				return equals(other.getSubClass(), inference.getSubClass())
						&& equals(other.getProperty(), inference.getProperty());
			}
		});
	}

	@Override
	public Boolean visit(
			final ElkClassInclusionExistentialOwlNothing inference) {
		return other_.accept(new DefaultVisitor() {
			@Override
			public Boolean visit(ElkClassInclusionExistentialOwlNothing other) {
				return equals(other.getProperty(), inference.getProperty());
			}
		});
	}

	@Override
	public Boolean visit(
			final ElkClassInclusionExistentialPropertyExpansion inference) {
		return other_.accept(new DefaultVisitor() {
			@Override
			public Boolean visit(
					ElkClassInclusionExistentialPropertyExpansion other) {
				return equals(other.getClassExpressions(),
						inference.getClassExpressions())
						&& equals(other.getSubChain(), inference.getSubChain())
						&& equals(other.getSuperProperty(),
								inference.getSuperProperty());
			}
		});
	}

	@Override
	public Boolean visit(
			final ElkClassInclusionExistentialTransitivity inference) {
		return other_.accept(new DefaultVisitor() {
			@Override
			public Boolean visit(
					ElkClassInclusionExistentialTransitivity other) {
				return equals(other.getClassExpressions(),
						inference.getClassExpressions())
						&& equals(other.getTransitiveProperty(),
								inference.getTransitiveProperty());
			}
		});
	}

	@Override
	public Boolean visit(final ElkClassInclusionHierarchy inference) {
		return other_.accept(new DefaultVisitor() {
			@Override
			public Boolean visit(ElkClassInclusionHierarchy other) {
				return equals(other.getExpressions(),
						inference.getExpressions());
			}
		});
	}

	@Override
	public Boolean visit(final ElkClassInclusionNegationClash inference) {
		return other_.accept(new DefaultVisitor() {
			@Override
			public Boolean visit(ElkClassInclusionNegationClash other) {
				return equals(other.getExpression(), inference.getExpression());
			}
		});
	}

	@Override
	public Boolean visit(
			final ElkClassInclusionObjectIntersectionOfComposition inference) {
		return other_.accept(new DefaultVisitor() {
			@Override
			public Boolean visit(
					ElkClassInclusionObjectIntersectionOfComposition other) {
				return equals(other.getSubExpression(),
						inference.getSubExpression())
						&& equals(other.getConjuncts(),
								inference.getConjuncts());
			}
		});
	}

	@Override
	public Boolean visit(
			final ElkClassInclusionObjectIntersectionOfDecomposition inference) {
		return other_.accept(new DefaultVisitor() {
			@Override
			public Boolean visit(
					ElkClassInclusionObjectIntersectionOfDecomposition other) {
				return equals(other.getConjuncts(), inference.getConjuncts())
						&& equals(other.getConjunctPos(),
								inference.getConjunctPos());
			}
		});
	}

	@Override
	public Boolean visit(
			final ElkClassInclusionObjectOneOfInclusion inference) {
		return other_.accept(new DefaultVisitor() {
			@Override
			public Boolean visit(ElkClassInclusionObjectOneOfInclusion other) {
				return equals(other.getSuperIndividuals(),
						inference.getSuperIndividuals())
						&& equals(other.getSubIndividualPositions(),
								inference.getSubIndividualPositions());
			}
		});
	}

	@Override
	public Boolean visit(
			final ElkClassInclusionObjectUnionOfComposition inference) {
		return other_.accept(new DefaultVisitor() {
			@Override
			public Boolean visit(
					ElkClassInclusionObjectUnionOfComposition other) {
				return equals(other.getDisjuncts(), inference.getDisjuncts())
						&& equals(other.getDisjunctPos(),
								inference.getDisjunctPos());
			}
		});
	}

	@Override
	public Boolean visit(final ElkClassInclusionOfClassAssertion inference) {
		return other_.accept(new DefaultVisitor() {
			@Override
			public Boolean visit(ElkClassInclusionOfClassAssertion other) {
				return equals(other.getInstance(), inference.getInstance())
						&& equals(other.getType(), inference.getType());
			}
		});
	}

	@Override
	public Boolean visit(final ElkClassInclusionOfDisjointClasses inference) {
		return other_.accept(new DefaultVisitor() {
			@Override
			public Boolean visit(ElkClassInclusionOfDisjointClasses other) {
				return equals(other.getExpressions(),
						inference.getExpressions())
						&& equals(other.getFirstPos(), inference.getFirstPos())
						&& equals(other.getSecondPos(),
								inference.getSecondPos());
			}
		});
	}

	@Override
	public Boolean visit(final ElkClassInclusionOfEquivaletClasses inference) {
		return other_.accept(new DefaultVisitor() {
			@Override
			public Boolean visit(ElkClassInclusionOfEquivaletClasses other) {
				return equals(other.getExpressions(),
						inference.getExpressions())
						&& equals(other.getSubPos(), inference.getSubPos())
						&& equals(other.getSuperPos(), inference.getSuperPos());
			}
		});
	}

	@Override
	public Boolean visit(
			final ElkClassInclusionOfInconsistentIndividual inference) {
		return other_.accept(new DefaultVisitor() {
			@Override
			public Boolean visit(
					ElkClassInclusionOfInconsistentIndividual other) {
				return equals(other.getInconsistent(),
						inference.getInconsistent());
			}
		});
	}

	@Override
	public Boolean visit(
			final ElkClassInclusionOfObjectPropertyAssertion inference) {
		return other_.accept(new DefaultVisitor() {
			@Override
			public Boolean visit(
					ElkClassInclusionOfObjectPropertyAssertion other) {
				return equals(other.getSubject(), inference.getSubject())
						&& equals(other.getProperty(), inference.getProperty())
						&& equals(other.getObject(), inference.getObject());
			}
		});
	}

	@Override
	public Boolean visit(
			final ElkClassInclusionOfObjectPropertyDomain inference) {
		return other_.accept(new DefaultVisitor() {
			@Override
			public Boolean visit(
					ElkClassInclusionOfObjectPropertyDomain other) {
				return equals(other.getProperty(), inference.getProperty())
						&& equals(other.getDomain(), inference.getDomain());
			}
		});
	}

	@Override
	public Boolean visit(
			final ElkClassInclusionOfReflexiveObjectProperty inference) {
		return other_.accept(new DefaultVisitor() {
			@Override
			public Boolean visit(
					ElkClassInclusionOfReflexiveObjectProperty other) {
				return equals(other.getProperty(), inference.getProperty());
			}
		});
	}

	@Override
	public Boolean visit(final ElkClassInclusionOwlNothing inference) {
		return other_.accept(new DefaultVisitor() {
			@Override
			public Boolean visit(ElkClassInclusionOwlNothing other) {
				return equals(other.getSuperClass(), inference.getSuperClass());
			}
		});
	}

	@Override
	public Boolean visit(final ElkClassInclusionOwlThing inference) {
		return other_.accept(new DefaultVisitor() {
			@Override
			public Boolean visit(ElkClassInclusionOwlThing other) {
				return equals(other.getSubClass(), inference.getSubClass());
			}
		});
	}

	@Override
	public Boolean visit(
			final ElkClassInclusionOwlThingEmptyObjectIntersectionOf inference) {
		return other_.accept(new DefaultVisitor() {
			@Override
			public Boolean visit(
					ElkClassInclusionOwlThingEmptyObjectIntersectionOf other) {
				return true;
			}
		});
	}

	@Override
	public Boolean visit(
			final ElkClassInclusionReflexivePropertyRange inference) {
		return other_.accept(new DefaultVisitor() {
			@Override
			public Boolean visit(
					ElkClassInclusionReflexivePropertyRange other) {
				return equals(other.getSubClass(), inference.getSubClass())
						&& equals(other.getProperty(), inference.getProperty())
						&& equals(other.getRange(), inference.getRange());
			}
		});
	}

	@Override
	public Boolean visit(
			final ElkClassInclusionSingletonObjectUnionOfDecomposition inference) {
		return other_.accept(new DefaultVisitor() {
			@Override
			public Boolean visit(
					ElkClassInclusionSingletonObjectUnionOfDecomposition other) {
				return equals(other.getDisjunct(), inference.getDisjunct());
			}
		});
	}

	@Override
	public Boolean visit(final ElkClassInclusionTautology inference) {
		return other_.accept(new DefaultVisitor() {
			@Override
			public Boolean visit(ElkClassInclusionTautology other) {
				return equals(other.getExpression(), inference.getExpression());
			}
		});
	}

	@Override
	public Boolean visit(
			final ElkDisjointClassesOfDifferentIndividuals inference) {
		return other_.accept(new DefaultVisitor() {
			@Override
			public Boolean visit(
					ElkDisjointClassesOfDifferentIndividuals other) {
				return equals(other.getDifferent(), inference.getDifferent());
			}
		});
	}

	@Override
	public Boolean visit(final ElkDisjointClassesOfDisjointUnion inference) {
		return other_.accept(new DefaultVisitor() {
			@Override
			public Boolean visit(ElkDisjointClassesOfDisjointUnion other) {
				return equals(other.getDefined(), inference.getDefined())
						&& equals(other.getDisjoint(), inference.getDisjoint());
			}
		});
	}

	@Override
	public Boolean visit(final ElkEquivalentClassesCycle inference) {
		return other_.accept(new DefaultVisitor() {
			@Override
			public Boolean visit(ElkEquivalentClassesCycle other) {
				return equals(other.getExpressions(),
						inference.getExpressions());
			}
		});
	}

	@Override
	public Boolean visit(final ElkEquivalentClassesObjectHasValue inference) {
		return other_.accept(new DefaultVisitor() {
			@Override
			public Boolean visit(ElkEquivalentClassesObjectHasValue other) {
				return equals(other.getProperty(), inference.getProperty())
						&& equals(other.getValue(), inference.getValue());
			}
		});
	}

	@Override
	public Boolean visit(final ElkEquivalentClassesObjectOneOf inference) {
		return other_.accept(new DefaultVisitor() {
			@Override
			public Boolean visit(ElkEquivalentClassesObjectOneOf other) {
				return equals(other.getMembers(), inference.getMembers());
			}
		});
	}

	@Override
	public Boolean visit(final ElkEquivalentClassesOfDisjointUnion inference) {
		return other_.accept(new DefaultVisitor() {
			@Override
			public Boolean visit(ElkEquivalentClassesOfDisjointUnion other) {
				return equals(other.getDefined(), inference.getDefined())
						&& equals(other.getDisjoint(), inference.getDisjoint());
			}
		});
	}

	@Override
	public Boolean visit(final ElkEquivalentClassesOfSameIndividual inference) {
		return other_.accept(new DefaultVisitor() {
			@Override
			public Boolean visit(ElkEquivalentClassesOfSameIndividual other) {
				return equals(other.getSame(), inference.getSame());
			}
		});
	}

	@Override
	public Boolean visit(final ElkPropertyInclusionHierarchy inference) {
		return other_.accept(new DefaultVisitor() {
			@Override
			public Boolean visit(ElkPropertyInclusionHierarchy other) {
				return equals(other.getSubExpression(),
						inference.getSubExpression())
						&& equals(other.getExpressions(),
								inference.getExpressions());
			}
		});
	}

	@Override
	public Boolean visit(final ElkPropertyInclusionOfEquivalence inference) {
		return other_.accept(new DefaultVisitor() {
			@Override
			public Boolean visit(ElkPropertyInclusionOfEquivalence other) {
				return equals(other.getExpressions(),
						inference.getExpressions())
						&& equals(other.getSubPos(), inference.getSubPos())
						&& equals(other.getSuperPos(), inference.getSuperPos());
			}
		});
	}

	@Override
	public Boolean visit(
			final ElkPropertyInclusionOfTransitiveObjectProperty inference) {
		return other_.accept(new DefaultVisitor() {
			@Override
			public Boolean visit(
					ElkPropertyInclusionOfTransitiveObjectProperty other) {
				return equals(other.getProperty(), inference.getProperty());
			}
		});
	}

	@Override
	public Boolean visit(final ElkPropertyInclusionTautology inference) {
		return other_.accept(new DefaultVisitor() {
			@Override
			public Boolean visit(ElkPropertyInclusionTautology other) {
				return equals(other.getExpression(), inference.getExpression());
			}
		});
	}

	@Override
	public Boolean visit(final ElkPropertyRangePropertyExpansion inference) {
		return other_.accept(new DefaultVisitor() {
			@Override
			public Boolean visit(ElkPropertyRangePropertyExpansion other) {
				return equals(other.getSubProperty(),
						inference.getSubProperty())
						&& equals(other.getSuperProperty(),
								inference.getSuperProperty())
						&& equals(other.getRange(), inference.getRange());
			}
		});
	}

	@Override
	public Boolean visit(final ElkToldAxiom inference) {
		return other_.accept(new DefaultVisitor() {
			@Override
			public Boolean visit(ElkToldAxiom other) {
				return equals(other.getAxiom(), inference.getAxiom());
			}
		});
	}

}
