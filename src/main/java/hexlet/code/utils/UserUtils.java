package hexlet.code.utils;

import hexlet.code.exception.ResourceNotFoundException;
import hexlet.code.model.User;
import hexlet.code.repository.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class UserUtils {
    private UserRepository userRepository;

    public User getCurrentUser() {
        var authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            return null;
        }
        var email = authentication.getName();
        return userRepository.findByEmail(email).get();
    }

    public boolean isCurrentUser(long userId) {
        var currentUser = getCurrentUser();
        var user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User with id " + userId + " not found"));
        return currentUser.equals(user);
    }
}
