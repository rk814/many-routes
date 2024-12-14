package pl.manyroutes.config.swagger;

import pl.manyroutes.errorhandling.ErrorResponseDto;
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
        @ApiResponse(responseCode = "409", description = "Name conflict",
                content = @Content(schema = @Schema(implementation = ErrorResponseDto.class)))
})
public @interface ApiCreateResponses {
}