package org.example;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
public class Main {
    public static void main(String[] args) {
        //TIP Press <shortcut actionId="ShowIntentionActions"/> with your caret at the highlighted text
        // to see how IntelliJ IDEA suggests fixing it.
        System.out.println("Hello and welcome!");

        CrptApi crptApi = new CrptApi(TimeUnit.SECONDS, 10);


        CrptApi.Document document = new CrptApi.Document(
                "1234567",
                "doc-id-12313",
                "draft",
                true,
                "456456456");

        CrptApi.Document.DocumentDescription documentDescription =
                new CrptApi.Document.DocumentDescription("84455412");

        document.setDescription(documentDescription);

        CrptApi.Document.DocumentProduct documentProduct = new CrptApi.Document.DocumentProduct(
                "111111",
                "22222",
                "33333",
                "4444444",
                "55555",
                "666666",
                "777",
                "888888",
                "999999");

        List<CrptApi.Document.DocumentProduct> documentProductList = new ArrayList<>();

        documentProductList.add(documentProduct);
        document.setListProducts(documentProductList);

        String signature = "";
        boolean result = crptApi.createDocument(document, signature);

        System.out.println("Result: " + result);
    }
}