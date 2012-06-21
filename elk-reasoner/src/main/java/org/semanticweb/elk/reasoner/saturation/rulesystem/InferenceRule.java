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
package org.semanticweb.elk.reasoner.saturation.rulesystem;

/**
 * Abstract base class for inference rules. An inference rule for a
 * saturation-based inference system is defined by implementing methods that
 * apply this rule in various situations.</p>
 * 
 * <p>
 * Derived information is stored in Contexts, and different inference systems
 * will require different Context implementation to store the information that
 * they work on. The generic type parameter C is used for this, and
 * implementations of inference rules can require C to be more specific
 * according to their needs.
 * </p>
 * 
 * <p>
 * The application of inference rules further needs a
 * {@link RuleApplicationFactory} that manages all contexts and that organizes
 * the invocation of rules. The results of inference rules are forwarded to the
 * RuleApplicationManager, which will schedule their further processing
 * (possibly triggering new rules).
 * </p>
 * 
 * <p>
 * Inference rules are implemented in two kinds of methods with the following
 * signatures that are defined by the InferenceSystemInvocationManager:
 * <ul>
 * <li>public void init(C, RuleApplicationManager)</li>
 * <li>public void apply(Queueuable<C>, C, RuleApplicationManager)</li>
 * </ul>
 * The init method represents rules that do not require any premises to be
 * derived first (but that may check side conditions). These rules are essential
 * to start a saturation process (if all rules would require a previously
 * computed input, then no rule would apply first). There can only be one init
 * method per inference rule since its signature is fixed.
 * </p>
 * 
 * <p>
 * The apply methods implement rules that require one or more premises to be
 * applicable. Such premises are represented by Queueable objects (that have
 * been derived and queued earlier). Whenever such a queued derivation is
 * processed, all rules that apply to this type of Queueable will be invoked to
 * check if they can use this new information to derive something. It is usually
 * not determined in which order premises are derived, hence a rule that
 * requires multiple kinds of premises will need to implement multiple apply
 * methods for each.
 * </p>
 * 
 * <p>
 * All methods that are called "init" or "apply" must have a compatible
 * signature; otherwise rule registration will fail with an exception. No method
 * of either kind is required (but a rule without any method to apply it is
 * meaningless for inferencing).
 * </p>
 * 
 * <p>
 * Inference rules are grouped in an {@link InferenceSystem}.
 * 
 * @author Markus Kroetzsch
 * 
 * @param <C>
 */
public interface InferenceRule<C extends Context> {
}