package engine;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface QuizUserRepository extends CrudRepository<QuizUser, String> {
}
