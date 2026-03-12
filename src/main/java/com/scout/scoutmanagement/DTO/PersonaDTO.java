package com.scout.scoutmanagement.DTO;

import com.scout.scoutmanagement.domain.Rol;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class PersonaDTO {

    @NotBlank(message = "El nombre es obligatorio")
    private String nombre;

    @NotBlank(message = "El apellido es obligatorio")
    private String apellido;

    @NotNull(message = "El DNI es obligatorio")
    @Positive(message = "El DNI debe ser un numero positivo")
    private Long dni;

    @Email(message = "El correo debe ser valido")
    @NotBlank(message = "El correo es obligatorio")
    private String mail;

    @NotNull(message = "El ID de la rama es obligatorio")
    private Long ramaId;

    @NotNull(message = "El rol es obligatorio")
    private Rol rol;

    public PersonaDTO() {
    }
}

