package dev.luisespinoza.customerflow.company;

import dev.luisespinoza.customerflow.industry.IndustryResponse;
import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CompanyResponse {
    private Long id;
    private String name;
    private String domainName;
    private String city;
    private String state;
    private String postalCode;
    private Integer numberOfEmployees;
    private Integer annualRevenue;
    private IndustryResponse industry;
    private RelationshipType relationshipType;
    private Currency annualRevenueCurrency;
}
