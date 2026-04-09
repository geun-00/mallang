package io.mallang.architecture;

import com.tngtech.archunit.core.domain.JavaClasses;
import com.tngtech.archunit.core.importer.ImportOption;
import com.tngtech.archunit.junit.AnalyzeClasses;
import com.tngtech.archunit.junit.ArchTest;
import org.springframework.beans.factory.annotation.Autowired;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.fields;
import static com.tngtech.archunit.library.Architectures.layeredArchitecture;
import static com.tngtech.archunit.library.dependencies.SlicesRuleDefinition.slices;

@AnalyzeClasses(
        packages = "io.mallang",
        importOptions = ImportOption.DoNotIncludeTests.class
)
class GlobalArchitectureTest {

    @ArchTest
    void 레이어검사_모든_계층의_의존흐름은_안쪽으로만_향해야한다(JavaClasses classes) {
        layeredArchitecture()
                .consideringAllDependencies()

                .layer("Adapter").definedBy("io.mallang..adapter..", "io.mallang.security..")
                .layer("Application").definedBy("io.mallang..application..")
                .layer("Domain").definedBy("io.mallang..domain..")

                .whereLayer("Adapter").mayNotBeAccessedByAnyLayer()
                .whereLayer("Application").mayOnlyBeAccessedByLayers("Adapter")
                .whereLayer("Domain").mayOnlyBeAccessedByLayers("Application", "Adapter")
                .check(classes);
    }

    @ArchTest
    void 필드주입검사_모든_클래스는_필드주입을_사용하지_않는다(JavaClasses classes) {
        fields()
                .that().areDeclaredInClassesThat().resideInAPackage("io.mallang..")
                .should().notBeAnnotatedWith(Autowired.class)
                .check(classes);
    }

    @ArchTest
    void 순환참조검사_상위_패키지는_순환_의존성을_가지면_안_된다(JavaClasses classes) {
        slices()
                .matching("io.mallang.(*)..")
                .should().beFreeOfCycles()
                .check(classes);
    }
}
