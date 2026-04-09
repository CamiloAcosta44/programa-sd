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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class ProductServiceImp implements ProductService {

    private static final Logger log = LoggerFactory.getLogger(ProductServiceImp.class);

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
        log.info("Consultando productos - página: {}, tamaño: {}", page, size);

        int safeSize = Math.min(size, 100);
        Pageable pageable = PageRequest.of(page, safeSize);

        Page<ProductDTO> result = productRepository.findAll(PageRequest.of(page, safeSize))
                .map(product -> {
                    ProductDTO dto = new ProductDTO();
                    dto.setName(product.getName());
                    dto.setPrice(product.getPrice());
                    dto.setProgram(program);
                    return dto;
                });
        log.info("Retornando {} productos de {} totales", result.getNumberOfElements(), result.getTotalElements());
        return result;
    }

    @Override
    public ProductResponseDTO saveProduct(ProductRequestDTO requestDTO) {
        log.info("Creando producto: nombre='{}', precio={}", requestDTO.getName(), requestDTO.getPrice());
        try {
            Product newProduct = new Product(null, requestDTO.getName(), requestDTO.getPrice());
            Product saved = productRepository.save(newProduct);
            log.info("Producto creado con ID={} en réplica {}", saved.getId(), program);

            ProductResponseDTO responseDTO = new ProductResponseDTO();
            responseDTO.setId(saved.getId());
            responseDTO.setName(saved.getName());
            responseDTO.setPrice(saved.getPrice());
            responseDTO.setMessage("Producto agregado exitosamente - réplica: " + program);
            return responseDTO;
        } catch (Exception e) {
            log.error("Error al guardar producto '{}': {}", requestDTO.getName(), e.getMessage(), e);
            throw e;
        }
    }
}
