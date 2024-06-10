package edu.badpals;

import edu.badpals.domain.Farmer;
import edu.badpals.domain.Fruit;
import edu.badpals.repositorio.ServiceFruit;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Optional;

@QuarkusTest
@Transactional
public class ServiceTest {

    @Inject
    ServiceFruit service;

    @Test
    public void test_mapping_Fruit() {
        Fruit fruta = Fruit.findById(1000);
        Assertions.assertThat(fruta).isNotNull();
        Assertions.assertThat(fruta.toString()).containsIgnoringCase("Apple"); // item_name
        Assertions.assertThat(fruta.toString()).contains("Winter fruit"); // item_quality
        Assertions.assertThat(fruta.getId()).isEqualTo(1000);
    }


    // @Test de jupiter, no el de junit
    @Test
    public void testList() {
        Assertions.assertThat(service.list()).hasSize(2);
    }


    @Test
    public void containsTest() {
        Assertions.assertThat(service.list().stream().anyMatch(f -> f.getName().equals("Apple"))).isTrue();
    }


    @Test
    public void addTest() {
        service.add(new Fruit("Banana",
                "And an attached Gorilla",
                new Farmer("Farmer Rick", "Sa Pobla")));
        Assertions.assertThat(service.list()).hasSize(3);
        Assertions.assertThat(service.list().stream().anyMatch(f -> f.getName().equals("Banana"))).isTrue();
        Assertions.assertThat(Farmer.count()).isEqualTo(2L);

        // handmade rollback gracias al antipatron ActiveRecord ;)
        Fruit fruit = Fruit.find("name", "Banana").firstResult();
        fruit.delete();
        Assertions.assertThat(Fruit.count()).isEqualTo(2L);
        Assertions.assertThat(Farmer.count()).isEqualTo(2L);
    }


    // CORREGIR ESTE TEST PORQUE ES NUEVO
    @Test
    public void addFarmerTest() {
        service.add(new Fruit("Navel Late",
                "Crist en pel",
                new Farmer("Jerrys Bites", "Es Pla")));
        Assertions.assertThat(service.list()).hasSize(3);
        Assertions.assertThat(service.list().stream().anyMatch(f -> f.getName().equals("Navel Late"))).isTrue();
        // hay un nuevo registro en la tabla Farmer
        Assertions.assertThat(Farmer.count()).isEqualTo(3L);

        // handmade rollback gracias al antipatron ActiveRecord ;)
        Fruit fruit = Fruit.find("name", "Navel Late").firstResult();
        fruit.delete();
        Assertions.assertThat(Fruit.count()).isEqualTo(2L);
        Farmer farmer = Farmer.find("name", "Jerrys Bites").firstResult();
        farmer.delete();
        Assertions.assertThat(Farmer.count()).isEqualTo(2L);
    }


    @Test
    public void removeTest(){
        service.remove("Apple");
        Assertions.assertThat(service.list()).hasSize(1);
        Assertions.assertThat(service.list().stream().anyMatch(f -> f.getName().equals("Apple"))).isFalse();
        Assertions.assertThat(Fruit.count()).isEqualTo(1L);
        Assertions.assertThat(Farmer.count()).isEqualTo(2L);

        Optional<Farmer> supplier = Farmer.find("name", "Farmer Rick").firstResultOptional();
        Assertions.assertThat(supplier).isNotEmpty();

        // handmade rollback gracias al antipatron ActiveRecord ;)
        Fruit fruit = new Fruit("Apple", "Winter fruit", supplier.get());
        fruit.persist();
        Assertions.assertThat(Fruit.count()).isEqualTo(2);
    }


    @Test
    public void getFruitTest() {
        Assertions.assertThat(service.getFruit("Apple")).get().hasFieldOrPropertyWithValue("name", "Apple").hasFieldOrPropertyWithValue("description", "Winter fruit").extracting("farmer").toString().compareTo("Farmer Rick, Sa Pobla");
        Assertions.assertThat(service.getFruit("Mandarina")).isEmpty();
    }

}