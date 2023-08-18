package jpabook.jpashop.service;

import jpabook.jpashop.domain.Delivery;
import jpabook.jpashop.domain.Member;
import jpabook.jpashop.domain.Order;
import jpabook.jpashop.domain.OrderItem;
import jpabook.jpashop.domain.item.Item;
import jpabook.jpashop.repository.ItemRepository;
import jpabook.jpashop.repository.MemberRepository;
import jpabook.jpashop.repository.OrderRepository;
import jpabook.jpashop.repository.OrderSearch;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final MemberRepository memberRepository;
    private final ItemRepository itemRepository;

    /**
     * 주문
     */
    @Transactional
    public Long order(Long memberId, Long itemId, int count) {
        //엔티티 조회
        Member member = memberRepository.findOne(memberId); // 트랜잭션 안에서 엔티티 조회해야 영속 상태로 진행됨 -> update 용이
        Item item = itemRepository.findOne(itemId);

        //배송정보 생성
        Delivery delivery = new Delivery();
        delivery.setAddress(member.getAddress()); // order 저장 시 cascade = CascadeType.ALL로 persist

        //주문상품 생성(생성메소드)
        OrderItem orderItem = OrderItem.createOrderItem(item, item.getPrice(), count); // order 저장 시 cascade = CascadeType.ALL로 persist

        //주문 생성
        Order order = Order.createOrder(member, delivery, orderItem);

        //주문 저장
        //하나만 저장해줘도 Delivery와 OrderItem도 같이 저장됨
        orderRepository.save(order);

        return order.getId();
    }

    /**
     * 주문 취소
     */
    @Transactional
    public void cancelOrder(Long orderId) {
        //주문 엔티티 조회
        Order order = orderRepository.findOne(orderId);
        //주문 취소
        order.cancel();

        //JPA의 큰 강점 : 객체의 값 바꿔준다음, 실제 DB에 날리는 쿼리문 짜줘야 함!
        //JPA는 엔티티 안의 데이터들만 바꿔주면 JPA가 변경내역을 감지(dirty checking)해서 update하는 쿼리가 날라감
        //Order Status, Item의 stockQuantity 수정하는 쿼리 날라감
    }

    //검색
    public List<Order> findOrders(OrderSearch orderSearch) {
        return orderRepository.findAllByString(orderSearch);
    }
}
