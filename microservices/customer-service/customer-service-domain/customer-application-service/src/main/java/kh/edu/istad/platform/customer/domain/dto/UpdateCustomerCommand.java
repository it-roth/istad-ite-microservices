package kh.edu.istad.platform.customer.domain.dto;

public record UpdateCustomerCommand (
        String familyName,
        String givenName
){

}
