package cde.chameleon;

import com.tngtech.archunit.core.importer.ImportOption;
import com.tngtech.archunit.junit.AnalyzeClasses;
import com.tngtech.archunit.junit.ArchTest;
import com.tngtech.archunit.lang.ArchRule;
import com.tngtech.archunit.lang.syntax.ArchRuleDefinition;
import jakarta.servlet.http.HttpFilter;

@AnalyzeClasses(packages = "cde.chameleon", importOptions = ImportOption.DoNotIncludeTests.class)
class ChameleonArchitectureTest {

    @SuppressWarnings("unused")
    @ArchTest
    public static final ArchRule httpFilterMustBeInApi =
            ArchRuleDefinition
                    .classes()
                    .that()
                    .areAssignableTo(HttpFilter.class)
                    .should()
                    .resideInAPackage("..api..");

    @SuppressWarnings("unused")
    @ArchTest
    public static final ArchRule dontUseEntityInDomain =
            ArchRuleDefinition
                    .noClasses()
                    .that()
                    .resideInAPackage("..domain..")
                    .should()
                    .dependOnClassesThat()
                    .resideInAPackage("..entity..")
                    .as("Hexagonal architecture: The domain should be agnostic to the persistence implementation.");

    @SuppressWarnings("unused")
    @ArchTest
    public static final ArchRule dontUseApiInDomain =
            ArchRuleDefinition
                    .noClasses()
                    .that()
                    .resideInAPackage("..domain..")
                    .should()
                    .dependOnClassesThat()
                    .resideInAPackage("..api..")
                    .as("Hexagonal architecture: The domain should be agnostic to the API implementation.");

    @SuppressWarnings("unused")
    @ArchTest
    public static final ArchRule dontUseEntityInApi =
            ArchRuleDefinition
                    .noClasses()
                    .that()
                    .resideInAPackage("..api..")
                    .should()
                    .dependOnClassesThat()
                    .resideInAPackage("..entity..")
                    .as("Hexagonal architecture: The persistence implementation must not be used in the API implementation.");

    @SuppressWarnings("unused")
    @ArchTest
    public static final ArchRule dontUseApiInEntity =
            ArchRuleDefinition
                    .noClasses()
                    .that()
                    .resideInAPackage("..entity..")
                    .should()
                    .dependOnClassesThat()
                    .resideInAPackage("..api..")
                    .as("Hexagonal architecture: The API implementation must not be used in the persistence implementation.");

    @SuppressWarnings("unused")
    @ArchTest
    public static final ArchRule dontUseConfig =
            ArchRuleDefinition
                    .noClasses()
                    .that()
                    .resideOutsideOfPackage("cde.chameleon.config")
                    .should()
                    .dependOnClassesThat()
                    .resideInAPackage("cde.chameleon.config")
                    .as("The Spring Boot config classes should not be referenced at all.");

    @SuppressWarnings("unused")
    @ArchTest
    public static final ArchRule dontUseOtherPackagesInConfig =
            ArchRuleDefinition
                    .classes()
                    .that()
                    .resideInAPackage("cde.chameleon.config")
                    .should()
                    .onlyDependOnClassesThat()
                    .resideInAnyPackage(
                            "cde.chameleon.config", "java..", "jakarta..", "org.springframework..",
                            "io.swagger..", "org.slf4j..", "org.modelmapper..", "com.fasterxml.jackson.databind..")
                    .as("The Spring Boot config classes must not depend on other packages of project Chameleon except for the cross-cutting concerns for APIs.");

    @SuppressWarnings("unused")
    @ArchTest
    public static final ArchRule onlyUseApiForApiAndConfig =
            ArchRuleDefinition
                    .noClasses()
                    .that()
                    .resideOutsideOfPackages("..api..", "cde.chameleon.config")
                    .should()
                    .dependOnClassesThat()
                    .resideInAPackage("cde.chameleon.api")
                    .as("The cross-cutting concerns for APIs may only be used by the config and the specific api packages.");

    @SuppressWarnings("unused")
    @ArchTest
    public static final ArchRule dontUseOtherPackagesInApi =
            ArchRuleDefinition
                    .classes()
                    .that()
                    .resideInAPackage("cde.chameleon.api")
                    .should()
                    .onlyDependOnClassesThat()
                    .resideInAnyPackage(
                            "cde.chameleon.api", "java..", "jakarta..", "org.springframework..",
                            "io.swagger..", "org.slf4j..", "lombok..", "org.apache.commons..")
                    .as("The cross-cutting concerns for APIs must not depend on other packages of project Chameleon.");
}
