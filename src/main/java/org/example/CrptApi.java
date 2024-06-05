package org.example;

import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;

import java.util.List;
import java.util.concurrent.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;


public class CrptApi {

    private TimeUnit timeUnit;
    private int requestLimit;
    private Semaphore semaphore;
    private ExecutorService executor;
    private long timeUnitMillis;


    public CrptApi(TimeUnit timeUnit, int requestLimit) {
        this.timeUnit = timeUnit;
        this.requestLimit = requestLimit;
        this.executor = new ThreadPoolExecutor(
                requestLimit, requestLimit, 0L, TimeUnit.MILLISECONDS,
                new LinkedBlockingQueue<Runnable>(requestLimit));
        this.semaphore = new Semaphore(requestLimit);
        this.timeUnitMillis = timeUnit.toMillis(1);
    }

    public boolean createDocument(Document document, String signature)  {

        try {
            semaphore.acquire();

            this.executor.submit(new Callable() {

                private long startTimeMillis;

                @Override
                public Object call() throws Exception {
                    try {

                        this.startTimeMillis = System.currentTimeMillis();

                        CloseableHttpClient httpClient = HttpClientBuilder.create().build();
                        HttpPost request = new HttpPost("https://ismp.crpt.ru/api/v3/lk/documents/create");

                        Gson gson = new Gson();
                        String documentJson = gson.toJson(document);
                        request.setEntity(new StringEntity(documentJson));
                        HttpResponse response = httpClient.execute(request);

                        return response.getStatusLine().getStatusCode() == HttpStatus.SC_OK;
                    } catch (Exception ex) {
                        System.out.println(ex);
                    } finally {
                        long endTimeMillis = System.currentTimeMillis();
                        long runTime = endTimeMillis - startTimeMillis;
                        if (runTime < timeUnitMillis) {
                            System.out.println("Thread sleep for: " + (timeUnitMillis - runTime));
                            Thread.sleep(timeUnitMillis - runTime);
                        } else {
                            System.out.println("Thread run time: " + (runTime));
                        }
                        semaphore.release();
                    }

                    return false;
                }
            });
        } catch (InterruptedException e) {
            System.out.println(e);
            return false;
        }
        return true;
    }


    public static class Document {

        private DocumentDescription description;
        @SerializedName(value = "doc_id")
        private String docId;
        @SerializedName(value = "doc_status")
        private String docStatus;
        private boolean importRequest;
        @SerializedName(value = "owner_inn")
        private String ownerInn;
        @SerializedName(value = "ticipant_inn")
        private String ticipantInn;
        @SerializedName(value = "producer_inn")
        private String producerInn;
        @SerializedName(value = "production_date")
        private String productionDate;
        @SerializedName(value = "production_type")
        private String productionType;
        private String products;

        List<DocumentProduct> listProducts;

        public static class DocumentDescription {
            private String participantInn;

            public DocumentDescription(String participantInn) {
                this.participantInn = participantInn;
            }
        }
         public static class DocumentProduct {
             private String certificate_document;
             @SerializedName(value = "certificate_document_date")
             private String certificateDocumentDate;
             private String certificate_document_number;
             private String owner_inn;
             private String producer_inn;
             private String production_date;
             @SerializedName(value = "tnved_code")
             private String tnvedCode;
             @SerializedName(value = "uit_code")
             private String uitCode;
             @SerializedName(value = "uitu_code")
             private String uituCode;

             public DocumentProduct(String certificate_document, String certificateDocumentDate,
                                    String certificate_document_number, String owner_inn,
                                    String producer_inn, String production_date, String tnvedCode,
                                    String uitCode, String uituCode) {
                 this.certificate_document = certificate_document;
                 this.certificateDocumentDate = certificateDocumentDate;
                 this.certificate_document_number = certificate_document_number;
                 this.owner_inn = owner_inn;
                 this.producer_inn = producer_inn;
                 this.production_date = production_date;
                 this.tnvedCode = tnvedCode;
                 this.uitCode = uitCode;
                 this.uituCode = uituCode;
             }

             public String getCertificate_document() {
                 return certificate_document;
             }

             public void setCertificate_document(String certificate_document) {
                 this.certificate_document = certificate_document;
             }

             public String getCertificateDocumentDate() {
                 return certificateDocumentDate;
             }

             public void setCertificateDocumentDate(String certificateDocumentDate) {
                 this.certificateDocumentDate = certificateDocumentDate;
             }

             public String getCertificate_document_number() {
                 return certificate_document_number;
             }

             public void setCertificate_document_number(String certificate_document_number) {
                 this.certificate_document_number = certificate_document_number;
             }

             public String getOwner_inn() {
                 return owner_inn;
             }

             public void setOwner_inn(String owner_inn) {
                 this.owner_inn = owner_inn;
             }

             public String getProducer_inn() {
                 return producer_inn;
             }

             public void setProducer_inn(String producer_inn) {
                 this.producer_inn = producer_inn;
             }

             public String getProduction_date() {
                 return production_date;
             }

             public void setProduction_date(String production_date) {
                 this.production_date = production_date;
             }

             public String getTnvedCode() {
                 return tnvedCode;
             }

             public void setTnvedCode(String tnvedCode) {
                 this.tnvedCode = tnvedCode;
             }

             public String getUitCode() {
                 return uitCode;
             }

             public void setUitCode(String uitCode) {
                 this.uitCode = uitCode;
             }

             public String getUituCode() {
                 return uituCode;
             }

             public void setUituCode(String uituCode) {
                 this.uituCode = uituCode;
             }
         }


        public Document(String participantInn, String docId, String docStatus, boolean importRequest, String ownerInn) {
            this.description = new DocumentDescription(participantInn);
            this.docId = docId;
            this.docStatus = docStatus;
            this.importRequest = importRequest;
            this.ownerInn = ownerInn;
        }

        public DocumentDescription getDescription() {
            return description;
        }

        public void setDescription(DocumentDescription description) {
            this.description = description;
        }

        public List<DocumentProduct> getListProducts() {
            return listProducts;
        }

        public void setListProducts(List<DocumentProduct> listProducts) {
            this.listProducts = listProducts;
        }

        public String getDocId() {
            return docId;
        }

        public void setDocId(String docId) {
            this.docId = docId;
        }

        public String getDocStatus() {
            return docStatus;
        }

        public void setDocStatus(String docStatus) {
            this.docStatus = docStatus;
        }

        public boolean isImportRequest() {
            return importRequest;
        }

        public void setImportRequest(boolean importRequest) {
            this.importRequest = importRequest;
        }

        public String getOwnerInn() {
            return ownerInn;
        }

        public void setOwnerInn(String ownerInn) {
            this.ownerInn = ownerInn;
        }

        public String getTicipantInn() {
            return ticipantInn;
        }

        public void setTicipantInn(String ticipantInn) {
            this.ticipantInn = ticipantInn;
        }

        public String getProducerInn() {
            return producerInn;
        }

        public void setProducerInn(String producerInn) {
            this.producerInn = producerInn;
        }

        public String getProductionDate() {
            return productionDate;
        }

        public void setProductionDate(String productionDate) {
            this.productionDate = productionDate;
        }

        public String getProductionType() {
            return productionType;
        }

        public void setProductionType(String productionType) {
            this.productionType = productionType;
        }

        public String getProducts() {
            return products;
        }

        public void setProducts(String products) {
            this.products = products;
        }
    }

}
