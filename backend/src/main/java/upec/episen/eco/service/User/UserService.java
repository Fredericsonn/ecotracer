package upec.episen.eco.service.User;

import java.util.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import upec.episen.eco.exceptions.UserNotFoundException;
import upec.episen.eco.models.User.Enterprise;
import upec.episen.eco.models.User.Person;
import upec.episen.eco.models.User.User;
import upec.episen.eco.repositories.User.IUser;
import upec.episen.eco.service.ServiceHelper;

@Service
public class UserService {

    @Autowired
    private IUser iuser;

    // get all users
    public List<User> getAllUsers() {
        return iuser.findAll();
    }

    // get all users by type
    public List<User> getAllUsersByType(String type) {
        return iuser.findAllByDtype(type);
    }

    // get user by id
    public User getUserById(Long id) throws UserNotFoundException {
        Optional<User> user = iuser.findById(id);

        if (user.isPresent())
            return user.get();

        throw new UserNotFoundException(id);
    }

    // get user by username
    public User getUserByUsername(String username) throws UserNotFoundException {
        Optional<User> user = iuser.findByUsername(username);

        if (user.isPresent())
            return user.get();

        throw new UserNotFoundException(username);
    }

    // save a new user
    public User saveUser(User user) {

        if (user instanceof Person) user = (Person) user;

        else user = (Enterprise) user;

        return iuser.save(user);
    }

    // delete a user
    public void deleteUser(User user) {
        iuser.delete(user);
    }

    // update a user
    public User updateUser(long id, Map<String, Object> updates) {

        try {

            User user = getUserById(id);

            ServiceHelper.genericUpdate(user, updates);

            return saveUser(user);

        } catch (UserNotFoundException e) {

            e.printStackTrace();

            return null;
        }

    }

}
