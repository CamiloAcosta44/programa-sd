package co.edu.uptc.api_rest.service;

import co.edu.uptc.api_rest.dto.ProductDTO;
import co.edu.uptc.api_rest.dto.ProductRequestDTO;
import co.edu.uptc.api_rest.dto.ProductResponseDTO;
import co.edu.uptc.api_rest.model.Product;
import co.edu.uptc.api_rest.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ProductServiceImp implements ProductService {

    private final ProductRepository productRepository;

    @Value("${program.id}")
    private String program;

    @Autowired
    public ProductServiceImp(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    @Override
    @Transactional(readOnly = true) // optimiza la transacción: solo lectura
    public Page<ProductDTO> getAllProducts(int page, int size) {
        // Limita a máximo 100 por página para proteger la memoria
        int safeSize = Math.min(size, 100);
        Pageable pageable = PageRequest.of(page, safeSize);

        // findAll(Pageable) genera: SELECT * FROM products LIMIT ? OFFSET ?
        // Solo trae 'safeSize' registros a memoria — nunca los 5 millones
        return productRepository.findAll(pageable)
                .map(product -> {
                    ProductDTO dto = new ProductDTO();
                    dto.setName(product.getName());
                    dto.setPrice(product.getPrice());
                    dto.setProgram(program);
                    return dto;
                });
    }

    @Override
    public ProductResponseDTO saveProduct(ProductRequestDTO requestDTO) {
        Product newProduct = new Product(null, requestDTO.getName(), requestDTO.getPrice());
        Product saved = productRepository.save(newProduct);

        ProductResponseDTO responseDTO = new ProductResponseDTO();
        responseDTO.setId(saved.getId());
        responseDTO.setName(saved.getName());
        responseDTO.setPrice(saved.getPrice());
        responseDTO.setMessage("Producto agregado exitosamente - réplica: " + program);

        return responseDTO;
    }
}
