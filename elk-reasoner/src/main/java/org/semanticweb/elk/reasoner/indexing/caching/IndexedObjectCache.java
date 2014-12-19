package org.semanticweb.elk.reasoner.indexing.caching;

import java.util.Collection;

import org.semanticweb.elk.owl.interfaces.ElkClass;
import org.semanticweb.elk.owl.interfaces.ElkClassExpression;
import org.semanticweb.elk.owl.interfaces.ElkIndividual;
import org.semanticweb.elk.owl.interfaces.ElkObjectProperty;
import org.semanticweb.elk.owl.interfaces.ElkSubObjectPropertyExpression;
import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedClass;
import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedClassExpression;
import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedIndividual;
import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedObjectProperty;
import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedPropertyChain;

public interface IndexedObjectCache {

	/**
	 * @return the {@link IndexedClass}es for all {@link ElkClass}es occurring
	 *         in the ontology (including {@code owl:Thing} and
	 *         {@code owl:Nothing})
	 */
	public Collection<? extends IndexedClass> getClasses();

	/**
	 * @return the {@link IndexedIndividual}s for all {@link ElkIndividual}s
	 *         occurring in the ontology.
	 */
	public Collection<? extends IndexedIndividual> getIndividuals();

	/**
	 * @return the {@link IndexedObjectProperty}s for all
	 *         {@link ElkObjectProperty}s occurring in the ontology.
	 */
	public Collection<? extends IndexedObjectProperty> getObjectProperties();

	/**
	 * @return the {@link IndexedClassExpression}s for all
	 *         {@link ElkClassExpression}s occurring in the ontology (including
	 *         {@code owl:Thing} and {@code owl:Nothing}) or added/removed from
	 *         the ontology since the last commit of the differential index
	 */
	public Collection<? extends IndexedClassExpression> getClassExpressions();

	/**
	 * @return the {@link IndexedPropertyChain}s for all
	 *         {@link ElkSubObjectPropertyExpression}s occurring in the
	 *         ontology.
	 */
	public Collection<? extends IndexedPropertyChain> getPropertyChains();

	/**
	 * @return the {@link IndexedClass} corresponding to {@code owl:Thing}
	 *         occurring in this {@link IndexedObjectCache} or {@code null} if
	 *         there is no such an object
	 */
	public IndexedClass getOwlThing();

	/**
	 * @return the {@link IndexedClass} corresponding to {@code owl:Nothing}
	 *         occurring in this {@link IndexedObjectCache} or {@code null} if
	 *         there is no such an object
	 */
	public IndexedClass getOwlNothing();

}
