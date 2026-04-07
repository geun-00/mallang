package io.mallang.test.product.application.provided.query;

import io.mallang.annotations.UseCaseTest;
import io.mallang.member.application.required.command.SaveMemberPort;
import io.mallang.member.domain.Member;
import io.mallang.product.application.provided.query.GetProductDetailUseCase;
import io.mallang.product.application.provided.query.model.ProductDetailView;
import io.mallang.product.application.required.command.SaveProductPort;
import io.mallang.product.domain.Product;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static io.mallang.assertions.ProductAssertions.isDerivedFrom;
import static io.mallang.fixtures.MemberFixture.generateMember;
import static io.mallang.fixtures.ProductFixture.generateProductWithSeller;
import static org.assertj.core.api.Assertions.assertThat;

@UseCaseTest
@DisplayName("GetProductDetail UseCase")
class GetProductDetailUseCaseTest {

    @Test
    void 상품_상세를_조회할_수_있다(
            @Autowired SaveMemberPort saveMemberPort,
            @Autowired SaveProductPort saveProductPort,
            @Autowired GetProductDetailUseCase getProductDetailUseCase
    ) {

        Member seller = generateMember();
        saveMemberPort.save(seller);

        Product product = generateProductWithSeller(seller.getId());
        saveProductPort.save(product);

        ProductDetailView result = getProductDetailUseCase.get(product.getId().value());

        assertThat(result).isNotNull().satisfies(isDerivedFrom(product, seller));
    }
}
