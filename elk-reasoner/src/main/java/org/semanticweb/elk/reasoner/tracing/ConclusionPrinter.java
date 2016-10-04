package org.semanticweb.elk.reasoner.tracing;

import java.util.List;

import org.semanticweb.elk.reasoner.indexing.model.IndexedClassExpression;
import org.semanticweb.elk.reasoner.indexing.model.IndexedDeclarationAxiom;
import org.semanticweb.elk.reasoner.indexing.model.IndexedEquivalentClassesAxiom;
import org.semanticweb.elk.reasoner.indexing.model.IndexedDisjointClassesAxiom;
import org.semanticweb.elk.reasoner.indexing.model.IndexedObjectPropertyRangeAxiom;
import org.semanticweb.elk.reasoner.indexing.model.IndexedSubClassOfAxiom;
import org.semanticweb.elk.reasoner.indexing.model.IndexedSubObjectPropertyOfAxiom;
import org.semanticweb.elk.reasoner.saturation.conclusions.model.BackwardLink;
import org.semanticweb.elk.reasoner.saturation.conclusions.model.ClassInconsistency;
import org.semanticweb.elk.reasoner.saturation.conclusions.model.ContextInitialization;
import org.semanticweb.elk.reasoner.saturation.conclusions.model.DisjointSubsumer;
import org.semanticweb.elk.reasoner.saturation.conclusions.model.ForwardLink;
import org.semanticweb.elk.reasoner.saturation.conclusions.model.Propagation;
import org.semanticweb.elk.reasoner.saturation.conclusions.model.PropertyRange;
import org.semanticweb.elk.reasoner.saturation.conclusions.model.SubClassInclusionComposed;
import org.semanticweb.elk.reasoner.saturation.conclusions.model.SubClassInclusionDecomposed;
import org.semanticweb.elk.reasoner.saturation.conclusions.model.SubContextInitialization;
import org.semanticweb.elk.reasoner.saturation.conclusions.model.SubPropertyChain;

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

public class ConclusionPrinter implements Conclusion.Visitor<String> {

	private static ConclusionPrinter INSTANCE_ = new ConclusionPrinter();

	private ConclusionPrinter() {

	}

	public static String toString(Conclusion conclusion) {
		return conclusion.accept(INSTANCE_);
	}

	static Conclusion.Visitor<String> getPrinterVisitor() {
		return INSTANCE_;
	}

	@Override
	public String visit(BackwardLink conclusion) {
		return String.format("%s ⊑ <∃%s>.[%s]", conclusion.getSource(),
				conclusion.getRelation(), conclusion.getDestination());
	}

	@Override
	public String visit(ContextInitialization conclusion) {
		return String.format("![%s]", conclusion.getDestination());
	}

	@Override
	public String visit(ClassInconsistency conclusion) {
		return String.format("[%s] = 0", conclusion.getDestination());
	}

	@Override
	public String visit(DisjointSubsumer conclusion) {
		List<? extends IndexedClassExpression> members = conclusion
				.getDisjointExpressions().getElements();
		return String.format("[%s] ⊑ %s|%s", conclusion.getDestination(),
				members.get(conclusion.getPosition()), members);
	}

	@Override
	public String visit(ForwardLink conclusion) {
		return String.format("[%s] ⊑ <∃%s>.%s", conclusion.getDestination(),
				conclusion.getChain(), conclusion.getTarget());
	}

	@Override
	public String visit(IndexedDeclarationAxiom conclusion) {
		return String.format("[Declaration(%s)]", conclusion.getEntity());
	}

	@Override
	public String visit(IndexedEquivalentClassesAxiom conclusion) {
		return String.format("[%s = %s]", conclusion.getFirstMember(),
				conclusion.getSecondMember());
	}

	@Override
	public String visit(IndexedDisjointClassesAxiom conclusion) {
		return String.format("[Disjoint(%s)]", conclusion.getMembers());
	}

	@Override
	public String visit(IndexedObjectPropertyRangeAxiom conclusion) {
		return String.format("[Range(%s,%s)]", conclusion.getProperty(),
				conclusion.getRange());
	}

	@Override
	public String visit(IndexedSubClassOfAxiom conclusion) {
		return String.format("[%s ⊑ %s]", conclusion.getSubClass(),
				conclusion.getSuperClass());
	}

	@Override
	public String visit(IndexedSubObjectPropertyOfAxiom conclusion) {
		return String.format("[%s ⊑ %s]", conclusion.getSubPropertyChain(),
				conclusion.getSuperProperty());
	}

	@Override
	public String visit(Propagation conclusion) {
		return String.format("∃[%s].[%s] ⊑ %s", conclusion.getSubDestination(),
				conclusion.getDestination(), conclusion.getCarry());
	}

	@Override
	public String visit(PropertyRange conclusion) {
		return String.format("Range(%s,%s)", conclusion.getProperty(),
				conclusion.getRange());
	}

	@Override
	public String visit(SubClassInclusionComposed conclusion) {
		return String.format("[%s] ⊑ +%s", conclusion.getDestination(),
				conclusion.getSubsumer());
	}

	@Override
	public String visit(SubClassInclusionDecomposed conclusion) {
		return String.format("[%s] ⊑ -%s", conclusion.getDestination(),
				conclusion.getSubsumer());
	}

	@Override
	public String visit(SubContextInitialization conclusion) {
		return String.format("![%s:%s]", conclusion.getDestination(),
				conclusion.getSubDestination());
	}

	@Override
	public String visit(SubPropertyChain conclusion) {
		return String.format("%s ⊑ %s", conclusion.getSubChain(),
				conclusion.getSuperChain());
	}
}
