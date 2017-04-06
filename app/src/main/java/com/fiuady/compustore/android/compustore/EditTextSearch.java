package com.fiuady.compustore.android.compustore;

import android.content.Context;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.support.v7.widget.AppCompatEditText;
import android.view.View;
import android.widget.EditText;

/**
 * Created by Kuro on 06/04/2017.
 */

public class EditTextSearch extends android.support.v7.widget.AppCompatEditText {


        public EditTextSearch( Context context )
        {
            super( context );
        }

        public EditTextSearch( Context context, AttributeSet attribute_set )
        {
            super( context, attribute_set );
        }

        public EditTextSearch( Context context, AttributeSet attribute_set, int def_style_attribute )
        {
            super( context, attribute_set, def_style_attribute );
        }

        @Override
        public boolean onKeyPreIme( int key_code, KeyEvent event )
        {
            if ( event.getKeyCode() == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_UP )
                this.clearFocus();

            return super.onKeyPreIme( key_code, event );
        }


}
