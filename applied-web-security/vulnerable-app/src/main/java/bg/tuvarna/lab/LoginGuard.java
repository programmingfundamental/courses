package bg.tuvarna.lab;

import java.time.*;
import java.util.concurrent.ConcurrentHashMap;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/** Single-process laboratory policy. A cluster needs shared atomic storage. */
@Component
public class LoginGuard {
    private record State(int failures, Instant until) {}
    private final ConcurrentHashMap<String,State> states = new ConcurrentHashMap<>();
    private final Clock clock;
    private final int max;
    private final Duration duration;
    private final boolean reset;
    public LoginGuard(Clock clock, @Value("${lab.max-attempts}") int max,
        @Value("${lab.lock-duration}") Duration duration, @Value("${lab.reset-after-success}") boolean reset) {
        if(max<1 || duration.isNegative() || duration.isZero()) throw new IllegalArgumentException("Invalid policy");
        this.clock=clock; this.max=max; this.duration=duration; this.reset=reset;
    }
    private void expire(String name) { states.computeIfPresent(name,(k,s)->clock.instant().isBefore(s.until)?s:null); }
    public boolean blocked(String name) { expire(name); State s=states.get(name); return s!=null && s.failures>=max; }
    public void failure(String name) {
        expire(name);
        // Bound storage for unknown usernames; teaching limit, not a distributed abuse defense.
        if(states.size()>10000) states.entrySet().removeIf(e -> !clock.instant().isBefore(e.getValue().until));
        if(states.size()>10000 && !states.containsKey(name)) return;
        states.compute(name,(k,s)->new State(s==null?1:s.failures+1,clock.instant().plus(duration)));
    }
    public void success(String name) { if(reset) states.remove(name); }
    void clear() { states.clear(); }
}
