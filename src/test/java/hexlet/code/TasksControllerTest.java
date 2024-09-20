package hexlet.code;

import com.fasterxml.jackson.databind.ObjectMapper;
import hexlet.code.dto.task.TaskUpdateDTO;
import hexlet.code.mapper.TaskMapper;
import hexlet.code.model.Label;
import hexlet.code.model.Task;
import hexlet.code.model.TaskStatus;
import hexlet.code.model.User;
import hexlet.code.repository.LabelRepository;
import hexlet.code.repository.TaskRepository;
import hexlet.code.repository.TaskStatusRepository;
import hexlet.code.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.instancio.Instancio;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openapitools.jackson.nullable.JsonNullable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.nio.charset.StandardCharsets;

import static net.javacrumbs.jsonunit.assertj.JsonAssertions.assertThatJson;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles(value = "development")
@Transactional
public class TasksControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private WebApplicationContext webApplicationContext;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private TaskRepository taskRepository;
    @Autowired
    private TaskMapper taskMapper;
    @Autowired
    private TaskStatusRepository taskStatusRepository;
    @Autowired
    private ModelGenerator modelGenerator;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private LabelRepository labelRepository;

    private Task testTask;
    private TaskStatus testTaskStatus;
    private TaskStatus testTaskStatus2;
    private User testUser;
    private Label testLabel;
    private Label testLabel2;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext)
                .defaultResponseCharacterEncoding(StandardCharsets.UTF_8)
                .apply(springSecurity())
                .build();

        //following data is taken from application.yml
        testUser = userRepository.findByEmail("hexlet@example.com").get();
        testTaskStatus = taskStatusRepository.findBySlug("draft").get();
        testTaskStatus2 = taskStatusRepository.findBySlug("published").get();
        testLabel = labelRepository.findByName("bug").get();
        testLabel2 = labelRepository.findByName("feature").get();

        testTask = Instancio.of(modelGenerator.getTaskModel()).create();
        testTask.setAssignee(testUser);
        testTask.setTaskStatus(testTaskStatus);
        testTask.addLabel(testLabel);

        taskRepository.save(testTask);
        testTaskStatus.addTask(testTask);
        taskStatusRepository.save(testTaskStatus);
        testUser.addTask(testTask);
        userRepository.save(testUser);
        testLabel.addTask(testTask);
        labelRepository.save(testLabel);
    }

    @Test
    public void testIndex() throws Exception {
        long count = taskRepository.count();
        MvcResult result = mockMvc.perform(get("/api/tasks").with(jwt()))
                .andExpect(status().isOk())
                .andReturn();

        String body = result.getResponse().getContentAsString();
        assertThatJson(body).isArray().hasSize((int) count);
    }

    @Test
    public void testShow() throws Exception {
        MvcResult result = mockMvc.perform(get("/api/tasks/{id}", testTask.getId()).with(jwt()))
                .andExpect(status().isOk())
                .andReturn();

        String body = result.getResponse().getContentAsString();
        assertThatJson(body).and(
                v -> v.node("content").isEqualTo(testTask.getDescription()),
                v -> v.node("title").isEqualTo(testTask.getName())
        );
    }

    @Test
    public void testCreate() throws Exception {
        Task newTask = Instancio.of(modelGenerator.getTaskModel()).create();
        newTask.setAssignee(testUser);
        newTask.setTaskStatus(testTaskStatus);
        newTask.addLabel(testLabel);

        var taskDTO = taskMapper.map(newTask);

        MvcResult result = mockMvc.perform(post("/api/tasks")
                        .with(jwt())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(taskDTO)))
                .andExpect(status().isCreated())
                .andReturn();

        String body = result.getResponse().getContentAsString();
        assertThatJson(body).and(
                v -> v.node("content").isEqualTo(newTask.getDescription()),
                v -> v.node("title").isEqualTo(newTask.getName())
        );
    }

    @Test
    public void testUpdate() throws Exception {
        testTask.setTaskStatus(testTaskStatus2);
        testTask.setName("newName");
        //testTask.setLabels(Set.of(testLabel2));
        testTask.addLabel(testLabel2);
        testTask.removeLabel(testLabel);
        //System.out.println(testTask.getLabels().toString());
        var taskDTO = taskMapper.map(testTask);

        mockMvc.perform(put("/api/tasks/{id}", testTask.getId()).with(jwt())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(taskDTO)))
                .andExpect(status().isOk());

        Task task = taskRepository.findById(testTask.getId()).get();
        //System.out.println(task.getLabels().toString());
        assertThat(task.getTaskStatus()).isEqualTo(testTaskStatus2);
        assertThat(task.getName()).isEqualTo("newName");
        assertThat(task.getLabels()).containsOnly(testLabel2);
    }

    @Test
    public void testPartialUpdate() throws Exception {
        TaskUpdateDTO taskDTO = new TaskUpdateDTO();
        taskDTO.setTitle(JsonNullable.of("new name"));

        mockMvc.perform(put("/api/tasks/{id}", testTask.getId()).with(jwt())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(taskDTO)))
                .andExpect(status().isOk());

        Task task = taskRepository.findById(testTask.getId()).get();

        assertThat(task.getName()).isEqualTo("new name");
        assertThat(task.getDescription()).isEqualTo(testTask.getDescription());
    }

    @Test
    public void testDelete() throws Exception {
        mockMvc.perform(delete("/api/tasks/{id}", testTask.getId()).with(jwt()))
                .andExpect(status().isNoContent());
        assertThat(taskRepository.existsById(testTask.getId())).isEqualTo(false);
    }

    @Test
    public void testIndexWithoutAuth() throws Exception {
        mockMvc.perform(get("/api/tasks"))
                .andExpect(status().isUnauthorized());
    }
}
