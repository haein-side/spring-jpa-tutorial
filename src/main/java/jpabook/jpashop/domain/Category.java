package jpabook.jpashop.domain;

import jpabook.jpashop.domain.item.Item;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter @Setter
public class Category {

    @Id @GeneratedValue
    @Column(name = "category_id")
    private Long id;

    private String name;

    @ManyToMany
    @JoinTable(name = "category_item",
                joinColumns = @JoinColumn(name = "category_id"),
                inverseJoinColumns = @JoinColumn(name = "item_id")) // 중간 테이블
    private List<Item> items = new ArrayList<>();

    @ManyToOne // self join 예제 : self로 양방향 연관관계를 걸었다고 생각하면 됨
    @JoinColumn(name = "parent_id")
    private Category parent;

    @OneToMany(mappedBy = "parent")
    private List<Category> child = new ArrayList<>();
}
