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
import java.util.TreeSet;

import org.semanticweb.elk.owl.implementation.ElkObjectFactoryImpl;
import org.semanticweb.elk.owl.interfaces.ElkClass;
import org.semanticweb.elk.owl.interfaces.ElkEquivalentClassesAxiom;
import org.semanticweb.elk.owl.interfaces.ElkObjectFactory;
import org.semanticweb.elk.owl.interfaces.ElkSubClassOfAxiom;
import org.semanticweb.elk.owl.predefined.PredefinedElkIri;
import org.semanticweb.elk.owl.printers.OwlFunctionalStylePrinter;
import org.semanticweb.elk.owl.util.Comparators;

/**
 * Class of static helper functions for printing and hashing a taxonomy. It is
 * primarily intended to be used for controlling the output of classification.
 * 
 * @author Markus Kroetzsch
 */
public class ClassTaxonomyPrinter {

	protected static Comparator<ElkClass> comparator = Comparators.ELK_CLASS_COMPARATOR;

	/**
	 * Convenience method for printing a Taxonomy<ElkClass> to a file at the given
	 * location.
	 * 
	 * @see org.semanticweb.elk.reasoner.taxonomy.Taxonomy<ElkClass>Printer#dumpClassTaxomomy
	 * 
	 * @param classTaxonomy
	 * @param fileName
	 * @param addHash
	 *            if true, a hash string will be added at the end of the output
	 *            using comment syntax of OWL 2 Functional Style
	 */
	public static void dumpClassTaxomomyToFile(Taxonomy<ElkClass> classTaxonomy,
			String fileName, boolean addHash) throws IOException {
		FileWriter fstream = new FileWriter(fileName);
		BufferedWriter writer = new BufferedWriter(fstream);
		dumpClassTaxomomy(classTaxonomy, writer, addHash);
		writer.close();
	}

	/**
	 * Print the contents of the given Taxonomy<ElkClass> to the specified Writer.
	 * Class expressions are ordered for generating the output, ensuring that
	 * the output is deterministic.
	 * 
	 * @param classTaxonomy
	 * @param writer
	 * @param addHash
	 *            if true, a hash string will be added at the end of the output
	 *            using comment syntax of OWL 2 Functional Style
	 */
	public static void dumpClassTaxomomy(Taxonomy<ElkClass> classTaxonomy,
			Writer writer, boolean addHash) throws IOException {
		processClassTaxomomy(classTaxonomy, writer);
		if (addHash) {
			writer.write("\n\n# Hash code: " + getHashString(classTaxonomy)
					+ "\n");
		}
	}

	/**
	 * Get a has string for the given Taxonomy<ElkClass>. Besides possible hash
	 * collisions (which have very low probability) the hash string is the same
	 * for two inputs if and only if the inputs describe the same taxonomy. So
	 * it can be used to compare classification results.
	 * 
	 * @param classTaxonomy
	 * @return hash string
	 */
	public static String getHashString(Taxonomy<ElkClass> classTaxonomy) {
		return Integer.toHexString(ClassTaxonomyHasher.hash(classTaxonomy));
	}

	/**
	 * Process a taxonomy and write a normalized serialization.
	 * 
	 * @param classTaxonomy
	 * @param writer
	 * @throws IOException
	 */
	protected static void processClassTaxomomy(Taxonomy<ElkClass> classTaxonomy,
			Writer writer) throws IOException {

		writer.write("Ontology(\n");

		TreeSet<ElkClass> canonicalElkClasses = new TreeSet<ElkClass>(
				comparator);
		for (TaxonomyNode<ElkClass> classNode : classTaxonomy.getNodes())
			canonicalElkClasses.add(classNode.getCanonicalMember());

		for (ElkClass elkClass : canonicalElkClasses) {
			TaxonomyNode<ElkClass> classNode = classTaxonomy.getNode(elkClass);

			ArrayList<ElkClass> orderedEquivalentClasses = new ArrayList<ElkClass>(
					classNode.getMembers());
			Collections.sort(orderedEquivalentClasses, comparator);

			TreeSet<ElkClass> orderedSubClasses = new TreeSet<ElkClass>(
					comparator);
			for (TaxonomyNode<ElkClass> childNode : classNode.getDirectSubNodes()) {
				orderedSubClasses.add(childNode.getCanonicalMember());
			}

			processClassAxioms(elkClass, orderedEquivalentClasses,
					orderedSubClasses, writer);
		}

		writer.write(")");
	}

	/**
	 * Process axioms related to one ElkClass, where the relevant related
	 * classes are given in two ordered collections of equivalent classes and
	 * subclasses, respectively. The method serializes the axioms to the Writer.
	 * 
	 * @param elkClass
	 * @param orderedEquivalentClasses
	 * @param orderedSubClasses
	 * @param writer
	 * @throws IOException
	 */
	protected static void processClassAxioms(ElkClass elkClass,
			ArrayList<ElkClass> orderedEquivalentClasses,
			TreeSet<ElkClass> orderedSubClasses, Writer writer)
			throws IOException {

		ElkObjectFactory objectFactory = new ElkObjectFactoryImpl();

		if (orderedEquivalentClasses.size() > 1) {
			ElkEquivalentClassesAxiom elkEquivalentClassesAxiom = objectFactory
					.getEquivalentClassesAxiom(orderedEquivalentClasses);
			OwlFunctionalStylePrinter.append(writer, elkEquivalentClassesAxiom);
			writer.append('\n');
		}

		if (elkClass.getIri() != PredefinedElkIri.OWL_THING)
			for (ElkClass elkSubClass : orderedSubClasses)
				if (elkSubClass.getIri() != PredefinedElkIri.OWL_NOTHING) {
					ElkSubClassOfAxiom elkSubClassAxiom = objectFactory
							.getSubClassOfAxiom(elkSubClass, elkClass);
					OwlFunctionalStylePrinter.append(writer, elkSubClassAxiom);
					writer.append('\n');
				}
	}
}