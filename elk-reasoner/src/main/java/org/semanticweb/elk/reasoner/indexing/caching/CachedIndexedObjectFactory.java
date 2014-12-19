package org.semanticweb.elk.reasoner.indexing.caching;

import java.util.List;

import org.semanticweb.elk.owl.interfaces.ElkClass;
import org.semanticweb.elk.owl.interfaces.ElkDataHasValue;
import org.semanticweb.elk.owl.interfaces.ElkNamedIndividual;
import org.semanticweb.elk.owl.interfaces.ElkObjectProperty;
import org.semanticweb.elk.reasoner.indexing.modifiable.ModifiableIndexedClassExpression;
import org.semanticweb.elk.reasoner.indexing.modifiable.ModifiableIndexedObjectProperty;
import org.semanticweb.elk.reasoner.indexing.modifiable.ModifiableIndexedPropertyChain;

public interface CachedIndexedObjectFactory {

	public CachedIndexedBinaryPropertyChain getIndexedBinaryPropertyChain(
			ModifiableIndexedObjectProperty leftProperty,
			ModifiableIndexedPropertyChain rightProperty);

	public CachedIndexedClass getIndexedClass(ElkClass elkClass);

	public CachedIndexedDataHasValue getIndexedDataHasValue(
			ElkDataHasValue elkDataHasValue);

	public CachedIndexedDisjointnessAxiom getIndexedDisjointnessAxiom(
			List<? extends ModifiableIndexedClassExpression> members);

	public CachedIndexedIndividual getIndexedIndividual(
			ElkNamedIndividual elkNamedIndividual);

	public CachedIndexedObjectComplementOf getIndexedObjectComplementOf(
			ModifiableIndexedClassExpression negated);

	public CachedIndexedObjectIntersectionOf getIndexedObjectIntersectionOf(
			ModifiableIndexedClassExpression conjunctA,
			ModifiableIndexedClassExpression conjunctB);

	public CachedIndexedObjectProperty getIndexedObjectProperty(
			ElkObjectProperty elkObjectProperty);

	public CachedIndexedObjectSomeValuesFrom getIndexedObjectSomeValuesFrom(
			ModifiableIndexedObjectProperty property,
			ModifiableIndexedClassExpression filler);

	public CachedIndexedObjectUnionOf getIndexedObjectUnionOf(
			List<? extends ModifiableIndexedClassExpression> disjuncts);

}
