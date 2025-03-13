package dev.luisespinoza.customerflow.company;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class CompanyRequest {
    private UUID id;
    @NotBlank(message = "The company name cannot be empty")
    private String name;
}
