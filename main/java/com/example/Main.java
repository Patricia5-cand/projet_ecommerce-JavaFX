package com.example;
import com.model.DB_connection;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.stage.Stage;
import java.io.IOException;

public class Main  extends Application {

    @Override
    public void start(Stage primaryStage) throws IOException {
        TabPane tabPane = new TabPane();
        tabPane.setTabClosingPolicy(TabPane.TabClosingPolicy.UNAVAILABLE);

        // Onglet Clients
        Tab tabClients = new Tab("👤 Clients");
        tabClients.setContent(FXMLLoader.load(
                getClass().getResource("/view/client.fxml")));

        // Onglet Produits
        Tab tabProduits = new Tab("📦 Produits");
        tabProduits.setContent(FXMLLoader.load(
                getClass().getResource("/view/produit.fxml")));

        // Onglet Commandes
        Tab tabCommandes = new Tab("🛒 Commandes");
        tabCommandes.setContent(FXMLLoader.load(
                getClass().getResource("/view/Commande.fxml")));

        tabPane.getTabs().addAll(tabClients, tabProduits, tabCommandes);

        Scene scene = new Scene(tabPane, 1100, 700);
        scene.getStylesheets().add(getClass().getResource("/css/style.css").toExternalForm());

        primaryStage.setTitle("🛍️ Mini E-Commerce");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    @Override
    public void stop() {
        DB_connection.closeConnection();
    }

    public static void main(String[] args) {
        launch(args);
    }
}

