/*-
 * #%L
 * ELK Reasoner Core
 * $Id:$
 * $HeadURL:$
 * %%
 * Copyright (C) 2011 - 2016 Department of Computer Science, University of Oxford
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
package org.semanticweb.elk.reasoner;

import org.semanticweb.elk.reasoner.stages.ElkInterruptedException;
import org.semanticweb.elk.util.concurrent.computation.Interrupter;

/**
 * A simple interrupter, which uses a flag about the interrupt status.
 * 
 * @author "Yevgeny Kazakov"
 * @author Peter Skocovsky
 */
public class ReasonerInterrupter implements Interrupter {

	/**
	 * The interruption status of this interrupter.
	 */
	private volatile boolean isInterrupted_ = false;

	@Override
	public void interrupt() {
		isInterrupted_ = true;
	}

	@Override
	public boolean isInterrupted() {
		return isInterrupted_;
	}

	/**
	 * If interrupted, clears the flag and throws ElkInterruptedException
	 * 
	 * @throws ElkInterruptedException
	 *             if interrupted
	 */
	public void checkInterrupt() throws ElkInterruptedException {
		if (isInterrupted()) {
			isInterrupted_ = false;
			throw new ElkInterruptedException();
		}
	}

}
