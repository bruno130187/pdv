package br.com.bruno.pdv.service;

import br.com.bruno.pdv.dto.ProductInfoDTO;
import br.com.bruno.pdv.dto.ProductSaleDTO;
import br.com.bruno.pdv.dto.SaleDTO;
import br.com.bruno.pdv.dto.SaleInfoDTO;
import br.com.bruno.pdv.entity.ItemSale;
import br.com.bruno.pdv.entity.Product;
import br.com.bruno.pdv.entity.Sale;
import br.com.bruno.pdv.entity.User;
import br.com.bruno.pdv.exceptions.SaleException;
import br.com.bruno.pdv.repository.ItemSaleRepository;
import br.com.bruno.pdv.repository.ProductRepository;
import br.com.bruno.pdv.repository.SaleRepository;
import br.com.bruno.pdv.repository.UserRepository;
import br.com.bruno.pdv.util.Message;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SaleService {

    private final UserRepository userRepository;
    private final ProductRepository productRepository;
    private final SaleRepository saleRepository;
    private final ItemSaleRepository itemSaleRepository;

    @Transactional
    public Long save(SaleDTO saleDTO) {
        Optional<User> optionalUser= userRepository.findById(saleDTO.getUserid());

        if (optionalUser.isEmpty()) {
            throw new SaleException(String.format("%s%d", Message.SEM_USUARIO_COM_ID_, saleDTO.getUserid()));
        } else {
            User user = optionalUser.get();

            Sale newSale = Sale.builder()
                    .user(user)
                    .date(LocalDate.now())
                    .build();

            List<ItemSale> items = getItemsSale(saleDTO.getItems());

            newSale = saleRepository.save(newSale);

            saveItemsSale(items, newSale);

            return newSale.getId();
        }
    }

    private void saveItemsSale(List<ItemSale> items, Sale newSale) {
        items.stream()
                .peek(item -> item.setSale(newSale))
                .forEach(itemSaleRepository::save);
    }

    private List<ItemSale> getItemsSale(List<ProductSaleDTO> products) {
        return products.stream().map(item -> {
            Optional<Product> optionalProduct = productRepository.findById(item.getProductid());

            if (optionalProduct.isEmpty()) {
                throw new SaleException(String.format("%s%d", Message.SEM_PRODUTO_COM_ID_, item.getProductid()));
            } else {
                Product product = optionalProduct.get();

                ItemSale itemSale = ItemSale.builder()
                        .product(product)
                        .quantity(item.getQuantity())
                        .date(LocalDate.now())
                        .build();

                if (item.getQuantity() <= 0) {
                    throw new SaleException(String.format("%s%d", Message.ITEM_SEM_QUANTIDADE_INFORMADA_COM_ID_, item.getProductid()));
                } else if (product.getQuantity() == 0) {
                    throw new SaleException(String.format("%s%d", Message.SEM_PRODUTO_NO_ESTOQUE_COM_ID_, product.getId()));
                } else if (product.getQuantity() < item.getQuantity()) {
                    throw new SaleException(String.format("%s%d", Message.ESTOQUE_PRODUTO_MENOR_QUE_A_QUANTIDADE_DA_VENDA_COM_ID_, product.getId()));
                }

                int newQuantity = product.getQuantity() - item.getQuantity();
                product.setQuantity(newQuantity);
                productRepository.save(product);

                return itemSale;
            }
        }).collect(Collectors.toList());
    }

    public List<SaleInfoDTO> getAll(){
        return saleRepository.findAll().stream().map(sale -> getSaleInfo(sale)).collect(Collectors.toList());
    }

    private SaleInfoDTO getSaleInfo(Sale sale) {

        var products = getProductInfo(sale.getItems());

        BigDecimal total =  getTotal(products);

        return SaleInfoDTO.builder()
                .user(sale.getUser().getName())
                .date(sale.getDate().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")))
                .products(products)
                .total(total)
                .build();
    }

    private BigDecimal getTotal(List<ProductInfoDTO> products) {
        BigDecimal total = BigDecimal.ZERO;

        for (ProductInfoDTO product : products) {
            if (product.getPrice() != null && product.getQuantity() != null) {
                BigDecimal productTotal = product.getPrice().multiply(new BigDecimal(product.getQuantity()));
                total = total.add(productTotal);
            }
        }

        return total;
    }

    private List<ProductInfoDTO> getProductInfo(List<ItemSale> items) {

        if(CollectionUtils.isEmpty(items)){
            return Collections.emptyList();
        }

        return items.stream().map(
                item -> ProductInfoDTO
                        .builder()
                        .id(item.getId())
                        .description(item.getProduct().getDescription())
                        .quantity(item.getQuantity())
                        .price(item.getProduct().getPrice())
                        .build()
        ).collect(Collectors.toList());
    }

    public SaleInfoDTO getById(Long id){
        Optional<Sale> saleOptional = saleRepository.findById(id);

        if (saleOptional.isPresent()) {
            Sale sale = saleOptional.get();
            return convertToSaleInfoDTO(sale);
        } else {
            throw new SaleException(String.format("%s%d", Message.SALE_NOT_FOUND_WITH_ID_, id));
        }
    }

    private SaleInfoDTO convertToSaleInfoDTO(Sale sale) {
        SaleInfoDTO saleInfoDTO = SaleInfoDTO.builder()
                .date(sale.getDate().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")))
                .user(sale.getUser().getName())
                .products(getProductInfo(sale.getItems()))
                .build();

        return saleInfoDTO;
    }

}
