package dev.luisespinoza.customerflow.company;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/company")
@RequiredArgsConstructor
public class CompanyController {

    private final CompanyService service;

    @PostMapping
    public ResponseEntity<CompanyResponse> create (
            @Validated @RequestBody CompanyRequest request
    ) {
        return ResponseEntity.status(HttpStatus.CREATED)
                            .body(service.create(request));
    }

    @GetMapping
    public ResponseEntity<List<CompanyResponse>> findAll () {
        return ResponseEntity.ok(service.findAll());
    }
}
