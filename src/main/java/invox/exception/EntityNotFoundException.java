package invox.exception;

public class EntityNotFoundException extends InvoxException {

    public EntityNotFoundException(String message) {
        super(message);
    }

    public EntityNotFoundException(String entityName, int id) {
        super(entityName + " cu id=" + id + " nu a fost gasit.");
    }
}
