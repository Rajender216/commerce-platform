package com.rcp.commerce.catalog.adapter.in.web;

import static org.hamcrest.Matchers.endsWith;
import static org.hamcrest.Matchers.matchesPattern;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.rcp.commerce.catalog.application.port.in.CreateProductCommand;
import com.rcp.commerce.catalog.application.port.in.CreateProductUseCase;
import com.rcp.commerce.catalog.application.port.in.GetProductUseCase;
import com.rcp.commerce.catalog.application.service.ProductNotFoundException;
import com.rcp.commerce.catalog.configuration.CatalogServiceProperties;
import com.rcp.commerce.catalog.domain.Product;
import com.rcp.commerce.catalog.domain.ProductDescription;
import com.rcp.commerce.catalog.domain.ProductId;
import com.rcp.commerce.catalog.domain.ProductName;
import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.JacksonJsonHttpMessageConverter;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;

class ProductControllerTest {

    private static final String CORRELATION_HEADER = "X-Correlation-ID";
    private static final Clock CLOCK = Clock.fixed(Instant.parse("2026-07-22T00:00:00Z"), ZoneOffset.UTC);

    private MockMvc mockMvc;
    private FakeProductUseCases useCases;

    @BeforeEach
    void setUp() {
        useCases = new FakeProductUseCases(CLOCK);
        ProductController controller = new ProductController(useCases, useCases);
        GlobalExceptionHandler exceptionHandler = new GlobalExceptionHandler(CLOCK);
        CorrelationIdFilter correlationIdFilter = new CorrelationIdFilter(
                new CatalogServiceProperties(new CatalogServiceProperties.Correlation(CORRELATION_HEADER, 128)));

        LocalValidatorFactoryBean validator = new LocalValidatorFactoryBean();
        validator.afterPropertiesSet();

        mockMvc = MockMvcBuilders.standaloneSetup(controller)
                .setControllerAdvice(exceptionHandler)
                .addFilters(correlationIdFilter)
                .setValidator(validator)
                .setMessageConverters(new JacksonJsonHttpMessageConverter())
                .build();
    }

    @Test
    void validCreateRequestReturnsCreatedWithLocationHeader() throws Exception {
        mockMvc.perform(
                        post("/api/v1/products")
                                .header(CORRELATION_HEADER, "test-correlation-1")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(
                                        """
                                {
                                  "name": "  Draft product  ",
                                  "description": "  Draft description  "
                                }
                                """))
                .andExpect(status().isCreated())
                .andExpect(header().string(CORRELATION_HEADER, "test-correlation-1"))
                .andExpect(header().string("Location", endsWith("/api/v1/products/" + useCases.lastCreatedId)))
                .andExpect(jsonPath("$.id").value(useCases.lastCreatedId.toString()))
                .andExpect(jsonPath("$.name").value("Draft product"))
                .andExpect(jsonPath("$.description").value("Draft description"))
                .andExpect(jsonPath("$.status").value("DRAFT"))
                .andExpect(jsonPath("$.version").value(0));
    }

    @Test
    void invalidNameReturnsBadRequestProblem() throws Exception {
        mockMvc.perform(
                        post("/api/v1/products")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(
                                        """
                                {
                                  "name": "   ",
                                  "description": "Draft description"
                                }
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(header().string(CORRELATION_HEADER, matchesPattern("[0-9a-fA-F-]{36}")))
                .andExpect(jsonPath("$.type").value("https://api.rcp-commerce.local/problems/validation-failed"))
                .andExpect(jsonPath("$.title").value("Validation failed"))
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.code").value("VALIDATION_FAILED"))
                .andExpect(jsonPath("$.correlationId").exists())
                .andExpect(jsonPath("$.validationErrors[0].field").value("name"));
    }

    @Test
    void overlongNameReturnsBadRequestProblem() throws Exception {
        mockMvc.perform(post("/api/v1/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(
                                """
                        {
                          "name": "%s",
                          "description": "Draft description"
                        }
                        """
                                        .formatted("a".repeat(ProductName.MAX_LENGTH + 1))))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("VALIDATION_FAILED"))
                .andExpect(jsonPath("$.validationErrors[0].field").value("name"));
    }

    @Test
    void overlongDescriptionReturnsBadRequestProblem() throws Exception {
        mockMvc.perform(post("/api/v1/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(
                                """
                        {
                          "name": "Draft product",
                          "description": "%s"
                        }
                        """
                                        .formatted("a".repeat(ProductDescription.MAX_LENGTH + 1))))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("VALIDATION_FAILED"))
                .andExpect(jsonPath("$.validationErrors[0].field").value("description"));
    }

    @Test
    void getExistingProductReturnsOk() throws Exception {
        Product product =
                useCases.createProduct(new CreateProductCommand(new ProductName("Draft"), ProductDescription.empty()));

        mockMvc.perform(get("/api/v1/products/{productId}", product.id().value()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(product.id().value().toString()))
                .andExpect(jsonPath("$.name").value("Draft"))
                .andExpect(jsonPath("$.status").value("DRAFT"));
    }

    @Test
    void getMissingProductReturnsNotFoundProblem() throws Exception {
        UUID missingId = UUID.randomUUID();

        mockMvc.perform(get("/api/v1/products/{productId}", missingId).header(CORRELATION_HEADER, "lookup-123"))
                .andExpect(status().isNotFound())
                .andExpect(header().string(CORRELATION_HEADER, "lookup-123"))
                .andExpect(jsonPath("$.type").value("https://api.rcp-commerce.local/problems/product-not-found"))
                .andExpect(jsonPath("$.title").value("Product not found"))
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.code").value("PRODUCT_NOT_FOUND"))
                .andExpect(jsonPath("$.correlationId").value("lookup-123"))
                .andExpect(jsonPath("$.detail").value("Product " + missingId + " was not found."));
    }

    @Test
    void invalidCorrelationIdIsReplaced() throws Exception {
        mockMvc.perform(get("/api/v1/products/{productId}", UUID.randomUUID())
                        .header(CORRELATION_HEADER, "x".repeat(129)))
                .andExpect(status().isNotFound())
                .andExpect(header().string(CORRELATION_HEADER, matchesPattern("[0-9a-fA-F-]{36}")));
    }

    static class FakeProductUseCases implements CreateProductUseCase, GetProductUseCase {

        private final Clock clock;
        private final Map<ProductId, Product> products = new LinkedHashMap<>();
        private UUID lastCreatedId;

        FakeProductUseCases(Clock clock) {
            this.clock = clock;
        }

        @Override
        public Product createProduct(CreateProductCommand command) {
            Product product = Product.createDraft(command.name(), command.description(), clock);
            products.put(product.id(), product);
            lastCreatedId = product.id().value();
            return product;
        }

        @Override
        public Product getProduct(ProductId productId) throws ProductNotFoundException {
            Product product = products.get(productId);
            if (product == null) {
                throw new ProductNotFoundException(productId);
            }
            return product;
        }
    }
}
