package com.jdaalba.eda.resilience.outbox;

import com.jdaalba.eda.resilience.ConnectionProvider;
import com.jdaalba.eda.resilience.messaging.Sink;
import com.jdaalba.eda.resilience.messaging.impl.KafkaSink;
import com.jdaalba.eda.resilience.serde.JsonMapper;
import com.jdaalba.eda.resilience.vo.OutputEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.SQLException;
import java.time.Instant;

public class DbListener {

    private static final Logger log = LoggerFactory.getLogger(DbListener.class);

    private final Sink sink = new KafkaSink();

    public void execute() {
        try (
                var conn = ConnectionProvider.getConnection();
                var stmt = conn.createStatement();
                var updateStmt = conn.prepareStatement("update outbox set processed = true where id = ?")
        ) {
            stmt.execute("""
                    select *
                    from outbox
                    where not processed
                    """);
            var rs = stmt.getResultSet();
            while (rs.next()) {
                var id = rs.getLong("id");
                var event = JsonMapper.asEvent(rs.getBytes("message"), rs.getString("class"));
                var destination = rs.getString("destination");
                var emittedOn = Instant.ofEpochMilli(rs.getTimestamp("emitted_on").getTime());
                log.info("Event: {}, to '{}' and emitted on {}", event, destination, emittedOn);
                sink.send(new OutputEvent(event, destination));
                updateStmt.setLong(1, id);
                updateStmt.execute();
            }
        } catch (SQLException e) {
            log.error("SQL error: ", e);
            throw new OutboxException(e);
        }
    }
}
