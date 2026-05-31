package invox.ui;

import invox.model.Category;
import invox.service.CategoryService;

import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class CategoryDialog extends Stage {

    private Category created;

    public CategoryDialog(CategoryService categoryService) {
        initModality(Modality.APPLICATION_MODAL);
        setTitle("Categorie noua");

        TextField name = new TextField();
        TextField description = new TextField();

        GridPane grid = new GridPane();
        grid.setHgap(8);
        grid.setVgap(6);
        grid.setPadding(new Insets(12));
        grid.addRow(0, new Label("Nume:"), name);
        grid.addRow(1, new Label("Descriere:"), description);

        Button saveBtn = new Button("Salveaza");
        saveBtn.setOnAction(e -> {
            if (name.getText().trim().isEmpty()) {
                Dialogs.error("Numele categoriei este obligatoriu.");
                return;
            }
            created = categoryService.addCategory(name.getText().trim(), description.getText().trim());
            close();
        });
        Button cancelBtn = new Button("Anuleaza");
        cancelBtn.setOnAction(e -> close());

        VBox root = new VBox(10, grid, new HBox(8, saveBtn, cancelBtn));
        root.setPadding(new Insets(12));
        setScene(new Scene(root));
        Theme.apply(getScene());
    }

    public Category getCreated() {
        return created;
    }
}
