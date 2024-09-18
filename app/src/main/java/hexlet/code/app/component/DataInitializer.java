package hexlet.code.app.component;

import hexlet.code.app.model.Label;
import hexlet.code.app.model.TaskStatus;
import hexlet.code.app.model.User;
import hexlet.code.app.repository.LabelRepository;
import hexlet.code.app.repository.TaskStatusRepository;
import hexlet.code.app.repository.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class DataInitializer implements ApplicationRunner {
    private final UserRepository userRepository;
    private TaskStatusRepository taskStatusRepository;
    private PasswordEncoder passwordEncoder;
    private LabelRepository labelRepository;


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

        addLabel("feature");
        addLabel("bug");
    }

    private void addStatus(String slug, String name) {
        var taskStatus = taskStatusRepository.findBySlug(slug).orElse(new TaskStatus());
        taskStatus.setName(name);
        taskStatus.setSlug(slug);
        taskStatusRepository.save(taskStatus);
    }

    private void addLabel(String name) {
        var label = labelRepository.findByName(name).orElse(new Label());
        label.setName(name);
        labelRepository.save(label);
    }
}
