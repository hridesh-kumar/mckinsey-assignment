package com.mckinsey.services;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.mckinsey.dtos.UserDTO;

@Lazy
@FeignClient(name = "AuthenticationService", url = "${app.user-service.url}", path = "/user")
public interface AuthenticationService {

	@GetMapping("/profile")
    public ResponseEntity<UserDTO> getUserByToken(@RequestParam String jwt);
}
