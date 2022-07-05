package com.example.demo.resource;

import com.example.demo.domain.User;
import com.example.demo.exception.domain.*;
import com.example.demo.service.UserService;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

@RestController
@RequestMapping("/users")
public class UserResource extends ExceptionHandling {  // ExceptionHandling class will be used when exception occurs (looks for handler)
    private final UserService userService;

    public UserResource(UserService userService) {
        this.userService = userService;
    }

    @PreAuthorize("hasAuthority('user:read')")
    @RequestMapping(value = {"/", ""}, method = RequestMethod.GET)
    @ResponseStatus(HttpStatus.OK)
    public Iterable<User> getUsers(@RequestParam(value = "page", defaultValue = "0") int page, @RequestParam(value = "size", defaultValue = "20") int size){
        return userService.getUsers(page, size);
    }
    @PreAuthorize("hasAuthority('user:read')")
    @RequestMapping(value = "/{publicId}", method = RequestMethod.GET)
    @ResponseStatus(HttpStatus.OK)
    public User getUserByPublicId(@PathVariable("publicId") String publicId) throws UserNotFoundException {
        return userService.getUserByPublicId(publicId);
    }

    @PreAuthorize("hasAuthority('user:create')")
    @RequestMapping(value = {"/", ""}, method = RequestMethod.POST)
    @ResponseStatus(HttpStatus.CREATED)
    public User createUser(@Valid @RequestBody User user, BindingResult result) throws Exception {
        if (result.hasErrors()) {
            throw new UserValidationException(result.getAllErrors().get(0).getDefaultMessage());
        }
        return userService.createUser(user);
    }

    @PreAuthorize("hasAuthority('user:update')")
    @RequestMapping(value = "/{publicId}", method = RequestMethod.PUT)
    @ResponseStatus(HttpStatus.CREATED)
    public User updateUser(@PathVariable("publicId") String publicId, @Valid @RequestBody User user, BindingResult result) throws Exception {
        if (result.hasErrors()) {
            throw new UserValidationException(result.getAllErrors().get(0).getDefaultMessage());
        }
        return userService.updateUser(user, publicId);
    }

    @PreAuthorize("hasAuthority('user:delete')")
    @RequestMapping(value = "/{publicId}", method = RequestMethod.DELETE)
    @ResponseStatus(HttpStatus.NO_CONTENT) //204
    public void deleteUser(@PathVariable("publicId") String publicId) throws UserNotFoundException{
        try{
            userService.deleteUser(publicId);
        }catch(EmptyResultDataAccessException | IOException e){
            //prevent error if ID or image doesn't exist
        }
    }

    @PreAuthorize("hasAuthority('user:update')")
    @RequestMapping(value = "/updateProfileImage", method = RequestMethod.POST, consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<User> updateProfileImage(@RequestParam(value = "email") String email, @RequestParam(value = "profileImage") MultipartFile profileImg) throws IOException {
        try{
            if(!Arrays.asList(MediaType.IMAGE_PNG_VALUE, MediaType.IMAGE_JPEG_VALUE, MediaType.IMAGE_GIF).contains(profileImg.getContentType())) throw new NotAnImageFileException();
            userService.updateProfilePicture(email, profileImg);
        }catch (Exception e){
//            System.err.println(e);
            return ResponseEntity.badRequest().build();
        }
        return ResponseEntity.ok(userService.getUserByEmail(email)); //.build();
    }

    @RequestMapping(value = "/image/{publicId}/{fileName}", method = RequestMethod.GET, produces = MediaType.IMAGE_PNG_VALUE)
    public ResponseEntity<byte[]> getProfileImage(@PathVariable(value = "publicId") String publicId, @PathVariable(value = "fileName") String fileName) {
        // return image as byte array
        try {
            byte[] image = userService.getUserProfileImage(publicId, fileName);
            if(image == null) throw new IOException();
            return ResponseEntity.ok(image);
        }
        catch (IOException | UserNotFoundException e){
            System.err.println(e);
            return ResponseEntity.badRequest().build();
        }
    }

}
