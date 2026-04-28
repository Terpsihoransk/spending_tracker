package spending.tracker.backend.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;
import spending.tracker.backend.model.UserDto;
import spending.tracker.backend.service.UserService;

@RestController
@RequestMapping("/api/v1/user")
@RequiredArgsConstructor
@Tag(name = "User", description = "User management API")
public class UserController {

    private final UserService userService;

    @Operation(summary = "Create new user")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User created successfully")
    })
    @PostMapping
    public UserDto createUser(@RequestBody UserDto userDto) {
        return userService.saveUser(userDto);
    }

    @Operation(summary = "Get all users")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Users retrieved successfully")
    })
    @GetMapping
    public List<UserDto> getAllUsers() {
        return userService.getAllUsers();
    }
}