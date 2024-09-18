package hexlet.code.app.service;

import hexlet.code.app.dto.user.UserCreateDTO;
import hexlet.code.app.dto.user.UserDTO;
import hexlet.code.app.dto.user.UserUpdateDTO;
import hexlet.code.app.exception.MethodNotAllowedException;
import hexlet.code.app.exception.ResourceNotFoundException;
import hexlet.code.app.mapper.UserMapper;
import hexlet.code.app.repository.TaskRepository;
import hexlet.code.app.repository.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

import static hexlet.code.app.utils.Utils.urlNormalize;

@Service
@AllArgsConstructor
public class UserService implements UserDetailsService {
    private UserRepository repository;
    private UserMapper mapper;
    private TaskRepository taskRepository;

    public List<UserDTO> getAll(Map<String, String> allParams) {
        Map<String, Object> params = urlNormalize(allParams);
        var users = repository.findAll(PageRequest.of((Integer) params.get("page"),
                (Integer) params.get("perPage")));
        return users.stream()
                .map(p -> mapper.map(p))
                .toList();
    }

    public int count() {
        return repository.countBy();
    }

    public UserDTO getById(long id) {
        var user = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User with id " + id + " not found"));
        return mapper.map(user);
    }

    public UserDTO create(UserCreateDTO userData) {
        var user = mapper.map(userData);
        repository.save(user);
        return mapper.map(user);
    }

    public UserDTO update(long id, UserUpdateDTO userData) {
        var user = repository.findById(id).orElseThrow(
                () -> new ResourceNotFoundException("User with id " + id + " not found"));
        mapper.update(userData, user);
        repository.save(user);
        return mapper.map(user);
    }

    public void delete(long id) {
        if (taskRepository.existsTasksByAssignee_Id(id)) {
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
