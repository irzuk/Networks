package com.network_hw;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;

@RestController
public class Shop {

    private HashMap<Integer, Product> storage = new HashMap<>();

    {  // for tests
        addProduct("Bread", "cutted");
        addProduct("Eggs", "10");
        addProduct("Ham","rEaL mEaT");
        addProduct("Salt","must have");
    }

    @PostMapping(value="/products", consumes={"application/json"})
    public Product addProduct(@RequestBody Product p) {
        storage.put(p.getId(), p);
        return p;
    }

    public int addProduct(String name, String description) {
        Product p = new Product(name, description);
        storage.put(p.getId(), p);
        return p.getId();
    }

    @GetMapping("/products/{id}")
    public Product getProductById(@PathVariable Integer id) {
        return storage.get(id);
    }

    @DeleteMapping("/products/{id}")
    public void deleteProduct(@PathVariable Integer id) {
        storage.remove(id);
    }

    @PutMapping("/products/{id}")
    public void updateProduct(@PathVariable Integer id, @RequestBody Product p) {
        storage.get(id).updateProduct(p.getName(),p.getDescription());
    }

    // Aggregate root
    // tag::get-aggregate-root[]
    @GetMapping("/products")
    public List<Product> getAllProducts() {
        return new ArrayList<>(storage.values());
    }
    // end::get-aggregate-root[]

}
