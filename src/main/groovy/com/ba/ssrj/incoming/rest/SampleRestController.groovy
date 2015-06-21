package com.ba.ssrj.incoming.rest

import com.ba.ssrj.framework.model.RestDto
import com.ba.ssrj.model.dto.Sample
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

import static org.springframework.web.bind.annotation.RequestMethod.GET

/**
 * TODO: Document Me
 *
 * @author Ganeshji Marwaha
 * @since 6/20/15
 */
@RestController
@RequestMapping("/rest/samples")
class SampleRestController {
    
    @RequestMapping(value="/test1", method = GET)
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    ResponseEntity<RestDto> test1() {
        Sample sample = new Sample(name: "Name", title: "Title")
        return RestDto.forDto(sample)
    }
    
}
