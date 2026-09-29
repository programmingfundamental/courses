package bg.tu_varna.sit.task_manager.exception;

/***
 * Добавено в лабораторно упражнение 9
 */
public class RelatedEntityException extends Exception {
    public RelatedEntityException(long id, Class item) {
        super(String.format("Conflict: %s with ID = %d is still linked to other records and cannot be deleted", id, item.getName()));
    }
}