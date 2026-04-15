package com.ecommerce.controller;

import com.ecommerce.dao.ProduitDAO;
import com.ecommerce.model.Produit;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;

import java.net.URL;
import java.util.ResourceBundle;

public class ProduitController implements Initializable {

    @FXML private TextField tfNom, tfPrix, tfStock;
    @FXML private TableView<Produit> tableProduits;
    @FXML private TableColumn<Produit, Integer> colId, colStock;
    @FXML private TableColumn<Produit, String>  colNom;
    @FXML private TableColumn<Produit, Double>  colPrix;
    @FXML private Label lblMessage;

    private ProduitDAO produitDAO = new ProduitDAO();
    private ObservableList<Produit> produits = FXCollections.observableArrayList();
    private Produit produitSelectionne = null;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        colId.setCellValueFactory(data -> data.getValue().idProperty().asObject());
        colNom.setCellValueFactory(data -> data.getValue().nomProperty());
        colPrix.setCellValueFactory(data -> data.getValue().prixProperty().asObject());
        colStock.setCellValueFactory(data -> data.getValue().stockProperty().asObject());

        // Formater le prix
        colPrix.setCellFactory(col -> new TableCell<>() {
            @Override
            protected void updateItem(Double prix, boolean empty) {
                super.updateItem(prix, empty);
                setText(empty || prix == null ? null : String.format("%.0f FCFA", prix));
            }
        });

        tableProduits.setItems(produits);
        chargerProduits();
    }

    private void chargerProduits() {
        produits.setAll(produitDAO.listerTous());
    }

    @FXML
    private void ajouterProduit() {
        if (!validerFormulaire()) return;
        Produit p = new Produit(0, tfNom.getText().trim(),
                Double.parseDouble(tfPrix.getText().trim()),
                Integer.parseInt(tfStock.getText().trim()));
        if (produitDAO.ajouter(p)) {
            afficherMessage("✅ Produit ajouté.", true);
            chargerProduits();
            effacerFormulaire();
        } else {
            afficherMessage("❌ Erreur ajout produit.", false);
        }
    }

    @FXML
    private void modifierProduit() {
        if (produitSelectionne == null) { afficherMessage("⚠️ Sélectionnez un produit.", false); return; }
        if (!validerFormulaire()) return;
        produitSelectionne.setNom(tfNom.getText().trim());
        produitSelectionne.setPrix(Double.parseDouble(tfPrix.getText().trim()));
        produitSelectionne.setStock(Integer.parseInt(tfStock.getText().trim()));
        if (produitDAO.modifier(produitSelectionne)) {
            afficherMessage("✅ Produit modifié.", true);
            chargerProduits();
            effacerFormulaire();
        } else {
            afficherMessage("❌ Erreur modification.", false);
        }
    }

    @FXML
    private void supprimerProduit() {
        if (produitSelectionne == null) { afficherMessage("⚠️ Sélectionnez un produit.", false); return; }
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION,
            "Supprimer " + produitSelectionne.getNom() + " ?", ButtonType.YES, ButtonType.NO);
        alert.showAndWait().ifPresent(btn -> {
            if (btn == ButtonType.YES) {
                if (produitDAO.supprimer(produitSelectionne.getId())) {
                    afficherMessage("✅ Produit supprimé.", true);
                    chargerProduits();
                    effacerFormulaire();
                } else {
                    afficherMessage("❌ Erreur suppression.", false);
                }
            }
        });
    }

    @FXML
    private void selectionnerProduit(MouseEvent e) {
        produitSelectionne = tableProduits.getSelectionModel().getSelectedItem();
        if (produitSelectionne != null) {
            tfNom.setText(produitSelectionne.getNom());
            tfPrix.setText(String.valueOf(produitSelectionne.getPrix()));
            tfStock.setText(String.valueOf(produitSelectionne.getStock()));
        }
    }

    @FXML
    public void effacerFormulaire() {
        tfNom.clear(); tfPrix.clear(); tfStock.clear();
        produitSelectionne = null;
        tableProduits.getSelectionModel().clearSelection();
        lblMessage.setText("");
    }

    private boolean validerFormulaire() {
        try {
            if (tfNom.getText().trim().isEmpty()) throw new Exception("Nom vide");
            Double.parseDouble(tfPrix.getText().trim());
            Integer.parseInt(tfStock.getText().trim());
            return true;
        } catch (Exception e) {
            afficherMessage("⚠️ Vérifiez les champs (nom, prix, stock valides).", false);
            return false;
        }
    }

    private void afficherMessage(String msg, boolean succes) {
        lblMessage.setText(msg);
        lblMessage.setStyle("-fx-text-fill: " + (succes ? "#27ae60" : "#e74c3c") + ";");
    }
}

