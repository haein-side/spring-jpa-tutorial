package jpabook.jpashop.api;

import jpabook.jpashop.domain.Address;
import jpabook.jpashop.domain.Order;
import jpabook.jpashop.domain.OrderItem;
import jpabook.jpashop.domain.OrderStatus;
import jpabook.jpashop.repository.OrderRepository;
import jpabook.jpashop.repository.OrderSearch;
import lombok.Data;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
public class OrderApiController {

    private final OrderRepository orderRepository;

    @GetMapping("/api/v1/orders")
    public List<Order> ordersV1() { //api entity 직접 노출하므로 좋지 않은 방법
        List<Order> all = orderRepository.findAllByString(new OrderSearch());

        for (Order order : all) {
            order.getMember().getName();
            order.getDelivery().getAddress();

            List<OrderItem> orderItems = order.getOrderItems(); //LAZY 강제 초기화 - hiber5module : proxy는 데이터를 안 뿌리므로 강제 초기화 함
            orderItems.stream().forEach(o -> o.getItem().getName()); //LAZY 강제 초기화
        }

        return all;
    }

    @GetMapping("/api/v2/orders")
    public List<OrderDTO> ordersV2() { //쿼리가 너무 많이 나와서 성능이 안나옴
        List<Order> orders = orderRepository.findAllByString(new OrderSearch());
        List<OrderDTO> result = orders.stream()
                .map(o -> new OrderDTO(o))
                .collect(Collectors.toList());

        return result;
    }

    @GetMapping("/api/v3/orders")
    public List<OrderDTO> ordersV3() { //Patch Join해서 개선 (쿼리 한 방으로 개선함) but 페이징 처리가 안 됨
        List<Order> orders = orderRepository.findAllWithItem();

        // orderItem과 OneToMany join되어 order(2) -> orderItem(4) -> order(4)개가 출력
        for (Order order : orders) {
            System.out.println("order ref=" + order + " id=" + order.getId());
        }
        /*
        id까지 동일한 애들이 중복되어서 출력됨 <= 이때 사용해주어야 하는 키워드가 distinct
        order ref=jpabook.j줌pashop.domain.Order@24a494b7 id=4
        order ref=jpabook.jpashop.domain.Order@24a494b7 id=4
        order ref=jpabook.jpashop.domain.Order@50e9709 id=11
        order ref=jpabook.jpashop.domain.Order@50e9709 id=11

        distinct 추가 후, 이렇게 order 원래의 개수만 나옴
        쿼리는 한 방으로 보내지만 application으로 보내는 데이터의 전송량이 많음
        중복 있음
        order ref=jpabook.jpashop.domain.Order@70f7a102 id=4
        order ref=jpabook.jpashop.domain.Order@3753ea8a id=11
        * */

        List<OrderDTO> result = orders.stream()
                .map(o -> new OrderDTO(o))
                .collect(Collectors.toList());

        return result;
    }

    @GetMapping("/api/v3.1/orders")
    public List<OrderDTO> ordersV3_page(@RequestParam(value = "offset", defaultValue = "0") int offset,
                                        @RequestParam(value = "limit", defaultValue = "100") int limit) { //페이징 처리 가능하게 개선해보기
        List<Order> orders = orderRepository.findAllWithMemberDelivery(offset, limit); //패치조인은 ToOne 관계에만 적용

        List<OrderDTO> result = orders.stream()
                .map(o -> new OrderDTO(o))
                .collect(Collectors.toList());

        return result;

        // Order + Member + Delivery - 쿼리 1번
        // 루프 돌면서 OrderItems N+1, Items M+1

        // default_batch_fetch_size 설정을 통해 IN으로 OrderItems와 Items 가져옴 -> 쿼리 총 3번
        // 데이터 중복 없이 가져오고 싶은 것만 가져옴!
        // 네트워크 호출(v3) vs. 데이터 전송량(v3.1)
        // 페이징 처리 가능
    }


        @Getter
    static class OrderDTO {

        private Long orderId;
        private String name;
        private LocalDateTime orderDate;
        private OrderStatus orderStatus;
        private Address address;
        private List<OrderItemDTO> orderItems; // OrderItemDTO로 보냄

        public OrderDTO(Order order) {
            orderId = order.getId();
            name = order.getMember().getName();
            orderDate = order.getOrderDate();
            orderStatus = order.getStatus();
            address = order.getDelivery().getAddress();
            orderItems = order.getOrderItems().stream() // OrderItem 엔티티를 OrderItemDTO로 변환하고, 변환된 결과를 컬렉션으로 수집
                    .map(orderItem -> new OrderItemDTO(orderItem))
                    .collect(Collectors.toList());
        }
    }

    @Getter
    static class OrderItemDTO {

        private String itemName; //상품명
        private int orderPrice; //주문가격
        private int count; //주문수량

        public OrderItemDTO(OrderItem orderItem) {
            itemName = orderItem.getItem().getName();
            orderPrice = orderItem.getOrderPrice();
            count = orderItem.getCount();
        }
    }


}
