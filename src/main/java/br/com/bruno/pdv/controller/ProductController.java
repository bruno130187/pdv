package br.com.bruno.pdv.controller;

import br.com.bruno.pdv.dto.ProductDTO;
import br.com.bruno.pdv.entity.Product;
import br.com.bruno.pdv.entity.User;
import br.com.bruno.pdv.repository.ProductRepository;
import br.com.bruno.pdv.util.Message;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping(path = "/product")
public class ProductController {

    @Autowired
    private ProductRepository productRepository;

    private ModelMapper mapper = new ModelMapper();

    @GetMapping(path = "/{id}")
    public ResponseEntity getOne(@PathVariable Long id) {
        try {
            if (productRepository.existsById(id)) {
                return new ResponseEntity<>(productRepository.findById(id), HttpStatus.OK);
            }
            return new ResponseEntity<>(Message.PRODUTO_NAO_ENCONTRADO, HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping()
    public ResponseEntity getAll() {
        return new ResponseEntity<>(productRepository.findAll(), HttpStatus.OK);
    }

    @PostMapping()
    public ResponseEntity post(@RequestBody ProductDTO productDTO) {
        try {
            Product product = mapper.map(productDTO, Product.class);
            return new ResponseEntity<>(productRepository.save(product), HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PutMapping()
    public ResponseEntity put(@RequestBody ProductDTO productDTO) {
        try {
            if (productRepository.existsById(productDTO.getId())) {
                Product product = mapper.map(productDTO, Product.class);
                return new ResponseEntity<>(productRepository.save(product), HttpStatus.OK);
            }
            return new ResponseEntity<>(Message.PRODUTO_NAO_ENCONTRADO, HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @DeleteMapping(path = "/{id}")
    public ResponseEntity delete(@PathVariable Long id) {
        try {
            if (productRepository.existsById(id)) {
                productRepository.deleteById(id);
                return new ResponseEntity<>(Message.PRODUTO_DELETADO_COM_SUCESSO, HttpStatus.NO_CONTENT);
            }
            return new ResponseEntity<>(Message.PRODUTO_NAO_ENCONTRADO, HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            return new ResponseEntity<>(Message.ERRO_AO_DELETAR_PRODUTO + ": " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

}
