package io.mallang.architecture;

import com.tngtech.archunit.core.domain.JavaClasses;
import com.tngtech.archunit.core.importer.ImportOption;
import com.tngtech.archunit.junit.AnalyzeClasses;
import com.tngtech.archunit.junit.ArchTest;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.classes;
import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses;

@AnalyzeClasses(
        packages = "io.mallang",
        importOptions = ImportOption.DoNotIncludeTests.class
)
class RequiredPortArchitectureTest {

    @ArchTest
    void 커맨드_포트는_Port로_끝나고_인터페이스여야_한다(JavaClasses classes) {
        classes()
                .that().resideInAPackage("..application.required.command")
                .should().haveSimpleNameEndingWith("Port")
                .andShould().beInterfaces()
                .check(classes);
    }

    @ArchTest
    void 쿼리_포트는_Port로_끝나고_인터페이스여야_한다(JavaClasses classes) {
        classes()
                .that().resideInAPackage("..application.required.query")
                .should().haveSimpleNameEndingWith("Port")
                .andShould().beInterfaces()
                .check(classes);
    }

    @ArchTest
    void required_port는_adapter를_직접_의존하면_안_된다(JavaClasses classes) {
        noClasses()
                .that().resideInAPackage("..application.required..")
                .should().dependOnClassesThat()
                .resideInAnyPackage("io.mallang..adapter..", "io.mallang.security..")
                .check(classes);
    }
}
