package com.codecool.kgp.config.swagger;

import com.codecool.kgp.errorhandling.ErrorResponseDto;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;


@Target(value = {ElementType.TYPE, ElementType.METHOD, ElementType.ANNOTATION_TYPE})
@Retention(RetentionPolicy.RUNTIME)
@ApiGeneralResponses
@ApiResponses(value = {
        @ApiResponse(responseCode = "400", description = "Endpoint not found or invalid Id type",
                content = @Content(schema = @Schema(implementation = ErrorResponseDto.class))),
        @ApiResponse(responseCode = "404", description = "Id not found",
                content = @Content(schema = @Schema(implementation = ErrorResponseDto.class))
        )
})
public @interface ApiRetrieveUpdateDeleteResponses {
}