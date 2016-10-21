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

import org.semanticweb.elk.exceptions.ElkException;
import org.semanticweb.elk.exceptions.ElkRuntimeException;
import org.semanticweb.elk.owl.inferences.ReasonerElkInferenceSet;
import org.semanticweb.elk.owl.interfaces.ElkClass;
import org.semanticweb.elk.owl.interfaces.ElkObject;
import org.semanticweb.elk.owl.interfaces.ElkObjectProperty;
import org.semanticweb.elk.owlapi.wrapper.OwlConverter;
import org.semanticweb.elk.reasoner.DummyProgressMonitor;
import org.semanticweb.elk.reasoner.ElkUnsupportedReasoningTaskException;
import org.semanticweb.elk.reasoner.ProgressMonitor;
import org.semanticweb.elk.reasoner.Reasoner;
import org.semanticweb.elk.reasoner.ReasonerFactory;
import org.semanticweb.elk.reasoner.config.ReasonerConfiguration;
import org.semanticweb.elk.reasoner.stages.ElkInterruptedException;
import org.semanticweb.elk.util.logging.LogLevel;
import org.semanticweb.elk.util.logging.LoggerWrap;
import org.semanticweb.owlapi.model.AxiomType;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLClassExpression;
import org.semanticweb.owlapi.model.OWLDataProperty;
import org.semanticweb.owlapi.model.OWLDataPropertyExpression;
import org.semanticweb.owlapi.model.OWLException;
import org.semanticweb.owlapi.model.OWLLiteral;
import org.semanticweb.owlapi.model.OWLNamedIndividual;
import org.semanticweb.owlapi.model.OWLObjectProperty;
import org.semanticweb.owlapi.model.OWLObjectPropertyExpression;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyChange;
import org.semanticweb.owlapi.model.OWLOntologyChangeListener;
import org.semanticweb.owlapi.model.OWLOntologyChangeProgressListener;
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
import org.semanticweb.owlapi.reasoner.TimeOutException;
import org.semanticweb.owlapi.reasoner.UnsupportedEntailmentTypeException;
import org.semanticweb.owlapi.reasoner.impl.OWLNamedIndividualNode;
import org.semanticweb.owlapi.util.Version;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * {@link OWLReasoner} interface implementation for ELK {@link Reasoner}
 * 
 * @author Yevgeny Kazakov
 * @author Markus Kroetzsch
 * @author Peter Skocovsky
 */
public class ElkReasoner implements OWLReasoner {
	// logger for this class
	private static final Logger LOGGER_ = LoggerFactory
			.getLogger(ElkReasoner.class);

	// OWL API related objects
	private final OWLOntology owlOntology_;
	private final OWLOntologyManager owlOntologymanager_;
	/**
	 * ELK progress monitor to display progress of main reasoning tasks, e.g.,
	 * classification
	 */
	private final ProgressMonitor mainProgressMonitor_;
	/**
	 * ELK progress monitor to display progress of other reasoning tasks, e.g.,
	 * satisfiability checking
	 */
	private final ProgressMonitor secondaryProgressMonitor_;
	/**
	 * {@code true} iff the buffering mode for reasoner is
	 * {@link BufferingMode#BUFFERING}
	 */
	private final boolean isBufferingMode_;
	/** listener to implement addition and removal of axioms */
	private final OntologyChangeListener ontologyChangeListener_;
	/** listener to keep track of when changes are applied to the ontology */
	private final OntologyChangeProgressListener ontologyChangeProgressListener_;
	/** ELK object factory used to create any ElkObjects */
	private final ElkObject.Factory objectFactory_;
	/** Converter from OWL API to ELK OWL */
	private final OwlConverter owlConverter_;
	/** Converter from ELK OWL to OWL API */
	private final ElkConverter elkConverter_;
	/** this object is used to load pending changes */
	private volatile OwlChangesLoaderFactory bufferedChangesLoader_;
	/** configurations required for ELK reasoner */
	private ReasonerConfiguration config_;
	private final boolean isAllowFreshEntities;
	/** the ELK reasoner instance used for reasoning */
	private Reasoner reasoner_;
	/** Inferences for derived ELK axioms */
	private ReasonerElkInferenceSet elkInferenceSet_;

	/**
	 * {@code true} if the ontology should be loaded before any changes are
	 * applied to the ontology (used only for buffering mode)
	 */
	private boolean loadBeforeChanges_ = true;
	/**
	 * {@code true} if it is required to reload the whole ontology next time the
	 * changes should be flushed
	 */
	private boolean ontologyReloadRequired_;

	ElkReasoner(OWLOntology ontology, boolean isBufferingMode,
			ElkReasonerConfiguration elkConfig,
			final Reasoner internalReasoner) {
		this.owlOntology_ = ontology;
		this.owlOntologymanager_ = ontology.getOWLOntologyManager();
		this.mainProgressMonitor_ = elkConfig.getProgressMonitor() == null ? new DummyProgressMonitor()
				: new ElkReasonerProgressMonitor(elkConfig.getProgressMonitor());
		this.secondaryProgressMonitor_ = new DummyProgressMonitor();
		this.isBufferingMode_ = isBufferingMode;
		this.ontologyChangeListener_ = new OntologyChangeListener();
		this.owlOntologymanager_
				.addOntologyChangeListener(ontologyChangeListener_);
		this.ontologyChangeProgressListener_ = new OntologyChangeProgressListener();
		this.owlOntologymanager_
				.addOntologyChangeProgessListener(ontologyChangeProgressListener_);
		this.objectFactory_ = internalReasoner.getElkFactory();
		this.owlConverter_ = OwlConverter.getInstance();
		this.elkConverter_ = ElkConverter.getInstance();

		this.config_ = elkConfig.getElkConfiguration();
		this.isAllowFreshEntities = elkConfig.getFreshEntityPolicy() == FreshEntityPolicy.ALLOW;

		initReasoner(internalReasoner);
		this.bufferedChangesLoader_ = new OwlChangesLoaderFactory(
				this.mainProgressMonitor_);
		if (!isBufferingMode_) {
			// register the change loader only in non-buffering mode;
			// in buffering mode the loader is registered only when
			// changes are flushed
			reasoner_.registerAxiomLoader(bufferedChangesLoader_);
		}
		this.ontologyReloadRequired_ = false;
	}

	ElkReasoner(OWLOntology ontology, boolean isBufferingMode,
			ElkReasonerConfiguration elkConfig) {
		this(ontology, isBufferingMode, elkConfig, new ReasonerFactory()
				.createReasoner(elkConfig.getElkConfiguration()));
	}


	OWLOntology getOWLOntology() {
		return owlOntology_;
	}
	
	ElkObject.Factory getElkObjectFactory() {
		return objectFactory_;
	}
	
	ReasonerElkInferenceSet getElkInferenceSet() {
		return elkInferenceSet_;
	}

	private void initReasoner(final Reasoner reasoner) {
		this.reasoner_ = reasoner;
		this.reasoner_.registerAxiomLoader(new OwlOntologyLoader.Factory(
				owlOntology_, this.mainProgressMonitor_));
		this.reasoner_.setAllowFreshEntities(isAllowFreshEntities);
		// use the secondary progress monitor by default, when necessary, we
		// switch to the primary progress monitor; this is to avoid bugs with
		// progress monitors in Protege
		this.reasoner_.setProgressMonitor(this.secondaryProgressMonitor_);
		this.elkInferenceSet_ = new ReasonerElkInferenceSet(reasoner_,
				objectFactory_);
	}

	/**
	 * @return the ELK reasoner used internally in this OWL API wrapper.
	 */
	public Reasoner getInternalReasoner() {
		return reasoner_;
	}

	public ReasonerConfiguration getConfigurationOptions() {
		return config_;
	}
	
	public void setConfigurationOptions(ReasonerConfiguration config) {
		this.config_ = config;
		reasoner_.setConfigurationOptions(config);
	}

	/**
	 * Logs a warning message for unsupported OWL API method
	 * 
	 * @param method
	 *            the method which is not supported
	 * @return the {@link UnsupportedOperationException} saying that this method
	 *         is not supported
	 */
	private static UnsupportedOperationException unsupportedOwlApiMethod(
			String method) {
		String message = "OWL API reasoner method is not implemented: "
				+ method + ".";
		/*
		 * TODO: The method String can be used to create more specific message
		 * types, but with the current large amount of unsupported methods and
		 * non-persistent settings for ignoring them, we better use only one
		 * message type to make it easier to ignore them.
		 */
		LoggerWrap.log(LOGGER_, LogLevel.WARN, "owlapi.unsupportedMethod",
				message);

		return new UnsupportedOperationException(message);
	}

	/**
	 * Logs a warning message for unsupported case of an OWL API method
	 * 
	 * @param method
	 *            the method which some case is not supported
	 * @param reason
	 *            the reason why the case is not supported
	 * @return the {@link UnsupportedOperationException} saying that this method
	 *         is not supported
	 */
	private static UnsupportedOperationException unsupportedOwlApiMethod(
			String method, String reason) {
		String message = "OWL API reasoner method is not fully implemented: "
				+ method + ": " + reason;
		LoggerWrap.log(LOGGER_, LogLevel.WARN, "owlapi.unsupportedMethod",
				message);

		return new UnsupportedOperationException(message);
	}

	private Node<OWLClass> getClassNode(ElkClass elkClass)
			throws FreshEntitiesException, InconsistentOntologyException,
			ElkException {
		try {
			return elkConverter_.convertClassNode(reasoner_
					.getEquivalentClasses(elkClass));
		} catch (ElkException e) {
			throw elkConverter_.convert(e);
		}
	}

	private Node<OWLObjectPropertyExpression> getObjectPropertyNode(
			final ElkObjectProperty elkClass) throws FreshEntitiesException,
					InconsistentOntologyException, ElkException {
		try {
			return elkConverter_.convertObjectPropertyNode(
					reasoner_.getObjectPropertyNode(elkClass));
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
		try {
			reasoner_.checkInterrupt();
		} catch (final ElkInterruptedException e) {
			throw new ReasonerInterruptedException(e);
		}
	}

	/* Methods required by the OWLReasoner interface */

	@Override
	public void dispose() {
		LOGGER_.trace("dispose()");

		owlOntologymanager_
				.removeOntologyChangeListener(ontologyChangeListener_);
		owlOntologymanager_
				.removeOntologyChangeProgessListener(ontologyChangeProgressListener_);
		try {
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
		LOGGER_.trace("flush()");

		checkInterrupted();

		try {
			if (ontologyReloadRequired_) {
				/**
				 * re-creates a new instance of reasoner for the parameters,
				 * since a reasoner can do initial load only once
				 */
				initReasoner(new ReasonerFactory().createReasoner(reasoner_,
						objectFactory_, config_));
				bufferedChangesLoader_ = new OwlChangesLoaderFactory(
						this.secondaryProgressMonitor_);
				ontologyReloadRequired_ = false;
			} else if (!bufferedChangesLoader_.isLoadingFinished()) {
				// there is something new in the buffer
				if (isBufferingMode_) {
					// in buffering mode, new changes need to be buffered
					// separately in order not to mix with the old changes
					// so, we need to register the buffer with the reasoner
					// and create a new one
					reasoner_.registerAxiomLoader(bufferedChangesLoader_);
					bufferedChangesLoader_ = new OwlChangesLoaderFactory(
							this.secondaryProgressMonitor_);
				} else {
					// in non-buffering node the changes loader is already
					// registered, so we just need to
					// notify the reasoner about new axioms
					reasoner_.resetAxiomLoading();
				}
			}
			// proofs should be recomputed
			elkInferenceSet_.clear();
		} catch (ElkRuntimeException e) {
			throw elkConverter_.convert(e);
		}
	}

	@Override
	public Node<OWLClass> getBottomClassNode() {
		LOGGER_.trace("getBottomClassNode()");

		checkInterrupted();

		try {
			return getClassNode(objectFactory_.getOwlNothing());
		} catch (ElkUnsupportedReasoningTaskException e) {
			throw unsupportedOwlApiMethod("getBottomClassNode()",
					e.getMessage());
		} catch (ElkException e) {
			throw elkConverter_.convert(e);
		} catch (ElkRuntimeException e) {
			throw elkConverter_.convert(e);
		}
	}

	@Override
	public Node<OWLDataProperty> getBottomDataPropertyNode() {
		LOGGER_.trace("getBottomDataPropertyNode()");

		checkInterrupted();
		// TODO Provide implementation
		throw unsupportedOwlApiMethod("getBottomDataPropertyNode()");
	}

	@Override
	public Node<OWLObjectPropertyExpression> getBottomObjectPropertyNode() {
		LOGGER_.trace("getBottomObjectPropertyNode()");

		checkInterrupted();

		try {
			return getObjectPropertyNode(
					objectFactory_.getOwlBottomObjectProperty());
		} catch (final ElkUnsupportedReasoningTaskException e) {
			throw unsupportedOwlApiMethod("getBottomObjectPropertyNode()",
					e.getMessage());
		} catch (final ElkException e) {
			throw elkConverter_.convert(e);
		} catch (final ElkRuntimeException e) {
			throw elkConverter_.convert(e);
		}
	}

	@Override
	public BufferingMode getBufferingMode() {
		LOGGER_.trace("getBufferingMode()");

		return isBufferingMode_ ? BufferingMode.BUFFERING
				: BufferingMode.NON_BUFFERING;
	}

	@Override
	public NodeSet<OWLClass> getDataPropertyDomains(OWLDataProperty arg0,
			boolean arg1) throws InconsistentOntologyException,
			FreshEntitiesException, ReasonerInterruptedException,
			TimeOutException {
		LOGGER_.trace("getDataPropertyDomains(OWLDataProperty, boolean)");

		checkInterrupted();
		// TODO Provide implementation
		throw unsupportedOwlApiMethod("getDataPropertyDomains(OWLDataProperty, boolean)");
	}

	@Override
	public Set<OWLLiteral> getDataPropertyValues(OWLNamedIndividual arg0,
			OWLDataProperty arg1) throws InconsistentOntologyException,
			FreshEntitiesException, ReasonerInterruptedException,
			TimeOutException {
		LOGGER_.trace("getDataPropertyValues(OWLNamedIndividual, OWLDataProperty)");

		checkInterrupted();
		// TODO Provide implementation
		throw unsupportedOwlApiMethod("getDataPropertyValues(OWLNamedIndividual, OWLDataProperty)");
	}

	@Override
	public NodeSet<OWLNamedIndividual> getDifferentIndividuals(
			OWLNamedIndividual arg0) throws InconsistentOntologyException,
			FreshEntitiesException, ReasonerInterruptedException,
			TimeOutException {
		LOGGER_.trace("getDifferentIndividuals(OWLNamedIndividual)");

		checkInterrupted();
		// TODO Provide implementation
		throw unsupportedOwlApiMethod("getDifferentIndividuals(OWLNamedIndividual)");
	}

	@Override
	public NodeSet<OWLClass> getDisjointClasses(OWLClassExpression arg0)
			throws ReasonerInterruptedException, TimeOutException,
			FreshEntitiesException, InconsistentOntologyException {
		LOGGER_.trace("getDisjointClasses(OWLClassExpression)");

		checkInterrupted();
		// TODO Provide implementation
		throw unsupportedOwlApiMethod("getDisjointClasses(OWLClassExpression)");
	}

	@Override
	public NodeSet<OWLDataProperty> getDisjointDataProperties(
			OWLDataPropertyExpression arg0)
			throws InconsistentOntologyException, FreshEntitiesException,
			ReasonerInterruptedException, TimeOutException {
		LOGGER_.trace("getDisjointDataProperties(OWLDataPropertyExpression)");

		checkInterrupted();
		// TODO Provide implementation
		throw unsupportedOwlApiMethod("getDisjointDataProperties(OWLDataPropertyExpression)");
	}

	@Override
	public NodeSet<OWLObjectPropertyExpression> getDisjointObjectProperties(
			OWLObjectPropertyExpression arg0)
			throws InconsistentOntologyException, FreshEntitiesException,
			ReasonerInterruptedException, TimeOutException {
		LOGGER_.trace("getDisjointObjectProperties(OWLObjectPropertyExpression)");

		checkInterrupted();
		// TODO Provide implementation
		throw unsupportedOwlApiMethod("getDisjointObjectProperties(OWLObjectPropertyExpression)");
	}

	@Override
	public Node<OWLClass> getEquivalentClasses(OWLClassExpression ce)
			throws InconsistentOntologyException,
			ClassExpressionNotInProfileException, FreshEntitiesException,
			ReasonerInterruptedException, TimeOutException {
		LOGGER_.trace("getEquivalentClasses(OWLClassExpression)");

		checkInterrupted();
		try {
			return elkConverter_.convertClassNode(reasoner_
					.getEquivalentClasses(owlConverter_.convert(ce)));
		} catch (ElkUnsupportedReasoningTaskException e) {
			throw unsupportedOwlApiMethod(
					"getEquivalentClasses(OWLClassExpression)", e.getMessage());
		} catch (ElkException e) {
			throw elkConverter_.convert(e);
		} catch (ElkRuntimeException e) {
			throw elkConverter_.convert(e);
		}
	}

	@Override
	public Node<OWLDataProperty> getEquivalentDataProperties(
			OWLDataProperty arg0) throws InconsistentOntologyException,
			FreshEntitiesException, ReasonerInterruptedException,
			TimeOutException {
		LOGGER_.trace("getEquivalentDataProperties(OWLDataProperty)");

		checkInterrupted();
		// TODO Provide implementation
		throw unsupportedOwlApiMethod("getEquivalentDataProperties(OWLDataProperty)");
	}

	@Override
	public Node<OWLObjectPropertyExpression> getEquivalentObjectProperties(
			final OWLObjectPropertyExpression pe)
					throws InconsistentOntologyException,
					FreshEntitiesException, ReasonerInterruptedException,
					TimeOutException {
		LOGGER_.trace(
				"getEquivalentObjectProperties(OWLObjectPropertyExpression)");

		checkInterrupted();
		if (pe instanceof OWLObjectProperty) {
			try {
				return elkConverter_
						.convertObjectPropertyNode(reasoner_.getObjectPropertyNode(
								owlConverter_.convert((OWLObjectProperty) pe)));
			} catch (final ElkUnsupportedReasoningTaskException e) {
				throw unsupportedOwlApiMethod(
						"getEquivalentObjectProperties(OWLObjectPropertyExpression)",
						e.getMessage());
			} catch (final ElkException e) {
				throw elkConverter_.convert(e);
			} catch (final ElkRuntimeException e) {
				throw elkConverter_.convert(e);
			}
		}
		throw unsupportedOwlApiMethod(
				"getEquivalentObjectProperties(OWLObjectPropertyExpression)");
	}

	@Override
	public FreshEntityPolicy getFreshEntityPolicy() {
		LOGGER_.trace("getFreshEntityPolicy()");

		return reasoner_.getAllowFreshEntities() ? FreshEntityPolicy.ALLOW
				: FreshEntityPolicy.DISALLOW;
	}

	@Override
	public IndividualNodeSetPolicy getIndividualNodeSetPolicy() {
		LOGGER_.trace("getIndividualNodeSetPolicy()");

		return IndividualNodeSetPolicy.BY_NAME;
	}

	@Override
	public NodeSet<OWLNamedIndividual> getInstances(OWLClassExpression ce,
			boolean direct) throws InconsistentOntologyException,
			ClassExpressionNotInProfileException, FreshEntitiesException,
			ReasonerInterruptedException, TimeOutException {
		LOGGER_.trace("getInstances(OWLClassExpression, boolean)");

		checkInterrupted();
		try {
			return elkConverter_.convertIndividualNodes(reasoner_.getInstances(
					owlConverter_.convert(ce), direct));
		} catch (ElkUnsupportedReasoningTaskException e) {
			throw unsupportedOwlApiMethod(
					"getInstances(OWLClassExpression, boolean)", e.getMessage());
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
		LOGGER_.trace("getInverseObjectProperties(OWLObjectPropertyExpression)");

		checkInterrupted();
		// TODO Provide implementation
		throw unsupportedOwlApiMethod("getInverseObjectProperties(OWLObjectPropertyExpression)");
	}

	@Override
	public NodeSet<OWLClass> getObjectPropertyDomains(
			OWLObjectPropertyExpression arg0, boolean arg1)
			throws InconsistentOntologyException, FreshEntitiesException,
			ReasonerInterruptedException, TimeOutException {
		LOGGER_.trace("getObjectPropertyDomains(OWLObjectPropertyExpression, boolean)");

		checkInterrupted();
		// TODO Provide implementation
		throw unsupportedOwlApiMethod("getObjectPropertyDomains(OWLObjectPropertyExpression, boolean)");
	}

	@Override
	public NodeSet<OWLClass> getObjectPropertyRanges(
			OWLObjectPropertyExpression arg0, boolean arg1)
			throws InconsistentOntologyException, FreshEntitiesException,
			ReasonerInterruptedException, TimeOutException {
		LOGGER_.trace("getObjectPropertyRanges(OWLObjectPropertyExpression, boolean)");

		checkInterrupted();
		// TODO Provide implementation
		throw unsupportedOwlApiMethod("getObjectPropertyRanges(OWLObjectPropertyExpression, boolean)");
	}

	@Override
	public NodeSet<OWLNamedIndividual> getObjectPropertyValues(
			OWLNamedIndividual arg0, OWLObjectPropertyExpression arg1)
			throws InconsistentOntologyException, FreshEntitiesException,
			ReasonerInterruptedException, TimeOutException {
		LOGGER_.trace("getObjectPropertyValues(OWLNamedIndividual, OWLObjectPropertyExpression)");

		checkInterrupted();
		// TODO Provide implementation
		throw unsupportedOwlApiMethod("getObjectPropertyValues(OWLNamedIndividual, OWLObjectPropertyExpression)");
	}

	@Override
	public Set<OWLAxiom> getPendingAxiomAdditions() {
		LOGGER_.trace("getPendingAxiomAdditions()");
		return bufferedChangesLoader_.getPendingAxiomAdditions();
	}

	@Override
	public Set<OWLAxiom> getPendingAxiomRemovals() {
		LOGGER_.trace("getPendingAxiomRemovals()");
		return bufferedChangesLoader_.getPendingAxiomRemovals();
	}

	@Override
	public List<OWLOntologyChange> getPendingChanges() {
		LOGGER_.trace("getPendingChanges()");
		return bufferedChangesLoader_.getPendingChanges();
	}

	@Override
	public Set<InferenceType> getPrecomputableInferenceTypes() {
		LOGGER_.trace("getPrecomputableInferenceTypes()");
		return new HashSet<InferenceType>(Arrays.asList(
				InferenceType.CLASS_ASSERTIONS, InferenceType.CLASS_HIERARCHY,
				InferenceType.OBJECT_PROPERTY_HIERARCHY));
	}

	@Override
	public String getReasonerName() {
		LOGGER_.trace("getReasonerName()");
		return ElkReasoner.class.getPackage().getImplementationTitle();
	}

	@Override
	public Version getReasonerVersion() {
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
		LOGGER_.trace("getRootOntology()");
		return owlOntology_;
	}

	@Override
	public Node<OWLNamedIndividual> getSameIndividuals(OWLNamedIndividual arg0)
			throws InconsistentOntologyException, FreshEntitiesException,
			ReasonerInterruptedException, TimeOutException {
		LOGGER_.trace("getSameIndividuals(OWLNamedIndividual)");
		checkInterrupted();
		// TODO This needs to be updated when we support nominals
		return new OWLNamedIndividualNode(arg0);
	}

	@Override
	public NodeSet<OWLClass> getSubClasses(OWLClassExpression ce, boolean direct)
			throws ReasonerInterruptedException, TimeOutException,
			FreshEntitiesException, InconsistentOntologyException,
			ClassExpressionNotInProfileException {
		LOGGER_.trace("getSubClasses(OWLClassExpression, boolean)");
		checkInterrupted();
		try {
			return elkConverter_.convertClassNodes(reasoner_.getSubClasses(
					owlConverter_.convert(ce), direct));
		} catch (ElkUnsupportedReasoningTaskException e) {
			throw unsupportedOwlApiMethod(
					"getSubClasses(OWLClassExpression, boolean)",
					e.getMessage());
		} catch (ElkException e) {
			throw elkConverter_.convert(e);
		} catch (ElkRuntimeException e) {
			throw elkConverter_.convert(e);
		}
	}

	@Override
	public NodeSet<OWLDataProperty> getSubDataProperties(OWLDataProperty arg0,
			boolean arg1) throws InconsistentOntologyException,
			FreshEntitiesException, ReasonerInterruptedException,
			TimeOutException {
		LOGGER_.trace("getSubDataProperties(OWLDataProperty, boolean)");
		checkInterrupted();
		// TODO Provide implementation
		throw unsupportedOwlApiMethod("getSubDataProperties(OWLDataProperty, boolean)");
	}

	@Override
	public NodeSet<OWLObjectPropertyExpression> getSubObjectProperties(
			final OWLObjectPropertyExpression pe, final boolean direct)
					throws InconsistentOntologyException,
					FreshEntitiesException, ReasonerInterruptedException,
					TimeOutException {
		LOGGER_.trace(
				"getSubObjectProperties(OWLObjectPropertyExpression, boolean)");

		checkInterrupted();

		if (pe instanceof OWLObjectProperty) {
			try {
				return elkConverter_
						.convertObjectPropertyNodes(reasoner_.getSubObjectProperties(
								owlConverter_.convert((OWLObjectProperty) pe),
								direct));
			} catch (final ElkUnsupportedReasoningTaskException e) {
				throw unsupportedOwlApiMethod(
						"getSubObjectProperties(OWLObjectPropertyExpression, boolean)",
						e.getMessage());
			} catch (final ElkException e) {
				throw elkConverter_.convert(e);
			} catch (final ElkRuntimeException e) {
				throw elkConverter_.convert(e);
			}
		}
		throw unsupportedOwlApiMethod(
				"getSubObjectProperties(OWLObjectPropertyExpression, boolean)");
	}

	@Override
	public NodeSet<OWLClass> getSuperClasses(OWLClassExpression ce,
			boolean direct) throws InconsistentOntologyException,
			ClassExpressionNotInProfileException, FreshEntitiesException,
			ReasonerInterruptedException, TimeOutException {
		LOGGER_.trace("getSuperClasses(OWLClassExpression, boolean)");
		checkInterrupted();
		try {
			return elkConverter_.convertClassNodes(reasoner_.getSuperClasses(
					owlConverter_.convert(ce), direct));
		} catch (ElkUnsupportedReasoningTaskException e) {
			throw unsupportedOwlApiMethod(
					"getSuperClasses(OWLClassExpression, boolean)",
					e.getMessage());
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
		LOGGER_.trace("getSuperDataProperties(OWLDataProperty, boolean)");
		checkInterrupted();
		// TODO Provide implementation
		throw unsupportedOwlApiMethod("getSuperDataProperties(OWLDataProperty, boolean)");
	}

	@Override
	public NodeSet<OWLObjectPropertyExpression> getSuperObjectProperties(
			final OWLObjectPropertyExpression pe, final boolean direct)
					throws InconsistentOntologyException,
					FreshEntitiesException, ReasonerInterruptedException,
					TimeOutException {
		LOGGER_.trace(
				"getSuperObjectProperties(OWLObjectPropertyExpression, boolean)");

		checkInterrupted();

		if (pe instanceof OWLObjectProperty) {
			try {
				return elkConverter_.convertObjectPropertyNodes(
						reasoner_.getSuperObjectProperties(
								owlConverter_.convert((OWLObjectProperty) pe),
								direct));
			} catch (final ElkUnsupportedReasoningTaskException e) {
				throw unsupportedOwlApiMethod(
						"getSuperObjectProperties(OWLObjectPropertyExpression, boolean)",
						e.getMessage());
			} catch (final ElkException e) {
				throw elkConverter_.convert(e);
			} catch (final ElkRuntimeException e) {
				throw elkConverter_.convert(e);
			}
		}
		throw unsupportedOwlApiMethod(
				"getSuperObjectProperties(OWLObjectPropertyExpression, boolean)");
	}

	@Override
	public long getTimeOut() {
		LOGGER_.trace("getTimeOut()");
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public Node<OWLClass> getTopClassNode() {
		LOGGER_.trace("getTopClassNode()");
		checkInterrupted();
		try {
			return getClassNode(objectFactory_.getOwlThing());
		} catch (ElkUnsupportedReasoningTaskException e) {
			throw unsupportedOwlApiMethod("getTopClassNode()", e.getMessage());
		} catch (ElkException e) {
			throw elkConverter_.convert(e);
		} catch (ElkRuntimeException e) {
			throw elkConverter_.convert(e);
		}
	}

	@Override
	public Node<OWLDataProperty> getTopDataPropertyNode() {
		LOGGER_.trace("getTopDataPropertyNode()");
		checkInterrupted();
		// TODO Provide implementation
		throw unsupportedOwlApiMethod("getTopDataPropertyNode()");
	}

	@Override
	public Node<OWLObjectPropertyExpression> getTopObjectPropertyNode() {
		LOGGER_.trace("getTopObjectPropertyNode()");

		checkInterrupted();

		try {
			return getObjectPropertyNode(
					objectFactory_.getOwlTopObjectProperty());
		} catch (final ElkUnsupportedReasoningTaskException e) {
			throw unsupportedOwlApiMethod("getTopObjectPropertyNode()",
					e.getMessage());
		} catch (final ElkException e) {
			throw elkConverter_.convert(e);
		} catch (final ElkRuntimeException e) {
			throw elkConverter_.convert(e);
		}
	}

	@Override
	public NodeSet<OWLClass> getTypes(OWLNamedIndividual ind, boolean direct)
			throws InconsistentOntologyException, FreshEntitiesException,
			ReasonerInterruptedException, TimeOutException {
		LOGGER_.trace("getTypes(OWLNamedIndividual, boolean)");
		checkInterrupted();
		try {
			return elkConverter_.convertClassNodes(reasoner_.getTypes(
					owlConverter_.convert(ind), direct));
		} catch (ElkUnsupportedReasoningTaskException e) {
			throw unsupportedOwlApiMethod(
					"getTypes(OWLNamedIndividual, boolean)", e.getMessage());
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
		LOGGER_.trace("getUnsatisfiableClasses()");
		checkInterrupted();
		try {
			return getClassNode(objectFactory_.getOwlNothing());
		} catch (ElkUnsupportedReasoningTaskException e) {
			throw unsupportedOwlApiMethod("getUnsatisfiableClasses()",
					e.getMessage());
		} catch (ElkException e) {
			throw elkConverter_.convert(e);
		} catch (ElkRuntimeException e) {
			throw elkConverter_.convert(e);
		}

	}

	@Override
	public void interrupt() {
		LOGGER_.trace("interrupt()");
		reasoner_.interrupt();
	}

	@Override
	public boolean isConsistent() throws ReasonerInterruptedException,
			TimeOutException {
		LOGGER_.trace("isConsistent()");
		try {
			return !reasoner_.isInconsistent();
		} catch (ElkUnsupportedReasoningTaskException e) {
			throw unsupportedOwlApiMethod("isConsistent()", e.getMessage());
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
		LOGGER_.trace("isEntailed(OWLAxiom)");
		checkInterrupted();
		// TODO Provide implementation
		throw unsupportedOwlApiMethod("isEntailed(OWLAxiom)");
	}

	@Override
	public boolean isEntailed(Set<? extends OWLAxiom> arg0)
			throws ReasonerInterruptedException,
			UnsupportedEntailmentTypeException, TimeOutException,
			AxiomNotInProfileException, FreshEntitiesException,
			InconsistentOntologyException {
		LOGGER_.trace("isEntailed(Set<? extends OWLAxiom>)");
		checkInterrupted();
		// TODO Provide implementation
		throw unsupportedOwlApiMethod("isEntailed(Set<? extends OWLAxiom>)");
	}

	@Override
	public boolean isEntailmentCheckingSupported(AxiomType<?> arg0) {
		LOGGER_.trace("isEntailmentCheckingSupported(AxiomType<?>)");
		return false;
	}

	@Override
	public boolean isPrecomputed(InferenceType inferenceType) {
		LOGGER_.trace("isPrecomputed(InferenceType)");
		if (inferenceType.equals(InferenceType.CLASS_HIERARCHY))
			return reasoner_.doneTaxonomy();
		if (inferenceType.equals(InferenceType.CLASS_ASSERTIONS))
			return reasoner_.doneInstanceTaxonomy();
		if (inferenceType.equals(InferenceType.OBJECT_PROPERTY_HIERARCHY)) {
			return reasoner_.doneObjectPropertyTaxonomy();
		}

		return false;
	}

	@Override
	public boolean isSatisfiable(OWLClassExpression classExpression)
			throws ReasonerInterruptedException, TimeOutException,
			ClassExpressionNotInProfileException, FreshEntitiesException,
			InconsistentOntologyException {
		LOGGER_.trace("isSatisfiable(OWLClassExpression)");
		checkInterrupted();
		try {
			return reasoner_.isSatisfiable(owlConverter_
					.convert(classExpression));
		} catch (ElkUnsupportedReasoningTaskException e) {
			throw unsupportedOwlApiMethod("isSatisfiable(classExpression)",
					e.getMessage());
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
		LOGGER_.trace("precomputeInferences(InferenceType...)");
		checkInterrupted();
		// we use the main progress monitor only here
		this.reasoner_.setProgressMonitor(this.mainProgressMonitor_);
		try {
			for (InferenceType inferenceType : inferenceTypes) {
				if (inferenceType.equals(InferenceType.CLASS_HIERARCHY))
					reasoner_.getTaxonomy();
				else if (inferenceType.equals(InferenceType.CLASS_ASSERTIONS))
					reasoner_.getInstanceTaxonomy();
				else if (inferenceType
						.equals(InferenceType.OBJECT_PROPERTY_HIERARCHY)) {
					reasoner_.getObjectPropertyTaxonomy();
				}
			}
		} catch (ElkUnsupportedReasoningTaskException e) {
			throw unsupportedOwlApiMethod(
					"precomputeInferences(inferenceTypes)", e.getMessage());
		} catch (ElkException e) {
			throw elkConverter_.convert(e);
		} catch (ElkRuntimeException e) {
			throw elkConverter_.convert(e);
		} finally {
			this.reasoner_.setProgressMonitor(this.secondaryProgressMonitor_);
		}

	}
	
	protected class OntologyChangeListener implements OWLOntologyChangeListener {
		@Override
		public void ontologiesChanged(List<? extends OWLOntologyChange> changes)
				throws OWLException {
			Set<OWLOntology> importClosure = null;
			for (OWLOntologyChange change : changes) {
				OWLOntology changedOntology = change.getOntology();
				if (!changedOntology.equals(owlOntology_)) {
					if (importClosure == null) {
						importClosure = owlOntology_.getImportsClosure();
					}
					if (!importClosure.contains(changedOntology)) {
						LOGGER_.trace("Ignoring the change not applicable to the current ontology: {}"
								+ change);
						continue;
					}
				}

				if (!change.isAxiomChange()) {
					LOGGER_.trace(
							"Non-axiom change: {}\n The ontology will be reloaded.",
							change);
					// cannot handle non-axiom changes incrementally
					ontologyReloadRequired_ = true;
				} else {
					bufferedChangesLoader_.registerChange(change);					
				}
			}
			if (!isBufferingMode_)
				flush();
		}

	}

	private class OntologyChangeProgressListener implements
			OWLOntologyChangeProgressListener {

		private static final long serialVersionUID = -609834181047406971L;

		@Override
		public void begin(int size) {
			if (isBufferingMode_ && loadBeforeChanges_) {
				try {
					LOGGER_.trace("force initial loading");
					reasoner_.forceLoading();
					loadBeforeChanges_ = false;
				} catch (ElkException e) {
					throw elkConverter_.convert(e);
				}
			}
		}

		@Override
		public void appliedChange(OWLOntologyChange change) {
			// nothing to do

		}

		@Override
		public void end() {
			// nothing to do
		}
	}

}
