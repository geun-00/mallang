package io.mallang.architecture;

import com.tngtech.archunit.core.domain.JavaClass;
import com.tngtech.archunit.core.domain.JavaClasses;
import com.tngtech.archunit.junit.ArchTest;
import com.tngtech.archunit.lang.ArchCondition;
import com.tngtech.archunit.lang.ConditionEvents;
import com.tngtech.archunit.lang.SimpleConditionEvent;
import io.mallang.annotations.ArchitectureTest;
import org.springframework.web.bind.annotation.RequestMapping;
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

    @ArchTest
    void 웹_어댑터_엔드포인트는_api_v버전_경로로_시작해야_한다(JavaClasses classes) {
        classes()
                .that().resideInAPackage("..adapter.web")
                .and().haveSimpleNameEndingWith("Api")
                .should(new ArchCondition<>("클래스 레벨 @RequestMapping이 /api/v<버전> 경로로 시작해야 한다") {
                    @Override
                    public void check(JavaClass javaClass, ConditionEvents events) {
                        RequestMapping requestMapping = javaClass.reflect().getAnnotation(RequestMapping.class);
                        
                        boolean satisfied = requestMapping != null
                                && requestMapping.value().length > 0
                                && requestMapping.value()[0].matches("^/api/v\\d+(/.*)?$");

                        String actualPath = requestMapping == null || requestMapping.value().length == 0
                                ? "<missing>"
                                : requestMapping.value()[0];

                        events.add(new SimpleConditionEvent(
                                javaClass,
                                satisfied,
                                javaClass.getName() + " 클래스 레벨 요청 경로 검증 실패 => " + actualPath
                        ));
                    }
                })
                .check(classes);
    }

}
