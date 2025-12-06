package ktb.service;

import com.tngtech.archunit.core.domain.JavaClasses;
import com.tngtech.archunit.core.importer.ImportOption;
import com.tngtech.archunit.junit.AnalyzeClasses;
import com.tngtech.archunit.junit.ArchTest;
import com.tngtech.archunit.lang.syntax.ArchRuleDefinition;

import ktb.repository.ArticleRepository;

@AnalyzeClasses(
        packagesOf = ArticleService.class,
        importOptions = ImportOption.DoNotIncludeTests.class
)
class ArticleServiceTest {

    @ArchTest
    void 게시글_서비스는_레포지토리만_주입받는다(JavaClasses importedClasses) {
        ArchRuleDefinition.fields()
                .that().areDeclaredIn(ArticleService.class)
                .should().haveRawType(ArticleRepository.class)
                .check(importedClasses);
    }

    @ArchTest
    void 게시글_서비스는_연쇄_삭제_전략_그리고_커맨드와_쿼리만_사용한다(JavaClasses importedClasses) {
        ArchRuleDefinition.noClasses()
                .that().resideOutsideOfPackage("ktb.handler.strategy.impl..")
                .and().doNotBelongToAnyOf(
                        ArticleCommandService.class
                        , ArticleQueryService.class
                        , CommentCommandService.class
                        , CommentQueryService.class)
                .should().dependOnClassesThat().areAssignableTo(ArticleService.class)
                .check(importedClasses);
    }
}
