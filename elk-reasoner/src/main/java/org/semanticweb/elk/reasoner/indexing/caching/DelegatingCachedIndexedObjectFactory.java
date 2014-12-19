package org.semanticweb.elk.reasoner.indexing.caching;

import java.util.List;

import org.semanticweb.elk.owl.interfaces.ElkClass;
import org.semanticweb.elk.owl.interfaces.ElkDataHasValue;
import org.semanticweb.elk.owl.interfaces.ElkNamedIndividual;
import org.semanticweb.elk.owl.interfaces.ElkObjectProperty;
import org.semanticweb.elk.reasoner.indexing.modifiable.ModifiableIndexedClassExpression;
import org.semanticweb.elk.reasoner.indexing.modifiable.ModifiableIndexedObjectProperty;
import org.semanticweb.elk.reasoner.indexing.modifiable.ModifiableIndexedPropertyChain;

public class DelegatingCachedIndexedObjectFactory implements
		CachedIndexedObjectFactory {

	private final CachedIndexedObjectFactory baseFactory_;

	DelegatingCachedIndexedObjectFactory(CachedIndexedObjectFactory baseFactory) {
		this.baseFactory_ = baseFactory;
	}

	/**
	 * Filters the objects created by the factory; can be overriden in
	 * subclasses
	 * 
	 * @param input
	 * @return
	 */
	@SuppressWarnings("static-method")
	<T extends CachedIndexedObject<T>> T filter(T input) {
		return input;
	}

	@Override
	public final CachedIndexedBinaryPropertyChain getIndexedBinaryPropertyChain(
			ModifiableIndexedObjectProperty leftProperty,
			ModifiableIndexedPropertyChain rightProperty) {
		return filter(baseFactory_.getIndexedBinaryPropertyChain(leftProperty,
				rightProperty));
	}

	@Override
	public final CachedIndexedClass getIndexedClass(ElkClass elkClass) {
		return filter(baseFactory_.getIndexedClass(elkClass));
	}

	@Override
	public final CachedIndexedDataHasValue getIndexedDataHasValue(
			ElkDataHasValue elkDataHasValue) {
		return filter(baseFactory_.getIndexedDataHasValue(elkDataHasValue));
	}

	@Override
	public final CachedIndexedDisjointnessAxiom getIndexedDisjointnessAxiom(
			List<? extends ModifiableIndexedClassExpression> members) {
		return filter(baseFactory_.getIndexedDisjointnessAxiom(members));
	}

	@Override
	public final CachedIndexedIndividual getIndexedIndividual(
			ElkNamedIndividual elkNamedIndividual) {
		return filter(baseFactory_.getIndexedIndividual(elkNamedIndividual));
	}

	@Override
	public final CachedIndexedObjectComplementOf getIndexedObjectComplementOf(
			ModifiableIndexedClassExpression negated) {
		return filter(baseFactory_.getIndexedObjectComplementOf(negated));
	}

	@Override
	public final CachedIndexedObjectIntersectionOf getIndexedObjectIntersectionOf(
			ModifiableIndexedClassExpression conjunctA,
			ModifiableIndexedClassExpression conjunctB) {
		return filter(baseFactory_.getIndexedObjectIntersectionOf(conjunctA,
				conjunctB));
	}

	@Override
	public final CachedIndexedObjectProperty getIndexedObjectProperty(
			ElkObjectProperty elkObjectProperty) {
		return filter(baseFactory_.getIndexedObjectProperty(elkObjectProperty));
	}

	@Override
	public final CachedIndexedObjectSomeValuesFrom getIndexedObjectSomeValuesFrom(
			ModifiableIndexedObjectProperty property,
			ModifiableIndexedClassExpression filler) {
		return filter(baseFactory_.getIndexedObjectSomeValuesFrom(property,
				filler));
	}

	@Override
	public final CachedIndexedObjectUnionOf getIndexedObjectUnionOf(
			List<? extends ModifiableIndexedClassExpression> disjuncts) {
		return filter(baseFactory_.getIndexedObjectUnionOf(disjuncts));
	}

}
