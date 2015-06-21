package com.ba.ssrj.framework.model

import com.fasterxml.jackson.annotation.JsonInclude

/**
 * DTO will represent any data transfer object in the system.
 * All DBEntities are implicitly DTOs as well
 *
 * @author Ganeshji Marwaha
 * @since 9/7/14
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Dto implements Serializable {


}
