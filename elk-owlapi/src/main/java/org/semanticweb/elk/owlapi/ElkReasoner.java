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

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.log4j.Logger;
import org.semanticweb.elk.owl.implementation.ElkObjectFactoryImpl;
import org.semanticweb.elk.owl.interfaces.ElkClass;
import org.semanticweb.elk.owl.interfaces.ElkObjectFactory;
import org.semanticweb.elk.owl.predefined.PredefinedElkClass;
import org.semanticweb.elk.owlapi.wrapper.OwlConverter;
import org.semanticweb.elk.reasoner.DummyProgressMonitor;
import org.semanticweb.elk.reasoner.ProgressMonitor;
import org.semanticweb.elk.reasoner.Reasoner;
import org.semanticweb.elk.reasoner.ReasonerFactory;
import org.semanticweb.elk.reasoner.config.ReasonerConfiguration;
import org.semanticweb.elk.reasoner.taxonomy.ClassNode;
import org.semanticweb.elk.util.logging.Statistics;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.AddAxiom;
import org.semanticweb.owlapi.model.AxiomType;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLClassExpression;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLDataProperty;
import org.semanticweb.owlapi.model.OWLDataPropertyExpression;
import org.semanticweb.owlapi.model.OWLException;
import org.semanticweb.owlapi.model.OWLLiteral;
import org.semanticweb.owlapi.model.OWLNamedIndividual;
import org.semanticweb.owlapi.model.OWLObjectPropertyExpression;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyChange;
import org.semanticweb.owlapi.model.OWLOntologyChangeListener;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.model.RemoveAxiom;
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
import org.semanticweb.owlapi.reasoner.ReasonerProgressMonitor;
import org.semanticweb.owlapi.reasoner.TimeOutException;
import org.semanticweb.owlapi.reasoner.UnsupportedEntailmentTypeException;
import org.semanticweb.owlapi.reasoner.impl.OWLClassNode;
import org.semanticweb.owlapi.reasoner.impl.OWLDataPropertyNode;
import org.semanticweb.owlapi.reasoner.impl.OWLDataPropertyNodeSet;
import org.semanticweb.owlapi.reasoner.impl.OWLObjectPropertyNode;
import org.semanticweb.owlapi.reasoner.impl.OWLObjectPropertyNodeSet;
import org.semanticweb.owlapi.util.Version;

/**
 * {@link OWLReasoner} interface implementation for ELK {@link Reasoner}
 * 
 * @author Yevgeny Kazakov
 * 
 */
public class ElkReasoner implements OWLReasoner {

	// OWL API related objects
	protected final OWLOntology owlOntology;
	protected final OWLOntologyManager manager;
	protected final OWLDataFactory owlDataFactory;
	// the ELK reasoner instance used for reasoning
	protected final Reasoner reasoner;
	// ELK progress monitor implementation to display progress
	protected final ProgressMonitor elkProgressMonitor;
	// isBufferingMode == true iff the buffering mode for reasoner is {@link
	// BufferingMode.BUFFERING}
	protected final boolean isBufferingMode;
	// listener to implement addition and removal of axioms
	protected final OntologyChangeListener ontologyChangeListener;
	// list to accumulate the unprocessed changes to the ontology
	protected final List<OWLOntologyChange> pendingChanges;
	// ELK object factory used to create any ElkObjects
	protected final ElkObjectFactory objectFactory;
	// Converter from OWL API to ELK OWL
	protected final OwlConverter owlConverter;
	// Converter from ELK OWL to OWL API
	protected final ElkConverter elkConverter;

	protected boolean isSynced = false;
	// logger the messages
	protected final static Logger LOGGER_ = Logger.getLogger(ElkReasoner.class);

	ElkReasoner(OWLOntology ontology, boolean isBufferingMode, ReasonerProgressMonitor progressMonitor, ReasonerConfiguration elkConfig) {
		this.owlOntology = ontology;
		this.manager = ontology.getOWLOntologyManager();
		this.owlDataFactory = OWLManager.getOWLDataFactory();
		this.reasoner = new ReasonerFactory().createReasoner(elkConfig);
		this.ontologyChangeListener = new OntologyChangeListener();
		this.isBufferingMode = isBufferingMode;
		this.manager.addOntologyChangeListener(ontologyChangeListener);
		this.elkProgressMonitor = progressMonitor == null ? new DummyProgressMonitor() :  new ElkReasonerProgressMonitor(progressMonitor);
		this.pendingChanges = new ArrayList<OWLOntologyChange>();
		this.objectFactory = new ElkObjectFactoryImpl();
		this.owlConverter = OwlConverter.getInstance();
		this.elkConverter = ElkConverter.getInstance();
	}
	
	ElkReasoner(OWLOntology ontology, boolean isBufferingMode, 	ReasonerProgressMonitor progressMonitor) {
		this(ontology, isBufferingMode, progressMonitor, ReasonerConfiguration.getDefaultConfiguration());
	}
	
	ElkReasoner(OWLOntology ontology, boolean isBufferingMode) {
		this(ontology, isBufferingMode, null, ReasonerConfiguration.getDefaultConfiguration());
	}

	protected Reasoner getInternalReasoner() {
		return reasoner;
	}
	
	protected void addAxiom(OWLAxiom ax) {
		reasoner.addAxiom(owlConverter.convert(ax));
	}

	protected void removeAxiom(OWLAxiom ax) {
		reasoner.removeAxiom(owlConverter.convert(ax));
	}

	protected void syncOntology() {
		if (!isSynced) {
			reasoner.reset();
			try {
				Set<OWLOntology> importsClosure = owlOntology
						.getImportsClosure();
				int ontCount = importsClosure.size();
				int currentOntology = 0;
				for (OWLOntology ont : importsClosure) {
					currentOntology++;
					String status;
					if (ontCount == 1)
						status = ReasonerProgressMonitor.LOADING;
					else
						status = ReasonerProgressMonitor.LOADING + " "
								+ currentOntology + " of " + ontCount;
					Statistics.logOperationStart(status, LOGGER_);
					elkProgressMonitor.start(status);
					Set<OWLAxiom> axioms = ont.getAxioms();
					int axiomCount = axioms.size();
					int currentAxiom = 0;
					for (OWLAxiom ax : axioms) {
						currentAxiom++;
						if (ax.isLogicalAxiom()
								|| ax.isOfType(AxiomType.DECLARATION))
							addAxiom(ax);
						elkProgressMonitor.report(currentAxiom, axiomCount);
					}
					elkProgressMonitor.finish();
					Statistics.logOperationFinish(status, LOGGER_);
				}
			} catch (ReasonerInterruptedException e) {
			}
			isSynced = true;
			pendingChanges.clear();
		}
	}

	protected void reloadChanges() {
		if (!pendingChanges.isEmpty()) {
			String status = ReasonerProgressMonitor.LOADING;
			Statistics.logOperationStart(status, LOGGER_);
			elkProgressMonitor.start(status);
			int axiomCount = pendingChanges.size();
			int currentAxiom = 0;
			for (OWLOntologyChange change : pendingChanges) {
				if (change instanceof AddAxiom)
					addAxiom(change.getAxiom());
				if (change instanceof RemoveAxiom) {
					removeAxiom(change.getAxiom());
				}
				currentAxiom++;
				elkProgressMonitor.report(currentAxiom, axiomCount);
			}
			elkProgressMonitor.finish();
			Statistics.logOperationFinish(status, LOGGER_);
			pendingChanges.clear();
		}
	}

	protected void classifyOntology() {
		reasoner.classify(elkProgressMonitor);
	}

	protected ClassNode getElkClassNode(ElkClass cls) throws FreshEntitiesException {
		if (reasoner.getTaxonomy() == null)
			classifyOntology();
		ClassNode node = reasoner.getTaxonomy().getNode(cls);
		if (node == null)
			throw new FreshEntitiesException(elkConverter.convert(cls));
		return node;
	}

	/* Methods required by the OWLReasoner interface */

	@Override
	public void dispose() {
		owlOntology.getOWLOntologyManager().removeOntologyChangeListener(
				ontologyChangeListener);
		pendingChanges.clear();
		reasoner.shutdown();
	}

	@Override
	public void flush() {
		syncOntology();
		reloadChanges();
	}

	@Override
	public Node<OWLClass> getBottomClassNode() {
		return elkConverter
				.convert(getElkClassNode(PredefinedElkClass.OWL_NOTHING));
	}

	@Override
	public Node<OWLDataProperty> getBottomDataPropertyNode() {
		// TODO Provide implementation
		return new OWLDataPropertyNode(
				owlDataFactory.getOWLBottomDataProperty());
	}

	@Override
	public Node<OWLObjectPropertyExpression> getBottomObjectPropertyNode() {
		// TODO Provide implementation
		return new OWLObjectPropertyNode(
				owlDataFactory.getOWLBottomObjectProperty());
	}

	@Override
	public BufferingMode getBufferingMode() {
		return isBufferingMode ? BufferingMode.BUFFERING
				: BufferingMode.NON_BUFFERING;
	}

	@Override
	public NodeSet<OWLClass> getDataPropertyDomains(OWLDataProperty arg0,
			boolean arg1) throws InconsistentOntologyException,
			FreshEntitiesException, ReasonerInterruptedException,
			TimeOutException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Set<OWLLiteral> getDataPropertyValues(OWLNamedIndividual arg0,
			OWLDataProperty arg1) throws InconsistentOntologyException,
			FreshEntitiesException, ReasonerInterruptedException,
			TimeOutException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public NodeSet<OWLNamedIndividual> getDifferentIndividuals(
			OWLNamedIndividual arg0) throws InconsistentOntologyException,
			FreshEntitiesException, ReasonerInterruptedException,
			TimeOutException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public NodeSet<OWLClass> getDisjointClasses(OWLClassExpression arg0)
			throws ReasonerInterruptedException, TimeOutException,
			FreshEntitiesException, InconsistentOntologyException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public NodeSet<OWLDataProperty> getDisjointDataProperties(
			OWLDataPropertyExpression arg0)
			throws InconsistentOntologyException, FreshEntitiesException,
			ReasonerInterruptedException, TimeOutException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public NodeSet<OWLObjectPropertyExpression> getDisjointObjectProperties(
			OWLObjectPropertyExpression arg0)
			throws InconsistentOntologyException, FreshEntitiesException,
			ReasonerInterruptedException, TimeOutException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Node<OWLClass> getEquivalentClasses(OWLClassExpression ce)
			throws InconsistentOntologyException,
			ClassExpressionNotInProfileException, FreshEntitiesException,
			ReasonerInterruptedException, TimeOutException {
		if (ce.isAnonymous())
			return null;
		return elkConverter.convert(getElkClassNode(owlConverter.convert(ce
				.asOWLClass())));
	}

	@Override
	public Node<OWLDataProperty> getEquivalentDataProperties(
			OWLDataProperty arg0) throws InconsistentOntologyException,
			FreshEntitiesException, ReasonerInterruptedException,
			TimeOutException {
		// TODO Auto-generated method stub
		return new OWLDataPropertyNode(arg0);
	}

	@Override
	public Node<OWLObjectPropertyExpression> getEquivalentObjectProperties(
			OWLObjectPropertyExpression arg0)
			throws InconsistentOntologyException, FreshEntitiesException,
			ReasonerInterruptedException, TimeOutException {
		// TODO Auto-generated method stub
		return new OWLObjectPropertyNode(arg0);
	}

	@Override
	public FreshEntityPolicy getFreshEntityPolicy() {
		return FreshEntityPolicy.DISALLOW;
	}

	@Override
	public IndividualNodeSetPolicy getIndividualNodeSetPolicy() {
		return IndividualNodeSetPolicy.BY_NAME;
	}

	@Override
	public NodeSet<OWLNamedIndividual> getInstances(OWLClassExpression arg0,
			boolean arg1) throws InconsistentOntologyException,
			ClassExpressionNotInProfileException, FreshEntitiesException,
			ReasonerInterruptedException, TimeOutException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Node<OWLObjectPropertyExpression> getInverseObjectProperties(
			OWLObjectPropertyExpression arg0)
			throws InconsistentOntologyException, FreshEntitiesException,
			ReasonerInterruptedException, TimeOutException {
		// TODO Auto-generated method stub
		return new OWLObjectPropertyNode();
	}

	@Override
	public NodeSet<OWLClass> getObjectPropertyDomains(
			OWLObjectPropertyExpression arg0, boolean arg1)
			throws InconsistentOntologyException, FreshEntitiesException,
			ReasonerInterruptedException, TimeOutException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public NodeSet<OWLClass> getObjectPropertyRanges(
			OWLObjectPropertyExpression arg0, boolean arg1)
			throws InconsistentOntologyException, FreshEntitiesException,
			ReasonerInterruptedException, TimeOutException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public NodeSet<OWLNamedIndividual> getObjectPropertyValues(
			OWLNamedIndividual arg0, OWLObjectPropertyExpression arg1)
			throws InconsistentOntologyException, FreshEntitiesException,
			ReasonerInterruptedException, TimeOutException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Set<OWLAxiom> getPendingAxiomAdditions() {
		Set<OWLAxiom> added = new HashSet<OWLAxiom>();
		for (OWLOntologyChange change : pendingChanges)
			if (change instanceof AddAxiom)
				added.add(change.getAxiom());
		return added;
	}

	@Override
	public Set<OWLAxiom> getPendingAxiomRemovals() {
		Set<OWLAxiom> removed = new HashSet<OWLAxiom>();
		for (OWLOntologyChange change : pendingChanges)
			if (change instanceof RemoveAxiom)
				removed.add(change.getAxiom());
		return removed;
	}

	@Override
	public List<OWLOntologyChange> getPendingChanges() {
		return pendingChanges;
	}

	@Override
	public Set<InferenceType> getPrecomputableInferenceTypes() {
		return Collections.singleton(InferenceType.CLASS_HIERARCHY);
	}

	@Override
	public String getReasonerName() {
		return ElkReasoner.class.getPackage().getImplementationTitle();
	}

	@Override
	public Version getReasonerVersion() {
		String versionString = ElkReasoner.class.getPackage()
				.getImplementationVersion();
		String[] splitted;
		int filled = 0;
		int version[] = new int[4];
		if (versionString != null) {
			splitted = versionString.split("\\.");
			while (filled < splitted.length) {
				version[filled] = Integer.parseInt(splitted[filled]);
				filled++;
			}
		}
		while (filled < version.length) {
			version[filled] = 0;
			filled++;
		}
		return new Version(version[0], version[1], version[2], version[3]);
	}

	@Override
	public OWLOntology getRootOntology() {
		return owlOntology;
	}

	@Override
	public Node<OWLNamedIndividual> getSameIndividuals(OWLNamedIndividual arg0)
			throws InconsistentOntologyException, FreshEntitiesException,
			ReasonerInterruptedException, TimeOutException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public NodeSet<OWLClass> getSubClasses(OWLClassExpression ce, boolean direct)
			throws ReasonerInterruptedException, TimeOutException,
			FreshEntitiesException, InconsistentOntologyException,
			ClassExpressionNotInProfileException {
		if (ce.isAnonymous())
			return null;

		ClassNode ceClassNode = getElkClassNode(owlConverter.convert(ce
				.asOWLClass()));

		return (direct) ? elkConverter.convert(ceClassNode.getDirectSubNodes())
				: elkConverter.convert(ceClassNode.getAllSubNodes());
	}

	@Override
	public NodeSet<OWLDataProperty> getSubDataProperties(OWLDataProperty arg0,
			boolean arg1) throws InconsistentOntologyException,
			FreshEntitiesException, ReasonerInterruptedException,
			TimeOutException {
		// TODO Provide implementation
		return new OWLDataPropertyNodeSet();
	}

	@Override
	public NodeSet<OWLObjectPropertyExpression> getSubObjectProperties(
			OWLObjectPropertyExpression arg0, boolean arg1)
			throws InconsistentOntologyException, FreshEntitiesException,
			ReasonerInterruptedException, TimeOutException {
		// TODO Provide implementation
		return new OWLObjectPropertyNodeSet();
	}

	@Override
	public NodeSet<OWLClass> getSuperClasses(OWLClassExpression ce,
			boolean direct) throws InconsistentOntologyException,
			ClassExpressionNotInProfileException, FreshEntitiesException,
			ReasonerInterruptedException, TimeOutException {
		if (ce.isAnonymous())
			return null;

		ClassNode ceClassNode = getElkClassNode(owlConverter.convert(ce
				.asOWLClass()));

		return (direct) ? elkConverter.convert(ceClassNode
				.getDirectSuperNodes()) : elkConverter.convert(ceClassNode
				.getAllSuperNodes());
	}

	@Override
	public NodeSet<OWLDataProperty> getSuperDataProperties(
			OWLDataProperty arg0, boolean arg1)
			throws InconsistentOntologyException, FreshEntitiesException,
			ReasonerInterruptedException, TimeOutException {
		// TODO Provide implementation
		return new OWLDataPropertyNodeSet();
	}

	@Override
	public NodeSet<OWLObjectPropertyExpression> getSuperObjectProperties(
			OWLObjectPropertyExpression arg0, boolean arg1)
			throws InconsistentOntologyException, FreshEntitiesException,
			ReasonerInterruptedException, TimeOutException {
		// TODO Provide implementation
		return new OWLObjectPropertyNodeSet();
	}

	@Override
	public long getTimeOut() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public Node<OWLClass> getTopClassNode() {
		return elkConverter
				.convert(getElkClassNode(PredefinedElkClass.OWL_THING));
	}

	@Override
	public Node<OWLDataProperty> getTopDataPropertyNode() {
		// TODO Auto-generated method stub
		return new OWLDataPropertyNode(owlDataFactory.getOWLTopDataProperty());
	}

	@Override
	public Node<OWLObjectPropertyExpression> getTopObjectPropertyNode() {
		// TODO Auto-generated method stub
		return new OWLObjectPropertyNode(
				owlDataFactory.getOWLTopObjectProperty());
	}

	@Override
	public NodeSet<OWLClass> getTypes(OWLNamedIndividual arg0, boolean arg1)
			throws InconsistentOntologyException, FreshEntitiesException,
			ReasonerInterruptedException, TimeOutException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Node<OWLClass> getUnsatisfiableClasses()
			throws ReasonerInterruptedException, TimeOutException,
			InconsistentOntologyException {
		return elkConverter
				.convert(getElkClassNode(PredefinedElkClass.OWL_NOTHING));
	}

	@Override
	public void interrupt() {
		// TODO Auto-generated method stub

	}

	@Override
	public boolean isConsistent() throws ReasonerInterruptedException,
			TimeOutException {
		return getElkClassNode(PredefinedElkClass.OWL_NOTHING) !=
			getElkClassNode(PredefinedElkClass.OWL_THING);
	}

	@Override
	public boolean isEntailed(OWLAxiom arg0)
			throws ReasonerInterruptedException,
			UnsupportedEntailmentTypeException, TimeOutException,
			AxiomNotInProfileException, FreshEntitiesException,
			InconsistentOntologyException {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isEntailed(Set<? extends OWLAxiom> arg0)
			throws ReasonerInterruptedException,
			UnsupportedEntailmentTypeException, TimeOutException,
			AxiomNotInProfileException, FreshEntitiesException,
			InconsistentOntologyException {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isEntailmentCheckingSupported(AxiomType<?> arg0) {
		return false;
	}

	@Override
	public boolean isPrecomputed(InferenceType inferenceType) {
		// TODO Auto-generated method stub
		if (inferenceType.equals(InferenceType.CLASS_HIERARCHY))
			return reasoner.getTaxonomy() != null;
		else
			return false;
	}

	@Override
	public boolean isSatisfiable(OWLClassExpression classExpression)
			throws ReasonerInterruptedException, TimeOutException,
			ClassExpressionNotInProfileException, FreshEntitiesException,
			InconsistentOntologyException {
		if (classExpression.isAnonymous())
			return true;
		else {
			OWLClassNode botNode = elkConverter
					.convert(getElkClassNode(PredefinedElkClass.OWL_NOTHING));
			return (!botNode.contains(classExpression.asOWLClass()));
		}
	}

	@Override
	public void precomputeInferences(InferenceType... inferenceTypes)
			throws ReasonerInterruptedException, TimeOutException,
			InconsistentOntologyException {
		for (InferenceType inferenceType : inferenceTypes) {
			if (inferenceType.equals(InferenceType.CLASS_HIERARCHY)) {
				syncOntology();
				reloadChanges();
				classifyOntology();
			}
		}
	}

	protected class OntologyChangeListener implements OWLOntologyChangeListener {
		@Override
		public void ontologiesChanged(List<? extends OWLOntologyChange> changes)
				throws OWLException {
			for (OWLOntologyChange change : changes) {
				if (change.isAxiomChange()) {
					OWLAxiom axiom = change.getAxiom();
					if (axiom.isLogicalAxiom()
							|| axiom.isOfType(AxiomType.DECLARATION))
						pendingChanges.add(change);
				} else if (change.isImportChange())
					isSynced = false;
			}
		}
	}

}
