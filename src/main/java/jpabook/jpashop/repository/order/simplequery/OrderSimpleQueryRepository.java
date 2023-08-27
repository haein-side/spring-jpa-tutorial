package jpabook.jpashop.repository.order.simplequery;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class OrderSimpleQueryRepository {
    private final EntityManager em;

    // V4 : API 스펙에 맞춰서 repository에 작성했다는 단점
    // repository는 객체(엔티티) 그래프를 가져올 때 사용되어야 -> repository가 화면을 의존하므로 안 좋음
    // 화면에 박힌 것들은 다른 패키지로 만들어서 Repository와 분리함!
    public List<OrderSimpleQueryDTO> findOrderDTOs() {
        return em.createQuery(
                        "select new jpabook.jpashop.repository.order.simplequery.OrderSimpleQueryDTO(o.id, m.name, o.orderDate, o.status, d.address) from Order o" +
                                " join o.member m" +
                                " join o.delivery d", OrderSimpleQueryDTO.class)
                .getResultList();

    }
}
