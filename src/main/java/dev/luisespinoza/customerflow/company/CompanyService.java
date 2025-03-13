package dev.luisespinoza.customerflow.company;

import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class CompanyService {

    private final CompanyRepository repository;
    private final ModelMapper mapper;

    public CompanyResponse create (CompanyRequest request) {
        Company company = mapper.map(request, Company.class);
        company = repository.save(company);
        return mapper.map(company, CompanyResponse.class);
    }
}
