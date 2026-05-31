package invox.ui;

import invox.exception.EntityNotFoundException;
import invox.model.Category;
import invox.model.Product;
import invox.service.CategoryService;
import invox.service.ProductService;

import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class ProductDetailDialog extends Stage {

    public ProductDetailDialog(Product product, ProductService productService,
                               CategoryService categoryService) {
        initModality(Modality.APPLICATION_MODAL);
        setTitle("Editare produs #" + product.getId());

        TextField code = new TextField(product.getCode());
        TextField name = new TextField(product.getName());
        TextField unit = new TextField(product.getUnit());
        TextField price = new TextField(String.valueOf(product.getPrice()));
        TextField vat = new TextField(String.valueOf(product.getVatRate()));
        TextField stock = new TextField(String.valueOf(product.getStockQuantity()));

        ComboBox<Category> categoryCombo = new ComboBox<>(
                FXCollections.observableArrayList(categoryService.listCategories()));
        categoryCombo.setCellFactory(cb -> categoryCell());
        categoryCombo.setButtonCell(categoryCell());
        if (product.getCategory() != null) {
            for (Category c : categoryCombo.getItems()) {
                if (c.getId() == product.getCategory().getId()) {
                    categoryCombo.getSelectionModel().select(c);
                    break;
                }
            }
        }

        GridPane grid = new GridPane();
        grid.setHgap(8);
        grid.setVgap(6);
        grid.setPadding(new Insets(12));
        grid.addRow(0, new Label("Cod:"), code);
        grid.addRow(1, new Label("Denumire:"), name);
        grid.addRow(2, new Label("UM:"), unit);
        grid.addRow(3, new Label("Pret:"), price);
        grid.addRow(4, new Label("TVA %:"), vat);
        grid.addRow(5, new Label("Stoc:"), stock);
        grid.addRow(6, new Label("Categorie:"), categoryCombo);

        Button saveBtn = new Button("Salveaza");
        saveBtn.setOnAction(e -> {
            try {
                productService.updateProduct(product.getId(), code.getText().trim(),
                        name.getText().trim(), unit.getText().trim(),
                        Double.parseDouble(price.getText().trim()),
                        Double.parseDouble(vat.getText().trim()),
                        Integer.parseInt(stock.getText().trim()),
                        categoryCombo.getValue());
                close();
            } catch (NumberFormatException ex) {
                Dialogs.error("Pret, TVA si stoc trebuie sa fie numere.");
            } catch (EntityNotFoundException ex) {
                Dialogs.error(ex.getMessage());
            }
        });
        Button cancelBtn = new Button("Inchide");
        cancelBtn.setOnAction(e -> close());

        VBox root = new VBox(10, grid, new HBox(8, saveBtn, cancelBtn));
        root.setPadding(new Insets(12));
        setScene(new Scene(root));
        Theme.apply(getScene());
    }

    private ListCell<Category> categoryCell() {
        return new ListCell<>() {
            @Override protected void updateItem(Category c, boolean empty) {
                super.updateItem(c, empty);
                setText((empty || c == null) ? null : c.getName());
            }
        };
    }
}
