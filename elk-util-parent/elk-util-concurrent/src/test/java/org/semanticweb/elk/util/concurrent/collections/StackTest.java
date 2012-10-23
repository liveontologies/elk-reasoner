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

import java.util.Queue;
import java.util.Random;
import java.util.concurrent.ConcurrentLinkedQueue;

import org.semanticweb.elk.util.concurrent.collections.Stack;

import junit.framework.TestCase;

public class StackTest extends TestCase {

	private static final int STACKS_COUNT_ = 100;
	private static final int WORKER_COUNT_ = 10;
	private static final int ELEMENTS_COUNT_ = 10000;
	private static final int ITERATIONS_ = 10000;

	@SuppressWarnings("unchecked")
	final StackMonitor<Integer> stacks[] = new StackMonitor[STACKS_COUNT_];
	final Queue<StackMonitor<Integer>> activeStacks = new ConcurrentLinkedQueue<StackMonitor<Integer>>();
	final Random generator = new Random(123);

	public StackTest(String testName) {
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

	public void enqueue(int element) {
		StackMonitor<Integer> monitor = stacks[generator.nextInt(STACKS_COUNT_)];
		if (monitor.stack.add(element))
			activeStacks.add(monitor);
	}

	public void testStack() {
		prepare();

		// we use one instance of the worker
		Worker worker = new Worker();
		for (int i = 0; i < WORKER_COUNT_; i++) {
			(new Thread(worker)).start();
		}
	}

	private class Worker implements Runnable {

		@Override
		public void run() {
			for (int i = 0; i < ITERATIONS_; i++) {
				StackMonitor<Integer> monitor = activeStacks.poll();
				if (monitor == null)
					break;				
				for (Integer element : monitor.stack) {
					monitor.lock();
					enqueue(element);
					monitor.unlock();
				}					
			}

		}
	}

	private static class StackMonitor<T> {
		final Stack<T> stack;
		private volatile boolean accessed = false;

		public StackMonitor() {
			this.stack = new Stack<T>();
		}

		public void lock() {
			if (accessed)
				fail("The stack is aready being accessed");
			accessed = true;
		}

		public void unlock() {
			accessed = false;
		}
	}

}
