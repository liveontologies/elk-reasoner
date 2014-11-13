package org.semanticweb.elk.explanations;
/*
 * #%L
 * Explanation Workbench
 * $Id:$
 * $HeadURL:$
 * %%
 * Copyright (C) 2011 - 2014 Department of Computer Science, University of Oxford
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.semanticweb.owl.explanation.api.Explanation;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.OWLAxiom;

import uk.ac.manchester.cs.bhig.util.Tree;
import uk.ac.manchester.cs.owl.explanation.ordering.ExplanationOrderer;
import uk.ac.manchester.cs.owl.explanation.ordering.ExplanationOrdererImpl;
import uk.ac.manchester.cs.owl.explanation.ordering.ExplanationTree;
/*
 * Copyright (C) 2008, University of Manchester
 *
 * Modifications to the initial code base are copyright of their
 * respective authors, or their employers as appropriate.  Authorship
 * of the modifications may be determined from the ChangeLog placed at
 * the end of this file.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.

 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.

 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA
 */


/**
 * Author: Matthew Horridge<br> The University Of Manchester<br> Information Management Group<br> Date:
 * 23-Oct-2008<br><br>
 */
public class JustificationFormattingManager {

    static private JustificationFormattingManager manager;

    private Map<Explanation, Map<OWLAxiom, Integer>> indentMap;

    private Map<Explanation, List<OWLAxiom>> orderingMap;

    private JustificationFormattingManager() {
        indentMap = new HashMap<Explanation, Map<OWLAxiom, Integer>>();
        orderingMap = new HashMap<Explanation, List<OWLAxiom>>();
    }

    public static synchronized JustificationFormattingManager getManager() {
        if(manager == null) {
            manager = new JustificationFormattingManager();
        }
        return manager;
    }

    private void init(Explanation explanation) {
    	ExplanationOrderer orderer = new ExplanationOrdererImpl(OWLManager.createOWLOntologyManager());
        ExplanationTree tree = orderer.getOrderedExplanation((OWLAxiom) explanation.getEntailment(), explanation.getAxioms());
        List<OWLAxiom> ordering = new ArrayList<OWLAxiom>();
        Map<OWLAxiom, Integer> im = new HashMap<OWLAxiom, Integer>();
        fill(tree, ordering, im);
        indentMap.put(explanation, im);
        orderingMap.put(explanation, ordering);

    }

    private static void fill(Tree<OWLAxiom> tree, List<OWLAxiom> ordering, Map<OWLAxiom, Integer> indentMap) {
        if (!tree.isRoot()) {
            ordering.add(tree.getUserObject());
            indentMap.put(tree.getUserObject(), tree.getPathToRoot().size() - 2);
        }
        for(Tree<OWLAxiom> child : tree.getChildren()) {
            fill(child, ordering, indentMap);
        }
    }

    private void initIfNecessary(Explanation explanation) {
        if(!indentMap.containsKey(explanation)) {
            init(explanation);
        }
    }

    public int getIndentation(Explanation explanation, OWLAxiom axiom) {
        if(!explanation.getAxioms().contains(axiom) && !explanation.getEntailment().equals(axiom)) {
            throw new IllegalArgumentException("The explanation does not contain the specified axiom: " + axiom + "  " + explanation);
        }
        initIfNecessary(explanation);
        Integer i = indentMap.get(explanation).get(axiom);
        if(i != null) {
            return i;
        }
        else {
            return 0;
        }
    }

    public void setIndentation(Explanation explanation, OWLAxiom axiom, int indentation) {
        initIfNecessary(explanation);
        indentMap.get(explanation).put(axiom, indentation);
    }

    public void increaseIndentation(Explanation explanation, OWLAxiom axiom) {
        initIfNecessary(explanation);
        Integer indent = getIndentation(explanation, axiom);
        setIndentation(explanation, axiom, indent + 1);
    }

    public void decreaseIndentation(Explanation explanation, OWLAxiom axiom) {
        initIfNecessary(explanation);
        Integer indent = getIndentation(explanation, axiom);
        indent = indent - 1;
        if(indent < 0) {
            indent = 0;
        }
        setIndentation(explanation, axiom, indent);
    }

    public int moveAxiomUp(Explanation explanation, OWLAxiom axiom) {
        initIfNecessary(explanation);
        List<OWLAxiom> ordering = orderingMap.get(explanation);
        // Lowest index is 1 - the entailment is held in position 0
        int index = ordering.indexOf(axiom);
        if(index > 0) {
            index--;
        }
        ordering.remove(axiom);
        ordering.add(index, axiom);
        return index;
    }

    public int moveAxiomDown(Explanation explanation, OWLAxiom axiom) {
        initIfNecessary(explanation);
        List<OWLAxiom> ordering = orderingMap.get(explanation);
        // Lowest index is 1 - the entailment is held in position 0
        int index = ordering.indexOf(axiom);
        if(index < ordering.size() - 1) {
            index++;
        }
        ordering.remove(axiom);
        ordering.add(index, axiom);
        return index;
    }

    public List<OWLAxiom> getOrdering(Explanation explanation) {
        initIfNecessary(explanation);
        return Collections.unmodifiableList(orderingMap.get(explanation));
    }

    public void clearFormatting(Explanation explanation) {
        indentMap.remove(explanation);
        orderingMap.remove(explanation);
    }
}
