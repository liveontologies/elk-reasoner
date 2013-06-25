package org.semanticweb.elk.owl.parsing.javacc;
/*
 * #%L
 * ELK OWL JavaCC Parser
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

/**
 * A {@link LexerMessage} that holds tokens
 * 
 * @author "Yevgeny Kazakov"
 */
class LexerBatch extends ArrayList<Token> implements LexerMessage {
	/**
	 * 
	 */
	private static final long serialVersionUID = -1438353325200022815L;

	public LexerBatch(int length) {
		super(length);
	}

	@Override
	public void accept(LexerMessageVisitor visitor) {
		visitor.visit(this);
	}
}