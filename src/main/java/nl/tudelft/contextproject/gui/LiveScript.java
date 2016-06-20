package nl.tudelft.contextproject.gui;

import javafx.css.PseudoClass;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;

import nl.tudelft.contextproject.ContextTFP;
import nl.tudelft.contextproject.script.Script;
import nl.tudelft.contextproject.script.Shot;

/**
 * Class that handles the live script. For now just enables the table
 * to highlight the current shot. Put in a seperate class
 * to prevent code duplication.
 * 
 * @since 0.9
 */
public final class LiveScript {

    static final Script script = ContextTFP.getScript();

    /**
     * Constructor should not be called, so defining
     * it as private.
     */
    private LiveScript() { }

    /**
     * Sets the rowFactory for the given table, to allow it to highlight the
     * current shot by applying a CSS {@link PseudoClass}.
     * 
     * @param tableShots The table to set the rowFactory of.
     */
    protected static void setRowFactory(TableView<Shot> tableShots) {
        final PseudoClass currentPseudoClass = PseudoClass.getPseudoClass("current");

        tableShots.setRowFactory(table -> new TableRow<Shot>() {

            @Override
            protected void updateItem(Shot s, boolean b) {
                super.updateItem(s, b);
                if (s != null) {
                    boolean current = s.equals(script.getCurrentShot());
                    pseudoClassStateChanged(currentPseudoClass, current);
                    tableShots.refresh();
                }
            }
        });
    }
}
