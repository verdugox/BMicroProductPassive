package com.ProductPassive.ProductPassive.repository;

import com.ProductPassive.ProductPassive.entity.ProductPassive;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import reactor.core.publisher.Mono;

public interface ProductPassiveRepository extends ReactiveMongoRepository<ProductPassive, String> {
    Mono<ProductPassive> findByIdentityAccount(String identityAccount);

}
