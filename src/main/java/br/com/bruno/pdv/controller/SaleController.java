package br.com.bruno.pdv.controller;

import br.com.bruno.pdv.dto.ResponseDTO;
import br.com.bruno.pdv.dto.SaleDTO;
import br.com.bruno.pdv.exceptions.SaleException;
import br.com.bruno.pdv.service.SaleService;
import br.com.bruno.pdv.util.Message;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping(path = "/sale")
public class SaleController {

    @Autowired
    private SaleService saleService;

    @PostMapping()
    public ResponseEntity post(@Valid @RequestBody SaleDTO saleDTO){
        try {
            Long id = saleService.save(saleDTO);
            return new ResponseEntity<>(new ResponseDTO(String.format("%s%d", Message.VENDA_REALIZADA_COM_SUCESSO_ID_, id)), HttpStatus.CREATED);
        } catch (Exception error){
            return new ResponseEntity<>(error.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping()
    public ResponseEntity getAll(){
        try {
            return new ResponseEntity<>(saleService.getAll(), HttpStatus.OK);
        } catch (Exception error){
            return new ResponseEntity<>(new ResponseDTO(error.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping(path = "/{id}")
    public ResponseEntity getById(@PathVariable Long id){
        try {
            return new ResponseEntity<>(saleService.getById(id), HttpStatus.OK);
        } catch (SaleException ex) {
            return new ResponseEntity<>(new ResponseDTO(ex.getMessage()), HttpStatus.NOT_FOUND);
        } catch (Exception error) {
            return new ResponseEntity<>(new ResponseDTO(error.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

}
