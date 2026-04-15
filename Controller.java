package controller;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import model.Produit;
import dao.ProduitDAO;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class ProduitController {

    @FXML private TextField txtNom;
    @FXML private TextField txtPrix;
    @FXML private TextField txtQuantite;
    @FXML private TableView<Produit> tableProduits;
    @FXML private TableColumn<Produit, Integer> colId;
    @FXML private TableColumn<Produit, String> colNom;
    @FXML private TableColumn<Produit, Double> colPrix;
    @FXML private TableColumn<Produit, Integer> colStock;

    private ProduitDAO dao = new ProduitDAO();

    @FXML
    public void initialize() {
        // Cette méthode s'exécute au chargement de la vue
        configurerColonnes();
        chargerDonnees();
    }

    private void configurerColonnes() {
        // Lie les colonnes du tableau aux attributs de la classe Produit.java
        colId.setCellValueFactory(cellData -> cellData.getValue().idProperty().asObject());
        colNom.setCellValueFactory(cellData -> cellData.getValue().nomProperty());
        // ... etc
    }

    @FXML
    private void handleAjouter() {
        // Logique d'ajout avec vérification
        if (txtNom.getText().isEmpty()) {
            afficherAlerte("Erreur", "Le nom est obligatoire !");
            return;
        }
        // Appeler le DAO ici...
    }

    private void afficherAlerte(String titre, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(titre);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
