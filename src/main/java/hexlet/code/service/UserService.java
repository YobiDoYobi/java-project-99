package hexlet.code.service;

import hexlet.code.component.TaskSpecification;
import hexlet.code.dto.user.UserDTO;
import hexlet.code.dto.user.UserUpsertDTO;
import hexlet.code.exception.MethodNotAllowedException;
import hexlet.code.exception.ResourceNotFoundException;
import hexlet.code.mapper.UserMapper;
import hexlet.code.repository.TaskRepository;
import hexlet.code.repository.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

import static hexlet.code.utils.Utils.urlNormalize;

@Service
@AllArgsConstructor
public class UserService implements UserDetailsService {
    private UserRepository repository;
    private UserMapper mapper;
    private TaskRepository taskRepository;
    private TaskSpecification taskSpecification;

    public List<UserDTO> getAll(Map<String, String> allParams) {
        Map<String, Object> params = urlNormalize(allParams);
        var users = repository.findAll(PageRequest.of((Integer) params.get("page"),
                (Integer) params.get("perPage")));
        return users.stream()
                .map(p -> mapper.map(p))
                .toList();
    }

    public long count() {
        return repository.count();
    }

    public UserDTO getById(long id) {
        var user = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User with id " + id + " not found"));
        return mapper.map(user);
    }

    public UserDTO create(UserUpsertDTO userData) {
        var user = mapper.map(userData);
        repository.save(user);
        return mapper.map(user);
    }

    public UserDTO update(long id, UserUpsertDTO userData) {
        var user = repository.findById(id).orElseThrow(
                () -> new ResourceNotFoundException("User with id " + id + " not found"));
        mapper.update(userData, user);
        repository.save(user);
        return mapper.map(user);
    }

    public void delete(long id) {
        var user = repository.findById(id).orElseThrow(
                () -> new ResourceNotFoundException("User with id " + id + " not found"));
        if (taskRepository.exists(taskSpecification.withAssigneeId(user.getId()))) {
            throw new MethodNotAllowedException("There are tasks associated with this assignee");
        }
        repository.deleteById(id);
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        return repository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
    }
}
