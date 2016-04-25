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

import java.util.List;

import org.semanticweb.elk.owl.comparison.ElkObjectEquality;
import org.semanticweb.elk.owl.interfaces.ElkObject;

public class ElkInferenceEquality implements ElkInference.Visitor<Boolean> {

	private final ElkInference other_;

	ElkInferenceEquality(ElkInference other) {
		this.other_ = other;
	}

	private static class DefaultVisitor
			extends ElkInferenceDummyVisitor<Boolean> {

		@Override
		protected Boolean defaultVisit(ElkInference conclusion) {
			return false;
		}

		static boolean equals(ElkObject first, ElkObject second) {
			return (ElkObjectEquality.equals(first, second));
		}

		static boolean equals(List<? extends ElkObject> first,
				List<? extends ElkObject> second) {
			return (ElkObjectEquality.equals(first, second));
		}

		static boolean equals(int first, int second) {
			return first == second;
		}

	}

	public static boolean equals(ElkInference first, ElkInference second) {
		return first.accept(new ElkInferenceEquality(second));
	}

	@Override
	public Boolean visit(
			final ElkClassInclusionExistentialFillerExpansion inference) {
		return other_.accept(new DefaultVisitor() {
			@Override
			public Boolean visit(
					ElkClassInclusionExistentialFillerExpansion other) {
				return equals(other.getSubClass(), inference.getSubClass())
						&& equals(other.getProperty(), inference.getProperty())
						&& equals(other.getSubFiller(),
								inference.getSubFiller())
						&& equals(other.getSuperFiller(),
								inference.getSuperFiller());
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
				return equals(other.getSubExpression(),
						inference.getSubExpression())
						&& equals(other.getConjuncts(),
								inference.getConjuncts())
						&& equals(other.getConjunctPos(),
								inference.getConjunctPos());
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
				return equals(other.getSubExpression(),
						inference.getSubExpression())
						&& equals(other.getDisjuncts(),
								inference.getDisjuncts())
						&& equals(other.getDisjunctPos(),
								inference.getDisjunctPos());
			}
		});
	}

	@Override
	public Boolean visit(final ElkClassInclusionOfEquivalence inference) {
		return other_.accept(new DefaultVisitor() {
			@Override
			public Boolean visit(ElkClassInclusionOfEquivalence other) {
				return equals(other.getExpressions(),
						inference.getExpressions())
						&& equals(other.getSubPos(), inference.getSubPos())
						&& equals(other.getSuperPos(), inference.getSuperPos());
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
	public Boolean visit(final ElkClassInclusionTautology inference) {
		return other_.accept(new DefaultVisitor() {
			@Override
			public Boolean visit(ElkClassInclusionTautology other) {
				return equals(other.getExpression(), inference.getExpression());
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

}
