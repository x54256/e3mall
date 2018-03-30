package cn.e3mall.cart.service;

import cn.e3mall.pojo.TbItem;

import java.util.List;

public interface CartService {
    void addCart(Long id, Long itemId, Integer num);

    void mergeCart(Long id, List<TbItem> cartList);

    List<TbItem> getCartList(Long id);

    void updateCartNum(Long id, Long itemId, Integer num);

    void deleteCartItem(Long id, Long itemId);
}
