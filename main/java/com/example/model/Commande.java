package com.model;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class Commande {
    private int id;
    private LocalDate date;
    private double total;
    private int idClient;
    private Client client;
    private List<LigneCommande> lignes;

    public Commande() {
        this.lignes = new ArrayList<>();
        this.date   = LocalDate.now();
    }

    public Commande(int id, LocalDate date, double total, int idClient) {
        this.id       = id;
        this.date     = date;
        this.total    = total;
        this.idClient = idClient;
        this.lignes   = new ArrayList<>();
    }

    public void calculerTotal() {
        this.total = lignes.stream()
                .mapToDouble(LigneCommande::getSousTotal)
                .sum();
    }

    // Getters / Setters
    public int getId()              { return id; }
    public void setId(int id)       { this.id = id; }

    public LocalDate getDate()              { return date; }
    public void setDate(LocalDate date)     { this.date = date; }

    public double getTotal()                { return total; }
    public void setTotal(double total)      { this.total = total; }

    public int getIdClient()                    { return idClient; }
    public void setIdClient(int idClient)       { this.idClient = idClient; }

    public Client getClient()               { return client; }
    public void setClient(Client client)    { this.client = client; }

    public List<LigneCommande> getLignes()              { return lignes; }
    public void setLignes(List<LigneCommande> lignes)   { this.lignes = lignes; }

    public String getClientNom() {
        return client != null ? client.toString() : "Client #" + idClient;
    }
}

