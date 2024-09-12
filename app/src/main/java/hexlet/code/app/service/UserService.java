package hexlet.code.app.service;

import hexlet.code.app.dto.UserCreateDTO;
import hexlet.code.app.dto.UserDTO;
import hexlet.code.app.dto.UserUpdateDTO;
import hexlet.code.app.exception.ResourceNotFoundException;
import hexlet.code.app.mapper.UserMapper;
import hexlet.code.app.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService {
    @Autowired
    private UserRepository repository;
    @Autowired
    private UserMapper mapper;

    public List<UserDTO> getAll() {
        var users = repository.findAll();
        return users.stream()
                .map(p -> mapper.map(p))
                .toList();
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
        repository.deleteById(id);
    }
}
