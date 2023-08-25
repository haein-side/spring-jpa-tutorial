package jpabook.jpashop.domain;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "orders")
@Getter @Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED) //new Order() 막고 생성 메서드로만 생성
public class Order {

    @Id @GeneratedValue
    @Column(name = "order_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id") // FK 이름이 member_id가 됨
    private Member member = new ByteBuddyInterceptor();

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

    // == 연관관계 편의 메소드 for 양방향 연관관계 == //
    public void setMember(Member member) {
        this.member = member;
        member.getOrders().add(this);
    }

    public void addOrderItem(OrderItem orderItem) {
        orderItems.add(orderItem);
        orderItem.setOrder(this);
    }

    public void setDelivery(Delivery delivery) {
        this.delivery = delivery;
        delivery.setOrder(this);
    }

    //==생성 메서드==//
    // 주문 생성에 대한 복잡한 로직을 아래 메소드에서 완결시킴
    // 뭔가 생성하는 지점을 바꾸고자 한다면 createOrder만 바꾸면 된다 - 응집도 높음
    public static Order createOrder(Member member, Delivery delivery, OrderItem... orderItems) {
        Order order = new Order();
        order.setMember(member);
        order.setDelivery(delivery); // order 저장 시 cascade = CascadeType.ALL로 persist

        for (OrderItem orderItem : orderItems) {
            order.addOrderItem(orderItem); // order 저장 시 cascade = CascadeType.ALL로 persist
        }
        order.setStatus(OrderStatus.ORDER);
        order.setOrderDate(LocalDateTime.now());
        return order;
    }

    //==비즈니스 로직==//
    /**
     * 주문 취소
     */
    public void cancel() {
        if (delivery.getStatus() == DeliveryStatus.COMP) {
            throw new IllegalStateException("이미 배송 완료된 상품은 취소가 불가능합니다.");
        }

        this.setStatus(OrderStatus.CANCEL);
        for (OrderItem orderItem : orderItems) {
            orderItem.cancel();
        }
    }

    //==조회 로직==//
    /**
     * 전체 주문 가격 조회
     */
    public int getTotalPrice() {
        int totalPrice = 0;
        for (OrderItem orderItem : orderItems) {
            totalPrice += orderItem.getTotalPrice();
        }
        return totalPrice;
    }

}
