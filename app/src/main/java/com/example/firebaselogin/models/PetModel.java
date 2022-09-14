package com.example.firebaselogin.models;

import java.util.Arrays;
import java.util.List;

public class PetModel {

    private String nameOfPet;
    private String species;
    private int age;
    private boolean isVaccinated;
    private boolean isNeutered;

    

    public void setNameOfPet(String nameOfPet) {
        this.nameOfPet = nameOfPet;
    }

    public void setSpecies(String species) {
        this.species = species;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public void setVaccinated(boolean vaccinated) {
        isVaccinated = vaccinated;
    }

    public void setNeutered(boolean neutered) {
        isNeutered = neutered;
    }

    public String getNameOfPet() {
        return nameOfPet;
    }

    public String getSpecies() {
        return species;
    }

    public int getAge() {
        return age;
    }

    public boolean isVaccinated() {
        return isVaccinated;
    }

    public boolean isNeutered() {
        return isNeutered;
    }


}
