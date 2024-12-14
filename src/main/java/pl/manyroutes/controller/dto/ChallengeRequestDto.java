package pl.manyroutes.controller.dto;

import pl.manyroutes.controller.validation.ChallengeUniqueName;
import pl.manyroutes.entity.enums.Status;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record ChallengeRequestDto(

        @NotBlank(message = "Pole 'name' jest obowiązkowe")
        @Size(max = 50, message = "Nazwa może mieć maksymalnie 50 znaków")
        @ChallengeUniqueName
        String name,


        String description,

        @NotNull(message = "Pole 'status' jest obowiązkowe")
        Status status
) {
}
