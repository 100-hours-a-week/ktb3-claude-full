package ktb.service;

import com.tngtech.archunit.core.domain.JavaClasses;
import com.tngtech.archunit.core.importer.ImportOption;
import com.tngtech.archunit.junit.AnalyzeClasses;
import com.tngtech.archunit.junit.ArchTest;
import com.tngtech.archunit.lang.syntax.ArchRuleDefinition;
import ktb.handler.strategy.impl.SingleCommentDeleteStrategy;
import ktb.handler.strategy.impl.UserCommentsDeleteStrategy;
import ktb.repository.ArticleCommentRepository;

@AnalyzeClasses(
        packagesOf = CommentService.class,
        importOptions = ImportOption.DoNotIncludeTests.class
)
class CommentServiceTest {

    @ArchTest
    void 댓글_서비스는_레포지토리만_주입(JavaClasses importedClasses) {
        ArchRuleDefinition.fields()
                .that().areDeclaredIn(CommentService.class)
                .should().haveRawType(ArticleCommentRepository.class)
                .check(importedClasses);
    }

    @ArchTest
    void 댓글_서비스는_연쇄_삭제_전략_그리고_커맨드와_쿼리만_사용한다(JavaClasses importedClasses) {
        ArchRuleDefinition.noClasses()
                .that().resideOutsideOfPackage("ktb.handler.strategy.impl..")
                .and().doNotBelongToAnyOf(
                        CommentCommandService.class,
                        CommentQueryService.class
                )
                .should().dependOnClassesThat().areAssignableTo(CommentService.class)
                .check(importedClasses);
    }
}
