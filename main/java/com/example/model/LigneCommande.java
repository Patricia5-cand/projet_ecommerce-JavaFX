package com.model;

public class LigneCommande {
    private int id;
    private int idCommande;
    private int idProduit;
    private int quantite;
    private double sousTotal;
    private Produit produit;

    public LigneCommande() {}

    public LigneCommande(int idCommande, int idProduit, int quantite, double sousTotal) {
        this.idCommande = idCommande;
        this.idProduit  = idProduit;
        this.quantite   = quantite;
        this.sousTotal  = sousTotal;
    }

    public LigneCommande(Produit produit, int quantite) {
        this.produit   = produit;
        this.idProduit = produit.getId();
        this.quantite  = quantite;
        this.sousTotal = produit.getPrix() * quantite;
    }

    // Getters / Setters
    public int getId()              { return id; }
    public void setId(int id)       { this.id = id; }

    public int getIdCommande()                  { return idCommande; }
    public void setIdCommande(int idCommande)   { this.idCommande = idCommande; }

    public int getIdProduit()                   { return idProduit; }
    public void setIdProduit(int idProduit)     { this.idProduit = idProduit; }

    public int getQuantite()                    { return quantite; }
    public void setQuantite(int quantite)       { this.quantite = quantite; }

    public double getSousTotal()                    { return sousTotal; }
    public void setSousTotal(double sousTotal)      { this.sousTotal = sousTotal; }

    public Produit getProduit()                 { return produit; }
    public void setProduit(Produit produit)     { this.produit = produit; }

    public String getProduitNom() {
        return produit != null ? produit.getNom() : "Produit #" + idProduit;
    }

    public double getPrixUnitaire() {
        return produit != null ? produit.getPrix() : 0;
    }
}


