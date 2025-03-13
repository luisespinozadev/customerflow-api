package dev.luisespinoza.customerflow.company;

import lombok.*;

import java.util.UUID;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CompanyResponse {
    private UUID id;
    private String name;
}
