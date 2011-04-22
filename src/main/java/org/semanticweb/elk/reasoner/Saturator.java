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
	protected ArrayList<Derivable> localQueue = 
		new ArrayList<Derivable> ();
	
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
	
	protected void enqueue(Context context, Derivable derivable) {
		if (derivable.accept(context)) {
			if (context.saturated) {
				activeContexts.add(context);
				context.saturated = false;
			}
			context.queue.add(derivable);
		}
	}
	
	
	protected void process(final Context context) {
		while (!context.queue.isEmpty()) {
			int last = context.queue.size()-1;
			Derivable toProcess = context.queue.get(last);
			context.queue.remove(last);
	
			DerivableVisitor<Void> processor = new DerivableVisitor<Void> () {
			
				public Void visit(Concept concept) {
					
					//process told super concepts
					for (Concept c : concept.getToldSuperConcepts())
						localQueue.add(c);

					//process conjunctions
					for (Conjunction conjunction : concept.getConjunctions()) {
						boolean arePremisesSatisfied = true;
						for (Concept premise : conjunction.getPremises())
							if (!context.derivedConcepts.contains(premise)) {
								arePremisesSatisfied = false;
								break;
							}


						if (arePremisesSatisfied)
							localQueue.add(conjunction.getConclusion());
					}

					//process existentials
					for (Existential e : concept.getExistentials()) {
						localQueue.add(e);
					}

					//process universals
					for (Quantifier u : concept.getUniversals()) {
						List<Context> parents = context.linksToParents.get(u.getRole());
						if (parents != null)
							for (Context target : parents)
								enqueue(target, u.getConcept());
					}

					return null;
				}
				
				public Void visit(Existential existential) {
					Role role = existential.getRole();
					Context target = getContext(existential.getConcept());
										
					target.linksToParents.add(role, context);

					for (Concept c : target.derivedConcepts) {
						for (Quantifier u : c.getUniversals())
							if (role == u.getRole())
								localQueue.add(u.getConcept());
					}
					
					//process told super roles
					for (Role s : role.getToldSuperRoles())
						localQueue.add(new Existential(s, existential.getConcept()));
					
					//process transitivity
					if (role.isTransitive()) {
						
						for (Existential e : target.derivedExistentials) {
							if (role == e.getRole())
								localQueue.add(e);								
						}
						
						List<Context> parents = context.linksToParents.get(role);
						if (parents != null)
							for (Context c : parents)
								enqueue(c, existential);
					}

					return null;
				}
			};
			
			toProcess.accept(processor);
						
			for (Derivable d : localQueue)
				enqueue(context, d);
			localQueue.clear();
		}
		context.queue.trimToSize();
		context.saturated = true;
	}
}