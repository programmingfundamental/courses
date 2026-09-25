package bg.tuvarna.mobile;
import jakarta.enterprise.context.ApplicationScoped; import jakarta.inject.Inject;
import org.eclipse.microprofile.reactive.messaging.*;
@ApplicationScoped public class EventPublisher {
 @Inject @Channel("activity-events") Emitter<io.smallrye.reactive.messaging.kafka.Record<String,String>> events;
 public java.util.concurrent.CompletionStage<Void> send(String activityId,String eventJson){return events.send(io.smallrye.reactive.messaging.kafka.Record.of(activityId,eventJson));}
 // L09-G: извикайте след durable status change. Обсъдете commit/publish gap и outbox.
}
