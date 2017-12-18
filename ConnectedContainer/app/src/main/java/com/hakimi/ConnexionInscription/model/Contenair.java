package com.hakimi.ConnexionInscription.model;

/**
 * Created by hakimi imed on 03/11/2017.
 */

public class Contenair {
    int id;
    String name, zone, joiningDate,etat;
    double volume,poid;

    public Contenair(int id, String name, String zone, String joiningDate,  String etat) {
        this.id = id;
        this.name = name;
        this.zone = zone;
        this.joiningDate = joiningDate;
        this.etat = etat;

    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getZone() {
        return zone;
    }

    public String getJoiningDate() {
        return joiningDate;
    }

    public String getEtat() {
        return etat;
    }




}
