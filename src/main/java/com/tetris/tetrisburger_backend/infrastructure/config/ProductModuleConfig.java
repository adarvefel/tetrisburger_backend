package com.tetris.tetrisburger_backend.infrastructure.config;


import com.tetris.tetrisburger_backend.application.usecase.product.AdjustProductStockUseCase;
import com.tetris.tetrisburger_backend.application.usecase.product.CreateProductUseCase;
import com.tetris.tetrisburger_backend.application.usecase.product.DeleteProductUseCase;
import com.tetris.tetrisburger_backend.application.usecase.product.GetProductByIdUseCase;
import com.tetris.tetrisburger_backend.application.usecase.product.ListProductsUseCase;
import com.tetris.tetrisburger_backend.application.usecase.product.SearchProductsUseCase;
import com.tetris.tetrisburger_backend.application.usecase.product.SetProductAvailabilityUseCase;
import com.tetris.tetrisburger_backend.application.usecase.product.UpdateProductUseCase;
import com.tetris.tetrisburger_backend.domain.port.out.ProductRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Registra casos de uso si no están anotados con @Service.
 * Los mappers de MapStruct se registran solos con componentModel="spring".
 */
@Configuration
public class ProductModuleConfig {

    @Bean
    public CreateProductUseCase createProductUseCase(ProductRepository repo) {
        return new CreateProductUseCase(repo);
    }

    @Bean
    public UpdateProductUseCase updateProductUseCase(ProductRepository repo) {
        return new UpdateProductUseCase(repo);
    }

    @Bean
    public DeleteProductUseCase deleteProductUseCase(ProductRepository repo) {
        return new DeleteProductUseCase(repo);
    }

    @Bean
    public GetProductByIdUseCase getProductByIdUseCase(ProductRepository repo) {
        return new GetProductByIdUseCase(repo);
    }

    @Bean
    public ListProductsUseCase listProductsUseCase(ProductRepository repo) {
        return new ListProductsUseCase(repo);
    }

    @Bean
    public SearchProductsUseCase searchProductsUseCase(ProductRepository repo) {
        return new SearchProductsUseCase(repo);
    }

    @Bean
    public SetProductAvailabilityUseCase setProductAvailabilityUseCase(ProductRepository repo) {
        return new SetProductAvailabilityUseCase(repo);
    }

    @Bean
    public AdjustProductStockUseCase adjustProductStockUseCase(ProductRepository repo) {
        return new AdjustProductStockUseCase(repo);
    }
}