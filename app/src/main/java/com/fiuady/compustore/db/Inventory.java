package com.fiuady.compustore.db;

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




public final class Inventory {
    private InventoryHelper inventoryHelper;
    private SQLiteDatabase db;

    public Inventory(Context context){
        inventoryHelper = new InventoryHelper(context);
        db = inventoryHelper.getWritableDatabase();

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
        ProductCategoryCursor cursor = new ProductCategoryCursor(db.rawQuery("SELECT * FROM product_categories ORDER BY id", null));
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
    public void updateCategory(ProductCategory productCategory){
        ContentValues values = new ContentValues();
        values.put(ProductCategoriesTable.Columns.DESCRIPTION, productCategory.getDescription());
        db.update(ProductCategoriesTable.NAME,
                values,
                ProductCategoriesTable.Columns.ID + " =? ",
                new String[]{Integer.toString(productCategory.getId())});
    }

    //-------------------------------------------------------------------------
    //      Product
    //-------------------------------------------------------------------------

    public List<Product> getAllProducts()
    {
        ArrayList<Product> list = new ArrayList<Product>();
        ProductCursor cursor = new ProductCursor(db.rawQuery("SELECT * FROM products ORDER BY id", null));
        while (cursor.moveToNext())
        {
            list.add(cursor.getProduct());
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
}
