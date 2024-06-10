package edu.badpals.repositorio;

import edu.badpals.domain.Farmer;
import edu.badpals.domain.Fruit;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;

import java.util.List;
import java.util.Optional;

@ApplicationScoped
public class ServiceFruit {

    public List<Fruit> list(){

        List<Fruit> listaDeFrutas = Fruit.listAll();

        return listaDeFrutas;
    }

    @Transactional
    public void add(Fruit fruit){

        Optional<Farmer> posibleFarmero = Farmer.find("name = ?1", fruit.getFarmer().getName()).firstResultOptional();

        Farmer farmero = null;

        if (posibleFarmero.isPresent()){
            farmero = posibleFarmero.get();
        } else {

            Farmer nuevoFarmero = new Farmer(fruit.getFarmer().getName(), fruit.getFarmer().getLocation());
            nuevoFarmero.persist();

            farmero = nuevoFarmero;
        }

        Fruit fruta = new Fruit(fruit.getName(),fruit.getDescription(),farmero);
        fruta.persist();
    }

    @Transactional
    public void remove(String nombre) {

        Optional<Fruit> frutaParaSerEliminada = Fruit.find("name = ?1", nombre).firstResultOptional();

        frutaParaSerEliminada.get().delete();

    }

    public Optional<Fruit> getFruit(String nombre){
        Optional<Fruit> fruit = Fruit.find("name = ?1", nombre).firstResultOptional();
        return fruit.isPresent() ? Optional.of(fruit.get()) : Optional.empty();
    }

}
