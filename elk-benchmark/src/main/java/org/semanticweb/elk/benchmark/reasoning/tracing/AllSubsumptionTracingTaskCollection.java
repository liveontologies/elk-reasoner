/*
 * #%L
 * ELK Bencharking Package
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
/**
 * 
 */
package org.semanticweb.elk.benchmark.reasoning.tracing;

import java.io.File;
import java.io.IOException;
import java.io.StringWriter;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import org.semanticweb.elk.benchmark.BenchmarkUtils;
import org.semanticweb.elk.benchmark.Metrics;
import org.semanticweb.elk.benchmark.Task;
import org.semanticweb.elk.benchmark.TaskCollection;
import org.semanticweb.elk.benchmark.TaskException;
import org.semanticweb.elk.loading.AxiomLoader;
import org.semanticweb.elk.loading.Owl2StreamLoader;
import org.semanticweb.elk.owl.exceptions.ElkException;
import org.semanticweb.elk.owl.interfaces.ElkClass;
import org.semanticweb.elk.owl.parsing.javacc.Owl2FunctionalStyleParserFactory;
import org.semanticweb.elk.owl.predefined.PredefinedElkIri;
import org.semanticweb.elk.reasoner.Reasoner;
import org.semanticweb.elk.reasoner.ReasonerFactory;
import org.semanticweb.elk.reasoner.config.ReasonerConfiguration;
import org.semanticweb.elk.reasoner.saturation.tracing.TRACE_MODE;
import org.semanticweb.elk.reasoner.saturation.tracing.TracedConclusionVisitor;
import org.semanticweb.elk.reasoner.stages.RuleAndConclusionCountMeasuringExecutor;
import org.semanticweb.elk.reasoner.stages.SimpleStageExecutor;
import org.semanticweb.elk.reasoner.taxonomy.TaxonomyPrinter;
import org.semanticweb.elk.reasoner.taxonomy.model.Taxonomy;
import org.semanticweb.elk.reasoner.taxonomy.model.TaxonomyNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A task to trace all atomic subsumptions
 * 
 * @author Pavel Klinov
 * 
 *         pavel.klinov@uni-ulm.de
 */
public class AllSubsumptionTracingTaskCollection implements TaskCollection {

	private static final Logger LOGGER_ = LoggerFactory.getLogger(AllSubsumptionTracingTaskCollection.class);
	
	private final String ontologyFile_;
	private Reasoner reasoner_;
	private final ReasonerConfiguration reasonerConfig_;
	private final Metrics metrics_ = new Metrics();
	
	public AllSubsumptionTracingTaskCollection(String... args) {
		ontologyFile_ = args[0];
		reasonerConfig_ = BenchmarkUtils.getReasonerConfiguration(args);
	}
	
	@Override
	public Collection<Task> getTasks() throws TaskException {
		// classify the ontology and instantiate tracing tasks
		Taxonomy<ElkClass> taxonomy = loadAndClassify(ontologyFile_);
		
		if (LOGGER_.isTraceEnabled()) {
			StringWriter writer = new StringWriter();
			
			try {
				TaxonomyPrinter.dumpClassTaxomomy(taxonomy, writer, false);
			} catch (IOException e) {
				throw new TaskException(e);
			}
			
			writer.flush();
			LOGGER_.debug("{}", writer.getBuffer().toString());
		}
		
		// TODO lazy task collection would be better for performance
		return createTracingTasks(taxonomy);
	}
	
	private Collection<Task> createTracingTasks(Taxonomy<ElkClass> taxonomy) {
		List<Task> tasks = new LinkedList<Task>();
		Queue<TaxonomyNode<ElkClass>> toDo = new LinkedList<TaxonomyNode<ElkClass>>();
		
		toDo.add(taxonomy.getBottomNode());
		
		for (;;) {
			TaxonomyNode<ElkClass> next = toDo.poll();
			
			if (next == null) {
				break;
			}
			
			addEquivalentClassesTracingTasks(next, tasks);
			
			for (TaxonomyNode<ElkClass> superNode : next.getDirectSuperNodes()) {
				addTracingTasksForDirectSuperClasses(next, superNode, tasks);
				toDo.add(superNode);
			}
		}
		
		LOGGER_.debug("{} subsumptions to trace", tasks.size());
		
		return tasks;
	}

	private void addTracingTasksForDirectSuperClasses(
			TaxonomyNode<ElkClass> node, TaxonomyNode<ElkClass> superNode,
			List<Task> tasks) {
		for (ElkClass sub : node.getMembers()) {
			if (sub.getIri() == PredefinedElkIri.OWL_NOTHING.get()) {
				continue;
			}
			
			for (ElkClass sup : superNode.getMembers()) {
				if (sub.getIri() == PredefinedElkIri.OWL_THING.get()) {
					continue;
				}
				
				if (sub != sup) {
					tasks.add(new TracingTask(reasoner_, sub, sup));
				}
			}
		}
		
	}

	private void addEquivalentClassesTracingTasks(TaxonomyNode<ElkClass> node, List<Task> tasks) {
		addTracingTasksForDirectSuperClasses(node, node, tasks);
	}

	private Taxonomy<ElkClass> loadAndClassify(String ontologyFile) throws TaskException {
		try {
			File ontFile = BenchmarkUtils.getFile(ontologyFile);

			AxiomLoader loader = new Owl2StreamLoader(
					new Owl2FunctionalStyleParserFactory(), ontFile);
			//TODO subclass the executor to measure the number of traced contexts
			reasoner_ = new ReasonerFactory().createReasoner(loader,
					//new SimpleStageExecutor(),
					new RuleAndConclusionCountMeasuringExecutor( new SimpleStageExecutor(), metrics_),
					//new TimingStageExecutor(new SimpleStageExecutor(), metrics_),
					reasonerConfig_);
			
			return reasoner_.getTaxonomy();
			
		} catch (Exception e) {
			throw new TaskException(e);
		}
	}

	@Override
	public Metrics getMetrics() {
		return metrics_;
	}
	
	@Override
	public void dispose() {
		try {
			reasoner_.shutdown();
		} catch (InterruptedException e) {
			// who cares..
			throw new RuntimeException(e);
		}
	}

	/**
	 * 
	 */
	private static class TracingTask implements Task {

		private final Reasoner reasoner_;
		private final ElkClass subsumee_;
		private final ElkClass subsumer_;
		
		TracingTask(Reasoner r, ElkClass sub, ElkClass sup) {
			reasoner_ = r;
			subsumee_ = sub;
			subsumer_ = sup;
		}
		
		@Override
		public String getName() {
			return "Subsumption tracing";// + subsumee_ + " => " + subsumer_;
		}

		@Override
		public void prepare() throws TaskException {
			reasoner_.resetTraceState();
		}

		@Override
		public void run() throws TaskException {
			try {
				reasoner_.explainSubsumption(subsumee_, subsumer_, TracedConclusionVisitor.DUMMY, TRACE_MODE.RECURSIVE);
			} catch (ElkException e) {
				throw new TaskException(e);
			}
		}

		@Override
		public void dispose() {
		}

		@Override
		public Metrics getMetrics() {
			return null;
		}
		
	}

}
