package jpabook.jpashop.service;

import jpabook.jpashop.domain.item.Book;
import jpabook.jpashop.domain.item.Item;
import jpabook.jpashop.repository.ItemRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ItemService { // ItemRepository에 위임

    private final ItemRepository itemRepository;

    @Transactional
    public void saveItem(Item item) {
        itemRepository.save(item);
    }

    @Transactional
    public void updateItem(Long itemId, String name, int price, int stockQuantity) { //id를 명확하게 받아야 Transaction 안에서 엔티티 조회가 되고 영속상태가 된다 -> 값 변경해야 변경 감지 일어남
        Item findItem = itemRepository.findOne(itemId); //findItem : 영속상태
        findItem.setPrice(price);
        findItem.setName(name);
        findItem.setStockQuantity(stockQuantity);
        //itemRepository.save(findItem); // 호출할 필요 X
        //@Transactional -> 트랜잭션 커밋됨 -> flush() -> 영속성 컨텍스트에 있는 엔티티 중 변경사항 찾음
        //-> findItem 변경된 것 쿼리 날림

        //merge보다 내가 변경할 값들만 골라서 set 날려주는(변경 감지 방식) 게 더 안전한 방식임!
        //merge는 없는 필드는 null로 갈아버리므로
        //TODO 사실 set보다 change() 함수 같은 의미있는 메소드를 만들어서 변경지점을 엔티티로 몰아서 하는 게 나음!!
        //엔티티 안에서 바로 추적 가능한 메소드 만드는 게 나음!
        //추적 가능하게 만들기 위해
    }

    public List<Item> findItems() {
        return itemRepository.findAll();
    }

    public Item findOne(Long itemId) {
        return itemRepository.findOne(itemId);
    }

}
