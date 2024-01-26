import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;

public class JsonFileCreator {
    public static void main(String[] args) {

        // Set the configurable directory path
        String directoryPath = "C:\\jsonGenerator";

        // Set the number of JSON files to generate
        int numberOfFiles = 10;

        for (int i = 1; i <= numberOfFiles; i++) {
            // Generate the filename with a timestamp
            String fileName = "I_InboundOrder_" + new SimpleDateFormat("yyyyMMddHHmmss").format(new Date()) + "_" + i + ".json";

            // Combine the directory path and filename
            String filePath = directoryPath + File.separator + fileName;

            // Create JSON content
            ObjectMapper objectMapper = new ObjectMapper();
            try {
                PickOrder pickOrder = createPickOrder();
                String jsonContent = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(pickOrder);

                // Write JSON content to the file
                writeToFile(filePath, jsonContent);

                System.out.println("JSON file created at: " + filePath);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private static PickOrder createPickOrder() {
        PickOrder pickOrder = new PickOrder();
        PickOrderData pickOrderData = new PickOrderData();
        pickOrderData.setMessageId("PK-392618");
        pickOrderData.setMessageTimestamp("2023-05-19T07:23:09");
        pickOrderData.setPickOrderId(generateRandom7DigitNumber());
        pickOrderData.setAction("SAVE");
        pickOrderData.setSingleUnitOrder(false);
        pickOrderData.setPriority(200);
        pickOrderData.setDeliveryDate("2023-05-21T07:23:09");
        pickOrderData.setSendToPacking(false);

        PickOrderLine[] pickOrderLines = {
                new PickOrderLine(generateRandom8DigitNumber(), "69000000000001", 1),
                new PickOrderLine(generateRandom8DigitNumber(), "69000000000001", 1)
        };
        pickOrderData.setPickOrderLines(pickOrderLines);

        pickOrder.setPickOrder(pickOrderData);

        return pickOrder;
    }

    private static String generateRandom7DigitNumber() {
        Random random = new Random();
        int randomNum = random.nextInt(9000000) + 1000000;
        return String.valueOf(randomNum);
    }

    private static int generateRandom8DigitNumber() {
        Random random = new Random();
        int randomNum = random.nextInt(90000000) + 10000000;
        return randomNum;
    }



    private static void writeToFile(String filePath, String content) throws IOException {
        File file = new File(filePath);
        file.getParentFile().mkdirs();
        file.createNewFile();

        // Use try-with-resources to automatically close the FileWriter
        try (java.io.FileWriter fileWriter = new java.io.FileWriter(file)) {
            fileWriter.write(content);
        }
    }
}

class PickOrder {
    @JsonProperty("PickOrder")
    private PickOrderData pickOrder;

    // Getters and setters

    public PickOrderData getPickOrder() {
        return pickOrder;
    }

    public void setPickOrder(PickOrderData pickOrder) {
        this.pickOrder = pickOrder;
    }
}

class PickOrderData {
    @JsonProperty("MessageId")
    private String messageId;
    @JsonProperty("MessageTimestamp")
    private String messageTimestamp;
    @JsonProperty("PickOrderId")
    private String pickOrderId;
    @JsonProperty("Action")
    private String action;
    @JsonProperty("SingleUnitOrder")
    private boolean singleUnitOrder;
    @JsonProperty("Priority")
    private int priority;
    @JsonProperty("DeliveryDate")
    private String deliveryDate;
    @JsonProperty("SendToPacking")
    private boolean sendToPacking;
    @JsonProperty("PickOrderLines")
    private PickOrderLine[] pickOrderLines;

    // Getters and setters

    public String getMessageId() {
        return messageId;
    }

    public void setMessageId(String messageId) {
        this.messageId = messageId;
    }

    public String getMessageTimestamp() {
        return messageTimestamp;
    }

    public void setMessageTimestamp(String messageTimestamp) {
        this.messageTimestamp = messageTimestamp;
    }

    public String getPickOrderId() {
        return pickOrderId;
    }

    public void setPickOrderId(String pickOrderId) {
        this.pickOrderId = pickOrderId;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public boolean isSingleUnitOrder() {
        return singleUnitOrder;
    }

    public void setSingleUnitOrder(boolean singleUnitOrder) {
        this.singleUnitOrder = singleUnitOrder;
    }

    public int getPriority() {
        return priority;
    }

    public void setPriority(int priority) {
        this.priority = priority;
    }

    public String getDeliveryDate() {
        return deliveryDate;
    }

    public void setDeliveryDate(String deliveryDate) {
        this.deliveryDate = deliveryDate;
    }

    public boolean isSendToPacking() {
        return sendToPacking;
    }

    public void setSendToPacking(boolean sendToPacking) {
        this.sendToPacking = sendToPacking;
    }

    public PickOrderLine[] getPickOrderLines() {
        return pickOrderLines;
    }

    public void setPickOrderLines(PickOrderLine[] pickOrderLines) {
        this.pickOrderLines = pickOrderLines;
    }
}

class PickOrderLine {
    @JsonProperty("PickOrderLineId")
    private int pickOrderLineId;
    @JsonProperty("Gtin")
    private String gtin;
    @JsonProperty("Quantity")
    private int quantity;

    public PickOrderLine() {
        // Default constructor
    }

    public PickOrderLine(int pickOrderLineId, String gtin, int quantity) {
        this.pickOrderLineId = pickOrderLineId;
        this.gtin = gtin;
        this.quantity = quantity;
    }

    // Getters and setters
}
