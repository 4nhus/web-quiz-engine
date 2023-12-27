package engine;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface QuizCompletionRepository extends PagingAndSortingRepository<QuizCompletion, String> {
    @Query("SELECT qc FROM QuizCompletion qc WHERE qc.user = :user")
    Page<QuizCompletion> findQuizCompletionsBy(String user, Pageable pageable);
}
