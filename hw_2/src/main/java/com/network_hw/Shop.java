package com.network_hw;

import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;

import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
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
        addProduct("Ham", "rEaL mEaT");
        addProduct("Salt", "must have");
    }

    @PostMapping(value = "/products", consumes = {"application/json"})
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
        storage.get(id).updateProduct(p.getName(), p.getDescription());
    }

    // Aggregate root
    // tag::get-aggregate-root[]
    @GetMapping("/products")
    public List<Product> getAllProducts() {
        return new ArrayList<>(storage.values());
    }
    // end::get-aggregate-root[]

    @PostMapping("/productsLogo")
    public ResponseEntity<String> uploadFile(
            @RequestParam("name") String name,
            @RequestParam("description") String description,
            @RequestParam("image") MultipartFile multipartFile
            ) throws IOException {

        String fileName = StringUtils.cleanPath(multipartFile.getOriginalFilename());

        System.out.println(multipartFile.getSize());

        Files.copy(multipartFile.getInputStream(), Paths.get("src/main/resources/" + fileName));

        String response = fileName;
        Product p = new Product(name, description);
        p.setLogo(fileName);
        storage.put(p.getId(), p);

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/productsLogo/{id}")
    @ResponseBody
    public ResponseEntity<Resource> getProductByIdWithFile(@PathVariable Integer id) throws MalformedURLException {
        Product p = storage.get(id);
        Resource resource = null;

        File f = new File("src/main/resources/" + p.getLogo());
        resource = new UrlResource(f.toURI());

        String contentType = "application/octet-stream";
        String headerValue = "attachment; filename=\"" + resource.getFilename() + "\"";

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(contentType))
                .header(HttpHeaders.CONTENT_DISPOSITION, headerValue)
                .body(resource);
    }

}
