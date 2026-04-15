
package com.ecommerce.dao;

import com.ecommerce.model.Commande;
import com.ecommerce.model.LigneCommande;
import java.sql.*;
import java.util.List;

public class CommandeDAO {
    public void saveCommande(Commande commande) {
        String queryCmd = "INSERT INTO commande (total, id_client) VALUES (?, ?)";
        String queryLigne = "INSERT INTO ligne_commande (id_commande, id_produit, quantite, sous_total) VALUES (?, ?, ?, ?)";
        String queryUpdateStock = "UPDATE produit SET stock = stock - ? WHERE id = ?";

        Connection conn = null;
        try {
            conn = DatabaseConnection.getConnection();
            conn.setAutoCommit(false); // Début de la transaction

            // 1. Insertion de la commande
            try (PreparedStatement pstmt = conn.prepareStatement(queryCmd, Statement.RETURN_GENERATED_KEYS)) {
                pstmt.setDouble(1, commande.getTotal());
                pstmt.setInt(2, commande.getClient().getId());
                pstmt.executeUpdate();

                ResultSet rs = pstmt.getGeneratedKeys();
                if (rs.next()) {
                    int idCommande = rs.getInt(1);
                    commande.setId(idCommande);

                    // 2. Insertion des lignes de commande et mise à jour du stock
                    try (PreparedStatement pstmtLigne = conn.prepareStatement(queryLigne);
                         PreparedStatement pstmtStock = conn.prepareStatement(queryUpdateStock)) {
                        
                        for (LigneCommande ligne : commande.getLignes()) {
                            // Ligne de commande
                            pstmtLigne.setInt(1, idCommande);
                            pstmtLigne.setInt(2, ligne.getProduit().getId());
                            pstmtLigne.setInt(3, ligne.getQuantite());
                            pstmtLigne.setDouble(4, ligne.getSousTotal());
                            pstmtLigne.addBatch();

                            // Mise à jour stock
                            pstmtStock.setInt(1, ligne.getQuantite());
                            pstmtStock.setInt(2, ligne.getProduit().getId());
                            pstmtStock.addBatch();
                        }
                        pstmtLigne.executeBatch();
                        pstmtStock.executeBatch();
                    }
                }
            }
            conn.commit(); // Fin de la transaction
        } catch (SQLException e) {
            if (conn != null) {
                try { conn.rollback(); } catch (SQLException ex) { ex.printStackTrace(); }
            }
            e.printStackTrace();
        } finally {
            if (conn != null) {
                try { conn.close(); } catch (SQLException e) { e.printStackTrace(); }
            }
        }
    }
}
