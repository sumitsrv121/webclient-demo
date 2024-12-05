package com.srv.sumit.webclient_demo;

import com.fasterxml.jackson.core.type.TypeReference;
import com.srv.sumit.webclient_demo.dto.ProductDTO;
import com.srv.sumit.webclient_demo.dto.ProductRequestDTO;
import com.srv.sumit.webclient_demo.util.HttpClientHelper;
import com.srv.sumit.webclient_demo.util.RestClientHelper;
import com.srv.sumit.webclient_demo.util.WebClientHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

@SpringBootApplication
public class WebclientDemoApplication implements CommandLineRunner {
    @Autowired
    private WebClientHelper webClientHelper;
    @Autowired
    private HttpClientHelper httpClientHelper;
    @Autowired
    private RestClientHelper restClientHelper;

    public static void main(String[] args) {
        SpringApplication.run(WebclientDemoApplication.class, args);
    }

    @Override
    public void run(String... args) throws Exception {
        MultiValueMap<String, String> queryParams = new LinkedMultiValueMap<>();
        queryParams.add("id", "1");
        queryParams.add("id", "7");
        List<ProductDTO> list = webClientHelper
                .getList("https://api.restful-api.dev","/objects", ProductDTO.class, null, null,queryParams);
        System.out.println(list);

        ProductDTO productDTO = webClientHelper.get("https://api.restful-api.dev", "/objects/{id}", ProductDTO.class, Map.of(), Map.of("id", "1"), new LinkedMultiValueMap<>());
        System.out.println(productDTO);

        ProductRequestDTO.ProductDataDTO productDataDTO = new ProductRequestDTO.ProductDataDTO();
        productDataDTO.setCpuModel("Intel Core i9");
        productDataDTO.setPrice(1849.99);
        productDataDTO.setYear(2019);
        productDataDTO.setHardDiskSize("1 TB");
        ProductRequestDTO requestDTO = new ProductRequestDTO();
		requestDTO.setData(productDataDTO);
        requestDTO.setName("Apple MacBook Pro 16");

        Map<?,?> post = webClientHelper.post("https://api.restful-api.dev", "/objects", Map.class, null, null, requestDTO);
        Map<?,?> post1 = httpClientHelper.post("https://api.restful-api.dev", "/objects", null, requestDTO, null,  Map.class);
        Map<?,?> post2 = restClientHelper.post("https://api.restful-api.dev", "/objects", null, requestDTO, null,  Map.class);
        Map<?,?>[] post3 = restClientHelper.post("https://api.restful-api.dev", "/objects", null, requestDTO, null, Map[].class);
        System.out.println(post);
        System.out.println(post1);
        System.out.println(post2);
    }
}
