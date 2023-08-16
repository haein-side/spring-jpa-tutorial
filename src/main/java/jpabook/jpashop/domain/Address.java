package jpabook.jpashop.domain;

import lombok.Getter;

import javax.persistence.Embeddable;

@Embeddable
@Getter // 값 타입은 변경 불가하게 설계해야 : 값 타입은 식별자의 개념이 없어 변경하면 추적이 어렵기 때문! (다 삭제하고 새로 저장 필요)
public class Address {
    private String city;
    private String street;
    private String zipcode;

    protected Address() {
    }

    public Address(String city, String street, String zipcode) {
        this.city = city;
        this.street = street;
        this.zipcode = zipcode;
    }
}
