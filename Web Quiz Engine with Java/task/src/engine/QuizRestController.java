package engine;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

@RestController
public class QuizRestController {
    private final QuizRepository quizRepository;
    private final QuizUserRepository userRepository;
    private final QuizCompletionRepository completionRepository;
    private final PasswordEncoder passwordEncoder;

    public QuizRestController(QuizRepository quizRepository, QuizUserRepository userRepository, QuizCompletionRepository completionRepository, PasswordEncoder passwordEncoder) {
        this.quizRepository = quizRepository;
        this.userRepository = userRepository;
        this.completionRepository = completionRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @PostMapping("/api/quizzes")
    public ResponseEntity<Quiz> createQuiz(@Valid @RequestBody Quiz quiz, Authentication auth) {
        quiz.setUser(auth.getName());
        quiz = quizRepository.save(quiz);
        return ResponseEntity.ok(quiz);
    }

    @GetMapping("/api/quizzes/{id}")
    public ResponseEntity<QuizResponse> getQuiz(@PathVariable long id) {
        Quiz quiz = quizRepository.findById(id).orElse(null);

        if (quiz == null) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(new QuizResponse(quiz.getId(), quiz.getTitle(), quiz.getText(), quiz.getOptions()));
    }

    @GetMapping("/api/quizzes")
    public ResponseEntity<Page<QuizResponse>> getQuizzes(@RequestParam int page) {
        Page<Quiz> quizzes = quizRepository.findAll(PageRequest.of(page, 10));
        Page<QuizResponse> quizzesWithoutAnswers = quizzes.map(q -> new QuizResponse(q.getId(), q.getTitle(), q.getText(), q.getOptions()));
        return ResponseEntity.ok(quizzesWithoutAnswers);
    }

    @PostMapping("/api/quizzes/{id}/solve")
    public ResponseEntity<AnswerResponse> answerQuiz(@PathVariable long id, @RequestBody Answer answer, Authentication auth) {
        Quiz quiz = quizRepository.findById(id).orElse(null);

        if (quiz == null) {
            return ResponseEntity.notFound().build();
        }

        Set<Integer> quizSet = new HashSet<>(quiz.getAnswer());
        Set<Integer> answerSet = new HashSet<>(answer.answer.orElse(new ArrayList<>()));
        boolean answerIsCorrect = quizSet.equals(answerSet);

        if (answerIsCorrect) {
            completionRepository.save(new QuizCompletion(auth.getName(), id, LocalDateTime.now()));
        }

        return ResponseEntity.ok(new AnswerResponse(answerIsCorrect, answerIsCorrect ? "Congratulations, you're right!" : "Wrong answer! Please, try again."));
    }

    @PostMapping("/api/register")
    public ResponseEntity<Void> registerUser(@Valid @RequestBody RegistrationRequest registration) {
        String password = registration.password;

        if (Arrays.stream(password.split("")).filter(s -> !s.isBlank()).count() < 5 || userRepository.findById(registration.email).isPresent()) {
            return ResponseEntity.badRequest().build();
        }

        QuizUser user = new QuizUser(registration.email, passwordEncoder.encode(registration.password));
        userRepository.save(user);

        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/api/quizzes/{id}")
    public ResponseEntity<Void> deleteQuiz(@PathVariable long id, Authentication auth) {
        Quiz quiz = quizRepository.findById(id).orElse(null);

        if (quiz == null) {
            return ResponseEntity.notFound().build();
        }

        if (!Objects.equals(quiz.getUser(), auth.getName())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        quizRepository.delete(quiz);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/api/quizzes/completed")
    public ResponseEntity<Page<QuizCompletionResponse>> getCompletedQuizzes(@RequestParam int page, Authentication auth) {
        return ResponseEntity.ok(completionRepository.findQuizCompletionsBy(auth.getName(), PageRequest.of(page, 10, Sort.by("completedAt").descending())).map(qc -> new QuizCompletionResponse(qc.getQuizId(), qc.getCompletedAt())));
    }

    public record RegistrationRequest(@NotNull @Email(regexp = "\\w+@\\w+\\.\\w+") String email,
                                      @NotNull String password) {
    }

    public record QuizResponse(long id, String title, String text, List<String> options) {
    }

    public record QuizCompletionResponse(long id, LocalDateTime completedAt) {
    }

    public record Answer(Optional<List<Integer>> answer) {
    }

    public record AnswerResponse(boolean success, String feedback) {
    }
}
