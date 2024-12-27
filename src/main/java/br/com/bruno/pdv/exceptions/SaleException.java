package br.com.bruno.pdv.exceptions;

public class SaleException extends RuntimeException {

    public SaleException(String message) {
        super(String.valueOf(message));
    }

}
