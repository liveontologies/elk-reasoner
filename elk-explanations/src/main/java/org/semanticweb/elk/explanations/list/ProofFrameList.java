package org.semanticweb.elk.explanations.list;
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

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import javax.swing.AbstractAction;
import javax.swing.AbstractListModel;
import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.ListSelectionModel;
import javax.swing.border.Border;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import org.protege.editor.core.ui.list.MListButton;
import org.protege.editor.core.ui.list.MListDeleteButton;
import org.protege.editor.core.ui.list.MListEditButton;
import org.protege.editor.core.ui.list.MListItem;
import org.protege.editor.core.ui.util.InputVerificationStatusChangedListener;
import org.protege.editor.owl.OWLEditorKit;
import org.protege.editor.owl.ui.framelist.OWLFrameList;
import org.protege.editor.owl.ui.framelist.OWLFrameListInferredSectionRowBorder;
import org.protege.editor.owl.ui.inference.PrecomputeAction;
import org.semanticweb.elk.explanations.editing.AxiomExpressionEditor;
import org.semanticweb.elk.explanations.editing.CollapseButton;
import org.semanticweb.elk.explanations.editing.EditAndSyncAxiomPane;
import org.semanticweb.elk.explanations.editing.EditAxiomPane;
import org.semanticweb.elk.explanations.editing.ExpandButton;
import org.semanticweb.owlapi.model.AddAxiom;
import org.semanticweb.owlapi.model.AxiomType;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLClassAxiom;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyChange;
import org.semanticweb.owlapi.model.RemoveAxiom;
import org.semanticweb.owlapi.reasoner.BufferingMode;
import org.semanticweb.owlapitools.proofs.expressions.OWLExpression;
import org.semanticweb.owlapitools.proofs.util.CycleFreeProofRoot;

/**
 * 
 * @author	Pavel Klinov
 * 			pavel.klinov@uni-ulm.de
 *
 */
@SuppressWarnings("serial")
public class ProofFrameList extends OWLFrameList<CycleFreeProofRoot> {
	
	private static final Border INFERRED_BORDER = new OWLFrameListInferredSectionRowBorder();
	
	public static final Color EXPANDED_COLOR = new Color(232, 246, 219);
	
	public static final Color SELECTED_COLOR = new Color(230, 215, 246);
	
	private static final Set<AxiomType<?>> EDITABLE_AXIOM_TYPES = new HashSet<AxiomType<?>>(Arrays.<AxiomType<?>>asList(AxiomType.SUBCLASS_OF, AxiomType.OBJECT_PROPERTY_DOMAIN, AxiomType.OBJECT_PROPERTY_RANGE, AxiomType.EQUIVALENT_CLASSES));
	
	private final OWLEditorKit kit_;
	
	private final PrecomputeAction reasonerSyncAction_;
	
    public ProofFrameList(OWLEditorKit editorKit, ProofFrame proofFrame) {
        super(editorKit, proofFrame);
        
        kit_ = editorKit;
        reasonerSyncAction_ = new PrecomputeAction();
        reasonerSyncAction_.setEditorKit(editorKit);
        setCellRenderer(new ProofFrameListRenderer(editorKit));
        getSelectionModel().setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        addListSelectionListener(new ListSelectionListener() {

			@Override
			public void valueChanged(ListSelectionEvent event) {
				FlattenedListModel model = getModel();
				
				handleSelect(event.getFirstIndex(), event.getLastIndex(), model);
			}
        	
        });
    }
    
    protected void handleSelect(int first, int second, FlattenedListModel model) {
		Iterator<?> rowIter = model.iterate();
		int i = 0;
		
		while (rowIter.hasNext()) {
			Object item = rowIter.next();
			
			if (i == first || i == second) {
				if (item instanceof ProofFrameSection) {
					ProofFrameSection section = (ProofFrameSection) item;
					
					section.setSelected(isSelectedIndex(i));
				}
			}
			
			i++;
		}
		
		repaint();
	}

	@Override
	public ProofFrame getFrame() {
		return (ProofFrame) super.getFrame();
	}
    
	@Override
    protected List<MListButton> getButtons(final Object value) {
        if (value instanceof ProofFrameSectionRow) {
        	ProofFrameSectionRow row = (ProofFrameSectionRow) value;
        	
			if (row.isInferred()) {
				if (!row.isExpanded()) {
					// explain button for inferred expressions
					return Arrays.<MListButton> asList(new ExpandButton(
								new AbstractAction() {

									public void actionPerformed(ActionEvent e) {
										explainRow((ProofFrameSectionRow) value);
									}
								}));
				}
				else {
					return Arrays.<MListButton> asList(new CollapseButton(
							new AbstractAction() {

								public void actionPerformed(ActionEvent e) {
									collapseRow((ProofFrameSectionRow) value);
								}
							}));
				}
			}
			else if (row.getAxiom().isOfType(EDITABLE_AXIOM_TYPES)) {
				// asserted axiom, can edit or delete it
				return Arrays.<MListButton> asList(
						new MListEditButton(new AbstractAction() {

							public void actionPerformed(ActionEvent e) {
								editRow((ProofFrameSectionRow) value);
							}
						}),
						new MListDeleteButton(
								new AbstractAction() {

									public void actionPerformed(ActionEvent e) {
										deleteRow((ProofFrameSectionRow) value);
									}
								})
						);
			}
			else {
				// can only remove
				return Arrays.<MListButton> asList(
						new MListDeleteButton(
								new AbstractAction() {

									public void actionPerformed(ActionEvent e) {
										deleteRow((ProofFrameSectionRow) value);
									}
								})
						);
			}
        }
        else if (value == getFrame().getRootSection()) {
        	if (!getFrame().isFullyExpanded()) {
        		// add the "expand all" button
        		return Arrays.<MListButton> asList(new ExpandButton(
						new AbstractAction() {

							public void actionPerformed(ActionEvent e) {
								getFrame().fullyExpand();
								refreshComponent();
							}
						}, "Explain all inferences"));
        	}
        }

        return Collections.emptyList();
    }
    
	protected void editRow(ProofFrameSectionRow expressionRow) {
		showAxiomEditor(expressionRow);
    }
    
    protected void deleteRow(ProofFrameSectionRow expressionRow) {
    	// we'll show a confirmation window to give a chance to abort it
    	String rendering = expressionRow.getRendering();
    	String remove = "Remove";
    	String removeSync = "Remove and synchronize reasoner";
    	int result = JOptionPane.showOptionDialog (
    			this, 
    			String.format("Are you sure to remove the axiom \"%s\" from the ontology?", rendering), 
    			"Confirmation", 
    			JOptionPane.DEFAULT_OPTION,
    			JOptionPane.QUESTION_MESSAGE,
    			null,
    			new Object[] {remove, removeSync, "Cancel"},
    			removeSync);

        if (result == 0) { 
        	handleDelete();
        	getFrame().setReasonerSynchronized(false);
        	blockInferencesForPremise(expressionRow.getRoot());
        }
        else if (result == 1) {
        	handleDelete();
        	// the proof frame will refresh when synchronization finishes
        	reasonerSyncAction_.actionPerformed(null);
        }
    }
    
    private void blockInferencesForPremise(OWLExpression premise) {
    	getFrame().blockInferencesForPremise(premise);
    	refreshComponent();
    }
    
    protected void collapseRow(ProofFrameSectionRow expressionRow) {
    	expressionRow.setExpanded(false);
    	getFrame().setFullyExpanded(false);
    	refreshComponent();
    }

	protected void explainRow(ProofFrameSectionRow expressionRow) {
		expressionRow.expand(false/*do not recursively expand inferences that derive the expression*/);
		refreshComponent();
	}

	@Override
	protected Border createPaddingBorder(@SuppressWarnings("rawtypes") JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
        return BorderFactory.createMatteBorder(0, 0, 1, 0, Color.WHITE);
    }
	
	@Override
    protected Border createListItemBorder(@SuppressWarnings("rawtypes") JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
		// creating indents to reflect the proof's structure
		if (value instanceof ProofFrameSectionRow) {
			ProofFrameSectionRow row = (ProofFrameSectionRow) value;
			
			Border internalPadding = BorderFactory.createEmptyBorder(1, 1, 1, 1);
			Border line = BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(220, 220, 220));
			Border externalBorder = BorderFactory.createMatteBorder(0, 20 * row.getDepth(), 0, 0, list.getBackground());
			Border border = BorderFactory.createCompoundBorder(externalBorder, BorderFactory.createCompoundBorder(row.isInferred() ? INFERRED_BORDER : line, internalPadding));
			
			return border;
		}
		
		return super.createListItemBorder(list, value, index, isSelected, cellHasFocus);
    }

	
	@Override
	public FlattenedListModel getModel() {
		return new FlattenedListModel((ProofFrameSection) this.getFrame().getFrameSections().get(0));
	}

	@Override
	protected Color getItemBackgroundColor(MListItem item) {
        if (item instanceof ProofFrameSectionRow) {
        	ProofFrameSectionRow exprRow = (ProofFrameSectionRow) item;
        	
        	if (exprRow.isSelected()) {
        		return SELECTED_COLOR;
        	}
        	
            if (exprRow.isInferred()) {
                return exprRow.isExpanded() ? EXPANDED_COLOR : INFERRED_BG_COLOR;
            }
        }
        
        return super.getItemBackgroundColor(item);
    }
	
	// TODO cache the list of items for better performance, e.g., constant time lookup by index
	private static class FlattenedListModel extends AbstractListModel<Object> {

		private final ProofFrameSection header_;
		
		FlattenedListModel(ProofFrameSection header) {
			header_ = header;
		}
		
		@Override
		public Object getElementAt(int arg0) {
			int i = 0;
			Iterator<?> iter = iterate();
			
			while (iter.hasNext()) {
				Object item = iter.next();
				
				if (i == arg0) {
					return item;
				}
				
				i++;
			}
			
			return null;
		}

		@Override
		public int getSize() {
			int i = 0;
			Iterator<?> iter = iterate();
			
			while (iter.hasNext()) {
				iter.next();
				i++;
			}
			
			return i;
		}
		
		Iterator<?> iterate() {
			final LinkedList<Object> todo = new LinkedList<Object>();
			
			todo.add(header_);
			
			return new Iterator<Object>() {
				
				private Object next_ = null;

				@Override
				public boolean hasNext() {
					if (next_ != null) {
						return true;
					}

					next_ = todo.poll();
					
					if (next_ == null) {
						return false;
					}
					
					if (next_ instanceof ProofFrameSection) {
						ProofFrameSection section = (ProofFrameSection) next_;
						int i = 0;
						
						for (Object row : section.getRows()) {
							todo.add(i++, row);
						}
					}
					else if (next_ instanceof ProofFrameSectionRow) {
						ProofFrameSectionRow row = (ProofFrameSectionRow) next_;
						int i = 0;
						
						if (row.isExpanded()) {
							for (Object section : row.getInferenceSections()) {
								todo.add(i++, section);
							}
						}
					}
					
					return true;
				}

				@Override
				public Object next() {
					Object tmp = next_;
					
					next_ = null;
					
					return tmp;
				}

				@Override
				public void remove() {
					// no-op
				}
				
			};
		}
		
	}
	
	private boolean isBufferingMode() {
		return kit_.getOWLModelManager().getOWLReasonerManager().getCurrentReasoner().getBufferingMode() == BufferingMode.BUFFERING;
	}
	
    private void showAxiomEditor(final ProofFrameSectionRow expressionRow) {
    	final OWLAxiom axiom = expressionRow.getAxiom();
    	
    	if (axiom == null) {
    		// lemma?? either way we can edit only axioms
    		return;
    	}
    	
    	AxiomExpressionEditor editor = new AxiomExpressionEditor(kit_);
        JComponent editorComponent = editor.getEditorComponent();
		final EditAxiomPane editorPane = createEditAxiomPane(editorComponent, editor, expressionRow);
        
        final InputVerificationStatusChangedListener verificationListener = new InputVerificationStatusChangedListener() {
            public void verifiedStatusChanged(boolean verified) {
            	editorPane.setOKEnabled(verified);
            }
        };
        // Protege's syntax checkers only cover the class axiom's syntax
        editor.setEditedObject((OWLClassAxiom) axiom);
        // prevent the OK button from being available until the expression is syntactically valid
        //editor.addStatusChangedListener(verificationListener);
        
        JDialog dlg = editorPane.createEditorDialog(this);

        dlg.setVisible(true);
    }
    
    private EditAxiomPane createEditAxiomPane(JComponent component, AxiomExpressionEditor editor, ProofFrameSectionRow row) {
    	if (isBufferingMode()) {
			EditAndSyncAxiomPane optionPane = new EditAndSyncAxiomPane(component);
			
			optionPane.setEditorHandler(new BufferingEditHandler(component, optionPane, editor, row));
			
			return optionPane;
		}
		else {
			EditAxiomPane optionPane = new EditAxiomPane(component);
			
			optionPane.setEditorHandler(new NonBufferingEditHandler(component, optionPane, editor, row));
			
			return optionPane;
		}
    }
	
	/**
	 * 
	 * @author	Pavel Klinov
	 * 			pavel.klinov@uni-ulm.de
	 *
	 */
	private abstract class EditHandler<C extends EditAxiomPane> extends ComponentAdapter {
		
		protected final JComponent editorComponent;
		
		protected final C editorPane;
		
		protected final AxiomExpressionEditor editor;
		
		protected final ProofFrameSectionRow expressionRow;
		
		public abstract void componentHidden(ComponentEvent e);
		
		EditHandler(JComponent component, C pane, AxiomExpressionEditor ed, ProofFrameSectionRow row) {
			editorComponent = component;
			editorPane = pane;
			editor = ed;
			expressionRow = row;
		}
		
		void handleEditFinished(OWLAxiom newAxiom, ProofFrameSectionRow exprRow) {
			List<OWLOntologyChange> changes = new ArrayList<OWLOntologyChange>();
			OWLOntology ontology = kit_.getOWLModelManager().getActiveOntology();
			// remove the old axiom
			changes.add(new RemoveAxiom(ontology, exprRow.getAxiom()));
			changes.add(new AddAxiom(ontology, newAxiom));

			kit_.getOWLModelManager().applyChanges(changes);
		}
    
	}
	
	/**
	 * 
	 * @author	Pavel Klinov
	 * 			pavel.klinov@uni-ulm.de
	 *
	 */
	private class NonBufferingEditHandler extends EditHandler<EditAxiomPane> {
		
		NonBufferingEditHandler(JComponent component, EditAxiomPane pane, AxiomExpressionEditor ed, ProofFrameSectionRow row) {
			super(component, pane, ed, row);
		}
		
		public void componentHidden(ComponentEvent e) {
            Object retVal = editorPane.getValue();
            
            editorComponent.setPreferredSize(editorComponent.getSize());
            
            if (retVal != null) { 
            	if (retVal.equals(EditAxiomPane.OK)) {
            		handleEditFinished(editor.getEditedObject(), expressionRow);
            		// immediately update the root, the reasoner should return up-to-date results
            		// mainPanel_.updateProofRoot();
            	}
            }
            
            //editor.removeStatusChangedListener(verificationListener);
            editor.dispose();
        }
    
	}
	
	/**
	 * 
	 * @author	Pavel Klinov
	 * 			pavel.klinov@uni-ulm.de
	 *
	 */
	private class BufferingEditHandler extends EditHandler<EditAndSyncAxiomPane> {
		
		BufferingEditHandler(JComponent component, EditAndSyncAxiomPane pane, AxiomExpressionEditor ed, ProofFrameSectionRow row) {
			super(component, pane, ed, row);
		}
		
		public void componentHidden(ComponentEvent e) {
            Object retVal = editorPane.getValue();
            
            editorComponent.setPreferredSize(editorComponent.getSize());
            
            if (retVal != null) { 
            	if (retVal.equals(EditAxiomPane.OK)) {
            		handleEditFinished(editor.getEditedObject(), expressionRow);
            		// only show proofs which do not use the old axiom
            		blockInferencesForPremise(expressionRow.getRoot());
            	}
            	else if (retVal.equals(EditAndSyncAxiomPane.OK_SYNC)) {
            		handleEditFinished(editor.getEditedObject(), expressionRow);
            		// synchronize the reasoner
            		reasonerSyncAction_.actionPerformed(new ActionEvent(editorPane.getSyncButton(), ActionEvent.ACTION_PERFORMED, EditAndSyncAxiomPane.OK_SYNC));
            	}
            }
            
            //editor.removeStatusChangedListener(verificationListener);
            editor.dispose();
        }
	}

}
