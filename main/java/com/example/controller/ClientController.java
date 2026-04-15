package com.ecommerce.controller;

import com.ecommerce.dao.ClientDAO;
import com.ecommerce.model.Client;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;

import java.net.URL;
import java.util.ResourceBundle;

public class ClientController implements Initializable {

    @FXML private TextField tfNom, tfPrenom, tfEmail;
    @FXML private TableView<Client> tableClients;
    @FXML private TableColumn<Client, Integer> colId;
    @FXML private TableColumn<Client, String>  colNom, colPrenom, colEmail;
    @FXML private Label lblMessage;

    private ClientDAO clientDAO = new ClientDAO();
    private ObservableList<Client> clients = FXCollections.observableArrayList();
    private Client clientSelectionne = null;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // Liaison colonnes ↔ propriétés
        colId.setCellValueFactory(data -> data.getValue().idProperty().asObject());
        colNom.setCellValueFactory(data -> data.getValue().nomProperty());
        colPrenom.setCellValueFactory(data -> data.getValue().prenomProperty());
        colEmail.setCellValueFactory(data -> data.getValue().emailProperty());

        tableClients.setItems(clients);
        chargerClients();
    }

    private void chargerClients() {
        clients.setAll(clientDAO.listerTous());
    }

    @FXML
    private void ajouterClient() {
        if (!validerFormulaire()) return;
        Client c = new Client(0, tfNom.getText().trim(),
                                 tfPrenom.getText().trim(),
                                 tfEmail.getText().trim());
        if (clientDAO.ajouter(c)) {
            afficherMessage("✅ Client ajouté avec succès.", true);
            chargerClients();
            effacerFormulaire();
        } else {
            afficherMessage("❌ Erreur lors de l'ajout.", false);
        }
    }

    @FXML
    private void modifierClient() {
        if (clientSelectionne == null) { afficherMessage("⚠️ Sélectionnez un client.", false); return; }
        if (!validerFormulaire()) return;
        clientSelectionne.setNom(tfNom.getText().trim());
        clientSelectionne.setPrenom(tfPrenom.getText().trim());
        clientSelectionne.setEmail(tfEmail.getText().trim());
        if (clientDAO.modifier(clientSelectionne)) {
            afficherMessage("✅ Client modifié.", true);
            chargerClients();
            effacerFormulaire();
        } else {
            afficherMessage("❌ Erreur lors de la modification.", false);
        }
    }

    @FXML
    private void supprimerClient() {
        if (clientSelectionne == null) { afficherMessage("⚠️ Sélectionnez un client.", false); return; }
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION,
            "Supprimer " + clientSelectionne.getPrenom() + " " + clientSelectionne.getNom() + " ?",
            ButtonType.YES, ButtonType.NO);
        alert.showAndWait().ifPresent(btn -> {
            if (btn == ButtonType.YES) {
                if (clientDAO.supprimer(clientSelectionne.getId())) {
                    afficherMessage("✅ Client supprimé.", true);
                    chargerClients();
                    effacerFormulaire();
                } else {
                    afficherMessage("❌ Impossible de supprimer (commandes liées ?).", false);
                }
            }
        });
    }

    @FXML
    private void selectionnerClient(MouseEvent e) {
        clientSelectionne = tableClients.getSelectionModel().getSelectedItem();
        if (clientSelectionne != null) {
            tfNom.setText(clientSelectionne.getNom());
            tfPrenom.setText(clientSelectionne.getPrenom());
            tfEmail.setText(clientSelectionne.getEmail());
        }
    }

    @FXML
    public void effacerFormulaire() {
        tfNom.clear(); tfPrenom.clear(); tfEmail.clear();
        clientSelectionne = null;
        tableClients.getSelectionModel().clearSelection();
        lblMessage.setText("");
    }

    private boolean validerFormulaire() {
        if (tfNom.getText().trim().isEmpty() ||
            tfPrenom.getText().trim().isEmpty() ||
            tfEmail.getText().trim().isEmpty()) {
            afficherMessage("⚠️ Tous les champs sont obligatoires.", false);
            return false;
        }
        if (!tfEmail.getText().contains("@")) {
            afficherMessage("⚠️ Email invalide.", false);
            return false;
        }
        return true;
    }

    private void afficherMessage(String msg, boolean succes) {
        lblMessage.setText(msg);
        lblMessage.setStyle("-fx-text-fill: " + (succes ? "#27ae60" : "#e74c3c") + ";");
    }
}

