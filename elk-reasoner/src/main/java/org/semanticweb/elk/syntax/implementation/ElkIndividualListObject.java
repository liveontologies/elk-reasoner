package org.semanticweb.elk.syntax.implementation;

import java.util.List;

import org.semanticweb.elk.syntax.interfaces.ElkIndividual;

/**
 * Implementation for ElkObjects that maintain a list of individuals.
 * 
 * @author Markus Kroetzsch
 */
public abstract class ElkIndividualListObject extends
		ElkObjectListObject<ElkIndividual> {

	/* package-private */ElkIndividualListObject(
			List<? extends ElkIndividual> individuals) {
		super(individuals);
	}

	public List<? extends ElkIndividual> getIndividuals() {
		return elkObjects;
	}

}
