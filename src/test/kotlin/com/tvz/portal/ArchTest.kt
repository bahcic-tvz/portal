package com.tvz.portal

import com.tngtech.archunit.core.importer.ClassFileImporter
import com.tngtech.archunit.core.importer.ImportOption
import com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses
import org.junit.jupiter.api.Test

class ArchTest {

    @Test
    fun servicesAndRepositoriesShouldNotDependOnWebLayer() {

        val importedClasses = ClassFileImporter()
            .withImportOption(ImportOption.Predefined.DO_NOT_INCLUDE_TESTS)
            .importPackages("com.tvz.portal")

        noClasses()
            .that()
            .resideInAnyPackage("com.tvz.portal.service..")
            .or()
            .resideInAnyPackage("com.tvz.portal.repository..")
            .should().dependOnClassesThat()
            .resideInAnyPackage("..com.tvz.portal.web..")
            .because("Services and repositories should not depend on web layer")
            .check(importedClasses)
    }
}
