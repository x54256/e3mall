import cn.e3mall.pojo.TbItem;
import cn.e3mall.service.ItemService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)     // 创建容器
@ContextConfiguration("classpath:spring/*.xml")    // 通过类加载形式，读取配置文件，创建对象
public class test {

    @Autowired
    private ItemService itemService;

    @Test
    public void func(){

        TbItem item = new TbItem();

        item.setTitle("xxx");
        item.setSellPoint("xc");
        item.setPrice(1L);
        item.setNum(1);
        item.setBarcode("dd");
        item.setImage("xx");
        item.setCid(1L);

        itemService.save(item,"xxx");
    }

}
