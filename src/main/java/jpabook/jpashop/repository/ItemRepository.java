package jpabook.jpashop.repository;

import jpabook.jpashop.domain.item.Item;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class ItemRepository {

    private final EntityManager em;

    public void save(Item item) {
        if (item.getId() == null) { // item을 JPA에 저장하기 전까진 id값이 없음 -> 즉 새로 생성한 객체라는 뜻
            em.persist(item); // 신규 등록
        } else { // 이미 DB에 등록된 걸 가져온 것
            em.merge(item); // 이미 JPA를 통해 DB에 한 번 들어갔구나 생각하고 update 같은 것
        }
    }

    public Item findOne(Long id) {
        return em.find(Item.class, id);
    }

    public List<Item> findAll() {
        return em.createQuery("select i from Item i", Item.class)
                .getResultList();
    }

}
