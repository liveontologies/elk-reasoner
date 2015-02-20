package org.semanticweb.elk.explanations.editing;
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

import java.awt.BorderLayout;
import java.awt.Dimension;

import javax.swing.JComponent;
import javax.swing.JPanel;

import org.protege.editor.core.ui.util.InputVerificationStatusChangedListener;
import org.protege.editor.core.ui.util.VerifiedInputEditor;
import org.protege.editor.owl.OWLEditorKit;
import org.protege.editor.owl.ui.clsdescriptioneditor.ExpressionEditor;
import org.protege.editor.owl.ui.editor.AbstractOWLObjectEditor;
import org.semanticweb.owlapi.model.OWLClassAxiom;
import org.semanticweb.owlapi.model.OWLException;


/**
 * Editor for editing all kinds of axioms which may appear in the proofs.
 * 
 * Pavel Klinov
 */
public class AxiomExpressionEditor extends AbstractOWLObjectEditor<OWLClassAxiom> implements VerifiedInputEditor {

    private final OWLEditorKit editorKit;

    private final ExpressionEditor<OWLClassAxiom> editor;

    private final JComponent editingComponent;


    public AxiomExpressionEditor(OWLEditorKit editorKit) {
        this.editorKit = editorKit;

        editor = new ExpressionEditor<OWLClassAxiom>(editorKit, editorKit.getOWLModelManager().getOWLExpressionCheckerFactory().getClassAxiomChecker());

        editingComponent = new JPanel(new BorderLayout());
        editingComponent.add(editor);
        editingComponent.setPreferredSize(new Dimension(400, 200));
    }


    public boolean setEditedObject(OWLClassAxiom axiom) {
        if (axiom == null){
            editor.setText("");
        }
        else{
            editor.setText(editorKit.getModelManager().getRendering(axiom));
        }
        return true;
    }


    public JComponent getInlineEditorComponent() {
        // Same as general editor component
        return editingComponent;
    }


    public String getEditorTypeName() {
        return "Axiom Editor";
    }


    public boolean canEdit(Object object) {
        return object instanceof OWLClassAxiom;
    }


    /**
     * Gets a component that will be used to edit the specified
     * object.
     * @return The component that will be used to edit the object
     */
    public JComponent getEditorComponent() {
        return editingComponent;
    }


    /**
     * Gets the object that has been edited.
     * @return The edited object
     */
    public OWLClassAxiom getEditedObject() {
        try {
            if (editor.isWellFormed()) {
                return editor.createObject();
            }
            else {
                return null;
            }
        }
        catch (OWLException e) {
            return null;
        }
    }


    public void dispose() {
    }


    public void addStatusChangedListener(InputVerificationStatusChangedListener listener) {
        editor.addStatusChangedListener(listener);
    }


    public void removeStatusChangedListener(InputVerificationStatusChangedListener listener) {
        editor.removeStatusChangedListener(listener);
    }
    
    
}
