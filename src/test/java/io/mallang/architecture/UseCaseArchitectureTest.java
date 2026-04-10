package io.mallang.architecture;

import com.tngtech.archunit.core.domain.JavaClasses;
import com.tngtech.archunit.core.importer.ImportOption;
import com.tngtech.archunit.junit.AnalyzeClasses;
import com.tngtech.archunit.junit.ArchTest;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.classes;

@AnalyzeClasses(
        packages = "io.mallang",
        importOptions = ImportOption.DoNotIncludeTests.class
)
class UseCaseArchitectureTest {

    @ArchTest
    void 제공_커맨드_유스케이스는_UseCase로_끝나고_인터페이스여야_한다(JavaClasses classes) {
        classes()
                .that().resideInAPackage("..application.provided.command")
                .should().haveSimpleNameEndingWith("UseCase")
                .andShould().beInterfaces()
                .check(classes);
    }

    @ArchTest
    void 제공_쿼리_유스케이스는_UseCase로_끝나고_인터페이스여야_한다(JavaClasses classes) {
        classes()
                .that().resideInAPackage("..application.provided.query")
                .should().haveSimpleNameEndingWith("UseCase")
                .andShould().beInterfaces()
                .check(classes);
    }
}
