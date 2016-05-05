package edu.niu.cs.z1761257.grocerylist;

import android.content.Context;
import android.database.Cursor;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    //data members
    DBAdapter myDB;
    EditText itemET;

    List<String> items;

    MyAdapter dataAdapter = null;

    private final int UNCHECKED = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //avoids android keyboard to automatically appear when activity start
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

        //create object for DBAdapter
        myDB = new DBAdapter(this);
        myDB.open();


        //connect to the screen
        itemET = (EditText)findViewById(R.id.itemEditText);

        //initialize ArrayList
        items = new ArrayList<String>();

        //call update function
        updateGroceryList();
    }//end of onCreate

    //close db connection when app is closed
    @Override
    protected void onDestroy() {
        super.onDestroy();
        myDB.close();
    }

    //add item to the grocery list
    public void addItem(View view){
        if(!(itemET.getText().toString().equals(""))) {

            long newID = myDB.insertRow(itemET.getText().toString(), UNCHECKED);

            //call update function
            updateGroceryList();
        }
        else
        {
            //show toast message when edittext is empty
            Toast.makeText(this,"Please Enter Item",Toast.LENGTH_SHORT).show();
        }
    }//end of addRecord

    //delete all items in grocery list
    public void clearAllItem(View view){
        myDB.deleteAll();
        displayList();
    }//end of clearAll


    //display grocery list
    public void displayList()
    {
        //create arraylist object
        ArrayList<Grocery_Item> grocery_list = new ArrayList<Grocery_Item>();


           dataAdapter = new MyAdapter(this,
                    R.layout.grocery_item, grocery_list);
            ListView listView = (ListView) findViewById(R.id.listView);
            // Assign adapter to ListView
            listView.setAdapter(null);


    }//end of displayList


    //update grocery list
    public void updateGroceryList(){

        ArrayList<Grocery_Item> grocery_list = new ArrayList<Grocery_Item>();

        Cursor cursor = myDB.getAllRows();
        if(cursor.moveToFirst()){
            boolean isData = cursor.moveToFirst();
            while(isData){

                int id = cursor.getInt(DBAdapter.COL_ROWID),
                        status = cursor.getInt(DBAdapter.COL_STATUS);
                String name = cursor.getString(DBAdapter.COL_NAME);

                 Grocery_Item grocery = new Grocery_Item(id,name,status);

                grocery_list.add(grocery);

                isData = cursor.moveToNext();

            }

            dataAdapter = new MyAdapter(this,
                    R.layout.grocery_item, grocery_list);
            ListView listView = (ListView) findViewById(R.id.listView);
            // Assign adapter to ListView
            listView.setAdapter(dataAdapter);

        }
        cursor.close();

    //clear edit text field
    itemET.setText("");

    }

    private class MyAdapter extends ArrayAdapter<Grocery_Item>
    {
        Context context;
        List<Grocery_Item> taskList = new ArrayList<Grocery_Item>();

        //Constructor
        //
        //c is the current context
        //resourceId is the resource ID for a layout file to use when instantiating views
        //objects are the objects to represent in the ListView

        public MyAdapter(Context c, int resourceId, List<Grocery_Item> objects)
        {
            super(c, resourceId, objects);
            taskList = objects;
            context = c;
        }//end constructor


        //getView
        //Get a View that displays the data at a specified position in the data set
        //
        //position is the position of the item within the adapter's data set
        //convertView is the old view
        //parent is the parent that the view will be attached to

        @Override
        public View getView(int position, View convertView, ViewGroup parent)
        {
            CheckBox isDoneCB = null;

            if( convertView == null )
            {
                //Get a layout inflater so that the layout can be inflated in the ListView
                LayoutInflater inflater = (LayoutInflater)context.getSystemService(context.LAYOUT_INFLATER_SERVICE);

                //Use the custom layout for a grocery item for each grocery item in the ListView
                convertView = inflater.inflate(R.layout.grocery_item, parent, false);

                //Connect to the checkbox on the custom layout
                isDoneCB = (CheckBox)convertView.findViewById(R.id.checkBox1);

                //Store the checkbox state with the view
                convertView.setTag(isDoneCB);

                //Listen for changes to the checkbox
                isDoneCB.setOnClickListener(new View.OnClickListener()
                {
                    //When the checkbox is clicked
                    @Override
                    public void onClick(View v)
                    {
                        //Create a named checkbox from the clicked checkbox
                        CheckBox checkBox = (CheckBox)v;

                        //Get the item that was selected
                        Grocery_Item changeItem = (Grocery_Item)checkBox.getTag();

                        //Set the is_done property for the specific grocery item
                        //A checked box indicates the grocery item has been purchased
                        if( checkBox.isChecked() )
                        {
                            changeItem.setStatus(1);
                        }
                        else
                        {
                            changeItem.setStatus(0);
                        }

                        //Update the specific item in the database
                        myDB.updateItem(changeItem);
                    }
                });//end of onClickListener for checkbox
            }
            else
            {
                isDoneCB = (CheckBox)convertView.getTag();
            }

            //Get the item that was selected from the ListView
            Grocery_Item current = taskList.get(position);

            //Set the information for the checkbox from the grocery item in the ListView
            isDoneCB.setText(current.getName());
            isDoneCB.setChecked(current.getStatus() == 1 ? true : false);
            isDoneCB.setTag(current);

            //Return the item
            return convertView;
        }//end of getView

    }//end MyAdapter

}

