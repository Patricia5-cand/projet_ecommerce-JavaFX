
package com.ecommerce.dao;

import com.ecommerce.model.Produit;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ProduitDAO {
    public List<Produit> getAllProduits() {
        List<Produit> produits = new ArrayList<>();
        String query = "SELECT * FROM produit";
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            while (rs.next()) {
                produits.add(new Produit(rs.getInt("id"), rs.getString("nom"), rs.getDouble("prix"), rs.getInt("stock")));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return produits;
    }

    public void saveProduit(Produit produit) {
        String query = "INSERT INTO produit (nom, prix, stock) VALUES (?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setString(1, produit.getNom());
            pstmt.setDouble(2, produit.getPrix());
            pstmt.setInt(3, produit.getStock());
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void updateStock(int id, int newStock) {
        String query = "UPDATE produit SET stock = ? WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setInt(1, newStock);
            pstmt.setInt(2, id);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
