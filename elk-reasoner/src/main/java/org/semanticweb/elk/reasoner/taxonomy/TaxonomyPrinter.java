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
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import org.semanticweb.elk.owl.implementation.ElkObjectFactoryImpl;
import org.semanticweb.elk.owl.interfaces.ElkClass;
import org.semanticweb.elk.owl.interfaces.ElkClassAssertionAxiom;
import org.semanticweb.elk.owl.interfaces.ElkDeclarationAxiom;
import org.semanticweb.elk.owl.interfaces.ElkEquivalentClassesAxiom;
import org.semanticweb.elk.owl.interfaces.ElkNamedIndividual;
import org.semanticweb.elk.owl.interfaces.ElkObjectFactory;
import org.semanticweb.elk.owl.interfaces.ElkSameIndividualAxiom;
import org.semanticweb.elk.owl.interfaces.ElkSubClassOfAxiom;
import org.semanticweb.elk.owl.predefined.PredefinedElkIris;
import org.semanticweb.elk.owl.printers.OwlFunctionalStylePrinter;
import org.semanticweb.elk.owl.util.Comparators;
import org.semanticweb.elk.reasoner.taxonomy.hashing.InstanceTaxonomyHasher;
import org.semanticweb.elk.reasoner.taxonomy.hashing.TaxonomyHasher;
import org.semanticweb.elk.reasoner.taxonomy.model.InstanceNode;
import org.semanticweb.elk.reasoner.taxonomy.model.InstanceTaxonomy;
import org.semanticweb.elk.reasoner.taxonomy.model.Taxonomy;
import org.semanticweb.elk.reasoner.taxonomy.model.TaxonomyNode;

/**
 * Class of static helper functions for printing and hashing a taxonomy. It is
 * primarily intended to be used for controlling the output of classification.
 * 
 * @author Markus Kroetzsch
 */
public class TaxonomyPrinter {

	protected static Comparator<ElkClass> CLASS_COMPARATOR = Comparators.ELK_CLASS_COMPARATOR;
	protected static Comparator<ElkNamedIndividual> INDIVIDUAL_COMPARATOR = Comparators.ELK_NAMED_INDIVIDUAL_COMPARATOR;

	/**
	 * Convenience method for printing a {@link Taxonomy} to a file at the given
	 * location.
	 * 
	 * @see org.semanticweb.elk.reasoner.taxonomy.TaxonomyPrinter#dumpClassTaxomomy
	 * 
	 * @param taxonomy
	 * @param fileName
	 * @param addHash
	 *            if true, a hash string will be added at the end of the output
	 *            using comment syntax of OWL 2 Functional Style
	 * @throws IOException
	 *             If an I/O error occurs
	 */
	public static void dumpClassTaxomomyToFile(Taxonomy<ElkClass> taxonomy,
			String fileName, boolean addHash) throws IOException {
		FileWriter fstream = new FileWriter(fileName);
		BufferedWriter writer = new BufferedWriter(fstream);

		dumpClassTaxomomy(taxonomy, writer, addHash);
		writer.close();
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
	public static void dumpClassTaxomomy(Taxonomy<ElkClass> taxonomy,
			Writer writer, boolean addHash) throws IOException {
		writer.append("Ontology(\n");
		TaxonomyPrinter.processTaxomomy(taxonomy, writer);
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
			InstanceTaxonomy<ElkClass, ElkNamedIndividual> taxonomy,
			String fileName, boolean addHash) throws IOException {
		FileWriter fstream = new FileWriter(fileName);
		BufferedWriter writer = new BufferedWriter(fstream);

		dumpInstanceTaxomomy(taxonomy, writer, addHash);
		writer.close();
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
			InstanceTaxonomy<ElkClass, ElkNamedIndividual> taxonomy,
			Writer writer, boolean addHash) throws IOException {
		writer.write("Ontology(\n");
		TaxonomyPrinter.processInstanceTaxomomy(taxonomy, writer);
		writer.write(")\n");

		if (addHash) {
			writer.write("\n# Hash code: " + getInstanceHashString(taxonomy)
					+ "\n");
		}
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
	public static String getHashString(Taxonomy<ElkClass> taxonomy) {
		return Integer.toHexString(TaxonomyHasher.hash(taxonomy));
	}

	public static String getInstanceHashString(
			InstanceTaxonomy<ElkClass, ElkNamedIndividual> taxonomy) {
		return Integer.toHexString(InstanceTaxonomyHasher.hash(taxonomy));
	}

	/**
	 * Process a taxonomy and write a normalized serialization.
	 * 
	 * @param classTaxonomy
	 * @param writer
	 * @throws IOException
	 */
	protected static void processTaxomomy(Taxonomy<ElkClass> classTaxonomy,
			Appendable writer) throws IOException {

		ElkObjectFactory objectFactory = new ElkObjectFactoryImpl();

		printDeclarations(classTaxonomy, objectFactory, writer);

		TreeSet<ElkClass> canonicalElkClasses = new TreeSet<ElkClass>(
				CLASS_COMPARATOR);
		for (TaxonomyNode<ElkClass> classNode : classTaxonomy.getNodes())
			canonicalElkClasses.add(classNode.getCanonicalMember());

		for (ElkClass elkClass : canonicalElkClasses) {
			TaxonomyNode<ElkClass> classNode = classTaxonomy.getNode(elkClass);

			ArrayList<ElkClass> orderedEquivalentClasses = new ArrayList<ElkClass>(
					classNode.getMembers());
			Collections.sort(orderedEquivalentClasses, CLASS_COMPARATOR);

			TreeSet<ElkClass> orderedSubClasses = new TreeSet<ElkClass>(
					CLASS_COMPARATOR);
			for (TaxonomyNode<ElkClass> childNode : classNode
					.getDirectSubNodes()) {
				orderedSubClasses.add(childNode.getCanonicalMember());
			}

			printClassAxioms(elkClass, orderedEquivalentClasses,
					orderedSubClasses, writer);
		}
	}

	/**
	 * Prints class declarations
	 * 
	 * @param classTaxonomy
	 * @param objectFactory
	 * @param writer
	 * @throws IOException
	 */
	protected static void printDeclarations(Taxonomy<ElkClass> classTaxonomy,
			ElkObjectFactory objectFactory, Appendable writer)
			throws IOException {

		List<ElkClass> classes = new ArrayList<ElkClass>(classTaxonomy
				.getNodes().size() * 2);

		for (TaxonomyNode<ElkClass> classNode : classTaxonomy.getNodes()) {
			for (ElkClass clazz : classNode.getMembers()) {
				if (!clazz.getIri().equals(PredefinedElkIris.OWL_THING)
						&& !clazz.getIri()
								.equals(PredefinedElkIris.OWL_NOTHING)) {
					classes.add(clazz);

				}
			}
		}

		Collections.sort(classes, CLASS_COMPARATOR);

		for (ElkClass clazz : classes) {
			ElkDeclarationAxiom decl = objectFactory.getDeclarationAxiom(clazz);
			OwlFunctionalStylePrinter.append(writer, decl, true);
			writer.append('\n');
		}
	}

	protected static void printIndividualDeclarations(
			Set<? extends InstanceNode<ElkClass, ElkNamedIndividual>> individualNodes,
			ElkObjectFactory objectFactory, Writer writer) throws IOException {
		for (InstanceNode<ElkClass, ElkNamedIndividual> individualNode : individualNodes) {
			for (ElkNamedIndividual individual : individualNode.getMembers()) {
				ElkDeclarationAxiom decl = objectFactory
						.getDeclarationAxiom(individual);
				OwlFunctionalStylePrinter.append(writer, decl, true);
				writer.append('\n');
			}
		}
	}

	/**
	 * Process axioms related to one {@link ElkClass}, where the relevant
	 * related classes are given in two ordered collections of equivalent
	 * classes and subclasses, respectively. The method serializes the axioms to
	 * the Writer.
	 * 
	 * @param elkClass
	 * @param orderedEquivalentClasses
	 * @param orderedSubClasses
	 * @param writer
	 * @throws IOException
	 */
	protected static void printClassAxioms(ElkClass elkClass,
			ArrayList<ElkClass> orderedEquivalentClasses,
			TreeSet<ElkClass> orderedSubClasses, Appendable writer)
			throws IOException {

		ElkObjectFactory objectFactory = new ElkObjectFactoryImpl();

		if (orderedEquivalentClasses.size() > 1) {
			ElkEquivalentClassesAxiom elkEquivalentClassesAxiom = objectFactory
					.getEquivalentClassesAxiom(orderedEquivalentClasses);
			OwlFunctionalStylePrinter.append(writer, elkEquivalentClassesAxiom,
					true);
			writer.append('\n');
		}

		if (!elkClass.getIri().equals(PredefinedElkIris.OWL_THING))
			for (ElkClass elkSubClass : orderedSubClasses)
				if (!elkSubClass.getIri().equals(PredefinedElkIris.OWL_NOTHING)) {
					ElkSubClassOfAxiom elkSubClassAxiom = objectFactory
							.getSubClassOfAxiom(elkSubClass, elkClass);
					OwlFunctionalStylePrinter.append(writer, elkSubClassAxiom,
							true);
					writer.append('\n');
				}
	}

	protected static void printIndividualAxioms(ElkNamedIndividual individual,
			ArrayList<ElkNamedIndividual> orderedSameIndividuals,
			TreeSet<ElkClass> orderedDirectClasses,
			ElkObjectFactory objectFactory, Writer writer) throws IOException {

		if (orderedSameIndividuals.size() > 1) {
			ElkSameIndividualAxiom axiom = objectFactory
					.getSameIndividualAxiom(orderedSameIndividuals);

			OwlFunctionalStylePrinter.append(writer, axiom, true);
			writer.append('\n');
		}

		for (ElkClass clazz : orderedDirectClasses) {
			if (!clazz.getIri().equals(PredefinedElkIris.OWL_THING)) {
				ElkClassAssertionAxiom axiom = objectFactory
						.getClassAssertionAxiom(clazz, individual);

				OwlFunctionalStylePrinter.append(writer, axiom, true);
				writer.append('\n');
			}
		}
	}

	protected static void processInstanceTaxomomy(
			InstanceTaxonomy<ElkClass, ElkNamedIndividual> taxonomy,
			Writer writer) throws IOException {

		ElkObjectFactory objectFactory = new ElkObjectFactoryImpl();

		printIndividualDeclarations(taxonomy.getInstanceNodes(), objectFactory,
				writer);
		// TBox printed here
		processTaxomomy(taxonomy, writer);
		// print the ABox
		TreeSet<ElkNamedIndividual> canonicalIndividuals = new TreeSet<ElkNamedIndividual>(
				INDIVIDUAL_COMPARATOR);
		for (InstanceNode<ElkClass, ElkNamedIndividual> node : taxonomy
				.getInstanceNodes()) {
			canonicalIndividuals.add(node.getCanonicalMember());
		}

		for (ElkNamedIndividual individual : canonicalIndividuals) {
			InstanceNode<ElkClass, ElkNamedIndividual> node = taxonomy
					.getInstanceNode(individual);

			ArrayList<ElkNamedIndividual> orderedSameIndividuals = new ArrayList<ElkNamedIndividual>(
					node.getMembers());
			Collections.sort(orderedSameIndividuals, INDIVIDUAL_COMPARATOR);

			TreeSet<ElkClass> orderedTypes = new TreeSet<ElkClass>(
					CLASS_COMPARATOR);
			for (TaxonomyNode<ElkClass> typeNode : node.getDirectTypeNodes()) {
				orderedTypes.add(typeNode.getCanonicalMember());
			}

			printIndividualAxioms(individual, orderedSameIndividuals,
					orderedTypes, objectFactory, writer);
		}
	}
}