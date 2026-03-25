package io.mallang.test.product.adapter.web;

import io.mallang.TestFixture;
import io.mallang.TestFixtureConfiguration;
import io.mallang.product.adapter.web.model.CreateProductRequest;
import io.mallang.product.application.required.query.LoadProductPort;
import io.mallang.product.domain.ProductId;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.ResponseEntity;

import java.net.URI;
import java.util.List;

import static io.mallang.fixtures.ProductFixture.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.FOUND;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Import(TestFixtureConfiguration.class)
@DisplayName("POST /products")
class ProductCommandApi_POST {

    @Test
    void 올바르게_요청하면_201_Created_상태코드를_반환한다(@Autowired TestFixture fixture) {
        // given
        fixture.createMemberThenLogin();
        var request = generateCreateProductRequest();

        // when
        ResponseEntity<Void> response = fixture.registerProduct(request);

        // then
        assertThat(response.getStatusCode()).isEqualTo(CREATED);
    }

    @Test
    void 올바르게_요청하면_식별자가_포함된_Location_헤더를_반환한다(@Autowired TestFixture fixture) {
        // given
        fixture.createMemberThenLogin();
        var request = generateCreateProductRequest();

        // when
        ResponseEntity<Void> response = fixture.registerProduct(request);

        // then
        URI location = response.getHeaders().getLocation();
        assertThat(location).isNotNull();
        assertThat(location.getPath()).startsWith("/products/");
        assertThat(location.getPath().replace("/products/", "")).isNotBlank();
    }

    @Test
    void 올바르게_요청하면_Location_헤더의_식별자로_상품을_조회할_수_있다(
            @Autowired TestFixture fixture,
            @Autowired LoadProductPort loadProductPort
    ) {
        // given
        fixture.createMemberThenLogin();
        var request = generateCreateProductRequest();

        // when
        ResponseEntity<Void> response = fixture.registerProduct(request);

        // then
        String productIdValue = response.getHeaders().getLocation().getPath().substring("/products/".length());
        assertThatCode(() -> loadProductPort.getById(new ProductId(productIdValue)))
                .doesNotThrowAnyException();
    }

    @Test
    void 이미지_없이_등록할_수_있다(@Autowired TestFixture fixture) {
        // given
        fixture.createMemberThenLogin();
        var request = generateCreateProductRequest();

        // when
        ResponseEntity<Void> response = fixture.registerProduct(request);

        // then
        assertThat(response.getStatusCode()).isEqualTo(CREATED);
    }

    @Test
    void 이미지와_함께_등록할_수_있다(@Autowired TestFixture fixture) {
        // given
        fixture.createMemberThenLogin();
        var request = generateCreateProductRequestWithImages();

        // when
        ResponseEntity<Void> response = fixture.registerProduct(request);

        // then
        assertThat(response.getStatusCode()).isEqualTo(CREATED);
    }

    @Test
    void 인증되지_않은_요청이면_로그인_페이지로_리다이렉트한다(@Autowired TestFixture fixture) {
        // given
        var request = generateCreateProductRequest();

        // when
        ResponseEntity<Void> response = fixture.unauthenticatedClient().postForEntity(
                "/products",
                request,
                Void.class
        );

        // then
        assertThat(response.getStatusCode()).isEqualTo(FOUND);
    }

    @ParameterizedTest
    @NullSource
    @ValueSource(strings = {" "})
    void name_속성이_지정되지_않으면_400_Bad_Request_상태코드를_반환한다(
            String invalidName,
            @Autowired TestFixture fixture
    ) {
        // given
        fixture.createMemberThenLogin();
        var request = new CreateProductRequest(
                invalidName,
                generateProductDescription(),
                generateProductPriceAmount(),
                generateProductStockQuantity(),
                "FOOD",
                List.of()
        );

        // when
        ResponseEntity<Void> response = fixture.registerProduct(request);

        // then
        assertThat(response.getStatusCode()).isEqualTo(BAD_REQUEST);
    }

    @Test
    void description_속성이_지정되지_않으면_400_Bad_Request_상태코드를_반환한다(
            @Autowired TestFixture fixture
    ) {
        // given
        fixture.createMemberThenLogin();
        var request = new CreateProductRequest(
                generateProductName(),
                null,
                generateProductPriceAmount(),
                generateProductStockQuantity(),
                "FOOD",
                List.of()
        );

        // when
        ResponseEntity<Void> response = fixture.registerProduct(request);

        // then
        assertThat(response.getStatusCode()).isEqualTo(BAD_REQUEST);
    }

    @Test
    void price_속성이_지정되지_않으면_400_Bad_Request_상태코드를_반환한다(
            @Autowired TestFixture fixture
    ) {
        // given
        fixture.createMemberThenLogin();
        var request = new CreateProductRequest(
                generateProductName(),
                generateProductDescription(),
                null,
                generateProductStockQuantity(),
                "FOOD",
                List.of()
        );

        // when
        ResponseEntity<Void> response = fixture.registerProduct(request);

        // then
        assertThat(response.getStatusCode()).isEqualTo(BAD_REQUEST);
    }

    @ParameterizedTest
    @NullSource
    @ValueSource(strings = {" "})
    void category_속성이_지정되지_않으면_400_Bad_Request_상태코드를_반환한다(
            String invalidCategory,
            @Autowired TestFixture fixture
    ) {
        // given
        fixture.createMemberThenLogin();
        var request = new CreateProductRequest(
                generateProductName(),
                generateProductDescription(),
                generateProductPriceAmount(),
                generateProductStockQuantity(),
                invalidCategory,
                List.of()
        );

        // when
        ResponseEntity<Void> response = fixture.registerProduct(request);

        // then
        assertThat(response.getStatusCode()).isEqualTo(BAD_REQUEST);
    }

    @Test
    void 도메인_규칙을_위반하면_400_Bad_Request_상태코드를_반환한다(@Autowired TestFixture fixture) {
        // given
        fixture.createMemberThenLogin();
        var request = new CreateProductRequest(
                generateProductName(),
                generateProductDescription(),
                generateProductPriceAmount(),
                generateProductStockQuantity(),
                "invalid-Category",
                List.of()
        );

        // when
        ResponseEntity<Void> response = fixture.registerProduct(request);

        // then
        assertThat(response.getStatusCode()).isEqualTo(BAD_REQUEST);
    }
}
