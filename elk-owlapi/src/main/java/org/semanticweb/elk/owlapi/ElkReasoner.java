/*
 * #%L
 * elk-reasoner
 * 
 * $Id$
 * $HeadURL$
 * %%
 * Copyright (C) 2011 Department of Computer Science, University of Oxford
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
/**
 * @author Yevgeny Kazakov, Jun 28, 2011
 */
package org.semanticweb.elk.owlapi;

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
import org.semanticweb.owlapi.reasoner.AxiomNotInProfileException;
import org.semanticweb.owlapi.reasoner.BufferingMode;
import org.semanticweb.owlapi.reasoner.ClassExpressionNotInProfileException;
import org.semanticweb.owlapi.reasoner.FreshEntitiesException;
import org.semanticweb.owlapi.reasoner.FreshEntityPolicy;
import org.semanticweb.owlapi.reasoner.InconsistentOntologyException;
import org.semanticweb.owlapi.reasoner.IndividualNodeSetPolicy;
import org.semanticweb.owlapi.reasoner.InferenceType;
import org.semanticweb.owlapi.reasoner.Node;
import org.semanticweb.owlapi.reasoner.NodeSet;
import org.semanticweb.owlapi.reasoner.OWLReasoner;
import org.semanticweb.owlapi.reasoner.ReasonerInterruptedException;
import org.semanticweb.owlapi.reasoner.TimeOutException;
import org.semanticweb.owlapi.reasoner.UnsupportedEntailmentTypeException;
import org.semanticweb.owlapi.util.Version;

/**
 * @author Yevgeny Kazakov
 *
 */
public class ElkReasoner implements OWLReasoner {

	/* (non-Javadoc)
	 * @see org.semanticweb.owlapi.reasoner.OWLReasoner#dispose()
	 */
	public void dispose() {
		// TODO Auto-generated method stub
		
	}

	/* (non-Javadoc)
	 * @see org.semanticweb.owlapi.reasoner.OWLReasoner#flush()
	 */
	public void flush() {
		// TODO Auto-generated method stub
		
	}

	/* (non-Javadoc)
	 * @see org.semanticweb.owlapi.reasoner.OWLReasoner#getBottomClassNode()
	 */
	public Node<OWLClass> getBottomClassNode() {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see org.semanticweb.owlapi.reasoner.OWLReasoner#getBottomDataPropertyNode()
	 */
	public Node<OWLDataProperty> getBottomDataPropertyNode() {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see org.semanticweb.owlapi.reasoner.OWLReasoner#getBottomObjectPropertyNode()
	 */
	public Node<OWLObjectPropertyExpression> getBottomObjectPropertyNode() {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see org.semanticweb.owlapi.reasoner.OWLReasoner#getBufferingMode()
	 */
	public BufferingMode getBufferingMode() {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see org.semanticweb.owlapi.reasoner.OWLReasoner#getDataPropertyDomains(org.semanticweb.owlapi.model.OWLDataProperty, boolean)
	 */
	public NodeSet<OWLClass> getDataPropertyDomains(OWLDataProperty arg0,
			boolean arg1) throws InconsistentOntologyException,
			FreshEntitiesException, ReasonerInterruptedException,
			TimeOutException {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see org.semanticweb.owlapi.reasoner.OWLReasoner#getDataPropertyValues(org.semanticweb.owlapi.model.OWLNamedIndividual, org.semanticweb.owlapi.model.OWLDataProperty)
	 */
	public Set<OWLLiteral> getDataPropertyValues(OWLNamedIndividual arg0,
			OWLDataProperty arg1) throws InconsistentOntologyException,
			FreshEntitiesException, ReasonerInterruptedException,
			TimeOutException {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see org.semanticweb.owlapi.reasoner.OWLReasoner#getDifferentIndividuals(org.semanticweb.owlapi.model.OWLNamedIndividual)
	 */
	public NodeSet<OWLNamedIndividual> getDifferentIndividuals(
			OWLNamedIndividual arg0) throws InconsistentOntologyException,
			FreshEntitiesException, ReasonerInterruptedException,
			TimeOutException {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see org.semanticweb.owlapi.reasoner.OWLReasoner#getDisjointClasses(org.semanticweb.owlapi.model.OWLClassExpression)
	 */
	public NodeSet<OWLClass> getDisjointClasses(OWLClassExpression arg0)
			throws ReasonerInterruptedException, TimeOutException,
			FreshEntitiesException, InconsistentOntologyException {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see org.semanticweb.owlapi.reasoner.OWLReasoner#getDisjointDataProperties(org.semanticweb.owlapi.model.OWLDataPropertyExpression)
	 */
	public NodeSet<OWLDataProperty> getDisjointDataProperties(
			OWLDataPropertyExpression arg0)
			throws InconsistentOntologyException, FreshEntitiesException,
			ReasonerInterruptedException, TimeOutException {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see org.semanticweb.owlapi.reasoner.OWLReasoner#getDisjointObjectProperties(org.semanticweb.owlapi.model.OWLObjectPropertyExpression)
	 */
	public NodeSet<OWLObjectPropertyExpression> getDisjointObjectProperties(
			OWLObjectPropertyExpression arg0)
			throws InconsistentOntologyException, FreshEntitiesException,
			ReasonerInterruptedException, TimeOutException {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see org.semanticweb.owlapi.reasoner.OWLReasoner#getEquivalentClasses(org.semanticweb.owlapi.model.OWLClassExpression)
	 */
	public Node<OWLClass> getEquivalentClasses(OWLClassExpression arg0)
			throws InconsistentOntologyException,
			ClassExpressionNotInProfileException, FreshEntitiesException,
			ReasonerInterruptedException, TimeOutException {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see org.semanticweb.owlapi.reasoner.OWLReasoner#getEquivalentDataProperties(org.semanticweb.owlapi.model.OWLDataProperty)
	 */
	public Node<OWLDataProperty> getEquivalentDataProperties(
			OWLDataProperty arg0) throws InconsistentOntologyException,
			FreshEntitiesException, ReasonerInterruptedException,
			TimeOutException {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see org.semanticweb.owlapi.reasoner.OWLReasoner#getEquivalentObjectProperties(org.semanticweb.owlapi.model.OWLObjectPropertyExpression)
	 */
	public Node<OWLObjectPropertyExpression> getEquivalentObjectProperties(
			OWLObjectPropertyExpression arg0)
			throws InconsistentOntologyException, FreshEntitiesException,
			ReasonerInterruptedException, TimeOutException {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see org.semanticweb.owlapi.reasoner.OWLReasoner#getFreshEntityPolicy()
	 */
	public FreshEntityPolicy getFreshEntityPolicy() {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see org.semanticweb.owlapi.reasoner.OWLReasoner#getIndividualNodeSetPolicy()
	 */
	public IndividualNodeSetPolicy getIndividualNodeSetPolicy() {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see org.semanticweb.owlapi.reasoner.OWLReasoner#getInstances(org.semanticweb.owlapi.model.OWLClassExpression, boolean)
	 */
	public NodeSet<OWLNamedIndividual> getInstances(OWLClassExpression arg0,
			boolean arg1) throws InconsistentOntologyException,
			ClassExpressionNotInProfileException, FreshEntitiesException,
			ReasonerInterruptedException, TimeOutException {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see org.semanticweb.owlapi.reasoner.OWLReasoner#getInverseObjectProperties(org.semanticweb.owlapi.model.OWLObjectPropertyExpression)
	 */
	public Node<OWLObjectPropertyExpression> getInverseObjectProperties(
			OWLObjectPropertyExpression arg0)
			throws InconsistentOntologyException, FreshEntitiesException,
			ReasonerInterruptedException, TimeOutException {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see org.semanticweb.owlapi.reasoner.OWLReasoner#getObjectPropertyDomains(org.semanticweb.owlapi.model.OWLObjectPropertyExpression, boolean)
	 */
	public NodeSet<OWLClass> getObjectPropertyDomains(
			OWLObjectPropertyExpression arg0, boolean arg1)
			throws InconsistentOntologyException, FreshEntitiesException,
			ReasonerInterruptedException, TimeOutException {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see org.semanticweb.owlapi.reasoner.OWLReasoner#getObjectPropertyRanges(org.semanticweb.owlapi.model.OWLObjectPropertyExpression, boolean)
	 */
	public NodeSet<OWLClass> getObjectPropertyRanges(
			OWLObjectPropertyExpression arg0, boolean arg1)
			throws InconsistentOntologyException, FreshEntitiesException,
			ReasonerInterruptedException, TimeOutException {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see org.semanticweb.owlapi.reasoner.OWLReasoner#getObjectPropertyValues(org.semanticweb.owlapi.model.OWLNamedIndividual, org.semanticweb.owlapi.model.OWLObjectPropertyExpression)
	 */
	public NodeSet<OWLNamedIndividual> getObjectPropertyValues(
			OWLNamedIndividual arg0, OWLObjectPropertyExpression arg1)
			throws InconsistentOntologyException, FreshEntitiesException,
			ReasonerInterruptedException, TimeOutException {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see org.semanticweb.owlapi.reasoner.OWLReasoner#getPendingAxiomAdditions()
	 */
	public Set<OWLAxiom> getPendingAxiomAdditions() {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see org.semanticweb.owlapi.reasoner.OWLReasoner#getPendingAxiomRemovals()
	 */
	public Set<OWLAxiom> getPendingAxiomRemovals() {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see org.semanticweb.owlapi.reasoner.OWLReasoner#getPendingChanges()
	 */
	public List<OWLOntologyChange> getPendingChanges() {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see org.semanticweb.owlapi.reasoner.OWLReasoner#getPrecomputableInferenceTypes()
	 */
	public Set<InferenceType> getPrecomputableInferenceTypes() {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see org.semanticweb.owlapi.reasoner.OWLReasoner#getReasonerName()
	 */
	public String getReasonerName() {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see org.semanticweb.owlapi.reasoner.OWLReasoner#getReasonerVersion()
	 */
	public Version getReasonerVersion() {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see org.semanticweb.owlapi.reasoner.OWLReasoner#getRootOntology()
	 */
	public OWLOntology getRootOntology() {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see org.semanticweb.owlapi.reasoner.OWLReasoner#getSameIndividuals(org.semanticweb.owlapi.model.OWLNamedIndividual)
	 */
	public Node<OWLNamedIndividual> getSameIndividuals(OWLNamedIndividual arg0)
			throws InconsistentOntologyException, FreshEntitiesException,
			ReasonerInterruptedException, TimeOutException {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see org.semanticweb.owlapi.reasoner.OWLReasoner#getSubClasses(org.semanticweb.owlapi.model.OWLClassExpression, boolean)
	 */
	public NodeSet<OWLClass> getSubClasses(OWLClassExpression arg0, boolean arg1)
			throws ReasonerInterruptedException, TimeOutException,
			FreshEntitiesException, InconsistentOntologyException,
			ClassExpressionNotInProfileException {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see org.semanticweb.owlapi.reasoner.OWLReasoner#getSubDataProperties(org.semanticweb.owlapi.model.OWLDataProperty, boolean)
	 */
	public NodeSet<OWLDataProperty> getSubDataProperties(OWLDataProperty arg0,
			boolean arg1) throws InconsistentOntologyException,
			FreshEntitiesException, ReasonerInterruptedException,
			TimeOutException {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see org.semanticweb.owlapi.reasoner.OWLReasoner#getSubObjectProperties(org.semanticweb.owlapi.model.OWLObjectPropertyExpression, boolean)
	 */
	public NodeSet<OWLObjectPropertyExpression> getSubObjectProperties(
			OWLObjectPropertyExpression arg0, boolean arg1)
			throws InconsistentOntologyException, FreshEntitiesException,
			ReasonerInterruptedException, TimeOutException {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see org.semanticweb.owlapi.reasoner.OWLReasoner#getSuperClasses(org.semanticweb.owlapi.model.OWLClassExpression, boolean)
	 */
	public NodeSet<OWLClass> getSuperClasses(OWLClassExpression arg0,
			boolean arg1) throws InconsistentOntologyException,
			ClassExpressionNotInProfileException, FreshEntitiesException,
			ReasonerInterruptedException, TimeOutException {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see org.semanticweb.owlapi.reasoner.OWLReasoner#getSuperDataProperties(org.semanticweb.owlapi.model.OWLDataProperty, boolean)
	 */
	public NodeSet<OWLDataProperty> getSuperDataProperties(
			OWLDataProperty arg0, boolean arg1)
			throws InconsistentOntologyException, FreshEntitiesException,
			ReasonerInterruptedException, TimeOutException {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see org.semanticweb.owlapi.reasoner.OWLReasoner#getSuperObjectProperties(org.semanticweb.owlapi.model.OWLObjectPropertyExpression, boolean)
	 */
	public NodeSet<OWLObjectPropertyExpression> getSuperObjectProperties(
			OWLObjectPropertyExpression arg0, boolean arg1)
			throws InconsistentOntologyException, FreshEntitiesException,
			ReasonerInterruptedException, TimeOutException {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see org.semanticweb.owlapi.reasoner.OWLReasoner#getTimeOut()
	 */
	public long getTimeOut() {
		// TODO Auto-generated method stub
		return 0;
	}

	/* (non-Javadoc)
	 * @see org.semanticweb.owlapi.reasoner.OWLReasoner#getTopClassNode()
	 */
	public Node<OWLClass> getTopClassNode() {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see org.semanticweb.owlapi.reasoner.OWLReasoner#getTopDataPropertyNode()
	 */
	public Node<OWLDataProperty> getTopDataPropertyNode() {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see org.semanticweb.owlapi.reasoner.OWLReasoner#getTopObjectPropertyNode()
	 */
	public Node<OWLObjectPropertyExpression> getTopObjectPropertyNode() {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see org.semanticweb.owlapi.reasoner.OWLReasoner#getTypes(org.semanticweb.owlapi.model.OWLNamedIndividual, boolean)
	 */
	public NodeSet<OWLClass> getTypes(OWLNamedIndividual arg0, boolean arg1)
			throws InconsistentOntologyException, FreshEntitiesException,
			ReasonerInterruptedException, TimeOutException {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see org.semanticweb.owlapi.reasoner.OWLReasoner#getUnsatisfiableClasses()
	 */
	public Node<OWLClass> getUnsatisfiableClasses()
			throws ReasonerInterruptedException, TimeOutException,
			InconsistentOntologyException {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see org.semanticweb.owlapi.reasoner.OWLReasoner#interrupt()
	 */
	public void interrupt() {
		// TODO Auto-generated method stub
		
	}

	/* (non-Javadoc)
	 * @see org.semanticweb.owlapi.reasoner.OWLReasoner#isConsistent()
	 */
	public boolean isConsistent() throws ReasonerInterruptedException,
			TimeOutException {
		// TODO Auto-generated method stub
		return false;
	}

	/* (non-Javadoc)
	 * @see org.semanticweb.owlapi.reasoner.OWLReasoner#isEntailed(org.semanticweb.owlapi.model.OWLAxiom)
	 */
	public boolean isEntailed(OWLAxiom arg0)
			throws ReasonerInterruptedException,
			UnsupportedEntailmentTypeException, TimeOutException,
			AxiomNotInProfileException, FreshEntitiesException,
			InconsistentOntologyException {
		// TODO Auto-generated method stub
		return false;
	}

	/* (non-Javadoc)
	 * @see org.semanticweb.owlapi.reasoner.OWLReasoner#isEntailed(java.util.Set)
	 */
	public boolean isEntailed(Set<? extends OWLAxiom> arg0)
			throws ReasonerInterruptedException,
			UnsupportedEntailmentTypeException, TimeOutException,
			AxiomNotInProfileException, FreshEntitiesException,
			InconsistentOntologyException {
		// TODO Auto-generated method stub
		return false;
	}

	/* (non-Javadoc)
	 * @see org.semanticweb.owlapi.reasoner.OWLReasoner#isEntailmentCheckingSupported(org.semanticweb.owlapi.model.AxiomType)
	 */
	public boolean isEntailmentCheckingSupported(AxiomType<?> arg0) {
		// TODO Auto-generated method stub
		return false;
	}

	/* (non-Javadoc)
	 * @see org.semanticweb.owlapi.reasoner.OWLReasoner#isPrecomputed(org.semanticweb.owlapi.reasoner.InferenceType)
	 */
	public boolean isPrecomputed(InferenceType arg0) {
		// TODO Auto-generated method stub
		return false;
	}

	/* (non-Javadoc)
	 * @see org.semanticweb.owlapi.reasoner.OWLReasoner#isSatisfiable(org.semanticweb.owlapi.model.OWLClassExpression)
	 */
	public boolean isSatisfiable(OWLClassExpression arg0)
			throws ReasonerInterruptedException, TimeOutException,
			ClassExpressionNotInProfileException, FreshEntitiesException,
			InconsistentOntologyException {
		// TODO Auto-generated method stub
		return false;
	}

	/* (non-Javadoc)
	 * @see org.semanticweb.owlapi.reasoner.OWLReasoner#precomputeInferences(org.semanticweb.owlapi.reasoner.InferenceType[])
	 */
	public void precomputeInferences(InferenceType... arg0)
			throws ReasonerInterruptedException, TimeOutException,
			InconsistentOntologyException {
		// TODO Auto-generated method stub
		
	}

}
