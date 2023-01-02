package ch.epfl.javelo.gui;

import javafx.animation.FadeTransition;
import javafx.animation.PauseTransition;
import javafx.animation.SequentialTransition;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.util.Duration;

/**
 * @author Ursula El-Khoury (340994)
 * @author Omar Hammoud (341700)
 */

/**
 * Public final class that handles the graphics of the errors
 */
public final class ErrorManager {
    private final VBox pane;
    private final Text text;
    private final ObjectProperty<SequentialTransition> transitionObjectProperty;

    /**
     * Public constructor of the object ErrorManager
     */
    public ErrorManager() {
        pane = new VBox(text = new Text());

        pane.getStylesheets().add("error.css");
        pane.setMouseTransparent(true);

        transitionObjectProperty = new SimpleObjectProperty<>();
        transitionVBox();
    }

    /**
     * Public class that returns the pane of ErrorManager
     * @return the attribute pane of the class
     */
    public Pane pane() {
        return pane;
    }

    /**
     * Public method that handles the display of the error on the screen
     * @param errorMessage : the error message
     */
    public void displayError(String errorMessage) {
        transitionObjectProperty.get().stop();
        text.setText(errorMessage);
        transitionObjectProperty.get().play();
        java.awt.Toolkit.getDefaultToolkit().beep();
    }

    /**
     * Private method that creates the transition of the pane
     */
    private void transitionVBox() {
        FadeTransition firstFadeTransition = fadeTransition(0.2, 0, 0.8);
        PauseTransition pauseTransition = new PauseTransition(Duration.seconds(2));
        FadeTransition secondFadeTransition = fadeTransition(0.5, 0.8, 0);

        SequentialTransition sequentialTransition = new SequentialTransition(pane,
                firstFadeTransition, pauseTransition, secondFadeTransition);

        transitionObjectProperty.set(sequentialTransition);
    }

    /**
     * Private method that creates a fade transition using the parameters :
     * @param time      : the time that the transition takes to play
     * @param fromValue : the value of the original opacity of the transition
     * @param toValue   : the value of the final opacity of the transition
     * @return the fade transition
     */
    private FadeTransition fadeTransition(double time, double fromValue, double toValue) {
        FadeTransition fadeTransition = new FadeTransition(Duration.seconds(time), pane);
        fadeTransition.setFromValue(fromValue);
        fadeTransition.setToValue(toValue);
        return fadeTransition;
    }

}
