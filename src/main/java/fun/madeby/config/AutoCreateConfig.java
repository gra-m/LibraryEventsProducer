package fun.madeby.config;
//not recommended for production
/* these partitions and replicas are matching the three brokers
 ./kafka-server-start.sh ../config/server.properties
    ./kafka-server-start.sh ../config/server-1.properties
      ./kafka-server-start.sh ../config/server-2.properties
      currently running in the background.

      and matching the admin properties in the application-local.yml

      admin:
      properties:
        bootstrap.servers: localhost:9092, localhost:9093, localhost:9094

   run this in terminal to check created:
   ┌──(kali㉿kaliPerm)-[~/LocalTools/kafka3.4.0/bin]
└─$ ./kafka-topics.sh --bootstrap-server localhost:9092 --list

 */

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
@Profile("local")
public class AutoCreateConfig {
      @Bean
      public NewTopic libEvents(){

           return TopicBuilder.name("lib-events")
                .partitions(3)
                .replicas(3)
                .build();
      }
}
