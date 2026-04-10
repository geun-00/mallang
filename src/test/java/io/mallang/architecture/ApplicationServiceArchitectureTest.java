package io.mallang.architecture;

import com.tngtech.archunit.core.domain.JavaClass;
import com.tngtech.archunit.core.domain.JavaClasses;
import com.tngtech.archunit.junit.ArchTest;
import com.tngtech.archunit.lang.ArchCondition;
import com.tngtech.archunit.lang.ConditionEvents;
import com.tngtech.archunit.lang.SimpleConditionEvent;
import io.mallang.annotations.ArchitectureTest;
import org.springframework.stereotype.Service;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.classes;
import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses;

@ArchitectureTest
class ApplicationServiceArchitectureTest {

    @ArchTest
    void 애플리케이션_서비스는_adapter를_직접_의존하면_안_된다(JavaClasses classes) {
        noClasses()
                .that().resideInAPackage("..application.service..")
                .should().dependOnClassesThat()
                .resideInAnyPackage("io.mallang..adapter..", "io.mallang.security..")
                .check(classes);
    }

    @ArchTest
    void 커맨드_서비스_이름은_CommandService로_끝나야_한다(JavaClasses classes) {
        classes()
                .that().resideInAPackage("..application.service.command")
                .should().haveSimpleNameEndingWith("CommandService")
                .check(classes);
    }

    @ArchTest
    void 쿼리_서비스_이름은_QueryService로_끝나야_한다(JavaClasses classes) {
        classes()
                .that().resideInAPackage("..application.service.query")
                .should().haveSimpleNameEndingWith("QueryService")
                .check(classes);
    }

    @ArchTest
    void 애플리케이션_서비스는_Service_어노테이션이_있어야_한다(JavaClasses classes) {
        classes()
                .that().resideInAPackage("..application.service..")
                .should().beAnnotatedWith(Service.class)
                .check(classes);
    }

    @ArchTest
    void 애플리케이션_서비스는_UseCase_인터페이스를_구현해야_한다(JavaClasses classes) {
        classes()
                .that().resideInAPackage("..application.service..")
                .should(new ArchCondition<>("application.provided 패키지의 UseCase 인터페이스를 구현해야 한다") {
                    @Override
                    public void check(JavaClass item, ConditionEvents events) {
                        boolean implementsUseCase = item.getAllRawInterfaces()
                                                         .stream()
                                                         .anyMatch(it -> it.getPackageName().contains(".application.provided."));

                        events.add(new SimpleConditionEvent(
                                item,
                                implementsUseCase,
                                item.getName() + " does not implement any provided use case interface"
                        ));
                    }
                })
                .check(classes);
    }
}
