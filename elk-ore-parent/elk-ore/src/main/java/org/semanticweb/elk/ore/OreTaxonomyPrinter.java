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
import java.util.TreeSet;

import org.semanticweb.elk.io.IOUtils;
import org.semanticweb.elk.owl.implementation.ElkObjectFactoryImpl;
import org.semanticweb.elk.owl.interfaces.ElkClass;
import org.semanticweb.elk.owl.interfaces.ElkClassAssertionAxiom;
import org.semanticweb.elk.owl.interfaces.ElkEquivalentClassesAxiom;
import org.semanticweb.elk.owl.interfaces.ElkNamedIndividual;
import org.semanticweb.elk.owl.interfaces.ElkObjectFactory;
import org.semanticweb.elk.owl.interfaces.ElkSameIndividualAxiom;
import org.semanticweb.elk.owl.interfaces.ElkSubClassOfAxiom;
import org.semanticweb.elk.owl.predefined.PredefinedElkClass;
import org.semanticweb.elk.owl.predefined.PredefinedElkIri;
import org.semanticweb.elk.owl.printers.OwlFunctionalStylePrinter;
import org.semanticweb.elk.reasoner.taxonomy.TaxonomyPrinter;
import org.semanticweb.elk.reasoner.taxonomy.model.InstanceNode;
import org.semanticweb.elk.reasoner.taxonomy.model.InstanceTaxonomy;
import org.semanticweb.elk.reasoner.taxonomy.model.Taxonomy;
import org.semanticweb.elk.reasoner.taxonomy.model.TaxonomyNode;

/**
 * A wrapper around {@link TaxonomyPrinter} to satisfy the ORE specification
 * 
 * @author Pavel Klinov
 * 
 *         pavel.klinov@uni-ulm.de
 */
public class OreTaxonomyPrinter extends TaxonomyPrinter {

	// prints SubClassOf(A, owl:Thing) for all direct subclasses of owl:Thing
	static void printClassTaxonomy(Taxonomy<ElkClass> taxonomy, File out)
			throws IOException {
		FileWriter fstream = null;
		BufferedWriter writer = null;

		try {
			fstream = new FileWriter(out);
			writer = new BufferedWriter(fstream);

			writer.append("Ontology(\n");

			new OreTaxonomyPrinter().processTaxomomy(taxonomy, writer);

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

			new OreTaxonomyPrinter().processInstanceTaxomomy(taxonomy, writer);

			writer.append(")\n");
			writer.flush();

		} finally {
			IOUtils.closeQuietly(fstream);
			IOUtils.closeQuietly(writer);
		}
	}

	@Override
	protected void processTaxomomy(Taxonomy<ElkClass> classTaxonomy,
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
			// adding owl:Thing if the class is its direct subclass
			if (orderedEquivalentClasses.isEmpty()
					&& orderedSubClasses.isEmpty()) {
				orderedSubClasses.add(PredefinedElkClass.OWL_THING);
			}

			printClassAxioms(elkClass, orderedEquivalentClasses,
					orderedSubClasses, writer);
		}
	}

	@Override
	protected void processInstanceTaxomomy(
			InstanceTaxonomy<ElkClass, ElkNamedIndividual> taxonomy,
			Writer writer) throws IOException {
		ElkObjectFactory objectFactory = new ElkObjectFactoryImpl();

		printIndividualDeclarations(taxonomy.getInstanceNodes(), objectFactory,
				writer);
		// TBox printed here
		//processTaxomomy(taxonomy, writer);
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
			for (TaxonomyNode<ElkClass> typeNode : node.getAllTypeNodes()) {
				for (ElkClass member : typeNode.getMembers()) {
					orderedTypes.add(member);
				}
			}

			orderedTypes.add(PredefinedElkClass.OWL_THING);

			printIndividualAxioms(individual, orderedSameIndividuals,
					orderedTypes, objectFactory, writer);
		}
	}

	@Override
	protected void printClassAxioms(ElkClass elkClass,
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

		for (ElkClass elkSubClass : orderedSubClasses)
			if (!elkSubClass.getIri()
					.equals(PredefinedElkIri.OWL_NOTHING.get())) {
				ElkSubClassOfAxiom elkSubClassAxiom = objectFactory
						.getSubClassOfAxiom(elkSubClass, elkClass);
				OwlFunctionalStylePrinter
						.append(writer, elkSubClassAxiom, true);
				writer.append('\n');
			}
	}

	@Override
	protected void printIndividualAxioms(ElkNamedIndividual individual,
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
			ElkClassAssertionAxiom axiom = objectFactory
					.getClassAssertionAxiom(clazz, individual);

			OwlFunctionalStylePrinter.append(writer, axiom, true);
			writer.append('\n');
		}
	}
}
