package com.ba.ssrj.framework

import org.springframework.validation.ObjectError

/**
 * Represents error
 *
 * @author Ganeshji Marwaha
 * @since 9/12/14
 */
class RestError extends Dto {
    String entityName;
    String fieldName;

    String code;
    String message;

    String rejectedValue;

    public RestError() {}

    public RestError(ObjectError objectError) {
        this.entityName = objectError.objectName
        this.fieldName = objectError.field

        this.code = objectError.code
        this.message = objectError.defaultMessage

        this.rejectedValue = objectError.rejectedValue
    }
}
