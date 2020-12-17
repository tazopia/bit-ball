package spoon.inPlay.odds.service;


import com.rabbitmq.client.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import spoon.config.domain.Config;
import spoon.inPlay.config.domain.InPlayConfig;
import spoon.inPlay.odds.domain.InPlayDataDto;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.util.concurrent.TimeoutException;

@Slf4j
@RequiredArgsConstructor
@Service
public class InPlayEvent {

    private final ApplicationEventPublisher eventPublisher;

    private static boolean active = false;

    private static final ConnectionFactory factory;

    private Connection connection;

    private Channel channel;

    static {
        factory = new ConnectionFactory();
        factory.setHost(InPlayConfig.getHost());
        factory.setPort(InPlayConfig.getPort());
        factory.setUsername(InPlayConfig.getUsername());
        factory.setPassword(InPlayConfig.getPassword());
    }

    public void exchange() {
        active = true;

        try {
            if (connection != null) {
                connection.close();
            }
            if (channel != null) {
                channel.close();
            }
        } catch (IOException | TimeoutException e) {
            log.error("Connection과 Channel을 종료하지 못하였습니다.");
        }


        try {
            connection = factory.newConnection();
            channel = connection.createChannel();
            channel.exchangeDeclare(InPlayConfig.getExchange(), BuiltinExchangeType.FANOUT, false);
            String queueName = channel.queueDeclare().getQueue();
            channel.queueBind(queueName, InPlayConfig.getExchange(), "");

            log.info("===================================================");
            log.info("채널 : {}", channel.getChannelNumber());
            log.info("큐 : {}", queueName);
            log.info("===================================================");

            DeliverCallback callback = (consumerTag, delivery) -> {
                eventPublisher.publishEvent(InPlayDataDto.of(delivery.getBody()));
            };

            channel.basicConsume(queueName, true, callback, consumerTag -> {
            });
        } catch (IOException | TimeoutException e) {
            log.error("인플레이 서버에 문제가 발생하였습니다.", e);
            active = false;
        }

        log.debug("인플레이 서버를 시작하였습니다.");
    }

    @PostConstruct
    public void start() {
        if (!Config.getSysConfig().getSports().isCanInplay()) return;
        this.exchange();
    }

    public void stop() {
        try {
            if (connection != null) {
                connection.close();
            }
            if (channel != null) {
                channel.close();
            }
            active = false;
        } catch (IOException | TimeoutException e) {
            log.error("Connection과 Channel을 종료하지 못하였습니다.");
        }
    }

    // 서버 활성화 유무 체크
    public boolean status() {
        return active;
    }

}
