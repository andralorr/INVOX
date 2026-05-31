package invox.service;

import invox.exception.AuthenticationException;
import invox.exception.DuplicateEntityException;
import invox.exception.EntityNotFoundException;
import invox.model.User;
import invox.repository.UserRepository;
import invox.utils.PasswordHasher;

public class AuthService {

    private final UserRepository userRepository;
    private final AuditService audit = AuditService.getInstance();

    public AuthService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public User register(String username, String password, String companyName, String cui,
                         String tradeRegisterNumber, String iban, String bankName,
                         String email, String phone, String address, String city, String county)
            throws DuplicateEntityException {
        if (userRepository.findByUsername(username).isPresent()) {
            throw new DuplicateEntityException("Exista deja un utilizator cu numele " + username);
        }
        User user = new User(0, username, PasswordHasher.sha256(password), companyName, cui,
                tradeRegisterNumber, iban, bankName, email, phone, address, city, county);
        userRepository.add(user);
        audit.log("REGISTER_USER");
        return user;
    }

    public User login(String username, String password) throws AuthenticationException {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new AuthenticationException("Utilizator sau parola incorecte."));
        if (!user.getPasswordHash().equals(PasswordHasher.sha256(password))) {
            throw new AuthenticationException("Utilizator sau parola incorecte.");
        }
        audit.log("LOGIN");
        return user;
    }

    public User updateAccount(User user) throws EntityNotFoundException {
        userRepository.update(user);
        audit.log("UPDATE_ACCOUNT");
        return user;
    }

    public void changePassword(User user, String newPassword) throws EntityNotFoundException {
        user.setPasswordHash(PasswordHasher.sha256(newPassword));
        userRepository.update(user);
        audit.log("CHANGE_PASSWORD");
    }
}
