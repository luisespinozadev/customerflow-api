package dev.luisespinoza.customerflow.company;

import dev.luisespinoza.customerflow.industry.Industry;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "companies")
public class Company {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private String domainName;
    @ManyToOne(fetch = FetchType.EAGER)
    private Industry industry;
    private String city;
    private String state;
    private String postalCode;
    private Integer numberOfEmployees;
    private Integer annualRevenue;

    @Enumerated(EnumType.STRING)
    private RelationshipType relationshipType;

    @Enumerated(EnumType.STRING)
    private Currency annualRevenueCurrency;

}
