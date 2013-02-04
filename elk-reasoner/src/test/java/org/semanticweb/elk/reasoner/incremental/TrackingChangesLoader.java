package org.semanticweb.elk.reasoner.incremental;

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
import java.util.Random;

import org.apache.log4j.Logger;
import org.semanticweb.elk.RandomSeedProvider;
import org.semanticweb.elk.loading.AxiomChangeListener;
import org.semanticweb.elk.loading.ChangesLoader;
import org.semanticweb.elk.loading.ElkLoadingException;
import org.semanticweb.elk.loading.Loader;
import org.semanticweb.elk.loading.SimpleElkAxiomChange;
import org.semanticweb.elk.owl.interfaces.ElkAxiom;
import org.semanticweb.elk.owl.printers.OwlFunctionalStylePrinter;
import org.semanticweb.elk.owl.visitors.ElkAxiomProcessor;

public class TrackingChangesLoader implements ChangesLoader {

	protected static final Logger LOGGER_ = Logger
			.getLogger(TrackingChangesLoader.class);

	private final static Random RANDOM_ = new Random(RandomSeedProvider.VALUE);

	/**
	 * Setting seed for generating random changes (so that the results can be
	 * reproduced)
	 * 
	 * @param seed
	 *            the seed to be used for generating of random changes
	 */
	public static void setSeed(long seed) {
		RANDOM_.setSeed(seed);
	}

	/**
	 * the axioms from which to generate the changes
	 */
	private final OnOffVector<ElkAxiom> changingAxioms_;
	/**
	 * the queue in which to append the generated changes
	 */
	private final Queue<IncrementalChange> changes_;
	/**
	 * The number of added and removed axioms to generate
	 */
	private final int changeSize_;
	/**
	 * we generate random changes here
	 */
	private final IncrementalChange change_ = new IncrementalChange();

	/**
	 * change listener if any is needed
	 */
	private AxiomChangeListener listener_;

	public TrackingChangesLoader(OnOffVector<ElkAxiom> changingAxioms,
			Queue<IncrementalChange> changes, int changeSize) {
		this.changingAxioms_ = changingAxioms;
		this.changes_ = changes;
		this.changeSize_ = changeSize;
	}

	@Override
	public Loader getLoader(final ElkAxiomProcessor axiomInserter,
			final ElkAxiomProcessor axiomDeleter) {

		return new Loader() {

			/**
			 * the counter for the number of changes loaded
			 */
			private int changesLoaded_ = 0;

			@Override
			public void load() throws ElkLoadingException {
				for (;;) {
					if (Thread.currentThread().isInterrupted())
						break;
					if (changesLoaded_++ == changeSize_)
						break;
					int index = RANDOM_.nextInt(changingAxioms_.size());
					ElkAxiom axiom = changingAxioms_.get(index);
					// removing a random axiom if its status was "on", that is,
					// it was loaded to the reasoner; otherwise adding this
					// axiom; the status is flip
					if (changingAxioms_.flipOnOff(index)) {
						if (LOGGER_.isTraceEnabled())
							LOGGER_.trace("removing axiom " + index + ": "
									+ OwlFunctionalStylePrinter.toString(axiom));
						change_.registerDeletion(axiom);
						axiomDeleter.visit(axiom);
						if (listener_ != null)
							listener_
									.notify(new SimpleElkAxiomChange(axiom, -1));
					} else {
						if (LOGGER_.isTraceEnabled())
							LOGGER_.trace("adding axiom " + index + ": "
									+ OwlFunctionalStylePrinter.toString(axiom));
						change_.registerAddition(axiom);
						axiomInserter.visit(axiom);
						if (listener_ != null)
							listener_
									.notify(new SimpleElkAxiomChange(axiom, 1));
					}
				}
				changes_.add(change_);
			}

			@Override
			public void dispose() {
				// nothing to do
			}

		};
	}

	@Override
	public void registerChangeListener(AxiomChangeListener listener) {
		this.listener_ = listener;
	}
}
