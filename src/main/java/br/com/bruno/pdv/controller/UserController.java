package br.com.bruno.pdv.controller;

import br.com.bruno.pdv.dto.ResponseDTO;
import br.com.bruno.pdv.dto.UserDTO;
import br.com.bruno.pdv.dto.UserResponseDTO;
import br.com.bruno.pdv.service.UserService;
import br.com.bruno.pdv.util.Message;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping(path = "/user")
public class UserController {

    @Autowired
    private UserService userService;

    @GetMapping(path = "/{id}")
    public ResponseEntity findById(@PathVariable Long id) {
        try {
            UserResponseDTO userResponseDTO = userService.findByIdWithoutPass(id);
            if (userResponseDTO.getId() != null) {
                return new ResponseEntity<>(userResponseDTO, HttpStatus.OK);
            }
            return new ResponseEntity<>(new ResponseDTO(String.format("%s", Message.USUARIO_NAO_ENCONTRADO)), HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping()
    public ResponseEntity getAll(){
        return new ResponseEntity<>(userService.findAll(), HttpStatus.OK);
    }

    @PostMapping()
    public ResponseEntity post(@Valid @RequestBody UserDTO userDTO){
        try {
            userDTO.setEnabled(true);
            return new ResponseEntity<>(userService.save(userDTO), HttpStatus.CREATED);
        }catch (Exception error){
            return new ResponseEntity<>(new ResponseDTO(error.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PutMapping()
    public ResponseEntity put(@RequestBody UserDTO user) {
        try {
            UserDTO userDTO = userService.findById(user.getId());
            if (userDTO.getId() != null) {
                userDTO.setName(user.getName());
                userDTO.setEnabled(user.isEnabled());
                userService.save(userDTO);
                return new ResponseEntity<>(Message.USUARIO_ATUALIZADO_COM_SUCESSO, HttpStatus.OK);
            }
            return new ResponseEntity<>(new ResponseDTO(String.format("%s", Message.USUARIO_NAO_ENCONTRADO)), HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            return new ResponseEntity<>(new ResponseDTO(e.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @DeleteMapping(path = "/{id}")
    public ResponseEntity delete(@PathVariable Long id) {
        try {
            UserDTO userDTO = userService.findById(id);
            if (userDTO.getId() != null) {
                userService.deleteById(id);
                return new ResponseEntity<>(new ResponseDTO(String.format("%s", Message.USUARIO_DELETADO_COM_SUCESSO)), HttpStatus.OK);
            }
            return new ResponseEntity<>(new ResponseDTO(String.format("%s", Message.USUARIO_NAO_ENCONTRADO)), HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            return new ResponseEntity<>(new ResponseDTO(String.format("%s %s", Message.ERRO_AO_DELETAR_USUARIO, e.getMessage())), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PutMapping(path = "/{id}")
    public ResponseEntity habilitaDesabilita(@PathVariable Long id) {
        try {
            UserDTO userDTO = userService.findById(id);
            if (userDTO.getId() != null) {
                userDTO.setEnabled(!userDTO.isEnabled());
                userService.update(userDTO);
                return new ResponseEntity<>(new ResponseDTO(String.format("%s", Message.STATUS_ATUALIZADO_COM_SUCESSO)), HttpStatus.OK);
            }
            return new ResponseEntity<>(new ResponseDTO(String.format("%s", Message.USUARIO_NAO_ENCONTRADO)), HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            return new ResponseEntity<>(new ResponseDTO(String.format("%s %s", Message.ERRO_AO_ATUALIZAR_STATUS, e.getMessage())), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

}
