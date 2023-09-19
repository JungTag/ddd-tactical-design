package kitchenpos.eatinorders.tobe.domain;

import kitchenpos.Fixtures;
import kitchenpos.menus.tobe.domain.Menu;
import kitchenpos.menus.tobe.domain.MenuProducts;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;

import static kitchenpos.Fixtures.menuProduct;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class EatInOrderTest {

    private final UUID menuId = Fixtures.orderLineItem().getMenuId();

    @Test
    @DisplayName("1개 이상의 등록된 메뉴로 매장 주문을 등록할 수 있다")
    void createdMenuRequired() {
        assertThatThrownBy(() -> EatInOrder.create(
                List.of(menu()),
                List.of(orderLineItem(UUID.randomUUID())),
                orderTable()
            )
        ).isExactlyInstanceOf(NoSuchElementException.class);
    }

    @Test
    @DisplayName("매장 주문은 주문 항목의 수량이 0 미만일 수 있다")
    void lessThanZero() {
        EatInOrder actual = EatInOrder.create(
            List.of(menu()),
            List.of(orderLineItem(-1L)),
            orderTable()
        );
        assertThat(actual).isNotNull();
    }

    @Test
    @DisplayName("빈 테이블에는 매장 주문을 등록할 수 없다")
    void emptyOrderTable() {
        assertThatThrownBy(() -> EatInOrder.create(
                List.of(menu()),
                List.of(orderLineItem()),
                orderTable(false)
            )
        ).isExactlyInstanceOf(IllegalStateException.class);
    }

    @Test
    @DisplayName("숨겨진 메뉴는 주문할 수 없다")
    void hiddenMenu() {
        assertThatThrownBy(() -> EatInOrder.create(
                List.of(menu(false)),
                List.of(orderLineItem()),
                orderTable()
            )
        ).isExactlyInstanceOf(IllegalStateException.class);
    }

    @Test
    @DisplayName("주문한 메뉴의 가격은 실제 메뉴 가격과 일치해야 한다")
    void equalMenuPrice() {
        assertThatThrownBy(() -> EatInOrder.create(
                List.of(menu(17_000L)),
                List.of(orderLineItem()),
                orderTable()
            )
        ).isExactlyInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @DisplayName("메뉴를 생성한다")
    void create() {
        EatInOrder actual = EatInOrder.create(
            List.of(menu()),
            List.of(orderLineItem()),
            orderTable()
        );
        assertThat(actual).isNotNull();
    }

    @Test
    @DisplayName("접수 대기 중인 주문만 접수할 수 있다")
    void acceptFailed() {
        EatInOrder eatInOrder = acceptedOrder();
        assertThatThrownBy(eatInOrder::accept)
            .isExactlyInstanceOf(IllegalStateException.class);
    }

    @Test
    @DisplayName("주문을 접수한다")
    void accept() {
        EatInOrder eatInOrder = eatInOrder();
        eatInOrder.accept();
        assertThat(eatInOrder.getStatus()).isEqualTo(OrderStatus.ACCEPTED);
    }

    @Test
    @DisplayName("접수된 주문만 서빙할 수 있다")
    void serveFailed() {
        EatInOrder eatInOrder = servedOrder();
        assertThatThrownBy(eatInOrder::serve)
            .isExactlyInstanceOf(IllegalStateException.class);
    }

    @Test
    @DisplayName("주문을 서빙한다")
    void serve() {
        EatInOrder eatInOrder = acceptedOrder();
        eatInOrder.serve();
        assertThat(eatInOrder.getStatus()).isEqualTo(OrderStatus.SERVED);
    }

    @Test
    @DisplayName("서빙 완료된 주문만 완료할 수 있다")
    void completeFailed() {
        EatInOrder eatInOrder = completedOrder();
        assertThatThrownBy(eatInOrder::complete)
            .isExactlyInstanceOf(IllegalStateException.class);
    }

    @Test
    @DisplayName("주문을 완료한다")
    void complete() {
        EatInOrder eatInOrder = servedOrder();
        eatInOrder.complete();
        assertThat(eatInOrder.getStatus()).isEqualTo(OrderStatus.COMPLETED);
    }

    private Menu menu() {
        return menu(menuId, true, 18_000L);
    }

    private Menu menu(final boolean displayed) {
        return menu(menuId, displayed, 18_000L);
    }

    private Menu menu(final long price) {
        return menu(menuId, true, price);
    }

    private Menu menu(final UUID id, final boolean displayed, long price) {
        return Menu.create(
            id,
            "후라이드+후라이드",
            BigDecimal.valueOf(price),
            UUID.randomUUID(),
            displayed,
            new MenuProducts(List.of(menuProduct())),
            (name) -> false
        );
    }

    private OrderLineItem orderLineItem() {
        return orderLineItem(18_000L, menuId);
    }

    private OrderLineItem orderLineItem(final UUID menuId) {
        return OrderLineItem.create(1L, menuId, 18_000L);
    }


    private OrderLineItem orderLineItem(final long quantity) {
        return OrderLineItem.create(quantity, menuId, 18_000L);
    }

    private OrderLineItem orderLineItem(final Long price, final UUID menuId) {
        return OrderLineItem.create(1L, menuId, price);
    }

    private OrderTable orderTable(final int numberOfGuests, final boolean occupied) {
        return new OrderTable(UUID.randomUUID(), new OrderTableName("1번"), new NumberOfGuests(numberOfGuests), occupied);
    }

    private OrderTable orderTable() {
        return orderTable(4, true);
    }

    private OrderTable orderTable(final boolean occupied) {
        return orderTable(4, occupied);
    }

    private EatInOrder eatInOrder() {
        return EatInOrder.create(
            List.of(menu()),
            List.of(orderLineItem()),
            orderTable()
        );
    }

    private EatInOrder acceptedOrder() {
        EatInOrder eatInOrder = eatInOrder();
        eatInOrder.accept();
        return eatInOrder;
    }

    private EatInOrder servedOrder() {
        EatInOrder eatInOrder = acceptedOrder();
        eatInOrder.serve();
        return eatInOrder;
    }

    private EatInOrder completedOrder() {
        EatInOrder eatInOrder = servedOrder();
        eatInOrder.complete();
        return eatInOrder;
    }
}
