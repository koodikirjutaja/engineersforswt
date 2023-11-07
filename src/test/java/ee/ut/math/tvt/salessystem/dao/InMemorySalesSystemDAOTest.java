package ee.ut.math.tvt.salessystem.dao;
import ee.ut.math.tvt.salessystem.dataobjects.StockItem;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ee.ut.math.tvt.salessystem.FieldFormatException;
import org.mockito.InOrder;
import org.mockito.Mockito;

import java.sql.SQLOutput;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.verify;


public class InMemorySalesSystemDAOTest{

    private SalesSystemDAO dao;
@BeforeEach
public void setUp(){
    dao = new InMemorySalesSystemDAO();
}
@Test
public void testAddingItemBeginsAndCommitsTransaction() {
    SalesSystemDAO mockdao = mock(InMemorySalesSystemDAO.class);
    doCallRealMethod().when(mockdao).addNewStockItem("asdf","asdf","1","1");
    InOrder inOrder = inOrder(mockdao);
    mockdao.addNewStockItem("asdf","asdf","1","1");
    inOrder.verify(mockdao, times(1)).beginTransaction();
    inOrder.verify(mockdao, times(1)).commitTransaction();
}
@Test
    public void testAddingNewItem(){
    StockItem testitem = new StockItem((long) 5,"item", "item", 15.0, 15);
    dao.addStockItem(testitem);
    assertEquals(dao.findStockItem(5),testitem);
}
@Test
    public void testAddingExistingItem(){
    StockItem testitem = dao.findStockItem(3);
    int quantity = dao.findStockItem(3).getQuantity();
    dao.addExistingStockItem(testitem, testitem.getName(),testitem.getDescription(),Double.toString(testitem.getPrice()),Integer.toString(testitem.getQuantity()));
    assertEquals(quantity*2, dao.findStockItem(3).getQuantity());
}
@Test
    public void testAddingItemWithNegativeQuantity(){

    assertThrows(FieldFormatException.class, () -> dao.addNewStockItem("item", "item", "-5", "5"));
}
}