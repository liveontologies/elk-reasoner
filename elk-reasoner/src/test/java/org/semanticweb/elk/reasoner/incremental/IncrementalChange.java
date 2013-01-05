package org.semanticweb.elk.reasoner.incremental;
/*
 * #%L
 * ELK Reasoner
 * $Id:$
 * $HeadURL:$
 * %%
 * Copyright (C) 2011 - 2013 Department of Computer Science, University of Oxford
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
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.semanticweb.elk.owl.interfaces.ElkAxiom;
import org.semanticweb.elk.owl.printers.OwlFunctionalStylePrinter;

/**
 * An axiom change represented by (possibly overlapping) list of additions and
 * deletions
 * 
 * @author "Yevgeny Kazakov"
 * 
 */
public class IncrementalChange {

	private final List<ElkAxiom> additions_;
	private final List<ElkAxiom> deletions_;

	public IncrementalChange(List<ElkAxiom> additions, List<ElkAxiom> deletions) {
		this.additions_ = additions;
		this.deletions_ = deletions;
	}

	public IncrementalChange() {
		this(new ArrayList<ElkAxiom>(), new ArrayList<ElkAxiom>());
	}

	public void clear() {
		additions_.clear();
		deletions_.clear();
	}

	public List<ElkAxiom> getAdditions() {
		return Collections.unmodifiableList(this.additions_);
	}

	public List<ElkAxiom> getDeletions() {
		return Collections.unmodifiableList(this.deletions_);
	}

	public void registerAddition(ElkAxiom axiom) {
		this.additions_.add(axiom);
	}

	public void registerDeletion(ElkAxiom axiom) {
		this.deletions_.add(axiom);
	}

	// TODO: move to OwlFunctionalStylePrinter
	static void dumpAxiomsToFile(Iterable<ElkAxiom> axioms, String fileName)
			throws IOException {
		FileWriter fstream = new FileWriter(fileName);
		BufferedWriter writer = new BufferedWriter(fstream);
		writer.append("Ontology(\n");
		for (ElkAxiom axiom : axioms) {
			OwlFunctionalStylePrinter.append(writer, axiom, true);
			writer.append("\n");
		}
		writer.append(")\n");
		writer.close();
	}

	public void dumpAdditionsToFile(String fileName) throws IOException {
		dumpAxiomsToFile(additions_, fileName);
	}

	public void dumpDeletionsToFile(String fileName) throws IOException {
		dumpAxiomsToFile(deletions_, fileName);
	}

}
