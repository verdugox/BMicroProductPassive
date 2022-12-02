package com.ProductPassive.ProductPassive.web;

import com.ProductPassive.ProductPassive.entity.ProductPassive;
import com.ProductPassive.ProductPassive.service.ProductPassiveService;
import com.ProductPassive.ProductPassive.web.mapper.ProductPassiveMapper;
import com.ProductPassive.ProductPassive.web.model.ProductPassiveModel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import javax.validation.Valid;
import java.net.URI;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/v1/productPassive")
public class ProductPassiveController {

    @Value("${spring.application.name}")
    String name;

    @Value("${server.port}")
    String port;

    @Autowired
    private ProductPassiveService productPassiveService;


    @Autowired
    private ProductPassiveMapper productPassiveMapper;


    @GetMapping("/findAll")
    public Mono<ResponseEntity<Flux<ProductPassiveModel>>> getAll(){
        log.info("getAll executed");
        return Mono.just(ResponseEntity.ok()
                .body(productPassiveService.findAll()
                        .map(productPassive -> productPassiveMapper.entityToModel(productPassive))));
    }

    @GetMapping("/findById/{id}")
    public Mono<ResponseEntity<ProductPassiveModel>> findById(@PathVariable String id){
        log.info("findById executed {}", id);
        Mono<ProductPassive> response = productPassiveService.findById(id);
        return response
                .map(productPassive -> productPassiveMapper.entityToModel(productPassive))
                .map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    @GetMapping("/findByIdentityAccount/{identityAccount}")
    public Mono<ResponseEntity<ProductPassiveModel>> findByIdentityAccount(@PathVariable String identityAccount){
        log.info("findByIdentityAccount executed {}", identityAccount);
        Mono<ProductPassive> response = productPassiveService.findByIdentityAccount(identityAccount);
        return response
                .map(productPassive -> productPassiveMapper.entityToModel(productPassive))
                .map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    @PostMapping
    public Mono<ResponseEntity<ProductPassiveModel>> create(@Valid @RequestBody ProductPassiveModel request){
        log.info("create executed {}", request);
        return productPassiveService.create(productPassiveMapper.modelToEntity(request))
                .map(productPassive -> productPassiveMapper.entityToModel(productPassive))
                .flatMap(p -> Mono.just(ResponseEntity.created(URI.create(String.format("http://%s:%s/%s/%s", name, port, "productPassive", p.getId())))
                        .body(p)))
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}")
    public Mono<ResponseEntity<ProductPassiveModel>> updateById(@PathVariable String id, @Valid @RequestBody ProductPassiveModel request){
        log.info("updateById executed {}:{}", id, request);
        return productPassiveService.update(id, productPassiveMapper.modelToEntity(request))
                .map(productPassive -> productPassiveMapper.entityToModel(productPassive))
                .flatMap(p -> Mono.just(ResponseEntity.created(URI.create(String.format("http://%s:%s/%s/%s", name, port, "productPassive", p.getId())))
                        .body(p)))
                .defaultIfEmpty(ResponseEntity.badRequest().build());
    }

    @DeleteMapping("/{id}")
    public Mono<ResponseEntity<Void>> deleteById(@PathVariable String id){
        log.info("deleteById executed {}", id);
        return productPassiveService.delete(id)
                .map( r -> ResponseEntity.ok().<Void>build())
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }

}
