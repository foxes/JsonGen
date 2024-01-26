package com.hmradd;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.*;

public class JsonFileCreator {
    private static Set<String> usedToteIds = new HashSet<>();
    public static void main(String[] args) {
        // Set the configurable directory path
        String directoryPath = "C:\\jsonGenerator";

        // Set the number of JSON files to generate for I_PickOrder and I_PalletOrder
        int numberOfPickOrderFiles = 0;
        int numberOfPalletOrderFiles = 5;

        // Generate I_PickOrder JSON files
        createJsonFiles(directoryPath, numberOfPickOrderFiles, "I_PickOrder");

        // Generate I_PalletOrder JSON files
        createJsonFiles(directoryPath, numberOfPalletOrderFiles, "I_PalletOrder");
    }

    private static void createJsonFiles(String directoryPath, int numberOfFiles, String fileType) {

        Set<String> pickTUs = readPickTUsFromFile("C:\\jsonGenerator\\pickTUs.txt");


        for (int i = 1; i <= numberOfFiles; i++) {

            // Check if there are remaining unused pickTUs
            if (pickTUs.isEmpty()) {
                System.out.println("No more available pickTUs");
                break;
            }



            // Generate the filename with a timestamp
            String fileName = fileType + "_" + new SimpleDateFormat("yyyyMMddHHmmss").format(new Date()) + "_" + i + ".json";
            String filePath = directoryPath + File.separator + fileName;

            // Create JSON content based on file type
            ObjectMapper objectMapper = new ObjectMapper();
            try {
                if ("I_PickOrder".equals(fileType)) {
                    PickOrder pickOrder = createPickOrder();
                    String jsonContent = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(pickOrder);
                    writeToFile(filePath, jsonContent);
                    System.out.println("I_PickOrder JSON file created at: " + filePath);
                } else if ("I_PalletOrder".equals(fileType)) {
                    PalletOrder palletOrder = createPalletOrder(pickTUs);
                    String jsonContent = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(palletOrder);
                    writeToFile(filePath, jsonContent);
                    System.out.println("I_PalletOrder JSON file created at: " + filePath);
                } else {
                    System.out.println("Unsupported file type: " + fileType);
                }
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

    private static PalletOrder createPalletOrder(Set<String> pickTUs) {

        PalletOrder palletOrder = new PalletOrder();
        PalletOrderData palletOrderData = new PalletOrderData();
        palletOrderData.setMessageId("PO-014523131");
        palletOrderData.setMessageTimestamp("2021-08-24T11:34:42");
        palletOrderData.setPalletOrderId(generateRandom7DigitInt());
        palletOrderData.setPriority("200");
        palletOrderData.setDeliveryDate("2021-08-24T11:34:41");

        OrderLine[] orderLines = new OrderLine[2]; // Assuming you want 2 OrderLines

        // Assign unique toteIds to each OrderLine
        for (int i = 0; i < orderLines.length; i++) {
            String selectedToteId = getRandomUnusedToteId(pickTUs);
            usedToteIds.add(selectedToteId); // Mark the selected pickTU as used
            orderLines[i] = new OrderLine(generateRandom8DigitNumber(), selectedToteId);
        }

//        OrderLine[] orderLines = {
//                new OrderLine(generateRandom8DigitNumber(), "7142740"),
//                new OrderLine(generateRandom8DigitNumber(), "7131734")
//        };
        palletOrderData.setOrderLines(orderLines);

        palletOrder.setPalletOrder(palletOrderData);

        return palletOrder;
    }


    private static Set<String> readPickTUsFromFile(String filePath) {
        Set<String> pickTUs = new HashSet<>();
        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = br.readLine()) != null) {
                // Split the comma-delimited values
                String[] toteIds = line.split(",\\s*");
                pickTUs.addAll(Arrays.asList(toteIds));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return pickTUs;
    }

    private static String getRandomUnusedToteId(Set<String> pickTUs) {
        // Remove used ToteIds from the available set
        pickTUs.removeAll(usedToteIds);

        // Get a random ToteId from the remaining set

        return pickTUs.stream().findAny().orElseThrow();
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

    private static int generateRandom7DigitInt() {
        Random random = new Random();
        int randomNum = random.nextInt(9000000) + 1000000;
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
}

class PalletOrder {
    @JsonProperty("PalletOrder")
    private PalletOrderData palletOrder;

    public PalletOrderData getPalletOrder() {
        return palletOrder;
    }

    public void setPalletOrder(PalletOrderData palletOrder) {
        this.palletOrder = palletOrder;
    }
}

class PalletOrderData {
    @JsonProperty("MessageId")
    private String messageId;
    @JsonProperty("MessageTimestamp")
    private String messageTimestamp;
    @JsonProperty("PalletOrderId")
    private int palletOrderId;
    @JsonProperty("Priority")
    private String priority;
    @JsonProperty("DeliveryDate")
    private String deliveryDate;
    @JsonProperty("OrderLines")
    private OrderLine[] orderLines;

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

    public int getPalletOrderId() {
        return palletOrderId;
    }

    public void setPalletOrderId(int palletOrderId) {
        this.palletOrderId = palletOrderId;
    }

    public String getPriority() {
        return priority;
    }

    public void setPriority(String priority) {
        this.priority = priority;
    }

    public String getDeliveryDate() {
        return deliveryDate;
    }

    public void setDeliveryDate(String deliveryDate) {
        this.deliveryDate = deliveryDate;
    }

    public OrderLine[] getOrderLines() {
        return orderLines;
    }

    public void setOrderLines(OrderLine[] orderLines) {
        this.orderLines = orderLines;
    }
}

class OrderLine {
    @JsonProperty("OrderLineId")
    private int orderLineId;
    @JsonProperty("ToteId")
    private String toteId;

    public OrderLine() {
        // Default constructor
    }

    public OrderLine(int orderLineId, String toteId) {
        this.orderLineId = orderLineId;
        this.toteId = toteId;
    }
}
