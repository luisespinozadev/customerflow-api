package dev.luisespinoza.customerflow.company;

import dev.luisespinoza.customerflow.industry.Industry;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class CompanyRequest {
    @NotBlank(message = "The company name cannot be empty")
    private String name;
    private String domainName;
    private String city;
    private String state;
    private String postalCode;
    private Integer numberOfEmployees;
    private Integer annualRevenue;
    private Long industryId;
    private RelationshipType relationshipType;
    private Currency annualRevenueCurrency;
}
