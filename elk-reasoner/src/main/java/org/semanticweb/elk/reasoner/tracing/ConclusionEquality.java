package org.semanticweb.elk.reasoner.tracing;

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

import org.semanticweb.elk.owl.comparison.ElkObjectEquality;
import org.semanticweb.elk.owl.interfaces.ElkObject;
import org.semanticweb.elk.reasoner.indexing.model.IndexedDeclarationAxiom;
import org.semanticweb.elk.reasoner.indexing.model.IndexedDefinitionAxiom;
import org.semanticweb.elk.reasoner.indexing.model.IndexedDisjointClassesAxiom;
import org.semanticweb.elk.reasoner.indexing.model.IndexedObject;
import org.semanticweb.elk.reasoner.indexing.model.IndexedObjectPropertyRangeAxiom;
import org.semanticweb.elk.reasoner.indexing.model.IndexedSubClassOfAxiom;
import org.semanticweb.elk.reasoner.indexing.model.IndexedSubObjectPropertyOfAxiom;
import org.semanticweb.elk.reasoner.saturation.conclusions.model.BackwardLink;
import org.semanticweb.elk.reasoner.saturation.conclusions.model.ContextInitialization;
import org.semanticweb.elk.reasoner.saturation.conclusions.model.Contradiction;
import org.semanticweb.elk.reasoner.saturation.conclusions.model.DisjointSubsumer;
import org.semanticweb.elk.reasoner.saturation.conclusions.model.ForwardLink;
import org.semanticweb.elk.reasoner.saturation.conclusions.model.Propagation;
import org.semanticweb.elk.reasoner.saturation.conclusions.model.PropertyRange;
import org.semanticweb.elk.reasoner.saturation.conclusions.model.SubClassInclusionComposed;
import org.semanticweb.elk.reasoner.saturation.conclusions.model.SubClassInclusionDecomposed;
import org.semanticweb.elk.reasoner.saturation.conclusions.model.SubContextInitialization;
import org.semanticweb.elk.reasoner.saturation.conclusions.model.SubPropertyChain;

public class ConclusionEquality implements Conclusion.Visitor<Boolean> {

	public static boolean equals(Conclusion first, Conclusion second) {
		return first.accept(new ConclusionEquality(second));
	}

	private static class DefaultConclusionVisitor
			extends
				DummyConclusionVisitor<Boolean> {

		@Override
		protected Boolean defaultVisit(Conclusion conclusion) {
			return false;
		}

		static boolean equals(ElkObject first, ElkObject second) {
			return (ElkObjectEquality.equals(first, second));
		}

		static boolean equals(IndexedObject first, IndexedObject second) {
			return first == second;
		}

		static boolean equals(int first, int second) {
			return first == second;
		}

	}

	private final Conclusion other_;

	private ConclusionEquality(Conclusion other) {
		this.other_ = other;
	}

	@Override
	public Boolean visit(final BackwardLink conclusion) {
		return other_.accept(new DefaultConclusionVisitor() {
			@Override
			public Boolean visit(BackwardLink other) {
				return equals(other.getDestination(),
						conclusion.getDestination())
						&& equals(other.getDestinationSubRoot(),
								conclusion.getDestinationSubRoot())
						&& equals(other.getTraceRoot(),
								conclusion.getTraceRoot());
			}
		});
	}

	@Override
	public Boolean visit(final ContextInitialization conclusion) {
		return other_.accept(new DefaultConclusionVisitor() {
			@Override
			public Boolean visit(ContextInitialization other) {
				return equals(other.getDestination(),
						conclusion.getDestination());
			}
		});
	}

	@Override
	public Boolean visit(final Contradiction conclusion) {
		return other_.accept(new DefaultConclusionVisitor() {
			@Override
			public Boolean visit(Contradiction other) {
				return equals(other.getDestination(),
						conclusion.getDestination());
			}
		});
	}

	@Override
	public Boolean visit(final DisjointSubsumer conclusion) {
		return other_.accept(new DefaultConclusionVisitor() {
			@Override
			public Boolean visit(DisjointSubsumer other) {
				return equals(other.getDestination(),
						conclusion.getDestination())
						&& equals(other.getDisjointExpressions(),
								conclusion.getDisjointExpressions())
						&& equals(other.getPosition(), conclusion.getPosition())
						&& equals(other.getReason(), conclusion.getReason());
			}
		});
	}

	@Override
	public Boolean visit(final ForwardLink conclusion) {
		return other_.accept(new DefaultConclusionVisitor() {
			@Override
			public Boolean visit(ForwardLink other) {
				return equals(other.getDestination(),
						conclusion.getDestination())
						&& equals(other.getForwardChain(),
								conclusion.getForwardChain())
						&& equals(other.getTarget(), conclusion.getTarget());
			}
		});
	}

	@Override
	public Boolean visit(final IndexedDeclarationAxiom conclusion) {
		return other_.accept(new DefaultConclusionVisitor() {
			@Override
			public Boolean visit(IndexedDeclarationAxiom other) {
				return equals(other.getEntity(), conclusion.getEntity());
			}
		});
	}

	@Override
	public Boolean visit(final IndexedDefinitionAxiom conclusion) {
		return other_.accept(new DefaultConclusionVisitor() {
			@Override
			public Boolean visit(IndexedDefinitionAxiom other) {
				return equals(other.getDefinedClass(),
						conclusion.getDefinedClass())
						&& equals(other.getDefinition(),
								conclusion.getDefinition());
			}
		});
	}

	@Override
	public Boolean visit(final IndexedDisjointClassesAxiom conclusion) {
		return other_.accept(new DefaultConclusionVisitor() {
			@Override
			public Boolean visit(IndexedDisjointClassesAxiom other) {
				return equals(other.getMembers(), conclusion.getMembers());
			}
		});
	}

	@Override
	public Boolean visit(final IndexedObjectPropertyRangeAxiom conclusion) {
		return other_.accept(new DefaultConclusionVisitor() {
			@Override
			public Boolean visit(IndexedObjectPropertyRangeAxiom other) {
				return equals(other.getProperty(), conclusion.getProperty())
						&& equals(other.getRange(), conclusion.getRange());
			}
		});
	}

	@Override
	public Boolean visit(final IndexedSubClassOfAxiom conclusion) {
		return other_.accept(new DefaultConclusionVisitor() {
			@Override
			public Boolean visit(IndexedSubClassOfAxiom other) {
				return equals(other.getSubClass(), conclusion.getSubClass())
						&& equals(other.getSuperClass(),
								conclusion.getSuperClass());
			}
		});
	}

	@Override
	public Boolean visit(final IndexedSubObjectPropertyOfAxiom conclusion) {
		return other_.accept(new DefaultConclusionVisitor() {
			@Override
			public Boolean visit(IndexedSubObjectPropertyOfAxiom other) {
				return equals(other.getSubPropertyChain(),
						conclusion.getSubPropertyChain())
						&& equals(other.getSuperProperty(),
								conclusion.getSuperProperty());
			}
		});
	}

	@Override
	public Boolean visit(final Propagation subConclusion) {
		return other_.accept(new DefaultConclusionVisitor() {
			@Override
			public Boolean visit(Propagation other) {
				return equals(other.getDestination(),
						subConclusion.getDestination())
						&& equals(other.getDestinationSubRoot(),
								subConclusion.getDestinationSubRoot())
						&& equals(other.getCarry(), subConclusion.getCarry());
			}
		});
	}

	@Override
	public Boolean visit(final PropertyRange conclusion) {
		return other_.accept(new DefaultConclusionVisitor() {
			@Override
			public Boolean visit(PropertyRange other) {
				return equals(other.getProperty(), conclusion.getProperty())
						&& equals(other.getRange(), conclusion.getRange());
			}
		});
	}

	@Override
	public Boolean visit(final SubClassInclusionComposed conclusion) {
		return other_.accept(new DefaultConclusionVisitor() {
			@Override
			public Boolean visit(SubClassInclusionComposed other) {
				return equals(other.getDestination(),
						conclusion.getDestination())
						&& equals(other.getSuperExpression(),
								conclusion.getSuperExpression());
			}
		});
	}

	@Override
	public Boolean visit(final SubClassInclusionDecomposed conclusion) {
		return other_.accept(new DefaultConclusionVisitor() {
			@Override
			public Boolean visit(SubClassInclusionDecomposed other) {
				return equals(other.getDestination(),
						conclusion.getDestination())
						&& equals(other.getSuperExpression(),
								conclusion.getSuperExpression());
			}
		});
	}

	@Override
	public Boolean visit(final SubContextInitialization subConclusion) {
		return other_.accept(new DefaultConclusionVisitor() {
			@Override
			public Boolean visit(SubContextInitialization other) {
				return equals(other.getDestination(),
						subConclusion.getDestination())
						&& equals(other.getDestinationSubRoot(),
								subConclusion.getDestinationSubRoot());
			}
		});
	}

	@Override
	public Boolean visit(final SubPropertyChain conclusion) {
		return other_.accept(new DefaultConclusionVisitor() {
			@Override
			public Boolean visit(SubPropertyChain other) {
				return equals(other.getSubChain(), conclusion.getSubChain())
						&& equals(other.getSuperChain(),
								conclusion.getSuperChain());
			}
		});
	}

}
