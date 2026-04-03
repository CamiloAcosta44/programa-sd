package co.edu.uptc.api_rest.controller;

import co.edu.uptc.api_rest.dto.ProductDTO;
import co.edu.uptc.api_rest.dto.ProductRequestDTO;
import co.edu.uptc.api_rest.dto.ProductResponseDTO;
import co.edu.uptc.api_rest.service.ProductService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("api/products")
public class ProductController {

    private final ProductService productService;

    @Value("${program.id}")
    private String programId;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @PostMapping
    public ProductResponseDTO createProduct(@RequestBody ProductRequestDTO requestDTO) {
        return productService.saveProduct(requestDTO);
    }

    /**
     * GET /api/products?page=0&size=20
     *
     * Antes:  devolvía los 5 millones de registros de golpe → timeout
     * Ahora:  devuelve solo la página solicitada (máximo 100 por página)
     *
     * Ejemplo de respuesta:
     * {
     *   "content": [...],
     *   "totalElements": 5000000,
     *   "totalPages": 250000,
     *   "number": 0,       ← página actual
     *   "size": 20
     * }
     */
    @GetMapping
    public Page<ProductDTO> getAllProducts(
            @RequestParam(defaultValue = "0")  int page,
            @RequestParam(defaultValue = "20") int size) {
        return productService.getAllProducts(page, size);
    }

    @GetMapping("/info")
    public Map<String, String> info() {
        return Map.of(
            "replica", programId,
            "status", "UP"
        );
    }
}
