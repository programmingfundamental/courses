package bg.tu_varna.sit.task_manager.exception;

/***
 * Добавено в лабораторно упражнение 8
 */
public class ResourceNotFoundException extends Exception {
    public ResourceNotFoundException(long id, Class item) {
        super(String.format("Resource with id = %i from class = %s", id,  item.getName()));
    }
}
