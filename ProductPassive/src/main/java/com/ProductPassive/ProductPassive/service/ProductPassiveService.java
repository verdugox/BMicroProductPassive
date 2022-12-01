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

    public Mono<ProductPassive> create(ProductPassive productPassive){
        log.debug("create executed {}",productPassive);

        Mono<Client> client = findClientByDNI(productPassive.getDocument());
        log.debug("findClientByDNI executed {}" , client);
        log.info("findClientByDNI executed {}" , client);
        System.out.println("client " +client);

        return client.switchIfEmpty(Mono.error(new Exception("Client Not Found" + productPassive.getDocument())))
                .flatMap(client1 -> {
                    if(client1.getTypeClient().equals("PERSONAL")){
                        return productPassiveRepository.save(productPassive);
                    }
                    else{
                        return Mono.error(new Exception("Need type client PERSONAL because the type client actually is " + client1.getTypeClient()));
                    }
                    });


        //Valida si no existe, retornar un nuevo cliente.
        //client.defaultIfEmpty(new Client());


               /* client.con(user-> {
                    if(!isValid(user)){
                        return Mono.error(new InvalidUserException());
                    }
                    return Mono.just(user);
                })
*/
        //

        /*client.map(client1 -> {
            client1.getFirstName();
        })*/





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
