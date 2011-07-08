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
package org.semanticweb.elk.reasoner.classification;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.HashMap;
import java.util.SortedMap;
import java.util.TreeMap;

import org.semanticweb.elk.syntax.ElkClass;
import org.semanticweb.elk.syntax.ElkEquivalentClassesAxiom;
import org.semanticweb.elk.syntax.ElkSubClassOfAxiom;

/**
 * Class of static helper functions for printing and hashing a taxonomy. It is
 * primarily intended to be used for controlling the output of classification.
 * 
 * @author Markus Kroetzsch
 */
public class ClassTaxonomyPrinter {

	/**
	 * Convenience method for printing a ClassTaxonomy to a file at the given
	 * location.
	 * 
	 * @see org.semanticweb.elk.reasoner.classification.ClassTaxonomyPrinter#dumpClassTaxomomy
	 * 
	 * @param classTaxonomy
	 * @param fileName
	 * @param addHash
	 *            if true, a hash string will be added at the end of the output
	 *            using comment syntax of OWL 2 Functional Style
	 */
	public static void dumpClassTaxomomyToFile(ClassTaxonomy classTaxonomy,
			String fileName, boolean addHash) throws IOException {
		FileWriter fstream = new FileWriter(fileName);
		BufferedWriter writer = new BufferedWriter(fstream);
		dumpClassTaxomomy(classTaxonomy, writer, addHash);
		writer.close();
	}

	/**
	 * Print the contents of the given ClassTaxonomy to the specified Writer.
	 * Class expressions are ordered for generating the output, ensuring that
	 * the output is deterministic.
	 * 
	 * @param classTaxonomy
	 * @param writer
	 * @param addHash
	 *            if true, a hash string will be added at the end of the output
	 *            using comment syntax of OWL 2 Functional Style
	 */
	public static void dumpClassTaxomomy(ClassTaxonomy classTaxonomy,
			Writer writer, boolean addHash) throws IOException {
		processClassTaxomomy(classTaxonomy, writer);
		if (addHash) {
			writer.write("\n\n# Hash code: " + getHashString(classTaxonomy)
					+ "\n");
		}
	}

	/**
	 * Get a has string for the given ClassTaxonomy. Besides possible hash
	 * collisions (which have very low probability) the hash string is the same
	 * for two inputs if and only if the inputs describe the same taxonomy. So
	 * it can be used to compare classification results.
	 * 
	 * @param classTaxonomy
	 * @return hash string
	 */
	public static String getHashString(ClassTaxonomy classTaxonomy) {
		return Integer.toHexString(classTaxonomy.structuralHashCode());
	}

	/**
	 * Process a taxonomy and write a normalized serialization.
	 * 
	 * @param classTaxonomy
	 * @param writer
	 * @throws IOException
	 */
	protected static void processClassTaxomomy(ClassTaxonomy classTaxonomy,
			Writer writer) throws IOException {
		TreeMap<String, ElkClass> orderedElkClasses = new TreeMap<String, ElkClass>();
		HashMap<ElkClass, ElkClass> firstElkClasses = new HashMap<ElkClass, ElkClass>();
		TreeMap<String, ElkClass> orderedEquivalentClasses = new TreeMap<String, ElkClass>();
		for (ClassNode classNode : classTaxonomy.getNodes()) {
			orderedEquivalentClasses.clear();
			for (ElkClass elkClass : classNode.getMembers()) {
				orderedEquivalentClasses.put(elkClass.toString(), elkClass);
			}

			ElkClass firstClass = orderedEquivalentClasses.firstEntry()
					.getValue();
			firstElkClasses.put(classNode.getCanonicalMember(), firstClass);
			orderedElkClasses.put(firstClass.toString(), firstClass);
		}

		TreeMap<String, ElkClass> orderedSubClasses = new TreeMap<String, ElkClass>();
		for (ElkClass elkClass : orderedElkClasses.values()) {
			orderedEquivalentClasses.clear();
			orderedSubClasses.clear();
			ClassNode classNode = classTaxonomy.getNode(elkClass);

			for (ElkClass elkClassMember : classNode.getMembers()) {
				if (!elkClassMember.structuralEquals(elkClass)) {
					orderedEquivalentClasses.put(elkClassMember.toString(),
							elkClassMember);
				}
			}
			for (ClassNode classNodeChild : classNode.getDirectSubNodes()) {
				ElkClass firstClass = firstElkClasses.get(classNodeChild
						.getCanonicalMember());
				orderedSubClasses.put(firstClass.toString(), firstClass);
			}

			processClassAxioms(elkClass, orderedEquivalentClasses,
					orderedSubClasses, writer);
		}

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
			SortedMap<String, ElkClass> orderedEquivalentClasses,
			SortedMap<String, ElkClass> orderedSubClasses, Writer writer)
			throws IOException {
		for (ElkClass elkClassMember : orderedEquivalentClasses.values()) {
			ElkEquivalentClassesAxiom elkEquivalentClassesAxiom = ElkEquivalentClassesAxiom
					.create(elkClass, elkClassMember);
			writer.write(elkEquivalentClassesAxiom.toString() + "\n");
		}
		for (ElkClass elkSubClass : orderedSubClasses.values()) {
			ElkSubClassOfAxiom elkSubClassAxiom = ElkSubClassOfAxiom.create(
					elkSubClass, elkClass);
			writer.write(elkSubClassAxiom.toString() + "\n");
		}
	}

}
