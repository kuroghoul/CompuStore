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

    public enum InsertResponse {Ok, DuplicateId, DuplicateDescription, InvalidDescription, InvalidPrice, InvalidCategory}
    public enum DeleteResponse {Ok, AlreadyInUse}
    public enum ModifyResponse {Ok, DuplicateDescription, InvalidDescription, InvalidPrice, InvalidCategory, InvalidId}

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





    public int getNewIdFrom(String Table)
    {

        Cursor cursor = db.query(Table, new String[]{"MAX(id) AS newId"}, null, null, null, null, null);
        if (cursor.moveToNext()) {
            int id = cursor.getInt(cursor.getColumnIndex("newId"));
            cursor.close();
            return (id + 1);
        }
        else
        {
            return -1;
        }
    }




    //region ProductCategory Methods
    //---------------------------------------------------------------------------------------
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
        ProductCategoryCursor cursor = new ProductCategoryCursor(db.query(ProductCategoriesTable.NAME,
                null,
                ProductCategoriesTable.Columns.ID + " = ? ",
                new String[]{Integer.toString(id)},
                null, null, null));
        if (cursor.moveToNext()) {
            ProductCategory category = cursor.getProductCategory();
            cursor.close();
            return category;
        }
        else{
            return null;
        }
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

    public List<Product> findProductCategoryUsageInProducts (ProductCategory category)
    {
        ProductCursor cursor = new ProductCursor(db.query(ProductsTable.NAME,
                null,
                ProductsTable.Columns.CATEGORY_ID + " = ?",
                new String[]{Integer.toString(category.getId())},
                null, null, null));
        ArrayList<Product> list = new ArrayList<Product>();
        while(cursor.moveToNext())
        {
            list.add(cursor.getProduct());
        }
        cursor.close();
        return list;

    }

    public ModifyResponse modifyProductCategory(ProductCategory productCategory){

        if(productCategory.getDescription().trim().equals(""))
        {
            return ModifyResponse.InvalidDescription;
        }


        ProductCategory oldCategory = getProductCategoryById(productCategory.getId());
        if (oldCategory!=null)
        {
            if(!((oldCategory.getDescription().trim().toLowerCase()).equals((productCategory.getDescription().trim().toLowerCase())))
                    && getProductCategoryByDescription(productCategory.getDescription())!=null)
            {
                return ModifyResponse.DuplicateDescription;
            }
            else {
                ContentValues values = new ContentValues();
                values.put(ProductCategoriesTable.Columns.DESCRIPTION, productCategory.getDescription().trim());
                db.update(ProductCategoriesTable.NAME,
                        values,
                        ProductCategoriesTable.Columns.ID + " = ? ",
                        new String[]{Integer.toString(productCategory.getId())});
                return ModifyResponse.Ok;
            }
        }
        else
        {
            return ModifyResponse.InvalidId;
        }


    }

    public InsertResponse insertProductCategory (String description)
    {
        if (description.trim().equals("")) {
            return InsertResponse.InvalidDescription;
        }
        else if (getProductCategoryByDescription(description)!=null){
            return InsertResponse.DuplicateDescription;
        }
        else{
            int newId = getNewIdFrom(ProductCategoriesTable.NAME);
            ContentValues values = new ContentValues();
                values.put(ProductCategoriesTable.Columns.ID, Integer.toString(newId));
                values.put(ProductCategoriesTable.Columns.DESCRIPTION, description.trim());
            db.insert(ProductCategoriesTable.NAME, null, values);
            return InsertResponse.Ok;
        }
    }

    public DeleteResponse deleteProductCategory (ProductCategory category)
    {
        if(findProductCategoryUsageInProducts(category).isEmpty()) {
            db.delete(ProductCategoriesTable.NAME,
                    "id = ?",
                    new String[]{Integer.toString(category.getId())});
            return DeleteResponse.Ok;
        }
        else{
            return DeleteResponse.AlreadyInUse;
        }
    }
    //---------------------------------------------------------------------------------------
    //endregion


    //region Products Methods
    //---------------------------------------------------------------------------------------
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

    public Product getProductById(int id)
    {
        ProductCursor cursor = new ProductCursor(db.query(ProductsTable.NAME,
                null,
                ProductsTable.Columns.ID + " = ? ",
                new String[]{Integer.toString(id)},
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

    public List<Assembly> findAssembliesWithProduct (Product product)
    {

        AssemblyCursor cursor = new AssemblyCursor(db.query(AssembliesTable.NAME,
                null,
                AssembliesTable.Columns.ID + " IN (SELECT " + AssemblyProductsTable.Columns.ID +
                                                    " FROM " + AssemblyProductsTable.NAME  +
                                                    " WHERE " + AssemblyProductsTable.Columns.PRODUCT_ID + " = ?)" ,
                new String[]{Integer.toString(product.getId())},
                null, null, null));
        ArrayList<Assembly> list = new ArrayList<Assembly>();
        while(cursor.moveToNext())
        {
            list.add(cursor.getAssembly());
        }
        cursor.close();
        return list;
    }

    public InsertResponse insertProduct(String description, int price, ProductCategory category)
    {
        if (description.trim().equals(""))
        {
            return InsertResponse.InvalidDescription;
        }
        else if (price < 0)
        {
            return InsertResponse.InvalidPrice;
        }
        else if (getProductCategoryById(category.getId()) == null)
        {
            return InsertResponse.InvalidCategory;
        }
        else
        {
            ContentValues values = new ContentValues();
            values.put(ProductsTable.Columns.ID, Integer.toString(getNewIdFrom(ProductsTable.NAME)));
            values.put(ProductsTable.Columns.CATEGORY_ID, category.getId());
            values.put(ProductsTable.Columns.DESCRIPTION, description);
            values.put(ProductsTable.Columns.PRICE, price);
            values.put(ProductsTable.Columns.QTY, 0);

            db.insert(ProductsTable.NAME, null, values);
            return InsertResponse.Ok;
        }

    }

    public ModifyResponse modifyProduct(Product product){
        if (product.getDescription().trim().equals(""))
        {
            return ModifyResponse.InvalidDescription;
        }
        else if (product.getPrice() < 0)
        {
            return ModifyResponse.InvalidPrice;
        }
        else if (getProductCategoryById(product.getProductCategory().getId()) == null)
        {
            return ModifyResponse.InvalidCategory;
        }
        Product oldProduct = getProductById(product.getId());
        if(oldProduct!=null)
        {
            if( !(oldProduct.getDescription().trim().toLowerCase().equals(product.getDescription().trim().toLowerCase())) &&
                    getProductByDescription(product.getDescription())!=null)
            {
                return ModifyResponse.DuplicateDescription;
            }
            else {
                ContentValues values = new ContentValues();
                values.put(ProductsTable.Columns.DESCRIPTION, product.getDescription().trim());
                values.put(ProductsTable.Columns.PRICE, product.getPrice());
                values.put(ProductsTable.Columns.CATEGORY_ID, product.getProductCategory().getId());
                values.put(ProductsTable.Columns.QTY, product.getQty());

                db.update(ProductsTable.NAME,
                        values,
                        ProductsTable.Columns.ID + " = ? ",
                        new String[]{Integer.toString(product.getId())});
                return ModifyResponse.Ok;
            }
        }
        else
        {
            return ModifyResponse.InvalidId;
        }

    }

    public DeleteResponse deleteProduct (Product product)
    {
        if ( !findAssembliesWithProduct(product).isEmpty())
        {
            return DeleteResponse.AlreadyInUse;
        }
        else
        {
            db.delete(ProductsTable.NAME,
                    "id = ?",
                    new String[]{Integer.toString(product.getId())});
            return DeleteResponse.Ok;

        }
    }
    //---------------------------------------------------------------------------------------
    //endregion


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
