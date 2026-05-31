package invox.ui;

import javafx.scene.Scene;

import java.io.File;
import java.nio.file.Files;

public final class Theme {

    public static final String STYLESHEET;

    static {
        STYLESHEET = resolve();
    }

    private Theme() {
    }

    private static String resolve() {
        var url = Theme.class.getResource("/styles.css");
        if (url != null) {
            System.out.println("[THEME] Folosesc /styles.css de pe classpath: " + url);
            return url.toExternalForm();
        }
        try {
            File tmp = File.createTempFile("invox-style", ".css");
            tmp.deleteOnExit();
            Files.writeString(tmp.toPath(), EMBEDDED_CSS);
            System.out.println("[THEME] /styles.css nu e pe classpath; folosesc stilul inglobat: "
                    + tmp.toURI());
            return tmp.toURI().toString();
        } catch (Exception e) {
            System.err.println("[THEME] Nu am putut aplica stilul: " + e.getMessage());
            return null;
        }
    }

    public static void apply(Scene scene) {
        if (STYLESHEET != null && scene != null) {
            scene.getStylesheets().add(STYLESHEET);
        }
    }

    private static final String EMBEDDED_CSS = """
            .root {
                -fx-font-family: "Segoe UI", "Inter", "Helvetica Neue", Arial, sans-serif;
                -fx-font-size: 13px;
                -fx-background-color: #f4f5f7;
                -fx-focus-color: #6366f1;
                -fx-faint-focus-color: rgba(99, 102, 241, 0.15);
            }
            .label { -fx-text-fill: #1f2937; }

            .button {
                -fx-background-color: #4f46e5;
                -fx-text-fill: white;
                -fx-font-weight: bold;
                -fx-background-radius: 8;
                -fx-padding: 8 16 8 16;
                -fx-cursor: hand;
            }
            .button:hover  { -fx-background-color: #4338ca; }
            .button:pressed { -fx-background-color: #3730a3; }

            .text-field, .combo-box, .combo-box-base {
                -fx-background-color: white;
                -fx-background-radius: 8;
                -fx-border-radius: 8;
                -fx-border-color: #d1d5db;
                -fx-border-width: 1;
                -fx-padding: 6 8 6 8;
            }
            .text-field:focused, .combo-box:focused { -fx-border-color: #6366f1; }

            .card {
                -fx-background-color: white;
                -fx-background-radius: 12;
                -fx-border-color: #e5e7eb;
                -fx-border-radius: 12;
                -fx-effect: dropshadow(gaussian, rgba(17,24,39,0.08), 14, 0, 0, 4);
            }

            .menu-bar { -fx-background-color: #111827; }
            .menu-bar .label { -fx-text-fill: #f9fafb; }
            .menu-bar .menu:hover, .menu-bar .menu:showing { -fx-background-color: #1f2937; }
            .menu-item .label {
                -fx-text-fill: black;
            }
            
            .tab-pane .tab-header-area .tab-header-background { -fx-background-color: transparent; }
            .tab-pane .tab { -fx-background-color: transparent; -fx-padding: 8 20 8 20; }
            .tab-pane .tab:selected { -fx-background-color: white; -fx-background-radius: 10 10 0 0; }
            .tab-pane .tab .tab-label { -fx-text-fill: #374151; -fx-font-weight: bold; }
            .tab-pane .tab:selected .tab-label { -fx-text-fill: #4f46e5; }

            .table-view {
                -fx-background-color: white;
                -fx-background-radius: 10;
                -fx-border-color: #e5e7eb;
                -fx-border-radius: 10;
            }
            .table-view .column-header-background { -fx-background-color: #eef2ff; -fx-background-radius: 10 10 0 0; }
            .table-view .column-header .label { -fx-text-fill: #3730a3; -fx-font-weight: bold; }
            .table-row-cell:odd { -fx-background-color: #fafafa; }
            .table-row-cell:selected { -fx-background-color: #c7d2fe; -fx-text-fill: #111827; }
            """;
}
