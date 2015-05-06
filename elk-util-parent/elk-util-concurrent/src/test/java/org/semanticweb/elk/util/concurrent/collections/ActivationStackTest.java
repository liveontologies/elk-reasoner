package org.semanticweb.elk.util.concurrent.collections;

/*
 * #%L
 * ELK Utilities for Concurrency
 * $Id:$
 * $HeadURL:$
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

import java.util.Random;

import junit.framework.TestCase;

public class ActivationStackTest extends TestCase {

	/**
	 * the number of stack used in the test
	 */
	private static final int STACKS_COUNT_ = 1000;
	/**
	 * the number of worker threads used in the test
	 */
	private static final int WORKER_COUNT_ = 10;
	/**
	 * the total number of elements stored in the test
	 */
	private static final int ELEMENTS_COUNT_ = 1000000;
	/**
	 * the number of stack iterations for every worker
	 */
	private static final int ITERATIONS_ = 10000;
	/**
	 * we create an array of stacks that can store integer values
	 */
	@SuppressWarnings("unchecked")
	final StackMonitor<Integer> stacks[] = new StackMonitor[STACKS_COUNT_];
	/**
	 * the stack to store all non-empty stacks
	 */
	final ActivationStack<StackMonitor<Integer>> nonEmptyStacks = new ConcurrentLinkedActivationStack<StackMonitor<Integer>>();
	/**
	 * this will be used to determine in which stack to insert elements
	 */
	final Random generator = new Random(123);

	public ActivationStackTest(String testName) {
		super(testName);
	}

	public void prepare() {
		// creating stack monitors
		for (int i = 0; i < STACKS_COUNT_; i++) {
			stacks[i] = new StackMonitor<Integer>();
		}
		// generating elements
		for (int i = 0; i < ELEMENTS_COUNT_; i++) {
			enqueue(i);
		}
	}

	/**
	 * The given element will be inserted into the random stack.
	 * 
	 * @param element
	 */
	public void enqueue(int element) {
		StackMonitor<Integer> monitor = stacks[generator.nextInt(STACKS_COUNT_)];
		if (monitor.stack.push(element)) {
			/*
			 * the element is the first element in the stack and therefore the
			 * stack should be added to the nonEmptyStacks. No worker should
			 * access the stack at this time.
			 */
			monitor.checkActive();
			nonEmptyStacks.push(monitor);
		}
	}

	public void testStack() {
		prepare();

		// we use one instance of the worker
		Worker worker = new Worker();
		for (int i = 0; i < WORKER_COUNT_; i++) {
			(new Thread(worker)).start();
		}
	}

	/**
	 * Each worker is repeatedly takes the next stack with unprocessed values
	 * and distribute these values randomly over stacks until it becomes empty.
	 * Only one worker should be working with each stack, therefore, when
	 * working with elements the stack we lock the respective monitor of the
	 * stack. If everything works correctly, no double locking should occur.
	 * 
	 * @author "Yevgeny Kazakov"
	 * 
	 */
	private class Worker implements Runnable {

		@Override
		public void run() {
			for (int i = 0; i < ITERATIONS_; i++) {
				StackMonitor<Integer> monitor = nonEmptyStacks.pop();
				if (monitor == null)
					break;
				for (;;) {
					Integer element = monitor.stack.pop();
					if (element == null)
						break;
					monitor.lock();
					enqueue(element);
					monitor.unlock();
				}
			}
		}
	}

	/**
	 * A wrapper around {@link ConcurrentLinkedActivationStack} which allows to
	 * monitor access to the stack using locks (boolean values)
	 * 
	 * @author "Yevgeny Kazakov"
	 * 
	 * @param <T>
	 *            the type of elements in the stack
	 */
	private static class StackMonitor<T> {
		final ConcurrentLinkedActivationStack<T> stack;
		/**
		 * {@code true} if this monitor is locked by a worker
		 */
		private volatile boolean islocked_ = false;

		public StackMonitor() {
			this.stack = new ConcurrentLinkedActivationStack<T>();
		}

		public void lock() {
			checkActive();
			islocked_ = true;
		}

		public void unlock() {
			islocked_ = false;
		}

		public void checkActive() {
			if (islocked_)
				fail("The stack is being accessed from two workers");
		}
	}

}
