package org.semanticweb.elk.reasoner.indexing.classes;

/*
 * #%L
 * ELK Reasoner
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

import org.semanticweb.elk.owl.interfaces.ElkClass;
import org.semanticweb.elk.owl.predefined.PredefinedElkIris;
import org.semanticweb.elk.reasoner.indexing.model.IndexedClass;
import org.semanticweb.elk.reasoner.indexing.model.IndexedClassExpressionList;
import org.semanticweb.elk.reasoner.indexing.model.IndexedComplexPropertyChain;
import org.semanticweb.elk.reasoner.indexing.model.IndexedDataHasValue;
import org.semanticweb.elk.reasoner.indexing.model.IndexedDeclarationAxiom;
import org.semanticweb.elk.reasoner.indexing.model.IndexedDefinitionAxiom;
import org.semanticweb.elk.reasoner.indexing.model.IndexedDisjointClassesAxiom;
import org.semanticweb.elk.reasoner.indexing.model.IndexedIndividual;
import org.semanticweb.elk.reasoner.indexing.model.IndexedObject;
import org.semanticweb.elk.reasoner.indexing.model.IndexedObjectComplementOf;
import org.semanticweb.elk.reasoner.indexing.model.IndexedObjectHasSelf;
import org.semanticweb.elk.reasoner.indexing.model.IndexedObjectIntersectionOf;
import org.semanticweb.elk.reasoner.indexing.model.IndexedObjectProperty;
import org.semanticweb.elk.reasoner.indexing.model.IndexedObjectPropertyRangeAxiom;
import org.semanticweb.elk.reasoner.indexing.model.IndexedObjectSomeValuesFrom;
import org.semanticweb.elk.reasoner.indexing.model.IndexedObjectUnionOf;
import org.semanticweb.elk.reasoner.indexing.model.IndexedRangeFiller;
import org.semanticweb.elk.reasoner.indexing.model.IndexedSubClassOfAxiom;
import org.semanticweb.elk.reasoner.indexing.model.IndexedSubObjectPropertyOfAxiom;

public class IndexedObjectPrinter implements IndexedObject.Visitor<String> {

	private static IndexedObjectPrinter INSTANCE_ = new IndexedObjectPrinter();

	static IndexedObject.Visitor<String> getPrinterVisitor() {
		return INSTANCE_;
	}

	public static String toString(IndexedObject element) {
		return element.accept(INSTANCE_);
	}

	private IndexedObjectPrinter() {

	}

	// TODO: parentheses and precedence of symbols

	@Override
	public String visit(IndexedClass element) {
		ElkClass entity = element.getElkEntity();
		if (entity.getIri().equals(PredefinedElkIris.OWL_THING)) {
			return "⊤";
		}
		// else
		if (entity.getIri().equals(PredefinedElkIris.OWL_NOTHING)) {
			return "⊥";
		}
		// else
		return entity.toString();
	}

	@Override
	public String visit(IndexedClassExpressionList element) {
		return element.getElements().toString();
	}

	@Override
	public String visit(IndexedComplexPropertyChain element) {
		return element.getFirstProperty() + "∘" + element.getSuffixChain();
	}

	@Override
	public String visit(IndexedDataHasValue element) {
		return "∃" + element.getRelation() + '.' + element.getFiller();
	}

	@Override
	public String visit(IndexedDeclarationAxiom axiom) {
		return axiom.getEntity().getElkEntity().getEntityType() + "("
				+ axiom.getEntity() + ")";
	}

	@Override
	public String visit(IndexedDefinitionAxiom axiom) {
		return axiom.getDefinedClass() + " = " + axiom.getDefinition();
	}

	@Override
	public String visit(IndexedDisjointClassesAxiom axiom) {
		return "Disjoint(" + axiom.getMembers() + ")";
	}

	@Override
	public String visit(IndexedIndividual element) {
		return "{" + element.getElkEntity() + "}";
	}

	@Override
	public String visit(IndexedObjectComplementOf element) {
		return "¬" + element.getNegated();
	}

	@Override
	public String visit(IndexedObjectHasSelf element) {
		return "∃" + element.getProperty() + ".Self";
	}

	@Override
	public String visit(IndexedObjectIntersectionOf element) {
		return element.getFirstConjunct() + " ⊓ " + element.getSecondConjunct();
	}

	@Override
	public String visit(IndexedObjectProperty element) {
		return element.getElkEntity().getIri().toString();
	}

	@Override
	public String visit(IndexedObjectPropertyRangeAxiom axiom) {
		return "Range(" + axiom.getProperty() + ' ' + axiom.getRange() + ')';
	}

	@Override
	public String visit(IndexedObjectSomeValuesFrom element) {
		return "∃" + element.getProperty() + '.' + element.getFiller();
	}

	@Override
	public String visit(IndexedObjectUnionOf element) {
		return "ObjectUnionOf(" + element.getDisjuncts() + ')';
	}

	@Override
	public String visit(IndexedRangeFiller element) {
		return element.getFiller() + " ⊓ " + "∃" + element.getProperty() + "-"
				+ ".⊤";
	}

	@Override
	public String visit(IndexedSubClassOfAxiom axiom) {
		return axiom.getSubClass() + " ⊑ " + axiom.getSuperClass();
	}

	@Override
	public String visit(IndexedSubObjectPropertyOfAxiom axiom) {
		return axiom.getSubPropertyChain() + " ⊑ " + axiom.getSuperProperty();
	}

}
