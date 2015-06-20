import ch.qos.logback.classic.encoder.PatternLayoutEncoder
import ch.qos.logback.core.ConsoleAppender
import static ch.qos.logback.classic.Level.*

def STDOUT = 'STDOUT'

appender STDOUT, ConsoleAppender, {
    encoder(PatternLayoutEncoder) {
        pattern = '[%-5level %logger{5}] - %msg%n'
    }
}

root WARN, ['STDOUT']
logger 'com.blueberry', TRACE, [STDOUT], false
logger 'org.hibernate.SQL', DEBUG, [STDOUT], false           // DEBUG for Equivalent of show_sql
logger 'org.hibernate.type', DEBUG, [STDOUT], false          // TRACE for parameter values bound to SQL
