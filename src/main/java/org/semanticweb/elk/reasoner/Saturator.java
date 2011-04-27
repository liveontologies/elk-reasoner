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

import java.util.Collection;
import java.util.Deque;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

import org.semanticweb.elk.util.Pair;

class Saturator {
	
	protected Deque<Context> activeContexts =
		new LinkedList<Context> ();
	protected Map<Concept, Context> mapConceptToContext =
		new HashMap<Concept, Context> ();

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
	//	if (derivable.accept(context)) {
			if (context.saturated) {
				activeContexts.add(context);
				context.saturated = false;
			}
			context.queue.add(derivable);
//		}
	}
	
	protected void process(final Context context) {
	
		DerivableVisitor<Void> processor = new DerivableVisitor<Void> () {
			
			public Void visit(Concept concept) {
				if (!context.derivedConcepts.add(concept))
					return null;
				
				//process told super concepts
				for (Concept c : concept.getToldSuperConcepts())
					enqueue(context, c);

				//process conjunctions
				for (Conjunction conjunction : concept.getConjunctions()) {
					boolean arePremisesSatisfied = true;
					for (Concept premise : conjunction.getPremises())
						if (!context.derivedConcepts.contains(premise)) {
							arePremisesSatisfied = false;
							break;
						}


					if (arePremisesSatisfied)
						enqueue(context, conjunction.getConclusion());
				}

				//process existentials
				for (Quantifier e : concept.getExistentials()) {
					enqueue(context, new ForwardLink(e.getRole(), getContext(e.getConcept())));
				}

				//process universals
				for (Quantifier u : concept.getUniversals()) {
					Collection<Context> parents = context.backwardLinks.get(u.getRole());
					if (parents != null)
						for (Context c : parents)
							enqueue(c, u.getConcept());
				}
				
				return null;
			}
			
			public Void visit(ForwardLink link) {
				if (!context.forwardLinks.add(link))
					return null;
			
				switch (Reasoner.TRANSITIVITY) {
				
				case 0:
					link.getContext().backwardLinks.add(link.getRole(), context);
					
					//process universals
					for (Concept c : link.getContext().derivedConcepts)
						for (Quantifier u : c.getUniversals())
							if (link.getRole() == u.getRole())
								enqueue(context, u.getConcept());
					
					
					//process told super roles
					for (Role r : link.getRole().getToldSuperRoles())
						enqueue(context, new ForwardLink(r, link.getContext()));
					
					//process role chains on right
					for (Pair<Role, Role> p : link.getRole().getRightPropertyChains()) {
						Collection<Context> parents = context.backwardLinks.get(p.getFirst());
						if (parents != null)
							for (Context c : parents)
								enqueue(c, new ForwardLink(p.getSecond(), link.getContext()));
					}
					
					//process role chains on left
					for (Pair<Role, Role> p : link.getRole().getRightPropertyChains()) {
						Collection<Context> children = link.getContext().forwardLinks.get(p.getFirst());
						if (children != null)
							for (Context c : children)
								enqueue(context, new ForwardLink(p.getSecond(), c));
/*
						for (ForwardLink l : link.getContext().forwardLinks)
							if (l.getRole() == p.getFirst())
								enqueue(context, new ForwardLink(p.getSecond(), l.getContext()));
*/								
					}
					
					break;
				
				case 1:
					enqueue(link.getContext(), new BackwardLink(link.getRole(), context));
				
					//process role chains on right
					for (Pair<Role, Role> p : link.getRole().getRightPropertyChains()) {
						Collection<Context> parents = context.backwardLinks.get(p.getFirst());
						if (parents != null)
							for (Context c : parents)
								enqueue(link.getContext(), new BackwardLink(p.getSecond(), c));
					}
					break;
				}
				
				return null;
			}
			
			public Void visit(BackwardLink link) {
				if (!context.backwardLinks.add(link))
					return null;

				//process universals
				for (Concept c : context.derivedConcepts)
					for (Quantifier u : c.getUniversals())
						if (link.getRole() == u.getRole())
							enqueue(link.getContext(), u.getConcept());

				//process told superroles
				for (Role r : link.getRole().getToldSuperRoles())
					enqueue(context, new BackwardLink(r, link.getContext()));

				//process role chains on left
				for (Pair<Role, Role> p : link.getRole().getLeftPropertyChains()) {
					Collection<Context> children = context.forwardLinks.get(p.getFirst());
					if (children != null)
						for (Context c : children)
							enqueue(c, new BackwardLink(p.getSecond(), link.getContext()));
				}

				return null;
			}

		};
		
		
		while (!context.queue.isEmpty()) {
			int last = context.queue.size()-1;
			Derivable toProcess = context.queue.get(last);
			context.queue.remove(last);
			toProcess.accept(processor);
		}
		context.queue.trimToSize();
		context.saturated = true;
	}
}