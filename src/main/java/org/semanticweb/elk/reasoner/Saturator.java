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

class Saturator {
	
	protected Deque<Queueable> globalQueue =
		new LinkedList<Queueable> ();
	
	protected Map<Concept, Context> mapConceptToContext =
		new HashMap<Concept, Context> ();
	
	protected Map<Link, Link> linkSet = new HashMap<Link, Link> (); 
	
	int initRulesNo = 0;
	int subsumptionRulesNo = 0;
	int conjunctionRulesNo = 0;
	int existentialRulesNo = 0;
	int universalRulesNo = 0;
	int hierarchyRulesNo = 0;
	int transitivityRulesNo = 0;
	
	int subsumptionDerivedNo = 0;
	int linkDerivedNo = 0;
	
	void saturate(Concept concept) {
		getContext(concept);
		while (!globalQueue.isEmpty())
			globalQueue.removeFirst().accept(processor);
	}

	protected Context getContext(Concept concept) {
		Context context = mapConceptToContext.get(concept);
		if (context == null) {
			context = new Context();
			mapConceptToContext.put(concept, context);
			enqueue(context, concept);
			initRulesNo++;
		}
		return context;
	}
	
	protected void enqueue(Context context, Concept concept) {
		if (context.saturated) {
			globalQueue.add(context);
			context.saturated = false;
		}
		context.localQueue.add(concept);
	}
	
	protected void enqueue(Link link) {
		globalQueue.add(link);
	}
	
	QueueableVisitor processor = new QueueableVisitor () {
		
		public void visit(Context context) {
			while (!context.localQueue.isEmpty()) {
				int last = context.localQueue.size()-1;
				Concept concept = context.localQueue.get(last);
				context.localQueue.remove(last);
				
				if (context.derivedConcepts.add(concept)) {
					subsumptionDerivedNo++;
					
					//process told super concepts
					for (Concept c : concept.getToldSuperConcepts()) {
						enqueue(context, c);
						subsumptionRulesNo++;
					}

					//process conjunctions
					for (Conjunction conjunction : concept.getConjunctions()) {
						boolean arePremisesSatisfied = true;
						for (Concept premise : conjunction.getPremises())
							if (!context.derivedConcepts.contains(premise)) {
								arePremisesSatisfied = false;
								break;
							}


						if (arePremisesSatisfied) {
							enqueue(context, conjunction.getConclusion());
							conjunctionRulesNo++;
						}
					}

					//process existentials
					for (Quantifier e : concept.getExistentials()) {
						enqueue(new Link(context, getContext(e.getConcept()), e.getRole()));
						existentialRulesNo++;
					}

					//process universals
					for (Quantifier u : concept.getUniversals()) {
						Collection<Link> toParents = context.backwardLinks.get(u.getRole());
						if (toParents != null)
							for (Link l : toParents) {
								enqueue(l.getSource(), u.getConcept());
								universalRulesNo++;
							}
					}
				}
			}
			context.localQueue.trimToSize();
			context.saturated = true;
		}
		
		public void visit(Link link) {
			if (linkSet.get(link) == null) {
				linkSet.put(link, link);
				linkDerivedNo++;
				
				link.getSource().forwardLinks.add(link.getRole(), link);
				link.getTarget().backwardLinks.add(link.getRole(), link);
				
				//process universals
				for (Concept c : link.getTarget().derivedConcepts)
					for (Quantifier u : c.getUniversals())
						if (link.getRole() == u.getRole()) {
							enqueue(link.getSource(), u.getConcept());
							universalRulesNo++;
						}
			
				//process told super roles
				for (Role r : link.getRole().getToldSuperRoles()) {
					enqueue(new Link(link.getSource(), link.getTarget(), r, link.length));
					hierarchyRulesNo++;
				}
				
				//process left role chains
				for (RoleChain p : link.getRole().getLeftPropertyChains()) {
					Collection<Link> toChildren = link.getTarget().forwardLinks.get(p.getRightSubRole());
					if (toChildren != null)
						for (Link l : toChildren) 
							if (l.length == 1 || !p.isLeftLinear) {
								enqueue(new Link(link.getSource(), l.getTarget(), p.getSuperRole(), link.length+l.length));
								transitivityRulesNo++;
							}
				}
				
				//process right role chains
				for (RoleChain p : link.getRole().getRightPropertyChains()) 
					if (link.length == 1 || !p.isLeftLinear) {
						Collection<Link> toParents = link.getSource().backwardLinks.get(p.getLeftSubRole());
						if (toParents != null)
							for (Link l : toParents) {
								enqueue(new Link(l.getSource(), link.getTarget(), p.getSuperRole(), link.length+l.length));
								transitivityRulesNo++;
							}
					}
			}
		}
	};
}