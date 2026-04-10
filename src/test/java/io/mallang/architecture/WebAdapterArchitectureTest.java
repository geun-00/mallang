package io.mallang.architecture;

import com.tngtech.archunit.core.domain.JavaClasses;
import com.tngtech.archunit.junit.ArchTest;
import io.mallang.annotations.ArchitectureTest;
import org.springframework.web.bind.annotation.RestController;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.classes;
import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses;

@ArchitectureTest
class WebAdapterArchitectureTest {

    @ArchTest
    void 웹_어댑터는_application_service_구현체를_직접_의존하면_안_된다(JavaClasses classes) {
        noClasses()
                .that().resideInAPackage("..adapter.web")
                .should().dependOnClassesThat()
                .resideInAPackage("..application.service..")
                .check(classes);
    }

    @ArchTest
    void 웹_어댑터는_UseCase_인터페이스를_의존해야_한다(JavaClasses classes) {
        classes()
                .that().resideInAPackage("..adapter.web")
                .and().haveSimpleNameEndingWith("Api")
                .should().dependOnClassesThat()
                .resideInAPackage("..application.provided..")
                .check(classes);
    }

    @ArchTest
    void 웹_어댑터_클래스명은_Api로_끝나야_한다(JavaClasses classes) {
        classes()
                .that().resideInAPackage("..adapter.web")
                .and().areTopLevelClasses()
                .and().doNotHaveSimpleName("GlobalExceptionHandler")
                .should().haveSimpleNameEndingWith("Api")
                .check(classes);
    }

    @ArchTest
    void 웹_어댑터는_RestController_여야_한다(JavaClasses classes) {
        classes()
                .that().resideInAPackage("..adapter.web")
                .and().haveSimpleNameEndingWith("Api")
                .should().beAnnotatedWith(RestController.class)
                .check(classes);
    }
}
