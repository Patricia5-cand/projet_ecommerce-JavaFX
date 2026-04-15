package com.ecommerce.controller;

import com.ecommerce.dao.ClientDAO;
import com.ecommerce.dao.CommandeDAO;
import com.ecommerce.dao.ProduitDAO;
import com.ecommerce.model.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;

import java.net.URL;
import java.time.LocalDate;
import java.util.ResourceBundle;

public class CommandeController implements Initializable {

    // ── Formulaire ──────────────────────────────────────────
    @FXML private ComboBox<Client>  cbClient;
    @FXML private ComboBox<Produit> cbProduit;
    @FXML private TextField         tfQuantite;
    @FXML private Label             lblTotal;
    @FXML private Label             lblMessage;

    // ── Panier (tableau temporaire) ─────────────────────────
    @FXML private TableView<LigneCommande>       tablePanier;
    @FXML private TableColumn<LigneCommande, String>  colProduitPanier;
    @FXML private TableColumn<LigneCommande, Double>  colPrixPanier;
    @FXML private TableColumn<LigneCommande, Integer> colQtePanier;
    @FXML private TableColumn<LigneCommande, Double>  colSousTotalPanier;

    // ── Historique ──────────────────────────────────────────
    @FXML private TableView<Commande>       tableCommandes;
    @FXML private TableColumn<Commande, Integer>    colId;
    @FXML private TableColumn<Commande, LocalDate>  colDate;
    @FXML private TableColumn<Commande, String>     colClient;
    @FXML private TableColumn<Commande, Double>     colTotal;

    private ClientDAO   clientDAO   = new ClientDAO();
    private ProduitDAO  produitDAO  = new ProduitDAO();
    private CommandeDAO commandeDAO = new CommandeDAO();

    private ObservableList<LigneCommande> panier    = FXCollections.observableArrayList();
    private ObservableList<Commande>      commandes = FXCollections.observableArrayList();

    @Override
    public void initialize(URL url, ResourceBundle rb) {

        // ── ComboBoxes ──────────────────────────────────────
        cbClient.setItems(FXCollections.observableArrayList(clientDAO.listerTous()));
        cbProduit.setItems(FXCollections.observableArrayList(produitDAO.listerTous()));

        // ── Colonnes panier ─────────────────────────────────
        colProduitPanier.setCellValueFactory(d -> d.getValue().nomProduitProperty());
        colPrixPanier.setCellValueFactory(d -> d.getValue().prixUnitaireProperty().asObject());
        colQtePanier.setCellValueFactory(d -> d.getValue().quantiteProperty().asObject());
        colSousTotalPanier.setCellValueFactory(d -> d.getValue().sousTotalProperty().asObject());

        colPrixPanier.setCellFactory(c -> new FormattedDoubleCell<>());
        colSousTotalPanier.setCellFactory(c -> new FormattedDoubleCell<>());

        tablePanier.setItems(panier);

        // ── Colonnes historique ─────────────────────────────
        colId.setCellValueFactory(d -> d.getValue().idProperty().asObject());
        colDate.setCellValueFactory(d -> d.getValue().dateProperty());
        colClient.setCellValueFactory(d -> d.getValue().nomClientProperty());
        colTotal.setCellValueFactory(d -> d.getValue().totalProperty().asObject());
        colTotal.setCellFactory(c -> new FormattedDoubleCell<>());

        tableCommandes.setItems(commandes);
        chargerCommandes();

        lblTotal.setText("0 FCFA");
    }

    private class FormattedDoubleCell<T> extends TableCell<T, Double> {
        @Override
        protected void updateItem(Double val, boolean empty) {
            super.updateItem(val, empty);
            setText(empty || val == null ? null : String.format("%.0f FCFA", val));
        }
    }

    // ── Ajouter une ligne au panier ─────────────────────────
    @FXML
    private void ajouterLigne() {
        Produit p = cbProduit.getValue();
        if (p == null) { afficherMessage("⚠️ Sélectionnez un produit.", false); return; }

        int qte;
        try {
            qte = Integer.parseInt(tfQuantite.getText().trim());
            if (qte <= 0) throw new NumberFormatException();
        } catch (NumberFormatException e) {
            afficherMessage("⚠️ Quantité invalide.", false);
            return;
        }

        if (qte > p.getStock()) {
            afficherMessage("⚠️ Stock insuffisant (" + p.getStock() + " disponibles).", false);
            return;
        }

        // Vérifier si le produit est déjà dans le panier
        for (LigneCommande lc : panier) {
            if (lc.getIdProduit() == p.getId()) {
                lc.setQuantite(lc.getQuantite() + qte);
                lc.setSousTotal(lc.getPrixUnitaire() * lc.getQuantite());
                tablePanier.refresh();
                mettreAJourTotal();
                return;
            }
        }

        LigneCommande ligne = new LigneCommande(p.getId(), p.getNom(), p.getPrix(), qte);
        panier.add(ligne);
        mettreAJourTotal();
        afficherMessage("", true);
        tfQuantite.clear();
    }

    // ── Valider la commande ─────────────────────────────────
    @FXML
    private void validerCommande() {
        if (cbClient.getValue() == null) { afficherMessage("⚠️ Sélectionnez un client.", false); return; }
        if (panier.isEmpty())            { afficherMessage("⚠️ Le panier est vide.", false); return; }

        double total = panier.stream().mapToDouble(LigneCommande::getSousTotal).sum();

        Commande commande = new Commande(0, LocalDate.now(), total,
                cbClient.getValue().getId(),
                cbClient.getValue().toString());

        if (commandeDAO.creerCommande(commande, panier)) {
            afficherMessage("✅ Commande validée ! Total : " + String.format("%.0f FCFA", total), true);
            panier.clear();
            lblTotal.setText("0 FCFA");
            chargerCommandes();
            // Rafraîchir le stock dans le ComboBox
            cbProduit.setItems(FXCollections.observableArrayList(produitDAO.listerTous()));
        } else {
            afficherMessage("❌ Erreur lors de la validation.", false);
        }
    }

    // ── Vider le panier ─────────────────────────────────────
    @FXML
    private void viderPanier() {
        panier.clear();
        lblTotal.setText("0 FCFA");
    }

    // ── Sélectionner une commande (affiche les lignes) ──────
    @FXML
    private void selectionnerCommande(MouseEvent e) {
        Commande c = tableCommandes.getSelectionModel().getSelectedItem();
        if (c != null && e.getClickCount() == 2) {
            // Double-clic : afficher le détail
            StringBuilder sb = new StringBuilder("📋 Détail commande #" + c.getId() + "\n\n");
            commandeDAO.listerLignes(c.getId()).forEach(lc ->
                sb.append(lc.getNomProduit())
                  .append("  ×").append(lc.getQuantite())
                  .append("  → ").append(String.format("%.0f FCFA", lc.getSousTotal()))
                  .append("\n")
            );
            sb.append("\nTOTAL : ").append(String.format("%.0f FCFA", c.getTotal()));
            Alert alert = new Alert(Alert.AlertType.INFORMATION, sb.toString(), ButtonType.OK);
            alert.setTitle("Détail commande");
            alert.setHeaderText("Commande du " + c.getDate() + " — " + c.getNomClient());
            alert.showAndWait();
        }
    }

    // ── Helpers ─────────────────────────────────────────────
    private void chargerCommandes() {
        commandes.setAll(commandeDAO.listerToutes());
    }

    private void mettreAJourTotal() {
        double total = panier.stream().mapToDouble(LigneCommande::getSousTotal).sum();
        lblTotal.setText(String.format("%.0f FCFA", total));
    }

    private void afficherMessage(String msg, boolean succes) {
        lblMessage.setText(msg);
        lblMessage.setStyle("-fx-text-fill: " + (succes ? "#27ae60" : "#e74c3c") + ";");
    }
}

