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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * An axiom change represented by (possibly overlapping) list of additions and
 * deletions
 * 
 * @author "Yevgeny Kazakov"
 * 
 * @param <T> 
 */
public class IncrementalChange<T> {

	private final List<T> additions_;
	private final List<T> deletions_;

	public IncrementalChange(List<T> additions, List<T> deletions) {
		this.additions_ = additions;
		this.deletions_ = deletions;
	}

	public IncrementalChange() {
		this(new ArrayList<T>(), new ArrayList<T>());
	}

	public void clear() {
		additions_.clear();
		deletions_.clear();
	}

	public List<T> getAdditions() {
		return Collections.unmodifiableList(this.additions_);
	}

	public List<T> getDeletions() {
		return Collections.unmodifiableList(this.deletions_);
	}

	public void registerAddition(T axiom) {
		this.additions_.add(axiom);
	}

	public void registerDeletion(T axiom) {
		this.deletions_.add(axiom);
	}

	// TODO: move to OwlFunctionalStylePrinter
/*	static void dumpAxiomsToFile(Iterable<ElkAxiom> axioms, String fileName)
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
	}*/

}
