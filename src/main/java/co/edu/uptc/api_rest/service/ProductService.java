package co.edu.uptc.api_rest.service;

import co.edu.uptc.api_rest.dto.ProductDTO;
import co.edu.uptc.api_rest.dto.ProductRequestDTO;
import co.edu.uptc.api_rest.dto.ProductResponseDTO;
import org.springframework.data.domain.Page;

public interface ProductService {

    // Devuelve una página de productos en lugar de toda la lista
    Page<ProductDTO> getAllProducts(int page, int size);

    ProductResponseDTO saveProduct(ProductRequestDTO requestDTO);
}
