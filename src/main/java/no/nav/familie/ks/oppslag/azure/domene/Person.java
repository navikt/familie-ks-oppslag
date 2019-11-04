package no.nav.familie.ks.oppslag.azure.domene;

import java.util.LinkedList;
import java.util.List;

public class Person {

    private String userPrincipalName;
    private String displayName;
    private String onPremisesSamAccountName; //Navident
    private List<Gruppe> grupper = new LinkedList<>();


    public Person() {}

    public Person(String userPrincipalName, String navn, String onPremisesSamAccountName) {
        this.userPrincipalName = userPrincipalName;
        this.displayName = navn;
        this.onPremisesSamAccountName = onPremisesSamAccountName;
    }

    public String getUserPrincipalName() {
        return userPrincipalName;
    }

    public void setUserPrincipalName(String userPrincipalName) {
        this.userPrincipalName = userPrincipalName;
    }

    public String getNavn() {
        return displayName;
    }

    public void setNavn(String navn) {
        this.displayName = navn;
    }

    public String getOnPremisesSamAccountName() {
        return onPremisesSamAccountName;
    }

    public void setOnPremisesSamAccountName(String onPremisesSamAccountName) {
        this.onPremisesSamAccountName = onPremisesSamAccountName;
    }

    public List<Gruppe> getGrupper() {
        return grupper;
    }

    public void setGrupper(List<Gruppe> grupper) {
        this.grupper = grupper;
    }
}
