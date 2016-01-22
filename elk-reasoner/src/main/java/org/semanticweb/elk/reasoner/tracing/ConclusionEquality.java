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

public class ConclusionEquality implements Conclusion.Visitor<Conclusion> {

	public static boolean equals(Conclusion first, Object second) {
		return first == null
				? second == null
				: first.accept(new ConclusionEquality(second)) == second;
	}

	private static boolean equals(ElkObject first, ElkObject second) {
		return (ElkObjectEquality.equals(first, second));
	}

	private static boolean equals(IndexedObject first, IndexedObject second) {
		return first == second;
	}

	private static boolean equals(int first, int second) {
		return first == second;
	}

	private final Object object_;

	private ConclusionEquality(Object object) {
		this.object_ = object;
	}

	@Override
	public BackwardLink visit(BackwardLink subConclusion) {
		if (object_ == subConclusion)
			return subConclusion;
		if (object_ instanceof BackwardLink) {
			BackwardLink result = (BackwardLink) object_;
			if (equals(result.getConclusionRoot(),
					subConclusion.getConclusionRoot())
					&& equals(result.getConclusionSubRoot(),
							subConclusion.getConclusionSubRoot())
					&& equals(result.getOriginRoot(),
							subConclusion.getOriginRoot()))
				return result;
		}
		return null;
	}

	@Override
	public ContextInitialization visit(ContextInitialization conclusion) {
		if (object_ == conclusion)
			return conclusion;
		if (object_ instanceof ContextInitialization) {
			ContextInitialization result = (ContextInitialization) object_;
			if (equals(result.getConclusionRoot(),
					conclusion.getConclusionRoot()))
				return result;
		}
		return null;
	}

	@Override
	public Contradiction visit(Contradiction conclusion) {
		if (object_ == conclusion)
			return conclusion;
		if (object_ instanceof Contradiction) {
			Contradiction result = (Contradiction) object_;
			if (equals(result.getConclusionRoot(),
					conclusion.getConclusionRoot()))
				return result;
		}
		return null;
	}

	@Override
	public DisjointSubsumer visit(DisjointSubsumer conclusion) {
		if (object_ == conclusion)
			return conclusion;
		if (object_ instanceof DisjointSubsumer) {
			DisjointSubsumer result = (DisjointSubsumer) object_;
			if (equals(result.getConclusionRoot(),
					conclusion.getConclusionRoot())
					&& equals(result.getDisjointExpressions(),
							conclusion.getDisjointExpressions())
					&& equals(result.getPosition(), conclusion.getPosition())
					&& equals(result.getReason(), conclusion.getReason()))
				return result;
		}
		return null;
	}

	@Override
	public ForwardLink visit(ForwardLink conclusion) {
		if (object_ == conclusion)
			return conclusion;
		if (object_ instanceof ForwardLink) {
			ForwardLink result = (ForwardLink) object_;
			if (equals(result.getConclusionRoot(),
					conclusion.getConclusionRoot())
					&& equals(result.getForwardChain(),
							conclusion.getForwardChain())
					&& equals(result.getTarget(), conclusion.getTarget()))
				return result;
		}
		return null;
	}

	@Override
	public IndexedDeclarationAxiom visit(IndexedDeclarationAxiom conclusion) {
		if (object_ == conclusion)
			return conclusion;
		if (object_ instanceof IndexedDeclarationAxiom) {
			IndexedDeclarationAxiom result = (IndexedDeclarationAxiom) object_;
			if (equals(result.getEntity(), conclusion.getEntity()))
				return result;
		}
		return null;
	}

	@Override
	public IndexedDefinitionAxiom visit(IndexedDefinitionAxiom conclusion) {
		if (object_ == conclusion)
			return conclusion;
		if (object_ instanceof IndexedDefinitionAxiom) {
			IndexedDefinitionAxiom result = (IndexedDefinitionAxiom) object_;
			if (equals(result.getDefinedClass(), conclusion.getDefinedClass())
					&& equals(result.getDefinition(),
							conclusion.getDefinition()))
				return result;
		}
		return null;
	}

	@Override
	public IndexedDisjointClassesAxiom visit(
			IndexedDisjointClassesAxiom conclusion) {
		if (object_ == conclusion)
			return conclusion;
		if (object_ instanceof IndexedDisjointClassesAxiom) {
			IndexedDisjointClassesAxiom result = (IndexedDisjointClassesAxiom) object_;
			if (equals(result.getMembers(), conclusion.getMembers()))
				return result;
		}
		return null;
	}

	@Override
	public IndexedObjectPropertyRangeAxiom visit(
			IndexedObjectPropertyRangeAxiom conclusion) {
		if (object_ == conclusion)
			return conclusion;
		if (object_ instanceof IndexedObjectPropertyRangeAxiom) {
			IndexedObjectPropertyRangeAxiom result = (IndexedObjectPropertyRangeAxiom) object_;
			if (equals(result.getProperty(), conclusion.getProperty())
					&& equals(result.getRange(), conclusion.getRange()))
				return result;
		}
		return null;
	}

	@Override
	public IndexedSubClassOfAxiom visit(IndexedSubClassOfAxiom conclusion) {
		if (object_ == conclusion)
			return conclusion;
		if (object_ instanceof IndexedSubClassOfAxiom) {
			IndexedSubClassOfAxiom result = (IndexedSubClassOfAxiom) object_;
			if (equals(result.getSubClass(), conclusion.getSubClass())
					&& equals(result.getSuperClass(),
							conclusion.getSuperClass()))
				return result;
		}
		return null;
	}

	@Override
	public IndexedSubObjectPropertyOfAxiom visit(
			IndexedSubObjectPropertyOfAxiom conclusion) {
		if (object_ == conclusion)
			return conclusion;
		if (object_ instanceof IndexedSubObjectPropertyOfAxiom) {
			IndexedSubObjectPropertyOfAxiom result = (IndexedSubObjectPropertyOfAxiom) object_;
			if (equals(result.getSubPropertyChain(),
					conclusion.getSubPropertyChain())
					&& equals(result.getSuperProperty(),
							conclusion.getSuperProperty()))
				return result;
		}
		return null;
	}

	@Override
	public Propagation visit(Propagation subConclusion) {
		if (object_ == subConclusion)
			return subConclusion;
		if (object_ instanceof Propagation) {
			Propagation result = (Propagation) object_;
			if (equals(result.getConclusionRoot(),
					subConclusion.getConclusionRoot())
					&& equals(result.getConclusionSubRoot(),
							subConclusion.getConclusionSubRoot())
					&& equals(result.getCarry(), subConclusion.getCarry()))
				return result;
		}
		return null;
	}

	@Override
	public Conclusion visit(PropertyRange conclusion) {
		if (object_ == conclusion)
			return conclusion;
		if (object_ instanceof PropertyRange) {
			PropertyRange result = (PropertyRange) object_;
			if (equals(result.getProperty(), conclusion.getProperty())
					&& equals(result.getRange(), conclusion.getRange()))
				return result;
		}
		return null;
	}

	@Override
	public SubClassInclusionComposed visit(
			SubClassInclusionComposed conclusion) {
		if (object_ == conclusion)
			return conclusion;
		if (object_ instanceof SubClassInclusionComposed) {
			SubClassInclusionComposed result = (SubClassInclusionComposed) object_;
			if (equals(result.getConclusionRoot(),
					conclusion.getConclusionRoot())
					&& equals(result.getSuperExpression(),
							conclusion.getSuperExpression()))
				return result;
		}
		return null;
	}

	@Override
	public SubClassInclusionDecomposed visit(
			SubClassInclusionDecomposed conclusion) {
		if (object_ == conclusion)
			return conclusion;
		if (object_ instanceof SubClassInclusionDecomposed) {
			SubClassInclusionDecomposed result = (SubClassInclusionDecomposed) object_;
			if (equals(result.getConclusionRoot(),
					conclusion.getConclusionRoot())
					&& equals(result.getSuperExpression(),
							conclusion.getSuperExpression()))
				return result;
		}
		return null;
	}

	@Override
	public SubContextInitialization visit(
			SubContextInitialization subConclusion) {
		if (object_ == subConclusion)
			return subConclusion;
		if (object_ instanceof SubContextInitialization) {
			SubContextInitialization result = (SubContextInitialization) object_;
			if (equals(result.getConclusionRoot(),
					subConclusion.getConclusionRoot())
					&& equals(result.getConclusionSubRoot(),
							subConclusion.getConclusionSubRoot()))
				return result;
		}
		return null;
	}

	@Override
	public SubPropertyChain visit(SubPropertyChain conclusion) {
		if (object_ == conclusion)
			return conclusion;
		if (object_ instanceof SubPropertyChain) {
			SubPropertyChain result = (SubPropertyChain) object_;
			if (equals(result.getSubChain(), conclusion.getSubChain())
					&& equals(result.getSuperChain(),
							conclusion.getSuperChain()))
				return result;
		}
		return null;
	}

}
