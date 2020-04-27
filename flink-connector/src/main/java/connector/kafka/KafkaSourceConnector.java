package connector.kafka;

import org.apache.flink.api.common.serialization.SimpleStringSchema;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.connectors.kafka.FlinkKafkaConsumer010;
import org.apache.flink.streaming.connectors.kafka.FlinkKafkaProducer010;

import java.util.Properties;

/**
 * kafka connector,支持source/sink
 * https://ci.apache.org/projects/flink/flink-docs-release-1.10/dev/connectors/kafka.html
 */
public class KafkaSourceConnector {
    public static void main(String[] args) throws Exception {
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        env.setParallelism(2);
        DataStream<String> src = getKafkaSource(env);
        src.addSink(getKafkaSink());
        env.execute();
    }

    public static DataStream<String> getKafkaSource(StreamExecutionEnvironment env) {
        Properties properties = new Properties();
        properties.setProperty("bootstrap.servers", "kafkasit02broker01.cnsuning.com:9092,kafkasit02broker02.cnsuning.com:9092,kafkasit02broker03.cnsuning.com:9092");
        properties.setProperty("zookeeper.connect", "kafkasit02zk01.cnsuning.com:2181,kafkasit02zk02.cnsuning.com:2181,kafkasit02zk03.cnsuning.com:2181");
        properties.setProperty("group.id", "bi_dfp_order_rank_list_dfp");
        //开始消费的offset位置，支持latest, earliest, none
        properties.setProperty("auto.offset.reset", "latest");
        DataStream<String> stream = env
                .addSource(new FlinkKafkaConsumer010<>("bi_dfp_order_rank_list", new SimpleStringSchema(), properties));
        return stream;
    }

    public static FlinkKafkaProducer010 getKafkaSink() {
        return new FlinkKafkaProducer010("kafkasit02broker01.cnsuning.com:9092,kafkasit02broker02.cnsuning.com:9092," +
                "kafkasit02broker03.cnsuning.com:9092", "bi_dfp_oms_serv_unit", new SimpleStringSchema());
    }
}