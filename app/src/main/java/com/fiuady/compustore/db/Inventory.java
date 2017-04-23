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

    public enum InsertResponse {Ok, DuplicateId, DuplicateDescription, InvalidDescription, InvalidPrice, InvalidCategory, InvalidFirstName, InvalidLastName, InvalidAddress}
    public enum DeleteResponse {Ok, AlreadyInUse}
    public enum ModifyResponse {Ok, DuplicateDescription, InvalidDescription, InvalidPrice, InvalidCategory, InvalidId, InvalidFirstName, InvalidLastName, InvalidAddress}
    public enum CustomerFilters {Default, FirstName, LastName, Address, Phone, Email}

    public Inventory(Context context){

        //inventoryHelper = new InventoryHelper(context);
        inventoryHelper = InventoryHelper.getInventoryHelper(context);
        db = inventoryHelper.getWritableDatabase();
    }


    //region Cursors
    //---------------------------------------------------------------------------------------
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

    class AssemblyProductsCursor extends CursorWrapper{
        public AssemblyProductsCursor(Cursor cursor){
            super(cursor);
        }

        public AssemblyProducts getAssemblyProducts () {
            Cursor cursor = getWrappedCursor();
            return new AssemblyProducts(cursor.getInt(cursor.getColumnIndex(AssemblyProductsTable.Columns.ID)),
                    cursor.getInt(cursor.getColumnIndex(AssemblyProductsTable.Columns.PRODUCT_ID)),
                    cursor.getInt(cursor.getColumnIndex(AssemblyProductsTable.Columns.QTY)));
        }
    }
    //---------------------------------------------------------------------------------------
    //endregion

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

    public List<Product> getAllProductsWithException(String except)
    {
        ArrayList<Product> list = new ArrayList<Product>();
        //ProductCursor cursor = new ProductCursor(db.rawQuery("SELECT * FROM products ORDER BY description", null));
        ProductCursor cursor = new ProductCursor(db.query(ProductsTable.NAME,
                null,
                ProductsTable.Columns.ID + " NOT IN ",
                new String[]{except},
                null, null, null));
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


    //region Assemblies Methods
    //---------------------------------------------------------------------------------------
    public ArrayList<Assembly> getAllAssemblies()
    {
        ArrayList<Assembly> list = new ArrayList<Assembly>();
        AssemblyCursor cursor = new AssemblyCursor(db.query(AssembliesTable.NAME,
                null,
                null,
                null,
                AssembliesTable.Columns.DESCRIPTION, null, null));
        while (cursor.moveToNext())
        {
            list.add(cursor.getAssembly());
        }
        cursor.close();
        return list;
    }

    public ArrayList<Product> getAllAssemblyProducts(int assemblyId)
    {
        AssemblyProductsCursor cursor = new AssemblyProductsCursor(db.query(AssemblyProductsTable.NAME,
                null,
                AssemblyProductsTable.Columns.ID + " = ? ",
                new String[]{Integer.toString(assemblyId)},
                null, null, null));
        ArrayList<Product> products = new ArrayList<Product>();
        while(cursor.moveToNext())
        {
            products.add(getProductById(cursor.getAssemblyProducts().getProductId()));
        }
        return products;
    }

    public ArrayList<AssemblyProducts> getAssemblyProductsById (int assemblyId)
    {
        AssemblyProductsCursor cursor = new AssemblyProductsCursor(db.query(AssemblyProductsTable.NAME,
                null,
                AssemblyProductsTable.Columns.ID + " = ? ",
                new String[]{Integer.toString(assemblyId)},
                null, null, null));
        ArrayList<AssemblyProducts> products = new ArrayList<AssemblyProducts>();
        while(cursor.moveToNext())
        {
            products.add(cursor.getAssemblyProducts());
        }
        return products;
    }

    public Assembly getAssemblyById(int id)
    {
        AssemblyCursor cursor = new AssemblyCursor(db.query(AssembliesTable.NAME,
                null,
                AssembliesTable.Columns.ID + " = ? ",
                new String[]{Integer.toString(id)},
                null, null, null));
        if(cursor.moveToNext())
        {
            Assembly assembly = cursor.getAssembly();
            cursor.close();
            return assembly;
        }
        else
        {
            return null;
        }
    }

    public Assembly getAssemblyByDescription(String description)
    {
        AssemblyCursor cursor = new AssemblyCursor(db.query(AssembliesTable.NAME,
                null,
                AssembliesTable.Columns.DESCRIPTION + " LIKE ? ",
                new String[]{description},
                null, null, null));
        if(cursor.moveToNext())
        {
            return cursor.getAssembly();
        }
        else
        {
            return null;
        }
    }

    public List<Order> findOrdersWithAssembly(Assembly assembly)
    {
        ArrayList<Order> orders = new ArrayList<Order>();
        OrderCursor cursor = new OrderCursor(db.query(OrdersTable.NAME,
                null,
                OrdersTable.Columns.ID + " IN (SELECT " + AssemblyProductsTable.Columns.ID +
                        " FROM " + OrderAssembliesTable.NAME  +
                                " WHERE " + OrderAssembliesTable.Columns.ASSEMBLY_ID+ " = ?)" ,
                        new String[]{Integer.toString(assembly.getId())},
                null, null, null
                        ));
        while (cursor.moveToNext())
        {
            orders.add(cursor.getOrder());
        }

        return orders;
    }


    public ModifyResponse modifyAssembly (Assembly assembly, ArrayList<AssemblyProducts> products)
    {
        if (assembly.getDescription().trim().equals(""))
        {
            return ModifyResponse.InvalidDescription;
        }

        Assembly oldAssembly = getAssemblyById(assembly.getId());
        if(oldAssembly!=null)
        {
            if( !(oldAssembly.getDescription().trim().toLowerCase().equals(assembly.getDescription().trim().toLowerCase())) &&
                    getAssemblyByDescription(assembly.getDescription())!=null)
            {
                return ModifyResponse.DuplicateDescription;
            }
            else {
                ContentValues values = new ContentValues();
                values.put(AssembliesTable.Columns.DESCRIPTION, assembly.getDescription().trim());

                db.update(AssembliesTable.NAME,
                        values,
                        AssembliesTable.Columns.ID + " = ? ",
                        new String[]{Integer.toString(assembly.getId())});

                db.delete(AssemblyProductsTable.NAME, "ID = ?", new String[]{Integer.toString(assembly.getId())});
                if(products!=null) {
                    for (AssemblyProducts assemblyP : products )
                    {
                        ContentValues valuesAP = new ContentValues();
                        valuesAP.put(AssemblyProductsTable.Columns.ID, assemblyP.getAssemblyId());
                        valuesAP.put(AssemblyProductsTable.Columns.PRODUCT_ID, assemblyP.getProductId());
                        valuesAP.put(AssemblyProductsTable.Columns.QTY, assemblyP.getQty());
                        db.insert(AssemblyProductsTable.NAME, null, valuesAP);
                    }

                }
                return ModifyResponse.Ok;
            }
        }
        else
        {
            return ModifyResponse.InvalidId;
        }
    }

    public InsertResponse insertAssembly (String description, ArrayList<Product> products, ArrayList<Integer> Qty)
    {
        if(description.trim().equals(""))
        {
            return InsertResponse.InvalidDescription;
        }
        else if (getAssemblyByDescription(description.trim())!=null)
        {
            return InsertResponse.DuplicateDescription;
        }

        else if (!products.isEmpty())
        {
            int newId = getNewIdFrom(AssembliesTable.NAME);
            ContentValues values = new ContentValues();
            values.put(AssembliesTable.Columns.ID, Integer.toString(newId));
            values.put(AssembliesTable.Columns.DESCRIPTION, description.trim());
            db.insert(AssembliesTable.NAME, null, values);

            if(products.size() == Qty.size())
            {
                for (int i = 0 ; i<products.size() ; i++)
                {
                    ContentValues values2 = new ContentValues();
                    values2.put(AssemblyProductsTable.Columns.ID, newId);
                    values2.put(AssemblyProductsTable.Columns.PRODUCT_ID, products.get(i).getId());
                    values2.put(AssemblyProductsTable.Columns.QTY, Qty.get(i));
                    db.insert(AssemblyProductsTable.NAME, null, values2);
                }
            }
            return InsertResponse.Ok;
        }
        else
        {
            ContentValues values = new ContentValues();
            values.put(AssembliesTable.Columns.ID, Integer.toString(getNewIdFrom(AssembliesTable.NAME)));
            values.put(AssembliesTable.Columns.DESCRIPTION, description);
            db.insert(AssembliesTable.NAME, null, values);
            return InsertResponse.Ok;
        }


    }

    public DeleteResponse deleteAssembly (Assembly assembly)
    {
        if ( !findOrdersWithAssembly(assembly).isEmpty())
        {
            return DeleteResponse.AlreadyInUse;
        }
        else
        {
            db.delete(AssemblyProductsTable.NAME,
                    "id = ?",
                    new String[]{Integer.toString(assembly.getId())});
            db.delete(AssembliesTable.NAME,
                    "id = ?",
                    new String[]{Integer.toString(assembly.getId())});
            return DeleteResponse.Ok;

        }
    }


    //---------------------------------------------------------------------------------------
    //endregion


    //region Customers Methods
    //---------------------------------------------------------------------------------------

    public ArrayList<Customer> getAllCustomers()
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

    public Customer getCustomerById (int id)
    {
        CustomerCursor cursor = new CustomerCursor(db.query(CustomersTable.NAME,
                null,
                CustomersTable.Columns.ID + " = ?",
                new String[]{Integer.toString(id)},
                null,
                null,
                null));
        if(cursor.moveToNext())
        {
            Customer customer = cursor.getCustomer();
            cursor.close();
            return customer;
        }
        else
        {
            return null;
        }
    }


    public ArrayList<Customer> searchCustomersWithFilter (ArrayList<CustomerFilters> filters, String searchTxt)
    {
        ArrayList<Customer> list = new ArrayList<Customer>();


        StringBuilder selection = new StringBuilder();
        int counter = 0;

        if (!filters.isEmpty())
        {
            for(CustomerFilters filter : filters)
            {

                switch (filter){
                    case FirstName:
                        if(selection.length()!=0)
                        {
                            selection.append(" OR ");
                        }
                        counter++;
                        selection.append("(" + CustomersTable.Columns.FIRST_NAME + " LIKE ?)");
                        break;
                    case LastName:
                        if(selection.length()!=0)
                        {
                            selection.append(" OR ");
                        }
                        counter++;
                        selection.append("(" + CustomersTable.Columns.LAST_NAME + " LIKE ?)");
                        break;
                    case Address:
                        if(selection.length()!=0)
                        {
                            selection.append(" OR ");
                        }
                        counter++;
                        selection.append("(" + CustomersTable.Columns.ADDRESS + " LIKE ?)");
                        break;
                    case Phone:
                        if(selection.length()!=0)
                        {
                            selection.append(" OR ");
                        }
                        counter = counter + 3;
                        selection.append("(( newphone1 LIKE ?)");
                        selection.append(" OR (newphone2 LIKE ?)");
                        selection.append(" OR (newphone3 LIKE ?))");
                        break;
                    case Email:
                        if(selection.length()!=0)
                        {
                            selection.append(" OR ");
                        }
                        counter++;
                        selection.append("(" + CustomersTable.Columns.EMAIL + " LIKE ?)");
                        break;
                    //case Default:
                    //    if(selection.length()!=0)
                    //    {
                    //        selection.append(" OR ");
                    //    }
                    //    break;
                }
            }
            String selectionArgs[] = new String[counter];
            for (int i=0; i<counter;i++)
            {
                selectionArgs[i]= "%" + searchTxt + "%";
            }
            String columns[] = new String[]{CustomersTable.Columns.ID,
                    CustomersTable.Columns.FIRST_NAME,
                    CustomersTable.Columns.LAST_NAME,
                    CustomersTable.Columns.ADDRESS,
                    CustomersTable.Columns.PHONE1,
                    CustomersTable.Columns.PHONE2,
                    CustomersTable.Columns.PHONE3,
                    CustomersTable.Columns.EMAIL,
                    "REPLACE(phone1,'-','') as newphone1",
                    "REPLACE(phone2,'-','') as newphone2",
                    "REPLACE(phone3,'-','') as newphone3"};
            String orderBy= CustomersTable.Columns.FIRST_NAME;
            CustomerCursor cursor = new CustomerCursor(db.query(CustomersTable.NAME, columns, selection.toString(), selectionArgs, null, null, orderBy));
            while(cursor.moveToNext())
            {
                list.add(cursor.getCustomer());
            }

        }
        return list;
    }


    public InsertResponse insertCustomer (String firstname, String lastname, String address, String phone1, String phone2, String phone3, String email)
    {
        if (firstname.equals(""))
        {
            return InsertResponse.InvalidFirstName;
        }
        else if (lastname.equals(""))
        {
            return InsertResponse.InvalidLastName;
        }
        else if (address.equals(""))
        {
            return InsertResponse.InvalidAddress;
        }

        else
        {
            int newId = getNewIdFrom(CustomersTable.NAME);
            ContentValues values = new ContentValues();
            values.put(CustomersTable.Columns.ID, Integer.toString(newId));
            values.put(CustomersTable.Columns.FIRST_NAME, firstname);
            values.put(CustomersTable.Columns.LAST_NAME, lastname);
            values.put(CustomersTable.Columns.ADDRESS, address);
            values.put(CustomersTable.Columns.PHONE1, (phone1.equals("") ? null : phone1));
            values.put(CustomersTable.Columns.PHONE2, (phone2.equals("") ? null : phone3));
            values.put(CustomersTable.Columns.PHONE3, (phone3.equals("") ? null : phone3));
            values.put(CustomersTable.Columns.EMAIL, (email.equals("") ? null : email));

            db.insert(CustomersTable.NAME, null, values);
            return InsertResponse.Ok;
        }
    }

    public ModifyResponse modifyCustomer (Customer newCustomer)
    {

        if(newCustomer.getFirstName().equals(""))
        {
            return ModifyResponse.InvalidFirstName;
        }
        else if(newCustomer.getLastName().equals(""))
        {
            return ModifyResponse.InvalidLastName;
        }
        else if (newCustomer.getAddress().equals(""))
        {
            return ModifyResponse.InvalidAddress;
        }
        else
        {
            Customer oldCustomer = getCustomerById(newCustomer.getId());
            if (oldCustomer!=null)
            {
                ContentValues values = new ContentValues();

                values.put(CustomersTable.Columns.FIRST_NAME, newCustomer.getFirstName());
                values.put(CustomersTable.Columns.LAST_NAME, newCustomer.getLastName());
                values.put(CustomersTable.Columns.ADDRESS, newCustomer.getAddress());
                values.put(CustomersTable.Columns.PHONE1, (newCustomer.getPhone1().equals("") ? null : newCustomer.getPhone1()));
                values.put(CustomersTable.Columns.PHONE2, (newCustomer.getPhone2().equals("") ? null : newCustomer.getPhone2()));
                values.put(CustomersTable.Columns.PHONE3, (newCustomer.getPhone3().equals("") ? null : newCustomer.getPhone3()));
                values.put(CustomersTable.Columns.EMAIL, (newCustomer.getEmail().equals("") ? null : newCustomer.getEmail()));

                db.update(CustomersTable.NAME, values,
                        CustomersTable.Columns.ID + " = ?", new String[] {Integer.toString(newCustomer.getId())});
                return ModifyResponse.Ok;
            }
            else
            {
                return ModifyResponse.InvalidId;
            }
        }

    }

    public DeleteResponse deleteCustomer (Customer customer)
    {
        if(!getOrdersByCustomer(customer).isEmpty())
        {

            return DeleteResponse.AlreadyInUse;
        }
        else
        {
            db.delete(CustomersTable.NAME,
                    "id = ?",
                    new String[]{Integer.toString(customer.getId())});
            return DeleteResponse.Ok;
        }

    }

    //endregion


    //region Orders Methods
    //---------------------------------------------------------------------------------------

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

    public ArrayList<Order> getOrdersByCustomer (Customer customer)
    {
        OrderCursor cursor = new OrderCursor(db.query(OrdersTable.NAME,
                null,
                OrdersTable.Columns.CUSTOMER_ID + " = ?",
                new String[]{Integer.toString(customer.getId())},
                null, null, null));
        ArrayList<Order> orders = new ArrayList<Order>();
        while(cursor.moveToNext())
        {
            orders.add(cursor.getOrder());
        }
        return orders;
    }
    //public Customer getCustomerById(int id) {
//
//
    //    CustomerCursor cursor = new CustomerCursor(db.rawQuery("SELECT * FROM customers WHERE id="+Integer.toString(id)+" ORDER BY id", null));
    //    cursor.moveToNext();
    //    Customer customer= cursor.getCustomer();
    //    cursor.close();
    //    return customer;
//
    //}


    public OrderStatus getOrderStatusById(int id) {


        OrderStatusCursor cursor = new OrderStatusCursor(db.rawQuery("SELECT * FROM order_status WHERE id="+Integer.toString(id)+" ORDER BY id", null));
        if(cursor.moveToNext())
        {
            OrderStatus orderstatus= cursor.getOrderStatus();
            cursor.close();
            return orderstatus;
        }
        else
        {
            return null;
        }


    }

    //---------------------------------------------------------------------------------------
    //endregion

}



