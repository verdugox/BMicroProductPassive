package com.ProductPassive.ProductPassive.service;

import com.ProductPassive.ProductPassive.entity.Client;
import com.ProductPassive.ProductPassive.entity.ProductPassive;
import com.ProductPassive.ProductPassive.repository.ProductPassiveRepository;
import com.ProductPassive.ProductPassive.web.mapper.ProductPassiveMapper;
import io.netty.channel.ChannelOption;
import io.netty.handler.timeout.ReadTimeoutHandler;
import io.netty.handler.timeout.WriteTimeoutHandler;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.netty.http.client.HttpClient;
import reactor.netty.tcp.TcpClient;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
@AllArgsConstructor
public class ProductPassiveService {

    @Autowired
    private ProductPassiveRepository productPassiveRepository;

    @Autowired
    private ProductPassiveMapper productPassiveMapper;

    /*public ProductPassiveService(WebClient.Builder webClientBuilder){
        this.webClient = webClientBuilder.baseUrl("http://localhost:9040/v1/client").build();
    }*/

    private final String BASE_URL = "http://localhost:9040";

    TcpClient tcpClient = TcpClient.create()
            .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 3000)
            .doOnConnected(connection ->
                    connection.addHandlerLast(new ReadTimeoutHandler(3))
                            .addHandlerLast(new WriteTimeoutHandler(3)));
    private final WebClient client = WebClient.builder()
            .baseUrl(BASE_URL)
            .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
            .defaultHeader(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE)
            .clientConnector(new ReactorClientHttpConnector(HttpClient.from(tcpClient)))  // timeout
            .build();

    public Mono<Client> findClientByDNI(String identityDni){
        return this.client.get().uri("/v1/client/findByIdentityDni/{identityDni}",identityDni)
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .retrieve()
                .bodyToMono(Client.class);
    }


    public Flux<ProductPassive> findAll(){
        log.debug("findAll executed");
        return productPassiveRepository.findAll();
    }

    public Mono<ProductPassive> findById(String productPassiveId){
        log.debug("findById executed {}" , productPassiveId);
        return productPassiveRepository.findById(productPassiveId);
    }

    public Mono<ProductPassive> findByIdentityAccount(String identityAccount){
        log.debug("findByIdentityAccount executed {}" , identityAccount);
        return productPassiveRepository.findByIdentityAccount(identityAccount);
    }

    public Mono<ProductPassive> findByTypeAccountAndDocument(String typeAccount, String document){
        log.debug("findByTypeAccountAndDocument executed {}" , typeAccount, document);
        return productPassiveRepository.findByTypeAccountAndDocument(typeAccount, document);
    }


    public Mono<ProductPassive> create(ProductPassive productPassive){
        log.debug("create executed {}",productPassive);

        Mono<Client> client = findClientByDNI(productPassive.getDocument());

        log.debug("findClientByDNI executed {}" , client);
        log.info("findClientByDNI executed {}" , client);
        System.out.println("client " +client);


        return client.switchIfEmpty(Mono.error(new Exception("Client Not Found" + productPassive.getDocument())))
                .flatMap(client1 -> {
                    if(client1.getTypeClient().equals("PERSONAL")){
                        return findByTypeAccountAndDocument(productPassive.getTypeAccount(),productPassive.getDocument())
                                .flatMap(account1 -> {
                                    if(account1.getTypeAccount().equals("AHORRO") || account1.getTypeAccount().equals("CORRIENTE")){
                                        return Mono.error(new Exception("No puede tener más de una cuenta de AHORRO O CORRIENTE" + productPassive.getTypeAccount()));
                                    }
                                    else{
                                        return productPassiveRepository.save(productPassive);
                                    }
                                }).switchIfEmpty(productPassiveRepository.save(productPassive));
                    }
                    else
                    {
                        if(productPassive.getTypeAccount().equals("CORRIENTE")){
                            return productPassiveRepository.save(productPassive);
                        }else{
                            return Mono.error(new Exception("No puede tener una cuenta de AHORRO O PLAZO FIJO" + productPassive.getTypeAccount()));
                        }
                    }

                });

    }

    public Mono<ProductPassive> update(String productPassiveId, ProductPassive productPassive){
        log.debug("update executed {}:{}", productPassiveId, productPassive);
        return productPassiveRepository.findById(productPassiveId)
                .flatMap(dbProductPassive -> {
                    productPassiveMapper.update(dbProductPassive, productPassive);
                    return productPassiveRepository.save(dbProductPassive);
                });
    }

    public Mono<ProductPassive>delete(String productPassiveId){
        log.debug("delete executed {}",productPassiveId);
        return productPassiveRepository.findById(productPassiveId)
                .flatMap(existingProductPassive -> productPassiveRepository.delete(existingProductPassive)
                        .then(Mono.just(existingProductPassive)));
    }


}
