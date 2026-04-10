package io.mallang.architecture;

import com.tngtech.archunit.core.domain.JavaClasses;
import com.tngtech.archunit.junit.ArchTest;
import io.mallang.annotations.ArchitectureTest;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RestController;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses;

@ArchitectureTest
class DomainArchitectureTest {

    @ArchTest
    void 도메인은_application_adapter_security를_직접_의존하면_안_된다(JavaClasses classes) {
        noClasses()
                .that().resideInAPackage("..domain..")
                .should().dependOnClassesThat()
                .resideInAnyPackage(
                        "io.mallang..application..",
                        "io.mallang..adapter..",
                        "io.mallang.security.."
                )
                .check(classes);
    }

    @ArchTest
    void 도메인은_스프링_JPA_보안_웹_라이브러리를_직접_의존하면_안_된다(JavaClasses classes) {
        noClasses()
                .that().resideInAPackage("..domain..")
                .should().dependOnClassesThat()
                .resideInAnyPackage(
                        "org.springframework..",
                        "jakarta.persistence..",
                        "org.mybatis..",
                        "org.springframework.security.."
                )
                .check(classes);
    }

    @ArchTest
    void 도메인은_스프링_계층_어노테이션을_사용하면_안_된다(JavaClasses classes) {
        noClasses()
                .that().resideInAPackage("..domain..")
                .should().beAnnotatedWith(Service.class)
                .orShould().beAnnotatedWith(Repository.class)
                .orShould().beAnnotatedWith(Component.class)
                .orShould().beAnnotatedWith(RestController.class)
                .check(classes);
    }
}
