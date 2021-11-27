import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.DeliverCallback;
import java.nio.charset.StandardCharsets;
import java.util.Date;

public class Worker {

    private final static String QUEUE_NAME = "hello";

    public static void main(String[] argv) throws Exception {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("localhost");
        Connection connection = factory.newConnection();
        Channel channel = connection.createChannel();

        channel.queueDeclare(QUEUE_NAME, true, false, false, null);
        channel.basicQos(50);
        System.out.println(" [*] Waiting for messages. To exit press CTRL+C");

        DeliverCallback deliverCallback = (consumerTag, delivery) -> {
            String message = new String(delivery.getBody(), StandardCharsets.UTF_8);
            System.out.println(" [x] Received '" + message + "'");
            Date agora = new Date();
            try {
                doWork2(message);
              } catch (InterruptedException e) {            	  
				e.printStackTrace();
			}
            Date fim = new Date();
            long dif = (fim.getTime() - agora.getTime());  
            channel.basicAck(delivery.getEnvelope().getDeliveryTag(), false);
            System.out.println(" [x] Done " + message + " in " + dif + "ms");
             
            
        };
        boolean autoAck = false; // acknowledgment is covered below
        channel.basicConsume(QUEUE_NAME, autoAck, deliverCallback, consumerTag -> { });
    }
    
    private static void doWork(String task) throws InterruptedException {
        for (char ch: task.toCharArray()) {
            if (ch == '.') Thread.sleep(1000);
        }
    }
    
    private static void doWork2(String in) throws InterruptedException {
    	int i = Integer.valueOf(in);
    	if (i%2 == 0) {
            Thread.sleep(300);	
    	} else {
    	    Thread.sleep(2000);
    	}
    }
    
}