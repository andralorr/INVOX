package invox.ui;

import invox.model.Category;
import invox.model.Product;
import invox.service.CategoryService;
import invox.service.ProductService;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

public class ProductsView extends BorderPane {

    private final ProductService productService;
    private final CategoryService categoryService;

    private final ObservableList<Product> products = FXCollections.observableArrayList();
    private final FilteredList<Product> filtered = new FilteredList<>(products, p -> true);
    private final TableView<Product> table = new TableView<>(filtered);
    private final TextField searchField = new TextField();

    private final TextField codeField = new TextField();
    private final TextField nameField = new TextField();
    private final TextField unitField = new TextField();
    private final TextField priceField = new TextField();
    private final TextField vatField = new TextField();
    private final TextField stockField = new TextField();
    private final ComboBox<Category> categoryCombo = new ComboBox<>();

    public ProductsView(ProductService productService, CategoryService categoryService) {
        this.productService = productService;
        this.categoryService = categoryService;
        buildTable();
        searchField.setPromptText("Cauta dupa cod, nume sau id...");
        searchField.textProperty().addListener((obs, oldV, newV) -> applyFilter(newV));
        VBox center = new VBox(8, searchField, table);
        VBox.setVgrow(table, Priority.ALWAYS);
        center.setPadding(new Insets(12));
        setCenter(center);
        setRight(buildForm());
        reload();
    }

    private void applyFilter(String query) {
        String s = query == null ? "" : query.trim().toLowerCase();
        filtered.setPredicate(p -> {
            if (s.isEmpty()) {
                return true;
            }
            return (p.getName() != null && p.getName().toLowerCase().contains(s))
                    || (p.getCode() != null && p.getCode().toLowerCase().contains(s))
                    || String.valueOf(p.getId()).contains(s);
        });
    }

    private void buildTable() {
        addCol("Cod", p -> p.getCode());
        addCol("Denumire", p -> p.getName());
        addCol("UM", p -> p.getUnit());
        addCol("Pret", p -> String.valueOf(p.getPrice()));
        addCol("TVA %", p -> String.valueOf(p.getVatRate()));
        addCol("Stoc", p -> String.valueOf(p.getStockQuantity()));
        addCol("Categorie", p -> p.getCategory() != null ? p.getCategory().getName() : "");
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
    }

    private void addCol(String title, java.util.function.Function<Product, String> getter) {
        TableColumn<Product, String> col = new TableColumn<>(title);
        col.setCellValueFactory(cd -> new SimpleStringProperty(getter.apply(cd.getValue())));
        table.getColumns().add(col);
    }

    private VBox buildForm() {
        categoryCombo.setCellFactory(cb -> categoryCell());
        categoryCombo.setButtonCell(categoryCell());
        refreshCategories();
        if (!categoryCombo.getItems().isEmpty()) {
            categoryCombo.getSelectionModel().selectFirst();
        }

        GridPane grid = new GridPane();
        grid.setHgap(8);
        grid.setVgap(8);
        grid.setPadding(new Insets(12));
        grid.addRow(0, new Label("Cod:"), codeField);
        grid.addRow(1, new Label("Denumire:"), nameField);
        grid.addRow(2, new Label("UM:"), unitField);
        grid.addRow(3, new Label("Pret:"), priceField);
        grid.addRow(4, new Label("TVA %:"), vatField);
        grid.addRow(5, new Label("Stoc:"), stockField);
        grid.addRow(6, new Label("Categorie:"), categoryCombo);

        Button addBtn = new Button("Adauga");
        addBtn.setOnAction(e -> onAdd());
        Button editBtn = new Button("Editeaza selectat");
        editBtn.setOnAction(e -> onEdit());
        Button deleteBtn = new Button("Sterge selectat");
        deleteBtn.setOnAction(e -> onDelete());
        Button newCatBtn = new Button("Categorie noua");
        newCatBtn.setOnAction(e -> onNewCategory());

        VBox buttons = new VBox(8, addBtn, editBtn, deleteBtn, newCatBtn);

        VBox box = new VBox(10, new Label("Produs nou"), grid, buttons);
        box.setPadding(new Insets(12));
        box.setPrefWidth(440);
        box.getStyleClass().add("card");
        return box;
    }

    private ListCell<Category> categoryCell() {
        return new ListCell<>() {
            @Override protected void updateItem(Category c, boolean empty) {
                super.updateItem(c, empty);
                setText((empty || c == null) ? null : c.getName());
            }
        };
    }

    private void onAdd() {
        try {
            String code = codeField.getText().trim();
            String name = nameField.getText().trim();
            if (code.isEmpty() || name.isEmpty()) {
                Dialogs.error("Cod si denumire sunt obligatorii.");
                return;
            }
            double price = Double.parseDouble(priceField.getText().trim());
            double vat = Double.parseDouble(vatField.getText().trim());
            int stock = Integer.parseInt(stockField.getText().trim());
            productService.addProduct(code, name, unitField.getText().trim(),
                    price, vat, stock, categoryCombo.getValue());
            clearForm();
            reload();
        } catch (NumberFormatException ex) {
            Dialogs.error("Pret, TVA si stoc trebuie sa fie numere.");
        }
    }

    private void onDelete() {
        Product selected = table.getSelectionModel().getSelectedItem();
        if (selected == null) {
            Dialogs.error("Selecteaza un produs din lista.");
            return;
        }
        try {
            productService.deleteProduct(selected.getId());
            reload();
        } catch (Exception ex) {
            Dialogs.error(ex.getMessage());
        }
    }

    private void onEdit() {
        Product selected = table.getSelectionModel().getSelectedItem();
        if (selected == null) {
            Dialogs.error("Selecteaza un produs din lista.");
            return;
        }
        new ProductDetailDialog(selected, productService, categoryService).showAndWait();
        reload();
    }

    private void onNewCategory() {
        CategoryDialog dialog = new CategoryDialog(categoryService);
        dialog.showAndWait();
        if (dialog.getCreated() != null) {
            refreshCategories();
            for (Category c : categoryCombo.getItems()) {
                if (c.getId() == dialog.getCreated().getId()) {
                    categoryCombo.getSelectionModel().select(c);
                    break;
                }
            }
        }
    }

    private void refreshCategories() {
        categoryCombo.setItems(FXCollections.observableArrayList(categoryService.listCategories()));
    }

    private void clearForm() {
        codeField.clear();
        nameField.clear();
        unitField.clear();
        priceField.clear();
        vatField.clear();
        stockField.clear();
    }

    public void reload() {
        products.setAll(productService.listProducts());
    }
}