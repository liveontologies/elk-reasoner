/*
 * #%L
 * elk-reasoner
 * 
 * $Id$
 * $HeadURL$
 * %%
 * Copyright (C) 2011 Oxford University Computing Laboratory
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
package org.semanticweb.elk.reasoner;

import java.util.Deque;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

class Saturator {
	protected Deque<Context> activeContexts;
	protected Map<Concept, Context> mapConceptToContext;

	Saturator() {
		activeContexts = new LinkedList<Context> ();
		mapConceptToContext = new HashMap<Concept, Context> ();
	}
	
	void saturate(Concept concept) {
		getContext(concept);
		while (!activeContexts.isEmpty())
			process(activeContexts.removeLast());
	}

	protected Context getContext(Concept concept) {
		Context context = mapConceptToContext.get(concept);
		if (context == null) {
			context = new Context();
			mapConceptToContext.put(concept, context);
			enqueue(context, concept);
		}
		return context;
	}

	protected void enqueue(Context context, Concept concept) {
		if (context.derived.add(concept)) {
			if (context.queue.isEmpty())
				activeContexts.add(context);
			context.queue.addLast(concept);
		}
	}

	protected void process(Context context) {
		while (true) {
			Concept concept = context.queue.peekFirst();	
			if (concept == null)
				return;

			for (Concept c : concept.getToldSuperConcepts())
				enqueue(context, c);

			for (Conjunction conjunction : concept.getConjunctions()) {
				boolean arePremisesSatisfied = true;

				for (Concept premise : conjunction.getPremises())
					if (!concept.equals(premise) && !context.derived.contains(premise)) {
						arePremisesSatisfied = false;
						break;
					}

				if (arePremisesSatisfied)
					enqueue(context, conjunction.getConclusion());
			}

			for (Existential e : concept.getExistentials()) {
				Context target = getContext(e.getConcept());
				Role role = e.getRole();
				target.links.put(role, context);

				for (Concept c : target.derived) {
					for (Universal u : c.getUniversals())
						if (role.equals(u.getRole()))
							enqueue(context, u.getConcept());
				}
			}

			for (Universal u : concept.getUniversals()) {
				List<Context> propagate = context.links.get(u.getRole());
				if (propagate != null)
					for (Context target : propagate)
						enqueue(target, u.getConcept());
			}

			context.queue.removeFirst();
		}
	}
}