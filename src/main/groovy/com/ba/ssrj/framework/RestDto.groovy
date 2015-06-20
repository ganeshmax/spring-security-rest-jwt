package com.ba.ssrj.framework

import com.fasterxml.jackson.annotation.JsonInclude
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.validation.ObjectError

/**
 * Rest Response. This is the Dto that is always sent as a response to the rest client
 *
 * @author Ganeshji Marwaha
 * @since 9/12/14
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
class RestDto extends Dto {

    List<RestError> errors

    Dto dto

    List<? extends Dto> dtos

    public static ResponseEntity<RestDto> forDto(Dto dto, HttpStatus status = HttpStatus.OK) {
        return new ResponseEntity<RestDto>(new RestDto(dto: dto), status)
    }

    public static ResponseEntity<RestDto> forEntities(Collection<? extends Dto> dtos,
                                                      HttpStatus status = HttpStatus.OK) {
        return new ResponseEntity<RestDto>(new RestDto(dtos: dtos), status)
    }

    public static ResponseEntity<RestDto> forErrors(List<ObjectError> objectErrors,
                                                    HttpStatus status = HttpStatus.UNPROCESSABLE_ENTITY) {
        List<RestError> errors = objectErrors.collect(new ArrayList<RestError>()) { new RestError(it) }
        return new ResponseEntity<RestDto>(new RestDto(errors: errors), status)
    }

    public static ResponseEntity<RestDto> forRestErrors(List<RestError> errors,
                                                        HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR) {
        return new ResponseEntity<RestDto>(new RestDto(errors: errors), status)
    }

    public static ResponseEntity<RestDto> forStatus(HttpStatus status) {
        return new ResponseEntity<RestDto>(status)
    }

    public static ResponseEntity<RestDto> success() {
        forStatus(HttpStatus.OK)
    }
}
