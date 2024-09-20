package capstone.capbackend.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import io.netty.channel.ChannelOption;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.http.codec.LoggingCodecSupport;
import org.springframework.web.reactive.function.client.ExchangeStrategies;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.netty.http.client.HttpClient;
import reactor.netty.resources.ConnectionProvider;

import java.time.Duration;

@Configuration
public class AppConfig {

    @Bean
    public WebClient webClient() {
        ConnectionProvider provider = ConnectionProvider.builder("custom-provider")
                .maxConnections(100)
                .maxIdleTime(Duration.ofSeconds(10))
                .maxLifeTime(Duration.ofSeconds(20))
                .pendingAcquireTimeout(Duration.ofMillis(10000))
                .pendingAcquireMaxCount(-1)
                .evictInBackground(Duration.ofSeconds(10))
                .lifo()
                .build();
        // Http Timeouts
        HttpClient httpClient = HttpClient
                .create()
                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 10000)
                .responseTimeout(Duration.ofSeconds(30));
        // MaxInMemorySize (Buffering data in memory)
        ExchangeStrategies exchangeStrategies = ExchangeStrategies.builder()
                .codecs(clientCodecConfigurer -> clientCodecConfigurer.defaultCodecs().maxInMemorySize(1024 * 1024 * 10))
                .build();
        // Logging
        exchangeStrategies
                .messageWriters()
                .stream()
                .filter(LoggingCodecSupport.class::isInstance)
                .forEach(httpMessageWriter -> ((LoggingCodecSupport) httpMessageWriter).setEnableLoggingRequestDetails(true));
        return WebClient.builder()
                .exchangeStrategies(exchangeStrategies)
                .clientConnector(new ReactorClientHttpConnector(HttpClient.create(provider)
                        .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 10000)
                        .responseTimeout(Duration.ofSeconds(30))))
                .build();
    }

    @Bean
    public ObjectMapper objectMapper() {
        ObjectMapper objectMapper = new ObjectMapper();
        // To Enable Serialization of LocalDateTime fields
        objectMapper.registerModule(new JavaTimeModule());
        return objectMapper;
    }

}
