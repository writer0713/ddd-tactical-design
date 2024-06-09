package kitchenpos.menus.tobe.application

import kitchenpos.menus.tobe.application.dto.CreateMenuRequest
import kitchenpos.menus.tobe.application.dto.MenuProductRequest
import kitchenpos.menus.tobe.domain.menu.TobeProductClient
import kitchenpos.menus.tobe.domain.menugroup.TobeMenuGroup
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatIllegalArgumentException
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.mockito.BDDMockito.anyList
import org.mockito.BDDMockito.given
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.test.context.TestConstructor
import org.springframework.transaction.annotation.Transactional
import java.util.*

@TestConstructor(autowireMode = TestConstructor.AutowireMode.ALL)
@Transactional
@SpringBootTest
class TobeMenuServiceTest(
    private val sut: TobeMenuService,
    private val tobeMenuGroupService: TobeMenuGroupService,
    @MockBean
    private val tobeProductClient: TobeProductClient,
) {
    companion object {
        private lateinit var menuGroup: TobeMenuGroup
    }

    @BeforeEach
    fun setUp() {
        menuGroup = tobeMenuGroupService.create(TobeMenuGroup(UUID.randomUUID(), "후라이드"))
    }

    @DisplayName("1 개 이상의 등록된 상품으로 메뉴를 등록할 수 있다. (등록된 상품 o)")
    @Test
    fun case_1() {
        // given
        val createMenuRequest =
            CreateMenuRequest(
                name = "후라이드 치킨",
                price = 10_000,
                groupId = menuGroup.id,
                displayed = true,
                menuProducts = listOf(MenuProductRequest(1, 10_000, UUID.randomUUID())),
            )
        given(tobeProductClient.validateAllProductsExists(anyList())).willReturn(true)

        // when
        val savedMenu = sut.create(createMenuRequest)

        // then
        assertThat(savedMenu).isNotNull()
    }

    @DisplayName("1 개 이상의 등록된 상품으로 메뉴를 등록할 수 있다. (등록된 상품 x)")
    @Test
    fun case_1_1() {
        // given
        val createMenuRequest =
            CreateMenuRequest(
                name = "후라이드 치킨",
                price = 10_000,
                groupId = menuGroup.id,
                displayed = true,
                menuProducts = listOf(MenuProductRequest(1, 10_000, UUID.randomUUID())),
            )
        given(tobeProductClient.validateAllProductsExists(anyList())).willReturn(false)

        // when
        // then
        assertThatIllegalArgumentException().isThrownBy { sut.create(createMenuRequest) }
    }

    @DisplayName("메뉴의 이름에는 비속어가 포함될 수 없다.")
    @Test
    fun case_2() {
        // given
        val createMenuRequest =
            CreateMenuRequest(
                name = "비속어",
                price = 10_000,
                groupId = menuGroup.id,
                displayed = true,
                menuProducts = listOf(MenuProductRequest(1, 10_000, UUID.randomUUID())),
            )

        // when
        // then
        assertThatIllegalArgumentException().isThrownBy { sut.create(createMenuRequest) }
    }

    @DisplayName("메뉴의 가격은 0원 이상이어야 한다.")
    @Test
    fun case_3() {
        // given
        val createMenuRequest =
            CreateMenuRequest(
                name = "후라이드 치킨",
                price = -1,
                groupId = menuGroup.id,
                displayed = true,
                menuProducts = listOf(MenuProductRequest(1, 10_000, UUID.randomUUID())),
            )

        // when
        // then
        assertThatIllegalArgumentException().isThrownBy { sut.create(createMenuRequest) }
    }

    @DisplayName("메뉴에 속한 상품의 수량은 0 이상이어야 한다.")
    @Test
    fun case_4() {
        // given
        val createMenuRequest =
            CreateMenuRequest(
                name = "후라이드 치킨",
                price = -1,
                groupId = menuGroup.id,
                displayed = true,
                menuProducts = listOf(MenuProductRequest(-1, 10_000, UUID.randomUUID())),
            )

        // when
        // then
        assertThatIllegalArgumentException().isThrownBy { sut.create(createMenuRequest) }
    }

    @DisplayName("메뉴는 특정 메뉴 그룹에 속해야 한다.")
    @Test
    fun case_5() {
        // given
        val notExistsMenuGroupId = UUID.randomUUID()
        val createMenuRequest =
            CreateMenuRequest(
                name = "후라이드 치킨",
                price = 10_000,
                groupId = notExistsMenuGroupId,
                displayed = true,
                menuProducts = listOf(MenuProductRequest(1, 10_000, UUID.randomUUID())),
            )

        // when
        // then
        assertThrows<NoSuchElementException> { sut.create(createMenuRequest) }
    }
}
