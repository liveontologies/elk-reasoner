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
import org.semanticweb.elk.owl.iris.ElkIri;
import org.semanticweb.elk.owl.predefined.PredefinedElkIri;
import org.semanticweb.elk.owl.printers.OwlFunctionalStylePrinter;
import org.semanticweb.elk.reasoner.taxonomy.hashing.InstanceTaxonomyHasher;
import org.semanticweb.elk.reasoner.taxonomy.hashing.TaxonomyHasher;
import org.semanticweb.elk.reasoner.taxonomy.nodes.InstanceNode;
import org.semanticweb.elk.reasoner.taxonomy.nodes.Node;
import org.semanticweb.elk.reasoner.taxonomy.nodes.TaxonomyNode;

/**
 * Class of static helper functions for printing and hashing a taxonomy. It is
 * primarily intended to be used for controlling the output of classification.
 * 
 * @author Markus Kroetzsch
 * 
 * @author "Yevgeny Kazakov"
 */
public class TaxonomyPrinter {

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
	public static void dumpClassTaxomomyToFile(
			Taxonomy<ElkIri, ElkClass> taxonomy, String fileName,
			boolean addHash) throws IOException {
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
	public static void dumpClassTaxomomy(Taxonomy<ElkIri, ElkClass> taxonomy,
			Writer writer, boolean addHash) throws IOException {
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
			InstanceTaxonomy<ElkIri, ElkClass, ElkIri, ElkNamedIndividual> taxonomy,
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
			InstanceTaxonomy<ElkIri, ElkClass, ElkIri, ElkNamedIndividual> taxonomy,
			Writer writer, boolean addHash) throws IOException {
		writer.write("Ontology(\n");
		processInstanceTaxomomy(taxonomy, writer);
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
	public static String getHashString(Taxonomy<ElkIri, ElkClass> taxonomy) {
		return Integer.toHexString((new TaxonomyHasher<ElkIri>())
				.hash(taxonomy));
	}

	public static String getInstanceHashString(
			InstanceTaxonomy<ElkIri, ElkClass, ElkIri, ElkNamedIndividual> taxonomy) {
		return Integer
				.toHexString((new InstanceTaxonomyHasher<ElkIri, ElkIri>())
						.hash(taxonomy));
	}

	/**
	 * Prints class declarations
	 * 
	 * @param classTaxonomy
	 * @param objectFactory
	 * @param writer
	 * @throws IOException
	 */
	protected static void printDeclarations(
			Taxonomy<ElkIri, ElkClass> classTaxonomy,
			ElkObjectFactory objectFactory, Appendable writer)
			throws IOException {

		for (TaxonomyNode<ElkIri, ElkClass> classNode : classTaxonomy
				.getNodes()) {
			for (ElkClass member : classNode.getMembersLookup().values()) {
				if (member.getIri().equals(PredefinedElkIri.OWL_THING.get())
						|| member.getIri().equals(
								PredefinedElkIri.OWL_NOTHING.get()))
					// owl:Thing and owl:Nothing are assumed to be already
					// declared
					continue;
				ElkDeclarationAxiom declarationAxiom = objectFactory
						.getDeclarationAxiom(member);
				OwlFunctionalStylePrinter
						.append(writer, declarationAxiom, true);
				writer.append('\n');
			}
		}

	}

	protected static void printIndividualDeclarations(
			Set<? extends InstanceNode<ElkIri, ElkClass, ElkIri, ElkNamedIndividual>> individualNodes,
			ElkObjectFactory objectFactory, Writer writer) throws IOException {
		for (InstanceNode<ElkIri, ElkClass, ElkIri, ElkNamedIndividual> individualNode : individualNodes) {
			for (ElkNamedIndividual individual : individualNode
					.getMembersLookup().values()) {
				ElkDeclarationAxiom declarationAxiom = objectFactory
						.getDeclarationAxiom(individual);
				OwlFunctionalStylePrinter
						.append(writer, declarationAxiom, true);
				writer.append('\n');
			}
		}
	}

	/**
	 * Process a taxonomy and write a normalized serialization.
	 * 
	 * @param classTaxonomy
	 * @param writer
	 * @throws IOException
	 */
	protected static void processTaxomomy(
			Taxonomy<ElkIri, ElkClass> classTaxonomy, Appendable writer)
			throws IOException {

		ElkObjectFactory objectFactory = new ElkObjectFactoryImpl();

		printDeclarations(classTaxonomy, objectFactory, writer);

		for (TaxonomyNode<ElkIri, ElkClass> node : classTaxonomy.getNodes()) {

			if (node.getMembersLookup().size() > 1) {
				ElkEquivalentClassesAxiom equivalentClassesAxiom = objectFactory
						.getEquivalentClassesAxiom(new ArrayList<ElkClass>(node
								.getMembersLookup().values()));
				OwlFunctionalStylePrinter.append(writer,
						equivalentClassesAxiom, true);
				writer.append('\n');
			}

			if (node.getMembersLookup().containsKey(
					PredefinedElkIri.OWL_NOTHING.get()))
				// do not print (redundant) super-classes of owl:Nothing
				continue;

			ElkClass nodeRepresentative = node.getMembersLookup().values()
					.iterator().next();

			for (Node<ElkIri, ElkClass> superNode : node.getDirectSuperNodes()) {
				if (superNode.getMembersLookup().containsKey(
						PredefinedElkIri.OWL_THING.get()))
					// do not print (redundant) sub-classes owl:Thing
					// TODO: sufficient to check if the superNode equals to the
					// top node??
					continue;
				ElkClass superNodeRepresentative = superNode.getMembersLookup()
						.values().iterator().next();
				ElkSubClassOfAxiom subClassAxiom = objectFactory
						.getSubClassOfAxiom(nodeRepresentative,
								superNodeRepresentative);
				OwlFunctionalStylePrinter.append(writer, subClassAxiom, true);
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

		if (!elkClass.getIri().equals(PredefinedElkIri.OWL_THING.get()))
			for (ElkClass elkSubClass : orderedSubClasses)
				if (!elkSubClass.getIri().equals(
						PredefinedElkIri.OWL_NOTHING.get())) {
					ElkSubClassOfAxiom elkSubClassAxiom = objectFactory
							.getSubClassOfAxiom(elkSubClass, elkClass);
					OwlFunctionalStylePrinter.append(writer, elkSubClassAxiom,
							true);
					writer.append('\n');
				}
	}

	protected static void processInstanceTaxomomy(
			InstanceTaxonomy<ElkIri, ElkClass, ElkIri, ElkNamedIndividual> taxonomy,
			Writer writer) throws IOException {

		ElkObjectFactory objectFactory = new ElkObjectFactoryImpl();

		printIndividualDeclarations(taxonomy.getInstanceNodes(), objectFactory,
				writer);
		// print the TBox
		processTaxomomy(taxonomy, writer);

		// print the ABox
		for (InstanceNode<ElkIri, ElkClass, ElkIri, ElkNamedIndividual> node : taxonomy
				.getInstanceNodes()) {

			if (node.getMembersLookup().size() > 1) {
				ElkSameIndividualAxiom sameIndividualAxiom = objectFactory
						.getSameIndividualAxiom(new ArrayList<ElkNamedIndividual>(
								node.getMembersLookup().values()));
				OwlFunctionalStylePrinter.append(writer, sameIndividualAxiom,
						true);
				writer.append('\n');
			}

			ElkNamedIndividual nodeRepresentative = node.getMembersLookup()
					.values().iterator().next();

			for (TaxonomyNode<ElkIri, ElkClass> typeNode : node
					.getDirectTypeNodes()) {
				if (typeNode.getMembersLookup().containsKey(
						PredefinedElkIri.OWL_THING.get()))
					// do not print (redundant) sub-classes owl:Thing
					// TODO: sufficient to check if the superNode equals to
					// the top node??
					continue;
				ElkClass typeNodeRepresentative = typeNode.getMembersLookup()
						.values().iterator().next();
				ElkClassAssertionAxiom assertionAxiom = objectFactory
						.getClassAssertionAxiom(typeNodeRepresentative,
								nodeRepresentative);
				OwlFunctionalStylePrinter.append(writer, assertionAxiom, true);
				writer.append('\n');
			}

		}

	}
}