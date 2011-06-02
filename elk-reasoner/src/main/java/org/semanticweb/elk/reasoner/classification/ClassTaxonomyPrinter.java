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
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
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
		if (addHash) {
			String hash = processClassTaxomomy(classTaxonomy, writer,
					getMessageDigest());
			writer.write("\n\n# Hash code: " + hash + "\n");
		} else {
			processClassTaxomomy(classTaxonomy, writer, null);
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
		try {
			return processClassTaxomomy(classTaxonomy, null, getMessageDigest());
		} catch (IOException e) {
			assert (false);
			return "";
		}
	}

	/**
	 * Process a taxonomy and write a normalized serialization or build a hash
	 * string, depending on the given parameters.
	 * 
	 * @param classTaxonomy
	 * @param writer
	 * @param messageDigest
	 * @return
	 * @throws IOException
	 */
	protected static String processClassTaxomomy(ClassTaxonomy classTaxonomy,
			Writer writer, MessageDigest messageDigest) throws IOException {
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
			for (ClassNode classNodeChild : classNode.getChildren()) {
				ElkClass firstClass = firstElkClasses.get(classNodeChild
						.getCanonicalMember());
				orderedSubClasses.put(firstClass.toString(), firstClass);
			}

			processClassAxioms(elkClass, orderedEquivalentClasses,
					orderedSubClasses, writer, messageDigest);
		}

		if (messageDigest != null) {
			return getHex(messageDigest.digest());
		} else {
			return "";
		}
	}

	/**
	 * Process axioms related to one ElkClass, where the relevant related
	 * classes are given in two ordered collections of equivalent classes and
	 * subclasses, respectively. The method either serializes the axioms to the
	 * Writer, or adds the serialization to the MessageDigest, or both,
	 * depending on which if these objects are not null.
	 * 
	 * @param elkClass
	 * @param orderedEquivalentClasses
	 * @param orderedSubClasses
	 * @param writer
	 * @param messageDigest
	 * @throws IOException
	 */
	protected static void processClassAxioms(ElkClass elkClass,
			SortedMap<String, ElkClass> orderedEquivalentClasses,
			SortedMap<String, ElkClass> orderedSubClasses, Writer writer,
			MessageDigest messageDigest) throws IOException {
		for (ElkClass elkClassMember : orderedEquivalentClasses.values()) {
			ElkEquivalentClassesAxiom elkEquivalentClassesAxiom = ElkEquivalentClassesAxiom
					.create(elkClass, elkClassMember);
			writeToWriterOrDigest(elkEquivalentClassesAxiom.toString() + "\n",
					writer, messageDigest);
		}
		for (ElkClass elkSubClass : orderedSubClasses.values()) {
			ElkSubClassOfAxiom elkSubClassAxiom = ElkSubClassOfAxiom.create(
					elkSubClass, elkClass);
			writeToWriterOrDigest(elkSubClassAxiom.toString() + "\n", writer,
					messageDigest);
		}
	}

	/**
	 * Write a string to the given Writer and/or add it to the given
	 * MessageDigest, provided that the respective objects are not null.
	 * 
	 * @param string
	 * @param writer
	 * @param messageDigest
	 * @throws IOException
	 */
	protected static void writeToWriterOrDigest(String string, Writer writer,
			MessageDigest messageDigest) throws IOException {
		if (writer != null) {
			writer.write(string);
		}
		if (messageDigest != null) {
			messageDigest.update(string.getBytes());
		}
	}

	/**
	 * Get a MessageDigest with the standard hashing algorithm we use.
	 * 
	 * @return
	 */
	protected static MessageDigest getMessageDigest() {
		try {
			return MessageDigest.getInstance("MD5");
		} catch (NoSuchAlgorithmException e) {
			assert (false);
			return null;
		}
	}

	protected static final String HEXES = "0123456789ABCDEF";

	/**
	 * Convert a byte array to a string that shows its entries in Hex format.
	 * Based on code from http://www.rgagnon.com/javadetails/java-0596.html.
	 * 
	 * @param raw
	 * @return
	 */
	protected static String getHex(byte[] raw) {
		if (raw == null) {
			return null;
		}
		final StringBuilder hex = new StringBuilder(2 * raw.length);
		for (final byte b : raw) {
			hex.append(HEXES.charAt((b & 0xF0) >> 4)).append(
					HEXES.charAt((b & 0x0F)));
		}
		return hex.toString();
	}
}
