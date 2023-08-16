package jpabook.jpashop.domain;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "orders")
@Getter @Setter
public class Order {

    @Id @GeneratedValue
    @Column(name = "order_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id") // FK 이름이 member_id가 됨
    private Member member;

    // 연관관계 주인이란?
    // 어떤 값이 변경되었을 때 Member와 Order 테이블 중 어떤 FK를 변경해줄거냐?
    // 외래키를 가지고 있는 쪽을 연관관계의 주인으로 설정해주기!
    // 객체는 2군데, DB는 1군데만 변경하면 됨 - 이것을 맞춰준 것

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL)
    private List<OrderItem> orderItems = new ArrayList<>();
    // Order 저장할 때 orderItems 값도 같이 persist 해줌 (영속성 전이)
    /*
    * Parent parent = new Parent();
            parent.addchild(child1);
            parent.addchild(child2);

            em.persist(parent);
    * */

    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "delivery_id")
    private Delivery delivery;
    // Order 저장할 때 delivery 값도 같이 persist 해줌

    private LocalDateTime orderDate;

    @Enumerated(EnumType.STRING) // Enum 타입 넣을 때 주의할 것!
    private OrderStatus status;
}
