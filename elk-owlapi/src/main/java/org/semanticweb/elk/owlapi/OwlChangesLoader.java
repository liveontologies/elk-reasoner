/*
 * #%L
 * ELK OWL API Binding
 * 
 * $Id$
 * $HeadURL$
 * %%
 * Copyright (C) 2011 - 2012 Department of Computer Science, University of Oxford
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
package org.semanticweb.elk.owlapi;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Set;

import org.apache.log4j.Logger;
import org.semanticweb.elk.loading.AxiomChangeListener;
import org.semanticweb.elk.loading.ChangesLoader;
import org.semanticweb.elk.loading.ElkAxiomChange;
import org.semanticweb.elk.loading.ElkLoadingException;
import org.semanticweb.elk.loading.Loader;
import org.semanticweb.elk.loading.SimpleElkAxiomChange;
import org.semanticweb.elk.owl.interfaces.ElkAxiom;
import org.semanticweb.elk.owl.visitors.ElkAxiomProcessor;
import org.semanticweb.elk.owlapi.wrapper.OwlConverter;
import org.semanticweb.elk.reasoner.ProgressMonitor;
import org.semanticweb.owlapi.model.AddAxiom;
import org.semanticweb.owlapi.model.AddImport;
import org.semanticweb.owlapi.model.AddOntologyAnnotation;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLOntologyChange;
import org.semanticweb.owlapi.model.OWLOntologyChangeVisitorEx;
import org.semanticweb.owlapi.model.RemoveAxiom;
import org.semanticweb.owlapi.model.RemoveImport;
import org.semanticweb.owlapi.model.RemoveOntologyAnnotation;
import org.semanticweb.owlapi.model.SetOntologyID;

/**
 * An {@link ChangesLoader} that accumulates the {@link OWLOntologyChange} and
 * provides them by converting through {@link OwlConverter}
 * 
 * @author "Yevgeny Kazakov"
 * 
 */
public class OwlChangesLoader implements ChangesLoader {

	// logger for this class
	private static final Logger LOGGER_ = Logger
			.getLogger(OwlChangesLoader.class);

	private static final OwlConverter OWL_CONVERTER_ = OwlConverter
			.getInstance();

	private final ProgressMonitor progressMonitor;

	/** list to accumulate the unprocessed changes to the ontology */
	protected final LinkedList<OWLOntologyChange> pendingChanges;
	
	private AxiomChangeListener changeListener_ = null;
	
	protected final LinkedList<ElkAxiomChange> changesToNotifyListener_;

	OwlChangesLoader(ProgressMonitor progressMonitor) {
		this.progressMonitor = progressMonitor;
		this.pendingChanges = new LinkedList<OWLOntologyChange>();
		this.changesToNotifyListener_ = new LinkedList<ElkAxiomChange>();
	}
	

	@Override
	public void registerChangeListener(AxiomChangeListener listener) {
		changeListener_ = listener;
	}


	@Override
	public Loader getLoader(final ElkAxiomProcessor axiomInserter,
			final ElkAxiomProcessor axiomDeleter) {
		return new Loader() {
			@Override
			public void load() throws ElkLoadingException {
				if (!pendingChanges.isEmpty()) {
					String status = "Loading of Changes";
					progressMonitor.start(status);
					int changesCount = pendingChanges.size();
					if (LOGGER_.isTraceEnabled())
						LOGGER_.trace(status + ": " + changesCount);
					int currentAxiom = 0;
					for (;;) {
						if (Thread.currentThread().isInterrupted())
							break;
						OWLOntologyChange change = ((Queue<OWLOntologyChange>) pendingChanges)
								.poll();
						if (change == null)
							break;
						if (!change.isAxiomChange()) {
							ElkLoadingException exception = new ElkLoadingException(
									"Cannot apply non-axiom change!");
							LOGGER_.error(exception);
							throw exception;
						}
						
						ElkLoadingException error = change.accept(new BasicAxiomChangeVisitor<ElkLoadingException>() {
							
							@Override
							public ElkLoadingException visit(RemoveAxiom arg) {
								ElkAxiom elkAxiom = OWL_CONVERTER_.convert(arg.getAxiom());
								
								axiomDeleter.visit( elkAxiom);
								
								if (LOGGER_.isTraceEnabled())
									LOGGER_.trace("removing " + arg.getAxiom());
								
								return null;
							}
							
							@Override
							public ElkLoadingException visit(AddAxiom arg) {
								ElkAxiom elkAxiom = OWL_CONVERTER_.convert(arg.getAxiom());
								
								axiomInserter.visit( elkAxiom);
								
								if (LOGGER_.isTraceEnabled())
									LOGGER_.trace("adding " + arg.getAxiom());
								
								return null;
							}

							@Override
							protected ElkLoadingException defaultVisit(OWLOntologyChange arg) {
								return new ElkLoadingException("Ontology change " + arg.toString() + " is not supported");
							}
						});
						
						if (error != null) {
							LOGGER_.error(error);
							
							throw error;
						}
						
						currentAxiom++;
						progressMonitor.report(currentAxiom, changesCount);
					}
					progressMonitor.finish();
				}
			}

			@Override
			public void dispose() {
				// nothing was allocated by this loader
			}
		};
	}
	
	void flush() {
		// notify the listener of all buffered changes
		if (changeListener_ != null) {
			for (ElkAxiomChange change : changesToNotifyListener_) {
				changeListener_.notify(change);
			}

			changesToNotifyListener_.clear();
		}
	}

	void registerChange(OWLOntologyChange change) {
		if (LOGGER_.isTraceEnabled()) {
			LOGGER_.trace("Registering change: " + change);
		}
		
		pendingChanges.add(change);
		// convert this change to the Elk's axiom change and notify the listener
		// TODO save the converted Elk axiom to the queue to not convert the second time?
		if (changeListener_ != null) {
			change.accept(new BasicAxiomChangeVisitor<Object>() {

				@Override
				public Object visit(RemoveAxiom arg) {
					changesToNotifyListener_.add(new SimpleElkAxiomChange(
							OWL_CONVERTER_.convert(arg.getAxiom()), -1));

					return null;
				}

				@Override
				public Object visit(AddAxiom arg) {
					changesToNotifyListener_.add(new SimpleElkAxiomChange(
							OWL_CONVERTER_.convert(arg.getAxiom()), 1));

					return null;
				}
			});
		}
	}

	Set<OWLAxiom> getPendingAxiomAdditions() {
		Set<OWLAxiom> added = new HashSet<OWLAxiom>();
		for (OWLOntologyChange change : pendingChanges) {
			if (change instanceof AddAxiom) {
				added.add(change.getAxiom());
			}
		}
		return added;
	}

	Set<OWLAxiom> getPendingAxiomRemovals() {
		Set<OWLAxiom> removed = new HashSet<OWLAxiom>();
		for (OWLOntologyChange change : pendingChanges) {
			if (change instanceof RemoveAxiom) {
				removed.add(change.getAxiom());
			}
		}
		return removed;
	}

	List<OWLOntologyChange> getPendingChanges() {
		return pendingChanges;
	}

	
	private abstract static class BasicAxiomChangeVisitor<O> implements OWLOntologyChangeVisitorEx<O> {

		@Override
		public abstract O visit(AddAxiom arg);

		@Override
		public abstract O visit(RemoveAxiom arg);
		
		protected O defaultVisit(OWLOntologyChange arg) {
			return null;
		}

		@Override
		public O visit(SetOntologyID arg0) {
			return defaultVisit(arg0);
		}

		@Override
		public O visit(AddImport arg0) {
			return defaultVisit(arg0);
		}

		@Override
		public O visit(RemoveImport arg0) {
			return defaultVisit(arg0);
		}

		@Override
		public O visit(AddOntologyAnnotation arg0) {
			return defaultVisit(arg0);
		}

		@Override
		public O visit(RemoveOntologyAnnotation arg0) {
			return defaultVisit(arg0);
		}		
	}
}
