package co.edu.uptc.api_rest.controller;

import co.edu.uptc.api_rest.dto.ProductDTO;
import co.edu.uptc.api_rest.dto.ProductRequestDTO;
import co.edu.uptc.api_rest.dto.ProductResponseDTO;
import co.edu.uptc.api_rest.service.ProductService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("api/products")
@Tag(name = "Productos", description = "Operaciones de gestión de productos")
public class ProductController {

    private final ProductService productService;

    @Value("${program.id}")
    private String programId;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @Operation(
            summary = "Crear un producto",
            description = "Registra un nuevo producto en la base de datos de la réplica activa"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Producto creado exitosamente",
                    content = @Content(schema = @Schema(implementation = ProductResponseDTO.class))),
            @ApiResponse(responseCode = "400", description = "Datos de entrada inválidos",
                    content = @Content)
    })
    @PostMapping
    public ProductResponseDTO createProduct(@RequestBody ProductRequestDTO requestDTO) {
        return productService.saveProduct(requestDTO);
    }

    @Operation(
            summary = "Listar productos paginados",
            description = "Devuelve una página de productos. Máximo 100 por página para evitar timeouts."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Página de productos retornada exitosamente",
                    content = @Content(schema = @Schema(implementation = Page.class)))
    })
    @GetMapping
    public Page<ProductDTO> getAllProducts(
            @Parameter(description = "Número de página (inicia en 0)", example = "0")
            @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Cantidad de elementos por página (máximo 100)", example = "20")
            @RequestParam(defaultValue = "20") int size) {
        return productService.getAllProducts(page, size);
    }

    @Operation(
            summary = "Info de la réplica",
            description = "Retorna el identificador de la réplica activa y su estado"
    )
    @ApiResponse(responseCode = "200", description = "Información de la réplica",
            content = @Content(schema = @Schema(example = "{\"replica\":\"replica-1\",\"status\":\"UP\"}")))
    @GetMapping("/info")
    public Map<String, String> info() {
        return Map.of(
                "replica", programId,
                "status", "UP"
        );
    }

    @Operation(
            summary = "Generar carga de CPU",
            description = "Genera carga artificial de CPU durante N segundos para pruebas de autoescalado"
    )
    @ApiResponse(responseCode = "200", description = "Carga generada exitosamente")
    @GetMapping("/load")
    public Map<String, Object> generateLoad(
            @Parameter(description = "Segundos de carga", example = "10")
            @RequestParam(defaultValue = "10") int seconds) {
        long endTime = System.currentTimeMillis() + (seconds * 1000L);
        long iterations = 0;
        while (System.currentTimeMillis() < endTime) {
            Math.sqrt(Math.random() * 999999);
            iterations++;
        }
        Map<String, Object> result = new HashMap<>();
        result.put("replica", System.getenv("HOSTNAME"));
        result.put("iterations", iterations);
        result.put("seconds", seconds);
        return result;
    }
}
