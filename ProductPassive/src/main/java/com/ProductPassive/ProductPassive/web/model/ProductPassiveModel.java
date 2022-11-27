package com.ProductPassive.ProductPassive.web.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductPassiveModel {

    @JsonIgnore
    private String id;
    @NotBlank(message="Account Number cannot be null or empty")
    private String identityAccount;
    @NotBlank(message="Document Number cannot be null or empty")
    private String document;
    @NotBlank(message="typeAccount cannot be null or empty")
    private String typeAccount;
    @NotNull(message="availableAmount cannot be null or empty")
    private Double availableAmount;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDate dateRegister;

}
