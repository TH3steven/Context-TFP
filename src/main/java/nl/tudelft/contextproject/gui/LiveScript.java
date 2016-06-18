package nl.tudelft.contextproject.gui;

import javafx.css.PseudoClass;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;

import nl.tudelft.contextproject.ContextTFP;
import nl.tudelft.contextproject.script.Script;
import nl.tudelft.contextproject.script.Shot;

public class LiveScript {

    static Script script = ContextTFP.getScript();
    
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
