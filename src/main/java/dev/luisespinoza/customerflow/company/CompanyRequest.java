package dev.luisespinoza.customerflow.company;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class CompanyRequest {
    private Long id;
    @NotBlank(message = "The company name cannot be empty")
    private String name;
}
