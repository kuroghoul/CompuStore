package com.fiuady.compustore.db;

import android.app.Application;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.CursorWrapper;
import android.database.sqlite.SQLiteDatabase;
import com.fiuady.compustore.db.InventoryDbSchema.*;


import java.util.ArrayList;
import java.util.List;

/**
 * Created by Kuro on 02/04/2017.
 */




public final class Inventory extends Application {

    private  InventoryHelper inventoryHelper;
    private  SQLiteDatabase db;



    public Inventory(Context context){

        //inventoryHelper = new InventoryHelper(context);
        inventoryHelper = InventoryHelper.getInventoryHelper(context);
        db = inventoryHelper.getWritableDatabase();
    }


class AssemblyCursor extends CursorWrapper{
    public AssemblyCursor(Cursor cursor) {
        super(cursor);}

    public Assembly getAssembly(){
        Cursor cursor = getWrappedCursor();
        return new Assembly(cursor.getInt(cursor.getColumnIndex(InventoryDbSchema.AssembliesTable.Columns.ID)),
                cursor.getString(cursor.getColumnIndex(InventoryDbSchema.AssembliesTable.Columns.DESCRIPTION)));
    }

}

    class OrderCursor extends CursorWrapper{
        public OrderCursor(Cursor cursor) {
            super(cursor);}

        public Order getOrder(){
            Cursor cursor = getWrappedCursor();
            return new Order(cursor.getInt(cursor.getColumnIndex(InventoryDbSchema.OrdersTable.Columns.ID)),
                    getOrderStatusById(cursor.getInt(cursor.getColumnIndex(InventoryDbSchema.OrdersTable.Columns.STATUS_ID))),
                    getCustomerById(cursor.getInt(cursor.getColumnIndex(InventoryDbSchema.OrdersTable.Columns.CUSTOMER_ID))),
                    cursor.getString(cursor.getColumnIndex(InventoryDbSchema.OrdersTable.Columns.DATE)),
                    cursor.getString(cursor.getColumnIndex(InventoryDbSchema.OrdersTable.Columns.CHANGELOG)));

        }



    }

    class ProductCategoryCursor extends CursorWrapper {
        public ProductCategoryCursor(Cursor cursor) {
            super(cursor);
        }

        public ProductCategory getProductCategory(){
            Cursor cursor = getWrappedCursor();
            return new ProductCategory(cursor.getInt(cursor.getColumnIndex(InventoryDbSchema.ProductCategoriesTable.Columns.ID)),
                    cursor.getString(cursor.getColumnIndex(InventoryDbSchema.ProductCategoriesTable.Columns.DESCRIPTION)));
        }
    }
    class OrderStatusCursor extends CursorWrapper {
        public OrderStatusCursor(Cursor cursor) {
            super(cursor);
        }

        public OrderStatus getOrderStatus(){
            Cursor cursor = getWrappedCursor();
            return new OrderStatus(cursor.getInt(cursor.getColumnIndex(InventoryDbSchema.OrderStatusTable.Columns.ID)),
                    cursor.getString(cursor.getColumnIndex(InventoryDbSchema.OrderStatusTable.Columns.DESCRIPTION)),
                    cursor.getInt(cursor.getColumnIndex(InventoryDbSchema.OrderStatusTable.Columns.EDITABLE)),
                    cursor.getString(cursor.getColumnIndex(InventoryDbSchema.OrderStatusTable.Columns.PREVIOUS)),
                    cursor.getString(cursor.getColumnIndex(InventoryDbSchema.OrderStatusTable.Columns.NEXT)));
        }
    }


    class ProductCursor extends CursorWrapper{
        public ProductCursor(Cursor cursor) {
            super(cursor);
        }

        public Product getProduct(){
            Cursor cursor = getWrappedCursor();
            return new Product(cursor.getInt(cursor.getColumnIndex(InventoryDbSchema.ProductsTable.Columns.ID)),getProductCategoryById(cursor.getInt(cursor.getColumnIndex(ProductsTable.Columns.CATEGORY_ID))),
                    cursor.getString(cursor.getColumnIndex(InventoryDbSchema.ProductsTable.Columns.DESCRIPTION)), cursor.getInt(cursor.getColumnIndex(InventoryDbSchema.ProductsTable.Columns.PRICE)), cursor.getInt(cursor.getColumnIndex(ProductsTable.Columns.QTY)));
        }

    }

    class CustomerCursor extends CursorWrapper{
        public CustomerCursor(Cursor cursor) {
            super(cursor);
        }

        public Customer getCustomer(){
            Cursor cursor = getWrappedCursor();
            return new Customer(cursor.getInt(cursor.getColumnIndex(InventoryDbSchema.CustomersTable.Columns.ID)),
                    cursor.getString(cursor.getColumnIndex(InventoryDbSchema.CustomersTable.Columns.FIRST_NAME)),
                     cursor.getString(cursor.getColumnIndex(InventoryDbSchema.CustomersTable.Columns.LAST_NAME)),
                    cursor.getString(cursor.getColumnIndex(InventoryDbSchema.CustomersTable.Columns.ADDRESS)),
                    cursor.getString(cursor.getColumnIndex(InventoryDbSchema.CustomersTable.Columns.PHONE1)),
                    cursor.getString(cursor.getColumnIndex(InventoryDbSchema.CustomersTable.Columns.PHONE2)),
                    cursor.getString(cursor.getColumnIndex(InventoryDbSchema.CustomersTable.Columns.PHONE3)),
                    cursor.getString(cursor.getColumnIndex(CustomersTable.Columns.EMAIL)));
        }

    }






    //-------------------------------------------------------------------------
    //      ProductCategory
    //-------------------------------------------------------------------------
    public List<ProductCategory> getAllProductCategories()
    {
        ArrayList<ProductCategory> list = new ArrayList<ProductCategory>();
        ProductCategoryCursor cursor = new ProductCategoryCursor(db.rawQuery("SELECT * FROM product_categories ORDER BY description", null));
        while (cursor.moveToNext())
        {
            list.add(cursor.getProductCategory());
        }
        cursor.close();
        return list;
    }

    public ProductCategory getProductCategoryById(int id) {


        ProductCategoryCursor cursor = new ProductCategoryCursor(db.rawQuery("SELECT * FROM product_categories WHERE id="+Integer.toString(id)+" ORDER BY id", null));
        cursor.moveToNext();
            ProductCategory category= cursor.getProductCategory();
            cursor.close();
            return category;

    }

    public ProductCategory getProductCategoryByDescription(String description) {


        ProductCategoryCursor cursor = new ProductCategoryCursor(db.query(ProductCategoriesTable.NAME,
                null,
                ProductCategoriesTable.Columns.DESCRIPTION + " LIKE ? ",
                new String[]{description},
                null, null, null));
        if(cursor.moveToNext()) {
            ProductCategory category = cursor.getProductCategory();
            cursor.close();
            return category;
        }
        else
        {
            return null;
        }
    }

    public void updateCategory(ProductCategory productCategory){
        ContentValues values = new ContentValues();
        values.put(ProductCategoriesTable.Columns.DESCRIPTION, productCategory.getDescription());
        db.update(ProductCategoriesTable.NAME,
                values,
                ProductCategoriesTable.Columns.ID + " =? ",
                new String[]{Integer.toString(productCategory.getId())});
    }

    public int getNewIdFrom(String Table)
    {
        Cursor cursor = db.rawQuery("SELECT MAX(t.id) AS LastId FROM "+Table+" t",null);
        cursor.moveToNext();
        int id = cursor.getInt(cursor.getColumnIndex("LastId"));
        cursor.close();
        return id+1;
    }

    public void insertProductCategory(ProductCategory productCategory)
    {
        ContentValues values = new ContentValues();
        values.put(ProductCategoriesTable.Columns.ID, Integer.toString(productCategory.getId()));
        values.put(ProductCategoriesTable.Columns.DESCRIPTION, productCategory.getDescription());
        db.insert(ProductCategoriesTable.NAME, null, values);
    }
    public void deleteProductCategory (ProductCategory category)
    {
        db.delete(ProductCategoriesTable.NAME,
                "id = ?",
                new String[]{Integer.toString(category.getId())});
    }
    //-------------------------------------------------------------------------
    //      Product
    //-------------------------------------------------------------------------

    public List<Product> getAllProducts()
    {
        ArrayList<Product> list = new ArrayList<Product>();
        ProductCursor cursor = new ProductCursor(db.rawQuery("SELECT * FROM products ORDER BY description", null));
        while (cursor.moveToNext())
        {
            list.add(cursor.getProduct());
        }
        cursor.close();
        return list;
    }
    //-------------------------------------------------------------------------
    //      Assembly
    //-------------------------------------------------------------------------
    public List<Assembly> getAllAssemblies()
    {
        ArrayList<Assembly> list = new ArrayList<Assembly>();
        AssemblyCursor cursor = new AssemblyCursor(db.rawQuery("SELECT * FROM assemblies ORDER BY id", null));
        while (cursor.moveToNext())
        {
            list.add(cursor.getAssembly());
        }
        cursor.close();
        return list;
    }



    public List<Product> getProductsFilterByCategory(ProductCategory category, String search)
    {
        ProductCursor cursor;
        if(category==null){
             cursor = new ProductCursor(db.query(ProductsTable.NAME,
                    null,
                    ProductsTable.Columns.DESCRIPTION + " LIKE ?",
                    new String[]{"%"+search.trim()+"%"},
                    null, null, null));
        }
        else
        {
             cursor = new ProductCursor(db.query(ProductsTable.NAME,
                    null,
                    ProductsTable.Columns.CATEGORY_ID + " = ? AND " + ProductsTable.Columns.DESCRIPTION + " LIKE ?",
                    new String[]{Integer.toString(category.getId()), "%"+search.trim()+"%"},
                    null, null, ProductsTable.Columns.DESCRIPTION));
        }

        ArrayList<Product> list = new ArrayList<Product>();

        while (cursor.moveToNext())
        {
            list.add(cursor.getProduct());
        }
        cursor.close();
        return list;
    }

    public Product getProductByDescription(String description)
    {
        ProductCursor cursor = new ProductCursor(db.query(ProductsTable.NAME,
                null,
                ProductsTable.Columns.DESCRIPTION + " LIKE ? ",
                new String[]{description},
                null, null, null));
        if(cursor.moveToNext()) {
            Product product = cursor.getProduct();
            cursor.close();
            return product;
        }
        else
        {
            return null;
        }
    }

    public void insertProduct(Product product)
    {
        ContentValues values = new ContentValues();
        values.put(ProductsTable.Columns.ID, Integer.toString(product.getId()));
        values.put(ProductsTable.Columns.CATEGORY_ID, product.getProductCategory().getId());
        values.put(ProductsTable.Columns.DESCRIPTION, product.getDescription());
        values.put(ProductsTable.Columns.PRICE, product.getPrice());
        values.put(ProductsTable.Columns.QTY, product.getQty());

        db.insert(ProductsTable.NAME, null, values);
    }

    public void deleteProduct (Product product)
    {
        db.delete(ProductsTable.NAME,
                "id = ?",
                new String[]{Integer.toString(product.getId())});
    }

    //-------------------------------------------------------------------------
    //      Customer
    //-------------------------------------------------------------------------

    public List<Customer> getAllCustomers()
    {
        ArrayList<Customer> list = new ArrayList<Customer>();
        CustomerCursor cursor = new CustomerCursor(db.rawQuery("SELECT * FROM customers ORDER BY id", null));
        while (cursor.moveToNext())
        {
            list.add(cursor.getCustomer());
        }
        cursor.close();
        return list;
    }

    //-------------------------------------------------------------------------
    //      Order
    //-------------------------------------------------------------------------

    public List<Order> getAllOrders()
    {
        ArrayList<Order> list = new ArrayList<Order>();
        OrderCursor cursor = new OrderCursor(db.rawQuery("SELECT * FROM orders ORDER BY id", null));
        while (cursor.moveToNext())
        {
            list.add(cursor.getOrder());
        }
        cursor.close();
        return list;
    }

    public Customer getCustomerById(int id) {


        CustomerCursor cursor = new CustomerCursor(db.rawQuery("SELECT * FROM customers WHERE id="+Integer.toString(id)+" ORDER BY id", null));
        cursor.moveToNext();
        Customer customer= cursor.getCustomer();
        cursor.close();
        return customer;

    }


    public OrderStatus getOrderStatusById(int id) {


        OrderStatusCursor cursor = new OrderStatusCursor(db.rawQuery("SELECT * FROM order_status WHERE id="+Integer.toString(id)+" ORDER BY id", null));
        cursor.moveToNext();
        OrderStatus orderstatus= cursor.getOrderStatus();
        cursor.close();
        return orderstatus;

    }



}
