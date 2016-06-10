package nl.tudelft.contextproject.gui;

import javafx.animation.FadeTransition;
import javafx.animation.Interpolator;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.animation.TranslateTransition;
import javafx.scene.Node;
import javafx.util.Duration;

/**
 * This class is responsible for all animations made by the
 * gui. It handles all translations, fades and other visually
 * appealing effects that the end user can see.
 * 
 * @since 0.7
 */
public final class Animation {

    private static final int DURATION_FADE = 500;
    private static final int DURATION_TIMEOUT = 1000;
    private static final int DURATION_TRANS = 300;
    private static final int OFFSET_Y = 80;

    /**
     * Abstract classes should not have a public constructor,
     * so defining it as private below.
     */
    private Animation() {
        throw new UnsupportedOperationException();
    }

    /**
     * Animation for a {@link Node}. Makes the Node move up.
     * 
     * @param n The Node to animate.
     * @see #animNodeUpDown(Node, boolean)
     */
    protected static void animNodeUp(Node n) {
        animNodeUpDown(n, true);
    }

    /**
     * Animation for a {@link Node}. Makes the Node move down.
     * 
     * @param n The Node to animate.
     * @see #animNodeUpDown(Node, boolean)
     */
    protected static void animNodeDown(Node n) {
        animNodeUpDown(n, false);
    }

    /**
     * Move a {@link Node} up or down. Uses a {@link TranslateTransition}
     * to translate the Node. Also uses an EASE_OUT {@link Interpolator}
     * to smooth out the translation. The Node is disabled during the
     * translation to prevent the Node from being clicked on twice, 
     * messing up the translation.
     * 
     * @param n The Node to move.
     * @param up True if the animation is up, false if down.
     */
    private static void animNodeUpDown(Node n, boolean up) {
        n.setDisable(true);

        TranslateTransition tt = new TranslateTransition(Duration.millis(DURATION_TRANS), n);

        if (up) {
            tt.setByY(-OFFSET_Y);
        } else {
            tt.setByY(OFFSET_Y);
        }

        tt.setInterpolator(Interpolator.EASE_OUT);
        tt.play();
        tt.setOnFinished(event -> {
            n.setDisable(false);
        });
    }

    /**
     * Animation for a {@link Node}. Makes the Node fade in. Uses a
     * {@link FadeTransition} to create the fade animation. Makes the
     * Node visible to make sure the fade is displayed.
     * 
     * @param n The Node to animate.
     */
    protected static void animNodeIn(Node n) {
        FadeTransition ft = new FadeTransition(Duration.millis(DURATION_FADE), n);
        ft.setFromValue(0.0);
        ft.setToValue(1.0);

        ft.play();
        n.setVisible(true);
    }

    /**
     * Animation for a {@link Node}. Makes the Node fade out. Uses a 
     * {@link FadeTransition} to create the fade animation. The duration of the
     * fade is {@link #DURATION_FADE} / 5 when bMakeInvisible is false, or
     * {@link #DURATION_FADE} / 3 when bMakeInvisible is true.
     * 
     * @param n The Node to animate.
     * @param bMakeInvisible If the Node should be made invisible after the animation.
     */
    protected static FadeTransition animNodeOut(Node n, boolean bMakeInvisible) {
        int dur;
        
        if (bMakeInvisible) {
            dur = DURATION_FADE / 5;
        } else {
            dur = DURATION_FADE / 3;
        }
        
        FadeTransition ft = new FadeTransition(Duration.millis(dur), n);
        ft.setFromValue(1.0);
        ft.setToValue(0.0);

        ft.play();
        
        if (bMakeInvisible) {
            ft.setOnFinished(event -> {
                n.setVisible(false);
            });
        } else {
            animTimeout();
        }
        
        return ft;
    }

    /**
     * Sets a timeout to prevent the window from being changed when a fade out
     * animation is still playing. Only called from {@link #animNodeOut(Node, boolean)}
     */
    private static void animTimeout() {
        Timeline timeout = 
                new Timeline(
                        new KeyFrame(
                                Duration.millis(DURATION_TIMEOUT)));

        timeout.play();
    }
}
