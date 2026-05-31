package invox.ui;

import invox.model.User;
import invox.repository.CategoryJdbcRepository;
import invox.repository.ClientJdbcRepository;
import invox.repository.InMemoryCategoryRepository;
import invox.repository.InMemoryClientRepository;
import invox.repository.InMemoryInvoiceRepository;
import invox.repository.InMemoryProductRepository;
import invox.repository.InMemoryUserRepository;
import invox.repository.InvoiceJdbcRepository;
import invox.repository.ProductJdbcRepository;
import invox.repository.UserJdbcRepository;
import invox.service.AuthService;
import invox.service.CategoryService;
import invox.service.ClientService;
import invox.service.InvoiceService;
import invox.service.ProductService;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

public class InvoxApp extends Application {

    private CategoryService categoryService;
    private ProductService productService;
    private ClientService clientService;
    private InvoiceService invoiceService;
    private AuthService authService;

    private User currentUser;

    private ProductsView productsView;
    private ClientsView clientsView;
    private InvoicesView invoicesView;
    private SettingsView settingsView;

    @Override
    public void start(Stage stage) {
        wireServices();

        LoginStage login = new LoginStage(authService);
        login.showAndWait();
        currentUser = login.getLoggedUser();
        if (currentUser == null) {
            Platform.exit();
            return;
        }

        productsView = new ProductsView(productService, categoryService);
        clientsView = new ClientsView(clientService);
        invoicesView = new InvoicesView(invoiceService, clientService, productService, currentUser);
        settingsView = new SettingsView(currentUser, authService);

        TabPane tabs = new TabPane(
                tab("Produse", productsView),
                tab("Clienti", clientsView),
                tab("Facturi", invoicesView),
                tab("Setari cont", settingsView));

        BorderPane root = new BorderPane();
        root.setTop(buildMenuBar(stage));
        root.setCenter(tabs);

        Scene scene = new Scene(root, 980, 620);
        Theme.apply(scene);
        stage.setScene(scene);
        stage.setTitle("INVOX - " + currentUser.getCompanyName());
        stage.show();
    }

    private Tab tab(String title, javafx.scene.Node content) {
        Tab t = new Tab(title, content);
        t.setClosable(false);
        return t;
    }

    private static final boolean USE_DATABASE = false;

    private void wireServices() {
        if (USE_DATABASE) {
            categoryService = new CategoryService(new CategoryJdbcRepository());
            productService = new ProductService(new ProductJdbcRepository());
            clientService = new ClientService(new ClientJdbcRepository());
            invoiceService = new InvoiceService(new InvoiceJdbcRepository(), productService);
        } else {
            categoryService = new CategoryService(new InMemoryCategoryRepository());
            productService = new ProductService(new InMemoryProductRepository());
            clientService = new ClientService(new InMemoryClientRepository());
            invoiceService = new InvoiceService(new InMemoryInvoiceRepository(), productService);
        }
        authService = new AuthService(USE_DATABASE
                ? new UserJdbcRepository()
                : new InMemoryUserRepository());
        seedDemoDataIfEmpty();
    }

    private void seedDemoDataIfEmpty() {
        try {
            if (!categoryService.listCategories().isEmpty()) {
                return;
            }
            var electronice = categoryService.addCategory("Electronice", "Produse IT");
            categoryService.addCategory("Servicii", "Servicii prestate");
            productService.addProduct("PRD-001", "Laptop Dell", "buc", 3500, 19, 10, electronice);
            productService.addProduct("PRD-002", "Mouse wireless", "buc", 90, 19, 50, electronice);
            clientService.addCompanyClient("contact@acme.ro", "0712345678", "Str. Industriilor 10",
                    "Bucuresti", "Bucuresti", "ACME SRL", "RO12345678", "J40/123/2020",
                    "RO49INGB0000999912345678", "ING Bank");
            clientService.addIndividualClient("ion.popescu@gmail.com", "0723456789", "Bd. Unirii 5",
                    "Cluj-Napoca", "Cluj", "Ion", "Popescu", "1900101123456");
            authService.register("demo", "demo", "Firma Mea SRL", "RO99999999", "J40/999/2024",
                    "RO12BTRL0000000000123456", "Banca Transilvania", "office@firmamea.ro",
                    "0799999999", "Str. Principala 1", "Bucuresti", "Bucuresti");
        } catch (Exception e) {
            System.err.println("[SEED] Nu am putut adauga datele de start: " + e.getMessage());
        }
    }

    private MenuBar buildMenuBar(Stage stage) {
        Menu fileMenu = new Menu("Fisier");
        MenuItem refreshItem = new MenuItem("Reincarca tot");
        refreshItem.setOnAction(e -> {
            productsView.reload();
            clientsView.reload();
            invoicesView.reload();
            settingsView.reload();
        });
        MenuItem exitItem = new MenuItem("Iesire");
        exitItem.setOnAction(e -> stage.close());
        fileMenu.getItems().addAll(refreshItem, exitItem);

        Menu helpMenu = new Menu("Ajutor");
        MenuItem aboutItem = new MenuItem("Despre");
        aboutItem.setOnAction(e -> Dialogs.info("INVOX",
                "Aplicatie de facturare si gestiune a stocului."));
        helpMenu.getItems().add(aboutItem);

        return new MenuBar(fileMenu, helpMenu);
    }

    public static void main(String[] args) {
        launch(args);
    }
}
