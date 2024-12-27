package br.com.bruno.pdv.service;

import br.com.bruno.pdv.dto.UserDTO;
import br.com.bruno.pdv.dto.UserResponseDTO;
import br.com.bruno.pdv.entity.User;
import br.com.bruno.pdv.exceptions.SaleException;
import br.com.bruno.pdv.repository.UserRepository;
import br.com.bruno.pdv.security.SecurityConfig;
import br.com.bruno.pdv.util.Message;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;
    private ModelMapper mapper = new ModelMapper();

    public List<UserResponseDTO> findAll(){
        return userRepository.findAll().stream().map(user ->
                new UserResponseDTO(user.getId(), user.getName(), user.getUsername(), user.isEnabled())).collect(Collectors.toList());
    }

    public UserResponseDTO findByIdWithoutPass(long id){
        Optional<User> optional = userRepository.findById(id);
        if(!optional.isPresent()){
            throw new SaleException(String.format("%s%d", Message.SEM_USUARIO_COM_ID_, id));
        }
        User user = optional.get();
        return new UserResponseDTO(user.getId(), user.getName(), user.getUsername(), user.isEnabled());
    }

    public UserDTO findById(long id){
        Optional<User> optional = userRepository.findById(id);
        if(!optional.isPresent()){
            throw new SaleException(String.format("%s%d", Message.SEM_USUARIO_COM_ID_, id));
        }
        User user = optional.get();
        return new UserDTO(user.getId(), user.getName(), user.getUsername(), user.getPassword(), user.isEnabled());
    }

    public UserDTO save(UserDTO user){
        user.setPassword(SecurityConfig.passwordEncoder().encode(user.getPassword()));
        User userToSave = mapper.map(user, User.class);
        userRepository.save(userToSave);
        return new UserDTO(userToSave.getId(), userToSave.getName(), userToSave.getUsername(), userToSave.getPassword(), userToSave.isEnabled());
    }

    public UserDTO update(UserDTO userDTO){
        userDTO.setPassword(SecurityConfig.passwordEncoder().encode(userDTO.getPassword()));
        User userToSave = mapper.map(userDTO, User.class);
        Optional<User> userToEdit = userRepository.findById(userToSave.getId());

        if(!userToEdit.isPresent()){
            throw new SaleException(String.format("%s%d", Message.SEM_USUARIO_COM_ID_, userDTO.getId()));
        }

        userRepository.save(userToSave);
        return new UserDTO(userToSave.getId(), userToSave.getName(), userToSave.getUsername(), userToSave.getPassword(), userToSave.isEnabled());
    }

    public void deleteById(long id){
        userRepository.deleteById(id);
    }

    public User getByUserName(String username){
        return userRepository.findUserByUsername(username);
    }

}
