package org.semanticweb.elk.reasoner.saturation.properties;
/*
 * #%L
 * ELK Reasoner
 * $Id:$
 * $HeadURL:$
 * %%
 * Copyright (C) 2011 - 2013 Department of Computer Science, University of Oxford
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

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedBinaryPropertyChain;
import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedObjectProperty;
import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedPropertyChain;
import org.semanticweb.elk.reasoner.indexing.visitors.IndexedPropertyChainVisitor;
import org.semanticweb.elk.reasoner.saturation.tracing.TraceStore;
import org.semanticweb.elk.reasoner.saturation.tracing.inferences.properties.ObjectPropertyInference;
import org.semanticweb.elk.reasoner.saturation.tracing.inferences.properties.ReflexivePropertyChainInference;
import org.semanticweb.elk.reasoner.saturation.tracing.inferences.properties.ReflexiveToldSubObjectProperty;
import org.semanticweb.elk.reasoner.saturation.tracing.inferences.properties.ToldReflexiveProperty;
import org.semanticweb.elk.util.concurrent.computation.InputProcessor;
import org.semanticweb.elk.util.concurrent.computation.InputProcessorFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The factory of engines that compute implied reflexivity of object property
 * (chains). A property is reflexive if one of its told sub-properties is
 * reflexive, or it is a property chain consisting of reflexive properties.
 * 
 * @author "Yevgeny Kazakov"
 * 
 */
public class ReflexivePropertyComputationFactory
		implements
		InputProcessorFactory<IndexedObjectProperty, ReflexivePropertyComputationFactory.Engine> {

	// logger for this class
	private static final Logger LOGGER_ = LoggerFactory
			.getLogger(ReflexivePropertyComputationFactory.class);
	
	private final TraceStore.Writer traceWriter_;
	
	public ReflexivePropertyComputationFactory() {
		this(TraceStore.Writer.Dummy);
	}
	
	public ReflexivePropertyComputationFactory(TraceStore.Writer traceWriter) {
		traceWriter_ = traceWriter;
	}

	/**
	 * A queue of reflexive {@code IndexedPropertyChain}s that are yet to be
	 * used to infer reflexivity of their direct super-properties or property
	 * chains they are part of.
	 */
	private final Queue<IndexedPropertyChain> toDo_ = new ConcurrentLinkedQueue<IndexedPropertyChain>();

	@Override
	public Engine getEngine() {
		return new Engine();
	}

	@Override
	public void finish() {
		// nothing to do
	}

	private void toDo(IndexedPropertyChain ipc, ObjectPropertyInference inference) {
		SaturatedPropertyChain saturation = SaturatedPropertyChain
				.getCreate(ipc);
		if (saturation.setReflexive()) {
			LOGGER_.trace("{}: set reflexive", ipc);

			toDo_.add(ipc);
			// record the reflexivity inference
			traceWriter_.addObjectPropertyInference(inference);
		}
	}

	private void toDoSuperProperties(IndexedPropertyChain ipc) {
		for (IndexedObjectProperty sup : ipc.getToldSuperProperties()) {
			toDo(sup, new ReflexiveToldSubObjectProperty(sup, ipc));
		}
	}

	private void toDoLeftChains(IndexedObjectProperty iop) {
		for (IndexedBinaryPropertyChain chain : iop.getLeftChains()) {
			SaturatedPropertyChain rightSaturation = chain.getRightProperty()
					.getSaturated();
			if (rightSaturation == null || !rightSaturation.isDerivedReflexive())
				continue;
			toDo(chain, new ReflexivePropertyChainInference(chain));
		}
	}

	private void toDoRightChains(IndexedPropertyChain ipc) {
		for (IndexedBinaryPropertyChain chain : ipc.getRightChains()) {
			SaturatedPropertyChain leftSaturation = chain.getLeftProperty()
					.getSaturated();
			if (leftSaturation == null || !leftSaturation.isDerivedReflexive())
				continue;
			toDo(chain, new ReflexivePropertyChainInference(chain));
		}
	}

	/**
	 * we use one visitor for propagating reflexivity for all workers
	 */
	private final IndexedPropertyChainVisitor<Void> reflexivityPropagator_ = new IndexedPropertyChainVisitor<Void>() {

		@Override
		public Void visit(IndexedObjectProperty element) {
			toDoLeftChains(element);
			commonVisit(element);
			return null;
		}

		@Override
		public Void visit(IndexedBinaryPropertyChain element) {
			commonVisit(element);
			return null;
		}

		public void commonVisit(IndexedPropertyChain element) {
			toDoSuperProperties(element);
			toDoRightChains(element);
		}
	};

	class Engine implements InputProcessor<IndexedObjectProperty> {

		@Override
		public void submit(IndexedObjectProperty job) {
			toDo(job, new ToldReflexiveProperty(job));
		}

		@Override
		public void process() throws InterruptedException {
			for (;;) {
				IndexedPropertyChain next = toDo_.poll();
				if (next == null)
					return;
				next.accept(reflexivityPropagator_);
			}
		}

		@Override
		public void finish() {
			// nothing to do
		}

	}

}
