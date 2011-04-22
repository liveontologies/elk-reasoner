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

import java.util.ArrayList;
import java.util.Deque;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

class Saturator {
	protected Deque<Context> activeContexts =
		new LinkedList<Context> ();
	protected Map<Concept, Context> mapConceptToContext =
		new HashMap<Concept, Context> ();
	protected ArrayList<Concept> localQueue = 
		new ArrayList<Concept> ();
	
	void saturate(Concept concept) {
		getContext(concept);
		while (!activeContexts.isEmpty())
			process(activeContexts.removeFirst());
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
			if (context.saturated) {
				activeContexts.add(context);
				context.saturated = false;
			}
			context.queue.add(concept);
		}
	}
	
	void processToldSuperConcepts(Context context, Concept concept ) {
		for (Concept c : concept.getToldSuperConcepts())
			localQueue.add(c);
	}
	
	void processConjunctions(Context context, Concept concept) {
		for (Conjunction conjunction : concept.getConjunctions()) {
			boolean arePremisesSatisfied = true;
			for (Concept premise : conjunction.getPremises())
				if (!context.derived.contains(premise)) {
					arePremisesSatisfied = false;
					break;
				}

			if (arePremisesSatisfied)
				localQueue.add(conjunction.getConclusion());
		}		
	}

	void processExistentials(Context context, Concept concept) {
		for (Quantifier e : concept.getExistentials()) {
			Context target = getContext(e.getConcept());
			target.linksToParents.add(e.getRole(), context);

			for (Concept c : target.derived) {
				for (Quantifier u : c.getUniversals())
					if (e.getRole() == u.getRole())
						localQueue.add(u.getConcept());
			}
		}
	}
	
	void processUniversals(Context context, Concept concept) {
		for (Quantifier u : concept.getUniversals()) {
			List<Context> parents = context.linksToParents.get(u.getRole());
			if (parents != null)
				for (Context target : parents)
					enqueue(target, u.getConcept());
		}
	}
	
	protected void process(Context context) {
		while (!context.queue.isEmpty()) {
			int last = context.queue.size()-1;
			Concept concept = context.queue.get(last);
			context.queue.remove(last);
			
			processToldSuperConcepts(context, concept);
			processConjunctions(context, concept);
			processExistentials(context, concept);
			processUniversals(context, concept);
			
			for (Concept c : localQueue)
				enqueue(context, c);
			localQueue.clear();
		}
		context.queue.trimToSize();
		context.saturated = true;
	}
}