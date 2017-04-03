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


class ProductCategoryCursor extends CursorWrapper {
    public ProductCategoryCursor(Cursor cursor) {
        super(cursor);
    }

    public ProductCategories getProductCategory(){
        Cursor cursor = getWrappedCursor();
        return new ProductCategories(cursor.getInt(cursor.getColumnIndex(InventoryDbSchema.ProductCategoriesTable.Columns.ID)),
                cursor.getString(cursor.getColumnIndex(InventoryDbSchema.ProductCategoriesTable.Columns.DESCRIPTION)));
    }
}

public final class Inventory {
    private InventoryHelper inventoryHelper;
    private SQLiteDatabase db;

    public Inventory(Context context){
        inventoryHelper = new InventoryHelper(context);
        db = inventoryHelper.getWritableDatabase();

    }

    //-------------------------------------------------------------------------
    //      ProductCategories
    //-------------------------------------------------------------------------
    public List<ProductCategories> getAllProductCategories()
    {
        ArrayList<ProductCategories> list = new ArrayList<ProductCategories>();
        ProductCategoryCursor cursor = new ProductCategoryCursor(db.rawQuery("SELECT * FROM product_categories ORDER BY id", null));
        while (cursor.moveToNext())
        {
            list.add(cursor.getProductCategory());
        }
        cursor.close();
        return list;
    }
    public void updateCategory(ProductCategories productCategory){
        ContentValues values = new ContentValues();
        values.put(ProductCategoriesTable.Columns.DESCRIPTION, productCategory.getDescription());
        db.update(ProductCategoriesTable.NAME,
                values,
                ProductCategoriesTable.Columns.ID + " =? ",
                new String[]{Integer.toString(productCategory.getId())});
    }

    //-------------------------------------------------------------------------
    //      Products
    //-------------------------------------------------------------------------
}
