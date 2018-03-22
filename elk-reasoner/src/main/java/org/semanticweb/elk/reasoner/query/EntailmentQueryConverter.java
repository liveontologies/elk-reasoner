/*-
 * #%L
 * ELK Reasoner Core
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
package org.semanticweb.elk.reasoner.query;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;

import org.semanticweb.elk.owl.interfaces.ElkAxiom;
import org.semanticweb.elk.owl.interfaces.ElkClassAssertionAxiom;
import org.semanticweb.elk.owl.interfaces.ElkClassExpression;
import org.semanticweb.elk.owl.interfaces.ElkDifferentIndividualsAxiom;
import org.semanticweb.elk.owl.interfaces.ElkDisjointClassesAxiom;
import org.semanticweb.elk.owl.interfaces.ElkEquivalentClassesAxiom;
import org.semanticweb.elk.owl.interfaces.ElkIndividual;
import org.semanticweb.elk.owl.interfaces.ElkObject;
import org.semanticweb.elk.owl.interfaces.ElkObjectPropertyAssertionAxiom;
import org.semanticweb.elk.owl.interfaces.ElkObjectPropertyDomainAxiom;
import org.semanticweb.elk.owl.interfaces.ElkSameIndividualAxiom;
import org.semanticweb.elk.owl.interfaces.ElkSubClassOfAxiom;
import org.semanticweb.elk.owl.predefined.ElkPolarity;
import org.semanticweb.elk.owl.visitors.DummyElkAxiomVisitor;
import org.semanticweb.elk.reasoner.entailments.impl.ClassAssertionAxiomEntailmentImpl;
import org.semanticweb.elk.reasoner.entailments.impl.DifferentIndividualsAxiomEntailmentImpl;
import org.semanticweb.elk.reasoner.entailments.impl.DisjointClassesAxiomEntailmentImpl;
import org.semanticweb.elk.reasoner.entailments.impl.EquivalentClassesAxiomEntailmentImpl;
import org.semanticweb.elk.reasoner.entailments.impl.ObjectPropertyAssertionAxiomEntailmentImpl;
import org.semanticweb.elk.reasoner.entailments.impl.ObjectPropertyDomainAxiomEntailmentImpl;
import org.semanticweb.elk.reasoner.entailments.impl.SameIndividualAxiomEntailmentImpl;
import org.semanticweb.elk.reasoner.entailments.impl.SubClassOfAxiomEntailmentImpl;
import org.semanticweb.elk.reasoner.entailments.model.Entailment;
import org.semanticweb.elk.reasoner.indexing.classes.ModifiableIndexedObjectBaseFactory;
import org.semanticweb.elk.reasoner.indexing.classes.UpdatingModifiableIndexedObjectFactory;
import org.semanticweb.elk.reasoner.indexing.conversion.ElkIndexingUnsupportedException;
import org.semanticweb.elk.reasoner.indexing.conversion.ElkPolarityExpressionConverter;
import org.semanticweb.elk.reasoner.indexing.conversion.ElkPolarityExpressionConverterImpl;
import org.semanticweb.elk.reasoner.indexing.model.IndexedClassExpression;
import org.semanticweb.elk.reasoner.indexing.model.IndexedIndividual;
import org.semanticweb.elk.reasoner.indexing.model.ModifiableIndexedObject;
import org.semanticweb.elk.reasoner.indexing.model.ModifiableOntologyIndex;
import org.semanticweb.elk.reasoner.indexing.model.OccurrenceIncrement;

/**
 * Indexes axiom entailment queries. All supported axiom entailment queries
 * should be indexed by this converter, so it also serves the purpose of list of
 * supported queries ({@link #isEntailmentCheckingSupported(Class)}).
 * 
 * @author Peter Skocovsky
 */
public class EntailmentQueryConverter extends
		DummyElkAxiomVisitor<IndexedEntailmentQuery<? extends Entailment>> {

	private final ElkObject.Factory elkFactory_;
	private final ElkPolarityExpressionConverter positiveConverter_;
	private final ElkPolarityExpressionConverter negativeConverter_;

	/**
	 * @param elkFactory
	 *            {@link ElkObject} factory.
	 * @param index
	 *            Indexed queries are inserted into/deleted from this index.
	 * @param increment
	 *            How should occurrence counts of indexed object change.
	 */
	public EntailmentQueryConverter(final ElkObject.Factory elkFactory,
			final ModifiableOntologyIndex index, final int increment) {
		this.elkFactory_ = elkFactory;
		final ModifiableIndexedObjectBaseFactory baseFactory = new ModifiableIndexedObjectBaseFactory();
		final ModifiableIndexedObject.Factory positiveFactory = new UpdatingModifiableIndexedObjectFactory(
				baseFactory, index,
				OccurrenceIncrement.getPositiveIncrement(increment));
		final ModifiableIndexedObject.Factory negativeFactory = new UpdatingModifiableIndexedObjectFactory(
				baseFactory, index,
				OccurrenceIncrement.getNegativeIncrement(increment));
		this.positiveConverter_ = new ElkPolarityExpressionConverterImpl(
				ElkPolarity.POSITIVE, elkFactory, positiveFactory,
				negativeFactory, index, increment);
		this.negativeConverter_ = positiveConverter_
				.getComplementaryConverter();
	}

	@Override
	protected IndexedEntailmentQuery<? extends Entailment> defaultVisit(
			final ElkAxiom axiom) {
		throw new ElkIndexingUnsupportedException(axiom);
	}

	@Override
	public ClassAssertionEntailmentQuery visit(
			final ElkClassAssertionAxiom axiom) {
		final IndexedIndividual individual = axiom.getIndividual()
				.accept(positiveConverter_);
		final IndexedClassExpression classExpression = axiom
				.getClassExpression().accept(negativeConverter_);

		return new ClassAssertionEntailmentQuery(
				new ClassAssertionAxiomEntailmentImpl(axiom), individual,
				classExpression);
	}

	@Override
	public DifferentIndividualsEntailmentQuery visit(
			final ElkDifferentIndividualsAxiom axiom) {

		final List<? extends ElkIndividual> individuals = axiom
				.getIndividuals();
		final List<ElkClassExpression> nominals = new ArrayList<ElkClassExpression>(
				individuals.size());
		for (final ElkIndividual individual : individuals) {
			nominals.add(elkFactory_.getObjectOneOf(individual));
		}
		final DisjointClassesEntailmentQuery disjointness = visit(
				elkFactory_.getDisjointClassesAxiom(nominals));

		return new DifferentIndividualsEntailmentQuery(
				new DifferentIndividualsAxiomEntailmentImpl(axiom),
				disjointness);
	}

	@Override
	public DisjointClassesEntailmentQuery visit(
			final ElkDisjointClassesAxiom axiom) {

		final List<? extends ElkClassExpression> disjoint = axiom
				.getClassExpressions();
		final int size = disjoint.size();
		final List<SubClassOfEntailmentQuery> premises = new ArrayList<SubClassOfEntailmentQuery>(
				size);

		for (int first = 0; first < size - 1; first++) {
			for (int second = first + 1; second < size; second++) {

				final SubClassOfEntailmentQuery subsumption = visit(elkFactory_
						.getSubClassOfAxiom(elkFactory_.getObjectIntersectionOf(
								disjoint.get(first), disjoint.get(second)),
								elkFactory_.getOwlNothing()));

				premises.add(subsumption);

			}
		}

		return new DisjointClassesEntailmentQuery(
				new DisjointClassesAxiomEntailmentImpl(axiom), premises);
	}

	@Override
	public EquivalentClassesEntailmentQuery visit(
			final ElkEquivalentClassesAxiom axiom) {
		final List<? extends ElkClassExpression> elkClassExpressions = axiom
				.getClassExpressions();

		final List<SubClassOfEntailmentQuery> subsumptionCycle = new ArrayList<SubClassOfEntailmentQuery>(
				elkClassExpressions.size());

		ElkClassExpression elkSubclass = elkClassExpressions
				.get(elkClassExpressions.size() - 1);
		for (int i = 0; i < elkClassExpressions.size(); i++) {
			final ElkClassExpression elkSuperclass = elkClassExpressions.get(i);

			final SubClassOfEntailmentQuery subsumption = visit(
					elkFactory_.getSubClassOfAxiom(elkSubclass, elkSuperclass));

			subsumptionCycle.add(subsumption);

			elkSubclass = elkSuperclass;
		}

		return new EquivalentClassesEntailmentQuery(
				new EquivalentClassesAxiomEntailmentImpl(axiom),
				subsumptionCycle);
	}

	@Override
	public ObjectPropertyAssertionEntailmentQuery visit(
			final ElkObjectPropertyAssertionAxiom axiom) {

		final IndexedIndividual subject = axiom.getSubject()
				.accept(positiveConverter_);
		final IndexedClassExpression ovjectExistential = elkFactory_
				.getObjectSomeValuesFrom(axiom.getProperty(),
						elkFactory_.getObjectOneOf(axiom.getObject()))
				.accept(negativeConverter_);

		return new ObjectPropertyAssertionEntailmentQuery(
				new ObjectPropertyAssertionAxiomEntailmentImpl(axiom), subject,
				ovjectExistential);
	}

	@Override
	public ObjectPropertyDomainEntailmentQuery visit(
			final ElkObjectPropertyDomainAxiom axiom) {

		final IndexedClassExpression existential = elkFactory_
				.getObjectSomeValuesFrom(axiom.getProperty(),
						elkFactory_.getOwlThing())
				.accept(positiveConverter_);
		final IndexedClassExpression domain = axiom.getDomain()
				.accept(negativeConverter_);

		return new ObjectPropertyDomainEntailmentQuery(
				new ObjectPropertyDomainAxiomEntailmentImpl(axiom), existential,
				domain);
	}

	@Override
	public SameIndividualEntailmentQuery visit(
			final ElkSameIndividualAxiom axiom) {

		final List<? extends ElkIndividual> individuals = axiom
				.getIndividuals();
		final List<ElkClassExpression> nominals = new ArrayList<ElkClassExpression>(
				individuals.size());
		for (final ElkIndividual individual : individuals) {
			nominals.add(elkFactory_.getObjectOneOf(individual));
		}
		final EquivalentClassesEntailmentQuery equivalence = visit(
				elkFactory_.getEquivalentClassesAxiom(nominals));

		return new SameIndividualEntailmentQuery(
				new SameIndividualAxiomEntailmentImpl(axiom), equivalence);
	}

	@Override
	public SubClassOfEntailmentQuery visit(final ElkSubClassOfAxiom axiom) {
		final IndexedClassExpression subClass = axiom.getSubClassExpression()
				.accept(positiveConverter_);
		final IndexedClassExpression superClass = axiom
				.getSuperClassExpression().accept(negativeConverter_);

		return new SubClassOfEntailmentQuery(
				new SubClassOfAxiomEntailmentImpl(axiom), subClass, superClass);
	}

	/**
	 * One-parameter, public, non-static methods called "visit" and declared in
	 * this class enumerate subclasses of {@link ElkAxiom} (parameter) for which
	 * entailment queries are supported. This method returns {@code true} iff
	 * the subclass of {@link ElkAxiom} specified as the parameter is a
	 * parameter type of some of these methods, e.g., whether entailment query
	 * of such an {@link ElkAxiom} is supported.
	 * 
	 * @param axiomClass
	 * @return
	 */
	public static boolean isEntailmentCheckingSupported(
			final Class<? extends ElkAxiom> axiomClass) {

		for (final Method declaredMethod : EntailmentQueryConverter.class
				.getDeclaredMethods()) {

			final int mod = declaredMethod.getModifiers();
			final Class<?>[] parameterTypes = declaredMethod
					.getParameterTypes();
			if ("visit".equals(declaredMethod.getName())
					&& Modifier.isPublic(mod) && !Modifier.isStatic(mod)
					&& parameterTypes.length == 1
					&& parameterTypes[0].isAssignableFrom(axiomClass)) {
				// There is a declared visit method that accepts axiomClass
				return true;
			}

		}

		return false;
	}

}
