package org.semanticweb.elk.benchmark.reasoning.owlapi;

/*
 * #%L
 * ELK Benchmarking Package
 * $Id:$
 * $HeadURL:$
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

import java.io.File;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Set;

import org.apache.log4j.Logger;
import org.semanticweb.elk.benchmark.AllFilesTaskCollection;
import org.semanticweb.elk.benchmark.Metrics;
import org.semanticweb.elk.benchmark.Task;
import org.semanticweb.elk.benchmark.TaskException;
import org.semanticweb.elk.benchmark.reasoning.IncrementalClassificationMultiDeltas;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.OWLLogicalAxiom;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyChange;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.reasoner.InferenceType;
import org.semanticweb.owlapi.reasoner.OWLReasoner;
import org.semanticweb.owlapi.reasoner.OWLReasonerFactory;

/**
 * Incrementally classifies an ontology wrt multiple deltas. Expects a folder
 * with a single file (the initial version of the ontology) and multiple folders
 * with additions and deletions (with suffixes ADDITION_SUFFIX and
 * DELETION_SUFFIX, resp.)
 * 
 * @author Pavel Klinov
 * 
 *         pavel.klinov@uni-ulm.de
 */
public abstract class OWLAPIIncrementalClassificationMultiDeltas extends
		AllFilesTaskCollection {

	// logger for this class
	protected static final Logger LOGGER_ = Logger.getLogger(IncrementalClassificationMultiDeltas.class);
	
	private static final String ADDITION_SUFFIX = "delta-plus";
	private static final String DELETION_SUFFIX = "delta-minus";
	public static final String DELETED_AXIOM_COUNT = "deleted-axioms.count";
	public static final String ADDED_AXIOM_COUNT = "added-axioms.count";

	protected OWLReasoner reasoner;
	protected OWLOntologyManager manager;
	protected OWLOntology initial;
	protected final Metrics metrics = new Metrics();

	public OWLAPIIncrementalClassificationMultiDeltas(String[] args) {
		super(args);
	}
	
	protected abstract OWLReasonerFactory getOWLReasonerFactory();
	
	@Override
	public Task instantiateSubTask(String[] args) throws TaskException {
		File source = new File(args[0]);

		if (!source.exists()) {
			throw new TaskException("Wrong source file/dir " + args[0]);
		}

		if (source.isFile()) {
			if (reasoner != null) {
				dispose();
			}
			// initial classification, argument is the first ontology
			return getFirstTimeClassificationTask(source);
		} else {
			// incremental classification, argument is a folder with the
			// positive and the negative delta
			return getIncrementalClassificationTask(source);
		}
	}

	protected Task getFirstTimeClassificationTask(File source) {
		return new ClassifyFirstTime(source);
	}

	protected Task getIncrementalClassificationTask(File source) {
		return new ClassifyIncrementally(source);
	}

	@Override
	protected File[] sortFiles(File[] files) {
		// There should be one file and multiple dirs.
		// the file should go first, the rest should be sorted by name
		File file = null;
		File[] result = new File[files.length];

		for (int i = 0; i < files.length; i++) {
			if (files[i].isDirectory()) {
				result[i] = files[i];
			} else {
				file = files[i];
			}
		}

		Arrays.sort(result, new Comparator<File>() {

			@Override
			public int compare(File o1, File o2) {
				if (o1 == null) {
					return -1;
				} else if (o2 == null) {
					return 1;
				} else {
					return o1.getName().compareTo(o2.getName());
				}
			}
		});

		result[0] = file;

		return result;
	}

	@Override
	public Metrics getMetrics() {
		return metrics;
	}

	@Override
	public void dispose() {
		if (reasoner != null) {
			reasoner.dispose();
			reasoner = null;
		}
	}

	/**
	 * Classifies the initial version of the ontology
	 * 
	 * @author Pavel Klinov
	 * 
	 *         pavel.klinov@uni-ulm.de
	 */
	protected class ClassifyFirstTime implements Task {

		private final File ontologyFile_;

		ClassifyFirstTime(File file) {
			ontologyFile_ = file;
		}

		@Override
		public String getName() {
			return "Classify first ontology: " + ontologyFile_.getName();
		}

		@Override
		public void prepare() throws TaskException {
			try {
				//load the initial version of the ontology
				manager = OWLManager.createOWLOntologyManager();
				initial = manager.loadOntologyFromOntologyDocument(ontologyFile_);
				//create the OWL reasoner
				reasoner = getOWLReasonerFactory().createReasoner(initial);
			} catch (OWLOntologyCreationException e) {
				throw new TaskException(e);
			}
		}

		@Override
		public void run() throws TaskException {
			try {
				reasoner.precomputeInferences(InferenceType.CLASS_HIERARCHY);
			} catch (Exception e) {
				throw new TaskException("Exception during initial classification", e);
			}
		}

		@Override
		public void dispose() {
		}

		@Override
		public Metrics getMetrics() {
			return metrics;
		}
	}

	/**
	 * Applies the deltas for the next version
	 * 
	 * @author Pavel Klinov
	 * 
	 *         pavel.klinov@uni-ulm.de
	 */
	protected class ClassifyIncrementally implements Task {

		private final File deltaDir_;
		
		private boolean measureTime;

		ClassifyIncrementally(File dir) {
			deltaDir_ = dir;
		}

		@Override
		public String getName() {
			return "Classify incrementally";
		}

		@Override
		public void prepare() throws TaskException {
			// load positive and negative deltas via the OWL API
			try {
				OWLOntology additions = loadFromDisk(ADDITION_SUFFIX);
				OWLOntology deletions = loadFromDisk(DELETION_SUFFIX);
				
				applyToReasoner(additions.getLogicalAxioms(), true);
				applyToReasoner(deletions.getLogicalAxioms(), false);
				
				measureTime = additions.getLogicalAxiomCount() > 0 && deletions.getLogicalAxiomCount() > 0;
				
			} catch (OWLOntologyCreationException e) {
				throw new TaskException(e);
			}
		}

		private void applyToReasoner(Set<OWLLogicalAxiom> axioms, boolean add) {
			List<OWLOntologyChange> changes = add ? manager.addAxioms(initial, axioms) : manager.removeAxioms(initial, axioms);
			
			manager.applyChanges(changes);
			reasoner.flush();
		}

		private OWLOntology loadFromDisk(String suffix) throws OWLOntologyCreationException {
			OWLOntologyManager changeManager = OWLManager.createOWLOntologyManager();
			
			for (File delta : deltaDir_.listFiles()) {
				if (delta.getName().endsWith(suffix)) {
					return changeManager.loadOntologyFromOntologyDocument(delta);
				}
			}
			
			return null;
		}


		@Override
		public void run() throws TaskException {
			long ts = System.currentTimeMillis();
			
			reasoner.precomputeInferences(InferenceType.CLASS_HIERARCHY);

			ts = System.currentTimeMillis() - ts;
			
			if (measureTime) {
				metrics.incrementRunCount();
				metrics.updateLongMetric("incremental update wall time", ts);
			}
		}

		@Override
		public void dispose() {
		}

		@Override
		public Metrics getMetrics() {
			return metrics;
		}
		
	}
}
