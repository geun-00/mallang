package io.mallang.test.product.application.provided.command;

import io.mallang.UseCaseTest;
import io.mallang.product.application.provided.command.RegisterProductUseCase;
import io.mallang.product.application.provided.command.model.RegisterProductCommand;
import io.mallang.product.application.provided.command.model.RegisterProductResult;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static io.mallang.fixtures.ProductFixture.generateRegisterProductCommand;
import static io.mallang.fixtures.ProductFixture.generateRegisterProductCommandWithImages;
import static org.assertj.core.api.Assertions.assertThat;

@UseCaseTest
@DisplayName("RegisterProduct UseCase")
class RegisterProductUseCaseTest {

    @Test
    void 상품_등록_성공_시_ProductId를_반환한다(
            @Autowired RegisterProductUseCase registerProductUseCase
    ) {
        // given
        RegisterProductCommand command = generateRegisterProductCommand();

        // when
        RegisterProductResult result = registerProductUseCase.register(command);

        // then
        assertThat(result.productId()).isNotNull();
    }

    @Test
    void 이미지_없이_상품을_등록할_수_있다(
            @Autowired RegisterProductUseCase registerProductUseCase
    ) {
        // given
        RegisterProductCommand command = generateRegisterProductCommand();

        // when & then
        assertThat(registerProductUseCase.register(command).productId()).isNotNull();
    }

    @Test
    void 이미지와_함께_상품을_등록할_수_있다(
            @Autowired RegisterProductUseCase registerProductUseCase
    ) {
        // given
        RegisterProductCommand command = generateRegisterProductCommandWithImages();

        // when & then
        assertThat(registerProductUseCase.register(command).productId()).isNotNull();
    }
}