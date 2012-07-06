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
	private final OWLOntology owlOntology;
	private final OWLOntologyManager manager;
	private final OWLDataFactory owlDataFactory;
	/** the ELK reasoner instance used for reasoning */
	private final Reasoner reasoner;
	/** ELK progress monitor implementation to display progress */
	private final ProgressMonitor elkProgressMonitor;
	/**
	 * isBufferingMode == true iff the buffering mode for reasoner is
	 * {@link BufferingMode.BUFFERING}
	 */
	private final boolean isBufferingMode;
	/** listener to implement addition and removal of axioms */
	private final OntologyChangeListener ontologyChangeListener;
	/** ELK object factory used to create any ElkObjects */
	private final ElkObjectFactory objectFactory;
	/** Converter from OWL API to ELK OWL */
	private final OwlConverter owlConverter;
	/** Converter from ELK OWL to OWL API */
	private final ElkConverter elkConverter;
	/**
	 * The stage executor used for execution of reasoning stages
	 */
	protected final ReasonerStageExecutor stageExecutor;

	/** the object using which one can load the ontology changes */
	private OwlChangesLoader ontologyChangesLoader;

	/**
	 * {@code true} if it is required to reload the whole ontology next time the
	 * changes should be flushed
	 */
	protected boolean ontologyReloadRequired;

	ElkReasoner(OWLOntology ontology, boolean isBufferingMode,
			ElkReasonerConfiguration elkConfig,
			ReasonerStageExecutor stageExecutor) {
		this.owlOntology = ontology;
		this.manager = ontology.getOWLOntologyManager();
		this.owlDataFactory = OWLManager.getOWLDataFactory();
		this.elkProgressMonitor = elkConfig.getProgressMonitor() == null ? new DummyProgressMonitor()
				: new ElkReasonerProgressMonitor(elkConfig.getProgressMonitor());
		this.reasoner = new ReasonerFactory().createReasoner(stageExecutor,
				elkConfig.getElkConfiguration());
		this.reasoner
				.setAllowFreshEntities(elkConfig.getFreshEntityPolicy() == FreshEntityPolicy.ALLOW);
		this.reasoner.setProgressMonitor(this.elkProgressMonitor);
		;
		this.isBufferingMode = isBufferingMode;
		this.manager
				.addOntologyChangeListener(this.ontologyChangeListener = new OntologyChangeListener());
		this.objectFactory = new ElkObjectFactoryImpl();
		this.owlConverter = OwlConverter.getInstance();
		this.elkConverter = ElkConverter.getInstance();
		this.stageExecutor = stageExecutor;

		this.ontologyChangesLoader = new OwlChangesLoader(
				this.elkProgressMonitor);

		reasoner.registerOntologyLoader(new OwlOntologyLoader(owlOntology,
				this.elkProgressMonitor));
		reasoner.registerOntologyChangesLoader(ontologyChangesLoader);

		if (isBufferingMode) {
			/*
			 * for buffering mode we need to load the ontology now in order to
			 * correctly answer queries if no changes are flushed
			 */
			try {
				reasoner.loadOntology();
			} catch (ElkException e) {
				throw elkConverter.convert(e);
			}
			this.ontologyReloadRequired = false;
		} else
			/*
			 * for non-buffering mode, we can load the ontology lazily when the
			 * first query is asked
			 */
			this.ontologyReloadRequired = true;
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
		return reasoner;
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
			return elkConverter.convertClassNode(reasoner
					.getClassNode(elkClass));
		} catch (ElkException e) {
			throw elkConverter.convert(e);
		}
	}

	/* Methods required by the OWLReasoner interface */

	@Override
	public void dispose() {
		if (LOGGER_.isTraceEnabled())
			LOGGER_.trace("dispose()");
		owlOntology.getOWLOntologyManager().removeOntologyChangeListener(
				ontologyChangeListener);
		try {
			reasoner.reset();
			for (;;) {
				try {
					if (!reasoner.shutdown())
						throw new ReasonerInternalException(
								"Failed to shut down ELK!");
					break;
				} catch (InterruptedException e) {
					continue;
				}
			}
		} catch (ElkRuntimeException e) {
			throw elkConverter.convert(e);
		}
	}

	@Override
	public void flush() {
		if (LOGGER_.isTraceEnabled())
			LOGGER_.trace("flush()");
		try {
			if (ontologyReloadRequired) {
				reasoner.registerOntologyLoader(new OwlOntologyLoader(
						owlOntology, this.elkProgressMonitor));
				this.ontologyChangesLoader = new OwlChangesLoader(
						this.elkProgressMonitor);
				reasoner.loadOntology();
				ontologyReloadRequired = false;
			}
			// this causes the reasoner to update the changes from the listener
			reasoner.registerOntologyChangesLoader(ontologyChangesLoader);
			reasoner.loadChanges();
		} catch (ElkException e) {
			throw elkConverter.convert(e);
		} catch (ElkRuntimeException e) {
			throw elkConverter.convert(e);
		}
	}

	@Override
	public Node<OWLClass> getBottomClassNode() {
		if (LOGGER_.isTraceEnabled())
			LOGGER_.trace("getBottomClassNode()");
		try {
			return getClassNode(objectFactory.getOwlNothing());
		} catch (ElkException e) {
			throw elkConverter.convert(e);
		} catch (ElkRuntimeException e) {
			throw elkConverter.convert(e);
		}
	}

	@Override
	public Node<OWLDataProperty> getBottomDataPropertyNode() {
		if (LOGGER_.isTraceEnabled())
			LOGGER_.trace("getBottomDataPropertyNode()");
		// TODO Provide implementation
		return new OWLDataPropertyNode(
				owlDataFactory.getOWLBottomDataProperty());
	}

	@Override
	public Node<OWLObjectPropertyExpression> getBottomObjectPropertyNode() {
		if (LOGGER_.isTraceEnabled())
			LOGGER_.trace("getBottomObjectPropertyNode()");
		// TODO Provide implementation
		return new OWLObjectPropertyNode(
				owlDataFactory.getOWLBottomObjectProperty());
	}

	@Override
	public BufferingMode getBufferingMode() {
		if (LOGGER_.isTraceEnabled())
			LOGGER_.trace("getBufferingMode()");
		return isBufferingMode ? BufferingMode.BUFFERING
				: BufferingMode.NON_BUFFERING;
	}

	@Override
	public NodeSet<OWLClass> getDataPropertyDomains(OWLDataProperty arg0,
			boolean arg1) throws InconsistentOntologyException,
			FreshEntitiesException, ReasonerInterruptedException,
			TimeOutException {
		if (LOGGER_.isTraceEnabled())
			LOGGER_.trace("getDataPropertyDomains(arg0, arg1)");
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
		if (ce.isAnonymous()) {
			// TODO Provide implementation
			logUnsupportedOperation(
					"computation of classes equivalent to unnamed class expressions",
					"getEquivalentClasses");
			return new OWLClassNode();
		} else {
			try {
				return getClassNode(owlConverter.convert(ce.asOWLClass()));
			} catch (ElkException e) {
				throw elkConverter.convert(e);
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
		// TODO Provide implementation
		logUnsupportedOperation("computation of equivalent object properties",
				"getEquivalentObjectProperties");
		return new OWLObjectPropertyNode(arg0);
	}

	@Override
	public FreshEntityPolicy getFreshEntityPolicy() {
		if (LOGGER_.isTraceEnabled())
			LOGGER_.trace("getFreshEntityPolicy()");
		return reasoner.getAllowFreshEntities() ? FreshEntityPolicy.ALLOW
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
		try {
			return elkConverter.convertIndividualNodes(reasoner.getInstances(
					owlConverter.convert(ce), direct));
		} catch (ElkException e) {
			throw elkConverter.convert(e);
		} catch (ElkRuntimeException e) {
			throw elkConverter.convert(e);
		}
	}

	@Override
	public Node<OWLObjectPropertyExpression> getInverseObjectProperties(
			OWLObjectPropertyExpression arg0)
			throws InconsistentOntologyException, FreshEntitiesException,
			ReasonerInterruptedException, TimeOutException {
		if (LOGGER_.isTraceEnabled())
			LOGGER_.trace("getInverseObjectProperties(arg0)");
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
		// TODO Provide implementation
		logUnsupportedOperation("computation of object property values",
				"getObjectPropertyValues");
		return new OWLNamedIndividualNodeSet();
	}

	@Override
	public Set<OWLAxiom> getPendingAxiomAdditions() {
		if (LOGGER_.isTraceEnabled())
			LOGGER_.trace("getPendingAxiomAdditions()");
		return ontologyChangesLoader.getPendingAxiomAdditions();
	}

	@Override
	public Set<OWLAxiom> getPendingAxiomRemovals() {
		if (LOGGER_.isTraceEnabled())
			LOGGER_.trace("getPendingAxiomRemovals()");
		return ontologyChangesLoader.getPendingAxiomRemovals();
	}

	@Override
	public List<OWLOntologyChange> getPendingChanges() {
		if (LOGGER_.isTraceEnabled())
			LOGGER_.trace("getPendingChanges()");
		return ontologyChangesLoader.getPendingChanges();
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
		return owlOntology;
	}

	@Override
	public Node<OWLNamedIndividual> getSameIndividuals(OWLNamedIndividual arg0)
			throws InconsistentOntologyException, FreshEntitiesException,
			ReasonerInterruptedException, TimeOutException {
		if (LOGGER_.isTraceEnabled())
			LOGGER_.trace("getSameIndividuals(arg0)");
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
		try {
			return elkConverter.convertClassNodes(reasoner.getSubClasses(
					owlConverter.convert(ce), direct));
		} catch (ElkException e) {
			throw elkConverter.convert(e);
		}
	}

	@Override
	public NodeSet<OWLDataProperty> getSubDataProperties(OWLDataProperty arg0,
			boolean arg1) throws InconsistentOntologyException,
			FreshEntitiesException, ReasonerInterruptedException,
			TimeOutException {
		if (LOGGER_.isTraceEnabled())
			LOGGER_.trace("getSubDataProperties(arg0, arg1)");
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
		try {
			return elkConverter.convertClassNodes(reasoner.getSuperClasses(
					owlConverter.convert(ce), direct));
		} catch (ElkException e) {
			throw elkConverter.convert(e);
		} catch (ElkRuntimeException e) {
			throw elkConverter.convert(e);
		}
	}

	@Override
	public NodeSet<OWLDataProperty> getSuperDataProperties(
			OWLDataProperty arg0, boolean arg1)
			throws InconsistentOntologyException, FreshEntitiesException,
			ReasonerInterruptedException, TimeOutException {
		if (LOGGER_.isTraceEnabled())
			LOGGER_.trace("getSuperDataProperties(arg0, arg1)");
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
		try {
			return getClassNode(objectFactory.getOwlThing());
		} catch (ElkException e) {
			throw elkConverter.convert(e);
		} catch (ElkRuntimeException e) {
			throw elkConverter.convert(e);
		}
	}

	@Override
	public Node<OWLDataProperty> getTopDataPropertyNode() {
		if (LOGGER_.isTraceEnabled())
			LOGGER_.trace("getTopDataPropertyNode()");
		// TODO Provide implementation
		return new OWLDataPropertyNode(owlDataFactory.getOWLTopDataProperty());
	}

	@Override
	public Node<OWLObjectPropertyExpression> getTopObjectPropertyNode() {
		if (LOGGER_.isTraceEnabled())
			LOGGER_.trace("getTopObjectPropertyNode()");
		// TODO Provide implementation
		logUnsupportedOperation(
				"computation of object properties equivalent to top",
				"getTopObjectPropertyNode");
		return new OWLObjectPropertyNode(
				owlDataFactory.getOWLTopObjectProperty());
	}

	@Override
	public NodeSet<OWLClass> getTypes(OWLNamedIndividual ind, boolean direct)
			throws InconsistentOntologyException, FreshEntitiesException,
			ReasonerInterruptedException, TimeOutException {
		if (LOGGER_.isTraceEnabled())
			LOGGER_.trace("getTypes(ind, direct)");
		try {
			return elkConverter.convertClassNodes(reasoner.getTypes(
					owlConverter.convert(ind), direct));
		} catch (ElkException e) {
			throw elkConverter.convert(e);
		} catch (ElkRuntimeException e) {
			throw elkConverter.convert(e);
		}
	}

	@Override
	public Node<OWLClass> getUnsatisfiableClasses()
			throws ReasonerInterruptedException, TimeOutException,
			InconsistentOntologyException {
		if (LOGGER_.isTraceEnabled())
			LOGGER_.trace("getUnsatisfiableClasses()");
		try {
			return getClassNode(objectFactory.getOwlNothing());
		} catch (ElkException e) {
			throw elkConverter.convert(e);
		} catch (ElkRuntimeException e) {
			throw elkConverter.convert(e);
		}

	}

	@Override
	public void interrupt() {
		if (LOGGER_.isTraceEnabled())
			LOGGER_.trace("interrupt()");
		reasoner.interrupt();
	}

	@Override
	public boolean isConsistent() throws ReasonerInterruptedException,
			TimeOutException {
		if (LOGGER_.isTraceEnabled())
			LOGGER_.trace("isConsistent()");
		try {
			return reasoner.isConsistent();
		} catch (ElkException e) {
			throw elkConverter.convert(e);
		} catch (ElkRuntimeException e) {
			throw elkConverter.convert(e);
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
			return reasoner.doneTaxonomy();
		if (inferenceType.equals(InferenceType.CLASS_ASSERTIONS))
			return reasoner.doneInstanceTaxonomy();

		return false;
	}

	@Override
	public boolean isSatisfiable(OWLClassExpression classExpression)
			throws ReasonerInterruptedException, TimeOutException,
			ClassExpressionNotInProfileException, FreshEntitiesException,
			InconsistentOntologyException {
		if (LOGGER_.isTraceEnabled())
			LOGGER_.trace("isSatisfiable(classExpression)");
		try {
			return reasoner
					.isSatisfiable(owlConverter.convert(classExpression));
		} catch (ElkException e) {
			throw elkConverter.convert(e);
		} catch (ElkRuntimeException e) {
			throw elkConverter.convert(e);
		}
	}

	@Override
	public void precomputeInferences(InferenceType... inferenceTypes)
			throws ReasonerInterruptedException, TimeOutException,
			InconsistentOntologyException {
		if (LOGGER_.isTraceEnabled())
			LOGGER_.trace("precomputeInferences(inferenceTypes)");
		try {
			for (InferenceType inferenceType : inferenceTypes) {
				if (inferenceType.equals(InferenceType.CLASS_HIERARCHY))
					reasoner.getTaxonomy();
				else if (inferenceType.equals(InferenceType.CLASS_ASSERTIONS))
					reasoner.getInstanceTaxonomy();
			}
		} catch (ElkException e) {
			throw elkConverter.convert(e);
		} catch (ElkRuntimeException e) {
			throw elkConverter.convert(e);
		}

	}

	protected class OntologyChangeListener implements OWLOntologyChangeListener {
		@Override
		public void ontologiesChanged(List<? extends OWLOntologyChange> changes)
				throws OWLException {
			for (OWLOntologyChange change : changes) {
				ontologyChangesLoader.registerChange(change);
				if (!change.isAxiomChange())
					// currently we cannot handle non-axiom changes
					// incrementally
					ontologyReloadRequired = true;
			}
			if (!isBufferingMode)
				flush();
		}
	}

}
