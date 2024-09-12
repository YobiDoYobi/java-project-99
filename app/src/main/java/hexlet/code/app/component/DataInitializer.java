package hexlet.code.app.component;

import hexlet.code.app.model.TaskStatus;
import hexlet.code.app.model.User;
import hexlet.code.app.repository.TaskStatusRepository;
import hexlet.code.app.repository.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;
import org.springframework.security.crypto.password.PasswordEncoder;

@Component
@AllArgsConstructor
public class DataInitializer implements ApplicationRunner {


    @Autowired
    private final UserRepository userRepository;

    @Autowired
    private TaskStatusRepository taskStatusRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;


    @Override
    public void run(ApplicationArguments args) {
        var user = userRepository.findByEmail("hexlet@example.com").orElse(new User());
        user.setFirstName("hexlet");
        user.setEmail("hexlet@example.com");
        user.setPassword(passwordEncoder.encode("qwerty"));
        userRepository.save(user);

        addStatus("draft", "Draft");
        addStatus("to_review", "ToReview");
        addStatus("to_be_fixed", "ToBeFixed");
        addStatus("to_publish", "ToPublish");
        addStatus("published", "Published");
    }

    private void addStatus(String slug, String name) {
        var taskStatus = taskStatusRepository.findBySlug(slug).orElse(new TaskStatus());
        taskStatus.setName(name);
        taskStatus.setSlug(slug);
        taskStatusRepository.save(taskStatus);
    }
}
