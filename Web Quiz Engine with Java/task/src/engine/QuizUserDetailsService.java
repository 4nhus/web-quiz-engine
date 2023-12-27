package engine;

import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class QuizUserDetailsService implements UserDetailsService {
    private final QuizUserRepository repository;

    public QuizUserDetailsService(QuizUserRepository repository) {
        this.repository = repository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        QuizUser user = repository.findById(username).orElseThrow(() -> new UsernameNotFoundException("Not found"));
        return User.withUsername(user.getEmail()).password(user.getPassword()).roles().build();
    }
}
