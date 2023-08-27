package jpabook.jpashop.api;

import jpabook.jpashop.domain.Address;
import jpabook.jpashop.domain.Order;
import jpabook.jpashop.domain.OrderStatus;
import jpabook.jpashop.repository.OrderRepository;
import jpabook.jpashop.repository.OrderSearch;
import lombok.Data;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * xToOne(ManyToOne, OneToOne)
 * Order
 * Order -> Member - LAZY (ManyToOne)
 * Order -> Delivery - LAZY (OneToOne)
 */
@RestController
@RequiredArgsConstructor
public class OrderSimpleApiController {

    private final OrderRepository orderRepository;

    @GetMapping("/api/v1/sample-orders")
    public List<Order> ordersV1() {
        List<Order> all = orderRepository.findAllByString(new OrderSearch()); // Order안에 Member가 있고 Member 안에 Order가 있음! -> 양방향 연관관계의 문제점 생감
        return all;
    }

    @GetMapping("/api/v2/simple-orders")
    public List<SimpleOrderDTO> ordersV2() { // 사실 List가 그대로 나가면 안됨
        //ORDER 2개 -> 쿼리 한 번 실행됨
        //N + 1 -> 1 + 회원 N(2) + 배송 N(2) : 지연로딩 때문에 발생하는 문제
        //모든 연관관계는 다 LAZY로 해놓고 Patch Join으로 튜닝해야 함 (EAGER는 절대 쓰면 안됨)
        List<Order> orders = orderRepository.findAllByString(new OrderSearch());

        //2번 LOOP : 결론 -> 쿼리 총 5번 실행됨
        List<SimpleOrderDTO> result = orders.stream()
                .map(o -> new SimpleOrderDTO(o))
                .collect(Collectors.toList());

        return result;
    }

    @GetMapping("/api/v3/simple-orders")
    public List<SimpleOrderDTO> ordersV3() { //Patch Join 적용 -> 쿼리 한 번 나감 - inner join으로 orders 안에 member와 delivery가 같이 조회되어 들어옴(LAZY 안 일어남)
        List<Order> orders = orderRepository.findAllWithMemberDelivery();
        List<SimpleOrderDTO> result = orders.stream()
                .map(o -> new SimpleOrderDTO(o))
                .collect(Collectors.toList());

        return result;
    }

        @Data
    static class SimpleOrderDTO {
        private Long orderId;
        private  String name;
        private LocalDateTime orderDate;
        private OrderStatus orderStatus;
        private Address address; // 값 객체 (value object)

        public SimpleOrderDTO(Order order) {
            orderId = order.getId();
            name = order.getMember().getName(); // 쿼리 총 2번 실행됨 LAZY 초기화
            orderDate = order.getOrderDate();
            orderStatus = order.getStatus();
            address = order.getDelivery().getAddress(); // 쿼리 총 2번 실행됨 LAZY 초기화
        }
    }


}
