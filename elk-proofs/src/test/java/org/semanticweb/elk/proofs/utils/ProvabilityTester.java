package org.semanticweb.elk.proofs.utils;

/*
 * #%L
 * ELK Proofs Package
 * $Id:$
 * $HeadURL:$
 * %%
 * Copyright (C) 2011 - 2016 Department of Computer Science, University of Oxford
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;

import org.semanticweb.elk.owl.inferences.ElkInference;
import org.semanticweb.elk.owl.inferences.ElkInferenceSet;
import org.semanticweb.elk.owl.interfaces.ElkAxiom;
import org.semanticweb.elk.owl.interfaces.ElkObject;
import org.semanticweb.elk.owl.managers.ElkObjectEntityRecyclingFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Testing provability of {@link ElkAxiom}s in {@link ElkInferenceSet}. An
 * {@link ElkAxiom} is provable if there exists a sequence of
 * {@link ElkInference}s such that all premises of each {@link ElkInference} in
 * the sequence is a conclusion of some preceding {@link ElkInference} or occurs
 * in the provided ontology. The method {@link #isProvable(ElkAxiom)} checks
 * provability of the given {@link ElkAxiom}. The method
 * {@link #getUnprovedLemmas()} can be used to retrieve all known
 * {@link ElkAxiom}s that were found to be not provable.
 * 
 * @author Yevgeny Kazakov
 *
 */
public class ProvabilityTester {

	private static final Logger LOGGER_ = LoggerFactory
			.getLogger(ProvabilityTester.class);

	private final ElkInferenceSet inferences_;

	private final Set<? extends ElkAxiom> ontology_;

	/**
	 * all axioms for which checking for provability was issued
	 */
	private final Set<ElkAxiom> checked_ = new HashSet<ElkAxiom>();

	/**
	 * new axioms that should be checked for probability; contained in
	 * {@link #checked_}
	 */
	private final Queue<ElkAxiom> toCheck_ = new LinkedList<ElkAxiom>();

	/**
	 * axioms found to be provable; is a subset of {@link #checked_}
	 */
	private final Set<ElkAxiom> proved_ = new HashSet<ElkAxiom>();

	/**
	 * newly provable axioms that are not yet used in inferences to compute
	 * other provable axioms
	 */
	private final Queue<ElkAxiom> toPropagate_ = new LinkedList<ElkAxiom>();

	/**
	 * a map from (currently unprovable) axioms to list of inferences in which
	 * this axiom is used as a premise; these inferences are "watching" for this
	 * axiom to be proved
	 */
	private final Map<ElkAxiom, List<ElkInference>> watchedInferences_ = new HashMap<ElkAxiom, List<ElkInference>>();

	/**
	 * a map from (currently unprovable) axioms to the position of the premise
	 * of the corresponding inference in {@link #watchedInferences_} that is
	 * equal to this axiom
	 */
	private final Map<ElkAxiom, List<Integer>> watchedPositions_ = new HashMap<ElkAxiom, List<Integer>>();

	/**
	 * a factory that is used for creating premises and conclusions of
	 * inferences
	 */
	private final ElkObject.Factory factory_ = new ElkObjectEntityRecyclingFactory();

	ProvabilityTester(ElkInferenceSet inferences,
			Set<? extends ElkAxiom> ontology) {
		this.inferences_ = inferences;
		this.ontology_ = ontology;
	}

	/**
	 * @param goal
	 *            an {@link ElkAxiom} to be checked for provability
	 * @return {@code true} if the {@link ElkAxiom} is provable and
	 *         {@code false} otherwise
	 */
	public boolean isProvable(ElkAxiom goal) {
		toCheck(goal);
		process();
		return proved_.contains(goal);
	}

	/**
	 * @return all unprovable {@link ElkAxiom}s that were used in the proofs of
	 *         the goals submitted by {@link #isProvable(ElkAxiom)}
	 */
	public Set<? extends ElkAxiom> getUnprovedLemmas() {
		return watchedInferences_.keySet();
	}

	private void process() {
		for (;;) {
			ElkAxiom next = toCheck_.poll();

			if (next != null) {
				if (ontology_.contains(next)) {
					proved(next);
				}
				for (ElkInference inf : inferences_.get(next)) {
					LOGGER_.trace("{}: expanding", inf);
					check(inf, 0);
					for (int i = 0; i < inf.getPremiseCount(); i++) {
						toCheck(inf.getPremise(i, factory_));
					}
				}
				continue;
			}

			next = toPropagate_.poll();

			if (next != null) {
				List<ElkInference> watched = watchedInferences_.get(next);
				if (watched == null) {
					continue;
				}
				List<Integer> positions = watchedPositions_.get(next);
				watchedInferences_.remove(next);
				watchedPositions_.remove(next);
				for (int i = 0; i < watched.size(); i++) {
					ElkInference inf = watched.get(i);
					int pos = positions.get(i);
					check(inf, pos + 1);
				}
				continue;
			}

			// all done
			return;

		}

	}

	private void toCheck(ElkAxiom conclusion) {
		if (checked_.add(conclusion)) {
			LOGGER_.trace("{}: new subgoal", conclusion);
			toCheck_.add(conclusion);
		}
	}

	private void addWatch(ElkAxiom premise, ElkInference inf, int pos) {
		List<ElkInference> inferences = watchedInferences_.get(premise);
		List<Integer> positions = watchedPositions_.get(premise);
		if (inferences == null) {
			inferences = new ArrayList<ElkInference>();
			watchedInferences_.put(premise, inferences);
			positions = new ArrayList<Integer>();
			watchedPositions_.put(premise, positions);
		}
		inferences.add(inf);
		positions.add(pos);
	}

	private void proved(ElkAxiom conclusion) {
		if (proved_.add(conclusion)) {
			LOGGER_.trace("{}: proved", conclusion);
			toPropagate_.add(conclusion);
		}
	}

	private void check(ElkInference inf, int pos) {
		for (int i = pos; i < inf.getPremiseCount(); i++) {
			ElkAxiom premise = inf.getPremise(i, factory_);
			if (!proved_.contains(premise)) {
				addWatch(premise, inf, i);
				return;
			}
		}
		// all premises are proved
		LOGGER_.trace("{}: fire", inf);
		proved(inf.getConclusion(factory_));
	}

}
