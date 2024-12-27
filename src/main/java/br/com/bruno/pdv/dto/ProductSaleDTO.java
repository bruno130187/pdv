package br.com.bruno.pdv.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductSaleDTO {

    @NotNull(message = "O item da venda é obrigatório.")
    private long productid;

    @NotNull(message = "A quantidade é obrigatória.")
    private int quantity;

}
