package cn.e3mall.search.listener;

import cn.e3mall.search.service.SearchItemService;
import org.springframework.beans.factory.annotation.Autowired;

import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.TextMessage;

public class ItemChangeListener implements MessageListener {

    @Autowired
    private SearchItemService searchItemService;

    @Override
    public void onMessage(Message message) {
        try {
            TextMessage textMessage = null;
            Long itemId = null;
            //取商品id
            if (message instanceof TextMessage) {
                textMessage = (TextMessage) message;
                itemId = Long.parseLong(textMessage.getText());
            }
            //向索引库添加文档
            searchItemService.addDocument(itemId);// 调用service层实现添加索引

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}