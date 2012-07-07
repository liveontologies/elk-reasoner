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

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.log4j.Logger;
import org.semanticweb.elk.owl.exceptions.ElkException;
import org.semanticweb.elk.owl.exceptions.ElkRuntimeException;
import org.semanticweb.elk.owl.implementation.ElkObjectFactoryImpl;
import org.semanticweb.elk.owl.interfaces.ElkClass;
import org.semanticweb.elk.owl.interfaces.ElkObjectFactory;
import org.semanticweb.elk.owlapi.wrapper.OwlConverter;
import org.semanticweb.elk.reasoner.DummyProgressMonitor;
import org.semanticweb.elk.reasoner.ProgressMonitor;
import org.semanticweb.elk.reasoner.Reasoner;
import org.semanticweb.elk.reasoner.ReasonerFactory;
import org.semanticweb.elk.reasoner.stages.LoggingStageExecutor;
import org.semanticweb.elk.reasoner.stages.ReasonerStageExecutor;
import org.semanticweb.elk.util.collections.ArraySet;
import org.semanticweb.elk.util.logging.ElkMessage;
import org.semanticweb.owlapi.apibinding.OWLManager;
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
import org.semanticweb.owlapi.reasoner.ReasonerInternalException;
import org.semanticweb.owlapi.reasoner.ReasonerInterruptedException;
import org.semanticweb.owlapi.reasoner.ReasonerProgressMonitor;
import org.semanticweb.owlapi.reasoner.TimeOutException;
import org.semanticweb.owlapi.reasoner.UnsupportedEntailmentTypeException;
import org.semanticweb.owlapi.reasoner.impl.OWLClassNode;
import org.semanticweb.owlapi.reasoner.impl.OWLClassNodeSet;
import org.semanticweb.owlapi.reasoner.impl.OWLDataPropertyNode;
import org.semanticweb.owlapi.reasoner.impl.OWLDataPropertyNodeSet;
import org.semanticweb.owlapi.reasoner.impl.OWLNamedIndividualNode;
import org.semanticweb.owlapi.reasoner.impl.OWLNamedIndividualNodeSet;
import org.semanticweb.owlapi.reasoner.impl.OWLObjectPropertyNode;
import org.semanticweb.owlapi.reasoner.impl.OWLObjectPropertyNodeSet;
import org.semanticweb.owlapi.util.Version;

/**
 * {@link OWLReasoner} interface implementation for ELK {@link Reasoner}
 * 
 * @author Yevgeny Kazakov
 * @author Markus Kroetzsch
 */
public class ElkReasoner implements OWLReasoner {
	// logger for this class
	private static final Logger LOGGER_ = Logger.getLogger(ElkReasoner.class);

	// OWL API related objects
	private final OWLOntology owlOntology_;
	private final OWLOntologyManager owlOntologymanager_;
	private final OWLDataFactory owlDataFactory_;
	/** the ELK reasoner instance used for reasoning */
	private final Reasoner reasoner_;
	/** ELK progress monitor implementation to display progress */
	private final ProgressMonitor elkProgressMonitor_;
	/**
	 * {@code true} iff the buffering mode for reasoner is
	 * {@link BufferingMode.BUFFERING}
	 */
	private final boolean isBufferingMode_;
	/** listener to implement addition and removal of axioms */
	private final OntologyChangeListener ontologyChangeListener_;
	/** ELK object factory used to create any ElkObjects */
	private final ElkObjectFactory objectFactory_;
	/** Converter from OWL API to ELK OWL */
	private final OwlConverter owlConverter_;
	/** Converter from ELK OWL to OWL API */
	private final ElkConverter elkConverter_;
	/** the object using which one can load the ontology changes */
	private OwlChangesLoader ontologyChangesLoader_;

	/**
	 * {@code true} if it is required to reload the whole ontology next time the
	 * changes should be flushed
	 */
	private boolean ontologyReloadRequired_;

	ElkReasoner(OWLOntology ontology, boolean isBufferingMode,
			ElkReasonerConfiguration elkConfig,
			ReasonerStageExecutor stageExecutor) {
		this.owlOntology_ = ontology;
		this.owlOntologymanager_ = ontology.getOWLOntologyManager();
		this.owlDataFactory_ = OWLManager.getOWLDataFactory();
		this.elkProgressMonitor_ = elkConfig.getProgressMonitor() == null ? new DummyProgressMonitor()
				: new ElkReasonerProgressMonitor(elkConfig.getProgressMonitor());
		this.reasoner_ = new ReasonerFactory().createReasoner(stageExecutor,
				elkConfig.getElkConfiguration());
		this.reasoner_
				.setAllowFreshEntities(elkConfig.getFreshEntityPolicy() == FreshEntityPolicy.ALLOW);
		this.reasoner_.setProgressMonitor(this.elkProgressMonitor_);
		;
		this.isBufferingMode_ = isBufferingMode;
		this.owlOntologymanager_
				.addOntologyChangeListener(this.ontologyChangeListener_ = new OntologyChangeListener());
		this.objectFactory_ = new ElkObjectFactoryImpl();
		this.owlConverter_ = OwlConverter.getInstance();
		this.elkConverter_ = ElkConverter.getInstance();

		this.ontologyChangesLoader_ = new OwlChangesLoader(
				this.elkProgressMonitor_);

		reasoner_.registerOntologyLoader(new OwlOntologyLoader(owlOntology_,
				this.elkProgressMonitor_));
		reasoner_.registerOntologyChangesLoader(ontologyChangesLoader_);

		if (isBufferingMode) {
			/*
			 * for buffering mode we need to load the ontology now in order to
			 * correctly answer queries if no changes are flushed
			 */
			try {
				reasoner_.loadOntology();
			} catch (ElkException e) {
				throw elkConverter_.convert(e);
			}
			this.ontologyReloadRequired_ = false;
		} else
			/*
			 * for non-buffering mode, we can load the ontology lazily when the
			 * first query is asked
			 */
			this.ontologyReloadRequired_ = true;
	}

	ElkReasoner(OWLOntology ontology, boolean isBufferingMode,
			ElkReasonerConfiguration elkConfig) {
		this(ontology, isBufferingMode, elkConfig, new LoggingStageExecutor());
	}

	ElkReasoner(OWLOntology ontology, boolean isBufferingMode,
			ReasonerStageExecutor stageExecutor,
			ReasonerProgressMonitor progressMonitor) {
		this(ontology, isBufferingMode, new ElkReasonerConfiguration(
				progressMonitor), stageExecutor);

	}

	ElkReasoner(OWLOntology ontology, boolean isBufferingMode,
			ReasonerStageExecutor stageExecutor) {
		this(ontology, isBufferingMode, new ElkReasonerConfiguration(),
				stageExecutor);
	}

	ElkReasoner(OWLOntology ontology, boolean isBufferingMode) {
		this(ontology, isBufferingMode, new ElkReasonerConfiguration(),
				new LoggingStageExecutor());
	}

	protected Reasoner getInternalReasoner() {
		return reasoner_;
	}

	/**
	 * Helper method for consistent message reporting.
	 * 
	 * TODO: The method String can be used to create more specific message
	 * types, but with the current large amount of unsupported methods and
	 * non-persistent settings for ignoring them, we better use only one message
	 * type to make it easier to ignore them.
	 * 
	 * @param operation
	 * @param method
	 */
	private static void logUnsupportedOperation(String operation, String method) {
		LOGGER_.warn(new ElkMessage("ELK does not support " + operation + ".",
				"owlapi.unsupportedOperation"));
	}

	private Node<OWLClass> getClassNode(ElkClass elkClass)
			throws FreshEntitiesException, InconsistentOntologyException,
			ElkException {
		try {
			return elkConverter_.convertClassNode(reasoner_
					.getClassNode(elkClass));
		} catch (ElkException e) {
			throw elkConverter_.convert(e);
		}
	}

	/**
	 * 
	 * @throws ReasonerInterruptedException
	 *             if the reasoner is in the interrupted state, throws
	 */
	private void checkInterrupted() throws ReasonerInterruptedException {
		if (reasoner_.isInterrupted())
			throw new ReasonerInterruptedException("ELK was interrupted");
	}

	/* Methods required by the OWLReasoner interface */

	@Override
	public void dispose() {
		if (LOGGER_.isTraceEnabled())
			LOGGER_.trace("dispose()");
		owlOntology_.getOWLOntologyManager().removeOntologyChangeListener(
				ontologyChangeListener_);
		try {
			reasoner_.reset();
			for (;;) {
				try {
					if (!reasoner_.shutdown())
						throw new ReasonerInternalException(
								"Failed to shut down ELK!");
					break;
				} catch (InterruptedException e) {
					continue;
				}
			}
		} catch (ElkRuntimeException e) {
			throw elkConverter_.convert(e);
		}
	}

	@Override
	public void flush() {
		if (LOGGER_.isTraceEnabled())
			LOGGER_.trace("flush()");
		checkInterrupted();
		try {
			if (ontologyReloadRequired_) {
				reasoner_.registerOntologyLoader(new OwlOntologyLoader(
						owlOntology_, this.elkProgressMonitor_));
				this.ontologyChangesLoader_ = new OwlChangesLoader(
						this.elkProgressMonitor_);
				reasoner_.loadOntology();
				ontologyReloadRequired_ = false;
			}
			// this causes the reasoner to update the changes from the listener
			reasoner_.registerOntologyChangesLoader(ontologyChangesLoader_);
			reasoner_.loadChanges();
		} catch (ElkException e) {
			throw elkConverter_.convert(e);
		} catch (ElkRuntimeException e) {
			throw elkConverter_.convert(e);
		}
	}

	@Override
	public Node<OWLClass> getBottomClassNode() {
		if (LOGGER_.isTraceEnabled())
			LOGGER_.trace("getBottomClassNode()");
		checkInterrupted();
		try {
			return getClassNode(objectFactory_.getOwlNothing());
		} catch (ElkException e) {
			throw elkConverter_.convert(e);
		} catch (ElkRuntimeException e) {
			throw elkConverter_.convert(e);
		}
	}

	@Override
	public Node<OWLDataProperty> getBottomDataPropertyNode() {
		if (LOGGER_.isTraceEnabled())
			LOGGER_.trace("getBottomDataPropertyNode()");
		checkInterrupted();
		// TODO Provide implementation
		return new OWLDataPropertyNode(
				owlDataFactory_.getOWLBottomDataProperty());
	}

	@Override
	public Node<OWLObjectPropertyExpression> getBottomObjectPropertyNode() {
		if (LOGGER_.isTraceEnabled())
			LOGGER_.trace("getBottomObjectPropertyNode()");
		checkInterrupted();
		// TODO Provide implementation
		return new OWLObjectPropertyNode(
				owlDataFactory_.getOWLBottomObjectProperty());
	}

	@Override
	public BufferingMode getBufferingMode() {
		if (LOGGER_.isTraceEnabled())
			LOGGER_.trace("getBufferingMode()");
		return isBufferingMode_ ? BufferingMode.BUFFERING
				: BufferingMode.NON_BUFFERING;
	}

	@Override
	public NodeSet<OWLClass> getDataPropertyDomains(OWLDataProperty arg0,
			boolean arg1) throws InconsistentOntologyException,
			FreshEntitiesException, ReasonerInterruptedException,
			TimeOutException {
		if (LOGGER_.isTraceEnabled())
			LOGGER_.trace("getDataPropertyDomains(arg0, arg1)");
		checkInterrupted();
		// TODO Provide implementation
		logUnsupportedOperation("computation of data property domains",
				"getDataPropertyDomains");
		return new OWLClassNodeSet();
	}

	@Override
	public Set<OWLLiteral> getDataPropertyValues(OWLNamedIndividual arg0,
			OWLDataProperty arg1) throws InconsistentOntologyException,
			FreshEntitiesException, ReasonerInterruptedException,
			TimeOutException {
		if (LOGGER_.isTraceEnabled())
			LOGGER_.trace("getDataPropertyValues(arg0, arg1)");
		checkInterrupted();
		// TODO Provide implementation
		logUnsupportedOperation("computation of data property values",
				"getDataPropertyValues");
		return new ArraySet<OWLLiteral>();
	}

	@Override
	public NodeSet<OWLNamedIndividual> getDifferentIndividuals(
			OWLNamedIndividual arg0) throws InconsistentOntologyException,
			FreshEntitiesException, ReasonerInterruptedException,
			TimeOutException {
		if (LOGGER_.isTraceEnabled())
			LOGGER_.trace("getDifferentIndividuals(arg0)");
		checkInterrupted();
		// TODO Provide implementation
		logUnsupportedOperation("computation of different individuals",
				"getDifferentIndividuals");
		return new OWLNamedIndividualNodeSet();
	}

	@Override
	public NodeSet<OWLClass> getDisjointClasses(OWLClassExpression arg0)
			throws ReasonerInterruptedException, TimeOutException,
			FreshEntitiesException, InconsistentOntologyException {
		if (LOGGER_.isTraceEnabled())
			LOGGER_.trace("getDisjointClasses(arg0)");
		checkInterrupted();
		// TODO Provide implementation
		logUnsupportedOperation("computation of disjoint classes",
				"getDisjointClasses");
		return new OWLClassNodeSet();
	}

	@Override
	public NodeSet<OWLDataProperty> getDisjointDataProperties(
			OWLDataPropertyExpression arg0)
			throws InconsistentOntologyException, FreshEntitiesException,
			ReasonerInterruptedException, TimeOutException {
		if (LOGGER_.isTraceEnabled())
			LOGGER_.trace("getDisjointDataProperties(arg0)");
		checkInterrupted();
		// TODO Provide implementation
		logUnsupportedOperation("computation of disjoint data properties",
				"getDisjointDataProperties");
		return new OWLDataPropertyNodeSet();
	}

	@Override
	public NodeSet<OWLObjectPropertyExpression> getDisjointObjectProperties(
			OWLObjectPropertyExpression arg0)
			throws InconsistentOntologyException, FreshEntitiesException,
			ReasonerInterruptedException, TimeOutException {
		if (LOGGER_.isTraceEnabled())
			LOGGER_.trace("getDisjointObjectProperties(arg0)");
		checkInterrupted();
		// TODO Provide implementation
		logUnsupportedOperation("computation of disjoint object properties",
				"getDisjointObjectProperties");
		return new OWLObjectPropertyNodeSet();
	}

	@Override
	public Node<OWLClass> getEquivalentClasses(OWLClassExpression ce)
			throws InconsistentOntologyException,
			ClassExpressionNotInProfileException, FreshEntitiesException,
			ReasonerInterruptedException, TimeOutException {
		if (LOGGER_.isTraceEnabled())
			LOGGER_.trace("getEquivalentClasses(ce)");
		checkInterrupted();
		if (ce.isAnonymous()) {
			// TODO Provide implementation
			logUnsupportedOperation(
					"computation of classes equivalent to unnamed class expressions",
					"getEquivalentClasses");
			return new OWLClassNode();
		} else {
			try {
				return getClassNode(owlConverter_.convert(ce.asOWLClass()));
			} catch (ElkException e) {
				throw elkConverter_.convert(e);
			}
		}
	}

	@Override
	public Node<OWLDataProperty> getEquivalentDataProperties(
			OWLDataProperty arg0) throws InconsistentOntologyException,
			FreshEntitiesException, ReasonerInterruptedException,
			TimeOutException {
		if (LOGGER_.isTraceEnabled())
			LOGGER_.trace("getEquivalentDataProperties(arg0)");
		checkInterrupted();
		// TODO Provide implementation
		logUnsupportedOperation("computation of equivalent data properties",
				"getEquivalentDataProperties");
		return new OWLDataPropertyNode(arg0);
	}

	@Override
	public Node<OWLObjectPropertyExpression> getEquivalentObjectProperties(
			OWLObjectPropertyExpression arg0)
			throws InconsistentOntologyException, FreshEntitiesException,
			ReasonerInterruptedException, TimeOutException {
		if (LOGGER_.isTraceEnabled())
			LOGGER_.trace("getEquivalentObjectProperties(arg0)");
		checkInterrupted();
		// TODO Provide implementation
		logUnsupportedOperation("computation of equivalent object properties",
				"getEquivalentObjectProperties");
		return new OWLObjectPropertyNode(arg0);
	}

	@Override
	public FreshEntityPolicy getFreshEntityPolicy() {
		if (LOGGER_.isTraceEnabled())
			LOGGER_.trace("getFreshEntityPolicy()");
		return reasoner_.getAllowFreshEntities() ? FreshEntityPolicy.ALLOW
				: FreshEntityPolicy.DISALLOW;
	}

	@Override
	public IndividualNodeSetPolicy getIndividualNodeSetPolicy() {
		if (LOGGER_.isTraceEnabled())
			LOGGER_.trace("getIndividualNodeSetPolicy()");
		return IndividualNodeSetPolicy.BY_NAME;
	}

	@Override
	public NodeSet<OWLNamedIndividual> getInstances(OWLClassExpression ce,
			boolean direct) throws InconsistentOntologyException,
			ClassExpressionNotInProfileException, FreshEntitiesException,
			ReasonerInterruptedException, TimeOutException {
		if (LOGGER_.isTraceEnabled())
			LOGGER_.trace("getInstances(ce, direct)");
		checkInterrupted();
		try {
			return elkConverter_.convertIndividualNodes(reasoner_.getInstances(
					owlConverter_.convert(ce), direct));
		} catch (ElkException e) {
			throw elkConverter_.convert(e);
		} catch (ElkRuntimeException e) {
			throw elkConverter_.convert(e);
		}
	}

	@Override
	public Node<OWLObjectPropertyExpression> getInverseObjectProperties(
			OWLObjectPropertyExpression arg0)
			throws InconsistentOntologyException, FreshEntitiesException,
			ReasonerInterruptedException, TimeOutException {
		if (LOGGER_.isTraceEnabled())
			LOGGER_.trace("getInverseObjectProperties(arg0)");
		checkInterrupted();
		// TODO Provide implementation
		logUnsupportedOperation("computation of inverse object properties",
				"getInverseObjectProperties");
		return new OWLObjectPropertyNode();
	}

	@Override
	public NodeSet<OWLClass> getObjectPropertyDomains(
			OWLObjectPropertyExpression arg0, boolean arg1)
			throws InconsistentOntologyException, FreshEntitiesException,
			ReasonerInterruptedException, TimeOutException {
		if (LOGGER_.isTraceEnabled())
			LOGGER_.trace("getObjectPropertyDomains(arg0, arg1)");
		checkInterrupted();
		// TODO Provide implementation
		logUnsupportedOperation("computation of object property domains",
				"getObjectPropertyDomains");
		return new OWLClassNodeSet();
	}

	@Override
	public NodeSet<OWLClass> getObjectPropertyRanges(
			OWLObjectPropertyExpression arg0, boolean arg1)
			throws InconsistentOntologyException, FreshEntitiesException,
			ReasonerInterruptedException, TimeOutException {
		if (LOGGER_.isTraceEnabled())
			LOGGER_.trace("getObjectPropertyRanges(arg0, arg1)");
		checkInterrupted();
		// TODO Provide implementation
		logUnsupportedOperation("computation of object property ranges",
				"getObjectPropertyRanges");
		return new OWLClassNodeSet();
	}

	@Override
	public NodeSet<OWLNamedIndividual> getObjectPropertyValues(
			OWLNamedIndividual arg0, OWLObjectPropertyExpression arg1)
			throws InconsistentOntologyException, FreshEntitiesException,
			ReasonerInterruptedException, TimeOutException {
		if (LOGGER_.isTraceEnabled())
			LOGGER_.trace("getObjectPropertyValues(arg0, arg1)");
		checkInterrupted();
		// TODO Provide implementation
		logUnsupportedOperation("computation of object property values",
				"getObjectPropertyValues");
		return new OWLNamedIndividualNodeSet();
	}

	@Override
	public Set<OWLAxiom> getPendingAxiomAdditions() {
		if (LOGGER_.isTraceEnabled())
			LOGGER_.trace("getPendingAxiomAdditions()");
		return ontologyChangesLoader_.getPendingAxiomAdditions();
	}

	@Override
	public Set<OWLAxiom> getPendingAxiomRemovals() {
		if (LOGGER_.isTraceEnabled())
			LOGGER_.trace("getPendingAxiomRemovals()");
		return ontologyChangesLoader_.getPendingAxiomRemovals();
	}

	@Override
	public List<OWLOntologyChange> getPendingChanges() {
		if (LOGGER_.isTraceEnabled())
			LOGGER_.trace("getPendingChanges()");
		return ontologyChangesLoader_.getPendingChanges();
	}

	@Override
	public Set<InferenceType> getPrecomputableInferenceTypes() {
		if (LOGGER_.isTraceEnabled())
			LOGGER_.trace("getPrecomputableInferenceTypes()");
		return new HashSet<InferenceType>(Arrays.asList(
				InferenceType.CLASS_ASSERTIONS, InferenceType.CLASS_HIERARCHY));
	}

	@Override
	public String getReasonerName() {
		if (LOGGER_.isTraceEnabled())
			LOGGER_.trace("getReasonerName()");
		return ElkReasoner.class.getPackage().getImplementationTitle();
	}

	@Override
	public Version getReasonerVersion() {
		if (LOGGER_.isTraceEnabled())
			LOGGER_.trace("getReasonerVersion()");
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
		if (LOGGER_.isTraceEnabled())
			LOGGER_.trace("getRootOntology()");
		return owlOntology_;
	}

	@Override
	public Node<OWLNamedIndividual> getSameIndividuals(OWLNamedIndividual arg0)
			throws InconsistentOntologyException, FreshEntitiesException,
			ReasonerInterruptedException, TimeOutException {
		if (LOGGER_.isTraceEnabled())
			LOGGER_.trace("getSameIndividuals(arg0)");
		checkInterrupted();
		// TODO This needs to be updated when we support nominals
		return new OWLNamedIndividualNode(arg0);
	}

	@Override
	public NodeSet<OWLClass> getSubClasses(OWLClassExpression ce, boolean direct)
			throws ReasonerInterruptedException, TimeOutException,
			FreshEntitiesException, InconsistentOntologyException,
			ClassExpressionNotInProfileException {
		if (LOGGER_.isTraceEnabled())
			LOGGER_.trace("getSubClasses(ce, direct)");
		checkInterrupted();
		try {
			return elkConverter_.convertClassNodes(reasoner_.getSubClasses(
					owlConverter_.convert(ce), direct));
		} catch (ElkException e) {
			throw elkConverter_.convert(e);
		}
	}

	@Override
	public NodeSet<OWLDataProperty> getSubDataProperties(OWLDataProperty arg0,
			boolean arg1) throws InconsistentOntologyException,
			FreshEntitiesException, ReasonerInterruptedException,
			TimeOutException {
		if (LOGGER_.isTraceEnabled())
			LOGGER_.trace("getSubDataProperties(arg0, arg1)");
		checkInterrupted();
		// TODO Provide implementation
		logUnsupportedOperation("computation of sub data properties",
				"getSubDataProperties");
		return new OWLDataPropertyNodeSet();
	}

	@Override
	public NodeSet<OWLObjectPropertyExpression> getSubObjectProperties(
			OWLObjectPropertyExpression arg0, boolean arg1)
			throws InconsistentOntologyException, FreshEntitiesException,
			ReasonerInterruptedException, TimeOutException {
		// TODO Provide implementation
		if (LOGGER_.isTraceEnabled())
			LOGGER_.trace("getSubObjectProperties(arg0, arg1)");
		checkInterrupted();
		logUnsupportedOperation("computation of sub object properties",
				"getSubObjectProperties");
		return new OWLObjectPropertyNodeSet();
	}

	@Override
	public NodeSet<OWLClass> getSuperClasses(OWLClassExpression ce,
			boolean direct) throws InconsistentOntologyException,
			ClassExpressionNotInProfileException, FreshEntitiesException,
			ReasonerInterruptedException, TimeOutException {
		if (LOGGER_.isTraceEnabled())
			LOGGER_.trace("getSuperClasses(ce, direct)");
		checkInterrupted();
		try {
			return elkConverter_.convertClassNodes(reasoner_.getSuperClasses(
					owlConverter_.convert(ce), direct));
		} catch (ElkException e) {
			throw elkConverter_.convert(e);
		} catch (ElkRuntimeException e) {
			throw elkConverter_.convert(e);
		}
	}

	@Override
	public NodeSet<OWLDataProperty> getSuperDataProperties(
			OWLDataProperty arg0, boolean arg1)
			throws InconsistentOntologyException, FreshEntitiesException,
			ReasonerInterruptedException, TimeOutException {
		if (LOGGER_.isTraceEnabled())
			LOGGER_.trace("getSuperDataProperties(arg0, arg1)");
		checkInterrupted();
		// TODO Provide implementation
		logUnsupportedOperation("computation of super data properties",
				"getSuperDataProperties");
		return new OWLDataPropertyNodeSet();
	}

	@Override
	public NodeSet<OWLObjectPropertyExpression> getSuperObjectProperties(
			OWLObjectPropertyExpression arg0, boolean arg1)
			throws InconsistentOntologyException, FreshEntitiesException,
			ReasonerInterruptedException, TimeOutException {
		if (LOGGER_.isTraceEnabled())
			LOGGER_.trace("getSuperObjectProperties(arg0, arg1)");
		checkInterrupted();
		// TODO Provide implementation
		logUnsupportedOperation("computation of super object properties",
				"getSuperObjectProperties");
		return new OWLObjectPropertyNodeSet();
	}

	@Override
	public long getTimeOut() {
		if (LOGGER_.isTraceEnabled())
			LOGGER_.trace("getTimeOut()");
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public Node<OWLClass> getTopClassNode() {
		if (LOGGER_.isTraceEnabled())
			LOGGER_.trace("getTopClassNode()");
		checkInterrupted();
		try {
			return getClassNode(objectFactory_.getOwlThing());
		} catch (ElkException e) {
			throw elkConverter_.convert(e);
		} catch (ElkRuntimeException e) {
			throw elkConverter_.convert(e);
		}
	}

	@Override
	public Node<OWLDataProperty> getTopDataPropertyNode() {
		if (LOGGER_.isTraceEnabled())
			LOGGER_.trace("getTopDataPropertyNode()");
		checkInterrupted();
		// TODO Provide implementation
		return new OWLDataPropertyNode(owlDataFactory_.getOWLTopDataProperty());
	}

	@Override
	public Node<OWLObjectPropertyExpression> getTopObjectPropertyNode() {
		if (LOGGER_.isTraceEnabled())
			LOGGER_.trace("getTopObjectPropertyNode()");
		checkInterrupted();
		// TODO Provide implementation
		logUnsupportedOperation(
				"computation of object properties equivalent to top",
				"getTopObjectPropertyNode");
		return new OWLObjectPropertyNode(
				owlDataFactory_.getOWLTopObjectProperty());
	}

	@Override
	public NodeSet<OWLClass> getTypes(OWLNamedIndividual ind, boolean direct)
			throws InconsistentOntologyException, FreshEntitiesException,
			ReasonerInterruptedException, TimeOutException {
		if (LOGGER_.isTraceEnabled())
			LOGGER_.trace("getTypes(ind, direct)");
		checkInterrupted();
		try {
			return elkConverter_.convertClassNodes(reasoner_.getTypes(
					owlConverter_.convert(ind), direct));
		} catch (ElkException e) {
			throw elkConverter_.convert(e);
		} catch (ElkRuntimeException e) {
			throw elkConverter_.convert(e);
		}
	}

	@Override
	public Node<OWLClass> getUnsatisfiableClasses()
			throws ReasonerInterruptedException, TimeOutException,
			InconsistentOntologyException {
		if (LOGGER_.isTraceEnabled())
			LOGGER_.trace("getUnsatisfiableClasses()");
		checkInterrupted();
		try {
			return getClassNode(objectFactory_.getOwlNothing());
		} catch (ElkException e) {
			throw elkConverter_.convert(e);
		} catch (ElkRuntimeException e) {
			throw elkConverter_.convert(e);
		}

	}

	@Override
	public void interrupt() {
		if (LOGGER_.isTraceEnabled())
			LOGGER_.trace("interrupt()");
		reasoner_.interrupt();
	}

	@Override
	public boolean isConsistent() throws ReasonerInterruptedException,
			TimeOutException {
		if (LOGGER_.isTraceEnabled())
			LOGGER_.trace("isConsistent()");
		try {
			return reasoner_.isConsistent();
		} catch (ElkException e) {
			throw elkConverter_.convert(e);
		} catch (ElkRuntimeException e) {
			throw elkConverter_.convert(e);
		}
	}

	@Override
	public boolean isEntailed(OWLAxiom arg0)
			throws ReasonerInterruptedException,
			UnsupportedEntailmentTypeException, TimeOutException,
			AxiomNotInProfileException, FreshEntitiesException,
			InconsistentOntologyException {
		if (LOGGER_.isTraceEnabled())
			LOGGER_.trace("isEntailed(arg0)");
		checkInterrupted();
		// TODO Provide implementation
		logUnsupportedOperation("checking axiom entailment", "isEntailed");
		return false;
	}

	@Override
	public boolean isEntailed(Set<? extends OWLAxiom> arg0)
			throws ReasonerInterruptedException,
			UnsupportedEntailmentTypeException, TimeOutException,
			AxiomNotInProfileException, FreshEntitiesException,
			InconsistentOntologyException {
		if (LOGGER_.isTraceEnabled())
			LOGGER_.trace("isEntailed(arg0)");
		checkInterrupted();
		// TODO Provide implementation
		logUnsupportedOperation("checking axiom entailment", "isEntailed");
		return false;
	}

	@Override
	public boolean isEntailmentCheckingSupported(AxiomType<?> arg0) {
		if (LOGGER_.isTraceEnabled())
			LOGGER_.trace("isEntailmentCheckingSupported(arg0)");
		return false;
	}

	@Override
	public boolean isPrecomputed(InferenceType inferenceType) {
		if (LOGGER_.isTraceEnabled())
			LOGGER_.trace("isPrecomputed(inferenceType)");
		if (inferenceType.equals(InferenceType.CLASS_HIERARCHY))
			return reasoner_.doneTaxonomy();
		if (inferenceType.equals(InferenceType.CLASS_ASSERTIONS))
			return reasoner_.doneInstanceTaxonomy();

		return false;
	}

	@Override
	public boolean isSatisfiable(OWLClassExpression classExpression)
			throws ReasonerInterruptedException, TimeOutException,
			ClassExpressionNotInProfileException, FreshEntitiesException,
			InconsistentOntologyException {
		if (LOGGER_.isTraceEnabled())
			LOGGER_.trace("isSatisfiable(classExpression)");
		checkInterrupted();
		try {
			return reasoner_.isSatisfiable(owlConverter_
					.convert(classExpression));
		} catch (ElkException e) {
			throw elkConverter_.convert(e);
		} catch (ElkRuntimeException e) {
			throw elkConverter_.convert(e);
		}
	}

	@Override
	public void precomputeInferences(InferenceType... inferenceTypes)
			throws ReasonerInterruptedException, TimeOutException,
			InconsistentOntologyException {
		if (LOGGER_.isTraceEnabled())
			LOGGER_.trace("precomputeInferences(inferenceTypes)");
		checkInterrupted();
		try {
			for (InferenceType inferenceType : inferenceTypes) {
				if (inferenceType.equals(InferenceType.CLASS_HIERARCHY))
					reasoner_.getTaxonomy();
				else if (inferenceType.equals(InferenceType.CLASS_ASSERTIONS))
					reasoner_.getInstanceTaxonomy();
			}
		} catch (ElkException e) {
			throw elkConverter_.convert(e);
		} catch (ElkRuntimeException e) {
			throw elkConverter_.convert(e);
		}

	}

	protected class OntologyChangeListener implements OWLOntologyChangeListener {
		@Override
		public void ontologiesChanged(List<? extends OWLOntologyChange> changes)
				throws OWLException {
			for (OWLOntologyChange change : changes) {
				ontologyChangesLoader_.registerChange(change);
				if (!change.isAxiomChange())
					// currently we cannot handle non-axiom changes
					// incrementally
					ontologyReloadRequired_ = true;
			}
			if (!isBufferingMode_)
				flush();
		}
	}

}
