package br.com.bruno.pdv.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SaleInfoDTO {

    private String user;
    private String date;
    private List<ProductInfoDTO> products;
    private BigDecimal total;

}
