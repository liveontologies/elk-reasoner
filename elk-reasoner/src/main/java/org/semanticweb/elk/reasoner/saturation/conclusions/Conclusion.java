package org.semanticweb.elk.reasoner.saturation.conclusions;

import org.semanticweb.elk.reasoner.saturation.context.Context;
import org.semanticweb.elk.reasoner.saturation.rules.Rule;

/*
 * #%L
 * ELK Reasoner
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

/**
 * A general type of conclusions, produced by inference rules. This is the main
 * type of information that is exchanged between {@link Context}s. When a
 * {@link Conclusion} has been derived for a particular {@link Context}, it
 * should be processed within this context, therefore every {@link Conclusion}
 * should implement the {@link Rule} interface for argument {@link Context}.
 * 
 * @author "Yevgeny Kazakov"
 * 
 */
public interface Conclusion {//extends ReversibleRule<Context> {

	//public void apply(SaturationState.Writer writer, Context element);
	
	//public void deapply(SaturationState.Writer writer, Context element);
	
	public <R> R accept(ConclusionVisitor<R> visitor, Context context);
	
	public Context getSourceContext(Context contextWhereStored);
}
