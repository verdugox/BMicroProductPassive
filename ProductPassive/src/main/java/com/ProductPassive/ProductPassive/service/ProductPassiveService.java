package com.ProductPassive.ProductPassive.service;

import com.ProductPassive.ProductPassive.entity.ProductPassive;
import com.ProductPassive.ProductPassive.repository.ProductPassiveRepository;
import com.ProductPassive.ProductPassive.web.mapper.ProductPassiveMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class ProductPassiveService {

    @Autowired
    private ProductPassiveRepository productPassiveRepository;

    @Autowired
    private ProductPassiveMapper productPassiveMapper;

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
        return productPassiveRepository.save(productPassive);
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
