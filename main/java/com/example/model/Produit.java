package com.model;

public class Produit {
    private int id;
    private String nom;
    private double prix;
    private int stock;

    public Produit() {}

    public Produit(int id, String nom, double prix, int stock) {
        this.id    = id;
        this.nom   = nom;
        this.prix  = prix;
        this.stock = stock;
    }

    public Produit(String nom, double prix, int stock) {
        this.nom   = nom;
        this.prix  = prix;
        this.stock = stock;
    }

    // Getters / Setters
    public int getId()              { return id; }
    public void setId(int id)       { this.id = id; }

    public String getNom()              { return nom; }
    public void setNom(String nom)      { this.nom = nom; }

    public double getPrix()                 { return prix; }
    public void setPrix(double prix)        { this.prix = prix; }

    public int getStock()               { return stock; }
    public void setStock(int stock)     { this.stock = stock; }

    @Override
    public String toString() {
        return nom + " (" + String.format("%.2f", prix) + " FCFA)";
    }
}


