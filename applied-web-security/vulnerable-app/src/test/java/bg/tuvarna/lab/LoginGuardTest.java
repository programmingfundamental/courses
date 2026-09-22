package bg.tuvarna.lab;
import org.junit.jupiter.api.Test;
import java.time.*;
import static org.assertj.core.api.Assertions.*;
class LoginGuardTest {
    static class MutableClock extends Clock {
        Instant now=Instant.parse("2026-01-01T00:00:00Z");
        public ZoneId getZone(){return ZoneOffset.UTC;} public Clock withZone(ZoneId z){return this;} public Instant instant(){return now;}
    }
    @Test void thresholdExpirationAndReset() {
        MutableClock clock=new MutableClock(); LoginGuard g=new LoginGuard(clock,3,Duration.ofSeconds(60),true);
        g.failure("alice");g.failure("alice");assertThat(g.blocked("alice")).isFalse();
        g.failure("alice");assertThat(g.blocked("alice")).isTrue();assertThat(g.blocked("bob")).isFalse();
        clock.now=clock.now.plusSeconds(60); assertThat(g.blocked("alice")).isFalse();
        g.failure("alice");g.success("alice");g.failure("alice");g.failure("alice");assertThat(g.blocked("alice")).isFalse();
    }
    @Test void noResetPolicy() {
        LoginGuard g=new LoginGuard(Clock.systemUTC(),2,Duration.ofMinutes(1),false);
        g.failure("alice");g.success("alice");g.failure("alice");assertThat(g.blocked("alice")).isTrue();
    }
    @Test void concurrentFailuresAreNotLost() throws Exception {
        LoginGuard g=new LoginGuard(Clock.systemUTC(),20,Duration.ofMinutes(1),true);
        try(var pool=java.util.concurrent.Executors.newFixedThreadPool(4)) {
            var futures=new java.util.ArrayList<java.util.concurrent.Future<?>>();
            for(int i=0;i<20;i++) futures.add(pool.submit(()->g.failure("alice")));
            for(var f:futures) f.get();
        }
        assertThat(g.blocked("alice")).isTrue();
    }
}
