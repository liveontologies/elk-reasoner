package org.semanticweb.elk.owlapi;

/*
 * #%L
 * ELK OWL API Binding
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
import java.util.Set;

import org.semanticweb.owlapi.model.AxiomType;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLClassExpression;
import org.semanticweb.owlapi.model.OWLDataProperty;
import org.semanticweb.owlapi.model.OWLDataPropertyExpression;
import org.semanticweb.owlapi.model.OWLLiteral;
import org.semanticweb.owlapi.model.OWLNamedIndividual;
import org.semanticweb.owlapi.model.OWLObjectPropertyExpression;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyChange;
import org.semanticweb.owlapi.reasoner.BufferingMode;
import org.semanticweb.owlapi.reasoner.FreshEntityPolicy;
import org.semanticweb.owlapi.reasoner.IndividualNodeSetPolicy;
import org.semanticweb.owlapi.reasoner.InferenceType;
import org.semanticweb.owlapi.reasoner.Node;
import org.semanticweb.owlapi.reasoner.NodeSet;
import org.semanticweb.owlapi.reasoner.OWLReasoner;
import org.semanticweb.owlapi.util.Version;

class DelegatingOWLReasoner<R extends OWLReasoner> implements OWLReasoner {

	private final R delegate_;

	DelegatingOWLReasoner(R delegate) {
		this.delegate_ = delegate;
	}

	public R getDelegate() {
		return delegate_;
	}

	@Override
	public String getReasonerName() {
		return delegate_.getReasonerName();
	}

	@Override
	public Version getReasonerVersion() {
		return delegate_.getReasonerVersion();
	}

	@Override
	public BufferingMode getBufferingMode() {
		return delegate_.getBufferingMode();
	}

	@Override
	public void flush() {
		delegate_.flush();
	}

	@Override
	public List<OWLOntologyChange> getPendingChanges() {
		return delegate_.getPendingChanges();
	}

	@Override
	public Set<OWLAxiom> getPendingAxiomAdditions() {
		return delegate_.getPendingAxiomAdditions();
	}

	@Override
	public Set<OWLAxiom> getPendingAxiomRemovals() {
		return delegate_.getPendingAxiomRemovals();
	}

	@Override
	public OWLOntology getRootOntology() {
		return delegate_.getRootOntology();
	}

	@Override
	public void interrupt() {
		delegate_.interrupt();
	}

	@Override
	public void precomputeInferences(InferenceType... inferenceTypes) {
		delegate_.precomputeInferences(inferenceTypes);
	}

	@Override
	public boolean isPrecomputed(InferenceType inferenceType) {
		return delegate_.isPrecomputed(inferenceType);
	}

	@Override
	public Set<InferenceType> getPrecomputableInferenceTypes() {
		return delegate_.getPrecomputableInferenceTypes();
	}

	@Override
	public boolean isConsistent() {
		return delegate_.isConsistent();
	}

	@Override
	public boolean isSatisfiable(OWLClassExpression classExpression) {
		return delegate_.isSatisfiable(classExpression);
	}

	@Override
	public Node<OWLClass> getUnsatisfiableClasses() {
		return delegate_.getUnsatisfiableClasses();
	}

	@Override
	public boolean isEntailed(OWLAxiom axiom) {
		return delegate_.isEntailed(axiom);
	}

	@Override
	public boolean isEntailed(Set<? extends OWLAxiom> axioms) {
		return delegate_.isEntailed(axioms);
	}

	@Override
	public boolean isEntailmentCheckingSupported(AxiomType<?> axiomType) {
		return delegate_.isEntailmentCheckingSupported(axiomType);
	}

	@Override
	public Node<OWLClass> getTopClassNode() {
		return delegate_.getTopClassNode();
	}

	@Override
	public Node<OWLClass> getBottomClassNode() {
		return delegate_.getBottomClassNode();
	}

	@Override
	public NodeSet<OWLClass> getSubClasses(OWLClassExpression ce,
			boolean direct) {
		return delegate_.getSubClasses(ce, direct);
	}

	@Override
	public NodeSet<OWLClass> getSuperClasses(OWLClassExpression ce,
			boolean direct) {
		return delegate_.getSuperClasses(ce, direct);
	}

	@Override
	public Node<OWLClass> getEquivalentClasses(OWLClassExpression ce) {
		return delegate_.getEquivalentClasses(ce);
	}

	@Override
	public NodeSet<OWLClass> getDisjointClasses(OWLClassExpression ce) {
		return delegate_.getDisjointClasses(ce);
	}

	@Override
	public Node<OWLObjectPropertyExpression> getTopObjectPropertyNode() {
		return delegate_.getTopObjectPropertyNode();
	}

	@Override
	public Node<OWLObjectPropertyExpression> getBottomObjectPropertyNode() {
		return delegate_.getBottomObjectPropertyNode();
	}

	@Override
	public NodeSet<OWLObjectPropertyExpression> getSubObjectProperties(
			OWLObjectPropertyExpression pe, boolean direct) {
		return delegate_.getSubObjectProperties(pe, direct);
	}

	@Override
	public NodeSet<OWLObjectPropertyExpression> getSuperObjectProperties(
			OWLObjectPropertyExpression pe, boolean direct) {
		return delegate_.getSuperObjectProperties(pe, direct);
	}

	@Override
	public Node<OWLObjectPropertyExpression> getEquivalentObjectProperties(
			OWLObjectPropertyExpression pe) {
		return delegate_.getEquivalentObjectProperties(pe);
	}

	@Override
	public NodeSet<OWLObjectPropertyExpression> getDisjointObjectProperties(
			OWLObjectPropertyExpression pe) {
		return delegate_.getDisjointObjectProperties(pe);
	}

	@Override
	public Node<OWLObjectPropertyExpression> getInverseObjectProperties(
			OWLObjectPropertyExpression pe) {
		return delegate_.getInverseObjectProperties(pe);
	}

	@Override
	public NodeSet<OWLClass> getObjectPropertyDomains(
			OWLObjectPropertyExpression pe, boolean direct) {
		return delegate_.getObjectPropertyDomains(pe, direct);
	}

	@Override
	public NodeSet<OWLClass> getObjectPropertyRanges(
			OWLObjectPropertyExpression pe, boolean direct) {
		return delegate_.getObjectPropertyRanges(pe, direct);
	}

	@Override
	public Node<OWLDataProperty> getTopDataPropertyNode() {
		return delegate_.getTopDataPropertyNode();
	}

	@Override
	public Node<OWLDataProperty> getBottomDataPropertyNode() {
		return delegate_.getBottomDataPropertyNode();
	}

	@Override
	public NodeSet<OWLDataProperty> getSubDataProperties(OWLDataProperty pe,
			boolean direct) {
		return delegate_.getSubDataProperties(pe, direct);
	}

	@Override
	public NodeSet<OWLDataProperty> getSuperDataProperties(OWLDataProperty pe,
			boolean direct) {
		return delegate_.getSuperDataProperties(pe, direct);
	}

	@Override
	public Node<OWLDataProperty> getEquivalentDataProperties(
			OWLDataProperty pe) {
		return delegate_.getEquivalentDataProperties(pe);
	}

	@Override
	public NodeSet<OWLDataProperty> getDisjointDataProperties(
			OWLDataPropertyExpression pe) {
		return delegate_.getDisjointDataProperties(pe);
	}

	@Override
	public NodeSet<OWLClass> getDataPropertyDomains(OWLDataProperty pe,
			boolean direct) {
		return delegate_.getDataPropertyDomains(pe, direct);
	}

	@Override
	public NodeSet<OWLClass> getTypes(OWLNamedIndividual ind, boolean direct) {
		return delegate_.getTypes(ind, direct);
	}

	@Override
	public NodeSet<OWLNamedIndividual> getInstances(OWLClassExpression ce,
			boolean direct) {
		return delegate_.getInstances(ce, direct);
	}

	@Override
	public NodeSet<OWLNamedIndividual> getObjectPropertyValues(
			OWLNamedIndividual ind, OWLObjectPropertyExpression pe) {
		return delegate_.getObjectPropertyValues(ind, pe);
	}

	@Override
	public Set<OWLLiteral> getDataPropertyValues(OWLNamedIndividual ind,
			OWLDataProperty pe) {
		return delegate_.getDataPropertyValues(ind, pe);
	}

	@Override
	public Node<OWLNamedIndividual> getSameIndividuals(OWLNamedIndividual ind) {
		return delegate_.getSameIndividuals(ind);
	}

	@Override
	public NodeSet<OWLNamedIndividual> getDifferentIndividuals(
			OWLNamedIndividual ind) {
		return delegate_.getDifferentIndividuals(ind);
	}

	@Override
	public long getTimeOut() {
		return delegate_.getTimeOut();
	}

	@Override
	public FreshEntityPolicy getFreshEntityPolicy() {
		return delegate_.getFreshEntityPolicy();
	}

	@Override
	public IndividualNodeSetPolicy getIndividualNodeSetPolicy() {
		return delegate_.getIndividualNodeSetPolicy();
	}

	@Override
	public void dispose() {
		delegate_.dispose();
	}

}
