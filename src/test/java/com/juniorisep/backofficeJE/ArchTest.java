package com.juniorisep.backofficeJE;

import com.tngtech.archunit.core.domain.JavaClasses;
import com.tngtech.archunit.core.importer.ClassFileImporter;
import com.tngtech.archunit.core.importer.ImportOption;
import org.junit.jupiter.api.Test;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses;

class ArchTest {

    @Test
    void servicesAndRepositoriesShouldNotDependOnWebLayer() {

        JavaClasses importedClasses = new ClassFileImporter()
            .withImportOption(ImportOption.Predefined.DO_NOT_INCLUDE_TESTS)
            .importPackages("com.juniorisep.backofficeJE");

        noClasses()
            .that()
                .resideInAnyPackage("com.juniorisep.backofficeJE.service..")
            .or()
                .resideInAnyPackage("com.juniorisep.backofficeJE.repository..")
            .should().dependOnClassesThat()
                .resideInAnyPackage("..com.juniorisep.backofficeJE.web..")
        .because("Services and repositories should not depend on web layer")
        .check(importedClasses);
    }
}
