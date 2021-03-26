/*
 * #%L
 * elk-reasoner
 * 
 * $Id$
 * $HeadURL$
 * %%
 * Copyright (C) 2011 Department of Computer Science, University of Oxford
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
package org.semanticweb.elk.reasoner.taxonomy;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;

import org.semanticweb.elk.owl.interfaces.ElkAxiom;
import org.semanticweb.elk.owl.interfaces.ElkClass;
import org.semanticweb.elk.owl.interfaces.ElkEntity;
import org.semanticweb.elk.owl.interfaces.ElkNamedIndividual;
import org.semanticweb.elk.owl.interfaces.ElkObject;
import org.semanticweb.elk.owl.interfaces.ElkObjectProperty;
import org.semanticweb.elk.owl.managers.ElkObjectEntityRecyclingFactory;
import org.semanticweb.elk.owl.printers.OwlFunctionalStylePrinter;
import org.semanticweb.elk.owl.visitors.AbstractElkEntityVisitor;
import org.semanticweb.elk.owl.visitors.ElkEntityVisitor;
import org.semanticweb.elk.reasoner.taxonomy.hashing.InstanceTaxonomyHasher;
import org.semanticweb.elk.reasoner.taxonomy.hashing.TaxonomyHasher;
import org.semanticweb.elk.reasoner.taxonomy.model.InstanceNode;
import org.semanticweb.elk.reasoner.taxonomy.model.InstanceTaxonomy;
import org.semanticweb.elk.reasoner.taxonomy.model.Taxonomy;
import org.semanticweb.elk.reasoner.taxonomy.model.TaxonomyNode;
import org.semanticweb.elk.reasoner.taxonomy.model.TypeNode;
import org.semanticweb.elk.util.collections.Operations;

/**
 * Class of static helper functions for printing and hashing a taxonomy. It is
 * primarily intended to be used for controlling the output of classification.
 * 
 * @author Markus Kroetzsch
 * @author Peter Skocovsky
 */
public class TaxonomyPrinter {

	/**
	 * Convenience method for printing a {@link Taxonomy} to a file at the given
	 * location.
	 * 
	 * @see #dumpTaxomomy(Taxonomy, Writer, boolean)
	 * 
	 * @param taxonomy
	 * @param fileName
	 * @param addHash
	 *            if true, a hash string will be added at the end of the output
	 *            using comment syntax of OWL 2 Functional Style
	 * @throws IOException
	 *             If an I/O error occurs
	 */
	public static void dumpTaxomomyToFile(
			final Taxonomy<? extends ElkEntity> taxonomy, final String fileName,
			final boolean addHash) throws IOException {
		final FileWriter fstream = new FileWriter(fileName);
		final BufferedWriter writer = new BufferedWriter(fstream);
		try {
			dumpTaxomomy(taxonomy, writer, addHash);
		} finally {
			writer.close();
		}
	}

	/**
	 * Print the contents of the given {@link Taxonomy} to the specified Writer.
	 * Expressions are ordered for generating the output, ensuring that the
	 * output is deterministic.
	 * 
	 * @param taxonomy
	 * @param writer
	 * @param addHash
	 *            if true, a hash string will be added at the end of the output
	 *            using comment syntax of OWL 2 Functional Style
	 * @throws IOException
	 *             If an I/O error occurs
	 */
	public static void dumpTaxomomy(
			final Taxonomy<? extends ElkEntity> taxonomy, final Writer writer,
			final boolean addHash) throws IOException {
		writer.append("Ontology(\n");
		processTaxomomy(taxonomy, writer);
		writer.append(")\n");

		if (addHash) {
			writer.append("\n# Hash code: " + getHashString(taxonomy) + "\n");
		}
		writer.flush();
	}

	/**
	 * Convenience method for printing an {@link InstanceTaxonomy} to a file at
	 * the given location.
	 * 
	 * @see org.semanticweb.elk.reasoner.taxonomy.TaxonomyPrinter#dumpInstanceTaxomomy
	 * 
	 * @param taxonomy
	 * @param fileName
	 * @param addHash
	 *            if true, a hash string will be added at the end of the output
	 *            using comment syntax of OWL 2 Functional Style
	 * @throws IOException
	 *             If an I/O error occurs
	 */
	public static void dumpInstanceTaxomomyToFile(
			final InstanceTaxonomy<? extends ElkEntity, ? extends ElkEntity> taxonomy,
			final String fileName, final boolean addHash) throws IOException {
		final FileWriter fstream = new FileWriter(fileName);
		final BufferedWriter writer = new BufferedWriter(fstream);
		try {
			dumpInstanceTaxomomy(taxonomy, writer, addHash);
		} finally {
			writer.close();
		}
	}

	/**
	 * Print the contents of the given {@link InstanceTaxonomy} to the specified
	 * Writer. Expressions are ordered for generating the output, ensuring that
	 * the output is deterministic.
	 * 
	 * @param taxonomy
	 * @param writer
	 * @param addHash
	 *            if true, a hash string will be added at the end of the output
	 *            using comment syntax of OWL 2 Functional Style
	 * @throws IOException
	 *             If an I/O error occurs
	 */
	public static void dumpInstanceTaxomomy(
			final InstanceTaxonomy<? extends ElkEntity, ? extends ElkEntity> taxonomy,
			final Writer writer, final boolean addHash) throws IOException {
		writer.write("Ontology(\n");
		processInstanceTaxomomy(taxonomy, writer);
		writer.write(")\n");

		if (addHash) {
			writer.write(
					"\n# Hash code: " + getInstanceHashString(taxonomy) + "\n");
		}
		writer.flush();
	}

	/**
	 * Get a has string for the given {@link Taxonomy}. Besides possible hash
	 * collisions (which have very low probability) the hash string is the same
	 * for two inputs if and only if the inputs describe the same taxonomy. So
	 * it can be used to compare classification results.
	 * 
	 * @param taxonomy
	 * @return hash string
	 */
	public static String getHashString(Taxonomy<? extends ElkEntity> taxonomy) {
		return Integer.toHexString(TaxonomyHasher.hash(taxonomy));
	}

	public static String getInstanceHashString(
			InstanceTaxonomy<? extends ElkEntity, ? extends ElkEntity> taxonomy) {
		return Integer.toHexString(InstanceTaxonomyHasher.hash(taxonomy));
	}

	/**
	 * Process a taxonomy and write a normalized serialization.
	 * 
	 * @param taxonomy
	 * @param writer
	 * @throws IOException
	 */
	protected static <T extends ElkEntity> void processTaxomomy(
			final Taxonomy<T> taxonomy, final Appendable writer)
					throws IOException {

		final ElkObject.Factory factory = new ElkObjectEntityRecyclingFactory();

		// Declarations.

		final List<T> members = new ArrayList<T>(
				taxonomy.getNodes().size() * 2);

		for (final TaxonomyNode<T> node : taxonomy.getNodes()) {
			for (final T member : node) {
				// TODO: this should check whether IRIs are predefined!
				if (!member.getIri()
						.equals(taxonomy.getTopNode().getCanonicalMember()
								.getIri())
						&& !member.getIri().equals(taxonomy.getBottomNode()
								.getCanonicalMember().getIri())) {
					members.add(member);
				}
			}
		}

		Collections.sort(members, taxonomy.getKeyProvider().getComparator());

		printDeclarations(members, factory, writer);

		// Relations.

		final TreeSet<T> canonicalMembers = new TreeSet<T>(
				taxonomy.getKeyProvider().getComparator());
		for (final TaxonomyNode<T> node : taxonomy.getNodes()) {
			canonicalMembers.add(node.getCanonicalMember());
		}

		for (final T canonicalMember : canonicalMembers) {
			final TaxonomyNode<T> node = taxonomy.getNode(canonicalMember);

			final ArrayList<T> orderedEquivalentMembers = new ArrayList<T>(
					node.size());
			for (final T member : node) {
				orderedEquivalentMembers.add(member);
			}
			Collections.sort(orderedEquivalentMembers,
					taxonomy.getKeyProvider().getComparator());

			final TreeSet<T> orderedSuperMembers = new TreeSet<T>(
					taxonomy.getKeyProvider().getComparator());
			for (final TaxonomyNode<T> superNode : node.getDirectSuperNodes()) {
				orderedSuperMembers.add(superNode.getCanonicalMember());
			}

			printMemberAxioms(canonicalMember, orderedEquivalentMembers,
					orderedSuperMembers, taxonomy, factory, writer);
		}
	}

	protected static <T extends ElkEntity> void printDeclarations(
			final Iterable<T> members, final ElkObject.Factory factory,
			final Appendable writer) throws IOException {
		for (final T member : members) {
			final ElkAxiom axiom = factory.getDeclarationAxiom(member);
			OwlFunctionalStylePrinter.append(writer, axiom, true);
			writer.append('\n');
		}
	}

	/**
	 * Process axioms related to one member of {@link Taxonomy}, where the
	 * relevant related members are given in two ordered collections of
	 * equivalent members and super-members, respectively. The method serializes
	 * the axioms to the Writer.
	 */
	protected static <T extends ElkEntity, I extends ElkEntity> void printMemberAxioms(
			final I member, final List<I> equivalentMembers,
			final SortedSet<T> directSuperMembers, final Taxonomy<T> taxonomy,
			final ElkObject.Factory factory, final Appendable writer)
					throws IOException {

		if (equivalentMembers.size() > 1) {
			final ElkAxiom axiom = member.accept(
					getEquivalentAxiomProvider(equivalentMembers, factory));
			OwlFunctionalStylePrinter.append(writer, axiom, true);
			writer.append('\n');
		}

		// TODO: this should exclude implicit axioms as owl:Thing ⊑ owl:Nothing
		if (!member.equals(taxonomy.getBottomNode().getCanonicalMember())) {
			for (final T superMember : directSuperMembers) {
				if (!superMember
						.equals(taxonomy.getTopNode().getCanonicalMember())) {
					final ElkAxiom axiom = member
							.accept(getSubAxiomProvider(superMember, factory));
					OwlFunctionalStylePrinter.append(writer, axiom, true);
					writer.append('\n');
				}
			}
		}

	}

	private static ElkEntityVisitor<ElkAxiom> getEquivalentAxiomProvider(
			final List<? extends ElkEntity> equivalent,
			final ElkObject.Factory factory) {

		return new AbstractElkEntityVisitor<ElkAxiom>() {

			@Override
			protected ElkAxiom defaultVisit(final ElkEntity entity) {
				return null;
			}

			@Override
			public ElkAxiom visit(final ElkClass cls) {
				return factory.getEquivalentClassesAxiom(
						new ArrayList<ElkClass>(Operations.getCollection(
								Operations.filter(equivalent, ElkClass.class),
								equivalent.size())));
			}

			@Override
			public ElkAxiom visit(final ElkNamedIndividual ind) {
				return factory.getSameIndividualAxiom(
						new ArrayList<ElkNamedIndividual>(
								Operations.getCollection(
										Operations.filter(equivalent,
												ElkNamedIndividual.class),
										equivalent.size())));
			}

			@Override
			public ElkAxiom visit(final ElkObjectProperty prop) {
				return factory.getEquivalentObjectPropertiesAxiom(
						new ArrayList<ElkObjectProperty>(
								Operations.getCollection(
										Operations.filter(equivalent,
												ElkObjectProperty.class),
										equivalent.size())));
			}

		};
	}

	private static ElkEntityVisitor<ElkAxiom> getSubAxiomProvider(
			final ElkEntity superEntity, final ElkObject.Factory factory) {

		return new AbstractElkEntityVisitor<ElkAxiom>() {

			@Override
			protected ElkAxiom defaultVisit(final ElkEntity entity) {
				return null;
			}

			@Override
			public ElkAxiom visit(final ElkClass cls) {
				if (superEntity instanceof ElkClass) {
					return factory.getSubClassOfAxiom(cls,
							(ElkClass) superEntity);
				}
				// else
				return defaultVisit(cls);
			}

			@Override
			public ElkAxiom visit(final ElkNamedIndividual ind) {
				if (superEntity instanceof ElkClass) {
					return factory.getClassAssertionAxiom(
							(ElkClass) superEntity, ind);
				}
				// else
				return defaultVisit(ind);
			}

			@Override
			public ElkAxiom visit(final ElkObjectProperty prop) {
				if (superEntity instanceof ElkObjectProperty) {
					return factory.getSubObjectPropertyOfAxiom(prop,
							(ElkObjectProperty) superEntity);
				}
				// else
				return defaultVisit(prop);
			}

		};
	}

	protected static <T extends ElkEntity, I extends ElkEntity> void processInstanceTaxomomy(
			final InstanceTaxonomy<T, I> taxonomy, final Appendable writer)
					throws IOException {

		final ElkObject.Factory factory = new ElkObjectEntityRecyclingFactory();

		// Declarations.

		final List<I> members = new ArrayList<I>(
				taxonomy.getInstanceNodes().size() * 2);

		for (final InstanceNode<T, I> node : taxonomy.getInstanceNodes()) {
			for (final I member : node) {
				members.add(member);
			}
		}

		Collections.sort(members,
				taxonomy.getInstanceKeyProvider().getComparator());

		printDeclarations(members, factory, writer);

		// TBox.

		processTaxomomy(taxonomy, writer);

		// ABox.

		final TreeSet<I> canonicalIndividuals = new TreeSet<I>(
				taxonomy.getInstanceKeyProvider().getComparator());
		for (final InstanceNode<T, I> node : taxonomy.getInstanceNodes()) {
			canonicalIndividuals.add(node.getCanonicalMember());
		}

		for (final I individual : canonicalIndividuals) {
			final InstanceNode<T, I> node = taxonomy
					.getInstanceNode(individual);

			final ArrayList<I> orderedSameIndividuals = new ArrayList<I>(
					node.size());
			for (final I member : node) {
				orderedSameIndividuals.add(member);
			}
			Collections.sort(orderedSameIndividuals,
					taxonomy.getInstanceKeyProvider().getComparator());

			final TreeSet<T> orderedTypes = new TreeSet<T>(
					taxonomy.getKeyProvider().getComparator());
			for (final TypeNode<T, I> typeNode : node.getDirectTypeNodes()) {
				orderedTypes.add(typeNode.getCanonicalMember());
			}

			printMemberAxioms(individual, orderedSameIndividuals, orderedTypes,
					taxonomy, factory, writer);
		}
	}

}