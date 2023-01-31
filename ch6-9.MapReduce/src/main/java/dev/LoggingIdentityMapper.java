package dev;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.hadoop.mapreduce.Mapper;

import java.io.IOException;

public class LoggingIdentityMapper<KEYIN, VALUEIN, KEYOUT, VALUEOUT> extends Mapper<KEYIN, VALUEIN, KEYOUT, VALUEOUT> {
    private static final Log LOG = LogFactory.getLog(LoggingIdentityMapper.class);

    @Override
    @SuppressWarnings("unchecked")
    protected void map(KEYIN key, VALUEIN value, Context context) throws IOException, InterruptedException {
        // 표준 출력 파일에 로깅
        System.out.println("Map key: " + key);

        // syslog 파일에 로깅
        LOG.info("Map key: "+ key);
        if (LOG.isDebugEnabled()){
            LOG.debug("Map value: "+ value);
        }
        context.write((KEYOUT) key, (VALUEOUT) value);
    }
}
