/**
 * 
 */
package org.semanticweb.elk.ore;

/*
 * #%L
 * ELK ORE build
 * $Id:$
 * $HeadURL:$
 * %%
 * Copyright (C) 2011 - 2014 Department of Computer Science, University of Oxford
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

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import org.semanticweb.elk.io.IOUtils;
import org.semanticweb.elk.owl.interfaces.ElkClass;
import org.semanticweb.elk.owl.interfaces.ElkClassAssertionAxiom;
import org.semanticweb.elk.owl.interfaces.ElkDeclarationAxiom;
import org.semanticweb.elk.owl.interfaces.ElkEntity;
import org.semanticweb.elk.owl.interfaces.ElkEquivalentClassesAxiom;
import org.semanticweb.elk.owl.interfaces.ElkNamedIndividual;
import org.semanticweb.elk.owl.interfaces.ElkObject;
import org.semanticweb.elk.owl.interfaces.ElkSameIndividualAxiom;
import org.semanticweb.elk.owl.interfaces.ElkSubClassOfAxiom;
import org.semanticweb.elk.owl.managers.ElkObjectEntityRecyclingFactory;
import org.semanticweb.elk.owl.predefined.PredefinedElkIris;
import org.semanticweb.elk.owl.printers.OwlFunctionalStylePrinter;
import org.semanticweb.elk.reasoner.taxonomy.ElkClassKeyProvider;
import org.semanticweb.elk.reasoner.taxonomy.ElkIndividualKeyProvider;
import org.semanticweb.elk.reasoner.taxonomy.model.InstanceNode;
import org.semanticweb.elk.reasoner.taxonomy.model.InstanceTaxonomy;
import org.semanticweb.elk.reasoner.taxonomy.model.Taxonomy;
import org.semanticweb.elk.reasoner.taxonomy.model.TaxonomyNode;

/**
 * TaxonomyPrinter that satisfies the ORE specification
 * 
 * @author Pavel Klinov
 * 
 *         pavel.klinov@uni-ulm.de
 */
public class OreTaxonomyPrinter {

	protected static Comparator<ElkEntity>
			CLASS_COMPARATOR = ElkClassKeyProvider.INSTANCE.getComparator(),
			INDIVIDUAL_COMPARATOR = ElkIndividualKeyProvider.INSTANCE.getComparator();

	// prints SubClassOf(A, owl:Thing) for all direct subclasses of owl:Thing
	static void printClassTaxonomy(Taxonomy<ElkClass> taxonomy, File out)
			throws IOException {
		FileWriter fstream = null;
		BufferedWriter writer = null;

		try {
			fstream = new FileWriter(out);
			writer = new BufferedWriter(fstream);

			writer.append("Ontology(\n");

			processTaxomomy(taxonomy, writer);

			writer.append(")\n");
			writer.flush();

		} finally {
			IOUtils.closeQuietly(fstream);
			IOUtils.closeQuietly(writer);
		}
	}

	static void printInstanceTaxonomy(
			InstanceTaxonomy<ElkClass, ElkNamedIndividual> taxonomy, File out)
			throws IOException {
		FileWriter fstream = null;
		BufferedWriter writer = null;

		try {
			fstream = new FileWriter(out);
			writer = new BufferedWriter(fstream);

			writer.append("Ontology(\n");

			processInstanceTaxomomy(taxonomy, writer);

			writer.append(")\n");
			writer.flush();

		} finally {
			IOUtils.closeQuietly(fstream);
			IOUtils.closeQuietly(writer);
		}
	}

	protected static void processTaxomomy(Taxonomy<ElkClass> classTaxonomy,
			Appendable writer) throws IOException {
		ElkObject.Factory objectFactory = new ElkObjectEntityRecyclingFactory();

		printDeclarations(classTaxonomy, objectFactory, writer);

		TreeSet<ElkClass> canonicalElkClasses = new TreeSet<ElkClass>(
				CLASS_COMPARATOR);
		for (TaxonomyNode<ElkClass> classNode : classTaxonomy.getNodes())
			canonicalElkClasses.add(classNode.getCanonicalMember());

		for (ElkClass elkClass : canonicalElkClasses) {
			TaxonomyNode<ElkClass> classNode = classTaxonomy.getNode(elkClass);

			ArrayList<ElkClass> orderedEquivalentClasses = new ArrayList<ElkClass>(
					classNode.size());
			for (ElkClass member : classNode) {
				orderedEquivalentClasses.add(member);
			}
			Collections.sort(orderedEquivalentClasses, CLASS_COMPARATOR);

			TreeSet<ElkClass> orderedSubClasses = new TreeSet<ElkClass>(
					CLASS_COMPARATOR);
			for (TaxonomyNode<ElkClass> childNode : classNode
					.getDirectSubNodes()) {
				orderedSubClasses.add(childNode.getCanonicalMember());
			}
			// adding owl:Thing if the class is its direct subclass
			if (orderedEquivalentClasses.isEmpty()
					&& orderedSubClasses.isEmpty()) {
				orderedSubClasses.add(objectFactory.getOwlThing());
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
			ElkObject.Factory objectFactory, Appendable writer)
			throws IOException {

		List<ElkClass> classes = new ArrayList<ElkClass>(classTaxonomy
				.getNodes().size() * 2);

		for (TaxonomyNode<ElkClass> classNode : classTaxonomy.getNodes()) {
			for (ElkClass clazz : classNode) {
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

	protected static void processInstanceTaxomomy(
			InstanceTaxonomy<ElkClass, ElkNamedIndividual> taxonomy,
			Writer writer) throws IOException {
		ElkObject.Factory objectFactory = new ElkObjectEntityRecyclingFactory();

		printIndividualDeclarations(taxonomy.getInstanceNodes(), objectFactory,
				writer);
		// TBox printed here
		// processTaxomomy(taxonomy, writer);
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
					node.size());
			for (ElkNamedIndividual member : node) {
				orderedSameIndividuals.add(member);
			}
			Collections.sort(orderedSameIndividuals, INDIVIDUAL_COMPARATOR);

			TreeSet<ElkClass> orderedTypes = new TreeSet<ElkClass>(
					CLASS_COMPARATOR);
			for (TaxonomyNode<ElkClass> typeNode : node.getAllTypeNodes()) {
				for (ElkClass member : typeNode) {
					orderedTypes.add(member);
				}
			}

			orderedTypes.add(objectFactory.getOwlThing());

			printIndividualAxioms(individual, orderedSameIndividuals,
					orderedTypes, objectFactory, writer);
		}
	}

	protected static void printIndividualDeclarations(
			Set<? extends InstanceNode<ElkClass, ElkNamedIndividual>> individualNodes,
			ElkObject.Factory objectFactory, Writer writer) throws IOException {
		for (InstanceNode<ElkClass, ElkNamedIndividual> individualNode : individualNodes) {
			for (ElkNamedIndividual individual : individualNode) {
				ElkDeclarationAxiom decl = objectFactory
						.getDeclarationAxiom(individual);
				OwlFunctionalStylePrinter.append(writer, decl, true);
				writer.append('\n');
			}
		}
	}

	protected static void printClassAxioms(ElkClass elkClass,
			ArrayList<ElkClass> orderedEquivalentClasses,
			TreeSet<ElkClass> orderedSubClasses, Appendable writer)
			throws IOException {

		ElkObject.Factory objectFactory = new ElkObjectEntityRecyclingFactory();

		if (orderedEquivalentClasses.size() > 1) {
			ElkEquivalentClassesAxiom elkEquivalentClassesAxiom = objectFactory
					.getEquivalentClassesAxiom(orderedEquivalentClasses);
			OwlFunctionalStylePrinter.append(writer, elkEquivalentClassesAxiom,
					true);
			writer.append('\n');
		}

		for (ElkClass elkSubClass : orderedSubClasses)
			if (!elkSubClass.getIri().equals(PredefinedElkIris.OWL_NOTHING)) {
				ElkSubClassOfAxiom elkSubClassAxiom = objectFactory
						.getSubClassOfAxiom(elkSubClass, elkClass);
				OwlFunctionalStylePrinter
						.append(writer, elkSubClassAxiom, true);
				writer.append('\n');
			}
	}

	protected static void printIndividualAxioms(ElkNamedIndividual individual,
			ArrayList<ElkNamedIndividual> orderedSameIndividuals,
			TreeSet<ElkClass> orderedDirectClasses,
			ElkObject.Factory objectFactory, Writer writer) throws IOException {

		if (orderedSameIndividuals.size() > 1) {
			ElkSameIndividualAxiom axiom = objectFactory
					.getSameIndividualAxiom(orderedSameIndividuals);

			OwlFunctionalStylePrinter.append(writer, axiom, true);
			writer.append('\n');
		}

		for (ElkClass clazz : orderedDirectClasses) {
			ElkClassAssertionAxiom axiom = objectFactory
					.getClassAssertionAxiom(clazz, individual);

			OwlFunctionalStylePrinter.append(writer, axiom, true);
			writer.append('\n');
		}
	}
}