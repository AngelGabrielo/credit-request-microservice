package org.app.credit.services;

import org.app.credit.entities.User;

public interface UserService {

    User save(User user);

    boolean existsByUsername(String username);

}
