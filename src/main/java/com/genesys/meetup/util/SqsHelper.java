package com.genesys.meetup.util;

import com.amazon.sqs.javamessaging.AmazonSQSMessagingClientWrapper;
import com.amazon.sqs.javamessaging.ProviderConfiguration;
import com.amazon.sqs.javamessaging.SQSConnection;
import com.amazon.sqs.javamessaging.SQSConnectionFactory;
import com.amazonaws.auth.DefaultAWSCredentialsProviderChain;
import com.amazonaws.client.builder.AwsClientBuilder;
import com.amazonaws.services.sqs.AmazonSQSClientBuilder;
import com.amazonaws.services.sqs.model.CreateQueueRequest;
import com.google.common.base.Strings;
import lombok.extern.slf4j.Slf4j;

import javax.jms.JMSException;
import java.util.HashMap;
import java.util.Map;

@Slf4j
public final class SqsHelper {

  private SqsHelper() {
  }

  public static SQSConnectionFactory createJmsConnectionFactory(String url, String region) {
    DefaultAWSCredentialsProviderChain awsCredentialsProviderChain = DefaultAWSCredentialsProviderChain.getInstance();

    if (!Strings.isNullOrEmpty(url) && !Strings.isNullOrEmpty(region)) {
      return new SQSConnectionFactory(
          new ProviderConfiguration(),
          AmazonSQSClientBuilder.standard()
              .withCredentials(awsCredentialsProviderChain)
              .withEndpointConfiguration(new AwsClientBuilder.EndpointConfiguration(url, region))
      );
    }

    return new SQSConnectionFactory(
        new ProviderConfiguration(),
        AmazonSQSClientBuilder.standard().withCredentials(awsCredentialsProviderChain)
    );
  }

  public static void createSqsQueue(String url, String region, String queueName) {
    SQSConnection connection = null;
    try {
      connection = createJmsConnectionFactory(url, region).createConnection();

      AmazonSQSMessagingClientWrapper wrappedAmazonSQSClient = connection.getWrappedAmazonSQSClient();

      if (!wrappedAmazonSQSClient.queueExists(queueName)) {
        if (queueName.contains("fifo")) {
          Map<String, String> attributes = new HashMap<String, String>();
          attributes.put("FifoQueue", "true");
          attributes.put("ContentBasedDeduplication", "true");
          wrappedAmazonSQSClient.createQueue(new CreateQueueRequest().withQueueName(queueName).withAttributes(attributes));
        } else {
          wrappedAmazonSQSClient.createQueue(queueName);
        }
      }
    } catch (JMSException e) {
      log.error("Can't create queue for SQS", e);
      throw new RuntimeException(e);
    } finally {
      Actions.silent(connection::close);
    }
  }
}
