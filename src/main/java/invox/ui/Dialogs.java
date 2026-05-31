package invox.ui;

import javafx.scene.control.Alert;

public final class Dialogs {

    private Dialogs() {
    }

    public static void info(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        style(alert);
        alert.showAndWait();
    }

    public static void error(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Eroare");
        alert.setHeaderText(null);
        alert.setContentText(message);
        style(alert);
        alert.showAndWait();
    }

    private static void style(Alert alert) {
        if (Theme.STYLESHEET != null) {
            alert.getDialogPane().getStylesheets().add(Theme.STYLESHEET);
        }
    }
}
