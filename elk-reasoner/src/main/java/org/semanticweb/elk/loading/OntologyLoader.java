/*
 * #%L
 * ELK Reasoner
 * 
 * $Id$
 * $HeadURL$
 * %%
 * Copyright (C) 2011 - 2012 Department of Computer Science, University of Oxford
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
package org.semanticweb.elk.loading;

import java.util.Queue;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

import org.semanticweb.elk.owl.interfaces.ElkAxiom;
import org.semanticweb.elk.owl.parsing.Owl2ParseException;
import org.semanticweb.elk.owl.parsing.Owl2Parser;
import org.semanticweb.elk.owl.visitors.ElkOntologyVisitor;

/**
 * An {@link IncrementalOntologyProvider} that loads an ontology using an
 * {@link Owl2Parser}.
 * 
 * @author "Yevgeny Kazakov"
 * 
 */
public class OntologyLoader implements IncrementalOntologyProvider {

	/**
	 * the parser used to load the ontology
	 */
	private final Owl2Parser parser;
	/**
	 * a bounded buffer into which the axioms are loaded; if the puffer is full
	 * the parser will block until axioms are taken
	 */
	private final BlockingQueue<ElkAxiom> axiomBuffer;
	/**
	 * the thread in which the parser is running
	 */
	private final Thread parserThread;
	/**
	 * the thread from which axioms are taken
	 */
	private volatile Thread controlThread;
	/**
	 * <tt>true</tt> if the parser thread has started
	 */
	private volatile boolean started;
	/**
	 * <tt>true</tt> if the parser has finished processing the ontology
	 */
	private volatile boolean finished;
	/**
	 * <tt>true</tt> if axioms are taken from the buffer and this operation can
	 * block
	 */
	private volatile boolean taking;
	/**
	 * the exception created if something goes wrong
	 */
	protected volatile LoadingException exception;

	/**
	 * Creating an {@link OntologyLoader} for a given {@link Owl2Parser} to load
	 * axioms and the given limit on the size of the buffer to hold loaded
	 * axioms
	 * 
	 * @param owlParser
	 *            the parser used to load the ontology
	 * @param bufferSize
	 *            the size of the bounded buffer for loaded axioms
	 */
	public OntologyLoader(Owl2Parser owlParser, int bufferSize) {
		this.parser = owlParser;
		this.axiomBuffer = new ArrayBlockingQueue<ElkAxiom>(bufferSize);
		this.finished = false;
		this.parserThread = new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					parser.accept(new AxiomInserter(axiomBuffer));
				} catch (Owl2ParseException e) {
					exception = new LoadingException(e.toString());
				}
				finished = true;
				if (taking)
					controlThread.interrupt();
			}

		});
		this.started = false;
	}

	/**
	 * Creating an {@link OntologyLoader} for a given {@link Owl2Parser} to load
	 * axioms
	 * 
	 * @param owlParser
	 *            the parser used to load the ontology
	 */
	public OntologyLoader(Owl2Parser owlParser) {
		this(owlParser, 256);
	}

	@Override
	public synchronized void accept(ElkOntologyVisitor ontologyVisitor)
			throws LoadingException {
		controlThread = Thread.currentThread();
		if (!started) {
			parserThread.start();
			started = true;
		}
		ElkAxiom axiom = null;
		for (;;) {
			if (finished) {
				axiom = axiomBuffer.poll();
			} else {
				taking = true;
				try {
					axiom = axiomBuffer.take();
				} catch (InterruptedException e) {
					if (exception != null)
						throw exception;
					else if (!finished) {
						// restore the interrupt status
						Thread.currentThread().interrupt();
						break;
					}
				} finally {
					taking = false;
					if (finished)
						// clear the interrupt status
						Thread.interrupted();
				}
			}
			if (axiom == null)
				break;
			ontologyVisitor.visit(axiom);
		}
		finish();
	}

	@Override
	public void accept(ElkOntologyVisitor adder, ElkOntologyVisitor remover)
			throws LoadingException {
		// nothing to do so far

	}

	/**
	 * the post-processing hook to run methods after a parser has been used;
	 * e.g., to free any resources used by the parser
	 */
	protected void finish() {

	}

	/**
	 * A simple {@link ElkOntologyVisitor} that insert the parsed axioms into
	 * the given queue
	 * 
	 * @author "Yevgeny Kazakov"
	 * 
	 */
	private static class AxiomInserter implements ElkOntologyVisitor {

		final Queue<ElkAxiom> axiomBuffer;

		AxiomInserter(Queue<ElkAxiom> axiomBuffer) {
			this.axiomBuffer = axiomBuffer;
		}

		@Override
		public void visit(ElkAxiom elkAxiom) {
			axiomBuffer.add(elkAxiom);
		}
	}

}
