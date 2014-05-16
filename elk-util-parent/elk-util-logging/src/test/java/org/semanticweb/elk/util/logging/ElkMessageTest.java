/**
 * 
 */
package org.semanticweb.elk.util.logging;
/*
 * #%L
 * ELK Utilities for Logging
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

import static org.junit.Assert.*;

import org.junit.Test;

/**
 * @author Pavel Klinov
 *
 * pavel.klinov@uni-ulm.de
 */
public class ElkMessageTest {

	@Test
	public void testSerialize() {
		String type = "elk.message";
		String body = "message body";
		String message = "[" + type + "]" + body;
		ElkMessage msg = ElkMessage.deserialize(message);
		
		assertNotNull(msg);
		assertEquals(type, msg.getMessageType());
		assertEquals(body, msg.getMessage());
		
		String serialized = ElkMessage.serialize(type, body);
		
		assertEquals(message, serialized);
		
		String err1 = "[elk.message Message body";
		
		assertNull(ElkMessage.deserialize(err1));
		
		String err2 = "elk.message] Message body";
		
		assertNull(ElkMessage.deserialize(err2));
	}

}
