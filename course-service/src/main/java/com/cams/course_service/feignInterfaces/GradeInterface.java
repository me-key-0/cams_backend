package com.cams.course_service.feignInterfaces;

import org.springframework.cloud.openfeign.FeignClient;


@FeignClient(name = "course-service", url = "localhost:8766", path = "/")
public interface GradeInterface {

    // @GetMapping("/address/{id}")
    // public ResponseEntity<AddressResponse> getAddressByEmployeeId(@PathVariable("id") int id);

}
