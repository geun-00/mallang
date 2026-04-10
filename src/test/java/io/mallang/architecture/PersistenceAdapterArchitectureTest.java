package io.mallang.architecture;

import com.tngtech.archunit.core.domain.JavaClasses;
import com.tngtech.archunit.junit.ArchTest;
import io.mallang.annotations.ArchitectureTest;
import org.springframework.stereotype.Repository;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.classes;
import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses;

@ArchitectureTest
class PersistenceAdapterArchitectureTest {

    @ArchTest
    void 영속성_어댑터는_application_service_구현체를_직접_의존하면_안_된다(JavaClasses classes) {
        noClasses()
                .that().resideInAPackage("..adapter.persistence..")
                .should().dependOnClassesThat()
                .resideInAPackage("..application.service..")
                .check(classes);
    }

    @ArchTest
    void 영속성_어댑터_구현체는_required_port를_의존해야_한다(JavaClasses classes) {
        classes()
                .that().resideInAPackage("..adapter.persistence.*")
                .and().haveSimpleNameEndingWith("PersistenceAdapter")
                .should().dependOnClassesThat()
                .resideInAPackage("..application.required..")
                .check(classes);
    }

    @ArchTest
    void 영속성_어댑터_구현체_이름은_PersistenceAdapter로_끝나야_한다(JavaClasses classes) {
        classes()
                .that().resideInAPackage("..adapter.persistence.*")
                .and().areAnnotatedWith(Repository.class)
                .and().areNotInterfaces()
                .should().haveSimpleNameEndingWith("PersistenceAdapter")
                .check(classes);
    }

    @ArchTest
    void JPA_리포지토리_이름은_Repository로_끝나야_한다(JavaClasses classes) {
        classes()
                .that().resideInAPackage("..adapter.persistence.jpa")
                .and().areInterfaces()
                .should().haveSimpleNameEndingWith("Repository")
                .check(classes);
    }
}
