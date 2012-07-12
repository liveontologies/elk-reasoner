/*
 * #%L
 * ELK Reasoner Protege Plug-in
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
/**
 * 
 */
package org.semanticweb.elk.protege;

import org.apache.log4j.Logger;
import org.protege.editor.core.editorkit.plugin.EditorKitHook;

/**
 * Carries out some initialization, e.g. Log4j, which we don't want to put into
 * the reasoner factory which could be used outside Protege (for example, in
 * Snow Owl)
 * 
 * 
 * @author Pavel Klinov
 * 
 */
public class ElkProtegePluginInstance extends EditorKitHook {

	@Override
	public void dispose() throws Exception {
	}

	@Override
	public void initialise() throws Exception {
		Logger.getLogger("org.semanticweb.elk").addAppender(
				ProtegeMessageAppender.getInstance());
	}
}
