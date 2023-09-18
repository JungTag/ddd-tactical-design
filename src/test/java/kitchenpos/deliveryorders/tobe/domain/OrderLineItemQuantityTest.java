package kitchenpos.deliveryorders.tobe.domain;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class OrderLineItemQuantityTest {

    @DisplayName("수량은 0 이상이어야 한다")
    @Test
    void lessThanZero() {
        assertThatThrownBy(() -> new OrderLineItemQuantity(-1L))
            .isExactlyInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("수량을 생성할 수 있다")
    @Test
    void constructor() {
        OrderLineItemQuantity acutal = new OrderLineItemQuantity(1L);
        assertThat(acutal).isNotNull();
    }
}
